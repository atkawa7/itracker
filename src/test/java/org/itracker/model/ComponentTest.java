package org.itracker.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.itracker.Assert.*;

public class ComponentTest {
	private Component component;

	@Test
	public void testSetProject() {
		try {
			component.setProject(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetName() {
		try {
			component.setName(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetStatus() {
		try {
			component.setStatus(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testToString() {
		assertNotNull(component.toString());
	}

	@Test
	public void testNameComparator() throws Exception {
		Component componentA, componentB;

		componentA = new Component();
		componentB = new Component();

		componentA.setName("a");
		componentB.setName("b");

		assertEntityComparator("name comparator", Component.NAME_COMPARATOR,
				componentA, componentB);
		assertEntityComparator("name comparator", Component.NAME_COMPARATOR,
				componentA, null);

		componentA.setName(componentB.getName());
		assertEntityComparatorEquals("name comparator",
				Component.NAME_COMPARATOR, componentA, componentB);
		assertEntityComparatorEquals("name comparator",
				Component.NAME_COMPARATOR, componentA, componentA);

	}

	@Before
	public void setUp() throws Exception {
		component = new Component();
	}

	@After
	public void tearDown() throws Exception {
		component = null;
	}

}
