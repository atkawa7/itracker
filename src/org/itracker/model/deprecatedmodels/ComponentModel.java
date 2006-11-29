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

class ComponentModel extends GenericModel implements Comparator<ComponentModel> {
    private String name;
    private String description;
    private Integer projectId;

    public ComponentModel() {
        name = "";
        description = "";
        projectId = new Integer(-1);
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer value) {
        projectId = value;
    }

    public int compare(ComponentModel a, ComponentModel b) {
        return new ComponentModel.CompareByName().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof ComponentModel)) {
            return false;
        }

        try {
            ComponentModel mo = (ComponentModel) obj;
            if(ComponentModel.this.getName() == mo.getName()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (ComponentModel.this.getName() == null ? 1 : ComponentModel.this.getName().hashCode());
    }

    public String toString() {
        return "Component [" + this.getId() + "] Project: " + this.getProjectId() + " Name: " + this.getName();
    }

    public static class CompareByName implements Comparator<ComponentModel> {
        public int compare(ComponentModel a, ComponentModel b) {
            if(! (a instanceof ComponentModel) || ! (b instanceof ComponentModel)) {
                throw new ClassCastException();
            }

            ComponentModel ma = (ComponentModel) a;
            ComponentModel mb = (ComponentModel) b;

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