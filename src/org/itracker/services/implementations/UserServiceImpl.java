/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.services.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// import javax.jms.JMSException;
// import javax.jms.MapMessage;
// import javax.jms.Queue;
// import javax.jms.QueueConnection;
// import javax.jms.QueueConnectionFactory;
// import javax.jms.QueueSender;
// import javax.jms.QueueSession;
// import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;

import org.itracker.services.authentication.PluggableAuthenticator;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.persistence.dao.NotificationDAO;
import org.itracker.persistence.dao.PermissionDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.ReportDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.persistence.dao.UserPreferencesDAO;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ProjectService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.services.exceptions.PasswordException;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.AuthenticationConstants;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;

//import org.itracker.services.message.NotificationMessageBean;
// import org.itracker.services.util.NotificationUtilities;

/**
 * Implements the UserService interface. See that interface for method
 * descriptions.
 * 
 * @see UserService
 */
public class UserServiceImpl implements UserService {
    
    private static final String DEFAULT_AUTHENTICATOR = 
            "org.itracker.services.authentication.DefaultAuthenticator";

   //  private static String notificationFactoryName = NotificationMessageBean.DEFAULT_CONNECTION_FACTORY;

//     private static String notificationQueueName = NotificationMessageBean.DEFAULT_QUEUE_NAME;

    private static String authenticatorClassName = null;
    private static Class authenticatorClass = null;
    private static String systemBaseURL = "";
    private static boolean allowSelfRegister = false;
    
    private final Logger logger;
    @SuppressWarnings("unused")
	private InitialContext initialContext = null;
    @SuppressWarnings("unused")
	private NotificationDAO notificationDAO = null;
    private PermissionDAO permissionDAO = null;
    private ProjectDAO projectDAO = null;
    @SuppressWarnings("unused")
	private ReportDAO reportDAO = null;
    private UserDAO userDAO = null;
    private UserPreferencesDAO userPreferencesDAO = null;
    private ProjectService projectService;
    private ConfigurationService configurationService;

