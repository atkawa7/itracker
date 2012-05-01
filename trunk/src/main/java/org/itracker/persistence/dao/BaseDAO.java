package org.itracker.persistence.dao;

import org.hibernate.Session;
import org.itracker.model.Entity;

/**
 * 
 */
public interface BaseDAO<T extends Entity> {
    /**
     * 
     * Insert a new entity.
     * create- and lastmodified-date is set with current time.
     * 
     * @see Session#save(Object)
     * 
     * @param entity - detached entity object
     */
    public void save(T entity);
    /**
     * 
     * Inserts a new detached entity or updates if it already exists.
     * create- and update-date are set automatically.
     * 
     * @see Session#saveOrUpdate(Object)
     * 
     * @param entity - entity object to be inserted or updated
     */
    public void saveOrUpdate(T entity);
    
    /**
     * 
     * Deletes entity from persistence store.
     * 
     * @see Session#delete(Object)
     * 
     * @param entity
     */
    public void delete(T entity);
    
    /**
     * 
	 * Remove this instance from the session cache.
	 * 
	 * @see Session#evict(Object)
	 * 
     * @param entity
     */
    public void detach(T entity);
    
    /**
     * 
     * Reloads an entity from persistance.
     * 
     * @see Session#refresh(Object)
     * 
     * @param entity
     */
    public void refresh(T entity);
    /**
     * 
     * Copy the state of the given object onto the persistent object with the same
	 * identifier. If there is no persistent instance currently associated with
	 * the session, it will be loaded.
	 * 
     * @see Session#merge(Object)
     * 
     * @param entity
     * @return
     */
    public T merge(T entity);
}
