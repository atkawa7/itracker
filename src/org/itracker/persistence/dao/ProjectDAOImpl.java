package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.Project;

/**
 * This is the implementation bean that seems to make the Hibernate request.
 * 
 * @author ready
 */
public class ProjectDAOImpl extends BaseHibernateDAOImpl<Project> 
        implements ProjectDAO {

    public Project findByPrimaryKey(Integer projectId) {
        Project project;
        try {
            project = (Project)getSession().get(Project.class, projectId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return project;
    }
    
    @SuppressWarnings("unchecked") 
    public List<Project> findAll() {
        List<Project> projects;
        
        try {
            Query query = getSession().getNamedQuery("ProjectsAllQuery");
            projects = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return projects;
    }
    
    @SuppressWarnings("unchecked")
    public List<Project> findByStatus(int status) {
        List<Project> projects;
        
        try {
            Query query = getSession().getNamedQuery("ProjectsByStatusQuery");
            query.setInteger("projectStatus", status);
            projects = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return projects;
    }
    
    @SuppressWarnings("unchecked") 
    public List<Project> findAllAvailable() {
    	List<Project> projects;
        
        try {
            Query query = getSession().getNamedQuery("ProjectsAvailableQuery");
            projects = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return projects;
    }

}
