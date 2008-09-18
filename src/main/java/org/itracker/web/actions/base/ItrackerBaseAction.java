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

package org.itracker.web.actions.base;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ITrackerServices;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.user.LoginAction;
import org.itracker.web.filters.ExecuteAlwaysFilter;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;
import org.itracker.web.util.SessionManager;

/**
 * 
 * This seems to be an action that is called always, checking for Permissions,
 * Authentication and other stuff
 * 
 * @author Marky Goldstein
 * 
 */
public abstract class ItrackerBaseAction extends Action {

	private static final Logger log = Logger.getLogger(ItrackerBaseAction.class);



	public ItrackerBaseAction() {
		super();
	}

	/**
	 * TODO: This method will not be executed always if it is not called by
	 * execute-implementor (inheriting ancester).
	 * 
	 * @deprecated use servlet-filter {@link Filter}:
	 *             {@link ExecuteAlwaysFilter}
	 * 
	 */
	public void executeAlways(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("Executing Action : " + getClass().getName());
		}
		// skipt this due to invalid pattern
		if (true)
			return;

		log
				.info("pageInit: setting the common request attributes, (coming from the former header.jsp)");
		boolean allowForgotPassword = true;
		boolean allowSelfRegister = false;
		boolean allowSaveLogin = true;
		String alternateLogo = null;

		ConfigurationService configurationService = getITrackerServices()
				.getConfigurationService();
		allowForgotPassword = configurationService.getBooleanProperty(
				"allow_forgot_password", true);
		allowSelfRegister = configurationService.getBooleanProperty(
				"allow_self_register", false);
		allowSaveLogin = configurationService.getBooleanProperty(
				"allow_save_login", true);
		alternateLogo = configurationService
				.getProperty("alternate_logo", null);

		request.setAttribute("allowForgotPassword", Boolean
				.valueOf(allowForgotPassword));
		request.setAttribute("allowSelfRegister", Boolean
				.valueOf(allowSelfRegister));
		request.setAttribute("allowSaveLogin", Boolean.valueOf(allowSaveLogin));
		request.setAttribute("alternateLogo", alternateLogo);

		// pasted from page_init (which is now empty, since we are moving logic
		// into actions):
		String baseURL = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();
		User currUser = (User) request.getSession().getAttribute("currUser");

