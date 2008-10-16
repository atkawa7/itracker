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
import org.itracker.model.IssueActivityType;
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

/**
 * Issue related service layer. A bit "fat" at this time, because of being a
 * direct EJB porting. Going go get thinner over time
 * 
 * @author ricardo
 * 
 */
public class IssueServiceImpl implements IssueService {


	private static final Logger logger = Logger
			.getLogger(IssueServiceImpl.class);

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

	public IssueServiceImpl(UserDAO userDAO, ProjectDAO projectDAO,
			IssueDAO issueDAO, IssueHistoryDAO issueHistoryDAO,
			IssueRelationDAO issueRelationDAO,
			IssueAttachmentDAO issueAttachmentDAO, ComponentDAO componentDAO,
			IssueActivityDAO issueActivityDAO, VersionDAO versionDAO, CustomFieldDAO customFieldDAO, NotificationService notificationService) {

		this.userDAO = userDAO;
		this.projectDAO = projectDAO;
		this.issueDAO = issueDAO;
		this.issueHistoryDAO = issueHistoryDAO;
		this.issueRelationDAO = issueRelationDAO;
		this.issueAttachmentDAO = issueAttachmentDAO;
		this.componentDAO = componentDAO;
		this.issueActivityDAO = issueActivityDAO;
		this.versionDAO = versionDAO;
		this.customFieldDAO = customFieldDAO;
		this.notificationService = notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		if (null == notificationService) {
			throw new IllegalArgumentException(
					"notification service must not be null");
		}
		if (null != this.notificationService) {
			throw new IllegalStateException(
					"notification service has already been set");
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
			logger.debug("getAllIssues: stacktrace was",
					new RuntimeException());
		}
		return getIssueDAO().findAll();
	}

	/**
	 * Added implementation to make proper count of ALL issues, instead select
	 * them in a list and return its size
	 */
	public Long getNumberIssues() {
		return getIssueDAO().countAllIssues();
	}

	public List<Issue> getIssuesCreatedByUser(Integer userId) {
		return getIssuesCreatedByUser(userId, true);
	}

