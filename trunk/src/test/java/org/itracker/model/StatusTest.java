package org.itracker.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StatusTest {
	
	@Test
	public void testValueOf(){
		try{
			Status.valueOf(0);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		
		assertEquals("code -1",Status.DELETED, Status.valueOf(-1));
		assertEquals("code 1",Status.ACTIVE,Status.valueOf(1));
		assertEquals("code 2",Status.VIEWABLE,Status.valueOf(2));
		assertEquals("code 3",Status.LOCKED,Status.valueOf(3));
	}
	
	
	
	@Before
    public void setUp() throws Exception {
    }
	
	@After
	public void tearDown() throws Exception {
	}

}
