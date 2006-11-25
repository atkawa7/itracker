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
 * A project component. 
 * 
 * @author Jason
 * @author Johnny
 */
public class Component extends AbstractBean implements Comparable<Component> {

    /** Project to which this component belongs. */
    private Project project;
    
    /** Unique name identifying this component within its project. */
    private String name;
    
    /** Component description. */
    private String description;
    
    /** Component status. */
    private int status;
    
    /** 
     * PENDING: move this association to the Issue class as it doesn't appear 
     * in the component table, but in the issue_component_rel table, 
     * which indicates that an Issue may be associated with more that 1 Component. 
     */
    private Collection<Issue> issues = new ArrayList<Issue>();
    
    private static final Comparator<Component> comparator = new CompareByName();
    
    /**
     * Default constructor required by Hibernate. 
     */
    public Component() {
    }
    
    public Component(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        this.name = name;
    }
    
    public Project getProject() {
        return(project);
    }
    
    public void setProject(Project value) {
        this.project = value;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * Use IssueDAO.findByComponent instead. 
     */
    @Deprecated
    public Collection<Issue> getIssues() {
        return issues;
    }
    
    /**
     * Use IssueDAO.findByComponent instead. 
     */
    @Deprecated
    public void setIssues(Collection<Issue> issues) {
        this.issues = issues;
    }
    
    /**
     * Use IssueDAO.findByComponent instead. 
     */
    @Deprecated
    public int getTotalNumberIssues() {
        return getIssues().size();
    }

    /**
     * Compares 2 Components by name. 
     */
    public int compareTo(Component other) {
        return this.comparator.compare(this, other);
    }
    
    /**
     * Two Components are equal if they have the same name. 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Component) {
            final Component other = (Component)obj;
            
            return (this.name == null) 
                ? (other.name == null) : this.name.equals(other.name);
        }
        return false;
    }

    /**
     * Overridden to match implementation of method {@link #equals(Object) }.
     */
    @Override
    public int hashCode() {
        return (this.name == null) ? 0 : this.name.hashCode();
    }

    /**
     * @return <tt>Component [&lt;name&gt;]</tt>
     */
    @Override
    public String toString() {
        return "Component [" + this.name + "]";
    }

    /**
     * Compares 2 Components by name. 
     */
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
