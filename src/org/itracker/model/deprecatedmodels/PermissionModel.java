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

class PermissionModel extends GenericModel implements Comparator<PermissionModel> {
    private int type;
    private String userLogin;
    private Integer projectId;
    private Integer userId;

    public PermissionModel() {
    }

    public PermissionModel(Integer projectId, int type) {
        this.projectId = projectId;
        this.type = type;
    }

    public PermissionModel(Integer projectId, int type, String userLogin) {
        this(projectId, type);
        this.userLogin = userLogin;
    }

    public PermissionModel(Integer projectId, int type, String userLogin, Integer userId) {
        this(projectId, type, userLogin);
        this.userId = userId;
    }

    public int getPermissionType() {
        return type;
    }

    public void setPermissionType(int value) {
        type = value;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String value) {
        userLogin = value;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer value) {
        userId = value;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer value) {
        projectId = value;
    }

    public String toString() {
        return "User: " + getUserLogin() + "(" + getUserId() + ") Project: " + getProjectId() + "  Permission: " + getPermissionType();
    }

    public int compare(PermissionModel a, PermissionModel b) {
        return new PermissionModel.CompareByPermission().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof PermissionModel)) {
            return false;
        }

        try {
            PermissionModel mo = (PermissionModel) obj;
            if(PermissionModel.this.getProjectId() == null || mo.getProjectId() == null) {
                return false;
            }

            if(PermissionModel.this.getPermissionType() == mo.getPermissionType() &&
               PermissionModel.this.getProjectId().intValue() == mo.getProjectId().intValue()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return ((PermissionModel.this.getProjectId() == null ? 1 : PermissionModel.this.getProjectId().hashCode()) ^
                 PermissionModel.this.getPermissionType());
    }

    public static class CompareByPermission implements Comparator {
        public int compare(Object a, Object b) {
            if(! (a instanceof PermissionModel) || ! (b instanceof PermissionModel)) {
                throw new ClassCastException();
            }

            PermissionModel ma = (PermissionModel) a;
            PermissionModel mb = (PermissionModel) b;

            if(ma.getPermissionType() == mb.getPermissionType()) {
                return 0;
            } else if(ma.getPermissionType() > mb.getPermissionType()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}