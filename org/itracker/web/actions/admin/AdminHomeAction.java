package org.itracker.web.actions.admin;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.scheduler.Scheduler;
import org.itracker.web.util.SessionManager;


public class AdminHomeAction extends ItrackerBaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		super.executeAlways(mapping,form,request,response);
		  IssueService issueService = this.getITrackerServices().getIssueService();
		  ProjectService projectService = this.getITrackerServices().getProjectService();
		  ReportService reportService = this.getITrackerServices().getReportService();
		  ConfigurationService configurationService = this.getITrackerServices().getConfigurationService();
		  UserService userService = this.getITrackerServices().getUserService();
		  Integer numberOfWorkflowScripts = configurationService.getWorkflowScripts().size();
		  request.setAttribute("numberOfWorkflowScripts",numberOfWorkflowScripts);
		  Integer numberDefinedKeys = configurationService.getNumberDefinedKeys(null);
		  request.setAttribute("numberDefinedKeys",numberDefinedKeys);
		  Integer numberOfStatuses = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS).size();
		  request.setAttribute("numberOfStatuses",numberOfStatuses);
		  Integer numberOfSeverities = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY).size();
		  request.setAttribute("numberOfSeverities",numberOfSeverities);
		  Integer numberOfResolutions = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION).size();
		  request.setAttribute("numberOfResolutions",numberOfResolutions);
		  Integer numberOfCustomProjectFields = configurationService.getCustomFields().size();
		  request.setAttribute("numberOfCustomProjectFields",numberOfCustomProjectFields);
		  int numberofActiveSesssionsString = SessionManager.getNumActiveSessions();
		  Integer numberofActiveSesssions = new Integer(numberofActiveSesssionsString);
		  request.setAttribute("numberofActiveSesssions",numberofActiveSesssions);
		  Integer allIssueAttachmentsTotalNumber = issueService.getAllIssueAttachments().size();
		  Long allIssueAttachmentsTotalSize = new Long(issueService.getAllIssueAttachmentSize());
		  request.setAttribute("allIssueAttachmentsTotalNumber",allIssueAttachmentsTotalNumber);
		  request.setAttribute("allIssueAttachmentsTotalSize",allIssueAttachmentsTotalSize);
		  Locale currLocale = super.getLocale(request);
		  SimpleDateFormat sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.full"), currLocale);
		  String lastRun = (Scheduler.getLastRun() == null ? "-" : sdf.format(Scheduler.getLastRun()));
		  request.setAttribute("lastRun",lastRun);
		  request.setAttribute("ih",issueService);
		  request.setAttribute("ph",projectService);
		  request.setAttribute("rh",reportService);
		  request.setAttribute("sc",configurationService);
		  request.setAttribute("uh",userService);
		  
		  String pageTitleKey = "itracker.web.admin.index.title"; 
	      String pageTitleArg = "";			
	      request.setAttribute("pageTitleKey",pageTitleKey); 
	      request.setAttribute("pageTitleArg",pageTitleArg); 
		  
		return mapping.findForward("adminhome");
	}

	public AdminHomeAction() {
		super();
	}

}
