package org.itracker.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;
import org.apache.log4j.Logger;

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
    private final static String PROPERTY_APPLICATION_HOST = "application.host";
    private final static String PROPERTY_APPLICATION_PORT = "application.port";
    private final static String PROPERTY_APPLICATION_PATH = "application.path";

    private static Selenium selenium = null;

    private static String seleniumHost = null;
    private static int seleniumPort = 4444;
    private static String seleniumBrowser = null;
    private static String applicationHost = null;
    private static int applicationPort = 8080;
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
                properties.getProperty(PROPERTY_SELENIUM_BROWSER, "*firefox");
        seleniumHost =
                properties.getProperty(PROPERTY_SELENIUM_HOST, "localhost");
        seleniumPort =
                Integer.valueOf(properties.getProperty(PROPERTY_SELENIUM_PORT, "5555"));
        applicationHost =
                properties.getProperty(PROPERTY_APPLICATION_HOST, "localhost");
        applicationPort =
                Integer.valueOf(properties.getProperty(PROPERTY_APPLICATION_PORT, "8888"));
        applicationPath =
                properties.getProperty(PROPERTY_APPLICATION_PATH, "itracker");
    }

    public static Selenium getSelenium() throws IOException {
        if (null == selenium) {
            log.info("starting new selenium");

            selenium = new DefaultSelenium(seleniumHost, seleniumPort,
                    seleniumBrowser,
                    "http://" + applicationHost + ":" + applicationPort + "/"
                            + applicationPath);
            selenium.start();
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
