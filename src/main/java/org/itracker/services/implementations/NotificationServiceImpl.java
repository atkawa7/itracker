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

package org.itracker.services.implementations;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.persistence.dao.IssueActivityDAO;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.NotificationDAO;
import org.itracker.services.EmailService;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.ProjectService;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.util.HTMLUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.mail.internet.InternetAddress;
import java.util.*;

public class NotificationServiceImpl implements NotificationService {

    // TODO: Cleanup this file, go through all issues, todos, etc.

    private EmailService emailService;
    private NotificationDAO notificationDao;
    private ProjectService projectService;
    private IssueActivityDAO issueActivityDao;
    private IssueDAO issueDao;

    private static final Logger logger = Logger
            .getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl() {

        this.emailService = null;
        this.projectService = null;
        this.notificationDao = null;
    }

    public NotificationServiceImpl(EmailService emailService,
                                   ProjectService projectService, NotificationDAO notificationDao, IssueActivityDAO issueActivityDao, IssueDAO issueDao) {
        this();
        this.setEmailService(emailService);
        this.setProjectService(projectService);
        this.setNotificationDao(notificationDao);
        this.setIssueActivityDao(issueActivityDao);
        this.setIssueDao(issueDao);
    }

    public void sendNotification(Notification notification, Type type,
                                 String url) {

        if (logger.isDebugEnabled()) {
            logger.debug("sendNotification: called with notification: "
                    + notification + ", type: " + url + ", url: " + url);
        }
        if (null == notification) {
            throw new IllegalArgumentException("notification must not be null");
        }
        if (null == this.emailService || null == this.notificationDao) {
            throw new IllegalStateException("service not initialized yet");
        }
        if (type == Type.SELF_REGISTER) {
            this.handleSelfRegistrationNotification(notification.getUser()
                    .getLogin(), notification.getUser().getEmailAddress(), notification.getUser().getPreferences().getUserLocale(), url);
        } else {
            handleIssueNotification(notification.getIssue(), type, url);

        }

    }

    public void sendNotification(Issue issue, Type type, String baseURL) {
        if (logger.isDebugEnabled()) {
            logger.debug("sendNotification: called with issue: " + issue
                    + ", type: " + type + ", baseURL: " + baseURL);
        }
        handleIssueNotification(issue, type, baseURL);

    }

    public void setEmailService(EmailService emailService) {

        if (null == emailService)
            throw new IllegalArgumentException("email service must not be null");

        if (null != this.emailService) {
            throw new IllegalStateException("email service allready set");
        }
        this.emailService = emailService;

    }

