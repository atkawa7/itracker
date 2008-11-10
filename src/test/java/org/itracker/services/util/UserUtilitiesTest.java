/*
 * 
 */
package org.itracker.services.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.NameValuePair;
import org.itracker.model.PermissionType;
import org.junit.Test;

/**
 *
 * @author Andrey Sergievskiy
 */
public class UserUtilitiesTest extends AbstractDependencyInjectionTest {

    private void doTestGetStatusName(final Locale locale,
            final int statusId, final String expected) {
        final String actual =
                UserUtilities.getStatusName(statusId, locale);
        assertEquals("UserUtilities.getStatusName(" + statusId + ", " +
                locale + ")", expected, actual);
    }

    /**
     * Verifies UserUtilities.getStatusName
     */
    @Test
    public void testGetStatusName() {
        // testing a case of missing key
        doTestGetStatusName(null,
                999, "MISSING KEY: itracker.user.status.999");

        // "Deleted"
        doTestGetStatusName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.STATUS_DELETED, "Deleted");
        doTestGetStatusName(null,
                UserUtilities.STATUS_DELETED, "Deleted");
        doTestGetStatusName(new Locale("test"),
                UserUtilities.STATUS_DELETED, "test_value");

        // "Active"
        doTestGetStatusName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.STATUS_ACTIVE, "Active");
        doTestGetStatusName(null,
                UserUtilities.STATUS_ACTIVE, "Active");
        doTestGetStatusName(new Locale("test"),
                UserUtilities.STATUS_ACTIVE, "Active");

        // "Locked"
        doTestGetStatusName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.STATUS_LOCKED, "Locked");
        doTestGetStatusName(null,
                UserUtilities.STATUS_LOCKED, "Locked");
        doTestGetStatusName(new Locale("test"),
                UserUtilities.STATUS_LOCKED, "Locked");
    }

    private void doTestGetStatusName(final int statusId, final String expected) {
        doTestGetStatusName(ITrackerResources.getLocale(), statusId, expected);
        final String actual = UserUtilities.getStatusName(statusId);
        assertEquals("UserUtilities.getStatusName(" + statusId + ")",
                expected, actual);
    }

    /**
     * Verifies UserUtilities#getStatusName(int)
     */
    @Test
    public void testGetStatusNameDefault() {
        doTestGetStatusName(999, "MISSING KEY: itracker.user.status.999");
        doTestGetStatusName(UserUtilities.STATUS_DELETED, "Deleted");
        doTestGetStatusName(UserUtilities.STATUS_ACTIVE, "Active");
        doTestGetStatusName(UserUtilities.STATUS_LOCKED, "Locked");
    }

    private void doTestGetStatusNames(final Locale locale,
            final NameValuePair[] expected) {
        final Map<String, String> actual = UserUtilities.getStatusNames(locale);
        for (final NameValuePair nvpExpected : expected) {
            final String key = nvpExpected.getName();
            if (actual.containsKey(key)) {
                final String valueActual = actual.get(key);
                assertEquals("UserUtilities.getStatusNames(" + locale + ")" +
                        ".get(" + nvpExpected.getName() + ")",
                        nvpExpected.getValue(), valueActual);
            } else {
                assertEquals("UserUtilities.getStatusNames(" + locale + ")" +
                        ".get(" + nvpExpected.getName() + ")." +
                        "contains(" + nvpExpected.getName() + ")",
                        false);
            }

        }
        assertEquals("UserUtilities.getStatusNames(" + locale + ").size()",
                expected.length, actual.size());
    }

    /**
     * Verifies UserUtilities#getStatusNames(Locale)
     */
    @Test
    public void testGetStatusNames() {
        doTestGetStatusNames(null, new NameValuePair[]{
                    new NameValuePair("-1", "Deleted"),
                    new NameValuePair("1", "Active"),
                    new NameValuePair("2", "Locked")
                });
        doTestGetStatusNames(new Locale(ITrackerResources.BASE_LOCALE),
                new NameValuePair[]{
                    new NameValuePair("-1", "Deleted"),
                    new NameValuePair("1", "Active"),
                    new NameValuePair("2", "Locked")
                });
        doTestGetStatusNames(new Locale("test"),
                new NameValuePair[]{
                    new NameValuePair("-1", "test_value"),
                    new NameValuePair("1", "Active"),
                    new NameValuePair("2", "Locked")
                });
    }

    private void doTestGetStatusNamesDefault(final NameValuePair[] expected) {
        final Map<String, String> actual = UserUtilities.getStatusNames();
        for (final NameValuePair nvpExpected : expected) {
            final String key = nvpExpected.getName();
            if (actual.containsKey(key)) {
                final String valueActual = actual.get(key);
                assertEquals("UserUtilities.getStatusNames().get(" +
                        nvpExpected.getName() + ")",
                        nvpExpected.getValue(), valueActual);
            } else {
                assertEquals("UserUtilities.getStatusNames().get(" +
                        nvpExpected.getName() + ")." +
                        "contains(" + nvpExpected.getName() + ")",
                        false);
            }

        }
        assertEquals("UserUtilities.getStatusNames().size()",
                expected.length, actual.size());
    }

    /**
     * Verifies UserUtilities#getStatusNames()
     */
    @Test
    public void testGetStatusNamesDefault() {
        doTestGetStatusNamesDefault(new NameValuePair[]{
                    new NameValuePair("-1", "Deleted"),
                    new NameValuePair("1", "Active"),
                    new NameValuePair("2", "Locked")
                });
    }

    private void doTestGetPermissionName(final Locale locale,
            final int permissionId, final String expected) {
        final String actual =
                UserUtilities.getPermissionName(permissionId, locale);
        assertEquals("UserUtilities.getPermissionName(" + permissionId + ", " +
                locale + ")", expected, actual);
    }

    /**
     * Verifies UserUtilities.getPermissionName
     */
    @Test
    public void testGetPermissionName() {
        // testing a case of missing key
        doTestGetPermissionName(null,
                999, "MISSING KEY: itracker.user.permission.999");

        // "USER ADMIN"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_USER_ADMIN, "User Admin");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_USER_ADMIN, "User Admin");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_USER_ADMIN, "test_value");

        // "PRODUCT_ADMIN"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_PRODUCT_ADMIN, "Project Admin");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_PRODUCT_ADMIN, "Project Admin");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_PRODUCT_ADMIN, "Project Admin");

        // "CREATE"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_CREATE, "Create Issues");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_CREATE, "Create Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_CREATE, "Create Issues");

        // "EDIT"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_EDIT, "Edit All Issues");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_EDIT, "Edit All Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_EDIT, "Edit All Issues");

        // "CLOSE"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_CLOSE, "Close Issues");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_CLOSE, "Close Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_CLOSE, "Close Issues");

        // "ASSIGN_SELF"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_ASSIGN_SELF,
                "Assign Issues to Self");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_ASSIGN_SELF,
                "Assign Issues to Self");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_ASSIGN_SELF,
                "Assign Issues to Self");

        // "ASSIGN_OTHERS"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_ASSIGN_OTHERS,
                "Assign Issues to Others");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_ASSIGN_OTHERS,
                "Assign Issues to Others");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_ASSIGN_OTHERS,
                "Assign Issues to Others");

        // "VIEW ALL"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_VIEW_ALL, "View All Issues");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_VIEW_ALL, "View All Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_VIEW_ALL, "View All Issues");

        // "VIEW USERS"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_VIEW_USERS, "View User's Own Issues");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_VIEW_USERS, "View User's Own Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_VIEW_USERS, "View User's Own Issues");

        // "EDIT USERS"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_EDIT_USERS, "Edit User's Own Issues");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_EDIT_USERS, "Edit User's Own Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_EDIT_USERS, "Edit User's Own Issues");

        // "UNASSIGN SLEF"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_UNASSIGN_SELF,
                "Unassign Issues from Self");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_UNASSIGN_SELF,
                "Unassign Issues from Self");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_UNASSIGN_SELF,
                "Unassign Issues from Self");

        // "ASSIGNABLE"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_ASSIGNABLE,
                "Can Be Assigned Any Issue");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_ASSIGNABLE,
                "Can Be Assigned Any Issue");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_ASSIGNABLE,
                "Can Be Assigned Any Issue");

        // "CREATE OTHERS"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_CREATE_OTHERS,
                "Create Issues for Others");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_CREATE_OTHERS,
                "Create Issues for Others");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_CREATE_OTHERS,
                "Create Issues for Others");

        // "EDIT FULL"
        doTestGetPermissionName(new Locale(ITrackerResources.BASE_LOCALE),
                UserUtilities.PERMISSION_EDIT_FULL, "Full Issue Edit");
        doTestGetPermissionName(null,
                UserUtilities.PERMISSION_EDIT_FULL, "Full Issue Edit");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_EDIT_FULL, "Full Issue Edit");
    }

    private void doTestGetPermissionNames(final Locale locale,
            final NameValuePair[] expected) {
        final List<NameValuePair> actual = UserUtilities.getPermissionNames(locale);
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                if (nvpExpected.getName().equals(nvpActual.getName())) {
                    found = true;
                    assertEquals("UserUtilities.getStatusNames(" + locale + ")" +
                            ".get(" + nvpExpected.getName() + ")",
                            nvpExpected.getValue(), nvpActual.getValue());
                }
            }
            assertTrue("UserUtilities.getStatusNames(" + locale + ")." +
                    "contains(" + nvpExpected.getName() + ")",
                    found);
        }
        assertEquals("UserUtilities.getStatusNames(" + locale + ").size()",
                expected.length, actual.size());
    }

    @Test
    public void testGetPermissionNames() {
        doTestGetPermissionNames(null, new NameValuePair[]{
                    new NameValuePair("Project Admin",
                    Integer.toString(UserUtilities.PERMISSION_PRODUCT_ADMIN)),
                    new NameValuePair("Create Issues",
                    Integer.toString(UserUtilities.PERMISSION_CREATE)),
                    new NameValuePair("Edit All Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT)),
                    new NameValuePair("Close Issues",
                    Integer.toString(UserUtilities.PERMISSION_CLOSE)),
                    new NameValuePair("Assign Issues to Self",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_SELF)),
                    new NameValuePair("Assign Issues to Others",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_OTHERS)),
                    new NameValuePair("View All Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_ALL)),
                    new NameValuePair("View User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_USERS)),
                    new NameValuePair("Edit User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_USERS)),
                    new NameValuePair("Unassign Issues from Self",
                    Integer.toString(UserUtilities.PERMISSION_UNASSIGN_SELF)),
                    new NameValuePair("Can Be Assigned Any Issue",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGNABLE)),
                    new NameValuePair("Create Issues for Others",
                    Integer.toString(UserUtilities.PERMISSION_CREATE_OTHERS)),
                    new NameValuePair("Full Issue Edit",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_FULL))
                });
        doTestGetPermissionNames(new Locale(ITrackerResources.BASE_LOCALE),
                new NameValuePair[]{
                    new NameValuePair("Project Admin",
                    Integer.toString(UserUtilities.PERMISSION_PRODUCT_ADMIN)),
                    new NameValuePair("Create Issues",
                    Integer.toString(UserUtilities.PERMISSION_CREATE)),
                    new NameValuePair("Edit All Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT)),
                    new NameValuePair("Close Issues",
                    Integer.toString(UserUtilities.PERMISSION_CLOSE)),
                    new NameValuePair("Assign Issues to Self",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_SELF)),
                    new NameValuePair("Assign Issues to Others",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_OTHERS)),
                    new NameValuePair("View All Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_ALL)),
                    new NameValuePair("View User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_USERS)),
                    new NameValuePair("Edit User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_USERS)),
                    new NameValuePair("Unassign Issues from Self",
                    Integer.toString(UserUtilities.PERMISSION_UNASSIGN_SELF)),
                    new NameValuePair("Can Be Assigned Any Issue",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGNABLE)),
                    new NameValuePair("Create Issues for Others",
                    Integer.toString(UserUtilities.PERMISSION_CREATE_OTHERS)),
                    new NameValuePair("Full Issue Edit",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_FULL))
                });
        doTestGetPermissionNames(new Locale("test"),
                new NameValuePair[]{
                    new NameValuePair("Project Admin",
                    Integer.toString(UserUtilities.PERMISSION_PRODUCT_ADMIN)),
                    new NameValuePair("Create Issues",
                    Integer.toString(UserUtilities.PERMISSION_CREATE)),
                    new NameValuePair("Edit All Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT)),
                    new NameValuePair("Close Issues",
                    Integer.toString(UserUtilities.PERMISSION_CLOSE)),
                    new NameValuePair("Assign Issues to Self",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_SELF)),
                    new NameValuePair("Assign Issues to Others",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_OTHERS)),
                    new NameValuePair("View All Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_ALL)),
                    new NameValuePair("View User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_USERS)),
                    new NameValuePair("Edit User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_USERS)),
                    new NameValuePair("Unassign Issues from Self",
                    Integer.toString(UserUtilities.PERMISSION_UNASSIGN_SELF)),
                    new NameValuePair("Can Be Assigned Any Issue",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGNABLE)),
                    new NameValuePair("Create Issues for Others",
                    Integer.toString(UserUtilities.PERMISSION_CREATE_OTHERS)),
                    new NameValuePair("Full Issue Edit",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_FULL))
                });
    }

    private void doTestGetPermissionNamesDefault(final NameValuePair[] expected) {
        final List<NameValuePair> actual = UserUtilities.getPermissionNames();
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                if (nvpExpected.getName().equals(nvpActual.getName())) {
                    found = true;
                    assertEquals("UserUtilities.getStatusNames().get(" +
                            nvpExpected.getName() + ")",
                            nvpExpected.getValue(), nvpActual.getValue());
                }
            }
            assertTrue("UserUtilities.getStatusNames()." +
                    "contains(" + nvpExpected.getName() + ")",
                    found);
        }
        assertEquals("UserUtilities.getStatusNames().size()",
                expected.length, actual.size());
    }

    /**
     * Verified UserUtilities#getPermissionNames()
     */
    @Test
    public void testGetPermissionNamesDefault() {
        doTestGetPermissionNamesDefault(new NameValuePair[]{
                    new NameValuePair("Project Admin",
                    Integer.toString(UserUtilities.PERMISSION_PRODUCT_ADMIN)),
                    new NameValuePair("Create Issues",
                    Integer.toString(UserUtilities.PERMISSION_CREATE)),
                    new NameValuePair("Edit All Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT)),
                    new NameValuePair("Close Issues",
                    Integer.toString(UserUtilities.PERMISSION_CLOSE)),
                    new NameValuePair("Assign Issues to Self",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_SELF)),
                    new NameValuePair("Assign Issues to Others",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGN_OTHERS)),
                    new NameValuePair("View All Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_ALL)),
                    new NameValuePair("View User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_VIEW_USERS)),
                    new NameValuePair("Edit User's Own Issues",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_USERS)),
                    new NameValuePair("Unassign Issues from Self",
                    Integer.toString(UserUtilities.PERMISSION_UNASSIGN_SELF)),
                    new NameValuePair("Can Be Assigned Any Issue",
                    Integer.toString(UserUtilities.PERMISSION_ASSIGNABLE)),
                    new NameValuePair("Create Issues for Others",
                    Integer.toString(UserUtilities.PERMISSION_CREATE_OTHERS)),
                    new NameValuePair("Full Issue Edit",
                    Integer.toString(UserUtilities.PERMISSION_EDIT_FULL))
                });
    }

    /**
     * Verifies UserUtilities#isSuperUser
     */
    @Test
    public void testIsSuperUser() {
        assertFalse("UserUtilities.isSuperUser(null)",
                UserUtilities.isSuperUser(null));
        final Map<Integer, Set<PermissionType>> permissionMap =
                new HashMap<Integer, Set<PermissionType>>();
        assertFalse("UserUtilities.isSuperUser(permissionMap)",
                UserUtilities.isSuperUser(permissionMap));
        final Set<PermissionType> permissions = new HashSet<PermissionType>();
        permissionMap.put(null, permissions);
        assertFalse("UserUtilities.isSuperUser(permissionMap)",
                UserUtilities.isSuperUser(permissionMap));
        permissions.add(PermissionType.PRODUCT_ADMIN);
        assertFalse("UserUtilities.isSuperUser(permissionMap)",
                UserUtilities.isSuperUser(permissionMap));
        permissions.add(PermissionType.USER_ADMIN);
        assertTrue("UserUtilities.isSuperUser(permissionMap)",
                UserUtilities.isSuperUser(permissionMap));
    }

    /**
     * Verifies UserUtilities#hasPermission(Map<Integer, Set<PermissionType>>,
     * int)
     */
    @Test
    public void testHasPermission() {
        final Map<Integer, Set<PermissionType>> permissionMap =
                new HashMap<Integer, Set<PermissionType>>();
        final Set<PermissionType> permissions = new HashSet<PermissionType>();
        permissionMap.put(null, permissions);
        permissions.add(PermissionType.PRODUCT_ADMIN);
        assertTrue("UserUtilities.hasPermission(permissionMap, " +
                "PERMISSION_PRODUCT_ADMIN)",
                UserUtilities.hasPermission(permissionMap,
                UserUtilities.PERMISSION_PRODUCT_ADMIN));
        assertFalse("UserUtilities.hasPermission(permissionMap, " +
                "PERMISSION_ASSIGN_SELF)",
                UserUtilities.hasPermission(permissionMap,
                UserUtilities.PERMISSION_ASSIGN_SELF));
    }

    /**
     * Verifies UserUtilities#hasPermission(Map<Integer, Set<PermissionType>>,
     * int[])
     */
    @Test
    public void testHasPermissions() {
        final Map<Integer, Set<PermissionType>> permissionMap =
                new HashMap<Integer, Set<PermissionType>>();
        final Set<PermissionType> permissions = new HashSet<PermissionType>();
        permissionMap.put(null, permissions);
        permissions.add(PermissionType.PRODUCT_ADMIN);
        assertTrue("UserUtilities.hasPermission(permissionMap, " +
                "{PERMISSION_PRODUCT_ADMIN})",
                UserUtilities.hasPermission(permissionMap,
                new int[]{
                    UserUtilities.PERMISSION_PRODUCT_ADMIN
                }));
        assertTrue("UserUtilities.hasPermission(permissionMap, " +
                "{PERMISSION_USER_ADMIN, " +
                "PERMISSION_PRODUCT_ADMIN, " +
                "PERMISSION_CLOSE}",
                UserUtilities.hasPermission(permissionMap,
                new int[]{
                    UserUtilities.PERMISSION_USER_ADMIN,
                    UserUtilities.PERMISSION_PRODUCT_ADMIN,
                    UserUtilities.PERMISSION_CLOSE
                }));
    }

    /**
     * Verifies UserUtilities#hasPermission(Map<Integer, Set<PermissionMap>>,
     * int, int)
     */
    @Test
    public void testHasPermissionsOnProject() {
        final Map<Integer, Set<PermissionType>> permissionMap =
                new HashMap<Integer, Set<PermissionType>>();
        final Set<PermissionType> permissions = new HashSet<PermissionType>();
        permissionMap.put(10, permissions);
        permissions.add(PermissionType.PRODUCT_ADMIN);
        assertTrue(UserUtilities.hasPermission(permissionMap, 10,
                UserUtilities.PERMISSION_PRODUCT_ADMIN));
        assertFalse(UserUtilities.hasPermission(permissionMap, 11,
                UserUtilities.PERMISSION_PRODUCT_ADMIN));
        assertFalse(UserUtilities.hasPermission(permissionMap, 10,
                UserUtilities.PERMISSION_ASSIGNABLE));
    }

    /**
     * Defines a set of datafiles to be uploaded into database.
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                    "dataset/languagebean_dataset.xml"
                };
    }

    /**
     * Defines a simple configuration, required for running tests.
     * @return an array of references to configuration files.
     */
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }
}
