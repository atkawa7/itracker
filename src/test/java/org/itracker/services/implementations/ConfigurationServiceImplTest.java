package org.itracker.services.implementations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.naming.NamingException;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Configuration;
import org.itracker.services.ConfigurationService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class ConfigurationServiceImplTest extends
		AbstractDependencyInjectionTest {
	static {
		System.out.println("setting up JNDI override values");
		setupJndiOverrideValues();
	}

	/**
	 * setting up some initial jndi-bindings to java:comp/env for testing
	 */
	private static final void setupJndiOverrideValues() {
		// initialize naming context
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.bind("java:comp/env/itracker/web_session_timeout", "300");
		try {
			builder.activate();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (NamingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Object to be Tested: configuration-service
	 */
	private ConfigurationService configurationService;
	private Properties configurationProperties;

	@Test
	public void testLookupConfigurationItemById() {
		// defined in configurationbean_dataset.xml
		Configuration config = configurationService.getConfigurationItem(2000);

		assertNotNull("configuration (id:2000)", config);
		assertEquals("id", 2000, config.getId().intValue());
		DateFormat format = new SimpleDateFormat("M-d-y");
		try {
			assertEquals("create date", format.parse("1-1-2008"), config
					.getCreateDate());
			assertEquals("modified date", format.parse("1-1-2008"), config
					.getLastModifiedDate());
		} catch (ParseException e) {
			e.printStackTrace();
			fail("failed to parse date for assertion: " + e.getMessage());
		}
		assertEquals("value", "Test Value", config.getValue());
		assertEquals("version", "Version 1.0", config.getVersion());
		assertEquals("order", 1, config.getOrder());
		assertEquals("type", 1, config.getType());

	}

	@Test
	public void testFailGetConfigurationItemById() {

		// not defined id
		Configuration config = configurationService
				.getConfigurationItem(999999);

		assertNull("non existing configuration item", config);

	}

	// seems not to work always, needs some debugging
	@Ignore
	public void testGetJndiOverriddenProperty() {

		String val = configurationService.getProperty("web_session_timeout");

		assertEquals("web_session_timeout", "300", val);

	}

	@Test
	public void testProperty() {
		assertEquals("project property", configurationProperties
				.getProperty("default_locale"), configurationService
				.getProperty("default_locale"));

	}

	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();
		configurationService = (ConfigurationService) applicationContext
				.getBean("configurationService");
		configurationProperties = (Properties) applicationContext
				.getBean("configurationProperties");
	}

	protected String[] getDataSetFiles() {
		return new String[] { "dataset/configurationbean_dataset.xml" };
	}

	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}
