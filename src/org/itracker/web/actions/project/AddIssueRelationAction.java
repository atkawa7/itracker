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
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.services.UserService;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.IssueUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;



public class AddIssueRelationAction extends ItrackerBaseAction {

    public AddIssueRelationAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        Integer issueId = null;
        String caller = "index";

        UserService userService = getITrackerServices().getUserService();
        
        try {

            IssueService issueService = getITrackerServices().getIssueService();
            
            caller = (String) PropertyUtils.getSimpleProperty(form, "caller");
            issueId = (Integer) PropertyUtils.getSimpleProperty(form, "issueId");
            Integer relatedIssueId = (Integer) PropertyUtils.getSimpleProperty(form, "relatedIssueId");
            int relationType = ((Integer) PropertyUtils.getSimpleProperty(form, "relationType")).intValue();

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            // HashMap<Integer,HashSet<Object>> userPermissions = (HashMap) session.getAttribute(Constants.PERMISSIONS_KEY);
            
            Map<Integer, Set<PermissionType>> userPermissions = 
                    userService.getUserPermissions(currUser, AuthenticationConstants.REQ_SOURCE_WEB);
            
            Integer currUserId = currUser.getId();

            Issue issue = issueService.getIssue(issueId);
            if(issue == null || issue.getProject() == null || ! IssueUtilities.canEditIssue(issue, currUserId, userPermissions)) {
                return mapping.findForward("unauthorized");
            }

            Issue relatedIssue = issueService.getIssue(relatedIssueId);
            if(relatedIssue == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.relation.invalidissue"));
            } else if(relatedIssue.getProject() == null || ! IssueUtilities.canEditIssue(relatedIssue, currUserId, userPermissions)) {
                return mapping.findForward("unauthorized");
            } else {
                if(IssueUtilities.hasIssueRelation(issue, relatedIssueId)) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.relation.exists", relatedIssueId));
                }
                if(! issueService.addIssueRelation(issueId, relatedIssueId, relationType, currUser.getId())) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.relation.adderror"));
                }
            }
        } catch(Exception e) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return new ActionForward(mapping.findForward(caller).getPath() + (issueId != null ? "?id=" + issueId : ""));
    }

}
  