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

package org.itracker.web.actions.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

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
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueField;
import org.itracker.model.NameValuePair;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.IssueService;
import org.itracker.services.UserService;
import org.itracker.services.util.Convert;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.HTMLUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.Constants;


/**
  * This class populates an IssueForm object for display by the edit issue page.
  */
public class EditIssueFormAction extends ItrackerBaseAction {

    public EditIssueFormAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        String pageTitleKey = "itracker.web.editissue.title"; 
		String pageTitleArg = request.getParameter("id");
		request.setAttribute("pageTitleKey",pageTitleKey); 
		request.setAttribute("pageTitleArg",pageTitleArg); 
        
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        try {
            IssueService issueService = getITrackerServices().getIssueService();
            request.setAttribute("ih",issueService);
            UserService userService = getITrackerServices().getUserService();

            Integer issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));

            Issue issue = issueService.getIssue(issueId);
            Project project = issueService.getIssueProject(issueId);

            if(issue == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidissue"));
            } else if(project == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            } else if(project.getStatus() != ProjectUtilities.STATUS_ACTIVE) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            } else {
                HttpSession session = request.getSession(true);
                User currUser = (User) session.getAttribute(Constants.USER_KEY);
                Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
                Locale currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);

                if(! IssueUtilities.canEditIssue(issue, currUser.getId(), userPermissions)) {
                        logger.debug("Unauthorized user requested access to edit issue for project " + project.getId());
                        return mapping.findForward("unauthorized");
                }

                if(errors.isEmpty()) {
                    Map<Integer, List<NameValuePair>> listOptions = new HashMap<Integer,List<NameValuePair>>();
                    List<NameValuePair> ownersList = new ArrayList<NameValuePair>();
                    
                    ownersList = GetIssuePossibleOwnersList(issue, project, currUser, currLocale, 
                                                             issueService, userService, userPermissions);

                    if ( ownersList != null && ownersList.size() > 0 )
                        listOptions.put(new Integer(IssueUtilities.FIELD_OWNER), ownersList);
                    
                    boolean hasFullEdit = UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_EDIT_FULL);

                    List<NameValuePair> allStatuses = IssueUtilities.getStatuses(currLocale);
                    List<NameValuePair> statusList = new ArrayList<NameValuePair>();
                    if(! hasFullEdit) {
                        if(issue.getStatus() >= IssueUtilities.STATUS_RESOLVED && UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_CLOSE)) {
                            for(int i = 0; i < allStatuses.size(); i++) {
                                int statusNumber = Integer.parseInt(allStatuses.get(i).getValue());
                                if(issue.getStatus() >= IssueUtilities.STATUS_CLOSED && statusNumber >= IssueUtilities.STATUS_CLOSED) {
                                    statusList.add(allStatuses.get(i));
                                } else if(issue.getStatus() >= IssueUtilities.STATUS_RESOLVED && statusNumber >= IssueUtilities.STATUS_RESOLVED) {
                                    statusList.add(allStatuses.get(i));
                                }
                            }
                        } else {
                              // Can't change
                        }
                    } else {
                        if(currUser.isSuperUser()) {
                            for(int i = 0; i < allStatuses.size(); i++) {
                                statusList.add(allStatuses.get(i));
                            }
                        } else if(issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED && issue.getStatus() < IssueUtilities.STATUS_RESOLVED) {
                            for(int i = 0; i < allStatuses.size(); i++) {
                                int statusNumber = Integer.parseInt(allStatuses.get(i).getValue());
                                if(statusNumber >= IssueUtilities.STATUS_ASSIGNED && statusNumber < IssueUtilities.STATUS_CLOSED) {
                                    statusList.add(allStatuses.get(i));
                                } else if(statusNumber >= IssueUtilities.STATUS_CLOSED && ProjectUtilities.hasOption(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE, project.getOptions()) && UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_CLOSE)) {
                                    statusList.add(allStatuses.get(i));
                                }
                            }
                        } else if(issue.getStatus() >= IssueUtilities.STATUS_RESOLVED && issue.getStatus() < IssueUtilities.STATUS_CLOSED) {
                            for(int i = 0; i < allStatuses.size(); i++) {
                                int statusNumber = Integer.parseInt(allStatuses.get(i).getValue());
                                if(statusNumber >= IssueUtilities.STATUS_ASSIGNED && statusNumber < IssueUtilities.STATUS_CLOSED) {
                                    statusList.add(allStatuses.get(i));
                                } else if(statusNumber >= IssueUtilities.STATUS_CLOSED && UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_CLOSE)) {
                                    statusList.add(allStatuses.get(i));
                                }
                            }
                        } else if(issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
                            for(int i = 0; i < allStatuses.size(); i++) {
                                int statusNumber = Integer.parseInt(allStatuses.get(i).getValue());
                                if((statusNumber >= IssueUtilities.STATUS_ASSIGNED && statusNumber < IssueUtilities.STATUS_RESOLVED) || statusNumber >= IssueUtilities.STATUS_CLOSED) {
                                    statusList.add(allStatuses.get(i));
                                }
                            }
                        } else {
                            // Can't change
                        }
                    }
                    List<NameValuePair> statuses = new ArrayList<NameValuePair>();
                    statuses = statusList;
                    listOptions.put(new Integer(IssueUtilities.FIELD_STATUS), statuses);

                    List<NameValuePair> severities = IssueUtilities.getSeverities(currLocale);
                    listOptions.put(new Integer(IssueUtilities.FIELD_SEVERITY), severities);

                    List<NameValuePair> resolutions = IssueUtilities.getResolutions(currLocale);
                    listOptions.put(new Integer(IssueUtilities.FIELD_RESOLUTION), resolutions);

                    List<Component> components = project.getComponents();
