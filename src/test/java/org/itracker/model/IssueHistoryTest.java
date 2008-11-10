package org.itracker.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueHistoryTest {
	private IssueHistory iss;
	
	@Test
	public void testSetIssue(){
		try{
			iss.setIssue(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetUser(){
		try{
			iss.setUser(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testToString(){		
		assertNotNull("toString", iss.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		iss = new IssueHistory();
    }
	
	@After
	public void tearDown() throws Exception {
		iss = null;
	}

}
