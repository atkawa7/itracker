package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Issue;
import org.itracker.model.IssueHistory;

public class IssueHistoryDAOImpl extends BaseHibernateDAOImpl<IssueHistory> 
        implements IssueHistoryDAO {

    private final IssueDAO issueDAO;

    public IssueHistoryDAOImpl(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }
    
    public IssueHistory findByPrimaryKey(Integer entryId) { 
        try {
            return (IssueHistory)getSession().load(IssueHistory.class,entryId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }    
    }

    @SuppressWarnings("unchecked")
    public List<IssueHistory> findByIssueId(Integer issueId) {
        Issue issue = issueDAO.findByPrimaryKey(issueId);
        Criteria criteria = getSession().createCriteria(IssueHistory.class);
        criteria.add(Expression.eq("issue", issue));
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

}
