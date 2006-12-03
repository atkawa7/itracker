package org.itracker.persistence.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.itracker.model.Issue;
import org.itracker.model.Project;
import org.itracker.services.util.IssueUtilities;


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
            issues = getSession().createCriteria(Issue.class).list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByCreatorInAvailableProjects(Integer userId, int status) {
        final List<Issue> issues;
        
        try {
            Query query = getSession().getNamedQuery(
                    "IssuesByCreatorInAvailableProjectsQuery");
            query.setInteger("projectStatus", Integer.valueOf(1));
            query.setInteger("creatorId", userId);
            query.setInteger("issueStatus", status);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return (List<Issue>)issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByCreator(Integer userId, int status) {
        final List<Issue> issues;
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.eq("creator.id", userId))
                    .add(Expression.eq("issue.status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    /*
     * Improved this criteria to only show the issues in available projecs.
     * I guess that's a join... I found documentation here
     * http://www.javalobby.org/articles/hibernatequery102/
     * @author ready
     */
    @SuppressWarnings("unchecked")
    public List<Issue> findByOwnerInAvailableProjects(Integer userId, int status) {
        final List<Issue> issues; 
        
        try {
            Criteria criteria = getSession().createCriteria(Issue.class)
                .createAlias("project","project")
                .add(Expression.eq("project.status", new Integer(1)));
            criteria.add(Expression.eq("owner.id", userId));
            criteria.add(Expression.ne("status", status));
            
            issues = (List<Issue>)criteria.list();
         
          // Equivalent HQL query: 
//        final String hql = 
//                "select issue from Issue as issue " + 
//                "inner join issue.project as project " +
//                "where project.status = :projectStatus " +
//                " and issue.owner.id = :ownerId " +
//                " and issue.status = :issueStatus";
//        
//        try {
//            return getSession().createQuery(hql)
//                    .setInteger("projectStatus", Integer.valueOf(1))
//                    .setInteger("ownerId", userId)
//                    .setInteger("issueStatus", status)
//                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByOwner(Integer userId, int status) {
        final List<Issue> issues;
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.eq("owner.id", userId))
                    .add(Expression.eq("status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByNotificationInAvailableProjects(Integer userId, int status) {
        final List<Issue> issues;
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssuesByNotificationInAvailableProjectsQuery");
            query.setInteger("userId", userId);
            query.setInteger("status", status);
            issues = query.list();
        } catch (HibernateException ex) { 
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByNotification(Integer userId, int status) {
        final List<Issue> issues; 
        
        try {
            final Query query = getSession().getNamedQuery(
                    "IssuesByNotificationQuery");
            query.setInteger("userId", userId);
            query.setInteger("status", status);
            issues = query.list();
        } catch (HibernateException ex) { 
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThanEqualToInAvailableProjects(int status) {
        final List<Issue> issues; 
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.le("status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThanEqualTo(int status) {
        final List<Issue> issues;
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.le("status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatus(int status) {
        final List<Issue> issues;
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.le("status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThan(int status) {
        final List<Issue> issues; 
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.lt("status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findBySeverity(int severity) {
        final List<Issue> issues;
        
        try {
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.eq("severity", severity))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByProjectAndLowerStatus(Integer projectId, int status) {
        final List<Issue> issues; 
        
        try {
            Project project = (Project) getSession().load(Project.class, projectId);
            issues = getSession().createCriteria(Issue.class)
                    .add(Expression.eq("project", project))
                    .add(Expression.lt("status", status))
                    .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByProjectAndHigherStatus(Integer projectId, int status) {
        final List<Issue> issues;
        
        try {
            Project project = (Project) getSession().load(Project.class, projectId);
            issues = getSession().createCriteria(Issue.class)
                   .add(Expression.eq("project", project))
                   .add(Expression.lt("status", status))
                   .list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return issues;
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByProject(Integer projectId) {
        final List<Issue> issues;
        
        try {
            Project project = (Project) getSession().load(Project.class, projectId);
            issues = getSession().createCriteria(Issue.class)
                   .add(Expression.eq("project", project))
                   .list();
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

    public Object[] getIssueStats(Integer projectId) {
        /* TODO: move this method to IssueServiceImpl */
        
        Object[] issueStats = new Object[4];

        int totalIssues = 0;
        Collection openIds = findByProjectAndLowerStatus(projectId, IssueUtilities.STATUS_RESOLVED);
        // Collection openIds = ejbSelectIdByProjectAndStatusLessThan(projectId,
        // IssueUtilities.STATUS_RESOLVED);
        issueStats[0] = (openIds == null ? "0" : Integer.toString(openIds.size()));
        totalIssues += openIds.size();
        Collection resolvedIds = findByProjectAndHigherStatus(projectId, IssueUtilities.STATUS_RESOLVED);
        issueStats[1] = (resolvedIds == null ? "0" : Integer.toString(resolvedIds.size()));
        totalIssues += resolvedIds.size();

        issueStats[2] = Integer.toString(totalIssues);
        issueStats[3] = new Date();// ejbHomeLatestModificationDate(projectId);

        return issueStats;
    }

}
