package org.itracker.web.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Helper utils for admin project actions.
 */
public class AdminProjectUtilities {
    private static final Logger log = Logger.getLogger(AdminProjectUtilities.class);

    /**
     * When creating project, initial set of users with specific set of rights
     * can be defined.
     *
     * @param project        the project
     * @param userIds        the user IDs
     * @param permissions    the permissions
     * @param projectService the project service
     * @param userService    the user service
     */
    public static void handleInitialProjectMembers(Project project,
                                                   Set<Integer> userIds, Set<Integer> permissions,
                                                   ProjectService projectService, UserService userService) {
        List<Permission> userPermissionModels = new ArrayList<Permission>();
        if (userIds != null && permissions != null && userIds.size() > 0
                && permissions.size() > 0) {

            Set<User> users = new HashSet<User>(userIds.size());
            for (Integer userId : userIds)
                users.add(userService.getUser(userId));

            // process member-users
            for (User user : users) {
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
                for (Integer type : permissions)
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
     * @param project     the project
     * @param userIds     the user IDs
     * @param userService the user service
     * @param locale      the user language locale
     */
    public static final void updateProjectOwners(Project project,
                                                 Set<Integer> userIds, ProjectService projectService,
                                                 UserService userService) {
        Set<Permission> userPermissionModels;

        if (log.isDebugEnabled()) {
            log.debug("updateProjectOwners: setting new owners: " + userIds);
        }

        // add all defined owners to project
        for (Integer userId : userIds) {
            User usermodel = userService.getUser(userId);
            boolean newPermissions = false;
            userPermissionModels = new HashSet<Permission>(userService.getUserPermissionsLocal(usermodel));
            if (log.isDebugEnabled()) {
                log.debug("updateProjectOwners: setting owner " + usermodel + " to " + project);
            }
            for (Integer permission : UserUtilities.ALL_PERMISSIONS_SET) {
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

    public static final void setFormProperties(Project project, ProjectService projectService,
                                               ActionForm form, ActionMessages errors)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        project.setDescription((String) PropertyUtils.getSimpleProperty(
                form, "description"));
        project.setName((String) PropertyUtils.getSimpleProperty(form,
                "name"));
        Integer projectStatus = (Integer) PropertyUtils.getSimpleProperty(
                form, "status");

        String projectName = (String) PropertyUtils.getSimpleProperty(form, "name");

        project.setName(projectName);


        if (errors.isEmpty()) {
            if (projectStatus != null) {
                project.setStatus(Status.valueOf(projectStatus));
            } else {
                project.setStatus(Status.ACTIVE);
            }

            Integer[] optionValues = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "options");
            int optionmask = 0;
            if (optionValues != null) {
                for (int i = 0; i < optionValues.length; i++) {
                    optionmask += optionValues[i].intValue();
                }
            }
            project.setOptions(optionmask);

            Integer[] fieldsArray = (Integer[]) PropertyUtils.getSimpleProperty(form, "fields");
            Set<Integer> fields = null == fieldsArray ? new HashSet<Integer>(0) :
                    new HashSet<Integer>(Arrays.asList(fieldsArray));

            projectService.setProjectFields(project, fields);

        }
    }
}
