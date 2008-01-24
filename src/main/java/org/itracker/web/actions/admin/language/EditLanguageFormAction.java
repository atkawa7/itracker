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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Language;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.LanguageForm;
import org.itracker.web.util.Constants;



public class EditLanguageFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditLanguageFormAction.class);
	
    public EditLanguageFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            HttpSession session = request.getSession(true);

            LanguageForm languageForm = (LanguageForm) form;
            if(languageForm == null) {
                languageForm = new LanguageForm();
            }

            String locale = (String) PropertyUtils.getSimpleProperty(form, "locale");
            int localeType = SystemConfigurationUtilities.getLocaleType(locale);
            if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_INVALID) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidlocale"));
            } else {
                if("create".equals((String) PropertyUtils.getSimpleProperty(form, "action"))) {
                    // The locale passed in on a create action is actually the parent locale.  Reset the parent
                    // locale, increment the type (since we are creating the next type, and clear the locale
                    localeType++;
                    languageForm.setParentLocale(locale);
                    if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) {
                        languageForm.setLocale(locale + "_");
                    } else {
                        languageForm.setLocale("");
                    }
                } 

                String[] sortedKeys = configurationService.getSortedKeys();
                // Fix for bug in beanutils.  Can remove this logic here and in EditLanguageAction
                // once the bug is fixed.
                for(int i = 0; i < sortedKeys.length; i++) {
                    sortedKeys[i] = sortedKeys[i].replace('.', '/');
                }

                Map<String,String> baseItems = new HashMap<String,String>();
                Map<String,String> langItems = new HashMap<String,String>();
                Map<String,String> locItems = new HashMap<String,String>();
                Map<String,String> items = new HashMap<String,String>();
                log.debug("Loading language elements for edit.  Edit type is " + localeType);
                
                if (localeType >= SystemConfigurationUtilities.LOCALE_TYPE_BASE) {
                    baseItems = configurationService.getDefinedKeys(null);
                    items = baseItems;
                    log.debug("Base Locale has " + baseItems.size() + " keys defined.");
                }
                
                if (localeType >= SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE) {
                    if (!locale.equalsIgnoreCase(ITrackerResources.BASE_LOCALE)) {
                        String parentLocale = SystemConfigurationUtilities.getLocalePart(locale, SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE);
                        languageForm.setParentLocale(parentLocale );
                        langItems = configurationService.getDefinedKeys(parentLocale);
                        items = langItems;
                        log.debug("Language " + parentLocale + " has " + langItems.size() + " keys defined.");
                    }
                }
                
                if (localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) {
                    locItems = configurationService.getDefinedKeys(locale);
                    items = locItems;
                    log.debug("Locale " + locale + " has " + locItems.size() + " keys defined.");
                }
                
                if (! "create".equals((String) PropertyUtils.getSimpleProperty(form, "action"))) {
                    // Fix for bug in beanutils.  Can remove this logic here and in EditLanguageAction
                    // once the bug is fixed.
                    // languageForm.set("items", items);
                    for (Iterator iter = baseItems.keySet().iterator(); iter.hasNext(); ) {
                        String key = (String) iter.next();
                        String itemStr = (String) items.get(key);
                        if ( itemStr == null || itemStr.length() == 0 )
                            items.put(key,"");
                    }

                    Map<String,String> formItems = new HashMap<String,String>();
                    
                    for(Iterator iter = items.keySet().iterator(); iter.hasNext(); ) {
                            String key = (String) iter.next();
                            formItems.put(key.replace('.', '/'), (String) items.get(key));
                    }
                    languageForm.setItems(formItems);
                } else {
                    String parentLocale = null;
                    
                    if (!locale.equalsIgnoreCase(ITrackerResources.BASE_LOCALE)) {
                        parentLocale = SystemConfigurationUtilities.getLocalePart(locale, SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE);
                    }
                    langItems = configurationService.getDefinedKeys(parentLocale);
                    Map<String,String> formItems = new HashMap<String,String>();
                    
                    for (Iterator iter = items.keySet().iterator(); iter.hasNext(); ) {
                        String key = (String) iter.next();
                        formItems.put(key.replace('.', '/'), (String) langItems.get(key));
                    }
                    languageForm.setItems(formItems);
                    
                }
                Language languageItem = null;
                Locale curLocale = ITrackerResources.getLocale(locale);
                languageItem = configurationService.getLanguageItemByKey("itracker.locale.name", curLocale);
                languageForm.setLocaleTitle(languageItem.getResourceValue());

                session.setAttribute(Constants.EDIT_LANGUAGE_KEYS_KEY, sortedKeys);
                session.setAttribute(Constants.EDIT_LANGUAGE_BASE_KEY, baseItems);
                session.setAttribute(Constants.EDIT_LANGUAGE_LANG_KEY, langItems);
                session.setAttribute(Constants.EDIT_LANGUAGE_LOC_KEY, locItems);
                session.setAttribute(Constants.EDIT_LANGUAGE_TYPE_KEY, new Integer(localeType));
                request.setAttribute("languageForm", languageForm);
                log.debug("Locale = " + languageForm.getLocale());
                saveToken(request);
                return mapping.getInputForward();
            }
        } catch(Exception e) {
            log.error("Exception while creating edit language form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
        	saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  