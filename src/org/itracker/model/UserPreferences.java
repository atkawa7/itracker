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
 * 
 * @author ready
 *
 */
public class UserPreferences extends AbstractBean {

    private boolean saveLogin;
    private String userLocale;
    private int numItemsOnIndex;
    private int numItemsOnIssueList;
    private boolean showClosedOnIssueList;
    private String sortColumnOnIssueList;
    private int hiddenIndexSections;
    private boolean rememberLastSearch;
    private boolean useTextActions;
    private User user;

    public int getHiddenIndexSections() {
        return hiddenIndexSections;
    }

    public void setHiddenIndexSections(int hiddenIndexSections) {
        this.hiddenIndexSections = hiddenIndexSections;
    }

    public int getNumItemsOnIndex() {
        return numItemsOnIndex;
    }

    public void setNumItemsOnIndex(int numItemsOnIndex) {
        this.numItemsOnIndex = numItemsOnIndex;
    }

    public int getNumItemsOnIssueList() {
        return numItemsOnIssueList;
    }

    public void setNumItemsOnIssueList(int numItemsOnIssueList) {
        this.numItemsOnIssueList = numItemsOnIssueList;
    }

    public boolean getRememberLastSearch() {
        return rememberLastSearch;
    }

    public void setRememberLastSearch(boolean rememberLastSearch) {
        this.rememberLastSearch = rememberLastSearch;
    }

    public boolean getSaveLogin() {
        return saveLogin;
    }

    public void setSaveLogin(boolean saveLogin) {
        this.saveLogin = saveLogin;
    }

    public boolean getShowClosedOnIssueList() {
        return showClosedOnIssueList;
    }

    public void setShowClosedOnIssueList(boolean showClosedOnIssueList) {
        this.showClosedOnIssueList = showClosedOnIssueList;
    }

    public String getSortColumnOnIssueList() {
        return sortColumnOnIssueList;
    }

    public void setSortColumnOnIssueList(String sortColumnOnIssueList) {
        this.sortColumnOnIssueList = sortColumnOnIssueList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(String userLocale) {
        this.userLocale = userLocale;
    }

    public boolean getUseTextActions() {
        return useTextActions;
    }

    public void setUseTextActions(boolean useTextActions) {
        this.useTextActions = useTextActions;
    }

}
