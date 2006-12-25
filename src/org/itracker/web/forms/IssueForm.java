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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.util.Constants;



/**
 * This form is by the struts actions to pass issue data.
 */
public class IssueForm extends ITrackerForm  {
    
    private Integer id;
    private String caller;
    private Integer projectId;
    private Integer creatorId;
    private Integer ownerId;
    private String description;
    private Integer severity;
    private Integer status;
    private Integer prevStatus;
    private String resolution;
    private Integer targetVersion;
    private Integer[] components;
    private Integer[] versions;
    private String attachmentDescription;
    private FormFile attachment;
    private String history;
    // lets try to put Integer,String here:
    private Map<Integer,String> customFields;
    private Integer relationType;
    private Integer relatedIssueId;
    
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
        return components;
    }
    
    public void setComponents(Integer[] components) {
        this.components = components;
    }
    
    public Integer getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
    // let's try to put Integer,String here:
    public Map<Integer,String> getCustomFields() {
        return customFields;
    }
//  let's try to put Integer,String here:
    public void setCustomFields(Map<Integer,String> customFields) {
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
        return versions;
    }
    
    public void setVersions(Integer[] versions) {
        this.versions = versions;
    }
    
    /**
     * This methods adds in validation for custom fields.  It makes sure the datatype
     * matches and also that all required fields have been populated.
     * @param mapping the ActionMapping object
     * @param request the current HttpServletRequest object
     * @return an ActionErrors object containing any validation errors
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        try {
            Project project = null;
            Integer projectId = (Integer) PropertyUtils.getSimpleProperty(this, "projectId");
            if(projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            } else {
                // check who should deliver ph to the form
                ProjectService projectService = getITrackerServices().getProjectService();
                project = projectService.getProject(projectId);
            }
            
            if(errors.isEmpty() && project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            } else if(errors.isEmpty() && project.getStatus() != ProjectUtilities.STATUS_ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            } else if(errors.isEmpty()) {
                Locale currLocale = ITrackerResources.getLocale();
                
                request.setAttribute(Constants.PROJECT_KEY, project);
                
                List<CustomField> projectFields = project.getCustomFields();
                if(projectFields.size() > 0) {
                    HttpSession session = request.getSession();
                    if(session != null) {
                        currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
                    }
                    
                    ResourceBundle bundle = ITrackerResources.getBundle(currLocale);
                    
                    for(int i = 0; i < projectFields.size(); i++) {
                        CustomField customField = projectFields.get(i);
                        
                        String fieldValue = request.getParameter("customFields(" + customField.getId() +")");
                        if(fieldValue != null && ! fieldValue.equals("")) {
                            
                            // Don't create an IssueField only so that we can call 
                            // setValue to validate the value! 
                            //IssueField issueField = new IssueField(projectFields.get(i));
                            try {
                            //    issueField.setValue(fieldValue, currLocale);
                                customField.checkAssignable(fieldValue, currLocale, bundle);
                            } catch(IssueException ie) {
                                String label = CustomFieldUtilities.getCustomFieldName(projectFields.get(i).getId(), currLocale);
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(ie.getType(), label));
                            }
                        } else if(projectFields.get(i).isRequired()) {
                            String label = CustomFieldUtilities.getCustomFieldName(projectFields.get(i).getId(), currLocale);
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(IssueException.TYPE_CF_REQ_FIELD, label));
                        }
                    }
                }
                
                List<ProjectScript> scripts = project.getScripts();
                WorkflowUtilities.processFieldScripts(scripts, WorkflowUtilities.EVENT_FIELD_ONVALIDATE, null, errors, this);
            }
        } catch(Exception e) {
            e.printStackTrace();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        IssueService issueService = getITrackerServices().getIssueService();
        request.setAttribute("ih",issueService);
        return errors;
    }
    
}
