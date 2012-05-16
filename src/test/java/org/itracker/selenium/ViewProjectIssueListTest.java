package org.itracker.selenium;

import org.junit.Test;

import java.io.IOException;

/**
 * Verifies the functionality of per-project issues list page.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewProjectIssueListTest extends AbstractSeleniumTestCase {
    /**
     * 1. Login into the application with a specific user (admin_test1).
     * 2. Goto Project List page.
     * 3. Select project "test_name" and click "View" link for it.
     * 4. At the "View Issue" page, compare all issues to the data we
     * expect.
     */
    @Test
    public void testViewProjectIssueList() throws IOException {
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

        assertEquals(4, selenium.getXpathCount("//tr[starts-with(@id, 'issue.')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']/../td[11][text()='test_description']/../td[13][contains(text(),'A. admin lastname')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']/../td[11][text()='test_description 2']/../td[13][contains(text(),'A. admin lastname')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='3']/../td[11][text()='test_description 3']/../td[13][contains(text(),'A. admin lastname')]"));
        assertTrue(selenium.isElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='4']/../td[11][text()='test_description 4']/../td[13][contains(text(),'A. admin lastname')]"));
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
        return new String[]{"application-context.xml"};
    }
}
