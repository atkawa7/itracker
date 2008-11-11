package org.itracker.model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		Locale en = new Locale("en");
		CustomField cust = new CustomField();
		cust.setFieldType(CustomField.Type.INTEGER);
		iss.setCustomField(cust);
		iss.setIntValue(23);		
		assertEquals("int value 23", "23", iss.getValue(en));
		
		cust.setFieldType(CustomField.Type.DATE);
		Date date = new Date(10000);
		iss.setDateValue(date);
		assertEquals("date value","01/01/1970", iss.getValue(en));
		
		cust.setRequired(false);
		iss.setDateValue(null);
		assertNull("date value is null", iss.getValue(en));
		
		cust.setRequired(true);
		iss.setDateValue(null);
		assertNotNull("date value is not null", iss.getValue(en));

	}
	
	@Test
	public void testGetStringValue(){
		Locale en = new Locale("en");
		CustomField cust = new CustomField();
		iss.setCustomField(cust);
		iss.setStringValue(null);
		cust.setFieldType(CustomField.Type.STRING);
		assertEquals("", iss.getValue(en));
		
		cust.setFieldType(CustomField.Type.STRING);
		iss.setCustomField(cust);
		iss.setStringValue("value");		
		assertEquals("value", iss.getValue(en));
	}
	
	@Test
	public void testSetValue() {
		//test type is integer
		Locale en = new Locale("en");
		CustomField cust = new CustomField();
		cust.setFieldType(CustomField.Type.INTEGER);
		iss.setCustomField(cust);
		try {
			iss.setValue("23",ITrackerResources.getBundle(en));
		} catch (IssueException e) {
			fail("throw IssueException" + e);
		}
		assertTrue("23".equals(iss.getValue(en)));
		
		//test wrong number
		try {
			iss.setValue("ww",ITrackerResources.getBundle(en));
		} catch (IssueException e) {
			assertTrue(true);
		}
		
		//test type is date
		cust.setFieldType(CustomField.Type.DATE);		
		try {
			iss.setValue("01/01/1970",ITrackerResources.getBundle(en));
		} catch (IssueException e) {
			fail("throw IssueException" + e);
		}		
		SimpleDateFormat sdf = CustomField.DEFAULT_DATE_FORMAT;
		try {
			assertEquals("date value",sdf.parseObject("01/01/1970"), iss.getDateValue());
		} catch (ParseException e) {
			fail("throw ParseException" + e);
		}
		//test wrong date
		try {
			iss.setValue("xxxx01/01/1970",ITrackerResources.getBundle(en));
		} catch (Exception e) {
			assertTrue(true);
		}
		
		
		//test value is null
		try {
			iss.setValue(null,ITrackerResources.getBundle(en));
			assertEquals("", iss.getStringValue());
			assertNull(iss.getDateValue());
			assertEquals(0, iss.getIntValue());
		} catch (IssueException e) {
			fail("throw IssueException" + e);
		}
		
		//test value is empty
		try {
			iss.setValue(null,ITrackerResources.getBundle(en));
			assertEquals("", iss.getStringValue());
			assertNull(iss.getDateValue());
			assertEquals(0, iss.getIntValue());
		} catch (IssueException e) {
			fail("throw IssueException" + e);
		}
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
