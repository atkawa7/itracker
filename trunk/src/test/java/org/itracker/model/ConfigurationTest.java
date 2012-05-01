package org.itracker.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.itracker.Assert.*;

public class ConfigurationTest {
	private Configuration conf;

	@Test
	public void testSetValue() {
		try {
			conf.setValue(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetVersion() {
		try {
			conf.setVersion(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testToString() {
		assertNotNull(conf.toString());
	}

	@Test
	public void SortOrderComparator() throws Exception {
		Configuration configurationB = new Configuration();
		configurationB.setOrder(conf.getOrder() + 1);

		assertEntityComparator("sort order",
				Configuration.CONFIGURATION_ORDER_COMPARATOR, conf,
				configurationB);
		assertEntityComparator("sort order",
				Configuration.CONFIGURATION_ORDER_COMPARATOR, conf, null);

		configurationB.setOrder(conf.getOrder());
		assertEntityComparatorEquals("sort order",
				Configuration.CONFIGURATION_ORDER_COMPARATOR, conf,
				configurationB);
	}

	@Before
	public void setUp() throws Exception {
		conf = new Configuration();
	}

	@After
	public void tearDown() throws Exception {
		conf = null;
	}

}
