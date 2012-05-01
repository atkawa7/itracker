package org.itracker.model;
import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.itracker.services.util.UserUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PermissionTest {
	private Permission per;
	
	@Test
	public void testSetUser(){
		try{
			per.setUser(null);
			fail("did not throw IllegalArgumentException");
		} catch (IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testToString(){		
		assertNotNull("toString", per.toString());
	}
	
	@Test
	public void testPermissionPropertiesComparator() {
		User userA = new User("aaa", "", "a", "a", "a@a.com", false);
		User userB = new User("bbb", "", "b", "b", "b@b.com", false);
		Project projectA = new Project("a");
		Project projectB = new Project("b");
		Permission entityA = new Permission(UserUtilities.PERMISSION_PRODUCT_ADMIN, userA);
		entityA.setProject(projectA);
		Permission entityB = new Permission(UserUtilities.PERMISSION_CREATE, userA);
		entityB.setProject(projectA);

		assertEntityComparator("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityB);
		assertEntityComparator("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, null);
		assertEntityComparatorEquals("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityA);

		entityA.setPermissionType(entityB.getPermissionType());
		entityB.setUser(userB);

		assertEntityComparator("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityB);
		assertEntityComparator("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, null);
		assertEntityComparatorEquals("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityA);
		
		entityB.setUser(userA);
		entityB.setProject(projectB);
		
		assertEntityComparator("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityB);
		assertEntityComparator("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, null);
		assertEntityComparatorEquals("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityA);
		
		entityA.setProject(entityB.getProject());
		
		assertEntityComparatorEquals("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityB);
		assertEntityComparatorEquals("permission comparator", Permission.PERMISSION_PROPERTIES_COMPARATOR, entityA, entityA);
	}
	
	@Before
    public void setUp() throws Exception {
		per = new Permission();
    }
	
	@After
	public void tearDown() throws Exception {
		per = null;
	}

}
