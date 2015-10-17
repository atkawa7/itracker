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

package org.itracker.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.ITrackerServices;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * TODO: Rewrite Javadocs here
 * This needs documentation.
 * Is it still used?
 * What is it used for?
 * How?
 * It is referenced by
 * AttachementDownloadController, (@deprecated Use org.itracker.web.actions.admin.attachment.DownloadAttachmentAction instead.)
 * ReportChartController,
 * ReportDownloadController
 *
 * @author ready
 */
public abstract class GenericController extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(GenericController.class);

    public GenericController() {
    }

    @SuppressWarnings("unchecked")
    protected Map<Integer, Set<PermissionType>> getPermissions(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Map<Integer, Set<PermissionType>>) session.getAttribute("permissions");
    }

    protected void saveMessages(HttpServletRequest request, ActionErrors errors) {

        if ((errors == null) || errors.isEmpty()) {
            request.removeAttribute(Globals.ERROR_KEY);
            return;
        }
        request.setAttribute(Globals.ERROR_KEY, errors);
    }

    protected boolean hasPermission(PermissionType[] permissionsNeeded,
                                    HttpServletRequest request,
                                    HttpServletResponse response)
            throws IOException, ServletException {
        if (isLoggedIn(request, response)) {
            HttpSession session = request.getSession(false);
            Map<Integer, Set<PermissionType>> permissions = getPermissions(session);
            if (!UserUtilities.hasPermission(permissions, permissionsNeeded)) {
                forward("/unauthorized.jsp", request, response);
                return false;
            }
            return true;
        }
        return false;
    }

    protected boolean hasPermission(PermissionType permissionNeeded,
                                    HttpServletRequest request,
                                    HttpServletResponse response)
            throws IOException, ServletException {
        if (isLoggedIn(request, response)) {
            HttpSession session = request.getSession(false);
            Map<Integer, Set<PermissionType>> permissionsMap = getPermissions(session);
            if (!UserUtilities.hasPermission(permissionsMap, permissionNeeded)) {
                forward("/unauthorized.jsp", request, response);
                return false;
            }
            return true;
        }
        return false;
    }

    protected boolean isLoggedIn(HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User user = (session == null ? null : (User) session.getAttribute("user"));
        String login = (user == null ? null : user.getLogin());

        return !StringUtils.isEmpty(login);
    }

    protected boolean isLoggedInWithRedirect(HttpServletRequest request,
                                             HttpServletResponse response)
            throws IOException, ServletException {
        if (!isLoggedIn(request, response)) {
            String requestPath = request.getRequestURI();
            if (!requestPath.endsWith("/login.jsp")) {
                String redirectURL = request.getRequestURI().substring(request.getContextPath().length());
                forward("/login.jsp?" + Constants.AUTH_REDIRECT_KEY + "=" + redirectURL, request, response);
            }
            return false;
        }
        return true;
    }

    protected void forward(String url, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        RequestDispatcher rd = request.getRequestDispatcher(url);
        if (rd == null) {
            throw new ServletException("RequestDispatcher is null. URL: " + url);
        }

        rd.forward(request, response);
    }

    protected void redirect(String url, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
                request.getContextPath();

        response.sendRedirect(baseURL + url);
    }

    public Locale getLocale(HttpServletRequest request) {
        Locale
                locale = LoginUtilities.getCurrentLocale(request);

        return locale;
    }

    protected ITrackerServices getITrackerServices(ServletContext context) {
        return ServletContextUtils.getItrackerServices();
    }


}