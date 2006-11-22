package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Component;


public interface ComponentDAO extends BaseDAO<Component> {

    /**
     * Finds the component with the given ID. 
     * 
     * @param componentId ID of the component to retrieve
     * @return component with the given ID or <tt>null</tt> if none exists
     */
    public Component findByPrimaryKey(Integer componentId);

    /**
     * Finds all components of a given project. 
     * 
     * @param projectId ID of the project of which to retrieve all components
     * @return list of components, in unspecified order
     */
    public List<Component> findByProjectId(Integer projectId);

}
