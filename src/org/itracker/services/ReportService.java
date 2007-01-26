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

package org.itracker.services;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.itracker.model.Issue;
import org.itracker.model.Report;
import org.itracker.persistence.dao.ReportDAO;

public interface ReportService {
       
    public List<Report> getAllReports() throws Exception;
    
    public int getNumberReports()throws Exception;

    public Report createReport(Report report);
    
    public ReportDAO getReportDAO();

	public void outputPDF(List<Issue> reportDataArray, Report reportModel,
			Locale userLocale, String reportOutput, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			ActionMapping mapping);
    
}