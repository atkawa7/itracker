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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.NameValuePair;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.services.util.Convert;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;



public class CreateIssueFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(CreateIssueFormAction.class);
	
    public CreateIssueFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
//        super.executeAlways(mapping,form,request,response);
//        
//        
//        
//        if(! isLoggedIn(request, response)) {
//            return mapping.findForward("login");
//        }

        try {
            ProjectService projectService = getITrackerServices().getProjectService();
            UserService userService = getITrackerServices().getUserService();

            Integer projectId = new Integer((request.getParameter("projectId") == null ? "-1" : (request.getParameter("projectId"))));

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> Permissions = getUserPermissions(session);
            Locale locale = LoginUtilities.getCurrentLocale(request);

            if(! UserUtilities.hasPermission(Permissions, projectId, UserUtilities.PERMISSION_CREATE)) {
                log.debug("Unauthorized user requested access to create issue for project " + projectId);
                return mapping.findForward("unauthorized");
            }

            Project project = projectService.getProject(projectId);
            log.debug("Received request for project " + projectId + "(" + project.getName() + ")");

            if(project == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            } else if(project.getStatus() != Status.ACTIVE) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            }

            if(errors.isEmpty()) {
                Map<Integer,List<NameValuePair>> listOptions = new HashMap<Integer,List<NameValuePair>>();
                if(UserUtilities.hasPermission(Permissions, project.getId(), UserUtilities.PERMISSION_ASSIGN_OTHERS)) {
                    List<User> possibleOwners = userService.getPossibleOwners(null, project.getId(), currUser.getId());
                    Collections.sort(possibleOwners, User.NAME_COMPARATOR);
                    listOptions.put(IssueUtilities.FIELD_OWNER, Convert.usersToNameValuePairs(possibleOwners));
                } else if(UserUtilities.hasPermission(Permissions, project.getId(), UserUtilities.PERMISSION_ASSIGN_SELF)) {
                	NameValuePair myNameValuePair = new NameValuePair(currUser.getFirstName() + " " + currUser.getLastName(), currUser.getId().toString());
                	List<NameValuePair> myNameValuePairList =  new ArrayList<NameValuePair>();
                	myNameValuePairList.add(myNameValuePair);
                    listOptions.put(IssueUtilities.FIELD_OWNER,myNameValuePairList);
                }

                if(UserUtilities.hasPermission(Permissions, project.getId(), UserUtilities.PERMISSION_CREATE_OTHERS)) {
                    List<User> possibleCreators = userService.getUsersWithAnyProjectPermission(project.getId(), new int[] {UserUtilities.PERMISSION_VIEW_ALL, UserUtilities.PERMISSION_VIEW_USERS});
                    Collections.sort(possibleCreators, User.NAME_COMPARATOR);
                    listOptions.put(IssueUtilities.FIELD_CREATOR, Convert.usersToNameValuePairs(possibleCreators));
                }

                List<NameValuePair> severities = IssueUtilities.getSeverities(locale);
        		// sort by severity code so it will be ascending output.
        		Collections.sort(severities, NameValuePair.VALUE_COMPARATOR);
                listOptions.put(IssueUtilities.FIELD_SEVERITY, severities);

                List<Component> components = project.getComponents();
                Collections.sort(components, Component.NAME_COMPARATOR);
                listOptions.put(IssueUtilities.FIELD_COMPONENTS, Convert.componentsToNameValuePairs(components));
                List<Version> versions = project.getVersions();
                Collections.sort(versions, new Version.VersionComparator());
                listOptions.put(IssueUtilities.FIELD_VERSIONS, Convert.versionsToNameValuePairs(versions));


                List<CustomField> projectFields = project.getCustomFields();
                for(int i = 0; i < projectFields.size(); i++) {
                    if(projectFields.get(i).getFieldType() == CustomField.Type.LIST) {
                        projectFields.get(i).setLabels(locale);
                        listOptions.put(projectFields.get(i).getId(), Convert.customFieldOptionsToNameValuePairs(projectFields.get(i).getOptions()));
                    }
                }

                IssueForm issueForm = (IssueForm) form;
                if(issueForm == null) {
                    issueForm = new IssueForm();
                }
                issueForm.setCreatorId(currUser.getId());
                if(severities.size() > 0) {
                    try {
                       	// this sets the selected severity to a medium level (middleSeverity). It was argued that this is not simple to understand and therefore needs simplification or refactoring
                        int middleSeverity = (severities.size() / 2);
                        issueForm.setSeverity(Integer.valueOf(severities.get(middleSeverity).getValue()));
                    } catch(NumberFormatException nfe) {
                        log.debug("Invalid status number found while preparing create issue form.", nfe);
                    }
                }

                if(versions.size() > 0) {
                    issueForm.setVersions(new Integer[] { versions.get(0).getId() } );
                }
                
                String pageTitleKey = "itracker.web.createissue.title"; 
      		    String pageTitleArg = project.getName();
      		    request.setAttribute("pageTitleKey",pageTitleKey); 
      		    request.setAttribute("pageTitleArg",pageTitleArg); 

                List<ProjectScript> scripts = project.getScripts();
                WorkflowUtilities.processFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONPOPULATE, listOptions, errors, issueForm);
                WorkflowUtilities.processFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, null, errors, issueForm);

                if(errors == null || errors.isEmpty()) {
                    log.debug("Forwarding to create issue form for project " + project.getId());
                    request.setAttribute("issueForm", issueForm);
                    session.setAttribute(Constants.PROJECT_KEY, project);
                    session.setAttribute(Constants.LIST_OPTIONS_KEY,listOptions);
                    saveToken(request);
                    return mapping.getInputForward();
                }
            }
        } catch(RuntimeException e) {
            log.error("Exception while creating create issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (WorkflowException e) {
			// TODO Auto-generated catch block
            log.error("Exception while creating create issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
		}

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }
}
  