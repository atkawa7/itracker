package org.itracker.core.resources;

import org.itracker.model.Language;
import org.itracker.services.AbstractServicesIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.itracker.Assert.*;

public class ItrackerResourceBundleIT extends AbstractServicesIntegrationTest {
    private ITrackerResourceBundle resourceBundle;

    @Before
    public void setup() {
        resourceBundle = (ITrackerResourceBundle)ITrackerResourceBundle.getBundle(Locale.ENGLISH);
        assertNotNull(resourceBundle);

       resourceBundle.removeValue("itracker.web.attr.admin", false);
    }


    @Test
    public void testValue() {
        resourceBundle.updateValue("itracker.web.attr.admin", "root");
        assertEquals("root", resourceBundle.getString("itracker.web.attr.admin"));
        Language language = new Language();
        language.setLocale("en");
        language.setResourceKey("itracker.web.attr.administer");
        language.setResourceValue("administer");
        resourceBundle.updateValue(language.getResourceKey(), language.getResourceValue());
        assertEquals("administer", resourceBundle.getString("itracker.web.attr.administer"));
    }

    @Test
    public void testGetLocale() {
        assertEquals("Locale.ENGLISH", Locale.ENGLISH, resourceBundle.getLocale());
    }

    @Test
    public void testDirty() {
        assertFalse(resourceBundle.isDirty("itracker.web.attr.admin"));
        resourceBundle.removeValue("itracker.web.attr.admin", true);
        assertTrue(resourceBundle.isDirty("itracker.web.attr.admin"));
        resourceBundle.removeValue("itracker.web.attr.admin", false);
        assertFalse(resourceBundle.isDirty("itracker.web.attr.admin"));

        assertNotNull(resourceBundle.getString("itracker.web.attr.admin"));
        assertEquals("itracker.web.attr.admin", ResourceBundle.getBundle(ITrackerResources.RESOURCE_BUNDLE_NAME, resourceBundle.getLocale()).getString("itracker.web.attr.admin"),
                 resourceBundle.getString("itracker.web.attr.admin"));

    }

    @Test
    public void testGetKeys() {
        Enumeration<String> keys = resourceBundle.getKeys();
        assertNotNull(keys);
        Set<String> keySet = new HashSet<>();
        keySet.add("itracker.web.attr.admin");
        keySet.add("itracker.web.attr.administer");

        Set<String> resultKeySet = new HashSet<>();
        while (keys.hasMoreElements()) {
            resultKeySet.add(keys.nextElement());
        }
        assertTrue("keys are contained", resultKeySet.containsAll(keySet));
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{};
    }

}

 	  	 
