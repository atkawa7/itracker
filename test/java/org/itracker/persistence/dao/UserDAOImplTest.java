package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.User;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;

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

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        userDAO = (UserDAO)applicationContext.getBean( "userDAO" );
    }

    protected String[] getDataSetFiles() {
        return new String[] {
                "dataset/userbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[] { "application-context.xml" };
    }

}
