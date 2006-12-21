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

package org.itracker.services;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.User;
import org.itracker.model.Version;


public interface ProjectService {
    
    public Project getProject(Integer projectId);
 
    public List<Project> getAllProjects();
    
    public List<Project> getListOfAllProjects();
    
    public int getNumberProjects();
 
    public List<Project> getAllAvailableProjects();
    
    public List<Project> getListOfAllAvailableProjects();
    
    public Project createProject(Project project);
    
    public Project updateProject(Project project);
    
    public boolean deleteProject(Project project);

    public Component getProjectComponent(Integer componentId);
  
    public List<Component> getProjectComponents(Integer projectId);
    
    public List<Component> getListOfProjectComponents(Integer projectId);
    
    public Component updateProjectComponent(Component component);
    
    public Component addProjectComponent(Integer projectId, Component component);
    
    public boolean removeProjectComponent(Integer projectId, Integer componentId);

    public Version getProjectVersion(Integer versionId);
 
    public List<Version> getProjectVersions(Integer projectId);
    
    public List<Version> getListOfProjectVersions(Integer projectId);
    
    public Version updateProjectVersion(Version version);
    
    public Version addProjectVersion(Integer projectId, Version version);
    
    public boolean removeProjectVersion(Integer projectId, Integer versionId);
 
    public List<User> getProjectOwners(Integer projectId);
    
    public List<User> getListOfProjectOwners(Integer projectId);
    
    public boolean setProjectOwners(Project project, HashSet<Integer> newOwners);
 
    public List<CustomField> getProjectFields(Integer projectId);
    
    public List<CustomField> getListOfProjectFields(Integer projectId);   
 
    public List<CustomField> getProjectFields(Integer projectId, Locale locale);
    
    public List<CustomField> getListOfProjectFields(Integer projectId, Locale locale);
    
    public boolean setProjectFields(Project project, HashSet<Integer> newFields);

    public ProjectScript updateProjectScript(ProjectScript projectScript);
    
    public ProjectScript addProjectScript(Integer projectId, ProjectScript projectScript);
    
    public boolean removeProjectScript(Integer projectId, Integer scriptId);

    /**
     * Counts the number of issues for a given component. 
     * 
     * @param componentId Id of the component to which the issues must be associated
     * @return 0 if the component has no issues or doesn't exist
     */
    public int countIssuesByComponent(Integer componentId);
    
    public int getTotalNumberIssuesByProject(Integer projectId);
    
    /**
     * Counts the number of issues for a given version. 
     * 
     * @param versionId Id of the version to which the issues must be associated
     * @return 0 if the version has no issues or doesn't exist
     */
    public int countIssuesByVersion(Integer versionId);

    /**
     * Returns the number of open and resolved issues in the given project. 
     * 
     * <p>PENDING: should use a class to hold statistics info to improve type-
     * safety. </p>
     * 
     * @return int[0] = open issues, int[1] = resolved issues
     */
    public int[] getProjectStats(Integer projectId);
    
    public List<Object> getListOfProjectStats(Integer projectId);
    
}