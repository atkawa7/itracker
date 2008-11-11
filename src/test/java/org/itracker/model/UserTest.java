package org.itracker.model;
import static org.junit.Assert.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
	private User user;
	
	@Test
	public void testSetLogin(){
		try{
			user.setLogin(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetEmailAddress() {
		user.setEmail("jerrycong@hotmail.com");
		user.setFirstName("jerry");
		user.setLastName("jun");		
		try{
			InternetAddress addr = new InternetAddress(user.getEmail(),user.getFirstName() + " "
						+ user.getLastName());
			assertEquals("InternetAddress ", addr, user.getEmailAddress());
		} catch (Exception e) {
			try {
				assertEquals("InternetAddress", new InternetAddress(user.getEmail()), user.getEmailAddress());
			} catch (AddressException e1) {
				assertNull("InternetAddress is null", user.getEmailAddress());
			}
		}
		
		//test email is null
		user.setEmail(null);
		assertNull("InternetAddress is null", user.getEmailAddress());
		
		//test email is empty
		user.setEmail("");
		assertNull("InternetAddress is null", user.getEmailAddress());
	}	
	
	@Test
	public void testGetFirstInitial(){
		user.setFirstName("jerry");
		assertEquals("first initail", "J.", user.getFirstInitial());
		
		//set first name empty
		user.setFirstName("");
		assertEquals("first initail", "", user.getFirstInitial());
		
		//set first name null
		try{
			user.setFirstName(null);
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		assertEquals("first initail", "", user.getFirstInitial());
	}
	
	@Test
	public void testHasRequiredData(){
		assertFalse(user.hasRequiredData(false));
		try{
			user.setLogin(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		assertFalse(user.hasRequiredData(false));
		assertFalse(user.hasRequiredData(true));
		
		user = new User();
		user.setLogin("");
		assertFalse(user.hasRequiredData(false));
		assertFalse(user.hasRequiredData(true));
		
		user = new User();
		user.setFirstName(null);
		assertFalse(user.hasRequiredData(false));
		user.setFirstName("");
		assertFalse(user.hasRequiredData(false));
		
		user = new User();
		user.setLastName(null);
		assertFalse(user.hasRequiredData(false));
		user.setLastName("");
		assertFalse(user.hasRequiredData(false));
		
		user = new User();
		user.setEmail(null);
		assertFalse(user.hasRequiredData(false));
		user.setEmail("");
		assertFalse(user.hasRequiredData(false));
	}
	
	@Test
	public void testToString(){		
		assertNotNull("toString", user.toString());
	}
	
	@Before
    public void setUp() throws Exception {
		user = new User();
    }
	
	@After
	public void tearDown() throws Exception {
		user = null;
	}

}
