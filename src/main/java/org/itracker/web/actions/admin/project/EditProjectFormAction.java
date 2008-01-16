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
import org.itracker.model.CustomField;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectForm;
import org.itracker.web.util.Constants;



public class EditProjectFormAction extends ItrackerBaseAction {

    public EditProjectFormAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            ProjectService projectService = getITrackerServices().getProjectService();
            UserService userService = getITrackerServices().getUserService();
            request.setAttribute("ph",projectService);
            request.setAttribute("uh",userService);
            HttpSession session = request.getSession(true);
            String action = (String) request.getParameter("action");
            
            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            User user = (User) session.getAttribute(Constants.USER_KEY);

            String pageTitleKey = "";
            String pageTitleArg = "";
            Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
            if(action != null && action.equals("update")) {
                 pageTitleKey = "itracker.web.admin.editproject.title.update";
                 // there was a problem with project.getName(); temp. commmented. 
                 //pageTitleArg = project.getName();
                 
            } else {
                pageTitleKey = "itracker.web.admin.editproject.title.create";
           //     pageTitleArg = ITrackerResources.getString("itracker.locale.name", parentLocale);
                pageTitleArg = ITrackerResources.getString("itracker.locale.name", this.getCurrLocale());
                
            }
           
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg); 
            
            project = null;
            ProjectForm projectForm = (ProjectForm) form;
            if(projectForm == null) {
                projectForm = new ProjectForm();
            }


            if("create".equals(action)) {
                if(! user.isSuperUser()) {
                    return mapping.findForward("unauthorized");
                }
                boolean allowPermissionUpdate = userService.allowPermissionUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
                request.setAttribute("allowPermissionUpdate", allowPermissionUpdate);
                project = new Project();
                project.setId(new Integer(-1));
                projectForm.setAction("create");
                projectForm.setId(project.getId());
            } else if ("update".equals(action)) {
                Integer projectId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                if(projectId == null) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
                } else {
                    project = projectService.getProject(projectId);
                    if(project == null) {
                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
                    } else if(! UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                        return mapping.findForward("unauthorized");
                    } else {
                        projectForm.setAction("update");
                        projectForm.setId(project.getId());
                        projectForm.setName(project.getName());
                        projectForm.setDescription(project.getDescription());
                        projectForm.setStatus(project.getStatus().getCode());
                        int currentOptions = project.getOptions();
                        projectForm.setOptions(ProjectUtilities.getOptions(currentOptions));

                        List<CustomField> fields = project.getCustomFields();
                        Integer[] fieldIds = new Integer[fields.size()];
                        for(int i = 0; i < fields.size(); i++) {
                            fieldIds[i] = fields.get(i).getId();
                        }
                        projectForm.setFields(fieldIds);

                        List<User> owners = project.getOwners();
                        Integer[] ownerIds = new Integer[owners.size()];
                        for(int i = 0; i < owners.size(); i++) {
                            ownerIds[i] = owners.get(i).getId();
                        }
                        projectForm.setOwners(ownerIds);
                    }
                }
            } else {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if(errors.isEmpty()) {
                request.setAttribute("projectForm", projectForm);
                session.setAttribute(Constants.PROJECT_KEY, project);
                saveToken(request);
                return mapping.getInputForward();
            }
        } catch(Exception e) {
            logger.error("Exception while creating edit project form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  