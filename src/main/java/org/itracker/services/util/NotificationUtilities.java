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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.User;

import java.util.*;

public class NotificationUtilities {

    /**
     * @deprecated use enum NotificationUtilities.Role.ANY instead
     */
    public static final int ROLE_ANY = Notification.Role.ANY.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.CREATOR instead
     */
    public static final int ROLE_CREATOR = Notification.Role.CREATOR.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.OWNER instead
     */
    public static final int ROLE_OWNER = Notification.Role.OWNER.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.CONTRIBUTOR instead
     */
    public static final int ROLE_CONTRIBUTER = Notification.Role.CONTRIBUTER.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.QA instead
     */
    public static final int ROLE_QA = Notification.Role.QA.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.PM instead
     */
    public static final int ROLE_PM = Notification.Role.PM.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.PO instead
     */
    public static final int ROLE_PO = Notification.Role.PO.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.CO instead
     */
    public static final int ROLE_CO = Notification.Role.CO.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.VO instead
     */
    public static final int ROLE_VO = Notification.Role.VO.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Role.IP instead
     */
    public static final int ROLE_IP = Notification.Role.IP.getCode();

    /**
     * @deprecated use enum NotificationUtilities.Type.CREATED instead
     */
    public static final int TYPE_CREATED = Notification.Type.CREATED.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Type.UPDATED instead
     */
    public static final int TYPE_UPDATED = Notification.Type.UPDATED.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Type.ASSIGNED instead
     */
    public static final int TYPE_ASSIGNED = Notification.Type.ASSIGNED.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Type.CLOSED instead
     */
    public static final int TYPE_CLOSED = Notification.Type.CLOSED.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Type.SELF_REGISTER instead
     */
    public static final int TYPE_SELF_REGISTER = Notification.Type.SELF_REGISTER.getCode();
    /**
     * @deprecated use enum NotificationUtilities.Type.ISSUE_REMINDER instead
     */
    public static final int TYPE_ISSUE_REMINDER = Notification.Type.ISSUE_REMINDER.getCode();

    /**
     * @deprecated, use enumeration Notification.Role instead
     */
    private static HashMap<Locale, HashMap<Role, String>> roleNames = new HashMap<Locale, HashMap<Role, String>>();

    public NotificationUtilities() {
        super();
    }

    /**
     * @deprecated
     */
    public static String getRoleName(int value) {
        return getRoleName(value, ITrackerResources.getLocale());
    }

    /**
     * @deprecated
     */
    public static String getRoleName(int value, Locale locale) {
        return ITrackerResources.getString("itracker.notification.role."
                + value, locale);
    }

    public static String getRoleName(Role role) {
        return getRoleName(role, ITrackerResources.getLocale());
    }

    public static String getRoleName(Role role, Locale locale) {
        String s;
        if (null != (s = ITrackerResources.getString(
                "itracker.notification.role." + role, locale))) {
            return s;
        }

        return ITrackerResources.getString("itracker.notification.role."
                + role.getCode(), locale);
    }

    /**
     * @deprecated prefer direct use of Role enumeration
     */
    public static HashMap<Role, String> getRoleNames() {
        return getRoleNames(ITrackerResources.getLocale());
    }

    public static HashMap<Role, String> getRoleNames(Locale locale) {
        HashMap<Role, String> roles = roleNames.get(locale);
        if (roles == null) {
            roles = new HashMap<Role, String>();
            roles.put(Notification.Role.CREATOR,
                    getRoleName(Notification.Role.CREATOR, locale));
            roles.put(Notification.Role.OWNER, getRoleName(Role.OWNER, locale));
            roles.put(Notification.Role.CONTRIBUTER, getRoleName(
                    Notification.Role.CONTRIBUTER, locale));
            roles.put(Notification.Role.QA, getRoleName(Role.QA, locale));
            roles.put(Notification.Role.PM, getRoleName(Role.PM, locale));
            roles.put(Notification.Role.PO, getRoleName(Role.PO, locale));
            roles.put(Notification.Role.CO, getRoleName(Role.CO, locale));
            roles.put(Notification.Role.VO, getRoleName(Role.VO, locale));
            roles.put(Notification.Role.IP, getRoleName(Role.IP, locale));
        }
        roleNames.put(locale, roles);
        return roles;
    }

    /**
     * @deprecated
     */
    public static String getTypeName(int value) {
        return getTypeName(value, ITrackerResources.getLocale());
    }

    /**
     * @deprecated
     */
    public static String getTypeName(int value, Locale locale) {
        return ITrackerResources.getString("itracker.notification.type."
                + value, locale);
    }

    public static final Map<User, Set<Notification.Role>> mappedRoles(List<Notification> notifications) {

        Map<User, Set<Role>> mapping = new Hashtable<User, Set<Role>>();
        Iterator<Notification> notificationIt = notifications.iterator();
        while (notificationIt.hasNext()) {
            Notification notification = (Notification) notificationIt.next();
            Set<Role> roles;
            if (mapping.keySet().contains(notification.getUser())) {
                roles = mapping.get(notification.getUser());
                roles.add(notification.getRole());
            } else {
                roles = new TreeSet<Role>(new Comparator<Role>() {
                    public int compare(Role o1, Role o2) {
                        return new CompareToBuilder().append(o1.getCode(), o2.getCode()).toComparison();
                    }
                });
                roles.add(notification.getRole());
                mapping.put(notification.getUser(), roles);
            }
        }


        return mapping;
    }

}
