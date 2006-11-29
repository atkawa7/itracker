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
import org.itracker.services.util.IssueUtilities;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class IssueHistory extends AbstractBean {

    public String description;
    public int status;
    public Issue issue;
    public User user;

    public IssueHistory() {
    }

    public IssueHistory(String description) {
        this();
        this.setDescription(description);
        this.setStatus(IssueUtilities.HISTORY_STATUS_AVAILABLE);
    }

    public IssueHistory(String description, int status, Issue issue, User user) {
        this();
        this.setDescription(description);
        this.setIssue(issue);
        this.setUser(user);
        this.setStatus(status);
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class CompareByDate implements Comparator<IssueHistory> {
        protected boolean isAscending = true;

        public CompareByDate() {
        }

        public CompareByDate(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(IssueHistory ma, IssueHistory mb) {
            int result = 0;

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
