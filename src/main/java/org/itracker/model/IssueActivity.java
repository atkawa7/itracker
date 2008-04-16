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

/**
 * An issue activity. 
 * 
 * <p>An IssueActivity can only belong to 1 Issue (composition). </p>
 * 
 * <p>The natural key of an IssueActivity is issue + user + type + createDate. 
 * </p>
 * 
 * @author ready
 */
public class IssueActivity extends AbstractEntity {
    
    /** Issue to which this activity is related. */
    private Issue issue;
    
    /** The User who generated this activity. */
    private User user;
    
    /** Type of activity. */
//    private int type;
    
    /** Optional activity description. */
    private String description = "";
    
    /** 
     * Whether a notification has been sent for this activity.
     */
    private boolean notificationSent = false;

	private Type activityType = Type.ISSUE_CREATED;
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that <code>issue</code>, <code>user</code> 
     * and <code>type</code>, which form an instance's identity, 
     * are always initialized. </p>
     */
    public IssueActivity() {
    	
    }
    
    /**
     * Creates a new instance with a <code>notificationSent</code> flag set 
     * to <tt>false</tt> and a creation and last modified time stamp 
     * set to the current time. 
     * 
     * @deprecated use {@link IssueActivity#IssueActivity(Issue, User, org.itracker.model.IssueActivity.Type)} instead
     * @param issue
     * @param user
     * @param type 
     * @param description 
     */
    public IssueActivity(Issue issue, User user, int type) {
        setIssue(issue);
        setUser(user);
        setType(type);
    }
    
    /**
     * Creates a new instance with a <code>notificationSent</code> flag set 
     * to <tt>false</tt> and a creation and last modified time stamp 
     * set to the current time. 
     * 
     * @param issue
     * @param user
     * @param type 
     * @param description 
     */
    public IssueActivity(Issue issue, User user, Type type) {
    	setIssue(issue);
    	setUser(user);
    	setActivityType(type);
    }
    
    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("null issue");
        }
        this.issue = issue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("null user");
        }
        this.user = user;
    }
    
    /**
     * @deprecated use getActivityType
     * @return
     */
    public int getType() {
    	if (null == this.activityType) {
    		return -1;
    	}
        return this.activityType.code;
    }

    /**
     * @deprecated use setActivityType
     * @param type
     */
    public void setType(int type) {
    	this.activityType = Type.forCode(type);
//        this.type = type;
    }

    public void setActivityType(Type type) {
    	
//    	this.type = type.code;
    	this.activityType = type;
    }
    
    public Type getActivityType() {
    	return this.activityType;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean sent) {
        this.notificationSent = sent;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof IssueActivity) {
            final IssueActivity other = (IssueActivity)obj;
            
            return this.issue.equals(other.issue)
                && this.user.equals(other.user)
                && (this.activityType == other.activityType)
                && (this.createDate == null) ? (other.createDate == null) 
                    : this.createDate.equals(other.createDate);
        }
        return false;
    }
    
    public int hashCode() {
        return this.issue.hashCode()
            + this.user.hashCode()
            + this.activityType.code
            + ((this.createDate == null) ? 0 : this.createDate.hashCode());
    }
    
    public String toString() {
        return "IssueActivity [id=" + this.id
                + ",issue=" + this.issue 
                + ",user=" + this.user 
                + ",type=" + this.activityType 
                + ",createDate=" + this.createDate + "]";
    }
    
    public static enum Type {
        
        ISSUE_CREATED(1),
        
        STATUS_CHANGE(2),
        
        OWNER_CHANGE(3), 
        
        SEVERITY_CHANGE(4),
        
        COMPONENTS_MODIFIED(5),
        
        VERSIONS_MODIFIED(6),
        
        REMOVE_HISTORY(7),
        
        ISSUE_MOVE(8),
        
        SYSTEM_UPDATE(9),
        
        TARGETVERSION_CHANGE(10),
        
        DESCRIPTION_CHANGE(11),
        
        RESOLUTION_CHANGE(12),
        
        RELATION_ADDED(13),
        
        RELATION_REMOVED(14);
        
        private final int code;
        
        private Type(int code) {
            this.code = code;
        }
        
        /**
         * @deprecated
         * @param type
         * @return
         */
        public static final Type forCode(int type) {
        	switch (type) {
    		case 1:
    			return ISSUE_CREATED;
    		case 2:
    			return STATUS_CHANGE;
    		case 3:
    			return OWNER_CHANGE;
    		case 4:
    			return SEVERITY_CHANGE;
    		case 5:
    			return COMPONENTS_MODIFIED;
    		case 6:
    			return VERSIONS_MODIFIED;
    		case 7:
    			return REMOVE_HISTORY;
    		case 8:
    			return ISSUE_MOVE;
    		case 9:
    			return SYSTEM_UPDATE;
    		case 10:
    			return TARGETVERSION_CHANGE;
    		case 11: 
    			return DESCRIPTION_CHANGE;
    		case 12:
    			return RESOLUTION_CHANGE;
    		case 13:
    			return RELATION_ADDED;
    		case 14:
    			return RELATION_REMOVED;
    		}
        	return null;
        }
    }
    

    
}
