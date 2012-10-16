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

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.NameValuePair;
import org.itracker.services.ConfigurationService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SystemConfigurationUtilities {

    public static final String DEFAULT_DATASOURCE = "java:/ITrackerDS";
    private static final Logger log = Logger.getLogger(SystemConfigurationUtilities.class);

    public static final int TYPE_INITIALIZED = -1;
    public static final int TYPE_LOCALE = 1;
    public static final int TYPE_STATUS = 2;
    public static final int TYPE_SEVERITY = 3;
    public static final int TYPE_RESOLUTION = 4;
    public static final int TYPE_CUSTOMFIELD = 5;

    public static final int ACTION_CREATE = 1;
    public static final int ACTION_UPDATE = 2;
    public static final int ACTION_REMOVE = 3;

    public static final int LOCALE_TYPE_INVALID = -1;
    public static final int LOCALE_TYPE_BASE = 1;
    public static final int LOCALE_TYPE_LANGUAGE = 2;
    public static final int LOCALE_TYPE_LOCALE = 3;

    /**
     * Returns the key for a particular configuration item. This is made up of a
     * static part based on the type of configuration item, and the unique value
     * of the configuration item.
     *
     * @param model the Configuration to return the key for
     * @return the key for the item
     */
    public static String getLanguageKey(Configuration configuration) {
        if (configuration != null) {
            int type = configuration.getType();
            if (type == SystemConfigurationUtilities.TYPE_STATUS) {
                return ITrackerResources.KEY_BASE_STATUS
                        + configuration.getValue();
            } else if (type == SystemConfigurationUtilities.TYPE_SEVERITY) {
                return ITrackerResources.KEY_BASE_SEVERITY
                        + configuration.getValue();
            } else if (type == SystemConfigurationUtilities.TYPE_RESOLUTION) {
                return ITrackerResources.KEY_BASE_RESOLUTION
                        + configuration.getValue();
            }
        }
        return "";
    }

    /**
     * This method will attempt to load all of the locales defined in the
     * ITracker.properties file, and add them to the database if they don't
     * already exist.
     *
     * @param configurationService a configurationService object to use when processing the
     *                             locales
     * @param forceReload          if true, it will reload the languages from the property files
     *                             even if they are listed as being up to date
     */
    public static void initializeAllLanguages(
            ConfigurationService configurationService, boolean forceReload) {
        HashSet<String> definedLocales = new HashSet<String>();

        configurationService.initializeLocale(ITrackerResources.BASE_LOCALE,
                forceReload);

        String definedLocalesString;
        try {
            definedLocalesString = configurationService
                    .getLanguageItemByKey(ITrackerResources.DEFINED_LOCALES_KEY,
                            null).getResourceValue();
        } catch (RuntimeException e) {
            definedLocalesString = ITrackerResources.getString(ITrackerResources.DEFINED_LOCALES_KEY);
        }
        if (definedLocalesString != null) {
            StringTokenizer token = new StringTokenizer(definedLocalesString, ",");
            while (token.hasMoreTokens()) {
                String locale = token.nextToken();
                if (locale.length() == 5 && locale.indexOf('_') == 2) {
                    definedLocales.add(locale.substring(0, 2));
                }
                definedLocales.add(locale);
            }
        }

        for (Iterator<String> iter = definedLocales.iterator(); iter.hasNext(); ) {
            String locale = iter.next();
            configurationService.initializeLocale(locale, forceReload);
        }
    }

    public static long getVersionAsLong(String version) {
        long versionNumber = 0;
        if (log.isDebugEnabled()) {
            log.debug("getVersionAsLong: transforming " + version);
        }
        if (version != null) {
            // support -SNAPSHOT versions
            version = version.split("-")[0];
            StringTokenizer token = new StringTokenizer(version, ".");
            try {
                if (token.countTokens() > 0 && token.countTokens() <= 3) {
                    versionNumber += 1000000L * Integer.parseInt(token
                            .nextToken());
                    versionNumber += 1000L * Integer
                            .parseInt(token.nextToken());
                    versionNumber += Integer.parseInt(token.nextToken());
                } else {
                    throw new IllegalArgumentException("The version " + version + " is not parseable, excpected '(int)number[.(int)major[.(int)minor]]");
                }
            } catch (NumberFormatException nfe) {
                log.warn("getVersionAsLong: failed to parse version " + version, nfe);
            } catch (NoSuchElementException nsee) {
                log.warn("getVersionAsLong: failed to parse version, elements are not complete for " + version, nsee);
            }

        }

        if (log.isDebugEnabled()) {
            log.debug("getVersionAsLong: returning " + versionNumber);
        }
        return versionNumber;
    }

    public static int getLocaleType(String locale) {
        if (locale == null || locale.equals("")) {
            return LOCALE_TYPE_INVALID;
        }

        if (ITrackerResources.BASE_LOCALE.equalsIgnoreCase(locale)) {
            return LOCALE_TYPE_BASE;
        } else if (locale.length() == 5 && locale.indexOf('_') == 2) {
            return LOCALE_TYPE_LOCALE;
        } else if (locale.length() == 2) {
            return LOCALE_TYPE_LANGUAGE;
        } else {
            return LOCALE_TYPE_INVALID;
        }
    }

    public static String getLocalePart(String locale, int partType) {
        if (locale == null || partType == LOCALE_TYPE_INVALID) {
            return null;
        }

        if (partType == LOCALE_TYPE_LOCALE && locale.length() == 5
                && locale.indexOf('_') == 2) {
            return locale;
        } else if (partType == LOCALE_TYPE_LANGUAGE && locale.length() == 5
                && locale.indexOf('_') == 2) {
            return locale.substring(0, 2);
        } else if (partType == LOCALE_TYPE_LANGUAGE && locale.length() == 2) {
            return locale;
        } else if (partType == LOCALE_TYPE_BASE) {
            return ITrackerResources.BASE_LOCALE;
        }

        return null;
    }

    public static Configuration[] nvpArrayToConfigurationArray(int configType,
                                                               NameValuePair[] names) {
        if (names == null) {
            return new Configuration[0];
        }

        Configuration[] configModels = new Configuration[names.length];
        for (int i = 0; i < names.length; i++) {
            configModels[i] = new Configuration(configType, names[i]);
        }

        return configModels;
    }
}
