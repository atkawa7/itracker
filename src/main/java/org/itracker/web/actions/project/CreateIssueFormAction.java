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

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.*;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.services.util.Convert;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.ptos.CreateIssuePTO;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class CreateIssueFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(CreateIssueFormAction.class);

    public CreateIssueFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        //  TODO: Action Cleanup

        try {
            ProjectService projectService = getITrackerServices()
                    .getProjectService();
            UserService userService = getITrackerServices().getUserService();

            Integer projectId = new Integer(
                    (request.getParameter("projectId") == null ? "-1"
                            : (request.getParameter("projectId"))));

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> permissions = getUserPermissions(session);
            Locale locale = LoginUtilities.getCurrentLocale(request);

            if (!UserUtilities.hasPermission(permissions, projectId,
                    UserUtilities.PERMISSION_CREATE)) {
                log
                        .debug("Unauthorized user requested access to create issue for project "
                                + projectId);
                return mapping.findForward("unauthorized");
            }

            Project project = projectService.getProject(projectId);
            if (log.isDebugEnabled() && project != null) {
                log.debug("execute: Received request for project " + projectId + "("
                        + project.getName() + ")");
            }
            if (project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidproject"));
            } else if (project.getStatus() != Status.ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.projectlocked"));
            }

            if (errors.isEmpty()) {
                
                
                final List<User> owners = new ArrayList<User>();  
                               
                if (UserUtilities.hasPermission(permissions, project.getId(),
                        UserUtilities.PERMISSION_ASSIGN_OTHERS)) {
                                                      
                    owners.addAll(userService.getPossibleOwners(
                                                null, project.getId(), currUser.getId()));
                    Collections.sort(owners, User.NAME_COMPARATOR);
                }  else if (UserUtilities.hasPermission(permissions, project
                                        .getId(), UserUtilities.PERMISSION_ASSIGN_SELF)) {
                     owners.add(currUser);
                }
                
                final Map<Integer, List<NameValuePair>> listOptions = EditIssueActionUtil.getListOptions(request, null, 
                        Convert.usersToNameValuePairs(owners), 
                        permissions, project, currUser);
                
                if (UserUtilities.hasPermission(permissions, project.getId(),
                        UserUtilities.PERMISSION_CREATE_OTHERS)) {
                    List<User> possibleCreators = userService
                            .getUsersWithAnyProjectPermission(
                                    project.getId(),
                                    new int[]{
                                            UserUtilities.PERMISSION_VIEW_ALL,
                                            UserUtilities.PERMISSION_VIEW_USERS});
                    Collections.sort(possibleCreators, User.NAME_COMPARATOR);
                    listOptions.put(IssueUtilities.FIELD_CREATOR, Convert
                            .usersToNameValuePairs(possibleCreators));
                }

                IssueForm issueForm = (IssueForm) form;
                if (issueForm == null) {
                    issueForm = new IssueForm();
                }
                issueForm.setCreatorId(currUser.getId());

                // Severity by configured default value or Major (2)
                List<Configuration> severities ;
                Integer severity = ServletContextUtils.getItrackerServices()
                        .getConfigurationService().getIntegerProperty("default_severity", 2);
                issueForm.setSeverity(severity);

                String pageTitleKey = "itracker.web.createissue.title";
                String pageTitleArg = project.getName();
                request.setAttribute("pageTitleKey", pageTitleKey);
                request.setAttribute("pageTitleArg", pageTitleArg);

                // populate the possible list options
                EditIssueActionUtil.invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONPOPULATE, listOptions, errors, issueForm);

                EditIssueActionUtil.invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, errors, issueForm);


                if (errors == null || errors.isEmpty()) {
                    log.debug("Forwarding to create issue form for project "
                            + project.getId());
                    request.setAttribute("issueForm", issueForm);
                    session.setAttribute(Constants.PROJECT_KEY, project);
                    session.setAttribute(Constants.LIST_OPTIONS_KEY,
                            listOptions);
                    saveToken(request);

                    if (project == null) {
                        return mapping.findForward("unauthorized");

                    } else {
                        CreateIssuePTO.setupCreateIssue(request);
                        return mapping.getInputForward();
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error("Exception while creating create issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } catch (WorkflowException e) {

            log.error("Exception while creating create issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }
}
