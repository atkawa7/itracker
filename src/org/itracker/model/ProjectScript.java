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
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class ProjectScript extends AbstractBean {

    private Project project;
    private Integer fieldId;
    private WorkflowScript script;
    private int priority;

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

    public static class CompareByFieldAndPriority implements Comparator<ProjectScript> {
        
        private boolean isAscending = true;

        public CompareByFieldAndPriority() {
        }

        public CompareByFieldAndPriority(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(ProjectScript ma, ProjectScript mb) {
            int result = 0;
            
            if(ma.getFieldId().equals(mb.getFieldId())) {
                if(ma.getPriority() > mb.getPriority()) {
                    result = 1;
                } else {
                    result = -1;
                }
            } else {
                result = ma.getFieldId().compareTo(mb.getFieldId());
            }

            return (isAscending ? result * -1 : result);
        }
    }
    
}
