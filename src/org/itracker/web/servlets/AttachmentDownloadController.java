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

package org.itracker.web.servlets;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.IssueAttachment;
import org.itracker.model.User;
import org.itracker.services.IssueService;

/**
 * @deprecated Use org.itracker.web.actions.admin.attachment.DownloadAttachmentAction instead.
 */
public class AttachmentDownloadController extends GenericController {

    public AttachmentDownloadController() {
    }

    public void init(ServletConfig config) {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream out = null;

        if(! isLoggedInWithRedirect(request, response)) {
            return;
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        try {
            IssueService issueService = getITrackerServices(request.getSession().getServletContext() ).getIssueService();

            Integer attachmentId = null;
            IssueAttachment attachment = null;

            try {
                attachmentId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
                attachment = issueService.getIssueAttachment(attachmentId);
            } catch(NumberFormatException nfe) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Invalid attachmentId " + request.getParameter("id") + " specified.");
                }
            }

            if(attachment == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidattachment"));
                saveMessages(request, errors);
                forward("/themes/defaulttheme/error.jsp", request, response);
                return;
            }

            if(! issueService.canViewIssue(attachment.getIssue().getId(), user)) {
                forward("/themes/defaulttheme/unauthorized.jsp", request, response);
                return;
            }

            byte[] fileData = issueService.getIssueAttachmentData(attachmentId);
            if(fileData == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingattachmentdata"));
                saveMessages(request, errors);
                forward("/themes/defaulttheme/error.jsp", request, response);
                return;
            }

            response.setContentType(attachment.getType());
            response.setHeader("Content-Disposition", "inline; filename=" + attachment.getOriginalFileName() + "");
            out = response.getOutputStream();
            logger.debug("Displaying attachment " + attachment.getId() + " of type " + attachment.getType() + " to client.  Attachment size: " + fileData.length);
            out.write(fileData);
        } catch(IOException ioe) {
            logger.info("Unable to display attachment.", ioe);
        } catch(Exception e) {
            logger.error( e.getMessage(), e );
        } finally {
            if(out != null) {
                out.flush();
                out.close();
            }
        }

        return;
    }
}   