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

package org.itracker.model.deprecatedmodels;

import java.util.Comparator;

class ProjectScriptModel extends GenericModel implements Comparator<ProjectScriptModel> {
    Integer projectId;
    WorkflowScriptModel script;
    Integer fieldId;
    int priority;

    public ProjectScriptModel() {
    }

    public ProjectScriptModel(Integer projectId, Integer fieldId, WorkflowScriptModel script, int priority) {
        setProjectId(projectId);
        setFieldId(fieldId);
        setScript(script);
        setPriority(priority);
    }

    public Integer getProjectId() {
        return (projectId == null ? new Integer(0) : projectId);
    }

    public void setProjectId(Integer value) {
        projectId = value;
    }

    public Integer getFieldId() {
        return (fieldId == null ? new Integer(0) : fieldId);
    }

    public void setFieldId(Integer value) {
        fieldId = value;
    }

    public WorkflowScriptModel getScript() {
        return (script == null ? new WorkflowScriptModel() : script);
    }

    public void setScript(WorkflowScriptModel value) {
        script = value;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int value) {
        priority = value;
    }

    public String toString() {
        return "ProjectScript [" + this.getId() + "] Project: " + this.getProjectId() + "  Field: " + this.getFieldId() +
               "  Priority: " + this.getPriority() + "  ScriptId: " + this.getScript().getId();
    }

    public int compare(ProjectScriptModel a, ProjectScriptModel b) {
        return new ProjectScriptModel.CompareByFieldAndPriority(false).compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof ProjectScriptModel)) {
            return false;
        }

        try {
            ProjectScriptModel mo = (ProjectScriptModel) obj;
            if(ProjectScriptModel.this.getId() == mo.getId()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (ProjectScriptModel.this.getId().intValue());
    }

    public static class CompareByFieldAndPriority implements Comparator {
        private boolean isAscending = true;

        public CompareByFieldAndPriority() {
        }

        public CompareByFieldAndPriority(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Object a, Object b) {
            int result = 0;

            if(! (a instanceof ProjectScriptModel) || ! (b instanceof ProjectScriptModel)) {
                throw new ClassCastException();
            }

            ProjectScriptModel ma = (ProjectScriptModel) a;
            ProjectScriptModel mb = (ProjectScriptModel) b;

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
