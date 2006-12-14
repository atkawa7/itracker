package org.itracker.web.actions.admin.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListWorkflowScriptAction extends ItrackerBaseAction {

	public ListWorkflowScriptAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			super.executeAlways(mapping,form,request,response);
			ConfigurationService configurationService = this.getITrackerServices().getConfigurationService();
			request.setAttribute("sc",configurationService);
			
			String pageTitleKey = "itracker.web.admin.listworkflow.title"; 
			String pageTitleArg = "";			
			request.setAttribute("pageTitleKey",pageTitleKey); 
			request.setAttribute("pageTitleArg",pageTitleArg); 
			
			return mapping.findForward("listworkflowscripts");
		 
	}
}
