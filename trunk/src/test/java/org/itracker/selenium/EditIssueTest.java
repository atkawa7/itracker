package org.itracker.selenium;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.io.IOException;
import java.util.Iterator;
import org.junit.Test;

/**
 * Verifies the functionality of Edit Issue page.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class EditIssueTest extends AbstractSeleniumTestCase {
    /**
     * Editing one of tickets.
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "View" link for selected issue (no 1).
     * 5. Click "Edit" link.
     * 6. Update the description for this issue.
     * 7. Submit the form.
     * 8. Check if we got email notification about modifications.
     * 9. Being at "View Issues" page, right after saving updated issues,
     *    check that no more issues with old description is here, but
     *    new description has appeared.
     * @throws java.io.IOException
     */
    @Test
    public void testEditIssue1FromViewIssue() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("//.[@name='login']"));
        assertTrue(selenium.isElementPresent("//.[@name='password']"));
        assertTrue(selenium.isElementPresent("//.[@value='Login']"));
        selenium.type("//.[@name='login']", "admin_test1");
        selenium.type("//.[@name='password']", "admin_test1");
        selenium.click("//.[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click view issue link (usually it's named "View").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("//td[@id='actions']/a[2]"));
        selenium.click("//td[@id='actions']/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);        
        
        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("//input[@type='submit']"));
        
        selenium.type("description", "test_description (updated)");
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals(smtpServer.getReceivedEmailSize(), 1);
        final Iterator<SmtpMessage> iter =
                (Iterator<SmtpMessage>)smtpServer.getReceivedEmail();
        // Checking email notification for creator.
        final SmtpMessage smtpMessage = iter.next();
        final String smtpMessageBody = smtpMessage.getBody();
        assertTrue(smtpMessageBody.contains("test_description (updated)"));
        smtpServer.stop();
        
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(4, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertFalse(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[13][contains(text(),'A. admin lastname')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description (updated)']/../td[13][contains(text(),'A. admin lastname')]"));
    }
    
    /**
     * Editing one of tickets.
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "Edit" link for selected issue (no 1).
     * 5. Update the description for this issue.
     * 6. Submit the form.
     * 7. Check if we got email notification about modifications.
     * 8. Being at "View Issues" page, right after saving updated issues,
     *    check that no more issues with old description is here, but
     *    new description has appeared.
     * @throws java.io.IOException
     */
    @Test
    public void testEditIssue1FromIssueList() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("//.[@name='login']"));
        assertTrue(selenium.isElementPresent("//.[@name='password']"));
        assertTrue(selenium.isElementPresent("//.[@value='Login']"));
        selenium.type("//.[@name='login']", "admin_test1");
        selenium.type("//.[@name='password']", "admin_test1");
        selenium.click("//.[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click view issue link (usually it's named "View").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[1]/a[2]"));
        selenium.click("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[1]/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("//input[@type='submit']"));
        
        selenium.type("description", "test_description (updated)");
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals(smtpServer.getReceivedEmailSize(), 1);
        final Iterator<SmtpMessage> iter =
                (Iterator<SmtpMessage>)smtpServer.getReceivedEmail();
        // Checking email notification for creator.
        final SmtpMessage smtpMessage = iter.next();
        final String smtpMessageBody = smtpMessage.getBody();
        assertTrue(smtpMessageBody.contains("test_description (updated)"));
        smtpServer.stop();
        
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(4, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertFalse(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[13][contains(text(),'A. admin lastname')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description (updated)']/../td[13][contains(text(),'A. admin lastname')]"));
    }
    
    /**
     * Move a ticket to another project.
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "View" link for selected issue (no 1).
     * 5. Click "Move" link.
     * 6. Select a project (test_name2) where we gonna move a ticket.
     * 7. Since test_name2 was an empty project, and now we have a single
     *    item there, we check that it has appeared at "View Issues"
     *    page for test_name2 project.
     * @throws java.io.IOException
     */
    @Test
    public void testMoveIssue1() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("//.[@name='login']"));
        assertTrue(selenium.isElementPresent("//.[@name='password']"));
        assertTrue(selenium.isElementPresent("//.[@value='Login']"));
        selenium.type("//.[@name='login']", "admin_test1");
        selenium.type("//.[@name='password']", "admin_test1");
        selenium.click("//.[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click view issue link (usually it's named "View").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("//td[@id='actions']/a[3]"));
        selenium.click("//td[@id='actions']/a[3]");
        selenium.waitForPageToLoad(SE_TIMEOUT);        
        
        assertTrue(selenium.isElementPresent("projectId"));
        selenium.select("projectId", "label=test_name2");
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals(smtpServer.getReceivedEmailSize(), 0); // ? really no notification
//        final Iterator<SmtpMessage> iter =
//                (Iterator<SmtpMessage>)smtpServer.getReceivedEmail();
//        // Checking email notification for creator.
//        final SmtpMessage smtpMessage1 = iter.next();
//        final String smtpMessageBody1 = smtpMessage1.getBody();
//        assertTrue(smtpMessageBody1.contains("test_description (updated)"));
        smtpServer.stop();
        
        assertTrue(selenium.isElementPresent("//td[@id='actions']/a[1]"));
        selenium.click("//td[@id='actions']/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);        
        
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(1, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[13][contains(text(),'A. admin lastname')]"));
    }
    
    /**
     * Editing one of tickets (another issues).
     * 1. Enter the system with some particular user (admin_test1).
     * 2. Goto Projects List page.
     * 3. Click "View" link for some project (test_name).
     * 4. Click "View" link for selected issue (no 2).
     * 5. Click "Edit" link.
     * 6. Update the description for this issue.
     * 7. Submit the form.
     * 8. Check if we got email notification about modifications.
     * 9. Being at "View Issues" page, right after saving updated issues,
     *    check that no more issues with old description is here, but
     *    new description has appeared.
     * @throws java.io.IOException
     */
    @Test
    public void testEditIssue2FromViewIssue() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);
        assertTrue(selenium.isElementPresent("//.[@name='login']"));
        assertTrue(selenium.isElementPresent("//.[@name='password']"));
        assertTrue(selenium.isElementPresent("//.[@value='Login']"));
        selenium.type("//.[@name='login']", "admin_test1");
        selenium.type("//.[@name='password']", "admin_test1");
        selenium.click("//.[@value='Login']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        // Click view issue link (usually it's named "View").
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']/../td[11][text()='test_description 2']/../td[1]/a[1]"));
        selenium.click("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']/../td[11][text()='test_description 2']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertEquals("test_description 2", selenium.getText("description"));
        assertEquals("admin firstname admin lastname", selenium.getText("ownerName"));
        
        assertTrue(selenium.isElementPresent("//td[@id='actions']/a[2]"));
        selenium.click("//td[@id='actions']/a[2]");
        selenium.waitForPageToLoad(SE_TIMEOUT);        
        
        assertTrue(selenium.isElementPresent("description"));
        assertTrue(selenium.isElementPresent("ownerId"));
        assertTrue(selenium.isElementPresent("//input[@type='submit']"));
        
        selenium.type("description", "test_description 2 (updated)");
        final SimpleSmtpServer smtpServer = SimpleSmtpServer.start(2525);
        selenium.click("//input[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        assertEquals(smtpServer.getReceivedEmailSize(), 1);
        final Iterator<SmtpMessage> iter =
                (Iterator<SmtpMessage>)smtpServer.getReceivedEmail();
        // Checking email notification for creator.
        final SmtpMessage smtpMessage1 = iter.next();
        final String smtpMessageBody1 = smtpMessage1.getBody();
        assertTrue(smtpMessageBody1.contains("test_description 2 (updated)"));
        smtpServer.stop();
        
        assertTrue(selenium.isElementPresent("issues"));
        assertEquals(4, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertFalse(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']/../td[11][text()='test_description 2']/../td[13][contains(text(),'A. admin lastname')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']/../td[11][text()='test_description 2 (updated)']/../td[13][contains(text(),'A. admin lastname')]"));        
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
