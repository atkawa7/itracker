package org.itracker.persistence.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.User;

/**
 * 
 */
public class UserDAOImpl extends BaseHibernateDAOImpl<User> implements UserDAO {

    public User findByPrimaryKey(Integer userId) {
        try {
            return (User)getSession().get(User.class, userId);
        } catch (ObjectNotFoundException ex) {
            // PENDING: throw NoSuchEntityException instead of returning null ? Yes, please. 
            return null;
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }       
    }

    /**
     * Searches all permissions for the given user and sorts it by project. The
     * <code>HashMap</code> returned has the project ids as key (<code>Integer</code>)
     * and a <code>HashSet</code> as values. The <code>HashSet</code> holds a set of 
     * string representation of the permission
     *
     * @param user The user of interest
     * @param requestSource requested by
     * @return HashMap of permission keyed by project ids
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, Set<PermissionType>> getUserPermissions(User user, int requestSource) {
        
        // create hashMap to hold permission by project id as key
        final Map<Integer, Set<PermissionType>> permissionsByProjectId = 
                new HashMap<Integer, Set<PermissionType>>();
        
        try {
            // load user bean
            User userBean = (User) getSession().load(User.class, user.getId());
            // create criteria
            Criteria criteria = getSession().createCriteria(Permission.class);
            criteria.add( Expression.eq ("user" , userBean) );
            criteria.addOrder( Order.asc( "project" ));
            // perform search
            List<Permission> permissionsList = criteria.list();
            
            for (int i = 0; i < permissionsList.size(); i++) {
                Permission permission = permissionsList.get(i);

                // Super user has access to all projects, which is indicated by the "null" project. 
                final Integer projectId = (permission.getProject() == null) 
                    ? null : permission.getProject().getId(); 

                Set<PermissionType> projectPermissions = permissionsByProjectId.get(projectId); 

                if (projectPermissions == null) {
                    // First permission for the project. 
                    projectPermissions = new HashSet<PermissionType>();
                    permissionsByProjectId.put(projectId, projectPermissions);
                } //else { // Add the permission to the existing set of permissions for the project. }

                PermissionType permissionType = PermissionType.fromInt(permission.getPermissionType());
                projectPermissions.add(permissionType);
            }
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return permissionsByProjectId;
    }

    public User findByLogin(String login) {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Expression.eq("login", login));
        
        try {
            return (User)criteria.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }
    
    @SuppressWarnings("unchecked") 
    public List<User> findAll() {
        Criteria criteria = getSession().createCriteria(User.class);
        
        try {
            return criteria.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> findActive() {        
        Criteria criteria = getSession().createCriteria(User.class);
        
        try {
            return criteria.add(Expression.gt("status", 0)).list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> findByStatus(int status) {
        Criteria criteria = getSession().createCriteria(User.class);
        
        try {
            return criteria.add(Expression.eq("status", status)).list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<User> findSuperUsers() {
        Criteria criteria = getSession().createCriteria(User.class);
        
        try {
            return criteria.add(Expression.eq("superUser", Boolean.TRUE)).list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> findByRegistrationType(int registrationType) {
        Criteria criteria = getSession().createCriteria(User.class);
        
        try {
            return criteria.add(Expression.eq("registrationType", registrationType)).list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }
    
}
