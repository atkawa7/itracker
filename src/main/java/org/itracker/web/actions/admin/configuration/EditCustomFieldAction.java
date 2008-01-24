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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
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
import org.itracker.web.actions.admin.attachment.DownloadAttachmentAction;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldForm;

public class EditCustomFieldAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditCustomFieldAction.class);
	
    public EditCustomFieldAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            log.debug("Invalid request token while editing configuration.");
            return mapping.findForward("listconfiguration");
        }
        resetToken(request);
        // HttpSession session = request.getSession(true);
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            if(action == null) {
                return mapping.findForward("listconfiguration");
            }
            CustomFieldForm customFieldForm = (CustomFieldForm) form;
            
            CustomField customField = null;
            if("create".equals(action)) {
                customField = new CustomField();
                customField.setName(" ");
                customField.setFieldType(CustomField.Type.valueOf(customFieldForm.getFieldType()));
                customField.setRequired(("true".equals((String) PropertyUtils.getSimpleProperty(form, "required")) ? true : false));
                customField.setSortOptionsByName(("true".equals((String) PropertyUtils.getSimpleProperty(form, "sortOptionsByName")) ? true : false));
                customField.setDateFormat((String) PropertyUtils.getSimpleProperty(form, "dateFormat"));
                customField = configurationService.createCustomField(customField);
            } else if("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                customField = configurationService.getCustomField(id);
                List<CustomFieldValue> customFieldValues = customField.getOptions();
                if(customField == null) {
                    throw new SystemConfigurationException("Invalid custom field id " + id);
                }
                customField.setName(CustomFieldUtilities.getCustomFieldName(id));
                customField.setFieldType(CustomField.Type.valueOf(customFieldForm.getFieldType()));
                customField.setRequired(("true".equals((String) PropertyUtils.getSimpleProperty(form, "required")) ? true : false));
                customField.setSortOptionsByName(("true".equals((String) PropertyUtils.getSimpleProperty(form, "sortOptionsByName")) ? true : false));
                customField.setDateFormat((String) PropertyUtils.getSimpleProperty(form, "dateFormat"));
                // Set options to null so they don't get updated.
//                customField.setOptions(null);
                customField.setOptions(customFieldValues);
                customField = configurationService.updateCustomField(customField);
            } else {
                throw new SystemConfigurationException("Invalid action " + action + " while editing custom field.");
            }

            if(customField == null) {
                throw new SystemConfigurationException("Unable to create new custom field model.");
            }

            HashMap<String,String> translations = (HashMap<String,String>) PropertyUtils.getSimpleProperty(form, "translations");
            String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
            log.debug("Processing label translations for custom field " + customField.getId() + " with key " + key);
            if(translations != null && key != null && ! key.equals("")) {
                for(Iterator iter = translations.keySet().iterator(); iter.hasNext(); ) {
                    String locale = (String) iter.next();
                    if(locale != null) {
                        String translation = (String) translations.get(locale);
                        if(translation != null && ! translation.equals("")) {
                            log.debug("Adding new translation for locale " + locale + " for " + String.valueOf(customField.getId()));
                            configurationService.updateLanguageItem(new Language(locale, key, translation));
                        }
                    }
                }
            }
            if ( key != null )
                ITrackerResources.clearKeyFromBundles(key, true);
            // Now reset the cached versions in IssueUtilities
            configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_CUSTOMFIELD);
            String pageTitleKey = "";
            String pageTitleArg = "";
            pageTitleKey = "itracker.web.admin.editcustomfield.title.create";
            if (action == "update") {
            	 pageTitleKey = "itracker.web.admin.editcustomfield.title.update";
            }
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg);      
//            session.removeAttribute(Constants.CUSTOMFIELD_KEY);
            saveToken(request);
            //String forwardAction = "listconfiguration";

            if(customField.getFieldType() == CustomField.Type.LIST && "create".equals(action) ) { 
                return new ActionForward(mapping.findForward("editcustomfield").getPath() + "?id=" + customField.getId() + "&action=update");
            }
            return mapping.findForward("listconfiguration");
        } catch(SystemConfigurationException sce) {
            log.error("Exception processing form data: " + sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(sce.getKey()));
        } catch(Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }
}
  
