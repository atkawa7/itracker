package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.IssueActivity;

public interface IssueActivityDAO extends BaseDAO<IssueActivity> {

    public List<IssueActivity> findByIssueId(Integer issueId);

    public List<IssueActivity> findByIssueIdAndNotification(Integer issueId, int i);

}
