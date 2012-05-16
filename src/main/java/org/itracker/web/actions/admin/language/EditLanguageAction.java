/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.web.actions.admin.language;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.Language;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


public class EditLanguageAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditLanguageAction.class);


    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        String action;
        try {
            action = (String) PropertyUtils.getSimpleProperty(form, "action");
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            return mapping.findForward("error");
        }
        if (!isTokenValid(request) && !"disable".equals(action)) {
            log.info("Invalid request token while editing language.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listlanguages");
        }
        resetToken(request);
        HttpSession session = request.getSession(true);

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            String locale = (String) PropertyUtils.getSimpleProperty(form, "locale");
            String localeTitle = (String) PropertyUtils.getSimpleProperty(form, "localeTitle");
            String localeBaseTitle = (String) PropertyUtils.getSimpleProperty(form, "localeBaseTitle");
            HashMap<String, String> items = (HashMap<String, String>) PropertyUtils.getSimpleProperty(form, "items");

            if (items == null) {
                return mapping.findForward("listlanguages");
            }

            // Fixes added for bug in beanutils.  Can remove all of the replace calls in the following
            // code once the bug is fixed.  Make sure the fix in EditLanguageFormAction is also removed.

            if (locale == null || "".equals(locale.trim())) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
            } else if ("disable".equals(action)) {

                Language languageItem = null;
                // This will update the Base Locale to remove the new language.
                languageItem = configurationService.getLanguageItemByKey("itracker.locales", null);
                String localeString = languageItem.getResourceValue();
                StringTokenizer t = new StringTokenizer(localeString, ",");
                StringBuffer newLocales = new StringBuffer();
                while (t.hasMoreTokens()) {
                    String s = t.nextToken();
                    if (!locale.equals(s)) {
                        newLocales.append(s).append(',');
                    }
                }
                if (newLocales.length() == 0) {
                    newLocales.append(ITrackerResources.getDefaultLocale());
                }
                newLocales.deleteCharAt(newLocales.lastIndexOf(","));
                languageItem.setResourceValue(newLocales.toString());
                configurationService.updateLanguageItem(languageItem);

                List<Configuration> localeConfigs = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_LOCALE);
                Iterator<Configuration> localeConfigIt = localeConfigs.iterator();

                while (localeConfigIt.hasNext()) {
                    Configuration configuration = (Configuration) localeConfigIt
                            .next();
                    if (configuration.getValue().equals(locale)) {
                        configurationService.removeConfigurationItem(configuration.getId());
                    }
                }
                ITrackerResources.clearBundles();
                clearSessionObjects(session);
                return mapping.findForward("listlanguages");

            } else if ("create".equals(action)) {

                if (locale.length() != 2 && (locale.length() != 5 || locale.indexOf('_') != 2)) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
                } else {
                    Language languageItem = null;

                    // This will update the Base Locale to include the new language.
                    languageItem = configurationService.getLanguageItemByKey("itracker.locales", null);
                    String localeString = languageItem.getResourceValue();
                    languageItem.setResourceValue(localeString + "," + locale);
                    configurationService.updateLanguageItem(languageItem);

                    Configuration localeConfig = new Configuration(SystemConfigurationUtilities.TYPE_LOCALE, locale);
                    if (configurationService.configurationItemExists(localeConfig)) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
                    } else {

                        configurationService.updateLanguageItem(new Language(locale, "itracker.locale.name", localeTitle));
                        configurationService.updateLanguageItem(new Language(locale, "itracker.locale.name." + locale, localeTitle));
                        configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, "itracker.locale.name." + locale, localeBaseTitle));
                        for (Iterator<String> iter = items.keySet().iterator(); iter.hasNext(); ) {
                            String key = iter.next();
                            if (key != null) {
                                String value = items.get(key);
                                if (value != null && value.length() != 0) {
                                    configurationService.updateLanguageItem(new Language(locale, key.replace('/', '.'), value));
                                }
                            }
                        }
                        configurationService.createConfigurationItem(localeConfig);
                        ITrackerResources.clearBundles();
                        clearSessionObjects(session);
                        return mapping.findForward("listlanguages");
                    }
                }
            } else if ("update".equals(action)) {

                Locale updateLocale = ITrackerResources.getLocale(locale);
                for (Iterator<String> iter = items.keySet().iterator(); iter.hasNext(); ) {
                    String key = iter.next();
                    if (key != null) {
                        String value = items.get(key);
                        try {
                            String currValue = ITrackerResources.getCheckForKey(key.replace('/', '.'), updateLocale);
                            if (value == null || value.length() == 0) {
                                try {
                                    configurationService.removeLanguageItem(new Language(locale, key.replace('/', '.')));
                                } catch (NoSuchEntityException e) {
                                    // do nothing; we want to delete it, so...
                                }

                            } else if (!value.equals(currValue)) {
                                configurationService.updateLanguageItem(new Language(locale, key.replace('/', '.'), value));
                            }
                        } catch (MissingResourceException mre) {
                            if (value != null && !value.trim().equals("")) {
                                configurationService.updateLanguageItem(new Language(locale, key.replace('/', '.'), value));
                            }
                        }
                    }
                }

                configurationService.updateLanguageItem(new Language(locale, "itracker.locale.name", localeTitle));
                configurationService.updateLanguageItem(new Language(locale, "itracker.locale.name." + locale, localeTitle));
                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, "itracker.locale.name." + locale, localeBaseTitle));

                ITrackerResources.clearBundles();
                clearSessionObjects(session);
                return mapping.findForward("listlanguages");
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        clearSessionObjects(session);
        return mapping.findForward("error");
    }


    private void clearSessionObjects(HttpSession session) {
        session.removeAttribute(Constants.EDIT_LANGUAGE_KEYS_KEY);
        session.removeAttribute(Constants.EDIT_LANGUAGE_BASE_KEY);
        session.removeAttribute(Constants.EDIT_LANGUAGE_LANG_KEY);
        session.removeAttribute(Constants.EDIT_LANGUAGE_LOC_KEY);
        session.removeAttribute(Constants.EDIT_LANGUAGE_TYPE_KEY);
    }
}
