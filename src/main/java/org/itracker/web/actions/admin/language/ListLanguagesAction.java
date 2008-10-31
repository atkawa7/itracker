package org.itracker.web.actions.admin.language;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;

public class ListLanguagesAction extends ItrackerBaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// super.executeAlways(mapping,form,request,response);
		ConfigurationService configurationService = this.getITrackerServices()
				.getConfigurationService();
		request.setAttribute("sc", configurationService);
		return mapping.findForward("listlanguages");

	}
}
