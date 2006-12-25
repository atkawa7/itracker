/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A project issue. 
 * 
 * @author ready
 */
public class Issue extends AbstractEntity implements Comparable<Issue> {

    public static final Comparator<Issue> STATUS_COMPARATOR = 
            new StatusComparator();
    
    public static final Comparator<Issue> PROJECT_AND_STATUS_COMPARATOR =
            new ProjectAndStatusComparator();
    
    public static final Comparator<Issue> OWNER_AND_STATUS_COMPARATOR =
            new OwnerAndStatusComparator();
    
    public static final Comparator<Issue> SEVERITY_COMPARATOR =
            new SeverityComparator();
    
    private String description;
    
    private int severity;
    
    private int status;
    
    /* PENDING: consider using an int enumeration like severity and status. */
    private String resolution;
    
    private Project project;
    
    private User creator;
    
    private User owner;
    
    /** Project version for which this issue must be fixed. */
    private Version targetVersion;
    
    /** List of project components affected by this Issue. */
    private List<Component> components = new ArrayList<Component>();
    
    /** List of project versions affected by this Issue. */
    private List<Version> versions = new ArrayList<Version>();
    
    /** List of custom fields and values. */
    private List<IssueField> fields = new ArrayList<IssueField>();
    
    private List<IssueAttachment> attachments = new ArrayList<IssueAttachment>();
    
    private List<IssueRelation> relations = new ArrayList<IssueRelation>();
    
    /* PENDING: do we really need to navigate these relationships from an Issue ? 
     * Moving these as DAO methods would make an Issue more light-weight. 
     */
    private List<Notification> notifications = new ArrayList<Notification>();
    private List<IssueActivity> activities = new ArrayList<IssueActivity>();
    private List<IssueHistory> history = new ArrayList<IssueHistory>();
    
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public Issue() {
    }
    
    public List<IssueActivity> getActivities() {
        return activities;
    }
    
    public void setActivities(List<IssueActivity> activities) {
        this.activities = activities;
    }
    
    public List<IssueAttachment> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<IssueAttachment> attachments) {
        this.attachments = attachments;
    }
    
    public List<Component> getComponents() {
        return components;
    }
    
    public void setComponents(List<Component> components) {
        this.components = components;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<IssueField> getFields() {
        return fields;
    }
    
    public void setFields(List<IssueField> fields) {
        this.fields = fields;
    }
    
    public List<IssueHistory> getHistory() {
        return history;
    }
    
    public void setHistory(List<IssueHistory> history) {
        this.history = history;
    }
    
    public List<Notification> getNotifications() {
        return notifications;
    }
    
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public List<IssueRelation> getRelations() {
        return relations;
    }
    
    public void setRelations(List<IssueRelation> relations) {
        this.relations = relations;
    }
    
    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    public int getSeverity() {
        return severity;
    }
    
    public void setSeverity(int severity) {
        this.severity = severity;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public Version getTargetVersion() {
        return targetVersion;
    }
    
    public void setTargetVersion(Version targetVersion) {
        this.targetVersion = targetVersion;
    }
    
    public List<Version> getVersions() {
        return versions;
    }
    
    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }
    
    /**
     * Compares by status. 
     */
    public int compareTo(Issue other) {
        return STATUS_COMPARATOR.compare(this, other);
    }
    
    /**
     * TODO: fix this!
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Issue) {
            final Issue other = (Issue)obj;
            
            return this.id == other.id;
        }
        return false;
    }
    
    /**
     * TODO: fix this!
     */
    @Override
    public int hashCode() {
        return (this.id == null) ? 0 : this.id.intValue();
    }
    
    @Override
    public String toString() {
        return "Issue [id=" + this.id + "]";
    }
    
    
    /**
     * Compares 2 Issues by status and severity. 
     */
    private static class StatusComparator implements Comparator<Issue> {

        public int compare(Issue a, Issue b) {
            final int statusComparison = a.status - b.status;
            
            if (statusComparison == 0) {
                return a.severity - b.severity;
            }
            return statusComparison;
        }
        
    }

    private static class ProjectAndStatusComparator implements Comparator<Issue> {

        public int compare(Issue a, Issue b) {
            final int projectComparison = a.project.compareTo(b.project);
            
            if (projectComparison == 0) {
                return STATUS_COMPARATOR.compare(a, b);
            }
            return projectComparison;
        }
        
    }

    private static class OwnerAndStatusComparator implements Comparator<Issue> {

        public int compare(Issue a, Issue b) {
            // PENDING : Used to be a last + first name comparison. 
            final int ownerComparison = a.owner.compareTo(b.owner);
            
            if (ownerComparison == 0) {
                return STATUS_COMPARATOR.compare(a, b);
            }
            return ownerComparison;
        }
    }

    private static class SeverityComparator implements Comparator<Issue> {
        
        public int compare(Issue a, Issue b) {
            final int severityComparison = a.severity - b.severity;
            
            if(severityComparison == 0) {
                return a.status - b.status;
            }
            return severityComparison;
        }
        
    }
    
}
