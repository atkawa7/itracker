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

import org.apache.log4j.Logger;
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
import org.itracker.services.ConfigurationService;
import org.itracker.services.ReportService;
import org.itracker.services.util.ReportUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ReportForm;
import org.itracker.web.util.Constants;


public class EditReportAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditReportAction.class);
	
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
            log.debug("Invalid request token while editing report.");
            return mapping.findForward("listreports");
        }
        resetToken(request);
        
        ReportForm reportForm = (ReportForm) form;
        if ( reportForm == null )
             return mapping.findForward("listreportsadmin");
       
        HttpSession session = request.getSession(true);
        Report editreport = null;
        try {
            boolean errorFound = false;
            ReportService reportService = getITrackerServices().getReportService();

            Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
            if(! UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_USER_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            
            editreport = (Report) session.getAttribute(Constants.REPORT_KEY);

            editreport = new Report();
            if( reportForm.getId() != -1 ) {
                editreport.setId(reportForm.getId());
            }
            editreport.setName(reportForm.getName());
            editreport.setNameKey(reportForm.getNameKey());
            editreport.setDescription(reportForm.getDescription());
            editreport.setDataType((reportForm.getDataType() != null ? reportForm.getDataType() : ReportUtilities.DATATYPE_ISSUE));
            editreport.setReportType((reportForm.getReportType() != null ? reportForm.getReportType() : ReportUtilities.REPORTTYPE_JFREE));
            String fileData = reportForm.getFileData();
            try {
                if ( fileData == null || fileData.length() == 0 ) {
                    FormFile file = reportForm.getFileDataFile();  
                    if(file.getFileData() == null ||
                            file.getFileData().length == 0) {
                        errorFound = true;
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingdatafile"));  
                    } else {
                        //editreport.setFileData(file.getFileData());
                    }
                } else {
                    //editreport.setFileData(fileData.getBytes());
                }
            } catch(Exception e) {
                log.error("Exception while verifying import data.", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingdatafile"));
            }

            editreport.setClassName((reportForm.getClassName() != null ? reportForm.getClassName() : ""));
                
            String action = (String) request.getParameter("action");
            if(!errorFound) {
                if("create".equals(action)) {
                    editreport = reportService.createReport(editreport);
                } else if ("update".equals(action)) {
                    Report existingreport = reportService.getReportDAO().findByPrimaryKey(editreport.getId());
                    if ( existingreport != null ) {
                        reportService.getReportDAO().saveOrUpdate(editreport);
                    }
                }
            }

            if(editreport == null && !errorFound) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
            } else if("create".equals(action) && editreport!=null) {
                // If it was a create, add a new language key in the base for it.
                ConfigurationService configurationService = getITrackerServices().getConfigurationService();

                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, editreport.getNameKey(), editreport.getName()));
                ITrackerResources.clearKeyFromBundles(editreport.getNameKey(), true);
            }
        } catch(Exception e) {
            log.error("Exception processing form data", e);
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
  