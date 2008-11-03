package org.itracker.web.actions.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.IssueService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.RequestHelper;

public class ViewIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(ViewIssueAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//		
		// super.executeAlways(mapping,form,request,response);
		//			
		IssueService issueService = this.getITrackerServices()
				.getIssueService();
		request.setAttribute("ih", issueService);

		String pageTitleKey = "itracker.web.viewissue.title";
		String pageTitleArg = request.getParameter("id");
		request.setAttribute("pageTitleKey", pageTitleKey);
		request.setAttribute("pageTitleArg", pageTitleArg);

		EditIssueFormAction.setupNotificationsInRequest(request, issueService
				.getIssue(Integer.valueOf(request.getParameter("id"))),
				getITrackerServices().getNotificationService());

		HttpSession session = request.getSession();
		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
				.getUserPermissions(session);
		User um = RequestHelper.getCurrentUser(session);

		IssueService ih = (IssueService) request.getAttribute("ih");

		Integer issueId = null;
		Issue issue = null;

		Integer currUserId = um.getId();

		try {
			issueId = new Integer((request.getParameter("id") == null ? "-1"
					: (request.getParameter("id"))));
			issue = ih.getIssue(issueId);
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
			User owner = issue.getOwner();
			User creator = issue.getCreator();
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
		if(project.getVersions() != null){
			Collections.sort(project.getVersions(), Version.VERSION_COMPARATOR);
		}
		if(issue.getComponents() !=null && issue.getComponents().size()>0){
		 Collections.sort(issue.getComponents(), Component.NAME_COMPARATOR);
		}
		if(issue.getVersions() != null && issue.getVersions().size()>0){
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
		String issueStatusName = IssueUtilities.getStatusName(issue.getStatus(), (java.util.Locale)session.getAttribute("currLocale"));
		/* Get issue severity name
		 * 
		 */
		String issueSeverityName = IssueUtilities.getSeverityName(issue.getSeverity(), (java.util.Locale)session.getAttribute("currLocale"));
		/*
		 * Create Project field map
		 */
		List<CustomField> projectFields = project.getCustomFields();
		Map<CustomField,String> projectFieldsMap = new HashMap<CustomField, String>();
        if(projectFields != null && projectFields.size() > 0) {
            Collections.sort(projectFields, CustomField.ID_COMPARATOR);
            List<IssueField> issueFields = issue.getFields();
            HashMap<Integer,String> fieldValues = new HashMap<Integer,String>();
            for(int i = 0; i < issueFields.size(); i++) {
               fieldValues.put(issueFields.get(i).getCustomField().getId(), issueFields.get(i).getValue((java.util.Locale)session.getAttribute("currLocale")));
            }
            for(CustomField projectField: projectFields) {
            	String fieldValue = (String) fieldValues.get(projectField.getId());
            	projectFieldsMap.put(projectField, fieldValue);
            }
        }
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
		request.setAttribute("hasIssueNotification", !ih.hasIssueNotification(
				issue.getId(), currUserId));
		request.setAttribute("canEditIssue", IssueUtilities.canEditIssue(issue,
				currUserId, permissions));
		request.setAttribute("canCreateIssue",
				(project.getStatus() == Status.ACTIVE && UserUtilities
						.hasPermission(permissions, project.getId(),
								UserUtilities.PERMISSION_CREATE)));
		request.setAttribute("issueStatusName",issueStatusName);
		request.setAttribute("issueSeverityName",issueSeverityName);
		request.setAttribute("issueOwnerName",(issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", (java.util.Locale)session.getAttribute("currLocale")) : issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName()) );
		request.setAttribute("projectFieldsMap", projectFieldsMap);
		log.info("ViewIssueAction: Forward: viewissue");
		return mapping.findForward("viewissue");

	}
}
