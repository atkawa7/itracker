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

    public SeleniumPreTest() throws IOException {

            final InputStream inputStream = SeleniumManager.class
                    .getResourceAsStream("SeleniumManager.properties");
            final Properties properties = new Properties();
            properties.load(inputStream);

    }

    @Test
    public void testSmtpPortAvailable() throws Exception {
        connectSocket("localhost", AbstractSeleniumTestCase.SMTP_PORT);
    }
    @Test
    public void testApplicationPortAvailable() throws Exception {
        connectSocket(SeleniumManager.getApplicationHost(), SeleniumManager.getApplicationPort());
    }
    @Test
    public void testJettyPortsAvailable() throws Exception {
        connectSocket("localhost", 9966);
    }
    @Test
    public void testSeleniumPortAvailable() throws Exception {
        connectSocket(SeleniumManager.getSeleniumHost(), SeleniumManager.getSeleniumPort());
    }

    private void connectSocket(String host, int i) throws IOException {
        try {
            log.info("checking port " + i + " on " + host);
            new Socket(host, i);
            fail("Socket is open cannot run selenium tests." + host + ":" + i);
        } catch (IOException e) {
            log.info("OK port " + i + " on " + host);
        }
    }
}
