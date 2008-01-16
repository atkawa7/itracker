package org.itracker.services.implementations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Issue;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.persistence.dao.PermissionDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.UserException;
import org.junit.Test;

public class UserServiceImplTest extends AbstractDependencyInjectionTest {

    private UserService userService;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private PermissionDAO permissionDAO;

    @Test
    public void getSuperUsers() {

        List<User> users = userService.getSuperUsers();

        assertNotNull(users);

        assertUsersEqual(users, 2, "admin_test1");

    }

    @Test
    public void getPermissionsByUserId() {
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
    public void setUserPermissions() {
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
    public void setAndUnsetUserPermissions() {
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
    public void getUsersWithProjectPermission() {
        Integer projectId = 2;
        Integer permissionId = 1;
        List<User> users = userService.getUsersWithProjectPermission(projectId,
                permissionId);
        assertNotNull(users);
    }

    @Test
    public void updateUser() throws UserException {
        User user = userService.getUser(2);

        user.setEmail("updated_email@test.com");

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("updated_email@test.com", updatedUser.getEmail());
    }

    @Test
    public void getPossibleOwners() {
        Issue issue = new Issue();
        Integer projectId = 2;
        Integer userId = 2;
        List<User> users =
                userService.getPossibleOwners(issue, projectId, userId);
        assertNotNull(users);
    }


    private void assertUsersEqual(List<User> users,
                                  Integer userID,
                                  String login) {
        for (User user : users) {
            assertEquals(userID, user.getId());
            assertEquals(login, user.getLogin());
        }
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();
        userService = (UserService) applicationContext.getBean("userService");
        projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
        userDAO = (UserDAO) applicationContext.getBean("userDAO");
        permissionDAO =
                (PermissionDAO) applicationContext.getBean("permissionDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}