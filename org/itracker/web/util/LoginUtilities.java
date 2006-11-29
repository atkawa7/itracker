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

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.util.AuthenticationConstants;


public class LoginUtilities {

    private static final Logger logger = Logger.getLogger(LoginUtilities.class);
    
    public static boolean checkAutoLogin(HttpServletRequest request, boolean allowSaveLogin) {
        boolean foundLogin = false;

        if(request != null) {
            int authType = getRequestAuthType(request);

            // Check for auto login in request
            if(! foundLogin) {
                if(authType == AuthenticationConstants.AUTH_TYPE_REQUEST) {
                    String redirectURL = request.getRequestURI().substring(request.getContextPath().length()) +
                                         (request.getQueryString() != null ? "?" + request.getQueryString() : "");
                    request.setAttribute(Constants.AUTH_TYPE_KEY, new Integer(AuthenticationConstants.AUTH_TYPE_REQUEST));
                    request.setAttribute(Constants.AUTH_REDIRECT_KEY, redirectURL);
                    request.setAttribute("processLogin", "true");
                    foundLogin = true;
                }
            }

            // Add in check for client certs

            // Check for auto login with cookies, this will only happen if users are allowed to save
            // their logins to cookies
            if(allowSaveLogin && ! foundLogin) {
                Cookie[] cookies = request.getCookies();
                if(cookies != null) {
                    for(int i = 0; i < cookies.length; i++) {
                        if(Constants.COOKIE_NAME.equals(cookies[i].getName())) {
                            int seperator = cookies[i].getValue().indexOf('~');
                            if(seperator > 0) {
                                if(logger.isDebugEnabled()) {
                                    logger.debug("Attempting autologin for user " + cookies[i].getValue().substring(0,seperator) + ".");
                                }

                                String redirectURL = request.getRequestURI().substring(request.getContextPath().length()) +
                                                     (request.getQueryString() != null ? "?" + request.getQueryString() : "");
                                request.setAttribute(Constants.AUTH_LOGIN_KEY, cookies[i].getValue().substring(0,seperator));
                                request.setAttribute(Constants.AUTH_TYPE_KEY, new Integer(AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC));
                                request.setAttribute(Constants.AUTH_VALUE_KEY, cookies[i].getValue().substring(seperator + 1));
                                request.setAttribute(Constants.AUTH_REDIRECT_KEY, redirectURL);
                                request.setAttribute("processLogin", "true");
                                foundLogin = true;
                            }
                        }
                    }
                }
            }

/*
            // If we haven't found any explicit type, try doing a login with an unknown type, just in case
            // This will allow authenticators to check whatever they want for an auto login
            if(! foundLogin) {
                String redirectURL = request.getRequestURI().substring(request.getContextPath().length()) +
                                     (request.getQueryString() != null ? "?" + request.getQueryString() : "");
                request.setAttribute(Constants.AUTH_TYPE_KEY, new Integer(AuthenticationConstants.AUTH_TYPE_UNKNOWN));
                request.setAttribute(Constants.AUTH_REDIRECT_KEY, redirectURL);
                request.setAttribute("processLogin", "true");
                foundLogin = true;
            }
*/
        }

        return foundLogin;
    }

    public static int getRequestAuthType(HttpServletRequest request) {
        int authType = AuthenticationConstants.AUTH_TYPE_UNKNOWN;

        try {
            if(request.getAttribute(Constants.AUTH_TYPE_KEY) != null) {
                authType = ((Integer) request.getAttribute(Constants.AUTH_TYPE_KEY)).intValue();
            }
            if(request.getParameter(Constants.AUTH_TYPE_KEY) != null) {
                authType = Integer.parseInt(request.getParameter(Constants.AUTH_TYPE_KEY));
            }
        } catch(Exception e) {
            logger.debug("Error retrieving auth type while checking auto login.  " + e.getMessage());
        }

        return authType;
    }

    public static Locale getCurrentLocale(HttpServletRequest request) {
        Locale currLocale = null;

        if(request != null) {
            HttpSession session = request.getSession(true);
            currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);

            if(request.getParameter("loc") != null && ! request.getParameter("loc").equals("")) {
                currLocale = ITrackerResources.getLocale(request.getParameter("loc"));
                session.setAttribute(Constants.LOCALE_KEY, currLocale);
            }
        }

        return (currLocale == null ? ITrackerResources.getLocale() : currLocale);
    }
}
