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

import java.util.List;
import java.util.Locale;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueHistory;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;



public class IssueTableModel extends ReportTableModel {

    public IssueTableModel(List<Issue> issues, Locale locale) {
        super(issues, locale);
    }

    /**
      * Returns the current number of rows of data (issues) in the
      * table model.
      * @return the number of issues that have been added to the tablemodel
      */
    public int getRowCount() {
        return issues.size();
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

        Issue issue = (Issue) issues.get(row);
        if(issue == null) {
            return null;
        }

        if (column == COLUMN_ISSUEID) {
            return issue.getId();
        } else if (column == COLUMN_PROJECTID) {
            return issue.getProject().getId();
        } else if (column == COLUMN_PROJECTNAME) {
            return issue.getProject().getName();
        } else if (column == COLUMN_DESCRIPTION) {
            return issue.getDescription();
        } else if (column == COLUMN_STATUS) {
            return IssueUtilities.getStatusName(issue.getStatus(), reportLocale);
        } else if (column == COLUMN_SEVERITY) {
            return IssueUtilities.getSeverityName(issue.getSeverity(), reportLocale);
        } else if (column == COLUMN_CREATEDATE) {
            return (sdf == null ? issue.getCreateDate().toString() : sdf.format(issue.getCreateDate()));
        } else if (column == COLUMN_LASTMODDATE) {
            return (sdf == null ? issue.getLastModifiedDate().toString() : sdf.format(issue.getLastModifiedDate()));
        } else if (column == COLUMN_OWNERNAME) {
            return (issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", reportLocale) : issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName());
        } else if (column == COLUMN_OWNEREMAIL) {
            return (issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", reportLocale) : issue.getOwner().getEmail());
        } else if (column == COLUMN_CREATORNAME) {
            return (issue.getCreator() == null ? ITrackerResources.getString("itracker.web.generic.unknown", reportLocale) : issue.getCreator().getFirstName() + " " + issue.getCreator().getLastName());
        } else if (column == COLUMN_CREATOREMAIL) {
            return (issue.getCreator() == null ? ITrackerResources.getString("itracker.web.generic.unknown", reportLocale) : issue.getCreator().getEmail());
        } else if (column == COLUMN_TARGETVERSION) {
            return (issue.getTargetVersion() == null ? "" : issue.getTargetVersion().getNumber());
        } else if (column == COLUMN_RESOLUTION) {
            if(issue.getProject() != null && ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, issue.getProject().getOptions())) {
                return IssueUtilities.getResolutionName(issue.getResolution(), reportLocale);
            } else {
                return issue.getResolution();
            }
        } else if (column == COLUMN_COMPONENTS) {
            return issue.getComponents();
        } else if (column == COLUMN_COMPONENTSSTRING) {
            return IssueUtilities.componentsToString(issue);
        } else if (column == COLUMN_VERSIONS) {
            return issue.getVersions();
        } else if (column == COLUMN_VERSIONSSTRING) {
            return IssueUtilities.versionsToString(issue);
        } else if (column == COLUMN_HISTORY) {
            return issue.getHistory();
        } else if (column == COLUMN_HISTORYSTRING) {
            return IssueUtilities.historyToString(issue, sdf);
        } else if (column == COLUMN_LASTHISTORY) {
            if(issue.getHistory().size() > 0) {
                IssueHistory history = issue.getHistory().get(issue.getHistory().size() - 1);
                return history.getDescription();
            } else {
                return "";
            }
        } else if (column == COLUMN_LASTHISTORYDATE) {
            if(issue.getHistory().size() > 0) {
                IssueHistory history = issue.getHistory().get(issue.getHistory().size() - 1);
                return sdf.format(history.getLastModifiedDate());
            } else {
                return "";
            }
        } else if (column == COLUMN_LASTHISTORYUSER) {
            if(issue.getHistory().size() > 0) {
                IssueHistory history = issue.getHistory().get(issue.getHistory().size() - 1);
                return history.getUser().getFirstName() + " " + history.getUser().getLastName();
            } else {
                return "";
            }
        } else if (column >= TOTAL_STATIC_COLUMNS && column < (TOTAL_STATIC_COLUMNS + customFields.size())) {
            CustomField customField = customFields.get(column - TOTAL_STATIC_COLUMNS);
            if (customField != null) {
                for(int i = 0; i < issue.getFields().size(); i++) {
                    if(issue.getFields().get(i).getCustomField().getId().equals(customField.getId())) {
                        String value = issue.getFields().get(i).getValue(reportLocale);
                        if(customField.getFieldType() == CustomField.Type.LIST) {
                            for(int j = 0; j < customField.getOptions().size(); j++) {
                                if(customField.getOptions().get(j).getValue().equals(value)) {
                                    value = customField.getOptions().get(j).getCustomField().getName();
                                    break;
                                }
                            }
                        }
                        return value;
                    }
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
