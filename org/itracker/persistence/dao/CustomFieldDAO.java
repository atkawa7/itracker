package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.CustomField;


public interface CustomFieldDAO extends BaseDAO<CustomField> {

    public CustomField findByPrimaryKey(Integer customFieldId);

    public List<CustomField> findAll();
    
}
