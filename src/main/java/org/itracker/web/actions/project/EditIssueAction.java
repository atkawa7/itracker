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
import java.util.Date;
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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.Constants;

public class EditIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditIssueAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		log.info("execute: called");
		ActionMessages errors = new ActionMessages();
		Date logDate = new Date();
		Date startDate = new Date();
		logTimeMillies("execute: called", logDate, log, Level.DEBUG);
		// TODO: can we make this token optional (configurable) and probably by form, not over the whole app..
		if (!isTokenValid(request)) {
			log.debug("execute: Invalid request token while editing issue.");

			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			"itracker.web.error.transaction"));
			saveErrors(request, errors);
			log.info("execute: return to edit-issue");
			saveToken(request);
			//			return mapping.findForward("error");
			return mapping.getInputForward();
		}
		resetToken(request);


		try {
			IssueService issueService = getITrackerServices().getIssueService();
			if (((IssueForm)form).getId() != null) {
				Issue issue = getITrackerServices().getIssueService().getIssue((((IssueForm)form).getId()));			
				List<CustomField> projectFields = issue.getProject().getCustomFields();
				if (projectFields.size() > 0) {
					HttpSession session = request.getSession();
					Locale locale = ITrackerResources.getLocale();
					if (session != null) {
						locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
					}

					ResourceBundle bundle = ITrackerResources.getBundle(locale);

					for (int i = 0; i < projectFields.size(); i++) {
						CustomField customField = projectFields.get(i);
						String fieldValue = ((IssueForm)form).getCustomFields().get(String.valueOf(customField.getId()));
//						String fieldValue = request.getParameter("customFields("
//								+ customField.getId() + ")");
						if (fieldValue != null && !fieldValue.equals("")) {
							try {
								customField.checkAssignable(fieldValue, locale, bundle);
							} catch (IssueException ie) {
								String label = CustomFieldUtilities.getCustomFieldName(
										projectFields.get(i).getId(), locale);
								errors.add(ActionMessages.GLOBAL_MESSAGE,
										new ActionMessage(ie.getType(), label));
							}
						} else if (projectFields.get(i).isRequired()) {
							String label = CustomFieldUtilities.getCustomFieldName(
									projectFields.get(i).getId(), locale);
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionMessage(IssueException.TYPE_CF_REQ_FIELD,
											label));
						}
					}
				}
				if (!errors.isEmpty()) {
					saveErrors(request, errors);
					log.info("execute: return to edit-issue");
					saveToken(request);	
					return mapping.getInputForward();
				}
			}
			
			logTimeMillies("execute: got issueService", logDate, log, Level.DEBUG);
			NotificationService notificationService = getITrackerServices()
			.getNotificationService();
			HttpSession session = request.getSession(true);
			User currUser = (User) session.getAttribute(Constants.USER_KEY);

			Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);

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

			if (errors.isEmpty()) {
				previousStatus = issue.getStatus();
				try {
					if (UserUtilities.hasPermission(userPermissions, project.getId(),
							UserUtilities.PERMISSION_EDIT_FULL)) {
						if (log.isDebugEnabled()) {
							log.debug("execute: process full, " + issue);
						}
						issue = EditIssueActionUtil.processFullEdit(issue, project, currUser, userPermissions,
								getLocale(request), issueForm, issueService, errors);
						logTimeMillies("execute: processed fulledit", logDate, log, Level.DEBUG);
					} else {				
						if (log.isDebugEnabled()) {
							log.debug("execute: process limited, " + issue);
						}
						issue = EditIssueActionUtil.processLimitedEdit(issue, project, currUser, userPermissions,
								getLocale(request), issueForm, issueService, errors);
						logTimeMillies("execute: processed limited edit", logDate, log, Level.DEBUG);
					}
				} catch (Exception e) {
					log.warn("execute: failed to update " + issue, e);
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.other"));
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
				}
			}

			if (errors.isEmpty()) {		
				if (log.isDebugEnabled()) {
					log.debug("execute: sending notification for issue: " + issue
							+ " (HISTORIES: " + issueService.getIssueHistory(issue.getId()) + ")");
				}
				EditIssueActionUtil.sendNotification(issue.getId(), previousStatus,
						getBaseURL(request), notificationService);
				logTimeMillies("execute: sent notification", logDate, log, Level.DEBUG);

				WorkflowUtilities.processFieldScripts(scripts,
						WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, null, errors,
						(ValidatorForm) form);
				logTimeMillies("execute: processed field scripts EVENT_FIELD_ONPOSTSUBMIT", logDate, log, Level.DEBUG);

				return EditIssueActionUtil.getReturnForward(issue, project, issueForm, mapping);
			}
		} catch (Exception e) {
			log.error("execute: Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			"itracker.web.error.system"));
		} finally {
			logTimeMillies("execute: processed", startDate, log, Level.DEBUG);
		}

		if (!errors.isEmpty()) {
			saveMessages(request, errors);
			saveErrors(request, errors);
			saveToken(request);

			return mapping.getInputForward();
			//			return null;//mapping.findForward("editissueform");
		}

		log.info("execute: Forward: Error");
		return mapping.findForward("error");
	}

}
