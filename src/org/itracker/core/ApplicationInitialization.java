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

package org.itracker.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.PasswordException;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;


/**
 * Please comment this for documentation reasons. What is this Class used for?
 * It seems like this gets started when the application starts up...
 * @author ready
 *
 */
public class ApplicationInitialization {
    
    private final Logger logger;
    private UserService userService;    
    private ConfigurationService configurationService;
    
    public ApplicationInitialization(UserService userService, ConfigurationService configurationService, ReportService reportService) {
        this.userService = userService;
        this.configurationService = configurationService;
        this.logger = Logger.getLogger(getClass());
        init();
    }
    
    public void init() {
        try {
            ITrackerResources.setDefaultLocale(configurationService.getProperty("default_locale", ITrackerResources.DEFAULT_LOCALE));
            logger.info("Set system default locale to '" + ITrackerResources.getDefaultLocale() + "'");
            
            logger.info("Checking and initializing languages in the database.");
            SystemConfigurationUtilities.initializeAllLanguages(configurationService, false);
            
            logger.info("Checking and initializing default system configuration in the database.");
            configurationService.initializeConfiguration();
            
//            logger.info("Checking for issue attachment files.");
//            processAttachmentFiles(configurationService.getProperty("attachment_dir", IssueAttachmentUtilities.DEFAULT_ATTACHMENT_DIR));
                       
            logger.info("Setting up cached configuration entries");
            configurationService.resetConfigurationCache();
            
            // Pre-initialize all of the PDF fonts available. Do it in a
            // seperate thread to speed up the
            // rest of the startup.
            // TODO I think this should be removed... why do we need to pre-init ? (rjst)
            // old code to pre-init fonts for jfree reports. make sure we can delete it
            //BaseFontFactory fontFactory = BaseFontFactory.getFontFactory();
            //fontFactory.registerDefaultFontPath();
            
            // check for and create admin user, if so configured
            createAdminUser(configurationService);
        } catch (PasswordException pe) {
            logger.info("Unable to create admin user.  Error: " + pe.getMessage());
        } catch (UserException ue) {
            logger.warn("Exception while creating admin user.", ue);
        }
    }
    
    /**
     * Check if we should create the admin user, if so, do it.
     *
     * @param configurationService
     * @throws PasswordException
     * @throws UserException
     */
    private void createAdminUser(ConfigurationService configurationService) throws PasswordException, UserException {
        boolean createAdmin = configurationService.getBooleanProperty("create_super_user", false);
        if (createAdmin) {
            logger.info("Create default admin user option set to true.  Checking for existing admin user.");
            try {
                userService.getUserByLogin("admin");
            } catch (NoSuchEntityException e) {
                logger.debug("Attempting to create admin user.");
                User adminUser = new User("admin", UserUtilities.encryptPassword("admin"), "Super", "User",
                        "", true);
                userService.createUser(adminUser);
            }
        }
    }
    
    @SuppressWarnings("unused")
	private void processAttachmentFiles(String attachmentDirectory) {
        if (attachmentDirectory == null || attachmentDirectory.equals("")) {
            return;
        }
        
        try {
            File directory = new File(attachmentDirectory.replace('/', File.separatorChar));
            if (directory == null || !directory.isDirectory()) {
                throw new Exception("Invalid attachment directory.");
            }
            File[] attachments = directory.listFiles();
            
            IssueService issueService = null;
            
            for (int i = 0; i < attachments.length; i++) {
                try {
                    if (attachments[i].length() > Integer.MAX_VALUE) {
                        throw new IOException("File too large to load.");
                    }
                    byte[] data = new byte[(int) attachments[i].length()];
                    FileInputStream fis = new FileInputStream(attachments[i]);
                    int bytesRead = fis.read(data);
                    fis.close();
                    if (bytesRead != data.length) {
                        throw new IOException(
                                "The number of bytes read from the file, did not match the size of the file.");
                    }
                    if (issueService.setIssueAttachmentData(attachments[i].getName(), data)) {
                        // attachments[i].delete();
                        logger.debug("Successfully moved attachment " + attachments[i].getName()
                        + " to the database.");
                    }
                } catch (IOException ioe) {
                    logger.error("Unable to save attachment: " + ioe.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Unable to check for existing file attachments: " + e.getMessage());
        }
    }
    
}
