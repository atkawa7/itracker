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

package org.itracker.web.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.itracker.model.Issue;
import org.itracker.services.util.IssueUtilities;

public class ProjectTableModel extends ReportTableModel {
    
    private List<ProjectData> projects;
    
    public ProjectTableModel(List<Issue> issues, Locale locale) {
        super(issues, locale);
    }
    
    /**
     * Returns the current number of unique projects in the issue data.
     * @return the number of projects that have been added to the tablemodel
     */
    public int getRowCount() {
        return projects.size();
    }
    
    /**
     * Returns the value at a particular row and column.
     * @param row the row (zero-based index).
     * @param column the column (zero-based index).
     * @return the value.
     */
    public Object getValueAt(final int row, final int column) {
        if(row > getRowCount()) {
            return null;
        }
        
        ProjectData project = getProjectData(row);
        
        if (column == COLUMN_PROJECTID) {
            return project.getProjectId();
        } else if (column == COLUMN_PROJECTNAME) {
            return project.getProjectName();
        } else if (column == COLUMN_TOTALISSUES) {
            return new Integer(project.getTotalIssues());
        } else if (column == COLUMN_TOTALOPEN) {
            return new Integer(project.getTotalOpen());
        } else if (column == COLUMN_TOTALRESOLVED) {
            return new Integer(project.getTotalResolved());
        } else if (column == COLUMN_SEVERITYOPEN) {
            return project.getOpenSeverityAsInteger();
        } else if (column == COLUMN_SEVERITYRESOLVED) {
            return project.getResolvedSeverityAsInteger();
        } else {
            return null;
        }
    }
    
    public ProjectData getProjectData(final int row) {
        if(row > getRowCount()) {
            return null;
        }
        
        ProjectData project = (ProjectData) projects.get(row);
        return project;
    }
    
    public void addValueAt(Issue issue, int row) {
        if(issue != null) {
            if(row < issues.size() && row >= 0) {
                this.issues.add(row, issue);
            } else {
                this.issues.add(issue);
            }
            
            if(projects == null) {
                projects = new ArrayList<ProjectData>();
            }
            
            for(int i = 0; i < projects.size(); i++) {
                ProjectData project = (ProjectData) projects.get(i);
                if(project.getProjectId().intValue() == issue.getProject().getId().intValue()) {
                    project.addIssue(issue);
                    return;
                }
            }
            
            // No existing project was found
            ProjectData project = new ProjectData(issue.getProject().getId(), issue.getProject().getName());
            project.addIssue(issue);
            projects.add(project);
        }
    }
    
}

class ProjectData {
    private Integer projectId;
    private String projectName;
    private int totalIssues;
    private int totalResolved;
    private int totalOpen;
    
    private int[] openSeverity;
    private int[] resolvedSeverity;
    
    public ProjectData(Integer id, String name) {
        this.projectId = id;
        this.projectName = name;
        
        openSeverity = new int[IssueUtilities.getNumberSeverities()];
        resolvedSeverity = new int[IssueUtilities.getNumberSeverities()];
    }
    
    public Integer getProjectId() {
        return (projectId == null ? new Integer(-1) : projectId);
    }
    
    public String getProjectName() {
        return (projectName == null ? "" : projectName);
    }
    
    public int getTotalIssues() {
        return totalIssues;
    }
    
    public int getTotalOpen() {
        return totalOpen;
    }
    
    public int getTotalResolved() {
        return totalResolved;
    }
    
    public int[] getOpenSeverity() {
        return openSeverity;
    }
    
    public Integer[] getOpenSeverityAsInteger() {
        Integer[] temp = new Integer[openSeverity.length];
        for(int i = 0; i < openSeverity.length; i++) {
            temp[i] = new Integer(openSeverity[i]);
        }
        return temp;
    }
    
    public int[] getResolvedSeverity() {
        return resolvedSeverity;
    }
    
    public Integer[] getResolvedSeverityAsInteger() {
        Integer[] temp = new Integer[resolvedSeverity.length];
        for(int i = 0; i < resolvedSeverity.length; i++) {
            temp[i] = new Integer(resolvedSeverity[i]);
        }
        return temp;
    }
    
    public void addIssue(Issue issue) {
        if(issue != null) {
            totalIssues++;
            if(issue.getStatus() < IssueUtilities.STATUS_RESOLVED) {
                totalOpen++;
                if(issue.getSeverity() > 0 && issue.getSeverity() < openSeverity.length) {
                    openSeverity[issue.getSeverity() - 1]++;
                }
            } else {
                totalResolved++;
                if(issue.getSeverity() > 0 && issue.getSeverity() < resolvedSeverity.length) {
                    resolvedSeverity[issue.getSeverity() - 1]++;
                }
            }
        }
    }
}
