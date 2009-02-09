package org.itracker.selenium;

import java.io.IOException;
import org.junit.Test;

/**
 * Verifies the functionality of View Issue page.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewIssueTest extends AbstractSeleniumTestCase {
    /**
     * 1. Enter the system with admin_test1 user login.
     * 2. Goto "Projects List" page.
     * 3. For project "test_name", click "View" link.
     * 4. At "View Issues" page, for Issue 1, click "View" link.
     * 5. At appeared page (View Issue), compare description and
     *    current owner to expected values.
     * @throws java.io.IOException
     */
    @Test
    public void testViewIssue1() throws IOException {
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
        
        assertEquals("test_description", selenium.getText("description"));
        assertEquals("admin firstname admin lastname", selenium.getText("ownerName"));
    }
    
    /**
     * 1. Enter the system with admin_test1 user login.
     * 2. Goto "Projects List" page.
     * 3. For project "test_name", click "View" link.
     * 4. At "View Issues" page, for Issue 2, click "View" link.
     * 5. At appeared page (View Issue), compare description and
     *    current owner to expected values.
     * @throws java.io.IOException
     */
    @Test
    public void testViewIssue2() throws IOException {
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
                "dataset/issuebean_dataset.xml"
        };
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{ "application-context.xml"};
    }
}
