package org.itracker.core.resources;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.itracker.AbstractDependencyInjectionTest;
import org.junit.Test;

public class ITrackerResourcesTest extends AbstractDependencyInjectionTest {
	private String defaultLocaleString_;
	private Locale defaultLocale_;
	private ResourceBundle defaultResourceBundle_;
	private String testLocaleString_ = "in_ID";
	private Locale testLocale_;
	private ITrackerResourceBundle testResourceBundle_;

	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();
		defaultLocaleString_ = ITrackerResources.getDefaultLocale();
		assertNotNull(defaultLocaleString_);
		if (defaultLocaleString_.length() == 2) {
			defaultLocale_ = new Locale(defaultLocaleString_);
		} else if (defaultLocaleString_.length() == 5) {
			defaultLocale_ = new Locale(defaultLocaleString_.substring(0, 2), defaultLocaleString_.substring(3, 5));
		} else {
			fail();
		}
		assertNotNull(defaultLocale_);
		defaultResourceBundle_ = ITrackerResources.getBundle();

		Object[][] data = {{"itracker.web.attr.admin", "itracker.web.attr.administer", "itracker.web.attr.adminTask", "itracker.web.attr.longString", "itracker.web.attr.newLine"},
				{"Admin", "Administer", "The {0} {1} {2}", "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567", "abc\nde"}};

