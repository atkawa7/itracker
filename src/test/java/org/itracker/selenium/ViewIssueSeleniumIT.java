package org.itracker.selenium;

import org.junit.Test;

import java.io.IOException;

/**
 * Verifies the functionality of View Issue page.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewIssueSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * 1. Enter the system with admin_test1 user login.
     * 2. Goto "Projects List" page.
     * 3. For project "test_name", click "View" link.
     * 4. At "View Issues" page, for Issue 1, click "View" link.
     * 5. At appeared page (View Issue), compare description and
     * current owner to expected values.
     */
    @Test
    public void testViewIssue1() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        loginUser("admin_test1", "admin_test1");

        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        // Click view issue link (usually it's named "View").
        assertElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']" +
                "/../td[11][text()='test_description']/../td[1]/a[1]");
        selenium.click("//tr[starts-with(@id, 'issue.')]/td[3][text()='1']" +
                "/../td[11][text()='test_description']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertElementTextEquals("test_description", "description");
        assertElementTextEquals("admin firstname admin lastname", "ownerName");
    }

    /**
     * 1. Enter the system with admin_test1 user login.
     * 2. Goto "Projects List" page.
     * 3. For project "test_name", click "View" link.
     * 4. At "View Issues" page, for Issue 2, click "View" link.
     * 5. At appeared page (View Issue), compare description and
     * current owner to expected values.
     */
    @Test
    public void testViewIssue2() throws IOException {
        closeSession();
        selenium.open("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        loginUser("admin_test1", "admin_test1");

        selenium.click("listprojects");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        // Click view issue link (usually it's named "View").
        assertElementPresent("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.click("//tr[starts-with(@id, 'project.')]/td[3][text()='test_name']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertElementPresent("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']" +
                "/../td[11][text()='test_description 2']/../td[1]/a[1]");
        selenium.click("//tr[starts-with(@id, 'issue.')]/td[3][text()='2']" +
                "/../td[11][text()='test_description 2']/../td[1]/a[1]");
        selenium.waitForPageToLoad(SE_TIMEOUT);

        assertElementTextEquals("test_description 2", "description");
        assertElementTextEquals("admin firstname admin lastname", "ownerName");
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
