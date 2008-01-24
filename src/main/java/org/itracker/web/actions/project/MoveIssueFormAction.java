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

package org.itracker.web.actions.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.MoveIssueForm;

public class MoveIssueFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(MoveIssueFormAction.class);
	
    public MoveIssueFormAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        
        String pageTitleKey = "itracker.web.moveissue.title"; 
		String pageTitleArg = request.getParameter("id");
		request.setAttribute("pageTitleKey",pageTitleKey); 
		request.setAttribute("pageTitleArg",pageTitleArg); 
        
        
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            IssueService issueService = getITrackerServices().getIssueService();
            ProjectService projectService = getITrackerServices().getProjectService();

            Integer issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
            Issue issue = issueService.getIssue(issueId);
            if(issue == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidissue"));
            }

            if(errors.isEmpty()) {
                HttpSession session = request.getSession(true);
                Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);

                if(! UserUtilities.hasPermission(userPermissions, issue.getProject().getId(), UserUtilities.PERMISSION_EDIT)) {
                    log.debug("Unauthorized user requested access to move issue for issue " + issueId);
                    return mapping.findForward("unauthorized");
                }

                List<Project> availableProjects = new ArrayList<Project>();
                List<Project> projects = projectService.getAllAvailableProjects();
                for(int i = 0; i < projects.size(); i++) {
                    if(projects.get(i).getId() != null && issue.getProject() != null && projects.get(i).getId().intValue() != issue.getProject().getId().intValue()) {
                        if(UserUtilities.hasPermission(userPermissions, projects.get(i).getId(), new int[] {UserUtilities.PERMISSION_EDIT, UserUtilities.PERMISSION_CREATE})) {
                            availableProjects.add(projects.get(i));
                        }
                    }
                }
                if(availableProjects.size() == 0) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprojects"));
                }

                if(errors.isEmpty()) {
                    MoveIssueForm moveIssueForm = (MoveIssueForm) form;
                    if(moveIssueForm == null) {
                        moveIssueForm = new MoveIssueForm();
                    }
                    moveIssueForm.setIssueId(issueId);
                    moveIssueForm.setCaller(request.getParameter("caller"));

                    List<Project> availableProjectsList = new ArrayList<Project>();
                    availableProjectsList=availableProjects;
                    if (issue == null || projects == null || projects.size() == 0) {
                    	return mapping.findForward("unauthorized");
                    } else {
                    	
                    	request.setAttribute("moveIssueForm", moveIssueForm);
                    	//session.setAttribute(Constants.PROJECTS_KEY, availableProjectsList);
                    	request.setAttribute("projects", availableProjectsList);
                    	// session.setAttribute(Constants.ISSUE_KEY, issue);
                    	request.setAttribute("issue", issue);
                    	saveToken(request);
                    	log.info("No errors while moving issue. Forwarding to move issue form.");
                    	return mapping.getInputForward();
                    }
                }
            }
        } catch(Exception e) {
            log.error("Exception while creating move issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }

}
  