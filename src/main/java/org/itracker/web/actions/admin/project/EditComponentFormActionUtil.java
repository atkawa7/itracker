package org.itracker.web.actions.admin.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Component;
import org.itracker.web.util.Constants;

public class EditComponentFormActionUtil {
	private static final Logger log = Logger.getLogger(EditComponentFormActionUtil.class);

	public ActionForward init(ActionMapping mapping, HttpServletRequest request){
		final Component component = (Component)request.getSession().getAttribute(Constants.COMPONENT_KEY);
		final boolean isNew = component.isNew();
		request.setAttribute("component", component);
		request.setAttribute("isNew", isNew);
		return null;
	}

}
