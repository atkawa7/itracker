package org.itracker.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PermissionTest {
	private Permission per;
	
	@Test
	public void testSetUser(){
		try{
			per.setUser(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testToString(){		
		assertNotNull("toString", per.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		per = new Permission();
    }
	
	@After
	public void tearDown() throws Exception {
		per = null;
	}

}
