package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.IssueRelation;

/**
 * 
 */
public interface IssueRelationDAO extends BaseDAO<IssueRelation> {
    
    IssueRelation findByPrimaryKey(Integer relationId);
    
    List<IssueRelation> findByIssue(Integer issueId);
    
}
