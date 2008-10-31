package org.itracker.web.actions.admin;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.SessionManager;


public class AdminHomeAction extends ItrackerBaseAction {

	private static final Logger log = Logger.getLogger(AdminHomeAction.class);
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

    	Date time_millies = new Date(System.currentTimeMillis());
//        super.executeAlways(mapping, form, request, response);

    	
        IssueService issueService = this.getITrackerServices().getIssueService();
        ProjectService projectService = this.getITrackerServices().getProjectService();
        ReportService reportService = this.getITrackerServices().getReportService();
        ConfigurationService configurationService = this.getITrackerServices().getConfigurationService();
        UserService userService = this.getITrackerServices().getUserService();
        
        logTimeMillies("execute: looked up services", time_millies, log, Level.INFO);
        Integer numberOfWorkflowScripts = configurationService.getWorkflowScripts().size();
        request.setAttribute("numberOfWorkflowScripts", numberOfWorkflowScripts);
        logTimeMillies("execute: looked up numberOfWorkflowScripts", time_millies, log, Level.INFO);

        Integer numberDefinedKeys = configurationService.getNumberDefinedKeys(null);
        request.setAttribute("numberDefinedKeys", numberDefinedKeys);
        logTimeMillies("execute: looked up numberDefinedKeys", time_millies, log, Level.INFO);

        Integer numberOfStatuses = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS).size();
        request.setAttribute("numberOfStatuses", numberOfStatuses);
        logTimeMillies("execute: looked up numberOfStatuses", time_millies, log, Level.INFO);

        Integer numberOfSeverities = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY).size();
        request.setAttribute("numberOfSeverities", numberOfSeverities);
        logTimeMillies("execute: looked up numberOfSeverities", time_millies, log, Level.INFO);

        Integer numberOfResolutions = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION).size();
        request.setAttribute("numberOfResolutions", numberOfResolutions);
        logTimeMillies("execute: looked up numberOfResolutions", time_millies, log, Level.INFO);

        Integer numberOfCustomProjectFields = configurationService.getCustomFields().size();
        request.setAttribute("numberOfCustomProjectFields", numberOfCustomProjectFields);
        logTimeMillies("execute: looked up numberOfCustomProjectFields", time_millies, log, Level.INFO);

        Integer numberofActiveSesssions = SessionManager.getNumActiveSessions();
        request.setAttribute("numberofActiveSesssions", numberofActiveSesssions);
        logTimeMillies("execute: looked up numberofActiveSesssions", time_millies, log, Level.INFO);

        Long allIssueAttachmentsTotalNumber = issueService.countSystemIssuesAttachments();
        request.setAttribute("allIssueAttachmentsTotalNumber", allIssueAttachmentsTotalNumber);
        logTimeMillies("execute: looked up allIssueAttachmentsTotalNumber", time_millies, log, Level.INFO);

        // TODO: performance issue when attachments size needs to be calculated over many issues !
//        select sum(size)
//        from IssueAttachment
        if (allIssueAttachmentsTotalNumber < 500) {
	        Long allIssueAttachmentsTotalSize = issueService.totalSystemIssuesAttachmentSize();
	        request.setAttribute("allIssueAttachmentsTotalSize", allIssueAttachmentsTotalSize);
        } else {
	        request.setAttribute("allIssueAttachmentsTotalSize", -1l);
        }
        logTimeMillies("execute: looked up allIssueAttachmentsTotalSize", time_millies, log, Level.INFO);
//        Locale locale = getCurrLocale(request);
//        SimpleDateFormat sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.full"), locale);
        String lastRun = null;//(Scheduler.getLastRun() == null ? "-" : sdf.format(Scheduler.getLastRun()));

        request.setAttribute("lastRun", lastRun);
        request.setAttribute("ih", issueService);
        request.setAttribute("ph", projectService);
        request.setAttribute("rh", reportService);
        request.setAttribute("sc", configurationService);
        request.setAttribute("uh", userService);
        logTimeMillies("execute: put services to request", time_millies, log, Level.INFO);

        String pageTitleKey = "itracker.web.admin.index.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        logTimeMillies("execute: returning", time_millies, log, Level.INFO);
        return mapping.findForward("adminhome");
    }

}