	public List<Issue> getIssuesCreatedByUser(Integer userId,
			boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByCreatorInAvailableProjects(userId,
					IssueUtilities.STATUS_CLOSED);
		} else {
			issues = getIssueDAO().findByCreator(userId,
					IssueUtilities.STATUS_CLOSED);
		}
		return issues;
	}

	public List<Issue> getIssuesOwnedByUser(Integer userId) {

		return getIssuesOwnedByUser(userId, true);

	}

	public List<Issue> getIssuesOwnedByUser(Integer userId,
			boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByOwnerInAvailableProjects(userId,
					IssueUtilities.STATUS_RESOLVED);
		} else {
			issues = getIssueDAO().findByOwner(userId,
					IssueUtilities.STATUS_RESOLVED);
		}
		return issues;
	}

	public List<Issue> getIssuesWatchedByUser(Integer userId) {
		return getIssuesWatchedByUser(userId, true);
	}

	/**
	 * TODO move to {@link NotificationService}
	 */
	public List<Issue> getIssuesWatchedByUser(Integer userId,
			boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO().findByNotificationInAvailableProjects(
					userId, IssueUtilities.STATUS_CLOSED);
		} else {
			issues = getIssueDAO().findByNotification(userId,
					IssueUtilities.STATUS_CLOSED);
		}
		return issues;
	}

	public List<Issue> getUnassignedIssues() {
		return getUnassignedIssues(true);
	}

	public List<Issue> getUnassignedIssues(boolean availableProjectsOnly) {
		final List<Issue> issues;

		if (availableProjectsOnly) {
			issues = getIssueDAO()
					.findByStatusLessThanEqualToInAvailableProjects(
							IssueUtilities.STATUS_UNASSIGNED);
		} else {
			issues = getIssueDAO().findByStatusLessThanEqualTo(
					IssueUtilities.STATUS_UNASSIGNED);
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
		List<Issue> issues = getIssueDAO().findByProjectAndLowerStatus(
				projectId, status);
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

	public Issue createIssue(Issue issue, Integer projectId, Integer userId,
			Integer createdById) throws ProjectException {
		Project project = getProjectDAO().findByPrimaryKey(projectId);
		User creator = getUserDAO().findByPrimaryKey(userId);

		if (project.getStatus() != Status.ACTIVE) {
			throw new ProjectException("Project is not active.");
		}

		IssueActivity activity = new IssueActivity(issue, creator,
				IssueActivityType.ISSUE_CREATED);
		// activity.setActivityType(org.itracker.model.IssueActivity.Type.ISSUE_CREATED);
		activity.setDescription(ITrackerResources
				.getString("itracker.activity.system.createdfor")
				+ " " + creator.getFirstName() + " " + creator.getLastName());

		activity.setIssue(issue);
		// activity.setCreateDate(new Date());
		// activity.setLastModifiedDate(new Date());

		if (!(createdById == null || createdById.equals(userId))) {

			User createdBy = getUserDAO().findByPrimaryKey(createdById);
			activity.setUser(createdBy);

			Notification watchModel = new Notification();

			watchModel.setUser(creator);

			watchModel.setIssue(issue);

			watchModel.setRole(Notification.Role.IP);

			addIssueNotification(watchModel);

		}

		List<IssueActivity> activities = new ArrayList<IssueActivity>();
		activities.add(activity);
		issue.setActivities(activities);

		issue.setProject(project);

		issue.setCreator(creator);

		// save
		// TODO: The filter should automatically take care of the
		// following two timestamps, removed them
		// issue.setCreateDate(new Timestamp(new Date().getTime()));
		// issue.setLastModifiedDate(issue.getCreateDate());
		getIssueDAO().save(issue);

		return issue;
	}

	/**
	 * Save a modified issue to the persistence layer
	 * 
	 * @param issueDirty
	 *            the changed, unsaved issue to update on persistency layer
	 * @param userId
	 *            the user-id of the changer
	 * 
	 */
	public Issue updateIssue(Issue issueDirty, Integer userId)
			throws ProjectException {
		if (logger.isDebugEnabled()) {
			logger.debug("updateIssue: updating issue " + issueDirty);
		}
		String existingTargetVersion = null;

		// detach the modified Issue form the Hibernate Session
		getIssueDAO().detach(issueDirty);
		// Retrieve the Issue from Hibernate Session and refresh it from
		// Hibernate Session to previous state.
		Issue persistedIssue = getIssueDAO().findByPrimaryKey(
				issueDirty.getId());
		
		getIssueDAO().refresh(persistedIssue);

		User user = getUserDAO().findByPrimaryKey(userId);

		if (persistedIssue.getProject().getStatus() != Status.ACTIVE) {
			throw new ProjectException("Project "
					+ persistedIssue.getProject().getName() + " is not active.");
		}

		if (persistedIssue.getDescription() != null
				&& !persistedIssue.getDescription().equalsIgnoreCase(
						issueDirty.getDescription())) {

			if (logger.isDebugEnabled()) {
				logger.debug("updateIssue: updating description from " + persistedIssue.getDescription());
			}
			IssueActivity activity = new IssueActivity();
			activity.setActivityType(IssueActivityType.DESCRIPTION_CHANGE);
			activity.setDescription(ITrackerResources
					.getString("itracker.web.generic.from")
					+ ": " + persistedIssue.getDescription());
			activity.setUser(user);
			activity.setIssue(issueDirty);
			issueDirty.getActivities().add(activity);

		}

		if (persistedIssue.getResolution() != null
				&& !persistedIssue.getResolution().equalsIgnoreCase(
						issueDirty.getResolution())) {

			IssueActivity activity = new IssueActivity();
			activity.setActivityType(IssueActivityType.RESOLUTION_CHANGE);
			activity.setDescription(ITrackerResources
					.getString("itracker.web.generic.from")
					+ ": " + persistedIssue.getResolution());
			activity.setUser(user);
			activity.setIssue(issueDirty);
			issueDirty.getActivities().add(activity);
		}

		if (persistedIssue.getStatus() != issueDirty.getStatus()
				&& issueDirty.getStatus() != -1) {

			IssueActivity activity = new IssueActivity();
			activity.setActivityType(IssueActivityType.STATUS_CHANGE);
			activity.setDescription(IssueUtilities.getStatusName(persistedIssue
					.getStatus())
					+ " "
					+ ITrackerResources.getString("itracker.web.generic.to")
					+ " "
					+ IssueUtilities.getStatusName(issueDirty.getStatus()));
			activity.setUser(user);
			activity.setIssue(issueDirty);
			issueDirty.getActivities().add(activity);
		}

		if (issueDirty.getSeverity()!= null && !issueDirty.getSeverity().equals(persistedIssue.getSeverity())
				&& issueDirty.getSeverity() != -1) {

			IssueActivity activity = new IssueActivity();
			activity.setActivityType(IssueActivityType.SEVERITY_CHANGE);
			// FIXME why does it state Critical to Critical when it should Major to Critical!?
			activity.setDescription(/*IssueUtilities
					.getSeverityName(persistedIssue.getSeverity())
					+ " "
					+ */
					ITrackerResources.getString("itracker.web.generic.to")
					+ " "
					+ IssueUtilities.getSeverityName(issueDirty.getSeverity()));

			activity.setUser(user);
			activity.setIssue(issueDirty);
			issueDirty.getActivities().add(activity);
		}

		if (persistedIssue.getTargetVersion() != null
				&& issueDirty.getTargetVersion() != null
				&& !persistedIssue.getTargetVersion().getId().equals(
						issueDirty.getTargetVersion().getId())) {
			existingTargetVersion = persistedIssue.getTargetVersion()
					.getNumber();
			Version version = this.getVersionDAO().findByPrimaryKey(
					issueDirty.getTargetVersion().getId());

			// persistedIssue.setTargetVersion(version);

			IssueActivity activity = new IssueActivity();
			activity.setActivityType(IssueActivityType.TARGETVERSION_CHANGE);
			String description = existingTargetVersion + " "
					+ ITrackerResources.getString("itracker.web.generic.to")
					+ " ";
			description += version.getNumber();
			activity.setDescription(description);
			activity.setUser(user);
			activity.setIssue(issueDirty);
			issueDirty.getActivities().add(activity);
		}
	
		if (logger.isDebugEnabled()) {
			logger.debug("updateIssue: merging issue " + issueDirty + " to " + persistedIssue);
		}
		
		persistedIssue = getIssueDAO().merge(issueDirty);

		if (logger.isDebugEnabled()) {
			logger.debug("updateIssue: merged issue for saving: " + persistedIssue);
		}
		getIssueDAO().saveOrUpdate(persistedIssue);
		if (logger.isDebugEnabled()) {
			logger.debug("updateIssue: saved issue: " + persistedIssue);
		}
		return persistedIssue;
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
		
		if (logger.isDebugEnabled()) {
			logger.debug("moveIssue: " + issue + " to project#" + projectId + ", user#" + userId);
		}
		
		Project project = getProjectDAO().findByPrimaryKey(projectId);
		User user = getUserDAO().findByPrimaryKey(userId);
		
		if (logger.isDebugEnabled()) {
			logger.debug("moveIssue: " + issue + " to project: " + project + ", user: " + user);
		}
		
		IssueActivity activity = new IssueActivity();
		activity
				.setActivityType(org.itracker.model.IssueActivityType.ISSUE_MOVE);
		activity.setDescription(issue.getProject().getName() + " "
				+ ITrackerResources.getString("itracker.web.generic.to") + " "
				+ project.getName());
		activity.setUser(user);
		activity.setIssue(issue);
		issue.setProject(project);
		
		
		// The versions and components are per project so we need to delete
		// these

// TODO: ranks, removed following lines due to failing issue-save. is it really necessary? how can it be done?
//		setIssueComponents(issue.getId(), new HashSet<Integer>(), userId);
//
//		setIssueVersions(issue.getId(), new HashSet<Integer>(), userId);
//
//		setIssueFields(issue.getId(), new ArrayList<IssueField>());
		
		issue.getActivities().add(activity);
		
		if (logger.isDebugEnabled()) {
			logger.debug("moveIssue: updated issue: " + issue);
		}
		try {
			getIssueDAO().saveOrUpdate(issue);
		} catch(Exception e) {
			logger.error("moveIssue: failed to save issue: " + issue, e);
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("moveIssue: saved move-issue to " + project);
		}
		return issue;

	}

	/**
	 * this should not exist. adding an history entry should be adding the
	 * history entry to the domain object and saving the object...
	 */
	public boolean addIssueHistory(IssueHistory history) {
		getIssueHistoryDAO().saveOrUpdate(history);
		history.getIssue().getHistory().add(history);
		getIssueDAO().saveOrUpdate(history.getIssue());
		return true;
	}

	public boolean setIssueFields(Integer issueId, List<IssueField> fields) {
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		List<IssueField> issueFields = issue.getFields();

		// for (Iterator<IssueField> iter = issueFields.iterator();
		// iter.hasNext();) {
		// TODO: clean this code
		// try {

		// IssueField field = (IssueField) iter.next();

		// iter.remove();

		// field.remove();

		// } catch(RemoveException re) {

		// logger.info("Unable to remove issue field value. Manual

		// database cleanup may be necessary.");

		// }

		// }

		if (fields.size() > 0) {
			for (int i = 0; i < fields.size(); i++) {
				IssueField field = new IssueField();
				CustomField customField = getCustomFieldDAO().findByPrimaryKey(
						fields.get(i).getCustomField().getId());
				field.setCustomField(customField);
				field.setIssue(issue);
				field.setDateValue(new Timestamp(new Date().getTime()));
				issueFields.add(field);
			}
		}

		getIssueDAO().saveOrUpdate(issue);
		return true;
	}

	public boolean setIssueComponents(Integer issueId,
			HashSet<Integer> componentIds, Integer userId) {

		boolean wasChanged = false;

		StringBuffer changesBuf = new StringBuffer();

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		List<Component> components = issue.getComponents();

		if (components != null) {

			if (componentIds.isEmpty() && !components.isEmpty()) {

				wasChanged = true;

				changesBuf.append(ITrackerResources
						.getString("itracker.web.generic.all")
						+ " "

						+ ITrackerResources
								.getString("itracker.web.generic.removed"));

				components.clear();

			} else {

				for (Iterator<Component> iterator = components.iterator(); iterator
						.hasNext();) {

					Component component = (Component) iterator.next();

					if (componentIds.contains(component.getId())) {

						componentIds.remove(component.getId());

					} else {

						wasChanged = true;

						changesBuf.append(ITrackerResources
								.getString("itracker.web.generic.removed")
								+ ": "

								+ component.getName() + "; ");

						iterator.remove();

					}

				}

				for (Iterator<Integer> iterator = componentIds.iterator(); iterator
						.hasNext();) {

					Integer componentId = iterator.next();

					Component component = getComponentDAO().findById(
							componentId);

					wasChanged = true;

					changesBuf.append(ITrackerResources
							.getString("itracker.web.generic.added")
							+ ": "

							+ component.getName() + "; ");

					components.add(component);

				}

			}
		} else {
			logger.debug("components was null!");
		}

		if (wasChanged) {

			IssueActivity activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.COMPONENTS_MODIFIED);
			activity.setDescription(changesBuf.toString());
			activity.setIssue(issue);
			issue.getActivities().add(activity);
			getIssueDAO().saveOrUpdate(issue);
		}

		return true;

	}

	public boolean setIssueVersions(Integer issueId,
			HashSet<Integer> versionIds, Integer userId) {

		boolean wasChanged = false;

		StringBuffer changesBuf = new StringBuffer();

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		List<Version> versions = issue.getVersions();

		if (versions != null) {

			if (versionIds.isEmpty() && !versions.isEmpty()) {

				wasChanged = true;

				changesBuf.append(ITrackerResources
						.getString("itracker.web.generic.all")
						+ " "

						+ ITrackerResources
								.getString("itracker.web.generic.removed"));

				versions.clear();

			} else {

				for (Iterator<Version> iterator = versions.iterator(); iterator
						.hasNext();) {

					Version version = (Version) iterator.next();

					if (versionIds.contains(version.getId())) {

						versionIds.remove(version.getId());

					} else {

						wasChanged = true;

						changesBuf.append(ITrackerResources
								.getString("itracker.web.generic.removed")
								+ ": "

								+ version.getNumber() + "; ");

						iterator.remove();

					}

				}

				for (Iterator<Integer> iterator = versionIds.iterator(); iterator
						.hasNext();) {

					Integer versionId = (Integer) iterator.next();

					Version version = getVersionDAO().findByPrimaryKey(
							versionId);

					wasChanged = true;

					changesBuf.append(ITrackerResources
							.getString("itracker.web.generic.added")
							+ ": "

							+ version.getNumber() + "; ");

					versions.add(version);

				}

			}

		} else {
			logger.debug("Versions were null!");
		}

		if (wasChanged) {

			IssueActivity activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.TARGETVERSION_CHANGE);
			activity.setDescription(changesBuf.toString());
			activity.setIssue(issue);
			issue.getActivities().add(activity);
			getIssueDAO().saveOrUpdate(issue);
		}

		return true;

	}

	public IssueRelation getIssueRelation(Integer relationId) {

		IssueRelation issueRelation = getIssueRelationDAO().findByPrimaryKey(
				relationId);

		return issueRelation;

	}
	/**
	 * add a relation between two issues.
	 * 
	 * TODO: There is no relation saved to database yet?
	 */
	public boolean addIssueRelation(Integer issueId, Integer relatedIssueId,
			int relationType, Integer userId) {

		if (issueId != null && relatedIssueId != null) {

			int matchingRelationType = IssueUtilities
					.getMatchingRelationType(relationType);

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

			relationA.setLastModifiedDate(new java.sql.Timestamp(
					new Date().getTime()));

			IssueRelation relationB = new IssueRelation();

			relationB.setRelationType(matchingRelationType);

			// relationB.setMatchingRelationId(relationAId);

			relationB.setIssue(relatedIssue);

			relationB.setRelatedIssue(issue);

			relationB.setLastModifiedDate(new java.sql.Timestamp(
					new Date().getTime()));

			IssueActivity activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.RELATION_ADDED);
			activity.setDescription(ITrackerResources
					.getString("itracker.activity.relation.add"));
			// probably add this to description
			// new Object[] {IssueUtilities.getRelationName(relationType),
			// relatedIssueId };
			activity.setIssue(issue);
			issue.getActivities().add(activity);
			getIssueDAO().saveOrUpdate(issue);
			// need to set user here... userId);
			// need to save here

			activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.RELATION_ADDED);
			activity.setDescription(ITrackerResources.getString(
					"itracker.activity.relation.add",
					new Object[] { IssueUtilities
							.getRelationName(matchingRelationType) }));
			activity.setIssue(relatedIssue);
			relatedIssue.getActivities().add(activity);
			getIssueDAO().saveOrUpdate(relatedIssue);
			return true;

		}

		return false;

	}

	public void removeIssueRelation(Integer relationId, Integer userId) {
		IssueRelation issueRelation = getIssueRelationDAO().findByPrimaryKey(
				relationId);
		Integer issueId = issueRelation.getIssue().getId();

		Integer relatedIssueId = issueRelation.getRelatedIssue().getId();

		Integer matchingRelationId = issueRelation.getMatchingRelationId();

		if (matchingRelationId != null) {
			IssueActivity activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.RELATION_REMOVED);
			activity.setDescription(ITrackerResources.getString(
					"itracker.activity.relation.removed", issueId.toString()));
			// need to fix the commented code and save
			// activity.setIssue(relatedIssueId);
			// activity.setUser(userId);
			// IssueRelationDAO.remove(matchingRelationId);
		}

		IssueActivity activity = new IssueActivity();
		activity
				.setActivityType(org.itracker.model.IssueActivityType.RELATION_REMOVED);
		activity.setDescription(ITrackerResources
				.getString("itracker.activity.relation.removed", relatedIssueId
						.toString()));
		// activity.setIssue(issueId);
		// activity.setUser(userId);
		// irHome.remove(relationId);
		// need to save
	}

	public boolean assignIssue(Integer issueId, Integer userId) {
		return assignIssue(issueId, userId, userId);
	}

	public boolean assignIssue(Integer issueId, Integer userId,
			Integer assignedByUserId) {
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
					&& !notificationService.hasIssueNotification(issue,
							currOwner.getId(), Role.CONTRIBUTER)) {
				// Notification notification = new Notification();
				Notification notification = new Notification(currOwner, issue,
						Role.CONTRIBUTER);
				// TODO check implementation
				addIssueNotification(notification);
			}

			IssueActivity activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.OWNER_CHANGE);
			activity.setDescription((currOwner == null ? "["
					+ ITrackerResources
							.getString("itracker.web.generic.unassigned") + "]"
					: currOwner.getLogin())
					+ " "
					+ ITrackerResources.getString("itracker.web.generic.to")
					+ " " + user.getLogin());
			activity.setUser(assignedByUser);
			activity.setIssue(issue);
			issue.getActivities().add(activity);

			issue.setOwner(user);

			if (issue.getStatus() < IssueUtilities.STATUS_ASSIGNED) {
				issue.setStatus(IssueUtilities.STATUS_ASSIGNED);
			}
		}
		// send assignment notification
		// TODO: configurationService should be set from context
		// This is not good, when an issue is updated and reassigned, 
		// there is two notifications sent (first assigned-notification, 
		// but old history, then the updated-notification)
