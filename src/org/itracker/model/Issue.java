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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.itracker.services.util.IssueUtilities;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class Issue extends AbstractBean implements Comparable<Issue> {

    private String description;
    private int severity;    
    private int status;
    private String resolution;
    private Project project;
    private User creator;
    private User owner;
    private Version targetVersion;
    private List<Component> components = new LinkedList<Component>();
    private List<Version> versions = new LinkedList<Version>();
    private List<Notification> notifications = new LinkedList<Notification>();
    private List<IssueActivity> activities = new LinkedList<IssueActivity>();
    private List<IssueAttachment> attachments = new LinkedList<IssueAttachment>();
    private List<IssueField> fields = new LinkedList<IssueField>();
    private List<IssueHistory> history = new LinkedList<IssueHistory>();
    private List<IssueRelation> relations = new LinkedList<IssueRelation>();

    private static final Comparator<Issue> issueComparator = new CompareByStatus();
    
    public Issue() {
    }
    
    public List getActivities() {
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
    public  List ejbSelectLastModifiedDates(Integer projectId) {
        return(null);
    }
    public  List ejbSelectIdByProjectAndStatusLessThan(Integer projectId, int status) {
        return(null);
    }
    public  List ejbSelectIdByProjectAndStatusGreaterThanEqualTo(Integer projectId, int status) {
        return(null);
    }

    public Date ejbHomeLatestModificationDate(Integer projectId) {
        Timestamp latestDate = null;

           Collection dates = ejbSelectLastModifiedDates(projectId);
            for(Iterator iterator = dates.iterator(); iterator.hasNext(); ) {
                Timestamp lastModDate = (Timestamp) iterator.next();
                if(latestDate == null) {
                    latestDate = lastModDate;
                }
                if(lastModDate.after(latestDate)) {
                    latestDate = lastModDate;
                }
            }
        return (Date) latestDate;
    }

    public Object[] ejbHomeGetIssueStats(Integer projectId) {
        Object[] issueStats = new Object[4];

        int totalIssues = 0;
            Collection openIds = ejbSelectIdByProjectAndStatusLessThan(projectId, IssueUtilities.STATUS_RESOLVED);
            issueStats[0] = (openIds == null ? "0" : Integer.toString(openIds.size()));
            totalIssues += openIds.size();
            Collection resolvedIds = ejbSelectIdByProjectAndStatusGreaterThanEqualTo(projectId, IssueUtilities.STATUS_RESOLVED);
            issueStats[1] = (resolvedIds == null ? "0" : Integer.toString(resolvedIds.size()));
            totalIssues += resolvedIds.size();

        issueStats[2] = Integer.toString(totalIssues);
        issueStats[3] = ejbHomeLatestModificationDate(projectId);

        return issueStats;
    }

    /**
     * Compares by status. 
     */
    public int compareTo(Issue other) {
        return this.issueComparator.compare(this, other);
    }
    
    /**
     * TODO: fix this!
     */
    public boolean equals(Object obj) {
        if (! (obj instanceof Issue)) {
            return false;
        }
        final Issue other = (Issue) obj;
        return this.id == other.id;
    }
    
    /**
     * TODO: fix this!
     */
    public int hashCode() {
        return (this.id == null) ? 0 : this.id.intValue();
    }
    
    public static abstract class IssueComparator implements Comparator<Issue> {
        protected boolean isAscending = true;

        public IssueComparator() {
        }

        public IssueComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(Issue ma, Issue mb);


        public final int compare(Issue a, Issue b) {
            int result = doComparison(a, b);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByStatus extends IssueComparator {
        
        public CompareByStatus(){
          super();
        }

        public CompareByStatus(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(Issue ma, Issue mb) {
            if(ma.getStatus() == mb.getStatus()) {
                if(ma.getSeverity() == mb.getSeverity()) {
                    return 0;
                } else if(ma.getSeverity() > mb.getSeverity()) {
                    return 1;
                } else if(ma.getSeverity() < mb.getSeverity()) {
                    return -1;
                }
            } else if(ma.getStatus() > mb.getStatus()) {
                return 1;
            } else if(ma.getStatus() < mb.getStatus()) {
                return -1;
            }

            return 0;
        }
    }

    public static class CompareByProjectAndStatus extends IssueComparator {
        public CompareByProjectAndStatus() {
            super();
        }

        public CompareByProjectAndStatus(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(Issue ma, Issue mb) {
            if(ma.getProject().getName().equals(mb.getProject().getName())) {
                if(ma.getStatus() == mb.getStatus()) {
                    if(ma.getSeverity() == mb.getSeverity()) {
                        return 0;
                    } else if(ma.getSeverity() > mb.getSeverity()) {
                        return 1;
                    } else if(ma.getSeverity() < mb.getSeverity()) {
                        return -1;
                    }
                } else if(ma.getStatus() > mb.getStatus()) {
                    return 1;
                } else if(mb.getStatus() < mb.getStatus()) {
                    return -1;
                }
            } else {
                return ma.getProject().getName().compareTo(mb.getProject().getName());
            }

            return 0;
        }
    }

    public static class CompareByOwnerAndStatus extends IssueComparator {
        public CompareByOwnerAndStatus() {
            super();
        }

        public CompareByOwnerAndStatus(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(Issue ma, Issue mb) {
            if(ma.getOwner().getLastName().equals(mb.getOwner().getLastName())) {
                if(ma.getOwner().getFirstName().equals(mb.getOwner().getFirstName())) {
                    if(ma.getStatus() == mb.getStatus()) {
                        if(ma.getSeverity() == mb.getSeverity()) {
                            return 0;
                        } else if(ma.getSeverity() > mb.getSeverity()) {
                            return 1;
                        } else if(ma.getSeverity() < mb.getSeverity()) {
                            return -1;
                        }
                    } else if(ma.getStatus() > mb.getStatus()) {
                        return 1;
                    } else if(ma.getStatus() < mb.getStatus()) {
                        return -1;
                    }
                } else {
                    return ma.getOwner().getFirstName().compareTo(mb.getOwner().getFirstName());
                }
            } else {
                return ma.getOwner().getLastName().compareTo(mb.getOwner().getLastName());
            }

            return 0;
        }
    }

    public static class CompareById extends IssueComparator {
        public CompareById() {
            super();
        }

        public CompareById(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(Issue ma, Issue mb) {
            if(ma.getId().intValue() > mb.getId().intValue()) {
                return 1;
            } else if(ma.getId().intValue() < mb.getId().intValue()) {
                return -1;
            }

            return 0;
        }
    }

    public static class CompareBySeverity extends IssueComparator {
        
        public CompareBySeverity() {
            super();
        }

        public CompareBySeverity(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(Issue ma, Issue mb) {
            if(ma.getSeverity() == mb.getSeverity()) {
                if(ma.getStatus() == mb.getStatus()) {
                    return 0;
                } else if(ma.getStatus() > mb.getStatus()) {
                    return 1;
                } else if(ma.getStatus() < mb.getStatus()) {
                    return -1;
                }
            } else if(ma.getSeverity() > mb.getSeverity()) {
                return 1;
            } else if(ma.getSeverity() < mb.getSeverity()) {
                return -1;
            }

            return 0;
        }
    }
    
}
