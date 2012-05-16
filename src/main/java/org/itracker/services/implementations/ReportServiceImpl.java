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

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JRViewer;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.RequestUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.Report;
import org.itracker.persistence.dao.ReportDAO;
import org.itracker.services.ReportService;
import org.itracker.services.exceptions.ReportException;
import org.itracker.web.util.Constants;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;

    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    // kill this
    public List<Report> getAllReports() {
        return reportDAO.findAll();
    }

    // kill this
    public int getNumberReports() {
        return reportDAO.findAll().size();
    }

    // kill this
    public Report createReport(Report report) {
//		report.setCreateDate(new Date());
//		report.setLastModifiedDate(report.getCreateDate());
        reportDAO.save(report);
        return report;
    }

    private JasperPrint generateReport(Report report,
                                       Map<String, Object> parameters, Object datasource) {
        try {

            JasperReport jasperReport = getJasperReport(report);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, (JRDataSource) datasource);
            return jasperPrint;
        } catch (JRException e) {
            throw new ReportException(e);
        }

    }

    private JasperReport getJasperReport(Report report) throws JRException {
        return (JasperReport) JRLoader.loadObject(report.getReportDefinition());
    }

    public ReportDAO getReportDAO() {
        return reportDAO;
    }

    /**
     * break this into stuff that belongs here and stuff that belongs in the web
     * layer
     *
     * @deprecated needs to be moved to static method in a new web-report-utility class
     */
    public void outputPDF(List<Issue> reportDataArray, Report report,
                          Locale userLocale, String reportOutput, HttpSession session,
                          HttpServletRequest request, HttpServletResponse response,
                          ActionMapping mapping) {

        try {
            // hack, we have to find a more structured way to support
            // various types of queries
            JRBeanCollectionDataSource beanCollectionDataSource;
            // JasperReport jr = getJasperReport(report);

            // TODO: Temporarily commenting out code to remove pnuts dependency
            // Will need to test for regressions.
            // if(jr.getQuery() != null) {
            // List objects = (List)Pnuts.eval(jr.getQuery().getText(), new
            // Context());
            // beanCollectionDataSource = new
            // JRBeanCollectionDataSource(objects);
            // } else {
            beanCollectionDataSource = new JRBeanCollectionDataSource(
                    reportDataArray);
            // }

            Map<String, Object> parameters = new HashMap<String, Object>();
            String reportTitle = report.getName();
            if (report.getNameKey() != null) {
                reportTitle = ITrackerResources.getString(report.getNameKey());
            }
            parameters.put("ReportTitle", reportTitle);
            parameters.put("BaseDir", new File("."));
            JasperPrint jasperPrint = generateReport(report, parameters,
                    beanCollectionDataSource);

            // test
            JRViewer jrViewer = new JRViewer(jasperPrint);
            JFrame frame = new JFrame();
            frame.add(jrViewer);
            // frame.show();
            // test
            response.setHeader("Content-Type", "application/pdf");
            ServletOutputStream out = response.getOutputStream();

            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            out.flush();
            out.close();
        } catch (JRException e) {
            // logger.debug("Error outputing PDF report.", e);
            throw new ReportException(e);
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }

    /**
     * @deprecated needs to be moved to static method in a new web-report-utility class
     */
    public void outputCSV(HttpServletRequest request,
                          HttpServletResponse response, ActionMapping mapping)
            throws ReportException {
        try {
            /*
                * if(jasperReport == null || jasperPrint == null) { throw new
                * Exception("The JasperReport object has not been initialized."); }
                */

            JRCsvExporter exporter = new JRCsvExporter();

            response.setHeader("Content-Disposition",
                    "inline; filename=\"itrackerreport.csv\"");
            response.setHeader("Content-Type", "text/csv; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, null);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            exporter.exportReport();

            out.flush();
            out.close();
        } catch (Exception e) {
            // logger.debug("Error outputing CSV report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
     * Outputs the JFreeReport as an Excel spreadsheet.
     *
     * @param out the OutputStream to send the XLS to.
     * @deprecated needs to be moved to static method in a new web-report-utility class
     */
    public void outputXLS(HttpServletRequest request,
                          HttpServletResponse response, ActionMapping mapping)
            throws ReportException {
        try {
            /*
                * if(jasperReport == null || jasperPrint == null) { throw new
                * Exception("The JasperReport object has not been initialized."); }
                */

            JRXlsExporter exporter = new JRXlsExporter();

            response.setHeader("Content-Disposition",
                    "inline; filename=\"itrackerreport.xls\"");
            response.setHeader("Content-Type",
                    "application/vnd.ms-excel; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, null);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            exporter.exportReport();

            out.flush();
            out.close();
        } catch (Exception e) {
            // logger.debug("Error outputing XLS report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
     * Outputs the JFreeReport as HTML.
     *
     * @param out the OutputStream to send the HTML to.
     * @deprecated needs to be moved to static method in a new web-report-utility class
     */
    public void outputHTML(HttpServletRequest request,
                           HttpServletResponse response, ActionMapping mapping)
            throws ReportException {
        try {
            /*
                * if(jasperReport == null || jasperPrint == null) { throw new
                * Exception("The JasperReport object has not been initialized."); }
                */

            JRHtmlExporter exporter = new JRHtmlExporter();

            Map<Object, Object> imagesMap = new HashMap<Object, Object>();
            HttpSession session = request.getSession(true);
            session.setAttribute(Constants.REPORT_IMAGEMAP_KEY, imagesMap);

            response.setHeader("Content-Disposition",
                    "inline; filename=\"itrackerreport.html\"");
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, null);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            exporter
                    .setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
            // logger.debug("Image URL=" + RequestUtils.forwardURL(request,
            // mapping.findForward("imagesurl")));
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,
                    RequestUtils.printableURL(RequestUtils.absoluteURL(request,
                            RequestUtils.forwardURL(request, mapping
                                    .findForward("imagesurl"))))
                            + "?image=");

            exporter.exportReport();

            out.flush();
            out.close();
        } catch (Exception e) {
            // logger.debug("Error outputing HTML report.", e);
            throw new ReportException(e.getMessage());
        }
    }

}
