package org.itracker.persistence.dao;

import java.util.Date;
import java.util.List;
import org.itracker.model.Issue;

/**
 * Issue Data Access Object interface. 
 */
public interface IssueDAO extends BaseDAO<Issue> {

    /**
     * Finds the issue with the given ID. 
     * 
     * PENDING: should this method throw a NoSuchEntityException  
     * instead of returning null if the issue doesn't exist ? 
     * 
     * @param issueId ID of the issue to retrieve
     * @return issue with the given ID or <tt>null</tt> if none exits
     */
    Issue findByPrimaryKey(Integer issueId);
    
    /**
     * Finds all existing issues in all projects. 
     * 
     * @return list of exiting issues, in an unspecified order
     */
    List<Issue> findAll();

    /**
     * Finds all issues created by the user with the given ID, in all active 
     * projects. 
     * 
     * @param userId ID of the user who created the issues to return
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByCreatorInAvailableProjects(Integer userId, int status);

    /**
     * Finds all issues created by the user with the given ID in all projects. 
     * 
     * @param userId ID of the user who created the issues to return
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByCreator(Integer userId, int status);

    /**
     * Finds all issues owned by the user with the given ID, in all active 
     * projects.  
     * 
     * @param userId ID of the user who owns the issues to return
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByOwnerInAvailableProjects(Integer userId, int status);

    /**
     * Finds all issues owned by the user with the given ID in all projects. 
     * 
     * @param userId ID of the user who owns the issues to return
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByOwner(Integer userId, int status);

    /**
     * Finds all issues with notifications for the user with the given ID,  
     * in active projects. 
     * 
     * @param userId ID of the user with notifications for the issues to return
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByNotificationInAvailableProjects(Integer userId, int status);

    /**
     * Finds all issues with notifications for the user with the given ID 
     * in all projects. 
     * 
     * @param userId ID of the user with notifications for the issues to return
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByNotification(Integer userId, int status);

    /**
     * Finds all issues with a status less than or equal to the given status, 
     * in active projects.  
     * 
     * @param status all issues less that or equal to this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatusLessThanEqualToInAvailableProjects(int status);

    /**
     * Finds all issues with a status less than or equal to the given status 
     * in all projects. 
     * 
     * @param status all issues less that or equal to this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatusLessThanEqualTo(int status);

    /**
     * Finds all issues in the given status in all projects. 
     * 
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatus(int status);

    /**
     * Finds all issues with a status less than the given status in all projects. 
     * 
     * @param status all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatusLessThan(int status);

    /**
     * Finds all issues with the given severity in all projects. 
     * 
     * @param severity severity of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findBySeverity(int severity);

    /**
     * Finds all issues of the given project with a status lower than 
     * the given one. 
     * 
     * @param projectId ID of the project of which to retrieve the issues
     * @param status all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByProjectAndLowerStatus(Integer projectId, int status);

    /**
     * Finds all issues of the given project with a status higher than 
     * the given one. 
     * 
     * @param projectId ID of the project of which to retrieve the issues
     * @param status all issues above this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByProjectAndHigherStatus(Integer projectId, int status);

    /**
     * Finds all issues of the project with the given ID. 
     * 
     * @param projectId ID of the project of which to retrieve all issues
     * @return list of issues in no particular order
     */
    List<Issue> findByProject(Integer projectId);

    /**
     * Finds all issues of the component with the given ID. 
     * 
     * @param componentId ID of the component of which to retrieve all issues
     * @return list of issues in no particular order
     */
    List<Issue> findByComponent(Integer componentId);
    
    /**
     * Counts the number of issues attached to a component. 
     * 
     * @param componentId ID of the component
     * @return number of issues
     */
    int countByComponent(Integer componentId);
    
    /**
     * Finds all issues of the version with the given ID. 
     * 
     * @param versionId ID of the version of which to retrieve all issues
     * @return list of issues in no particular order
     */
    List<Issue> findByVersion(Integer versionId);
    
    /**
     * Returns the modification date of the latest modified issue 
     * in the project with the given id. 
     * 
     * @param projectId ID of the project of which to retrieve the issues
     * @return date of the most recent issue modification for the project. 
     *         <tt>null</tt> if no issue exists in the project
     */
    Date latestModificationDate(Integer projectId);

    /**
     * TODO: refactor this into countIssuesXXX methods. 
     */
    Object[] getIssueStats(Integer projectId);

}
