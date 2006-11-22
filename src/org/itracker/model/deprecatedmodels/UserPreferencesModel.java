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

class UserPreferencesModel extends GenericModel {
    private Integer userId;
    private String userLogin;

    private String userLocale;

    private boolean saveLogin;

    private int numItemsOnIndex;
    private int numItemsOnIssueList;
    private boolean showClosedOnIssueList;
    private String sortColumnOnIssueList;
    private int hiddenIndexSections;

    private boolean rememberLastSearch;
    private boolean useTextActions;

    public UserPreferencesModel() {
        userId = new Integer(-1);
        userLogin = "";

        userLocale = "";

        saveLogin = false;

        numItemsOnIndex = -1;
        numItemsOnIssueList = -1;
        showClosedOnIssueList = true;
        sortColumnOnIssueList = "";
        hiddenIndexSections = 0;

        rememberLastSearch = true;
        useTextActions = false;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer value) {
        userId = value;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String value) {
        userLogin = value;
    }

    public boolean getSaveLogin() {
        return saveLogin;
    }

    public void setSaveLogin(boolean value) {
        saveLogin = value;
    }

    public String getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(String value) {
        userLocale = value;
    }

    public int getNumItemsOnIndex() {
        return numItemsOnIndex;
    }

    public void setNumItemsOnIndex(int value) {
        numItemsOnIndex = value;
    }

    public int getNumItemsOnIssueList() {
        return numItemsOnIssueList;
    }

    public void setNumItemsOnIssueList(int value) {
        numItemsOnIssueList = value;
    }

    public boolean getShowClosedOnIssueList() {
        return showClosedOnIssueList;
    }

    public void setShowClosedOnIssueList(boolean value) {
        showClosedOnIssueList = value;
    }

    public String getSortColumnOnIssueList() {
        return sortColumnOnIssueList;
    }

    public void setSortColumnOnIssueList(String value) {
        sortColumnOnIssueList = value;
    }

    public int getHiddenIndexSections() {
        return hiddenIndexSections;
    }

    public void setHiddenIndexSections(int value) {
        hiddenIndexSections = value;
    }

    public boolean getRememberLastSearch() {
        return rememberLastSearch;
    }

    public void setRememberLastSearch(boolean value) {
        rememberLastSearch = value;
    }

    public boolean getUseTextActions() {
        return useTextActions;
    }

    public void setUseTextActions(boolean value) {
        useTextActions = value;
    }

    public String toString() {
        return "UserPrefs " + this.getUserLogin() + "(" + this.getId() + ")" +
               "  UserLocale: " + this.getUserLocale() + "  UseTextActions: " + this.getUseTextActions();
    }

}
