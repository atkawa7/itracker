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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This is a POJO Business Domain Object, Hibernate Bean.
 * @author ready
 *
 */
public class Project extends AbstractBean implements Comparable<Project> {

    private String name;
    private String description;
    private int status;
    private int options;
    private List<CustomField> customFields = new ArrayList<CustomField>();
    private List<Component> components = new ArrayList<Component>();
    private List<Version> versions = new ArrayList<Version>();
    private List<Issue> issues = new ArrayList<Issue>();
    private List<Permission> permissions = new ArrayList<Permission>();
    private List<User> owners = new ArrayList<User>();
    private List<ProjectScript> scripts = new ArrayList<ProjectScript>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> getComponents) {
        this.components = getComponents;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomField> getCustomFields) {
        this.customFields = getCustomFields;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> getIssues) {
        this.issues = getIssues;
    }

    public List<User> getOwners() {
        return owners;
    }

    public void setOwners(List<User> getOwners) {
        this.owners = getOwners;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> getPermissions) {
        this.permissions = getPermissions;
    }

    public List<ProjectScript> getScripts() {
        return scripts;
    }

    public void setScripts(List<ProjectScript> getScripts) {
        this.scripts = getScripts;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> getVersions) {
        this.versions = getVersions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotalNumberIssues() {
        return getIssues().size();
    }

    /**
     * Two project instances are equal if they have the same name. 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Project) {
            final Project other = (Project)obj;
            
            return this.name.equals(other.name);
        }
        return false;
    }
    
    /**
     * Overridden to match implementation of method {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    /**
     * @return <tt>Project [id=id, name=name]</tt>
     */
    @Override
    public String toString() {
        return "Project [id=" + this.id + ", name=" + this.name + "]";
    }
    
    /**
     * Compares 2 projects by name. 
     */
    public int compareTo(Project other) {
        return this.name.compareTo(other.name);
    }

    public static class CompareByName implements Comparator<Project> {
        
        protected boolean isAscending = true;

        public CompareByName() {
        }

        public CompareByName(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Project ma, Project mb) {
            int result = 0;

            if(ma.getName() == null && mb.getName() == null) {
                result = 0;
            } else if(ma.getName() == null) {
                result = 1;
            } else if(mb.getName() == null) {
                result = -1;
            } else {
                result = ma.getName().compareTo(mb.getName());
            }

            return (isAscending ? result : result * -1);
        }
    }
    
}
