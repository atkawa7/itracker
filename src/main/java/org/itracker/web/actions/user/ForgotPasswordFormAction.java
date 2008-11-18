package org.itracker.web.actions.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ForgotPasswordForm;

public class ForgotPasswordFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(ForgotPasswordFormAction.class);


	public ActionForward execute(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {
		ActionMessages errors = new ActionMessages();

		try {
			ForgotPasswordForm forgotPasswordForm = (ForgotPasswordForm) form;
      if (forgotPasswordForm == null) {
      	forgotPasswordForm = new ForgotPasswordForm();
      }
      if (errors.isEmpty()) {
        request.setAttribute("forgotPasswordForm", forgotPasswordForm);
        saveToken(request);
        return mapping.getInputForward();
    }
		} catch (Exception e) {
			log.error("Exception while creating forgot password form.", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}

		return mapping.findForward("error");

	}

}
