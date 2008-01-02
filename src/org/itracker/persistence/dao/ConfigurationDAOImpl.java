package org.itracker.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Configuration;
 
/**
 * 
 */
public class ConfigurationDAOImpl extends BaseHibernateDAOImpl<Configuration> 
        implements ConfigurationDAO {
 
    public Configuration findByPrimaryKey(Integer configId) {
        try {
            return (Configuration)getSession().get(Configuration.class, configId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Configuration> findByType(int type) {
        Criteria criteria = getSession().createCriteria(Configuration.class);
        criteria.add(Expression.eq("type", new Integer(type)));
        
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Configuration> findByTypeAndValue(int type, String value) {
        Criteria criteria = getSession().createCriteria(Configuration.class);
        criteria.add(Expression.eq("type", new Integer(type)));
        criteria.add(Expression.eq("value", value));
        
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
