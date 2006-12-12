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

import java.sql.Timestamp;
import java.util.Date;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class IssueRelation extends AbstractBean {

    private Integer matchingRelationId;
    private int relationType;
    private Issue issue;
    private Issue relatedIssue;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Integer getMatchingRelationId() {
        return matchingRelationId;
    }

    public void setMatchingRelationId(Integer matchingRelationId) {
        this.matchingRelationId = matchingRelationId;
    }

    public Issue getRelatedIssue() {
        return relatedIssue;
    }

    public void setRelatedIssue(Issue relatedIssue) {
        this.relatedIssue = relatedIssue;
    }

    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }

}
