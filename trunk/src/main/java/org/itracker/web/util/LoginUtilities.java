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

package org.itracker.web.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.UserService;
import org.itracker.services.util.AuthenticationConstants;

public class LoginUtilities {

	private static final Logger logger = Logger.getLogger(LoginUtilities.class);
	private static final int DEFAULT_SESSION_TIMEOUT = 30;

	public static boolean checkAutoLogin(HttpServletRequest request,
			boolean allowSaveLogin) {
		boolean foundLogin = false;

		if (request != null) {
			int authType = getRequestAuthType(request);

			// Check for auto login in request
			if (!foundLogin) {
				if (authType == AuthenticationConstants.AUTH_TYPE_REQUEST) {
					String redirectURL = request.getRequestURI().substring(
							request.getContextPath().length())
							+ (request.getQueryString() != null ? "?"
									+ request.getQueryString() : "");
					request.setAttribute(Constants.AUTH_TYPE_KEY,
							AuthenticationConstants.AUTH_TYPE_REQUEST);
					request.setAttribute(Constants.AUTH_REDIRECT_KEY,
							redirectURL);
					request.setAttribute("processLogin", "true");
					foundLogin = true;
				}
			}

			// Add in check for client certs

			// Check for auto login with cookies, this will only happen if users
			// are allowed to save
			// their logins to cookies
			if (allowSaveLogin && !foundLogin) {
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						if (Constants.COOKIE_NAME.equals(cookies[i].getName())) {
							int seperator = cookies[i].getValue().indexOf('~');
							final String login;
							if (seperator > 0) {
								login = cookies[i].getValue()
								.substring(0,
										seperator);
								if (logger.isDebugEnabled()) {
									logger
											.debug("Attempting autologin for user "
													+ login
													+ ".");
								}

								String redirectURL = request.getRequestURI()
										.substring(
												request.getContextPath()
														.length())
										+ (request.getQueryString() != null ? "?"
												+ request.getQueryString()
												: "");
								request.setAttribute(Constants.AUTH_LOGIN_KEY,
										cookies[i].getValue().substring(0,
												seperator));
								request.setAttribute(Constants.AUTH_TYPE_KEY,
								AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC);
								
								request.setAttribute(Constants.AUTH_VALUE_KEY,
										cookies[i].getValue().substring(
												seperator + 1));
								request.setAttribute(
										Constants.AUTH_REDIRECT_KEY,
										redirectURL);
								request.setAttribute("processLogin", "true");
								foundLogin = true;
							}
						}
					}
				}
			}

		}

		return foundLogin;
	}

	public static int getRequestAuthType(HttpServletRequest request) {
		int authType = AuthenticationConstants.AUTH_TYPE_UNKNOWN;

		try {
			if (request.getAttribute(Constants.AUTH_TYPE_KEY) != null) {
				authType = ((Integer) request
						.getAttribute(Constants.AUTH_TYPE_KEY)).intValue();
			}
			if (request.getParameter(Constants.AUTH_TYPE_KEY) != null) {
				authType = Integer.valueOf(request
						.getParameter(Constants.AUTH_TYPE_KEY));
			}
		} catch (Exception e) {
			logger
					.debug("Error retrieving auth type while checking auto login.  "
							+ e.getMessage());
		}

		return authType;
	}

	/**
	 * Get a locale from request
	 * 
	 * <p>
	 * TODO the order of retrieving locale from request should be:
	 * <ol>
	 * <li>request-attribute Constants.LOCALE_KEY</li>
	 * <li>request-param 'loc'</li>
	 * <li>session attribute <code>Constants.LOCALE_KEY</code></li>
	 * <li>cookie 'loc'</li>
	 * <li>request.getLocale()/request.getLocales()</li>
	 * <li>ITrackerResources.DEFAULT_LOCALE</li>
	 * </ol>
	 * </p>
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Locale getCurrentLocale(HttpServletRequest request) {
		Locale requestLocale = null;
		HttpSession session = request.getSession(true);
		try {

			requestLocale = (Locale) request.getAttribute(Constants.LOCALE_KEY);

//			if (logger.isDebugEnabled()) {
//				logger.debug("getCurrentLocale: request-attribute was "
//						+ requestLocale);
//			}

			if (null == requestLocale) {
				// get locale from request param
				String loc = request
				.getParameter("loc");
				if (null != loc && loc.trim().length() > 1) {
					requestLocale = ITrackerResources.getLocale(loc);
				}
//				if (logger.isDebugEnabled()) {
//					logger.debug("getCurrentLocale: request-parameter was "
//							+ loc);
//				}
			}

			if (null == requestLocale) {
				// get it from the session
				requestLocale = (Locale) session
						.getAttribute(Constants.LOCALE_KEY);
//				if (logger.isDebugEnabled()) {
//					logger.debug("getCurrentLocale: session-attribute was "
//							+ requestLocale);
//				}
			}

			if (null == requestLocale) {
				ResourceBundle bundle = ITrackerResources.getBundle(request
						.getLocale());
//				if (logger.isDebugEnabled()) {
//					logger
//							.debug("getCurrentLocale: trying request header locale "
//									+ request.getLocale());
//				}
				if (bundle.getLocale().getLanguage().equals(
						request.getLocale().getLanguage())) {
					requestLocale = request.getLocale();
//					if (logger.isDebugEnabled()) {
//						logger.debug("getCurrentLocale: request-locale was "
//								+ requestLocale);
//					}
				}
			}

			// is there no way to detect supported locales of current
			// installation?

			if (null == requestLocale) {
				Enumeration<Locale> locales = (Enumeration<Locale>) request.getLocales();
				ResourceBundle bundle;
				Locale locale;
				while (locales.hasMoreElements()) {
					locale = (Locale) locales.nextElement();
					bundle = ITrackerResources.getBundle(locale);
//					if (logger.isDebugEnabled()) {
//						logger
//								.debug("getCurrentLocale: request-locales prcessing "
//										+ locale + ", bundle: " + bundle);
//					}
					if (bundle.getLocale().getLanguage().equals(
							locale.getLanguage())) {
						requestLocale = locale;
//						if (logger.isDebugEnabled()) {
//							logger
//									.debug("getCurrentLocale: request-locales locale was "
//											+ requestLocale);
//						}
					}
				}
			}

		} finally {
			if (null == requestLocale) {
				// fall back to default locale
				requestLocale = ITrackerResources.getLocale();
//				if (logger.isDebugEnabled()) {
//					logger
//							.debug("getCurrentLocale: fallback default locale was "
//									+ requestLocale);
//				}
			}
			session.setAttribute(Constants.LOCALE_KEY, requestLocale);
			request.setAttribute(Constants.LOCALE_KEY, requestLocale);
			request.setAttribute("currLocale", requestLocale);
//			if (logger.isDebugEnabled()) {
//				logger
//						.debug("getCurrentLocale: request and session was setup with "
//								+ requestLocale);
//			}
		}

		return requestLocale;
	}

	/**
	 * get current user from request-attribute currUser, if not set from request-session
	 * 
	 * @param request
	 * @return current user or null if unauthenticated
	 * @throws NullPointerException
	 *             if the request was null
	 */
	public static final User getCurrentUser(HttpServletRequest request) {

		User currUser = (User) request.getAttribute("currUser");
		if (null == currUser) {
			currUser = (User) request.getSession().getAttribute("currUser");
		}
		

		
//		try {
//			ITrackerServices iTrackerServices = ServletContextUtils.getItrackerServices();
//			// autologin
//			if (null == currUser && 
//					checkAutoLogin(request, iTrackerServices.getConfigurationService().getBooleanProperty(
//					"allow_save_login", true))) {			
//				if (Boolean.valueOf(String.valueOf(request.getAttribute("processLogin")))) {
//					currUser = LoginUtilities.processAutoLogin(request, iTrackerServices);
//				}
//			}
//		} catch (Exception e) {
//			currUser = null;
//			if (logger.isInfoEnabled()) {
//				logger.info("getCurrentUser: could not authenticate from request.");
//				if (logger.isDebugEnabled()) {
//					logger.debug("getCurrentUser: exception caught", e);
//				}
//			}
//		}
		return currUser;
	}
