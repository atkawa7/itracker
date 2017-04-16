package org.itracker.selenium.admin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.itracker.model.User;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.selenium.AbstractSeleniumTestCase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ViewUserListSeleniumIT extends AbstractSeleniumTestCase {

   @Test
   public void testViewUserList() throws Exception {
      closeSession();
      driver.get("http://" + applicationHost + ":" + applicationPort + "/"
              + applicationPath);
      login("admin_test1", "admin_test1");
      assertElementPresent(By.name("admin")).click();

      waitForPageToLoad();
      UserDAO dao = (UserDAO) applicationContext.getBean("userDAO");
      List<User> allUsers = dao.findAll();
      final Collection allLogins = CollectionUtils.collect(allUsers, new Transformer() {
         @Override
         public Object transform(Object input) {
            return ((User)input).getLogin();
         }
      });
      final long expUsers = allLogins.size();
      int totalUsers = Integer.valueOf(assertElementPresent(By.cssSelector("#totalUsers")).getText().trim());
      assertEquals("All users are counted", expUsers, totalUsers);
      assertElementPresent(By.cssSelector("#users .panel-heading a.btn-link")).click();

      waitForPageToLoad();
      WebElement users = assertElementPresent(By.id("users"));
      List<WebElement> elements = users.findElements(By.cssSelector("tbody tr"));
      assertEquals(expUsers, elements.size());
      for (WebElement tr : elements) {
         assertElementPresent(By.cssSelector("a[href*='action=update']"), tr);
         final String login = assertElementPresent(By.cssSelector(".login"), tr).getText().trim();
         assertTrue(allUsers.remove(dao.findByLogin(login)));
      }
      assertTrue(allUsers.isEmpty());
   }

   @Override
   protected String[] getDataSetFiles() {
      return new String[]{
              "dataset/languagebean_init_dataset.xml",
              "dataset/languagebean_dataset.xml",
              "dataset/userpreferencesbean_dataset.xml",
              "dataset/userbean_dataset.xml",
      };
   }
}
