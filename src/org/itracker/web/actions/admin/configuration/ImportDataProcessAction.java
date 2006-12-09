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
import java.util.HashSet;
import java.util.List;

import javax.naming.InitialContext;
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
import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.AbstractBean;
import org.itracker.model.ImportDataModel;
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.Issue;
import org.itracker.model.Language;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.ImportExportUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;



public class ImportDataProcessAction extends ItrackerBaseAction {

    public ImportDataProcessAction () {
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
            InitialContext ic = new InitialContext();

            HttpSession session = request.getSession(true);
            User importer = (User) session.getAttribute(Constants.USER_KEY);
            if(importer == null) {
                return mapping.findForward("unauthorized");
            }

            ImportDataModel model = (ImportDataModel) session.getAttribute(Constants.IMPORT_DATA_KEY);
            // TODO: it looks like the following line can be removed. Commented and Task added
            // AbstractBean[] importData = model.getData();
            logger.debug("Importing configuration data.");
            createConfig(model, importer, ic);
            logger.debug("Importing user data.");
            createUsers(model, importer, ic);
            logger.debug("Importing project data.");
            createProjects(model, importer, ic);
            logger.debug("Importing issue data.");
            createIssues(model, importer, ic);
            logger.debug("Import complete.");

        } catch(Exception e) {
            logger.error("Exception while importing data.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            return mapping.findForward("error");
        } else {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.importexport.importcomplete"));
            saveMessages(request, errors);
        }

        return mapping.findForward("adminindex");
    }

