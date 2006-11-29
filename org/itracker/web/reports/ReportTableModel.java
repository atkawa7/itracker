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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;

import org.itracker.services.util.IssueUtilities;

public abstract class ReportTableModel extends AbstractTableModel {
    
    // This number MUST be set to the total number of defined columns.
    public static final int TOTAL_STATIC_COLUMNS = 28;
    
    public static final int COLUMN_ISSUEID = 0;
    public static final int COLUMN_PROJECTID = 1;
    public static final int COLUMN_DESCRIPTION = 2;
    public static final int COLUMN_SEVERITY = 3;
    public static final int COLUMN_STATUS = 4;
    public static final int COLUMN_PROJECTNAME = 5;
    public static final int COLUMN_TOTALISSUES = 6;
    public static final int COLUMN_TOTALOPEN = 7;
    public static final int COLUMN_TOTALRESOLVED = 8;
    public static final int COLUMN_CREATEDATE = 9;
    public static final int COLUMN_LASTMODDATE = 10;
    public static final int COLUMN_OWNERNAME = 11;
    public static final int COLUMN_OWNEREMAIL = 12;
    public static final int COLUMN_CREATORNAME = 13;
    public static final int COLUMN_CREATOREMAIL = 14;
    public static final int COLUMN_TARGETVERSION = 15;
    public static final int COLUMN_RESOLUTION = 16;
    public static final int COLUMN_COMPONENTS = 17;
    public static final int COLUMN_COMPONENTSSTRING = 18;
    public static final int COLUMN_VERSIONS = 19;
    public static final int COLUMN_VERSIONSSTRING = 20;
    public static final int COLUMN_HISTORY = 21;
    public static final int COLUMN_HISTORYSTRING = 22;
    public static final int COLUMN_LASTHISTORY = 23;
    public static final int COLUMN_LASTHISTORYDATE = 24;
    public static final int COLUMN_LASTHISTORYUSER = 25;
    public static final int COLUMN_SEVERITYOPEN = 26;
    public static final int COLUMN_SEVERITYRESOLVED = 27;
    
    protected List<Issue> issues;
    protected List<CustomField> customFields = new ArrayList<CustomField>();
    protected Locale reportLocale;
    protected SimpleDateFormat sdf;
    
    public ReportTableModel() {
        this.issues = new ArrayList<Issue>();
        this.reportLocale = ITrackerResources.getLocale();
        this.sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.full"));
        this.customFields = IssueUtilities.getCustomFields(ITrackerResources.getLocale());
    }
    
    public ReportTableModel(List<Issue> issues, Locale locale) {
        this();
        if(issues != null) {
            for(int i = 0; i < issues.size(); i++) {
                if(issues.get(i) != null) {
                    addValue(issues.get(i));
                }
            }
        }
        if(locale != null) {
            this.reportLocale = locale;
            this.sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.full", locale));
            this.customFields = IssueUtilities.getCustomFields(locale);
        }
    }
    
    /**
     * Returns the number of columns that have been defined.
     * @return the number of columns available to reports
     */
    public int getColumnCount() {
        return TOTAL_STATIC_COLUMNS + customFields.size();
    }
    
    /**
     * Returns the name of the column.
     * @param column the column (zero-based index).
     * @return the column name.
     */
    public String getColumnName(final int column) {
        if(column >= TOTAL_STATIC_COLUMNS && column < (TOTAL_STATIC_COLUMNS + customFields.size())) {
            if(customFields.get(column - TOTAL_STATIC_COLUMNS) != null) {
                return "customfield" + customFields.get(column - TOTAL_STATIC_COLUMNS).getId();
            }
        }
        
        switch(column) {
            case COLUMN_ISSUEID:
                return "issueid";
            case COLUMN_PROJECTID:
                return "projectid";
            case COLUMN_DESCRIPTION:
                return "description";
            case COLUMN_STATUS:
                return "status";
            case COLUMN_SEVERITY:
                return "severity";
            case COLUMN_PROJECTNAME:
                return "projectname";
            case COLUMN_TOTALISSUES:
                return "totalissues";
            case COLUMN_TOTALOPEN:
                return "totalopen";
            case COLUMN_TOTALRESOLVED:
                return "totalresolved";
            case COLUMN_CREATEDATE:
                return "createdate";
            case COLUMN_LASTMODDATE:
                return "lastmoddate";
            case COLUMN_OWNERNAME:
                return "ownername";
            case COLUMN_OWNEREMAIL:
                return "owneremail";
            case COLUMN_CREATORNAME:
                return "creatorname";
            case COLUMN_CREATOREMAIL:
                return "creatoremail";
            case COLUMN_TARGETVERSION:
                return "targetversion";
            case COLUMN_RESOLUTION:
                return "resolution";
            case COLUMN_COMPONENTS:
                return "components";
            case COLUMN_COMPONENTSSTRING:
                return "componentsstring";
            case COLUMN_VERSIONS:
                return "versions";
            case COLUMN_VERSIONSSTRING:
                return "versionsstring";
            case COLUMN_HISTORY:
                return "history";
            case COLUMN_HISTORYSTRING:
                return "historystring";
            case COLUMN_LASTHISTORY:
                return "lasthistory";
            case COLUMN_LASTHISTORYDATE:
                return "lasthistorydate";
            case COLUMN_LASTHISTORYUSER:
                return "lasthistoryuser";
            case COLUMN_SEVERITYOPEN:
                return "severityopen";
            case COLUMN_SEVERITYRESOLVED:
                return "severityresolved";
            default:
                throw new IllegalArgumentException("ReportTableModel: Invalid column index.");
        }
    }
    
