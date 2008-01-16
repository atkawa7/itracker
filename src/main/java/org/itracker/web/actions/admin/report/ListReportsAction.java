package org.itracker.web.actions.admin.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Report;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListReportsAction extends ItrackerBaseAction {
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.executeAlways(mapping,form,request,response);
        
        ProjectService projectService = this.getITrackerServices().getProjectService();
        ReportService reportService = this.getITrackerServices().getReportService();
        request.setAttribute("ph",projectService);
        request.setAttribute("rh",reportService);
        
        String pageTitleKey = "itracker.web.admin.listreports.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
        List<Report> reports = reportService.getAllReports();
        request.setAttribute("reports",reports);
        //  String dataTypeString = "";
        // String reportTypeString = "";
        
        //  if(reports.get(i).getReportType() == ReportUtilities.REPORTTYPE_JFREE) {
        //        reportTypeString = "JFreeReport";
        //	  } else if(reports.get(i).getReportType() == ReportUtilities.REPORTTYPE_JASPER) {
        //        reportTypeString = "JasperReport";
        //		  } else {
        //        reportTypeString = ITrackerResources.getString("itracker.web.generic.unknown", (java.util.Locale)pageContext.getAttribute("currLocale"));
        //  }
        
        //	  if(reports.get(i).getDataType() == ReportUtilities.DATATYPE_ISSUE) {
        //        dataTypeString = ITrackerResources.getString("itracker.web.attr.issues", (java.util.Locale)pageContext.getAttribute("currLocale"));
        //  } else if(reports.get(i).getDataType() == ReportUtilities.DATATYPE_PROJECT) {
        //       dataTypeString = ITrackerResources.getString("itracker.web.attr.projects", (java.util.Locale)pageContext.getAttribute("currLocale"));
        // } else {
        //     dataTypeString = ITrackerResources.getString("itracker.web.generic.unknown", (java.util.Locale)pageContext.getAttribute("currLocale"));
        // }
        
        return mapping.findForward("listreportsadmin");
    }
    
    public ListReportsAction() {
        super();
    }
    
}
