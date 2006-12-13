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
        
        Component component = null;
        Project project = null;
        
        try {
            ComponentForm componentForm = (ComponentForm)form; 
            ProjectService projectService = getITrackerServices().getProjectService();
            
            HttpSession session = request.getSession(true);
            Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
            
            Integer projectId = componentForm.getProjectId();
            
            if (projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("itracker.web.error.invalidproject"));
            } else {
                project = projectService.getProject(projectId);
                
                boolean authorised = UserUtilities.hasPermission(userPermissionsMap, 
                        project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN);
                
                if (project == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, 
                            new ActionMessage("itracker.web.error.invalidproject"));
                } else if (!authorised) {
                    return mapping.findForward("unauthorized");
                } else {
                    String action = (String) request.getParameter("action");
           
                    if ("create".equals(action)) {
                        component = new Component(project, componentForm.getName());
                        component.setDescription(componentForm.getDescription());
                        component.setStatus(1);
                        component.setCreateDate(new Date());
                        component = projectService.addProjectComponent(project.getId(), component);
                    } else if ("update".equals(action)) {
                        component = projectService.getProjectComponent(componentForm.getId());
                        component.setLastModifiedDate(new Date());
                        component.setName(componentForm.getName());
                        component.setDescription(componentForm.getDescription());
                        component.setProject(project);
                        component.setStatus(1);
                        component = projectService.updateProjectComponent(component);
                    }
                    session.removeAttribute(Constants.COMPONENT_KEY);
                
                    return new ActionForward(
                            mapping.findForward("editproject").getPath() 
                            + "?id=" + project.getId() +"&action=update");
                }
            }
        } catch (Exception ex) {
            logger.error("Exception processing form data", ex);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveMessages(request, errors);
        }    
        return mapping.findForward("error");
    }
    
}
