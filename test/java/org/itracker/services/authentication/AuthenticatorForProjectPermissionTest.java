package org.itracker.services.authentication;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticatorForProjectPermissionTest extends AbstractDependencyInjectionTest {

    private UserService userService;
    private ConfigurationService configurationService;
    private DefaultAuthenticator authenticator;

    @Test
    public void testSuperUser() {

        int[] permissionTypes = new int[]{
                UserUtilities.PERMISSION_USER_ADMIN
        };

        List<User> users = authenticator.getUsersWithProjectPermission(2, permissionTypes, false, false, 1);

        assertEquals(1, users.size());

        User superUser = users.get(0);

        assertEquals("admin_test1", superUser.getLogin());
        assertEquals("admin_test1", superUser.getPassword());
        assertEquals("admin firstname", superUser.getFirstName());
        assertEquals("admin lastname", superUser.getLastName());
        assertEquals("email@email.email", superUser.getEmail());
        assertTrue(superUser.isSuperUser());

    }

    @Test
    public void testProjectAdmin() {

        int[] permissionTypes = new int[]{
                UserUtilities.PERMISSION_PRODUCT_ADMIN
        };

        List<User> users = authenticator.getUsersWithProjectPermission(2, permissionTypes, false, false, 1);

        assertEquals(2, users.size());

        User user1 = users.get(0);
        assertEquals("admin_test1", user1.getLogin());
        assertEquals("admin_test1", user1.getPassword());
        assertEquals("admin firstname", user1.getFirstName());
        assertEquals("admin lastname", user1.getLastName());
        assertEquals("email@email.email", user1.getEmail());
        assertTrue(user1.isSuperUser());

        User user2 = users.get(1);
        assertEquals("user_test1", user2.getLogin());
        assertEquals("user_test1", user2.getPassword());
        assertEquals("user firstname", user2.getFirstName());
        assertEquals("user lastname", user2.getLastName());
        assertEquals("email@email.email", user2.getEmail());
        assertFalse(user2.isSuperUser());

    }

    @Test
    public void testAllPermissions() {

        int[] permissionTypes = new int[]{
                UserUtilities.PERMISSION_USER_ADMIN,
                UserUtilities.PERMISSION_PRODUCT_ADMIN,
                UserUtilities.PERMISSION_CREATE,
                UserUtilities.PERMISSION_EDIT,
                UserUtilities.PERMISSION_CLOSE,
                UserUtilities.PERMISSION_ASSIGN_SELF,
                UserUtilities.PERMISSION_ASSIGN_OTHERS,
                UserUtilities.PERMISSION_VIEW_ALL,
                UserUtilities.PERMISSION_VIEW_USERS,
                UserUtilities.PERMISSION_EDIT_USERS,
                UserUtilities.PERMISSION_UNASSIGN_SELF,
                UserUtilities.PERMISSION_ASSIGNABLE,
                UserUtilities.PERMISSION_CREATE_OTHERS,
                UserUtilities.PERMISSION_EDIT_FULL
        };

        List<User> users = authenticator.getUsersWithProjectPermission(2, permissionTypes, false, false, 1);

        assertEquals(2, users.size());

        User user1 = users.get(0);
        assertEquals("admin_test1", user1.getLogin());
        assertEquals("admin_test1", user1.getPassword());
        assertEquals("admin firstname", user1.getFirstName());
        assertEquals("admin lastname", user1.getLastName());
        assertEquals("email@email.email", user1.getEmail());
        assertTrue(user1.isSuperUser());

        User user2 = users.get(1);
        assertEquals("user_test1", user2.getLogin());
        assertEquals("user_test1", user2.getPassword());
        assertEquals("user firstname", user2.getFirstName());
        assertEquals("user lastname", user2.getLastName());
        assertEquals("email@email.email", user2.getEmail());
        assertFalse(user2.isSuperUser());

        // TODO: Need to update userbean to have users having differnt roles

    }

    @Test
    public void testRequireAll() {

        int[] permissionTypes = new int[]{
                UserUtilities.PERMISSION_CREATE,
                UserUtilities.PERMISSION_EDIT,
                UserUtilities.PERMISSION_CLOSE,
                UserUtilities.PERMISSION_ASSIGN_SELF,
                UserUtilities.PERMISSION_ASSIGN_OTHERS,
                UserUtilities.PERMISSION_VIEW_ALL,
                UserUtilities.PERMISSION_VIEW_USERS,
                UserUtilities.PERMISSION_CREATE_OTHERS
        };

        List<User> users = authenticator.getUsersWithProjectPermission(2, permissionTypes, true, false, 1);

        assertEquals(2, users.size());

    }

    @Test
    public void testActiveOnly() {
        
    }

    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();

        userService = (UserService) applicationContext.getBean("userService");
        configurationService = (ConfigurationService) applicationContext.getBean("configurationService");

        initializeAuthenticator();

    }

    private void initializeAuthenticator() {

        Map servicesMap = new HashMap();

        servicesMap.put("userService", userService);
        servicesMap.put("configurationService", configurationService);

        authenticator = new DefaultAuthenticator();
        authenticator.initialize(servicesMap);

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/projectbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/permissionbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
