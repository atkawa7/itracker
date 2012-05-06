package org.itracker.selenium;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

import static org.junit.Assert.fail;
/**
 * Created by IntelliJ IDEA.
 * User: masta
 * Date: 15.04.12
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public class SeleniumPreTest {


    private static final Logger log = Logger.getLogger(SeleniumPreTest.class);
    private String seleniumHost;
    private Integer seleniumPort;
    private String applicationHost;
    private Integer applicationPort;


    private final static String PROPERTY_SELENIUM_HOST = "selenium.host";
    private final static String PROPERTY_SELENIUM_PORT = "selenium.port";
    private final static String PROPERTY_APPLICATION_HOST = "application.host";
    private final static String PROPERTY_APPLICATION_PORT = "application.port";

    public SeleniumPreTest() throws IOException {

            final InputStream inputStream = SeleniumManager.class
                    .getResourceAsStream("SeleniumManager.properties");
            final Properties properties = new Properties();
            properties.load(inputStream);

            seleniumHost =
                    properties.getProperty(PROPERTY_SELENIUM_HOST, "localhost");
            seleniumPort =
                    Integer.valueOf(properties.getProperty(PROPERTY_SELENIUM_PORT, "5555"));
            applicationHost =
                    properties.getProperty(PROPERTY_APPLICATION_HOST, "localhost");
            applicationPort =
                    Integer.valueOf(properties.getProperty(PROPERTY_APPLICATION_PORT, "8888"));

    }
    @Test
    public void testPortsAvailable() throws Exception {
        connectSocket("localhost", AbstractSeleniumTestCase.SMTP_PORT);
        connectSocket("localhost", 9966);
        // TODO wrong ports returned, 8080..
//        connectSocket(SeleniumManager.getSeleniumHost(), SeleniumManager.getSeleniumPort());
//        connectSocket(SeleniumManager.getApplicationHost(), SeleniumManager.getApplicationPort());
    }

    private void connectSocket(String host, int i) throws IOException {
        try {
            log.info("checking port " + i + " on " + host);
            new Socket(host, i);
            fail("Socket is open cannot run selenium tests." + host + ":" + i);
        } catch (IOException e) {
            log.info("OK port " + i + " on " + host);
        }
//        new ServerSocket(i);

    }
}
