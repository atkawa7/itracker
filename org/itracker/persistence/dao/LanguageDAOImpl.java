package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Language;

/**
 * 
 */
public class LanguageDAOImpl extends BaseHibernateDAOImpl<Language> 
        implements LanguageDAO {

    public Language findByKeyAndLocale(String key, String base_locale) {
        Criteria criteria = getSession().createCriteria(Language.class);
        criteria.add(Expression.eq("resourceKey", key));
        criteria.add(Expression.eq("locale", base_locale));
        try {
            Language language = (Language) criteria.uniqueResult();
            
            if (language == null) {
                throw new NoSuchEntityException("No language item for " + key + " " + base_locale);
            }
            return (language);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    @SuppressWarnings("unchecked") 
    public List<Language> findByKey(String key) {
        Criteria criteria = getSession().createCriteria(Language.class);
        criteria.add(Expression.eq("resourceKey", key));
        
        try {
            return (criteria.list());
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    @SuppressWarnings("unchecked") 
    public List<Language> findByLocale(String base_locale) {
        Criteria criteria = getSession().createCriteria(Language.class);
        criteria.add(Expression.eq("locale", base_locale));
        
        try {
            return (criteria.list());
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

}
