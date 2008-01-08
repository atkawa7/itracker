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

public class NotificationUtilities {

	public static enum Role {

		ANY(-1), CREATOR(1), OWNER(2), CONTRIBUTOR(3), QA(4), PM(5), PO(6), CO(
				7), VO(8), IP(9);

		private Integer numVal;

		Role(Integer numVal) {
			this.numVal = numVal;
		}

		public Integer numVal() {
			return numVal;
		}

	}

	public static enum Type {

		CREATED(1), UPDATED(2), ASSIGNED(3), CLOSED(4), SELF_REGISTER(5), ISSUE_REMINDER(
				6);

		private Integer numVal;

		Type(Integer numVal) {
			this.numVal = numVal;
		}

		public Integer numVal() {
			return numVal;
		}

	}

	/**
	 * @deprecated use enum NotificationUtilities.Role.ANY instead
	 */
	public static final int ROLE_ANY = NotificationUtilities.Role.ANY.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.CREATOR instead
	 */
	public static final int ROLE_CREATOR = NotificationUtilities.Role.CREATOR.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.OWNER instead
	 */
	public static final int ROLE_OWNER = NotificationUtilities.Role.OWNER.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.CONTRIBUTOR instead
	 */
	public static final int ROLE_CONTRIBUTER = NotificationUtilities.Role.CONTRIBUTOR.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.QA instead
	 */
	public static final int ROLE_QA = NotificationUtilities.Role.QA.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.PM instead
	 */
	public static final int ROLE_PM = NotificationUtilities.Role.PM.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.PO instead
	 */
	public static final int ROLE_PO = NotificationUtilities.Role.PO.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.CO instead
	 */
	public static final int ROLE_CO = NotificationUtilities.Role.CO.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.VO instead
	 */
	public static final int ROLE_VO = NotificationUtilities.Role.VO.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Role.IP instead
	 */
	public static final int ROLE_IP = NotificationUtilities.Role.IP.numVal;

	/**
	 * @deprecated use enum NotificationUtilities.Type.CREATED instead
	 */
	public static final int TYPE_CREATED = Type.CREATED.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Type.UPDATED instead
	 */
	public static final int TYPE_UPDATED = Type.UPDATED.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Type.ASSIGNED instead
	 */
	public static final int TYPE_ASSIGNED = Type.ASSIGNED.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Type.CLOSED instead
	 */
	public static final int TYPE_CLOSED = Type.CLOSED.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Type.SELF_REGISTER instead
	 */
	public static final int TYPE_SELF_REGISTER = Type.SELF_REGISTER.numVal;
	/**
	 * @deprecated use enum NotificationUtilities.Type.ISSUE_REMINDER instead
	 */
	public static final int TYPE_ISSUE_REMINDER = Type.ISSUE_REMINDER.numVal;

	private static HashMap<Locale, HashMap<String, String>> roleNames = new HashMap<Locale, HashMap<String, String>>();

	public NotificationUtilities() {
		super();
	}

	/**
	 * @deprecated
	 * @param value
	 * @return
	 */
	public static String getRoleName(int value) {
		return getRoleName(value, ITrackerResources.getLocale());
	}

	/**
	 * @deprecated
	 * @param value
	 * @param locale
	 * @return
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
				+ role.numVal, locale);
	}

	public static HashMap<String, String> getRoleNames() {
		return getRoleNames(ITrackerResources.getLocale());
	}

	public static HashMap<String, String> getRoleNames(Locale locale) {
		HashMap<String, String> roles = roleNames.get(locale);
		if (roles == null) {
			roles = new HashMap<String, String>();
			roles.put(Role.CREATOR.toString(),
					getRoleName(Role.CREATOR, locale));
			roles.put(Role.OWNER.toString(), getRoleName(Role.OWNER, locale));
			roles.put(Role.CONTRIBUTOR.toString(), getRoleName(
					Role.CONTRIBUTOR, locale));
			roles.put(Role.QA.toString(), getRoleName(Role.QA, locale));
			roles.put(Role.PM.toString(), getRoleName(Role.PM, locale));
			roles.put(Role.PO.toString(), getRoleName(Role.PO, locale));
			roles.put(Role.CO.toString(), getRoleName(Role.CO, locale));
			roles.put(Role.VO.toString(), getRoleName(Role.VO, locale));
			roles.put(Role.IP.toString(), getRoleName(Role.IP, locale));
		}
		roleNames.put(locale, roles);
		return roles;
	}

	/**
	 * @deprecated
	 * @param value
	 * @return
	 */
	public static String getTypeName(int value) {
		return getTypeName(value, ITrackerResources.getLocale());
	}

	/**
	 * @deprecated
	 * @param value
	 * @param locale
	 * @return
	 */
	public static String getTypeName(int value, Locale locale) {
		return ITrackerResources.getString("itracker.notification.type."
				+ value, locale);
	}

}
