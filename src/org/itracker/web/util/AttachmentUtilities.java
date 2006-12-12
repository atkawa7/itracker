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
import org.apache.struts.upload.FormFile;
import org.itracker.services.IssueService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ITrackerServices;
 
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

    
    public static boolean checkFile(FormFile file, ITrackerServices services) {
        if(! initialized) {
           if(! init(services)) {
              return false;
           }
        }

        if(file == null) {
            return false;
        }

        long origFileSize = file.getFileSize();
        if(origFileSize > maxFileSize) {
            logger.info("Cannot save attachment.  File is " + (origFileSize / 1024L) + " kB and max file size is set to " + (maxFileSize / 1024L) + "kB.");
            return false;
        }

        if((spaceLeft - origFileSize) < 0) {
            logger.info("Cannot save attachment.  Total allocated disk space already used.");
            return false;
        }
        spaceLeft = spaceLeft - origFileSize;

//            filename = attachmentDirName + File.separator + filename;

//            if(logger.isDebugEnabled()) {
//                logger.debug("Attempting to save attachment: " + filename);
//            }

//            FileOutputStream out = new FileOutputStream(filename);
//            out.write(file.getFileData());
//            out.close();
        return true;
    }

    private static boolean init(ITrackerServices services) {
        if(! initialized) {
            try {
                ConfigurationService configurationService = services.getConfigurationService();
                IssueService issueService = services.getIssueService();
                
                maxFileSize = configurationService.getLongProperty("max_attachment_size", MAX_FILE_SIZE_KB) * 1024L;
                maxTotalFileSize = configurationService.getLongProperty("max_total_attachment_size", MAX_TOTAL_FILE_SIZE_KB) * 1024L;
                spaceLeft = maxTotalFileSize - issueService.getAllIssueAttachmentsSizeAndCount()[0];

                if(logger.isDebugEnabled()) {
                    logger.debug("Attachment Properties: MaxAttachmentSize set to " + (maxFileSize / 1024L) + " kB");
                    logger.debug("Attachment Properties: MaxTotalAttachmentsSize set to " + (maxTotalFileSize / 1024L) + " kB");
                    logger.debug("Attachment Properties: Current space left is " + (spaceLeft / 1024L) + " kB");
                }
                initialized = true;
            } catch(Exception e) {
                logger.error("Exception initializing AttachmentUtilities.", e);
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
