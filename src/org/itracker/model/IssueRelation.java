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
 * A relation between issues. 
 * 
 * @author ready
 */
public class IssueRelation extends AbstractEntity {
    
    private Issue issue;
    
    private Issue relatedIssue;
    
    private int relationType;
    
    private Integer matchingRelationId;
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public IssueRelation() {
    }
    
    public IssueRelation(Issue issue, Issue relatedIssue, int relationType) {
        super(new Date());
        setIssue(issue);
        setRelatedIssue(relatedIssue);
        setRelationType(relationType);
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

    public Issue getRelatedIssue() {
        return relatedIssue;
    }

    public void setRelatedIssue(Issue relatedIssue) {
        if (relatedIssue == null) {
            throw new IllegalArgumentException("null relatedIssue");
        }
        this.relatedIssue = relatedIssue;
    }
    
    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }
    
    public Integer getMatchingRelationId() {
        return matchingRelationId;
    }

    public void setMatchingRelationId(Integer matchingRelationId) {
        this.matchingRelationId = matchingRelationId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof IssueRelation) {
            final IssueRelation other = (IssueRelation)obj;
            
            return this.issue.equals(other.issue)
                && this.relatedIssue.equals(other.relatedIssue)
                && this.relationType == other.relationType;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.issue.hashCode() 
            + this.relatedIssue.hashCode() 
            + this.relationType;
    }
    
    @Override
    public String toString() {
        return "[issue=" + this.issue 
            + ",relatedIssue=" + this.relatedIssue 
            + ",type=" + this.relationType + "]";
    }
    
}
