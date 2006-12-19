package org.itracker.persistence.dao;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Transaction;

/**
 * Contains common behaviour to all hibernate factories
 * 
 * @author rui silva
 */
public abstract class BaseHibernateDAOImpl<T> extends HibernateDaoSupport 
        implements BaseDAO<T> {

    /**
     * 
     */
    public BaseHibernateDAOImpl() {
        super();
    }

    public void save(T entity) {
        try {
            Transaction tx = getSession().beginTransaction();
            getSession().save(entity);
            tx.commit();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    /**
     * 
     */
    public void saveOrUpdate(T entity) {
        try {
            Transaction tx = getSession().beginTransaction();
            getSession().saveOrUpdate(entity);
            tx.commit();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public void delete(T entity) {
        try {
            Transaction tx = getSession().beginTransaction();
            getSession().delete(entity);
            tx.commit();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }    
    }

    /**
     * return the a <code>Connection</code>
     *
     * @return a database <code>Connection</code> 
     */
    public Connection getConnection() {
        try {
            return getSession().connection();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }
    
}
