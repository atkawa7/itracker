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
import org.itracker.web.forms.TaskForm;
import org.itracker.web.scheduler.SchedulerUtilities;
import org.itracker.web.util.Constants;


public class EditTaskFormAction extends ItrackerBaseAction {

    public EditTaskFormAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            
            SchedulerService schedulerService = getITrackerServices().getSchedulerService();
            HttpSession session = request.getSession(true);
            String action = (String) request.getParameter("action");
            Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
            if(! UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_USER_ADMIN)) {
                return mapping.findForward("unauthorized");
            }
            ScheduledTask task = (ScheduledTask) session.getAttribute(Constants.TASK_KEY);
            String pageTitleKey = "";
            String pageTitleArg = "";
            if(action != null && action.equals("update")) {
               pageTitleKey = "itracker.web.admin.edittask.title.update";
               pageTitleArg = task.getId().toString();
            } else {
               pageTitleKey = "itracker.web.admin.edittask.title.create";
            }
            request.setAttribute("pageTitleKey",pageTitleKey); 
            request.setAttribute("pageTitleArg",pageTitleArg); 


            task = null;

            TaskForm taskForm = (TaskForm) form;
            if(taskForm == null) {
                taskForm = new TaskForm();
            }

             
            if("create".equals(action)) {
                task = new ScheduledTask();
                task.setId(new Integer(-1));
                taskForm.setAction("create");
                taskForm.setId(task.getId());
            } else if ("update".equals(action)) {
                Integer taskId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                task = schedulerService.getTask(taskId);
                if(task == null) {
                	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidtask"));
                } else {
                    taskForm.setAction("update");
                    taskForm.setId(task.getId());
                    if(! task.isAll(task.getMonths())) {
                        taskForm.setMonths(task.joinString(task.getMonths()));
                    }
                    if(! task.isAll(task.getDaysOfMonth())) {
                        taskForm.setDaysOfMonth(task.joinString(task.getDaysOfMonth()));
                    }
                    if(! task.isAll(task.getHours())) {
                        taskForm.setHours(task.joinString(task.getHours()));
                    }
                    if(! task.isAll(task.getMinutes())) {
                        taskForm.setMinutes(task.joinString(task.getMinutes()));
                    }
                    if(! task.isAll(task.getWeekdays())) {
                        taskForm.setWeekdays(task.joinString(task.getWeekdays()));
                    }
                    if(SchedulerUtilities.getClassKey(task.getClassName()) != null) {
                        taskForm.setClassName(task.getClassName());
                    } else {
                        taskForm.setClassNameText(task.getClassName());
                    }
                    taskForm.setArgs(task.getArgsAsString());
                }
            } else {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if(errors.isEmpty()) {
                request.setAttribute("taskForm", taskForm);
                session.setAttribute(Constants.TASK_KEY, task);
                saveToken(request);
                return mapping.getInputForward();
            }
        } catch(Exception e) {
            logger.error("Exception while creating edit task form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  