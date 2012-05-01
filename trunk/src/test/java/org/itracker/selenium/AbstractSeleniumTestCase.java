package org.itracker.selenium;

import com.thoughtworks.selenium.Selenium;
import org.itracker.AbstractDependencyInjectionTest;

import java.io.IOException;

/**
 * It is a base class for all Selenium-based test case.
 * It performa initialization of Selenium client part and
 * retrieves some generally-used parameters like hose application
 * is running at, port, context.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public abstract class AbstractSeleniumTestCase extends AbstractDependencyInjectionTest {
    public final static String SE_TIMEOUT = "600";

    protected Selenium selenium;
    protected String applicationHost;
    protected int applicationPort;
    protected String applicationPath;

    public AbstractSeleniumTestCase() {
        try {
            selenium = SeleniumManager.getSelenium();
            assertNotNull(selenium);
            applicationHost = SeleniumManager.getApplicationHost();
            applicationPort = SeleniumManager.getApplicationPort();
            applicationPath = SeleniumManager.getApplicationPath();
        } catch (final IOException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /**
     * This will initialize a new selenium session for this test scope.
     */
    protected void closeSession() {
        SeleniumManager.closeSession(selenium);
    }
}
