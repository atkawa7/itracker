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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.IssueAttachment;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class RemoveIssueAttachmentAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(RemoveIssueAttachmentAction.class);
	
    public RemoveIssueAttachmentAction() {
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

        try {
            IssueService issueService = getITrackerServices().getIssueService();
            
            try {
                Integer attachmentId = new Integer((request.getParameter("id") == null ? "-1" : request.getParameter("id")));
                IssueAttachment attachment = issueService.getIssueAttachment(attachmentId);

                if(attachment != null) {
                    ConfigurationService configurationService = getITrackerServices().getConfigurationService();

                    File attachmentFile = new File( configurationService.getProperty("attachment_dir") 
                        + File.separator + attachment.getFileName());
                    attachmentFile.delete();

                    issueService.removeIssueAttachment(attachmentId);
                }
            } catch(NumberFormatException nfe) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.removeattachment"));
                if(log.isDebugEnabled()) {
                    log.debug("Invalid attachmentId " + request.getParameter("id") + " specified.");
                }
            }
        } catch(Exception e) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("listattachments");
    }

}
  