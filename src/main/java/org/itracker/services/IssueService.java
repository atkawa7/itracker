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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.itracker.model.Component;
import org.itracker.model.Issue;
import org.itracker.model.IssueActivity;
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.IssueRelation;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.Notification;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.exceptions.IssueSearchException;
import org.itracker.services.exceptions.ProjectException;

public interface IssueService {
    
    Issue getIssue(Integer issueId);
    
    /**
     * @deprecated Don't use due to EXPENSIVE memory use.
     * @return
     */
    List<Issue> getAllIssues();

    
    Long getNumberIssues();
    
    /**
     * Returns an array of issues that are currently at the given status.
     * @param status the status to search for
     * @return an array of issues with the given status
     */
    List<Issue> getIssuesWithStatus(int status);
    
    /**
     * Returns an array of issues that are currently at the given status or a status
     * less than the given status.
     * @param status the status to search for
     * @return an array of issues with the given status or lower
     */
    List<Issue> getIssuesWithStatusLessThan(int status);
    
    /**
     * Returns an array of issues that are currently at the given severity.
     * @param severity the severity to search for
     * @return an array of issues with the given severity
     */
    List<Issue> getIssuesWithSeverity(int severity);
    
    List<Issue> getIssuesByProjectId(Integer projectId);
    
    List<Issue> getIssuesByProjectId(Integer projectId, int status);
    
    List<Issue> getIssuesCreatedByUser(Integer userId);
    
    List<Issue> getIssuesCreatedByUser(Integer userId, boolean availableProjectsOnly);
    
    List<Issue> getIssuesOwnedByUser(Integer userId);
    
    List<Issue> getIssuesOwnedByUser(Integer userId, boolean availableProjectsOnly);
    
    List<Issue> getIssuesWatchedByUser(Integer userId);
    
    List<Issue> getIssuesWatchedByUser(Integer userId, boolean availableProjectsOnly);
    
    List<Issue> getUnassignedIssues();
    
    List<Issue> getUnassignedIssues(boolean availableProjectsOnly);
    /**
     * Creates a new issue in a project.
     * @param model an Issue representing the new issue information
     * @param projectId the projectId the issue belongs to
     * @param userId the id of registered creator of the new issue
     * @param createdById the id of the actual creator of the issue.  This would normally be the same as the userId.
     * @return an Issue containing the newly created issue, or null if the create failed
     */
    Issue createIssue(Issue issue, Integer projectId, Integer userId, Integer createdById)
    throws ProjectException;
    
    Issue updateIssue(Issue issue, Integer userId) throws ProjectException;
    
    /**
     * Moves an issues from its current project to a new project.
     * @param issue an Issue of the issue to move
     * @param projectId the id of the target project
     * @param userId the id of the user that is moving the issue
     * @return an Issue of the issue after it has been moved
     */
    Issue moveIssue(Issue issue, Integer projectId, Integer userId);
        
    boolean assignIssue(Integer issueId, Integer userId);
    
    boolean assignIssue(Integer issueId, Integer userId, Integer assignedByUserId);
    
    boolean setIssueFields(Integer issueId, List<IssueField> fields);
    
    boolean setIssueComponents(Integer issueId, HashSet<Integer> componentIds, Integer userId);
    
    boolean setIssueVersions(Integer issueId, HashSet<Integer> versionIds, Integer userId);
    
    boolean addIssueHistory(IssueHistory history);
    
    boolean addIssueRelation(Integer issueId, Integer relatedIssueId, int relationType, Integer userId);
    
    //boolean addIssueActivity(IssueActivityModel model);
    
    void updateIssueActivityNotification(Integer issueId, boolean notificationSent);
    
    boolean addIssueAttachment(IssueAttachment attachment, byte[] data);
    
    /**
     * Updates the binary data of the attachment stored in the database.
     * @param attachmentId the id of the attachment to update
     * @param data a byte arrray of the binary data for the attachment
     * @return true if the update was successful
     */
    boolean setIssueAttachmentData(Integer attachmentId, byte[] data);
    
    /**
     * Updates the binary data of the attachment stored in the database.  Used mainly
     * to take an existing attachment stored on the filesystem and move it into
     * the database.
     * @param fileName the filename listed in the database for the localtion of the attachment.
     * This is the name that was previously used to store the data on the
     * filesystem, not the original filename of the attachment.
     * @param data a byte arrray of the binary data for the attachment
     * @return true if the update was successful
     */
    boolean setIssueAttachmentData(String fileName, byte[] data);
    
    boolean addIssueNotification(Notification notification);
    
    boolean removeIssueAttachment(Integer attachmentId);
    
    Integer removeIssueHistoryEntry(Integer entryId, Integer userId);
    
    void removeIssueRelation(Integer relationId, Integer userId);
    
    Project getIssueProject(Integer issueId);
    
