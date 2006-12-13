package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.ScheduledTask;

/**
 * 
 */
public interface ScheduledTaskDAO extends BaseDAO<ScheduledTask> {
    
    public ScheduledTask findByPrimaryKey(Integer id);

    public List<ScheduledTask> findAll();
}
