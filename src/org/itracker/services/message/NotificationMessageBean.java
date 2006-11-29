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

package org.itracker.services.message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.MapMessage;
import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Component;
import org.itracker.model.IssueActivity;
import org.itracker.model.Issue;
import org.itracker.model.IssueHistory;
import org.itracker.model.Notification;
import org.itracker.model.Version;
import org.itracker.services.IssueService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.implementations.IssueServiceImpl;
import org.itracker.services.implementations.ConfigurationServiceImpl;
import org.itracker.services.util.EmailService;
import org.itracker.services.util.HTMLUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.NotificationUtilities;
import org.itracker.services.util.ProjectUtilities;


public class NotificationMessageBean implements MessageDrivenBean, MessageListener {
    
    public static final String DEFAULT_CONNECTION_FACTORY = "jms/QueueConnectionFactory";
    public static final String DEFAULT_QUEUE_NAME = "jms/ITrackerNotificationQueue";

    public static final String DEFAULT_FROM_ADDRESS = "itracker@localhost";
    public static final String DEFAULT_REPLYTO_ADDRESS = "itracker@localhost";
    public static final String DEFAULT_SMTP_HOST = "localhost";

    private final Logger logger;
    private MessageDrivenContext ejbContext;
    private Context jndiContext;
    private ConfigurationService configurationService;
    private EmailService emailService; 
    
    private String fromAddress = "";
    private String replyToAddress = "";
    private String smtpHost = "";

    public NotificationMessageBean() {
        this.logger = Logger.getLogger(getClass());
    }
    
    public void setMessageDrivenContext(MessageDrivenContext mdc) {
        ejbContext = mdc;

        try {
            jndiContext = new InitialContext();

            fromAddress = configurationService.getProperty("notification_from_address", DEFAULT_FROM_ADDRESS);
            replyToAddress = configurationService.getProperty("notification_replyto_address", DEFAULT_REPLYTO_ADDRESS);
            smtpHost = configurationService.getProperty("notification_smtp_host", DEFAULT_SMTP_HOST);
            if(logger.isDebugEnabled()) {
                logger.debug("Notification Init: From Address set to: " + fromAddress);
                logger.debug("Notification Init: ReplyTo Address set to: " + replyToAddress);
                logger.debug("Notification Init: SMTP server set to: " + smtpHost);
            }
        } catch(NamingException ne) {
            throw new EJBException(ne);
        }
    }

    public void onMessage(javax.jms.Message message) {
        try {
            MapMessage notificationMsg = (MapMessage) message;

            int type = notificationMsg.getInt("type");
            String url = notificationMsg.getString("baseURL");

            if(type == NotificationUtilities.TYPE_SELF_REGISTER) {
                handleSelfRegistrationNotification(notificationMsg, url);
            } else {
                handleIssueNotification(notificationMsg, type, url);
            }
        } catch(Exception e) {
            throw new EJBException(e);
        }
    }

    private void handleSelfRegistrationNotification(MapMessage notificationMsg, String url) {
        try {
            String toAddress = (String) notificationMsg.getObject("toAddress");

            if(toAddress != null && ! "".equals(toAddress)) {
                String subject = ITrackerResources.getString("itracker.email.selfreg.subject");
                String msgText = ITrackerResources.getString("itracker.email.selfreg.body", ITrackerResources.getDefaultLocale(),
                                 new Object[] {(String) notificationMsg.getObject("login"),  url + "/login.do"});
                emailService.sendEmail(toAddress, subject, msgText);
            }
        } catch(Exception e) {
            throw new EJBException(e);
        }
    }

