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

package org.itracker.web.actions.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.Report;
import org.itracker.model.SystemConfiguration;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ReportService;
import org.itracker.services.exceptions.ImportExportException;
import org.itracker.services.exceptions.ReportException;
import org.itracker.services.util.ImportExportTags;
import org.itracker.services.util.ImportExportUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ReportUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class DisplayReportAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(DisplayReportAction.class);
	
    public DisplayReportAction() {
    }

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
		//  TODO: Action Cleanup

        try {
            HttpSession session = request.getSession(false);
            Locale userLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            IssueSearchQuery isqm = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);

            List<Issue> reportingIssues = new ArrayList<Issue>();
            String reportType = (String) PropertyUtils.getSimpleProperty(form, "type");
        	log.info("execute: report type was " + reportType);
        	
            Integer[] projectIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "projectIds");
            // TODO All Issues this is huge, remove if possible
            if("all".equalsIgnoreCase(reportType)) {
                // Export all of the issues in the system
                User currUser = (User) session.getAttribute(Constants.USER_KEY);
                if(! currUser.isSuperUser()) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.unauthorized"));
                    throw new ReportException();
                }

                IssueService issueService = getITrackerServices().getIssueService();
                reportingIssues = issueService.getAllIssues();
                Collections.sort(reportingIssues, Issue.ID_COMPARATOR);
            } else if("project".equalsIgnoreCase(reportType)) {
                if(projectIds != null && projectIds.length > 0) {
                    // This wasn't a regular search.  So instead, take all the selected projects and find all the
                    // issues for them, check which ones the user can see, and then create a new array of issues
                    List<Issue> reportDataList = new ArrayList<Issue>();
                    
                    IssueService issueService = getITrackerServices().getIssueService();
                    
                    User currUser = (User) session.getAttribute(Constants.USER_KEY);
                    Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
                    Iterator<Issue> issuesIt = null;
                    Issue currentIssue = null;
                    List<Issue> issues;
                    for(int i = 0; i < projectIds.length; i++) {
                        issues = issueService.getIssuesByProjectId(projectIds[i]);
                        issuesIt = issues.iterator();
                        while (issuesIt.hasNext()) {
                        	currentIssue = issuesIt.next();
                            if(IssueUtilities.canViewIssue(currentIssue, currUser, userPermissions)) {
                                reportDataList.add(currentIssue);
                            }
                        }
                    }
                    reportingIssues = reportDataList;
                    Collections.sort(reportingIssues, Issue.ID_COMPARATOR);
                    
                } else {
                    throw new ReportException("", "itracker.web.error.projectrequired");
                }
            } else {
                // This must be a regular search, look for a search query result.
                reportingIssues = (isqm == null || isqm.getResults() == null ? new ArrayList<Issue>() : isqm.getResults());
            }

            log.debug("Report data contains " + reportingIssues.size() + " elements.");

            if(reportingIssues.size() == 0) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noreportdata"));
                throw new ReportException();
            }

            Integer reportId = (Integer) PropertyUtils.getSimpleProperty(form, "reportId");
            String reportOutput = (String) PropertyUtils.getSimpleProperty(form, "reportOutput");
            if(reportId == null || reportId.intValue() == 0) {
                log.debug("Invalid report id: " + reportId + " requested.");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                throw new ReportException();
            } else if(reportId.intValue() == ReportUtilities.REPORT_EXPORT_XML) {
                log.debug("Issue export requested.");

                ConfigurationService configurationService = getITrackerServices().getConfigurationService();
                SystemConfiguration config = configurationService.getSystemConfiguration(ImportExportTags.EXPORT_LOCALE);

                if(! exportIssues(reportingIssues, config, request, response)) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
                    throw new ReportException();
                }
                return null;
            } else if(reportId.intValue() > 0) {
                log.debug("Defined report (" + reportId + ") requested.");

                ReportService reportService = getITrackerServices().getReportService();
                Report reportModel = reportService.getReportDAO().findByPrimaryKey(reportId);

                // probably useless. the dao throws when the report doesn't exists
                if(reportModel == null) {
                    log.debug("Invalid report id: " + reportId + " requested.");
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                    throw new ReportException();
                }
                
                log.debug("Report " + reportModel.toString() + " found.");                                                               
                
                if(ReportUtilities.REPORT_OUTPUT_PDF.equals(reportOutput)) {
                    log.debug("Processing PDF report.");
                    reportService.outputPDF(reportingIssues, reportModel, userLocale, reportOutput, session, request, response, mapping);
                    return null;
                } else if(ReportUtilities.REPORT_OUTPUT_XLS.equals(reportOutput)) {
                    log.debug("Processing XLS report.");
                    //report.outputXLS(request, response, mapping);
                    throw new RuntimeException("not working");
                    //return null;
                } else if(ReportUtilities.REPORT_OUTPUT_CSV.equals(reportOutput)) {
                    log.debug("Processing CSV report.");
                    //report.outputCSV(request, response, mapping);
                    throw new RuntimeException("not working");
                    //return null;
                } else if(ReportUtilities.REPORT_OUTPUT_HTML.equals(reportOutput)) {
                    log.debug("Processing HTML report.");
                    //report.outputHTML(request, response, mapping);
                    throw new RuntimeException("not working");
                    //turn null;
                } else {
                    log.error("Invalid report output format: " + reportOutput);
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreportoutput"));
                    throw new ReportException();
                }
            }
        } catch(ReportException re) {
            if(re.getErrorKey() != null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(re.getErrorKey()));
            }
        } catch(Exception e) {
            log.debug("Error in report processing: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.details", e.getMessage()));
        }

        if(! errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }

    private boolean exportIssues(List<Issue> issues, SystemConfiguration config, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"issue_export.xml\"");
        PrintWriter out = response.getWriter();

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n");
        try {
            String xml = ImportExportUtilities.exportIssues(issues, config);
            out.print(xml);
            out.flush();
        } catch(ImportExportException iee) {
            log.error("Error exporting issue data. Message: " + iee.getMessage(), iee);
            return false;
        }
        out.flush();
        out.close();
        return true;
    }
}
  