    /**
     * Returns the number of the column.
     * @param columnName the column name
     * @return the column number
     */
    public int findColumn(String columnName) {
        if("issueid".equalsIgnoreCase(columnName)) {
            return COLUMN_ISSUEID;
        } else if("projectid".equalsIgnoreCase(columnName)) {
            return COLUMN_PROJECTID;
        } else if("description".equalsIgnoreCase(columnName)) {
            return COLUMN_DESCRIPTION;
        } else if("status".equalsIgnoreCase(columnName)) {
            return COLUMN_STATUS;
        } else if("severity".equalsIgnoreCase(columnName)) {
            return COLUMN_SEVERITY;
        } else if("projectname".equalsIgnoreCase(columnName)) {
            return COLUMN_PROJECTNAME;
        } else if("totalissues".equalsIgnoreCase(columnName)) {
            return COLUMN_TOTALISSUES;
        } else if("totalopen".equalsIgnoreCase(columnName)) {
            return COLUMN_TOTALOPEN;
        } else if("totalresolved".equalsIgnoreCase(columnName)) {
            return COLUMN_TOTALRESOLVED;
        } else if("createdate".equalsIgnoreCase(columnName)) {
            return COLUMN_CREATEDATE;
        } else if("lastmoddate".equalsIgnoreCase(columnName)) {
            return COLUMN_LASTMODDATE;
        } else if("ownername".equalsIgnoreCase(columnName)) {
            return COLUMN_OWNERNAME;
        } else if("owneremail".equalsIgnoreCase(columnName)) {
            return COLUMN_OWNEREMAIL;
        } else if("creatorname".equalsIgnoreCase(columnName)) {
            return COLUMN_CREATORNAME;
        } else if("creatoremail".equalsIgnoreCase(columnName)) {
            return COLUMN_CREATOREMAIL;
        } else if("targetversion".equalsIgnoreCase(columnName)) {
            return COLUMN_TARGETVERSION;
        } else if("resolution".equalsIgnoreCase(columnName)) {
            return COLUMN_RESOLUTION;
        } else if("components".equalsIgnoreCase(columnName)) {
            return COLUMN_COMPONENTS;
        } else if("componentsstring".equalsIgnoreCase(columnName)) {
            return COLUMN_COMPONENTSSTRING;
        } else if("versions".equalsIgnoreCase(columnName)) {
            return COLUMN_VERSIONS;
        } else if("versionsstring".equalsIgnoreCase(columnName)) {
            return COLUMN_VERSIONSSTRING;
        } else if("history".equalsIgnoreCase(columnName)) {
            return COLUMN_HISTORY;
        } else if("historystring".equalsIgnoreCase(columnName)) {
            return COLUMN_HISTORYSTRING;
        } else if("lasthistory".equalsIgnoreCase(columnName)) {
            return COLUMN_LASTHISTORY;
        } else if("lasthistorydate".equalsIgnoreCase(columnName)) {
            return COLUMN_LASTHISTORYDATE;
        } else if("lasthistoryuser".equalsIgnoreCase(columnName)) {
            return COLUMN_LASTHISTORYUSER;
        } else if("severityopen".equalsIgnoreCase(columnName)) {
            return COLUMN_SEVERITYOPEN;
        } else if("severityresolved".equalsIgnoreCase(columnName)) {
            return COLUMN_SEVERITYRESOLVED;
        } else if(columnName != null && columnName.startsWith("customfield")) {
            Integer id = new Integer(columnName.substring(11));
            for(int i = 0; i < customFields.size(); i++) {
                if(customFields.get(i) != null && id.equals(customFields.get(i).getId())) {
                    return TOTAL_STATIC_COLUMNS + i;
                }
            }
        } else {
            return -1;
        }
        
        return -1;
    }
    
    public void addValue(Issue issue) {
        addValueAt(issue, -1);
    }
    
    public void addValueAt(Issue issue, int row) {
        if(issue != null) {
            if(row < issues.size() && row >= 0) {
                this.issues.add(row,issue);
            } else {
                this.issues.add(issue);
            }
        }
    }
    
}
