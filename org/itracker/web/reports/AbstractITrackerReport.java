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

package org.itracker.web.reports;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import org.apache.struts.action.ActionMapping;
import org.itracker.model.Issue;
import org.itracker.model.Report;
import org.itracker.services.exceptions.ReportException;


/**
  * This class encapsulates a basic ITracker report.  Only the initialize method has been
  * implemented in this class, and it only provides some basic access to provided input
  * data.
  */
public abstract class AbstractITrackerReport implements ITrackerReport {
    
    protected final Logger logger;
    protected List<Issue> issues = null;
    protected Report report = null;
    protected Locale locale = null;
    protected String reportOutput = null;
    protected HttpSession session = null;

    public AbstractITrackerReport() {
        this.logger = Logger.getLogger(getClass());
    }

    /**
      * Initializes the ITrackerReport.  This implementation only takes the input data and stores
      * it for later use.
      */
    public void initializeReport(List<Issue> issues, Report report, Locale locale, String reportOutput, HttpSession session) throws ReportException {
        storeInputs(issues, report, locale, reportOutput, session);
    }

    /**
      * This method must be overriden by subclasses to perform any customization that can only
      * be performed through the API.  This method should be called after initializeReport.
      */
    public abstract void augmentReport() throws ReportException;

    /**
      * Outputs the report as PDF.  It can be assumed that the initializeReport and augementReport have
      * already been called prior to this method.
      * @param request the HttpServletRequest for the report request
      * @param response the HttpServletResponse to send the report output
      * @param mapping the ActionMapping for the display report action
      * @exception ReportException thrown if there is an error outputing the report as PDF
      */
    public abstract void outputPDF(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;

    /**
      * Outputs the report as HTML.  It can be assumed that the initializeReport and augementReport have
      * already been called prior to this method.
      * @param request the HttpServletRequest for the report request
      * @param response the HttpServletResponse to send the report output
      * @param mapping the ActionMapping for the display report action
      * @exception ReportException thrown if there is an error outputing the report as HTML
      */
    public abstract void outputHTML(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;

    /**
      * Outputs the report as an Excel spreadsheet (XLS).  It can be assumed that the initializeReport and augementReport have
      * already been called prior to this method.
      * @param request the HttpServletRequest for the report request
      * @param response the HttpServletResponse to send the report output
      * @param mapping the ActionMapping for the display report action
      * @exception ReportException thrown if there is an error outputing the report as XLS
      */
    public abstract void outputXLS(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;

    /**
      * Outputs the report as CSV.  It can be assumed that the initializeReport and augementReport have
      * already been called prior to this method.
      * @param request the HttpServletRequest for the report request
      * @param response the HttpServletResponse to send the report output
      * @param mapping the ActionMapping for the display report action
      * @exception ReportException thrown if there is an error outputing the report as CSV
      */
    public abstract void outputCSV(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;

    /**
      * Returns the current report object.  The class of the report depends on underlying framework
      * the report is based upon.
      * @exception ReportException thrown if the report has not been properly initialized
      */
    public abstract Object getReport() throws ReportException;

    protected void storeInputs(List<Issue> issues, Report report, Locale locale, String reportOutput, HttpSession session) {
        this.issues = issues;
        this.report = report;
        this.locale = locale;
        this.reportOutput = reportOutput;
        this.session = session;
    }
}
