package org.itracker.persistence.dao;

import java.util.Date;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.User;

public class UserDAOImplTest extends AbstractDependencyInjectionTest {

    private UserDAO userDAO;

    public void testHelloWorld() {

        User user = new User( "admin_test2","admin_test2", "admin firstname2", "admin lastname2", "", true );
        user.setCreateDate( new Date() );
        userDAO.saveOrUpdate( user );

        User foundUser = userDAO.findByLogin( "admin_test2" );

        assertNotNull( foundUser );
        assertEquals( "admin_test2", foundUser.getPassword() );
        assertEquals( "admin firstname2", foundUser.getFirstName() );
        assertEquals( "admin lastname2", foundUser.getLastName() );
        assertTrue( foundUser.isSuperUser() );

    }

    protected void onSetUp() throws Exception {
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
