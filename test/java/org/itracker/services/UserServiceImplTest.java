package org.itracker.services;

import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.User;

public class UserServiceImplTest extends AbstractDependencyInjectionTest {

    private UserService userService;

    public void testGetSuperUsers() {

        List<User> users = userService.getSuperUsers();
        
        assertNotNull( users );

        assertUsersEqual( users, 2, "admin_test1" );

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
