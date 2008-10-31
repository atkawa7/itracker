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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.User;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.SessionManager;



public class LogoffAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(LogoffAction.class);
	
    public LogoffAction() {
    }
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.executeAlways(mapping,form,request,response);
        try {
            
            HttpSession session = request.getSession(true);
            User user = (User) session.getAttribute("user");
            String login = (user != null ? user.getLogin() : "UNKNOWN");
            

            
            if(clearSession(login, request, response)) {
                log.info("User " + login + " logged out successfully.");
            }
        } catch(Exception e) {
        	if (log.isDebugEnabled())
        		log.debug("execute: Error logging out user. " + e.getMessage());
        }
        
        
        
        String pageTitleKey = "itracker.web.login.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
        return mapping.findForward("login");
    }
    
    public boolean clearSession(String login, HttpServletRequest request, HttpServletResponse response) {
        try {
        	
    		Cookie cookie = new Cookie(Constants.COOKIE_NAME, "");
    		cookie.setPath(request.getContextPath());
			if (log.isDebugEnabled()) {
				log.debug("clearSession: remove autologin cookie");
			}
			cookie.setValue("");
			cookie.setMaxAge(0);
		
			response.addCookie(cookie);
        	
            HttpSession session = request.getSession(true);
            session.invalidate();
            
            if(login != null) {
                SessionManager.invalidateSession(login);
            }
        } catch(Exception e) {
            log.debug("Unable to clear session for user " + (login == null ? "UNKNOWN" : login));
            return false;
        }
        return true;
    }
}
