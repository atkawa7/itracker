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

package org.itracker.services.implementations;


import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.itracker.model.Report;
import org.itracker.persistence.dao.ReportDAO;
import org.itracker.services.ReportService;

public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;
    
    public ReportServiceImpl(ReportDAO reportDAO) {
    	this.reportDAO=reportDAO;
    }

    public Report getReport(Integer id) {
        Report report = reportDAO.findByPrimaryKey(id);
        return report;
    }

    public byte[] getReportFile(Integer reportId) {
        byte[] data = new byte[0];

        Report report = reportDAO.findByPrimaryKey(reportId);
        data = report.getFileData();
        if (data == null) {
            data = new byte[0];
        }
        return data;
    }

    public List<Report> getAllReports() {
        List<Report> reports = reportDAO.findAll();
        return reports;
    }

    public int getNumberReports() {
        // TODO: use a SELECT COUNT(*)!
    	Collection<Report> reports = reportDAO.findAll();
        return reports.size();
    }

    public Report createReport(Report report) {
        report.setCreateDate(new Date());
        reportDAO.save(report);
        return report;
    }

    public Report updateReport(Report report) {
        report.setCreateDate(new Date(report.getCreateDate().getTime()));
        reportDAO.saveOrUpdate(report);
        return report;
    }

    public boolean removeReport(Integer reportId) {
        Report report = reportDAO.findByPrimaryKey(reportId);
        
        if(report == null) {
            return false;
        }
        reportDAO.delete(report);
        return true;
    }

}
