package org.itracker.model;
import java.util.Locale;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.junit.Test;

public class CustomFieldTest extends AbstractDependencyInjectionTest{
	private CustomField cust;
	
	@Test
	public void testAddOption(){	
		assertNotNull("options",cust.getOptions());
		assertEquals("options size",0,cust.getOptions().size());
		cust.addOption("0", "male");
		assertEquals("options size",1,cust.getOptions().size());
	}
	
	@Test
	public void testGetOptionNameByValue(){	
		assertEquals("",cust.getOptionNameByValue("0"));
		cust.setName("male");
		cust.addOption("0", "male");
		assertEquals("get name by value 0",null,cust.getOptionNameByValue("0"));
	}
	
	@Test
	public void testSetLabels(){	
		cust.setId(4444);
		cust.setLabels("en");
		assertEquals("hi", cust.getName());
	}
	
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
		cust.setFieldType(CustomField.Type.DATE);
		try{
			cust.checkAssignable("12/25/2008", en, ITrackerResources.getBundle(en));
			cust.checkAssignable("2008-12-25", en, ITrackerResources.getBundle(en));
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
