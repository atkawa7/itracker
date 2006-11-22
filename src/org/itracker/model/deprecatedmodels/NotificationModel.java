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

class NotificationModel extends GenericModel implements Comparator<NotificationModel> {
    private int type;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private Integer userId;
    private Integer issueId;

    public NotificationModel() {
        login = "";
        firstName = "";
        lastName = "";
        email = "";
        userId = new Integer(-1);
        issueId = new Integer(-1);
    }

    public NotificationModel(int type) {
        this.setNotificationRole(type);
    }

    public NotificationModel(UserModel user, Integer issueId, int type) {
        this.setUserLogin(user.getLogin());
        this.setUserFirstName(user.getFirstName());
        this.setUserLastName(user.getLastName());
        this.setUserEmail(user.getEmail());
        this.setUserId(user.getId());
        this.setIssueId(issueId);
        this.setNotificationRole(type);
    }

    public int getNotificationRole() {
        return type;
    }

    public void setNotificationRole(int value) {
        type = value;
    }

    public String getUserLogin() {
        return login;
    }

    public void setUserLogin(String value) {
         login = value;
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

    public int compare(NotificationModel a, NotificationModel b) {
        return new NotificationModel.CompareByName().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof NotificationModel)) {
            return false;
        }

        try {
            NotificationModel mo = (NotificationModel) obj;
            if(NotificationModel.this.getUserLogin().equals(mo.getUserLogin())) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return ((NotificationModel.this.getUserLogin() == null ? 1 : NotificationModel.this.getUserLogin().hashCode()));
    }

    public static abstract class NotificationModelComparator implements Comparator {
        protected boolean isAscending = true;

        public NotificationModelComparator() {
        }

        public NotificationModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(NotificationModel ma, NotificationModel mb);

        public final int compare(Object a, Object b) {
            if(! (a instanceof NotificationModel) || ! (b instanceof NotificationModel)) {
                throw new ClassCastException();
            }

            NotificationModel ma = (NotificationModel) a;
            NotificationModel mb = (NotificationModel) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }


    public static class CompareByName extends NotificationModelComparator {
        public CompareByName(){
          super();
        }

        public CompareByName(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(NotificationModel ma, NotificationModel mb) {
            if(ma.getUserLastName() == null && mb.getUserLastName() == null) {
                return 0;
            } else if(ma.getUserLastName() == null) {
                return 1;
            } else if(mb.getUserLastName() == null) {
                return -1;
            }

            return (ma.getUserLastName().compareTo(mb.getUserLastName()) == 0 ?
                    ma.getUserFirstName().compareTo(mb.getUserFirstName()) :
                    ma.getUserLastName().compareTo(mb.getUserLastName()));
        }
    }

    public static class CompareByType extends NotificationModelComparator {
        public CompareByType(){
          super();
        }

        public CompareByType(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(NotificationModel ma, NotificationModel mb) {
            if(ma.getNotificationRole() == mb.getNotificationRole()) {
                return 0;
            } else if(ma.getNotificationRole() < mb.getNotificationRole()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}