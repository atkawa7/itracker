package org.itracker.web.actions.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.IssueNavigationUtil;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class ViewIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(ViewIssueAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
		
		Locale locale = getLocale(request);

		String pageTitleKey = "itracker.web.viewissue.title";
		String pageTitleArg = request.getParameter("id");
		request.setAttribute("pageTitleKey", pageTitleKey);
		request.setAttribute("pageTitleArg", pageTitleArg);
		Integer issueId;
		try {
			issueId = Integer.valueOf(request.getParameter("id"));
		} catch (RuntimeException re) {
			getErrors(request).add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noissue"));
			saveErrors(request, getErrors(request));
			return mapping.findForward("index");
		}

		HttpSession session = request.getSession();
		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
				.getUserPermissions(session);
		User um = RequestHelper.getCurrentUser(session);

		NotificationService notificationService = ServletContextUtils.getItrackerServices().getNotificationService();

		Issue issue;

		Integer currUserId = um.getId();
//		TODO verify this code.
		try {
			issueId = new Integer((request.getParameter("id") == null ? "-1"
					: (request.getParameter("id"))));
			issue = issueService.getIssue(issueId);
		} catch (Exception ex) {
			issue = null;
		}
		if (issue == null) {
			this.getErrors(request).add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("itracker.web.error.noissue"));
			log.info("ViewIssueAction: Forward: error");
			return mapping.findForward("error");
		}

		Project project = issue.getProject();
		if (project != null && project.getStatus() != Status.ACTIVE
				&& project.getStatus() != Status.VIEWABLE) {
			this.getErrors(request).add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("itracker.web.error.projectlocked"));
			log.info("ViewIssueAction: Forward: error");
			return mapping.findForward("error");
		} else {

			if (project == null || !LoginUtilities.canViewIssue(issue,
					LoginUtilities.getPrincipal())) {
				log.info("ViewIssueAction: Forward: unauthorized");
				return mapping.findForward("unauthorized");
			}
		}
        IssueForm.setupNotificationsInRequest(request, issueService
                      .getIssue(issueId),
                      ServletContextUtils.getItrackerServices().getNotificationService());

        IssueNavigationUtil.setupNextPreviousIssueInRequest(request, issue, issueService);
		/*
		 * Get issue history, sort on create date.
		 */
		List<IssueHistory> issueHistories = issue.getHistory();
		List<IssueHistory> histories = new ArrayList<IssueHistory>();
		Collections.sort(issueHistories, IssueHistory.CREATE_DATE_COMPARATOR);
		for (IssueHistory history : issueHistories) {
			if (history.getStatus() == IssueUtilities.HISTORY_STATUS_AVAILABLE) {
				histories.add(history);
			}
		}
		if (project.getVersions() != null) {
			Collections.sort(project.getVersions(), Version.VERSION_COMPARATOR);
		}
		if (issue.getComponents() != null && issue.getComponents().size() > 0) {
			Collections.sort(issue.getComponents(), Component.NAME_COMPARATOR);
		}
		if (issue.getVersions() != null && issue.getVersions().size() > 0) {
			Collections.sort(issue.getVersions(), new Version.VersionComparator());
		}
		/*
		 * Get attachments of issue, and sort attachments on created date
		 */
		List<IssueAttachment> attachments = issue.getAttachments();
		Collections.sort(attachments, IssueAttachment.CREATE_DATE_COMPARATOR);
		/*
		 * Get the issue status name to display.
		 */
		String issueStatusName = IssueUtilities.getStatusName(issue.getStatus(), locale);
		/* Get issue severity name
		 * 
		 */
		String issueSeverityName = IssueUtilities.getSeverityName(issue.getSeverity(), locale);
		/*
		 * Create Project field map
		 */
		IssueForm.setupProjectFieldsMapJspEnv(project.getCustomFields(), issue.getFields(), request);

		/*
		 * Set the objects in request that are required for ui render
		 */
		request.setAttribute("issueId", issueId);
		request.setAttribute("issue", issue);
		request.setAttribute("attachments", attachments);
		request.setAttribute("hasAttachmentOption", !ProjectUtilities
				.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, project
						.getOptions()));
		request.setAttribute("histories", histories);
		request.setAttribute("project", project);
        request.setAttribute("rssFeed", "/servlets/issues/p" + project.getId() + "/i" + issue.getId());
		request.setAttribute("hasIssueNotification", !notificationService.hasIssueNotification(
				issue, currUserId));
		request.setAttribute("canEditIssue", IssueUtilities.canEditIssue(issue,
				currUserId, permissions));
		request.setAttribute("canCreateIssue",
				(project.getStatus() == Status.ACTIVE && UserUtilities
						.hasPermission(permissions, project.getId(),
								PermissionType.ISSUE_CREATE)));
		request.setAttribute("issueStatusName",issueStatusName);
		request.setAttribute("issueSeverityName",issueSeverityName);
		request.setAttribute("issueOwnerName",(issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", locale) : issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName()) );

		log.info("ViewIssueAction: Forward: viewissue");
		return mapping.findForward("viewissue");

	}
}
