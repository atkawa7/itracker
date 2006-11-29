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
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.NotificationUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class AssignIssueAction extends ItrackerBaseAction {

    public AssignIssueAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            IssueService issueService = getITrackerServices().getIssueService();
            ProjectService projectService = getITrackerServices().getProjectService();

            Integer defaultValue = new Integer(-1);
            IntegerConverter converter = new IntegerConverter(defaultValue);
            Integer issueId = (Integer) converter.convert(Integer.class, (String) PropertyUtils.getSimpleProperty(form, "issueId"));
            Integer projectId = (Integer) converter.convert(Integer.class, (String) PropertyUtils.getSimpleProperty(form, "projectId"));
            Integer userId = (Integer) converter.convert(Integer.class, (String) PropertyUtils.getSimpleProperty(form, "userId"));

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            Integer currUserId = currUser.getId();

            Project project = projectService.getProject(projectId);
            if(project == null) {
                return mapping.findForward("unauthorized");
            }

            if(! userId.equals(currUserId) && ! UserUtilities.hasPermission(userPermissions, projectId, UserUtilities.PERMISSION_ASSIGN_OTHERS)) {
                return mapping.findForward("unauthorized");
            } else if(! UserUtilities.hasPermission(userPermissions, projectId, UserUtilities.PERMISSION_ASSIGN_SELF)) {
                return mapping.findForward("unauthorized");
            }

            if(project.getStatus() != ProjectUtilities.STATUS_ACTIVE) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            } else {
                issueService.assignIssue(issueId, userId, currUserId);
                issueService.sendNotification(issueId, NotificationUtilities.TYPE_ASSIGNED, getBaseURL(request));
            }
        } catch(Exception e) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            saveToken(request);
        }
        return mapping.findForward("index");
    }

}
  