package org.itracker.services;

import org.hibernate.SessionFactory;
import org.itracker.persistence.dao.ComponentDAO;
import org.itracker.persistence.dao.ConfigurationDAO;
import org.itracker.persistence.dao.CustomFieldDAO;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.IssueHistoryDAO;
import org.itracker.persistence.dao.IssueRelationDAO;
import org.itracker.persistence.dao.LanguageDAO;
import org.itracker.persistence.dao.NotificationDAO;
import org.itracker.persistence.dao.PermissionDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.ReportDAO;
import org.itracker.persistence.dao.ScheduledTaskDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.persistence.dao.UserPreferencesDAO;
import org.itracker.persistence.dao.WorkflowScriptDAO;
import org.itracker.services.util.EmailService;

/**
 * Service layer is a bit messy. The are *Factories, which work mainly as data access objects,
 * and *Handlers, that work as the service layer. It's messy because it was a straight EJB migration,
 * and they were not refactored yet.
 * 
 * @author ricardo
 *
 */
public interface ITrackerServices {
    
    ScheduledTaskDAO getScheduledTaskDAO();
    
    void setScheduledTaskDAO(ScheduledTaskDAO scheduledTaskDAO);

    ConfigurationDAO getConfigurationDAO();

    void setConfigurationDAO(ConfigurationDAO configurationDAO);
    
    LanguageDAO getLanguageDAO();

    void setLanguageDAO(LanguageDAO languageDAO);
    
    CustomFieldDAO getCustomFieldDAO();

    void setCustomFieldDAO(CustomFieldDAO customFieldDAO);
    
    UserDAO getUserDAO();
    
    void setUserDAO(UserDAO userDAO);

    ReportDAO getReportDAO();

    void setReportDAO(ReportDAO reportDAO);
    
    ProjectDAO getProjectDAO();

    void setProjectDAO(ProjectDAO projectDAO);
    
    UserPreferencesDAO getUserPreferencesDAO();

    void setUserPreferencesDAO(UserPreferencesDAO userPreferencesDAO);
    
    PermissionDAO getPermissionDAO();
    
    void setPermissionDAO(PermissionDAO permissionDAO);
    
    IssueDAO getIssueDAO();
    
    void setIssueDAO(IssueDAO issueDAO);
    
    IssueHistoryDAO getIssueHistoryDAO();

    void setIssueHistoryDAO(IssueHistoryDAO issueHistoryDAO);
    
    NotificationDAO getNotificationDAO();

    void setNotificationDAO(NotificationDAO notificationDAO);
    
    IssueRelationDAO getIssueRelationDAO();

    ComponentDAO getComponentDAO();
    
    WorkflowScriptDAO getWorkflowScriptDAO();
    
    void setWorkflowScriptDAO(WorkflowScriptDAO workflowScriptDAO);
    
    void setSessionFactory(SessionFactory sessionFactory);

    SessionFactory getSessionFactory();

    // Handlers ? 
    
    IssueService getIssueService();
   
    void setIssueService(IssueService issueService);

    UserService getUserService();
    
    void setUserService(UserService userService);
    
    ProjectService getProjectService();
    
    void setProjectService(ProjectService projectService);
    
    ConfigurationService getConfigurationService();
    
    SchedulerService getSchedulerService();

    ReportService getReportService();
    
    IssueSearchService getIssueSearchService();
    
    EmailService getEmailService();
    
}
