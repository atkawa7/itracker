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

package org.itracker.web.actions.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.model.Notification.Type;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.AttachmentUtilities;
import org.itracker.web.util.Constants;

public class EditIssueAction extends ItrackerBaseAction {

	private static final Logger log = Logger.getLogger(EditIssueAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("execute: called");
		ActionErrors errors = new ActionErrors();
		Date logDate = new Date();
		Date startDate = new Date();
		logTimeMillies("execute: called", logDate, log, Level.DEBUG);
		// TODO: can we make this token optional (configurable) and probably by form, not over the whole app..
		if (!isTokenValid(request)) {
			log.debug("execute: Invalid request token while editing issue.");
			ProjectService projectService = getITrackerServices()
					.getProjectService();
//			request.setAttribute("projects", projectService.getAllProjects());
//			request.setAttribute("ph", projectService);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.transaction"));
			saveMessages(request, errors);
			log.info("execute: return to edit-issue");
			saveToken(request);
			return null;
//			return mapping.getInputForward();
		}

		resetToken(request);

		try {
			IssueService issueService = getITrackerServices().getIssueService();
			logTimeMillies("execute: got issueService", logDate, log, Level.DEBUG);
			NotificationService notificationService = getITrackerServices()
					.getNotificationService();
			logTimeMillies("execute: got notificationService", logDate, log, Level.DEBUG);
			HttpSession session = request.getSession(true);
			User currUser = (User) session.getAttribute(Constants.USER_KEY);
			logTimeMillies("execute: got currUser", logDate, log, Level.DEBUG);
			
			Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
			logTimeMillies("execute: got userPermissions", logDate, log, Level.DEBUG);

			Integer currUserId = currUser.getId();

			IssueForm issueForm = (IssueForm) form;


			int previousStatus = -1;
			Issue issue = issueService.getIssue(issueForm.getId());

			logTimeMillies("execute: got issue", logDate, log, Level.DEBUG);
			
			if (issue == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidissue"));
				log.info("execute: invalidissue " + issueForm.getId() + ", Forward: Error");
				return mapping.findForward("error");
			}

			Project project = issue.getProject();
			logTimeMillies("execute: got project", logDate, log, Level.DEBUG);
			if (project == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidproject"));
				log.info("execute: Forward: Error");
				return mapping.findForward("error");
			} else if (project.getStatus() != Status.ACTIVE) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.projectlocked"));
				log.info("execute: Forward: Error");
				return mapping.findForward("error");
			} else if (!IssueUtilities.canEditIssue(issue, currUserId,
					userPermissions)) {
				log.info("execute: Forward: unauthorized");
				return mapping.findForward("unauthorized");
			}

			List<ProjectScript> scripts = project.getScripts();
			logTimeMillies("execute: got scripts", logDate, log, Level.DEBUG);
			WorkflowUtilities.processFieldScripts(scripts,
					WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, null, errors,
					(ValidatorForm) form);
			logTimeMillies("execute: processed field scripts EVENT_FIELD_ONPRESUBMIT", logDate, log, Level.DEBUG);

			previousStatus = issue.getStatus();
			if (UserUtilities.hasPermission(userPermissions, project.getId(),
					UserUtilities.PERMISSION_EDIT_FULL)) {
				if (log.isDebugEnabled()) {
					log.debug("execute: process full, " + issue);
				}
				issue = processFullEdit(issue, project, currUser, userPermissions,
						getLocale(request), issueForm, issueService);
				logTimeMillies("execute: processed fulledit", logDate, log, Level.DEBUG);
			} else {				
				if (log.isDebugEnabled()) {
					log.debug("execute: process limited, " + issue);
				}
				issue = processLimitedEdit(issue, project, currUser, userPermissions,
						getLocale(request), issueForm, issueService);
				logTimeMillies("execute: processed limited edit", logDate, log, Level.DEBUG);
			}

			if (log.isDebugEnabled()) {
				log.debug("execute: sending notification for issue: " + issue
						+ " (HOSTORIES: " + issueService.getIssueHistory(issue.getId()) + ")");
			}

			sendNotification(issue.getId(), previousStatus,
					getBaseURL(request), notificationService);
			logTimeMillies("execute: sent notification", logDate, log, Level.DEBUG);
//			session.removeAttribute(Constants.ISSUE_KEY);

			WorkflowUtilities.processFieldScripts(scripts,
					WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, null, errors,
					(ValidatorForm) form);
			logTimeMillies("execute: processed field scripts EVENT_FIELD_ONPOSTSUBMIT", logDate, log, Level.DEBUG);

//			ProjectService projectService = getITrackerServices()
//					.getProjectService();
//			request.setAttribute("projects", projectService.getAllProjects());
//			request.setAttribute("ph", projectService);

			return getReturnForward(issue, project, issueForm, mapping);

		} catch (Exception e) {
			//e.printStackTrace();
			log.error("execute: Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		} finally {
			logTimeMillies("execute: processed", startDate, log, Level.DEBUG);
		}

		if (!errors.isEmpty()) {
			saveMessages(request, errors);
			saveToken(request);
			return mapping.findForward("editissueform");
		}

		log.info("execute: Forward: Error");

		return mapping.findForward("error");

	}

