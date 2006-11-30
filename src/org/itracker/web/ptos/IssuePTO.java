package org.itracker.web.ptos;

import org.itracker.model.Issue;


public class IssuePTO {
    
    private Issue issue;
    private boolean userCanEdit;
    private boolean userCanViewIssue;
    private boolean userHasIssueNotification;
    private String severityLocalizedString;
    private String statusLocalizedString;
    private String componentsSize;
    
    public IssuePTO(Issue issue) {
        this.issue = issue;
    }
    
    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }
    
    public String getSeverityLocalizedString() {
        return severityLocalizedString;
    }
    
    public void setSeverityLocalizedString(String severityLocalizedString) {
        this.severityLocalizedString = severityLocalizedString;
    }
    
    public String getStatusLocalizedString() {
        return statusLocalizedString;
    }
    
    public void setStatusLocalizedString(String statusLocalizedString) {
        this.statusLocalizedString = statusLocalizedString;
    }
    
    public boolean isUserCanEdit() {
        return userCanEdit;
    }
    
    public void setUserCanEdit(boolean userCanEdit) {
        this.userCanEdit = userCanEdit;
    }
    
    public boolean isUserCanViewIssue() {
        return userCanViewIssue;
    }
    
    public void setUserCanViewIssue(boolean userCanViewIssue) {
        this.userCanViewIssue = userCanViewIssue;
    }
    
    public boolean isUserHasIssueNotification() {
        return userHasIssueNotification;
    }
    
    public void setUserHasIssueNotification(boolean userHasIssueNotification) {
        this.userHasIssueNotification = userHasIssueNotification;
    }
    
    public String getComponentsSize() {
        return componentsSize;
    }
    
    public void setComponentsSize(String componentsSize) {
        this.componentsSize = componentsSize;
    }
    
}
