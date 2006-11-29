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

package org.itracker.model.deprecatedmodels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.itracker.model.Permission;
import org.itracker.model.UserPreferences;
import org.itracker.services.util.UserUtilities;


class UserModel extends GenericModel implements Comparator<UserModel> {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private int status;
    private int registrationType;
    private boolean loggedIn;
    private boolean superUser;
    private List<Permission> permissions;
    private UserPreferences preferences;

    public UserModel() {
        login = "";
        password = "";
        firstName = "";
        lastName = "";
        email = "";
        status = 0;
        registrationType = 0;
        superUser = false;
        loggedIn = false;
    }

    public UserModel(String login, String password, String firstName, String lastName, String email, boolean superUser) {
        this(login, password, firstName, lastName, email, UserUtilities.REGISTRATION_TYPE_ADMIN, superUser);
    }

    public UserModel(String login, String password, String firstName, String lastName, String email, int registrationType, boolean superUser) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationType = registrationType;
        this.superUser = superUser;
    }

    public String getFirstInitial() {
        return (getFirstName().length() > 0 ? getFirstName().substring(0,1).toUpperCase() + "." : "");
    }

    public boolean hasRequiredData() {
        return hasRequiredData(true);
    }

    public boolean hasRequiredData(boolean passwordRequired) {
        if(this.getLogin() == null || this.getLogin().equals("") ||
           this.getFirstName() == null || this.getFirstName().equals("") ||
           this.getLastName() == null || this.getLastName().equals("") ||
           this.getEmail() == null || this.getEmail().equals("")) {
            return false;
        }
        if(passwordRequired && (this.getPassword() == null || this.getPassword().equals(""))) {
            return false;
        }
        return true;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String value) {
         login = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        firstName = value;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        lastName = value;
    }

    public String getEmail() {
        return (email == null ? "" : email);
    }

    public void setEmail(String value) {
        email = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int value) {
        status = value;
    }

    public int getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(int value) {
        registrationType = value;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean value) {
        loggedIn = value;
    }

    public boolean isSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean value) {
        superUser = value;
    }

    public List<Permission> getPermissions() {
        return (permissions == null ? new ArrayList<Permission>() : permissions);
    }

    public void setPermissions(List<Permission> value) {
        permissions = value;
    }

    public UserPreferences getPreferences() {
        return (preferences == null ? new UserPreferences() : preferences);
    }

    public void setPreferences(UserPreferences value) {
        preferences = value;
    }

    public String toString() {
        return "User " + this.getLogin() + "(" + this.getId() + ")" + "  Name: " + this.getFirstName() + " " + this.getLastName() +
               "  Email: " + this.getEmail() + "  Status: " + this.getStatus() + "  RegType: " + this.getRegistrationType() + "  Super-User: " + this.isSuperUser();
    }

    public int compare(UserModel a, UserModel b) {
        return new CompareByLogin().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof UserModel)) {
            return false;
        }

        try {
            UserModel mo = (UserModel) obj;
            if(UserModel.this.getLogin().equals(mo.getLogin())) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (UserModel.this.getLogin() == null ? 1 : UserModel.this.getLogin().hashCode());
    }

    public static abstract class UserModelComparator implements Comparator<UserModel> {
        protected boolean isAscending = true;

        public UserModelComparator() {
        }

        public UserModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(UserModel ma, UserModel mb);

        public final int compare(UserModel a, UserModel b) {
            if(! (a instanceof UserModel) || ! (b instanceof UserModel)) {
                throw new ClassCastException();
            }

            UserModel ma = (UserModel) a;
            UserModel mb = (UserModel) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByLogin extends UserModelComparator {
        public CompareByLogin(){
          super();
        }

        public CompareByLogin(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(UserModel ma, UserModel mb) {
            if(ma.getLogin() == null && mb.getLogin() == null) {
                return 0;
            } else if(ma.getLogin() == null) {
                return 1;
            } else if(mb.getLogin() == null) {
                return -1;
            }

            return ma.getLogin().compareTo(mb.getLogin());
        }
    }

    public static class CompareByName extends UserModelComparator {
        public CompareByName(){
          super();
        }

        public CompareByName(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(UserModel ma, UserModel mb) {
            if(ma.getLastName() == null && mb.getLastName() == null) {
                return 0;
            } else if(ma.getLastName() == null) {
                return 1;
            } else if(mb.getLastName() == null) {
                return -1;
            }

            if(ma.getLastName().equals(mb.getLastName())) {
                if(ma.getFirstName() == null && mb.getFirstName() == null) {
                    return 0;
                } else if(ma.getFirstName() == null) {
                    return 1;
                } else if(mb.getFirstName() == null) {
                    return -1;
                }
                return ma.getFirstName().compareTo(mb.getFirstName());
            }
            return ma.getLastName().compareTo(mb.getLastName());
        }
    }
    
    public static class CompareByFirstName extends UserModelComparator {
        public CompareByFirstName(){
          super();
        }

        public CompareByFirstName(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(UserModel ma, UserModel mb) {
            if(ma.getFirstName() == null && mb.getFirstName() == null) {
                return 0;
            } else if(ma.getFirstName() == null) {
                return 1;
            } else if(mb.getFirstName() == null) {
                return -1;
            }

            if(ma.getFirstName().equals(mb.getFirstName())) {
                if(ma.getLastName() == null && mb.getLastName() == null) {
                    return 0;
                } else if(ma.getLastName() == null) {
                    return 1;
                } else if(mb.getLastName() == null) {
                    return -1;
                }
                return ma.getLastName().compareTo(mb.getLastName());
            }
            return ma.getFirstName().compareTo(mb.getFirstName());
        }
    }    
}