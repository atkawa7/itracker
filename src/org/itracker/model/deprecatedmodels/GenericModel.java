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

import java.io.Serializable;
import java.util.Date;

public class GenericModel implements Serializable, Cloneable {
    protected Integer id = null;
    protected Date lastModifiedDate;
    protected Date createDate;

    public GenericModel() {
    }

    public GenericModel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer value) {
         id = value;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date value) {
         createDate = value;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date value) {
         lastModifiedDate = value;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object value) {
        if(value != null && value instanceof GenericModel) {
            GenericModel mo = (GenericModel) value;
            if(mo.getId() != null && mo.getId().equals(this.getId())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (this.getId() == null ? super.hashCode() : this.getId().intValue());
    }
}
  