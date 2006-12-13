package org.itracker.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Permission;

public class PermissionDAOImpl extends BaseHibernateDAOImpl<Permission> 
        implements PermissionDAO {
    
    @SuppressWarnings("unchecked")
    public List<Permission> findByProjectIdAndPermission(Integer projectId, int permissionType) {
        Criteria criteria = getSession().createCriteria(Permission.class)
            .add(Expression.eq("permissionType", permissionType))
            .add(Expression.eq("project.id", projectId));
        
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Permission> findByUserId(Integer userId) {
        Criteria criteria = getSession().createCriteria(Permission.class)
            .add(Expression.eq("user.id", userId));
        
        try {
            return criteria.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

}
