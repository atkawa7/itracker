package org.itracker.web.actions.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.IssueActivity;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.RequestHelper;

public class ViewIssueActivityAction extends ItrackerBaseAction {
	private static final Logger log = Logger
			.getLogger(ViewIssueActivityAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// super.executeAlways(mapping,form,request,response);
		IssueService issueService = this.getITrackerServices()
				.getIssueService();

		request.setAttribute("ih", issueService);

		HttpSession session = request.getSession();
		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
				.getUserPermissions(session);
		User um = RequestHelper.getCurrentUser(session);
		IssueService ih = (IssueService) request.getAttribute("ih");

		Integer issueId = new Integer(
				(request.getParameter("id") == null ? "-1" : (request
						.getParameter("id"))));
		Integer currUserId = um.getId();

		List<IssueActivity> activity = ih.getIssueActivity(issueId);
		Map<IssueActivity,String> activities  = new HashMap<IssueActivity, String>();
		for (int i = 0; i < activity.size(); i++) {
			IssueActivity ia = activity.get(i);
			activities.put(ia,IssueUtilities.getActivityName(ia.getActivityType(),
					(java.util.Locale) session.getAttribute("currLocale")));
		}
		Project project = ih.getIssueProject(issueId);
		User owner = ih.getIssueOwner(issueId);
		User creator = ih.getIssueCreator(issueId);

		boolean hasPerm = (project == null || (!UserUtilities
				.hasPermission(permissions, project.getId(),
						UserUtilities.PERMISSION_VIEW_ALL) && !(UserUtilities
				.hasPermission(permissions, project.getId(),
						UserUtilities.PERMISSION_VIEW_USERS) && ((owner != null && owner
				.getId().equals(currUserId)) || (creator != null && creator
				.getId().equals(currUserId))))));

		

		/*
		 * Set the objects in request that are required for ui render
		 */
		request.setAttribute("hasPerm", hasPerm);
		request.setAttribute("issueId", issueId);
		request.setAttribute("activities", activities);
		if(hasPerm){
			return mapping.findForward("unauthorized");
		}else{
			return mapping.findForward("viewissueactivity");
		}

	}

}
