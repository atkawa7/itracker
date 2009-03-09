/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

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
import org.itracker.model.Notification;
import org.itracker.model.User;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;

public class SelfRegisterAction extends ItrackerBaseAction {
	private static final Logger log = Logger
			.getLogger(SelfRegisterAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ActionMessages errors = new ActionMessages();

		resetToken(request);

		try {
//			itracker.web.selfreg.title

//			String pageTitleKey = "itracker.web.selfreg.title";
//			String pageTitleArg = "";
//			request.setAttribute("pageTitleKey", pageTitleKey);
//			request.setAttribute("pageTitleArg", pageTitleArg);
//			
			ConfigurationService configurationService = getITrackerServices()
					.getConfigurationService();

			boolean allowSelfRegister = configurationService
					.getBooleanProperty("allow_self_register", false);

			if (!allowSelfRegister) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.notenabled"));
			} else {
				UserForm regForm = (UserForm) form;

				User user = new User(regForm.getLogin(), UserUtilities
						.encryptPassword(regForm.getPassword()), regForm
						.getFirstName(), regForm.getLastName(), regForm
						.getEmail(), UserUtilities.REGISTRATION_TYPE_SELF,
						false);

				if (!user.hasRequiredData()) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage(
									"itracker.web.error.missingfields"));
				} else {
					UserService userService = getITrackerServices()
							.getUserService();

					try {
						if (userService
								.allowRegistration(
										user,
										regForm.getPassword(),
										AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
										AuthenticationConstants.REQ_SOURCE_WEB)) {
							user = userService.createUser(user);
							Notification notification = new Notification();
							notification.setUser(user);
							notification.setRole(Role.ANY);
							getITrackerServices().getNotificationService()
									.sendNotification(notification,
											Type.SELF_REGISTER,
											getBaseURL(request));
						} else {
							errors
									.add(
											ActionMessages.GLOBAL_MESSAGE,
											new ActionMessage(
													"itracker.web.error.register.unauthorized"));
						}
					} catch (UserException ue) {
						errors.add(ActionMessages.GLOBAL_MESSAGE,
								new ActionMessage(
										"itracker.web.error.existinglogin"));
					}
				}
			}
		} catch (Exception e) {
			log.info("Error during self registration.  " + e.getMessage());
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.register.system"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return mapping.getInputForward();
		}

		return mapping.findForward("login");
	}

}
