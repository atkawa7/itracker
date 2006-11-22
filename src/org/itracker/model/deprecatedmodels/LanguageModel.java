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

class LanguageModel extends GenericModel {
    private String locale;
    private String resourceKey;
    private String resourceValue;
    private int action;

    public LanguageModel() {
    }

    public LanguageModel(String locale, String key) {
        setLocale(locale);
        setResourceKey(key);
    }

    public LanguageModel(String locale, String key, String value) {
        this(locale, key);
        setResourceValue(value);
    }

    public String getLocale() {
        return (locale == null ? "" : locale);
    }

    public void setLocale(String value) {
        locale = value;
    }

    public String getResourceKey() {
        return (resourceKey == null ? "" : resourceKey);
    }

    public void setResourceKey(String value) {
        resourceKey = value;
    }

    public String getResourceValue() {
        return (resourceValue == null ? "" : resourceValue);
    }

    public void setResourceValue(String value) {
        resourceValue = value;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int value) {
        action = value;
    }

    public String toString() {
        return "LanguageItem [" + this.getId() + "] Locale: " + this.getLocale() + " Key: " +
               this.getResourceKey() + " Value: " + this.getResourceValue();
    }
}
