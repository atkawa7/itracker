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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
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
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.persistence.dao.ComponentDAO;
import org.itracker.persistence.dao.CustomFieldDAO;
import org.itracker.persistence.dao.IssueActivityDAO;
import org.itracker.persistence.dao.IssueAttachmentDAO;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.IssueHistoryDAO;
import org.itracker.persistence.dao.IssueRelationDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.persistence.dao.VersionDAO;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.exceptions.IssueSearchException;
import org.itracker.services.exceptions.ProjectException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.IssueUtilities;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Issue related service layer. A bit "fat" at this time, because of being a
 * direct EJB porting. Going go get thinner over time
 * 
 * @author ricardo
 * 
 */
public class IssueServiceImpl implements IssueService {

	// TODO: work on these 3 not yet used items: notificationFactoryName,
	// notificationQueueName, systemBaseURL;


	private static String systemBaseURL = "";

	@SuppressWarnings("unused")
	private static  final Logger logger = Logger.getLogger(IssueServiceImpl.class);

	private CustomFieldDAO customFieldDAO;

	private UserDAO userDAO;

	private ProjectDAO projectDAO;

	private IssueDAO issueDAO;

	private IssueHistoryDAO issueHistoryDAO;

	private IssueRelationDAO issueRelationDAO;

	private IssueAttachmentDAO issueAttachmentDAO;

	private ComponentDAO componentDAO;

	private IssueActivityDAO issueActivityDAO;

	private VersionDAO versionDAO;
	
	private NotificationService notificationService;

	public IssueServiceImpl(UserDAO userDAO, ProjectDAO projectDAO, IssueDAO issueDAO, IssueHistoryDAO issueHistoryDAO,
			IssueRelationDAO issueRelationDAO, IssueAttachmentDAO issueAttachmentDAO,
			ComponentDAO componentDAO, IssueActivityDAO issueActivityDAO, VersionDAO versionDAO) {

		this.userDAO = userDAO;
		this.projectDAO = projectDAO;
		this.issueDAO = issueDAO;
		this.issueHistoryDAO = issueHistoryDAO;
		this.issueRelationDAO = issueRelationDAO;
		this.issueAttachmentDAO = issueAttachmentDAO;
		this.componentDAO = componentDAO;
		this.issueActivityDAO = issueActivityDAO;
		this.versionDAO = versionDAO;
	}
	
	public void setNotificationService(NotificationService notificationService) {
		if (null == notificationService) {
			throw new IllegalArgumentException("notification service must not be null");
		}
		if (null != this.notificationService) {
			throw new IllegalStateException("notification service has already been set");
		}
		this.notificationService = notificationService;
	}

	public Issue getIssue(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		return issue;
	}

