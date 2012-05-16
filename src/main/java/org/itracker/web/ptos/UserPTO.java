package org.itracker.web.ptos;

import org.itracker.model.User;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.util.SessionManager;

import java.util.Date;

public class UserPTO {
    private User user;

    public UserPTO(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isStatusLocked() {
        return (user.getStatus() == UserUtilities.STATUS_LOCKED);
    }

    public boolean isRegisrationTypeSelf() {
        return (user.getRegistrationType() == UserUtilities.REGISTRATION_TYPE_SELF);
    }

    public Date getLastAccess() {
        return SessionManager.getSessionLastAccess(user.getLogin());
    }
}