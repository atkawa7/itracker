package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.junit.Test;

public class ProjectDAOImplTest extends AbstractDependencyInjectionTest {

    private ProjectDAO projectDAO;

    @Test
    public void createProject() {

        Project foundProject = projectDAO.findByPrimaryKey(2);

        assertNotNull( foundProject );
        assertEquals( "test_name", foundProject.getName() );
        assertEquals( "test_description", foundProject.getDescription() );
        assertEquals( Status.ACTIVE, foundProject.getStatus() );
     

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        projectDAO = (ProjectDAO)applicationContext.getBean( "projectDAO" );
    }

    protected String[] getDataSetFiles() {
        return new String[] {
                "dataset/projectbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[] { "application-context.xml" };
    }

}