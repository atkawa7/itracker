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
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;

public class EditCustomFieldValueAction extends ItrackerBaseAction {

    public EditCustomFieldValueAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        logger.info("Kimba Went To Action");
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing configuration.");
            return mapping.findForward("listconfiguration");
        }
        resetToken(request);
        HttpSession session = request.getSession(true);
        CustomField customField = (CustomField) session.getAttribute(Constants.CUSTOMFIELD_KEY);
        if(customField == null) {
            return mapping.findForward("listconfiguration");
        }

        try {

            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            if(action == null) {
                return mapping.findForward("listconfiguration");
            }
        
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            CustomFieldValue customFieldValue = null;
            if("create".equals(action)) {
                // TODO: the following line can be removed, we guess - that's why we comment it now. 
            	// Integer id = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                List<CustomFieldValue> currOptions = customField.getOptions();
                int highestSortOrder = (currOptions.size() == 0 ? 1 : currOptions.get(currOptions.size() - 1).getSortOrder());
                customFieldValue = new CustomFieldValue();
                customFieldValue.setCustomField(customField);
                customFieldValue.setValue((String) PropertyUtils.getSimpleProperty(form, "value"));
                customFieldValue.setSortOrder(highestSortOrder + 1);
                customFieldValue = configurationService.createCustomFieldValue(customFieldValue);
            } else if("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                customFieldValue = configurationService.getCustomFieldValue(id);
                if(customField == null) {
                    throw new SystemConfigurationException("Invalid custom field value id " + id);
                }
                customFieldValue.setValue((String) PropertyUtils.getSimpleProperty(form, "value"));
                customFieldValue = configurationService.updateCustomFieldValue(customFieldValue);
            } else {
                throw new SystemConfigurationException("Invalid action " + action + " while editing custom field value.");
            }

            if(customFieldValue == null) {
                throw new SystemConfigurationException("Unable to create new custom field value model.");
            }

            HashMap translations = (HashMap) PropertyUtils.getSimpleProperty(form, "translations");
            String key = CustomFieldUtilities.getCustomFieldOptionLabelKey(customField.getId(), customFieldValue.getId());
            logger.debug("Processing label translations for custom field value " + customFieldValue.getId() + " with key " + key);
            if(translations != null && key != null && ! key.equals("")) {
                for(Iterator iter = translations.keySet().iterator(); iter.hasNext(); ) {
                    String locale = (String) iter.next();
                    if(locale != null) {
                        String translation = (String) translations.get(locale);
                        if(translation != null && ! translation.equals("")) {
                            logger.debug("Adding new translation for locale " + locale + " for " + customFieldValue);
                            configurationService.updateLanguageItem(new Language(locale, key, translation));
                        }
                    }
                }
                String baseValue = (String) translations.get(ITrackerResources.BASE_LOCALE);
                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, key, baseValue));
                ITrackerResources.clearKeyFromBundles(key, true);
            }

            // Now reset the cached versions in IssueUtilities
            configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_CUSTOMFIELD);
            request.setAttribute("action",action);
            String pageTitleKey = "";
            String pageTitleArg = "";
            pageTitleKey = "itracker.web.admin.editcustomfield.title.create";
            if (action == "update") {
            	 pageTitleKey = "itracker.web.admin.editcustomfield.title.update";
            }
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg);     
         
            saveToken(request);
            return new ActionForward(mapping.findForward("editcustomfield").getPath() + "?id=" + customField.getId() + "&action=update");
        } catch(SystemConfigurationException sce) {
            logger.error("Exception processing form data: " + sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(sce.getKey()));
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            saveToken(request);
            request.setAttribute("customFieldValueForm", form);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }
}
  