package org.itracker.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.itracker.services.util.SystemConfigurationUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemConfigurationTest {
	private SystemConfiguration conf;
	
	@Test
	public void testAddRESOLUTIONConfiguration(){
		assertEquals("Resolutions size 0", 0 ,conf.getResolutions().size());
		Configuration con = new Configuration();
		con.setType(SystemConfigurationUtilities.TYPE_RESOLUTION);
		conf.addConfiguration(con);
		assertEquals("Resolutions size 1", 1 ,conf.getResolutions().size());
		assertEquals("Resolutions type TYPE_RESOLUTION", SystemConfigurationUtilities.TYPE_RESOLUTION ,conf.getResolutions().get(0).getType());
	}
	
	@Test
	public void testAddSEVERITYConfiguration(){
		assertEquals("SEVERITY size 0", 0 ,conf.getSeverities().size());
		Configuration con = new Configuration();
		con.setType(SystemConfigurationUtilities.TYPE_SEVERITY);
		conf.addConfiguration(con);
		assertEquals("SEVERITY size 1", 1 ,conf.getSeverities().size());
		assertEquals("SEVERITY type TYPE_SEVERITY", SystemConfigurationUtilities.TYPE_SEVERITY ,conf.getSeverities().get(0).getType());
	}
	
	@Test
	public void testAddSTATUSConfiguration(){
		assertEquals("STATUS size 0", 0 ,conf.getStatuses().size());
		Configuration con = new Configuration();
		con.setType(SystemConfigurationUtilities.TYPE_STATUS);
		conf.addConfiguration(con);
		assertEquals("STATUS size 1", 1 ,conf.getStatuses().size());
		assertEquals("STATUS type TYPE_STATUS", SystemConfigurationUtilities.TYPE_STATUS ,conf.getStatuses().get(0).getType());
	}
	
	@Test
	public void testAddNullConfiguration(){			
		conf.addConfiguration(null);
		assertEquals("Resolutions size 0", 0 ,conf.getResolutions().size());
		assertEquals("SEVERITY size 0", 0 ,conf.getSeverities().size());
		assertEquals("STATUS size 0", 0 ,conf.getStatuses().size());
	}
	

	@Test
	public void testToString(){		
		assertNotNull("toString", conf.toString());
	}
	
	
	@Before
    public void setUp() throws Exception {
		conf = new SystemConfiguration();
    }
	
	@After
	public void tearDown() throws Exception {
		conf = null;
	}

}
