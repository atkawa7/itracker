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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Report;
import org.itracker.model.User;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.PasswordException;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.Base64;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Please comment this for documentation reasons. What is this Class used for?
 * It seems like this gets started when the application starts up...
 * @author ready
 *
 */
public class ApplicationInitialization {
    
    private final Logger logger;
    private UserService userService;
    private SessionFactory sessionFactory;
    private ConfigurationService configurationService;
    private ReportService reportService;
    
    public ApplicationInitialization(UserService userService, SessionFactory sessionFactory, ConfigurationService configurationService, ReportService reportService) {
        this.userService = userService;
        this.sessionFactory = sessionFactory;
        this.configurationService = configurationService;
        this.reportService = reportService;
        this.logger = Logger.getLogger(getClass());
        init();
    }
    
    public void init() {
        try {
            Session session = sessionFactory.openSession();
            TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
            
            ITrackerResources.setDefaultLocale(configurationService.getProperty("default_locale", ITrackerResources.DEFAULT_LOCALE));
            logger.info("Set system default locale to '" + ITrackerResources.getDefaultLocale() + "'");
            
            logger.info("Checking and initializing languages in the database.");
            SystemConfigurationUtilities.initializeAllLanguages(configurationService, false);
            
            logger.info("Checking and initializing default system configuration in the database.");
            configurationService.initializeConfiguration();
            
//            logger.info("Checking for issue attachment files.");
//            processAttachmentFiles(configurationService.getProperty("attachment_dir", IssueAttachmentUtilities.DEFAULT_ATTACHMENT_DIR));
            
            logger.info("Checking for predefined reports.");
            processReports();
            
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
        } catch (HibernateException e) {
            logger.error(e.getMessage(), e);
        } finally {
            org.springframework.orm.hibernate3.SessionHolder holder = (org.springframework.orm.hibernate3.SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
            Session session = holder.getSession();
            
            try {
                session.flush();
            } catch (HibernateException e) {
                logger.error(e.getMessage(), e);
            }
            
            TransactionSynchronizationManager.unbindResource(sessionFactory);
            SessionFactoryUtils.releaseSession(session, sessionFactory);
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
    
    private void processReports() {
        try {
            
            List<Report> reports = reportService.getAllReports();
            logger.info("size is " + reports.size());
            
            String line;
            InputStream is = getClass().getResourceAsStream("/org/itracker/web/reports/predefined/predefinedReports.properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            LINE: while ((line = br.readLine()) != null) {
                if (line.equals("") || line.startsWith("#")) {
                    continue;
                }
                int splitChar = line.indexOf('=');
                if (splitChar < 0) {
                    continue;
                }
                
                String key = line.substring(0, splitChar);
                String value = line.substring(splitChar + 1);
                
                if ("report".equalsIgnoreCase(key)) {
                    try {
                        String repLine;
                        Report report = new Report();
                        Report chkreport = new Report();
                        
                        InputStream reportStream = getClass().getResourceAsStream("/org/itracker/web/reports/predefined/" + value);
                        if (reportStream == null) {
                            throw new IOException("Could not access predefined report '" + value + "'");
                        }
                        BufferedReader rbr = new BufferedReader(new InputStreamReader(reportStream));
                        while ((repLine = rbr.readLine()) != null) {
                            if (repLine.equals("") || line.startsWith("#")) {
                                continue;
                            }
                            int repSplitChar = repLine.indexOf('=');
                            if (repSplitChar < 0) {
                                continue;
                            }
                            String repKey = repLine.substring(0, repSplitChar);
                            String repValue = (repLine.length() > repSplitChar ? repLine.substring(repSplitChar + 1)
                            : "");
                            if (repValue == null || "".equals(repValue)) {
                                continue;
                            } else if ("id".equals(repKey)) {
                                report.setId(new Integer(repValue));
                            } else if ("name".equals(repKey)) {
                                report.setName(repValue);
                            } else if ("namekey".equals(repKey)) {
                                report.setNameKey(repValue);
                            } else if ("dataType".equals(repKey)) {
                                report.setDataType(Integer.parseInt(repValue));
                            } else if ("reportType".equals(repKey)) {
                                report.setReportType(Integer.parseInt(repValue));
                            } else if ("className".equals(repKey)) {
                                report.setClassName(repValue);
                            } else if ("description".equals(repKey)) {
                                report.setDescription((String) Base64.decodeToObject(repValue));
                            } else if ("definition".equals(repKey)) {
                                report.setFileData(Base64.decode(repValue));
                            }
                        }
                        if ("".equals(report.getName()) || report.getDataType() == 0
                                || (report.getClassName() == null && report.getFileData().length == 0)) {
                            throw new Exception("Invalid report definition found.");
                        }
                        logger.debug("Loading " + report.toString());
                        
                        for (Iterator<Report> iter = reports.iterator(); iter.hasNext();) {
                            Report existingReport = iter.next();
                            if ( existingReport.getName() == null ) {
                                break;
                            }
                            if ( existingReport.getClassName().equals(report.getClassName()) &&
                                    existingReport.getDataType() == report.getDataType() &&
                                    existingReport.getName().equals(report.getName()) &&
                                    existingReport.getNameKey().equals(report.getNameKey()) &&
                                    existingReport.getReportType()== report.getReportType() ) {
                                logger.debug("Found existing report, updating.");
                                report.setId(existingReport.getId());
                                reportService.updateReport(report);
                                continue LINE;
                            }
                        }
                        logger.debug(report.getName() + " - Not found, creating new report.");
                        reportService.createReport(report);
                    } catch (Exception e) {
                        logger.error("Unable to process report '" + value + "': " + e.getMessage());
                        logger.debug("Stacktrace:", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to process predefined reports.", e);
        }
    }
}
