package org.itracker.core.resources;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Language;
import org.junit.Test;

public class ITrackerResourceBundleTest extends AbstractDependencyInjectionTest {
	private ITrackerResourceBundle resourceBundle;
	
	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();
		Object[][] data = {{"itracker.web.attr.admin", "itracker.web.attr.administer"}, {"Admin", "Administer"}};
		resourceBundle = new ITrackerResourceBundle(Locale.ENGLISH, data);
		assertNotNull(resourceBundle);
	}
	
	
	@Test
	public void testValue() {
		resourceBundle.updateValue("itracker.web.attr.admin", "root");
		assertEquals("root", resourceBundle.getString("itracker.web.attr.admin"));
		Language language = new Language();
		language.setLocale("en");
		language.setResourceKey("itracker.web.attr.administer");
		language.setResourceValue("administer");
		resourceBundle.updateValue(language);
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
		try {
			assertNotNull(resourceBundle.getString("itracker.web.attr.admin"));
			assertEquals("itracker.web.attr.admin", ResourceBundle.getBundle(ITrackerResources.RESOURCE_BUNDLE_NAME, resourceBundle.getLocale()).getString("itracker.web.attr.admin"), 
					resourceBundle.getString("itracker.web.attr.admin"));
//			fail("Should throw MissingResourceException");
		} catch (RuntimeException exception) {
			fail("should fall back to properties resource, but throwed " + exception.getClass() + ", " + exception.getMessage());
		}		
	}
	
	@Test
	public void testGetKeys() {			
		Enumeration<String> keys = resourceBundle.getKeys();
		assertNotNull(keys);
		Set<String> keySet = new HashSet<String>();
		keySet.add("itracker.web.attr.admin");
		keySet.add("itracker.web.attr.administer");
		
		Set<String> resultKeySet = new HashSet<String>();
		while (keys.hasMoreElements()) {
			resultKeySet.add(keys.nextElement());
		}
		assertTrue("keys are contained", resultKeySet.containsAll(keySet));
	}
	
	@Override
	protected String[] getDataSetFiles() {
        return new String[]{};
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"application-context.xml"};
	}
}

 	  	 
