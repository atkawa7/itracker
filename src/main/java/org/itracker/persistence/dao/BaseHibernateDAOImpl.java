package org.itracker.persistence.dao;

import java.sql.Connection;
import java.util.Date;

import org.hibernate.HibernateException;
import org.itracker.model.Entity;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Contains common behaviour to all hibernate factories
 * 
 * @author rui silva
 */
public abstract class BaseHibernateDAOImpl<T extends Entity> extends HibernateDaoSupport 
        implements BaseDAO<T> {

    /**
     * 
     */
    public BaseHibernateDAOImpl() {
        super();
    }

    /**
     * insert a new entity.
     * create- and lastmodified-date is set with current time.
     * 
     * @param entity - detached entity object
     */
    public void save(T entity) {
        try {
        	Date createDate = new Date();
        	entity.setCreateDate(createDate);
        	entity.setLastModifiedDate(createDate);
            getSession().save(entity);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    /**
     * inserts a new detached entity or updates if it already exists.
     * create- and update-date are set automatically.
     * 
     * @param entity - entity object to be inserted or updated
     */
    public void saveOrUpdate(T entity) {
        try {
        	if (null == entity.getCreateDate()){
        		entity.setCreateDate(new Date());
        		entity.setLastModifiedDate(entity.getCreateDate());
        	} else {
        		entity.setLastModifiedDate(new Date());
        	}
            getSession().saveOrUpdate(entity);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public void delete(T entity) {
        try {
            getSession().delete(entity);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }    
    }

    /**
     * return the a <code>Connection</code>
     *
     * @deprecated don't use connection directly
     * @return a database <code>Connection</code> 
     */
    public Connection getConnection() {
        try {
            return getSession().connection();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }
    
    public void detach(T o) {
    	getSession().evict(o);
    }
    
    public void refresh(T o) {
    	getSession().refresh(o);
    }
    
    @SuppressWarnings("unchecked")
	public T merge(T entity) {
    	
    	return (T)getSession().merge(entity);
    	
    }
}
