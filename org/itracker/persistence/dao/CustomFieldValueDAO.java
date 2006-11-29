package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.CustomFieldValue;

/**
 * 
 */
public interface CustomFieldValueDAO extends BaseDAO<CustomFieldValue> {

    public CustomFieldValue findByPrimaryKey(Integer customFieldId);

    public List findAll();
    
}
