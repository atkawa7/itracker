package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Project;

/**
 * 
 */
public interface ProjectDAO extends BaseDAO<Project> {
    
    /**
     * Finds a project by ID. 
     * 
     * @param projectId sytem ID
     * @return project instance or <tt>null</tt>
     */
    Project findByPrimaryKey(Integer projectId);

    /**
     * Finds all projects. 
     * 
     * @return list of all existing projects, in unspecified order
     */
    List<Project> findAll();

    /**
     * Finds all projects with a given status. 
     * 
     * @param status project status
     * @return list of projects in unspecified order
     */
    List<Project> findByStatus(int status);
    
    /**
     * Finds all projects that are active or viewable. 
     * 
     * @return list of projects in unspecified order
     */
    List<Project> findAllAvailable();
    
}
