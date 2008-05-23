package org.itracker.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.UserService;
import org.itracker.services.util.AuthenticationConstants;
import org.junit.Test;

public class IssueDAOImplTest extends AbstractDependencyInjectionTest {

    private IssueDAO issueDAO;
    private UserDAO userDAO;
    private UserService userService;

    @Test
    public void testCountByProjectAndLowerStatus() {

        Long issueCount = issueDAO.countByProjectAndLowerStatus(2, 3);

        assertEquals(Long.valueOf(2), issueCount);
    }

    @Test
    public void testFindByProjectAndHigherStatus() {

        List<Issue> issues = issueDAO.findByProjectAndHigherStatus(2, 2);

        assertEquals(3, issues.size());

        assertContainsIssue(issueDAO.findByPrimaryKey(2), issues);
        assertContainsIssue(issueDAO.findByPrimaryKey(3), issues);
    }

    @Test
    public void testCountByProjectAndHigherStatus() {

        Long issueCount = issueDAO.countByProjectAndHigherStatus(2, 2);

        assertEquals(Long.valueOf(3), issueCount);
    }

    @Test
    public void testCountByProject() {

        Long projectCount = issueDAO.countByProject(2);

        assertEquals(Long.valueOf(4), projectCount);
    }

    @Test
    public void testQuery() {

        IssueSearchQuery searchQuery = new IssueSearchQuery();

        List<Integer> projectsIDs = new ArrayList<Integer>();
        projectsIDs.add(2);

        searchQuery.setProjects(projectsIDs);

        User user = userDAO.findByPrimaryKey(2);
        Map<Integer, Set<PermissionType>> permissions = userService.getUsersMapOfProjectIdsAndSetOfPermissionTypes(user, AuthenticationConstants.REQ_SOURCE_WEB);

        List<Issue> issues = issueDAO.query(searchQuery, user, permissions);

        assertEquals(4, issues.size());

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
        userDAO = (UserDAO) applicationContext.getBean("userDAO");

        userService = (UserService) applicationContext.getBean("userService");

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
