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

package org.itracker.services;

import java.util.List;

import javax.mail.internet.InternetAddress;

import org.itracker.model.Issue;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;


public interface NotificationService {
    

    /**
     * Retrieves the primary issue notifications.  Primary notifications
     * are defined as the issue owner (or creator if not assigned), and any project owners.
     * This should encompass the list of people that should be notified so that action
     * can be taken on an issue that needs immediate attention.
     * @param issueId the id of the issue to find notifications for
     * @returns an array of NotificationModels
     */
    List<Notification> getPrimaryIssueNotifications(Issue issue);
    
    /**
     * Retrieves all notifications for an issue where the notification's user is also active.
     * @param issueId the id of the issue to find notifications for
     * @returns an array of NotificationModels
     */
    List<Notification> getIssueNotifications(Issue issue);
    
    /**
     * Retrieves an array of issue notifications.  The notifications by default
     * is the creator and owner of the issue, all project admins for the issue's project,
     * and anyone else that has a notfication on file.
     * @param issueId the id of the issue to find notifications for
     * @param pimaryOnly only include the primary notifications
     * @param activeOnly only include the notification if the user is currently active (not locked or deleted)
     * @returns an array of NotificationModels
     * @see org.itracker.services.implementations.IssueServiceImpl#getPrimaryIssueNotifications
     */
    boolean removeIssueNotification(Integer notificationId);
    
    /**
     * @param issueId
     * @param primaryOnly
     * @param activeOnly
     * @return
     */
    List<Notification> getIssueNotifications(Issue issue, boolean primaryOnly, boolean activeOnly);
    /**
     * @param issueId
     * @param userId
     * @return
     */
    boolean hasIssueNotification(Issue issue, Integer userId);
    public boolean hasIssueNotification(Issue issue, Integer userId, Role role);
    /**
     * 
     * @param notification
     * @param type
     * @param baseURL
     */
    void sendNotification(Notification notification, Type type, String baseURL);    /**
     * 
     * @param issue
     * @param type
     * @param baseURL
     */
    void sendNotification(Issue issue, Type type, String baseURL);
    /**
     * @param issueId
     * @param type
     * @param baseURL
     * @param receipients
     * @param lastModifiedDays
     */
    void sendNotification(Issue issue, Type type, String baseURL, InternetAddress[] receipients, Integer lastModifiedDays);
    
    
}