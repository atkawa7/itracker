package org.itracker.selenium;

import org.junit.Test;

import java.io.IOException;

/**
 * Verifies security issues - that user, leaving a system cannot
 * access its content anymore.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class LogoutTest extends AbstractSeleniumTestCase {
    /**
     * Verifies the successfull login case with valid login/password.
     * 
     * 0. Exit all available http sessions.
     * 1. Open some page inside the system.
     * 2. Verify, that browser was forwarded to login page.
     * 3. Enters correct login and password into appropriate input fields.
     * 4. Clicks "Login" button.
     * 5. Waits for page reload.
     * 6. Verifies if page contains ticket id input field, which means we
     * are inside an application.
     * 7. Verify if page contains Logout link and click it.
     * 8. After page refresh, verify that it's login page.
     * 9. Again, try to access some page inside the system.
     * 10. Verify, that browser has been forwarded to login page again.
     * @throws java.io.IOException
     */
    @Test
    public void testLoginAndLogout() throws IOException {
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/portalhome.do");

        assertFalse(selenium.isElementPresent("id"));
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("password"));
        assertTrue(selenium.isElementPresent("xpath=//.[@type='submit']"));
        selenium.type("login", "user_test1");
        selenium.type("password", "user_test1");
        selenium.click("xpath=//.[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertTrue(selenium.isElementPresent("id"));
        assertTrue(selenium.isElementPresent("logoff"));
        selenium.click("logoff");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertFalse(selenium.isElementPresent("id"));
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("password"));
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/portalhome.do");

        assertFalse(selenium.isElementPresent("id"));
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("password"));
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
        return new String[]{ "application-context.xml"};
    }
}
