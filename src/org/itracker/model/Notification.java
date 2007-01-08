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
 * A notification to a user about an Issue. 
 * 
 * <p>An Notification can only belong to 1 Issue (composition). </p>
 * 
 * @author ready
 */
public class Notification extends AbstractEntity 
        implements Comparable<Notification> {

    public static final Comparator<Notification> TYPE_COMPARATOR = 
            new RoleComparator();
    
    public static final Comparator<Notification> USER_COMPARATOR = 
            new UserComparator();
    
    private Issue issue;
    
    private User user;
    
    private int role;
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public Notification() {
    }

    public Notification(User user, Issue issue, int role) {
        this.setUser(user);
        this.setIssue(issue);
        this.setNotificationRole(role);
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

    public int getNotificationRole() {
        return role;
    }

    public void setNotificationRole(int role) {
        this.role = role;
    }
    
    public int compareTo(Notification other) {
        final int issueComparison = this.issue.compareTo(other.issue);
        
        if (issueComparison == 0) {
            final int userComparison = this.user.compareTo(other.user);
            
            if (userComparison == 0) {
                return this.role - other.role;
            }
            return userComparison;
        }
        return issueComparison;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Notification) {
            final Notification other = (Notification)obj;
            
            return this.issue.equals(other.issue)
                && this.user.equals(other.user)
                && this.role == other.role;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.issue.hashCode() + this.user.hashCode() + this.role;
    }
    
    @Override
    public String toString() {
        return "[issue=" + this.issue 
            + ",user=" + this.user 
            + ",role=" + this.role + "]";
    }


    private static class UserComparator implements Comparator<Notification> {
        
        public int compare(Notification a, Notification b) {
            return User.NAME_COMPARATOR.compare(a.user, b.user);
        }
        
    }

    private static class RoleComparator implements Comparator<Notification> {
        
        public int compare(Notification a, Notification b) {
            return a.role - b.role;
        }
        
    }
    
    public static enum Role {
        
        ANY(-1), 
        
        CREATOR(1), 
        
        OWNER(2), 
        
        CONTRIBUTER(3), 
        
        QA(4), 
        
        PM(5), 
        
        PO(6), 
        
        CO(7), 
        
        VO(8), 
        
        IP(9);
        
        private final int code;
        
        private Role(int code) {
            this.code = code;
        }
        
    }
    
    public static enum Type {
        
        CREATED(1), 
        
        UPDATED(2), 
        
        ASSIGNED(3), 
        
        CLOSED(4), 
        
        SELF_REGISTER(5), 
        
        ISSUE_REMINDER(6);
        
        private final int code;
        
        private Type(int code) {
            this.code = code;
        }
        
    }
    
}