//	public static final User processAutoLogin(HttpServletRequest request, ITrackerServices iTrackerServices) {
//		
//		UserService userService = iTrackerServices.getUserService();
//		final int authType = LoginUtilities.getRequestAuthType(request);
//		final User user;
//		final String login;
//		if (authType == AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC) {
//			login = (String) request
//					.getAttribute(Constants.AUTH_LOGIN_KEY);
//			String authenticator = (String) request
//					.getAttribute(Constants.AUTH_VALUE_KEY);
//
//			logger
//					.debug("Attempting login with encrypted password for user "
//							+ login);
//			user = userService.checkLogin(login, authenticator,
//					AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC,
//					AuthenticationConstants.REQ_SOURCE_WEB);
//			if (user != null) {
//				return user;
//			}
//		}
//
//		throw new AuthenticatorException(
//				AuthenticatorException.UNKNOWN_USER);
//		
//	}
	public static final Boolean allowSaveLogin(HttpServletRequest request) {
		return Boolean.valueOf((String)request.getAttribute("allowSaveLogin"));
	}
	
	public static User setupSession(String login, HttpServletRequest request,
			HttpServletResponse response) {
		if (null == login) {
			logger.warn("setupSession: null login", (logger.isDebugEnabled()? new RuntimeException(): null));
			throw new IllegalArgumentException("null login");
		}
		UserService userService = ServletContextUtils.getItrackerServices().getUserService();
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

	public static User setupSession(User user, String encPassword,
			HttpServletRequest request, HttpServletResponse response) {
		if (user == null) {
			logger.warn("setupSession: null user", (logger.isDebugEnabled()? new RuntimeException(): null));
			throw new IllegalArgumentException("null user");
		}

		UserService userService = ServletContextUtils.getItrackerServices().getUserService();

		if (logger.isDebugEnabled()) {
			logger.debug("Creating new session");
		}
		HttpSession session = request.getSession(true);

		if (logger.isDebugEnabled()) {
			logger.debug("Setting session timeout to "
					+ getConfiguredSessionTimeout() + " minutes");
		}
		session.setMaxInactiveInterval(getConfiguredSessionTimeout() * 60);

		if (logger.isDebugEnabled()) {
			logger.debug("Setting session tracker");
		}
		session.setAttribute(Constants.SESSION_TRACKER_KEY, new SessionTracker(
				user.getLogin(), session.getId()));

		if (logger.isDebugEnabled()) {
			logger.debug("Setting user information");
		}
		session.setAttribute(Constants.USER_KEY, user);

		if (logger.isDebugEnabled()) {
			logger.debug("Setting preferences for user " + user.getLogin());
		}
		UserPreferences userPrefs = user.getPreferences();
		// TODO : this is a hack, remove when possible
		if (userPrefs == null) {
			logger.warn("setupSession: got user with no preferences!: " + user + " (prefs: " + user.getPreferences() + ")");
			userPrefs = new UserPreferences();
		}
		session.setAttribute(Constants.PREFERENCES_KEY, userPrefs);

		if (logger.isDebugEnabled()) {
			logger.debug("Setting user " + user + " locale to "+ ITrackerResources
					.getLocale(userPrefs.getUserLocale()));
		}
		session.setAttribute(Constants.LOCALE_KEY, ITrackerResources
				.getLocale(userPrefs.getUserLocale()));

		if (logger.isDebugEnabled()) {
			logger.debug("Setting autologin cookie for user " + user.getLogin());
		}
		Cookie cookie = new Cookie(Constants.COOKIE_NAME, "");
		cookie.setPath(request.getContextPath());
		if (userPrefs.getSaveLogin()) {
			if (encPassword != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("User allows autologin");
				}
				cookie.setComment("ITracker autologin cookie");
				cookie.setValue(user.getLogin() + "~" + encPassword);
				cookie.setMaxAge(30 * 24 * 60 * 60);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("User does not allow autologin");
			}
			cookie.setValue("");
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);

		if (logger.isDebugEnabled()) {
			logger.debug("Setting permissions for user " + user.getLogin());
		}
		Map<Integer, Set<PermissionType>> usersMapOfProjectIdsAndSetOfPermissionTypes = userService
				.getUsersMapOfProjectIdsAndSetOfPermissionTypes(user,
						AuthenticationConstants.REQ_SOURCE_WEB);
		session.setAttribute(Constants.PERMISSIONS_KEY,
				usersMapOfProjectIdsAndSetOfPermissionTypes);

		// Reset some session forms
		session.setAttribute(Constants.SEARCH_QUERY_KEY, null);

		SessionManager.clearSessionNeedsReset(user.getLogin());
		if (logger.isDebugEnabled()) {
			logger.debug("User session data updated.");
		}
		return user;
	}

	public static int getConfiguredSessionTimeout() {
		return (ServletContextUtils.getItrackerServices().getConfigurationService()
				.getIntegerProperty("web_session_timeout", DEFAULT_SESSION_TIMEOUT));
	}
}
