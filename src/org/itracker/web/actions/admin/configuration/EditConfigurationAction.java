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
import org.itracker.model.Configuration;
import org.itracker.model.Issue;
import org.itracker.model.IssueActivity;
import org.itracker.model.Language;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class EditConfigurationAction extends ItrackerBaseAction {

    public EditConfigurationAction() {
    	
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing configuration.");
            return mapping.findForward("listconfiguration");
        }
        resetToken(request);
        HttpSession session = request.getSession(true);

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            String formValue = (String) PropertyUtils.getSimpleProperty(form, "value");
            
            String initialLanguageKey = null;
            HashMap translations = (HashMap) PropertyUtils.getSimpleProperty(form, "translations");

            if(action == null) {
                return mapping.findForward("listconfiguration");
            }

            Configuration configItem = null;
            if("createresolution".equals(action)) {
                int value = 0;
                int order = 0;

                try {
                    List<Configuration> resolutions = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION);
                    for(int i = 0; i < resolutions.size(); i++) {
                        value = Math.max(value, Integer.parseInt(resolutions.get(i).getValue()));
                        order = resolutions.get(i).getOrder();
                    }
                    if(value > 0) {
                        String version = configurationService.getProperty("version");
                        configItem = new Configuration(SystemConfigurationUtilities.TYPE_RESOLUTION, Integer.toString(++value), version, ++order);
                    }
                } catch(NumberFormatException nfe) {
                    logger.debug("Found invalid value or order for a resolution.", nfe);
                    throw new SystemConfigurationException("Found invalid value or order for a resolution.");
                }
            } else if("createseverity".equals(action)) {
                int value = 0;
                int order = 0;

                try {
                    List<Configuration> severities = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY);
                    for(int i = 0; i < severities.size(); i++) {
                        value = Math.max(value, Integer.parseInt(severities.get(i).getValue()));
                        order = severities.get(i).getOrder();
                    }
                    if(value > 0) {
                        String version = configurationService.getProperty("version");
                        configItem = new Configuration(SystemConfigurationUtilities.TYPE_SEVERITY, Integer.toString(++value), version, ++order);
                    }
                } catch(NumberFormatException nfe) {
                    logger.debug("Found invalid value or order for a severity.", nfe);
                    throw new SystemConfigurationException("Found invalid value or order for a severity.");
                }
            } else if("createstatus".equals(action)) {
                try {
                    int value = Integer.parseInt(formValue);
                    List<Configuration> statuses = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS);
                    for(int i = 0; i < statuses.size(); i++) {
                        if(value == Integer.parseInt(statuses.get(i).getValue())) {
                            throw new SystemConfigurationException("Supplied status value already equals existing status.", "itracker.web.error.existingstatus");
                        }
                    }

                    String version = configurationService.getProperty("version");
                    configItem = new Configuration(SystemConfigurationUtilities.TYPE_STATUS, formValue, version, value);
                } catch(NumberFormatException nfe) {
                    throw new SystemConfigurationException("Invalid value " + formValue + " for status.", "itracker.web.error.invalidstatus");
                }
            } else if("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                formValue = String.valueOf(id);
                configItem = configurationService.getConfigurationItem(id);
                if(configItem == null) {
                    throw new SystemConfigurationException("Invalid configuration item id " + id);
                }
                
                initialLanguageKey = SystemConfigurationUtilities.getLanguageKey(configItem);
                
                if(configItem.getType() == SystemConfigurationUtilities.TYPE_STATUS && formValue != null && ! formValue.equals("")) {
                    if(! configItem.getValue().equalsIgnoreCase(formValue)) {
                        try {
                            int currStatus = Integer.parseInt(configItem.getValue());
                            int newStatus = Integer.parseInt(formValue);

                            List<Configuration> statuses = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS);
                            for(int i = 0; i < statuses.size(); i++) {
                                if(newStatus == Integer.parseInt(statuses.get(i).getValue())) {
                                    throw new SystemConfigurationException("Supplied status value already equals existing status.", "itracker.web.error.existingstatus");
                                }
                            }
                            // set new value
                            configItem.setValue( formValue.trim() );
                            
                            logger.debug("Changing issue status values from " + configItem.getValue() + " to " + formValue);

                            User currUser = (User) session.getAttribute(Constants.USER_KEY);
                            Integer currUserId = (currUser == null ? new Integer(-1) : currUser.getId());

                            IssueService issueService = getITrackerServices().getIssueService();
                            
                            List<Issue> issues = issueService.getIssuesWithStatus(currStatus);
                            for(int i = 0; i < issues.size(); i++) {
                                if(issues.get(i) != null) {
                                    issues.get(i).setStatus(newStatus);
                                    issues.add(issueService.updateIssue(issues.get(i), currUserId));
                                    IssueActivity activity = new IssueActivity();
                                    activity.setType(IssueUtilities.ACTIVITY_SYSTEM_UPDATE);
                                    activity.setDescription(ITrackerResources.getString("itracker.activity.system.status"));
                                    // TODO need to fix this RJST
                                    //activity.setIssue(issues[i]);
                                    //.getId(), currUserId);                                    
                                }
                            }
                        } catch(NumberFormatException nfe) {
                            throw new SystemConfigurationException("Invalid value " + formValue + " for updated status.", "itracker.web.error.invalidstatus");
                        }
                    }
                }
            } else {
                throw new SystemConfigurationException("Invalid action " + action + " while editing configuration item.");
            }

            if(configItem == null) {
                throw new SystemConfigurationException("Unable to create new configuration item model.");
            }
            if("update".equals(action)) {
                configItem = configurationService.updateConfigurationItem(configItem);
            } else {
                configItem = configurationService.createConfigurationItem(configItem);
            }

            if(configItem == null) {
                throw new SystemConfigurationException("Unable to create new configuration item.");
            }


            String key = SystemConfigurationUtilities.getLanguageKey(configItem);
            logger.debug("Processing translations for configuration item " + configItem.getId() + " with key " + key);
            if(translations != null && key != null && ! key.equals("")) {
                for(Iterator iter = translations.keySet().iterator(); iter.hasNext(); ) {
                    String locale = (String) iter.next();
                    if(locale != null) {
                        String translation = (String) translations.get(locale);
                        if(translation != null && ! translation.equals("")) {
                            logger.debug("Adding new translation for locale " + locale + " for " + configItem);
                            configurationService.updateLanguageItem(new Language(locale, key, translation));
                        }
                    }
                }
                String baseValue = (String) translations.get(ITrackerResources.BASE_LOCALE);
                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, key, baseValue));
                // remove old languageItems if resource key has changed
                if ( initialLanguageKey != null && !initialLanguageKey.equals( key ) ) {
                    configurationService.removeLanguageKey( initialLanguageKey );
                }
                ITrackerResources.clearKeyFromBundles(key, true);
                ITrackerResources.clearKeyFromBundles(initialLanguageKey, true);
            }

            // Now reset the cached versions in IssueUtilities
            configurationService.resetConfigurationCache(configItem.getType());

            request.setAttribute("action",action);
            request.setAttribute("value",formValue); 
            String pageTitleKey = "";
            String pageTitleArg = "";
            boolean isUpdate = false;
            
            if("update".equals(request.getAttribute("action"))) {
               isUpdate = true;
               pageTitleKey = "itracker.web.admin.editconfiguration.title.update";
            } else {
               pageTitleKey = "itracker.web.admin.editconfiguration.title.create";
               if("createseverity".equals(request.getAttribute("action"))) {  
                   pageTitleArg = ITrackerResources.getString("itracker.web.attr.severity", this.getCurrLocale());
               } else if("createstatus".equals(request.getAttribute("action"))) {
                   pageTitleArg = ITrackerResources.getString("itracker.web.attr.status", this.getCurrLocale());
               } else if("createresolution".equals(request.getAttribute("action"))) {
                   pageTitleArg = ITrackerResources.getString("itracker.web.attr.resolution", this.getCurrLocale());
               } else {
            	   return mapping.findForward("unauthorized");
                        }
            }
            request.setAttribute("isUpdate",new Boolean(isUpdate)); 
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg); 
            return mapping.findForward("listconfiguration");
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
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }
}
  