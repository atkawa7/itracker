package org.itracker.model;

import java.io.Serializable;

/**
 * A database entity. 
 * 
 * @author johnny
 */
public interface Entity extends Serializable, Cloneable {
    
    /**
     * Returns the system ID. 
     * 
     * @return ID > 0 for persistent entities, <tt>null</tt> for transient ones
     */
    Integer getId();
    
    /**
     * Sets this entity's system ID. 
     * 
     * @param id ID > 0 for persistent entities, <tt>null</tt> for transient ones
     */
    void setId(Integer id);
    
}
