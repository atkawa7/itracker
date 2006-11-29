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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.RequestUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.Report;
import org.itracker.services.exceptions.ReportException;
import org.itracker.services.util.ReportUtilities;
import org.itracker.web.util.Constants;


/**
  * This class encapsulates a basic ITracker report based on JasperReports.  It will load and
  * process a JasperReport file.  This class can be extended to perform custom processing
  * through the API if needed.  Usually this can be done through overriding the
  * augmentReport method.
  */
public abstract class AbstractITrackerJasperReport extends AbstractITrackerReport {
    protected JasperReport jasperReport = null;
    protected JasperPrint jasperPrint = null;

    public AbstractITrackerJasperReport() {
    }

    /**
      * Initializes the JasperReport.  This loads the appropriate jasper file, sets the data
      * for the report and sets up the report locale.
      */
    public void initializeReport(List<Issue> issues, Report report, Locale locale, String reportOutput, HttpSession session) throws ReportException {
        super.initializeReport(issues, report, locale, reportOutput, session); 

        JRTableModelDataSource data = null;

        try {
            if(report.getFileData().length == 0) {
                logger.debug("Requested report did not contain a valid report definition.");
                throw new ReportException("Requested report did not contain a valid report definition.", "itracker.web.error.invalidreport");
            }
          ///	jasperReport = JasperManager.loadReport(new ByteArrayInputStream(report.getFileData()));
            jasperReport = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(report.getFileData()));
            
            		
          	Map<String,Object> parameters = new HashMap<String,Object>();
            String reportTitle = report.getName();
            if(report.getNameKey() != null) {
                reportTitle = ITrackerResources.getString(report.getNameKey(), locale);
            }
          	parameters.put("ReportTitle", reportTitle);
          	parameters.put("BaseDir", new File("."));

            if(report.getDataType() == ReportUtilities.DATATYPE_PROJECT) {
                data = new JRTableModelDataSource(new ProjectTableModel(issues, locale));
            } else {
                data = new JRTableModelDataSource(new IssueTableModel(issues, locale));
            }
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, data);
        } catch(JRException jre) {
            logger.debug("Error initializing report.", jre);
            jasperPrint = null;
            jasperReport = null;
        }
    }

    /**
      * This method must be overriden by subclasses to perform any customization that can only
      * be performed through the API.  This method should be called after initializeReport.
      */
    public abstract void augmentReport() throws ReportException;

    /**
      * Outputs the JFreeReport as a PDF.
      * @param out the OutputStream to send the PDF to.
      */
    public void outputPDF(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jasperReport == null || jasperPrint == null) {
                throw new Exception("The JasperReport object has not been initialized.");
            }

            response.setHeader("Content-Type", "application/pdf");
            ServletOutputStream out = response.getOutputStream();

         //   JasperExportManager exportManager = new JasperExportManager();
         //   exportManager.exportReportToPdfStream(jasperPrint, out);
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing PDF report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Outputs the JFreeReport as HTML.
      * @param out the OutputStream to send the HTML to.
      */
    public void outputHTML(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jasperReport == null || jasperPrint == null) {
                throw new Exception("The JasperReport object has not been initialized.");
            }

          	JRHtmlExporter exporter = new JRHtmlExporter();

          	Map imagesMap = new HashMap();
            HttpSession session = request.getSession(true);
          	session.setAttribute(Constants.REPORT_IMAGEMAP_KEY, imagesMap);

            response.setHeader("Content-Disposition", "inline; filename=\"itrackerreport.html\"");
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

          	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
          	exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
          	exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
            logger.debug("Image URL=" + RequestUtils.forwardURL(request, mapping.findForward("imagesurl")));
          	exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, RequestUtils.printableURL(RequestUtils.absoluteURL(request, RequestUtils.forwardURL(request, mapping.findForward("imagesurl")))) + "?image=");

          	exporter.exportReport();

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing HTML report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Outputs the JFreeReport as an Excel spreadsheet.
      * @param out the OutputStream to send the XLS to.
      */
    public void outputXLS(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jasperReport == null || jasperPrint == null) {
                throw new Exception("The JasperReport object has not been initialized.");
            }

          	JRXlsExporter exporter = new JRXlsExporter();

            response.setHeader("Content-Disposition", "inline; filename=\"itrackerreport.xls\"");
            response.setHeader("Content-Type", "application/vnd.ms-excel; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

          	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
          	exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
          	exporter.exportReport();

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing XLS report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Outputs the JFreeReport as a csv file.
      * @param out the OutputStream to send the XLS to.
      */
    public void outputCSV(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jasperReport == null || jasperPrint == null) {
                throw new Exception("The JasperReport object has not been initialized.");
            }

          	JRCsvExporter exporter = new JRCsvExporter();

            response.setHeader("Content-Disposition", "inline; filename=\"itrackerreport.csv\"");
            response.setHeader("Content-Type", "text/csv; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

          	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
          	exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
          	exporter.exportReport();

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing CSV report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Returns the current JasperReport object.
      * @exception ReportException thrown if the report has not been properly initialized
      */
    public Object getReport() throws ReportException {
        if(jasperReport == null) {
            throw new ReportException("The JasperReport object has not been initialized.");
        }

        return jasperReport;
    }
}
