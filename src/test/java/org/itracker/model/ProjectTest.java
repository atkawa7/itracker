package org.itracker.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectTest {
	private Project pro;
	
	@Test
	public void testSetName(){
		try{
			pro.setName(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetStatus(){
		try{
			pro.setStatus(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testToString(){
		assertNotNull("toString", pro.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		pro = new Project();
    }
	
	@After
	public void tearDown() throws Exception {
		pro = null;
	}

}
