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
public class Permission extends AbstractBean {

    private User user;
    private Project project;
    private int type;
    
    public Permission() {
    }

    public Permission(Project project, int type) {
        this.project = project;
        this.type = type;
    }

    public Permission(Project project, int type, User user) {
        this(project, type);
        this.user = user;
    }
    
    public int getPermissionType() {
        return type;
    }

    public void setPermissionType(int type) {
        this.type = type;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
