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

package org.itracker.web.actions.admin.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.itracker.model.CustomField;
import org.itracker.model.Language;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldForm;
import org.itracker.web.util.Constants;



public class EditCustomFieldFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditCustomFieldFormAction.class);
	
    public EditCustomFieldFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
//        super.executeAlways(mapping,form,request,response);
//        if(! isLoggedIn(request, response)) {
//            return mapping.findForward("login");
//        }

        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            
            Map<String,List<String>> languages_map = configurationService.getAvailableLanguages();
            String[] languages = new String[languages_map.size()];
            int idx = 0;
            // TODO: there is some bugs around here still, needs debugging. See jsp error output. 
            for(Iterator<String> iter = languages_map.keySet().iterator(); iter.hasNext(); ) {
         	   String language = (String) iter.next();
//         	 TODO: the following two lines have not been used, commented, task added
         	 //  String languageKey = "translations(" + language + ")";
         	  // Vector locales = (Vector) languages_map.get(language);
         	   languages[idx] = language;
                   idx++;
            }

            HttpSession session = request.getSession(true);
            Locale locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);

            CustomFieldForm customFieldForm = (CustomFieldForm) form;
            if(customFieldForm == null) {
                customFieldForm = new CustomFieldForm();
            }

            CustomField customField = new CustomField();
            String action = (String) PropertyUtils.getSimpleProperty(customFieldForm, "action");
            if("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(customFieldForm, "id");
                customField = configurationService.getCustomField(id);
                if(customField == null) {
                    throw new SystemConfigurationException("Invalid custom field id " + id);
                }
                customField.setName(CustomFieldUtilities.getCustomFieldName(id));
                customField.setOptions(customField.getOptions());
                customFieldForm.setId(id);
                customFieldForm.setFieldType(customField.getFieldType().getCode());
                customFieldForm.setRequired(Boolean.toString(customField.isRequired()));
                customFieldForm.setDateFormat(customField.getDateFormat());
                customFieldForm.setSortOptionsByName(Boolean.toString(customField.isSortOptionsByName()));

                HashMap<String,String> translations = new HashMap<String,String>();
                List<Language> languageItems = configurationService.getLanguageItemsByKey(CustomFieldUtilities.getCustomFieldLabelKey(customField.getId()));
                for(int i = 0; i < languageItems.size(); i++) {
                    translations.put(languageItems.get(i).getLocale(), languageItems.get(i).getResourceValue());
                }
                customFieldForm.setTranslations(translations);
                customField.setLabels(locale);
            }
            request.setAttribute("sc",configurationService);
            request.setAttribute("customFieldForm", customFieldForm);
            request.setAttribute("languages", languages);
//            request.setAttribute("languages_ary", languages_ary);
            request.setAttribute("action",action);
            session.setAttribute(Constants.CUSTOMFIELD_KEY, customField);
            saveToken(request);
            
            return mapping.getInputForward();
            
        } catch(SystemConfigurationException sce) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfield"));
        } catch(Exception e) {
            log.error("Exception while creating edit custom field form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        
        return mapping.findForward("error");
    }

}
  