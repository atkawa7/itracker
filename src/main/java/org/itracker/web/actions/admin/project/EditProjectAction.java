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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
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
import org.itracker.web.util.LoginUtilities;
import org.springframework.dao.DataAccessException;

//  TODO: Action Cleanup

public class EditProjectAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditProjectAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ActionMessages errors = new ActionMessages();

		if (!isTokenValid(request)) {
			log.debug("Invalid request token while editing project.");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.transaction"));
			saveErrors(request, errors);
			saveToken(request);
			return mapping.getInputForward();
//			return mapping.findForward("listprojectsadmin");
			
		}
		resetToken(request);

		Project project = null;
		try {
			ProjectService projectService = getITrackerServices()
					.getProjectService();
			UserService userService = getITrackerServices().getUserService();

			HttpSession session = request.getSession(true);
			User user = LoginUtilities.getCurrentUser(request);
			
			String action = (String) request.getParameter("action");

			if ("update".equals(action)) {

				Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
				
				project = projectService.getProject((Integer) PropertyUtils
						.getSimpleProperty(form, "id"));
				if (!UserUtilities.hasPermission(userPermissions, project
						.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
					return mapping.findForward("unauthorized");
				}
			} else {
				if (!user.isSuperUser()) {
					return mapping.findForward("unauthorized");
				}

				project = new Project();
			}
			project.setDescription((String) PropertyUtils.getSimpleProperty(
					form, "description"));
			project.setName((String) PropertyUtils.getSimpleProperty(form,
					"name"));
			Integer projectStatus = (Integer) PropertyUtils.getSimpleProperty(
					form, "status");
			
			String projectName = (String) PropertyUtils.getSimpleProperty(form,	"name");
			if (projectService.isUniqueProjectName(projectName, project.getId())) {
				project.setName(projectName);
			} else {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.project.duplicate.name"));
//				throw new ProjectException(
//						"Project already exist with this name.");
			}
			
			if (projectStatus != null) {
				project.setStatus(Status.valueOf(projectStatus));
			} else {
				project.setStatus(Status.ACTIVE);
			}

			Integer[] optionValues = (Integer[]) PropertyUtils
					.getSimpleProperty(form, "options");
			int optionmask = 0;
			if (optionValues != null) {
				for (int i = 0; i < optionValues.length; i++) {
					optionmask += optionValues[i].intValue();
				}
			}
			project.setOptions(optionmask);

			Integer[] fieldsArray = (Integer[]) PropertyUtils.getSimpleProperty(form, "fields");
			HashSet<Integer> fields = null == fieldsArray? new HashSet<Integer>(0):
				new HashSet<Integer>(Arrays.asList(fieldsArray));

			Integer[] ownersArray = (Integer[]) PropertyUtils.getSimpleProperty(form, "owners");
			HashSet<Integer> ownerIds = null == ownersArray ? new HashSet<Integer>():
				new HashSet<Integer>(Arrays.asList(ownersArray));

			if (errors.isEmpty()) {
				if ("create".equals(action)) {
	
					project = projectService.createProject(project, user.getId());
					
					if (log.isDebugEnabled()) {
						log.debug("execute: created new project: " + project);
					}
	
					Integer[] users = (Integer[]) PropertyUtils.getSimpleProperty(form, "users");
					if (users != null)
					{
						// get the initial project members from create-form
						Set<Integer> userIds = new HashSet<Integer>(Arrays
								.asList(users));
						// get the  permissions-set for initial project members
						Integer[] permissionArray = (Integer[]) PropertyUtils.getSimpleProperty(form, "permissions");
						Set<Integer> permissions = null == permissionArray ? new HashSet<Integer>(0): 
							new HashSet<Integer>(Arrays.asList(permissionArray));
		
						// if admin-permission is selected, all permissions will be granted and users added as project owners
						if (permissions.contains(UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
							ownerIds.addAll(userIds);
						} else {
							// handle special initial user-/permissions-set
							handleInitialProjectMembers(project, userIds, permissions,
									projectService, userService);
						}
		
						// set project owners with all permissions
						updateProjectOwners(project, ownerIds, projectService, userService);
					}
	
					if (log.isDebugEnabled()) {
						log.debug("execute: updating new project: " + project);
					}
					project = projectService.updateProject(project, user.getId());
	
				} else if ("update".equals(action)) {
	
					updateProjectOwners(project, ownerIds, projectService, userService);
					
					if (log.isDebugEnabled()) {
						log.debug("execute: updating existing project: " + project);
					}
					project = projectService.updateProject(project, user.getId());
				}
				
				session.removeAttribute(Constants.PROJECT_KEY);
			}

			if (errors.isEmpty()) {
				projectService.setProjectFields(project, fields);
				
				session.removeAttribute(Constants.PROJECT_KEY);
			}

			if (errors.isEmpty()) {
				if (log.isDebugEnabled()) {
					log.debug("execute: sucess, forward to listprojectsadmin");
				}
				return mapping.findForward("listprojectsadmin");
			} else {

				saveErrors(request, errors);

				saveToken(request);
				return mapping.getInputForward();
			}
				
		} catch (DataAccessException dae) {
			log.info("execute: Exception processing form data", dae);
			throw dae;
		} catch (RuntimeException e) {
			log.error("execute: Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		} catch (IllegalAccessException e) {
			log.error("execute: Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		} catch (InvocationTargetException e) {
			log.error("execute: Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		} catch (NoSuchMethodException e) {
			log.error("execute: Exception processing form data", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		}

		if (!errors.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("execute: got errors in action-messages: " + errors);
			}
			saveErrors(request, errors);
		}
		if (log.isDebugEnabled()) {
			log.debug("execute: failed, forward to error");
		}
		return mapping.findForward("error");
	}

	/**
	 * When creating project, initial set of users with specific set of rights
	 * can be defined.
	 * 
	 * @param project
	 * @param userIds
	 * @param permissions
	 * @param projectService
	 * @param userService
	 */
	private void handleInitialProjectMembers(Project project,
			Set<Integer> userIds, Set<Integer> permissions,
			ProjectService projectService, UserService userService) {
		Set<Permission> userPermissionModels;
		if (userIds != null && permissions != null && userIds.size() > 0
				&& permissions.size() > 0) {

			Set<User> users = new HashSet<User>(userIds.size());
			Iterator<Integer> usersIt = userIds.iterator();

			while (usersIt.hasNext()) {
				users.add(userService.getUser(usersIt.next()));
			}

			// process member-users
			Iterator<User> iterator = users.iterator(); 
			while (iterator.hasNext()) {
				User usermodel = (User) iterator.next();
				userPermissionModels = new HashSet<Permission>(userService.getUserPermissionsLocal(usermodel));

				// add all needed permissions
				Iterator<Integer> permssionsIt = permissions.iterator();
				while (permssionsIt.hasNext()) {
					userPermissionModels.add(new Permission(
							permssionsIt.next(), usermodel, project));
				}

				// save the permissions
				userService.setUserPermissions(usermodel.getId(),
						new ArrayList<Permission>(userPermissionModels));
				userService.updateAuthenticator(usermodel.getId(),
						new ArrayList<Permission>(userPermissionModels));

			}
		}

	}


	/**
	 * setup permissions for updated project-owners
	 * 
	 * @param project
	 * @param userIds
	 * @param userService
	 * @param locale
	 */
	private static final void updateProjectOwners(Project project,
			Set<Integer> userIds, ProjectService projectService,
			UserService userService) {
		Iterator<Integer> userIdsIt = userIds.iterator();
		Set<Permission> userPermissionModels;
		
		if (log.isDebugEnabled()) {
			log.debug("updateProjectOwners: setting new owners: " + userIds);
		}
		
		// cleanup current owners permissions
		// TODO: needed? If user is no more owner, he can still be admin
//		Collection<User> currentOwners = projectService
//				.getProjectOwners(project.getId());

//		Iterator<User> currentOwnersIt = currentOwners.iterator();
//		while (currentOwnersIt.hasNext()) {
//			User user = (User) currentOwnersIt.next();
//			Iterator<Permission> currentPermissionsIt = userService.getUserPermissionsLocal(user).iterator();
//			while (currentPermissionsIt.hasNext()) {
//				Permission permission = (Permission) currentPermissionsIt
//						.next();
//				if (project.equals(permission.getProject()) && 
//						permission.getPermissionType() == UserUtilities.PERMISSION_PRODUCT_ADMIN) {
//					user.getPermissions().remove(permission);
//				}
//				userService.setUserPermissions(user.getId(), user.getPermissions());
//			}
//		}
		
		// remove all owners
//		projectService.setProjectOwners(project, new HashSet<Integer>(0));

		// add all defined owners to project
		while (userIdsIt.hasNext()) {
			User usermodel = userService.getUser(userIdsIt.next());
			Iterator<Integer> permissionIt = UserUtilities.ALL_PERMISSIONS_SET
					.iterator();
			boolean newPermissions = false;
			userPermissionModels = new HashSet<Permission>(userService
					.getUserPermissionsLocal(usermodel));
			if (log.isDebugEnabled()) {
				log.debug("updateProjectOwners: setting owner " + usermodel + " to " + project);
			}
			while (permissionIt.hasNext()) {
				Integer superUserPermission = permissionIt.next();
				if (userPermissionModels.add(new Permission(superUserPermission,
						usermodel, project))) {
					newPermissions = true;
				}
			}
			if (newPermissions) {
				userService.addUserPermissions(usermodel.getId(),
						new ArrayList<Permission>(userPermissionModels));
				if (log.isDebugEnabled()) {
					log.debug("updateProjectOwners: updated permissions for " + usermodel + " to " + userPermissionModels);
				}
			}
		}
		projectService.setProjectOwners(project, userIds);

	}

}
