package org.itracker.services.implementations;

import org.hibernate.*;
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
import org.itracker.services.ITrackerServices;
import org.itracker.services.IssueService;
import org.itracker.services.IssueSearchService;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.services.SchedulerService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.util.EmailService;

//TODO: clean up messy stuff by refactoring
/**
 * 
 * Service layer is a bit messy. The are *Factories, which work mainly as data access objects,
 * and *Handlers, that work as the service layer. It's messy because it was a straight EJB migration,
 * and they were not refactored yet.
 * 
 * @author ricardo
 *
 */
public class ITrackerServicesImpl implements ITrackerServices {
    
    private UserDAO userDAO;
    private ScheduledTaskDAO scheduledTaskDAO;
    private ConfigurationDAO configurationDAO;
    private LanguageDAO languageDAO;
    private CustomFieldDAO customFieldDAO;
    private ReportDAO reportDAO;
    private ProjectDAO projectDAO;
    private UserPreferencesDAO userPreferencesDAO;
    private PermissionDAO permissionDAO;
    private IssueDAO issueDAO;
    private IssueHistoryDAO issueHistoryDAO;
    private NotificationDAO notificationDAO;
    private WorkflowScriptDAO workflowScriptDAO;
    private SessionFactory sessionFactory;
    
    private IssueService issueService;
    private UserService userService;
    private ProjectService projectService;
    private ConfigurationService configurationService;
    private SchedulerService schedulerService;
    private ReportService reportService;
    private IssueSearchService issueSearchService;
    private EmailService emailService;
    
    // Factories
    
    public ScheduledTaskDAO getScheduledTaskDAO() {       
        return scheduledTaskDAO;
    }
    
    public void setScheduledTaskDAO(ScheduledTaskDAO scheduledTaskDAO) {
        this.scheduledTaskDAO = scheduledTaskDAO;
    }

    public ConfigurationDAO getConfigurationDAO() {       
        return configurationDAO;
    }

    public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    }
    
    public LanguageDAO getLanguageDAO() {        
        return languageDAO;
    }

    public void setLanguageDAO(LanguageDAO languageDAO) {
        this.languageDAO = languageDAO;
    }
    
    public CustomFieldDAO getCustomFieldDAO() {               
        return customFieldDAO;
    }

    public void setCustomFieldDAO(CustomFieldDAO customFieldDAO) {
        this.customFieldDAO = customFieldDAO;
    }
    
    public UserDAO getUserDAO() {       
        return userDAO;
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }    

    public ReportDAO getReportDAO() {        
        return reportDAO;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    } 
    
    public ProjectDAO getProjectDAO() {        
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    } 
    
    public UserPreferencesDAO getUserPreferencesDAO() {         
        return userPreferencesDAO;
    }

    public void setUserPreferencesDAO(UserPreferencesDAO userPreferencesDAO) {
        this.userPreferencesDAO = userPreferencesDAO;
    } 
    
    public PermissionDAO getPermissionDAO() {        
        return permissionDAO;
    }

    public void setPermissionDAO(PermissionDAO permissionDAO) {
        this.permissionDAO = permissionDAO;
    } 
    
    public IssueDAO getIssueDAO() {        
        return issueDAO;
    }
    
    public void setIssueDAO(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }    

    public IssueHistoryDAO getIssueHistoryDAO() {           
        return issueHistoryDAO;
    }

    public void setIssueHistoryDAO(IssueHistoryDAO issueHistoryDAO) {
        this.issueHistoryDAO = issueHistoryDAO;
    }
    
    public NotificationDAO getNotificationDAO() {           
        return notificationDAO;
    }

    public void setNotificationDAO(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }
    
    public IssueRelationDAO getIssueRelationDAO() {              
        throw new UnsupportedOperationException();
    }

    public ComponentDAO getComponentDAO() {
        throw new UnsupportedOperationException();
    }   
    
    public WorkflowScriptDAO getWorkflowScriptDAO() {
        return(workflowScriptDAO);
    }
    
    public void setWorkflowScriptDAO(WorkflowScriptDAO workflowScriptDAO) {
        this.workflowScriptDAO = workflowScriptDAO;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    //  Handlers
    
    public IssueService getIssueService() {        
        return issueService;
    }

    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }
    
    public UserService getUserService() {        
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public ProjectService getProjectService() {        
        return projectService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public ReportService getReportService() {        
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public ConfigurationService getConfigurationService() {        
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public SchedulerService getSchedulerService() {
        return schedulerService;
    }
    
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public IssueSearchService getIssueSearchService() {        
        return issueSearchService;
    }    

    public void setIssueSearchService(IssueSearchService issueSearchService) {        
        this.issueSearchService = issueSearchService;
    }    

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    
}
