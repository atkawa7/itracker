package org.itracker.web.actions.admin.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListUsersAction extends ItrackerBaseAction {

	public ListUsersAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
//			super.executeAlways(mapping,form,request,response);
			UserService userService = this.getITrackerServices().getUserService();
			request.setAttribute("uh",userService);
			 
			String pageTitleKey = "itracker.web.admin.listusers.title"; 
		    String pageTitleArg = "";			
		    request.setAttribute("pageTitleKey",pageTitleKey); 
		    request.setAttribute("pageTitleArg",pageTitleArg); 
		    
		    return mapping.findForward("listusers");		 
	}
}
