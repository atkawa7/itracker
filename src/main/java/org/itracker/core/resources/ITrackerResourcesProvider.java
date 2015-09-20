package org.itracker.core.resources;

import java.util.Locale;
import java.util.Properties;

/**
 * To provide Locale values from application properties configuration.
 * @author ranks
 */
public interface ITrackerResourcesProvider {
    /**
     * Load all translation keys for a locale.
     *
     * @param locale the locale
     * @return loaded properties
     */
    Properties getLanguageProperties(Locale locale);

    /**
     *
     * @param key
     * @param locale
     * @return
     */
    String getLanguageValue(String key, Locale locale);

    String getProperty(String default_locale, String defaultLocale);
}
