package org.itracker.web.actions.admin.report;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
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
	private static final Logger log = Logger.getLogger(EditReportFormAction.class);
	
    public EditReportFormAction() {
    }

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionMessages errors = new ActionMessages();
		
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
                report.setId(-1); 
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
                    reportForm.setReportType(report.getReportType());
                    reportForm.setDataType(report.getDataType());
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
        } catch(RuntimeException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (IllegalAccessException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
		} catch (InvocationTargetException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
		} catch (NoSuchMethodException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
		}

        if(! errors.isEmpty()) {
        	saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }

}
  