package org.itracker.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;

/**
 * 
 */
public class UserPreferencesDAOImpl extends BaseHibernateDAOImpl<UserPreferences> 
        implements UserPreferencesDAO {

    private UserDAO userDAO;
    
    public UserPreferencesDAOImpl(UserDAO userDAO) {
        super();        
        this.userDAO = userDAO;
    }

    public UserPreferences findByUserId(Integer userId) {
        User user = userDAO.findByPrimaryKey(userId);
        
        Criteria criteria = getSession().createCriteria(UserPreferences.class);
        criteria.add(Expression.eq("user", user));
        
        try {
            return (UserPreferences)criteria.uniqueResult();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
