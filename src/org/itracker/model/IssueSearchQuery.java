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

package org.itracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IssueSearchQuery implements Serializable {
    
    public static final Integer TYPE_FULL = new Integer(1);
    public static final Integer TYPE_PROJECT = new Integer(2);

    private List<Project> availableProjects = new ArrayList<Project>();

    private List<Integer> projects = new ArrayList<Integer>();
    private List<Integer> statuses = new ArrayList<Integer>();
    private List<Integer> severities = new ArrayList<Integer>();
    private List<Integer> components = new ArrayList<Integer>();
    private List<Integer> versions = new ArrayList<Integer>();
    private Integer targetVersion = null;
    private Integer owner = null;
    private Integer creator = null;
    private Integer contributor = null;
    private String text = null;
    private String resolution = null;

    private String orderBy = null;

    private Integer type = new Integer(-1);
    private Project project = null;
    private Integer projectId = new Integer(-1);
    private String projectName = "";

    private List<Issue> results = null;

    public IssueSearchQuery() {
    }

    public List<Project> getAvailableProjects() {
        return availableProjects;
    }

    public void setAvailableProjects(List<Project> value) {
        availableProjects = value;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project value) {
        project = value;
    }

    public Integer getProjectId() {
        return (project == null ? projectId : project.getId());
    }

    public void setProjectId(Integer value) {
        projectId = value;
       
    }

    public String getProjectName() {
        return (project == null ? projectName : project.getName());
    }

    public void setProjectName(String value) {
        projectName = value;
    }

    public List<Integer> getProjects() {
        return projects;
    }

    public void setProjects(List<Integer> value) {
        if(value != null && value.size() > 0) {
            projects = value;
        }
    }

    public List<Integer> getSeverities() {
        return severities;
    }

    public void setSeverities(List<Integer> value) {
        if(value != null && value.size() > 0) {
            severities = value;
        }
    }

    public List<Integer> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Integer> value) {
        if(value != null && value.size() > 0) {
            statuses = value;
        }
    }

    public List<Integer> getComponents() {
        return components;
    }

    public void setComponents(List<Integer> value) {
        if(value != null && value.size() > 0) {
            components = value;
        }
    }

    public List<Integer> getVersions() {
        return versions;
    }

    public void setVersions(List<Integer> value) {
        if(value != null && value.size() > 0) {
            versions = value;
        }
    }

    public Integer getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(Integer value) {
        targetVersion = value;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer value) {
        owner = value;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer value) {
        creator = value;
    }

    public Integer getContributor() {
        return contributor;
    }

    public void setContributor(Integer value) {
        contributor = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String value) {
        resolution = value;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String value) {
        orderBy = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer value) {
        type = value;
    }

    public List<Issue> getResults() {
        return results;
    }

    public void setResults(List<Issue> value) {
        results = value;
    }
}
  