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

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class IssueActivity extends AbstractBean {

    private User user;
    private int type;
    private String description;
    private int notificationSent;
    private Issue issue;

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
        this.description = value;
    }

    public int getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(int value) {
        this.notificationSent = value;
    }

    public Issue getIssue() {
        return (issue);
    }

    public void setIssue(Issue value) {
        this.issue = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
