package org.itracker.web.actions.admin.scheduler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListTasksAction extends ItrackerBaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
//		super.executeAlways(mapping,form,request,response);
		 
		  ProjectService projectService = this.getITrackerServices().getProjectService();
		  request.setAttribute("ph",projectService);
		  
		  String pageTitleKey = "itracker.web.admin.listtasks.title"; 
		  String pageTitleArg = "";			
		  request.setAttribute("pageTitleKey",pageTitleKey); 
		  request.setAttribute("pageTitleArg",pageTitleArg); 
	 
		return mapping.findForward("listtasks");
	}

	public ListTasksAction() {
		super();
	}

}
