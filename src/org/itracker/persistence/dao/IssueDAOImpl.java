package org.itracker.persistence.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.Issue;

/**
 * Default implementation of <code>IssueDAO</code> using Hibernate. 
 * 
 * @author ready
 */
public class IssueDAOImpl extends BaseHibernateDAOImpl<Issue> implements IssueDAO {
    
    /**
     * Default constructor. 
     */
    public IssueDAOImpl() {
    }

    public Issue findByPrimaryKey(Integer issueId) {
        try {
            return (Issue)getSession().get(Issue.class, issueId);
        //    return (Issue)getSession().load(Issue.class, issueId);
        //} catch (ObjectNotFoundException onfe) {
        //    // PENDING: throw NoSuchEntityException instead of returning null ?
        //    return null;
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findAll() {
        final List<Issue> issues;
        
        try {
            issues = getSession().getNamedQuery("IssuesAllQuery").list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatus(int status) {
        final List<Issue> issues; 
        
        try {
            Query query = getSession().getNamedQuery("IssuesByStatusQuery");
            query.setInteger("issueStatus", status);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThan(int maxExclusiveStatus) {
        final List<Issue> issues; 
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByStatusLessThanQuery");
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThanEqualTo(int maxStatus) {
        final List<Issue> issues; 
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByStatusLessThanEqualToQuery");
            query.setInteger("maxStatus", maxStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThanEqualToInAvailableProjects(
            int maxStatus) {
        final List<Issue> issues; 
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByStatusLessThanEqualToInAvailableProjectsQuery");
            query.setInteger("maxStatus", maxStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findBySeverity(int severity) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery("IssuesBySeverityQuery");
            query.setInteger("severity", severity);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByProject(Integer projectId) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery("IssuesByProjectQuery");
            query.setInteger("projectId", projectId);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    public int countByProject(Integer projectId) {
        final Integer count;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByProjectQuery");
            query.setInteger("projectId", projectId);
            count = (Integer)query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return count;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByProjectAndLowerStatus(Integer projectId, 
            int maxExclusiveStatus) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByProjectAndLowerStatusQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    public int countByProjectAndLowerStatus(Integer projectId, 
            int maxExclusiveStatus) {
        final Integer count;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByProjectAndLowerStatusQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            count = (Integer)query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByProjectAndHigherStatus(Integer projectId, 
            int status) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByProjectAndHigherStatusQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("minStatus", status);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    public int countByProjectAndHigherStatus(Integer projectId, int minStatus) {
        final Integer count;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByProjectAndHigherStatusQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("minStatus", minStatus);
            count = (Integer)query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return count;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByOwner(Integer ownerId, int maxExclusiveStatus) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery("IssuesByOwnerQuery");
            query.setInteger("ownerId", ownerId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByOwnerInAvailableProjects(Integer ownerId, 
            int maxExclusiveStatus) {
        final List<Issue> issues; 
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByOwnerInAvailableProjectsQuery");
            query.setInteger("ownerId", ownerId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findUnassignedIssues(int maxStatus) {
        final List<Issue> issues; 
        
        try {
            Query query = getSession().getNamedQuery("IssuesUnassignedQuery");
            query.setInteger("maxStatus", maxStatus);
            
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByCreator(Integer creatorId, 
            int maxExclusiveStatus) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery("IssuesByCreatorQuery");
            query.setInteger("creatorId", creatorId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByCreatorInAvailableProjects(Integer creatorId, 
            int maxExclusiveStatus) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByCreatorInAvailableProjectsQuery");
            query.setInteger("creatorId", creatorId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByNotification(Integer userId, 
            int maxExclusiveStatus) {
        final List<Issue> issues; 
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssuesByNotificationQuery");
            query.setInteger("userId", userId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) { 
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByNotificationInAvailableProjects(Integer userId, 
            int maxExclusiveStatus) {
        final List<Issue> issues;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssuesByNotificationInAvailableProjectsQuery");
            query.setInteger("userId", userId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            issues = query.list();
        } catch (HibernateException ex) { 
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByComponent(Integer componentId) {
        final List<Issue> issues;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssuesByComponentQuery");
            query.setInteger("componentId", componentId);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    public int countByComponent(Integer componentId) {
        final Integer count;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByComponentQuery");
            query.setInteger("componentId", componentId);
            count = (Integer)query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return count;
    }
    
    @SuppressWarnings("unchecked")
    public List<Issue> findByVersion(Integer versionId) {
        final List<Issue> issues;
        
        try {
             final Query query = getSession().getNamedQuery(
                     "IssuesByVersionQuery");
             query.setInteger("versionId", versionId);
             issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }
    
    public int countByVersion(Integer versionId) {
        final Integer count;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByVersionQuery");
            query.setInteger("versionId", versionId);
            count = (Integer)query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return count;
    }
    
    @SuppressWarnings("unchecked")
    public Date latestModificationDate(Integer projectId) {
        final Date lastModifiedDate; 
        
        try {
            final Query query = getSession().getNamedQuery(
                    "MaxIssueModificationDateQuery");
            query.setInteger("projectId", projectId);
            lastModifiedDate = (Date)query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return lastModifiedDate;
    }

}
