/**
 * 
 */
package org.itracker.services.implementations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueActivity;
import org.itracker.model.IssueActivityType;
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.IssueRelation;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.model.CustomField.Type;
import org.itracker.persistence.dao.IssueAttachmentDAO;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.IssueHistoryDAO;
import org.itracker.persistence.dao.IssueRelationDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.services.IssueService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.exceptions.IssueSearchException;
import org.itracker.services.exceptions.ProjectException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.IssueUtilities;
import org.itracker.web.util.ServletContextUtils;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.log4j.Logger;

/**
 * @author ranks
 * 
 */
public class IssueServiceTest extends AbstractDependencyInjectionTest {

	private static final Logger logger = Logger
			.getLogger(IssueServiceTest.class);
	private IssueService issueService;
	
	private UserDAO userDAO;
	private IssueDAO issueDAO;
	private IssueRelationDAO issueRelationDAO;
	private IssueHistoryDAO issueHistoryDAO;
	private IssueAttachmentDAO issueAttachmentDAO;

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
		assertNotNull(issues);
		assertEquals("issues watched by#2", 1, issues.size());
		
		issues = issueService.getIssuesWatchedByUser(2, false);
		assertNotNull(issues);
		assertEquals("issues watched by#2 regardless of project status", 1, issues.size());
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
	public void testGetUnassignedIssues() {
		List<Issue> issues = issueService.getUnassignedIssues();
		assertNotNull(issues);
		
		// unassigned issues, status <= 200
		assertEquals("4 unassigned issues", 4, issues.size()); 
		
		issues = issueService.getUnassignedIssues(false);
		assertNotNull(issues);
		
		// unassigned issues, status <= 200
		assertEquals("4 unassigned issues", 4, issues.size());
		
		// TODO: test getUnassignedIssues(true)
		
	}

//	/**
//	 * Test method for
//	 * {@link org.itracker.services.IssueService#getUnassignedIssues(boolean)}.
//	 */
//	@Test
//	@Ignore
//	public void testGetUnassignedIssuesBoolean() {
//		fail("Not yet implemented");
//	}

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
		
		try {
			assertTrue("unassigned", issueService.assignIssue(issue.getId(), null, assignerUser.getId()));
			fail("null user allowed");
		} catch (Exception e) { /* ok */ }
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
		