    public UserServiceImpl(ConfigurationService configurationService, 
            ProjectService projectService, 
            UserDAO userDAO, 
            ProjectDAO projectDAO, 
            ReportDAO reportDAO, 
            PermissionDAO permissionDAO, 
            UserPreferencesDAO userPreferencesDAO) {
        this.logger = Logger.getLogger(getClass());
        this.configurationService = configurationService;
        this.projectService = projectService;
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.reportDAO = reportDAO;
        this.userPreferencesDAO = userPreferencesDAO;
        this.permissionDAO = permissionDAO;
        
        try {
            initialContext = new InitialContext();

            allowSelfRegister = configurationService.getBooleanProperty("allow_self_register", false);
            systemBaseURL = configurationService.getProperty("system_base_url", "");

            authenticatorClassName = configurationService.getProperty("authenticator_class", DEFAULT_AUTHENTICATOR);
            authenticatorClass = Class.forName(authenticatorClassName);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public User getUser(Integer userId) {
        User user = userDAO.findByPrimaryKey(userId);
        return user;
    }

    public User getUserByLogin(String login) throws NoSuchEntityException {
        User user = userDAO.findByLogin(login);
        if (user == null)
            throw new NoSuchEntityException("User " + login + " not found.");
        return user;
    }

    public String getUserPasswordByLogin(String login) {
        User user = userDAO.findByLogin(login);
        return user.getPassword();
    }

    public List<User> getAllUsers() {
        List<User> users = userDAO.findAll();
        
        return users;
    }

    public int getNumberUsers() {
        Collection<User> users = userDAO.findAll();
        return users.size();
    }

    public List<User> getActiveUsers() {
        List<User> users = userDAO.findActive();
        
        return users;
    }

    public List<User> getSuperUsers() {
        List<User> superUsers = userDAO.findSuperUsers();
        return superUsers;
    }

    public UserPreferences getUserPreferencesByUserId(Integer userId) {

        UserPreferences userPrefs = userPreferencesDAO.findByUserId(userId);
        if (userPrefs == null)
            return new UserPreferences();

        return userPrefs;
    }

    public User createUser(User user) throws UserException {
        try {
            if (user == null || user.getLogin() == null || user.getLogin().equals("")) {
                throw new UserException("User data was null, or login was empty.");
            }

            try {
                this.getUserByLogin(user.getLogin());
                throw new UserException("User already exists with login: " + user.getLogin());
            } catch (NoSuchEntityException e) {
                // doesn't exist, we'll create him
            }

            try {
                PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
                if (authenticator != null) {
                    HashMap<String,Object> values = new HashMap<String,Object>();
                    values.put("userService", this);
                    values.put("configurationService", configurationService);
                    authenticator.initialize(values);
                    authenticator.createProfile(user, null, AuthenticationConstants.AUTH_TYPE_UNKNOWN,
                            AuthenticationConstants.REQ_SOURCE_UNKNOWN);
                } else {
                    throw new AuthenticatorException("Unable to create new authenticator.", AuthenticatorException.SYSTEM_ERROR);
                }
            } catch (IllegalAccessException ex) {
                throw new AuthenticatorException(
                        "Authenticator class " + authenticatorClassName + " can not be instantiated.", 
                        AuthenticatorException.SYSTEM_ERROR, ex);
            } catch (InstantiationException ex) {
                throw new AuthenticatorException(
                        "Authenticator class " + authenticatorClassName + " can not be instantiated.", 
                        AuthenticatorException.SYSTEM_ERROR, ex);
            } catch (ClassCastException ex) {
                throw new AuthenticatorException("Authenticator class " + authenticatorClassName
                        + " does not extend the PluggableAuthenticator class.", 
                        AuthenticatorException.SYSTEM_ERROR, ex);
            }
            user.setStatus(UserUtilities.STATUS_ACTIVE);
            user.setRegistrationType(user.getRegistrationType());
            user.setCreateDate(new Date());
            user.setLastModifiedDate(user.getCreateDate());
            userDAO.save(user);
            return user;
        } catch (AuthenticatorException ex) {
            throw new UserException("Could not create user.", ex);
        }

    }

    public User updateUser(User user) throws UserException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                authenticator.updateProfile(user, AuthenticationConstants.UPDATE_TYPE_CORE, null,
                        AuthenticationConstants.AUTH_TYPE_UNKNOWN, AuthenticationConstants.REQ_SOURCE_UNKNOWN);
            } else {
                throw new AuthenticatorException("Unable to create new authenticator.", 
                        AuthenticatorException.SYSTEM_ERROR);
            }
        } catch (IllegalAccessException ex) {
            throw new AuthenticatorException(
                    "Authenticator class " + authenticatorClassName + " can not be instantiated.", 
                    AuthenticatorException.SYSTEM_ERROR, ex);
        } catch (InstantiationException ex) {
            throw new AuthenticatorException(
                    "Authenticator class " + authenticatorClassName + " can not be instantiated.", 
                    AuthenticatorException.SYSTEM_ERROR, ex);
        } catch (ClassCastException ex) {
            throw new AuthenticatorException(
                    "Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.", 
                    AuthenticatorException.SYSTEM_ERROR, ex);
        } catch (AuthenticatorException ex) {
            throw new UserException("Unable to update user.", ex);
        }
        User existinguser = userDAO.findByPrimaryKey(user.getId());
        existinguser.setLogin(user.getLogin());
        existinguser.setFirstName(user.getFirstName());
        existinguser.setLastName(user.getLastName());
        existinguser.setEmail(user.getEmail());
        existinguser.setSuperUser(user.isSuperUser());
        existinguser.setProjects(user.getProjects());
        existinguser.setLastModifiedDate(new Timestamp(new Date().getTime()));

        // Only set the password if it is a new value...
        if (user.getPassword() != null && !user.getPassword().equals("")
                && !user.getPassword().equals(user.getPassword())) {
            existinguser.setPassword(user.getPassword());
        }
        
