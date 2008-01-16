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

import java.util.Comparator;

/**
 * A Beanshell script configured to be executed for a specific Project field. 
 * 
 * @author ready
 */
public class ProjectScript extends AbstractEntity {

    /** 
     * The Project for which the script must be executed. 
     */
    private Project project;
    
    /** 
     * The ID of the built-in or custom field for which the script must 
     * be executed. 
     * 
     * <p>If the ID represents a CustomField, then the CustomField should 
     * be configured for the Project or the script will never be executed. 
     * </p>
     */
    private Integer fieldId;
    
    /** The Beanshell script to execute. */
    private WorkflowScript script;
    
    private int priority;

    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public ProjectScript() {
    }
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public WorkflowScript getScript() {
        return script;
    }

    public void setScript(WorkflowScript script) {
        this.script = script;
    }
    
    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static class CompareByFieldAndPriority 
            implements Comparator<ProjectScript> {

        public int compare(ProjectScript a, ProjectScript b) {
            final int fieldIdComparator = a.fieldId - b.fieldId;
            
            if (fieldIdComparator == 0) {
                return a.priority - b.priority;
            }
            return fieldIdComparator;
        }
        
    }
    
}
