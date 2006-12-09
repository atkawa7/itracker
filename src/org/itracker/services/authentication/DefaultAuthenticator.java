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

import java.util.ArrayList;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.itracker.model.Permission;
import org.itracker.model.User;

import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.services.exceptions.PasswordException;
import org.itracker.services.exceptions.UserException;
import org.itracker.services.util.UserUtilities;
import org.springframework.dao.DataAccessException;


/**
  * This class provides a default authentication scheme for ITracker.  It uses passwords
  * in the user table provided by ITracker to authenticate users.  This authenticator
  * allows any user to self register if self registration is available in the system.
  */
public class DefaultAuthenticator extends AbstractPluggableAuthenticator {

    public DefaultAuthenticator() {
    }

    /**
      * Checks the login of a user against the user profile provided in ITracker.  This is
      * the default authentication scheme provided by ITracker.
      * @param login the login the user/client provided
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return a User if the login is successful
      * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
      */
    public  User checkLogin(String login, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Checking login for " + login + " using DefaultAuthenticator");
        }
        
        if(login != null && authentication != null && ! login.equals("")) {
            User user = null;
            try {
                user = getUserService().getUserByLogin(login);
            } catch (DataAccessException e) {
                throw new AuthenticatorException(0, e.getMessage());
            }

            if(user == null) {
                throw new AuthenticatorException(AuthenticatorException.UNKNOWN_USER);
            }
            if(user.getStatus() != UserUtilities.STATUS_ACTIVE) {
                throw new AuthenticatorException(AuthenticatorException.INACTIVE_ACCOUNT);
            }

            String userPassword;
            try {
                userPassword = getUserService().getUserPasswordByLogin(login);
            } catch (DataAccessException e) {
                throw new AuthenticatorException(e.getMessage(), authType);
            }
            if(userPassword == null || userPassword.equals("")) {
                throw new AuthenticatorException(AuthenticatorException.INVALID_PASSWORD);
            }

            try {
                if(! userPassword.endsWith("=")) {
                    logger.info("User " + login + " has old style password.  Converting to SHA1 hash.");
                    try {
                        user.setPassword(UserUtilities.encryptPassword(userPassword));
                        getUserService().updateUser(user);
                    } catch(UserException ue) {
                        logger.info("User password conversion failed for user " + login);
                    }
                }

                if(authType == AUTH_TYPE_PASSWORD_PLAIN) {
                    if(! userPassword.equals(UserUtilities.encryptPassword((String) authentication))) {
                        throw new AuthenticatorException(AuthenticatorException.INVALID_PASSWORD);
                    }
                } else if(authType == AUTH_TYPE_PASSWORD_ENC) {
                    if(! userPassword.equals((String) authentication)) {
                        throw new AuthenticatorException(AuthenticatorException.INVALID_PASSWORD);
                    }
                } else {
                    throw new AuthenticatorException(AuthenticatorException.INVALID_AUTHENTICATION_TYPE);
                }
            } catch(ClassCastException cce) {
                logger.error("Authenticator was of wrong type.", cce);
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch(PasswordException pe) {
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            }

            return user;
        }

        throw new AuthenticatorException(AuthenticatorException.INVALID_DATA);
    }

    /**
      * The DefaultAuthenticator returns a list of user permissions from the database.
      * @param user a User object that contains the user to retrieve permissions for
      * @param reqSource the source of the request (eg web, api)
      * @return an array of PermissionModels
      * @throws AuthenticatorException an error occurs
      */
    public List<Permission> getUserPermissions(User user, int reqSource) throws AuthenticatorException {
        if (user == null || user.getId() == null) {
            throw new AuthenticatorException(AuthenticatorException.INVALID_DATA);
        }

        List<Permission> permissionList = new ArrayList<Permission>();
        try {
            permissionList = getUserService().getUserPermissionsLocal(user);
        } catch (DataAccessException e) {
            throw new AuthenticatorException(e.getMessage(), reqSource);
        }

        if (user.isSuperUser()) {
            List<Permission> augmentedPermissions = new ArrayList<Permission>();
            
            // Super user has access to all projects (represented by the "null" project). 
            Permission permission = new Permission(null, -1, user);
            augmentedPermissions.add(permission);
            augmentedPermissions.addAll(permissionList);
            return augmentedPermissions;
          
        } else{
            return permissionList;
        }

    }

