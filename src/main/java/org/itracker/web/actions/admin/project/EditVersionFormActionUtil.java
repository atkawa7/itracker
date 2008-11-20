package org.itracker.web.actions.admin.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Version;
import org.itracker.web.util.Constants;

public class EditVersionFormActionUtil {
	private static final Logger log = Logger
			.getLogger(EditVersionFormActionUtil.class);

	public ActionForward init(ActionMapping mapping, HttpServletRequest request) {

		if (log.isDebugEnabled()) {
			log.debug("init: Getting version from session");
		}
		final Version version = (Version) request.getSession().getAttribute(
				Constants.VERSION_KEY);
		if (log.isDebugEnabled()) {
			log.debug("init: Checking if version is new");
		}
		final boolean isNew = version.isNew();
		if (log.isDebugEnabled()) {
			log.debug("init: Putting the isNew=" + isNew + ", component :"
					+ version.getDescription()
					+ " and isNew attribute back into the request");
		}
		request.setAttribute("version", version);
		request.setAttribute("isNew", isNew);
		return null;
	}
}
