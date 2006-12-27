package org.itracker.persistence.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.Notification;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 */
public class NotificationDAOImpl extends HibernateDaoSupport 
        implements NotificationDAO {

    public NotificationDAOImpl() {
    }
    
    public Notification findById(Integer id) {
        Notification notification;
        
        try {
            notification = (Notification)getSession().get(Notification.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return notification;
    }
    
    @SuppressWarnings("unchecked")
    public List<Notification> findByIssueId(Integer issueId) {
        List<Notification> notifications;
        
        try {
            Query query = getSession().getNamedQuery(
                    "NotificationsByIssueQuery");
            query.setInteger("issueId", issueId);
            notifications = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return notifications;
    }

}
