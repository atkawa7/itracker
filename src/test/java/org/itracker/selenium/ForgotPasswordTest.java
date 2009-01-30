package org.itracker.selenium;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.Test;

/**
 * Verifies the ability retrieve/reset forgotten password.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ForgotPasswordTest extends AbstractSeleniumTestCase {
    /**
     * 1. Go to the Login Page.
     * 2. Leave all input fields (login and password) empty.
     * 3. Click Login button.
     * 4. Wait for page reload.
     * 5. Check that "Login is required" and "Last Name is required"
     *    message appeared.
     * @throws java.lang.Exception
     */
    @Test
    public void testIfBothRequired() throws Exception {
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        selenium.click("name=forgotpassword");//("link=Forgot My Password");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("lastName"));
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals("Login is required\n Last Name is required",
                selenium.getText("//span"));
    }
    
    /**
     * 1. Go to the Login Page.
     * 2. Type something into Last Name input field but leave Login empty.
     * 3. Click Login button.
     * 4. Wait for page reload.
     * 5. Check that "Login is required" message has appeared.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testIfLoginRequired() throws Exception {
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        selenium.click("name=forgotpassword");//("link=Forgot My Password");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("lastName"));
        selenium.type("lastName", "user");
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals("Login is required", selenium.getText("//span"));
    }
    
    /**
     * 1. Go to the Login Page.
     * 2. Type something into Login input field but leave Last Name empty.
     * 3. Click Login button.
     * 4. Wait for page reload.
     * 5. Check that "Last Name is required" message has appeared.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testIfLastNameRequired() throws Exception {
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        selenium.click("name=forgotpassword");//("link=Forgot My Password");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        selenium.type("login", "user");
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals("Last Name is required", selenium.getText("//span"));
    }
    
    /**
     * 1. Go to the Login Page.
     * 2. Click "Forgot Password" link.
     * 3. Wait for page reload.
     * 4. Type valid login into Login input field.
     * 5. Type valid last name into Last Name input field.
     * 6. Click "Submit" button.
     * 7. Wait for page reload.
     * 8. Check that you was forwarded back to the Login Page.
     * 9. Obtain email message from locally started mail server.
     * 10. Extract a new password from there.
     * 11. Redo a usual login procedure with a new password and make sure
     *     you can authorize to the system with it.
     * @throws java.lang.Exception
     */
    @Test
    public void testRetrievingForgottenPassword() throws Exception {
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("name=forgotpassword"));
        selenium.click("name=forgotpassword");//("link=Forgot My Password");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertTrue(selenium.isElementPresent("login"));
        selenium.type("login", "user_test1");
        assertTrue(selenium.isElementPresent("lastName"));
        selenium.type("lastName", "user lastname");
        assertTrue(selenium.isElementPresent("//input[@type='submit']"));
        assertEquals(smtpServer.getReceivedEmailSize(), 0);
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("password"));
        assertEquals(smtpServer.getReceivedEmailSize(), 1);
        final SmtpMessage smtpMessage = (SmtpMessage)smtpServer.getReceivedEmail().next();
        final String smtpMessageBody = smtpMessage.getBody();
        assertTrue(smtpMessageBody.contains("Password: "));
        final String newPassword = smtpMessageBody
                .replaceAll("\n", "").replaceFirst(".*Password: ", "");
        smtpServer.stop();
        
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("login"));
        selenium.type("login", "user_test1");
        assertTrue(selenium.isElementPresent("password"));
        selenium.type("password", newPassword);
        assertTrue(selenium.isElementPresent("//input[@type='submit']"));
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertTrue(selenium.isElementPresent("id"));
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
