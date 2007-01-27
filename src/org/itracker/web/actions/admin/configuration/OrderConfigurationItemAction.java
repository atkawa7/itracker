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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.Configuration;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;



public class OrderConfigurationItemAction extends ItrackerBaseAction {
    
    public OrderConfigurationItemAction() {
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
            
            Integer configId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            if(configId == null || configId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }
            
            Configuration configItem = configurationService.getConfigurationItem(configId);
            if(configItem == null) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }
            
            int configType = configItem.getType();
            List<Configuration> configItems = configurationService.getConfigurationItemsByType(configType);
            List<Configuration> newConfigItems = new ArrayList<Configuration>();
            
            for(int i = 0; i < configItems.size(); i++) {
                newConfigItems.add(configItems.get(i));
            }
            for(int i = 0; i < configItems.size(); i++) {
                if ( configItems.get(i) != null ) {
                    Configuration firstConfiguration = new Configuration();
                    Configuration secondConfiguration = new Configuration();
                    Configuration curConfiguration = (Configuration) configItems.get(i);
                    int todo_i = -1;
                    if ( curConfiguration.getId() == configId ) {
                        if ("up".equals(action) ){
                            todo_i = i - 1;
                        } else {
                            todo_i = i + 1;
                        }
                        Configuration todoConfiguration = (Configuration) configItems.get(todo_i);

                        firstConfiguration.setId(curConfiguration.getId());
                        firstConfiguration.setCreateDate(todoConfiguration.getCreateDate());
                        firstConfiguration.setLastModifiedDate(todoConfiguration.getLastModifiedDate());
                        firstConfiguration.setName(todoConfiguration.getName());
                        firstConfiguration.setOrder(todoConfiguration.getOrder());
                        firstConfiguration.setType(todoConfiguration.getType());
                        firstConfiguration.setValue(todoConfiguration.getValue());
                        firstConfiguration.setVersion(todoConfiguration.getVersion());


                        secondConfiguration.setId(todoConfiguration.getId());
                        secondConfiguration.setCreateDate(curConfiguration.getCreateDate());
                        secondConfiguration.setLastModifiedDate(curConfiguration.getLastModifiedDate());
                        secondConfiguration.setName(curConfiguration.getName());
                        secondConfiguration.setOrder(curConfiguration.getOrder());
                        secondConfiguration.setType(curConfiguration.getType());
                        secondConfiguration.setValue(curConfiguration.getValue());
                        secondConfiguration.setVersion(curConfiguration.getVersion());

                        newConfigItems.set(todo_i,firstConfiguration);
                        newConfigItems.set(i,secondConfiguration);
                    }
                }
            }
            
            newConfigItems = configurationService.updateConfigurationItems(newConfigItems,configType);
            
            // Only resolutions and severities can be reordered at this point.  Statuses
            // and some basic workflow depend on the actual value of the status, so
            // the order must equal the value of the status for it to work correctly.
            if(configType == SystemConfigurationUtilities.TYPE_RESOLUTION) {
                configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_RESOLUTION);
            } else if(configType == SystemConfigurationUtilities.TYPE_SEVERITY) {
                configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_SEVERITY);
            }
            
            return mapping.findForward("listconfiguration");
        } catch(SystemConfigurationException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
            logger.debug("Invalid configuration item id " + request.getParameter("id") + " specified.");
        } catch(NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
            logger.debug("Invalid configuration item id " + request.getParameter("id") + " specified.");
        } catch(Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            logger.error("System Error.", e);
        }
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }
    
}
