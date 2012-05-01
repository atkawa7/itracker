package org.itracker.services.exceptions;

import org.junit.Test;

import junit.framework.TestCase;

public class ImportExportExceptionTest extends TestCase {

	@Test
	public void testConstructor() {
		ImportExportException e = new ImportExportException();
		assertTrue(e instanceof Exception);
		
		e = new ImportExportException("my_message");
		assertEquals("e.message", "my_message", e.getMessage());
		
		e = new ImportExportException("my_message", 1);
		assertEquals("e.message", "my_message", e.getMessage());
		assertEquals("e.type", 1, e.getType());
	}
	
	@Test
	public void testSetType() {
		ImportExportException e = new ImportExportException();
		e.setType(1);
		assertEquals("e.type", 1, e.getType());
	}
	
}
