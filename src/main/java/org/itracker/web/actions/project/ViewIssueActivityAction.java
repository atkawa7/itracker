package org.itracker.web.actions.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ViewIssueActivityAction extends ItrackerBaseAction {
    
    public ViewIssueActivityAction() {
        super();
    }
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.executeAlways(mapping,form,request,response);
        IssueService issueService = this.getITrackerServices().getIssueService();
        
        request.setAttribute("ih",issueService);
        
        return mapping.findForward("viewissueactivity");
        
    }
    
}
