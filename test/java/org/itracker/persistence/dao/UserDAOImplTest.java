package org.itracker.persistence.dao;

import java.util.Date;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.User;
import org.itracker.services.util.UserUtilities;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class UserDAOImplTest extends AbstractDependencyInjectionTest {

    private UserDAO userDAO;

    @Test
    public void createUser() {

        User user = new User( "admin_test2","admin_test2", "admin firstname2", "admin lastname2", "", true );
        user.setCreateDate( new Date() );
        user.setLastModifiedDate(new Date());
        userDAO.saveOrUpdate( user );

        User foundUser = userDAO.findByLogin( "admin_test2" );

        assertNotNull( foundUser );
        assertEquals( "admin_test2", foundUser.getPassword() );
        assertEquals( "admin firstname2", foundUser.getFirstName() );
        assertEquals( "admin lastname2", foundUser.getLastName() );
        assertTrue( foundUser.isSuperUser() );

    }

    @Test
    public void createUserWithNotNullPK() {

        try {
            User user = new User( "admin_test3","admin_test3", "admin firstname3", "admin lastname3", "", true );
            user.setId( -1 );
            user.setCreateDate( new Date() );
            userDAO.saveOrUpdate( user );
        } catch( Exception e ) {
            // Expected behavior
            assertTrue( e instanceof DataIntegrityViolationException);
        }

    }

    @Test
    public void testFindUsersForProjectByAllPermissionTypeList() {

        Integer[] permissionTypes = new Integer[]{
                UserUtilities.PERMISSION_PRODUCT_ADMIN,
                UserUtilities.PERMISSION_CREATE,
                UserUtilities.PERMISSION_EDIT
        };

        List<User> users = userDAO.findUsersForProjectByAllPermissionTypeList(2, permissionTypes);

        assertEquals(2, users.size());

        assertContainsUser(userDAO.findByPrimaryKey(2), users);
        assertContainsUser(userDAO.findByPrimaryKey(3), users);

    }

    private void assertContainsUser(User user, List<User> users) {

        if (!users.contains(user)) {
            fail("User not found in the list.");
        }

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        userDAO = (UserDAO)applicationContext.getBean( "userDAO" );
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
