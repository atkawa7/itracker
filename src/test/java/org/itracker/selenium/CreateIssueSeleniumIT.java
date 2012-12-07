package org.itracker.selenium;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.itracker.model.User;
import org.itracker.persistence.dao.UserDAO;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

/**
 * Verifies the functionality of new issue creation.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class CreateIssueSeleniumIT extends AbstractSeleniumTestCase {

    private static final Logger log = Logger.getLogger(CreateIssueSeleniumIT.class);

    /**
     * 1. Login into the system with some particular user
     * (admin_test1 in our case, which already has 4 issues, 2 of them
     * are unassigned).
     * 2. Go to "Project List" page (by clicking "Project List" link).
     * 3. Click "Create" link for selected project.
     * 4. Fill issue fields with some data (we remember description
     * in our case, to check if new issue has appeared).
     * 5. After submitting new issue, we are at "View Issues" page and
     * check if new issue has appeared.
     * 6. Go to "Portal Home" page and check if new issue has appeared
     * in "Unassigned" area and "Created" area.
     */
    @Test
    public void testCreateUnassignedIssue() throws Exception {
        log.info(" running testCreateUnassignedIssue");
        closeSession();

        final String descriptionValue = "Issue to be unassigned.";
        final String historyValue = "Issue to be unassigned history.";

        selenium.open(applicationURL);

        loginUser("admin_test1", "admin_test1");

        // Click "Projects List".
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        // Click issue creation link (usually it's named "Create").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]" +
                "/td[3][text()='test_name']/../td[1]/a[2]"));
        selenium.click("//tr[starts-with(@id, 'project.')]" +
                "/td[3][text()='test_name']/../td[1]/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("creatorId"));
        assertTrue(selenium.isElementPresent("severity"));
        assertTrue(selenium.isElementPresent("versions"));

        selenium.type("//td[@id='description']/input", descriptionValue);
        selenium.type("history", historyValue);

        selenium.select("//td[@id='ownerId']/select", "value=-1");
        final UserDAO userDao = (UserDAO) applicationContext.getBean("userDAO");
        final User user = userDao.findByLogin("admin_test1");
        assertTrue(null != user);
        final long userId = user.getId();
        selenium.select("//td[@id='creatorId']/select", "value=" + userId);

        int received = wiser.getMessages().size();

        selenium.click("//td[@id='submit']/input");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertEquals("wiser.receivedEmailSize", received + 1, wiser.getMessages().size());
        final WiserMessage smtpMessage = wiser.getMessages().get(received);
        final String smtpMessageBody = (String) smtpMessage.getMimeMessage().getContent();


        log.debug("testCreateUnassignedIssue, received:\n" + smtpMessageBody);
        // as defined in jetty-env.xconf
        final String systemURL = applicationURL + "/env";
        assertTrue("System URL not contained in Message body, " + systemURL + ", " + smtpMessageBody,
                StringUtils.containsIgnoreCase(smtpMessageBody, systemURL));

        assertTrue("Description not contained in Message body, " + descriptionValue,
                smtpMessageBody.contains(descriptionValue));
        assertTrue("History not contained in Message body," + historyValue,
                smtpMessageBody.contains(historyValue));

        // Check that the total number of issues is 5 now (4 from db + 1 our).
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(5, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]" +
                "/td[11][text()='" + descriptionValue + "']"));

        selenium.open(applicationURL + "/portalhome.do");

        // Check that just created issue has appeared in "Unassigned" area.
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id,'unassignedIssue.')]" +
                "/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));

        // Check that just created issue has appeared in "Created" area.
        assertTrue(selenium.isElementPresent("xpath=//tr[starts-with(@id,'createdIssue.')]" +
                "/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));

        // Check that number of watched items is 0.
        assertFalse("unexpected watchedIssue",
                selenium.isElementPresent("//tr[starts-with(@id, 'watchedIssue.')]"));
    }

    /**
     * TODO
     */
    @Test
    public void testCreateAssignedIssue() throws Exception {

        final String descriptionValue = "Issue to be assigned.";
        final String historyValue = "Issue to be assigned history.";

        log.info(" running testCreateAssignedIssue");
        closeSession();

        selenium.open(applicationURL);

        loginUser("admin_test1", "admin_test1");

        // Clicking "Project List" link.
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        // Click issue creation link (usually it's named "Create").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]" +
                "/td[3][text()='test_name']/../td[1]/a[2]"));
        selenium.click("//tr[starts-with(@id, 'project.')]" +
                "/td[3][text()='test_name']/../td[1]/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("creatorId"));
        assertTrue(selenium.isElementPresent("severity"));
        assertTrue(selenium.isElementPresent("versions"));


        selenium.type("//input[@name='description']", descriptionValue);
        selenium.type("history", historyValue);
        final UserDAO userDao = (UserDAO) applicationContext.getBean("userDAO");
        final User user = userDao.findByLogin("admin_test1");
        assertTrue(null != user);
        final long userId = user.getId();
        selenium.select("//td[@id='ownerId']/select", "value=" + userId);
        selenium.select("//td[@id='creatorId']/select", "value=" + userId);


        int received = wiser.getMessages().size();
        selenium.click("//td[@id='submit']/input");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertEquals("wiser.receivedEmailSize", received + 2, wiser.getMessages().size());
        final WiserMessage smtpMessage1 = wiser.getMessages().get(received);
        final WiserMessage smtpMessage2 = wiser.getMessages().get(received + 1);

        final String smtpMessageBody1 = (String) smtpMessage1.getMimeMessage().getContent();
        final String smtpMessageBody2 = (String) smtpMessage2.getMimeMessage().getContent();

        // Checking email notification for creator.
        log.debug("testCreateAssignedIssue, received:\n " + smtpMessageBody1);
        assertTrue(smtpMessageBody1.contains(descriptionValue));
        assertTrue(smtpMessageBody1.contains(historyValue));

        // Checking email notification for owner.
        log.debug("testCreateAssignedIssue, received2:\n " + smtpMessageBody2);
        assertTrue(smtpMessageBody2.contains(descriptionValue));
        assertTrue(smtpMessageBody2.contains(historyValue));
//
        // Checking that our new issue has appeared in "View Issues".
        assertElementPresent("issues");
        assertEquals("count //tr[starts-with(@id, 'issue.')]", 5,
                selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertElementPresent("//tr[starts-with(@id, 'issue.')]/td[11][text()='" + descriptionValue + "']");

        selenium.open(applicationURL + "/portalhome.do");

        // Checking that our new issue has not appeared in "Unassigned" area.
        assertFalse("still unassigned issue " + descriptionValue,
                selenium.isElementPresent("//tr[starts-with(@id,'unassignedIssue.')]" +
                        "/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));
        // Checking that our new issue has appeared in "Created" area.
        assertElementPresent("//tr[starts-with(@id,'createdIssue.')]" +
                "/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']");

        // Check that "Watched" area is still empty.
        assertFalse("unexpected watchedIssue",
                selenium.isElementPresent("//tr[starts-with(@id, 'watchedIssue.')]")

        );
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_init_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueversionrel_dataset.xml",
                "dataset/issueactivitybean_dataset.xml",
                "dataset/issuehistorybean_dataset.xml"
        };
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }
}
