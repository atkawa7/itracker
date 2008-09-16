package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Configuration;
import org.junit.Test;

public class ConfigurationDAOImplTest extends AbstractDependencyInjectionTest {

    private ConfigurationDAO configurationDAO;

    @Test
    public void testFindByPrimaryKey() {
        Configuration configuration = configurationDAO.findByPrimaryKey(2000);

        assertNotNull(configuration);

        assertEquals(1, configuration.getType());
        assertEquals("Test Value", configuration.getValue());
        assertEquals("Version 1.0", configuration.getVersion());
        assertEquals(1, configuration.getOrder());
    }

    @Test
    public void testFindByType() {
        List<Configuration> configurations = configurationDAO.findByType(1);

        assertEquals(1, configurations.size());

        Configuration configuration = configurations.get(0);

        // assertEquals(2000, configuration.getId().intValue());
        assertEquals("Test Value", configuration.getValue());
        assertEquals("Version 1.0", configuration.getVersion());
        assertEquals(1, configuration.getOrder());
    }

    @Test
    public void testFindByTypeAndValue() {
        List<Configuration> configurations = configurationDAO.findByTypeAndValue(1, "Test Value");

        assertEquals(1, configurations.size());

        Configuration configuration = configurations.get(0);

        // assertEquals(2000, configuration.getId().intValue());
        assertEquals("Test Value", configuration.getValue());
        assertEquals("Version 1.0", configuration.getVersion());
        assertEquals(1, configuration.getOrder());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        configurationDAO = (ConfigurationDAO) applicationContext.getBean("configurationDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/configurationbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
