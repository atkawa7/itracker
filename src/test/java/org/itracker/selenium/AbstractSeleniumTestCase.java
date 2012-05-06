package org.itracker.selenium;

import com.dumbster.smtp.SimpleSmtpServer;
import com.thoughtworks.selenium.Selenium;
import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * It is a base class for all Selenium-based test ca se.
 * It performa initialization of Selenium client part and
 * retrieves some generally-used parameters like hose application
 * is running at, port, context.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public abstract class AbstractSeleniumTestCase extends AbstractDependencyInjectionTest {
    public final static String SE_TIMEOUT = "20000";
    public final static int SMTP_PORT = 2525;
    protected SimpleSmtpServer smtpServer;
    protected Selenium selenium;
    protected String applicationHost;
    protected int applicationPort;
    protected String applicationPath;
    Logger log = Logger.getLogger(getClass());

    public AbstractSeleniumTestCase() {
        try {
            selenium = SeleniumManager.getSelenium();
            assertNotNull(selenium);
            applicationHost = SeleniumManager.getApplicationHost();
            applicationPort = SeleniumManager.getApplicationPort();
            applicationPath = SeleniumManager.getApplicationPath();
        } catch (final IOException e) {
            log.error(e);
            fail(e.getMessage());
        }
    }

//    @Override
//    public void onTearDown() throws Exception {
//
//            log.info("onTearDown: stopping smtp");
//            stopSMTP();
//            log.info("onTearDown: stopped smtp");
//
//            super.onTearDown();
//
//    }
//
//    @Override
//    public void onSetUp() throws Exception {
//        super.onSetUp();
//        log.info("onSetUp: starting smtp");
//        startSMTP();
//        log.info("onSetUp: started smtp");
//    }

    SimpleSmtpServer startSMTP() throws InterruptedException {
        log.info("startSMTP: skip smtp until working");
        if (true) return null;


        log.info("Starting smtp");
        if (null != smtpServer && !smtpServer.isStopped()) {
            log.warn("Already running smtp");
            stopSMTP();
            log.info("Already running smtp stopped");
        }
        smtpServer = null;
        try {
            ServerSocket s = new ServerSocket(SMTP_PORT);
            log.info("sleep 100");
            Thread.currentThread().sleep(100);
            log.info("close socket");
            s.close();
            log.info("sleep 100");
            Thread.currentThread().sleep(100);
        } catch (IOException ioe) {
            fail("Socket " + SMTP_PORT + " is open: " + ioe.getMessage());
        }
        log.info("SimpleSmtpServer.start("+SMTP_PORT+")");
        SimpleSmtpServer smtp = SimpleSmtpServer.start(SMTP_PORT);
        log.info("sleep 300");
        Thread.currentThread().sleep(300);

        log.info("checking running smtp");
//        assertNotNull("smtp is null", smtp);
        if (smtp.isStopped()) {
            throw new RuntimeException("Could not Start smtpServer");
        }

        log.info("got running smtp " + smtp);
        smtpServer = smtp;
        log.info("Started smtp");
        return smtp;
    }

    void stopSMTP() {
        log.info("stopSMTP: skip smtp until working");
        if (true) return;

        log.info("Stopping smtp");
//        assertNotNull("null smtp", smtpServer);
        try {
            smtpServer.stop();
            Thread.currentThread().sleep(300);
            if (!smtpServer.isStopped()) {
                throw new RuntimeException("Could not Stop smtpServer");
            }
        } catch (Exception e) {
            log.warn("failed to close smtp", e);
//            fail("could not stop smtp: " + smtpServer + ": " + e.getMessage());
        } finally {
            smtpServer = null;
        }

        log.info("Stopped smtp");
    }

    final void assertElementPresent(String q) {
        assertTrue(selenium.getLocation() + "#" + q + " expected present", selenium.isElementPresent(q));
    }

    final void assertTextEquals(String expected, String q) {
        assertEquals(selenium.getLocation() + "#" + q, expected, selenium.getText(q));
    }

    /**
     * This will initialize a new selenium session for this test scope.
     */
    protected void closeSession() {
        SeleniumManager.closeSession(selenium);
        assertElementPresent("login");
    }
}
