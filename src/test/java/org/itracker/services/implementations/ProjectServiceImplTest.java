package org.itracker.services.implementations;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.Status;
import org.itracker.model.Version;
import org.itracker.model.WorkflowScript;
import org.itracker.persistence.dao.ComponentDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.ProjectScriptDAO;
import org.itracker.persistence.dao.VersionDAO;
import org.itracker.persistence.dao.WorkflowScriptDAO;
import org.itracker.services.ProjectService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class ProjectServiceImplTest extends AbstractDependencyInjectionTest {

	private ProjectDAO projectDAO;
	private ProjectService projectService;
	private ComponentDAO componentDAO;
	private VersionDAO versionDAO;
	private ProjectScriptDAO projectScriptDAO;
	private WorkflowScriptDAO workflowScriptDAO;

	@Override
	protected String[] getDataSetFiles() {
		return new String[] {

		"dataset/userpreferencesbean_dataset.xml",
				"dataset/userbean_dataset.xml",
				"dataset/customfieldbean_dataset.xml",
				"dataset/customfieldvaluebean_dataset.xml",
				"dataset/projectbean_dataset.xml",
				"dataset/project_owner_rel_dataset.xml",
				"dataset/projectbean_field_rel_dataset.xml",
				"dataset/workflowscriptbean_dataset.xml",
				"dataset/projectscriptbean_dataset.xml",
				"dataset/componentbean_dataset.xml",
				"dataset/versionbean_dataset.xml",
				"dataset/permissionbean_dataset.xml",
				"dataset/issuebean_dataset.xml",
				"dataset/issuefieldbean_dataset.xml",
				"dataset/issueattachmentbean_dataset.xml",
				"dataset/issueactivitybean_dataset.xml",
				"dataset/issuehistorybean_dataset.xml",
				"dataset/notificationbean_dataset.xml" };

	}

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

	@Override
	public void onSetUp() throws Exception {

		super.onSetUp();
		projectService = (ProjectService) applicationContext
				.getBean("projectService");
		projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		versionDAO = (VersionDAO) applicationContext.getBean("versionDAO");
		projectScriptDAO = (ProjectScriptDAO) applicationContext
				.getBean("projectScriptDAO");
		workflowScriptDAO = (WorkflowScriptDAO) applicationContext
				.getBean("workflowScriptDAO");

		// userService = (UserService)
		// applicationContext.getBean("userService");
		// userPreferencesDAO = (UserPreferencesDAO) applicationContext
		// .getBean("userPreferencesDAO");
		// projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
		// userDAO = (UserDAO) applicationContext.getBean("userDAO");
		// permissionDAO = (PermissionDAO) applicationContext
		// .getBean("permissionDAO");

	}

	@Test
	public void testGetProject() {
		Project project = projectService.getProject(2);
		Assert.assertNotNull("project #2", project);
		Assert.assertEquals("name", "test_name", project.getName());
		Assert.assertEquals("description", "test_description", project
				.getDescription());

		project = projectService.getProject(2);
		Assert.assertNotNull("project #3", project);
		Assert.assertEquals("name", "test_name", project.getName());
		Assert.assertEquals("description", "test_description", project
				.getDescription());

	}

	@Test
	public void testGetAllProjects() {
		List<Project> projects = projectService.getAllProjects();
		Assert.assertNotNull("allProjects", projects);
		Assert.assertEquals("allProjects", 2, projects.size());
	}

	@Test
	public void testGetAllAvailableProjects() {
		List<Project> projects = projectService.getAllAvailableProjects();
		Assert.assertNotNull("allAvailableProjects", projects);
		Assert.assertEquals("allAvailableProjects", 2, projects.size());
	}

	@Test
	public void testProjectComponents() {

		Project project = projectService.getProject(2);
		Component component = new Component(project, "new_component");
		component.setDescription("new_decription");
		Assert.assertNull("addProjectComponent", component.getId());
		Date then = new Date();

		int numberOfComponents = project.getComponents().size();

		projectService.addProjectComponent(project.getId(), component);
		Assert.assertNotNull("addProjectComponent", component.getId());
		assertEquals("component size", numberOfComponents + 1, project
				.getComponents().size());

		// refresh project bean
		project = projectService.getProject(project.getId());
		assertEquals("component size", numberOfComponents + 1, project
				.getComponents().size());

		// FIXME: since a component is just added to the project, shouldn't this
		// hold?
		// assertTrue("date modified",
		// project.getLastModifiedDate().after(then));
		assertFalse("date created", project.getCreateDate().after(then));

		// refresh saved component
		Component savedComponent = projectService.getProjectComponent(component
				.getId());
		Assert.assertNotNull("addProjectComponent", savedComponent);
		Assert.assertEquals("addProjectComponent", savedComponent.getName(),
				component.getName());
		Assert.assertEquals("addProjectComponent", savedComponent
				.getDescription(), component.getDescription());

		// FIXME: the component is just created, shouldn't this hold?
		// assertTrue("date modified",
		// savedComponent.getLastModifiedDate().after(then));
		// assertTrue("date created",
		// savedComponent.getCreateDate().after(then));
		assertEquals("parent project", project, savedComponent.getProject());

		then = new Date();
		savedComponent.setName("new_name");
		projectService.updateProjectComponent(savedComponent);

		Component updatedComponent = projectService
				.getProjectComponent(savedComponent.getId());
		assertEquals(savedComponent.getName(), updatedComponent.getName());
		assertEquals(savedComponent.getDescription(), updatedComponent
				.getDescription());
		// FIXME: I just updated the component??
		// assertTrue("date modified", updatedComponent.getLastModifiedDate()
		// .after(then));

		assertFalse("date created", updatedComponent.getCreateDate()
				.after(then));

		projectDAO.detach(project);
		componentDAO.detach(component);

		// // delete
		// projectService.removeProjectComponent(project.getId(), savedComponent
		// .getId());
	}

	@Test
	@Ignore
	public void testProjectComponentRemove() {

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project.getId());

		// component exists but fails to delete
		assertTrue(projectService.removeProjectComponent(project.getId(), 2));
	}

	@Test
	@Ignore
	public void testTryRemoveInvalidComponent() {

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project.getId());

		// Invalid component Id, so it should fail and it does but why throws an
		// exception
		assertFalse(projectService.removeProjectComponent(project.getId(), 89));
	}

	@Test
	public void testProjectVersions() {

		Version version = projectService.getProjectVersion(1);
		assertNotNull("version not found", version);

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project.getId());

		assertTrue("versions don't match", project.getVersions().size() == 1);

		version = new Version(project, "2.0");

		// FIXME: looks like version description is required, it should be
		// included in one of the constructors.
		version.setDescription("");
		projectService.addProjectVersion(project.getId(), version);

		project = projectService.getProject(2);
		assertTrue("versions dont' match", project.getVersions().size() == 2);

		versionDAO.detach(version);
		version.setMinor(2);
		projectService.updateProjectVersion(version);

		version = projectService.getProjectVersion(version.getId());
		assertEquals("version not updated", version.getMinor(), 2);
	}

	@Test
	@Ignore
	public void removeProjectVersion() {

		Version version = projectService.getProjectVersion(1);
		assertNotNull("version not found", version);

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project.getId());

		projectService.removeProjectVersion(project.getId(), version.getId());
		assertNull("version not removed", projectService
				.getProjectVersion(version.getId()));

	}

	@Test
	public void tryInsertDuplicateVersion() {

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project.getId());

		assertTrue("versions don't match", project.getVersions().size() == 1);
		Version version = project.getVersions().get(0);
		versionDAO.detach(version);
		version.setId(null);
		try {
			projectService.addProjectVersion(project.getId(), version);
			fail();
		} catch (DataIntegrityViolationException e) {
			// FIXME: Isn't a more specific exception like
			// VersionAlreadyExists
			// more appropriate?

		}
	}

	@Test
	public void testProjectOwner() {
		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project.getId());

		assertEquals(projectService.getProjectOwners(project.getId()).size(),
				project.getOwners().size());

		Set<Integer> newOwners = new HashSet<Integer>();
		newOwners.add(1);
		projectService.setProjectOwners(project, newOwners);

		assertEquals(1, projectService.getProjectOwners(project.getId()).size());

		// projectService.setProjectOwners(project, new HashSet<Integer>());

	}

	@Test
	public void testGetTotalNumberIssuesByProject() {
		Long issuesByProject = projectService.getTotalNumberIssuesByProject(2);
		assertEquals(Long.valueOf(4), issuesByProject);
	}

	@Test
	public void testCountIssuesByVersion() {
		Long issuesByVersion = projectService.countIssuesByVersion(2);
		assertEquals(Long.valueOf(0), issuesByVersion);

	}

	@Test
	public void testCountIssuesByComponent() {
		Long issuesByComponent = projectService.countIssuesByComponent(2);
		assertEquals(Long.valueOf(0), issuesByComponent);
	}

	@Test
	public void testGetTotalNumberOpenIssuesByProject() {
		Long issues = projectService.countIssuesByComponent(2);
		assertEquals(Long.valueOf(0), issues);
	}

	@Test
	public void testGetTotalNumberResolvedIssuesByProject() {
		Long number = projectService.getTotalNumberResolvedIssuesByProject(2);
		assertEquals(Long.valueOf(0), number);
	}

	@Test
	public void getgetTotalNumberOpenIssuesByProject() {
		Long number = projectService.getTotalNumberOpenIssuesByProject(2);
		assertEquals(Long.valueOf(4), number);
	}

	@Test
	public void getLatestIssueUpdatedDateByProjectId() throws ParseException {
		Date date = projectService.getLatestIssueUpdatedDateByProjectId(2);
		assertNotNull(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(2008, cal.get(Calendar.YEAR));
		assertEquals(0, cal.get(Calendar.MONTH));
		assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
	}

	// @Test
	// public void verifyDAOFields() {
	// assertNotNull("DAO null", projectService.getProjectDAO());
	// }

	@Test
	public void testProjectScripts() {
		ProjectScript projectScript = projectService.getProjectScript(1);
		assertNotNull("project script not found", projectScript);

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project);

		assertEquals("versions don't match", 1, project.getScripts().size());
		assertEquals("versions don't match", 1, projectService
				.getProjectScripts().size());

		projectScript = new ProjectScript();
		projectScript.setProject(project);
		projectScript.setPriority(1);
		projectScript.setFieldId(2);

		WorkflowScript script = workflowScriptDAO.findByPrimaryKey(2);
		assertNotNull("workflow script not found", script);
		projectScript.setScript(script);

		projectScript = projectService.addProjectScript(project.getId(),
				projectScript);

		projectDAO.refresh(project);
		assertEquals("project scripts don't match", 2, project.getScripts()
				.size());

		projectDAO.detach(project);
		projectScriptDAO.detach(projectScript);
		projectScript.setPriority(2);
		projectService.updateProjectScript(projectScript);

		projectScript = projectService.getProjectScript(projectScript.getId());
		assertEquals("version not updated", projectScript.getPriority(), 2);

		projectService.removeProjectScript(project.getId(), projectScript
				.getId());

		project = projectService.getProject(2);
		assertEquals("project scripts don't match", 1, project.getScripts()
				.size());
		assertNull("project script not removed", projectService
				.getProjectScript(projectScript.getId()));

	}

	@Test
	public void testProjectCustomFields() {

		Project project = projectService.getProject(2);
		Assert.assertNotNull("project not found", project);

		List<CustomField> fields = projectService.getProjectFields(project
				.getId());
		assertEquals("custom field count", 2, fields.size());

		Set<Integer> Ids = new HashSet<Integer>(2);
		projectService.setProjectFields(project, Ids);

		assertEquals("custom field count", 0, projectService.getProjectFields(
				project.getId()).size());
		Ids.add(1);
		Ids.add(2);
		projectService.setProjectFields(project, Ids);

		assertEquals("custom field count", 2, projectService.getProjectFields(
				project.getId()).size());

	}

	// @Test
	// public void testProjectStats() {
	// Long[] projectStats = projectService.getProjectStats(2);
	// assertNotNull(projectStats);
	// assertEquals(2, projectStats.length);
	// }

	@Ignore
	public void testGetListOfProjectFields() {
		fail("not implemented");
	}

	@Ignore
	public void getListOfProjectOwners() {
		fail("not implemented");
	}

	@Test
	public void testProjectCreateUpdate() {
		Project project = new Project("New Project");
		project.setDescription("desc");

		Project createProject = projectService.createProject(project, 1);
		assertNotNull(createProject);
		project = projectService.getProject(createProject.getId());
		assertNotNull(project);

		project = new Project("New Project2");
		project.setDescription("desc");
		List<Component> components = new ArrayList<Component>();
		Component component = new Component(project, "comp");
		component.setDescription("comp desc");
		components.add(component);
		project.setComponents(components);
		createProject = projectService.createProject(project, 1);
		assertNotNull(createProject);
		project = projectService.getProject(createProject.getId());
		assertNotNull(project);

		// FIXME: updateProject fails if the following line is enabled, which
		// means it assumes the project attached to current hibernate session;
		// while this may work it shouldn't be a precondition for a service
		// class (by definition those are meant to be stateless). I guess this
		// can be fixed by replacing saveOrUpdate() with a merge() call over
		// project in the impl.
		// projectDAO.detach(project);

		Version version = new Version();
		version.setProject(project);
		version.setVersionInfo("2.0");
		version.setDescription("version one");
		version.setStatus(Status.ACTIVE);
		project.setVersions(Arrays.asList(version));
		projectService.updateProject(project, 1);

		project = projectService.getProject(project.getId());
		assertNotNull(project);
		assertEquals("version not updated", 1, project.getVersions().size());

	}
}
