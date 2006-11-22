package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Issue;
import org.itracker.model.Notification;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 */
public class NotificationDAOImpl extends HibernateDaoSupport implements NotificationDAO {

    private final IssueDAO issueDAO;

    public NotificationDAOImpl(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }
    
    @SuppressWarnings("unchecked")
    public List<Notification> findByIssueId(Integer issueId) {
        List<Notification> notifications;
        
        Issue issue = issueDAO.findByPrimaryKey(issueId);
        Criteria criteria = getSession().createCriteria(Notification.class);
        criteria.add(Expression.eq("issue", issue));
        
        try {
            notifications = criteria.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return notifications;
    }

}
