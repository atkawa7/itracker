package org.itracker.model;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class AbstractEntityTest extends TestCase {
	private AbstractEntity ae;

	@Test
	public void testGetCreateDate() {
		Date date = new Date(1000);
		ae.setCreateDate(date);
		assertEquals("create date", date, ae.getCreateDate());

		ae.setCreateDate(null);
		assertNotNull("create date", ae.getCreateDate());
	}

	@Test
	public void testSetCreateDate() {
		Date date = new Date(1000);
		ae.setCreateDate(date);
		assertEquals("create date", date, ae.getCreateDate());

		ae.setCreateDate(null);
		assertNotNull("create date", ae.getCreateDate());
	}

	@Test
	public void testGetLastModifiedDate() {
		Date date = new Date(1000);
		ae.setLastModifiedDate(date);
		assertEquals("LastModifiedDate", date, ae.getLastModifiedDate());

		ae.setLastModifiedDate(null);
		assertNotNull("LastModifiedDate", ae.getLastModifiedDate());
	}

	@Test
	public void testSetLastModifiedDate() {
		Date date = new Date(1000);
		ae.setLastModifiedDate(date);
		assertEquals("LastModifiedDate", date, ae.getLastModifiedDate());

		ae.setLastModifiedDate(null);
		assertNotNull("LastModifiedDate", ae.getLastModifiedDate());
	}

	@Test
	public void testEquals() {
		AbstractEntity aeCopy = ae;
		assertTrue(ae.equals(aeCopy));
		assertFalse(ae.equals(null));

		aeCopy = new TestAbstractEntity();
		assertFalse(ae.equals(aeCopy));
		ae.setId(1000);
		assertFalse(ae.equals(aeCopy));
		aeCopy.setId(1000);
		assertTrue(ae.equals(aeCopy));
		assertFalse(ae.equals(new User()));
	}

	@Test
	public void testCompareTo() {
		ae.setId(1000);
		AbstractEntity aeCopy = new TestAbstractEntity();
		aeCopy.setId(1000);
		assertEquals(0, ae.compareTo(aeCopy));
		aeCopy.setId(2000);
		assertEquals(-1, ae.compareTo(aeCopy));
		try {
			ae.compareTo(null);
			fail("did not throw NullPointerException");
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testIsNew() {
		ae.setId(1000);
		assertFalse(ae.isNew());
		ae.setId(null);
		assertTrue(ae.isNew());
	}

	@Test
	public void testHashCode() {
		assertNotNull(ae.hashCode());
	}

	@Test
	public void testClone() throws CloneNotSupportedException {
		Object clone = ae.clone();
		assertTrue(clone instanceof AbstractEntity);
	}

	@Before
	protected void setUp() throws Exception {
		ae = new TestAbstractEntity();
	}

	@After
	protected void tearDown() throws Exception {
		ae = null;
	}
	
	private class TestAbstractEntity extends AbstractEntity {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

}
