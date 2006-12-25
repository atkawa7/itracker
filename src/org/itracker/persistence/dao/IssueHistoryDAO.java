package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.IssueHistory;

/**
 * 
 * 
 */
public interface IssueHistoryDAO extends BaseDAO<IssueHistory> {
    
    /**
     * Returns the issue history entry with the given primary key. 
     * 
     * @param entryId system ID
     * @return issue history entry or <tt>null</tt>
     */
    IssueHistory findByPrimaryKey(Integer entryId);

    /**
     * Finds all history entries for an Issue. 
     * 
     * @param issueId system ID
     * @return list of history entries in unspecified order
     */
    List<IssueHistory> findByIssueId(Integer issueId);
    
}
