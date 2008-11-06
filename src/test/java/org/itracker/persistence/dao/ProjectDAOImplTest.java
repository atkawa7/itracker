package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProjectDAOImplTest extends AbstractDependencyInjectionTest {

    private ProjectDAO projectDAO;

    @Test
    public void testCreateProject() {

        Project foundProject = projectDAO.findByPrimaryKey(2);

        assertNotNull( foundProject );
        assertEquals( "test_name", foundProject.getName() );
        assertEquals( "test_description", foundProject.getDescription() );
        assertEquals( Status.ACTIVE, foundProject.getStatus() );
    }

    @Test
    public void testFindByStatus() {
        List<Project> projects = projectDAO.findByStatus(1);

        assertNotNull(projects);
        assertEquals(2, projects.size());
    }

    @Test
    public void testFindAllAvailable() {
    	List<Project> projects = projectDAO.findAllAvailable();
    	
    	assertNotNull(projects);
    	assertEquals(2, projects.size());
    }
    
    @Test
    @Ignore
    public void testGetLastIssueUpdateDate() {
        Date date = projectDAO.getLastIssueUpdateDate(2);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // FIXME: there are 4 issues connected with project 2, why we got null here?
        assertNotNull( "null date", date );
        assertEquals("last issue update date", "2008-01-01", df.format(date));
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
