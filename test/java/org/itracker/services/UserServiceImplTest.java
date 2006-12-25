package org.itracker.services;

import java.util.ArrayList;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Issue;
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

    public void testGetPermissionsByUserId() {
        Integer userId = 3;
        Integer projectId = 2;
        List<Permission> currentPermissions;

        Project project = projectDAO.findByPrimaryKey( projectId );

        User user = userDAO.findByPrimaryKey( userId );

        List<Permission> assertedPermissions = new ArrayList<Permission>();
        assertedPermissions.add( new Permission( project, 1, user ) );
        assertedPermissions.add( new Permission( project, 2, user ) );
        assertedPermissions.add( new Permission( project, 3, user ) );

        currentPermissions = userService.getPermissionsByUserId( userId );
        assertEquals( assertedPermissions.get( 0 ).getProject().getName(),
                      currentPermissions.get( 0 ).getProject().getName() );

        assertEquals( assertedPermissions.get( 1 ).getProject().getName(),
                      currentPermissions.get( 1 ).getProject().getName() );

        assertEquals( assertedPermissions.get( 2 ).getProject().getName(),
                      currentPermissions.get( 2 ).getProject().getName() );

        assertEquals( assertedPermissions.get( 0 ).getPermissionType(),
                      currentPermissions.get( 0 ).getPermissionType() );

        assertEquals( assertedPermissions.get( 1 ).getPermissionType(),
                      currentPermissions.get( 1 ).getPermissionType() );

        assertEquals( assertedPermissions.get( 2 ).getPermissionType(),
                      currentPermissions.get( 2 ).getPermissionType() );

        assertEquals( assertedPermissions.get( 0 ).getUser().getEmail(),
                      currentPermissions.get( 0 ).getUser().getEmail() );

        assertEquals( assertedPermissions.get( 1 ).getUser().getEmail(),
                      currentPermissions.get( 1 ).getUser().getEmail() );

        assertEquals( assertedPermissions.get( 2 ).getUser().getEmail(),
                      currentPermissions.get( 2 ).getUser().getEmail() );
    }

    public void testSetUserPermissions() {
        Integer userId = 3;
        Integer projectId = 2;

        List<Permission> newPermissions = new ArrayList<Permission>();

        User user = userDAO.findByPrimaryKey( userId );

        Project project = projectDAO.findByPrimaryKey( projectId );

        newPermissions.add( new Permission( project, 4, user ) );

        userService.setUserPermissions( userId, newPermissions );

        assertEquals( newPermissions.get( 0 ).getPermissionType(),
                      userService.getPermissionsByUserId( userId ).get(
                              0 ).getPermissionType() );

    }

    public void testSetAndUnsetUserPermissions() {
        Integer userId = 3;
        Integer projectId = 2;
        List<Permission> newPermissions = new ArrayList<Permission>();

        User user = userDAO.findByPrimaryKey( userId );

        Project project = projectDAO.findByPrimaryKey( projectId );

        newPermissions.add( new Permission( project, 4, user ) );
        userService.setUserPermissions( userId, newPermissions );

        assertEquals( newPermissions.get( 0 ).getPermissionType(),
                      userService.getPermissionsByUserId( userId ).get(
                              0 ).getPermissionType() );

        newPermissions.clear();
        userService.setUserPermissions( userId, newPermissions );


        newPermissions.add( new Permission( project, 7, user ) );
        userService.setUserPermissions( userId, newPermissions );
        assertEquals( 7, userService.getPermissionsByUserId( userId ).get(
                0 ).getPermissionType() );


    }


    public void testGetUsersWithProjectPermission() {
        Integer projectId = 2;
        Integer permissionId = 1;
        List<User> users = userService.getUsersWithProjectPermission( projectId,
                                                                      permissionId );
        assertNotNull( users );
    }

    public void testGetPossibleOwners() {
        Issue issue = new Issue();
        Integer projectId = 2;
        Integer userId = 2;
        List<User> users =
                userService.getPossibleOwners( issue, projectId, userId );
        assertNotNull( users );
    }


    private void assertUsersEqual( List<User> users,
                                   Integer userID,
                                   String login ) {
        for( User user : users ) {
            assertEquals( userID, user.getId() );
            assertEquals( login, user.getLogin() );
        }
    }

    protected void onSetUp() throws Exception {
        super.onSetUp();
        userService = (UserService)applicationContext.getBean( "userService" );
        projectDAO = (ProjectDAO)applicationContext.getBean( "projectDAO" );
        userDAO = (UserDAO)applicationContext.getBean( "userDAO" );
        permissionDAO =
                (PermissionDAO)applicationContext.getBean( "permissionDAO" );
    }

    protected String[] getDataSetFiles() {
        return new String[] {
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml", 
                "dataset/permissionbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[] { "application-context.xml" };
    }

}
