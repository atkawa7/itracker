package org.itracker.model;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// TODO makit unit-test?
public class CustomFieldIT extends AbstractDependencyInjectionTest{
	private CustomField cust;
	
	@Test
	public void testAddOption(){	
		assertNotNull("options",cust.getOptions());
		assertEquals("options size",0,cust.getOptions().size());
		cust.addOption("0", "male");
		assertEquals("options size",1,cust.getOptions().size());
	}
	
//	@Test
//	public void testGetOptionNameByValue(){	
//		assertEquals("",cust.getOptionNameByValue("0"));
////		cust.setName("male");
////		cust.addOption("0", "male");
//		assertEquals("get name by value 0",null,cust.getOptionNameByValue("0"));
//	}
	
//	@Test
//	public void testSetLabels(){	
//		cust.setId(4444);
//		cust.setLabels("en");
//		assertEquals("hi", cust.getName());
//	}
	
	@Test
	public void testCheckAssignable() throws IssueException{	
		Locale en = new Locale("en");
		cust.setFieldType(CustomField.Type.INTEGER);
		try{
			cust.checkAssignable("23", en, ITrackerResources.getBundle(en));
			cust.checkAssignable("ww", en, ITrackerResources.getBundle(en));
		} catch (IssueException e){
			assertTrue(true);
		}
		//test type is date
		cust.setFieldType(CustomField.Type.DATE);
		cust.setDateFormat(CustomField.DateFormat.DATE.code);	
		SimpleDateFormat sdf =
			new SimpleDateFormat(ITrackerResources.getBundle(en)
					.getString("itracker.dateformat."
							+ cust.getDateFormat()), en);
		final Date date = new Date(100000);
		final String dateString = sdf.format(date);
		
		try{
			cust.checkAssignable(dateString, en, ITrackerResources.getBundle(en));
			cust.checkAssignable("abdcd" + dateString, en, ITrackerResources.getBundle(en));
		} catch (IssueException e){
			assertTrue(true);
		}
	}
	
	
	@Test	
	public void testToString(){		
		assertNotNull("toString",cust.toString());		
	}
	
	
	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();
		cust = new CustomField();
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
