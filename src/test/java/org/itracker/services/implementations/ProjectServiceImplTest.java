package org.itracker.services.implementations;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Component;
import org.itracker.model.Project;
import org.itracker.services.ProjectService;
import org.junit.Ignore;
import org.junit.Test;

public class ProjectServiceImplTest extends AbstractDependencyInjectionTest {


	private ProjectService projectService;

	@Override
	protected String[] getDataSetFiles() {
		return new String[] { "dataset/userpreferencesbean_dataset.xml",
				"dataset/userbean_dataset.xml",
				"dataset/projectbean_dataset.xml",
				"dataset/componentbean_dataset.xml",
				"dataset/permissionbean_dataset.xml" };
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

		// projectDAO.detach(project);
		// componentDAO.detach(component);
		//
		// // delete
		// projectService.removeProjectComponent(project.getId(), savedComponent
		// .getId());
	}

	@Test
	@Ignore
	public void testProjectComponentRemove() {

		Project project = projectService.getProject(2);
		Assert.assertNull("project not found", project.getId());

		// component exists but fails to delete
		assertTrue(projectService.removeProjectComponent(project.getId(), 2));
	}

	@Test
	@Ignore
	public void testTryRemoveInvalidComponent() {

		Project project = projectService.getProject(2);
		Assert.assertNull("project not found", project.getId());

		// Invalid component Id, so it should fail and it does but why throws an
		// exception
		assertFalse(projectService.removeProjectComponent(project.getId(), 89));
	}
}
