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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ProjectModel extends GenericModel implements Comparator<ProjectModel> {
    private String name;
    private String description;
    private int status;
    private int options;
    private List<CustomFieldModel> customFields;
    private List<ComponentModel> components = null;
    private List<VersionModel> versions = null;
    private List<UserModel> owners = null;
    private List<ProjectScriptModel> scripts = null;

    public ProjectModel() {
        name = "";
        description = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
         name = value;
    }

    public String getDescription() {
        return (description == null ? "" : description);
    }

    public void setDescription(String value) {
         description = value;
    }

    public int getStatus() {
         return status;
    }

    public void setStatus(int value) {
         status = value;
    }

    public int getOptions() {
         return (options < 0 ? 0 : options);
    }

    public void setOptions(int value) {
         options = (value < 0 ? 0 : value);
    }

    public List<CustomFieldModel> getCustomFields() {
         return (customFields == null ? new ArrayList<CustomFieldModel>() : customFields);
    }

    public void setCustomFields(List<CustomFieldModel> value) {
         customFields = (value == null ? new ArrayList<CustomFieldModel>() : value);
    }

    public List<ComponentModel> getComponents() {
        return (components == null ? new ArrayList<ComponentModel>() : components);
    }

    public void setComponents(List<ComponentModel> value) {
        if(value != null ) {
            components = new ArrayList<ComponentModel>();
           // System.arraycopy(value, 0, components, 0, components.size());
            components = value;
        }
    }

    public List<VersionModel> getVersions() {
        return (versions == null ? new ArrayList<VersionModel>() : versions);
    }

    public void setVersions(List<VersionModel> value) {
        if(value != null ) {
            versions = new ArrayList<VersionModel>();
            // System.arraycopy( value, 0, versions, 0, versions.size());
            versions=value;
        }
    }

    public List<UserModel> getOwners() {
        return (owners == null ? new ArrayList<UserModel>() : owners);
    }

    public void setOwners(List<UserModel> value) {
        if(value != null) {
            owners = new ArrayList<UserModel>();
            //System.arraycopy(value, 0, owners, 0, owners.size());
            owners=value;
        }
    }

    public List<ProjectScriptModel> getScripts() {
        return (scripts == null ? new ArrayList<ProjectScriptModel>() : scripts);
    }

    public void setScripts(List<ProjectScriptModel> value) {
        scripts = (value == null ? new ArrayList<ProjectScriptModel>() : value);
    }

    public String toString() {
        return "Project [" + getId() + "] Name: " + getName() + " Description: " + getDescription() +
               " Num Components: " + getComponents().size() + " Num Versions: " + getVersions().size() +
               " Num Custom Fields: " + getCustomFields().size() + " Num Scripts: " + getScripts().size() +
               " Num Owners: " + getOwners().size();
    }

    public int compare(ProjectModel a, ProjectModel b) {
        return new ProjectModel.CompareByName().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof ProjectModel)) {
            return false;
        }

        try {
            ProjectModel mo = (ProjectModel) obj;
            if(ProjectModel.this.getId().equals(mo.getId())) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (ProjectModel.this.getId() == null ? 1 : ProjectModel.this.getId().intValue());
    }

    public static class CompareByName implements Comparator {
        protected boolean isAscending = true;

        public CompareByName() {
        }

        public CompareByName(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Object a, Object b) {
            int result = 0;
            if(! (a instanceof ProjectModel) || ! (b instanceof ProjectModel)) {
                throw new ClassCastException();
            }

            ProjectModel ma = (ProjectModel) a;
            ProjectModel mb = (ProjectModel) b;

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