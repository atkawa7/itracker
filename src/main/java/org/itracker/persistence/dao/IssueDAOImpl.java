package org.itracker.persistence.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.services.util.IssueUtilities;

/**
 * Default implementation of <code>IssueDAO</code> using Hibernate.
 *
 * @author ready
 */
public class IssueDAOImpl extends BaseHibernateDAOImpl<Issue> implements IssueDAO {

    private ProjectDAO projectDAO;

    public Issue findByPrimaryKey(Integer issueId) {

        try {
        	Issue issue = (Issue) getSession().get(Issue.class, issueId);
            //    return (Issue)getSession().load(Issue.class, issueId);
            //} catch (ObjectNotFoundException onfe) {
            //    // PENDING: throw NoSuchEntityException instead of returning null ?
            //    return null;
            // make sure, its the actual database-object.
            getSession().refresh(issue);
            return issue;
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public Long countAllIssues() {

        final Long count;

        try {
            final Query query = getSession().getNamedQuery("IssueCountAll");
            count = (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        return count;

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

        try {
            Query query = getSession().getNamedQuery("IssuesByStatusQuery");
            query.setInteger("issueStatus", status);
            return query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThan(int maxExclusiveStatus) {

        final List<Issue> issues;

        try {
            Query query = getSession().getNamedQuery("IssuesByStatusLessThanQuery");
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
            Query query = getSession().getNamedQuery("IssuesByStatusLessThanEqualToQuery");
            query.setInteger("maxStatus", maxStatus);
            issues = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        return issues;

    }

    @SuppressWarnings("unchecked")
    public List<Issue> findByStatusLessThanEqualToInAvailableProjects(int maxStatus) {

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

    public Long countByProject(Integer projectId) {

        final Long count;

        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByProjectQuery");
            query.setInteger("projectId", projectId);
            count = (Long) query.uniqueResult();
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

    public Long countByProjectAndLowerStatus(Integer projectId,
                                            int maxExclusiveStatus) {

        final Long count;

        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByProjectAndLowerStatusQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("maxExclusiveStatus", maxExclusiveStatus);
            count = (Long) query.uniqueResult();
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

    public Long countByProjectAndHigherStatus(Integer projectId, int minStatus) {

        final Long count;

        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByProjectAndHigherStatusQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("minStatus", minStatus);
            count = (Long) query.uniqueResult();
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
            //@ToDo Check this query.
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

    public Long countByComponent(Integer componentId) {

        final Long count;

        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByComponentQuery");
            query.setInteger("componentId", componentId);
            count = (Long) query.uniqueResult();
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

    public Long countByVersion(Integer versionId) {

        final Long count;

        try {
            final Query query = getSession().getNamedQuery(
                    "IssueCountByVersionQuery");
            query.setInteger("versionId", versionId);
            count = (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        return count;

    }

    public Date latestModificationDate(Integer projectId) {

        final Date lastModifiedDate;

        try {
            final Query query = getSession().getNamedQuery(
                    "MaxIssueModificationDateQuery");
            query.setInteger("projectId", projectId);
            lastModifiedDate = (Date) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        return lastModifiedDate;

    }

    /**
     * It doens't really make sense for this method to receive projectDAO, it's just a quick
     * fix for the fact that IssueSearchQuery handles ids and not objects
     */
    @SuppressWarnings("unchecked")
    public List<Issue> query(
            IssueSearchQuery searchQuery,
            final User user,
            final Map<Integer, Set<PermissionType>> userPermissions) {

        Criteria criteria = getSession().createCriteria(Issue.class);

        // projects
        Collection<Project> projects = Collections.checkedCollection((Collection<Project>)searchQuery.getProjectsObjects(projectDAO), Project.class);

        if (projects.size() > 0) {
            criteria.add(Restrictions.in("project", projects));
        }

        // severities
        if (searchQuery.getSeverities().size() > 0) {
            criteria.add(Restrictions.in("severity", searchQuery.getSeverities()));
        }

        // status
        if (searchQuery.getStatuses().size() > 0) {
            criteria.add(Restrictions.in("status", searchQuery.getStatuses()));
        }

        // componentes
        if (searchQuery.getComponents().size() > 0) {
            criteria.add(Restrictions.in("components", searchQuery.getComponents()));
        }

        // versions
        if (searchQuery.getVersions().size() > 0) {
            criteria.add(Restrictions.in("version", searchQuery.getVersions()));
        }

        // creator
        if (searchQuery.getCreator() != null) {
            criteria.add(Restrictions.eq("creator", searchQuery.getCreator()));
        }

        // owner
        if (searchQuery.getOwner() != null) {
            criteria.add(Restrictions.eq("owner", searchQuery.getOwner()));
        }

        // description and history
        if (searchQuery.getText() != null && !searchQuery.getText().equals("")) {
            criteria.createAlias("history", "history").
                    add(Restrictions.or(
                            Restrictions.ilike("description", "%" + searchQuery.getText() + "%"),
                            Restrictions.ilike("history.description", "%" + searchQuery.getText() + "%")
                    ));
        }

        // resolution
        if (searchQuery.getResolution() != null) {
            criteria.add(Restrictions.eq("resolution", searchQuery.getResolution()));
        }

        // resolution
        if (searchQuery.getTargetVersion() != null) {
            criteria.add(Restrictions.eq("targetVersion", searchQuery.getTargetVersion()));
        }

        List<Issue> list = Collections.checkedList(Criteria.DISTINCT_ROOT_ENTITY.transformList(criteria.list()), Issue.class);

        // filter for permission
        list = new LinkedList<Issue>(Collections.checkedCollection(CollectionUtils.select(list, new Predicate() {
            public boolean evaluate(Object arg0) {
                return IssueUtilities.canViewIssue((Issue) arg0, user, userPermissions);
            }
        }), Issue.class));

        
        // sort
        String order = searchQuery.getOrderBy();
        if ("id".equals(order)) {
            Collections.sort(list, Issue.ID_COMPARATOR);
        } else if ("sev".equals(order)) {
            Collections.sort(list, Issue.SEVERITY_COMPARATOR);
        } else if ("proj".equals(order)) {
            Collections.sort(list, Issue.PROJECT_AND_STATUS_COMPARATOR);
        } else if ("owner".equals(order)) {
            Collections.sort(list, Issue.OWNER_AND_STATUS_COMPARATOR);
        } else if ("lm".equals(order)) {
            Collections.sort(list, Collections.reverseOrder(Issue.LAST_MODIFIED_DATE_COMPARATOR));
        } else {
            Collections.sort(list, Issue.STATUS_COMPARATOR);
        }

        return list;

    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    /**
     * {@inheritDoc}
     * @return 
     */
	@SuppressWarnings("unchecked")
	public List<Issue> findByTargetVersion(Integer versionId) {

	        try {
	            final Query query = getSession().getNamedQuery("FindByTargetVersion");
	            query.setInteger("versionId", versionId);
	            return query.list();
	        } catch (HibernateException ex) {
	            throw convertHibernateAccessException(ex);
	        }

		
	}


}
