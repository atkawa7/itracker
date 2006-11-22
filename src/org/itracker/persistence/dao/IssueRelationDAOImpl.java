package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.itracker.model.IssueRelation;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 */
public class IssueRelationDAOImpl extends HibernateDaoSupport 
        implements IssueRelationDAO {

    public IssueRelation findByPrimaryKey(Integer relationId) {
        try {
            return (IssueRelation)getSession().load(IssueRelation.class, relationId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

}
