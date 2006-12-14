package org.itracker.persistence.dao;

import java.util.Date;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Project;

public class ProjectDAOImplTest extends AbstractDependencyInjectionTest {

    private ProjectDAO projectDAO;

    public void testCreateProject() {

        Project project = new Project();
        project.setCreateDate( new Date() );
        project.setLastModifiedDate( new Date() );
        project.setName("test_name");
        project.setDescription("test_description");
        project.setStatus(1);
        project.setOptions(0);
        projectDAO.saveOrUpdate( project );

        Project foundProject = projectDAO.findByPrimaryKey(project.getId());

        assertNotNull( foundProject );
        assertEquals( "test_name", foundProject.getName() );
        assertEquals( "test_description", foundProject.getDescription() );
        assertEquals( 1, foundProject.getStatus() );
     

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
