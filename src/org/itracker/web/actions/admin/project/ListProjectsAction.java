package org.itracker.web.actions.admin.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListProjectsAction extends ItrackerBaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		super.executeAlways(mapping,form,request,response);
		 
		  ProjectService projectService = this.getITrackerServices().getProjectService();
		  request.setAttribute("ph",projectService);
	 
		return mapping.findForward("listprojects");
	}

	public ListProjectsAction() {
		super();
	}

}
