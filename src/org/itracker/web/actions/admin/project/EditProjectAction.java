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

package org.itracker.web.actions.admin.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.NameValuePair;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class EditProjectAction extends ItrackerBaseAction {
    
    public EditProjectAction() {
    }
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing project.");
            return mapping.findForward("listprojectsadmin");
        }
        resetToken(request);
        
        Project project = null;
        try {
            ProjectService projectService = getITrackerServices().getProjectService();
            UserService userService = getITrackerServices().getUserService();
            
            HttpSession session = request.getSession(true);
            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            User user = (User) session.getAttribute(Constants.USER_KEY);
            String action = (String) request.getParameter("action");
            
            project = new Project();
            project.setId((Integer) PropertyUtils.getSimpleProperty(form, "id"));
            if ("update".equals(action)) {
                project = projectService.getProject(project.getId());
            }
            project.setDescription((String) PropertyUtils.getSimpleProperty(form, "description"));
            project.setName((String) PropertyUtils.getSimpleProperty(form, "name"));
            Integer projectStatus = (Integer) PropertyUtils.getSimpleProperty(form, "status");
            if(projectStatus != null) {
                project.setStatus(Status.valueOf(projectStatus));
            } else {
                project.setStatus(Status.ACTIVE);
            }
            
            Integer[] optionValues = (Integer[]) PropertyUtils.getSimpleProperty(form, "options");
            int optionmask = 0;
            if(optionValues != null) {
                for(int i = 0; i < optionValues.length; i++) {
                    optionmask += optionValues[i].intValue();
                }
            }
            project.setOptions(optionmask);
            
            HashSet<Integer> fields = new HashSet<Integer>();
            Integer[] fieldIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "fields");
            if(fieldIds != null) {
                for(int i = 0; i < fieldIds.length; i++) {
                    fields.add(fieldIds[i]);
                }
            }
            
            HashSet<Integer> owners = new HashSet<Integer>();
            Integer[] ownerIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "owners");
            if(ownerIds != null) {
                for(int i = 0; i < ownerIds.length; i++) {
                    owners.add(ownerIds[i]);
                }
            } else {
                ownerIds = new Integer[0];
            }
            //TODO: commented this because it was causing authentication problems (rjst), why is it needed anyway ?
            //SessionManager.setAllSessionsNeedsReset();
            
            if("create".equals(action)) {
                if(! user.isSuperUser()) {
                    return mapping.findForward("unauthorized");
                }
                projectService.getProjectDAO().save(project);
                
                Integer[] userIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "users");
                if ( userIds == null ) {
                    userIds = new Integer[0];
                }
                Integer[] permissions = (Integer[]) PropertyUtils.getSimpleProperty(form, "permissions");
                if ( permissions == null ) {
                    permissions = new Integer[0];
                }
                List<User> users = new ArrayList<User>();
                
                List<Permission> userPermissionModels = new ArrayList<Permission>();
                
                List<NameValuePair> superUserPermissions = UserUtilities.getPermissionNames(ITrackerResources.getLocale());
                boolean fndprojectAdmins = false;
                for(int i = 0; i < ownerIds.length; i++) {
                    User usermodel = userService.getUser(ownerIds[i]);
                    userPermissionModels = userService.getUserPermissionsLocal(usermodel);
                    for ( int j = 0; j < superUserPermissions.size(); j++ ) {
                        int superUserPermission = Integer.parseInt(superUserPermissions.get(j).getValue());
                        if ( superUserPermission == UserUtilities.PERMISSION_PRODUCT_ADMIN )
                            fndprojectAdmins = true;
                        userPermissionModels.add(new Permission(superUserPermission, usermodel, project));
                    }
                    userService.addUserPermissions(ownerIds[i],userPermissionModels);
                }
                
                if(userIds != null && permissions != null && userIds.length > 0 && permissions.length > 0) {
                    for(int i = 0; i < userIds.length; i++) {
                        users.add(userService.getUser(userIds[i]));
                    }
                    userPermissionModels = new ArrayList<Permission>();
                    for(Iterator iterator = users.iterator(); iterator.hasNext(); ) {
                        User usermodel = (User) iterator.next();
                        userPermissionModels = userService.getUserPermissionsLocal(usermodel);
                        for(int i = 0; i < permissions.length; i++) {
                            userPermissionModels.add(new Permission(permissions[i], usermodel, project));
                        }
                        userService.setUserPermissions(usermodel.getId(), userPermissionModels );
                        userService.UpdateAuthenticator(usermodel.getId(), userPermissionModels);
                    }
                }
                for(int i = 0; i < userIds.length; i++) {
                    if ( fndprojectAdmins )
                        owners.add(userIds[i]);
                }
                projectService.setProjectOwners(project, owners);
                projectService.setProjectFields(project, fields);
                
                projectService.getProjectDAO().save(project);
                
            } else if ("update".equals(action)) {
                if(! UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                    return mapping.findForward("unauthorized");
                }
                List<User> users = project.getOwners();
                
                //List<Permission> userPermissionModels = project.getPermissions();
                for(Iterator iterator = users.iterator(); iterator.hasNext(); ) {
                    user = (User) iterator.next();
                    boolean fndUser = false;
                    for(int i = 0; i < ownerIds.length; i++) {
                        int userid = user.getId();
                        int ownerid = ownerIds[i];
                        if ( userid == ownerid ) {
                            fndUser = true;
                        }
                    }
                    if ( fndUser ) {
                        iterator.remove();
                    }
                }
                
                for(Iterator iterator = users.iterator(); iterator.hasNext(); ) {
                    User usermodel = (User) iterator.next();
                        List<Permission> permissionModels = userService.getUserPermissionsLocal(usermodel);
                        for(Iterator iter = permissionModels.iterator(); iter.hasNext(); ) {
                            Permission permission = (Permission) iter.next();
                            if ( permission.getProject().getId() == project.getId()) {
                                iter.remove();
                            }
                        }
                        userService.setUserPermissions(usermodel.getId(), permissionModels);
                        userService.UpdateAuthenticator(usermodel.getId(), permissionModels);
                }
                
                projectService.setProjectOwners(project, owners);
                projectService.setProjectFields(project, fields);
                projectService.getProjectDAO().save(project);
            }
            session.removeAttribute(Constants.PROJECT_KEY);
            return mapping.findForward("listprojectsadmin");
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }
    
}
