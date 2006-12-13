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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.NotificationUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.AttachmentUtilities;
import org.itracker.web.util.Constants;

public class CreateIssueAction extends ItrackerBaseAction {
    
    public CreateIssueAction() {
    }
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.info("Invalid request token while creating issue.");
            ProjectService projectService = getITrackerServices().getProjectService();
            request.setAttribute("projects",projectService.getAllProjects());
            request.setAttribute("ph",projectService);
            return mapping.findForward("listprojects");
        }
        resetToken(request);
        
        try {
            IssueService issueService = getITrackerServices().getIssueService();
            ProjectService projectService = getITrackerServices().getProjectService();
            
            
            
            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
            Locale currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            Integer currUserId = currUser.getId();
            
            Project project = null;
            Integer projectId = (Integer) PropertyUtils.getSimpleProperty(form, "projectId");
            if(projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            } else {
                project = projectService.getProject(projectId);
            }
            
            if(errors.isEmpty() && project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            } else if(errors.isEmpty() && project.getStatus() != ProjectUtilities.STATUS_ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            } else if(! UserUtilities.hasPermission(userPermissionsMap, projectId, UserUtilities.PERMISSION_CREATE)) {
                return mapping.findForward("unauthorized");
            } else {
                List<ProjectScript> scripts = project.getScripts();
                WorkflowUtilities.ProcessFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, null, errors, (ValidatorForm) form);
                
                Issue issue = new Issue();
                issue.setDescription((String) PropertyUtils.getSimpleProperty(form, "description"));
                issue.setSeverity(((Integer) PropertyUtils.getSimpleProperty(form, "severity")).intValue());
                issue.setStatus(IssueUtilities.STATUS_NEW);
                
                Integer creator = currUserId;
                // TODO temporarily disabled creating issues as another user
                /*if(UserUtilities.hasPermission(currPermissions, projectId, UserUtilities.PERMISSION_CREATE_OTHERS)) {
                    creator = (Integer) PropertyUtils.getSimpleProperty(form, "creatorId");
                    logger.debug("New issue creator set to " + creator + ".  Issue created by " + currUserId);
                }*/
                issue = issueService.createIssue(issue, projectId, (creator == null ? currUserId : creator), currUserId);
                
                if(issue != null) {
                    Integer newOwner = (Integer) PropertyUtils.getSimpleProperty(form, "ownerId");
                    if(newOwner != null && newOwner.intValue() >= 0) {
                        if(UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_ASSIGN_OTHERS) ||
                                (UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_ASSIGN_SELF) &&
                                currUserId.equals(newOwner))) {
                            issueService.assignIssue(issue.getId(), newOwner, currUserId);
                        }
                    }
                    
                    List<IssueField> issueFields = new ArrayList<IssueField>();
                    HashMap customFields = (HashMap) PropertyUtils.getSimpleProperty(form, "customFields");
                    if(customFields != null && customFields.size() > 0) {
                        List<IssueField> issueFieldsVector = new ArrayList<IssueField>();
                        for(Iterator iter = customFields.keySet().iterator(); iter.hasNext(); ) {
                            try {
                                Integer fieldId = new Integer((String) iter.next());
                                CustomField field = IssueUtilities.getCustomField(fieldId);
                                String fieldValue = (String) PropertyUtils.getMappedProperty(form, "customFields(" + fieldId + ")");
                                if(fieldValue != null && ! fieldValue.equals("")) {
                                    IssueField issueField = new IssueField(field, issue);
                                    issueField.setValue(fieldValue, currLocale);
                                    issueFieldsVector.add(issueField);
                                }
                            } catch(Exception e) {
                            }
                        }
                        issueFields = new ArrayList<IssueField>();
                        issueFields = issueFieldsVector;
                    }
                    issueService.setIssueFields(issue.getId(), issueFields);
                    
                    
                    IssueHistory issueHistory = new IssueHistory((String) PropertyUtils.getSimpleProperty(form, "history"),
                            IssueUtilities.HISTORY_STATUS_AVAILABLE, issue, currUser);
                    issueHistory.setCreateDate(new Date());
                    issueService.addIssueHistory(issueHistory);
                    
                    HashSet<Integer> components = new HashSet<Integer>();
                    Integer[] componentIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "components");
                    if(componentIds != null) {
                        for(int i = 0; i < componentIds.length; i++) {
                            components.add(componentIds[i]);
                        }
                        issueService.setIssueComponents(issue.getId(), components, creator);
                    }
                    HashSet<Integer> versions = new HashSet<Integer>();
                    Integer[] versionIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "versions");
                    if(versionIds != null) {
                        for(int i = 0; i < versionIds.length; i++) {
                            versions.add(versionIds[i]);
                        }
                        issueService.setIssueVersions(issue.getId(), versions, creator);
                    }
                    
                    if(! ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions())) {
                        FormFile file = (FormFile) PropertyUtils.getSimpleProperty(form, "attachment");
                        if(file != null && ! "".equals(file.getFileName())) {
                            String origFileName = file.getFileName();
                            if(AttachmentUtilities.checkFile(file, this.getITrackerServices())) {
                                int lastSlash = Math.max(origFileName.lastIndexOf('/'), origFileName.lastIndexOf('\\'));
                                if(lastSlash > -1) {
                                    origFileName = origFileName.substring(lastSlash + 1);
                                }
                                
                                IssueAttachment attachmentModel = new IssueAttachment(origFileName,
                                        file.getContentType(),
                                        (String) PropertyUtils.getSimpleProperty(form, "attachmentDescription"),
                                        file.getFileSize(), issue, currUser);
                                issueService.addIssueAttachment(attachmentModel, file.getFileData());
                            }
                        }
                    }
                    
                    try {
                        Integer relatedIssueId = (Integer) PropertyUtils.getSimpleProperty(form, "relatedIssueId");
                        Integer relationType = (Integer) PropertyUtils.getSimpleProperty(form, "relationType");
                        if(relatedIssueId != null && relatedIssueId.intValue() > 0 && relationType != null && relationType.intValue() > 0) {
                            Issue relatedIssue = issueService.getIssue(relatedIssueId);
                            if(relatedIssue == null) {
                                logger.debug("Unknown relation issue, relation not created.");
                            } else if(relatedIssue.getProject() == null || ! IssueUtilities.canEditIssue(relatedIssue, currUserId, userPermissionsMap)) {
                                logger.info("User not authorized to add issue relation from issue " + issue.getId() + " to issue " + relatedIssueId);
                            } else if(IssueUtilities.hasIssueRelation(issue, relatedIssueId)) {
                                logger.debug("Issue " + issue.getId() + " is already related to issue " + relatedIssueId + ", relation ot created.");
                            } else {
                                if(! issueService.addIssueRelation(issue.getId(), relatedIssueId, relationType.intValue(), currUser.getId())) {
                                    logger.info("Error adding issue relation from issue " + issue.getId() + " to issue " + relatedIssueId);
                                }
                            }
                        }
                    } catch(Exception e) {
                        logger.debug("Exception adding new issue relation.", e);
                    }
                    
                    issueService.sendNotification(issue.getId(), NotificationUtilities.TYPE_CREATED, getBaseURL(request));
                }
                session.removeAttribute(Constants.PROJECT_KEY);
                
                WorkflowUtilities.ProcessFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, null, errors, (ValidatorForm) form);
                
                request.setAttribute("projects",projectService.getAllProjects());
                request.setAttribute("ph",projectService);
                
                String uri = mapping.findForward("listissues").getPath() + "?projectId=" + projectId;
                return new ActionForward(uri);
                
                // return mapping.findForward("listissues");
            }
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }
    
}
