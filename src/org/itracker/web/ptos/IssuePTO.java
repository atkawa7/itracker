package org.itracker.web.ptos;

import java.util.List;
import org.itracker.model.Component;
import org.itracker.model.IssueAttachment;
import org.itracker.model.Issue;
import org.itracker.model.IssueField;
import org.itracker.model.IssueHistory;
import org.itracker.model.IssueRelation;
import org.itracker.model.Notification;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.Version;


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
