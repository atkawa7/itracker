package org.itracker.web.actions.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ViewIssueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(ViewIssueAction.class);
	
	 
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
//		
//			super.executeAlways(mapping,form,request,response);
//			
			IssueService issueService = this.getITrackerServices().getIssueService();	
			request.setAttribute("ih",issueService);
			
			
			String pageTitleKey = "itracker.web.viewissue.title"; 
			String pageTitleArg = request.getParameter("id");
			request.setAttribute("pageTitleKey",pageTitleKey); 
			request.setAttribute("pageTitleArg",pageTitleArg); 
			
			EditIssueFormAction.setupNotificationsInRequest(request, issueService.getIssue(Integer.valueOf(request.getParameter("id"))), getITrackerServices().getNotificationService());
			
			log.info("ViewIssueAction: Forward: viewissue");			
			return mapping.findForward("viewissue");
	 
	}

}
