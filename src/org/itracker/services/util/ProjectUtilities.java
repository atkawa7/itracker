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

package org.itracker.services.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.itracker.core.resources.ITrackerResources;


public class ProjectUtilities  {
    // Options use bitmasks and are stored as a single integer in the db
    public static final int OPTION_SURPRESS_HISTORY_HTML = 1;
    public static final int OPTION_ALLOW_ASSIGN_TO_CLOSE = 2;
    public static final int OPTION_PREDEFINED_RESOLUTIONS = 4;
    public static final int OPTION_ALLOW_SELF_REGISTERED_CREATE = 8;
    public static final int OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL = 16;
    public static final int OPTION_NO_ATTACHMENTS = 32;
    public static final int OPTION_LITERAL_HISTORY_HTML = 64;

    public static final int STATUS_DELETED = -1;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_VIEWABLE = 2;
    public static final int STATUS_LOCKED = 3;

    private static HashMap<Locale,HashMap<String,String>> statusNames = new HashMap<Locale,HashMap<String,String>>();

    public ProjectUtilities() {
    }

    public static String getStatusName(int value) {
        return getStatusName(value, ITrackerResources.getLocale());
    }

    public static String getStatusName(int value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_PROJECT_STATUS + value, locale);
    }

    public static HashMap<String,String> getStatusNames() {
        return getStatusNames(ITrackerResources.getLocale());
    }

    public static HashMap<String,String> getStatusNames(Locale locale) {
        HashMap<String,String> statuses = new HashMap<String,String>();
        statuses = statusNames.get(locale);
        if(statuses == null) {
            statuses = new HashMap<String,String>();
            statuses.put(Integer.toString(STATUS_DELETED), getStatusName(STATUS_DELETED, locale));
            statuses.put(Integer.toString(STATUS_ACTIVE), getStatusName(STATUS_ACTIVE, locale));
            statuses.put(Integer.toString(STATUS_VIEWABLE), getStatusName(STATUS_VIEWABLE, locale));
            statuses.put(Integer.toString(STATUS_LOCKED), getStatusName(STATUS_LOCKED, locale));
        }
        statusNames.put(locale, statuses);
        return statuses;
    }

    public static boolean hasOption(int option, int currentOptions) {
        return ((option & currentOptions) == option);
    }

    public static Integer[] getOptions(int currentOptions) {
        List<Integer> options = new ArrayList<Integer>();
        if(hasOption(OPTION_SURPRESS_HISTORY_HTML, currentOptions)) {
            options.add(new Integer(OPTION_SURPRESS_HISTORY_HTML));
        }
        if(hasOption(OPTION_ALLOW_ASSIGN_TO_CLOSE, currentOptions)) {
            options.add(new Integer(OPTION_ALLOW_ASSIGN_TO_CLOSE));
        }
        if(hasOption(OPTION_PREDEFINED_RESOLUTIONS, currentOptions)) {
            options.add(new Integer(OPTION_PREDEFINED_RESOLUTIONS));
        }
        if(hasOption(OPTION_ALLOW_SELF_REGISTERED_CREATE, currentOptions)) {
            options.add(new Integer(OPTION_ALLOW_SELF_REGISTERED_CREATE));
        }
        if(hasOption(OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL, currentOptions)) {
            options.add(new Integer(OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL));
        }
        if(hasOption(OPTION_NO_ATTACHMENTS, currentOptions)) {
            options.add(new Integer(OPTION_NO_ATTACHMENTS));
        }
        if(hasOption(OPTION_LITERAL_HISTORY_HTML, currentOptions)) {
            options.add(new Integer(OPTION_LITERAL_HISTORY_HTML));
        }
        Integer[] optionsArray = new Integer[options.size()];
        options.toArray(optionsArray);
        return optionsArray;
    }

}
