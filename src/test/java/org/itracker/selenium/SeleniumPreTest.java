package org.itracker.selenium;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
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

    }

    @Test
    public void testSmtpPortAvailable() throws Exception {
        connectSocket("localhost", AbstractSeleniumTestCase.SMTP_PORT);
    }


    @Test
    public void testLocalhost() throws Exception {
        // only run the selenium on localhost..
        // remote-host for selenium could run externally, not tested to work.
        // Selenium starts locally from maven anyways.
        assertEquals("application.host", "localhost", SeleniumManager.getApplicationHost());
        connectSocket("localhost", SeleniumManager.getSeleniumPort());
    }

    @Test
    public void testJettyPortsAvailable() throws Exception {
        connectSocket(SeleniumManager.getApplicationHost(), SeleniumManager.getApplicationPort());
        // Stop-port
        connectSocket(SeleniumManager.getApplicationHost(), 9966);
    }

    @Test
    public void testSeleniumPortAvailable() throws Exception {
        connectSocket(SeleniumManager.getSeleniumHost(), SeleniumManager.getSeleniumPort());
    }

    /**
     * Port should be open for a remote-service, or available on localhost.
     *
     * @param host the host
     * @param i the port
     * @throws Exception when failed
     */
    private void connectSocket(String host, int i) throws Exception {
        final boolean localhost = StringUtils.equalsIgnoreCase("localhost", host);
        try {
            log.info("checking port " + i + " on " + host);
            new Socket(host, i);
            if (localhost) {
                fail("Socket is open cannot run selenium tests on " + host + ":" + i);
            }
        } catch (IOException e) {
            if (!localhost) {
                fail("Remote socket is not open, cannot run selenium tests on" + host + ":" + i);
            }
        }
    }
}
