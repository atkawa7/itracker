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
 * Models a project component. 
 * 
 * <p>A Component is a project subdivision, like a sub-project 
 * or functional area, ... <br>
 * It is identified by a unique name within the project. <br>
 * e.g.: core, web-ui, swing-ui, help, ...</p>
 * 
 * <p>A component cannot have sub-components, unlike categories 
 * and sub-categories that exist in some issue tracking systems. </p>
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
     * Default constructor required by Hibernate. 
     */
    private Component() {
    }
    
    /**
     * Creates a new Component of the given name for the given Project. 
     * 
     * @param project owning this component
     * @param name unique component name within the project
     */
    public Component(Project project, String name) {
        setProject(project);
        setName(name);
        
        // A new component is active by default. 
        this.status = 1; // = ProjectUtilities.STATUS_ACTIVE
    }
    
    /**
     * Returns the project owning this component. 
     * 
     * @return parent project
     */
    public Project getProject() {
        return project;
    }
    
    /**
     * Sets the project owning this component. 
     * 
     * <p>PENDING: The project shouldn't be modified because it is part of 
     * a component's natural key and is used in the equals method! </p>
     * 
     * @param project parent project
     */
    public void setProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("null project");
        }
        this.project = project;
    }
    
    /**
     * Returns this component's name. 
     * 
     * @return unique name within the parent project
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets this component's name.
     * 
     * <p>PENDING: The name shouldn't be modified because it is part of 
     * a component's natural key and is used in the equals method! </p>
     * 
     * @param name unique name within the parent project
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        this.name = name;
    }
    
    /**
     * Returns this component's description. 
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets this component's description. 
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns this component's status. 
     * 
     * @return enum value
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets this component's status. 
     * 
     * @param status enum value
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * Two component instances are equal if they belong to the same project 
     * and have the same name. 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Component) {
            final Component other = (Component)obj;
            
            return this.project.equals(other.project) 
                && this.name.equals(other.name);
        }
        return false;
    }

    /**
     * Overridden to match implementation of method {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return this.project.hashCode() + this.name.hashCode();
    }

    /**
     * @return <tt>Component [id=id, project=project, name=name]</tt>
     */
    @Override
    public String toString() {
        return "Component [id=" + this.id + ", project=" + this.project 
                + ", name=" + this.name + "]";
    }

    /**
     * Compares 2 Components by project and name. 
     */
    public int compareTo(Component other) {
        final int projectComparison = this.project.compareTo(other.project);
        
        if (projectComparison == 0) {
            return this.name.compareTo(other.name);
        }
        return projectComparison;
    }
    
    
    /**
     * Compares 2 Components by name. 
     */
    public static class NameComparator implements Comparator<Component> {
        
        public int compare(Component a, Component b) {
            return a.name.compareTo(b.name);
        }
        
    }

}
