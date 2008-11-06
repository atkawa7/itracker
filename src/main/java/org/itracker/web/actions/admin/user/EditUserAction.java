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

package org.itracker.web.actions.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.SessionManager;


public class EditUserAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditUserAction.class);
	

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	ActionMessages errors = new ActionMessages();
    	
        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        if(! isTokenValid(request)) {
            log.debug("Invalid request token while editing component.");
            return mapping.findForward("listusers");
        }
        resetToken(request);

        UserForm userForm = (UserForm) form;
        if(userForm == null) {
            return mapping.findForward("listusers");
        }

        HttpSession session = request.getSession();

        try {
            UserService userService = getITrackerServices().getUserService();
            ProjectService projectService = getITrackerServices().getProjectService();

            String previousLogin = userForm.getLogin();
            User editUser;
            // if userForm.getID returns -1, then this is a new user.. 
            if( userForm.getId() != -1 ) {
//                editUser.setId(userForm.getId());
            	editUser = userService.getUser(userForm.getId());
                previousLogin = editUser.getLogin();
            } else {
            	editUser = new User();
            }


            editUser.setLogin(userForm.getLogin());
            editUser.setFirstName(userForm.getFirstName());
            editUser.setLastName(userForm.getLastName());
            editUser.setEmail(userForm.getEmail());
            editUser.setSuperUser(userForm.isSuperUser());

            try {
                if("create".equals(userForm.getAction())) {
                    if(! userService.allowProfileCreation(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofilecreates"));
                    	saveErrors(request, errors);
                        return mapping.findForward("error");
                    }

                    log.debug("Creating new userid.");
                    editUser.setRegistrationType(UserUtilities.REGISTRATION_TYPE_ADMIN);
                    if (null != userForm.getPassword() && userForm.getPassword().length() > 0) {
	                    if(userService.allowPasswordUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
	                        editUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));
	                    } else {
	                    	// Passwort was attempted to set, but authenticator is not able to. Exception
//	                    	itracker.web.error.nopasswordupdates
	                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.nopasswordupdates"));
	                    	saveErrors(request, errors);
	                        return mapping.findForward("error");
	                    }
                    }
                    editUser = userService.createUser(editUser);
                } else if ("update".equals(userForm.getAction())) {
                    User existingUser = editUser;//userService.getUser(editUser.getId());
                    if (log.isDebugEnabled()) {
                    	log.debug("execute: updating existingUser " + existingUser);
                    }
                    if(existingUser != null) {
                        previousLogin = existingUser.getLogin();
                        if(! userService.allowProfileUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                            editUser = existingUser;
//                            itracker.web.error.noprofileupdates
	                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofileupdates"));
	                    	saveErrors(request, errors);
	                        return mapping.findForward("error");
                        }
                        

//                            log.debug("updating " + editUser);
                        if (null != userForm.getPassword() && !userForm.getPassword().equals("")) {
	                        if(userService.allowPasswordUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {

                                editUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));
//	                                if (log.isDebugEnabled()) {
//	                                	log.debug("execute: setting password: " + userForm.getPassword() + " encrypted: " + editUser.getPassword());
//	                                }
	                            
	                        } else {
		                    	// Passwort was attempted to set, but authenticator is not able to. Exception
	                            editUser = existingUser;
//		                            itracker.web.error.nopasswordupdates
		                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.nopasswordupdates"));
		                    	saveErrors(request, errors);
		                        return mapping.findForward("error");
	                        }
                        }
                        
                    	if (log.isDebugEnabled()) {
                    		log.debug("execute: applying updates on user " + editUser);
                    	}
                        editUser = userService.updateUser(editUser);
                    	if (log.isDebugEnabled()) {
                    		log.debug("execute: applied updates on user " + editUser);
                    	}
                    } else {
                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invaliduser"));
                    }
                } else {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
                }
            } catch(UserException ue) {
                ue.printStackTrace();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.existinglogin"));
                saveErrors(request, errors);
                saveToken(request);
                mapping.findForward("error");
            }

            if(errors.isEmpty() && userService.allowPermissionUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                Map<String,String> permissionsMap = userForm.getPermissions();
                List<Permission> newPermissions = new ArrayList<Permission>();
                
                
                for (Iterator<String> iter = permissionsMap.keySet().iterator(); iter.hasNext(); ) {
                    String paramName = iter.next();
                    Integer projectIntValue =  new Integer(paramName.substring(paramName.lastIndexOf('j') + 1));
                    Project project = projectService.getProject(projectIntValue);
                    Integer permissionIntValue = Integer.parseInt(paramName.substring(4,paramName.lastIndexOf('P')));
                    Permission newPermission = new Permission(permissionIntValue, editUser, project); 
                    newPermission.setCreateDate(new Date());
                    newPermissions.add(newPermission); 
                
                    if ( permissionIntValue == UserUtilities.PERMISSION_PRODUCT_ADMIN ) {
                        List<User> users = projectService.getProjectOwners(projectIntValue);
                        HashSet<Integer> owners = new HashSet<Integer>();
                        for ( Iterator<User> userIterator = users.iterator(); userIterator.hasNext(); ) {
                            User user = userIterator.next();
                            owners.add(user.getId());
                        }
                        owners.add(editUser.getId());
                        projectService.setProjectOwners(project,owners);
                    }
                }
                
                boolean successful = userService.setUserPermissions(editUser.getId(), newPermissions);
                if (successful == true) { 
                	log.debug("User Permissions have been nicely set.");
                
                } else {
                	log.debug("No good. User Permissions have not been nicely set.");
                 
                }
            }

            if(errors.isEmpty()) {
                if(! previousLogin.equals(editUser.getLogin())) {
                    if(SessionManager.getSessionStart(previousLogin) != null) {
                        SessionManager.addRenamedLogin(previousLogin, editUser.getLogin());
                        SessionManager.setSessionNeedsReset(previousLogin);
                    }
                } else {
                    if(SessionManager.getSessionStart(editUser.getLogin()) != null) {
                        SessionManager.setSessionNeedsReset(editUser.getLogin());
                    }
                }

                log.debug("Forwarding to list users.");
                session.removeAttribute(Constants.EDIT_USER_KEY);
                return mapping.findForward("listusers");
            }
        } catch(Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
        	saveErrors(request, errors);
            saveToken(request);
            return mapping.findForward("error");
        }
        session.removeAttribute(Constants.EDIT_USER_KEY);
        return mapping.findForward("error");
    }

}
  