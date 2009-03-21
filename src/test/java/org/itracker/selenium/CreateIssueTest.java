package org.itracker.selenium;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.itracker.model.User;
import org.itracker.persistence.dao.UserDAO;
import org.junit.Test;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

/**
 * Verifies the functionality of new issue creation.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class CreateIssueTest extends AbstractSeleniumTestCase {
	
	private static final Logger log = Logger.getLogger(CreateIssueTest.class);

	/**
     * 1. Login into the system with some particular user
     *    (admin_test1 in our case, which already has 4 issues, 2 of them
     *     are unassigned).
     * 2. Go to "Project List" page (by clicking "Project List" link).
     * 3. Click "Create" link for selected project.
     * 4. Fill issue fields with some data (we remember description
     *    in our case, to check if new issue has appeared).
     * 5. After submitting new issue, we are at "View Issues" page and
     *    check if new issue has appeared.
     * 6. Go to "Portal Home" page and check if new issue has appeared
     *    in "Unassigned" area and "Created" area.
     * @throws java.io.IOException
     */
    @Test
    public void testCreateUnassignedIssue() throws IOException {
        closeSession();

        final String descriptionValue = "Issue to be assigned.";
        final String historyValue = "Issue to be assigned history.";
        
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("//.[@name='login']"));
        assertTrue(selenium.isElementPresent("//.[@name='password']"));
        assertTrue(selenium.isElementPresent("//.[@value='Login']"));
        selenium.type("//.[@name='login']", "admin_test1");
        selenium.type("//.[@name='password']", "admin_test1");
        selenium.click("//.[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click "Projects List".
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click issue creation link (usually it's named "Create").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[2]"));
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("creatorId"));
        assertTrue(selenium.isElementPresent("severity"));
        assertTrue(selenium.isElementPresent("versions"));
        
        selenium.type("//td[@id='description']/input", descriptionValue);
		selenium.type("history", historyValue);
		
        selenium.select("//td[@id='ownerId']/select", "value=-1");
        final UserDAO userDao = (UserDAO)applicationContext.getBean("userDAO");
        final User user = userDao.findByLogin("admin_test1");
        assertTrue(null != user);
        final long userId = user.getId();
        selenium.select("//td[@id='creatorId']/select", "value=" + userId);
        
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        
        try {
	        selenium.click("//td[@id='submit']/input");
	        selenium.waitForPageToLoad(SE_TIMEOUT);
	        assertEquals("sent notifications", 1, smtpServer.getReceivedEmailSize());
	        final Iterator<SmtpMessage> iter =
	                (Iterator<SmtpMessage>)smtpServer.getReceivedEmail();
	        // Checking email notification for creator.
	        final SmtpMessage smtpMessage1 = iter.next();
	        log.info("testCreateUnassignedIssue, raw:\n" + smtpMessage1.getBody());
	        final String smtpMessageBody1 = smtpMessage1.getBody();
	        log.info("testCreateUnassignedIssue, received:\n" + smtpMessageBody1);
	        assertTrue(smtpMessageBody1.contains(descriptionValue));
	        assertTrue(smtpMessageBody1.contains(historyValue));
        } finally {
        	smtpServer.stop();
        }
        
        
        
        // Check that the total number of issues is 5 now (4 from db + 1 our).
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(5, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[11][text()='" + descriptionValue + "']"));
        
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/portalhome.do");
        // Check that just created issue has appeared in "Unassigned" area.
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id,'unassignedIssue.')]/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));
        
        // Check that just created issue has appeared in "Created" area.
        assertTrue(selenium.isElementPresent("xpath=//tr[starts-with(@id,'createdIssue.')]/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));
        
        // Check that number of watched items is 0.
        assertEquals(0, selenium.getXpathCount("//tr[starts-with(@id, 'watchedIssue.')]"));        
    }
    
    /**
     * TODO
     * @throws java.io.IOException
     */
    @Test
    public void testCreateAssignedIssue() throws IOException {
        closeSession();
        
        final String descriptionValue = "Issue to be unassigned.";
        final String historyValue = "Issue to be unassigned history.";
        
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("//.[@name='login']"));
        assertTrue(selenium.isElementPresent("//.[@name='password']"));
        assertTrue(selenium.isElementPresent("//.[@value='Login']"));
        selenium.type("//.[@name='login']", "admin_test1");
        selenium.type("//.[@name='password']", "admin_test1");
        selenium.click("//.[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Clicking "Project List" link.        
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click issue creation link (usually it's named "Create").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[2]"));
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("creatorId"));
        assertTrue(selenium.isElementPresent("severity"));
        assertTrue(selenium.isElementPresent("versions"));

        
		selenium.type("//input[@name='description']", descriptionValue);
		selenium.type("history", historyValue);
        final UserDAO userDao = (UserDAO)applicationContext.getBean("userDAO");
        final User user = userDao.findByLogin("admin_test1");
        assertTrue(null != user);
        final long userId = user.getId();
        selenium.select("//td[@id='ownerId']/select", "value=" + userId);        
        selenium.select("//td[@id='creatorId']/select", "value=" + userId);
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        try {
	        selenium.click("//td[@id='submit']/input");
	        selenium.waitForPageToLoad(SE_TIMEOUT);
	        assertEquals("sent notifications", 2, smtpServer.getReceivedEmailSize());
	        final Iterator<SmtpMessage> iter =
	                (Iterator<SmtpMessage>)smtpServer.getReceivedEmail();
	        // Checking email notification for creator.
	        final SmtpMessage smtpMessage1 = iter.next();
	        log.info("testCreateAssignedIssue, raw:\n " + smtpMessage1.getBody());
	        final String smtpMessageBody1 = smtpMessage1.getBody();
	        log.info("testCreateAssignedIssue, received:\n " + smtpMessageBody1);
	        assertTrue(smtpMessageBody1.contains(descriptionValue));
	        assertTrue(smtpMessageBody1.contains(historyValue));
	        // Checking email notification for owner.
	        final SmtpMessage smtpMessage2 = iter.next();
	        final String smtpMessageBody2 = smtpMessage2.getBody();
	        assertTrue(smtpMessageBody2.contains(descriptionValue));
	        assertTrue(smtpMessageBody2.contains(historyValue));
		} finally {
        	smtpServer.stop();
        }
        // Checking that our new issue has appeared in "View Issues".
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(5, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[11][text()='" + descriptionValue + "']"));
        
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath + "/portalhome.do");
        // Checking that our new issue has not appeared in "Unassigned" area.
        assertFalse(selenium.isElementPresent("//tr[starts-with(@id,'unassignedIssue.')]/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));
        
        // Checking that our new issue has appeared in "Created" area.
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id,'createdIssue.')]/td[5][text()='test_name']/../td[11][text()='" + descriptionValue + "']"));
        
        // Check that "Watched" area is still empty.
        assertEquals(0, selenium.getXpathCount("//tr[starts-with(@id, 'watchedIssue.')]"));
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
        return new String[]{ "application-context.xml"};
    }
}
