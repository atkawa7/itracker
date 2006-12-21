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

import java.util.Date;

/**
 * This is a POJO Business Domain Object modelling an issue activity. 
 * 
 * <p>Hibernate Bean. </p>
 * 
 * <p>The natural key of an IssueActivity is issue + user + type + createDate. 
 * </p>
 * 
 * @author ready
 */
public class IssueActivity extends AbstractBean {
    
    /** Issue to which this activity is related. */
    private Issue issue;
    
    /** User who generated this activity. */
    private User user;
    
    /** Type of activity. */
    private int type;
    
    /** Optional activity description. */
    private String description;
    
    /** 
     * Whether a notification has been sent for this activity.
     */
    private boolean notificationSent;
    
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
     * @param issue
     * @param user
     * @param type 
     * @param description 
     */
    public IssueActivity(Issue issue, User user, int type, String description) {
        this(issue, user, type, description, false);
    }
    
    public IssueActivity(Issue issue, User user, int type, String description, 
            boolean notificationSent) {
        setIssue(issue);
        setUser(user);
        setType(type);
        setDescription(description);
        setNotificationSent(notificationSent);
        setCreateDate(new Date());
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
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
                && (this.type == other.type)
                && this.createDate.equals(other.createDate);
        }
        return false;
    }
    
    public int hashCode() {
        return this.issue.hashCode()
            + this.user.hashCode()
            + this.type
            + this.createDate.hashCode();
    }
    
    public String toString() {
        return "[issue=" + this.issue 
                + ",user=" + this.user 
                + ",type=" + this.type 
                + ",createDate=" + this.createDate + "]";
    }
    
}
