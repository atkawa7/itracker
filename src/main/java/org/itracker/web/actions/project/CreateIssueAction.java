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
import java.util.Date;
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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Notification.Type;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.ProjectService;
import org.itracker.services.exceptions.ProjectException;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.ptos.CreateIssuePTO;
import org.itracker.web.util.AttachmentUtilities;
import org.itracker.web.util.Constants;

/**
 * This action handles the actual creation of a new issue.
 * 
 * FIXME: There are validation issues, the createissue-form becomes invalid, missing inputs, missing options. 
 * @author ranks
 *
 */
public class CreateIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(CreateIssueAction.class);


	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionMessages errors = new ActionMessages();
		//  TODO: Action Cleanup

		if (!isTokenValid(request)) {
			log.info("execute: Invalid request token while creating issue.");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			"itracker.web.error.transaction"));
			saveErrors(request, errors);
			log.info("execute: return to edit-issue");
			saveToken(request);
		//	return mapping.findForward("error");
			
			CreateIssuePTO.setupCreateIssue(request);
			return mapping.findForward("createissue");
//			project PTOs must be set in request for listprojects-forward to work
			// return mapping.findForward("listprojects");
//			return null; 
		} 
		resetToken(request);

		try {
			IssueService issueService = getITrackerServices().getIssueService();
			NotificationService notificationService = getITrackerServices()
					.getNotificationService();
			ProjectService projectService = getITrackerServices()
					.getProjectService();

			HttpSession session = request.getSession(true);
			User currUser = (User) session.getAttribute(Constants.USER_KEY);
			Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
			Locale locale = getLocale(request);
			Integer currUserId = currUser.getId();

			IssueForm issueForm = (IssueForm) form;

			Integer creator = currUserId;

			Project project = null;
			Integer projectId = issueForm.getProjectId();

			if (projectId == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidproject"));
			} else {
				project = projectService.getProject(projectId);
			}

			if (errors.isEmpty() && project == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidproject"));
			} else if (errors.isEmpty() && project.getStatus() != Status.ACTIVE) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.projectlocked"));
			} else if (!UserUtilities.hasPermission(userPermissionsMap,
					projectId, UserUtilities.PERMISSION_CREATE)) {
				return mapping.findForward("unauthorized");
			} else {
				List<ProjectScript> scripts = project.getScripts();
				WorkflowUtilities.processFieldScripts(scripts,
						WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, null,
						errors, issueForm);

				Issue issue = new Issue();
				issue.setDescription((String) issueForm.getDescription());
				issue.setSeverity(issueForm.getSeverity());
				issue.setStatus(IssueUtilities.STATUS_NEW);
				issue.setResolution("");

				IssueHistory issueHistory = new IssueHistory(issue, currUser,
						issueForm.getHistory(),
						IssueUtilities.HISTORY_STATUS_AVAILABLE);
				issueHistory.setCreateDate(new Date());
				issue.getHistory().add(issueHistory);

				// creating issues as another user

				if (UserUtilities.hasPermission(userPermissionsMap,
						projectId, UserUtilities.PERMISSION_CREATE_OTHERS)) {
					creator = null != issueForm.getCreatorId()? issueForm.getCreatorId(): currUserId;
					if (log.isDebugEnabled()) {
						log.debug("New issue creator set to " + creator
							+ ". Issue created by " + currUserId); 
					}
				}
				// create the issue in the database
				ActionMessages msg = AttachmentUtilities.validate(issueForm.getAttachment(), getITrackerServices());
				
				if (!msg.isEmpty()) {
					log.info("execute: tried to create issue with invalid attachemnt: " + msg);
					errors.add(msg);
					saveErrors(request, errors);
					CreateIssuePTO.setupCreateIssue(request);
					return mapping.findForward("createissue");
				}
				
				if (log.isDebugEnabled()) {
					log.debug("execute: creating new issue..");
				}
				
				issue = issueService.createIssue(issue, projectId,
						 creator, currUserId);

				if (log.isDebugEnabled()) {
					log.debug("execute: issue created: " + issue);
				}
				
				if (issue != null) {
					if (!ProjectUtilities.hasOption(
							ProjectUtilities.OPTION_NO_ATTACHMENTS, project
									.getOptions())) {
//						FormFile file = issueForm.getAttachment();
						msg = new ActionMessages();
						issue = AttachmentUtilities.addAttachment(issue, project, currUser, issueForm, getITrackerServices(), msg);
						
						if (!msg.isEmpty()) {
							// Validation of attachment failed
							errors.add(msg);
						}
						
						
						
	//					if (file != null && !"".equals(file.getFileName())) {
	//						String origFileName = file.getFileName();
	//
	//						ActionMessages msg = AttachmentUtilities.validate(file, this
	//								.getITrackerServices());
	//						if (msg.isEmpty()) {
	//							int lastSlash = Math.max(origFileName
	//									.lastIndexOf('/'), origFileName
	//									.lastIndexOf('\\'));
	//
	//							if (lastSlash > -1) {
	//								origFileName = origFileName
	//										.substring(lastSlash + 1);
	//							}
	//
	//							IssueAttachment attachmentModel = new IssueAttachment(
	//									issue, origFileName, file.getContentType(),
	//									issueForm.getAttachmentDescription(), file
	//											.getFileSize(), currUser);
	//							
	//							
	//							AttachmentUtilities.addAttachment(issue, project, currUser, issueForm, getITrackerServices(), errors);
	////							issueService.addIssueAttachment(attachmentModel,
	////									file.getFileData());
	//						}
	//						else {
	//							errors.add(msg);
	//							saveErrors(request, errors);
	//							return mapping.getInputForward();
	//						}
	//					}
					}

				
					Integer newOwner = issueForm.getOwnerId();

					if (newOwner != null && newOwner.intValue() >= 0) {
						if (UserUtilities.hasPermission(userPermissionsMap,
								UserUtilities.PERMISSION_ASSIGN_OTHERS)
								|| (UserUtilities.hasPermission(
										userPermissionsMap,
										UserUtilities.PERMISSION_ASSIGN_SELF) && currUserId
										.equals(newOwner))) {
							issueService.assignIssue(issue.getId(), newOwner,
									currUserId);
						}
					}

					// FIXME this is absolutely complex, unreadable code. why do it, what does it do, can we keep it simple?
					// it seems to set issueCustomField (issueFields), you might be able to refactor this into its own method (hiding in a method ;) )
					List<IssueField> issueFields = new ArrayList<IssueField>();
					Map<String, String> customFields = issueForm.getCustomFields();

					if (customFields != null && customFields.size() > 0) {
						List<IssueField> issueFieldsVector = new ArrayList<IssueField>();
						ResourceBundle bundle = ITrackerResources
								.getBundle(locale);

						for (Iterator<String> iter = customFields.keySet().iterator(); iter
								.hasNext();) {
							try {
								Integer fieldId = Integer.valueOf(iter.next());
								CustomField field = IssueUtilities
										.getCustomField(fieldId);
								String fieldValue = (String) PropertyUtils
										.getMappedProperty(form,
												"customFields(" + fieldId + ")");

								if (fieldValue != null
										&& fieldValue.trim().length() != 0) {
									IssueField issueField = new IssueField(
											issue, field);
									issueField.setValue(fieldValue, bundle);
									issueFieldsVector.add(issueField);
								}
							} catch (Exception e) {
								log.error("execute: failed to assign issue", e);
							}
						}
						issueFields = new ArrayList<IssueField>();
						issueFields = issueFieldsVector;
					}
					issueService.setIssueFields(issue.getId(), issueFields);

					HashSet<Integer> components = new HashSet<Integer>();
					Integer[] componentIds = issueForm.getComponents();


					 

					if (componentIds != null) {
						for (int i = 0; i < componentIds.length; i++) {
							components.add(componentIds[i]);
						}
						issueService.setIssueComponents(issue.getId(),
								components, creator);
					}
					HashSet<Integer> versions = new HashSet<Integer>();
					Integer[] versionIds = issueForm.getVersions();

					if (versionIds != null) {
						for (int i = 0; i < versionIds.length; i++) {
							versions.add(versionIds[i]);
						}
						issueService.setIssueVersions(issue.getId(), versions,
								creator);
					}

					try {
						Integer relatedIssueId = issueForm.getRelatedIssueId();
						Integer relationType = issueForm.getRelationType();

						if (relatedIssueId != null
								&& relatedIssueId.intValue() > 0
								&& relationType != null
								&& relationType.intValue() > 0) {
							Issue relatedIssue = issueService
									.getIssue(relatedIssueId);

							if (relatedIssue == null) {
								log
										.debug("Unknown relation issue, relation not created.");
							} else if (relatedIssue.getProject() == null
									|| !IssueUtilities.canEditIssue(
											relatedIssue, currUserId,
											userPermissionsMap)) {
								log
										.info("User not authorized to add issue relation from issue "
												+ issue.getId()
												+ " to issue "
												+ relatedIssueId);
							} else if (IssueUtilities.hasIssueRelation(issue,
									relatedIssueId)) {
								log.debug("Issue " + issue.getId()
										+ " is already related to issue "
										+ relatedIssueId
										+ ", relation ot created.");
							} else {
								if (!issueService.addIssueRelation(issue
										.getId(), relatedIssueId, relationType
										.intValue(), currUser.getId())) {
									log
											.info("Error adding issue relation from issue "
													+ issue.getId()
													+ " to issue "
													+ relatedIssueId);
								}
							}
						}
					} catch (RuntimeException e) {
						log
								.warn(
										"execute: Exception adding new issue relation.",
										e);
						errors.add(ActionMessages.GLOBAL_MESSAGE,
								new ActionMessage("itracker.web.error.system"));
						saveErrors(request, errors);
						CreateIssuePTO.setupCreateIssue(request);
						return mapping.findForward("createissue");
					}

					notificationService.sendNotification(issue, Type.CREATED,
							getBaseURL(request));
				} else {

					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("itracker.web.error.system"));
					saveErrors(request, errors);
					return mapping.findForward("createissue");
				}
				session.removeAttribute(Constants.PROJECT_KEY);

				WorkflowUtilities.processFieldScripts(scripts,
						WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, null,
						errors, issueForm);
				if (errors.isEmpty()) {
					return getReturnForward(issue, project, issueForm, mapping);
				}
				saveErrors(request, errors);
				CreateIssuePTO.setupCreateIssue(request);
				return mapping.findForward("createissue");
				
			}
		} catch (RuntimeException e) {
			log.error("Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		} catch (WorkflowException e) {
			log.error("Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		} catch (ProjectException e) {
			log.error("Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}
		return mapping.findForward("createissue");
	}
	
	private ActionForward getReturnForward(Issue issue, Project project,
			IssueForm issueForm, ActionMapping mapping) {
		log.info("getReturnForward: listissues");
		
		return new ActionForward(mapping.findForward("listissues")
				.getPath()
				+ "?projectId=" + project.getId());
	}

}
