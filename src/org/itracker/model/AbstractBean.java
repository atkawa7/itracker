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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * 
 * <p>All entity Java Beans should inherit this class to make sure they 
 * are Serializable and Cloneable and have the following fields : 
 * an id, a creation date and a last modifiation date. </p>
 * 
 * @author ready
 */
public abstract class AbstractBean implements Serializable, Cloneable {

    protected Integer id;
    protected Date createDate;
    protected Date lastModifiedDate;

    public AbstractBean() {
    }
    
    public AbstractBean(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer value) {
        this.id = value;
    }

    public Date getCreateDate() {
        return createDate;
    }
    
    /**
     * Sets the creation and last modification date and time. 
     * 
     * <p>The creation time stamp should never change once initialized. </p>
     * 
     * @param dateTime creation time stamp
     */
    public void setCreateDate(Date dateTime) {
        this.createDate = dateTime;
        this.lastModifiedDate = dateTime;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    /**
     * Sets the last modification date and time. 
     * 
     * <p>Note that the last modification time stamp is automatically 
     * set to the creation time stamp when the latter is defined. </p>
     * 
     * @param dateTime last modification time stamp
     */
    public void setLastModifiedDate(Date dateTime) {
        this.lastModifiedDate = dateTime;       
    }

    /**
     * Returns whether this instance represents a new transient instance. 
     * 
     * @return <tt>true</tt> if <code>id</code> is <tt>null</tt> or == 0
     */
    public boolean isNew() {
        return (this.id == null) || (this.id.intValue() == 0);
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    public static class LastModifiedDateComparator implements Comparator<AbstractBean> {
        
        private boolean ascending;
        
        public LastModifiedDateComparator() {
        }

        public LastModifiedDateComparator(boolean ascending) {
            this.setAscending(ascending);
        }

        public boolean isAscending() {
            return ascending;
        }

        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }
        
        public int compare(AbstractBean a, AbstractBean b) {
            int result; 
            
            if(a.lastModifiedDate == null && b.lastModifiedDate == null) {
                result = 0;
            } else if (a.lastModifiedDate == null) {
                result = -1;
            } else if (b.lastModifiedDate == null) {
                result = 1;
            }

            if (a.lastModifiedDate.equals(b.lastModifiedDate)) {
                result = 0;
            } else if (a.lastModifiedDate.after(b.lastModifiedDate)) {
                result = 1;
            } else {
                result = -1;
            }
            return ascending ? result : -result;
        }

    }
    
}
