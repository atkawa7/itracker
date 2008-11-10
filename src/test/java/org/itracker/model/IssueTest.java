package org.itracker.model;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueTest {
	private Issue iss;
	
	@Test
	public void testToString(){				
		assertNotNull("toString", iss.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		iss = new Issue();
    }
	
	@After
	public void tearDown() throws Exception {
		iss = null;
	}

}
