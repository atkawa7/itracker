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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.Configuration;
import org.itracker.model.Language;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ConfigurationForm;



public class EditConfigurationFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditConfigurationFormAction.class);
	

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();


        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            ConfigurationForm configurationForm = (ConfigurationForm) form;
            if(configurationForm == null) {
                configurationForm = new ConfigurationForm();
            }

            String action = configurationForm.getAction();
            
            String formValue = configurationForm.getValue();
            
            if ("update".equals(action)) {
                Integer id = configurationForm.getId();
                formValue = String.valueOf(id);
                Configuration configItem = configurationService.getConfigurationItem(id);
                
                if (configItem == null) {
                    throw new SystemConfigurationException("Invalid configuration item id " + id);
                }
                configurationForm.setId(id);

                if(configItem.getType() == SystemConfigurationUtilities.TYPE_STATUS) {
                    configurationForm.setValue(configItem.getValue());
                }

                HashMap<String,String> translations = new HashMap<String,String>();
                List<Language> languageItems = configurationService.getLanguageItemsByKey(
                        SystemConfigurationUtilities.getLanguageKey(configItem));
                
                for (int i = 0; i < languageItems.size(); i++) {
                    translations.put(languageItems.get(i).getLocale(), 
                            languageItems.get(i).getResourceValue());
                }
                configurationForm.setTranslations(translations);
            }

            request.setAttribute("sc", configurationService);
            request.setAttribute("configurationForm", configurationForm);
            request.setAttribute("action",action);
            request.setAttribute("value",formValue);
            saveToken(request);
            
            return mapping.getInputForward();
        } catch(SystemConfigurationException sce) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("itracker.web.error.invalidconfiguration"));
        } catch(Exception e) {
            log.error("Exception while creating edit configuration form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  