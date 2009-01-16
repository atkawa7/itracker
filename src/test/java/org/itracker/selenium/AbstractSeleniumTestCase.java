package org.itracker.selenium;

import com.thoughtworks.selenium.Selenium;
import java.io.IOException;
import org.itracker.AbstractDependencyInjectionTest;

/**
 * It is a base class for all Selenium-based test case.
 * It performa initialization of Selenium client part and
 * retrieves some generally-used parameters like hose application
 * is running at, port, context.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public abstract class AbstractSeleniumTestCase extends AbstractDependencyInjectionTest {
    public final static String SE_TIMEOUT = "60000";
    
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
}
