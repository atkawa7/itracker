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
        } catch (final IOException e) {
            log.error(e);
            fail(e.getMessage());
        }
    }

//    @Override
//    public void onTearDown() throws Exception {
//
//        super.onTearDown();
//        log.info("onTearDown: stopping smtp");
//        stopSMTP();
//        log.info("onTearDown: stopped smtp");
//
//
//    }
//
//    @Override
//    public void onSetUp() throws Exception {
//        log.debug("onSetUp: starting smtp");
//        startSMTP();
//        log.info("onSetUp: started smtp");
//
//        super.onSetUp();
//    }

    protected Wiser startSMTP() throws InterruptedException {
//        int c = 0;
//        log.debug("Starting smtp");
//        if (null != wiser && wiser.getServer().isRunning()) {
//            log.warn("Already running smtp");
//            stopSMTP();
//            log.debug("Already running smtp stopped");
//        }
//        log.debug("Wiser.start(" + SMTP_PORT + ")");
//
//        wiser = new Wiser(SMTP_PORT);
//        wiser.start();
//        log.debug("sleep 500");
//        Thread.currentThread().sleep(500);
//        log.debug("checking running wiser");
//
//        if (!wiser.getServer().isRunning()) {
//            throw new RuntimeException("Could not Start wiser");
//        }
//        log.debug("got running wiser " + wiser);
//
//        log.info("Started wiser");
        return wiser;
    }

    void stopSMTP() {
//        log.info("stopSMTP: skip smtp until working");
//        if (true) return;

//        log.debug("Stopping smtp");
////        assertNotNull("null smtp", wiser);
//        try {
//            wiser.stop();
//            Thread.currentThread().sleep(500);
//            if (wiser.getServer().isRunning()) {
//                throw new RuntimeException("Could not Stop wiser");
//            }
//        } catch (Exception e) {
//            log.warn("failed to close smtp", e);
////            fail("could not stop smtp: " + wiser + ": " + e.getMessage());
//        } finally {
//            wiser = null;
//        }
//
//        log.info("Stopped smtp");
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
