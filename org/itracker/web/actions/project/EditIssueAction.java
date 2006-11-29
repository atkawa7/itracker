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
import org.itracker.model.IssueAttachment;
import org.itracker.model.Issue;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.NotificationUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.AttachmentUtilities;
import org.itracker.web.util.Constants;

public class EditIssueAction extends ItrackerBaseAction {

    public EditIssueAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        
        if(! isLoggedIn(request, response)) {
        	logger.info("EditIssueAction: Forward: login");
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while editing issue.");
            ProjectService projectService = getITrackerServices().getProjectService();
            request.setAttribute("projects",projectService.getAllProjects());
            request.setAttribute("ph",projectService);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.transaction" ) );
            saveMessages( request, errors ); 	
            logger.info("EditIssueAction: Forward: listprojects");
            return mapping.findForward("listprojects");
        }
        resetToken(request);

        try {
            IssueService issueService = getITrackerServices().getIssueService();

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
            Locale currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            Integer currUserId = currUser.getId();

            Integer issueId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            if(issueId == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidissue"));
            	logger.info("EditIssueAction: Forward: Error");
                return mapping.findForward("error");
            }

            int previousStatus = -1;
            Issue issue = issueService.getIssue(issueId);

            if(issue == null || issue.getId() == null || issue.getId() < 0) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidissue"));
            	logger.info("EditIssueAction: Forward: Error");
                return mapping.findForward("error");
            }

