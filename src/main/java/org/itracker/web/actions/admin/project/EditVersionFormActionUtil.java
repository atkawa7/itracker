package org.itracker.web.actions.admin.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Version;
import org.itracker.web.util.Constants;

public class EditVersionFormActionUtil {
	private static final Logger log = Logger.getLogger(EditVersionFormActionUtil.class);

	public ActionForward init(ActionMapping mapping, HttpServletRequest request){
		final Version version = (Version)request.getSession().getAttribute(Constants.VERSION_KEY);
		final boolean isNew = version.isNew();
		request.setAttribute("version", version);
		request.setAttribute("isNew", isNew);
		return null;
	}
}
