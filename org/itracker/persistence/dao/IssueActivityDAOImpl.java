package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.IssueActivity;
import org.itracker.model.Issue;


public class IssueActivityDAOImpl extends BaseHibernateDAOImpl<IssueActivity> 
        implements IssueActivityDAO {

    private final IssueDAO issueDAO;

    public IssueActivityDAOImpl(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }

    @SuppressWarnings("unchecked")
    public List<IssueActivity> findByIssueId(Integer issueId) {
        Issue issue = issueDAO.findByPrimaryKey(issueId);
        return issue.getActivities();
    }

    public List<IssueActivity> findByIssueIdAndNotification(Integer issueId, int i) {
        throw new UnsupportedOperationException();
    }

}
