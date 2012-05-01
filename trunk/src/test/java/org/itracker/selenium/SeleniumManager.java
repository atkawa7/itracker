package org.itracker.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jfree.util.Log;

/**
 *
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
    
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (null != SeleniumManager.selenium) {
                    SeleniumManager.selenium.stop();
                }
            }
        });
    }
    
    public static Selenium getSelenium() throws IOException {
        if (null == selenium) {
            final InputStream inputStream = SeleniumManager.class
                    .getResourceAsStream("SeleniumManager.properties");
            final Properties properties = new Properties();
            properties.load(inputStream);
            seleniumBrowser =
                    properties.getProperty(PROPERTY_SELENIUM_BROWSER, "*firefox");
            seleniumHost =
                    properties.getProperty(PROPERTY_SELENIUM_HOST, "localhost");
            seleniumPort =
                    Integer.valueOf(properties.getProperty(PROPERTY_SELENIUM_PORT, "5555"));
            applicationHost =
                    properties.getProperty(PROPERTY_APPLICATION_HOST, "localhost");
            applicationPort =
                    Integer.valueOf(properties.getProperty(PROPERTY_APPLICATION_PORT, "8080"));
            applicationPath =
                    properties.getProperty(PROPERTY_APPLICATION_PATH, "itracker");
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
    	if (Log.isDebugEnabled()) {
    		Log.debug("closeSession: " + selenium);
    	}
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/logoff.do");
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
