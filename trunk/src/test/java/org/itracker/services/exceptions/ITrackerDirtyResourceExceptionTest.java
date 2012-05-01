package org.itracker.services.exceptions;

import org.junit.Test;

import junit.framework.TestCase;

public class ITrackerDirtyResourceExceptionTest extends TestCase {
	
	@Test
	public void testConstructor() {
		
		ITrackerDirtyResourceException e = 
			new ITrackerDirtyResourceException("my_message", "my_class", "my_key");
		
		assertTrue(e instanceof Exception);
		
		assertEquals("e.message", "my_message", e.getMessage());
		assertEquals("e.className", "my_class", e.getClassName());
		assertEquals("e.key", "my_key", e.getKey());
		
	}
	
	
}