//		notificationService.sendNotification(issue, Type.ASSIGNED,
//				ServletContextUtils.getItrackerServices()
//						.getConfigurationService().getSystemBaseURL());
		
		return true;

	}

	public boolean unassignIssue(Integer issueId, Integer assignedByUserId) {
		User assignedByUser = getUserDAO().findByPrimaryKey(assignedByUserId);
		Issue issue = getIssueDAO().findByPrimaryKey(issueId);
		if (issue.getOwner() != null) {

			if (!notificationService.hasIssueNotification(issue, issue
					.getOwner().getId(), Role.CONTRIBUTER)) {
				// Notification notification = new Notification();
				Notification notification = new Notification(issue.getOwner(),
						issue, Role.CONTRIBUTER);
				// TODO check implementation
				addIssueNotification(notification);
			}
			IssueActivity activity = new IssueActivity(issue, assignedByUser,
					IssueActivityType.OWNER_CHANGE);
			activity
					.setDescription((issue.getOwner() == null ? "["
							+ ITrackerResources
									.getString("itracker.web.generic.unassigned")
							+ "]"
							: issue.getOwner().getLogin())
							+ " "
							+ ITrackerResources
									.getString("itracker.web.generic.to")
							+ " ["
							+ ITrackerResources
									.getString("itracker.web.generic.unassigned")
							+ "]");

			issue.setOwner(null);

			if (issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED) {
				issue.setStatus(IssueUtilities.STATUS_UNASSIGNED);
			}
		}
		return true;
	}

	/**
	 * System-Update an issue, adds the action to the issue and updates the issue
	 */
	public Issue systemUpdateIssue(Issue updateissue, Integer userId)
			throws ProjectException {

		IssueActivity activity = new IssueActivity();
		activity.setActivityType(IssueActivityType.SYSTEM_UPDATE);
		activity.setDescription(ITrackerResources
				.getString("itracker.activity.system.status"));
		ArrayList<IssueActivity> activities = new ArrayList<IssueActivity>();

		activity.setIssue(updateissue);
		updateissue.getActivities().add(activity);

		Issue updated = updateIssue(updateissue, userId);
		updated.getActivities().addAll(activities);
		getIssueDAO().saveOrUpdate(updated);

		return updated;
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
	 * I think this entire method is useless - RJST TODO move to
	 * {@link NotificationService}
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

	public void updateIssueActivityNotification(Integer issueId,
			boolean notificationSent) {

		if (issueId == null) {

			return;

		}

		Collection<IssueActivity> activity = getIssueActivityDAO()
				.findByIssueId(issueId);

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

		attachment.setFileName("attachment_issue_" + issue.getId() + "_"
				+ attachment.getOriginalFileName());
		attachment.setFileData((data == null ? new byte[0] : data));

		attachment.setIssue(issue);
		attachment.setUser(user);

		// TODO: activity for adding attachment?
		// IssueActivity activityAdd = new IssueActivity(attachment.getIssue(),
		// user, IssueActivity.Type.ATTACHEMENT_ADDED)

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

			IssueAttachment attachment = getIssueAttachmentDAO()
					.findByPrimaryKey(attachmentId);

			attachment.setFileData(data);

			return true;

		}

		return false;

	}

	public boolean setIssueAttachmentData(String fileName, byte[] data) {

		if (fileName != null && data != null) {

			IssueAttachment attachment = getIssueAttachmentDAO()
					.findByFileName(fileName);

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

		IssueAttachment attachementBean = this.getIssueAttachmentDAO()
				.findByPrimaryKey(attachmentId);

		getIssueAttachmentDAO().delete(attachementBean);

		return true;
	}

	public Integer removeIssueHistoryEntry(Integer entryId, Integer userId) {

		IssueHistory history = getIssueHistoryDAO().findByPrimaryKey(entryId);

		if (history != null) {

			history.setStatus(IssueUtilities.HISTORY_STATUS_REMOVED);

//	      moved date stuff to BaseHibernateDAO
//			history.setLastModifiedDate(new Timestamp(new Date().getTime()));

			IssueActivity activity = new IssueActivity();
			activity
					.setActivityType(org.itracker.model.IssueActivityType.REMOVE_HISTORY);
			activity.setDescription(ITrackerResources
					.getString("itracker.web.generic.entry")
					+ " "
					+ entryId
					+ " "
					+ ITrackerResources
							.getString("itracker.web.generic.removed") + ".");

			getIssueHistoryDAO().delete(history);

			// need to fix this - RJST
			// activity.setIssue(history.getIssue().getId());
			// activity.setUser(userId);
			return history.getIssue().getId();

		}

		return Integer.valueOf(-1);

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

		for (Iterator<Component> iterator = components.iterator(); iterator
				.hasNext();) {
			componentIds.add(((Component) iterator.next()).getId());
		}

		return componentIds;

	}

	public HashSet<Integer> getIssueVersionIds(Integer issueId) {

		HashSet<Integer> versionIds = new HashSet<Integer>();

		Issue issue = getIssueDAO().findByPrimaryKey(issueId);

		Collection<Version> versions = issue.getVersions();

		for (Iterator<Version> iterator = versions.iterator(); iterator
				.hasNext();) {

			versionIds.add(((Version) iterator.next()).getId());

		}

		return versionIds;

	}

	public List<IssueActivity> getIssueActivity(Integer issueId) {

		int i = 0;

		Collection<IssueActivity> activity = getIssueActivityDAO()
				.findByIssueId(issueId);

		IssueActivity[] activityArray = new IssueActivity[activity.size()];

		for (Iterator<IssueActivity> iterator = activity.iterator(); iterator
				.hasNext(); i++) {

			activityArray[i] = ((IssueActivity) iterator.next());

		}

		return Arrays.asList(activityArray);

	}

	/**
	 * TODO move to {@link NotificationService} ?
	 */
	public List<IssueActivity> getIssueActivity(Integer issueId,
			boolean notificationSent) {

		int i = 0;

		

		Collection<IssueActivity> activity = getIssueActivityDAO()
				.findByIssueIdAndNotification(issueId, notificationSent);

		IssueActivity[] activityArray = new IssueActivity[activity.size()];

		for (Iterator<IssueActivity> iterator = activity.iterator(); iterator
				.hasNext(); i++) {

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
			logger.debug("countSystemIssuesAttachments: stacktrace was",
					new RuntimeException());
		}

		return getIssueAttachmentDAO().countAll();
	}

	public Long getAllIssueAttachmentCount() {
		return getIssueAttachmentDAO().countAll().longValue();
	}

	/**
	 * @deprecated do not use this due to expensive memory use! use explicit
	 *             hsqldb queries instead.
	 */
	public List<IssueAttachment> getAllIssueAttachments() {
		logger.warn("getAllIssueAttachments: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getAllIssueAttachments: stacktrace was",
					new RuntimeException());
		}

		List<IssueAttachment> attachments = getIssueAttachmentDAO().findAll();

		return attachments;
	}

	/**
	 * Count total issues size and count from database
	 * 
	 * @deprecated use seperate issues size and count methods instead
	 */
	public long[] getAllIssueAttachmentsSizeAndCount() {
		logger
				.warn("getAllIssueAttachmentsSizeAndCount: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getAllIssueAttachmentsSizeAndCount: stacktrace was",
					new RuntimeException());
		}

		long[] sizeAndCount = new long[2];

		sizeAndCount[0] = getAllIssueAttachmentSize();
		sizeAndCount[1] = getAllIssueAttachmentCount();

		return sizeAndCount;

	}

	public IssueAttachment getIssueAttachment(Integer attachmentId) {
		IssueAttachment attachment = getIssueAttachmentDAO().findByPrimaryKey(
				attachmentId);

		return attachment;

	}

	public byte[] getIssueAttachmentData(Integer attachmentId) {

		byte[] data;

		IssueAttachment attachment = getIssueAttachmentDAO().findByPrimaryKey(
				attachmentId);

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

		Collection<IssueHistory> history = getIssueHistoryDAO().findByIssueId(
				issueId);

		Iterator<IssueHistory> iterator = history.iterator();

		while (iterator.hasNext()) {

			IssueHistory nextEntry = (IssueHistory) iterator.next();

			if (nextEntry != null) {

				if (lastEntry == null
						&& nextEntry.getLastModifiedDate() != null) {

					lastEntry = nextEntry;

				} else if (nextEntry.getLastModifiedDate() != null

						&& nextEntry.getLastModifiedDate().equals(
								lastEntry.getLastModifiedDate())

						&& nextEntry.getId().compareTo(lastEntry.getId()) > 0) {

					lastEntry = nextEntry;

				} else if (nextEntry.getLastModifiedDate() != null

						&& nextEntry.getLastModifiedDate().after(
								lastEntry.getLastModifiedDate())) {

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
	 * project, and anyone else that has a notfication on file. TODO move to
	 * {@link NotificationService}
	 * 
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
	public List<Notification> getIssueNotifications(Integer issueId,
			boolean primaryOnly, boolean activeOnly) {

		logger.warn("getIssueNotifications: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getIssueNotifications: stacktrace was",
					new RuntimeException());
		}

		return notificationService.getIssueNotifications(getIssue(issueId),
				primaryOnly, activeOnly);

	}

	/**
	 * TODO Move to {@link NotificationService}
	 * 
	 * @deprecated moved to {@link NotificationService}
	 */
	public boolean removeIssueNotification(Integer notificationId) {
		logger.warn("removeIssueNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("removeIssueNotification: stacktrace was",
					new RuntimeException());
		}

		return notificationService.removeIssueNotification(notificationId);
	}

	/**
	 * 
	 * @deprecated moved to {@link NotificationService}
	 */
	public boolean addIssueNotification(Notification notification) {		
		logger.warn("addIssueNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("addIssueNotification: stacktrace was",
					new RuntimeException());
		}
		return notificationService.addIssueNotification(notification);
//		User user = thisnotification.getUser();
//
//		Issue issue = thisnotification.getIssue();
//		if (thisnotification.getCreateDate() == null) {
//			thisnotification.setCreateDate(new Date());
//		}
//		if (thisnotification.getLastModifiedDate() == null) {
//			thisnotification.setLastModifiedDate(new Date());
//		}
//		List<Notification> notifications = new ArrayList<Notification>();
//		notifications.add(thisnotification);
//		issue.setNotifications(notifications);
//		// TODO: check these 3 lines - do we need them?:
//		Notification notification = new Notification();
//		notification.setIssue(issue);
//		notification.setUser(user);
//
//		getIssueDAO().saveOrUpdate(issue);
//		return true;
	}

	/**
	 * TODO move to notification service
	 * 
	 * @deprecated
	 */
	public boolean hasIssueNotification(Integer issueId, Integer userId) {
		logger.warn("hasIssueNotification: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("hasIssueNotification: stacktrace was",
					new RuntimeException());
		}
		Issue issue = getIssue(issueId);

		return notificationService.hasIssueNotification(issue, userId);

	}

	public int getOpenIssueCountByProjectId(Integer projectId) {

		Collection<Issue> issues = getIssueDAO().findByProjectAndLowerStatus(
				projectId, IssueUtilities.STATUS_RESOLVED);

		return issues.size();

	}

	public int getResolvedIssueCountByProjectId(Integer projectId) {

		Collection<Issue> issues = getIssueDAO().findByProjectAndHigherStatus(
				projectId, IssueUtilities.STATUS_RESOLVED);

		return issues.size();

	}

	public int getTotalIssueCountByProjectId(Integer projectId) {

		Collection<Issue> issues = getIssueDAO().findByProject(projectId);

		return issues.size();

	}

	public Date getLatestIssueDateByProjectId(Integer projectId) {

		return getIssueDAO().latestModificationDate(projectId);

	}



	public boolean canViewIssue(Integer issueId, User user) {

		Issue issue = getIssue(issueId);

		Map<Integer, Set<PermissionType>> permissions = getUserDAO()
				.getUsersMapOfProjectsAndPermissionTypes(user,
						AuthenticationConstants.REQ_SOURCE_WEB);

		return IssueUtilities.canViewIssue(issue, user.getId(), permissions);

	}

	public boolean canViewIssue(Issue issue, User user) {

		Map<Integer, Set<PermissionType>> permissions = getUserDAO()
				.getUsersMapOfProjectsAndPermissionTypes(user,
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

	public List<Issue> searchIssues(IssueSearchQuery queryModel, User user,
			Map<Integer, Set<PermissionType>> userPermissions)
			throws IssueSearchException {
		return getIssueDAO().query(queryModel, user, userPermissions);
	}



	/**
	 * @deprecated no more factory is used
	 */
	public static String getNotificationFactoryName() {
		logger.warn("getNotificationFactoryName: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getNotificationFactoryName: stacktrace was",
					new RuntimeException());
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
			logger.debug("setNotificationFactoryName: stacktrace was",
					new RuntimeException());
		}
	}

	/**
	 * @deprecated no more queue for notifications
	 * @return
	 */
	public static String getNotificationQueueName() {
		logger.warn("getNotificationQueueName: use of deprecated API");
		if (logger.isDebugEnabled()) {
			logger.debug("getNotificationQueueName: stacktrace was",
					new RuntimeException());
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
			logger.debug("setNotificationQueueName: stacktrace was",
					new RuntimeException());
		}
	}

	public Long totalSystemIssuesAttachmentSize() {
		return getIssueAttachmentDAO().totalAttachmentsSize();
	}


}
