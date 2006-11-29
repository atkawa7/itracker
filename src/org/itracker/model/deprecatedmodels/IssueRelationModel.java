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


class IssueRelationModel extends GenericModel implements Comparator<IssueRelationModel> {
    private int type;

    private Integer matchingRelationId;
    private Integer issueId;
    private Integer relatedIssueId;
    private String relatedIssueDescription;
    private int relatedIssueStatus;
    private int relatedIssueSeverity;

    public IssueRelationModel() {
        type = -1;
        issueId = new Integer(-1);
        relatedIssueId = new Integer(-1);
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer value) {
        issueId = value;
    }

    public Integer getMatchingRelationId() {
        return matchingRelationId;
    }

    public void setMatchingRelationId(Integer value) {
        matchingRelationId = value;
    }

    public int getRelationType() {
        return type;
    }

    public void setRelationType(int value) {
        type = value;
    }

    public String getRelatedIssueDescription() {
        return (relatedIssueDescription == null ? "" : relatedIssueDescription);
    }

    public void setRelatedIssueDescription(String value) {
        relatedIssueDescription = value;
    }

    public Integer getRelatedIssueId() {
        return (relatedIssueId == null ? new Integer(-1) : relatedIssueId);
    }

    public void setRelatedIssueId(Integer value) {
        relatedIssueId = value;
    }

    public int getRelatedIssueStatus() {
        return relatedIssueStatus;
    }

    public void setRelatedIssueStatus(int value) {
        relatedIssueStatus = value;
    }

    public int getRelatedIssueSeverity() {
        return relatedIssueSeverity;
    }

    public void setRelatedIssueSeverity(int value) {
        relatedIssueSeverity = value;
    }

    public String toString() {
        return "Relation [" + getId() + "] Type: " + getRelationType() +
        			 " Related Issue: " + getRelatedIssueDescription() + " (" + getRelatedIssueId() + ")" +
               " Created: " + getCreateDate();
    }

    public int compare(IssueRelationModel a, IssueRelationModel b) {
        return new IssueRelationModel.CompareByTypeAndId().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof IssueRelationModel)) {
            return false;
        }

        try {
            IssueRelationModel mo = (IssueRelationModel) obj;
            if(IssueRelationModel.this.getId() == mo.getId()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (IssueRelationModel.this.getId() == null ? 1 : IssueRelationModel.this.getId().intValue());
    }

    public static abstract class IssueRelationModelComparator implements Comparator {
        protected boolean isAscending = true;

        public IssueRelationModelComparator() {
        }

        public IssueRelationModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(IssueRelationModel ma, IssueRelationModel mb);

        public final int compare(Object a, Object b) {
            if(! (a instanceof IssueRelationModel) || ! (b instanceof IssueRelationModel)) {
                throw new ClassCastException();
            }

            IssueRelationModel ma = (IssueRelationModel) a;
            IssueRelationModel mb = (IssueRelationModel) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByTypeAndId extends IssueRelationModelComparator {
        public CompareByTypeAndId(){
          super();
        }

        public CompareByTypeAndId(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueRelationModel ma, IssueRelationModel mb) {
            if(ma.getRelationType() == mb.getRelationType()) {
                return ma.getRelatedIssueId().compareTo(mb.getRelatedIssueId());
            } else if(ma.getRelationType() > mb.getRelationType()) {
                return 1;
            } else if(ma.getRelationType() < mb.getRelationType()) {
                return -1;
            }

            return 0;
        }
    }

}
