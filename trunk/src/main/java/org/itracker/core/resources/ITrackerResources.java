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

package org.itracker.core.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.itracker.model.Language;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.exceptions.ITrackerDirtyResourceException;
import org.itracker.web.util.ServletContextUtils;

/**
 * 
 * Please comment this class here. What is it for?
 * 
 * @author ready
 * 
 */
public class ITrackerResources {

	private static final Logger logger = Logger
			.getLogger(ITrackerResources.class);

	public static final String RESOURCE_BUNDLE_NAME = "org.itracker.core.resources.ITracker";

	public static final String DEFINED_LOCALES_KEY = "itracker.locales";

	public static final String DEFAULT_LOCALE = "en_US";

	public static final String BASE_LOCALE = "BASE";

	public static final String NO_LOCALE = "ZZ_ZZ";

	public static final String KEY_BASE_CUSTOMFIELD_TYPE = "itracker.web.generic.";

	public static final String KEY_BASE_WORKFLOW_EVENT = "itracker.workflow.field.event.";

	public static final String KEY_BASE_PROJECT_STATUS = "itracker.project.status.";

	public static final String KEY_BASE_PERMISSION = "itracker.user.permission.";

	public static final String KEY_BASE_PRIORITY = "itracker.script.priority.";

	public static final String KEY_BASE_PRIORITY_LABEL = ".label";

	public static final String KEY_BASE_PRIORITY_SIZE = "size";

	public static final String KEY_BASE_RESOLUTION = "itracker.resolution.";

	public static final String KEY_BASE_ISSUE_RELATION = "itracker.issuerelation.";

	public static final String KEY_BASE_SEVERITY = "itracker.severity.";

	public static final String KEY_BASE_STATUS = "itracker.status.";

	public static final String KEY_BASE_USER_STATUS = "itracker.user.status.";

	public static final String KEY_BASE_CUSTOMFIELD = "itracker.customfield.";

	public static final String KEY_BASE_CUSTOMFIELD_OPTION = ".option.";

	public static final String KEY_BASE_CUSTOMFIELD_LABEL = ".label";

	public static final String KEY_BASE_LOCALE_NAME = "itracker.locale.name";

	private static String defaultLocale = DEFAULT_LOCALE;

	private static HashMap<String, Locale> locales = new HashMap<String, Locale>();

	private static HashMap<Locale, ResourceBundle> languages = new HashMap<Locale, ResourceBundle>();

	private static boolean initialized = false;

	private static Object bundleLock = new Object();

	// private static ConfigurationService configurationService;

	public static Locale getLocale() {
		return getLocale(getDefaultLocale());
	}

