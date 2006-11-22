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
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Version;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.VersionForm;
import org.itracker.web.util.Constants;

/**        logger.info("EDIT VERSION FORM");
 * 
 */
public class EditVersionFormAction extends ItrackerBaseAction {
    
    public EditVersionFormAction() {
    }
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        logger.info("EDIT VERSION FORM ACTION");
        
        if (true) {
            return null;
        }
        
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        String pageTitleKey = "";
        String pageTitleArg = "";
        
        if (!isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        
        VersionForm versionForm = (VersionForm)form;
                
        try {
            HttpSession session = request.getSession(true);
            String action = (String) request.getParameter("action");
            final Map<Integer, Set<PermissionType>> userPermissions =
                    getUserPermissions(session);
            
            Integer projectId = versionForm.getProjectId();
            
            if (projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("itracker.web.error.invalidproject"));
                saveMessages(request, errors);
                
                return mapping.findForward("error");
            }
            
            ProjectService projectService = getITrackerServices().getProjectService();
            Project project = projectService.getProject(projectId);
            
            if (project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("itracker.web.error.invalidproject"));
                saveMessages(request, errors);
                
                return mapping.findForward("error");
            } else if(!UserUtilities.hasPermission(userPermissions,
                    project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            
            boolean authorized = UserUtilities.hasPermission(userPermissions,
                    project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN);
            
            Version version;
            
            if ("create".equals(action)) {
                version = new Version();
                version.setCreateDate(new Date());
                version.setNumber(versionForm.getNumber());
                version.setDescription(versionForm.getDescription());
                version.setProject(project);
                
                 pageTitleKey = "itracker.web.admin.editversion.title.create";
            } else if ("update".equals(action)) {
                Integer versionId = versionForm.getId();
                
                //version = (Version) session.getAttribute(Constants.VERSION_KEY);
                version = projectService.getProjectVersion(versionId);
                
                if (version == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage("itracker.web.error.invalidversion"));
                    
                    saveMessages(request, errors);
                    
                    return mapping.findForward("error");
                }
                version.setLastModifiedDate(new Date());
                version.setNumber(versionForm.getNumber());
                version.setDescription(versionForm.getDescription());
                
                pageTitleKey = "itracker.web.admin.editversion.title.update";
                pageTitleArg = versionForm.getNumber();
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("itracker.web.error.invalidaction"));
                
                saveMessages(request, errors);
                
                return mapping.findForward("error");
            }
            
            if (version == null) {
                throw new RuntimeException("null version");
            }
            
            session.setAttribute(Constants.VERSION_KEY, version);
            
            if (!errors.isEmpty()) {
                saveMessages(request, errors);
                
                return mapping.findForward("error");
            }
        } catch (Exception ex) {
            pageTitleKey = "itracker.web.error.title";
            request.setAttribute("pageTitleKey",pageTitleKey);
            request.setAttribute("pageTitleArg",pageTitleArg);
            logger.error("Exception while creating edit version form.", ex);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        request.setAttribute("versionForm", versionForm);
        
        saveToken(request);

        return mapping.getInputForward();
    }
    
}