	/**
	 * @deprecated don't use to expensive memory use!
	 */
	public List<Issue> getAllIssues() {
		logger.warn("getAllIssues: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getAllIssues: stackgtrace was", new RuntimeException());
		}
		return getIssueDAO().findAll();
	}

	/**
	 * Added implementation to make proper count of ALL issues, instead select them in a list and return its size
	 */
	public Long getNumberIssues() {
		return getIssueDAO().countAllIssues();
	}

	public List<Issue> getIssuesCreatedByUser(Integer userId) {
		return getIssuesCreatedByUser(userId, true);
	}

	public List<Issue> getIssuesCreatedByUser(Integer userId, boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByCreatorInAvailableProjects(userId, IssueUtilities.STATUS_CLOSED);
		} else {
			issues = getIssueDAO().findByCreator(userId, IssueUtilities.STATUS_CLOSED);
		}
		return issues;
	}

	public List<Issue> getIssuesOwnedByUser(Integer userId) {

		return getIssuesOwnedByUser(userId, true);

	}

	public List<Issue> getIssuesOwnedByUser(Integer userId, boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByOwnerInAvailableProjects(userId, IssueUtilities.STATUS_RESOLVED);
		} else {
			issues = getIssueDAO().findByOwner(userId, IssueUtilities.STATUS_RESOLVED);
		}
		return issues;
	}
	

	public List<Issue> getIssuesWatchedByUser(Integer userId) {
		return getIssuesWatchedByUser(userId, true);
	}

	/**
	 * TODO move to {@link NotificationService}
	 */
	public List<Issue> getIssuesWatchedByUser(Integer userId, boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByNotificationInAvailableProjects(userId, IssueUtilities.STATUS_CLOSED);
		} else {
			issues = getIssueDAO().findByNotification(userId, IssueUtilities.STATUS_CLOSED);
		}
		return issues;
	}

	public List<Issue> getUnassignedIssues() {
		return getUnassignedIssues(true);
	}

	public List<Issue> getUnassignedIssues(boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByStatusLessThanEqualToInAvailableProjects(IssueUtilities.STATUS_UNASSIGNED);
		} else {
			issues = getIssueDAO().findByStatusLessThanEqualTo(IssueUtilities.STATUS_UNASSIGNED);
		}
		return issues;
	}

	/**
	 * 
	 * Returns all issues with a status equal to the given status number
	 * 
	 * 
	 * 
	 * @param status
	 * 
	 * the status to compare
	 * 
	 * @return an array of IssueModels that match the criteria
	 * 
	 */

	public List<Issue> getIssuesWithStatus(int status) {
		List<Issue> issues = getIssueDAO().findByStatus(status);
		return issues;
	}

	/**
	 * 
	 * Returns all issues with a status less than the given status number
	 * 
	 * 
	 * 
	 * @param status
	 * 
	 * the status to compare
	 * 
	 * @return an array of IssueModels that match the criteria
	 */

	public List<Issue> getIssuesWithStatusLessThan(int status) {
		List<Issue> issues = getIssueDAO().findByStatusLessThan(status);
		return issues;
	}

	/**
	 * 
	 * Returns all issues with a severity equal to the given severity number
	 * 
	 * 
	 * 
	 * @param severity
	 * 
	 * the severity to compare
	 * 
	 * @return an array of IssueModels that match the criteria
	 * 
	 */

	public List<Issue> getIssuesWithSeverity(int severity) {
		List<Issue> issues = getIssueDAO().findBySeverity(severity);
		return issues;

	}

	public List<Issue> getIssuesByProjectId(Integer projectId) {
		return getIssuesByProjectId(projectId, IssueUtilities.STATUS_END);
	}

	public List<Issue> getIssuesByProjectId(Integer projectId, int status) {
		List<Issue> issues = getIssueDAO().findByProjectAndLowerStatus(projectId, status);
		return issues;
	}

	public User getIssueCreator(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		User user = issue.getCreator();
		return user;

	}

	public User getIssueOwner(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		User user = issue.getOwner();

		return user;

	}

	public List<Component> getIssueComponents(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		List<Component> components = issue.getComponents();

		return components;
	}

	public List<Version> getIssueVersions(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		List<Version> versions = issue.getVersions();
		return versions;
	}

	public List<IssueAttachment> getIssueAttachments(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		List<IssueAttachment> attachments = issue.getAttachments();
		return attachments;
	}

	/**
	 * Old implementation is left here, commented, because it checked for
	 * history entry status. This feature was not finished, I think (RJST)
	 */
	public List<IssueHistory> getIssueHistory(Integer issueId) {
		return getIssueDAO().findByPrimaryKey(issueId).getHistory();
	}

	public Issue createIssue(Issue issue, Integer projectId, Integer userId, Integer createdById)
			throws ProjectException {
		Project project = getProjectDAO().findByPrimaryKey(projectId);
		User creator = getUserDAO().findByPrimaryKey(userId);

		if (project.getStatus() != Status.ACTIVE) {
			throw new ProjectException("Project is not active.");
		}

		if (createdById == null || createdById.equals(userId)) {

			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_ISSUE_CREATED);
			activity.setDescription(ITrackerResources.getString("itracker.activity.system.createdfor") + " "
					+ creator.getFirstName() + " " + creator.getLastName());
			activity.setUser(creator);
			activity.setIssue(issue);
			activity.setCreateDate(new Date());
			activity.setLastModifiedDate(new Date());
			List<IssueActivity> activities = new ArrayList<IssueActivity>();
			activities.add(activity);
			issue.setActivities(activities);

		} else {

			User createdBy = getUserDAO().findByPrimaryKey(createdById);

			IssueActivity activity = new IssueActivity();
			activity.setDescription(ITrackerResources.getString("itracker.activity.system.createdfor") + " "
					+ creator.getFirstName() + " " + creator.getLastName());
			activity.setUser(createdBy);
			activity.setIssue(issue);
			activity.setCreateDate(new Date());
			activity.setLastModifiedDate(new Date());
			List<IssueActivity> activities = new ArrayList<IssueActivity>();
			activities.add(activity);
			issue.setActivities(activities);
			Notification watchModel = new Notification();

			watchModel.setUser(creator);

			watchModel.setIssue(issue);

			watchModel.setRole(Notification.Role.IP);

			addIssueNotification(watchModel);

		}

		issue.setProject(project);

		issue.setCreator(creator);

		// save
		// TODO: The filter should automatically take care of the
		// following two timestamps, removed them
		issue.setCreateDate(new Timestamp(new Date().getTime()));
		issue.setLastModifiedDate(issue.getCreateDate());
		getIssueDAO().save(issue);

		return issue;
	}


	/**
	 * 
	 * TODO: improve this, verify how the update is done.. is there any reason to swap the issue-object?
	 * 
	 */
	public Issue updateIssue(Issue issue, Integer userId) throws ProjectException {
		String existingTargetVersion = null;
		// TODO why is the issue replaced and retrieved from DB again?
	
		Issue Updateissue = getIssueDAO().findByPrimaryKey(issue.getId());
		User user = getUserDAO().findByPrimaryKey(userId);
		IssueActivity activity;
		if (Updateissue.getProject().getStatus() != Status.ACTIVE) {
			// TODO: localization
			throw new ProjectException("Project is not active.");
		}
		
		// ensure there is no unset description
		if (null == issue.getDescription() || issue.getDescription().length() < 1) {
			issue.setDescription(Updateissue.getDescription());
		}
		if (logger.isDebugEnabled()) {
			// some sever informations about updating issue
			logger.debug("updateIssue: updating issue " + issue.getId() + ", old-description was: '" + Updateissue.getDescription() + "', old Description was: '" + issue.getDescription() + "', equals: " + Updateissue.getDescription().equalsIgnoreCase(issue.getDescription()));
		}
		
		// TODO: ist there a more 'clean' way to check for description updated? Timestamp e.g.?
	 // are we looking at the subject, or the last description in the history here?
		if (Updateissue.getDescription() != null && issue.getDescription() != null
				&& !Updateissue.getDescription().equalsIgnoreCase(issue.getDescription())) {

			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: creating new description-change activity");
			}
			activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_DESCRIPTION_CHANGE);
			activity.setDescription(ITrackerResources.getString("itracker.web.generic.from") + ": "
					+ Updateissue.getDescription());
			activity.setUser(user);
			activity.setIssue(Updateissue);
			
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: added IssueActivity: " + activity);
			}

		}
		// TODO: same here?
		if (Updateissue.getResolution() != null && issue.getResolution() != null
				&& !Updateissue.getResolution().equalsIgnoreCase(issue.getResolution())) {
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: creating new resolution-change activity");
			}
			activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_RESOLUTION_CHANGE);
			activity.setDescription(ITrackerResources.getString("itracker.web.generic.from") + ": "
					+ Updateissue.getResolution());
			activity.setIssue(Updateissue);
			activity.setUser(user);
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: added IssueActivity: " + activity);
			}
		}

		if (Updateissue.getStatus() != issue.getStatus() && issue.getStatus() != -1) {

			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: creating new status-change activity");
			}
			activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_STATUS_CHANGE);
			activity.setDescription(IssueUtilities.getStatusName(Updateissue.getStatus()) + " "
					+ ITrackerResources.getString("itracker.web.generic.to") + " "
					+ IssueUtilities.getStatusName(issue.getStatus()));
			activity.setIssue(Updateissue);
			activity.setUser(user);
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: added IssueActivity: " + activity);
			}
		}

		if (Updateissue.getSeverity() != issue.getSeverity() && issue.getSeverity() != -1) {
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: creating new severity-change activity");
			}
			activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_SEVERITY_CHANGE);
			activity.setDescription(IssueUtilities.getSeverityName(Updateissue.getSeverity()) + " "
					+ ITrackerResources.getString("itracker.web.generic.to") + " "
					+ IssueUtilities.getSeverityName(issue.getSeverity()));
			activity.setIssue(Updateissue);
			activity.setUser(user);
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: added IssueActivity: " + activity);
			}
		}

		if (Updateissue.getTargetVersion() != null && issue.getTargetVersion() != null
				&& !Updateissue.getTargetVersion().getId().equals(issue.getTargetVersion().getId())) {
			existingTargetVersion = Updateissue.getTargetVersion().getNumber();

		}

		logger.debug("updateIssue: setting description: "  + issue.getDescription());
		Updateissue.setDescription(issue.getDescription());
		logger.debug("updateIssue: setting severity: "  + issue.getSeverity());
		Updateissue.setSeverity(issue.getSeverity());
		logger.debug("updateIssue: setting status: "  + issue.getStatus());
		Updateissue.setStatus(issue.getStatus());
		logger.debug("updateIssue: setting resolution: "  + issue.getResolution());
		Updateissue.setResolution(issue.getResolution());
		logger.debug("updateIssue: setting last modified date: "  + issue.getLastModifiedDate());
		Updateissue.setLastModifiedDate(new Date());

		if (issue.getTargetVersion() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: creating new targetversion-change activity");
			}
			Version version = this.getVersionDAO().findByPrimaryKey(issue.getTargetVersion().getId());

			Updateissue.setTargetVersion(version);

			activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_TARGETVERSION_CHANGE);
			String description = existingTargetVersion + " " + ITrackerResources.getString("itracker.web.generic.to")
					+ " ";
			description += version.getNumber();
			activity.setDescription(description);
			activity.setUser(user);
			activity.setIssue(Updateissue);
			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: added IssueActivity: " + activity);
			}
		} else {
			Updateissue.setTargetVersion(null);
		}
        Updateissue.setFields(issue.getFields());
		// save
        getIssueDAO().saveOrUpdate(Updateissue);
        
        //return getIssue(issue.getId());
                
		return Updateissue;
	}

	/**
	 * 
	 * Moves an issues from its current project to a new project.
	 * 
	 * 
	 * 
	 * @param issue
	 * 
	 * an Issue of the issue to move
	 * 
	 * @param projectId
	 * 
	 * the id of the target project
	 * 
	 * @param userId
	 * 
	 * the id of the user that is moving the issue
	 * 
	 * @return an Issue of the issue after it has been moved
	 */

	public Issue moveIssue(Issue issue, Integer projectId, Integer userId) {
		Project project = getProjectDAO().findByPrimaryKey(projectId);

		User user = getUserDAO().findByPrimaryKey(userId);

		IssueActivity activity = new IssueActivity();
		activity.setType(IssueUtilities.ACTIVITY_ISSUE_MOVE);
		activity.setDescription(issue.getProject().getName() + " "
				+ ITrackerResources.getString("itracker.web.generic.to") + " " + project.getName());
		activity.setUser(user);
		activity.setIssue(issue);
		issue.setProject(project);

		// The versions and components are per project so we need to delete
		// these

		setIssueComponents(issue.getId(), new HashSet<Integer>(), userId);

		setIssueVersions(issue.getId(), new HashSet<Integer>(), userId);

		return issue;

	}

	/**
	 * this should not exist. adding an history entry should be adding the
	 * history entry to the domain object and saving the object...
	 */
	public boolean addIssueHistory(IssueHistory history) {
		getIssueHistoryDAO().saveOrUpdate(history);

		return true;
	}

	public boolean setIssueFields(Integer issueId, List<IssueField> fields) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		List<IssueField> issueFields = issue.getFields();