	private Issue processFullEdit(Issue issue, Project project, User user,
			Map<Integer, Set<PermissionType>> userPermissions, Locale locale,
			IssueForm form, IssueService issueService) throws Exception {

		int previousStatus = issue.getStatus();
		
		boolean needReloadIssue = false;
		
		needReloadIssue = issueService.setIssueVersions(issue.getId(), 
				new HashSet<Integer>(Arrays.asList(form.getVersions())), 
				user.getId());
				
		needReloadIssue = needReloadIssue | issueService.setIssueComponents(issue.getId(), 
				new HashSet<Integer>(Arrays.asList(form.getComponents())), 
				user.getId());
		// reload issue for further updates
		if (needReloadIssue) {
			issue = issueService.getIssue(issue.getId());
		}
		
		Integer targetVersion = form.getTargetVersion();
		if (targetVersion != null && targetVersion != -1) {
			ProjectService projectService = getITrackerServices()
					.getProjectService();
			Version version = projectService.getProjectVersion(targetVersion);
			if (version == null) {
				throw new RuntimeException("No version with Id "
						+ targetVersion);
			}
			issue.setTargetVersion(version);

		}



		issue.setResolution(form.getResolution());
		issue.setSeverity(form.getSeverity());
		
		applyLimitedFields(issue, project, user, userPermissions, locale, form, issueService);
		
		// TODO not so nice code. what means -1?
		if (previousStatus != -1) {
			// Reopened the issue. Reset the resolution field.
			if ((issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED && issue
					.getStatus() < IssueUtilities.STATUS_RESOLVED)
					&& (previousStatus >= IssueUtilities.STATUS_RESOLVED && previousStatus < IssueUtilities.STATUS_END)) {
				issue.setResolution("");
			}

			if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
					&& !UserUtilities.hasPermission(userPermissions, project
							.getId(), UserUtilities.PERMISSION_CLOSE)) {
				if (previousStatus < IssueUtilities.STATUS_CLOSED) {
					issue.setStatus(previousStatus);
				} else {
					issue.setStatus(IssueUtilities.STATUS_RESOLVED);
				}
			}

			if (issue.getStatus() < IssueUtilities.STATUS_NEW
					|| issue.getStatus() >= IssueUtilities.STATUS_END) {
				issue.setStatus(previousStatus);
			}

		} else if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
				&& !UserUtilities.hasPermission(userPermissions, project
						.getId(), UserUtilities.PERMISSION_CLOSE)) {
			issue.setStatus(IssueUtilities.STATUS_RESOLVED);
		}

		if (issue.getStatus() < IssueUtilities.STATUS_NEW) {
			issue.setStatus(IssueUtilities.STATUS_NEW);
		} else if (issue.getStatus() >= IssueUtilities.STATUS_END) {
			if (!UserUtilities.hasPermission(userPermissions, project.getId(),
					UserUtilities.PERMISSION_CLOSE)) {
				issue.setStatus(IssueUtilities.STATUS_RESOLVED);
			} else {
				issue.setStatus(IssueUtilities.STATUS_CLOSED);
			}
		}
		
		return issueService.updateIssue(issue, user.getId());

	}



	private Issue processLimitedEdit(final Issue issue, Project project,
			User user, Map<Integer, Set<PermissionType>> userPermissionsMap,
			Locale locale, IssueForm form, IssueService issueService)
			throws Exception {

		applyLimitedFields(issue, project, user, userPermissionsMap, locale, form, issueService);
		return issueService.updateIssue(issue, user.getId());
		
	}
	
	private void applyLimitedFields(Issue issue, Project project,
			User user, Map<Integer, Set<PermissionType>> userPermissionsMap,
			Locale locale, IssueForm form, IssueService issueService) throws Exception {
		

		// save attachment and reload updated issue
		addAttachment(issue, project, user, form, issueService);
		issue = issueService.getIssue(issue.getId());
		
		issue.setDescription(form.getDescription());

		Integer formStatus = form.getStatus();

		if (formStatus != null) {

			if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
					&& formStatus >= IssueUtilities.STATUS_CLOSED
					&& UserUtilities.hasPermission(userPermissionsMap,
							UserUtilities.PERMISSION_CLOSE)) {

				issue.setStatus(formStatus);
			}

		}

		setIssueFields(issue, user, locale, form, issueService);
		setOwner(issue, user, userPermissionsMap, form, issueService);
		addHistoryEntry(issue, user, form, issueService);

	}

	private void setOwner(Issue issue, User user,
			Map<Integer, Set<PermissionType>> userPermissionsMap,
			IssueForm form, IssueService issueService) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("setOwner: called to " + form.getOwnerId());
		}
		Integer currentOwner = (issue.getOwner() == null) ? null : issue
				.getOwner().getId();

		Integer ownerId = form.getOwnerId();

		if (ownerId == null || ownerId.equals(currentOwner)) {
			if (log.isDebugEnabled()) {
				log.debug("setOwner: returning, existing owner is the same: " + issue.getOwner());
			}
			return;
		}

		if (UserUtilities.hasPermission(userPermissionsMap,
				UserUtilities.PERMISSION_ASSIGN_OTHERS)
				|| (UserUtilities.hasPermission(userPermissionsMap,
						UserUtilities.PERMISSION_ASSIGN_SELF) && user.getId()
						.equals(ownerId))
				|| (UserUtilities.hasPermission(userPermissionsMap,
						UserUtilities.PERMISSION_UNASSIGN_SELF)
						&& user.getId().equals(currentOwner) && ownerId
						.intValue() == -1)) {
			User newOwner = getITrackerServices().getUserService().getUser(ownerId);
			if (log.isDebugEnabled()) {
				log.debug("setOwner: setting new owner " + newOwner + " to " + issue);
			}
			issue.setOwner(newOwner);
//			issueService.assignIssue(issue.getId(), ownerId, user.getId());
		}

	}

	private void setIssueFields(Issue issue, User user, Locale locale,
			IssueForm form, IssueService issueService) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("setIssueFields: called");
		}
		List<CustomField> projectCustomFields = issue.getProject()
				.getCustomFields();
		if (log.isDebugEnabled()) {
			log.debug("setIssueFields: got project custom fields: " + projectCustomFields);
		}

		if (projectCustomFields == null || projectCustomFields.size() == 0) {
			log.debug("setIssueFields: no custom fields, returning...");
			return;
		}
		
		
		// here you see some of the ugly side of Struts 1.3 - the forms... they
		// can only contain Strings and some simple objects types...
		HashMap<String, String> formCustomFields = form.getCustomFields();
		
		if (log.isDebugEnabled()) {
			log.debug("setIssueFields: got form custom fields: " + formCustomFields);
		}
		
		if (formCustomFields == null || formCustomFields.size() == 0) {
			log.debug("setIssueFields: no form custom fields, returning..");
			return;
		}
		
		ResourceBundle bundle = ITrackerResources.getBundle(locale);
		List<IssueField> issueFieldsList = new ArrayList<IssueField>(projectCustomFields.size());
		Iterator<CustomField> customFieldsIt = projectCustomFields.iterator();
		// declare iteration fields
		CustomField field;
		String fieldValue;
		IssueField issueField;
		try {
			if (log.isDebugEnabled()) {
				log.debug("setIssueFields: processing project fields");
			}
			// set values to issue-fields and add if needed
			while (customFieldsIt.hasNext()) {

				field = customFieldsIt.next();
				fieldValue = (String) formCustomFields.get(String.valueOf(field
						.getId()));

				if (fieldValue != null && fieldValue.trim().length() > 0) {

					issueField = new IssueField(issue, field);
					issueField.setValue(fieldValue, bundle);
					
					issueFieldsList.add(issueField);

				}
			}
			
			// set new issue fields for later saving
			issue.setFields(issueFieldsList);
			
//			issueService.setIssueFields(issue.getId(), issueFieldsList);
		} catch (Exception e) {
			log.error("setIssueFields: failed to process custom fields", e);
			throw e;
		}
	}

	private void addHistoryEntry(Issue issue, User user, IssueForm form,
			IssueService issueService) throws Exception {


			try {
				String history = form.getHistory();
	
			if (history == null || history.equals("")) {
				if (log.isDebugEnabled()) {
					log.debug("addHistoryEntry: skip history to " + issue);
				}
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug("addHistoryEntry: adding history to " + issue);
			}
			IssueHistory issueHistory = new IssueHistory(issue, user, history,
				IssueUtilities.HISTORY_STATUS_AVAILABLE);

			issueHistory.setDescription(((IssueForm) form).getHistory());
			issueHistory.setCreateDate(new Date());
		
			issueHistory.setLastModifiedDate(new Date());
			issue.getHistory().add(issueHistory);

//  TODO why do we need to updateIssue here, and can not later?
//			issueService.updateIssue(issue, user.getId());
		} catch (Exception e) {
			log.error("addHistoryEntry: failed to add", e);
			throw e;
		}
//		issueService.addIssueHistory(issueHistory);
		if (log.isDebugEnabled()) {
			log.debug("addHistoryEntry: added history for issue " + issue);
		}
	}

	private void addAttachment(Issue issue, Project project, User user,
			IssueForm form, IssueService issueService) throws Exception {

		if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS,
				project.getOptions())) {
			return;
		}

		FormFile file = form.getAttachment();

		if (file == null || file.getFileName().trim().length() < 1) {
			log.info("addAttachment: skipping file " + file);
			return;
		}

		String origFileName = file.getFileName();
		String contentType = file.getContentType();
		int fileSize = file.getFileSize();

		String attachmentDescription = form.getAttachmentDescription();

		if (null == contentType || 0 >= contentType.length()) {
			log
					.info("addAttachment: got no mime-type, using default plain-text");
			contentType = "text/plain";
		}

		if (log.isDebugEnabled()) {
			log.debug("addAttachment: adding file, name: " + origFileName
					+ " of type " + file.getContentType() + ", description: "
					+ form.getAttachmentDescription() + ". filesize: " + fileSize);
		}

		if (AttachmentUtilities.checkFile(file, this.getITrackerServices())) {
			int lastSlash = Math.max(origFileName.lastIndexOf('/'),
					origFileName.lastIndexOf('\\'));
			if (lastSlash > -1) {
				origFileName = origFileName.substring(lastSlash + 1);
			}

			IssueAttachment attachmentModel = new IssueAttachment(issue,
					origFileName, contentType, attachmentDescription, fileSize,
					user);
			
			attachmentModel.setIssue(issue);
//			issue.getAttachments().add(attachmentModel);
			issueService
					.addIssueAttachment(attachmentModel, file.getFileData());

		}

	}

	private void sendNotification(Integer issueId, int previousStatus,
			String baseURL, NotificationService notificationService) {

		Type notificationType = Type.UPDATED;

		Issue issue = getITrackerServices().getIssueService().getIssue(issueId);

		if ((previousStatus < IssueUtilities.STATUS_CLOSED)
				&& issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
			notificationType = Type.CLOSED;
		}

		if (log.isDebugEnabled()) {
			log.debug("notificationService: before send");
		}
		notificationService.sendNotification(issue, notificationType, baseURL);

		if (log.isDebugEnabled()) {
			log.debug("notificationService: after send");
		}
	}

	private ActionForward getReturnForward(Issue issue, Project project,
			IssueForm form, ActionMapping mapping) throws Exception {

		if ("index".equals(form.getCaller())) {
			log.info("EditIssueAction: Forward: index");
			return mapping.findForward("index");
		} else if ("viewissue".equals(form.getCaller()) && issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
			log.info("EditIssueAction: Forward: viewissue");
			return new ActionForward(mapping.findForward("viewissue").getPath()
					+ "?id=" + issue.getId());
		} else {
			log.info("EditIssueAction: Forward: listissues");
			
			
			return new ActionForward(mapping.findForward("listissues")
					.getPath()
					+ "?projectId=" + project.getId());
		}

	}

}
