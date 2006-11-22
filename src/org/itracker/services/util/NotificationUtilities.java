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

import java.util.HashMap;
import java.util.Locale;
import org.itracker.core.resources.ITrackerResources;

public class NotificationUtilities  {
    public static final int ROLE_ANY = -1;
    public static final int ROLE_CREATOR = 1;
    public static final int ROLE_OWNER = 2;
    public static final int ROLE_CONTRIBUTER = 3;
    public static final int ROLE_QA = 4;
    public static final int ROLE_PM = 5;
    public static final int ROLE_PO = 6;
    public static final int ROLE_CO = 7;
    public static final int ROLE_VO = 8;
    public static final int ROLE_IP = 9;

    public static final int TYPE_CREATED = 1;
    public static final int TYPE_UPDATED = 2;
    public static final int TYPE_ASSIGNED = 3;
    public static final int TYPE_CLOSED = 4;
    public static final int TYPE_SELF_REGISTER = 5;
    public static final int TYPE_ISSUE_REMINDER = 6;

    private static HashMap<Locale,HashMap<String,String>> roleNames;

    public NotificationUtilities() {
    }

    public static String getRoleName(int value) {
        return getRoleName(value, ITrackerResources.getLocale());
    }

    public static String getRoleName(int value, Locale locale) {
        return ITrackerResources.getString("itracker.notification.role." + value, locale);
    }

    public static HashMap getRoleNames() {
        return getRoleNames(ITrackerResources.getLocale());
    }

    public static HashMap getRoleNames(Locale locale) {
        HashMap<String,String> roles = roleNames.get(locale);
        if(roles == null) {
            roles = new HashMap<String,String>();
            roles.put(Integer.toString(ROLE_CREATOR), getRoleName(ROLE_CREATOR, locale));
            roles.put(Integer.toString(ROLE_OWNER), getRoleName(ROLE_OWNER, locale));
            roles.put(Integer.toString(ROLE_CONTRIBUTER), getRoleName(ROLE_CONTRIBUTER, locale));
            roles.put(Integer.toString(ROLE_QA), getRoleName(ROLE_QA, locale));
            roles.put(Integer.toString(ROLE_PM), getRoleName(ROLE_PM, locale));
            roles.put(Integer.toString(ROLE_PO), getRoleName(ROLE_PO, locale));
            roles.put(Integer.toString(ROLE_CO), getRoleName(ROLE_CO, locale));
            roles.put(Integer.toString(ROLE_VO), getRoleName(ROLE_VO, locale));
            roles.put(Integer.toString(ROLE_IP), getRoleName(ROLE_IP, locale));
        }
        roleNames.put(locale, roles);
        return roles;
    }

    public static String getTypeName(int value) {
        return getTypeName(value, ITrackerResources.getLocale());
    }

    public static String getTypeName(int value, Locale locale) {
        return ITrackerResources.getString("itracker.notification.type." + value, locale);
    }

}
