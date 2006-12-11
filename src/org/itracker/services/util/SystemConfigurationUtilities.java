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

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.Language;
import org.itracker.model.NameValuePair;
import org.itracker.services.ConfigurationService;


public class SystemConfigurationUtilities {
    
    public static final String DEFAULT_DATASOURCE = "java:/ITrackerDS";
    public static final String DEFAULT_COMPONENTBEAN_TABLE_NAME = "componentbean";
    public static final String DEFAULT_COMPONENTBEAN_REL_TABLE_NAME = "issue_component_rel";
    public static final String DEFAULT_ISSUEBEAN_TABLE_NAME = "issuebean";
    public static final String DEFAULT_ISSUEHISTORYBEAN_TABLE_NAME = "issuehistorybean";
    public static final String DEFAULT_PROJECTBEAN_TABLE_NAME = "projectbean";
    public static final String DEFAULT_VERSIONBEAN_TABLE_NAME = "versionbean";
    public static final String DEFAULT_VERSIONBEAN_REL_TABLE_NAME = "issue_version_rel";

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
      * Returns the key for a particular configuration item.  This is made up of
      * a static part based on the type of configuration item, and the unique value
      * of the configuration item.
      * @param model the Configuration to return the key for
      * @return the key for the item
      */
    public static String getLanguageKey(Configuration configuration) {
        if(configuration != null) {
            int type = configuration.getType();
            if(type == SystemConfigurationUtilities.TYPE_STATUS) {
                return ITrackerResources.KEY_BASE_STATUS + configuration.getValue();
            } else if(type == SystemConfigurationUtilities.TYPE_SEVERITY) {
                return ITrackerResources.KEY_BASE_SEVERITY + configuration.getValue();
            } else if(type == SystemConfigurationUtilities.TYPE_RESOLUTION) {
                return ITrackerResources.KEY_BASE_RESOLUTION + configuration.getValue();
            }
        }
        return "";
    }

    /**
      * This method will attempt to load all of the locales defined in the ITracker.properties file,
      * and add them to the database if they don't already exist.
      * @param configurationService a configurationService object to use when processing the locales
      * @param forceReload if true, it will reload the languages from the property files even if they are listed
      *                    as being up to date
      */
    public static void initializeAllLanguages(ConfigurationService configurationService, boolean forceReload) {
        HashSet<String> definedLocales = new HashSet<String>();

        configurationService.initializeLocale(ITrackerResources.BASE_LOCALE, forceReload);

        Language definedLocalesString = configurationService.getLanguageItemByKey(ITrackerResources.DEFINED_LOCALES_KEY, null);

        if(definedLocalesString != null && definedLocalesString.getResourceValue() != null) {
            String locales = definedLocalesString.getResourceValue();
            StringTokenizer token = new StringTokenizer(locales, ",");
            while(token.hasMoreTokens()) {
                String locale = token.nextToken();
                if(locale.length() == 5 && locale.indexOf('_') == 2) {
                    definedLocales.add(locale.substring(0, 2));
                }
                definedLocales.add(locale);
            }
        }

        for(Iterator iter = definedLocales.iterator(); iter.hasNext(); ) {
            String locale = (String) iter.next();
            configurationService.initializeLocale(locale, forceReload);
        }
    }

    public static long getVersionAsLong(String version) {
        long versionNumber = 0;

        if(version != null) {
            try {
                StringTokenizer token = new StringTokenizer(version, ".");
                if(token.countTokens() == 3) {
                      versionNumber += 1000000 * Integer.parseInt(token.nextToken());
                      versionNumber += 1000 * Integer.parseInt(token.nextToken());
                      versionNumber += Integer.parseInt(token.nextToken());
                }
            } catch(Exception e) {
            }
        }

        return versionNumber;
    }

    public static int getLocaleType(String locale) {
        if(locale == null || locale.equals("")) {
            return LOCALE_TYPE_INVALID;
        }

        if(ITrackerResources.BASE_LOCALE.equalsIgnoreCase(locale)) {
            return LOCALE_TYPE_BASE;
        } else if(locale.length() == 5 && locale.indexOf('_') == 2) {
            return LOCALE_TYPE_LOCALE;
        } else if(locale.length() == 2) {
            return LOCALE_TYPE_LANGUAGE;
        } else {
            return LOCALE_TYPE_INVALID;
        }
    }

    public static String getLocalePart(String locale, int partType) {
        if(locale == null || partType == LOCALE_TYPE_INVALID) {
            return null;
        }

        if(partType == LOCALE_TYPE_LOCALE && locale.length() == 5 && locale.indexOf('_') == 2) {
            return locale;
        } else if(partType == LOCALE_TYPE_LANGUAGE && locale.length() == 5 && locale.indexOf('_') == 2) {
            return locale.substring(0, 2);
        } else if(partType == LOCALE_TYPE_LANGUAGE && locale.length() == 2) {
            return locale;
        } else if(partType == LOCALE_TYPE_BASE) {
            return ITrackerResources.BASE_LOCALE;
        }

        return null;
    }

    public static Configuration[] nvpArrayToConfigurationArray(int configType, NameValuePair[] names) {
        if(names == null) {
            return new Configuration[0];
        }

        Configuration[] configModels = new Configuration[names.length];
        for(int i = 0; i < names.length; i++) {
            configModels[i] = new Configuration(configType, names[i]);
        }

        return configModels;
    }
}
