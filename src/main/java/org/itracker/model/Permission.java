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
 * A user permission on a project. 
 * 
 * <p>The permission type tells what kind of action the user is allowed perform. </p>
 * 
 * @author ready
 */
public class Permission extends AbstractEntity {

    /** 
     * The type of permission granted. 
     * TODO: use PermissionType enum 
     */
    private int type;
    
    /** 
     * The project on which this permission is granted. 
     * May be <tt>null</tt> to indicate the permission is granted on all projects. 
     */
    private Project project;
    
    /** The user who's granted this permission. */
    private User user;
    
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public Permission() {
    }

    /**
     * Grants permissions on all projects to the given user. 
     * 
     * @param type permission type
     * @param user grantee
     */
    public Permission(int type, User user) {
        this(type, user, null);
    }
    
    /**
     * Grants permissions on all projects to the given user. 
     * 
     * @param type permission type
     * @param user grantee
     * @param project on which permission is granted, or <tt>null</tt>
     *        for all projects
     */
    public Permission(int type, User user, Project project) {
        setPermissionType(type);
        setUser(user);
        setProject(project);
    }
    
    public int getPermissionType() {
        return type;
    }

    public void setPermissionType(int type) {
        this.type = type;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("null user");
        }
        this.user = user;
    }
    
    public Project getProject() {
        return project;
    }
    
    /** May be null to indicate a permission on all projects. */
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Permission) {
            final Permission other = (Permission)obj;
            
            return (this.type == other.type)
                && this.user.equals(other.user)
                && ( (this.project == null) 
                   ? (other.project == null) 
                   : this.project.equals(other.project));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type
            + this.user.hashCode() 
            + ((this.project == null) ? 0 : this.project.hashCode());
    }

    @Override
    public String toString() {
        return "Permission [id=" + this.id 
                + ", type=" + type
                + ", user=" + this.user 
                + ", project=" + this.project + "]";
    }
    
}