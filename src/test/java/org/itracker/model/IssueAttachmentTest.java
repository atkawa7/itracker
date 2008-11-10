package org.itracker.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueAttachmentTest {
	private IssueAttachment iss;
	
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
	public void testSetOriginalFileName(){
		try{
			iss.setOriginalFileName(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetFileData(){
		try{
			iss.setFileData(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testgetFileData(){
		try{
			iss.setFileData(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		assertNull("file data is null", iss.getFileData());		
	}
	
	@Test
	public void testSetType(){
		try{
			iss.setType(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testToString(){		
		assertNotNull("toString",iss.toString());

	}
	
	
	@Before
    public void setUp() throws Exception {
		iss = new IssueAttachment();
    }
	
	@After
	public void tearDown() throws Exception {
		iss = null;
	}

}
