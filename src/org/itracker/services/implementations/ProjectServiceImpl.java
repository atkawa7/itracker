/*  * This software was designed and created by Jason Carroll.  * Copyright (c) 2002, 2003, 2004 Jason Carroll.  * The author can be reached at jcarroll@cowsultants.com  * ITracker website: http://www.cowsultants.com  * ITracker forums: http://www.cowsultants.com/phpBB/index.php  *  * This program is free software; you can redistribute it and/or modify  * it only under the terms of the GNU General Public License as published by  * the Free Software Foundation; either version 2 of the License, or  * (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU General Public License for more details.  */package org.itracker.services.implementations;import java.sql.Timestamp;import java.util.ArrayList;import java.util.Collection;import java.util.Date;import java.util.HashSet;import java.util.Iterator;import java.util.List;import java.util.Locale;import org.itracker.model.Component;import org.itracker.model.CustomField;import org.itracker.model.Project;import org.itracker.model.ProjectScript;import org.itracker.model.User;import org.itracker.model.Version;import org.itracker.persistence.dao.ComponentDAO;import org.itracker.persistence.dao.CustomFieldDAO;import org.itracker.persistence.dao.IssueDAO;import org.itracker.persistence.dao.ProjectDAO;import org.itracker.persistence.dao.ProjectScriptDAO;import org.itracker.persistence.dao.UserDAO;import org.itracker.persistence.dao.VersionDAO;import org.itracker.services.ProjectService;public class ProjectServiceImpl implements ProjectService {        private ComponentDAO componentDAO;    private CustomFieldDAO customFieldDAO;    private ProjectDAO projectDAO;    private ProjectScriptDAO projectScriptDAO;    private UserDAO userDAO;    private VersionDAO versionDAO;    private IssueDAO issueDAO;        public ProjectServiceImpl() {    }        public Project getProject(Integer projectId) {        Project project = projectDAO.findByPrimaryKey(projectId);                return project;    }        public List<Project> getAllProjects() {        List<Project> projects = projectDAO.findAll();        return projects;    }        public List<Project> getListOfAllProjects() {        return getAllProjects();    }        public int getNumberProjects() {        // TODO: use SELECT COUNT(*); another comment to the comment: why?        Collection<Project> projects = projectDAO.findAll();        return projects.size();            }        public List<Project> getAllAvailableProjects() {        List<Project> projects = projectDAO.findAllAvailable();        return projects;    }        public List<Project> getListOfAllAvailableProjects() {        return getAllAvailableProjects();    }        public Project createProject(Project project) {        project.setCreateDate(new Timestamp(new Date().getTime()));        project.setLastModifiedDate(project.getCreateDate());        this.projectDAO.save(project);        return project;            }        public Project updateProject(Project project) {        Project model = projectDAO.findByPrimaryKey(project.getId());        model.setName(project.getName());        model.setStatus(project.getStatus());        model.setOptions(project.getOptions());        model.setDescription(project.getDescription());        model.setLastModifiedDate(new Date());        projectDAO.saveOrUpdate(model);        return model;    }        public boolean deleteProject(Project project) {        throw new UnsupportedOperationException();    }        public Component updateProjectComponent(Component component) {        component.setLastModifiedDate(new Date());        componentDAO.saveOrUpdate(component);        return component;    }        public Component addProjectComponent(Integer projectId, Component component) {        Project project = projectDAO.findByPrimaryKey(projectId);                component.setCreateDate(new Date());        component.setProject(project);                List<Component> components = project.getComponents();        components.add(component);                componentDAO.save(component);                return component;    }        public boolean removeProjectComponent(Integer projectId, Integer componentId) {                Project project = projectDAO.findByPrimaryKey(projectId);                Component component = componentDAO.findById(componentId);                Collection<Component> components = project.getComponents();                components.remove(component);        componentDAO.delete(component);                return true;            }        public Component getProjectComponent(Integer componentId) {                Component component = componentDAO.findById(componentId);                return component;            }        public List<Component> getProjectComponents(Integer projectId) {        List<Component> components = componentDAO.findByProject(projectId);        return components;    }        public Version addProjectVersion(Integer projectId, Version version) {        version.setCreateDate(new Date());                Project project = projectDAO.findByPrimaryKey(projectId);        version.setProject(project);                Collection<Version> versions = project.getVersions();        versions.add(version);        versionDAO.save(version);                return version;            }        public boolean removeProjectVersion(Integer projectId, Integer versionId) {        Project project = projectDAO.findByPrimaryKey(projectId);        Version version = versionDAO.findByPrimaryKey(versionId);        Collection<Version> versions = project.getVersions();                versions.remove(version);        versionDAO.delete(version);                return true;    }        public Version updateProjectVersion(Version version) {        versionDAO.saveOrUpdate(version);        return version;    }        public Version getProjectVersion(Integer versionId) {        Version version = versionDAO.findByPrimaryKey(versionId);                return version;            }        public List<Version> getProjectVersions(Integer projectId) {        List<Version> versions = versionDAO.findByProjectId(projectId);        return versions;    }        public List<User> getProjectOwners(Integer projectId) {        Project project = projectDAO.findByPrimaryKey(projectId);        List<User> users = project.getOwners();        return users;    }        public boolean setProjectOwners(Project project, HashSet newOwners) {        List<User> owners = project.getOwners();               owners.clear();        if (newOwners != null && !newOwners.isEmpty()) {            for (Iterator iterator = newOwners.iterator(); iterator.hasNext();) {                 Integer ownerId = (Integer) iterator.next();                User owner = userDAO.findByPrimaryKey(ownerId);                owners.add(owner);            }                    }        return true;    }        public List<CustomField> getProjectFields(Integer projectId) {        Project project = projectDAO.findByPrimaryKey(projectId);        List<CustomField> fields = project.getCustomFields();                return fields;    }        public List<CustomField> getProjectFields(Integer projectId, Locale locale) {        List<CustomField> fields = getProjectFields(projectId, locale);        return fields;    }        public boolean setProjectFields(Project project, HashSet newFields) {        List<CustomField> fields = new ArrayList<CustomField>();        fields = project.getCustomFields();        fields.clear();                if (newFields != null && !newFields.isEmpty()) {            for (Iterator iterator = newFields.iterator(); iterator.hasNext();) {                Integer fieldId = (Integer) iterator.next();                CustomField field = customFieldDAO.findByPrimaryKey(fieldId);                fields.add(field);            }        }        return true;    }        public ProjectScript addProjectScript(Integer projectId, ProjectScript projectScript) {        Project project = projectDAO.findByPrimaryKey(projectId);        List<ProjectScript> scripts = project.getScripts();                scripts.add(projectScript);                return projectScript;            }        public boolean removeProjectScript(Integer projectId, Integer scriptId) {        Project project = projectDAO.findByPrimaryKey(projectId);        ProjectScript script = projectScriptDAO.findByPrimaryKey(scriptId);        // TODO: check this: why is this comment "script.remove()" there? Which type is expected in Collection<T> scripts?         // (WorkflowsScript? ProjectScript? Do you understand this?)        Collection scripts = project.getScripts();        scripts.remove(script);                // script.remove();                return true;            }        public ProjectScript updateProjectScript(ProjectScript projectScript) {                return projectScript;    }        public int getTotalNumberIssuesByProject(Integer projectId) {        Project project = projectDAO.findByPrimaryKey(projectId);        return project != null ? project.getTotalNumberIssues() : 0;    }        public int countIssuesByVersion(Integer versionId) {                return issueDAO.countByVersion(versionId);            }        public int countIssuesByComponent(Integer componentId) {        return issueDAO.countByComponent(componentId);           }        public Object[] getProjectStats(Integer projectId) {        return issueDAO.getIssueStats(projectId);     }        public IssueDAO getIssueDAO() {        return issueDAO;    }        public void setIssueDAO(IssueDAO issueDAO) {        this.issueDAO = issueDAO;    }        public ProjectDAO getProjectDAO() {        return projectDAO;    }        public void setProjectDAO(ProjectDAO projectDAO) {        this.projectDAO = projectDAO;    }        public ComponentDAO getComponentDAO() {        return componentDAO;    }        public void setComponentDAO(ComponentDAO componentDAO) {        this.componentDAO = componentDAO;    }        public CustomFieldDAO getCustomFieldDAO() {        return this.customFieldDAO;    }        public void setCustomFieldDAO(CustomFieldDAO customFieldDAO) {        this.customFieldDAO = customFieldDAO;    }        public ProjectScriptDAO getProjectScriptDAO() {        return this.projectScriptDAO;    }        public void setProjectScriptDAO(ProjectScriptDAO projectScriptDAO) {        this.projectScriptDAO = projectScriptDAO;    }        public VersionDAO getVersionDAO() {        return this.versionDAO;    }        public void setVersionDAO(VersionDAO versionDAO) {        this.versionDAO = versionDAO;    }        public UserDAO getUserDAO() {        return this.userDAO;    }        public void setUserDAO(UserDAO userDAO) {        this.userDAO = userDAO;    }        public List<Component> getListOfProjectComponents(Integer projectId) {        throw new UnsupportedOperationException();    }        public List<CustomField> getListOfProjectFields(Integer projectId, Locale locale) {        throw new UnsupportedOperationException();    }        public List<CustomField> getListOfProjectFields(Integer projectId) {        throw new UnsupportedOperationException();    }        public List<User> getListOfProjectOwners(Integer projectId) {        throw new UnsupportedOperationException();    }        public List<Object> getListOfProjectStats(Integer projectId) {        throw new UnsupportedOperationException();    }        public List<Version> getListOfProjectVersions(Integer projectId) {        throw new UnsupportedOperationException();    }}