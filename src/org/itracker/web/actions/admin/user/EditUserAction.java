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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List; 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
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

    public EditUserAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing component.");
            return mapping.findForward("listusers");
        }
        resetToken(request);

        UserForm userForm = (UserForm) form;
        if(userForm == null) {
            return mapping.findForward("listusers");
        }

        HttpSession session = request.getSession(true);

        try {
            UserService userService = getITrackerServices().getUserService();
            ProjectService projectService = getITrackerServices().getProjectService();
            
            User editUser = new User();
            editUser.setId(userForm.getId());
            editUser.setLogin(userForm.getLogin());
            editUser.setFirstName(userForm.getFirstName());
            editUser.setLastName(userForm.getLastName());
            editUser.setEmail(userForm.getEmail());
            editUser.setSuperUser(userForm.isSuperUser());
            String previousLogin = editUser.getLogin();

            try {
                if("create".equals(userForm.getAction())) {
                    if(! userService.allowProfileCreation(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofilecreates"));
                        saveMessages(request, errors);
                        return mapping.findForward("error");
                    }

                    logger.debug("Creating new userid.");
                    editUser.setRegistrationType(UserUtilities.REGISTRATION_TYPE_ADMIN);
                    if(userService.allowPasswordUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                        editUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));
                    }
                    editUser = userService.createUser(editUser);
                } else if ("update".equals(userForm.getAction())) {
                    User existingUser = userService.getUser(editUser.getId());
                    if(existingUser != null) {
                        previousLogin = existingUser.getLogin();
                        boolean performUpdate = true;
                        if(! userService.allowProfileUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                            editUser = existingUser;
                            performUpdate = false;
                        }
                        if(userService.allowPasswordUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                            if(userForm.getPassword() != null && ! userForm.getPassword().equals("")) {
                                editUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));
                                performUpdate = true;
                            }
                        }
                        if(performUpdate) {
                            editUser = userService.updateUser(editUser);
                        }
                    }
                } else {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
                }
            } catch(UserException ue) {
                ue.printStackTrace();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.existinglogin"));
                saveMessages(request, errors);
                saveToken(request);
                return mapping.getInputForward();
            }

            if(errors.isEmpty() && userService.allowPermissionUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                HashMap<String, String> permissions = userForm.getPermissions();
                List<Permission> permissionsList = new ArrayList<Permission>();
                for(Iterator<String> iter = permissions.keySet().iterator(); iter.hasNext(); ) {
                    String paramName = iter.next();
                    Integer projectIntValue = Integer.parseInt(paramName.substring(4,paramName.lastIndexOf('P')));
                    Project project = projectService.getProject(projectIntValue);
                    Integer permissionIntValue = new Integer(paramName.substring(paramName.lastIndexOf('j') + 1));
                    permissionsList.add(new Permission(project,permissionIntValue)); 
      
                    // Perm7Proj5103
                    
              
                }
                List<Permission> newPermissions = new ArrayList<Permission>();
                newPermissions = permissionsList;
                userService.setUserPermissions(editUser.getId(), newPermissions);
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

                logger.debug("Forwarding to list users.");
                session.removeAttribute(Constants.EDIT_USER_KEY);
                return mapping.findForward("listusers");
            }
        } catch(Exception e) {            
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }
        session.removeAttribute(Constants.EDIT_USER_KEY);
        return mapping.findForward("error");
    }

}
  