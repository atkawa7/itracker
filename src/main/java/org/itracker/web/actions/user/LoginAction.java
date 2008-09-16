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
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.filters.ExecuteAlwaysFilter;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.SessionManager;
import org.itracker.web.util.SessionTracker;

public class LoginAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(LoginAction.class);

	// TODO: this should live in ConfigurationService. [Whoever wrote this,
	// could you please explain why?]

	private int SESSION_TIMEOUT = 30;


	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ActionErrors errors = new ActionErrors();
//		super.executeAlways(mapping, form, request, response);
		ActionForward errorMapping = null;
		String login = null;

		String pageTitleKey = "itracker.web.login.title";
		String pageTitleArg = "";
		request.setAttribute("pageTitleKey", pageTitleKey);
		request.setAttribute("pageTitleArg", pageTitleArg);

		try {
			UserService userService = getITrackerServices().getUserService();

			try {
				User user = null;
				String encPassword = null;

				int authType = LoginUtilities.getRequestAuthType(request);

				if (authType == AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN) {
					login = (String) request
							.getAttribute(Constants.AUTH_LOGIN_KEY);
					String authenticator = (String) request
							.getAttribute(Constants.AUTH_VALUE_KEY);
					if (login == null || login.equals("")) {
						login = (String) PropertyUtils.getSimpleProperty(form,
								"login");
					}
					if (authenticator == null || authenticator.equals("")) {
						authenticator = (String) PropertyUtils
								.getSimpleProperty(form, "password");
					}
					encPassword = UserUtilities.encryptPassword(authenticator);

					log
							.debug("Attempting login with plaintext password for user "
									+ login);
					user = userService.checkLogin(login, authenticator,
							AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
							AuthenticationConstants.REQ_SOURCE_WEB);
				} else if (authType == AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC) {
					login = (String) request
							.getAttribute(Constants.AUTH_LOGIN_KEY);
					String authenticator = (String) request
							.getAttribute(Constants.AUTH_VALUE_KEY);
					if (login == null || login.equals("")) {
						login = (String) PropertyUtils.getSimpleProperty(form,
								"login");
					}
					if (authenticator == null || authenticator.equals("")) {
						authenticator = (String) PropertyUtils
								.getSimpleProperty(form, "encpassword");
					}
					encPassword = authenticator;

					log
							.debug("Attempting login with encrypted password for user "
									+ login);
					user = userService.checkLogin(login, authenticator,
							AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC,
							AuthenticationConstants.REQ_SOURCE_WEB);
				} else if (authType == AuthenticationConstants.AUTH_TYPE_REQUEST) {
					log.debug("Attempting login with request object");
					user = userService.checkLogin(login, request,
							AuthenticationConstants.AUTH_TYPE_REQUEST,
							AuthenticationConstants.REQ_SOURCE_WEB);
				} else {
					log.debug("Attempting login with with unknown auth type");
					user = userService.checkLogin(login, request,
							AuthenticationConstants.AUTH_TYPE_UNKNOWN,
							AuthenticationConstants.REQ_SOURCE_WEB);
				}

				if (user == null) {
					throw new AuthenticatorException(
							AuthenticatorException.UNKNOWN_USER);
				}
				login = user.getLogin();

				setupSession(user, encPassword, request, response);
				SessionManager.createSession(user.getLogin());
				
				try {
					return ExecuteAlwaysFilter.forwardToOnLoginSuccess(request,
							response);
				} catch (Exception e) {
					log.error("execute: failed to redirect.", e);
				}

				// TODO: Deprecated code. Can we get rid of it? Why is it so complicated??
				// We only want to iknow if success or no success
 
				String redirect = request
						.getParameter(Constants.AUTH_REDIRECT_KEY);

				log.debug("Redirect URL from request param = " + redirect);
				if (redirect == null || "".equals(redirect)) {
					redirect = (String) request
							.getAttribute(Constants.AUTH_REDIRECT_KEY);
					log.debug("Redirect URL from request attribute = "
							+ redirect);
				}
				int redirectIndex = (redirect == null ? -1 : redirect
						.indexOf("?" + Constants.AUTH_REDIRECT_KEY + "="));
				if (redirectIndex > -1) {
					int extraParamIndex = redirect.indexOf("&", redirectIndex);
					int lastParamIndex = redirect.lastIndexOf("&",
							redirectIndex);
					if (log.isDebugEnabled()) {
						log.debug("Original redirect URL = " + redirect);
						log.debug("Redirect Index: " + redirectIndex
								+ "  ExtraParamIndex: " + extraParamIndex
								+ "  LastParamIndex: " + lastParamIndex);
					}
					if (extraParamIndex > -1 && lastParamIndex > -1) {
						redirect = redirect.substring(0, redirectIndex)
								+ "?"
								+ redirect.substring(extraParamIndex + 1,
										lastParamIndex);
					} else if (extraParamIndex > -1) {
						redirect = redirect.substring(0, redirectIndex) + "?"
								+ redirect.substring(extraParamIndex + 1);
					} else {
						redirect = redirect.substring(0, redirectIndex);
					}
				}
				log.info("User " + (user != null ? user.getLogin() : "UNKNOWN")
						+ " logged in successfully.");
				if (redirect == null || "".equals(redirect)) {
					log.info("Action is trying to forward to Forward index");
					return mapping.findForward("index");
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Redirecting to " + redirect);
					}
					return new ActionForward(redirect, true);
				}
			} catch (IllegalStateException ise) {
				if (log.isDebugEnabled()) {
					log
							.debug(
									"IllegalStateException caught during login.",
									ise);
				}
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.login.system"));
			} catch (AuthenticatorException le) {
				if (log.isDebugEnabled()) {
					log.debug("Login Exception for user "
							+ (login != null ? login : "UNKNOWN") + ". Type = "
							+ le.getType(), le);
				}
				// TODO: no information about login-credentials should be shown
				if (le.getType() == AuthenticatorException.INVALID_PASSWORD) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("itracker.web.error.login.unknown")
//									"itracker.web.error.login.badpass")
					);
				} else if (le.getType() == AuthenticatorException.INACTIVE_ACCOUNT) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage(
									"itracker.web.error.login.inactive"));
				} else if (le.getType() == AuthenticatorException.UNKNOWN_USER) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage(
									"itracker.web.error.login.unknown"));
				} else if (le.getType() == AuthenticatorException.INVALID_AUTHENTICATION_TYPE) {
					errors
							.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionMessage(
											"itracker.web.error.login.system"));
				} else if (le.getType() == AuthenticatorException.CUSTOM_ERROR) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage(le.getMessageKey()));
				} else {
					errors
							.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionMessage(
											"itracker.web.error.login.system"));
				}

				if (le.getErrorPageType() == AuthenticatorException.ERRORPAGE_TYPE_FORWARD) {
					errorMapping = mapping.findForward(le.getErrorPageValue());
				} else if (le.getErrorPageType() == AuthenticatorException.ERRORPAGE_TYPE_URL) {
					errorMapping = new ActionForward(le.getErrorPageValue());
				}
			}
			log.info("User " + (login != null ? login : "UNKNOWN")
					+ " login unsucessful.");
		} catch (Exception e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.login.system"));
			log.error("System Error.", e);
		}
		if (!errors.isEmpty()) {
			LogoffAction logoff = new LogoffAction();
			logoff.clearSession(login, request, response);
			saveMessages(request, errors);
		}
		return (errorMapping == null ? mapping.findForward("login")
				: errorMapping);
	}

	public User setupSession(String login, HttpServletRequest request,
			HttpServletResponse response) {
		UserService userService = getITrackerServices().getUserService();
		User user = userService.getUserByLogin(login);
		if (user != null) {
			String encPassword = null;
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					if (Constants.COOKIE_NAME.equals(cookies[i].getName())) {
						int seperator = cookies[i].getValue().indexOf('~');
						if (seperator > 0) {
							encPassword = cookies[i].getValue().substring(
									seperator + 1);
						}
					}
				}
			}

			return setupSession(user, encPassword, request, response);
		}
		return null;
	}

	public User setupSession(User user, String encPassword,
			HttpServletRequest request, HttpServletResponse response) {
		if (user == null) {
			return null;
		}

		UserService userService = getITrackerServices().getUserService();

		if (log.isDebugEnabled()) {
			log.debug("Creating new session");
		}
		HttpSession session = request.getSession(true);

		if (log.isDebugEnabled()) {
			log.debug("Setting session timeout to "
					+ getConfiguredSessionTimeout() + " minutes");
		}
		session.setMaxInactiveInterval(getConfiguredSessionTimeout() * 60);

		if (log.isDebugEnabled()) {
			log.debug("Setting session tracker");
		}
		session.setAttribute(Constants.SESSION_TRACKER_KEY, new SessionTracker(
				user.getLogin(), session.getId()));

		if (log.isDebugEnabled()) {
			log.debug("Setting user information");
		}
		session.setAttribute(Constants.USER_KEY, user);

		if (log.isDebugEnabled()) {
			log.debug("Setting preferences for user " + user.getLogin());
		}
		UserPreferences userPrefs = user.getPreferences();
		// TODO : this is a hack, remove when possible
		if (userPrefs == null) {
			userPrefs = new UserPreferences();
		}
		session.setAttribute(Constants.PREFERENCES_KEY, userPrefs);

		if (log.isDebugEnabled()) {
			log.debug("Setting user locale to "
					+ ITrackerResources.getLocale(userPrefs.getUserLocale()));
		}
		session.setAttribute(Constants.LOCALE_KEY, ITrackerResources
				.getLocale(userPrefs.getUserLocale()));

		if (log.isDebugEnabled()) {
			log.debug("Setting autologin cookie for user " + user.getLogin());
		}
		Cookie cookie = new Cookie(Constants.COOKIE_NAME, "");
		cookie.setPath(request.getContextPath());
		if (userPrefs.getSaveLogin()) {
			if (encPassword != null) {
				if (log.isDebugEnabled()) {
					log.debug("User allows autologin");
				}
				cookie.setComment("ITracker autologin cookie");
				cookie.setValue(user.getLogin() + "~" + encPassword);
				cookie.setMaxAge(30 * 24 * 60 * 60);
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("User does not allow autologin");
			}
			cookie.setValue("");
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);

		if (log.isDebugEnabled()) {
			log.debug("Setting permissions for user " + user.getLogin());
		}
		Map<Integer, Set<PermissionType>> usersMapOfProjectIdsAndSetOfPermissionTypes = userService
				.getUsersMapOfProjectIdsAndSetOfPermissionTypes(user,
						AuthenticationConstants.REQ_SOURCE_WEB);
		session.setAttribute(Constants.PERMISSIONS_KEY,
				usersMapOfProjectIdsAndSetOfPermissionTypes);

		// Reset some session forms
		session.setAttribute(Constants.SEARCH_QUERY_KEY, null);

		SessionManager.clearSessionNeedsReset(user.getLogin());
		if (log.isDebugEnabled()) {
			log.debug("User session data updated.");
		}
		return user;
	}

	private int getConfiguredSessionTimeout() {
		return (getITrackerServices().getConfigurationService()
				.getIntegerProperty("web_session_timeout", SESSION_TIMEOUT));
	}
}
