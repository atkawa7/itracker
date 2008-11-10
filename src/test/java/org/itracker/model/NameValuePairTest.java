package org.itracker.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NameValuePairTest {
	private NameValuePair nvp;
	
	@Test
	public void testSetName(){
		nvp.setName("jerry");
		assertEquals("name#jerry", "jerry", nvp.getName());
		nvp.setName(null);
		assertEquals("name is empty", "", nvp.getName());
	}
	
	@Test
	public void testCompareTo(){
		NameValuePair nvpCopy = new NameValuePair("name", "value");
		assertEquals(0,nvp.compareTo(nvpCopy));
		
		nvpCopy.setName("name1");
		assertEquals(-1,nvp.compareTo(nvpCopy));
		
		nvpCopy.setName(null);
		assertEquals(4,nvp.compareTo(nvpCopy));
		
		//test when NameValuePair is null
		nvpCopy = null;
		try{
			nvp.compareTo(nvpCopy);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testCompareValueTo(){
		NameValuePair nvpCopy = new NameValuePair("name", "value");
		assertEquals(0,nvp.compareValueTo(nvpCopy));
		
		nvpCopy.setValue("value1");
		assertEquals(-1, nvp.compareValueTo(nvpCopy));
		
		nvpCopy.setValue(null);
		assertEquals(0,nvp.compareValueTo(nvpCopy));
		
		//test when NameValuePair is null
		nvpCopy = null;
		try{
			nvp.compareValueTo(nvpCopy);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testEquals(){
		NameValuePair nvpCopy = nvp;
		assertTrue("nvp equals nvpCopy", nvp.equals(nvpCopy));
		
		nvpCopy = new NameValuePair("name", "value"); 
		assertTrue("nvp equals nvpCopy", nvp.equals(nvpCopy));
		
		nvpCopy = new NameValuePair("name1", "value"); 
		assertFalse("nvp not equals nvpCopy", nvp.equals(nvpCopy));
		
		nvpCopy = new NameValuePair("name", "value1"); 
		assertFalse("nvp not equals nvpCopy", nvp.equals(nvpCopy));
						
		assertFalse("nvp not equals nvpCopy", nvp.equals(new User()));
		assertFalse("nvp not equals nvpCopy", nvp.equals(null));
	}
	
	@Test
	public void testHashCode(){
		assertEquals(236823153, nvp.hashCode());
	}
	
	@Test
	public void testToString(){
		assertNotNull("toString", nvp.toString());
	}


	@Before
    public void setUp() throws Exception {		
		nvp = new NameValuePair("name", "value");
    }
	
	@After
	public void tearDown() throws Exception {
		nvp = null;
	}

}
