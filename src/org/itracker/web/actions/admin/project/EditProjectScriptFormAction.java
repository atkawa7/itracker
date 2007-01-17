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
import java.util.ArrayList;
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
import org.itracker.model.CustomField;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.WorkflowScript;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.ProjectUtilities;
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
        String action = "";
        Project project = null;
        
        try {
            ProjectScriptForm projectScriptForm = (ProjectScriptForm) form;
            ProjectService projectService = getITrackerServices().getProjectService();
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            
            if(projectScriptForm == null) {
                projectScriptForm = new ProjectScriptForm();
            }
            List<ProjectScript> projectScripts = new ArrayList<ProjectScript>();
            List<WorkflowScript> workflowScripts = configurationService.getWorkflowScripts();
            
            
            action = request.getParameter("action");
            if ( action == null )
                action = (String) PropertyUtils.getSimpleProperty(projectScriptForm, "action");
            projectScriptForm.setAction(action);
            
            Integer projectId = (Integer) PropertyUtils.getSimpleProperty(projectScriptForm, "projectId");
            projectScriptForm.setProjectId(projectId);
            project = projectService.getProject(projectId);
            projectScripts = project.getScripts();
            
//            if(action != null && action.equals("update")) {
//                isUpdate = true;
//                pageTitleKey = "itracker.web.admin.editprojectscript.title.update";
                
//            } else {
                pageTitleKey = "itracker.web.admin.editprojectscript.title.create";
//            }
            HashMap<String,String> scriptDescs = new HashMap<String,String>();
            HashMap<String,String> scriptItems = new HashMap<String,String>();
            HashMap<String,String> ids = new HashMap<String,String>();
            HashMap<String,String> fieldIds = new HashMap<String,String>();
            HashMap<String,String> priorities = new HashMap<String,String>();
//            Integer[] Ids = new Integer[workflowScripts.size()];
//            Integer[] fieldIds = new Integer[workflowScripts.size()];
//            Integer[] priorities = new Integer[workflowScripts.size()];
            int i = 0;
//            if ("update".equals(action)) {
            for ( Iterator<WorkflowScript> wfsIterator = workflowScripts.iterator(); wfsIterator.hasNext(); i++) {
                WorkflowScript workflowScript = (WorkflowScript) wfsIterator.next();
                scriptDescs.put(String.valueOf(workflowScript.getId()), workflowScript.getName());
                String idstr = "0";
                String sdstr = "";
                String fidstr = "";
                String pristr = "";
                for ( Iterator<ProjectScript> psIterator = projectScripts.iterator(); psIterator.hasNext(); ) {
                    ProjectScript chkprojectScript = psIterator.next();
                    if ( workflowScript.getId() == chkprojectScript.getScript().getId() ) {
                        idstr = String.valueOf(chkprojectScript.getId());
                        fidstr = String.valueOf(chkprojectScript.getFieldId());
                        pristr = String.valueOf(chkprojectScript.getPriority());
                        sdstr = "on";
                    }
                }
                scriptItems.put(String.valueOf(workflowScript.getId()), sdstr);
                ids.put(String.valueOf(workflowScript.getId()), idstr);
                fieldIds.put(String.valueOf(workflowScript.getId()), fidstr);
                priorities.put(String.valueOf(workflowScript.getId()), pristr);
            }
/*            } else {
                for ( Iterator<WorkflowScript> wfsIterator = workflowScripts.iterator(); wfsIterator.hasNext(); i++) {
                    WorkflowScript workflowScript = (WorkflowScript) wfsIterator.next();
                    scriptItems.put(String.valueOf(workflowScript.getId()), "");
                    scriptDescs.put(String.valueOf(workflowScript.getId()), workflowScript.getName());
                    ids.put(String.valueOf(workflowScript.getId()), "0");
                    fieldIds.put(String.valueOf(workflowScript.getId()), "");
                    priorities.put(String.valueOf(workflowScript.getId()), "");
                }
            }
 **/
            projectScriptForm.setScriptDescs(scriptDescs);
            projectScriptForm.setScriptItems(scriptItems);
            projectScriptForm.setId(ids);
            projectScriptForm.setFieldId(fieldIds);
            projectScriptForm.setPriority(priorities);
            
            projectScriptForm.setCustomFields(configurationService.getCustomFields());
            String prioritySizeStr = ProjectUtilities.getScriptPrioritySize();
            int prioritySize = Integer.parseInt(prioritySizeStr);
            
            HashMap<String,String> priorityList = new HashMap<String,String>();
            for ( int j = 1; j <= prioritySize; j++ ) {
                priorityList.put(String.valueOf(j), ProjectUtilities.getScriptPriorityLabelKey(j));
            }
            projectScriptForm.setPriorityList(priorityList);
            
            if(errors.isEmpty()) {
                HttpSession session = request.getSession(true);
                request.setAttribute("projectScriptForm", projectScriptForm);
                session.setAttribute(Constants.PROJECT_SCRIPT_KEY, project);
                request.setAttribute("action",action);
                saveToken(request);
                request.setAttribute("pageTitleKey",pageTitleKey);
                request.setAttribute("pageTitleArg",pageTitleArg);
                return mapping.getInputForward();
            }
        } catch(Exception e) {
            logger.error("Exception while the "+ action + " of ProjectScript form.", e);
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
