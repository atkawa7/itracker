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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
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
import org.itracker.services.IssueService;
import org.itracker.services.ReportService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.ImportExportException;
import org.itracker.services.exceptions.ReportException;
import org.itracker.services.util.ImportExportTags;
import org.itracker.services.util.ImportExportUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ReportUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.reports.DefaultITrackerJFreeReport;
import org.itracker.web.reports.DefaultITrackerJasperReport;
import org.itracker.web.reports.ITrackerReport;
import org.itracker.web.util.Constants;


public class DisplayReportAction extends ItrackerBaseAction {

    public DisplayReportAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            HttpSession session = request.getSession(false);
            Locale userLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            IssueSearchQuery isqm = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);

            List<Issue> reportDataArray = new ArrayList<Issue>();
            String type = (String) PropertyUtils.getSimpleProperty(form, "type");
            Integer[] projectIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "projectIds");
            if("all".equalsIgnoreCase(type)) {
                // Export all of the issues in the system
                User currUser = (User) session.getAttribute(Constants.USER_KEY);
                if(! currUser.isSuperUser()) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.unauthorized"));
                    throw new ReportException();
                }

                IssueService issueService = getITrackerServices().getIssueService();
                reportDataArray = issueService.getAllIssues();
                Collections.sort(reportDataArray, new Issue.CompareById());
            } else if("project".equalsIgnoreCase(type)) {
                if(projectIds != null && projectIds.length > 0) {
                    // This wasn't a regular search.  So instead, take all the selected projects and find all the
                    // issues for them, check which ones the user can see, and then create a new array of issues
                    List<Issue> reportDataList = new ArrayList<Issue>();
                    
                    IssueService issueService = getITrackerServices().getIssueService();
                    
                    User currUser = (User) session.getAttribute(Constants.USER_KEY);
                    Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);

                    for(int i = 0; i < projectIds.length; i++) {
                        List<Issue> issues = issueService.getIssuesByProjectId(projectIds[i]);
                        for(int j = 0; j < issues.size(); j++) {
                            if(IssueUtilities.canViewIssue(issues.get(j), currUser, userPermissions)) {
                                reportDataList.add(issues.get(j));
                            }
                        }
                    }
                    reportDataArray = new ArrayList<Issue>();
                    reportDataArray=reportDataList;
                    Collections.sort(reportDataArray, new Issue.CompareById());
                } else {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectrequired"));
                    throw new ReportException();
                }
            } else {
                // This must be a regular search, look for a search query result.
                reportDataArray = (isqm == null || isqm.getResults() == null ? new ArrayList<Issue>() : isqm.getResults());
            }

            logger.debug("Report data contains " + reportDataArray.size() + " elements.");

            if(reportDataArray.size() == 0) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noreportdata"));
                throw new ReportException();
            }

            Integer reportId = (Integer) PropertyUtils.getSimpleProperty(form, "reportId");
            String reportOutput = (String) PropertyUtils.getSimpleProperty(form, "reportOutput");
            if(reportId == null || reportId.intValue() == 0) {
                logger.debug("Invalid report id: " + reportId + " requested.");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                throw new ReportException();
            } else if(reportId.intValue() == ReportUtilities.REPORT_EXPORT_XML) {
                logger.debug("Issue export requested.");

                ConfigurationService configurationService = getITrackerServices().getConfigurationService();
                SystemConfiguration config = configurationService.getSystemConfiguration(ImportExportTags.EXPORT_LOCALE);

                if(! exportIssues(reportDataArray, config, request, response)) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
                    throw new ReportException();
                }
                return null;
            } else if(reportId.intValue() > 0) {
                logger.debug("Defined report (" + reportId + ") requested.");

                ReportService reportService = getITrackerServices().getReportService();

                Report reportModel = reportService.getReport(reportId);
                if(reportModel == null) {
                    logger.debug("Invalid report id: " + reportId + " requested.");
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                    throw new ReportException();
                }

                reportModel.setFileData(reportService.getReportFile(reportModel.getId()));
                logger.debug("Report " + reportModel.toString() + " found.");

                ITrackerReport report = null;
                if(reportModel.getClassName() != null && ! reportModel.getClassName().equals("")) {
                    logger.debug("Creating new class: " + reportModel.getClassName());
                    Class reportClass = Class.forName(reportModel.getClassName());
                    report = (ITrackerReport) reportClass.newInstance();
                } else if(reportModel.getReportType() == ReportUtilities.REPORTTYPE_JFREE) {
                    report = (ITrackerReport) new DefaultITrackerJFreeReport();
                } else if(reportModel.getReportType() == ReportUtilities.REPORTTYPE_JASPER) {
                    report = (ITrackerReport) new DefaultITrackerJasperReport();
                }

                if(report == null) {
                    logger.error("Unable to create new instance of report: " + reportModel.getName() + " class: " + reportModel.getClassName() + " type: " + reportModel.getReportType());
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                    throw new ReportException();
                }

                logger.debug("Initializing report object.");
                report.initializeReport(reportDataArray, reportModel, userLocale, reportOutput, session);
                logger.debug("Augmenting report object.");
                report.augmentReport();

                if(ReportUtilities.REPORT_OUTPUT_PDF.equals(reportOutput)) {
                    logger.debug("Processing PDF report.");
                    report.outputPDF(request, response, mapping);
                    return null;
                } else if(ReportUtilities.REPORT_OUTPUT_XLS.equals(reportOutput)) {
                    logger.debug("Processing XLS report.");
                    report.outputXLS(request, response, mapping);
                    return null;
                } else if(ReportUtilities.REPORT_OUTPUT_CSV.equals(reportOutput)) {
                    logger.debug("Processing CSV report.");
                    report.outputCSV(request, response, mapping);
                    return null;
                } else if(ReportUtilities.REPORT_OUTPUT_HTML.equals(reportOutput)) {
                    logger.debug("Processing HTML report.");
                    report.outputHTML(request, response, mapping);
                    return null;
                } else {
                    logger.error("Inavlid report output format: " + reportOutput);
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreportoutput"));
                    throw new ReportException();
                }
            }
        } catch(ReportException re) {
            if(re.getErrorKey() != null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(re.getErrorKey()));
            }
        } catch(Exception e) {
            logger.debug("Error in report processing: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.details", e.getMessage()));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
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
            logger.error("Error exporting issue data. Message: " + iee.getMessage(), iee);
            return false;
        }
        out.flush();
        out.close();
        return true;
    }
}
  