package org.itracker.selenium;

import com.thoughtworks.selenium.Selenium;
import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;
import org.subethamail.wiser.Wiser;

import java.io.IOException;

/**
 * It is a base class for all Selenium-based test ca se.
 * It performa initialization of Selenium client part and
 * retrieves some generally-used parameters like hose application
 * is running at, port, context.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public abstract class AbstractSeleniumTestCase
        extends AbstractDependencyInjectionTest {
    public final static String SE_TIMEOUT = "20000";
    public final static int SMTP_PORT = 2525;
    protected static final Wiser wiser;
    //    protected SMTPServer smtp;
    protected Selenium selenium;
    protected String applicationHost;
    protected int applicationPort;
    protected String applicationPath;
    protected String applicationURL;
    Logger log = Logger.getLogger(getClass());

    static {
        wiser = new Wiser(SMTP_PORT);
        wiser.start();
        Logger.getLogger(AbstractSeleniumTestCase.class).info("started wiser on " + SMTP_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (null != wiser) {
                    try {
                        wiser.stop();
                        Logger.getLogger(getClass()).info("stopped wiser " + wiser);
                    } catch (RuntimeException e) {
                        Logger.getLogger(getClass()).warn("could not stop running wiser: " + wiser);
                        Logger.getLogger(getClass()).debug("exception caught", e);
                    }

                }
            }
        });
    }


    public AbstractSeleniumTestCase() {
        try {
            selenium = SeleniumManager.getSelenium();
            assertNotNull(selenium);
            applicationHost = SeleniumManager.getApplicationHost();
            applicationPort = SeleniumManager.getApplicationPort();
            applicationPath = SeleniumManager.getApplicationPath();
            applicationURL = "http://" + applicationHost + ":" + applicationPort + "/"
                    + applicationPath;
        } catch (final IOException e) {
            log.error(e);
            fail(e.getMessage());
        }
    }

    final void assertElementPresent(String q) {
        assertTrue(selenium.getLocation() + " " + q + " expected present", selenium.isElementPresent(q));
    }

    final void assertElementNotPresent(String q) {
        assertFalse(selenium.getLocation() + " " + q + " expected NOT present", selenium.isElementPresent(q));
    }

    final void assertElementTextEquals(String expected, String q) {
        assertEquals(selenium.getLocation() + " " + q, expected, selenium.getText(q));
    }

    /**
     * This will initialize a new selenium session for this test scope.
     */
    protected void closeSession() {
        SeleniumManager.closeSession(selenium);
    }

    /**
     * Assert being on login page and enter the credentials.
     * <p>Success will be asserted with <code>itracker</code> Cookie present.</p>
     */
    protected final void loginUser(final String username, final String password) {

        log.debug("loginUser called with " + username + ", " + password);

        assertElementPresent("//*[@name='login']");
        assertElementPresent("//*[@name='password']");
        assertElementPresent("//*[@value='Login']");
        selenium.type("//*[@name='login']", username);
        selenium.type("//*[@name='password']", password);
        selenium.click("//*[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertNotNull("Login failed: 'itracker' Cookie was not found", selenium.getCookieByName("itracker"));

        log.debug("loginUser, logged in " + username + ", cookies: " + selenium.getCookie());
    }
}