        userDAO.saveOrUpdate(existinguser);
        return existinguser;
    }

    public String generateUserPassword(User user) throws PasswordException {
        String password = UserUtilities.generatePassword();
        user.setPassword(password);
        return password;
        // throw new PasswordException(PasswordException.UNKNOWN_USER);
    }

    public UserPreferences updateUserPreferences(UserPreferences userPrefs) throws UserException {
         UserPreferences NewUserPrefs;
         
         try {
            User user = userPrefs.getUser();
            user.setPreferences(userPrefs);
            
            try {
                PluggableAuthenticator authenticator = 
                        (PluggableAuthenticator) authenticatorClass.newInstance();
                
                if (authenticator != null) {
                    HashMap<String,Object> values = new HashMap<String,Object>();
                    values.put("userService", this);
                    values.put("configurationService", configurationService);
                    authenticator.initialize(values);
                    authenticator.updateProfile(user, AuthenticationConstants.UPDATE_TYPE_PREFERENCE, null,
                            AuthenticationConstants.AUTH_TYPE_UNKNOWN, AuthenticationConstants.REQ_SOURCE_UNKNOWN);
                } else {
                    throw new AuthenticatorException("Unable to create new authenticator.", 
                            AuthenticatorException.SYSTEM_ERROR);
                }
            } catch (IllegalAccessException ex) {
                throw new AuthenticatorException(
                        "Authenticator class " + authenticatorClassName + " can not be instantiated.", 
                        AuthenticatorException.SYSTEM_ERROR, ex);
            } catch (InstantiationException ex) {
                throw new AuthenticatorException(
                        "Authenticator class " + authenticatorClassName + " can not be instantiated.", 
                        AuthenticatorException.SYSTEM_ERROR, ex);
            } catch (ClassCastException ex) {
                throw new AuthenticatorException(
                        "Authenticator class " + authenticatorClassName
                        + " does not extend the PluggableAuthenticator class.", 
                        AuthenticatorException.SYSTEM_ERROR, ex);
            }

            NewUserPrefs = userPreferencesDAO.findByUserId(user.getId());
 
            if ( NewUserPrefs == null ) {
                NewUserPrefs = new UserPreferences();
            }
            NewUserPrefs.setSaveLogin(userPrefs.getSaveLogin());
            NewUserPrefs.setUserLocale(userPrefs.getUserLocale());
            NewUserPrefs.setNumItemsOnIndex(userPrefs.getNumItemsOnIndex());
            NewUserPrefs.setNumItemsOnIssueList(userPrefs.getNumItemsOnIssueList());
            NewUserPrefs.setShowClosedOnIssueList(userPrefs.getShowClosedOnIssueList());
            NewUserPrefs.setSortColumnOnIssueList(userPrefs.getSortColumnOnIssueList());
            NewUserPrefs.setHiddenIndexSections(userPrefs.getHiddenIndexSections());

            NewUserPrefs.setRememberLastSearch(userPrefs.getRememberLastSearch());
            NewUserPrefs.setUseTextActions(userPrefs.getUseTextActions());

            NewUserPrefs.setUser(user);
            NewUserPrefs.setLastModifiedDate(new Date());
            
            if ( userPrefs.getId() == null ) {
                userPrefs.setCreateDate(new Date());
                userPrefs.setLastModifiedDate(userPrefs.getCreateDate());
            }
            this.userPreferencesDAO.saveOrUpdate( NewUserPrefs );
            return NewUserPrefs;
            
        } catch (AuthenticatorException ex) {
            throw new UserException("Unable to create new preferences.", ex);
        } finally {
            return userPrefs;
        }
    }

    public boolean deleteUser(User user) {
        userDAO.delete(user);
        return true;
    }

    public boolean setUserStatus(Integer userId, int status) {
        User user = userDAO.findByPrimaryKey(userId);
        user.setStatus(status);
        return true;
    }

    public boolean clearOwnedProjects(Integer userId) {
        User user = userDAO.findByPrimaryKey(userId);
        Collection<Project> projects = user.getProjects();
        projects.clear();
        return true;
    }

    public List<User> getUsersWithPermissionLocal(Permission permission) {
        // TODO: implement findUserByPermission in UserDAO!
        List<User> users = new ArrayList<User>();

        if (permission.getProject() != null) {
            List<Permission> permissions = permissionDAO.findByProjectIdAndPermission(
                    permission.getProject().getId(), permission.getPermissionType());
            
            int i = 0;
            
            for (Iterator<Permission> iterator = permissions.iterator(); iterator.hasNext(); i++) {
                users.add(iterator.next().getUser());
            }
        }
        return users;
    }

    public List<Permission> getUserPermissionsLocal(User user) {
        List<Permission> permissions = permissionDAO.findByUserId(user.getId());
        return permissions;
    }

    public List<Permission> getPermissionsByUserId(Integer userId) {
        List<Permission> permissions = new ArrayList<Permission>();

        User user = getUser(userId);
        if (user != null) {
            try {
                PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
                if (authenticator != null) {
                    HashMap<String, Object> values = new HashMap<String,Object>();
                    values.put("userService", this);
                    values.put("configurationService", configurationService);
                    authenticator.initialize(values);
                    permissions = authenticator.getUserPermissions(user, AuthenticationConstants.REQ_SOURCE_UNKNOWN);
                }
                logger.debug("Found " + permissions.size() + " permissions for user " + user.getLogin());
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Authenticator class " 
                        + authenticatorClassName + " can not be instantiated.", ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException("Authenticator class " 
                        + authenticatorClassName + " can not be instantiated.", ex);
            } catch (ClassCastException ex) {
                throw new RuntimeException("Authenticator class " + authenticatorClassName
                        + " does not extend the PluggableAuthenticator class.", ex);
            } catch (AuthenticatorException ex) {
                throw new RuntimeException("Authenticator exception: ", ex);
            }
        }
        return permissions;
    }

    public boolean addUserPermissions(Integer userId, List<Permission> newPermissions) {
        boolean successful = false;

        if (newPermissions == null || newPermissions.size() == 0) {
            return false;
        }
        User user = null;
        try {
            user = userDAO.findByPrimaryKey(userId);
            user.setPermissions(newPermissions);
            try {
                PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
                if (authenticator != null) {
                    HashMap<String,Object> values = new HashMap<String,Object>();
                    values.put("userService", this);
                    values.put("configurationService", configurationService);
                    authenticator.initialize(values);
                    if (authenticator
                            .updateProfile(user, AuthenticationConstants.UPDATE_TYPE_PERMISSION_SET, null,
                                    AuthenticationConstants.AUTH_TYPE_UNKNOWN,
                                    AuthenticationConstants.REQ_SOURCE_UNKNOWN)) {
                        newPermissions = user.getPermissions();
                    }
                } else {
                    logger.error("Unable to create new authenticator.");
                    throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
                }
            } catch (IllegalAccessException iae) {
                logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch (InstantiationException ie) {
                logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch (ClassCastException cce) {
                logger.error("Authenticator class " + authenticatorClassName
                        + " does not extend the PluggableAuthenticator class.");
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            }
//            Collection<Permission> permissions = null;
//            try {
//                permissions = permissionDAO.findByUserId(userId);
//            } catch (Exception pex) {
//                logger.info("No Permissions Found");
//            }
            Integer projectId = null;
            Project project = null;
            
            for(Iterator iterator = newPermissions.iterator(); iterator.hasNext(); ) {
//            for (int i = 0; i < newPermissions.size(); i++) {
                Permission permission = (Permission) iterator.next();
                if (permission.getProject() == null) {
                    continue;
                }

                if (projectId == null || projectId.intValue() != permission.getProject().getId()) {
                    projectId = permission.getProject().getId();
                    project = projectDAO.findByPrimaryKey(projectId);
                }

                permission.setCreateDate(new Timestamp(new Date().getTime()));
                permission.setLastModifiedDate(permission.getCreateDate());
                permission.setProject(project);
                permission.setUser(user);
                permissionDAO.saveOrUpdate(permission);
            }
            successful = true;
        } catch (AuthenticatorException ae) {
            logger.warn("Error setting user (" + userId + ") permissions.  AuthenticatorException.", ae);
            successful = false;
        }

        return successful;
    }

    public boolean setUserPermissions(Integer userId, List<Permission> newPermissions) {
        boolean successful = true;

        try {
            User user = userDAO.findByPrimaryKey(userId);
            user.setPermissions(newPermissions);
            try {
                PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
                if (authenticator != null) {
                    HashMap<String,Object> values = new HashMap<String,Object>();
                    values.put("userService", this);
                    values.put("configurationService", configurationService);
                    authenticator.initialize(values);
                    if (authenticator
                            .updateProfile(user, AuthenticationConstants.UPDATE_TYPE_PERMISSION_SET, null,
                                    AuthenticationConstants.AUTH_TYPE_UNKNOWN,
                                    AuthenticationConstants.REQ_SOURCE_UNKNOWN)) {
                        newPermissions = user.getPermissions();
                    }
                } else {
                    logger.error("Unable to create new authenticator.");
                    throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
                }
            } catch (IllegalAccessException iae) {
                logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch (InstantiationException ie) {
                logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch (ClassCastException cce) {
                logger.error("Authenticator class " + authenticatorClassName
                        + " does not extend the PluggableAuthenticator class.");
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            }

            Collection<Permission> permissions = permissionDAO.findByUserId(userId);

            if (newPermissions == null || newPermissions.size() == 0) {
                // delete all existing permissions if no permissions 
                // have been submitted by http params
                for (Iterator<Permission> iterator = permissions.iterator(); iterator.hasNext();) {
                    iterator.next();
                    iterator.remove();
                }
            } else {
                HashMap<String,Permission> newPermissionsMap = new HashMap<String,Permission>();
                // put all permissions into map for later lookup
                for (int i = 0; i < newPermissions.size(); i++) {
                    if (newPermissions.get(i) != null) {
                        newPermissionsMap.put("Perm" + newPermissions.get(i).getPermissionType() + "Proj"
                                + newPermissions.get(i).getProject().getId(), newPermissions.get(i));
                    }
                }
                
                // - peek out permissions already granted
                // - delete permission not granted anymore
                for (Iterator<Permission> iterator = permissions.iterator(); iterator.hasNext();) {
                    Permission permission = (Permission) iterator.next();

                    if (newPermissionsMap.containsKey("Perm" + permission.getPermissionType() + "Proj"
                            + permission.getProject().getId())) {
                        newPermissionsMap.remove("Perm" + permission.getPermissionType() + "Proj" + permission.getProject().getId());
                    } else {
                        // delete permission
                    	permissionDAO.delete(permission);
                        iterator.remove();
                    }
                }
                // finally create all newPermissions which do not exist yet
                if (newPermissionsMap.values() != null) {
                    for (Iterator<Permission> iterator = newPermissionsMap.values().iterator(); iterator.hasNext();) {
                        Permission model = (Permission) iterator.next();
                        if (model.getProject() != null) {
                            Project project = model.getProject();
                     
                            Permission permission = new Permission();
                            permission.setCreateDate(new Date());
                            permission.setProject(project);
                            permission.setUser(user);
                            permissionDAO.saveOrUpdate(permission);
                        }
                    }
                }
            }
        } catch (AuthenticatorException ae) {
            logger.warn("Error setting user (" + userId + ") permissions.  AuthenticatorException.", ae);
            successful = false;
        }
        
        return successful;
    }

    @Deprecated
    public Map<Integer, Set<PermissionType>> getUserPermissions(User user, int reqSource) {
        Map<Integer, Set<PermissionType>> permissionsMap = new HashMap<Integer, Set<PermissionType>>();

        if (user == null) {
            return permissionsMap;
        }

        List<Permission> permissionList = new ArrayList<Permission>();

        try {
            PluggableAuthenticator authenticator = 
                    (PluggableAuthenticator) authenticatorClass.newInstance();
            
            if (authenticator != null) {
                HashMap<String, Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                permissionList = authenticator.getUserPermissions(user,(reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }
            logger.debug("Found " + permissionList.size() + " permissions for user " + user.getLogin());
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
        } catch (AuthenticatorException ae) {
            logger.error("Authenticator exception: " + ae.getMessage());
            logger.debug("Authenticator exception: ", ae);
        }

        permissionsMap = UserUtilities.mapPermissionTypesByProjectId(permissionList);

        if (allowSelfRegister) {
            List<Project> projects = projectService.getAllProjects();
            
            for (int i = 0; i < projects.size(); i++) {
                Project project = projects.get(i);
                
                if (project.getOptions() >= ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE) {
                    Set<PermissionType> projectPermissions = permissionsMap.get(project.getId());
                    
                    if (projectPermissions == null) {
                        projectPermissions = new HashSet<PermissionType>();
                        permissionsMap.put(project.getId(), projectPermissions);
                    }
                    
                    if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE, project.getOptions())) {
                        projectPermissions.add(PermissionType.ISSUE_VIEW_USERS);
                        projectPermissions.add(PermissionType.ISSUE_CREATE);
                    }
                    
                    if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL, project.getOptions())) {
                        projectPermissions.add(PermissionType.ISSUE_VIEW_ALL);
                    }
                }
            }
        }

        return permissionsMap;
    }

    public List<User> getUsersWithProjectPermission(Integer projectId, int permission) {
        return getUsersWithProjectPermission(projectId, permission, true);
    }

    public List<User> getUsersWithProjectPermission(Integer projectId, int permission, boolean activeOnly) {
        return getUsersWithAnyProjectPermission(projectId, new int[] { permission }, activeOnly);
    }

    public List<User> getUsersWithAnyProjectPermission(Integer projectId, int[] permissions) {
        return getUsersWithAnyProjectPermission(projectId, permissions, true);
    }

    public List<User> getUsersWithAnyProjectPermission(Integer projectId, int[] permissions, boolean activeOnly) {
        return getUsersWithProjectPermission(projectId, permissions, false, activeOnly);
    }

    public List<User> getUsersWithProjectPermission(Integer projectId, int[] permissions, boolean requireAll,
            boolean activeOnly) {
        List<User> userList = new ArrayList<User>();
        List<Permission> reqPermissions = new ArrayList<Permission>();
        Project project = projectDAO.findByPrimaryKey(projectId);
        
        for (int i = 0; i < permissions.length; i++) {
            reqPermissions.add(i, new Permission(project, permissions[i]));
        }

        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                userList = authenticator.getUsersWithProjectPermission(reqPermissions, requireAll, activeOnly,
                        AuthenticationConstants.REQ_SOURCE_UNKNOWN);
            }
            if (logger.isDebugEnabled()) {
                StringBuffer permissionsString = new StringBuffer("{ ");
                for (int i = 0; i < permissions.length; i++) {
                    permissionsString.append(permissions[i] + " ");
                }
                permissionsString.append("}");
                logger.debug("Found " + userList.size() + " users with project " + projectId + " permissions "
                        + permissionsString.toString() + (requireAll ? "[AllReq," : "[AnyReq,")
                        + (activeOnly ? "ActiveUsersOnly]" : "AllUsers]"));
            }
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
        } catch (AuthenticatorException ae) {
            logger.error("Authenticator exception: " + ae.getMessage());
            logger.debug("Authenticator exception: ", ae);
        }

        return userList;
    }

    public List<User> getPossibleOwners(Issue issue, Integer projectId, Integer userId) {
        HashSet<User> users = new HashSet<User>();

        List<User> editUsers = getUsersWithProjectPermission(projectId, UserUtilities.PERMISSION_EDIT, true);
        for (int i = 0; i < editUsers.size(); i++) {
            users.add(editUsers.get(i));
        }
        List<User> otherUsers = getUsersWithProjectPermission(projectId, new int[] { UserUtilities.PERMISSION_EDIT_USERS, UserUtilities.PERMISSION_ASSIGNABLE }, true, true);
        for (int i = 0; i < otherUsers.size(); i++) {
            users.add(otherUsers.get(i));
        }

        if (issue != null) {
            // Now add in the creator if the have edit own issues, and always
            // the owner
            User creator = issue.getCreator();
            
            if (UserUtilities.hasPermission(getUserPermissions(creator, 0), projectId,
                    UserUtilities.PERMISSION_EDIT_USERS)) {
                users.add(creator);
            }
            if (issue.getOwner() != null) {
                User owner = issue.getOwner();
                users.add(owner);
            }
        } else if (userId != null) {
            // New issue, so add in the creator if needed
            User creator = getUser(userId);
            if (UserUtilities.hasPermission(getUserPermissions(creator, 0), projectId,
                    UserUtilities.PERMISSION_EDIT_USERS)) {
                users.add(creator);
            }
        }

        int i = 0;
        List<User> userList = new ArrayList<User>();
        for (Iterator<User> iter = users.iterator(); iter.hasNext(); i++) {
            userList.add((User)iter.next());
        }
        return userList;
    }

    public List<User> getListOfPossibleOwners(Issue issue, Integer projectId, Integer userId) {
        HashSet<User> users = new HashSet<User>();

        List<User> editUsers = getUsersWithProjectPermission(projectId, UserUtilities.PERMISSION_EDIT, true);
        for (int i = 0; i < editUsers.size(); i++) {
            users.add(editUsers.get(i));
        }
        List<User> otherUsers = getUsersWithProjectPermission(projectId, new int[] {
                UserUtilities.PERMISSION_EDIT_USERS, UserUtilities.PERMISSION_ASSIGNABLE }, true, true);
        for (int i = 0; i < otherUsers.size(); i++) {
            users.add(otherUsers.get(i));
        }

        if (issue != null) {
            // Now add in the creator if the have edit own issues, and always
            // the owner
            User creator = issue.getCreator();
            if (UserUtilities.hasPermission(getUserPermissions(creator, 0), projectId,
                    UserUtilities.PERMISSION_EDIT_USERS)) {
                users.add(creator);
            }
            if (issue.getOwner() != null) {
                User owner = issue.getOwner();
                users.add(owner);
            }
        } else if (userId != null) {
            // New issue, so add in the creator if needed
            User creator = getUser(userId);
            if (UserUtilities.hasPermission(getUserPermissions(creator, 0), projectId,
                    UserUtilities.PERMISSION_EDIT_USERS)) {
                users.add(creator);
            }
        }

        int i = 0;
        List<User> userList = new ArrayList<User>();
        for (Iterator<User> iter = users.iterator(); iter.hasNext(); i++) {
            userList.add((User)iter.next());
        }
        return userList;
    }
    
    public User checkLogin(String login, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                return authenticator.checkLogin(login, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

    public boolean allowRegistration(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                if (authenticator.allowProfileCreation(user, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource))) {
                    return authenticator.allowRegistration(user, authentication, authType,
                            (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
                }
                return false;
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

    public boolean allowProfileCreation(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                return authenticator.allowProfileCreation(user, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

    public boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                return authenticator.allowProfileUpdates(user, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

    public boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                return authenticator.allowPasswordUpdates(user, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

    public boolean allowPermissionUpdates(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                return authenticator.allowPermissionUpdates(user, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

    public boolean allowPreferenceUpdates(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        try {
            PluggableAuthenticator authenticator = (PluggableAuthenticator) authenticatorClass.newInstance();
            if (authenticator != null) {
                HashMap<String,Object> values = new HashMap<String,Object>();
                values.put("userService", this);
                values.put("configurationService", configurationService);
                authenticator.initialize(values);
                return authenticator.allowPreferenceUpdates(user, authentication, authType,
                        (reqSource == 0 ? AuthenticationConstants.REQ_SOURCE_UNKNOWN : reqSource));
            }

            logger.error("Unable to create new authenticator.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (IllegalAccessException iae) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (InstantiationException ie) {
            logger.error("Authenticator class " + authenticatorClassName + " can not be instantiated.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        } catch (ClassCastException cce) {
            logger.error("Authenticator class " + authenticatorClassName
                    + " does not extend the PluggableAuthenticator class.");
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }
    }

 
    public void sendNotification(String login, String email, String baseURL) {
    }

	public static String getSystemBaseURL() {
		return systemBaseURL;
	}

	public static void setSystemBaseURL(String systemBaseURL) {
		UserServiceImpl.systemBaseURL = systemBaseURL;
	}
    
    /*
    public void sendNotification(String login, String email, String baseURL) {
        try {
            QueueConnectionFactory factory = (QueueConnectionFactory) initialContext.lookup("java:comp/env/"
                    + notificationFactoryName);
            Queue notificationQueue = (Queue) initialContext.lookup("java:comp/env/" + notificationQueueName);
            QueueConnection connect = factory.createQueueConnection();
            QueueSession session = connect.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            QueueSender sender = session.createSender(notificationQueue);

            MapMessage message = session.createMapMessage();
            message.setInt("type", NotificationUtilities.TYPE_SELF_REGISTER);
            if (systemBaseURL != null && !systemBaseURL.equals("")) {
                message.setString("baseURL", systemBaseURL);
            } else if (baseURL != null) {
                message.setString("baseURL", baseURL);
            }
            message.setString("toAddress", email);
            message.setString("login", login);

            sender.send(message);
        } catch (NamingException ne) {
            logger.error("Error looking up ConnectionFactory/Queue " + notificationFactoryName + "/"
                    + notificationQueueName + ".", ne);
        } catch (JMSException jmse) {
            logger.warn("Error sending notification message", jmse);
        }
    }
    */

}
