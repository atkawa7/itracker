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
import org.itracker.model.WorkflowScript;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectScriptForm;
import org.itracker.web.util.Constants;



public class EditProjectScriptFormAction extends ItrackerBaseAction {

    public EditProjectScriptFormAction() {
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
        boolean isUpdate = false;
        String pageTitleKey = "";
        String pageTitleArg = "";
     
        try {
            ProjectScriptForm workflowScriptForm = (ProjectScriptForm) form;
            if(workflowScriptForm == null) {
                workflowScriptForm = new ProjectScriptForm();
            }
            String action = (String) request.getParameter("action");
            if ( action == null )
                action = (String) PropertyUtils.getSimpleProperty(workflowScriptForm, "action");

            String projectId = (String) request.getParameter("projectId");
            if ( projectId == null )
                projectId = (String) PropertyUtils.getSimpleProperty(workflowScriptForm, "projectId");
            
            if(action != null && action.equals("update")) {
                isUpdate = true;
                pageTitleKey = "itracker.web.admin.editworkflowscript.title.update";
            } else {
                pageTitleKey = "itracker.web.admin.editworkflowscript.title.create";
            }

            WorkflowScript workflowScript = new WorkflowScript();
            if ("update".equals(action)) {
                ConfigurationService configurationService = getITrackerServices().getConfigurationService();
                

                Integer id = (Integer) PropertyUtils.getSimpleProperty(workflowScriptForm, "id");
                workflowScript = configurationService.getWorkflowScript(id);

                if(workflowScript == null) {
                    throw new SystemConfigurationException("Invalid workflow script id " + id);
                }

                workflowScriptForm.setAction("update");
                workflowScriptForm.setId(workflowScript.getId());
                workflowScriptForm.setName(workflowScript.getName());
                workflowScriptForm.setEvent(new Integer(workflowScript.getEvent()));

                workflowScriptForm.setScript(workflowScript.getScript());
            }

            if(errors.isEmpty()) {
								HttpSession session = request.getSession(true);
                request.setAttribute("workflowScriptForm", workflowScriptForm);
                session.setAttribute(Constants.WORKFLOW_SCRIPT_KEY, workflowScript);
                request.setAttribute("action",action);
                saveToken(request);
                request.setAttribute("pageTitleKey",pageTitleKey); 
                request.setAttribute("pageTitleArg",pageTitleArg); 
                return mapping.getInputForward();
            }
        } catch(SystemConfigurationException sce) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidworkflowscript"));
        } catch(Exception e) {
            logger.error("Exception while creating edit workflowScript form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        request.setAttribute("pageTitleKey",pageTitleKey); 
        request.setAttribute("pageTitleArg",pageTitleArg); 
        request.setAttribute("isUpdate",new Boolean(isUpdate)); 
        return mapping.findForward("error");
    }

}
  