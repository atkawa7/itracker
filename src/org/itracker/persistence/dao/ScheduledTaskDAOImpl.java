package org.itracker.persistence.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.ScheduledTask;

/**
 * 
 */
public class ScheduledTaskDAOImpl extends BaseHibernateDAOImpl<ScheduledTask> 
        implements ScheduledTaskDAO {

    /** 
     * Finds a <code>ScheduledTask</code> by its id
     *
     * @param id the id of the <code>ScheduledTask</code>
     * @return a <code>ScheduledTask</code>
     */
    public ScheduledTask findByPrimaryKey(Integer id) {
        ScheduledTask task;
        
        try {
            task = (ScheduledTask)getSession().get(ScheduledTask.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return task;
    }

    @SuppressWarnings("unchecked")
    public List<ScheduledTask> findAll() {
        List<ScheduledTask> tasks;
        
        try {
            Query query = getSession().getNamedQuery("ScheduledTasksAllQuery");
            tasks = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return tasks;
    }
    
}
