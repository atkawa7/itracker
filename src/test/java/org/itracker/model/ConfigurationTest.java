package org.itracker.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {
	private Configuration conf;
	
	@Test
	public void testSetValue(){		
		try{
			conf.setValue(null);
			fail("did not throw IllegalArgumentException");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetVersion(){		
		try{
			conf.setVersion(null);
			fail("did not throw IllegalArgumentException");
		}catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	

	
	@Test
	public void testToString(){		
		assertNotNull(conf.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		conf = new Configuration();
    }
	
	@After
	public void tearDown() throws Exception {
		conf = null;
	}

}
