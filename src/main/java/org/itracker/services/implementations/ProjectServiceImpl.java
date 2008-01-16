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

package org.itracker.services.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.persistence.dao.ComponentDAO;
import org.itracker.persistence.dao.CustomFieldDAO;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.ProjectScriptDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.persistence.dao.VersionDAO;
import org.itracker.services.ProjectService;
import org.itracker.services.util.IssueUtilities;


public class ProjectServiceImpl implements ProjectService {
    
    private ComponentDAO componentDAO;
    private CustomFieldDAO customFieldDAO;
    private ProjectDAO projectDAO;
    private ProjectScriptDAO projectScriptDAO;
    private UserDAO userDAO;
    private VersionDAO versionDAO;
    private IssueDAO issueDAO;
    
    public ProjectServiceImpl(ComponentDAO componentDAO, CustomFieldDAO customFieldDAO, 
    		ProjectDAO projectDAO, ProjectScriptDAO projectScriptDAO, UserDAO userDAO, 
    		VersionDAO versionDAO, IssueDAO issueDAO) 
    {
    	this.componentDAO = componentDAO;
    	this.customFieldDAO = customFieldDAO;
    	this.projectDAO = projectDAO;
    	this.projectScriptDAO = projectScriptDAO;
    	this.userDAO = userDAO;
    	this.versionDAO = versionDAO;
    	this.issueDAO = issueDAO;
    }
    
    public Project getProject(Integer projectId) {
        Project project = projectDAO.findByPrimaryKey(projectId);        
        return project;
    }
    
    public List<Project> getAllProjects() {
    	return projectDAO.findAll();
    }
    
    public List<Project> getAllAvailableProjects() {
        List<Project> projects = projectDAO.findAllAvailable();
        return projects;
    }
    
    public List<Project> getListOfAllAvailableProjects() {
        return getAllAvailableProjects();
    }
    
    public Component updateProjectComponent(Component component) {
        component.setLastModifiedDate(new Date());
        componentDAO.saveOrUpdate(component);
        return component;
    }
    
    public Component addProjectComponent(Integer projectId, Component component) {
        Project project = projectDAO.findByPrimaryKey(projectId);
        
        component.setCreateDate(new Date());
        component.setProject(project);
        
        List<Component> components = project.getComponents();
        components.add(component);
        
        componentDAO.save(component);
        
        return component;
    }
    
    public boolean removeProjectComponent(Integer projectId, Integer componentId) {
        
        Project project = projectDAO.findByPrimaryKey(projectId);
        
        Component component = componentDAO.findById(componentId);
        
        Collection<Component> components = project.getComponents();
        
        components.remove(component);
        componentDAO.delete(component);
        
        return true;
        
    }
    
    public Component getProjectComponent(Integer componentId) {
        
        Component component = componentDAO.findById(componentId);
        
        return component;
        
    }
    
    public Version addProjectVersion(Integer projectId, Version version) {
        version.setCreateDate(new Date());
        
        Project project = projectDAO.findByPrimaryKey(projectId);
        version.setProject(project);
        
        Collection<Version> versions = project.getVersions();
        versions.add(version);
        versionDAO.save(version);
        
        return version;
        
    }
    
    public boolean removeProjectVersion(Integer projectId, Integer versionId) {
        Project project = projectDAO.findByPrimaryKey(projectId);
        Version version = versionDAO.findByPrimaryKey(versionId);
        Collection<Version> versions = project.getVersions();
        
        versions.remove(version);
        versionDAO.delete(version);
        
        return true;
    }
    
    public Version updateProjectVersion(Version version) {
        versionDAO.saveOrUpdate(version);
        return version;
    }
    
    public Version getProjectVersion(Integer versionId) {
        Version version = versionDAO.findByPrimaryKey(versionId);
        
        return version;
        
    }
    
    public List<User> getProjectOwners(Integer projectId) {
        Project project = projectDAO.findByPrimaryKey(projectId);
        List<User> users = project.getOwners();
        return users;
    }
    
    public boolean setProjectOwners(Project project, Set<Integer> setOfNewOwnerIds) {
        List<User> owners = project.getOwners();       
        owners.clear();
        if (setOfNewOwnerIds != null && !setOfNewOwnerIds.isEmpty()) {
            for (Iterator<Integer> iterator = setOfNewOwnerIds.iterator(); iterator.hasNext();) { 
                Integer ownerId = iterator.next();
                User owner = userDAO.findByPrimaryKey(ownerId);
                owners.add(owner);
            }
            
        }
        return true;
    }
    
    public List<CustomField> getProjectFields(Integer projectId) {
        Project project = projectDAO.findByPrimaryKey(projectId);
        List<CustomField> fields = project.getCustomFields();
        
        return fields;
    }
    
