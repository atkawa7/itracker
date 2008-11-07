package org.itracker.services.exceptions;

import org.junit.Test;

import junit.framework.TestCase;

public class ProjectExceptionTest extends TestCase {
	
	@Test
	public void testConstructor() {
		ProjectException e = new ProjectException();
		assertTrue(e instanceof Exception);
		
		e = new ProjectException("my_message");
		assertEquals("e.message", "my_message", e.getMessage());
	}
	
}