    public List<User> getUsersWithProjectPermission(List<Permission> permissions, boolean requireAll, boolean activeOnly, int reqSource) throws AuthenticatorException {
        List<User> users = new ArrayList<User>();

        try {
            HashMap<Integer,User> userMap = new HashMap<Integer,User>();

            for(int i = 0; i < permissions.size(); i++) {
                List<User> explicitUsers = getUserService().getUsersWithPermissionLocal(permissions.get(i));
                if(! requireAll || permissions.size() == 1) {
                    for(int j = 0; j < explicitUsers.size(); j++) {
                        userMap.put(explicitUsers.get(j).getId(), explicitUsers.get(j));
                    }
                } else {
                    if(i == 0) {
                        for(int j = 0; j < explicitUsers.size(); j++) {
                            userMap.put(explicitUsers.get(j).getId(), explicitUsers.get(j));
                        }
                    } else {
                        for(Iterator iter = userMap.keySet().iterator(); iter.hasNext(); ) {
                            boolean found = false;
                            Integer userId = (Integer) iter.next();
                            for(int j = 0; j < explicitUsers.size(); j++) {
                                if(userId.equals(explicitUsers.get(j).getId())) {
                                    found = true;
                                    break;
                                }
                            }
                            if(! found) {
                                iter.remove();
                            }
                        }
                    }
                }
            }

            List<User> superUsers = getUserService().getSuperUsers();
            for(int i = 0; i < superUsers.size(); i++) {
                if(! activeOnly || superUsers.get(i).getStatus() == UserUtilities.STATUS_ACTIVE) {
                    userMap.put(superUsers.get(i).getId(), superUsers.get(i));
                }
            }

            int i = 0;
            users = new ArrayList<User>();
            for(Iterator iter = userMap.values().iterator(); iter.hasNext(); i++) {
            	users.add((User) iter.next());
            }
        } catch(Exception e) {
            logger.error("Error retreiving users with permissions.", e);
            throw new AuthenticatorException();
        }

        return users;
    }

    /**
      * The DefaultAuthenticator always allows self registered users.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true
      */
    public boolean allowRegistration(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }


    /**
      * The DefaultAuthenticator always allows new user profiles.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @return true
      * @throws AuthenticatorException an exception if an error occurs
      */
    public boolean allowProfileCreation(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
      * The DefaultAuthenticator always allows profile updates.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true
      * @throws AuthenticatorException an exception if an error occurs
      */
    public boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
      * The DefaultAuthenticator always allows password updates.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true
      * @throws AuthenticatorException an exception if an error occurs
      */
    public boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
      * The DefaultAuthenticator always allows permission updates.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true
      * @throws AuthenticatorException an exception if an error occurs
      */
    public boolean allowPermissionUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
      * The DefaultAuthenticator always allows preferences updates.
      * @param user a User object that contains the data the user submitted
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return true
      * @throws AuthenticatorException an exception if an error occurs
      */
    public boolean allowPreferenceUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
      * The DefaultAuthenticator does not make any changes to a newly created profile.
      * @param user a User object that contains the newly created profile
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return boolean indicating whther changes to the user were made
      * @throws AuthenticatorException an error occurs
      */
    public boolean createProfile(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return false;
    }

    /**
      * The DefaultAuthenticator does not make any changes to an updated profile.
      * @param user a User object that contains the updated profile
      * @param updateType the type of information that is being updated
      * @param authentication the user's authentication information, if known
      * @param authType the type of authentication information being provided
      * @param reqSource the source of the request (eg web, api)
      * @return boolean indicating whther changes to the user were made
      * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
      */
    public boolean updateProfile(User user, int updateType, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return false;
    }
}
