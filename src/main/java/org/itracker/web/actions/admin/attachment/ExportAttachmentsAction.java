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

package org.itracker.web.actions.admin.attachment;

import com.sun.tools.internal.ws.wsdl.document.mime.MIMEConstants;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEContent;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.IssueAttachment;
import org.itracker.services.IssueService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ExportAttachmentsAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(DownloadAttachmentAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            IssueService issueService = getITrackerServices().getIssueService();

            List<IssueAttachment> attachments = issueService.getAllIssueAttachments();
            if (attachments.size() > 0) {
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"ITracker_attachments.zip\"");
                ServletOutputStream out = response.getOutputStream();
                ZipOutputStream zipOut = new ZipOutputStream(out);
                try {
                    for (int i = 0; i < attachments.size(); i++) {
                        log.debug("Attempting export for: " + attachments.get(i));
                        byte[] attachmentData = issueService.getIssueAttachmentData(attachments.get(i).getId());
                        if (attachmentData.length > 0) {
                            ZipEntry zipEntry = new ZipEntry(attachments.get(i).getFileName());
                            zipEntry.setSize(attachmentData.length);
                            zipEntry.setTime(attachments.get(i).getLastModifiedDate().getTime());
                            zipOut.putNextEntry(zipEntry);
                            zipOut.write(attachmentData, 0, attachmentData.length);
                            zipOut.closeEntry();
                        }
                    }
                    zipOut.close();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    log.error("Exception while exporting attachments.", e);
                }
                return null;
            }
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noattachments"));
        } catch (Exception e) {
            log.error("Exception while exporting attachments.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);

        }

        return mapping.findForward("error");
    }

}
  