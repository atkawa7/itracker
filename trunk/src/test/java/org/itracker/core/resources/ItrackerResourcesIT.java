package org.itracker.core.resources;

import org.itracker.AbstractDependencyInjectionTest;
import org.junit.Test;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: masta
 * Date: 23.11.12
 * Time: 09:58
 * To change this template use File | Settings | File Templates.
 */
public class ItrackerResourcesIT extends AbstractDependencyInjectionTest {

    @Test
    public void testInitialized() {
        assertTrue(ITrackerResources.isInitialized());
    }

    @Test
    public void testGetBundleEmptyString() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle("");
        assertNotNull(resourceBundle);
        assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetBundleStringParameter() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle(ITrackerResources.getLocale());
        assertNotNull(resourceBundle);
        assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetBundleNullLocale() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle((Locale) null);
        assertNotNull(resourceBundle);
        assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey() {
        ResourceBundle b = ITrackerResources.getBundle();

        ((ITrackerResourceBundle)b).removeValue("itracker.web.attr.admin", true);
        String value = ITrackerResources.getString("itracker.web.attr.admin", ITrackerResources.getLocale());


        // When a language items is removed, the default is loaded from properties.
        assertEquals("Admin", value);
    }


    @Test
    public void testGetEditBundleNullLocale() {
        //TODO: set languageDAO of ConfigurationService
        ResourceBundle resourceBundle = ITrackerResources.getEditBundle(null);
        assertNotNull(resourceBundle);
        assertEquals(ITrackerResources.getLocale(), resourceBundle.getLocale());
        Enumeration<String> keys = resourceBundle.getKeys(); // keys of copy bundle
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            assertEquals(ITrackerResources.getString(key), resourceBundle.getString(key));
        }
        keys = ITrackerResources.getBundle().getKeys(); // keys of original bundle
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            assertEquals(ITrackerResources.getString(key), resourceBundle.getString(key));
        }
    }
    @Override
    protected String[] getDataSetFiles() {
        return new String[0];
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