    List<Component> getIssueComponents(Integer issueId);
    
    HashSet<Integer> getIssueComponentIds(Integer issueId);
    
    List<Version> getIssueVersions(Integer issueId);
    
    HashSet<Integer> getIssueVersionIds(Integer issueId);
    
    User getIssueCreator(Integer issueId);
    
    User getIssueOwner(Integer issueId);
    
    IssueRelation getIssueRelation(Integer relationId);
    
    List<IssueActivity> getIssueActivity(Integer issueId);
    
    List<IssueActivity> getIssueActivity(Integer issueId, boolean notificationSent);
    
    List<IssueAttachment> getAllIssueAttachments();
    
    /**
     * @deprecated use the methods getAllIssueAttachmentSize and getAllIssueAttachmentCount instead.
     * @return
     */
    long[] getAllIssueAttachmentsSizeAndCount();
    
    Long getAllIssueAttachmentSize();

    /**
     * @deprecated use getAllIssueAttachmentSize instead.
     * @return
     */
    Long totalSystemIssuesAttachmentSize();
    /**
     * @deprecated use getAllIssuesAttachmentCount instead
     * @return
     */
    Long countSystemIssuesAttachments();
    
    Long getAllIssueAttachmentCount();
    
    
    IssueAttachment getIssueAttachment(Integer attachmentId);
    
    /**
     * Returns the binary data for an attachment.
     * @param attachmentId the id of the attachment to obtain the data for
     * @return a byte array containing the attachment data
     */
    byte[] getIssueAttachmentData(Integer attachmentId);
    
    List<IssueAttachment> getIssueAttachments(Integer issueId);
    
    int getIssueAttachmentCount(Integer issueId);
    
    /**
     * Returns the latest issue history entry for a particular issue.
     * @param issueId the id of the issue to return the history entry for.
     * @return the latest IssueHistory, or null if no entries could be found
     */
    IssueHistory getLastIssueHistory(Integer issueId);
    
    List<IssueHistory> getIssueHistory(Integer issueId);
    
    /**
     * @deprecated Moved to NotificationService
     * Retrieves the primary issue notifications.  Primary notifications
     * are defined as the issue owner (or creator if not assigned), and any project owners.
     * This should encompass the list of people that should be notified so that action
     * can be taken on an issue that needs immediate attention.
     * @param issueId the id of the issue to find notifications for
     * @returns an array of NotificationModels
     */
    List<Notification> getPrimaryIssueNotifications(Integer issueId);
    
    /**
     * Retrieves all notifications for an issue where the notification's user is also active.
     * @param issueId the id of the issue to find notifications for
     * @returns an array of NotificationModels
     * @deprecated Moved to NotificationService
     */
    List<Notification> getIssueNotifications(Integer issueId);
    
    /**
     * Retrieves an array of issue notifications.  The notifications by default
     * is the creator and owner of the issue, all project admins for the issue's project,
     * and anyone else that has a notfication on file.
     * @param issueId the id of the issue to find notifications for
     * @param pimaryOnly only include the primary notifications
     * @param activeOnly only include the notification if the user is currently active (not locked or deleted)
     * @returns an array of NotificationModels
     * @see org.itracker.services.implementations.IssueServiceImpl#getPrimaryIssueNotifications
     * @deprecated moved to NotificationService
     */
    boolean removeIssueNotification(Integer notificationId);
    
    /**
     * @deprecated Moved to NotificationService
     * @param issueId
     * @param primaryOnly
     * @param activeOnly
     * @return
     */
    List<Notification> getIssueNotifications(Integer issueId, boolean primaryOnly, boolean activeOnly);
    /**
     * @deprecated Moved to NotificationService
     * @param issueId
     * @param userId
     * @return
     */
    boolean hasIssueNotification(Integer issueId, Integer userId);
    
    /**
     * @deprecated moved to NotificationService
     * 
     * @param issueId
     * @param type
     * @param baseURL
     */
    void sendNotification(Integer issueId, int type, String baseURL);
    /**
     * @deprecated Will not be replaced yet
     * @param issueId
     * @param type
     * @param baseURL
     * @param addresses
     * @param lastModifiedDays
     */
    void sendNotification(Integer issueId, int type, String baseURL, HashSet<String> addresses, Integer lastModifiedDays);
    
    int getOpenIssueCountByProjectId(Integer projectId);
    
    int getResolvedIssueCountByProjectId(Integer projectId);
    
    int getTotalIssueCountByProjectId(Integer projectId);
    
    Date getLatestIssueDateByProjectId(Integer projectId);
    
    boolean canViewIssue(Integer issueId, User user);
    
    boolean canViewIssue(Issue issue, User user);
    
    public List<Issue> searchIssues(IssueSearchQuery queryModel, User user, Map<Integer, Set<PermissionType>> userPermissions) throws IssueSearchException;
}
