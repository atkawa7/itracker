package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
        try {
            return (ScheduledTask)getSession().get(ScheduledTask.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException( ex );
        }
    }

    /**
     * Finds all <code>ScheduledTask</code>
     *
     * @returns a <code>Collection</code> of <code>ScheduledTask</code>s
     */
    @SuppressWarnings("unchecked")
    public List<ScheduledTask> findAll() {
        Criteria criteria = getSession().createCriteria(ScheduledTask.class);
        
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
