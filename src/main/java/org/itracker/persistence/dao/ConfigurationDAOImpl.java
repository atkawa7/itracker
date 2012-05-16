package org.itracker.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Configuration;

import java.util.List;

/**
 *
 */
public class ConfigurationDAOImpl extends BaseHibernateDAOImpl<Configuration>
        implements ConfigurationDAO {

    public Configuration findByPrimaryKey(Integer configId) {
        try {
            return (Configuration) getSession().get(Configuration.class, configId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Configuration> findByType(int type) {
        Criteria criteria = getSession().createCriteria(Configuration.class);
        criteria.add(Expression.eq("type", Integer.valueOf(type)));

        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Configuration> findByTypeAndValue(int type, String value) {
        Criteria criteria = getSession().createCriteria(Configuration.class);
        criteria.add(Expression.eq("type", Integer.valueOf(type)));
        criteria.add(Expression.eq("value", value));

        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
