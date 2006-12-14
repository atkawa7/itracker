package org.itracker.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.persistence.dao.PermissionDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.UserDAO;

public class UserServiceImplTest extends AbstractDependencyInjectionTest {

    private UserService userService;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private PermissionDAO permissionDAO;
    
    public void testGetSuperUsers() {

        List<User> users = userService.getSuperUsers();
        
        assertNotNull( users );

        assertUsersEqual( users, 2, "admin_test1" );

    }

    public void testSetUserPermissions() {
    	Integer userId = 2;
    	Integer projectId = 2;
    	List<Permission> newPermissions = new ArrayList<Permission>();
    	User user = new User();
    	user = userDAO.findByPrimaryKey(userId);
        Project project = new Project();
        project = projectDAO.findByPrimaryKey(projectId);
    	newPermissions.add(new Permission(project,4,user));
    	boolean successful = userService.setUserPermissions(userId, newPermissions);
    	if (successful) {
    		assertEquals(newPermissions, userService.getPermissionsByUserId(userId));
    	}
    }
    
    public void testSetAndUnsetUserPermissions() {
    	Integer userId = 2;
    	Integer projectId = 2;
    	List<Permission> newPermissions = new ArrayList<Permission>();
    	User user = new User();
    	user = userDAO.findByPrimaryKey(userId);
        Project project = new Project();
        project = projectDAO.findByPrimaryKey(projectId);
    	newPermissions.add(new Permission(project,4,user));
    	boolean successfulset = userService.setUserPermissions(userId, newPermissions);
    	if (successfulset) {
    		assertEquals(newPermissions, userService.getPermissionsByUserId(userId));
    	}
    	newPermissions.clear();
    	boolean successfulunset = userService.setUserPermissions(userId, newPermissions);
    	if (successfulunset) {
    		assertEquals(newPermissions, userService.getPermissionsByUserId(userId));
    	}
    	
    }

    public void testGetPermissionsByUserId() {
    	Integer userId = 1;
    	List<Permission> assertedPermissions = new ArrayList<Permission>();
    	List<Permission> currentPermissions = new ArrayList<Permission>();
    	Project project = new Project();
        project.setCreateDate( new Date() );
        project.setLastModifiedDate( new Date() );
        project.setName("test_name");
        project.setDescription("test_description");
        project.setStatus(1);
        project.setOptions(1);
        User user = new User( "admin_test2","admin_test2", "admin firstname2", "admin lastname2", "", true );
    	assertedPermissions.add(new Permission(project,1,user));
    	assertedPermissions.add(new Permission(project,2,user));
    	assertedPermissions.add(new Permission(project,3,user));
    	currentPermissions = userService.getPermissionsByUserId(userId);
    	assertEquals(assertedPermissions, currentPermissions);
    	
    }
    
    private void assertUsersEqual( List<User> users,
                                       Integer userID,
                                       String login  ) {
    	for (User user: users) {
        assertEquals( userID, user.getId() );
        assertEquals( login, user.getLogin());
    		   }
          }

    protected void onSetUp() throws Exception {
        super.onSetUp();
        userService = (UserService)applicationContext.getBean( "userService" );
        projectDAO = (ProjectDAO)applicationContext.getBean( "projectDAO" );
        userDAO = (UserDAO)applicationContext.getBean( "userDAO" );
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