//		for (Iterator<IssueField> iter = issueFields.iterator(); iter.hasNext();) {
			// TODO: clean this code
			// try {

			// IssueField field = (IssueField) iter.next();

			// iter.remove();

			// field.remove();

			// } catch(RemoveException re) {

			// logger.info("Unable to remove issue field value. Manual

			// database cleanup may be necessary.");

			// }

//		}

		if (fields.size() > 0) {
			for (int i = 0; i < fields.size(); i++) {
				IssueField field = new IssueField();
				CustomField customField = getCustomFieldDAO().findByPrimaryKey(fields.get(i).getCustomField().getId());
				field.setCustomField(customField);
				field.setIssue(issue);
				field.setDateValue(new Timestamp(new Date().getTime()));
				issueFields.add(field);
			}
		}

		return true;
	}

	public boolean setIssueComponents(Integer issueId, HashSet<Integer> componentIds, Integer userId) {

		boolean wasChanged = false;

		StringBuffer changesBuf = new StringBuffer();

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		List<Component> components = new ArrayList<Component>();
		components = issue.getComponents();

		if (components != null) {

			if (componentIds.isEmpty() && !components.isEmpty()) {

				wasChanged = true;

				changesBuf.append(ITrackerResources.getString("itracker.web.generic.all") + " "

				+ ITrackerResources.getString("itracker.web.generic.removed"));

				components.clear();

			} else {

				for (Iterator<Component> iterator = components.iterator(); iterator.hasNext();) {

					Component component = (Component) iterator.next();

					if (componentIds.contains(component.getId())) {

						componentIds.remove(component.getId());

					} else {

						wasChanged = true;

						changesBuf.append(ITrackerResources.getString("itracker.web.generic.removed") + ": "

						+ component.getName() + "; ");

						iterator.remove();

					}

				}

				for (Iterator<Integer> iterator = componentIds.iterator(); iterator.hasNext();) {

					Integer componentId = iterator.next();

					Component component = getComponentDAO().findById(componentId);

					wasChanged = true;

					changesBuf.append(ITrackerResources.getString("itracker.web.generic.added") + ": "

					+ component.getName() + "; ");

					components.add(component);

				}

			}
		} else {
			logger.debug("components was null!");
		}

		if (wasChanged) {

			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_COMPONENTS_MODIFIED);
			activity.setDescription(changesBuf.toString());
			activity.setIssue(issue);
			// activity.setUser();
			// userId); -> I think we need to set user here
		}

		return true;

	}

	public boolean setIssueVersions(Integer issueId, HashSet<Integer> versionIds, Integer userId) {

		boolean wasChanged = false;

		StringBuffer changesBuf = new StringBuffer();

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		List<Version> versions = issue.getVersions();

		if (versions != null) {

			if (versionIds.isEmpty() && !versions.isEmpty()) {

				wasChanged = true;

				changesBuf.append(ITrackerResources.getString("itracker.web.generic.all") + " "

				+ ITrackerResources.getString("itracker.web.generic.removed"));

				versions.clear();

			} else {

				for (Iterator<Version> iterator = versions.iterator(); iterator.hasNext();) {

					Version version = (Version) iterator.next();

					if (versionIds.contains(version.getId())) {

						versionIds.remove(version.getId());

					} else {

						wasChanged = true;

						changesBuf.append(ITrackerResources.getString("itracker.web.generic.removed") + ": "

						+ version.getNumber() + "; ");

						iterator.remove();

					}

				}

				for (Iterator<Integer> iterator = versionIds.iterator(); iterator.hasNext();) {

					Integer versionId = (Integer) iterator.next();

					Version version = getVersionDAO().findByPrimaryKey(versionId);

					wasChanged = true;

					changesBuf.append(ITrackerResources.getString("itracker.web.generic.added") + ": "

					+ version.getNumber() + "; ");

					versions.add(version);

				}

			}

		} else {
			logger.debug("Versions were null!");
		}

		if (wasChanged) {

			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_VERSIONS_MODIFIED);
			activity.setDescription(changesBuf.toString());
			activity.setIssue(issue);
			// need to set user here
			// userId);
		}

		return true;

	}

	public IssueRelation getIssueRelation(Integer relationId) {

		IssueRelation issueRelation = getIssueRelationDAO().findByPrimaryKey(relationId);

		return issueRelation;

	}

	public boolean addIssueRelation(Integer issueId, Integer relatedIssueId, int relationType, Integer userId) {

		if (issueId != null && relatedIssueId != null) {

			int matchingRelationType = IssueUtilities.getMatchingRelationType(relationType);

			// if(matchingRelationType < 0) {

			// throw new CreateException("Unable to find matching relation type

			// for type: " + relationType);

			// }

			Issue issue = getIssueDAO().findByPrimaryKey(issueId);

			Issue relatedIssue = getIssueDAO().findByPrimaryKey(relatedIssueId);

			IssueRelation relationA = new IssueRelation();

			relationA.setRelationType(relationType);

			// relationA.setMatchingRelationId(relationBId);

			relationA.setIssue(issue);

			relationA.setRelatedIssue(relatedIssue);

			relationA.setLastModifiedDate(new java.sql.Timestamp(new java.util.Date().getTime()));

			IssueRelation relationB = new IssueRelation();

			relationB.setRelationType(matchingRelationType);

			// relationB.setMatchingRelationId(relationAId);

			relationB.setIssue(relatedIssue);

			relationB.setRelatedIssue(issue);

			relationB.setLastModifiedDate(new java.sql.Timestamp(new java.util.Date().getTime()));

			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_RELATION_ADDED);
			activity.setDescription(ITrackerResources.getString("itracker.activity.relation.add"));
			// probably add this to description
			// new Object[] {IssueUtilities.getRelationName(relationType),
			// relatedIssueId };
			activity.setIssue(issue);
			// need to set user here... userId);
			// need to save here

			activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_RELATION_ADDED);
			activity.setDescription(ITrackerResources.getString("itracker.activity.relation.add",
					new Object[] { IssueUtilities.getRelationName(matchingRelationType) }));
			activity.setIssue(relatedIssue);
			// net to save and set user here.. userId);
			return true;

		}

		return false;

	}

	public void removeIssueRelation(Integer relationId, Integer userId) {
		IssueRelation issueRelation = getIssueRelationDAO().findByPrimaryKey(relationId);
		Integer issueId = issueRelation.getIssue().getId();

		Integer relatedIssueId = issueRelation.getRelatedIssue().getId();

		Integer matchingRelationId = issueRelation.getMatchingRelationId();

		if (matchingRelationId != null) {
			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_RELATION_REMOVED);
			activity.setDescription(ITrackerResources.getString("itracker.activity.relation.removed", issueId
					.toString()));
			// need to fix the commented code and save
			// activity.setIssue(relatedIssueId);
			// activity.setUser(userId);
			// IssueRelationDAO.remove(matchingRelationId);
		}

		IssueActivity activity = new IssueActivity();
		activity.setType(IssueUtilities.ACTIVITY_RELATION_REMOVED);
		activity.setDescription(ITrackerResources.getString("itracker.activity.relation.removed", relatedIssueId
				.toString()));
		// activity.setIssue(issueId);
		// activity.setUser(userId);
		// irHome.remove(relationId);
		// need to save
	}

	public boolean assignIssue(Integer issueId, Integer userId) {
		return assignIssue(issueId, userId, userId);
	}

	public boolean assignIssue(Integer issueId, Integer userId, Integer assignedByUserId) {
		if (userId.intValue() == -1) {
			return unassignIssue(issueId, assignedByUserId);
		}

		User assignedByUser;
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		User user = getUserDAO().findByPrimaryKey(userId);

		if (assignedByUserId.equals(userId)) {
			assignedByUser = user;
		} else {
			assignedByUser = getUserDAO().findByPrimaryKey(assignedByUserId);
		}

		User currOwner = issue.getOwner();

		if (currOwner == null || !currOwner.getId().equals(user.getId())) {
			if (currOwner != null
					&& !notificationService.hasIssueNotification(issue, currOwner.getId(), Role.CONTRIBUTER)) {
				// Notification notification = new Notification();
				Notification notification = new Notification(currOwner, issue, Role.CONTRIBUTER);
				// TODO check implementation
				addIssueNotification(notification);
			}

			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_OWNER_CHANGE);
			activity.setDescription((currOwner == null ? "["
					+ ITrackerResources.getString("itracker.web.generic.unassigned") + "]" : currOwner.getLogin())
					+ " " + ITrackerResources.getString("itracker.web.generic.to") + " " + user.getLogin());
			activity.setUser(assignedByUser);
			activity.setIssue(issue);

			issue.setOwner(user);

			if (issue.getStatus() < IssueUtilities.STATUS_ASSIGNED) {
				issue.setStatus(IssueUtilities.STATUS_ASSIGNED);
			}
		}
		// send assignment notification
		// TODO refactor
		notificationService.sendNotification(issue, Type.ASSIGNED, getSystemBaseURL());
		//sendNotification(issueId, NotificationUtilities.TYPE_ASSIGNED, "baseURL");
		return true;

	}

	public boolean unassignIssue(Integer issueId, Integer assignedByUserId) {
		User assignedByUser = getUserDAO().findByPrimaryKey(assignedByUserId);
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		if (issue.getOwner() != null) {

			if (!notificationService.hasIssueNotification(issue, issue.getOwner().getId(), Role.CONTRIBUTER)) {
				// Notification notification = new Notification();
				Notification notification = new Notification(issue.getOwner(), issue,
						Role.CONTRIBUTER);
				// TODO check implementation
				addIssueNotification(notification);
			}
			IssueActivity activity = new IssueActivity(issue, assignedByUser, IssueUtilities.ACTIVITY_OWNER_CHANGE);
			activity.setDescription((issue.getOwner() == null ? "["
					+ ITrackerResources.getString("itracker.web.generic.unassigned") + "]" : issue.getOwner()
					.getLogin())
					+ " "
					+ ITrackerResources.getString("itracker.web.generic.to")
					+ " ["
					+ ITrackerResources.getString("itracker.web.generic.unassigned") + "]");

			issue.setOwner(null);

			if (issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED) {
				issue.setStatus(IssueUtilities.STATUS_UNASSIGNED);
			}
		}
		return true;
	}

	/*
	 * public boolean addIssueActivity(IssueActivityModel model) {
	 * 
	 * Issue issue = ifHome.findByPrimaryKey(model.getIssueId());
	 * 
	 * User user = ufHome.findByPrimaryKey(model.getUserId());
	 * 
	 * //return addIssueActivity(model, issue, user); return
	 * addIssueActivity(null, issue, user); }
	 */

	/*
	 * public boolean addIssueActivity(IssueActivityModel model, Issue issue) {
	 * 
	 * User user = ufHome.findByPrimaryKey(model.getUserId());
	 * 
	 * return true;//addIssueActivity(model, issue, user); }
	 */

	/**
	 * I think this entire method is useless - RJST
	 * TODO move to {@link NotificationService}
	 * 
	 * @param model
	 * @param issue
	 * @param user
	 * @return
	 */
	/*
	 * public boolean addIssueActivity(IssueActivityBean model, Issue issue,
	 * User user) {
	 * 
	 * IssueActivityBean activity = new IssueActivityBean();
	 * 
	 * //activity.setModel(model);
	 * 
	 * activity.setIssue(issue);
	 * 
	 * activity.setUser(user);
	 * 
	 * return true; }
	 */

	public void updateIssueActivityNotification(Integer issueId, boolean notificationSent) {

		if (issueId == null) {

			return;

		}

		Collection<IssueActivity> activity = getIssueActivityDAO().findByIssueId(issueId);

		for (Iterator<IssueActivity> iter = activity.iterator(); iter.hasNext();) {

			((IssueActivity) iter.next()).setNotificationSent(notificationSent);

		}

	}

	/**
	 * Adds an attachment to an issue
	 * 
	 * @param model
	 *            The attachment data
	 * @param data
	 *            The byte data
	 */
	public boolean addIssueAttachment(IssueAttachment attachment, byte[] data) {
		Issue issue = attachment.getIssue();
		User user = attachment.getUser();

		attachment.setFileName("attachment_issue_" + issue.getId() + "_" + attachment.getOriginalFileName());
		attachment.setFileData((data == null ? new byte[0] : data));
		
		attachment.setIssue(issue);
		attachment.setUser(user);
		attachment.setCreateDate(new Date());
		attachment.setLastModifiedDate(attachment.getCreateDate());

		if (logger.isDebugEnabled()) {
			logger.debug("addIssueAttachment: adding attachment " + attachment);
		}
		// add attachment to issue
		issue.getAttachments().add(attachment);
		if (logger.isDebugEnabled()) {
			logger.debug("addIssueAttachment: saving updated issue " + issue);
		}
		this.getIssueDAO().saveOrUpdate(issue);
		return true;
	}

	public boolean setIssueAttachmentData(Integer attachmentId, byte[] data) {

		if (attachmentId != null && data != null) {

			IssueAttachment attachment = getIssueAttachmentDAO().findByPrimaryKey(attachmentId);

			attachment.setFileData(data);

			return true;

		}

		return false;

	}

	public boolean setIssueAttachmentData(String fileName, byte[] data) {

		if (fileName != null && data != null) {

			IssueAttachment attachment = getIssueAttachmentDAO().findByFileName(fileName);

			attachment.setFileData(data);

			return true;

		}

		return false;

	}

	/**
	 * Removes a attachement (deletes it)
	 * 
	 * @param attachmentId
	 *            the id of the <code>IssueAttachmentBean</code>
	 */
	public boolean removeIssueAttachment(Integer attachmentId) {

		IssueAttachment attachementBean = this.getIssueAttachmentDAO().findByPrimaryKey(attachmentId);

		getIssueAttachmentDAO().delete(attachementBean);

		return true;
	}

	public Integer removeIssueHistoryEntry(Integer entryId, Integer userId) {

		IssueHistory history = getIssueHistoryDAO().findByPrimaryKey(entryId);

		if (history != null) {

			history.setStatus(IssueUtilities.HISTORY_STATUS_REMOVED);

			history.setLastModifiedDate(new Timestamp(new Date().getTime()));

			IssueActivity activity = new IssueActivity();
			activity.setType(IssueUtilities.ACTIVITY_REMOVE_HISTORY);
			activity.setDescription(ITrackerResources.getString("itracker.web.generic.entry") + " " + entryId + " "
					+ ITrackerResources.getString("itracker.web.generic.removed") + ".");

			getIssueHistoryDAO().delete(history);

			// need to fix this - RJST
			// activity.setIssue(history.getIssue().getId());
			// activity.setUser(userId);
			return history.getIssue().getId();

		}

		return new Integer(-1);

	}

	public Project getIssueProject(Integer issueId) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		Project project = issue.getProject();

		return project;
	}

	public HashSet<Integer> getIssueComponentIds(Integer issueId) {

		HashSet<Integer> componentIds = new HashSet<Integer>();
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		Collection<Component> components = issue.getComponents();

		for (Iterator<Component> iterator = components.iterator(); iterator.hasNext();) {
			componentIds.add(((Component) iterator.next()).getId());
		}

		return componentIds;

	}

	public HashSet<Integer> getIssueVersionIds(Integer issueId) {

		HashSet<Integer> versionIds = new HashSet<Integer>();

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		Collection<Version> versions = issue.getVersions();

		for (Iterator<Version> iterator = versions.iterator(); iterator.hasNext();) {

			versionIds.add(((Version) iterator.next()).getId());

		}

		return versionIds;

	}

	public List<IssueActivity> getIssueActivity(Integer issueId) {

		int i = 0;

		IssueActivity[] activityArray = new IssueActivity[0];

		Collection<IssueActivity> activity = getIssueActivityDAO().findByIssueId(issueId);

		activityArray = new IssueActivity[activity.size()];

		for (Iterator<IssueActivity> iterator = activity.iterator(); iterator.hasNext(); i++) {

			activityArray[i] = ((IssueActivity) iterator.next());

		}

		return Arrays.asList(activityArray);

	}

	/**
	 * TODO move to {@link NotificationService} ?
	 */
	public List<IssueActivity> getIssueActivity(Integer issueId, boolean notificationSent) {

		int i = 0;

		IssueActivity[] activityArray = new IssueActivity[0];

		Collection<IssueActivity> activity = getIssueActivityDAO().findByIssueIdAndNotification(issueId, notificationSent);

		activityArray = new IssueActivity[activity.size()];

		for (Iterator<IssueActivity> iterator = activity.iterator(); iterator.hasNext(); i++) {

			activityArray[i] = ((IssueActivity) iterator.next());

		}

		return Arrays.asList(activityArray);

	}

	/**
	 * @deprecated use getAllIssuesAttachmentCount() instead.
	 */
	public Long countSystemIssuesAttachments() {
		logger.warn("countSystemIssuesAttachments: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("countSystemIssuesAttachments: stackgtrace was", new RuntimeException());
		}
		
		
		return getIssueAttachmentDAO().countAll();
	}
	public Long getAllIssueAttachmentCount() {
		return getIssueAttachmentDAO().countAll().longValue();
	}
	/**
	 * @deprecated do not use this due to expensive memory use! use explicit hsqldb queries instead.
	 */
	public List<IssueAttachment> getAllIssueAttachments() {
		logger.warn("getAllIssueAttachments: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getAllIssueAttachments: stackgtrace was", new RuntimeException());
		}
		
		List<IssueAttachment> attachments = getIssueAttachmentDAO().findAll();

		return attachments;
	}

	/**
	 * Count total issues size and count from database
	 * @deprecated use seperate issues size and count methods instead
	 */
	public long[] getAllIssueAttachmentsSizeAndCount() {
		logger.warn("getAllIssueAttachmentsSizeAndCount: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getAllIssueAttachmentsSizeAndCount: stackgtrace was", new RuntimeException());
		}

		long[] sizeAndCount = new long[2];
		
		sizeAndCount[0] = getAllIssueAttachmentSize();
		sizeAndCount[1] = getAllIssueAttachmentCount();


		return sizeAndCount;

	}

	public IssueAttachment getIssueAttachment(Integer attachmentId) {
		IssueAttachment attachment = getIssueAttachmentDAO().findByPrimaryKey(attachmentId);

		return attachment;

	}

	public byte[] getIssueAttachmentData(Integer attachmentId) {

		byte[] data = new byte[0];

		IssueAttachment attachment = getIssueAttachmentDAO().findByPrimaryKey(attachmentId);

		data = attachment.getFileData();

		return data;

	}

	public int getIssueAttachmentCount(Integer issueId) {

		int i = 0;

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		Collection<IssueAttachment> attachments = issue.getAttachments();

		i = attachments.size();

		return i;

	}

	/**
	 * 
	 * Returns the latest issue history entry for a particular issue.
	 * 
	 * 
	 * 
	 * @param issueId
	 * 
	 * the id of the issue to return the history entry for.
	 * 
	 * @return the latest IssueHistory, or null if no entries could be
	 * 
	 * found
	 */

	public IssueHistory getLastIssueHistory(Integer issueId) {

		IssueHistory model = null;

		IssueHistory lastEntry = null;

		Collection<IssueHistory> history = getIssueHistoryDAO().findByIssueId(issueId);

		Iterator<IssueHistory> iterator = history.iterator();

		while (iterator.hasNext()) {

			IssueHistory nextEntry = (IssueHistory) iterator.next();

			if (nextEntry != null) {

				if (lastEntry == null && nextEntry.getLastModifiedDate() != null) {

					lastEntry = nextEntry;

				} else if (nextEntry.getLastModifiedDate() != null

				&& nextEntry.getLastModifiedDate().equals(lastEntry.getLastModifiedDate())

				&& nextEntry.getId().compareTo(lastEntry.getId()) > 0) {

					lastEntry = nextEntry;

				} else if (nextEntry.getLastModifiedDate() != null

				&& nextEntry.getLastModifiedDate().after(lastEntry.getLastModifiedDate())) {

					lastEntry = nextEntry;

				}

			}

		}

		return model;

	}

	/**
	 * 
	 * Retrieves the primary issue notifications. Primary notifications are
	 * 
	 * defined as the issue owner (or creator if not assigned), and any project
	 * 
	 * owners. This should encompass the list of people that should be notified
	 * 
	 * so that action can be taken on an issue that needs immediate attention.
	 * 
	 * TODO move to {@link NotificationService}
	 * 
	 * @param issueId
	 * 
	 * the id of the issue to find notifications for
	 * 
	 * @returns an array of NotificationModels
	 * @deprecated moved to {@link NotificationService}
	 */

	public List<Notification> getPrimaryIssueNotifications(Integer issueId) {
		Issue issue = getIssue(issueId);
		return notificationService.getIssueNotifications(issue, true, false);

	}

	/**
	 * 
	 * Retrieves all notifications for an issue where the notification's user is
	 * 
	 * also active.
	 * 
	 * TODO move to {@link NotificationService}
	 * 
	 * @param issueId
	 * 
	 * the id of the issue to find notifications for
	 * @returns an array of NotificationModels
	 */

	public List<Notification> getIssueNotifications(Integer issueId) {
		Issue issue = getIssue(issueId);
		return notificationService.getIssueNotifications(issue, false, true);

	}

	/**
	 * Retrieves an array of issue notifications. The notifications by default
	 * is the creator and owner of the issue, all project admins for the issue's
	 * project, and anyone else that has a notfication on file.
	 * TODO move to {@link NotificationService}
	 * @deprecated moved to {@link NotificationService}
	 * 
	 * @param issueId
	 *            the id of the issue to find notifications for
	 * @param primaryOnly
	 *            only include the primary notifications
	 * @param activeOnly
	 *            only include the notification if the user is currently active
	 *            (not locked or deleted)
	 * @returns an array of NotificationModels
	 * @see IssueServiceImpl#getPrimaryIssueNotifications
	 */
	public List<Notification> getIssueNotifications(Integer issueId, boolean primaryOnly, boolean activeOnly) {
		
		logger.warn("getIssueNotifications: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getIssueNotifications: stackgtrace was", new RuntimeException());
		}
		
		return notificationService.getIssueNotifications(getIssue(issueId), primaryOnly, activeOnly);


	}

	/**
	 * TODO Move to {@link NotificationService}
	 * @deprecated moved to {@link NotificationService}
	 */
	public boolean removeIssueNotification(Integer notificationId) {
		logger.warn("removeIssueNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("removeIssueNotification: stackgtrace was", new RuntimeException());
		}
		
		return notificationService.removeIssueNotification(notificationId);
	}

	/**
	 */
	public boolean addIssueNotification(Notification thisnotification) {
		User user = thisnotification.getUser();

		Issue issue = thisnotification.getIssue();
		if (thisnotification.getCreateDate() == null) {
			thisnotification.setCreateDate(new Date());
		}
		if (thisnotification.getLastModifiedDate() == null) {
			thisnotification.setLastModifiedDate(new Date());
		}
		List<Notification> notifications = new ArrayList<Notification>();
		notifications.add(thisnotification);
		issue.setNotifications(notifications);
		// TODO: check these 3 lines - do we need them?:
		Notification notification = new Notification();
		notification.setIssue(issue);
		notification.setUser(user);

		getIssueDAO().saveOrUpdate(issue);
		return true;
	}

	/**
	 * TODO move to notification service
	 * @deprecated
	 */
	public boolean hasIssueNotification(Integer issueId, Integer userId) {
		logger.warn("hasIssueNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("hasIssueNotification: stackgtrace was", new RuntimeException());
		}
		Issue issue = getIssue(issueId);
		
		return notificationService.hasIssueNotification(issue, userId);

	}

	

	public int getOpenIssueCountByProjectId(Integer projectId) {

		Collection<Issue> issues = getIssueDAO().findByProjectAndLowerStatus(projectId, IssueUtilities.STATUS_RESOLVED);

		return issues.size();

	}

	public int getResolvedIssueCountByProjectId(Integer projectId) {

		Collection<Issue> issues = getIssueDAO().findByProjectAndHigherStatus(projectId, IssueUtilities.STATUS_RESOLVED);

		return issues.size();

	}

	public int getTotalIssueCountByProjectId(Integer projectId) {

		Collection<Issue> issues = getIssueDAO().findByProject(projectId);

		return issues.size();

	}

	public Date getLatestIssueDateByProjectId(Integer projectId) {

		return getIssueDAO().latestModificationDate(projectId);

	}

	/**
	 * TODO move to {@link NotificationService}
	 * @deprecated
	 */
	public void sendNotification(Integer issueId, int type, String baseURL) {
		logger.warn("sendNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("sendNotification: stackgtrace was", new RuntimeException());
		}
		
		logger.warn("sendNotification: method not implemented yet!!");
		throw new NotImplementedException();
	}

	/**
	 * TODO move to {@link NotificationService}
	 * @deprecated
	 */
	public void sendNotification(Integer issueId, int type, String baseURL, HashSet<String> addresses,
			Integer lastModifiedDays) {
		logger.warn("sendNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("sendNotification: stackgtrace was", new RuntimeException());
		}
		
		logger.warn("sendNotification: method not implemented yet!!");
	
		
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		
		throw new NotImplementedException();
		
		
		/*
		 * try {
		 * 
		 * message.setInt("issueId", issueId.intValue()); message.setInt("type",
		 * type); message.setObject("lastModifiedDays", (lastModifiedDays ==
		 * null ? new Integer(-1) : lastModifiedDays)); if (systemBaseURL !=
		 * null && !systemBaseURL.equals("")) { message.setString("baseURL",
		 * systemBaseURL); } else if (baseURL != null) {
		 * message.setString("baseURL", baseURL); }
		 * 
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 * ObjectOutputStream oos = new ObjectOutputStream(baos);
		 * oos.writeObject(addresses); message.setObject("addresses",
		 * baos.toByteArray()); } sender.send(message);
		 */

	}

	public boolean canViewIssue(Integer issueId, User user) {

		Issue issue = getIssue(issueId);

		Map<Integer, Set<PermissionType>> permissions = getUserDAO().getUsersMapOfProjectsAndPermissionTypes(user,
				AuthenticationConstants.REQ_SOURCE_WEB);

		return IssueUtilities.canViewIssue(issue, user.getId(), permissions);

	}

	public boolean canViewIssue(Issue issue, User user) {

		Map<Integer, Set<PermissionType>> permissions = getUserDAO().getUsersMapOfProjectsAndPermissionTypes(user,
				AuthenticationConstants.REQ_SOURCE_WEB);

		return IssueUtilities.canViewIssue(issue, user.getId(), permissions);

	}

	private UserDAO getUserDAO() {
		return userDAO;
	}

	private IssueDAO getIssueDAO() {
		return issueDAO;
	}


	private ProjectDAO getProjectDAO() {
		return projectDAO;
	}

	private IssueActivityDAO getIssueActivityDAO() {
		return issueActivityDAO;
	}

	private VersionDAO getVersionDAO() {
		return this.versionDAO;
	}

	private ComponentDAO getComponentDAO() {
		return this.componentDAO;
	}

	private CustomFieldDAO getCustomFieldDAO() {
		return customFieldDAO;
	}

	private IssueHistoryDAO getIssueHistoryDAO() {
		return issueHistoryDAO;
	}

	private IssueRelationDAO getIssueRelationDAO() {
		return issueRelationDAO;
	}

	private IssueAttachmentDAO getIssueAttachmentDAO() {
		return issueAttachmentDAO;
	}

	/**
	 * get total size of all attachments in database
	 */
	public Long getAllIssueAttachmentSize() {

		return getIssueAttachmentDAO().countAll().longValue();

	}
	
    public List<Issue> searchIssues(IssueSearchQuery queryModel, User user, Map<Integer, Set<PermissionType>> userPermissions) throws IssueSearchException {    	
    	return getIssueDAO().query(queryModel, user, userPermissions);
    }

    /**
     *  TODO right place here? ConfigurationService probably.
     * @param systemBaseURL
     */
	public static void setSystemBaseURL(String systemBaseURL) {
		IssueServiceImpl.systemBaseURL = systemBaseURL;
	
	}
	/**
	 * @deprecated no more factory is used
	 */
	public static String getNotificationFactoryName() {
		logger.warn("getNotificationFactoryName: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getNotificationFactoryName: stackgtrace was", new RuntimeException());
		}
		
		return null;
	}

	/**
	 * @deprecated no more factory is used
	 * @param notificationFactoryName
	 */
	public static void setNotificationFactoryName(String notificationFactoryName) {
		logger.warn("setNotificationFactoryName: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("setNotificationFactoryName: stackgtrace was", new RuntimeException());
		}
	}

	/**
	 * @deprecated no more queue for notifications
	 * @return
	 */
	public static String getNotificationQueueName() {
		logger.warn("getNotificationQueueName: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getNotificationQueueName: stackgtrace was", new RuntimeException());
		}
		return null;
	}

	/**
	 * @deprecated no more queue for notifications
	 * @param notificationQueueName
	 */
	public static void setNotificationQueueName(String notificationQueueName) {
		logger.warn("setNotificationQueueName: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("setNotificationQueueName: stackgtrace was", new RuntimeException());
		}
	}

	public static String getSystemBaseURL() {
		return systemBaseURL;
	}


	public Long totalSystemIssuesAttachmentSize() {
		return getIssueAttachmentDAO().totalAttachmentsSize();
	}
}	
