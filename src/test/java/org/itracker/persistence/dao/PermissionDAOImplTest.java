package org.itracker.persistence.dao;

import java.sql.SQLException;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

public class PermissionDAOImplTest extends AbstractDependencyInjectionTest {

    @SuppressWarnings("unused")
	private UserDAO userDAO;
    @SuppressWarnings("unused")
	private ProjectDAO projectDAO;
	private PermissionDAO permissionDAO;

	@Ignore
	public void testFindByUserId() {

		List<Permission> permissions = permissionDAO.findByUserId(2);

		assertNotNull(permissions);
		assertEquals(3, permissions.size());

		permissions = permissionDAO.findByUserId(-1);

		assertNull(permissions);

	}

	@Ignore
	public void testFailedFindByUserId() {

		try {
			getDataSource().getConnection().close();
			List<Permission> permissions = permissionDAO.findByUserId(-1);
			fail("Should have thrown a DataAccessException. Size of list:"
					+ permissions.size());
		} catch (DataAccessException e) {
			// Expected behavior
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreatePermission() {

		Permission foundPermission = permissionDAO.findByUserId(2).get(1);
		User foundUser = foundPermission.getUser();
		Project foundProject = foundPermission.getProject();

		assertNotNull(foundPermission);
		assertNotNull(foundUser);
		assertNotNull(foundProject);
		assertEquals(2, foundPermission.getPermissionType());
		assertEquals("admin_test1", foundUser.getLogin());
		assertEquals("test_name", foundProject.getName());

	}

	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();
		userDAO = (UserDAO) applicationContext.getBean("userDAO");
		projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
		permissionDAO = (PermissionDAO) applicationContext
				.getBean("permissionDAO");
	}

	protected String[] getDataSetFiles() {
		return new String[] {
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
				"dataset/projectbean_dataset.xml",
				"dataset/permissionbean_dataset.xml" };
	}

	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}
