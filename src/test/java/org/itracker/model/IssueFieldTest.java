package org.itracker.model;
import java.util.Date;
import java.util.Locale;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.junit.Test;
public class IssueFieldTest extends AbstractDependencyInjectionTest{
	private IssueField iss;
	
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
	public void testSetCustomField(){
		try{
			iss.setCustomField(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetDateValue(){
		Date date = new Date(100000000);
		iss.setDateValue(date);
		assertEquals("date value",date,iss.getDateValue());
		
		date = null;
		iss.setDateValue(date);
		assertNull("date value is null", iss.getDateValue());
	}
	
	@Test
	public void testSetDateValue(){
		Date date = new Date(100000000);
		iss.setDateValue(date);
		assertEquals("date value",date,iss.getDateValue());
		
		date = null;
		iss.setDateValue(date);
		assertNull("date value is null", iss.getDateValue());
	}
	
	@Test
	public void testGetValue(){
		CustomField cust = new CustomField();
		cust.setFieldType(CustomField.Type.INTEGER);
		iss.setCustomField(cust);
		iss.setIntValue(23);
		assertTrue("23".equals(iss.getValue(new Locale("en"))));
		
		cust.setFieldType(CustomField.Type.DATE);
		Date date = new Date(10000);
		iss.setDateValue(date);
		assertEquals("date value","01/01/1970", iss.getValue(new Locale("en")));
		
		cust.setRequired(false);
		iss.setDateValue(null);
		assertNull("date value is null", iss.getValue(new Locale("en")));
		
		cust.setRequired(true);
		iss.setDateValue(null);
		assertNotNull("date value is not null", iss.getValue(new Locale("en")));

	}
	
	@Test
	public void testGetStringValue(){
		CustomField cust = new CustomField();
		iss.setCustomField(cust);
		iss.setStringValue(null);
		cust.setFieldType(CustomField.Type.STRING);
		assertEquals("", iss.getValue(new Locale("en")));
		
//		cust = new CustomField();
//		cust.setFieldType(null);
		cust.setFieldType(CustomField.Type.STRING);
		iss.setCustomField(cust);
		iss.setStringValue("value");		
		assertEquals("value", iss.getValue(new Locale("en")));
	}
	
	@Test
	public void testSetValue() throws IssueException{
		Locale en = new Locale("en");
		CustomField cust = new CustomField();
		cust.setFieldType(CustomField.Type.INTEGER);
		iss.setCustomField(cust);
		iss.setValue("23",ITrackerResources.getBundle(en));
		assertTrue("23".equals(iss.getValue(en)));
	}
	
	@Test
	//test for method setValue(String value, ResourceBundle bundle)
	public void testSetValueByResourceBundle() throws IssueException{
		Locale en = new Locale("en");
		CustomField cust = new CustomField();
		cust.setFieldType(CustomField.Type.INTEGER);
		iss.setCustomField(cust);
		iss.setValue("23", en,ITrackerResources.getBundle(en));
		assertTrue("23".equals(iss.getValue(en)));
	}
	
	
	@Test
	public void testToString(){		
		assertNotNull("toString",iss.toString());		
	}
	
	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();
		iss = new IssueField();
    }

	@Override
	protected String[] getDataSetFiles() {
		return new String[] { 
				"dataset/languagebean_dataset.xml" 				
		};
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}
