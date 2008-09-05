package org.itracker.persistence.dao;

import java.sql.Connection;

import org.itracker.model.Entity;

/**
 * 
 */
public interface BaseDAO<T extends Entity> {
    
    public void save(T entity);
    
    public void saveOrUpdate(T entity);
    
    public void delete(T entity);
    
    /**
     * This method is a great bastardization of the architecture
     * It's here temporarily because the search facility requires connections
     * and operates on JDBC. This should be change to work via the service interfaces...
     * 
     * @deprecated don't work directly with connection
     * @return
     */
    public Connection getConnection();
    public void detach(T entity);
    public void refresh(T entity);
    /**
     * 
     * @param entity
     * @return
     */
    public T merge(T entity);
}
