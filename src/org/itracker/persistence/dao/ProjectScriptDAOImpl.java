package org.itracker.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.itracker.model.ProjectScript;

public class ProjectScriptDAOImpl extends BaseHibernateDAOImpl<ProjectScript> 
        implements ProjectScriptDAO {

    public ProjectScript findByPrimaryKey(Integer scriptId) {
        // TODO Auto-generated method stub
        try {
            ProjectScript projectScriptBean = new ProjectScript();
            projectScriptBean = (ProjectScript) getSession().get(ProjectScript.class, scriptId);
            return projectScriptBean;
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }
    /** 
     * Finds all <code>ProjectScript</code>s
     *
     * @return a <code>Collection</code> with all <code>ProjectScript</code>s
     */
    @SuppressWarnings("unchecked")
    public List<ProjectScript> findAll() {        
        Criteria criteria = getSession().createCriteria(ProjectScript.class);        
        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

}
