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

package org.itracker.web.actions.admin.report;

import java.io.IOException;
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
import org.apache.struts.upload.FormFile;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Language;
import org.itracker.model.PermissionType;
import org.itracker.model.Report;
import org.itracker.services.ReportService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.ReportUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class EditReportAction extends ItrackerBaseAction {

    public EditReportAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing report.");
            return mapping.findForward("listreports");
        }
        resetToken(request);

        HttpSession session = request.getSession(true);
        Report report = null;
        try {
            boolean errorFound = false;
            ReportService reportService = getITrackerServices().getReportService();

            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            if(! UserUtilities.hasPermission(userPermissions, UserUtilities.PERMISSION_USER_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            report = (Report) session.getAttribute(Constants.REPORT_KEY);

            report = new Report();
            Integer reportId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            report = reportService.getReport(reportId);
//            report.setId((Integer) PropertyUtils.getSimpleProperty(form, "id"));
            report.setName((String) PropertyUtils.getSimpleProperty(form, "name"));
            report.setNameKey((String) PropertyUtils.getSimpleProperty(form, "nameKey"));
            report.setDescription((String) PropertyUtils.getSimpleProperty(form, "description"));
            report.setDataType(((Integer) PropertyUtils.getSimpleProperty(form, "dataType") != null ? ((Integer) PropertyUtils.getSimpleProperty(form, "dataType")).intValue() : ReportUtilities.DATATYPE_ISSUE));
            report.setReportType(((Integer) PropertyUtils.getSimpleProperty(form, "reportType") != null ? ((Integer) PropertyUtils.getSimpleProperty(form, "reportType")).intValue() : ReportUtilities.REPORTTYPE_JFREE));
            String fileData = (String) PropertyUtils.getSimpleProperty(form, "fileData");
            try {
                if ( fileData == null || fileData.length() == 0 ) {
                    FormFile file = (FormFile) PropertyUtils.getSimpleProperty(form, "fileDataFile");  
                    if(file.getFileData() == null ||
                            file.getFileData().length == 0) {
                        errorFound = true;
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingdatafile"));  
                    } else {
                        report.setFileData(file.getFileData());
                    }
                } else {
                    report.setFileData(fileData.getBytes());
                }
            } catch(Exception e) {
                logger.error("Exception while verifying import data.", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingdatafile"));
            }

            String className = (String) PropertyUtils.getSimpleProperty(form, "className");
            if(className == null || className.equals("")) {
                report.setClassName("");
            } else {
                report.setClassName((String) PropertyUtils.getSimpleProperty(form, "className"));
            }
                
            String action = (String) request.getParameter("action");
            if(!errorFound) {
                if("create".equals(action)) {
                    report = reportService.createReport(report);
                } else if ("update".equals(action)) {
                    report = reportService.updateReport(report);
                }
            }

            if(report == null && !errorFound) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
            } else if("create".equals(action)) {
                // If it was a create, add a new language key in the base for it.
                ConfigurationService configurationService = getITrackerServices().getConfigurationService();

                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, report.getNameKey(), report.getName()));
                ITrackerResources.clearKeyFromBundles(report.getNameKey(), true);
            }
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
        }

        if(! errors.isEmpty()) {
      	    saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        session.removeAttribute(Constants.REPORT_KEY);
        return mapping.findForward("listreportsadmin");
    }

}
  