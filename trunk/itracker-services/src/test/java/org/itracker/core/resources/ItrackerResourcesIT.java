package org.itracker.core.resources;

import org.itracker.services.AbstractServicesIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.itracker.Assert.*;


public class ItrackerResourcesIT extends AbstractServicesIntegrationTest {

    @Test
    public void testInitialized() {
        assertTrue(ITrackerResources.isInitialized());
    }

    @Test
    public void testGetBundleEmptyString() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle("");
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetBundleStringParameter() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle(ITrackerResources.getLocale());
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetBundleNullLocale() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle((Locale) null);
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey() {
        ResourceBundle b = ITrackerResources.getBundle(Locale.ENGLISH);
        final String value_1 = ITrackerResources.getString("itracker.web.attr.admin", ITrackerResources.getLocale());
        Assert.assertEquals("Admin", value_1);
        ((ITrackerResourceBundle)b).removeValue("itracker.web.attr.admin", true);
        String value_2 = ITrackerResources.getString("itracker.web.attr.admin", ITrackerResources.getLocale());


        // When a language items is removed, the default is loaded from properties.
        Assert.assertEquals("Admin", value_2);
    }


    @Test
    public void testGetEditBundleNullLocale() {
        //TODO: set languageDAO of ConfigurationService
        ResourceBundle resourceBundle = ITrackerResources.getEditBundle(null);
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getLocale(), resourceBundle.getLocale());
        Enumeration<String> keys = resourceBundle.getKeys(); // keys of copy bundle
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Assert.assertEquals(ITrackerResources.getString(key), resourceBundle.getString(key));
        }
        keys = ITrackerResources.getBundle().getKeys(); // keys of original bundle
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Assert.assertEquals(ITrackerResources.getString(key), resourceBundle.getString(key));
        }
    }
    @Override
    protected String[] getDataSetFiles() {
        return new String[0];
    }

}
