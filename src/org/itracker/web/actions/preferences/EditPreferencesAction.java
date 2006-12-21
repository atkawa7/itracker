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

package org.itracker.web.actions.preferences;

import java.io.IOException;

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
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.Constants;



/**
  * This class performas an update of the user's profile information based on their input.
  * Only the users core profile information, password, and preferences are updated, no permissions
  * can be updated from here.  Also each type of information is only updated, if it is allowed
  * by the current systems plugable authentication.
  */
public class EditPreferencesAction extends ItrackerBaseAction {

    public EditPreferencesAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Starting pref mod");
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing user preferences.");
            return mapping.findForward("index");
        }
        resetToken(request);

        User user = null;
        try {
            UserService userService = getITrackerServices().getUserService();

            HttpSession session = request.getSession();
            user = (User) session.getAttribute(Constants.USER_KEY);
            if(user == null) {
                return mapping.findForward("login");
            }

            User existingUser = userService.getUser(user.getId());
            if(existingUser == null || user.getId().intValue() != existingUser.getId().intValue()) {
                logger.debug("Unauthorized edit preferences request from " + user.getLogin() + "(" + user.getId() + ") for " + existingUser.getLogin() + "(" + existingUser.getId() + ")");
                return mapping.findForward("unauthorized");
            }
            UserForm userForm = (UserForm) form;

            errors = form.validate(mapping, request);

            if(errors.isEmpty()) {
                if(userService.allowPasswordUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    if(userForm.getPassword() != null && userForm.getPassword().trim().length() > 1) {
                        if(userForm.getCurrPassword() == null || "".equals(userForm.getCurrPassword())) {
                        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingpassword"));
                        } else {
                            try {
                                User passwordCheck = userService.checkLogin(user.getLogin(), userForm.getCurrPassword(), AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN, AuthenticationConstants.REQ_SOURCE_WEB);
                                if(passwordCheck == null) {
                                    throw new AuthenticatorException(AuthenticatorException.INVALID_DATA);
                                }
                                existingUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword().trim()));
                            } catch(AuthenticatorException ae) {
                            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.wrongpassword"));
                            }
                        }
                    }
                }

                if(userService.allowProfileUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    existingUser.setFirstName(userForm.getFirstName());
                    existingUser.setLastName(userForm.getLastName());
                    existingUser.setEmail(userForm.getEmail());
                }
            }

            if(errors.isEmpty()) {
                logger.debug("Passed required checks.  Updating user info for " + user.getLogin());
                user = userService.updateUser(existingUser);

                UserPreferences userPrefs = userService.getUserPreferencesByUserId(user.getId());
                if(userService.allowPreferenceUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    userPrefs.setUser(existingUser);

                    userPrefs.setUserLocale(userForm.getUserLocale());
                    userPrefs.setSaveLogin(("true".equals(userForm.getSaveLogin()) ? true : false));
                    try {
                        userPrefs.setNumItemsOnIndex(Integer.parseInt(userForm.getNumItemsOnIndex()));
                    } catch(NumberFormatException nfe) {
                        userPrefs.setNumItemsOnIndex(-1);
                    }
                    try {
                        userPrefs.setNumItemsOnIssueList(Integer.parseInt(userForm.getNumItemsOnIssueList()));
                    } catch(NumberFormatException nfe) {
                        userPrefs.setNumItemsOnIssueList(-1);
                    }
                    userPrefs.setShowClosedOnIssueList(("true".equals(userForm.getShowClosedOnIssueList()) ? true : false));
                    userPrefs.setSortColumnOnIssueList(userForm.getSortColumnOnIssueList());

                    int hiddenSections = 0;
                    Integer[] hiddenSectionsArray = userForm.getHiddenIndexSections();
                    if(hiddenSectionsArray != null) {
                        for(int i = 0; i < hiddenSectionsArray.length; i++) {
                            hiddenSections += hiddenSectionsArray[i].intValue();
                        }
                    }
                    userPrefs.setHiddenIndexSections(hiddenSections);

                    userPrefs.setRememberLastSearch(("true".equals(userForm.getRememberLastSearch()) ? true : false));
                    userPrefs.setUseTextActions(("true".equals(userForm.getUseTextActions()) ? true : false));

                    userPrefs = userService.updateUserPreferences(userPrefs);
                }

                session.setAttribute(Constants.USER_KEY, existingUser);
                session.setAttribute(Constants.PREFERENCES_KEY, userPrefs);
                session.setAttribute(Constants.LOCALE_KEY, ITrackerResources.getLocale(userPrefs.getUserLocale()));
                session.removeAttribute(Constants.EDIT_USER_KEY);
                session.removeAttribute(Constants.EDIT_USER_PREFS_KEY);
            }
        } catch(Exception e) {
            e.printStackTrace();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
        }

      	if(! errors.isEmpty()) {
      	    saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
      	}

        return mapping.findForward("index");
    }
}
  