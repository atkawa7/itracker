package org.itracker.web.actions.admin.project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;

/**
 * Helper utils for admin project actions.
 *
 */
public class AdminProjectUtilities {
	private static final Logger log = Logger.getLogger(AdminProjectUtilities.class);

	/**
	 * When creating project, initial set of users with specific set of rights
	 * can be defined.
	 * 
	 * @param project the project
	 * @param userIds the user IDs
	 * @param permissions the permissions
	 * @param projectService the project service
	 * @param userService the user service
	 */
	public static void handleInitialProjectMembers(Project project,
			Set<Integer> userIds, Set<Integer> permissions,
			ProjectService projectService, UserService userService) {
		List<Permission> userPermissionModels = new ArrayList<Permission>();
		if (userIds != null && permissions != null && userIds.size() > 0
				&& permissions.size() > 0) {

			Set<User> users = new HashSet<User>(userIds.size());
			for (Integer userId: userIds) 
				users.add(userService.getUser(userId));
			
			// process member-users
			for (User user: users) {
				userPermissionModels = userService.getUserPermissionsLocal(user);

				// remove all user permissions for current project
				//				Iterator<Permission> userPIterator = userPermissionModels
				//						.iterator();
				//				while (userPIterator.hasNext()) {
				//					Permission permission = (Permission) userPIterator.next();
				//					if (project.equals(permission.getProject())) {
				//						userPermissionModels.remove(permission);
				//					}
				//				}
				// add all needed permissions
				for (Integer type: permissions) 
					userPermissionModels.add(new Permission(type, user, project));

				// save the permissions
				userService.setUserPermissions(user.getId(), userPermissionModels);
				userService.updateAuthenticator(user.getId(), userPermissionModels);
			}
		}

	}


	/**
	 * Setup permissions for updated project-owners.
	 * 
	 * @param project the project
	 * @param userIds the user IDs
	 * @param userService the user service
	 * @param locale the user language locale
	 */
	public static final void updateProjectOwners(Project project,
			Set<Integer> userIds, ProjectService projectService,
			UserService userService) {
		Set<Permission> userPermissionModels;

		if (log.isDebugEnabled()) {
			log.debug("updateProjectOwners: setting new owners: " + userIds);
		}

		// cleanup current owners permissions
		// TODO: needed? If user is no more owner, he can still be admin
		//		Collection<User> currentOwners = projectService
		//				.getProjectOwners(project.getId());

		//		Iterator<User> currentOwnersIt = currentOwners.iterator();
		//		while (currentOwnersIt.hasNext()) {
		//			User user = (User) currentOwnersIt.next();
		//			Iterator<Permission> currentPermissionsIt = userService.getUserPermissionsLocal(user).iterator();
		//			while (currentPermissionsIt.hasNext()) {
		//				Permission permission = (Permission) currentPermissionsIt
		//						.next();
		//				if (project.equals(permission.getProject()) && 
		//						permission.getPermissionType() == UserUtilities.PERMISSION_PRODUCT_ADMIN) {
		//					user.getPermissions().remove(permission);
		//				}
		//				userService.setUserPermissions(user.getId(), user.getPermissions());
		//			}
		//		}

		// remove all owners
		//		projectService.setProjectOwners(project, new HashSet<Integer>(0));

		// add all defined owners to project
		for (Integer userId: userIds) {
			User usermodel = userService.getUser(userId);
			boolean newPermissions = false;
			userPermissionModels = new HashSet<Permission>(userService.getUserPermissionsLocal(usermodel));
			if (log.isDebugEnabled()) {
				log.debug("updateProjectOwners: setting owner " + usermodel + " to " + project);
			}
			for (Integer permission: UserUtilities.ALL_PERMISSIONS_SET) {
				if (userPermissionModels.add(new Permission(permission,
						usermodel, project))) {
					newPermissions = true;
				}
			}
			if (newPermissions) {
				userService.addUserPermissions(usermodel.getId(),
						new ArrayList<Permission>(userPermissionModels));
				if (log.isDebugEnabled()) {
					log.debug("updateProjectOwners: updated permissions for " + usermodel + " to " + userPermissionModels);
				}
			}
		}
		
		projectService.setProjectOwners(project, userIds);
	}

}