//                    Collections.sort(components, new Component());
                    listOptions.put(new Integer(IssueUtilities.FIELD_COMPONENTS), Convert.componentsToNameValuePairs(components));
                    
                    List<Version> versions = project.getVersions();
//                    Collections.sort(versions, new Version());
                    listOptions.put(new Integer(IssueUtilities.FIELD_VERSIONS), Convert.versionsToNameValuePairs(versions));
                    listOptions.put(new Integer(IssueUtilities.FIELD_TARGET_VERSION), Convert.versionsToNameValuePairs(versions));


                    List<CustomField> projectFields = project.getCustomFields();
                    for(int i = 0; i < projectFields.size(); i++) {
                        if(projectFields.get(i).getFieldType() == CustomField.Type.LIST) {
                            projectFields.get(i).setLabels(currLocale);
                            listOptions.put(projectFields.get(i).getId(), Convert.customFieldOptionsToNameValuePairs(projectFields.get(i).getOptions()));
                        }
                    }

                    IssueForm issueForm = (IssueForm) form;
                    if(issueForm == null) {
                        issueForm = new IssueForm();
                    }
                    issueForm.setId(issue.getId());
                    issueForm.setProjectId(issue.getProject().getId());
                    issueForm.setPrevStatus(new Integer(issue.getStatus()));
                    issueForm.setCaller(request.getParameter("caller"));

                    issueForm.setDescription(HTMLUtilities.handleQuotes(issue.getDescription()));
                    issueForm.setStatus(new Integer(issue.getStatus()));

                    if(! ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, project.getOptions())) {
                        try {
                            issue.setResolution(IssueUtilities.checkResolutionName(issue.getResolution(), currLocale));
                        } catch(MissingResourceException mre) {
                        	logger.error(mre.getMessage());
                        } catch(NumberFormatException nfe) {
                        	logger.error(nfe.getMessage());
                        }
                    }
                    issueForm.setResolution(HTMLUtilities.handleQuotes(issue.getResolution()));
                    issueForm.setSeverity(new Integer(issue.getSeverity()));
                    
                    issueForm.setTargetVersion(issue.getTargetVersion() == null 
                            ? -1 : issue.getTargetVersion().getId());
                    
                    issueForm.setOwnerId((issue.getOwner() == null ? -1 : issue.getOwner().getId()));

                    List<IssueField> fields = issue.getFields();
                    HashMap<Integer,String> customFields = new HashMap<Integer,String>();
                    for(int i = 0; i < fields.size(); i++) {
                        customFields.put(fields.get(i).getCustomField().getId(), fields.get(i).getValue(currLocale));
                    }
                    issueForm.setCustomFields(customFields);

                    HashSet selectedComponents = issueService.getIssueComponentIds(issueId);
                    if(selectedComponents != null) {
                        Integer[] componentIds = new Integer[selectedComponents.size()];
                        int i = 0;
                        for(Iterator iter = selectedComponents.iterator(); iter.hasNext(); i++) {
                            componentIds[i] = (Integer) iter.next();
                        }
                        issueForm.setComponents(componentIds);
                    }

                    HashSet selectedVersions = issueService.getIssueVersionIds(issueId);
                    if(selectedVersions != null) {
                        Integer[] versionIds = new Integer[selectedVersions.size()];
                        int i = 0;
                        for(Iterator iter = selectedVersions.iterator(); iter.hasNext(); i++) {
                            versionIds[i] = (Integer) iter.next();
                        }
                        issueForm.setVersions(versionIds);
                    }

                    List<ProjectScript> scripts = project.getScripts();
                    WorkflowUtilities.processFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONPOPULATE, listOptions, errors, issueForm);
                    WorkflowUtilities.processFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, null, errors, issueForm);

                    if(errors == null || errors.isEmpty()) {
                        logger.debug("Forwarding to edit issue form for issue " + issue.getId());

                        request.setAttribute("issueForm", issueForm);
                        session.setAttribute(Constants.ISSUE_KEY, issue);
                        session.setAttribute(Constants.LIST_OPTIONS_KEY, listOptions);
                        saveToken(request);
                        logger.info("EditIssueFormAction: Forward: InputForward");
                        return mapping.getInputForward();
                    }
                }
            }
        } catch(Exception e) {
            logger.error("Exception while creating edit issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        logger.info("EditIssueFormAction: Forward: Error");
        return mapping.findForward("error");
    }

}
  