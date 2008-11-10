package org.itracker.core.resources;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
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
	public void testITrackerResourceBundleDefault() {
		assertNotNull(new ITrackerResourceBundle());
	}
	
	@Test
	public void testGetContents() {
		Object[][] contents = resourceBundle.getContents();
		assertNotNull(contents);
		assertEquals(2, contents.length);
		for (int i = 0; i < contents.length; ++i) {
			assertEquals(2, contents[i].length);
		}
		assertEquals("itracker.web.attr.admin", contents[0][0]);
		assertEquals("itracker.web.attr.administer", contents[0][1]);
		assertEquals("Admin", contents[1][0]);
		assertEquals("Administer", contents[1][1]);		
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
	public void testDirty() {
		assertFalse(resourceBundle.isDirty("itracker.web.attr.admin"));
		resourceBundle.removeValue("itracker.web.attr.admin", true);
		assertTrue(resourceBundle.isDirty("itracker.web.attr.admin"));
		resourceBundle.removeValue("itracker.web.attr.admin", false);
		try {
			resourceBundle.getString("itracker.web.attr.admin");
			fail("Should throw MissingResourceException");
		} catch (MissingResourceException exception) {
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
		assertEquals(keySet, resultKeySet);
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

 	  	 
