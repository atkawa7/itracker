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

//import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.WorkflowScript;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;

import bsh.ParseException;


public class EditWorkflowScriptAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(EditWorkflowScriptAction.class);
	
   
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
//        super.executeAlways(mapping,form,request,response);
//        if(! isLoggedIn(request, response)) {
//            return mapping.findForward("login");
//        }

        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        if(! isTokenValid(request)) {
            log.debug("Invalid request token while editing workflow script.");
            return mapping.findForward("listworkflow");
        }
        resetToken(request);

        WorkflowScript workflowScript = null;
        
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            String scriptData = (String) PropertyUtils.getSimpleProperty(form, "script");
            if ( scriptData != null && scriptData.trim().length() > 0 ) {
                //ByteArrayInputStream sbis = new ByteArrayInputStream(scriptData.getBytes());
                //Parser parser = new Parser(sbis);
//                try {
//                    while(!parser.Line()) {
                        // do nothing, if script is syntactically correct
                        // no exception is thrown
//                    }
//                } catch(Throwable t) {
//                    throw new ParseException(t.getMessage());
//                }
            }

            log.info("Kimba:  using this module action 1" );
            workflowScript = new WorkflowScript();
            workflowScript.setId((Integer) PropertyUtils.getSimpleProperty(form, "id"));
            workflowScript.setName((String) PropertyUtils.getSimpleProperty(form, "name"));
            workflowScript.setEvent(((Integer) PropertyUtils.getSimpleProperty(form, "event")).intValue());
            workflowScript.setScript(scriptData);

            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            log.info("Kimba:  using this module action 2"+action );
            if("create".equals(action)) {
                workflowScript = configurationService.createWorkflowScript(workflowScript);
            } else if ("update".equals(action)) {
                workflowScript = configurationService.updateWorkflowScript(workflowScript);
            }

            if (workflowScript == null) {
                throw new Exception("Error creating/updating workflow script.");
            }

            HttpSession session = request.getSession(true);
            session.removeAttribute(Constants.WORKFLOW_SCRIPT_KEY);
            request.setAttribute("action",action);
            saveToken(request);
//            return mapping.findForward("listworkflow");
            return new ActionForward(mapping.findForward("listworkflow").getPath() + "?id=" + workflowScript.getId() +"&action=update");
        } catch(ParseException pe) {
            log.debug("Error parseing script.  Redisplaying form for correction.", pe);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidscriptdata", pe.getMessage()));
            saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        } catch(Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }

}
  