		// TODO: think about this: are permissions put into the request? or into
		// the session? Markys knowledge: permissions are being set, when login
		// happens... do we really need the following line then?
		Map<Integer, Set<PermissionType>> permissions = getUserPermissions(request
				.getSession());
		Locale locale = LoginUtilities.getCurrentLocale(request);
		String currLogin = (currUser == null ? null : currUser.getLogin());
		// now these are put into the request scope... (new).
		request.setAttribute("baseURL", baseURL);
		request.getSession().setAttribute("currUser", currUser);
		request.setAttribute("permissions", permissions);
		request.setAttribute("currLocale", locale);
		request.setAttribute("currLogin", currLogin);

	}

	protected Map<Integer, Set<PermissionType>> getUserPermissions(
			HttpSession session) {
		return RequestHelper.getUserPermissions(session);
	}

	/**
	 * TODO: Deprecate and move to {@link org.itracker.web.util.LoginUtilities}
	 * 
	 * 
	 * @param permissionsNeeded
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	protected boolean hasPermission(int[] permissionsNeeded,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (isLoggedIn(request, response)) {
			HttpSession session = request.getSession(false);
			Map<Integer, Set<PermissionType>> permissions = (session == null) ? null
					: getUserPermissions(session);
			if (!UserUtilities.hasPermission(permissions, permissionsNeeded)) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * TODO: Deprecate and move to {@link org.itracker.web.util.LoginUtilities}
	 * 
	 * @param permissionNeeded
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	protected boolean hasPermission(int permissionNeeded,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (isLoggedIn(request, response)) {
			HttpSession session = request.getSession(false);
			Map<Integer, Set<PermissionType>> permissionsMap = (session == null) ? null
					: getUserPermissions(session);
			if (!UserUtilities.hasPermission(permissionsMap, permissionNeeded)) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @deprecated move to {@link org.itracker.web.util.LoginUtilities}
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	protected boolean isLoggedIn(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		User user = (session == null ? null : (User) session
				.getAttribute(Constants.USER_KEY));
		String login = (user == null ? null : user.getLogin());

		if (login == null || "".equals(login)) {
			LoginUtilities.checkAutoLogin(request, getAllowSaveLogin());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param request -
	 *            request for base-url
	 * @return normalized base-url for the request
	 */
	public String getBaseURL(HttpServletRequest request) {
		String url = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();
		try {
			return new URL(url).toString();
		} catch (MalformedURLException e) {
			log.error("failed to get URL normalized, returning manual url: "
					+ url);
		}
		return url;
	}

	/**
	 * TODO: Deprecate and move to new static method in a
	 * ITrackerServicesFactory-class
	 * 
	 * @return
	 */
	protected ITrackerServices getITrackerServices() {
		ITrackerServices itrackerServices = ServletContextUtils
				.getItrackerServices();
		return itrackerServices;
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public String getName() {
		log.warn("getName: is deprecated", new RuntimeException());
		return null;
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public void setName(String value) {
		log.warn("setName: is deprecated", new RuntimeException());
		// name = value;
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public String getPage() {
		log.warn("getPage: is deprecated", new RuntimeException());
		return null;
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public void setPage(String value) {
		log.warn("setPage: is deprecated", new RuntimeException());
		// page = value;
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public int getPermission() {
		log.warn("getPermission: is deprecated", new RuntimeException());
		return -1;
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public void setPermission(int value) {
		log.warn("setPermission: is deprecated", new RuntimeException());
		// permission = value;
	}

	/**
	 * former checkLogin Tag(lib) Code..
	 * 
	 * TODO: move to static utility-method in {@link LoginUtilities}
	 * 
	 * TODO: Clean and split this code up, so it is understandable what it does.
	 * 
	 */
	public ActionForward loginRouter(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			ActionForward thisactionforward) {
		ActionForward forward = new ActionForward();
		forward = null;
		forward = thisactionforward;
		boolean hasGlobalPermission;

		log
				.info("Starting loginRouter (formerly Checklogin tag) proceedure...");
		ConfigurationService configurationService = getITrackerServices()
				.getConfigurationService();
		boolean allowSaveLogin = configurationService.getBooleanProperty(
				"allow_save_login", true);

		String requestPath = request.getRequestURI();
		String redirectURL = request.getRequestURI().substring(
				request.getContextPath().length())
				+ (request.getQueryString() != null ? "?"
						+ request.getQueryString() : "");
		log.info("Setting redirectURL = " + redirectURL);

		HttpSession session = request.getSession();

		try {

			// IF NO SESSION IS ACTIVE, THEN DO A REDIRECT (TO THE CURRENT
			// PAGE?)...
			if (session == null) {
				log
						.info("No session found, preparing for redirect (not yet implemented");
				// pageContext.setAttribute("redirect",
				// URLEncoder.encode(redirectURL));
				request.setAttribute(Constants.AUTH_REDIRECT_KEY, redirectURL);
				// pageContext.forward(getPage());
				// RETURN return SKIP_PAGE;
				return forward;
			}

			// GET USER, LOGIN AND PERMISSIONS FROM SESSION
			log
					.info("Get user, login and permissions from session, if available there.");
			User user = (User) session.getAttribute(Constants.USER_KEY);
			if (user != null) {
				log.info("Found User:" + user.getFirstName() + " "
						+ user.getLastName() + " in Session.");
			}
			String login = (user == null ? null : user.getLogin());
			if (login != null) {
				log.info("Found Login:" + login + " in Session.");
			}
			Map<Integer, Set<PermissionType>> permissionsMap = getUserPermissions(session);
			if (permissionsMap != null) {
				log
						.info("Found Permissions:" + permissionsMap
								+ " in Session.");
			}

			// IF THERE IS NO LOGIN, THEN WHAT? THEN TRY A AUTOLOGIN?
			log.info("Checkin if login in not null or empty");
			if (login == null || login.equals("")) {
				log.info("Login is null or empty");
				if (LoginUtilities.checkAutoLogin(request, allowSaveLogin)) {
					log.info("Trying autologin, because we found a cookie...");
					forward = mapping.findForward("autologin");
					return forward;

				}
				// IF WE ARE NOT ON THE LOGIN PAGE...
				if (!requestPath.endsWith("/login.do")) {

					request.setAttribute("pageTitleKey",
							"itracker.web.login.title");
					request.setAttribute("pageTitleArg", "");

					// logger.logDebug("Request for page " + requestPath + "
					// attempted by unknown user.");
					request.setAttribute(Constants.AUTH_REDIRECT_KEY,
							redirectURL);
					// logger.logDebug("Setting redirect url to " +
					// request.getAttribute(Constants.AUTH_REDIRECT_KEY));
					forward = mapping.findForward("login");
					return forward;
				}
				// IF THERE IS A LOGIN...
			} else {
				log.info("Login found...: " + login);
				if (SessionManager.getSessionNeedsReset(login)) {
					// RESET THE SESSION STUFF
					log.info("Resetting the Session stuff...");
					session.removeAttribute(Constants.USER_KEY);
					session.removeAttribute(Constants.PERMISSIONS_KEY);
					user = null;
					LoginAction action = new LoginAction();
					String newLogin = SessionManager.checkRenamedLogin(login);
					user = action.setupSession((newLogin == null ? login
							: newLogin), request, response);
					SessionManager.removeRenamedLogin(login);
					if (user == null
							|| user.getStatus() != UserUtilities.STATUS_ACTIVE) {
						ActionErrors errors = new ActionErrors();
						errors.add(ActionMessages.GLOBAL_MESSAGE,
								new ActionMessage(
										"itracker.web.error.login.inactive"));
						request.setAttribute(Globals.ERROR_KEY, errors);
						// pageContext.forward(getPage());
						// RETURN return SKIP_BODY;
						return forward;
					}
				}

				// IF THERE IS NO USER...
				log.info("Checkin again if user is null...");
				if (user == null) {
					request.setAttribute(Constants.AUTH_REDIRECT_KEY,
							redirectURL);
					// pageContext.forward(getPage());
					// RETURN SKIP_PAGE;
					return forward;

					// IF THERE IS A USER...
				} else {
					log.info("else...");
					log.info("User, yes found...: " + user.getLogin());
					log.info("If there is a user...");
					permissionsMap = getUserPermissions(session);
					SessionManager.updateSessionLastAccess(login);

					hasGlobalPermission = true;

					// CHECK FOR PERMISSIONS
					log
							.info("Start check if permissions for this user are found...");
					if (getPermission() >= 0) {
						log.info("Permissions found...");
						if (!UserUtilities.hasPermission(permissionsMap,
								getPermission())) {
							log
									.info("But this user is not allowed by his permissions");
							hasGlobalPermission = false;
							// check this...
							request.setAttribute("hasGlobalPermission",
									hasGlobalPermission);
							if (!requestPath.endsWith("/unauthorized.do")) {
								// pageContext.forward("/unauthorized.do");
								// RETURN return SKIP_PAGE;
								forward = mapping.findForward("unauthorized");
								return forward;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			// logger.logError("IOException while checking login. " +
			// ioe.getMessage());
			// return SKIP_BODY;
			return forward;
		}

		return forward;
	}

	private boolean getAllowSaveLogin() {
		return (getITrackerServices().getConfigurationService()
				.getBooleanProperty("allow_save_login", true));
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public Locale getCurrLocale() {
		return null;
	}

	/**
	 * get locale from request-attribute Constants.LOCALE_KEY as initialized by {@link ExecuteAlwaysFilter}.
	 * 
	 * @return
	 * @deprecated use getLocale instead
	 */
	public  Locale getCurrLocale(HttpServletRequest request) {
		return getLocale(request);
	}

	/**
	 * @deprecated no state information is allowed in actions. use a correct
	 *             pattern like request-attributes.
	 * @return
	 */
	public void setCurrLocale(Locale currLocale) {
//		this.currLocale = currLocale;
	}

	/**
	 * set the locale in request attribute Constants.LOCALE_KEY
	 * 
	 * @deprecated
	 */
	public void setCurrLocale(Locale currLocale, HttpServletRequest request) {
		request.setAttribute(Constants.LOCALE_KEY, currLocale);
//		this.currLocale = currLocale;
	}
	@Override
	protected Locale getLocale(HttpServletRequest request) {
		Locale locale = super.getLocale(request);
		if (null == locale) {
			locale = LoginUtilities.getCurrentLocale(request);
		}
		return locale;
	}


}
