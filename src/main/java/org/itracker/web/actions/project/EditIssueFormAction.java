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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.IssueRelation;
import org.itracker.model.NameValuePair;
import org.itracker.model.Notification;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.model.Notification.Role;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.services.util.Convert;
import org.itracker.services.util.HTMLUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.NotificationUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

/**
 * This class populates an IssueForm object for display by the edit issue page.
 */
public class EditIssueFormAction extends ItrackerBaseAction {

	private static final Logger log = Logger
			.getLogger(EditIssueFormAction.class);

	/**
	 * method needed to prepare request for edit_issue.jsp
	 * 
	 * @throws WorkflowException
	 */
	public static final void setupJspEnv(ActionMapping mapping,
			IssueForm issueForm, HttpServletRequest request, Issue issue,
			IssueService issueService, UserService userService,
			Map<Integer, Set<PermissionType>> userPermissions,
			Map<Integer, List<NameValuePair>> listOptions, ActionMessages errors)
			throws ServletException, IOException, WorkflowException {
		
		if (log.isDebugEnabled()) {
			log.debug("setupJspEnv: for issue " + issue);
		}
		
		NotificationService notificationService = ServletContextUtils
		.getItrackerServices().getNotificationService();
		String pageTitleKey = "itracker.web.editissue.title";
		String pageTitleArg = request.getParameter("id");
		Locale locale = LoginUtilities.getCurrentLocale(request);
		User um = LoginUtilities.getCurrentUser(request);
		List<NameValuePair> statuses = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_STATUS);
		String statusName = IssueUtilities.getStatusName(issue.getStatus(),locale);
		boolean hasFullEdit = UserUtilities.hasPermission(userPermissions,
				issue.getProject().getId(), UserUtilities.PERMISSION_EDIT_FULL);
		List<NameValuePair> resolutions = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_RESOLUTION);
		String severityName = IssueUtilities.getSeverityName(issue
				.getSeverity(),locale);
		List<NameValuePair> components = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_COMPONENTS);
		List<NameValuePair> versions = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_VERSIONS);
		List<NameValuePair> targetVersion = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_TARGET_VERSION);
		List<Component> issueComponents = issue.getComponents();
		Collections.sort(issueComponents);
		List<Version> issueVersions = issue.getVersions();
		Collections.sort(issueVersions, new Version.VersionComparator());
		/* Get project fields and put name and value in map */
		setupProjectFieldsMapJspEnv(issue.getProject().getCustomFields(), issue.getFields(), request);

		setupRelationsRequestEnv(issue.getRelations(), request);
		
		String wrap = "soft";
        if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, issue.getProject().getOptions()) ||
                ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, issue.getProject().getOptions())) {
            wrap = "hard";
        }

		request.setAttribute("pageTitleKey", pageTitleKey);
		request.setAttribute("pageTitleArg", pageTitleArg);
		request.setAttribute("wrap", wrap);
		request.getSession().setAttribute(Constants.LIST_OPTIONS_KEY,
				listOptions);
		request.setAttribute("targetVersions", targetVersion);
		request.setAttribute("components", components);
		request.setAttribute("versions", versions);
		request.setAttribute("hasIssueNotification", !notificationService
				.hasIssueNotification(issue, um.getId()));
		request.setAttribute("hasEditIssuePermission", UserUtilities
				.hasPermission(userPermissions, issue.getProject().getId(),
						UserUtilities.PERMISSION_EDIT));
		request.setAttribute("canCreateIssue",
				issue.getProject().getStatus() == Status.ACTIVE
						&& UserUtilities.hasPermission(userPermissions, issue
								.getProject().getId(),
								UserUtilities.PERMISSION_CREATE));
		request.setAttribute("issueComponents", issueComponents);
		request.setAttribute("issueVersions",
				issueVersions == null ? new ArrayList<Version>()
						: issueVersions);
		request.setAttribute("statuses", statuses);
		request.setAttribute("statusName", statusName);
		request.setAttribute("hasFullEdit", hasFullEdit);
		request.setAttribute("resolutions", resolutions);
		request.setAttribute("severityName", severityName);
		request.setAttribute("hasPredefinedResolutionsOption", ProjectUtilities
				.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
						issue.getProject().getOptions()));
		request.setAttribute("issueOwnerName",
				(issue.getOwner() == null ? ITrackerResources.getString(
						"itracker.web.generic.unassigned",locale)
						: issue.getOwner().getFirstName() + " "
								+ issue.getOwner().getLastName()));
		request.setAttribute("isStatusResolved",
				issue.getStatus() >= IssueUtilities.STATUS_RESOLVED);
		

		request.setAttribute("fieldSeverity", WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_SEVERITY));
		request.setAttribute("possibleOwners", WorkflowUtilities
				.getListOptions(listOptions, IssueUtilities.FIELD_OWNER));

		request.setAttribute("hasNoViewAttachmentOption", ProjectUtilities
				.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, issue
						.getProject().getOptions()));
		
		if (log.isDebugEnabled()) {
			log.debug("setupJspEnv: options " + issue.getProject().getOptions() + ", hasNoViewAttachmentOption: " + request.getAttribute("hasNoViewAttachmentOption"));
		}
		
		setupNotificationsInRequest(request, issue, notificationService);

		// setup issue to request, as it's needed by the jsp.
		request.setAttribute(Constants.ISSUE_KEY, issue);
		request.setAttribute("issueForm", issueForm);
		request.setAttribute(Constants.PROJECT_KEY, issue.getProject());
		List<IssueHistory> issueHistory = issueService.getIssueHistory(issue
				.getId());
		Collections.sort(issueHistory, IssueHistory.CREATE_DATE_COMPARATOR);
		request.setAttribute("issueHistory", issueHistory);


	}
	
	/**
	 *  Get project fields and put name and value in map 
	 */
	protected static final void setupProjectFieldsMapJspEnv(List<CustomField> projectFields, Collection<IssueField> issueFields, HttpServletRequest request) {
		Map<CustomField,String> projectFieldsMap = new HashMap<CustomField, String>();

		if (projectFields != null && projectFields.size() > 0) {
			Collections.sort(projectFields, CustomField.ID_COMPARATOR);
			
			HashMap<String, String> fieldValues = new HashMap<String, String>();
			Iterator<IssueField> issueFieldsIt = issueFields.iterator();
			while (issueFieldsIt.hasNext()) {
				IssueField issueField = (IssueField) issueFieldsIt.next();
			
				if (issueField.getCustomField() != null
						&& issueField.getCustomField().getId() > 0) {
					Locale currLocale = LoginUtilities.getCurrentLocale(request);
					fieldValues.put(issueField.getCustomField().getId()
							.toString(), issueField
							.getValue(currLocale));
				}
			}
			for (int i = 0; i < projectFields.size(); i++) {
				String fieldValue = (fieldValues.get(String
						.valueOf(projectFields.get(i).getId())) == null ? ""
						: fieldValues.get(String.valueOf(projectFields.get(i)
								.getId())));
				if (fieldValue != null) { 
	        	 fieldValue = (projectFields.get(i).getFieldType() == CustomField.Type.LIST ? projectFields.get(i).getOptionNameByValue(fieldValue) : fieldValue);
	        	 } 
				projectFieldsMap.put(projectFields.get(i), fieldValue);
				
			}
			
			request.setAttribute("projectFieldsMap", projectFieldsMap);
		}
	}

	
	protected static final void setupRelationsRequestEnv(List<IssueRelation> relations, HttpServletRequest request) {
        Collections.sort(relations, IssueRelation.LAST_MODIFIED_DATE_COMPARATOR);
        request.setAttribute("issueRelations", relations);

	}
	
	
	public static final void setupNotificationsInRequest(
			HttpServletRequest request, Issue issue,
			NotificationService notificationService) {
		List<Notification> notifications = notificationService
				.getIssueNotifications(issue);

		Collections.sort(notifications, Notification.TYPE_COMPARATOR);

		request.setAttribute("notifications", notifications);
		Map<User, Set<Role>> notificationMap = NotificationUtilities
				.mappedRoles(notifications);
		request.setAttribute("notificationMap", notificationMap);
		request.setAttribute("notifiedUsers", notificationMap.keySet());
	}

	public static Map<Integer, List<NameValuePair>> getListOptions(
			HttpServletRequest request, Issue issue,
			List<NameValuePair> ownersList,
			Map<Integer, Set<PermissionType>> userPermissions, Project project,
			User currUser) {
		Map<Integer, List<NameValuePair>> listOptions = new HashMap<Integer, List<NameValuePair>>();

		Locale locale = (Locale) request.getSession().getAttribute(
				Constants.LOCALE_KEY);

		if (ownersList != null && ownersList.size() > 0) {
			listOptions.put(IssueUtilities.FIELD_OWNER, ownersList);
		}

		boolean hasFullEdit = UserUtilities.hasPermission(userPermissions,
				project.getId(), UserUtilities.PERMISSION_EDIT_FULL);

		List<NameValuePair> allStatuses = IssueUtilities.getStatuses(locale);
		List<NameValuePair> statusList = new ArrayList<NameValuePair>();

		if (!hasFullEdit) {

			if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
					&& UserUtilities.hasPermission(userPermissions, project
							.getId(), UserUtilities.PERMISSION_CLOSE)) {
				for (int i = 0; i < allStatuses.size(); i++) {
					int statusNumber = Integer.parseInt(allStatuses.get(i)
							.getValue());
					if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
							&& statusNumber >= IssueUtilities.STATUS_CLOSED) {
						statusList.add(allStatuses.get(i));
					} else if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
							&& statusNumber >= IssueUtilities.STATUS_RESOLVED) {
						statusList.add(allStatuses.get(i));
					}
				}
			} else {
				// Can't change
			}

		} else {

			if (currUser.isSuperUser()) {
				for (int i = 0; i < allStatuses.size(); i++) {
					statusList.add(allStatuses.get(i));
				}
			} else if (issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED
					&& issue.getStatus() < IssueUtilities.STATUS_RESOLVED) {
				for (int i = 0; i < allStatuses.size(); i++) {
					int statusNumber = Integer.parseInt(allStatuses.get(i)
							.getValue());
					if (statusNumber >= IssueUtilities.STATUS_ASSIGNED
							&& statusNumber < IssueUtilities.STATUS_CLOSED) {
						statusList.add(allStatuses.get(i));
					} else if (statusNumber >= IssueUtilities.STATUS_CLOSED
							&& ProjectUtilities
									.hasOption(
											ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
											project.getOptions())
							&& UserUtilities.hasPermission(userPermissions,
									project.getId(),
									UserUtilities.PERMISSION_CLOSE)) {
						statusList.add(allStatuses.get(i));
					}
				}
			} else if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
					&& issue.getStatus() < IssueUtilities.STATUS_CLOSED) {
				for (int i = 0; i < allStatuses.size(); i++) {
					int statusNumber = Integer.parseInt(allStatuses.get(i)
							.getValue());
					if (statusNumber >= IssueUtilities.STATUS_ASSIGNED
							&& statusNumber < IssueUtilities.STATUS_CLOSED) {
						statusList.add(allStatuses.get(i));
					} else if (statusNumber >= IssueUtilities.STATUS_CLOSED
							&& UserUtilities.hasPermission(userPermissions,
									project.getId(),
									UserUtilities.PERMISSION_CLOSE)) {
						statusList.add(allStatuses.get(i));
					}
				}
			} else if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
				for (int i = 0; i < allStatuses.size(); i++) {
					int statusNumber = Integer.parseInt(allStatuses.get(i)
							.getValue());
					if ((statusNumber >= IssueUtilities.STATUS_ASSIGNED && statusNumber < IssueUtilities.STATUS_RESOLVED)
							|| statusNumber >= IssueUtilities.STATUS_CLOSED) {
						statusList.add(allStatuses.get(i));
					}
				}
			} else {
				// Can't change
			}

		}
		// sort by status code so it will be ascending output.
		Collections.sort(statusList, NameValuePair.VALUE_COMPARATOR);
		listOptions.put(IssueUtilities.FIELD_STATUS, statusList);

		List<NameValuePair> severities = IssueUtilities.getSeverities(locale);
		// sort by severity code so it will be ascending output.
		Collections.sort(severities, NameValuePair.VALUE_COMPARATOR);
		listOptions.put(IssueUtilities.FIELD_SEVERITY, severities);

		List<NameValuePair> resolutions = IssueUtilities.getResolutions(locale);
		listOptions.put(IssueUtilities.FIELD_RESOLUTION, resolutions);

		List<Component> components = project.getComponents();
		Collections.sort(components, Component.NAME_COMPARATOR);
		listOptions.put(IssueUtilities.FIELD_COMPONENTS, Convert
				.componentsToNameValuePairs(components));

		List<Version> versions = project.getVersions();
		// Collections.sort(versions, new Version());
		listOptions.put(IssueUtilities.FIELD_VERSIONS, Convert
				.versionsToNameValuePairs(versions));
		listOptions.put(IssueUtilities.FIELD_TARGET_VERSION, Convert
				.versionsToNameValuePairs(versions));

		List<CustomField> projectFields = project.getCustomFields();
		for (int i = 0; i < projectFields.size(); i++) {
			if (projectFields.get(i).getFieldType() == CustomField.Type.LIST) {
				projectFields.get(i).setLabels(locale);
				listOptions.put(projectFields.get(i).getId(), Convert
						.customFieldOptionsToNameValuePairs(projectFields
								.get(i).getOptions()));
			}
		}

		return listOptions;
	}

	public static final void setupIssueForm(IssueForm issueForm, Issue issue,
			Map<Integer, List<NameValuePair>> listOptions,
			HttpServletRequest request, ActionMessages errors)
			throws WorkflowException {
		HttpSession session = request.getSession(true);

		IssueService issueService = ServletContextUtils.getItrackerServices()
				.getIssueService();
		Locale locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
		issueForm.setId(issue.getId());
		issueForm.setProjectId(issue.getProject().getId());
		issueForm.setPrevStatus(issue.getStatus());
		issueForm.setCaller(request.getParameter("caller"));

		issueForm.setDescription(HTMLUtilities.handleQuotes(issue
				.getDescription()));
		issueForm.setStatus(issue.getStatus());

		if (!ProjectUtilities.hasOption(
				ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, issue
						.getProject().getOptions())) {
			try {
				issue.setResolution(IssueUtilities.checkResolutionName(issue
						.getResolution(), locale));
			} catch (MissingResourceException mre) {
				log.error(mre.getMessage());
			} catch (NumberFormatException nfe) {
				log.error(nfe.getMessage());
			}
		}

		issueForm.setResolution(HTMLUtilities.handleQuotes(issue
				.getResolution()));
		issueForm.setSeverity(issue.getSeverity());

		issueForm.setTargetVersion(issue.getTargetVersion() == null ? -1
				: issue.getTargetVersion().getId());

		issueForm.setOwnerId((issue.getOwner() == null ? -1 : issue.getOwner()
				.getId()));

		List<IssueField> fields = issue.getFields();
		HashMap<String, String> customFields = new HashMap<String, String>();
		for (int i = 0; i < fields.size(); i++) {
			customFields.put(fields.get(i).getCustomField().getId().toString(),
					fields.get(i).getValue(locale));
		}

		issueForm.setCustomFields(customFields);

		HashSet<Integer> selectedComponents = issueService
				.getIssueComponentIds(issue.getId());
		if (selectedComponents != null) {
			Integer[] componentIds = null;
			ArrayList<Integer> components = new ArrayList<Integer>(
					selectedComponents);
			componentIds = components.toArray(new Integer[] {});
			issueForm.setComponents(componentIds);
		}

		HashSet<Integer> selectedVersions = issueService
				.getIssueVersionIds(issue.getId());
		if (selectedVersions != null) {
			Integer[] versionIds = null;
			ArrayList<Integer> versions = new ArrayList<Integer>(
					selectedVersions);
			versionIds = versions.toArray(new Integer[] {});
			issueForm.setVersions(versionIds);
		}

		List<ProjectScript> scripts = issue.getProject().getScripts();
		WorkflowUtilities.processFieldScripts(scripts,
				WorkflowUtilities.EVENT_FIELD_ONPOPULATE, listOptions, errors,
				issueForm);
		WorkflowUtilities.processFieldScripts(scripts,
				WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, null, errors,
				issueForm);

	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("execute: called with mapping: " + mapping + ", form: "
					+ form + ", request: " + request + ", response: "
					+ response);
		}
		ActionMessages errors = new ActionMessages();

		try {
			IssueService issueService = getITrackerServices().getIssueService();

			UserService userService = getITrackerServices().getUserService();

			Integer issueId = new Integer(
					(request.getParameter("id") == null ? "-1" : (request
							.getParameter("id"))));

			Issue issue = issueService.getIssue(issueId);
			Project project = issueService.getIssueProject(issueId);

			if (issue == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidissue"));
				saveErrors(request, errors);
				return mapping.findForward("error");
			} else if (project == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidproject"));
			} else if (project.getStatus() != Status.ACTIVE) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.projectlocked"));
			} else {
				HttpSession session = request.getSession(true);
				User currUser = (User) session.getAttribute(Constants.USER_KEY);
				Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);

				Locale locale = getLocale(request);
				// (Locale) session
				// .getAttribute(Constants.LOCALE_KEY);

				List<NameValuePair> ownersList = UserUtilities
						.getAssignableIssueOwnersList(issue, project, currUser,
								locale, userService, userPermissions);

				if (!IssueUtilities.canEditIssue(issue, currUser.getId(),
						userPermissions)) {
					log
							.debug("Unauthorized user requested access to edit issue for project "
									+ project.getId());
					return mapping.findForward("unauthorized");
				}

				IssueForm issueForm = (IssueForm) form;
				if (issueForm == null) {
					issueForm = new IssueForm();
				}
				Map<Integer, List<NameValuePair>> listOptions = getListOptions(
						request, issue, ownersList, userPermissions, issue
								.getProject(), currUser);

				setupIssueForm(issueForm, issue, listOptions, request, errors);

				EditIssueFormAction.setupJspEnv(mapping, issueForm, request,
						issue, issueService, userService, userPermissions,
						listOptions, errors);

				log.debug("Forwarding to edit issue form for issue "
						+ issue.getId());

				// TODO: Sort attachments
				// Collections.sort(attachments,
				// IssueAttachment.CREATE_DATE_COMPARATOR);

				saveToken(request);
				if(issue == null){
					return mapping.findForward("error");
				}
				if (errors.isEmpty()) {
					log.info("EditIssueFormAction: Forward: InputForward");
					saveErrors(request, errors);
					return mapping.getInputForward();
				}

			}

		} catch (Exception e) {
			log.error("Exception while creating edit issue form.", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}

		log.info("EditIssueFormAction: Forward: Error");
		return mapping.findForward("error");
	}
	
	

}
