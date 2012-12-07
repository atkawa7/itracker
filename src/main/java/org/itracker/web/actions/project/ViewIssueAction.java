package org.itracker.web.actions.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.RequestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class ViewIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(ViewIssueAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//  TODO: Action Cleanup

		IssueService issueService = this.getITrackerServices()
				.getIssueService();
		
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
		EditIssueActionUtil.setupNotificationsInRequest(request, issueService
				.getIssue(issueId),
				getITrackerServices().getNotificationService());

		HttpSession session = request.getSession();
		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
				.getUserPermissions(session);
		User um = RequestHelper.getCurrentUser(session);

		NotificationService notificationService = getITrackerServices().getNotificationService();

		Issue issue = null;

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
			//User owner = issue.getOwner();
			//User creator = issue.getCreator();
			boolean canViewIssue = IssueUtilities.canViewIssue(issue,
					currUserId, permissions);

			if (project == null || !canViewIssue) {
				log.info("ViewIssueAction: Forward: unauthorized");
				return mapping.findForward("unauthorized");
			}
		}

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
		EditIssueActionUtil.setupProjectFieldsMapJspEnv(project.getCustomFields(), issue.getFields(), request);

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
								UserUtilities.PERMISSION_CREATE)));
		request.setAttribute("issueStatusName",issueStatusName);
		request.setAttribute("issueSeverityName",issueSeverityName);
		request.setAttribute("issueOwnerName",(issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", locale) : issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName()) );

		log.info("ViewIssueAction: Forward: viewissue");
		return mapping.findForward("viewissue");

	}
}
