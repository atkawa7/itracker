package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.Project;

/**
 * 
 */
public interface ProjectDAO extends BaseDAO<Project> {
    
    public Project findByPrimaryKey(Integer projectId);

    public List<Project> findAll();

    public List<Project> findAllAvailable();
    
}
