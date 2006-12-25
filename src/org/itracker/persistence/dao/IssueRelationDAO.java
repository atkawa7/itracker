package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.IssueRelation;

/**
 * 
 */
public interface IssueRelationDAO {
    
    IssueRelation findByPrimaryKey(Integer relationId);
    
    List<IssueRelation> findByIssue(Integer issueId);
    
}
