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

package org.itracker.web.forms;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.services.ITrackerServices;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.actions.project.EditIssueActionUtil;
import org.itracker.web.ptos.CreateIssuePTO;
import org.itracker.web.util.AttachmentUtilities;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * This form is by the struts actions to pass issue data.
 */
public class IssueForm extends ITrackerForm {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(IssueForm.class);

    private Integer id = null;
    private String caller = null;
    private Integer projectId = null;
    private Integer creatorId = null;
    private Integer ownerId = null;
    private String description = null;
    private Integer severity = null;
    private Integer status = null;
    private Integer prevStatus = null;
    private String resolution = null;
    private Integer targetVersion = null;
    private Integer[] components = new Integer[0];
    private Integer[] versions = new Integer[0];
    private String attachmentDescription = null;
    transient private FormFile attachment = null;
    private String history = null;
    // lets try to put Integer,String here:
    private HashMap<String, String> customFields = new HashMap<String, String>();
    private Integer relationType = null;
    private Integer relatedIssueId = null;

    public FormFile getAttachment() {
        return attachment;
    }

    public void setAttachment(FormFile attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentDescription() {
        return attachmentDescription;
    }

    public void setAttachmentDescription(String attachmentDescription) {
        this.attachmentDescription = attachmentDescription;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public Integer[] getComponents() {
        if (null == components)
            return null;
        return components.clone();
    }

    public void setComponents(Integer[] components) {
        if (null == components)
            this.components = null;
        else
            this.components = components.clone();
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    // let's try to put Integer,String here:
    public HashMap<String, String> getCustomFields() {
        return customFields;
    }

    // let's try to put Integer,String here:
    public void setCustomFields(HashMap<String, String> customFields) {
        this.customFields = customFields;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getPrevStatus() {
        return prevStatus;
    }

    public void setPrevStatus(Integer prevStatus) {
        this.prevStatus = prevStatus;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getRelatedIssueId() {
        return relatedIssueId;
    }

    public void setRelatedIssueId(Integer relatedIssueId) {
        this.relatedIssueId = relatedIssueId;
    }

    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(Integer targetVersion) {
        this.targetVersion = targetVersion;
    }

    public Integer[] getVersions() {
        if (null == versions)
            return null;
        return versions.clone();
    }

    public void setVersions(Integer[] versions) {
        if (null == versions)
            this.versions = null;
        else
            this.versions = versions.clone();
    }

    /**
     * This methods adds in validation for custom fields. It makes sure the
     * datatype matches and also that all required fields have been populated.
     *
     * @param mapping the ActionMapping object
     * @param request the current HttpServletRequest object
     * @return an ActionErrors object containing any validation errors
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("validate called: mapping: " + mapping + ", request: "
                    + request);
        }
        ActionErrors errors = super.validate(mapping, request);

        if (log.isDebugEnabled()) {
            log.debug("validate called: mapping: " + mapping + ", request: "
                    + request + ", errors: " + errors);
        }

        try {
            if (null != getId()) {
                Issue issue = getITrackerServices().getIssueService().getIssue(
                        getId());

                Locale locale = (Locale) request.getSession().getAttribute(
                        Constants.LOCALE_KEY);
                User currUser = (User) request.getSession().getAttribute(
                        Constants.USER_KEY);
                List<NameValuePair> ownersList = UserUtilities
                        .getAssignableIssueOwnersList(issue,
                                issue.getProject(), currUser, locale,
                                getITrackerServices().getUserService(),
                                RequestHelper.getUserPermissions(request
                                        .getSession()));

                EditIssueActionUtil.setupJspEnv(mapping, this, request, issue,
                        getITrackerServices().getIssueService(),
                        getITrackerServices().getUserService(), RequestHelper
                        .getUserPermissions(request.getSession()),
                        EditIssueActionUtil.getListOptions(request, issue,
                                ownersList, RequestHelper
                                .getUserPermissions(request
                                        .getSession()), issue
                                .getProject(), currUser), errors);

                if (errors.isEmpty() && issue.getProject() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate: issue project is null: " + issue);
                    }
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(
                                    "itracker.web.error.invalidproject"));
                } else if (errors.isEmpty()
                        && issue.getProject().getStatus() != Status.ACTIVE) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate: issue project is not active: " + issue);
                    }
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(
                                    "itracker.web.error.projectlocked"));
                } else if (errors.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate: validation had errors for " + issue + ": " + errors);
                    }
                    validateProjectScripts(issue.getProject(), errors, this);
                    validateAttachment(this.getAttachment(), getITrackerServices(), errors);
                }
            } else {
                CreateIssuePTO.setupCreateIssue(request);
                HttpSession session = request.getSession();
                Project project = (Project) session
                        .getAttribute(Constants.PROJECT_KEY);
                if (log.isDebugEnabled()) {
                    log.debug("validate: validating create new issue for project: " + page);
                }
                validateProjectFields(project, request, errors);
                validateProjectScripts(project, errors, this);
                validateAttachment(this.getAttachment(), getITrackerServices(), errors);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("validate: unexpected exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }
        if (log.isDebugEnabled()) {
            log.debug("validate: returning errors: " + errors);
        }
        return errors;
    }

    private static void validateAttachment(FormFile attachment, ITrackerServices services, ActionMessages errors) {
        if (null != attachment) {
            ActionMessages msg = AttachmentUtilities.validate(attachment, services);
            if (!msg.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("validateAttachment: failed to validate, " + msg);
                }
                errors.add(msg);
            }
        }
    }

    private static void validateProjectFields(Project project,
                                              HttpServletRequest request, ActionErrors errors) {
        List<CustomField> projectFields = project.getCustomFields();
        if (null != projectFields && projectFields.size() > 0) {
            HttpSession session = request.getSession();

            Locale locale = ITrackerResources.getLocale();
            if (session != null) {
                locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            }

            ResourceBundle bundle = ITrackerResources.getBundle(locale);
            Iterator<CustomField> it = projectFields.iterator();
            while (it.hasNext()) {
                CustomField customField = it.next();
                String fieldValue = request.getParameter("customFields("
                        + customField.getId() + ")");
                if (fieldValue != null && !fieldValue.equals("")) {

                    // Don't create an IssueField only so that we can call
                    // setValue to validate the value!
                    // IssueField issueField = new
                    // IssueField(projectFields.get(i));
                    try {
                        customField.checkAssignable(fieldValue, locale, bundle);
                    } catch (IssueException ie) {
                        String label = CustomFieldUtilities.getCustomFieldName(
                                customField.getId(), locale);
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage(ie.getType(), label));
                    }
                } else if (customField.isRequired()) {
                    String label = CustomFieldUtilities.getCustomFieldName(
                            customField.getId(), locale);
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(IssueException.TYPE_CF_REQ_FIELD,
                                    label));
                }
            }
        }
    }

    private static void validateProjectScripts(Project project, ActionErrors errors, IssueForm form)
            throws WorkflowException {

        List<ProjectScript> scripts = project.getScripts();
        WorkflowUtilities.processFieldScripts(scripts,
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, null, errors, form);
    }

}
