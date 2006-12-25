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
import org.itracker.services.util.IssueUtilities;

/**
 * An issue history entry. 
 * 
 * @author ready
 */
public class IssueHistory extends AbstractEntity {

    private Issue issue;
    
    private String description;
    
    private int status;
    
    private User creator;

    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public IssueHistory() {
    }

    public IssueHistory(Issue issue, User creator) {
        super(new Date());
        setIssue(issue);
        setUser(creator);
        setStatus(IssueUtilities.HISTORY_STATUS_AVAILABLE);
    }

    public IssueHistory(Issue issue, User creator, String description, int status) {
        super(new Date());
        setIssue(issue);
        setUser(creator);
        setDescription(description);
        setStatus(status);
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
        return creator;
    }

    public void setUser(User creator) {
        if (creator == null) {
            throw new IllegalArgumentException("null creator");
        }
        this.creator = creator;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof IssueHistory) {
            final IssueHistory other = (IssueHistory)obj;
            
            return this.issue.equals(other.issue)
                && this.creator.equals(other.creator)
                && this.createDate.equals(other.createDate);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.issue.hashCode() 
            + this.creator.hashCode() 
            + this.createDate.hashCode();
    }
    
    @Override
    public String toString() {
        return "IssueHistory [id=" + this.getId() 
            + ", issue=" + this.issue 
            + ", creator=" + this.creator 
            + ", createDate=" + this.createDate + "]";
    }
    
}
