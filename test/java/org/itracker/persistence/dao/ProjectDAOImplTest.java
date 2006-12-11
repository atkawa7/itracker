package org.itracker.persistence.dao;

import java.util.Date;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Project;

public class ProjectDAOImplTest extends AbstractDependencyInjectionTest {

    private ProjectDAO projectDAO;

    public void testCreateProject() {

        Project project = new Project();
        project.setCreateDate( new Date() );
        projectDAO.saveOrUpdate( project );

        Project foundProject = projectDAO.findByPrimaryKey(2);

        assertNotNull( foundProject );
        assertEquals( "dbunit Testproject 1", foundProject.getName() );
        assertEquals( "Testproject 1 description", foundProject.getDescription() );
        assertEquals( 1, foundProject.getStatus() );
        assertEquals( 0, foundProject.getOptions() );

    }

    protected void onSetUp() throws Exception {
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
