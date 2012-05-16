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

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.PasswordException;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;


/**
 * TODO: Add Javadocs here: please comment this for documentation reasons. What is this Class used for?
 * <p/>
 * It seems like this gets started when the application starts up...
 * <p/>
 * What's the general idea?
 * <p/>
 * Why is processAttachmentFiles commented and therefore not used currently?
 * Where does itracker store its attachments?
 * What's the idea behind the attachment_dir ?
 *
 * @author ready
 */

public class ApplicationInitialization {

    private final Logger logger;
    private UserService userService;
    private ConfigurationService configurationService;

    public ApplicationInitialization(UserService userService, ConfigurationService configurationService, ReportService reportService) {
        this.userService = userService;
        this.configurationService = configurationService;
        this.logger = Logger.getLogger(getClass());
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

            // Preinitialize all of the PDF fonts available. Do it in a
            // separate thread to speed up the rest of the startup.
            // TODO: I think this should be removed... why do we need to pre-init ? (rjst)
            // old code to pre-init fonts for jfree reports. make sure we can delete it
            // BaseFontFactory fontFactory = BaseFontFactory.getFontFactory();
            // fontFactory.registerDefaultFontPath();

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

}
