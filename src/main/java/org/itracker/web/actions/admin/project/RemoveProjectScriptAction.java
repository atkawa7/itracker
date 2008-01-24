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

//import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectScriptForm;
import org.itracker.web.util.Constants;


public class RemoveProjectScriptAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(RemoveProjectScriptAction.class);
	
    public RemoveProjectScriptAction() {
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
        
/*        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing workflow script.");
            return new ActionForward(
                    mapping.findForward("editproject").getPath()
                    + "?id=" + project.getId() +"&action=update");
        }
*/
        resetToken(request);
        ProjectScriptForm projectScriptForm = (ProjectScriptForm) form;
        
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            ProjectService projectService = getITrackerServices().getProjectService();
            String id = request.getParameter("delId");
            
            ProjectScript projectScript = projectService.getProjectScript(Integer.valueOf(id));
            Project project = projectScript.getProject();
            boolean status = projectService.removeProjectScript(project.getId(), Integer.valueOf(id));
            if ( ! status ) {
                log.debug("Error deleting script.  Redisplaying form for correction.");
            }
            HttpSession session = request.getSession(true);
            session.removeAttribute(Constants.PROJECT_SCRIPT_KEY);
            request.setAttribute("action","update");
            saveToken(request);
            return new ActionForward(
                    mapping.findForward("editproject").getPath()
                    + "?id=" + project.getId() +"&action=update");
        } catch(Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }
    
}
