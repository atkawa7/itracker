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

import org.itracker.services.util.IssueUtilities;


class IssueHistoryModel extends GenericModel implements Comparator<IssueHistoryModel> {
    private String description;
    private int status;

    private Integer issueId;
    private Integer userId;

    private UserModel user;

    public IssueHistoryModel() {
        userId = new Integer(-1);
    }

    public IssueHistoryModel(String description) {
        this();
        this.setDescription(description);
        this.setStatus(IssueUtilities.HISTORY_STATUS_AVAILABLE);
    }

    public IssueHistoryModel(String description, int status, Integer issueId, Integer userId) {
        this();
        this.setDescription(description);
        this.setIssueId(issueId);
        this.setUserId(userId);
        this.setStatus(status);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
         description = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int value) {
         status = value;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel value) {
        user = value;
    }

    public Integer getUserId() {
        return (user == null ? userId : user.getId());
    }

    public void setUserId(Integer value) {
         userId = value;
    }

    public String getUserLogin() {
        return (user == null ? "" : user.getLogin());
    }

    public String getUserFirstName() {
        return (user == null ? "" : user.getFirstName());
    }

    public String getUserLastName() {
        return (user == null ? "" : user.getLastName());
    }

    public String getUserEmail() {
        return (user == null ? "" : user.getEmail());
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer value) {
         issueId = value;
    }

    public int compare(IssueHistoryModel a, IssueHistoryModel b) {
        return new IssueHistoryModel.CompareByDate().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof IssueHistoryModel)) {
            return false;
        }

        try {
            IssueHistoryModel mo = (IssueHistoryModel) obj;
            if(IssueHistoryModel.this.getCreateDate().equals(mo.getCreateDate()) &&
               IssueHistoryModel.this.getUserLogin().equals(mo.getUserLogin()) &&
               IssueHistoryModel.this.getDescription().equals(mo.getDescription())) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return ((IssueHistoryModel.this.getCreateDate() == null ? 1 : IssueHistoryModel.this.getCreateDate().hashCode()) ^
                IssueHistoryModel.this.getUserLogin().hashCode() ^
                (IssueHistoryModel.this.getDescription() == null ? 1 : IssueHistoryModel.this.getDescription().hashCode()));
    }

    public String toString() {
        return "IssueHistory [" + this.getId() + "] Issue: " + this.getIssueId() + " Creator: " + this.getUserId() +  " Description: " + this.getDescription();
    }

    public static class CompareByDate implements Comparator {
        protected boolean isAscending = true;

        public CompareByDate() {
        }

        public CompareByDate(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Object a, Object b) {
            int result = 0;

            if(! (a instanceof IssueHistoryModel) || ! (b instanceof IssueHistoryModel)) {
                throw new ClassCastException();
            }

            IssueHistoryModel ma = (IssueHistoryModel) a;
            IssueHistoryModel mb = (IssueHistoryModel) b;

            if(ma.getCreateDate() == null && mb.getCreateDate() == null) {
                result = 0;
            } else if(ma.getCreateDate() == null) {
                result = 1;
            } else if(mb.getCreateDate() == null) {
                result = -1;
            } else {
                if(ma.getCreateDate().equals(mb.getCreateDate())) {
                    result = 0;
                } else if(ma.getCreateDate().before(mb.getCreateDate())) {
                    result = -1;
                } else {
                    result = 1;
                }
            }

            return (isAscending ? result : result * -1);
        }
    }
}