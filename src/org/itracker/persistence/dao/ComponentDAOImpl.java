package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Component;
import org.itracker.model.Project;

public class ComponentDAOImpl extends BaseHibernateDAOImpl<Component>
        implements ComponentDAO {
    
    public Component findById(Integer componentId) {
        try {
            return (Component)getSession().get(Component.class, componentId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Component> findByProject(Integer projectId) {
        try {
            return getSession().createCriteria(Component.class)
                .add(Expression.eq("project.id", projectId))
                .list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
}
