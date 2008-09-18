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
import java.util.ResourceBundle;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.util.AuthenticationConstants;

public class LoginUtilities {

	private static final Logger logger = Logger.getLogger(LoginUtilities.class);

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
							if (seperator > 0) {
								if (logger.isDebugEnabled()) {
									logger
											.debug("Attempting autologin for user "
													+ cookies[i].getValue()
															.substring(0,
																	seperator)
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

			/*
			 * // If we haven't found any explicit type, try doing a login with
			 * an unknown type, just in case // This will allow authenticators
			 * to check whatever they want for an auto login if(! foundLogin) {
			 * String redirectURL =
			 * request.getRequestURI().substring(request.getContextPath().length()) +
			 * (request.getQueryString() != null ? "?" +
			 * request.getQueryString() : "");
			 * request.setAttribute(Constants.AUTH_TYPE_KEY, new
			 * Integer(AuthenticationConstants.AUTH_TYPE_UNKNOWN));
			 * request.setAttribute(Constants.AUTH_REDIRECT_KEY, redirectURL);
			 * request.setAttribute("processLogin", "true"); foundLogin = true; }
			 */
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
				authType = Integer.parseInt(request
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
	public static Locale getCurrentLocale(HttpServletRequest request) {
		Locale requestLocale = null;
		HttpSession session = request.getSession(true);
		try {

			requestLocale = (Locale) request.getAttribute(Constants.LOCALE_KEY);

			if (logger.isDebugEnabled()) {
				logger.debug("getCurrentLocale: request-attribute was "
						+ requestLocale);
			}

			if (null == requestLocale) {
				// get locale from request param
				requestLocale = ITrackerResources.getLocale(request
						.getParameter("loc"));
				if (logger.isDebugEnabled()) {
					logger.debug("getCurrentLocale: request-parameter was "
							+ requestLocale);
				}
			}

			if (null == requestLocale) {
				// get it from the session
				requestLocale = (Locale) session
						.getAttribute(Constants.LOCALE_KEY);
				if (logger.isDebugEnabled()) {
					logger.debug("getCurrentLocale: session-attribute was "
							+ requestLocale);
				}
			}

			if (null == requestLocale) {
				ResourceBundle bundle = ITrackerResources.getBundle(request
						.getLocale());
				if (logger.isDebugEnabled()) {
					logger
							.debug("getCurrentLocale: trying request header locale "
									+ request.getLocale());
				}
				if (bundle.getLocale().getLanguage().equals(
						request.getLocale().getLanguage())) {
					requestLocale = request.getLocale();
					if (logger.isDebugEnabled()) {
						logger.debug("getCurrentLocale: request-locale was "
								+ requestLocale);
					}
				}
			}

			// is there no way to detect supported locales of current
			// installation?

			if (null == requestLocale) {
				Enumeration<Locale> locales = (Enumeration<Locale>) request
						.getLocales();
				ResourceBundle bundle;
				Locale locale;
				while (locales.hasMoreElements()) {
					locale = (Locale) locales.nextElement();
					bundle = ITrackerResources.getBundle(locale);
					if (logger.isDebugEnabled()) {
						logger
								.debug("getCurrentLocale: request-locales prcessing "
										+ locale + ", bundle: " + bundle);
					}
					if (bundle.getLocale().getLanguage().equals(
							locale.getLanguage())) {
						requestLocale = locale;
						if (logger.isDebugEnabled()) {
							logger
									.debug("getCurrentLocale: request-locales locale was "
											+ requestLocale);
						}
					}
				}
			}

		} finally {
			if (null == requestLocale) {
				// fall back to default locale
				requestLocale = ITrackerResources.getLocale();
				if (logger.isDebugEnabled()) {
					logger
							.debug("getCurrentLocale: fallback default locale was "
									+ requestLocale);
				}
			}
			session.setAttribute(Constants.LOCALE_KEY, requestLocale);
			request.setAttribute(Constants.LOCALE_KEY, requestLocale);
			request.setAttribute("currLocale", requestLocale);
			if (logger.isDebugEnabled()) {
				logger
						.debug("getCurrentLocale: request and session was setup with "
								+ requestLocale);
			}
		}

		return requestLocale;
	}
}
