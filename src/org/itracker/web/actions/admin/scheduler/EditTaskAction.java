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

package org.itracker.web.actions.admin.scheduler;

import java.io.IOException;
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
import org.itracker.model.PermissionType;
import org.itracker.model.ScheduledTask;
import org.itracker.services.SchedulerService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.scheduler.Scheduler;
import org.itracker.web.util.Constants;


public class EditTaskAction extends ItrackerBaseAction {

    public EditTaskAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing task.");
            return mapping.findForward("listtasks");
        }
        resetToken(request);

        HttpSession session = request.getSession(true);
        ScheduledTask task = null;
        try {

            SchedulerService schedulerService = getITrackerServices().getSchedulerService();

            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            if(! UserUtilities.hasPermission(userPermissions, UserUtilities.PERMISSION_USER_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            task = (ScheduledTask) session.getAttribute(Constants.TASK_KEY);

            task = new ScheduledTask();
            task.setId((Integer) PropertyUtils.getSimpleProperty(form, "id"));
            task.setMonths((String) PropertyUtils.getSimpleProperty(form, "months"));
            task.setDaysOfMonth((String) PropertyUtils.getSimpleProperty(form, "daysOfMonth"));
            task.setHours((String) PropertyUtils.getSimpleProperty(form, "hours"));
            task.setMinutes((String) PropertyUtils.getSimpleProperty(form, "minutes"));
            task.setWeekdays((String) PropertyUtils.getSimpleProperty(form, "weekdays"));

            String className = (String) PropertyUtils.getSimpleProperty(form, "className");
            if(className != null && ! className.equals("")) {
                try {
                    Class.forName( className );
                    task.setClassName(className);
                } catch ( ClassNotFoundException e ) {
                	 errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save" ) );
                    saveMessages(request, errors);
                    saveToken(request);
                    return mapping.getInputForward();
                }
            } else {
                String classNameText = (String) PropertyUtils.getSimpleProperty(form, "classNameText");
                try {
                    Class.forName( classNameText );
                    task.setClassName( classNameText );
                } catch ( ClassNotFoundException e ) {
                	 errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save" ) );
                    saveMessages(request, errors);
                    saveToken(request);
                    return mapping.getInputForward();
                }
            }
            task.setArgs((String) PropertyUtils.getSimpleProperty(form, "args"));

            String action = (String) request.getParameter("action");
            if("create".equals(action)) {
                task = schedulerService.createTask(task);
            } else if ("update".equals(action)) {
                task = schedulerService.updateTask(task);
            }

            if(task == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
            } else if("update".equals(action)) {
                Scheduler.removeTask(task.getId().intValue());
            }
            Scheduler.addTask(task);
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
        }

        if(! errors.isEmpty()) {
      	    saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        session.removeAttribute(Constants.TASK_KEY);
        return mapping.findForward("listtasks");
    }

}
  