package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Issue;
import org.junit.Test;

public class IssueDAOImplTest extends AbstractDependencyInjectionTest {

    private IssueDAO issueDAO;

    @Test
    public void testCountByProjectAndLowerStatus() {

        int issueCount = issueDAO.countByProjectAndLowerStatus(2, 3);

        assertEquals(2, issueCount);
    }

    @Test
    public void testFindByProjectAndHigherStatus() {

        List<Issue> issues = issueDAO.findByProjectAndHigherStatus(2, 2);

        assertEquals(2, issues.size());

        assertContainsIssue(issueDAO.findByPrimaryKey(2), issues);
        assertContainsIssue(issueDAO.findByPrimaryKey(3), issues);
    }

    @Test
    public void testCountByProjectAndHigherStatus() {

        int issueCount = issueDAO.countByProjectAndHigherStatus(2, 2);

        assertEquals(2, issueCount);
    }

    @Test
    public void testCountByProject() {

        int projectCount = issueDAO.countByProject(2);

        assertEquals(3, projectCount);
    }

    private void assertContainsIssue(Issue issue, List<Issue> issues) {

        boolean found = false;

        for (Issue issueItem : issues) {
            if (issueItem.getId().equals(issue.getId())) {
                found = true;
                break;
            }
        }

        if (!found) {
            fail("Issue not found in the list.");
        }

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
