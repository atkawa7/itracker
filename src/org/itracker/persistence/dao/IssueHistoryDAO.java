package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.IssueHistory;

/**
 * 
 */
public interface IssueHistoryDAO extends BaseDAO<IssueHistory> {
    
    public IssueHistory findByPrimaryKey(Integer entryId);

    public List<IssueHistory> findByIssueId(Integer issueId);
}
