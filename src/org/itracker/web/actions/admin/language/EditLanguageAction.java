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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.Language;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;



public class EditLanguageAction extends ItrackerBaseAction {

    public EditLanguageAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing language.");
            return mapping.findForward("listlanguages");
        }
        resetToken(request);
        HttpSession session = request.getSession(true);

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            String locale = (String) PropertyUtils.getSimpleProperty(form, "locale");
            HashMap items = (HashMap) PropertyUtils.getSimpleProperty(form, "items");

            if(items == null) {
                return mapping.findForward("listlanguages");
            }

            // Fixes added for bug in beanutils.  Can remove all of the replace calls in the following
            // code once the bug is fixed.  Make sure the fix in EditLanguageFormAction is also removed.

            if(locale == null || "".equals(locale.trim())) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
            } else if("create".equals(action)) {
                if(locale.length() != 2 && (locale.length() != 5 || locale.indexOf('_') != 2)) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
                } else {
                    Configuration localeConfig = new Configuration(SystemConfigurationUtilities.TYPE_LOCALE, locale);
                    if(configurationService.configurationItemExists(localeConfig)) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
                    } else {
                        for(Iterator iter = items.keySet().iterator(); iter.hasNext(); ) {
                            String key = (String) iter.next();
                            if(key != null) {
                                String value = (String) items.get(key);
                                if(value != null && ! value.trim().equals("")) {
                                    configurationService.updateLanguageItem(new Language(locale, key.replace('/', '.'), value));
                                }
                            }
                        }
                        configurationService.createConfigurationItem(localeConfig);
                        clearSessionObjects(session);
                        return mapping.findForward("listlanguages");
                    }
                }
            } else if("update".equals(action)) {
                Locale updateLocale = ITrackerResources.getLocale(locale);
                for(Iterator iter = items.keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    if(key != null) {
                        String value = (String) items.get(key);
                        try {
                            String currValue = ITrackerResources.getCheckForKey(key.replace('/', '.'), updateLocale);
                            if(value == null || value.trim().equals("")) {
                                try {
                                    configurationService.removeLanguageItem(new Language(locale, key.replace('/', '.')));
                                    ITrackerResources.clearKeyFromBundles(key.replace('/', '.'), true);
                                } catch ( NoSuchEntityException e ) {
                                    // do nothing; we want to delete it, so...
                                }
                                        
                            } else if(! value.equals(currValue)) {
                                configurationService.updateLanguageItem(new Language(locale, key.replace('/', '.'), value));
                                ITrackerResources.clearKeyFromBundles(key.replace('/', '.'), true);
                            }
                        } catch(MissingResourceException mre) {
                            if(value != null && ! value.trim().equals("")) {
                                configurationService.updateLanguageItem(new Language(locale, key.replace('/', '.'), value));
                                ITrackerResources.clearKeyFromBundles(key.replace('/', '.'), true);
                            }
                        }
                    }
                }
                clearSessionObjects(session);
                return mapping.findForward("listlanguages");
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
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
  