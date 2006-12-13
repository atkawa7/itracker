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
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class Notification extends AbstractBean {

    private int notificationRole;
    private User user;
    private Issue issue;
    
    public Notification() {
    }
    
    public Notification(int type) {
        this.setNotificationRole(type);
    }

    public Notification(User user, Issue issue, int type) {
        this.setUser(user);
        this.setIssue(issue);
        this.setNotificationRole(type);
    }
    
    public int getNotificationRole() {
        return notificationRole;
    }

    public void setNotificationRole(int notificationRole) {
        this.notificationRole = notificationRole;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static abstract class NotificationModelComparator implements Comparator<Notification> {
        
        protected boolean isAscending = true;

        public NotificationModelComparator() {
        }

        public NotificationModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(Notification ma, Notification mb);

        public final int compare(Notification ma, Notification mb) {
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

        protected int doComparison(Notification ma, Notification mb) {
            if(ma.getUser().getLastName() == null && mb.getUser().getLastName() == null) {
                return 0;
            } else if(ma.getUser().getLastName() == null) {
                return 1;
            } else if(mb.getUser().getLastName() == null) {
                return -1;
            }

            return (ma.getUser().getLastName().compareTo(mb.getUser().getLastName()) == 0 ?
                    ma.getUser().getFirstName().compareTo(mb.getUser().getFirstName()) :
                    ma.getUser().getLastName().compareTo(mb.getUser().getLastName()));
        }
    }

    public static class CompareByType extends NotificationModelComparator {
        public CompareByType(){
          super();
        }

        public CompareByType(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(Notification ma, Notification mb) {
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
