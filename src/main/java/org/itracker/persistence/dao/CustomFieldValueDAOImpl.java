package org.itracker.persistence.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.itracker.model.CustomFieldValue;

/**
 *
 * @author mbae
 */
public class CustomFieldValueDAOImpl 
        extends BaseHibernateDAOImpl<CustomFieldValue> 
        implements CustomFieldValueDAO {

    public CustomFieldValue findByPrimaryKey(Integer customFieldId) {
        try {
            return (CustomFieldValue)getSession().load(CustomFieldValue.class, customFieldId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<CustomFieldValue> findAll() {
        try {
            return getSession().createCriteria(CustomFieldValue.class).list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
