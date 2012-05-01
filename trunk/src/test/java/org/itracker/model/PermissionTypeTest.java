package org.itracker.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PermissionTypeTest {
	
	@Test
	public void testFromCode(){
		try{
			PermissionType.fromCode(0);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		try{
			PermissionType.fromCode(-2);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		try{
			PermissionType.fromCode(14);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
		assertEquals("code 1",PermissionType.PRODUCT_ADMIN, PermissionType.fromCode(1));
		assertEquals("code 5",PermissionType.ISSUE_ASSIGN_SELF,PermissionType.fromCode(5));
		assertEquals("code -1",PermissionType.USER_ADMIN,PermissionType.fromCode(-1));
	}
	
	
	
	@Before
    public void setUp() throws Exception {
    }
	
	@After
	public void tearDown() throws Exception {
	}

}
