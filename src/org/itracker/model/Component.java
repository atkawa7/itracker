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
import java.util.Collection;
import java.util.Comparator;

/**
 * This is a POJO bean, and yes, a Hibernate bean. 
 * @author Jason
 *  
 */
public class Component extends AbstractBean implements Comparable<Component> {

    private Project project;
    private String name;
    private String description;
    private Collection<Issue> issues = new ArrayList<Issue>();
    
    private static final Comparator<Component> comparator = new CompareByName();
    
    public Component() {
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Collection<Issue> getIssues() {
        return issues;
    }
    public void setIssues(Collection<Issue> issues) {
        this.issues = issues;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Project getProject() {
        return(project);
    }
    public void setProject(Project value) {
        this.project = value;
    }

    public int getTotalNumberIssues() {
        return getIssues().size();
    }

    public int compareTo(Component other) {
        return this.comparator.compare(this, other);
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof Component)) {
            return false;
        }

        Component other = (Component) obj;

        if (this.name == null) {
            return other.name == null;
        }
        return this.name.equals(other.name);
    }

    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode());
    }

    public String toString() {
        return "Component [" + this.id + "] Project: " + this.project + " Name: " + this.name;
    }

    public static class CompareByName implements Comparator<Component> {
        
        public int compare(Component ma, Component mb) {
            if(ma.getName() == null && mb.getName() == null) {
                return 0;
            } else if(ma.getName() == null) {
                return 1;
            } else if(mb.getName() == null) {
                return -1;
            }

            return ma.getName().compareTo(mb.getName());
        }
    }
    
}