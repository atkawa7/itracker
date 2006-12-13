package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Notification;

/**
 * 
 */
public interface NotificationDAO {
    
    public List<Notification> findByIssueId(Integer issueId);
}