		boolean added = issueService.setIssueFields(issue.getId(), new ArrayList<IssueField>());
		assertTrue(added);
		
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
	@Ignore // need to set User on IssueActivity entity
	public void testSetIssueComponents() {
		HashSet<Integer> componentIds = new HashSet<Integer>();
		componentIds.add(1);
		boolean updated = issueService.setIssueComponents(3, componentIds, 2);
		assertTrue(updated);
		issueService.getIssueComponentIds(3).contains(1);
		
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#setIssueVersions(java.lang.Integer, java.util.HashSet, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore // need to set User on IssueActivity entity
	public void testSetIssueVersions() {
		HashSet<Integer> versionIds = new HashSet<Integer>();
		versionIds.add(1);
		boolean updated = issueService.setIssueVersions(3, versionIds, 2);
		assertTrue(updated);
		issueService.getIssueVersionIds(3).contains(1);
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueHistory(org.itracker.model.IssueHistory)}
	 * .
	 */
	@Test
	public void testAddIssueHistory() {
		
		Issue issue = issueDAO.findByPrimaryKey(1);
		User user = userDAO.findByPrimaryKey(2);
		
		IssueHistory history = new IssueHistory( issue, user, "", IssueUtilities.STATUS_NEW );
		
		history.setIssue( issue );
		issueService.addIssueHistory(history);
		
		assertNotNull( issueHistoryDAO.findByPrimaryKey(history.getId()) );
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueRelation(java.lang.Integer, java.lang.Integer, int, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore // need to set User on IssueActivity entity
	public void testAddIssueRelation() {
		// connect issues 1,2
		boolean added = issueService.addIssueRelation(1,2,IssueUtilities.RELATION_TYPE_DUPLICATE_C,2);
		assertTrue(added);
		
		// TODO: test relations are saved to db, fix the 
		// org.itracker.services.IssueService#addIssueRelation first
				
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#addIssueAttachment(org.itracker.model.IssueAttachment, byte[])}
	 * .
	 */
	@Test
	public void testAddIssueAttachment() {
		Issue issue = issueDAO.findByPrimaryKey(1);
		assertNotNull(issue.getAttachments());
		int attachments = issue.getAttachments().size();
		IssueAttachment attachment = new IssueAttachment(issue, "my_file", "text/xml", "", 0);
		attachment.setUser( userDAO.findByPrimaryKey(2) );
		boolean added = issueService.addIssueAttachment(attachment, new byte[] {});
		assertTrue("attachment added", added);
		
		issue = issueDAO.findByPrimaryKey(1);
		assertNotNull(issue.getAttachments());
		assertEquals("atachment added", attachments + 1, issue.getAttachments().size());
		
	}
	
	@Test
	public void testSetIssueAttachmentData() {
		boolean modified = issueService.setIssueAttachmentData(1, new byte[]{9,8,7});
		assertTrue("attachment modified", modified);
		
		IssueAttachment attachment = issueAttachmentDAO.findByPrimaryKey(1);
		assertNotNull(attachment.getFileData());
		assertTrue("updated data", Arrays.equals(new byte[]{9,8,7}, attachment.getFileData()));
		
		
		modified = issueService.setIssueAttachmentData("Derived Filename 1", new byte[]{7,8,9});
		assertTrue("attachment modified", modified);
		
		attachment = issueAttachmentDAO.findByPrimaryKey(1);
		assertNotNull(attachment.getFileData());
		assertTrue("updated data", Arrays.equals(new byte[]{7,8,9}, attachment.getFileData()));
		
		
		
	}

//	/**
//	 * Test method for
//	 * {@link org.itracker.services.IssueService#addIssueNotification(org.itracker.model.Notification)}
//	 * 
//	 */
//	@Test
//	@Ignore
//	public void testAddIssueNotification() {
//		fail("Not yet implemented");
//	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#removeIssueAttachment(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testRemoveIssueAttachment() {
		boolean removed = issueService.removeIssueAttachment(1);
		assertTrue("attachment removed", removed);
		assertNull("no db attachment", issueAttachmentDAO.findByPrimaryKey(1));
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#removeIssueHistoryEntry(java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	// FIXME: what's the purpose of passing userId to removeIssueHistoryEntry() ?
	public void testRemoveIssueHistoryEntry() {
		IssueHistory issueHistory = issueHistoryDAO.findByPrimaryKey(1);
		assertNotNull(issueHistory);
		issueService.removeIssueHistoryEntry(1, 2);
		issueHistory = issueHistoryDAO.findByPrimaryKey(1);
		assertNull(issueHistory);
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#removeIssueRelation(java.lang.Integer, java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore // FIXME: issue relation isn't actually being removed from db
	public void testRemoveIssueRelation() {
		IssueRelation issueRelation = issueRelationDAO.findByPrimaryKey(1); // issue 1-2 connection
		assertNotNull("issueRelation", issueRelation);
		
		// FIXME: what's the purpose of passing userId to removeIssueRelation?
		issueService.removeIssueRelation(1, 2);
		
		issueRelation = issueRelationDAO.findByPrimaryKey(1); // issue 1-2 connection
		System.out.println( "issueRelation = " + issueRelation );
		assertNull("issueRelation", issueRelation);
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
	public void testGetIssueVersions() {
		
		List<Version> versions = issueService.getIssueVersions(1);
		assertNotNull(versions);
		assertEquals(1, versions.size());
		assertEquals("version id", new Integer(1), versions.get(0).getId());
		
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueVersionIds(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssueVersionIds() {
		Set<Integer> versions = issueService.getIssueVersionIds(1);
		assertNotNull(versions);
		assertEquals(1, versions.size());
		assertTrue("version id", versions.contains(new Integer(1)));
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
		
		User creator = issueService.getIssueCreator(1);
		assertNotNull(creator);
		assertEquals(new Integer(2), creator.getId());

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
		
		User owner = issueService.getIssueOwner(1);
		assertNotNull(owner);
		assertEquals(new Integer(2), owner.getId());
		
	}	

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueActivity(java.lang.Integer)}
	 * .
	 */
	@Test
	public void testGetIssueActivityInteger() {
		List<IssueActivity> issueActivities = issueService.getIssueActivity(1);
		assertNotNull(issueActivities);
		assertEquals("issue activities for issue#1", 1, issueActivities.size());
		
		issueActivities = issueService.getIssueActivity(4);
		assertNotNull(issueActivities);
		assertEquals("issue activities for issue#4", 1, issueActivities.size());

	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getIssueActivity(java.lang.Integer, boolean)}
	 * .
	 */
	@Test
	public void testGetIssueActivityIntegerBoolean() {
		List<IssueActivity> issueActivities = issueService.getIssueActivity(1, true);
		assertNotNull(issueActivities);
		assertEquals("issue activities for issue#1 (with notification)", 1, issueActivities.size());
		
		issueActivities = issueService.getIssueActivity(1, false);
		assertNotNull(issueActivities);
		assertEquals("issue activities for issue#1 (without notification)", 0, issueActivities.size());
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getAllIssueAttachmentCount()}.
	 */
	@Test
	public void testGetAllIssueAttachmentCount() {
		assertEquals("total attachments", new Long(4), issueService.getAllIssueAttachmentCount()); 
	}
	
	@Test
	public void testGetAllIssueAttachmentSize() {
		assertEquals("total attachments", new Long(4), issueService.getAllIssueAttachmentSize()); 
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetAllIssueAttachmentsSizeAndCount() {
		long[] sizeAndCount = issueService.getAllIssueAttachmentsSizeAndCount();
		assertNotNull(sizeAndCount);
		assertEquals("size and count", 2, sizeAndCount.length); 
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#getLastIssueHistory(java.lang.Integer)}
	 * .
	 */
	@Test
	@Ignore
	// FIXME: fix getLastIssueHistory() method first, it always returns null
	public void testGetLastIssueHistory() {
		IssueHistory issueHistory = issueService.getLastIssueHistory(2);
		assertNotNull( "issueHistory", issueHistory );
		assertEquals( "issueHistory id", new Integer(1), issueHistory.getId() );
	}

	/**
	 * Test method for
	 * {@link org.itracker.services.IssueService#canViewIssue(java.lang.Integer, org.itracker.model.User)}
	 * .
	 */
	@Test
	@Ignore // FIXME: first test fails however user 2 is an owner and creator of issue#1
	public void testCanViewIssue() {
		
		Issue issue1 = issueDAO.findByPrimaryKey(1);
		
		assertTrue("view issue#1 permission for user#2", 
				issueService.canViewIssue(1, userDAO.findByPrimaryKey(2)));
		assertTrue("view issue#1 permission for user#2", 
				issueService.canViewIssue(issue1, userDAO.findByPrimaryKey(2)));
		
		assertFalse("view issue#1 permission for user#3", 
				issueService.canViewIssue(1, userDAO.findByPrimaryKey(3)));
		assertFalse("view issue#1 permission for user#3", 
				issueService.canViewIssue(issue1, userDAO.findByPrimaryKey(3)));
		
		assertTrue("view issue#1 permission for user#4", 
				issueService.canViewIssue(1, userDAO.findByPrimaryKey(4)));
		assertTrue("view issue#1 permission for user#4", 
				issueService.canViewIssue(issue1, userDAO.findByPrimaryKey(4)));
		
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


	@Test
	public void testGetIssueComponents() {
		List<Component> components = issueService.getIssueComponents(1);
		assertNotNull(components);
		assertEquals(1, components.size());
		
		components = issueService.getIssueComponents(4);
		assertNotNull(components);
		assertEquals(0, components.size());
		
	}
	
	@Test
	public void testGetIssueComponentIds() {
		Set<Integer> componentIds = issueService.getIssueComponentIds(1);
		assertNotNull(componentIds);
		assertEquals("component ids for issue#1", 1, componentIds.size());
		
		componentIds = issueService.getIssueComponentIds(4);
		assertNotNull(componentIds);
		assertEquals("component ids for issue#4",0, componentIds.size());
		
	}
	
	@Test
	public void testGetIssueAttachments() {
		List<IssueAttachment> attachments = issueService.getIssueAttachments(1);
		assertNotNull(attachments);
		assertEquals(4, attachments.size());
		
		attachments = issueService.getIssueAttachments(2);
		assertNotNull(attachments);
		assertEquals(0, attachments.size());
		
	}
	
	@Test
	public void testGetIssueAttachment() {
		IssueAttachment attachment = issueService.getIssueAttachment(1);
		assertNotNull(attachment);
		assertEquals("attachment id", new Integer(1), attachment.getId());
		assertEquals("attachment file name", "Derived Filename 1", attachment.getFileName());
		
	}
	
	@Test
	public void testGetIssueAttachmentData() {
		byte[] data = issueService.getIssueAttachmentData(1);
		assertNotNull(data);
		assertEquals("attachment1.xml size", 44, data.length);
		
	}
	
	@Test
	public void testGetIssueHistory() {
		List<IssueHistory> historyItems = issueService.getIssueHistory(1);
		assertNotNull(historyItems);
		assertEquals(0, historyItems.size());
		
		historyItems = issueService.getIssueHistory(2);
		assertNotNull(historyItems);
		assertEquals(1, historyItems.size());
		
	}
	
	@Test
	public void testGetIssueAttachmentCount() {
		assertEquals("attachment count for issue#1", 4, issueService.getIssueAttachmentCount(1));
		assertEquals("attachment count for issue#4", 0, issueService.getIssueAttachmentCount(4));
		
	}
	
	@Test
	public void testGetOpenIssueCountByProjectId() {
		assertEquals("open issues for project#2", 4, issueService.getOpenIssueCountByProjectId(2));
		assertEquals("open issues for project#3", 0, issueService.getOpenIssueCountByProjectId(3));
		
	}
	
	@Test
	public void testGetResolvedIssueCountByProjectId() {
		assertEquals("resolved issues for project#2", 0, issueService.getResolvedIssueCountByProjectId(2));
		assertEquals("resolved issues for project#3", 0, issueService.getResolvedIssueCountByProjectId(3));
		
	}
	
	@Test
	public void testGetTotalIssueCountByProjectId() {
		assertEquals("total issues for project#2", 4, issueService.getTotalIssueCountByProjectId(2));
		assertEquals("total issues for project#3", 0, issueService.getTotalIssueCountByProjectId(3));
		
	}
	
	@Test
	public void testGetLatestIssueDateByProjectId() {
		Date date = issueService.getLatestIssueDateByProjectId(2);
		assertEquals("latest issue date for project#2", "2008-01-01", new SimpleDateFormat("yyyy-MM-dd").format(date));
		assertNull("latest issue date for project#3", issueService.getLatestIssueDateByProjectId(3));
		
	}
	
	
	@Test
	@Ignore // need to set User on IssueActivity entity
	public void testSystemUpdateIssue() {
		Issue issue = issueDAO.findByPrimaryKey(1);
		try {
			issueService.systemUpdateIssue(issue, 2);
			issue = issueDAO.findByPrimaryKey(1);
			assertNotNull(issue);
			assertNotNull(issue.getActivities());
			boolean hasSystemTypeActivity = false;
			for (IssueActivity activity : issue.getActivities()) {
				if (IssueActivityType.SYSTEM_UPDATE.equals(activity.getActivityType())) {
					hasSystemTypeActivity = true;
					break;
				}
			}
			assertTrue("has SYSTEM_UPDATE activity", hasSystemTypeActivity);
			
		} catch (ProjectException e) {
			fail(e.getMessage());
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSetNotificationService() {
		List<Issue> issues = issueService.getAllIssues();
		assertNotNull(issues);
		assertEquals("4 issues", 4, issues.size());
	}
	
	@Test
	public void testGetIssueRelation() {
		IssueRelation issueRelation = issueService.getIssueRelation(1);
		assertNotNull(issueRelation);
		assertNotNull("issue",issueRelation.getIssue());
		assertNotNull("related issue",issueRelation.getRelatedIssue());
		assertEquals("issue 1", new Integer(1), issueRelation.getIssue().getId());
		assertEquals("issue 2", new Integer(2), issueRelation.getRelatedIssue().getId());
		
	}

	@Override
	public void onSetUp() throws Exception {

		super.onSetUp();
		this.issueService = (IssueService) applicationContext
		.getBean("issueService");
		
		this.userDAO = (UserDAO) applicationContext.getBean("userDAO");
		this.issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
		this.issueRelationDAO = (IssueRelationDAO) applicationContext.getBean("issueRelationDAO");
		this.issueHistoryDAO = (IssueHistoryDAO) applicationContext.getBean("issueHistoryDAO");
		this.issueHistoryDAO = (IssueHistoryDAO) applicationContext.getBean("issueHistoryDAO");
		this.issueAttachmentDAO = (IssueAttachmentDAO) applicationContext.getBean("issueAttachmentDAO");

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
				"dataset/notificationbean_dataset.xml",
				"dataset/componentbean_dataset.xml",
				"dataset/issue_component_rel_dataset.xml",
				"dataset/issue_version_rel_dataset.xml",
				"dataset/issuerelationbean_dataset.xml",
				 };
	}

	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}
