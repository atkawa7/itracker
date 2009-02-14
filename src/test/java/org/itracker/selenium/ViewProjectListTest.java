package org.itracker.selenium;

import java.io.IOException;
import org.junit.Test;

/**
 * Check the content and the functionality of "Projects List" page.
 * 
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewProjectListTest extends AbstractSeleniumTestCase {
    /**
     * 0. Exit all available http sessions.
     * 1. Login into the system with particular login (admin_test1).
     * 2. Check that "Project List" link is present at the "Portal Home" page.
     * 3. Click "Project List".
     * 4. Check that "Projects" element is present at the "Project List" page.
     * 5. Check that there are two projects in a table at the page.
     * 6. Check that "test_name" project contains 4 open, 0 resolved
     *    and 4 issues total.
     * 7. Check that "test_name2" project contains no issues at all.
     * @throws java.io.IOException
     */
    @Test
    public void testViewProjectList() throws IOException {
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
        
        assertTrue(selenium.isElementPresent("listprojects"));
        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);
        
        assertTrue(selenium.isElementPresent("projects"));
        assertEquals(2, selenium.getXpathCount("//tr[starts-with(@id, 'project.')]"));
        
        //// project "test_name"
        // Check the number of open issues.
        assertEquals("4", selenium.getText("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[4]"));
        
        // Check the number of resolved issues.
        assertEquals("0", selenium.getText("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[5]"));
        
        // Check total number of issues.
        assertEquals("4", selenium.getText("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[6]"));
        
        //// project "test_name2"
        // Check the number of open issues.
        assertEquals("0", selenium.getText("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name2']/../td[4]"));
        
        // Check the number of resolved issues.
        assertEquals("0", selenium.getText("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name2']/../td[5]"));
        
        // Check total number of issues.
        assertEquals("0", selenium.getText("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name2']/../td[6]"));
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