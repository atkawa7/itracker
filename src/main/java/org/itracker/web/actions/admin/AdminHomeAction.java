package org.itracker.web.actions.admin;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.PermissionType;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.ReportUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;
import org.itracker.web.util.SessionManager;

public class AdminHomeAction extends ItrackerBaseAction {

	private static final Logger log = Logger.getLogger(AdminHomeAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
		.getUserPermissions(request.getSession());

		
		
		if (!UserUtilities.hasPermission(permissions,
				UserUtilities.PERMISSION_USER_ADMIN)) {

			mapping.findForward("listprojectadmin");
		}

		execSetupJspEnv(request);
		
		return mapping.findForward("adminhome");
	}

	
	/**
	 * This utility has to be called for any page forwarding to the admin-home, before forwarding. Else the page will contain no data.
	 * 
	 * @param request
	 */
	public static final void execSetupJspEnv(HttpServletRequest request) {
		Date time_millies = new Date(System.currentTimeMillis());
		// super.executeAlways(mapping, form, request, response);

		IssueService issueService = ServletContextUtils.getItrackerServices()
				.getIssueService();
//		ProjectService projectService = ServletContextUtils.getItrackerServices()
//				.getProjectService();
//		ReportService reportService = ServletContextUtils.getItrackerServices()
//				.getReportService();
		ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
				.getConfigurationService();
//		UserService userService = ServletContextUtils.getItrackerServices().getUserService();


		ProjectService projectService2 = ServletContextUtils
				.getItrackerServices().getProjectService();

		String exportReport = "type=all&reportOutput=XML&reportId="
				+ ReportUtilities.REPORT_EXPORT_XML;

		logTimeMillies("execute: looked up services", time_millies, log,
				Level.INFO);
		Integer numberOfWorkflowScripts = configurationService
				.getWorkflowScripts().size();
		request
				.setAttribute("numberOfWorkflowScripts",
						numberOfWorkflowScripts);
		logTimeMillies("execute: looked up numberOfWorkflowScripts",
				time_millies, log, Level.INFO);

		Integer numberDefinedKeys = configurationService
				.getNumberDefinedKeys(null);
		request.setAttribute("numberDefinedKeys", numberDefinedKeys);
		logTimeMillies("execute: looked up numberDefinedKeys", time_millies,
				log, Level.INFO);

		Integer numberOfStatuses = configurationService
				.getConfigurationItemsByType(
						SystemConfigurationUtilities.TYPE_STATUS).size();
		request.setAttribute("numberOfStatuses", numberOfStatuses);
		logTimeMillies("execute: looked up numberOfStatuses", time_millies,
				log, Level.INFO);

		Integer numberOfSeverities = configurationService
				.getConfigurationItemsByType(
						SystemConfigurationUtilities.TYPE_SEVERITY).size();
		request.setAttribute("numberOfSeverities", numberOfSeverities);
		logTimeMillies("execute: looked up numberOfSeverities", time_millies,
				log, Level.INFO);

		Integer numberOfResolutions = configurationService
				.getConfigurationItemsByType(
						SystemConfigurationUtilities.TYPE_RESOLUTION).size();
		request.setAttribute("numberOfResolutions", numberOfResolutions);
		logTimeMillies("execute: looked up numberOfResolutions", time_millies,
				log, Level.INFO);

		Integer numberOfCustomProjectFields = configurationService
				.getCustomFields().size();
		request.setAttribute("numberOfCustomProjectFields",
				numberOfCustomProjectFields);
		logTimeMillies("execute: looked up numberOfCustomProjectFields",
				time_millies, log, Level.INFO);

		Integer numberofActiveSesssions = SessionManager.getNumActiveSessions();
		request
				.setAttribute("numberofActiveSesssions",
						numberofActiveSesssions);
		logTimeMillies("execute: looked up numberofActiveSesssions",
				time_millies, log, Level.INFO);

		Long allIssueAttachmentsTotalNumber = issueService
				.getAllIssueAttachmentCount();
		request.setAttribute("allIssueAttachmentsTotalNumber",
				allIssueAttachmentsTotalNumber);
		logTimeMillies("execute: looked up allIssueAttachmentsTotalNumber",
				time_millies, log, Level.INFO);

		// TODO: performance issue when attachments size needs to be calculated
		// over many issues !
		// select sum(size)
		// from IssueAttachment
		if (allIssueAttachmentsTotalNumber < 500) {
			Long allIssueAttachmentsTotalSize = issueService
					.getAllIssueAttachmentSize();
			request.setAttribute("allIssueAttachmentsTotalSize",
					allIssueAttachmentsTotalSize);
		} else {
			request.setAttribute("allIssueAttachmentsTotalSize", -1l);
		}
		logTimeMillies("execute: looked up allIssueAttachmentsTotalSize",
				time_millies, log, Level.INFO);
		// Locale locale = getCurrLocale(request);
		// SimpleDateFormat sdf = new
		// SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.full"),
		// locale);
		String lastRun = null;// (Scheduler.getLastRun() == null ? "-" :
		// sdf.format(Scheduler.getLastRun()));
		
		/* set objects needed to render output in request object */
		request.setAttribute("projectService", projectService2);
		request.setAttribute("exportReport", exportReport);
		request.setAttribute("sizeps", projectService2.getAllProjects().size());
		request.setAttribute("lastRun", lastRun);
//		request.setAttribute("ih", issueService);
//		request.setAttribute("ph", projectService);
//		request.setAttribute("rh", reportService);
//		request.setAttribute("sc", configurationService);
//		request.setAttribute("uh", userService);
		logTimeMillies("execute: put services to request", time_millies, log,
				Level.INFO);

		String pageTitleKey = "itracker.web.admin.index.title";
		String pageTitleArg = "";
		request.setAttribute("pageTitleKey", pageTitleKey);
		request.setAttribute("pageTitleArg", pageTitleArg);

		logTimeMillies("execute: returning", time_millies, log, Level.INFO);
	}
}
