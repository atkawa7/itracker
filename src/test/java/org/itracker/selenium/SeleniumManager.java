package org.itracker.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class SeleniumManager {
    private final static String PROPERTY_SELENIUM_BROWSER = "selenium.browser";
    private final static String PROPERTY_SELENIUM_HOST = "selenium.host";
    private final static String PROPERTY_SELENIUM_PORT = "selenium.port";
    private final static String PROPERTY_SELENIUM_BROWSER_LOG_LEVEL = "selenium.browserLogLevel";
    private final static String PROPERTY_SELENIUM_SPEED = "selenium.speed";
    private final static String PROPERTY_SELENIUM_USE_XPATH_LIBRARY = "selenium.useXPathLibrary";
    private final static String PROPERTY_APPLICATION_HOST = "application.host";
    private final static String PROPERTY_APPLICATION_PORT = "application.port";
    private final static String PROPERTY_APPLICATION_PATH = "application.path";


    private final static String PROPERTY_SELENIUM_BROWSER_DEFAULT = "*firefox";
    private final static String
            PROPERTY_SELENIUM_HOST_DEFAULT = "localhost";
    private final static String
            PROPERTY_SELENIUM_PORT_DEFAULT = "5555";
    private final static String
            PROPERTY_APPLICATION_HOST_DEFAULT = "localhost";
    private final static String
            PROPERTY_APPLICATION_PORT_DEFAULT = "8888";
    private final static String
            PROPERTY_APPLICATION_PATH_DEFAULT = "itracker";
    private final static String
            PROPERTY_SELENIUM_BROWSER_LOG_LEVEL_DEFAULT = "error";
    private final static String
            PROPERTY_SELENIUM_SPEED_DEFAULT = "0";
    private final static String
            PROPERTY_SELENIUM_USE_XPATH_LIBRARY_DEFAULT = "javascript-xpath";


    private static Selenium selenium = null;

    private static String seleniumHost = null;
    private static Integer seleniumPort = null;
    private static String seleniumBrowser = null;
    private static String seleniumBrowserLogLevel = null;
    private static String seleniumSpeed = null;
    private static String seleniumUseXPathLibrary = null;

    private static String applicationHost = null;
    private static Integer applicationPort = null;
    private static String applicationPath = null;

    private static final Logger log = Logger.getLogger(SeleniumManager.class);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (null != SeleniumManager.selenium) {
                    try {
                        SeleniumManager.selenium.stop();
                    } catch (SeleniumException e) {
                        log.warn("could not stop running selenium: " + selenium);
                        log.debug("exception caught", e);
                    }

                }
            }
        });
    }

    static {
        final InputStream inputStream = SeleniumManager.class
                .getResourceAsStream("SeleniumManager.properties");
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        seleniumBrowser =
                properties.getProperty(PROPERTY_SELENIUM_BROWSER, PROPERTY_SELENIUM_BROWSER_DEFAULT);
        seleniumHost =
                properties.getProperty(PROPERTY_SELENIUM_HOST, PROPERTY_SELENIUM_HOST_DEFAULT);
        seleniumPort =
                Integer.valueOf(properties.getProperty(PROPERTY_SELENIUM_PORT, PROPERTY_SELENIUM_PORT_DEFAULT));
        applicationHost =
                properties.getProperty(PROPERTY_APPLICATION_HOST, PROPERTY_APPLICATION_HOST_DEFAULT);
        applicationPort =
                Integer.valueOf(properties.getProperty(PROPERTY_APPLICATION_PORT, PROPERTY_APPLICATION_PORT_DEFAULT));
        applicationPath =
                properties.getProperty(PROPERTY_APPLICATION_PATH, PROPERTY_APPLICATION_PATH_DEFAULT);
        seleniumBrowserLogLevel =
                properties.getProperty(PROPERTY_SELENIUM_BROWSER_LOG_LEVEL, PROPERTY_SELENIUM_BROWSER_LOG_LEVEL_DEFAULT);
        seleniumSpeed =
                properties.getProperty(PROPERTY_SELENIUM_SPEED, PROPERTY_SELENIUM_SPEED_DEFAULT);
        seleniumUseXPathLibrary =
                properties.getProperty(PROPERTY_SELENIUM_USE_XPATH_LIBRARY, PROPERTY_SELENIUM_USE_XPATH_LIBRARY_DEFAULT);
    }

    public static Selenium getSelenium() throws IOException {
        if (null == selenium) {
            log.info("starting new selenium");
            final String seleniumUrl = "http://" + applicationHost + ":" + applicationPort + "/"
                    + applicationPath;
            if (seleniumBrowser.equals("htmlunit")) {
                // TODO FIXME not working yet due to javascript issues
                WebDriver driver = new HtmlUnitDriver(true);
                selenium = new WebDriverBackedSelenium(driver, seleniumUrl);
            } else {
                selenium = new DefaultSelenium(seleniumHost, seleniumPort,
                        seleniumBrowser,
                        seleniumUrl);
            }

            selenium.start();
            selenium.setBrowserLogLevel(seleniumBrowserLogLevel);
            selenium.setSpeed(seleniumSpeed);
            selenium.useXpathLibrary(seleniumUseXPathLibrary);

        }
        return selenium;
    }

    /**
     * This will initialize a new selenium session for this test scope.
     */
    protected static void closeSession(Selenium selenium) {
        if (log.isDebugEnabled()) {
            log.debug("closeSession: " + selenium);
        }
        selenium.deleteAllVisibleCookies();
    }

    public static String getSeleniumHost() {
        return seleniumHost;
    }

    public static int getSeleniumPort() {
        return seleniumPort;
    }

    public static String getSeleniumBrowser() {
        return seleniumBrowser;
    }

    public static String getApplicationHost() {
        return applicationHost;
    }

    public static int getApplicationPort() {
        return applicationPort;
    }

    public static String getApplicationPath() {
        return applicationPath;
    }
}
