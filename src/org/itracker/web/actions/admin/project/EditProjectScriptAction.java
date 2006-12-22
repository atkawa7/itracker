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
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.WorkflowScript;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectScriptForm;
import org.itracker.web.util.Constants;

import bsh.ParseException;


public class EditProjectScriptAction extends ItrackerBaseAction {
    
    public EditProjectScriptAction() {
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
        
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing workflow script.");
            return mapping.findForward("listworkflow");
        }
        resetToken(request);
        ProjectScriptForm projectScriptForm = (ProjectScriptForm) form;
        
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            ProjectService projectService = getITrackerServices().getProjectService();
            String action = request.getParameter("action");
            if ( action == null  )
                action = (String) projectScriptForm.getAction();
            Integer projectId = (Integer) projectScriptForm.getProjectId();
            Project project = projectService.getProject(projectId);
            HashMap<String,String> fieldIds = (HashMap<String,String>) projectScriptForm.getFieldId();
            HashMap<String,String> priorities = (HashMap<String,String>) projectScriptForm.getPriority();
            HashMap<String,String> scriptItems = (HashMap<String,String>) projectScriptForm.getScriptItems();
            HashMap<String,String> ids = (HashMap<String,String>) projectScriptForm.getId();
            for ( Iterator siIterator = scriptItems.keySet().iterator(); siIterator.hasNext(); ) {
                String key = (String) siIterator.next();
                if(key != null) {
                    String scriptItemsvalue = (String) scriptItems.get(key);
                    if(scriptItemsvalue != null && ! scriptItemsvalue.trim().equals("")&& scriptItemsvalue.trim().equals("on")) {
                        Integer wfsIds = Integer.valueOf(key);
                        Integer fieldId = Integer.valueOf((String) fieldIds.get(key));
                        Integer priority = Integer.valueOf((String) priorities.get(key));
                        WorkflowScript workflowScript = configurationService.getWorkflowScript(wfsIds);
                        ProjectScript projectScript = new ProjectScript();
                        Integer id = Integer.valueOf((String) ids.get(key));
                        projectScript.setId(id);
                        ProjectScript chkprojectScript = projectService.getProjectScript(id);
                       
                        projectScript.setFieldId(fieldId);
                        projectScript.setPriority(priority);
                        projectScript.setProject(project);
                        projectScript.setScript(workflowScript);
                        if("create".equals(action) || chkprojectScript == null ) {
                            projectScript = projectService.addProjectScript(projectId, projectScript);
                        } else {
                            projectScript = projectService.updateProjectScript(projectScript);
                        }
                        if (projectScript == null) {
                            throw new Exception("Error creating/updating project script.");
                        }
                    }
                }
            }
            
            
            HttpSession session = request.getSession(true);
            session.removeAttribute(Constants.PROJECT_SCRIPT_KEY);
            request.setAttribute("action",action);
            saveToken(request);
            return new ActionForward(
                    mapping.findForward("editproject").getPath()
                    + "?id=" + project.getId() +"&action=update");
        } catch(ParseException pe) {
            logger.debug("Error parseing script.  Redisplaying form for correction.", pe);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidscriptdata", pe.getMessage()));
            saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }
    
}
