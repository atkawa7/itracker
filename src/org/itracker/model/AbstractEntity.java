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
import java.util.Date;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * 
 * <p>All entities are Java Beans and should inherit this class to make sure they 
 * are Serializable and Cloneable and have the following fields : 
 * an id, a creation date and a last modifiation date. </p>
 * 
 * @author ready
 */
public abstract class AbstractEntity implements Entity {

    public static final Comparator<AbstractEntity> ID_COMPARATOR = 
            new IdComparator();
    
    public static final Comparator<AbstractEntity> CREATE_DATE_COMPARATOR = 
            new CreateDateComparator();
    
    public static final Comparator<AbstractEntity> LAST_MODIFIED_DATE_COMPARATOR = 
            new LastModifiedDateComparator();
    
    /** System ID */
    protected Integer id;
    
    /** Creation date and time. */
    protected Date createDate;
    
    /** Last modification date and time. */
    protected Date lastModifiedDate;

    /**
     * Default constructor (required by Hibernate).
     */
    public AbstractEntity() {
    }
    
    /**
     * Creates a new instance and initializes the createDate 
     * and lastModifiedDate properties to the given date and time. 
     * 
     * @param creationDateTime creation timestamp
     */
    public AbstractEntity(Date creationDateTime) {
        this.createDate = this.lastModifiedDate = creationDateTime;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }
    
    /**
     * Sets the creation date and time. 
     * 
     * <p>The creation time stamp should never change once initialized. </p>
     * 
     * @param dateTime creation time stamp
     */
    public void setCreateDate(Date dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("null dateTime");
        }
        this.createDate = dateTime;
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
     * @return <tt>true</tt> if <code>id</code> is <tt>null</tt>
     */
    public boolean isNew() {
        return (this.id == null);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    /**
     * Compares 2 instances by ID. 
     */
    protected static class IdComparator implements Comparator<AbstractEntity> {
        
        public int compare(AbstractEntity a, AbstractEntity b) {
            final int result; 
            
            if (a.id == null && b.id == null) {
                result = 0;
            } else if (a.id == null) {
                result = -1;
            } else if (b.id == null) {
                result = 1;
            } else {
                result = a.id.compareTo(b.id);
            }
            return result;
        }

    }
    
    protected static class CreateDateComparator 
            implements Comparator<AbstractEntity> {
        
        public int compare(AbstractEntity a, AbstractEntity b) {
            final int result; 
            
            if (a.createDate == null && b.createDate == null) {
                result = 0;
            } else if (a.createDate == null) {
                result = -1;
            } else if (b.createDate == null) {
                result = 1;
            } else {
                result = a.createDate.compareTo(b.createDate);
            }
            return result;
        }

    }
    
    /**
     * Compares 2 instances by last modified date. 
     */
    protected static class LastModifiedDateComparator 
            implements Comparator<AbstractEntity> {
        
        public int compare(AbstractEntity a, AbstractEntity b) {
            final int result; 
            
            if (a.lastModifiedDate == null && b.lastModifiedDate == null) {
                result = 0;
            } else if (a.lastModifiedDate == null) {
                result = -1;
            } else if (b.lastModifiedDate == null) {
                result = 1;
            } else {
                result = a.lastModifiedDate.compareTo(b.lastModifiedDate);
            }
            return result;
        }

    }
    
}