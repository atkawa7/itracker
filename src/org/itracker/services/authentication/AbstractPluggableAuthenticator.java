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

package org.itracker.services.authentication;

import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.itracker.model.Permission;
import org.itracker.model.User;

import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.services.util.AuthenticationConstants;


/**
  * This class provides a skeleton implementation of the PluggableAuthenticator interface.
  * It can be extended to provide a new authentication module for ITracker reducing the amount
  * of effort to implement the PluggableAuthenticator interface.
  */
public abstract class AbstractPluggableAuthenticator 
        implements PluggableAuthenticator, AuthenticationConstants {
    
    protected final Logger logger;
    private UserService userService = null;
    private ConfigurationService configurationService = null;

    public AbstractPluggableAuthenticator() {
        this.logger = Logger.getLogger(getClass());
    }

    /**
      * This method should be overridden to determine if a user login is successful.  The method
      * should return a valid User object.
      * @param login the login the user/client provided
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a User if the login is successful
      * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
      */
    public abstract User checkLogin(String login, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to return all the permissions a user has in the
      * authentication system.  This list may then be augmented based on other attributes of
      * the user, or project level options.
      * @param user a User object that contains the user to retrieve permissions for
      * @param reqSource the source of the request (eg web, api)
      * @return an array of PermissionModels
      * @throws AuthenticatorException an error occurs
      */
    public abstract List<Permission> getUserPermissions(User user, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to return an array of users that have certian permissions
      * in the authentication system.  This list must always include all super users, even if they
      * do not explicitly have the required permission.
      * @param permissions an array of PermissionModels that define which permissions in which
               projects are required.
      * @param requireAll true if the user must possess any of the permissions, false if only one is required
      * @param activeOnly true if only users listed as active should be returned
      * @param reqSource the source of the request (eg web, api)
      * @return an array of UserModels
      * @throws AuthenticatorException an error occurs
      */
    public abstract List<User> getUsersWithProjectPermission(List<Permission> permissions, boolean requireAll, boolean activeOnly, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to determine if a user is authorized to self register.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a boolean whether the user should be allowed to register
      * @throws AuthenticatorException an exception if an error occurs
      */
    public abstract boolean allowRegistration(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overwritten to return if any new user profile should be allowed
      * to be created.
      * @param user a User object that contains the data for the new user.  If null,
               then the request is being made for an unknown future user.  For example,
               the system may request this with an null user if it needs to know if the system
               should even present the option to create a new user
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a boolean whether the new profile creation is allowed
      * @throws AuthenticatorException an exception if an error occurs
      */
    public abstract boolean allowProfileCreation(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to return if the particular user can have
      * core profile information updates on the system.
      * @param user a User object that contains the current user data
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a boolean whether the user's core profile information can be updated
      * @throws AuthenticatorException an exception if an error occurs
      * @see PluggableAuthenticator#allowPasswordUpdates
      * @see PluggableAuthenticator#allowPermissionUpdates
      * @see PluggableAuthenticator#allowPreferenceUpdates
      */
    public abstract boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to return if the particular user profile can have
      * password updates on the system.
      * @param user a User object that contains the current user data
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a boolean whether the user's core profile information can be updated
      * @throws AuthenticatorException an exception if an error occurs
      * @see PluggableAuthenticator#allowProfileUpdates
      * @see PluggableAuthenticator#allowPermissionUpdates
      * @see PluggableAuthenticator#allowPreferenceUpdates
      */
    public abstract boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to return if the particular user profile can have
      * permission updates on the system.
      * information is allowed to be updated through ITracker.
      * @param user a User object that contains the current user data, or null if multiple users
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a boolean whether the user's core profile information can be updated
      * @throws AuthenticatorException an exception if an error occurs
      * @see PluggableAuthenticator#allowProfileUpdates
      * @see PluggableAuthenticator#allowPasswordUpdates
      * @see PluggableAuthenticator#allowPreferenceUpdates
      */
    public abstract boolean allowPermissionUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be implemented to determine if the particular user can have
      * preferences updates on the system.
      * @param user a User object that contains the current user data
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a boolean whether the user's core profile information can be updated
      * @throws AuthenticatorException an exception if an error occurs
      * @see PluggableAuthenticator#allowProfileUpdates
      * @see PluggableAuthenticator#allowPasswordUpdates
      * @see PluggableAuthenticator#allowPermissionUpdates
      */
    public abstract boolean allowPreferenceUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to perform any updates that are necessary in the authentication
      * system to support a new user.  Any changes should be made directly to user model supplied to the
      * method.  The system will then update the information in the ITracker datastore.  Only changes to the
      * core profile information and password are made here.  Any permission information for the new user
      * whould be done through an updateProfile call.
      * @param user a User object that contains the profile information to be created
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true if changes were made
      * @throws AuthenticatorException an error occurs
      * @see PluggableAuthenticator#updateProfile
      */
    public abstract boolean createProfile(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method should be overridden to perform any updates that are necessary in the authentication
      * system to support the updated user information.  This action will be called any time there are any
      * updates to a user includeing core profile information, password information, permission information
      * or preference changes.  Any changes should be made directly to user model supplied to the method.
      * @param user a User object that contains the updated profile
      * @param updateType the type of information that is being updated
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true if changes were made
      * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
      */
    public abstract boolean updateProfile(User user, int updateType, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
      * This method is called after creating a new instance of the Authenticator.  It supplies
      * some default EJB objects that the authenticator can use.
      */
    public void initialize(HashMap values) {
        if(values != null) {
            Object userService = values.get("userService");
            Object configurationService = values.get("configurationService");

            if(userService instanceof UserService) {
                this.userService = (UserService) userService;
            }
            if(configurationService instanceof ConfigurationService) {
                this.configurationService = (ConfigurationService) configurationService;
            }
        }
    }

    /**
      * Returns a UserService session bean that can be used to call needed methods such
      * as retrieving a user.
     * @return userService
     * @throws AuthenticatorException an exception if an error occur
     */
    public UserService getUserService() throws AuthenticatorException {
        if(userService == null || ! (userService instanceof UserService)) {
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }

        return userService;
    }

    /**
      * Returns an ConfigurationService session bean that can be used to retreive properties
      * that have been set in the system.  These properties can be used to provide any
      * needed configuration for the authenticator.
     * @return configurationService
     * @throws AuthenticatorException an exception if an error occur
     */
    public ConfigurationService getConfigurationService() throws AuthenticatorException {
        if(configurationService == null || ! (configurationService instanceof ConfigurationService)) {
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }

        return configurationService;
    }
}