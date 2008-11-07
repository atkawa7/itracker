package org.itracker.services.exceptions;

import org.junit.Test;

import junit.framework.TestCase;

public class PasswordExceptionTest extends TestCase {

	@Test
	public void testConstructor() {
		PasswordException e = new PasswordException();
		assertTrue(e instanceof Exception);
		
		e = new PasswordException(1);
		assertEquals("e.type", 1, e.getType());
		
	}
	
	@Test
	public void testSetType() {
		PasswordException e = new PasswordException();
		e.setType(1);
		assertEquals("e.type", 1, e.getType());
	}
}