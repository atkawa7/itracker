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
 
import java.util.HashMap;
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
import org.itracker.model.User;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.Constants;
 

public class EditUserFormAction extends ItrackerBaseAction {

    public EditUserFormAction() {
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

        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute(Constants.USER_KEY);
        String action = (String) request.getParameter("action"); 
        String pageTitleKey = "";
        String pageTitleArg = "";
        boolean isUpdate = false;
        
        if(action != null && action.equals("update")) {
        	 isUpdate = true;
        	 request.setAttribute("isUpdate",new Boolean(isUpdate)); 
        	 pageTitleKey = "itracker.web.admin.edituser.title.update";
             pageTitleArg = user.getLogin();
             request.setAttribute("pageTitleKey",pageTitleKey); 
             request.setAttribute("pageTitleArg",pageTitleArg); 
        } else {
        	 request.setAttribute("isUpdate",new Boolean(isUpdate)); 
            pageTitleKey = "itracker.web.admin.edituser.title.create";
       //     pageTitleArg = ITrackerResources.getString("itracker.locale.name", parentLocale);
        //    pageTitleArg = ITrackerResources.getString("itracker.locale.name", this.getCurrLocale());          
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg); 
        }
     
      
        try {
            UserService userService = getITrackerServices().getUserService();
            request.setAttribute("uh",userService);
            ProjectService projectService = getITrackerServices().getProjectService();
            request.setAttribute("ph",projectService);
            User editUser = null;
            HashMap<Integer,HashMap<Integer,Permission>> userPermissions = new HashMap<Integer,HashMap<Integer,Permission>>();
            UserForm userForm = (UserForm) form;
            
            if(userForm == null) {
                userForm = new UserForm();
            }

           
            if("create".equals(action)) {
                if(! userService.allowProfileCreation(null, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofilecreates"));
                    saveMessages(request, errors);
                    return mapping.findForward("error");
                }

                editUser = new User();
                editUser.setId(new Integer(-1));
                editUser.setStatus(UserUtilities.STATUS_ACTIVE);
                userForm.setAction("create");
                userForm.setId(editUser.getId());
            } else if ("update".equals(action)) {
                Integer userId = userForm.getId();
                if(userId == null) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invaliduser"));
                } else {
                    editUser = userService.getUser(userId);
                    if(editUser == null) {
                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invaliduser"));
                    } else {
                        userForm.setAction("update");
                        userForm.setId(editUser.getId());
                        userForm.setLogin(editUser.getLogin());
                        userForm.setFirstName(editUser.getFirstName());
                        userForm.setLastName(editUser.getLastName());
                        userForm.setEmail(editUser.getEmail());
                        userForm.setSuperUser(editUser.isSuperUser());

                        List<Permission> permissionList = userService.getPermissionsByUserId(editUser.getId());
                        HashMap<String,String> formPermissions = new HashMap<String,String>();
              
                        for(int i = 0; i < permissionList.size(); i++) {
                        	logger.debug("Processing permission type: "+permissionList.get(i).getPermissionType());
                     
                            //if getPermissionType returned -1, this is a SuperUser. He will still be able to set project permissions.  
                      
                        	if (permissionList.size()>0 && permissionList.get(0).getPermissionType()==-1) {
                        		if (permissionList.size()>1 && i!=0){
                        			Integer projectId = permissionList.get(i).getProject().getId();
                                    if(userPermissions.get(projectId) == null) {
                                        HashMap<Integer,Permission> projectPermissions = new HashMap<Integer,Permission>();
                                        userPermissions.put(permissionList.get(i).getProject().getId(), projectPermissions);
                                    }
                                    formPermissions.put("Perm" + permissionList.get(i).getPermissionType() + "Proj" + permissionList.get(i).getProject().getId(), "on");
         
                                    Integer permissionType = permissionList.get(i).getPermissionType();
                                    
                                    Permission thisPermission = permissionList.get(i);
                                    HashMap<Integer,Permission> permissionHashMap = ((HashMap<Integer,Permission>) userPermissions.get(projectId));
                                    permissionHashMap.put(permissionType,thisPermission);
                        		}
                        	} else {
                        		Integer projectId = permissionList.get(i).getProject().getId();
                                if(userPermissions.get(projectId) == null) {
                                    HashMap<Integer,Permission> projectPermissions = new HashMap<Integer,Permission>();
                                    userPermissions.put(permissionList.get(i).getProject().getId(), projectPermissions);
                                }
                                formPermissions.put("Perm" + permissionList.get(i).getPermissionType() + "Proj" + permissionList.get(i).getProject().getId(), "on");
     
                                Integer permissionType = permissionList.get(i).getPermissionType();
                                
                                Permission thisPermission = permissionList.get(i);
                                HashMap<Integer,Permission> permissionHashMap = ((HashMap<Integer,Permission>) userPermissions.get(projectId));
                                permissionHashMap.put(permissionType,thisPermission);
                        	}
                        	
                        	
                        }
                      
                        userForm.setPermissions(formPermissions);
                    }
                }
            } else {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if(errors.isEmpty()) {
                request.setAttribute("userForm", userForm);
                session.setAttribute(Constants.EDIT_USER_KEY, editUser);
                session.setAttribute(Constants.EDIT_USER_PERMS_KEY, userPermissions);
                saveToken(request);
                return mapping.findForward("edituserform");
            }
        } catch(Exception e) {
            logger.error("Exception while creating edit user form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  