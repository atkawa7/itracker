package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Component;
import org.itracker.model.Project;

public class ComponentDAOImpl extends BaseHibernateDAOImpl<Component> 
        implements ComponentDAO {

    public Component findByPrimaryKey(Integer componentId) {
        try {
            return (Component)getSession().get(Component.class, componentId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Component> findByProjectId(Integer projectId) {
            try {
            Project project = (Project) getSession().load(Project.class,projectId);
            return getSession().createCriteria(Component.class)
                    .add(Expression.eq("project", project))
                    .list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

}
