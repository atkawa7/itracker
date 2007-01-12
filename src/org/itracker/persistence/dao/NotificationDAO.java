package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Notification;

/**
 * 
 */
public interface NotificationDAO  extends BaseDAO<Notification>{
    
    Notification findById(Integer id);
    
    /**
     * Finds all Notifications for an Issue. 
     * 
     * @param issueId 
     * @return list of notification for the given issue, in unspecified order
     */
    List<Notification> findByIssueId(Integer issueId);
    
}
