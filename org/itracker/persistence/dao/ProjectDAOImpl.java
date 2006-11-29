package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Project;

/**
 * This is the implementation bean that seems to make the Hibernate request.
 * 
 * @author ready
 */
public class ProjectDAOImpl extends BaseHibernateDAOImpl<Project> 
        implements ProjectDAO {

    public Project findByPrimaryKey(Integer projectId) {        
        try {
            return (Project)getSession().load(Project.class, projectId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    @SuppressWarnings("unchecked") 
    public List<Project> findAll() {
        Criteria criteria = getSession().createCriteria(Project.class);
        
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    @SuppressWarnings("unchecked") 
    public List<Project> findAllAvailable() {
    	  Criteria criteria = getSession().createCriteria(Project.class);
    	  criteria.add( Expression.eq ("status" , 1));
          
          try {
              return criteria.list();
          } catch (HibernateException e) {
              throw convertHibernateAccessException(e);
          }
    }

}
