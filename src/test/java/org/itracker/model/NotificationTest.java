package org.itracker.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NotificationTest {
	private Notification not;
	
	@Test
	public void testSetIssue(){
		try{
			not.setIssue(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetUser(){
		try{
			not.setUser(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	/**
	 * TODO remove method from Notification
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSetNotificationRole(){
		not.setNotificationRole(1);
		assertEquals(1, not.getNotificationRole());
		not.setNotificationRole(10000);
		assertEquals(-1, not.getNotificationRole());
	}
	
	@Test
	public void testToString(){		
		assertNotNull("toString", not.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		not = new Notification();
    }
	
	@After
	public void tearDown() throws Exception {
		not = null;
	}

}
