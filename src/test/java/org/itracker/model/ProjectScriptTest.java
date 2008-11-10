package org.itracker.model;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectScriptTest {
	private ProjectScript pro;
	
	
	@Test
	public void testToString(){		
		assertNotNull("toString", pro.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		pro = new ProjectScript();
    }
	
	@After
	public void tearDown() throws Exception {
		pro = null;
	}

}
