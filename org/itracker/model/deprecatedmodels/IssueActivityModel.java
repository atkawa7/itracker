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

class IssueActivityModel extends GenericModel implements Comparator<IssueActivityModel> {
    private int type;
    private String description;
    private boolean notificationSent;
    private String userLogin;
    private String firstName;
    private String lastName;
    private String email;
    private Integer issueId;
    private Integer userId;

    public IssueActivityModel() {
    }

    /*public IssueActivityModel(int type, String description, Integer issueId, Integer userId) {
        this.setType(type);
        this.setDescription(description);
        this.setIssueId(issueId);
        this.setUserId(userId);
    }*/

    /*public IssueActivityModel(int type, String description, boolean notificationSent, Integer issueId, Integer userId) {
        this(type, description, issueId, userId);
        this.setNotificationSent(notificationSent);
    }*/

    public int getType() {
        return type;
    }

    public void setType(int value) {
         type = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
         description = value;
    }

    public boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean value) {
         notificationSent = value;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String value) {
         userLogin = value;
    }

    public String getUserFirstName() {
        return firstName;
    }

    public void setUserFirstName(String value) {
         firstName = value;
    }

    public String getUserLastName() {
        return lastName;
    }

    public void setUserLastName(String value) {
         lastName = value;
    }

    public String getUserEmail() {
        return email;
    }

    public void setUserEmail(String value) {
         email = value;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer value) {
         issueId = value;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer value) {
         userId = value;
    }

    public int compare(IssueActivityModel a, IssueActivityModel b) {
        return new IssueActivityModel.CompareByDate().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof IssueActivityModel)) {
            return false;
        }

        try {
            IssueActivityModel mo = (IssueActivityModel) obj;
            if(IssueActivityModel.this.getCreateDate().equals(mo.getCreateDate()) &&
               IssueActivityModel.this.getUserLogin().equals(mo.getUserLogin()) &&
               IssueActivityModel.this.getDescription().equals(mo.getDescription())) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return ((IssueActivityModel.this.getCreateDate() == null ? 1 : IssueActivityModel.this.getCreateDate().hashCode()) ^
                (IssueActivityModel.this.getUserLogin() == null ? 1 : IssueActivityModel.this.getUserLogin().hashCode()) ^
                (IssueActivityModel.this.getDescription() == null ? 1 : IssueActivityModel.this.getDescription().hashCode()));
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

            if(! (a instanceof IssueActivityModel) || ! (b instanceof IssueActivityModel)) {
                throw new ClassCastException();
            }

            IssueActivityModel ma = (IssueActivityModel) a;
            IssueActivityModel mb = (IssueActivityModel) b;

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
