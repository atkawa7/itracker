package org.itracker.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Issue;
import org.itracker.model.IssueActivity;
import org.itracker.model.IssueActivityType;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.UserService;
import org.itracker.services.util.AuthenticationConstants;
import org.junit.Test;

public class IssueActivityDAOImplTest extends AbstractDependencyInjectionTest {

	private IssueDAO issueDAO;
	private IssueActivityDAO issueActivityDAO;
	private UserDAO userDAO;
	private UserService userService;

	@Test
	public void testFindByIssueId() {

		List<IssueActivity> activities = issueActivityDAO.findByIssueId(2);
		assertNotNull("issue#2.activities", activities);

	}

	@Test
	public void testFindById() throws Exception {

		IssueActivity activity = issueActivityDAO.findById(1);
		assertNotNull("issueActivity#1", activity);
		assertEquals("issueActivity#1.description",
				"user 2 created this issue", activity.getDescription());
		assertEquals("issueActivity#1.type", IssueActivityType.ISSUE_CREATED,
				activity.getActivityType());

	}

	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();

		issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
		issueActivityDAO = (IssueActivityDAO) applicationContext
				.getBean("issueActivityDAO");
		userDAO = (UserDAO) applicationContext.getBean("userDAO");

		userService = (UserService) applicationContext.getBean("userService");

	}

	protected String[] getDataSetFiles() {
		return new String[] { "dataset/userpreferencesbean_dataset.xml",
				"dataset/userbean_dataset.xml",
				"dataset/projectbean_dataset.xml",
				"dataset/versionbean_dataset.xml",
				"dataset/issuebean_dataset.xml",
				"dataset/issueactivitybean_dataset.xml" };
	}

	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}
