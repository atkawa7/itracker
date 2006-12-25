package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.ScheduledTask;

/**
 * 
 * 
 */
public interface ScheduledTaskDAO extends BaseDAO<ScheduledTask> {
    
    /**
     * Finds a scheduled task by id. 
     * 
     * @param taskId
     * @return scheduled task instance or <tt>null</tt>
     */
    ScheduledTask findByPrimaryKey(Integer taskId);

    /**
     * Finds all scheduled tasks. 
     * 
     * @return list of scheduled tasks in unspecified order
     */
    List<ScheduledTask> findAll();
    
}
