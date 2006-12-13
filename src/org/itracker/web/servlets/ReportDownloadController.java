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

package org.itracker.web.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.itracker.model.Report;
import org.itracker.services.ReportService;
 

public class ReportDownloadController extends GenericController {
 
    public ReportDownloadController() {
    }

    public void init(ServletConfig config) {
 
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(! isLoggedInWithRedirect(request, response)) {
            return;
        }
        
        // TODO: the 2-3 lines are most propably not used; commented, task added
        // Commented more not used lines, but added a task for testing. 
        // HttpSession session = request.getSession();
        // UserModel user = (UserModel) session.getAttribute("user");
        // User user = (session == null ? null : (User) session.getAttribute(Constants.USER_KEY));

        try {
            ReportService reportService = getITrackerServices(getServletContext()).getReportService();

            Integer reportId = null;
            Report report = null;

            try {
                reportId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
                report = reportService.getReport(reportId);
            } catch(NumberFormatException nfe) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid reportId " + request.getParameter("id") + " specified.");
                }
            }

            if(report == null) {
                forward("/error.jsp", request, response);
                return;
            }

            response.setHeader("Content-Disposition", "attachment; filename=report" + report.getId() + "\"");
            ServletOutputStream out = response.getOutputStream();
            out.write(reportService.getReportFile(reportId));
            out.close();
        } catch(IOException ioe) {
            logger.info("Unable to display report.", ioe);
        }

        return;
    }
}   