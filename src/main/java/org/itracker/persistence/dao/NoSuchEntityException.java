package org.itracker.persistence.dao;

import org.springframework.dao.DataAccessException;

public class NoSuchEntityException extends DataAccessException {
    
    public NoSuchEntityException(String msg) {
        super(msg);
    }

    public NoSuchEntityException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
