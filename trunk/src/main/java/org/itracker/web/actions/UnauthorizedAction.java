package org.itracker.web.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class UnauthorizedAction extends ItrackerBaseAction {

	/* forward name="unauthorized" path="null" */
	private final static String UNAUTHORIZED = "unauthorized";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
//		 super.executeAlways(mapping,form,request,response);
		// TODO process request and return an ActionForward instance, for example:
		// return mapping.findForward(UNAUTHORIZED);
		// return null;
		 return mapping.findForward(UNAUTHORIZED);
	}

	public UnauthorizedAction() {
		super();
		// TODO Auto-generated constructor stub
	}

}
