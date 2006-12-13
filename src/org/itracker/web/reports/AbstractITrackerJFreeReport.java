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
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.Report;
import org.itracker.services.exceptions.ReportException;
import org.itracker.services.util.ReportUtilities;
import org.jfree.report.JFreeReport;
import org.jfree.report.modules.output.csv.CSVProcessor;
import org.jfree.report.modules.output.pageable.base.PageableReportProcessor;
import org.jfree.report.modules.output.pageable.pdf.PDFOutputTarget;
import org.jfree.report.modules.output.table.html.HtmlProcessor;
import org.jfree.report.modules.output.table.html.StreamHtmlFilesystem;
import org.jfree.report.modules.output.table.xls.ExcelProcessor;
import org.jfree.report.modules.parser.base.ReportGenerator;
import org.jfree.report.util.ReportConfiguration;
import org.xml.sax.InputSource;


/**
  * This class encapsulates a basic ITracker report based on JFreeReport.  It will load
  * and process a JFreeReport xml file.  This class can be extended to perform custom
  * processing through the API if needed.  Usually this can be done through overriding the
  * augmentReport method.
  */
public abstract class AbstractITrackerJFreeReport extends AbstractITrackerReport {
    protected JFreeReport jreport = null;

    public AbstractITrackerJFreeReport() {
    }

    /**
      * Initializes the JFreeReport.  This loads the appropriate xml file, sets the data
      * for the report and setups up the report locale.
      */
    public void initializeReport(List<Issue> issues, Report report, Locale locale, String reportOutput, HttpSession session) throws ReportException{
        super.initializeReport(issues, report, locale, reportOutput, session);

        ReportTableModel data = null;

        try {
            if(report.getFileData().length == 0) {
                logger.debug("Requested report did not contain a valid report definition.");
                throw new ReportException("Requested report did not contain a valid report definition.", "itracker.web.error.invalidreport");
            }

            ReportGenerator generator = ReportGenerator.getInstance();
            jreport = generator.parseReport(new InputSource(new ByteArrayInputStream(report.getFileData())), new File(".").toURL());

            if(report.getDataType() == ReportUtilities.DATATYPE_PROJECT) {
                data = new ProjectTableModel(issues, locale);
            } else {
                data = new IssueTableModel(issues, locale);
            }
            jreport.setData(data);

            // Set some configuration properties
            ReportConfiguration config = (ReportConfiguration)jreport.getReportConfiguration();

            config.setConfigProperty("com.jrefinery.report.targets.locale", locale.toString());

            String reportTitle = report.getName();
            if(report.getNameKey() != null) {
                reportTitle = ITrackerResources.getString(report.getNameKey(), locale);
            }
            config.setConfigProperty("com.jrefinery.report.targets.pageable.pdf.Title", reportTitle);
            config.setConfigProperty("com.jrefinery.report.targets.table.html.Title", reportTitle);

        } catch(Exception e) {
            logger.debug("Error initializing report.", e);
            jreport = null;
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * This method must be overriden by subclasses to perform any customization that can only
      * be performed through the API.  This method should be called after initalizeReport.
      */
    public abstract void augmentReport();

    /**
      * Outputs the JFreeReport as a PDF.
      * @param out the OutputStream to send the PDF to.
      */
    public void outputPDF(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jreport == null) {
                throw new Exception("The JFreeReport object has not been initialized.");
            }

            response.setHeader("Content-Type", "application/pdf");
            ServletOutputStream out = response.getOutputStream();

            final PDFOutputTarget target = new PDFOutputTarget(out);
            final PageableReportProcessor processor = new PageableReportProcessor(jreport);
            processor.setOutputTarget(target);
            target.open();
            processor.processReport();
            target.close();

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
            if(jreport == null) {
                throw new Exception("The JFreeReport object has not been initialized.");
            }

            response.setHeader("Content-Disposition", "inline; filename=\"itrackerreport.html\"");
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

            final HtmlProcessor processor = new HtmlProcessor(jreport);
            processor.setFilesystem(new StreamHtmlFilesystem(out));
            processor.processReport();

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing PDF report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Outputs the JFreeReport as an Excel Spreadsheet (XLS).
      * @param out the OutputStream to send the XLS to.
      */
    public void outputXLS(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jreport == null) {
                throw new Exception("The JFreeReport object has not been initialized.");
            }

            response.setHeader("Content-Disposition", "inline; filename=\"itrackerreport.xls\"");
            response.setHeader("Content-Type", "application/vnd.ms-excel; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

            final ExcelProcessor processor = new ExcelProcessor(jreport);
            processor.setOutputStream(response.getOutputStream());
            processor.processReport();

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing XLS report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Outputs the JFreeReport as a CSV file.
      * @param out the OutputStream to send the CSV to.
      */
    public void outputCSV(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ReportException {
        try {
            if(jreport == null) {
                throw new Exception("The JFreeReport object has not been initialized.");
            }

            response.setHeader("Content-Disposition", "inline; filename=\"itrackerreport.csv\"");
            response.setHeader("Content-Type", "text/csv; charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();

            final CSVProcessor processor = new CSVProcessor(jreport);
            processor.setWriter(new OutputStreamWriter(response.getOutputStream()));
            processor.processReport();

            out.flush();
            out.close();
        } catch(Exception e) {
            logger.debug("Error outputing CSV report.", e);
            throw new ReportException(e.getMessage());
        }
    }

    /**
      * Returns the current JFreeReport object.
      * @exception ReportException thrown if the report has not been properly initialized
      */
    public Object getReport() throws ReportException {
        if(jreport == null) {
            throw new ReportException("The JFreeReport object has not been initialized.");
        }

        return jreport;
    }

}