            Project project = issue.getProject();
            if(project == null) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            	logger.info("EditIssueAction: Forward: Error");
            	return mapping.findForward("error");
            } else if(project.getStatus() != ProjectUtilities.STATUS_ACTIVE) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            	logger.info("EditIssueAction: Forward: Error");
            	return mapping.findForward("error");
            } else if(! IssueUtilities.canEditIssue(issue, currUserId, userPermissions)) {
            	logger.info("EditIssueAction: Forward: unauthorized");
            	return mapping.findForward("unauthorized");
            }

            List<ProjectScript> scripts = project.getScripts();
            WorkflowUtilities.ProcessFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, null, errors, (ValidatorForm) form);

            if(UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_EDIT_FULL)) {
                previousStatus = issue.getStatus();
                processFullEdit(issue, project, currUser, userPermissions, currLocale, form, issueService);
            } else {
                previousStatus = issue.getStatus();
                processLimitedEdit(issue, project, currUser, userPermissions, currLocale, form, issueService);
            }

            sendNotification(issue, previousStatus, getBaseURL(request), issueService);
            session.removeAttribute(Constants.ISSUE_KEY);

            WorkflowUtilities.ProcessFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, null, errors, (ValidatorForm) form);

            ProjectService projectService = getITrackerServices().getProjectService();
            request.setAttribute("projects",projectService.getAllProjects());
            request.setAttribute("ph",projectService);
            
            return getReturnForward(issue, project, form, mapping);
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
      	    saveMessages(request, errors);
            saveToken(request);
            return mapping.findForward("editissueform");
        }
        logger.info("EditIssueAction: Forward: Error");
        return mapping.findForward("error");
    }

    private void processFullEdit(Issue issue, Project project, User user, Map<Integer, Set<PermissionType>> userPermissions, Locale locale, ActionForm form, IssueService issueService) throws Exception {
        int previousStatus = issue.getStatus();

        issue.setDescription((String) PropertyUtils.getSimpleProperty(form, "description"));
        issue.setResolution((String) PropertyUtils.getSimpleProperty(form, "resolution"));
        issue.setSeverity(((Integer) PropertyUtils.getSimpleProperty(form, "severity")).intValue());

        Integer targetVersionId = (Integer) PropertyUtils.getSimpleProperty(form, "targetVersion");
        if(targetVersionId != null && targetVersionId.intValue() != -1) {
            ProjectService projectService = getITrackerServices().getProjectService();
            Version targetVersion = projectService.getProjectVersion(targetVersionId);
            
            if (targetVersion == null) {
                throw new RuntimeException("No version with Id " + targetVersionId);
            }
            issue.setTargetVersion(targetVersion);
        }

        Integer formStatus = (Integer) PropertyUtils.getSimpleProperty(form, "status");
        if(formStatus != null) {
            issue.setStatus(formStatus.intValue());
        }

        if(previousStatus != -1) {
            // Reopened the issue.  Reset the resolution field.
            if((issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED && issue.getStatus() < IssueUtilities.STATUS_RESOLVED) &&
               (previousStatus >= IssueUtilities.STATUS_RESOLVED && previousStatus < IssueUtilities.STATUS_END)) {
                issue.setResolution("");
            }

            if(issue.getStatus() >= IssueUtilities.STATUS_CLOSED && ! UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_CLOSE)) {
                if(previousStatus < IssueUtilities.STATUS_CLOSED) {
                    issue.setStatus(previousStatus);
                } else {
                    issue.setStatus(IssueUtilities.STATUS_RESOLVED);
                }
            }

            if(issue.getStatus() < IssueUtilities.STATUS_NEW || issue.getStatus() >= IssueUtilities.STATUS_END) {
                issue.setStatus(previousStatus);
            }
        } else if(issue.getStatus() >= IssueUtilities.STATUS_CLOSED && ! UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_CLOSE)) {
            issue.setStatus(IssueUtilities.STATUS_RESOLVED);
        }

        if(issue.getStatus() < IssueUtilities.STATUS_NEW) {
            issue.setStatus(IssueUtilities.STATUS_NEW);
        } else if(issue.getStatus() >= IssueUtilities.STATUS_END) {
            if(! UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_CLOSE)) {
                issue.setStatus(IssueUtilities.STATUS_RESOLVED);
            } else {
                issue.setStatus(IssueUtilities.STATUS_CLOSED);
            }
        }

        issue = issueService.updateIssue(issue, user.getId());
        
        if (issue != null) {
            setIssueFields(issue, user, locale, form, issueService);
            setOwner(issue, user, userPermissions, form, issueService);
            addHistoryEntry(issue, user, form, issueService);

            HashSet<Integer> components = new HashSet<Integer>();
            Integer[] componentIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "components");
            if(componentIds != null) {
                for(int i = 0; i < componentIds.length; i++) {
                    components.add(componentIds[i]);
                }
                issueService.setIssueComponents(issue.getId(), components, user.getId());
            }
            HashSet<Integer> versions = new HashSet<Integer>();
            Integer[] versionIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "versions");
            if(versionIds != null) {
                for(int i = 0; i < versionIds.length; i++) {
                    versions.add(versionIds[i]);
                }
                issueService.setIssueVersions(issue.getId(), versions, user.getId());
            }

            addAttachment(issue, project, user, form, issueService);
        }
    }

    private void processLimitedEdit(Issue issue, Project project, User user, Map<Integer, Set<PermissionType>> userPermissions, Locale locale, ActionForm form, IssueService issueService) throws Exception {
        issue.setDescription((String) PropertyUtils.getSimpleProperty(form, "description"));

        Integer formStatus = (Integer) PropertyUtils.getSimpleProperty(form, "status");
        if(formStatus != null) {
            int newStatus = formStatus.intValue();
            if(issue.getStatus() >= IssueUtilities.STATUS_RESOLVED && newStatus >= IssueUtilities.STATUS_CLOSED &&
               UserUtilities.hasPermission(userPermissions, UserUtilities.PERMISSION_CLOSE)) {
                issue.setStatus(newStatus);
            }
        }

        issue = issueService.updateIssue(issue, user.getId());

        setIssueFields(issue, user, locale, form, issueService);
        setOwner(issue, user, userPermissions, form, issueService);
        addHistoryEntry(issue, user, form, issueService);
        addAttachment(issue, project, user, form, issueService);
    }

    private void setOwner(Issue issue, User user, Map<Integer, Set<PermissionType>> userPermissions, ActionForm form, IssueService issueService) throws Exception {
        Integer currentOwner = (issue.getOwner() == null) ? null : issue.getOwner().getId();
        Integer ownerId = (Integer) PropertyUtils.getSimpleProperty(form, "ownerId");
        
        if(ownerId != null && !ownerId.equals(currentOwner)) {
            if(UserUtilities.hasPermission(userPermissions, UserUtilities.PERMISSION_ASSIGN_OTHERS) ||
               (UserUtilities.hasPermission(userPermissions, UserUtilities.PERMISSION_ASSIGN_SELF) && user.getId().equals(ownerId)) ||
               (UserUtilities.hasPermission(userPermissions, UserUtilities.PERMISSION_UNASSIGN_SELF) && user.getId().equals(currentOwner) && ownerId.intValue() == -1)
              ) {
                issueService.assignIssue(issue.getId(), ownerId, user.getId());
            }
        }
    }

    private void setIssueFields(Issue issue, User user, Locale locale, ActionForm form, IssueService issueService) throws Exception {
        List<IssueField> issueFields = new ArrayList<IssueField>();
        HashMap customFields = (HashMap) PropertyUtils.getSimpleProperty(form, "customFields");
        if(customFields != null && customFields.size() > 0) {
            List<IssueField> issueFieldsList = new ArrayList<IssueField>();
            for(Iterator iter = customFields.keySet().iterator(); iter.hasNext(); ) {
                try {
                    Integer fieldId = new Integer((String) iter.next());
                    CustomField field = IssueUtilities.getCustomField(fieldId);
                    String fieldValue = (String) PropertyUtils.getMappedProperty(form, "customFields(" + fieldId + ")");
                    if(fieldValue != null && ! fieldValue.equals("")) {
                        IssueField issueField = new IssueField(field, issue);
                        issueField.setValue(fieldValue, locale);
                        issueFieldsList.add(issueField);
                    }
                } catch(Exception e) {
                }
            }
             issueFields = new ArrayList<IssueField>(issueFieldsList);
             
        }
        issueService.setIssueFields(issue.getId(), issueFields);
    }

    private void addHistoryEntry(Issue issue, User user, ActionForm form, IssueService issueService) throws Exception {
        String history = (String) PropertyUtils.getSimpleProperty(form, "history");
        if(history != null && ! history.equals("")) {
            IssueHistory issueHistory = new IssueHistory(history, IssueUtilities.HISTORY_STATUS_AVAILABLE, issue, user);
            issueHistory.setDescription(((IssueForm)form).getHistory());
            issueHistory.setCreateDate(new Date());
            issueService.addIssueHistory(issueHistory);
        }
    }

    private void addAttachment(Issue issue, Project project, User user, ActionForm form, IssueService issueService) throws Exception {
        if(! ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions())) {
            FormFile file = (FormFile) PropertyUtils.getSimpleProperty(form, "attachment");
            if(file != null && ! "".equals(file.getFileName())) {
                String origFileName = file.getFileName();
//                int numAttachments = issueService.getIssueAttachmentCount(issue.getId()) + 1;
//                String filename = "proj" + project.getId() + "_issue" + issue.getId() + "_attachment" + numAttachments;
                if(AttachmentUtilities.checkFile(file, this.getITrackerServices())) {
                    int lastSlash = Math.max(origFileName.lastIndexOf('/'), origFileName.lastIndexOf('\\'));
                    if(lastSlash > -1) {
                        origFileName = origFileName.substring(lastSlash + 1);
                    }
                    IssueAttachment attachmentModel = new IssueAttachment(origFileName,
                                                                                    file.getContentType(),
                                                                                    (String) PropertyUtils.getSimpleProperty(form, "attachmentDescription"),
                                                                                    file.getFileSize(),
                                                                                    issue, user);
                    issueService.addIssueAttachment(attachmentModel, file.getFileData());
                }
            }
        }
    }

    private void sendNotification(Issue issue, int previousStatus, String baseURL, IssueService issueService) {
        int notificationType = NotificationUtilities.TYPE_UPDATED;
        if(previousStatus >= IssueUtilities.STATUS_CLOSED && issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
            notificationType = NotificationUtilities.TYPE_CLOSED;
        }
        issueService.sendNotification(issue.getId(), notificationType, baseURL);
    }

    private ActionForward getReturnForward(Issue issue, Project project, ActionForm form, ActionMapping mapping) throws Exception {
        if("index".equals((String) PropertyUtils.getSimpleProperty(form, "caller"))) {
        	logger.info("EditIssueAction: Forward: index");
            return mapping.findForward("index");
        } else if("viewissue".equals((String) PropertyUtils.getSimpleProperty(form, "caller"))) {
        	logger.info("EditIssueAction: Forward: viewissue");
        	return new ActionForward(mapping.findForward("viewissue").getPath() + "?id=" + issue.getId());
        } else {
        	logger.info("EditIssueAction: Forward: listissues");
            return new ActionForward(mapping.findForward("listissues").getPath() + "?projectId=" + project.getId());
        }
    }

}
  