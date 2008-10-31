/**
 * 
 */
package org.itracker.services.implementations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueActivityType;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.CustomField.Type;
import org.itracker.services.IssueService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.exceptions.IssueSearchException;
import org.itracker.services.exceptions.ProjectException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.web.util.ServletContextUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author ranks
 * 
 */
public class IssueServiceTest extends AbstractDependencyInjectionTest {

	private static final Logger logger = Logger
			.getLogger(IssueServiceTest.class);
	private IssueService issueService;

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssue(java.lang.Integer)}.
	 */
	@Test
	public void testGetIssue() {

		Issue issue = this.issueService.getIssue(1);

		assertNotNull("issue#1", issue);

		this.issueService.getIssue(2);
		assertNotNull("issue#2", issue);

		this.issueService.getIssue(3);
		assertNotNull("issue#3", issue);

		this.issueService.getIssue(4);
		assertNotNull("issue#4", issue);

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getNumberIssues()}.
	 */
	@Test
	public void testGetNumberIssues() {

		Long nrOfIssues = issueService.getNumberIssues();
		assertEquals("allissues", (Long) 4l, nrOfIssues);

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesWithStatus(int)}.
	 */
	@Test
	public void testGetIssuesWithStatus() {

		Collection<Issue> issues = issueService.getIssuesWithStatus(1);
		assertEquals("status 1 issues", 1, issues.size());
		issues = issueService.getIssuesWithStatus(2);
		assertEquals("status 2 issues", 1, issues.size());
		issues = issueService.getIssuesWithStatus(3);
		assertEquals("status 3 issues", 2, issues.size());

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesWithStatusLessThan(int)}
	 * .
	 */
	@Test
	public void testGetIssuesWithStatusLessThan() {
		Collection<Issue> issues = issueService.getIssuesWithStatusLessThan(2);
		assertEquals("status less 2 issues", 1, issues.size());

		issues = issueService.getIssuesWithStatusLessThan(3);
		assertEquals("status less 3 issues", 2, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesWithSeverity(int)}.
	 */
	@Test
	public void testGetIssuesWithSeverity() {
		Collection<Issue> issues = issueService.getIssuesWithSeverity(1);

		assertEquals("issues severity#1", 4, issues.size());
		assertTrue("issue#1 countained", issues.contains(issueService
				.getIssue(1)));
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesByProjectId(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssuesByProjectIdInteger() {
		Collection<Issue> issues = issueService.getIssuesByProjectId(2);

		assertEquals("issues by project#2", 4, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesByProjectId(java.lang.Integer, int)}
	 * .
	 */
	@Test
	public void testGetIssuesByProjectIdIntegerInt() {
		Collection<Issue> issues = issueService.getIssuesByProjectId(2, 2);
		assertEquals("issues count", 1, issues.size());
		issues = issueService.getIssuesByProjectId(2, 3);
		assertEquals("issues count", 2, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesCreatedByUser(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssuesCreatedByUserInteger() {
		Collection<Issue> issues = issueService.getIssuesCreatedByUser(3);
		assertEquals("issues count createdBy#3", 0, issues.size());

		issues = issueService.getIssuesCreatedByUser(2);
		assertEquals("issues count createdBy#2", 4, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesCreatedByUser(java.lang.Integer, boolean)}
	 * .
	 */
	@Test
	public void testGetIssuesCreatedByUserIntegerBoolean() {
		// TODO test function for unavailable projects
		Collection<Issue> issues = issueService
				.getIssuesCreatedByUser(2, false);
		assertEquals("issues count createdBy#3", 4, issues.size());

		issues = issueService.getIssuesCreatedByUser(2, true);
		assertEquals("issues count createdBy#2", 4, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesOwnedByUser(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssuesOwnedByUserInteger() {
		Collection<Issue> issues = issueService.getIssuesOwnedByUser(2);
		assertEquals("issues count owner#2", 4, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesOwnedByUser(java.lang.Integer, boolean)}
	 * .
	 */
	@Test
	public void testGetIssuesOwnedByUserIntegerBoolean() {
		// TODO test function for unavailable projects
		Collection<Issue> issues = issueService.getIssuesOwnedByUser(2, false);
		assertEquals("issues count owner#2", 4, issues.size());

		issues = issueService.getIssuesOwnedByUser(2, true);
		assertEquals("issues count owner#2", 4, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesWatchedByUser(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssuesWatchedByUserInteger() {
		Collection<Issue> issues = issueService.getIssuesWatchedByUser(2);
		// TODO verify this check..
		assertEquals("issues watched by#2", 1, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssuesWatchedByUser(java.lang.Integer, boolean)}
	 * .
	 */
	@Test
	@Ignore
	public void testGetIssuesWatchedByUserIntegerBoolean() {
		fail("Not yet implemented");
		// TODO test function for unavailable projects
		// currently failing..?
		// Collection<Issue> issues = issueService.getIssuesWatchedByUser(2,
		// true);
		// assertEquals("issues watched by#2", 4, issues.size());
		// issues = issueService.getIssuesWatchedByUser(2, false);
		// assertEquals("issues watched by#2", 4, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getUnassignedIssues()}.
	 */
	@Test
	@Ignore
	public void testGetUnassignedIssues() {
		fail("Not yet implemented");
		// TODO fails, expecting 0 unassigned..?
		// Collection<Issue> issues = issueService.getUnassignedIssues();
		//		
		// assertEquals("unassignedIssues", 0, issues.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getUnassignedIssues(boolean)}.
	 */
	@Test
	@Ignore
	public void testGetUnassignedIssuesBoolean() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#createIssue(org.itracker.model.Issue, java.lang.Integer, java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	public void testCreateIssue() {
		Issue issue = new Issue();
		issue.setStatus(1);
		issue.setDescription("hi");
		issue.setSeverity(1);
		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(2);
		assertNotNull("user#2", user);
		IssueHistory history = new IssueHistory(issue, user);
		history.setDescription("hello");
		history.setStatus(1);

		try {
			Issue newIssue = issueService.createIssue(issue, 2, user.getId(),
					user.getId());
			assertNotNull("new issue", newIssue);
			assertNotNull("model issue id", issue.getId());
			assertNotNull("new issue id", issue.getId());
			assertTrue("new issue id == model issue id",
					newIssue.getId() == issue.getId());

		} catch (ProjectException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#updateIssue(org.itracker.model.Issue, java.lang.Integer)}
	 * .
	 */
	@Test
	public void testUpdateIssue() {
		Issue updateIssue = issueService.getIssue(2);
		assertNotNull("issue", updateIssue);

		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(2);
		assertNotNull("user#2", user);

		IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);
		Integer histCount = updateIssue.getHistory().size();

		updateIssue.getHistory().add(history);

		try {
			updateIssue = issueService.updateIssue(updateIssue, 2);
			assertEquals("new history size", histCount + 1, updateIssue
					.getHistory().size());

		} catch (ProjectException e) {
			logger.error("testUpdateIssue", e);
			fail(e.getMessage());
		}

	}

	@Test
	public void testUpdateIssueSeverity() throws Exception {
		Issue updateIssue = issueService.getIssue(2);
		assertNotNull("issue", updateIssue);

		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(2);
		assertNotNull("user#2", user);

		IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);
		// Integer histCount = updateIssue.getHistory().size();
		Integer actCount = updateIssue.getActivities().size();

		updateIssue.getHistory().add(history);
		int severity = updateIssue.getSeverity() + 1;

		updateIssue.setSeverity(severity);

		try {

			updateIssue = issueService.updateIssue(updateIssue, 2);

			assertEquals("new activity size", actCount + 1, updateIssue
					.getActivities().size());

			assertEquals("new issue severity", severity, updateIssue
					.getSeverity().intValue());

			assertEquals("new added activity type",
					IssueActivityType.SEVERITY_CHANGE, issueService.getIssue(
							updateIssue.getId()).getActivities().get(
							updateIssue.getActivities().size() - 1)
							.getActivityType());

			// Won't work, the equals method is not even called on IssueActivity
			// once during the test. Althought, they have the equal Contents.
			// assertEquals("new activities", updateIssue.getActivities(),
			// issueService.getIssue(updateIssue.getId()).getActivities());

		} catch (ProjectException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateIssueDescription() throws Exception {
		Issue updateIssue = issueService.getIssue(2);
		assertNotNull("issue", updateIssue);

		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(2);
		assertNotNull("user#2", user);

		IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);

		Integer actCount = updateIssue.getActivities().size();

		updateIssue.getHistory().add(history);
		String description = "new issue description";

		updateIssue.setDescription(description);

		try {

			updateIssue = issueService.updateIssue(updateIssue, 2);

			assertEquals("updateIssue.activities.size", actCount + 1,
					updateIssue.getActivities().size());

			assertEquals("updateIssue.description", description, updateIssue
					.getDescription());

			assertEquals("updateIssue.activity.last.type",
					IssueActivityType.DESCRIPTION_CHANGE, updateIssue
							.getActivities().get(
									updateIssue.getActivities().size() - 1)
							.getActivityType());
			// test reloaded issue values
			Issue reloadedIssue = issueService.getIssue(updateIssue.getId());

			assertEquals("reloadedIssue.activities.size", actCount + 1,
					updateIssue.getActivities().size());

			assertEquals("reloadedIssue.description", description, updateIssue
					.getDescription());

			assertEquals("reloadedIssue.activity.last.type",
					IssueActivityType.DESCRIPTION_CHANGE, reloadedIssue
							.getActivities().get(
									reloadedIssue.getActivities().size() - 1)
							.getActivityType());
		} catch (ProjectException e) {
			logger.error("testUpdateIssueDescription: failed to test, failing",
					e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateIssueResolution() throws Exception {
		Issue updateIssue = issueService.getIssue(2);
		assertNotNull("issue", updateIssue);

		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(2);
		assertNotNull("user#2", user);

		IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);

		Integer actCount = updateIssue.getActivities().size();

		updateIssue.getHistory().add(history);
		String resolution = "new issue resolution";

		updateIssue.setResolution(resolution);

		try {

			updateIssue = issueService.updateIssue(updateIssue, 2);

			assertEquals("new activity size", actCount + 1, updateIssue
					.getActivities().size());

			assertEquals("new issue resolution", resolution, updateIssue
					.getResolution());

			assertEquals("new added activity type",
					IssueActivityType.RESOLUTION_CHANGE, issueService.getIssue(
							updateIssue.getId()).getActivities().get(
							updateIssue.getActivities().size() - 1)
							.getActivityType());

		} catch (ProjectException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#moveIssue(org.itracker.model.Issue, java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	public void testMoveIssue() {

		Issue issue = issueService.getIssue(1);
		assertNotNull("issue", issue);
		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(2);
		Integer actCount = issue.getActivities().size();
		assertNotNull("user#2", user);
		issue = issueService.moveIssue(issue, 3, user.getId());

		Issue reloaded = issueService.getIssue(1);

		assertEquals("issue.project.id", Integer.valueOf(3), issue.getProject()
				.getId());
		assertEquals("reloaded.project.id", Integer.valueOf(3), reloaded
				.getProject().getId());

		assertEquals("reloaded.activities.size", actCount + 1, reloaded
				.getActivities().size());

		// org.itracker.model.IssueActivityType.ISSUE_MOVE

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#assignIssue(java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	public void testAssignIssueIntegerInteger() {

		Issue issue = issueService.getIssue(2);
		assertNotNull("issue", issue);
		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(4);
		assertNotNull("user#2", user);

		assertTrue("assigned", issueService.assignIssue(issue.getId(), user
				.getId()));

		assertEquals("owner", user, issue.getOwner());

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#assignIssue(java.lang.Integer, java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	public void testAssignIssueIntegerIntegerInteger() {
		Issue issue = issueService.getIssue(2);
		assertNotNull("issue", issue);
		User user = ((UserService) applicationContext.getBean("userService"))
				.getUser(4);
		User assignerUser = ((UserService) applicationContext
				.getBean("userService")).getUser(2);
		assertNotNull("user#2", user);

		assertTrue("assigned", issueService.assignIssue(issue.getId(), user
				.getId(), assignerUser.getId()));

		assertEquals("owner", user, issue.getOwner());
	}

	/**
	 * TODO: please somebody do tests on populate (multiple?) custom fields on
	 * an issue Test method for
	 * {@link org.itracker.services.IssueService#setIssueFields(java.lang.Integer, java.util.List)}
	 * .
	 */
	@Test
	public void testSetIssueFields() {
		Issue issue = issueService.getIssue(2);
		assertNotNull("issue", issue);
		assertEquals("issue.fields.size", 2, issue.getProject().getCustomFields().size());

		assertEquals("issue.fields[0].customField", issue.getProject().getCustomFields().get(0), issue.getFields().get(0).getCustomField());
		
		IssueField field = issue.getFields().get(0);
		assertEquals("issue.fields[0].fieldType", Type.STRING, field.getCustomField().getFieldType());
		
		try {
			field.setValue("1", ITrackerResources.getBundle(Locale.US));
		} catch (IssueException e) {
			logger.error("testSetIssueFields: failed to set value", e);
			fail(e.getMessage());
		}
		
		issueService.setIssueFields(issue.getId(), issue.getFields());
		
		CustomField dateField = issue.getProject().getCustomFields().get(1);
		IssueField dateFieldValue = new IssueField(issue, dateField);
		
		// 1973-11-16
		dateFieldValue.setDateValue(new Date(122255164431l));
		
//		issue.getFields().add(dateFieldValue);
		ArrayList<IssueField> issueFields = new ArrayList<IssueField>(issue.getFields().size() + 1);
		issueFields.add(dateFieldValue);
		
		issueService.setIssueFields(issue.getId(), issueFields);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		issue = issueService.getIssue(2);
		
		assertEquals("issue.fields[0]", field, issue.getFields().get(0));
		assertEquals("issue.fields[1]", df.format(dateFieldValue.getDateValue()), df.format(issue.getFields().get(1).getDateValue().getTime()));
		
	}

	@Test
	public void testUpdateIssueCustomFields() {
		
		Issue issue = issueService.getIssue(2);
		assertNotNull("issue", issue);
		assertEquals("issue.fields.size", 2, issue.getProject().getCustomFields().size());

		assertEquals("issue.fields[0].customField", issue.getProject().getCustomFields().get(0), issue.getFields().get(0).getCustomField());
		
		IssueField field = issue.getFields().get(0);
		assertEquals("issue.fields[0].fieldType", Type.STRING, field.getCustomField().getFieldType());
		
		try {
			field.setValue("1", ITrackerResources.getBundle(Locale.US));
			
		} catch (IssueException e) {
			logger.error("testSetIssueFields: failed to set value", e);
			fail(e.getMessage());
		}
        
		try {
			issueService.updateIssue(issue, issue.getOwner().getId());
		} catch (ProjectException e) {
			logger.error("testSetIssueFields: failed to update issue", e);
			fail(e.getMessage());
		}

		
		CustomField dateField = issue.getProject().getCustomFields().get(1);
		IssueField dateFieldValue = new IssueField(issue, dateField);
		
		// 1973-11-16
		dateFieldValue.setDateValue(new Date(122255164431l));
		
		issue.getFields().add(dateFieldValue);

		try {
			issueService.updateIssue(issue, issue.getOwner().getId());
		} catch (ProjectException e) {
			logger.error("testSetIssueFields: failed to update issue", e);
			fail(e.getMessage());
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		issue = issueService.getIssue(2);
		
		assertEquals("issue.fields[0]", field, issue.getFields().get(0));
		assertEquals("issue.fields[1]", df.format(dateFieldValue.getDateValue()), df.format(issue.getFields().get(1).getDateValue().getTime()));
		
		
	}
	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#setIssueComponents(java.lang.Integer, java.util.HashSet, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testSetIssueComponents() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#setIssueVersions(java.lang.Integer, java.util.HashSet, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testSetIssueVersions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueHistory(org.itracker.model.IssueHistory)}
	 * .
	 */
	@Test
	@Ignore
	public void testAddIssueHistory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueRelation(java.lang.Integer, java.lang.Integer, int, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testAddIssueRelation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueAttachment(org.itracker.model.IssueAttachment, byte[])}
	 * .
	 */
	@Test
	@Ignore
	public void testAddIssueAttachment() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueNotification(org.itracker.model.Notification)}
	 * .
	 */
	@Test
	@Ignore
	public void testAddIssueNotification() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#removeIssueAttachment(java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testRemoveIssueAttachment() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#removeIssueHistoryEntry(java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testRemoveIssueHistoryEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#removeIssueRelation(java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testRemoveIssueRelation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueProject(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssueProject() {
		Issue issue = issueService.getIssue(2);

		assertEquals("issue project", issue.getProject(), issueService
				.getIssueProject(issue.getId()));
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueVersions(java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testGetIssueVersions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueVersionIds(java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testGetIssueVersionIds() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueCreator(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssueCreator() {

		Collection<Issue> issues = issueService.getIssuesCreatedByUser(2);

		Iterator<Issue> issuesIt = issues.iterator();
		while (issuesIt.hasNext()) {
			Issue issue = (Issue) issuesIt.next();
			assertEquals("creator", (Integer) 2, issue.getCreator().getId());
		}

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueOwner(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssueOwner() {

		Collection<Issue> issues = issueService.getIssuesOwnedByUser(2);

		Iterator<Issue> issuesIt = issues.iterator();
		while (issuesIt.hasNext()) {
			Issue issue = (Issue) issuesIt.next();
			assertEquals("creator", (Integer) 2, issue.getOwner().getId());
		}
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueActivity(java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testGetIssueActivityInteger() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueActivity(java.lang.Integer, boolean)}
	 * .
	 */
	@Test
	@Ignore
	public void testGetIssueActivityIntegerBoolean() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getAllIssueAttachmentCount()}.
	 */
	@Test
	@Ignore
	public void testGetAllIssueAttachmentCount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getLastIssueHistory(java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	public void testGetLastIssueHistory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#canViewIssue(java.lang.Integer, org.itracker.model.User)}
	 * .
	 */
	@Test
	@Ignore
	public void testCanViewIssueIntegerUser() {
		fail("Not yet implemented");
	}

	/**
	 * Simple test to search for text. Test method for
	 * {@link org.itracker.services.IssueService#searchIssues(org.itracker.model.IssueSearchQuery, org.itracker.model.User, java.util.Map)}
	 * .
	 */
	@Test
	public void testSearchIssues() {
		Issue expected = issueService.getIssue(2);
		assertNotNull("expected", expected);
		assertEquals("expected.history[0].description", "hello..", expected
				.getHistory().get(0).getDescription());

		IssueSearchQuery query = new IssueSearchQuery();

		query.setText("hello");

		ArrayList<Integer> projectIds = new ArrayList<Integer>();
		projectIds.add(2);
		query.setProjects(projectIds);

		User user = expected.getOwner();

		Map<Integer, Set<PermissionType>> permissionsMap = ServletContextUtils
				.getItrackerServices().getUserService()
				.getUsersMapOfProjectIdsAndSetOfPermissionTypes(user,
						AuthenticationConstants.REQ_SOURCE_WEB);

		try {
			List<Issue> result = issueService.searchIssues(query, user,
					permissionsMap);
			assertTrue("result.contains(expected)", result.contains(expected));
		} catch (IssueSearchException e) {
			logger.error("testSearchIssues: failed to search issues", e);
			fail(e.getMessage());
		}
	}

	@Override
	public void onSetUp() throws Exception {

		super.onSetUp();
		this.issueService = (IssueService) applicationContext
				.getBean("issueService");

	}

	protected String[] getDataSetFiles() {
		return new String[] { "dataset/userpreferencesbean_dataset.xml",
				"dataset/userbean_dataset.xml",
				"dataset/customfieldbean_dataset.xml",
				"dataset/customfieldvaluebean_dataset.xml",
				"dataset/projectbean_dataset.xml",
				"dataset/projectbean_field_rel_dataset.xml",
				"dataset/versionbean_dataset.xml",
				"dataset/permissionbean_dataset.xml",
				"dataset/issuebean_dataset.xml",
				"dataset/issuefieldbean_dataset.xml",
				"dataset/issueattachmentbean_dataset.xml",
				"dataset/issueactivitybean_dataset.xml",
				"dataset/issuehistorybean_dataset.xml",
				"dataset/notificationbean_dataset.xml" };
	}

	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}