    /**
     * @param login
     * @param toAddress
     * @param url
     */
    private void handleSelfRegistrationNotification(String login,
                                                    InternetAddress toAddress, String locale, String url) {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("handleSelfRegistrationNotification: called with login: "
                            + login
                            + ", toAddress"
                            + toAddress
                            + ", url: "
                            + url);
        }
        try {

            if (toAddress != null && !"".equals(toAddress.getAddress())) {
                String subject = ITrackerResources
                        .getString("itracker.email.selfreg.subject", locale);
                String msgText = ITrackerResources.getString(
                        "itracker.email.selfreg.body", locale, new Object[]{login,
                        url + "/login.do"});
                emailService.sendEmail(toAddress, subject, msgText);
            } else {
                throw new IllegalArgumentException(
                        "To-address must be set for self registration notification.");
            }
        } catch (RuntimeException e) {
            logger.error("failed to handle self registration notification for "
                    + toAddress, e);
            throw e;
        }
    }

    /**
     * Method for internal sending of a notification of specific type.
     */
    private void handleIssueNotification(Issue issue, Type type, String url) {

        if (logger.isDebugEnabled()) {
            logger.debug("handleIssueNotification: called with issue: " + issue
                    + ", type: " + type + "url: " + url);
        }
        this.handleLocalizedIssueNotification(issue, type, url, null, null);
        //this.handleIssueNotification(issue, type, url, null, null);
    }

    /**
     * Method for internal sending of a notification of specific type.
     */
    private void handleIssueNotification(Issue issue, Type type, String url,
                                         InternetAddress[] recipients, Integer lastModifiedDays) {
        try {

            if (logger.isDebugEnabled()) {
                logger
                        .debug("handleIssueNotificationhandleIssueNotification: called with issue: "
                                + issue
                                + ", type: "
                                + type
                                + "url: "
                                + url
                                + ", recipients: "
                                + (null == recipients ? "<null>" : String
                                .valueOf(Arrays.asList(recipients)))
                                + ", lastModifiedDays: " + lastModifiedDays);
            }
            List<Notification> notifications;

            if (issue == null) {
                logger
                        .warn("handleIssueNotification: issue was null. Notification will not be handled");
                return;
            }

            if (lastModifiedDays == null || lastModifiedDays.intValue() < 0) {
                lastModifiedDays = Integer
                        .valueOf(org.itracker.web.scheduler.tasks.ReminderNotification.DEFAULT_ISSUE_AGE);
            }

            if (recipients == null) {
                ArrayList<InternetAddress> recList = new ArrayList<InternetAddress>();
                notifications = this.getIssueNotifications(issue);
                Iterator<Notification> it = notifications.iterator();
                User currentUser;
                while (it.hasNext()) {
                    currentUser = it.next().getUser();
                    if (null != currentUser
                            && null != currentUser.getEmailAddress()
                            && null != currentUser.getEmail()
                            && (!recList
                            .contains(currentUser.getEmailAddress()))
                            && currentUser.getEmail().indexOf('@') >= 0) {

                        recList.add(currentUser.getEmailAddress());
                    }
                }
                recipients = recList.toArray(new InternetAddress[]{});
            }

            List<IssueActivity> activity = getIssueService().getIssueActivity(
                    issue.getId(), false);
            issue.getActivities();
            List<IssueHistory> histories = issue.getHistory();
            Iterator<IssueHistory> it = histories.iterator();
            IssueHistory history = null, currentHistory;
            history = getIssueService().getLastIssueHistory(issue.getId());

            Integer historyId = 0;
            // find history with greatest id
            while (it.hasNext()) {
                currentHistory = (IssueHistory) it.next();
                if (logger.isDebugEnabled()) {
                    logger.debug("handleIssueNotification: found history: "
                            + currentHistory.getDescription() + " (time: "
                            + currentHistory.getCreateDate());
                }
                if (currentHistory.getId() > historyId) {
                    historyId = currentHistory.getId();
                    history = currentHistory;
                }
            }
            if (logger.isDebugEnabled() && null != history) {
                logger
                        .debug("handleIssueNotification: got most recent history: "
                                + history
                                + " ("
                                + history.getDescription()
                                + ")");
            }

            List<Component> components = issue.getComponents();

            List<Version> versions = issue.getVersions();

            if (recipients.length > 0) {
                String subject = "";
                if (type == Type.CREATED) {
                    subject = ITrackerResources.getString(
                            "itracker.email.issue.subject.created",
                            new Object[]{issue.getId(),
                                    issue.getProject().getName(),
                                    lastModifiedDays});
                } else if (type == Type.ASSIGNED) {
                    subject = ITrackerResources.getString(
                            "itracker.email.issue.subject.assigned",
                            new Object[]{issue.getId(),
                                    issue.getProject().getName(),
                                    lastModifiedDays});
                } else if (type == Type.CLOSED) {
                    subject = ITrackerResources.getString(
                            "itracker.email.issue.subject.closed",
                            new Object[]{issue.getId(),
                                    issue.getProject().getName(),
                                    lastModifiedDays});
                } else if (type == Type.ISSUE_REMINDER) {
                    subject = ITrackerResources.getString(
                            "itracker.email.issue.subject.reminder",
                            new Object[]{issue.getId(),
                                    issue.getProject().getName(),
                                    lastModifiedDays});
                } else {
                    subject = ITrackerResources.getString(
                            "itracker.email.issue.subject.updated",
                            new Object[]{issue.getId(),
                                    issue.getProject().getName(),
                                    lastModifiedDays});
                }

                String activityString;
                String componentString = "";
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < activity.size(); i++) {
                    sb.append(
                            IssueUtilities.getActivityName(activity.get(i)
                                    .getActivityType())).append(": ").append(
                            activity.get(i).getDescription()).append("\n");

                }
                activityString = sb.toString();
                for (int i = 0; i < components.size(); i++) {
                    componentString += (i != 0 ? ", " : "")
                            + components.get(i).getName();
                }

                String msgText = "";
                if (type == Type.ISSUE_REMINDER) {
                    msgText = ITrackerResources
                            .getString(
                                    "itracker.email.issue.body.reminder",
                                    new Object[]{
                                            url
                                                    + "/module-projects/view_issue.do?id="
                                                    + issue.getId(),
                                            issue.getProject().getName(),
                                            issue.getDescription(),
                                            IssueUtilities.getStatusName(issue
                                                    .getStatus()),
                                            IssueUtilities
                                                    .getSeverityName(issue
                                                            .getSeverity()),
                                            (issue.getOwner().getFirstName() != null ? issue
                                                    .getOwner().getFirstName()
                                                    : "")
                                                    + " "
                                                    + (issue.getOwner()
                                                    .getLastName() != null ? issue
                                                    .getOwner()
                                                    .getLastName()
                                                    : ""),
                                            componentString,
                                            (history == null ? "" : history
                                                    .getUser().getFirstName()
                                                    + " "
                                                    + history.getUser()
                                                    .getLastName()),
                                            (history == null ? ""
                                                    : HTMLUtilities
                                                    .removeMarkup(history
                                                            .getDescription())),
                                            lastModifiedDays, activityString});
                } else {
                    String resolution = (issue.getResolution() == null ? ""
                            : issue.getResolution());
                    if (!resolution.equals("")
                            && ProjectUtilities
                            .hasOption(
                                    ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
                                    issue.getProject().getOptions())) {
                        resolution = IssueUtilities.getResolutionName(
                                resolution, ITrackerResources.getLocale());
                    }

                    msgText = ITrackerResources
                            .getString(
                                    "itracker.email.issue.body.standard",
                                    new Object[]{
                                            new StringBuffer(url).append("/module-projects/view_issue.do?id=").append(issue.getId()).toString(),
                                            issue.getProject().getName(),
                                            issue.getDescription(),
                                            IssueUtilities.getStatusName(issue
                                                    .getStatus()),
                                            resolution,
                                            IssueUtilities
                                                    .getSeverityName(issue
                                                            .getSeverity()),
                                            (null != issue.getOwner() && null != issue.getOwner().getFirstName() ? issue
                                                    .getOwner().getFirstName()
                                                    : "")
                                                    + " "
                                                    + (null != issue.getOwner() && null != issue.getOwner()
                                                    .getLastName() ? issue
                                                    .getOwner()
                                                    .getLastName()
                                                    : ""),
                                            componentString,
                                            (history == null ? "" : history
                                                    .getUser().getFirstName()
                                                    + " "
                                                    + history.getUser()
                                                    .getLastName()),
                                            (history == null ? ""
                                                    : HTMLUtilities
                                                    .removeMarkup(history
                                                            .getDescription())),
                                            activityString});
                }
                emailService.sendEmail(recipients, subject, msgText);

                updateIssueActivityNotification(issue, true);
            }

        } catch (Exception e) {
            logger
                    .error(
                            "handleIssueNotification: unexpected exception caught, throwing runtime exception",
                            e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Method for internal sending of a notification of specific type.
     * <p/>
     * TODO: final debugging/integration/implementation
     * TODO: Decide if this code is really needed and document for what
     */

    @SuppressWarnings("unused")
    private void handleLocalizedIssueNotification(final Issue issue, final Type type, final String url,
                                                  final InternetAddress[] recipients, Integer lastModifiedDays) {
        try {

            if (logger.isDebugEnabled()) {
                logger
                        .debug("handleIssueNotificationhandleIssueNotification: running as thread, called with issue: "
                                + issue
                                + ", type: "
                                + type
                                + "url: "
                                + url
                                + ", recipients: "
                                + (null == recipients ? "<null>" : String
                                .valueOf(Arrays.asList(recipients)))
                                + ", lastModifiedDays: " + lastModifiedDays);
            }

            final Integer notModifiedSince;

            if (lastModifiedDays == null || lastModifiedDays.intValue() < 0) {
                notModifiedSince = Integer
                        .valueOf(org.itracker.web.scheduler.tasks.ReminderNotification.DEFAULT_ISSUE_AGE);
            } else {
                notModifiedSince = lastModifiedDays;
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("handleIssueNotificationhandleIssueNotification.run: running as thread, called with issue: "
                                    + issue
                                    + ", type: "
                                    + type
                                    + "url: "
                                    + url
                                    + ", recipients: "
                                    + (null == recipients ? "<null>" : String
                                    .valueOf(Arrays.asList(recipients)))
                                    + ", notModifiedSince: " + notModifiedSince);
                }
                final List<Notification> notifications;
                if (issue == null) {
                    logger
                            .warn("handleIssueNotification: issue was null. Notification will not be handled");
                    return;
                }
                Map<InternetAddress, Locale> localeMapping;

                if (recipients == null) {


                    notifications = this.getIssueNotifications(issue);

                    localeMapping = new Hashtable<InternetAddress, Locale>(notifications.size());
                    Iterator<Notification> it = notifications.iterator();
                    User currentUser;
                    while (it.hasNext()) {
                        currentUser = it.next().getUser();
                        if (null != currentUser
                                && null != currentUser.getEmailAddress()
                                && null != currentUser.getEmail()
                                && (!localeMapping.keySet()
                                .contains(currentUser.getEmailAddress()))) {

                            try {
                                localeMapping.put(currentUser.getEmailAddress(), ITrackerResources.getLocale(currentUser.getPreferences().getUserLocale()));
                            } catch (RuntimeException re) {
                                localeMapping.put(currentUser.getEmailAddress(), ITrackerResources.getLocale());
                            }
                        }
                    }
                } else {
                    localeMapping = new Hashtable<InternetAddress, Locale>(1);
                    Locale locale = ITrackerResources.getLocale();
                    Iterator<InternetAddress> it = Arrays.asList(recipients).iterator();
                    while (it.hasNext()) {
                        InternetAddress internetAddress = it
                                .next();
                        localeMapping.put(internetAddress, locale);
                    }
                }

                this.handleNotification(issue, type, notModifiedSince, localeMapping, url);
            } catch (Exception e) {
                logger.error("run: failed to process notification", e);
            }

        } catch (Exception e) {
            logger
                    .error(
                            "handleIssueNotification: unexpected exception caught, throwing runtime exception",
                            e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Send notifications to mapped addresses by locale.
     */
    private void handleNotification(Issue issue, Type type, Integer notModifiedSince, Map<InternetAddress, Locale> recipientsLocales, final String url) {
        Set<InternetAddress> recipients = recipientsLocales.keySet();
        Map<Locale, Set<InternetAddress>> localeRecipients = new Hashtable<Locale, Set<InternetAddress>>();

        List<Component> components = issue.getComponents();
        List<Version> versions = issue.getVersions();

        List<IssueActivity> activity = getIssueService().getIssueActivity(
                issue.getId(), false);

        List<IssueHistory> histories = issue.getHistory();
        Iterator<IssueHistory> it = histories.iterator();
        IssueHistory history = null, currentHistory;
        history = getIssueService().getLastIssueHistory(issue.getId());
        StringBuilder recipientsString = new StringBuilder();

        Integer historyId = 0;
        // find history with greatest id
        while (it.hasNext()) {
            currentHistory = it.next();
            if (logger.isDebugEnabled()) {
                logger.debug("handleIssueNotification: found history: "
                        + currentHistory.getDescription() + " (time: "
                        + currentHistory.getCreateDate());
            }
            if (currentHistory.getId() > historyId) {
                historyId = currentHistory.getId();
                history = currentHistory;
            }
        }
        if (logger.isDebugEnabled() && null != history) {
            logger
                    .debug("handleIssueNotification: got most recent history: "
                            + history
                            + " ("
                            + history.getDescription()
                            + ")");
        }

        Iterator<InternetAddress> iaIt = recipientsLocales.keySet().iterator();
        while (iaIt.hasNext()) {
            InternetAddress internetAddress = iaIt.next();

            recipientsString.append("\n  ");
            recipientsString.append(internetAddress.getPersonal());

            if (localeRecipients.keySet().contains(recipientsLocales.get(internetAddress))) {
                localeRecipients.get(recipientsLocales.get(internetAddress)).add(internetAddress);
            } else {
                Set<InternetAddress> addresses = new HashSet<InternetAddress>();
                addresses.add(internetAddress);
                localeRecipients.put(recipientsLocales.get(internetAddress), addresses);
            }
        }

        Iterator<Locale> localesIt = localeRecipients.keySet().iterator();
        try {
            while (localesIt.hasNext()) {
                Locale currentLocale = localesIt.next();
                recipients = localeRecipients.get(currentLocale);


                if (recipients.size() > 0) {
                    String subject = "";
                    if (type == Type.CREATED) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.created",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName(),
                                        notModifiedSince});
                    } else if (type == Type.ASSIGNED) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.assigned",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName(),
                                        notModifiedSince});
                    } else if (type == Type.CLOSED) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.closed",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName(),
                                        notModifiedSince});
                    } else if (type == Type.ISSUE_REMINDER) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.reminder",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName(),
                                        notModifiedSince});
                    } else {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.updated",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName(),
                                        notModifiedSince});
                    }

                    String activityString;
                    String componentString = "";
                    StringBuffer sb = new StringBuffer();
                    if (activity.size() == 0) {
                        sb.append("-");
                    } else {
                        for (int i = 0; i < activity.size(); i++) {
                            sb.append("\n ").append(
                                    IssueUtilities.getActivityName(activity.get(i)
                                            .getActivityType(), currentLocale)).append(": ").append(
                                    activity.get(i).getDescription());

                        }
                    }
                    sb.append("\n");
                    activityString = sb.toString();
                    // TODO localize..
                    for (int i = 0; i < components.size(); i++) {
                        componentString += (i != 0 ? ", " : "")
                                + components.get(i).getName();
                    }

                    String msgText;
                    if (type == Type.ISSUE_REMINDER) {
                        msgText = ITrackerResources
                                .getString(
                                        "itracker.email.issue.body.reminder",
                                        currentLocale,
                                        new Object[]{
                                                url
                                                        + "/module-projects/view_issue.do?id="
                                                        + issue.getId(),
                                                issue.getProject().getName(),
                                                issue.getDescription(),
                                                IssueUtilities.getStatusName(issue
                                                        .getStatus(), currentLocale),
                                                IssueUtilities
                                                        .getSeverityName(issue
                                                                .getSeverity(), currentLocale),
                                                (issue.getOwner().getFirstName() != null ? issue
                                                        .getOwner().getFirstName()
                                                        : "")
                                                        + " "
                                                        + (issue.getOwner()
                                                        .getLastName() != null ? issue
                                                        .getOwner()
                                                        .getLastName()
                                                        : ""),
                                                componentString,
                                                (history == null ? "" : history
                                                        .getUser().getFirstName()
                                                        + " "
                                                        + history.getUser()
                                                        .getLastName()),
                                                (history == null ? ""
                                                        : HTMLUtilities
                                                        .removeMarkup(history
                                                                .getDescription())),
                                                notModifiedSince, activityString});
                    } else {
                        String resolution = (issue.getResolution() == null ? ""
                                : issue.getResolution());
                        if (!resolution.equals("")
                                && ProjectUtilities
                                .hasOption(
                                        ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
                                        issue.getProject().getOptions())) {
                            resolution = IssueUtilities.getResolutionName(
                                    resolution, currentLocale);
                        }
                        User oUser = issue.getOwner();
                        String owner = (null != oUser && null != oUser.getFirstName()
                                ? oUser.getFirstName()
                                : "")
                                + " "
                                + (null != oUser && null != oUser.getLastName()
                                ? oUser.getLastName()
                                : "");
                        User hUser = null == history ? null : history.getUser();
                        String historyUser = (null != hUser && null != hUser.getFirstName()
                                ? hUser.getFirstName()
                                : "")
                                + " "
                                + (null != hUser && null != hUser.getLastName()
                                ? hUser.getLastName()
                                : "");
                        msgText = ITrackerResources
                                .getString(
                                        "itracker.email.issue.body.standard",
                                        currentLocale,
                                        new Object[]{
                                                new StringBuffer(url).append("/module-projects/view_issue.do?id=").append(issue.getId()).toString(),
                                                issue.getProject().getName(),
                                                issue.getDescription(),
                                                IssueUtilities.getStatusName(issue
                                                        .getStatus(), currentLocale),
                                                resolution,
                                                IssueUtilities
                                                        .getSeverityName(issue
                                                                .getSeverity(), currentLocale),
                                                owner,
                                                componentString,
                                                historyUser,
                                                (history == null ? ""
                                                        : HTMLUtilities
                                                        .removeMarkup(history
                                                                .getDescription())),
                                                activityString,
                                                recipientsString});
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info(new StringBuilder("handleNotification: sending notification for ").append(issue).append(" (").append(type).append(") to ").append(currentLocale).append("-users (").append(recipients + ")").toString());

                    }
                    for (InternetAddress iadr : recipients) {
                        emailService.sendEmail(iadr, subject, msgText);
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("handleNotification: sent notification for " + issue
                                + ": " + subject + "\n  " + msgText);
                    }
                }

                updateIssueActivityNotification(issue, true);
                if (logger.isDebugEnabled()) {
                    logger.debug("handleNotification: sent notification for locales " + localeRecipients.keySet() + " recipients: " + localeRecipients.values());
                }
            }
        } catch (RuntimeException e) {
            logger.error("handleNotification: failed to notify: " + issue + " (locales: " + localeRecipients.keySet() + ")", e);

        }


    }

    private IssueService getIssueService() {
        return ServletContextUtils.getItrackerServices().getIssueService();
    }

    public void updateIssueActivityNotification(Issue issue,
                                                Boolean notificationSent) {
        if (logger.isDebugEnabled()) {
            logger.debug("updateIssueActivityNotification: called with "
                    + issue + ", notificationSent: " + notificationSent);
        }

        Collection<IssueActivity> activity = getIssueActivityDao()
                .findByIssueId(issue.getId());

        for (Iterator<IssueActivity> iter = activity.iterator(); iter.hasNext(); ) {

            ((IssueActivity) iter.next()).setNotificationSent(notificationSent);

        }
    }

    /**
     */
    public boolean addIssueNotification(Notification notification) {
        if (logger.isDebugEnabled()) {
            logger.debug("addIssueNotification: called with notification: "
                    + notification);
        }
        Issue issue = notification.getIssue();
        if (!issue.getNotifications().contains(notification)) {
            if (notification.getCreateDate() == null) {
                notification.setCreateDate(new Date());
            }
            if (notification.getLastModifiedDate() == null) {
                notification.setLastModifiedDate(new Date());
            }
            //		List<Notification> notifications = new ArrayList<Notification>();
            // TODO: check these 3 lines - do we need them?:
            //		notifications.addAll(issue.getNotifications());
            //		notifications.add(notification);
            //		issue.setNotifications(notifications);
            getNotificationDao().save(notification);
            // TODO: is it needed to update issue too?
            issue.getNotifications().add(notification);
            getIssueDao().merge(issue);

            return true;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("addIssueNotification: attempted to add duplicate notification " + notification + " for issue: " + issue);
        }
        return false;
    }

    /**
     *
     */
    public List<Notification> getIssueNotifications(Issue issue,
                                                    boolean primaryOnly, boolean activeOnly) {
        if (logger.isDebugEnabled()) {
            logger.debug("getIssueNotifications: called with issue: " + issue
                    + ", primaryOnly: " + primaryOnly + ", activeOnly: "
                    + activeOnly);
        }
        List<Notification> issueNotifications = new ArrayList<Notification>();
        if (issue == null) {
            logger.warn("getIssueNotifications: no issue, throwing exception");
            throw new IllegalArgumentException("issue must not be null");
        }
        if (!primaryOnly) {
            List<Notification> notifications = getNotificationDao()
                    .findByIssueId(issue.getId());

            for (Iterator<Notification> iterator = notifications.iterator(); iterator
                    .hasNext(); ) {
                Notification notification = iterator.next();
                User notificationUser = notification.getUser();

                if (!activeOnly
                        || notificationUser.getStatus() == UserUtilities.STATUS_ACTIVE) {
                    issueNotifications.add(notification);
                }
            }
        }

        // Now add in other notifications like owner, creator, project owners,
        // etc...

        boolean hasOwner = false;
        if (issue != null) {
            if (issue.getOwner() != null) {
                User ownerModel = issue.getOwner();

                if (ownerModel != null
                        && (!activeOnly || ownerModel.getStatus() == UserUtilities.STATUS_ACTIVE)) {
                    issueNotifications.add(new Notification(ownerModel, issue,
                            Role.OWNER));
                    hasOwner = true;
                }
            }

            if (!primaryOnly || !hasOwner) {
                User creatorModel = issue.getCreator();

                if (creatorModel != null
                        && (!activeOnly || creatorModel.getStatus() == UserUtilities.STATUS_ACTIVE)) {
                    issueNotifications.add(new Notification(creatorModel,
                            issue, Role.CREATOR));
                }
            }

            Project project = getProjectService().getProject(
                    issue.getProject().getId());
            Collection<User> projectOwners = project.getOwners();

            for (Iterator<User> iterator = projectOwners.iterator(); iterator
                    .hasNext(); ) {
                User projectOwner = (User) iterator.next();

                if (projectOwner != null
                        && (!activeOnly || projectOwner.getStatus() == UserUtilities.STATUS_ACTIVE)) {
                    issueNotifications.add(new Notification(projectOwner,
                            issue, Role.PO));
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getIssueNotifications: returning "
                    + issueNotifications);
        }
        return issueNotifications;
    }

    public List<Notification> getIssueNotifications(Issue issue) {
        if (logger.isDebugEnabled()) {
            logger.debug("getIssueNotifications: called with: " + issue);
        }
        return this.getIssueNotifications(issue, false, true);
    }

    public List<Notification> getPrimaryIssueNotifications(Issue issue) {
        if (logger.isDebugEnabled()) {
            logger.debug("getPrimaryIssueNotifications: called with: " + issue);
        }
        return this.getIssueNotifications(issue, true, false);
    }

    public boolean hasIssueNotification(Issue issue, Integer userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("hasIssueNotification: called with: " + issue
                    + ", userId: " + userId);
        }
        return hasIssueNotification(issue, userId, Role.ANY);
    }

    /**
     * @param issue
     * @param userId
     * @param role
     * @return
     */
    public boolean hasIssueNotification(Issue issue, Integer userId, Role role) {

        if (issue != null && userId != null) {

            List<Notification> notifications = getIssueNotifications(issue,
                    false, false);

            for (int i = 0; i < notifications.size(); i++) {

                if (role == Role.ANY || notifications.get(i).getRole() == role) {

                    if (notifications.get(i).getUser().getId().equals(userId)) {

                        return true;

                    }

                }

            }

        }

        return false;

    }

    public boolean removeIssueNotification(Integer notificationId) {
        Notification notification = this.getNotificationDao().findById(
                notificationId);
        getNotificationDao().delete(notification);
        return true;
    }

    public void sendNotification(Issue issue, Type type, String baseURL,
                                 InternetAddress[] receipients, Integer lastModifiedDays) {
        this.handleIssueNotification(issue, type, baseURL, receipients,
                lastModifiedDays);

    }


    /**
     * @return the emailService
     */
    public EmailService getEmailService() {
        return emailService;
    }

    /**
     * @return the notificationDao
     */
    private NotificationDAO getNotificationDao() {
        return notificationDao;
    }

    /**
     * @return the projectService
     */
    public ProjectService getProjectService() {
        return projectService;
    }

    /**
     * @param projectService the projectService to set
     */
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * @param notificationDao the notificationDao to set
     */
    public void setNotificationDao(NotificationDAO notificationDao) {
        if (null == notificationDao) {
            throw new IllegalArgumentException(
                    "notification dao must not be null");
        }
        if (null != this.notificationDao) {
            throw new IllegalStateException("notification dao allready set");
        }
        this.notificationDao = notificationDao;
    }

    /**
     * TODO url should be automatically generated by configuration (baseurl) and
     * notification (issue-id).
     */
    public void sendNotification(Integer notificationId, Type type, String url) {

        Notification notification = notificationDao.findById(notificationId);
        this.sendNotification(notification, type, url);

    }

    /**
     * @return the issueActivityDao
     */
    public IssueActivityDAO getIssueActivityDao() {
        return issueActivityDao;
    }

    /**
     * @param issueActivityDao the issueActivityDao to set
     */
    public void setIssueActivityDao(IssueActivityDAO issueActivityDao) {
        this.issueActivityDao = issueActivityDao;
    }

    /**
     * @return the issueDao
     */
    public IssueDAO getIssueDao() {
        return issueDao;
    }

    /**
     * @param issueDao the issueDao to set
     */
    public void setIssueDao(IssueDAO issueDao) {
        this.issueDao = issueDao;
    }

}