    @SuppressWarnings("unchecked")
	private void handleIssueNotification(MapMessage notificationMsg, int type, String url) {
        try {
            HashSet<String> addresses = null;
            List<Notification> notifications;
            Integer issueId = (Integer) notificationMsg.getObject("issueId");

            if(issueId != null) {
                byte[] addressesBytes = (byte[]) notificationMsg.getObject("addresses");
                if(addressesBytes != null) {
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(addressesBytes);
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        addresses = (HashSet<String>) ois.readObject();
                    } catch(Exception e) {
                        logger.debug("Unable to read address list for notification: " + e.getMessage());
                    }
                }
                Integer lastModifiedDays = (Integer) notificationMsg.getObject("lastModifiedDays");
                if(lastModifiedDays == null || lastModifiedDays.intValue() < 0) {
                    lastModifiedDays = new Integer(org.itracker.web.scheduler.tasks.ReminderNotification.DEFAULT_ISSUE_AGE);
                }

                IssueService issueService = new IssueServiceImpl();
                if(addresses == null) {
                    addresses = new HashSet<String>();
                    notifications = issueService.getIssueNotifications(issueId);
                    for(int i = 0; i < notifications.size(); i++) {
                        if(notifications.get(i).getUser().getEmail() != null && notifications.get(i).getUser().getEmail().indexOf('@') >= 0) {
                            addresses.add(notifications.get(i).getUser().getEmail());
                        }
                    }
                }

                Issue issue = issueService.getIssue(issueId);
                List<IssueActivity> activity = issueService.getIssueActivity(issueId, false);
                IssueHistory history = issueService.getLastIssueHistory(issueId);
                List<Component> components = issueService.getIssueComponents(issueId);
                List<Version> versions = issueService.getIssueVersions(issueId);
                
                if(addresses.size() > 0) {
                    String subject = "";
                    if(type == NotificationUtilities.TYPE_CREATED) {
                        subject = ITrackerResources.getString("itracker.email.issue.subject.created",
                                      new Object[] {issue.getId(), issue.getProject().getName(), lastModifiedDays});
                    } else if(type == NotificationUtilities.TYPE_ASSIGNED) {
                        subject = ITrackerResources.getString("itracker.email.issue.subject.assigned",
                                      new Object[] {issue.getId(), issue.getProject().getName(), lastModifiedDays});
                    } else if(type == NotificationUtilities.TYPE_CLOSED) {
                        subject = ITrackerResources.getString("itracker.email.issue.subject.closed",
                                      new Object[] {issue.getId(), issue.getProject().getName(), lastModifiedDays});
                    } else if(type == NotificationUtilities.TYPE_ISSUE_REMINDER) {
                        subject = ITrackerResources.getString("itracker.email.issue.subject.reminder",
                                      new Object[] {issue.getId(), issue.getProject().getName(), lastModifiedDays});
                    } else {
                        subject = ITrackerResources.getString("itracker.email.issue.subject.updated",
                                      new Object[] {issue.getId(), issue.getProject().getName(), lastModifiedDays});
                    }


                    String activityString = "";
                    String componentString = "";
                    String versionString = "";
                    for(int i = 0; i < activity.size(); i++) {
                        activityString += IssueUtilities.getActivityName(activity.get(i).getType()) + ": " + activity.get(i).getDescription() + "\n";
                    }
                    for(int i = 0; i < components.size(); i++) {
                        componentString += (i != 0 ? ", " : "") + components.get(i).getName();
                    }
                    for(int i = 0; i < versions.size(); i++) {
                        versionString += (i != 0 ? ", " : "") + versions.get(i).getNumber();
                    }

                    String msgText = "";
                    if(type == NotificationUtilities.TYPE_ISSUE_REMINDER) {
                        msgText = ITrackerResources.getString("itracker.email.issue.body.reminder",
                                    new Object[] {url + "/view_issue.jsp?id=" + issue.getId(),
                                                  issue.getProject().getName(),
                                                  issue.getDescription(),
                                                  IssueUtilities.getStatusName(issue.getStatus()),
                                                  IssueUtilities.getSeverityName(issue.getSeverity()),
                                                  (issue.getOwner().getFirstName() != null ? issue.getOwner().getFirstName() : "") 
                                                  + " " + (issue.getOwner().getLastName() != null ? issue.getOwner().getLastName() : ""),
                                                  componentString,
                                                  (history == null ? "" : history.getUser().getFirstName() + " " + history.getUser().getLastName()),
                                                  (history == null ? "" : HTMLUtilities.removeMarkup(history.getDescription())),
                                                  lastModifiedDays,
                                                  activityString
                                                  });
                    } else {
                        String resolution = (issue.getResolution() == null ? "" : issue.getResolution());
                        if(! resolution.equals("") && ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, issue.getProject().getOptions())) {
                            resolution = IssueUtilities.getResolutionName(resolution, ITrackerResources.getLocale());
                        }

                        msgText = ITrackerResources.getString("itracker.email.issue.body.standard",
                                    new Object[] {url + "/view_issue.jsp?id=" + issue.getId() + "&authtype=5",
                                                  issue.getProject().getName(),
                                                  issue.getDescription(),
                                                  IssueUtilities.getStatusName(issue.getStatus()),
                                                  resolution,
                                                  IssueUtilities.getSeverityName(issue.getSeverity()),
                                                  (issue.getOwner().getFirstName() != null ? issue.getOwner().getFirstName() : "") 
                                                  + " " + (issue.getOwner().getLastName() != null ? issue.getOwner().getLastName() : ""),
                                                  componentString,
                                                  (history == null ? "" : history.getUser().getFirstName() + " " + history.getUser().getLastName()),
                                                  (history == null ? "" : HTMLUtilities.removeMarkup(history.getDescription())),
                                                  activityString
                                                  });
                    }
                    emailService.sendEmail(addresses, subject, msgText);
                    issueService.updateIssueActivityNotification(issue.getId(), true);
                }
            }
        } catch(Exception e) {
            throw new EJBException(e);
        }
    }

    public void ejbCreate() {}

    public void ejbRemove() {
        try {
            jndiContext.close();
            ejbContext = null;
        } catch(NamingException ne) {
        }
    }
}
