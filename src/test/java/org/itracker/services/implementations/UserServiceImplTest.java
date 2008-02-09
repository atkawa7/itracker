package org.itracker.services.implementations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Issue;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.persistence.dao.PermissionDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.persistence.dao.UserPreferencesDAO;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;
import org.itracker.services.exceptions.UserException;
import org.junit.Test;

public class UserServiceImplTest extends AbstractDependencyInjectionTest {

    private UserService userService;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    @SuppressWarnings("unused")
    private PermissionDAO permissionDAO;
    private UserPreferencesDAO userPreferencesDAO;

    @Test
    public void getSuperUsers() {

        List<User> users = userService.getSuperUsers();

        assertNotNull(users);
        assertEquals(1, users.size());

    }

    @Test
    public void testGetPermissionsByUserId() {
        Integer userId = 3;
        Integer projectId = 2;
        List<Permission> currentPermissions;

        Project project = projectDAO.findByPrimaryKey(projectId);

        User user = userDAO.findByPrimaryKey(userId);

        List<Permission> assertedPermissions = new ArrayList<Permission>();
        assertedPermissions.add(new Permission(1, user, project));
        assertedPermissions.add(new Permission(2, user, project));
        assertedPermissions.add(new Permission(3, user, project));

        currentPermissions = userService.getPermissionsByUserId(userId);
        assertEquals(assertedPermissions.get(0).getProject().getName(),
                currentPermissions.get(0).getProject().getName());

        assertEquals(assertedPermissions.get(1).getProject().getName(),
                currentPermissions.get(1).getProject().getName());

        assertEquals(assertedPermissions.get(2).getProject().getName(),
                currentPermissions.get(2).getProject().getName());

        assertEquals(assertedPermissions.get(0).getPermissionType(),
                currentPermissions.get(0).getPermissionType());

        assertEquals(assertedPermissions.get(1).getPermissionType(),
                currentPermissions.get(1).getPermissionType());

        assertEquals(assertedPermissions.get(2).getPermissionType(),
                currentPermissions.get(2).getPermissionType());

        assertEquals(assertedPermissions.get(0).getUser().getEmail(),
                currentPermissions.get(0).getUser().getEmail());

        assertEquals(assertedPermissions.get(1).getUser().getEmail(),
                currentPermissions.get(1).getUser().getEmail());

        assertEquals(assertedPermissions.get(2).getUser().getEmail(),
                currentPermissions.get(2).getUser().getEmail());
    }

    @Test
    public void testSetUserPermissions() {
        Integer userId = 3;
        Integer projectId = 2;

        List<Permission> newPermissions = new ArrayList<Permission>();

        User user = userDAO.findByPrimaryKey(userId);

        Project project = projectDAO.findByPrimaryKey(projectId);

        Permission permission = new Permission(4, user, project);
        permission.setCreateDate(new Date());
        permission.setLastModifiedDate(new Date());

        newPermissions.add(permission);

        userService.setUserPermissions(userId, newPermissions);

        assertEquals(newPermissions.get(0).getPermissionType(),
                userService.getPermissionsByUserId(userId).get(
                        0).getPermissionType());

    }

    @Test
    public void testSetAndUnsetUserPermissions() {
        Integer userId = 3;
        Integer projectId = 2;
        List<Permission> newPermissions = new ArrayList<Permission>();

        User user = userDAO.findByPrimaryKey(userId);

        Project project = projectDAO.findByPrimaryKey(projectId);

        Permission permission = new Permission(4, user, project);
        permission.setCreateDate(new Date());
        permission.setLastModifiedDate(new Date());

        newPermissions.add(permission);
        userService.setUserPermissions(userId, newPermissions);

        assertEquals(newPermissions.get(0).getPermissionType(),
                userService.getPermissionsByUserId(userId).get(
                        0).getPermissionType());

        newPermissions.clear();
        userService.setUserPermissions(userId, newPermissions);

        permission = new Permission(7, user, project);
        permission.setCreateDate(new Date());
        permission.setLastModifiedDate(new Date());

        newPermissions.add(permission);

        userService.setUserPermissions(userId, newPermissions);
        assertEquals(7, userService.getPermissionsByUserId(userId).get(
                0).getPermissionType());


    }

    @Test
    public void testGetUsersWithProjectPermission() {
        Integer projectId = 2;
        Integer permissionId = 1;
        List<User> users = userService.getUsersWithProjectPermission(projectId,
                permissionId);
        assertNotNull(users);
    }

    @Test
    public void testUpdateUser() throws UserException {
        User user = userService.getUser(2);

        user.setEmail("updated_email@test.com");

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("updated_email@test.com", updatedUser.getEmail());
    }

    @Test
    public void testGetPossibleOwners() {
        Issue issue = new Issue();
        Integer projectId = 2;
        Integer userId = 2;
        List<User> users =
                userService.getPossibleOwners(issue, projectId, userId);
        assertNotNull(users);
    }

    @Test
    public void testUpdateUserPreferences() throws UserException {

        UserPreferences userPreferences = userPreferencesDAO.findByUserId(2);
        assertTrue(userPreferences.getSaveLogin());

        userPreferences.setSaveLogin(false);

        UserPreferences updatedUserPreferences = userService.updateUserPreferences(userPreferences);

        assertNotNull(updatedUserPreferences);
        assertFalse(updatedUserPreferences.getSaveLogin());

    }

    @Test
    public void testAllowProfileUpdates() {

        User user = userService.getUser(2);

        boolean allowProfileUpdates = userService.allowProfileUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);

        assertTrue(allowProfileUpdates);

    }


    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();

        userService = (UserService) applicationContext.getBean("userService");
        userPreferencesDAO = (UserPreferencesDAO) applicationContext.getBean("userPreferencesDAO");
        projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
        userDAO = (UserDAO) applicationContext.getBean("userDAO");
        permissionDAO = (PermissionDAO) applicationContext.getBean("permissionDAO");

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
