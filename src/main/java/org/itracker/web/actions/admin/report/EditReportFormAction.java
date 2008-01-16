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
import org.itracker.model.PermissionType;
import org.itracker.model.Report;
import org.itracker.services.ReportService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ReportForm;
import org.itracker.web.util.Constants;


public class EditReportFormAction extends ItrackerBaseAction {

    public EditReportFormAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            ReportService reportService = getITrackerServices().getReportService();

            HttpSession session = request.getSession(true);
            String action = (String) request.getParameter("action");
            Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
            // TODO: never used, therefore commented, task added
            // UserModel user = (UserModel) session.getAttribute(Constants.USER_KEY);
            
            if(! UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_USER_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            Report report = (Report) session.getAttribute(Constants.REPORT_KEY);
            String pageTitleKey = "";
            String pageTitleArg = "";
            if(action != null && action.equals("update")) {
               pageTitleKey = "itracker.web.admin.editreport.title.update";
               // pageTitleArg = report.getId().toString();
            } else {
               pageTitleKey = "itracker.web.admin.editreport.title.create";
            }
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg); 

            report = null;

            ReportForm reportForm = (ReportForm) form;
            if(reportForm == null) {
                reportForm = new ReportForm();
            }

            
            if("create".equals(action)) {
                report = new Report();
                report.setId(new Integer(-1)); 
                reportForm.setAction("create");
                reportForm.setId(report.getId()); 
            } else if ("update".equals(action)) {
                Integer reportId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                report = reportService.getReportDAO().findByPrimaryKey(reportId);
                if(report == null) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                } else {
                    reportForm.setAction("update");
                    reportForm.setId(report.getId());
                    reportForm.setName(report.getName());
                    reportForm.setNameKey(report.getNameKey());
                    reportForm.setDescription(report.getDescription());
                    reportForm.setReportType(new Integer(report.getReportType()));
                    reportForm.setDataType(new Integer(report.getDataType()));
                    reportForm.setClassName(report.getClassName());
                    //reportForm.setFileData(new String((byte[]) report.getFileData()));
                }
            } else {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if(errors.isEmpty()) {
                request.setAttribute("reportForm", reportForm);
                session.setAttribute(Constants.REPORT_KEY, report);
                saveToken(request);
                return mapping.getInputForward();
            }
        } catch(Exception e) {
            logger.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  