	public static Locale getLocale(String localeString) {

//		if (logger.isDebugEnabled()) {
//			logger.debug("getLocale: " + localeString);
//		}
		if (localeString == null || localeString.trim().equals("")) {
			return getLocale(getDefaultLocale());
		}

		Locale locale = locales.get(localeString);
		if (locale == null && localeString != null
				&& !localeString.trim().equals("")) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Creating new locale for '" + localeString
							+ "'");
				}
				if (localeString.length() == 5) {
					locale = new Locale(localeString.substring(0, 2),
							localeString.substring(3));
				} else if (localeString.length() == 2) {
					locale = new Locale(localeString, "");
				} else if (localeString.equals(BASE_LOCALE)) {
					locale = new Locale("", "");
				} else {

					logger
								.error("Invalid locale '"
										+ localeString
										+ "' specified.  It must be either LN or LN_CN.");
					throw new Exception("Invalid locale string");
				}
			} catch (Exception ex) {
				if (!localeString.equals(getDefaultLocale())) {
					logger.error("Failed creating new locale for '"
							+ localeString
							+ "' attempting for default locale '"
							+ getDefaultLocale() + "'", ex);
					return getLocale(getDefaultLocale());
				} else {
					logger.error("Failed creating new default locale for '"
							+ getDefaultLocale()
							+ "' attempting for DEFAULT_LOCALE '"
							+ DEFAULT_LOCALE + "'", ex);
					return getLocale(DEFAULT_LOCALE);
				}
			}
			locales.put(localeString, locale);
		}
		return locale;
	}

	public static String getDefaultLocale() {
		return (defaultLocale == null ? DEFAULT_LOCALE : defaultLocale);
	}

	public static void setDefaultLocale(String value) {
		defaultLocale = value;
	}

	public static String getLocaleDN(String locale, Locale displayLocale) {
		String name;
		if (null == displayLocale) {
			displayLocale = getLocale();
		}
		try {
			name = getBundle(displayLocale).getString(
					KEY_BASE_LOCALE_NAME + "." + locale);
		} catch (RuntimeException e) {
			name = getLocaleNativeName(getLocale(locale));
		}

		return name;
	}

	public static String getLocaleDN(Locale locale, Locale displayLocale) {

		if (null == displayLocale) {
			return getLocaleNativeName(displayLocale);
		}
		return getLocaleDN(locale.toString(), displayLocale);

	}
	public static String getLocaleFullDN(Locale locale, Locale displayLocale) {

		if (null == locale) {
			locale = new Locale("");
		}
		String fullName = getLocaleNativeName(locale);
		if (null == displayLocale || locale.getLanguage().equals(displayLocale.getLanguage())) {
			return fullName;
		}
		if (fullName.equals(locale.toString())) {
			fullName = getLocaleDN(locale, displayLocale);
		} else {
			String localizedName = getLocaleDN(locale, displayLocale);
			if (null != fullName && null != localizedName && !localizedName.trim().equals(fullName.trim())) {
				return fullName.trim() + " (" + localizedName.trim() + ")";
			} else if (null != localizedName) {
				return localizedName.trim();
			} else if (null != fullName) {
				return fullName.trim();
			}
			
		}
		
		return "Unknown: " + locale.toString();

	}
	public static String getLocaleNativeName(Locale locale) {
		try {
			return getString(KEY_BASE_LOCALE_NAME, locale);
//			return getBundle(locale).getString(KEY_BASE_LOCALE_NAME);
		} catch (MissingResourceException e) {
			return locale.toString();
//			return locale.getDisplayName(locale);
		}
	}

	public static final Map<String, String> getLocaleNamesMap(Locale locale, Set<String> languageCodes,Map<String, List<String>> languagesMap ) {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		for (String languageCode : languageCodes) {
			List<String> languagelist = languagesMap.get(languageCode);
			
			String name = getLocaleFullDN(ITrackerResources.getLocale(languageCode), locale);

			ret.put(languageCode, name);
			for (String languageitem : languagelist) {
				name = getLocaleFullDN(ITrackerResources.getLocale(languageitem), locale);
				ret.put(languageitem, name);
			}

		}
		if (ret.size() == 0) {
			ret.put(getDefaultLocale(), getLocaleNativeName(getLocale(getDefaultLocale())));
		}
		return ret;
		
	}
	public static ResourceBundle getBundle() {
		return getBundle(getDefaultLocale());
	}

	public static ResourceBundle getBundle(String locale) {
		if (locale == null || locale.equals("")) {
			locale = getDefaultLocale();
		}

		return getBundle(getLocale(locale));
	}

	public static ResourceBundle getBundle(Locale locale) {
		if (locale == null) {
			locale = getLocale(getDefaultLocale());
		}
		ResourceBundle bundle = (ResourceBundle) languages.get(locale);
		if (bundle == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("getBundle: Loading new resource bundle for locale " + locale
					+ " from the database.");
			}
			List<Language> languageItems = ServletContextUtils
					.getItrackerServices().getConfigurationService()
					.getLanguage(locale);
//			if (locale.getLanguage().equals("")) {
//				locale = getLocale()
//			}
			bundle = ITrackerResourceBundle.loadBundle(locale, languageItems);
			if (logger.isDebugEnabled()) {
				logger.debug("getBundle: got loaded for locale " + locale
					+ " with items " + languageItems + " from the database.");
			}
			if (bundle != null) {
				putBundle(locale, bundle);
			} else if (!locale.toString().equals(getDefaultLocale())) {
				bundle = getBundle(getLocale());
			}
		}

		return bundle;
	}

	public static ResourceBundle getEditBundle(Locale locale) {
		if (locale == null) {
			locale = getLocale(getDefaultLocale());
		}
		ResourceBundle bundle = (ResourceBundle) languages.get(locale);
		logger.debug("Loading new resource bundle for locale " + locale
				+ " from the database.");
		List<Language> languageItems = ServletContextUtils
				.getItrackerServices().getConfigurationService().getLanguage(
						locale);
		bundle = ITrackerResourceBundle.loadBundle(locale, languageItems);
		if (bundle != null) {
			putBundle(locale, bundle);
		} else if (!locale.toString().equals(getDefaultLocale())) {
			bundle = getBundle(getLocale());
		}

		return bundle;
	}

	public static void putBundle(Locale locale, ResourceBundle bundle) {
		if (locale != null && bundle != null) {
			synchronized (bundleLock) {
				languages.put(locale, bundle);
//				if (bundle instanceof ITrackerResourceBundle) {
//					setData((ITrackerResourceBundle)bundle);
//				}
				String localeString = locale.toString();
				if (localeString.length() == 5) {
					localeString = localeString.substring(0, 2) + "_"
							+ localeString.substring(3).toUpperCase();
				}
				locales.put(localeString, locale);
			}
		}
	}

	static void setData(ITrackerResourceBundle bundle) {

		List<Language> languageItems = ServletContextUtils
				.getItrackerServices().getConfigurationService()
				.getLanguage(bundle.getLocale());
		bundle.setContents(languageItems);
	}
	/**
	 * Clears a single cached resource bundle. The next time the bundle is
	 * accessed, it will be reloaded and placed into the cache.
	 */
	public static void clearBundle(Locale locale) {
		if (locale != null) {
			synchronized (bundleLock) {
				ResourceBundle bundle = languages.get(locale);
//				if (bundle instanceof ITrackerResourceBundle) {
//					((ITrackerResourceBundle)bundle).
//				}
				languages.remove(locale);
			}
		}
	}

	/**
	 * Clears all cached resource bundles. The next time a bundle is accessed,
	 * it will be reloaded and placed into the cache.
	 */
	public static void clearBundles() {
		synchronized (bundleLock) {

			languages.clear();
			
		}
	}

	/**
	 * Clears a single key from all cached resource bundles. The key is then
	 * marked that it is dirty and should be reloaded on hte next access.
	 */
	public static void clearKeyFromBundles(String key, boolean markDirty) {
		if (key != null) {
			synchronized (bundleLock) {
				for (Iterator<ResourceBundle> iter = languages.values()
						.iterator(); iter.hasNext();) {
					((ITrackerResourceBundle) iter.next()).removeValue(key,
							markDirty);
				}
			}
		}
	}

	public static String getString(String key) {
		return getString(key, getLocale(defaultLocale));
	}

	public static String getString(String key, String locale) {
		if (key == null) {
			return "";
		}

		if (locale == null || locale.equals("")) {
			locale = getDefaultLocale();
		}

		return getString(key, getLocale(locale));
	}

	public static String getString(String key, Locale locale) {
		if (key == null) {
			return "";
		}

		if (locale == null) {
			locale = getLocale(getDefaultLocale());
		}
		String val;
		try {
			try {
//				 if (logger.isDebugEnabled()) {
//				 logger.debug("getString: " + key + " for locale " + locale);
//				 }
				val = getBundle(locale).getString(key);
				if (null != val) {

//					 if (logger.isDebugEnabled()) {
//					 logger.debug("getString: found " + val + " for key" + key
//					 + ", locale " + locale);
//					 }
					return val;
				} else {
					val = ITrackerResources.getString(key);

//					 if (logger.isDebugEnabled()) {
//					 logger.debug("getString: found in base: " + val +
//					 " for key" + key);
//					 }
				}
			} catch (ITrackerDirtyResourceException idre) {

//				 logger.debug("Loading new key to replace dirty key " + key +
//				 " for resource bundle for locale "
//				 + locale);
				Language languageItem = ServletContextUtils
						.getItrackerServices().getConfigurationService()
						.getLanguageItemByKey(key, locale);
				ResourceBundle bundle = getBundle(locale);
				((ITrackerResourceBundle) bundle)
						.updateValue(languageItem);
				val = bundle.getString(key);
			}
			return val;
		} catch (NullPointerException ex) {
			logger.error(
					"Unable to get any resources.  The requested locale was "
							+ locale, ex);
			return "MISSING BUNDLE: " + locale;
		} catch (MissingResourceException ex) {
			logger.warn(
					"MissingResourceException caught while retrieving translation key '"
							+ key + "' for locale " + locale, ex);
			return "MISSING KEY: " + key;
		} catch (NoSuchEntityException ex) {
			logger.info("getString: not found " + key + " locale: " + locale,
					ex);
			try {
				return getEditBundle(locale).getString(key);
			} catch (Exception ex2) {
				logger.warn(
						"getString: caught while retrieving translation key '"
								+ key + "' for locale " + locale, ex2);
				return "MISSING KEY: " + key;
			}
		}
	}

	public static String getString(String key, Object[] options) {
		return getString(key, getLocale(getDefaultLocale()), options);
	}

	public static String getString(String key, String locale, Object[] options) {
		return getString(key, getLocale(locale), options);
	}

	public static String getString(String key, Locale locale, Object[] options) {
		String message = getString(key, locale);
		return MessageFormat.format(message, options, locale);
	}

	public static String getString(String key, String locale, String option) {
		String message = getString(key, locale);
		return MessageFormat.format(message, new Object[] { option },
				getLocale(locale));
	}

	public static String getString(String key, Locale locale, String option) {
		String message = getString(key, locale);
		return MessageFormat.format(message, new Object[] { option }, locale);
	}

	public static String getCheckForKey(String key)
			throws MissingResourceException {
		return getCheckForKey(key, getLocale());
	}

	public static String getCheckForKey(String key, Locale locale)
			throws MissingResourceException {
		try {
			return getBundle(locale).getString(key);
		} catch (ITrackerDirtyResourceException idre) {
			return getString(key, locale);
		} catch (NullPointerException ex) {
			logger.error("Unable to get ResourceBundle for locale " + locale,
					ex);
			throw new MissingResourceException("MISSING LOCALE: " + locale,
					"ITrackerResources", key);
		}
	}

	public static boolean isLongString(String key) {
		String value = getString(key);
		if (value.length() > 80 || value.indexOf('\n') > 0) {
			return true;
		}
		return false;
	}

	public static String escapeUnicodeString(String str, boolean escapeAll) {
		if (str == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!escapeAll && ((ch >= 0x0020) && (ch <= 0x007e))) {
				sb.append(ch);
			} else {
				sb.append('\\').append('u');
				sb.append(encodeHex((ch >> 12) & 0xF));
				sb.append(encodeHex((ch >> 8) & 0xF));
				sb.append(encodeHex((ch >> 4) & 0xF));
				sb.append(encodeHex(ch & 0xF));
			}
		}
		return sb.toString();
	}

	public static String unescapeUnicodeString(String str) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < str.length();) {
			char ch = str.charAt(i++);
			if (ch == '\\') {
				if (str.charAt(i++) == 'u') {
					int value = 0;
					for (int j = 0; j < 4; j++) {
						value = (value << 4) + decodeHex(str.charAt(i++));
					}
					sb.append((char) value);
				} else {
					sb.append("\\" + str.charAt(i));
				}
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public static final String HEXCHARS = "0123456789ABCDEF";

	public static char encodeHex(int value) {
		return HEXCHARS.charAt(value & 0xf);
	}

	public static int decodeHex(char ch) {
		int value = -1;

		if (ch >= '0' && ch <= '9') {
			value = ch - '0';
		} else if (ch >= 'a' && ch <= 'f') {
			value = ch - 'a' + 10;
		} else if (ch >= 'A' && ch <= 'F') {
			value = ch - 'A' + 10;
		}

		return value;
	}

	// public static void setConfigurationService(ConfigurationService
	// configurationService) {
	// ITrackerResources.configurationService = configurationService;
	// }

	public static boolean isInitialized() {
		return initialized;
	}

	public static void setInitialized(boolean initialized) {
		ITrackerResources.initialized = initialized;
	}

	public static String getParentLocale(String locale) {
		String localeCode = locale;
		if (localeCode == null) {
			localeCode = getDefaultLocale();
		} else if (localeCode.equals(getDefaultLocale())) {
			localeCode = BASE_LOCALE;
		} else {
			Locale l = getLocale(locale);

			if (!l.getVariant().equals("")) {
				localeCode = l.getLanguage() + "_" + l.getCountry();
			}
			if (!l.getCountry().equals("")) {
				localeCode = l.getLanguage();
			}
			if (!l.getLanguage().equals("")) {
				localeCode = getDefaultLocale();
			}
		}
		return localeCode;
	}

}
