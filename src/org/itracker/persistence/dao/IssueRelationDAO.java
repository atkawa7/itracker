package org.itracker.persistence.dao;

import org.itracker.model.IssueRelation;

/**
 * 
 */
public interface IssueRelationDAO {
    
    public IssueRelation findByPrimaryKey(Integer relationId);
}
