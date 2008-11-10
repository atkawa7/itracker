package org.itracker.model;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomFieldValueTest {
	private CustomFieldValue cust;
	
	@Test
	public void testSetCustomField(){	
		try{
			cust.setCustomField(null);
			fail("did not throw IllegalArgumentException ");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetValue(){		
		try{
			cust.setValue(null);
			fail("did not throw IllegalArgumentException ");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	

	
	@Test
	public void testToString(){		
		assertNotNull(cust.toString());	
	}
	
	
	@Before
    public void setUp() throws Exception {
		cust = new CustomFieldValue();
    }
	
	@After
	public void tearDown() throws Exception {
		cust = null;
	}

}
