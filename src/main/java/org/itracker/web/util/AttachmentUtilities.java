/* This software was designed and created by Jason Carroll.
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
package org.itracker.web.util;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.IssueAttachment;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ITrackerServices;
import org.itracker.services.IssueService;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.web.forms.IssueForm;

import java.io.IOException;

public class AttachmentUtilities {

    private static final Logger logger = Logger.getLogger(AttachmentUtilities.class);
    private static boolean initialized = false;
    private static final String CONTENT_TYPE = "multipart/form-data";
    private static final String CHAR_ENCODING = "ISO-8859-1";
    private static final long MAX_FILE_SIZE_KB = 256L;
    private static final long MAX_TOTAL_FILE_SIZE_KB = 1000000L;

    private static long maxFileSize = MAX_FILE_SIZE_KB * 1024L;
    private static long maxTotalFileSize = MAX_TOTAL_FILE_SIZE_KB * 1024L;
    private static long spaceLeft = 0;


    /**
     * @deprecated use {@link AttachmentUtilities#validate(FormFile, ITrackerServices)} instead for more detailed messages
     */
    public static boolean checkFile(FormFile file, ITrackerServices services) {

        return validate(file, services).isEmpty();
//    	
//    	
//        if(! initialized) {
//           if(! init(services)) {
//              return false;
//           }
//        }
//
//        if(file == null) {
//            return false;
//        }
//
//        long origFileSize = file.getFileSize();
//        if(origFileSize > maxFileSize) {
//            logger.info("Cannot save attachment.  File is " + (origFileSize / 1024L) + " kB and max file size is set to " + (maxFileSize / 1024L) + "kB.");
//            return false;
//        }
//
//        if((spaceLeft - origFileSize) < 0) {
//            logger.info("Cannot save attachment.  Total allocated disk space already used.");
//            return false;
//        }
//        spaceLeft = spaceLeft - origFileSize;
//          // @TODO: please check this code and clean up 
////            filename = attachmentDirName + File.separator + filename;
//
////            if(logger.isDebugEnabled()) {
////                logger.debug("Attempting to save attachment: " + filename);
////            }
//
////            FileOutputStream out = new FileOutputStream(filename);
////            out.write(file.getFileData());
////            out.close();
//        return true;
    }

    /**
     * Adds an attachment to issue.
     *
     * @return updated issue
     */
    public static Issue addAttachment(Issue issue, Project project, User user,
                                      IssueForm form, ITrackerServices services, ActionMessages messages) {


        FormFile file = form.getAttachment();

        if (file == null || file.getFileName().trim().length() < 1) {
            logger.info("addAttachment: skipping file " + file);
            return issue;
        }

        if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS,
                project.getOptions())) {
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.attachment.disabled", project.getName()));
            return issue;
        }

        String origFileName = file.getFileName();
        String contentType = file.getContentType();
        int fileSize = file.getFileSize();

        String attachmentDescription = form.getAttachmentDescription();

        if (null == contentType || 0 >= contentType.length()) {
            logger.info("addAttachment: got no mime-type, using default plain-text");
            contentType = "text/plain";
        }

        if (logger.isDebugEnabled()) {
            logger.debug("addAttachment: adding file, name: " + origFileName
                    + " of type " + file.getContentType() + ", description: "
                    + form.getAttachmentDescription() + ". filesize: " + fileSize);
        }
        ActionMessages validation = AttachmentUtilities.validate(file, services);
        if (validation.isEmpty()) {

//		if (AttachmentUtilities.checkFile(file, getITrackerServices())) {
            int lastSlash = Math.max(origFileName.lastIndexOf('/'),
                    origFileName.lastIndexOf('\\'));
            if (lastSlash > -1) {
                origFileName = origFileName.substring(lastSlash + 1);
            }

            IssueAttachment attachmentModel = new IssueAttachment(issue,
                    origFileName, contentType, attachmentDescription, fileSize,
                    user);

            attachmentModel.setIssue(issue);
//			issue.getAttachments().add(attachmentModel);
            byte[] fileData;
            try {
                fileData = file.getFileData();
            } catch (IOException e) {
                logger.error("addAttachment: failed to get file data", e);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
                return issue;
            }
            if (services.getIssueService()
                    .addIssueAttachment(attachmentModel, fileData)) {
                return services.getIssueService().getIssue(issue.getId());
            }


        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("addAttachment: failed to validate: " + origFileName + ", " + validation);
            }
            messages.add(validation);
        }
        return issue;
    }

    public static ActionMessages validate(FormFile file, ITrackerServices services) {
        ActionMessages msg = new ActionMessages();
        if (!initialized) {
            if (!init(services)) {
                msg.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
                return msg;
            }
        }

        if (file == null) {
            return msg;
        }

        long origFileSize = file.getFileSize();
        if (origFileSize > maxFileSize) {
            logger.info("Cannot save attachment.  File is " + (origFileSize / 1024L) + " kB and max file size is set to " + (maxFileSize / 1024L) + "kB.");

            msg.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.attachment.size", (Math.round((maxFileSize / 1024L) * 100) / 100) + " " + ITrackerResources.getString("itracker.web.generic.kilobyte")));

            return msg;
        }

        if ((spaceLeft - origFileSize) < 0) {
            logger.info("Cannot save attachment.  Total allocated disk space already used.");
            msg.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.attachment.quota", (Math.round((maxTotalFileSize / 1024L) * 100) / 100) + " " + ITrackerResources.getString("itracker.web.generic.kilobyte")));

        }
        spaceLeft = spaceLeft - origFileSize;

        return msg;
    }


    private static boolean init(ITrackerServices services) {
        if (!initialized) {
            try {
                ConfigurationService configurationService = services.getConfigurationService();
                IssueService issueService = services.getIssueService();

                maxFileSize = configurationService.getLongProperty("max_attachment_size", MAX_FILE_SIZE_KB) * 1024L;
                maxTotalFileSize = configurationService.getLongProperty("max_total_attachment_size", MAX_TOTAL_FILE_SIZE_KB) * 1024L;
                spaceLeft = maxTotalFileSize - issueService.getAllIssueAttachmentSize();

                if (logger.isDebugEnabled()) {
                    logger.debug("Attachment Properties: MaxAttachmentSize set to " + (maxFileSize / 1024L) + " kB");
                    logger.debug("Attachment Properties: MaxTotalAttachmentsSize set to " + (maxTotalFileSize / 1024L) + " kB");
                    logger.debug("Attachment Properties: Current space left is " + (spaceLeft / 1024L) + " kB");
                }
                initialized = true;
            } catch (Exception e) {
                logger.error("Exception initializing AttachmentUtilities.", e);
                throw new Error("Failed to initialize AttachmentUtilities.", e);
            }
        }
        return initialized;
    }

    public static String getCONTENT_TYPE() {
        return CONTENT_TYPE;
    }

    public static String getCHAR_ENCODING() {
        return CHAR_ENCODING;
    }

}
