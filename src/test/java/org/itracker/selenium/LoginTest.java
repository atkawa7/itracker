package org.itracker.selenium;

import org.junit.Test;

import java.io.IOException;

/**
 * Verifies authorization to the system functionality.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class LoginTest extends AbstractSeleniumTestCase {


    /**
     * Verifies the successfull login case with valid login/password.
     * <p/>
     * 1. Opens login page of an application.
     * 2. Enters correct login and password into appropriate input fields.
     * 3. Clicks "Login" button.
     * 4. Waits for page reload.
     * 5. Verifies if page contains ticket id input field, which means we
     * are inside an application.
     */
    @Test
    public void testLoginSuccessDefaultAdmin() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        loginUser("admin_test1", "admin_test1");

        assertTrue(selenium.isElementPresent("xpath=//*[@name='id']"));
    }

    /**
     * Verifies login failure case with invalid login/password.
     * <p/>
     * 1. Opens login page of an application.
     * 2. Enters incorrect login and password into appropriate input fields.
     * 3. Clicks "Login" button.
     * 4. Waits for page reload.
     * 5. Verifies if page contains ticket id input field, which means we
     * are inside an application.
     */
    @Test
    public void testLoginFailure() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        try {
            loginUser("wrong_login", "wrong_password");
        } catch (Exception e) {

        } finally {
            assertFalse(selenium.isElementPresent("xpath=//*[@name='id']"));
        }

    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_init_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml"
        };
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }
}