		testLocale_ = new Locale(testLocaleString_);
		testResourceBundle_ = new ITrackerResourceBundle(testLocale_, data);
		assertNotNull(testResourceBundle_);
		ITrackerResources.putBundle(testLocale_, testResourceBundle_);
	}

	@Override
	public void onTearDown() throws Exception {
		super.onTearDown();
		ITrackerResources.setDefaultLocale(defaultLocaleString_);
	}

	@Test
	public void testGetLocaleNull() {
		Locale locale = ITrackerResources.getLocale(null);
		assertNotNull(locale);
		assertEquals(defaultLocale_, locale);
	}

	@Test
	public void testGetLocaleInvalidString() {
		Locale localeInvalid = ITrackerResources.getLocale("ABCDEFG");
		Locale localeDefault = ITrackerResources.getLocale(ITrackerResources.getDefaultLocale());
		assertEquals(localeInvalid, localeDefault);
		ITrackerResources.setDefaultLocale("ABCDEFG");
		localeInvalid  = ITrackerResources.getLocale("ABCDEFG");
		localeDefault = ITrackerResources.getLocale(ITrackerResources.DEFAULT_LOCALE);
		assertEquals(localeInvalid, localeDefault);
	}

	@Test
	public void testGetBundleEmptyString() {
		ResourceBundle resourceBundle = ITrackerResources.getBundle("");
		assertNotNull(resourceBundle);
		assertEquals(defaultResourceBundle_, resourceBundle);
	}

	@Test
	public void testGetBundleStringParameter() {
		ResourceBundle resourceBundle = ITrackerResources.getBundle(defaultLocaleString_);
		assertNotNull(resourceBundle);
		assertEquals(defaultResourceBundle_, resourceBundle);
	}

	@Test
	public void testGetBundleNullLocale() {
		ResourceBundle resourceBundle = ITrackerResources.getBundle((Locale) null);
		assertNotNull(resourceBundle);
		assertEquals(defaultResourceBundle_, resourceBundle);
	}

	@Test
	public void testGetEditBundleNullLocale() {
		ResourceBundle resourceBundle = ITrackerResources.getEditBundle(null);
		assertNotNull(resourceBundle);
		assertEquals(defaultResourceBundle_.getLocale(), resourceBundle.getLocale());
		Enumeration<String> keys = resourceBundle.getKeys(); // keys of copy bundle
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			assertEquals(defaultResourceBundle_.getString(key), resourceBundle.getString(key));
		}
		keys = defaultResourceBundle_.getKeys(); // keys of original bundle
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			assertEquals(defaultResourceBundle_.getString(key), resourceBundle.getString(key));
		}
	}

	@Test
	public void testClearBundles() {
		Enumeration<String> keys = ITrackerResources.getBundle(testLocale_).getKeys();
		int nKeys = 0;
		while (keys.hasMoreElements()) {
			keys.nextElement();
			++nKeys;
		}
		assertTrue(5 < nKeys);

		ITrackerResources.clearBundles();

		keys = ITrackerResources.getBundle(testLocale_).getKeys();
		nKeys = 0;
		while (keys.hasMoreElements()) {
			keys.nextElement();
			++nKeys;
		}

        // When a bundle is cleared and accessed again, it will default to the the base
        // language items.

		assertTrue(5 < nKeys);
	}

	@Test
	public void testClearKeyFromBundles() {
		ResourceBundle resourceBundle = ITrackerResources.getBundle(testLocale_);
		String value = resourceBundle.getString("itracker.web.attr.admin");
		assertNotNull(value);
		assertEquals("Admin", value);

		ITrackerResources.clearKeyFromBundles("itracker.web.attr.admin", false);

		resourceBundle = ITrackerResources.getBundle(testLocale_);
		try {
			resourceBundle.getString("itracker.web.attr.admin");
			
		} catch (RuntimeException exception) {
			fail("throwed " + exception.getClass() + ": " + exception.getMessage());
		}
	}

	@Test
	public void testGetStringNullKey() {
		assertEquals("", ITrackerResources.getString(null, testLocaleString_));
	}

	@Test
	public void testGetStringDefaultLocaleString() {
		ITrackerResources.setDefaultLocale(testLocaleString_);
		assertEquals("Admin", ITrackerResources.getString("itracker.web.attr.admin", (String) null));
	}

	@Test
	public void testGetStringWithLocaleString() {
		assertEquals("Admin", ITrackerResources.getString("itracker.web.attr.admin", testLocaleString_));
	}

	@Test
	public void testGetStringWithLocaleWithNullKey() {
		assertEquals("", ITrackerResources.getString(null, testLocale_));
	}

	@Test
	public void testGetStringWithLocaleWithDirtyKey() {
		testResourceBundle_.removeValue("itracker.web.attr.admin", true);
		String value = ITrackerResources.getString("itracker.web.attr.admin", testLocale_);

        // When a language items is removed, the default is loaded from properties.
        assertEquals("Admin", value);
	}

	@Test
	public void testGetStringWithLocaleWithRemovedKey() {
		testResourceBundle_.removeValue("itracker.web.attr.admin", false);
		String value = ITrackerResources.getString("itracker.web.attr.admin", testLocale_);
        // When a language items is removed, the default is loaded from properties.
		assertEquals("Admin", value);
	}

	@Test
	public void testGetStringWithWrongLocale() {
		Locale locale = new Locale("EEEEEEEEEEe");
		String value = ITrackerResources.getString("itracker.web.attr.admin", locale);

        // When a language items is removed, the default is loaded from properties.
        assertEquals("Admin", value);
	}

	@Test
	public void testGetStringWithMultipleOptions() {
		Object[] options = {"administrator", "administer", "the library"};
		String value = ITrackerResources.getString("itracker.web.attr.adminTask", testLocaleString_, options);
		assertEquals("The administrator administer the library", value);
	}

	@Test
	public void testGetStringWithSingleOption() {
		String value = ITrackerResources.getString("itracker.web.attr.adminTask", testLocaleString_, "administrator");
		assertEquals("The administrator  ", value);
	}

	@Test
	public void testGetStringWithLocaleWithSingleOption() {
		String value = ITrackerResources.getString("itracker.web.attr.adminTask", testLocale_, "administrator");
		assertEquals("The administrator  ", value);
	}

	@Test
	public void testGetCheckForKey() {
		ITrackerResources.setDefaultLocale(testLocaleString_);
		String value = ITrackerResources.getCheckForKey("itracker.web.attr.admin");
		assertEquals("Admin", value);
	}

	@Test
	public void testGetCheckForKeyDirty() {
		ITrackerResources.setDefaultLocale(testLocaleString_);
		testResourceBundle_.removeValue("itracker.web.attr.admin", true);
		String value = ITrackerResources.getCheckForKey("itracker.web.attr.admin");

        // When a language items is removed, the default is reloaded by the configuration service.
        assertEquals("Admin", value);
	}

	@Test
	public void testGetCheckForKeyWithWrongBundle() {
		ITrackerResources.setDefaultLocale("AAAAAA");
		try {
			ITrackerResources.getCheckForKey("itracker.web.attr.admin");
			
		} catch (RuntimeException exception) {

			fail("throwed " + exception.getClass() + ": " + exception.getMessage());
		}
	}

	@Test
	public void testIsLongStringFalse() {
		ITrackerResources.setDefaultLocale(testLocaleString_);
		assertFalse(ITrackerResources.isLongString("itracker.web.attr.admin"));
	}

	@Test
	public void testIsLongStringTrueLong() {
		ITrackerResources.setDefaultLocale(testLocaleString_);
		assertTrue(ITrackerResources.isLongString("itracker.web.attr.longString"));
	}

	@Test
	public void testIsLongStringTrueNewLine() {
		ITrackerResources.setDefaultLocale(testLocaleString_);
		assertTrue(ITrackerResources.isLongString("itracker.web.attr.newLine"));
	}

	@Test
	public void testInitialized() {
		ITrackerResources.setInitialized(false);
		assertFalse(ITrackerResources.isInitialized());
		ITrackerResources.setInitialized(true);
		assertTrue(ITrackerResources.isInitialized());
	}

	@Test
	public void testHex() {
		assertEquals('0', ITrackerResources.encodeHex(0));
		assertEquals(0, ITrackerResources.decodeHex('0'));
		assertEquals('1', ITrackerResources.encodeHex(1));
		assertEquals(1, ITrackerResources.decodeHex('1'));
		assertEquals('2', ITrackerResources.encodeHex(2));
		assertEquals(2, ITrackerResources.decodeHex('2'));
		assertEquals('3', ITrackerResources.encodeHex(3));
		assertEquals(3, ITrackerResources.decodeHex('3'));
		assertEquals('4', ITrackerResources.encodeHex(4));
		assertEquals(4, ITrackerResources.decodeHex('4'));
		assertEquals('5', ITrackerResources.encodeHex(5));
		assertEquals(5, ITrackerResources.decodeHex('5'));
		assertEquals('6', ITrackerResources.encodeHex(6));
		assertEquals(6, ITrackerResources.decodeHex('6'));
		assertEquals('7', ITrackerResources.encodeHex(7));
		assertEquals(7, ITrackerResources.decodeHex('7'));
		assertEquals('8', ITrackerResources.encodeHex(8));
		assertEquals(8, ITrackerResources.decodeHex('8'));
		assertEquals('9', ITrackerResources.encodeHex(9));
		assertEquals(9, ITrackerResources.decodeHex('9'));
		assertEquals('A', ITrackerResources.encodeHex(10));
		assertEquals(10, ITrackerResources.decodeHex('A'));
		assertEquals(10, ITrackerResources.decodeHex('a'));
		assertEquals('B', ITrackerResources.encodeHex(11));
		assertEquals(11, ITrackerResources.decodeHex('B'));
		assertEquals(11, ITrackerResources.decodeHex('b'));
		assertEquals('C', ITrackerResources.encodeHex(12));
		assertEquals(12, ITrackerResources.decodeHex('C'));
		assertEquals(12, ITrackerResources.decodeHex('c'));
		assertEquals('D', ITrackerResources.encodeHex(13));
		assertEquals(13, ITrackerResources.decodeHex('D'));
		assertEquals(13, ITrackerResources.decodeHex('d'));
		assertEquals('E', ITrackerResources.encodeHex(14));
		assertEquals(14, ITrackerResources.decodeHex('E'));
		assertEquals(14, ITrackerResources.decodeHex('e'));
		assertEquals('F', ITrackerResources.encodeHex(15));
		assertEquals(15, ITrackerResources.decodeHex('F'));
		assertEquals(15, ITrackerResources.decodeHex('f'));
	}

	@Test
	public void testUnicodeString() {
		String originalString = "itracker unit testing";
		String escapedString = ITrackerResources.escapeUnicodeString(originalString, true);
		assertEquals(originalString, ITrackerResources.unescapeUnicodeString(escapedString));
		escapedString = ITrackerResources.escapeUnicodeString(originalString, false);
		assertEquals(originalString, ITrackerResources.unescapeUnicodeString(escapedString));
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

 	  	 
