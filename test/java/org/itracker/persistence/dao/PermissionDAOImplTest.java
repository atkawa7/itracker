package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.junit.Test;

public class PermissionDAOImplTest extends AbstractDependencyInjectionTest {

    private UserDAO userDAO;
    private ProjectDAO projectDAO;
    private PermissionDAO permissionDAO;

    @Test
    public void createPermission() {

        Permission foundPermission = permissionDAO.findByUserId(2).get(1);
        User foundUser=foundPermission.getUser();
        Project foundProject=foundPermission.getProject();
        
        assertNotNull( foundPermission );
        assertNotNull( foundUser );
        assertNotNull( foundProject );
        assertEquals( 2, foundPermission.getPermissionType() );
        assertEquals( "admin_test1", foundUser.getLogin() );
        assertEquals( "test_name", foundProject.getName() );

    }
 
    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();
        userDAO = (UserDAO)applicationContext.getBean( "userDAO" );
        projectDAO = (ProjectDAO)applicationContext.getBean( "projectDAO" );
        permissionDAO = (PermissionDAO)applicationContext.getBean( "permissionDAO" );
    }

    protected String[] getDataSetFiles() {
        return new String[] {
        		"dataset/userbean_dataset.xml", "dataset/projectbean_dataset.xml", "dataset/permissionbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[] { "application-context.xml" };
    }

}
