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
import java.util.Iterator;
import java.util.List;
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
import org.itracker.model.Issue;
import org.itracker.model.Notification;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.NotificationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;



public class WatchIssueAction extends ItrackerBaseAction {

    public WatchIssueAction() {
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
            
            Integer issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));

            Issue issue = issueService.getIssue(issueId);
            
            Project project = issueService.getIssueProject(issueId);
            if(project == null) {
                return mapping.findForward("unauthorized");
            }

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            
            // TODO: never used line, therefore commented, task added:
            //Integer currUserId = currUser.getId();

            if(! UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_VIEW_ALL)) {
                return mapping.findForward("unauthorized");
            }

            Notification notification = new Notification();
            notification.setUser(currUser);
            notification.setIssue(issue);
            notification.setNotificationRole(NotificationUtilities.ROLE_IP);
            
            boolean UserHasIssueNotification = false;
            List<Notification> notifications = issue.getNotifications();
            
            for ( Iterator<Notification> nIterator = notifications.iterator(); nIterator.hasNext(); ) {
                Notification issue_notification = nIterator.next();
                if(issue_notification.getUser().getId().equals(currUser.getId())) {
                    notification = issue_notification;
                    UserHasIssueNotification = true;
                    nIterator.remove();
                    break;
                }
            }
            if ( UserHasIssueNotification ) {
                issue.setNotifications(notifications);
                issueService.removeIssueNotification(notification.getId());
            } else {
                issueService.addIssueNotification(notification);
            }
            String caller = request.getParameter("caller");
            if("editissue".equals(caller)) {
                return new ActionForward(mapping.findForward("editissue").getPath() + "?id=" + issueId);
            } else if("viewissue".equals(caller)) {
                return new ActionForward(mapping.findForward("viewissue").getPath() + "?id=" + issueId);
                //index was the old name for portalhome, we have to clean the naming in this area... 
            } else if("index".equals(caller)) {
                return mapping.findForward("index");
            } else {
                return new ActionForward(mapping.findForward("listissues").getPath() + "?projectId=" + project.getId());
            }
        } catch(Exception e) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.login.system"));
            logger.error("System Error.", e);
        }
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }

}
  