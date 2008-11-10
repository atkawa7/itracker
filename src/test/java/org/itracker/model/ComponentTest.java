package org.itracker.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ComponentTest {
	private Component com;
	
	@Test
	public void testSetProject(){		
		try{
			com.setProject(null);
			fail("did not throw IllegalArgumentException");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetName(){		
		try{
			com.setName(null);
			fail("did not throw IllegalArgumentException");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetStatus(){		
		try{
			com.setStatus(null);
			fail("did not throw IllegalArgumentException");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testToString(){		
		assertNotNull(com.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		com = new Component();
    }
	
	@After
	public void tearDown() throws Exception {
		com = null;
	}

}
