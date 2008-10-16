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
import org.apache.log4j.Logger;
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
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;



public class MoveIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(MoveIssueAction.class);
	
    public MoveIssueAction() {
    }
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
//        super.executeAlways(mapping,form,request,response);
        
        String pageTitleKey = "itracker.web.moveissue.title";
        String pageTitleArg = request.getParameter("issueId");
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
//        if(! isLoggedIn(request, response)) {
//            return mapping.findForward("login");
//        }
        if(! isTokenValid(request)) {
            log.debug("Invalid request token while creating issue.");
            return mapping.findForward("index");
        }
        resetToken(request);
        
        try {
            IssueService issueService = getITrackerServices().getIssueService();
            
            Integer issueId = (Integer) PropertyUtils.getSimpleProperty(form, "issueId");
            Integer projectId = (Integer) PropertyUtils.getSimpleProperty(form, "projectId");
            String caller = (String) PropertyUtils.getSimpleProperty(form, "caller");
            if(caller == null) {
                caller = "index";
            }
            
            Issue issue = issueService.getIssue(issueId);
            if(issue == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidissue"));
            }
            
            if (issue.getProject() != null && issue.getProject().getId().equals(projectId)) {
            	// is already on this issue
            	log.error("execute: attempted to move issue to its containing project");
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            }
            
            if(errors.isEmpty()) {
                HttpSession session = request.getSession(true);
                User user = (User) session.getAttribute(Constants.USER_KEY);
                Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
                
                if(! UserUtilities.hasPermission(userPermissions, issue.getProject().getId(), UserUtilities.PERMISSION_EDIT)) {
                    log.debug("User not authorized to move issue " + issueId);
                    return mapping.findForward("unauthorized");
                }
                if(! UserUtilities.hasPermission(userPermissions, projectId, new int[] {UserUtilities.PERMISSION_EDIT, UserUtilities.PERMISSION_CREATE})) {
                    log.debug("User attempted to move issue " + issueId + " to unauthorized project.");
                    return mapping.findForward("unauthorized");
                }
                
                issueService.moveIssue(issue, projectId, user.getId());
                //TODO these session attribute are not needed because they are request attributes now.
                session.removeAttribute(Constants.PROJECTS_KEY);
                session.removeAttribute(Constants.ISSUE_KEY);
                
                if("editissue".equals((String) PropertyUtils.getSimpleProperty(form, "caller"))) {
                    log.info("go to forward editissue");
                    return new ActionForward(mapping.findForward("editissue").getPath() + "?id=" + issue.getId());
                } else if("viewissue".equals((String) PropertyUtils.getSimpleProperty(form, "caller"))) {
                    log.info("go to forward viewissue");
                    return new ActionForward(mapping.findForward("move_view_issue").getPath() + "?id=" + issue.getId());
                } else {
                    return mapping.findForward("index");
                }
            }
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
