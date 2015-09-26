package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

/**
 * Check the content and the functionality of "Projects List" page.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewProjectListSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * 0. Exit all available http sessions.
     * 1. Login into the system with particular login (admin_test1).
     * 2. Check that "Project List" link is present at the "Portal Home" page.
     * 3. Click "Project List".
     * 4. Check that "Projects" element is present at the "Project List" page.
     * 5. Check that there are two projects in a table at the page.
     * 6. Check that "test_name" project contains 4 open, 0 resolved
     * and 4 issues total.
     * 7. Check that "test_name2" project contains no issues at all.
     */
    @Test
    public void testViewProjectList() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("listprojects")).click();

        waitForPageToLoad();

        assertElementPresent(By.id("projects"));
        assertEquals("projects count", 2,
                driver.findElements(By.xpath("//*[starts-with(@id, 'project.')]")).size());

        //// project "test_name"
        // Check the number of open issues.
        assertElementTextEquals("4", By.xpath("//*[starts-with(@id, 'project.')]/*[3][text()='test_name']/../*[4]"));

        // Check the number of resolved issues.
        assertElementTextEquals("0", By.xpath("//*[starts-with(@id, 'project.')]/*[3][text()='test_name']/../*[5]"));

        // Check total number of issues.
        assertElementTextEquals("4", By.xpath("//*[starts-with(@id, 'project.')]/*[3][text()='test_name']/../*[6]"));

        //// project "test_name2"
        // Check the number of open issues.
        assertElementTextEquals("0", By.xpath("//*[starts-with(@id, 'project.')]/*[3][text()='test_name2']/../*[4]"));

        // Check the number of resolved issues.
        assertElementTextEquals("0", By.xpath("//*[starts-with(@id, 'project.')]/*[3][text()='test_name2']/../*[5]"));

        // Check total number of issues.
        assertElementTextEquals("0", By.xpath("//*[starts-with(@id, 'project.')]/*[3][text()='test_name2']/../*[6]"));
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
}
