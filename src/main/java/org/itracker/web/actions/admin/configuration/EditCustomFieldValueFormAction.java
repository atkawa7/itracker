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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldValueForm;
import org.itracker.web.util.Constants;


public class EditCustomFieldValueFormAction extends ItrackerBaseAction {

    public EditCustomFieldValueFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        if (! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        if (! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            // TODO: it looks like to following 3 lines can be removed, we comment them and add a task.
            HttpSession session = request.getSession(true);
            //Locale currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            Map<String, List<String>> languages = configurationService.getAvailableLanguages();
            
            CustomFieldValueForm customFieldValueForm = (CustomFieldValueForm) form;
            
            if (customFieldValueForm == null) {
                customFieldValueForm = new CustomFieldValueForm();
            }

            CustomFieldValue customFieldValue = new CustomFieldValue();
            
            String action = customFieldValueForm.getAction();
            
            if ("update".equals(action)) {
                Integer id = customFieldValueForm.getId();
                customFieldValue = configurationService.getCustomFieldValue(id);
                if(customFieldValue == null) {
                    throw new SystemConfigurationException("Invalid custom field value id " + id);
                }
                String name = CustomFieldUtilities.getCustomFieldOptionName(customFieldValue.getCustomField().getId(), id);
                customFieldValue.setName(name);
                
                customFieldValueForm.setId(id);
                customFieldValueForm.setValue(customFieldValue.getValue());

                HashMap<String, String> translations = new HashMap<String, String>();
                List<Language> languageItems = configurationService.getLanguageItemsByKey(CustomFieldUtilities.getCustomFieldOptionLabelKey(customFieldValue.getCustomField().getId(), customFieldValue.getId()));
                
                for (int i = 0; i < languageItems.size(); i++) {
                    translations.put(languageItems.get(i).getLocale(), languageItems.get(i).getResourceValue());
                }
                customFieldValueForm.setTranslations(translations);
            }

            request.setAttribute("languages", languages);
            request.setAttribute("customFieldValueForm", customFieldValueForm);
            request.setAttribute("action",action);
            session.setAttribute(Constants.CUSTOMFIELDVALUE_KEY, customFieldValue);
            saveToken(request);
            return mapping.getInputForward();
        } catch(SystemConfigurationException sce) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfieldvalue"));
        } catch(Exception e) {
            logger.error("Exception while creating edit custom field value form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  