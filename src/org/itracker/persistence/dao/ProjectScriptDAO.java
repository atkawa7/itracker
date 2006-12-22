package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.ProjectScript;

/**
 * Interface to define basic operations to deal with the 
 * <code>ProjectScript</code> entity
 */
public interface ProjectScriptDAO extends BaseDAO<ProjectScript> {
    
    /**
     * Find a <code>ProjectScript</code> by its primary key
     *
     * @param id primary key of the <code>ProjectScript</code>
     * @return The <code>ProjectScript</code> found
     */
    public ProjectScript findByPrimaryKey(Integer scriptId);
    
    /**
     * Finds all <code>ProjectScript</code>s
     *
     * @return a <code>Collection</code> with all <code>ProjectScript</code>s
     */
    public List<ProjectScript> findAll();

}
