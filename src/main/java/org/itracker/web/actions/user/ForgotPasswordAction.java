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

package org.itracker.web.actions.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.PasswordException;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;

public class ForgotPasswordAction extends ItrackerBaseAction {

    public ForgotPasswordAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            UserService userService = getITrackerServices().getUserService();
            
            if(! configurationService.getBooleanProperty("allow_forgot_password", true)) {
                throw new PasswordException(PasswordException.FEATURE_DISABLED);
            }

            String login = (String) PropertyUtils.getSimpleProperty(form, "login");
            String lastName = (String) PropertyUtils.getSimpleProperty(form, "lastName");

            if(login != null && lastName != null && ! login.equals("") && ! lastName.equals("")) {
                User user = null;
                try {
                    user = userService.getUserByLogin(login);
                    if(user == null) {
                        throw new PasswordException(PasswordException.UNKNOWN_USER);
                    }
                    if(user.getLastName() == null || ! user.getLastName().equalsIgnoreCase(lastName)) {
                        throw new PasswordException(PasswordException.INVALID_NAME);
                    }
                    if(user.getEmail() == null || user.getEmail().equals("")) {
                        throw new PasswordException(PasswordException.INVALID_EMAIL);
                    }
                    if(user.getStatus() != UserUtilities.STATUS_ACTIVE) {
                        throw new PasswordException(PasswordException.INACTIVE_ACCOUNT);
                    }

                    if(logger.isDebugEnabled()) {
                        logger.debug("ForgotPasswordHandler found matching user: " + user.getFirstName() + " " + user.getLastName() + "(" + user.getLogin() + ")");
                    }

                    String subject = ITrackerResources.getString("itracker.email.forgotpass.subject");
                    StringBuffer msgText = new StringBuffer();
                    msgText.append(ITrackerResources.getString("itracker.email.forgotpass.body"));
                    msgText.append(ITrackerResources.getString("itracker.web.attr.password") + ": " + userService.generateUserPassword(user));
                    
                    getITrackerServices().getEmailService()
                        .sendEmail(user.getEmail(), subject, msgText.toString());
                } catch(PasswordException pe) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("Password Exception for user " + (login != null ? login : "UNKNOWN") + ". Type = " + pe.getType());
                    }
                    if(pe.getType() == PasswordException.INVALID_NAME) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.lastname"));
                    } else if(pe.getType() == PasswordException.INVALID_EMAIL) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.invalidemail"));
                    } else if(pe.getType() == PasswordException.INACTIVE_ACCOUNT) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.inactive"));
                    } else if(pe.getType() == PasswordException.UNKNOWN_USER) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.unknown"));
                    }
                }
            }
        } catch(PasswordException pe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.notenabled"));
            logger.error("Forgot Password function has been disabled.", pe);
        } catch(Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.system"));
            logger.error("Error during password retrieval.", e);
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            return (mapping.getInputForward());
        }

        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.message.forgotpass"));
        saveMessages(request, errors);
        return mapping.findForward("login");
    }

}
  