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
import java.util.Map;
import java.util.Set;

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
import org.itracker.model.Component;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ComponentForm;
import org.itracker.web.util.Constants;


/**
 * Action for edit a component entity
 * 
 * @author ranks
 *
 */
public class EditComponentAction extends ItrackerBaseAction {
	private static final Logger log = Logger
			.getLogger(EditComponentAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	ActionMessages errors = new ActionMessages();
    	
		if (!isTokenValid(request)) {
			log.debug("Invalid request token while editing component.");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			"itracker.web.error.transaction"));
			saveErrors(request, errors);
			return mapping.findForward("listprojectsadmin");
		}
		resetToken(request);

		Component component = null;
		Project project = null;

		try {
			ComponentForm componentForm = (ComponentForm) form;
			ProjectService projectService = getITrackerServices()
					.getProjectService();

			HttpSession session = request.getSession(true);
			Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);

			Integer projectId = componentForm.getProjectId();

			if (projectId == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"itracker.web.error.invalidproject"));
			} else {
				project = projectService.getProject(projectId);
				if (project == null) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage(
									"itracker.web.error.invalidproject"));
				} else {
					boolean authorised = UserUtilities.hasPermission(
							userPermissionsMap, project.getId(),
							UserUtilities.PERMISSION_PRODUCT_ADMIN);

					if (!authorised) {
						return mapping.findForward("unauthorized");
					} else {
						String action = (String) request.getParameter("action");

						if ("create".equals(action)) {
							component = new Component(project, componentForm
									.getName());
							component.setDescription(componentForm
									.getDescription());
							component = projectService.addProjectComponent(
									project.getId(), component);
						} else if ("update".equals(action)) {
							component = projectService
									.getProjectComponent(componentForm.getId());
							component.setName(componentForm.getName());
							component.setDescription(componentForm
									.getDescription());
							component.setProject(project);
							component = projectService
									.updateProjectComponent(component);
						}
						session.removeAttribute(Constants.COMPONENT_KEY);

						return new ActionForward(mapping.findForward(
								"editproject").getPath()
								+ "?id=" + project.getId() + "&action=update");
					}
				}
			}
		} catch (RuntimeException ex) {
			log.error("Exception processing form data", ex);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}
		return mapping.findForward("error");
	}

}
