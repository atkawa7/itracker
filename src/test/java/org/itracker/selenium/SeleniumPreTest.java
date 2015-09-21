package org.itracker.selenium;

import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Makes sure selenium is working.
 * Will also check the itracker website.
 */
public class SeleniumPreTest {
    @Test
    public void testITrackerSiteSeleium() throws Exception {
        WebDriver driver = SeleniumManager.getWebDriver();

        driver.get("http://itracker.sourceforge.net");
        driver.findElement(By.cssSelector("body #banner"));
        driver.findElement(By.cssSelector("body #breadcrumbs"));
        driver.findElement(By.cssSelector("body #leftColumn"));
        driver.findElement(By.cssSelector("body #bodyColumn"));
        driver.findElement(By.cssSelector("body #footer"));


        driver.get("http://itracker.sourceforge.net/license.html");
        final String license = driver.findElement(By.xpath("//*[a[@name=\"GNU_Lesser_General_Public_License\"]]")).getText();
        Assert.assertEquals("project license", "GNU Lesser General Public License", license);

    }


}
