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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.itracker.services.util.UserUtilities;

/**
 * A user. 
 * 
 * @author ready
 */
public class User extends AbstractEntity implements Comparable<User> {

    public static final Comparator<User> NAME_COMPARATOR = 
            new NameComparator();
    
    private String login;
    
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private int status;
    
    private boolean superUser;
    
    private int registrationType;
    
    private UserPreferences preferences;
    
    private List<Permission> permissions = new ArrayList<Permission>();
    
    private List<Project> projects = new ArrayList<Project>();
    
    //private Collection<Notification> notifications = new ArrayList<Notification>();
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public User() {
    }

    public User(String login) {
        super(new Date());
        setLogin(login);
    }
    
    public User(String login, String password, String firstName, String lastName, String email, boolean superUser) {
        this(login, password, firstName, lastName, email, UserUtilities.REGISTRATION_TYPE_ADMIN, superUser);
    }

    public User(String login, String password, String firstName, String lastName, String email, int registrationType, boolean superUser) {
        this(login);
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationType = registrationType;
        setSuperUser(superUser);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        if (login == null) {
            throw new IllegalArgumentException("null login");
        }
        this.login = login;
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
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> getPermissions) {
        this.permissions = getPermissions;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences getPreferences) {
        this.preferences = getPreferences;
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
    
    public List<Project> getProjects() {
        return projects;
    }
    
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof User) {
            final User other = (User)obj;
            
            return this.login.equals(other.login);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.login.hashCode();
    }
    
    @Override
    public String toString() {
        return "User [id=" + this.id 
            + ", login=" + this.login + "]";
    }
    
    public int compareTo(User other) {
        return this.login.compareTo(other.login);
    }

    /**
     * Compares 2 users by last and first name. 
     */
    private static class NameComparator implements Comparator<User> {
        
        public int compare(User a, User b) {
            if (a.lastName == null && b.lastName == null) {
                return 0;
            } else if (a.lastName == null) {
                return 1;
            } else if (b.lastName == null) {
                return -1;
            }

            final int lastNameComparison = 
                    a.lastName.compareTo(b.lastName);
            
            return (lastNameComparison == 0) 
                ? a.firstName.compareTo(b.firstName)
                : lastNameComparison;
        }
        
    }
    
}
