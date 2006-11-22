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

package org.itracker.web.actions.admin.project;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

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
import org.itracker.model.Component;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ComponentForm;
import org.itracker.web.util.Constants;


public class EditComponentAction extends ItrackerBaseAction {

    public EditComponentAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        ActionErrors errors = new ActionErrors();
        
        super.executeAlways(mapping,form, request, response);
        
        if (!isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        
        if (!isTokenValid(request)) {
            logger.debug("Invalid request token while editing component.");
            
            return mapping.findForward("listprojectsadmin");
        }
        resetToken(request);
        
        final HttpSession session = request.getSession(true);
        final Map<Integer, Set<PermissionType>> userPermissions = 
                getUserPermissions(session);
        final ProjectService projectService = getITrackerServices().getProjectService();
        
        try {
            ComponentForm componentForm = (ComponentForm)form; 
            Integer projectId = componentForm.getProjectId();
            Project project;
            
            if (projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("itracker.web.error.invalidproject"));
                saveMessages(request, errors);
                
                return mapping.findForward("error");
            }
            project = projectService.getProject(projectId);
            
            if (project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("itracker.web.error.invalidproject"));
                saveMessages(request, errors);
                
                return mapping.findForward("error");
            } else if (!UserUtilities.hasPermission(userPermissions, 
                    project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            
            final String action = (String) request.getParameter("action");
            
            if ("create".equals(action)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating component for project " + componentForm.getProjectId());
                }
                
                Component component = new Component();
                component.setName(componentForm.getName());
                component.setDescription(componentForm.getDescription());
                component.setCreateDate(new Date());

                component = projectService.addProjectComponent(project.getId(), component);
            } else if ("update".equals(action)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Updating component for project " + componentForm.getProjectId());
                }
                Component component = projectService.getProjectComponent(componentForm.getId());
                component.setLastModifiedDate(new Date());

                component.setName(componentForm.getName());
                component.setDescription(componentForm.getDescription());
                component.setProject(project);

                component = projectService.updateProjectComponent(component);

                session.removeAttribute(Constants.COMPONENT_KEY);
            }
        } catch (Exception ex) {
            logger.error("Exception processing form data", ex);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveMessages(request, errors);
            
            return mapping.findForward("error");
        }
        return mapping.findForward("editproject");
    }
    
}