    private boolean createConfig(ImportDataModel model, User importer, InitialContext ic) {
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            AbstractBean[] importData = model.getData();
            for(int i = 0; i < importData.length; i++) {
                if(importData[i] instanceof Configuration && ! model.getExistingModel(i)) {
                    Configuration configItem = (Configuration) importData[i];
                    Configuration newConfigItem = configurationService.createConfigurationItem(configItem);
                    configItem.setId(newConfigItem.getId());

                    // Now add a new language key
                    String key = SystemConfigurationUtilities.getLanguageKey(configItem);
                    configurationService.updateLanguageItem(new Language(ImportExportUtilities.EXPORT_LOCALE_STRING, key, configItem.getName()));
                    ITrackerResources.clearKeyFromBundles(key, true);
                } else if(importData[i] instanceof CustomField && ! model.getExistingModel(i)) {
                    CustomField customField = (CustomField) importData[i];
                    CustomField newCustomField = configurationService.createCustomField(customField);
                    customField.setId(newCustomField.getId());

                    // Now add new language keys.  One for the field and then add one for for
                    // each option that exists.
                    String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
                    configurationService.updateLanguageItem(new Language(ImportExportUtilities.EXPORT_LOCALE_STRING, key, customField.getName()));
                    ITrackerResources.clearKeyFromBundles(key, true);
                    if(customField.getFieldType() == CustomFieldUtilities.TYPE_LIST) {
                        for(int j = 0; j < customField.getOptions().size(); j++) {
                            String optionKey = CustomFieldUtilities.getCustomFieldOptionLabelKey(customField.getId(), customField.getOptions().get(j).getId());
                            configurationService.updateLanguageItem(new Language(ImportExportUtilities.EXPORT_LOCALE_STRING, optionKey, customField.getOptions().get(j).getCustomField().getName()));
                            ITrackerResources.clearKeyFromBundles(optionKey, true);
                        }
                    }
                }
            }
            configurationService.resetConfigurationCache();
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    private boolean createUsers(ImportDataModel model, User importer, InitialContext ic) {
        try {
            UserService userService = getITrackerServices().getUserService();

            AbstractBean[] importData = model.getData();
            for(int i = 0; i < importData.length; i++) {
                if(importData[i] instanceof User && ! model.getExistingModel(i)) {
                    User user = (User) importData[i];
                    user.setRegistrationType(UserUtilities.REGISTRATION_TYPE_IMPORT);
                    if(model.getCreatePasswords()) {
                        user.setPassword(UserUtilities.encryptPassword(user.getLogin()));
                    }
                    user.setLogin(user.getLogin());
                    User newUser = userService.createUser(user);
                    user.setId(newUser.getId());
                }
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    private boolean createProjects(ImportDataModel model, User importer, InitialContext ic) {
        try {
            ProjectService projectService = getITrackerServices().getProjectService();

            AbstractBean[] importData = model.getData();
            for(int i = 0; i < importData.length; i++) {
                if(importData[i] instanceof Project && ! model.getExistingModel(i)) {
                    Project project = (Project) importData[i];
                    Project newProject = projectService.createProject(project);
                    project.setId(newProject.getId());

                    HashSet<Integer> ownerIDs = new HashSet<Integer>();
                    for(int j = 0; j < project.getOwners().size(); j++) {
                    	ownerIDs.add(project.getOwners().get(j).getId());
                    }
                    projectService.setProjectOwners(project, ownerIDs);

                    HashSet<Integer> fieldIDs = new HashSet<Integer>();
                    for(int j = 0; j < project.getCustomFields().size(); j++) {
                    	fieldIDs.add(project.getCustomFields().get(j).getId());
                    }
                    projectService.setProjectFields(project, fieldIDs);

                    List<Component> components = project.getComponents();
                    for(int j = 0; j < components.size(); j++) {
                        Component newComponent = projectService.addProjectComponent(project.getId(), components.get(j));
                        components.get(j).setId(newComponent.getId());
                    }

                    List<Version> versions = project.getVersions();
                    for(int j = 0; j < versions.size(); j++) {
                        Version newVersion = projectService.addProjectVersion(project.getId(), versions.get(j));
                        versions.get(j).setId(newVersion.getId());
                    }
                }
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    private boolean createIssues(ImportDataModel model, User importer, InitialContext ic) {
        try {
            IssueService issueService = getITrackerServices().getIssueService();

            AbstractBean[] importData = model.getData();
            for(int i = 0; i < importData.length; i++) {
                if(importData[i] instanceof Issue && ! model.getExistingModel(i)) {
                    Issue issue = (Issue) importData[i];
                    Issue newIssue = issueService.createIssue(issue, 
                            issue.getProject().getId(), issue.getCreator().getId(), importer.getId());
                    issue.setId(newIssue.getId());

                    // Assign the issue
                    if(issue.getOwner() != null) {
                        issueService.assignIssue(issue.getId(), issue.getOwner().getId(), importer.getId());
                    }

                    // Now set Issue Custom Fields
                    List<IssueField> fields = issue.getFields();
                    if(fields.size() > 0) {
                        for(int j = 0; j < fields.size(); j++) {
                            fields.get(j).setIssue(issue);
                        }
                        issueService.setIssueFields(issue.getId(), issue.getFields());
                    }

                    // Now add all the issue history
                    List<IssueHistory> history = issue.getHistory();
                    if(history.size() > 0) {
                        for(int j = 0; j < history.size(); j++) {
                            history.get(j).setIssue(issue);
                            issueService.addIssueHistory(history.get(j));
                        }
                    }

                    // Now add components and versions
                    HashSet<Integer> components = new HashSet<Integer>();
                    List<Component> componentsList = issue.getComponents();
                    if(componentsList.size() > 0) {
                        for(int j = 0; j < componentsList.size(); j++) {
                            components.add(componentsList.get(j).getId());
                        }
                        issueService.setIssueComponents(issue.getId(), components, importer.getId());
                    }
                    HashSet<Integer> versions = new HashSet<Integer>();
                    List<Version> versionsList = issue.getVersions();
                    if(versionsList.size() > 0) {
                        for(int j = 0; j < versionsList.size(); j++) {
                            versions.add(versionsList.get(j).getId());
                        }
                        issueService.setIssueVersions(issue.getId(), versions, importer.getId());
                    }

                    // Now add any attachments
                    List<IssueAttachment> attachments = issue.getAttachments();
                    if(attachments.size() > 0) {
                        for(int j = 0; j < history.size(); j++) {
                            attachments.get(j).setIssue(issue);
                            issueService.addIssueAttachment(attachments.get(j), null);
                        }
                    }
                }
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    // TODO: it looks like the following method is not used; commented and task added
   // private void printArray(AbstractBean[] models) {
    //    for(int i = 0; i < models.length; i++) {
     //       logger.debug(i + ") " + models[i].toString());
      //  }
   // }

}
  