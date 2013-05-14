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

package org.itracker.web.actions.admin.workflow;

import bsh.ParseException;
import bsh.Parser;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.WorkflowScript;
import org.itracker.services.ConfigurationService;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.ServletContextUtils;
import org.springframework.dao.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringReader;


public class EditWorkflowScriptAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditWorkflowScriptAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing workflow script.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listworkflow");
        }
        resetToken(request);

        WorkflowScript workflowScript = null;

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

            // parse the file to check syntax
            String scriptData = (String) PropertyUtils.getSimpleProperty(form, "script");
            if (scriptData != null && scriptData.trim().length() > 0) {
                Parser parser = new Parser(new StringReader(scriptData));
                boolean eof;
                while (!(eof = parser.Line())) {
                }
            }

            workflowScript = new WorkflowScript();
            workflowScript.setId((Integer) PropertyUtils.getSimpleProperty(form, "id"));
            workflowScript.setName((String) PropertyUtils.getSimpleProperty(form, "name"));
            workflowScript.setEvent(((Integer) PropertyUtils.getSimpleProperty(form, "event")).intValue());
            workflowScript.setScript(scriptData);

            String action = (String) PropertyUtils.getSimpleProperty(form, "action");


            if ("create".equals(action)) {
                workflowScript = configurationService.createWorkflowScript(workflowScript);
            } else if ("update".equals(action)) {
                workflowScript = configurationService.updateWorkflowScript(workflowScript);
            }

            if (workflowScript == null) {
                throw new Exception("Error creating/updating workflow script.");
            }
            HttpSession session = request.getSession(true);
            session.removeAttribute(Constants.WORKFLOW_SCRIPT_KEY);
            request.setAttribute("action", action);
            saveToken(request);
            return new ActionForward(mapping.findForward("listworkflow").getPath() + "?id=" + workflowScript.getId() + "&action=update");
        } catch (ParseException pe) {
            log.info("Error parsing script.  Redisplaying form for correction.", pe);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidscriptdata", pe.getMessage()));
            saveErrors(request, errors);
            return toInputForward(request, mapping);
        } catch (DataAccessException dae) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message",
                    dae.getRootCause().getMessage(), "Data"));

            saveErrors(request, errors);
            return toInputForward(request, mapping);
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

    private ActionForward toInputForward(HttpServletRequest request, ActionMapping mapping) {
        EditWorkflowScriptFormAction.setupFormEeventTypes(request, getLocale(request));
        saveToken(request);
        return mapping.getInputForward();
    }

}
  