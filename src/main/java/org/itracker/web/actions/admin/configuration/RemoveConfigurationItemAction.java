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

package org.itracker.web.actions.admin.configuration;

import java.io.IOException;
import java.util.List;

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
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.Issue;
import org.itracker.model.IssueActivity;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class RemoveConfigurationItemAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(RemoveConfigurationItemAction.class);
	
    public RemoveConfigurationItemAction() {
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

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            Integer configId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            if(configId == null || configId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }

            Configuration configItem = configurationService.getConfigurationItem(configId);
            if(configItem == null) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }

            String key = null;
            if(configItem.getType() == SystemConfigurationUtilities.TYPE_SEVERITY) {
                key = ITrackerResources.KEY_BASE_SEVERITY + configItem.getValue();

                // Need to promote all issues with the deleted severity.  The safest thing to do is
                // promote them to the next higher severity.
                try {
                    String currConfigValue = configItem.getValue();
                    String newConfigValue = null;

                    List<Configuration> configItems = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY);
                    for(int i = 0; i < configItems.size(); i++) {
                        if(configItems.get(i) != null && configId.equals(configItems.get(i).getId())) {
                            if(i == 0 && (i + 1) < configItems.size()) {
                                newConfigValue = configItems.get(i+1).getValue();
                                break;
                            } else if(i > 0) {
                                newConfigValue = configItems.get(i-1).getValue();
                                break;
                            }
                        }
                    }

                    int currSeverity = Integer.parseInt(currConfigValue);
                    int newSeverity = Integer.parseInt(newConfigValue);
                    log.debug("Promoting issues in severity " + IssueUtilities.getSeverityName(currSeverity) + " to " + IssueUtilities.getSeverityName(newSeverity));

                    HttpSession session = request.getSession(true);
                    User currUser = (User) session.getAttribute(Constants.USER_KEY);
                    Integer currUserId = (currUser == null ? new Integer(-1) : currUser.getId());

                    IssueService issueService = getITrackerServices().getIssueService();
                    List<Issue> issues = issueService.getIssuesWithSeverity(currSeverity);
                    for(int i = 0; i < issues.size(); i++) {
                        if(issues.get(i) != null) {
                            issues.get(i).setSeverity(newSeverity);
                            issues.add(issueService.updateIssue(issues.get(i), currUserId));
                            IssueActivity activity = new IssueActivity();
                            activity.setType(IssueUtilities.ACTIVITY_SYSTEM_UPDATE);
                            activity.setDescription(ITrackerResources.getString("itracker.activity.system.severity"));
                            //, issues.get(i).getId(), currUserId)
                            // TODO: need to fix this - RJST
                            //issueService.addIssueActivity(activity);
                        }
                    }
                } catch(Exception e) {
                    log.debug("Exception while promoting issues with severity " + configItem.getValue(), e);
                }
            } else if(configItem.getType() == SystemConfigurationUtilities.TYPE_STATUS) {
                key = ITrackerResources.KEY_BASE_STATUS + configItem.getValue();

                // Need to demote all issues with the deleted severity.  The safest thing to do is
                // move them down one status to make sure they don't skip something important in any
                // workflow.

                try {
                    String currConfigValue = configItem.getValue();
                    String newConfigValue = null;

                    List<Configuration> configItems = configurationService.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS);
                    for(int i = 0; i < configItems.size(); i++) {
                        if(configItems.get(i) != null && configId.equals(configItems.get(i).getId())) {
                            if(i == 0 && (i + 1) < configItems.size()) {
                                newConfigValue = configItems.get(i+1).getValue();
                                break;
                            } else if(i > 0) {
                                newConfigValue = configItems.get(i-1).getValue();
                                break;
                            }
                        }
                    }

                    int currStatus = Integer.parseInt(currConfigValue);
                    int newStatus = Integer.parseInt(newConfigValue);
                    log.debug("Promoting issues in status " + IssueUtilities.getStatusName(currStatus) + " to " + IssueUtilities.getStatusName(newStatus));

                    HttpSession session = request.getSession(true);
                    User currUser = (User) session.getAttribute(Constants.USER_KEY);
                    Integer currUserId = (currUser == null ? new Integer(-1) : currUser.getId());

                    IssueService issueService = getITrackerServices().getIssueService();
                    List<Issue> issues = issueService.getIssuesWithStatus(currStatus);
                    for(int i = 0; i < issues.size(); i++) {
                        if(issues.get(i) != null) {
                            issues.get(i).setStatus(newStatus);
                            issues.add(issueService.updateIssue(issues.get(i), currUserId));
                            IssueActivity activity = new IssueActivity();
                            activity.setType(IssueUtilities.ACTIVITY_SYSTEM_UPDATE);
                            activity.setDescription(ITrackerResources.getString("itracker.activity.system.status"));
                            // TODO fix this - RJST
                            //, issues.get(i).getId(), currUserId
                            //issueService.addIssueActivity(activity);
                        }
                    }
                } catch(Exception e) {
                    log.debug("Exception while promoting issues with status " + configItem.getValue(), e);
                }
            } else if(configItem.getType() == SystemConfigurationUtilities.TYPE_RESOLUTION) {
                key = ITrackerResources.KEY_BASE_RESOLUTION + configItem.getValue();

                // No need to edit any issues since the resolutions are stored as text in the issue
            } else {
                throw new SystemConfigurationException("Unsupported configuration item type " + configItem.getType() + " found.");
            }

            configurationService.removeConfigurationItem(configItem.getId());
            if(key != null) {
                configurationService.removeLanguageKey(key);
                ITrackerResources.clearKeyFromBundles(key, false);
            }

            return mapping.findForward("listconfiguration");
        } catch(SystemConfigurationException sce) {
            log.debug(sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
        } catch(NumberFormatException nfe) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
            log.debug("Invalid configuration item id " + request.getParameter("id") + " specified.");
        } catch(Exception e) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }

}
  