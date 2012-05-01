package org.itracker.model;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueSearchQueryTest {
	private IssueSearchQuery iss;
	
	
	@Test
	public void testToString(){		
		assertNotNull("toString", iss.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		iss = new IssueSearchQuery();
    }
	
	@After
	public void tearDown() throws Exception {
		iss = null;
	}

}
