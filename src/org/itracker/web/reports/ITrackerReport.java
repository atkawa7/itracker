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

import org.apache.struts.action.ActionMapping;
import org.itracker.model.Issue;
import org.itracker.model.Report;
import org.itracker.services.exceptions.ReportException;


public interface ITrackerReport {
    
    /**
     * Initializes the report.  It will be called immediately after the ITrackerReport
     * object is created.
     * @param issues an array of issues that is supplied as the data
     * @param report the report as a Report
     * @param locale the locale to be used for the report output
     * @param reportOutput the requested output method
     * @param session the session of the user who ran the report.  Can be used for storage, or to determine user attributes
     * @exception ReportException thrown if the report has not been properly initialized
     */
    public void initializeReport(List<Issue> issues, Report report, Locale locale, String reportOutput, HttpSession session) throws ReportException;
    
    /**
     * This method should be called after initializeReport.  It is provided so that the abstract
     * class can have a default initialization, and still allow subclasses to add their own
     * logic.
     * @exception ReportException thrown if the report has not been properly augmented
     */
    public void augmentReport() throws ReportException;
    
    /**
     * Outputs the report as PDF.  It can be assumed that the initializeReport and augementReport have
     * already been called prior to this method.
     * @param request the HttpServletRequest for the report request
     * @param response the HttpServletResponse to send the report output
     * @param mapping the ActionMapping for the display report action
     * @exception ReportException thrown if there is an error outputing the report as PDF
     */
    public void outputPDF(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;
    
    /**
     * Outputs the report as HTML.  It can be assumed that the initializeReport and augementReport have
     * already been called prior to this method.
     * @param request the HttpServletRequest for the report request
     * @param response the HttpServletResponse to send the report output
     * @param mapping the ActionMapping for the display report action
     * @exception ReportException thrown if there is an error outputing the report as HTML
     */
    public void outputHTML(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;
    
    /**
     * Outputs the report as an Excel spreadsheet (XLS).  It can be assumed that the initializeReport and augementReport have
     * already been called prior to this method.
     * @param request the HttpServletRequest for the report request
     * @param response the HttpServletResponse to send the report output
     * @param mapping the ActionMapping for the display report action
     * @exception ReportException thrown if there is an error outputing the report as XLS
     */
    public void outputXLS(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;
    
    /**
     * Outputs the report as CSV.  It can be assumed that the initializeReport and augementReport have
     * already been called prior to this method.
     * @param request the HttpServletRequest for the report request
     * @param response the HttpServletResponse to send the report output
     * @param mapping the ActionMapping for the display report action
     * @exception ReportException thrown if there is an error outputing the report as CSV
     */
    public void outputCSV(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException;
    
    /**
     * Returns the current report object.  The class of the report depends on underlying framework
     * the report is based upon.
     * @exception ReportException thrown if the report has not been properly initialized
     */
    public Object getReport() throws ReportException;
}
