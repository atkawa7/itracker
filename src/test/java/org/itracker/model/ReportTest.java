package org.itracker.model;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReportTest {
	private Report rpt;
	
	@Test
	public void testToString(){				
		assertNotNull("toString", rpt.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		rpt = new Report();
    }
	
	@After
	public void tearDown() throws Exception {
		rpt = null;
	}

}
