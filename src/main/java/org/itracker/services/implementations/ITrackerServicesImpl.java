package org.itracker.services.implementations;

import org.itracker.services.ConfigurationService;
import org.itracker.services.ITrackerServices;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.util.EmailService;

//TODO: clean up messy stuff by refactoring
/**
 * 
 * Service layer is a bit messy. The are *Factories, which work mainly as data access objects,
 * and *Handlers, that work as the service layer. It's messy because it was a straight EJB migration,
 * and they were not refactored yet.
 * 
 * @author ricardow
 *
 */
public class ITrackerServicesImpl implements ITrackerServices {
    
    private IssueService issueService;
    private UserService userService;
    private ProjectService projectService;
    private ConfigurationService configurationService;    
    private ReportService reportService;
    private EmailService emailService;
    
    // Factories
           
	public ITrackerServicesImpl(IssueService issueService,
			UserService userService, ProjectService projectService,
			ConfigurationService configurationService,
			ReportService reportService, EmailService emailService) {
		super();
		this.issueService = issueService;
		this.userService = userService;
		this.projectService = projectService;
		this.configurationService = configurationService;
		this.reportService = reportService;
		this.emailService = emailService;
	}      
    
    public IssueService getIssueService() {        
        return issueService;
    }

    public UserService getUserService() {        
        return userService;
    }

    public ProjectService getProjectService() {        
        return projectService;
    }

    public ReportService getReportService() {        
        return reportService;
    }

    public ConfigurationService getConfigurationService() {        
        return configurationService;
    }

    public EmailService getEmailService() {
        return emailService;
    }  
}