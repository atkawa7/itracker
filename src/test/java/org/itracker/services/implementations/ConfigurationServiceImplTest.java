package org.itracker.services.implementations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Configuration;
import org.itracker.services.ConfigurationService;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class ConfigurationServiceImplTest extends
		AbstractDependencyInjectionTest {

	private static final Logger log = Logger
			.getLogger(ConfigurationServiceImplTest.class);

	public static final class TestInitialContextFactory implements
			InitialContextFactory {

		public TestInitialContextFactory() {

		}

		public Context getInitialContext(Hashtable<?, ?> environment)
				throws NamingException {
			if (null == SimpleNamingContextBuilder.getCurrentContextBuilder()) {
				SimpleNamingContextBuilder.emptyActivatedContextBuilder();
			}
			return SimpleNamingContextBuilder.getCurrentContextBuilder().createInitialContextFactory(environment)
					.getInitialContext(environment);
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
			log
					.error(
							"testLookupConfigurationItemById: failed to parse date for assertion",
							e);
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

	/**
	 * Test if the configurationServiceImpl does override property values by JNDI.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetJndiOverriddenProperty() throws Exception {


		final String web_session_timeout = "web_session_timeout";

		// initialize new initialContextFactoryBuilder
		SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		SimpleNamingContextBuilder.getCurrentContextBuilder().bind("java:comp/env/itracker/web_session_timeout", "300");
		
		Context ctx = new InitialContext();

		// basic assertions
		assertEquals("java:comp/env/itracker/web_session_timeout", "300", ctx.lookup("java:comp/env/itracker/web_session_timeout"));
		String val = configurationProperties.getProperty(web_session_timeout);
		assertEquals("configurationProperties#web_session_timeout", "30", val);
		
		// check getProperty() in configurationService for jndi overridden value
		val = configurationService.getProperty(web_session_timeout);
		assertEquals("configurationService.web_session_timeout", "300", val);

		// check getProperties().get() in configurationService for jndi overridden value
		Object valObj = configurationService.getProperties().get(web_session_timeout);
		assertEquals("configurationService.properties#web_session_timeout", "300", valObj);
		
		// check getProperties().getProperty() in configurationService for jndi overridden value
		val = configurationService.getProperties().getProperty(web_session_timeout);
		assertEquals("configurationService.properties#web_session_timeout", "300", valObj);
		
		

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
