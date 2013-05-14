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
import org.itracker.model.Language;
import org.itracker.model.util.PropertiesFileHandler;
import org.itracker.services.ConfigurationService;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.LanguageForm;
import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class EditLanguageFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditLanguageFormAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            HttpSession session = request.getSession(true);

            LanguageForm languageForm = (LanguageForm) form;
            if (languageForm == null) {
                languageForm = new LanguageForm();
            }

            String locale = (String) PropertyUtils.getSimpleProperty(form, "locale");
            int localeType = SystemConfigurationUtilities.getLocaleType(locale);
            if (localeType == SystemConfigurationUtilities.LOCALE_TYPE_INVALID) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
            } else {
                if ("create".equals((String) PropertyUtils.getSimpleProperty(form, "action"))) {
                    // The locale passed in on a create action is actually the parent locale.  Reset the parent
                    // locale, increment the type (since we are creating the next type, and clear the locale
                    localeType++;
                    languageForm.setParentLocale(locale);
                    if (localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) {
                        languageForm.setLocale(locale + "_");
                    } else {
                        languageForm.setLocale("");
                    }
                }

                String[] sortedKeys = configurationService.getSortedKeys();
                // Fix for bug in beanutils.  Can remove this logic here and in EditLanguageAction
                // once the bug is fixed.
                for (int i = 0; i < sortedKeys.length; i++) {
                    sortedKeys[i] = sortedKeys[i].replace('.', '/');
                }

                Map<String, String> baseItems = new HashMap<String, String>();
                Map<String, String> langItems = new HashMap<String, String>();
                Map<String, String> locItems = new HashMap<String, String>();
                Map<String, String> items = new HashMap<String, String>();

                log.debug("Loading language elements for edit.  Edit type is " + localeType);

                if (localeType >= SystemConfigurationUtilities.LOCALE_TYPE_BASE) {
                    baseItems = configurationService.getDefinedKeys(null);
                    putPropertiesKeys(baseItems, items, ITrackerResources.BASE_LOCALE);
                    items = baseItems;
                    log.debug("Base Locale has " + baseItems.size() + " keys defined.");
                }

                if (localeType >= SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE) {
                    if (!locale.equalsIgnoreCase(ITrackerResources.BASE_LOCALE)) {
                        String parentLocale = SystemConfigurationUtilities.getLocalePart(locale, SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE);
                        languageForm.setParentLocale(parentLocale);
                        langItems = configurationService.getDefinedKeys(parentLocale);
                        putPropertiesKeys(langItems, items, parentLocale);

                        items = langItems;
                        log.debug("Language " + parentLocale + " has " + langItems.size() + " keys defined.");
                    }
                }

                if (localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) {
                    locItems = configurationService.getDefinedKeys(locale);
                    putPropertiesKeys(locItems, items, locale);

                    items = locItems;
                    log.debug("Locale " + locale + " has " + locItems.size() + " keys defined.");
                }

                if (!"create".equals((String) PropertyUtils.getSimpleProperty(form, "action"))) {
                    // Fix for bug in beanutils.  Can remove this logic here and in EditLanguageAction
                    // once the bug is fixed.
                    // languageForm.set("items", items);
//                    for (Iterator<String> iter = baseItems.keySet().iterator(); iter.hasNext(); ) {
//                        String key = (String) iter.next();
//                        String itemStr = (String) items.get(key);
//                        if ( itemStr == null || itemStr.length() == 0 )
//                            items.put(key,"");
//                    }

                    Map<String, String> formItems = new HashMap<String, String>();
                    for (Enumeration<String> en = ITrackerResources.getBundle(locale).getKeys(); en.hasMoreElements(); ) {
                        String key = en.nextElement();
                        formItems.put(key, "");
                    }
                    formItems.putAll(items);

                    languageForm.setItems(new TreeMap<String, String>(formItems));
                } else {
                    String parentLocale = null;

                    if (!locale.equalsIgnoreCase(ITrackerResources.BASE_LOCALE)) {
                        parentLocale = SystemConfigurationUtilities.getLocalePart(locale, SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE);
                    }
                    langItems = configurationService.getDefinedKeys(parentLocale);

                    Map<String, String> formItems = new HashMap<String, String>();
                    if (log.isDebugEnabled()) {
                        log.debug("putPropertiesKeys: items: " + items);
                    }
                    for (Enumeration<String> en = ITrackerResources.getBundle(locale).getKeys(); en.hasMoreElements(); ) {
                        String key = en.nextElement();
                        formItems.put(key, "");
                    }

                    formItems.putAll(items);

                    languageForm.setItems(new TreeMap<String, String>(formItems));

                }
                Language languageItem = null;
                Locale curLocale = ITrackerResources.getLocale(locale);

                languageItem = new Language(locale, "itracker.locale.name",
                        ITrackerResources.getString("itracker.locale.name", curLocale));// configurationService.getLanguageItemByKey("itracker.locale.name", curLocale);

                languageForm.setLocaleTitle(languageItem.getResourceValue());
                languageItem = new Language(locale, "itracker.locale.name",
                        ITrackerResources.getString("itracker.locale.name." + locale, ITrackerResources.BASE_LOCALE));// configurationService.getLanguageItemByKey("itracker.locale.name", curLocale);

                languageForm.setLocaleBaseTitle(languageItem.getResourceValue());
                session.setAttribute(Constants.EDIT_LANGUAGE_KEYS_KEY, sortedKeys);
                session.setAttribute(Constants.EDIT_LANGUAGE_BASE_KEY, baseItems);
                session.setAttribute(Constants.EDIT_LANGUAGE_LANG_KEY, langItems);
                session.setAttribute(Constants.EDIT_LANGUAGE_LOC_KEY, locItems);
                session.setAttribute(Constants.EDIT_LANGUAGE_TYPE_KEY, localeType);
                request.setAttribute("languageForm", languageForm);
                if (log.isDebugEnabled()) {
                    log.debug("Locale = " + languageForm.getLocale());
                }
                saveToken(request);
                return mapping.getInputForward();
            }
        } catch (RuntimeException e) {
            log.error("Exception while creating edit language form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (IllegalAccessException e) {
            log.error("Exception while creating edit language form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (InvocationTargetException e) {
            log.error("Exception while creating edit language form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (NoSuchMethodException e) {
            log.error("Exception while creating edit language form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }

    @SuppressWarnings("unchecked")
    void putPropertiesKeys(Map<String, String> locItems, Map<String, String> items, String locale) {
        try {
            Hashtable<Object, Object> p;
            try {
                String path = File.separatorChar + ITrackerResources.RESOURCE_BUNDLE_NAME.replace('.', File.separatorChar) + (null != locale && !(ITrackerResources.BASE_LOCALE.equals(locale)) ? "_" + locale : "") + ".properties";
                if (log.isDebugEnabled()) {
                    log.debug("putPropertiesKeys: loading: " + path);
                }
                p = new PropertiesFileHandler(path).getProperties();
                p = new Hashtable<Object, Object>(p);

                if (log.isDebugEnabled()) {
                    log.debug("putPropertiesKeys: loaded properties: " + p);
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("putPropertiesKeys", e);
                }
                p = new Properties();
            }
            // overload properties by loc items from db
            if (log.isDebugEnabled()) {
                log.debug("putPropertiesKeys: overloading locItems: " + locItems);
            }
            p.putAll(locItems);
            locItems.putAll(Collections.checkedMap((Map) p, String.class, String.class));

        } catch (RuntimeException e) {
            log.error("addPropertiesKeys: caught ", e);
        }
    }
}
  