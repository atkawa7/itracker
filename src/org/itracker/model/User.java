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

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.itracker.services.util.UserUtilities;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class User extends AbstractBean implements Comparable<User> {

    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private int status;
    private boolean superUser;
    private int registrationType;
    private UserPreferences preferences;
    private List<Permission> permissions = new LinkedList<Permission>();
    private Collection<Notification> notifications = new LinkedList<Notification>();
    private Collection<IssueActivity> activities = new LinkedList<IssueActivity>();
    private Collection<IssueHistory> history = new LinkedList<IssueHistory>();
    private Collection<Project> projects = new LinkedList<Project>();
    private Collection<IssueAttachment> attachments = new LinkedList<IssueAttachment>();
    
    private static final Comparator<User> comparator = new CompareByName();
    
    public User() {
    }

    public User(String login, String password, String firstName, String lastName, String email, boolean superUser) {
        this(login, password, firstName, lastName, email, UserUtilities.REGISTRATION_TYPE_ADMIN, superUser);
    }

    public User(String login, String password, String firstName, String lastName, String email, int registrationType, boolean superUser) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationType = registrationType;
        setSuperUser(superUser);
    }
    
    public Collection<IssueActivity> getActivities() {
        return activities;
    }

    public void setActivities(Collection<IssueActivity> activities) {
        this.activities = activities;
    }

    public Collection<IssueAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<IssueAttachment> attachments) {
        this.attachments = attachments;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<IssueHistory> getHistory() {
        return history;
    }

    public void setHistory(Collection<IssueHistory> history) {
        this.history = history;
    }

    public Collection<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Collection<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public int getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(int registrationType) {
        this.registrationType = registrationType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuperUser() {
        return superUser;
    }
    
    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String value) {
        this.login = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        firstName = value;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public String getFirstInitial() {
        return (getFirstName().length() > 0 ? getFirstName().substring(0,1).toUpperCase() + "." : "");
    }
    
    public boolean hasRequiredData() {
        return hasRequiredData(true);
    }

    public boolean hasRequiredData(boolean passwordRequired) {
        if(this.getLogin() == null || this.getLogin().equals("") ||
           this.getFirstName() == null || this.getFirstName().equals("") ||
           this.getLastName() == null || this.getLastName().equals("") ||
           this.getEmail() == null || this.getEmail().equals("")) {
            return false;
        }
        if(passwordRequired && (this.getPassword() == null || this.getPassword().equals(""))) {
            return false;
        }
        return true;
    }
    
    public int compareTo(User other) {
        return comparator.compare(this, other);
    }
    
    public static abstract class UserModelComparator implements Comparator<User> {
        
        protected boolean isAscending = true;

        public UserModelComparator() {
        }

        public UserModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(User ma, User mb);

        public final int compare(User a, User b) {
            if(! (a instanceof User) || ! (b instanceof User)) {
                throw new ClassCastException();
            }

            User ma = (User) a;
            User mb = (User) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByLogin extends UserModelComparator {
        public CompareByLogin(){
          super();
        }

        public CompareByLogin(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(User ma, User mb) {
            if(ma.getLogin() == null && mb.getLogin() == null) {
                return 0;
            } else if(ma.getLogin() == null) {
                return 1;
            } else if(mb.getLogin() == null) {
                return -1;
            }

            return ma.getLogin().compareTo(mb.getLogin());
        }
    }

    public static class CompareByName extends UserModelComparator {
        public CompareByName(){
          super();
        }

        public CompareByName(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(User ma, User mb) {
            if(ma.getLastName() == null && mb.getLastName() == null) {
                return 0;
            } else if(ma.getLastName() == null) {
                return 1;
            } else if(mb.getLastName() == null) {
                return -1;
            }

            if(ma.getLastName().equals(mb.getLastName())) {
                if(ma.getFirstName() == null && mb.getFirstName() == null) {
                    return 0;
                } else if(ma.getFirstName() == null) {
                    return 1;
                } else if(mb.getFirstName() == null) {
                    return -1;
                }
                return ma.getFirstName().compareTo(mb.getFirstName());
            }
            return ma.getLastName().compareTo(mb.getLastName());
        }
    }
    
    public static class CompareByFirstName extends UserModelComparator {
        public CompareByFirstName(){
          super();
        }

        public CompareByFirstName(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(User ma, User mb) {
            if(ma.getFirstName() == null && mb.getFirstName() == null) {
                return 0;
            } else if(ma.getFirstName() == null) {
                return 1;
            } else if(mb.getFirstName() == null) {
                return -1;
            }

            if(ma.getFirstName().equals(mb.getFirstName())) {
                if(ma.getLastName() == null && mb.getLastName() == null) {
                    return 0;
                } else if(ma.getLastName() == null) {
                    return 1;
                } else if(mb.getLastName() == null) {
                    return -1;
                }
                return ma.getLastName().compareTo(mb.getLastName());
            }
            return ma.getFirstName().compareTo(mb.getFirstName());
        }
    }
    
}
