package org.itracker.selenium;

import org.junit.Test;

import java.io.IOException;

/**
 * Check the content of PortalHome page with some data available.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewPortalHomeTest extends AbstractSeleniumTestCase {
    /**
     * Verifies the successfull login case with valid login/password.
     * <p/>
     * 0. Exit all available http sessions.
     * 1. Login into the system with particular login (admin_test1).
     * 2. Check that "Unassigned Issues" section is present at the page.
     * 3. Check that number of unassigned issues is 2 (as we defined in
     * database before running this test).
     * 4. Check all unassigned issues we expect to be in the system are
     * shown at the page (check them one by one).
     * 5. Check that "Created Issues" section is present at the page.
     * 6. Check that number of created issues is 4 (as we defined in
     * database before running this test).
     * 7. Check all all create issues we expect to be in the system are
     * shown at the page (check them one by one).
     * 8. Check that "Watched Issues" section is present at the page.
     * 9. Check that number of watched items is 0.
     */
    @Test
    public void testViewHomePage() throws IOException {
        SeleniumManager.closeSession(selenium);
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        assertFalse(selenium.isElementPresent("id"));
        assertTrue(selenium.isElementPresent("login"));
        assertTrue(selenium.isElementPresent("password"));
        assertTrue(selenium.isElementPresent("xpath=//.[@type='submit']"));
        selenium.type("login", "admin_test1");
        selenium.type("password", "admin_test1");
        selenium.click("xpath=//.[@type='submit']");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertElementPresent("id");
        assertElementPresent("id=unassignedIssues");
        assertEquals("count //tr[starts-with(@id, 'unassignedIssue.')]", 2, selenium.getXpathCount("//tr[starts-with(@id, 'unassignedIssue.')]"));
        assertElementPresent("xpath=//tr[starts-with(@id,'unassignedIssue.')]/td[3][text()='1']/../td[5][text()='test_name']/../td[11][text()='test_description']");
        assertElementPresent("xpath=//tr[starts-with(@id,'unassignedIssue.')]/td[3][text()='2']/../td[5][text()='test_name']/../td[11][text()='test_description 2']");

        assertElementPresent("id=createdIssues");
        assertEquals("count //tr[starts-with(@id, 'createdIssue.')]", 4, selenium.getXpathCount("//tr[starts-with(@id, 'createdIssue.')]"));
        assertElementPresent("xpath=//tr[starts-with(@id,'createdIssue.')]/td[3][text()='1']/../td[5][text()='test_name']/../td[11][text()='test_description']");
        assertElementPresent("xpath=//tr[starts-with(@id,'createdIssue.')]/td[3][text()='2']/../td[5][text()='test_name']/../td[11][text()='test_description 2']");
        assertElementPresent("xpath=//tr[starts-with(@id,'createdIssue.')]/td[3][text()='3']/../td[5][text()='test_name']/../td[11][text()='test_description 3']");
        assertElementPresent("xpath=//tr[starts-with(@id,'createdIssue.')]/td[3][text()='4']/../td[5][text()='test_name']/../td[11][text()='test_description 4']");

        assertElementPresent("id=watchedIssues");
        assertFalse("unexpected watchedIssue", selenium.isElementPresent("//tr[starts-with(@id, 'watchedIssue.')]"));
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
