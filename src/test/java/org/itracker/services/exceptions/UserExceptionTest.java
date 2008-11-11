package org.itracker.services.exceptions;

import org.junit.Test;

import junit.framework.TestCase;

public class UserExceptionTest extends TestCase {

	@Test
	public void testConstructor() {
		UserException e = new UserException();
		assertTrue(e instanceof Exception);
		
		e = new UserException("my_message");
		assertEquals("e.message", "my_message", e.getMessage());
		
		Throwable cause = new Throwable();
		e = new UserException("my_message", cause);
		assertEquals("e.message", "my_message", e.getMessage());
		assertSame("e.cause", cause, e.getCause());
	}
	
}