    public List<CustomField> getProjectFields(Integer projectId, Locale locale) {
    	return getProjectFields(projectId, locale);
    }
    
    public boolean setProjectFields(Project project, Set<Integer> setOfNewsFieldIds) {
        List<CustomField> fields = new ArrayList<CustomField>();
        fields = project.getCustomFields();
        fields.clear();
        
        if (setOfNewsFieldIds != null && !setOfNewsFieldIds.isEmpty()) {
            for (Iterator<Integer> iterator = setOfNewsFieldIds.iterator(); iterator.hasNext();) {
                Integer fieldId = iterator.next();
                CustomField field = customFieldDAO.findByPrimaryKey(fieldId);
                fields.add(field);
            }
        }
        return true;
    }
    
    public ProjectScript getProjectScript(Integer scriptId) {
        ProjectScript projectScript = this.projectScriptDAO.findByPrimaryKey(scriptId);
        return projectScript;

    }

    public List<ProjectScript> getProjectScripts() {
        List<ProjectScript> projectScripts = this.projectScriptDAO.findAll();
        return projectScripts;
    }
    

    public ProjectScript addProjectScript(Integer projectId, ProjectScript projectScript) {
        ProjectScript addprojectScript = new ProjectScript();
        addprojectScript.setId(projectScript.getId());
        addprojectScript.setFieldId(projectScript.getFieldId());
        addprojectScript.setPriority(projectScript.getPriority());
        addprojectScript.setProject(projectScript.getProject());
        addprojectScript.setScript(projectScript.getScript());
        addprojectScript.setCreateDate(new Date());
        addprojectScript.setLastModifiedDate(addprojectScript.getCreateDate());
        this.projectScriptDAO.save(addprojectScript);
        
        return addprojectScript;
        
    }
    
    public boolean removeProjectScript(Integer projectId, Integer scriptId) {
//        Project project = projectDAO.findByPrimaryKey(projectId);
        ProjectScript script = projectScriptDAO.findByPrimaryKey(scriptId);
        this.projectScriptDAO.delete(script);
        // TODO: check this: why is this comment "script.remove()" there? Which type is expected in Collection<T> scripts? 
        // (WorkflowsScript? ProjectScript? Do you understand this? - I would say ProjectScript)
//        Collection<ProjectScript> scripts = project.getScripts();
//        scripts.remove(script);
        
        // script.remove();
        
        return true;
        
    }
    
    public ProjectScript updateProjectScript(ProjectScript projectScript) {
        ProjectScript editprojectScript = projectScriptDAO.findByPrimaryKey(projectScript.getId());
        editprojectScript.setId(projectScript.getId());
        editprojectScript.setFieldId(projectScript.getFieldId());
        editprojectScript.setPriority(projectScript.getPriority());
        editprojectScript.setProject(projectScript.getProject());
        editprojectScript.setScript(projectScript.getScript());
//        editprojectScript.setCreateDate(projectScript.getCreateDate());
        editprojectScript.setLastModifiedDate(new Date());
        
        this.projectScriptDAO.saveOrUpdate(editprojectScript);
       
        return editprojectScript;
    }
    
    public int getTotalNumberIssuesByProject(Integer projectId) {
        return issueDAO.countByProject(projectId);
    }
    
    public int countIssuesByVersion(Integer versionId) {        
        return issueDAO.countByVersion(versionId);
        
    }
    
    public int countIssuesByComponent(Integer componentId) {
        return issueDAO.countByComponent(componentId);       
    }
    
    public int[] getProjectStats(Integer projectId) {
        final int[] issueStats = new int[2];

        int openIssuesCount = issueDAO.countByProjectAndLowerStatus(projectId, 
                IssueUtilities.STATUS_RESOLVED);
        
        issueStats[0] = openIssuesCount;
        
        int resolvedIssuesCount = issueDAO.countByProjectAndHigherStatus(
                projectId, IssueUtilities.STATUS_RESOLVED);
        issueStats[1] = resolvedIssuesCount;

        return issueStats;
    }
    
    public IssueDAO getIssueDAO() {
        return issueDAO;
    }
    
    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }
    
    public ComponentDAO getComponentDAO() {
        return componentDAO;
    }
    
    public CustomFieldDAO getCustomFieldDAO() {
        return this.customFieldDAO;
    }
    
    public ProjectScriptDAO getProjectScriptDAO() {
        return this.projectScriptDAO;
    }
    
    public VersionDAO getVersionDAO() {
        return this.versionDAO;
    }
    
    public UserDAO getUserDAO() {
        return this.userDAO;
    }
        
    public List<CustomField> getListOfProjectFields(Integer projectId) {
        throw new UnsupportedOperationException();
    }
    
    public List<User> getListOfProjectOwners(Integer projectId) {
        throw new UnsupportedOperationException();
    }
    
}