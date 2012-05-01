package org.itracker.web.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class UnauthorizedAction extends ItrackerBaseAction {

	private final static String UNAUTHORIZED = "unauthorized";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		 return mapping.findForward(UNAUTHORIZED);
	}

	public UnauthorizedAction() {
		super();
	 
	}

}
