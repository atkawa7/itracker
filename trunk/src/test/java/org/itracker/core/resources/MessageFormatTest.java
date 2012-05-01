package org.itracker.core.resources;

import org.itracker.AbstractDependencyInjectionTest;
import org.junit.Test;

public class MessageFormatTest extends AbstractDependencyInjectionTest {
	@Test
	public void testFormat() {
		Object[] options = {"administrator", "administer", "the network"};
		String message = MessageFormat.format("The {0} {1} {2}", options);
		assertEquals("The administrator administer the network", message);
	}
	
	@Test
	public void testWrongOptions() {
		Object[] options = {};
		String message = MessageFormat.format("The {0} {1} {2}", options);
		assertEquals("The   ", message);		
	}
	
	@Test
	public void testNumberFormat() {
		Integer a  = new Integer(1);
		Integer b = new Integer(2);
		Integer c = a + b;
		Object[] options = {a, b, c};
		String message = MessageFormat.format("{0,number} + {1,number} equals {2,number}", options);
		assertEquals(a + " + " + b +" equals " + c, message);
	}
	
	@Test
	public void testDecimalFormat() {
		Double a  = new Double(1);
		Double b = new Double(2);
		Double c = a - b;
		Object[] options = {a, b, c};
		String message = MessageFormat.format("{0,number,#.##} - {1,number,#.##} equals {2,number,#.##;(#.##)}", options);
		assertEquals("1 - 2 equals (1)", message);
	}
	
	@Test
	public void testWrongDecimalFormat() {
		Double a  = new Double(((double) 1) / 3);
		Object[] options = {a};
		String message = MessageFormat.format("{0,number,\\.\\.\\.}", options);
		assertEquals(a.toString(), message);
	}
	
	@Override
	protected String[] getDataSetFiles() {
        return new String[]{};
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"application-context.xml"};
	}
}

 	  	 
