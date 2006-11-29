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

package org.itracker.services.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.Issue;
import org.itracker.model.IssueRelation;
import org.itracker.model.NameValuePair;
import org.itracker.model.Notification;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;


/**
  * Contains utilities used when displaying and processing issues.
  */
public class IssueUtilities  {
    
    public static final int FIELD_TYPE_SINGLE = 1;
    public static final int FIELD_TYPE_INDEXED = 2;
    public static final int FIELD_TYPE_MAP = 3;

    public static final int FIELD_ID = -1;
    public static final int FIELD_DESCRIPTION = -2;
    public static final int FIELD_STATUS = -3;
    public static final int FIELD_RESOLUTION = -4;
    public static final int FIELD_SEVERITY = -5;
    public static final int FIELD_CREATOR = -6;
    public static final int FIELD_CREATEDATE = -7;
    public static final int FIELD_OWNER = -8;
    public static final int FIELD_LASTMODIFIED = -9;
    public static final int FIELD_PROJECT = -10;
    public static final int FIELD_TARGET_VERSION = -11;
    public static final int FIELD_COMPONENTS = -12;
    public static final int FIELD_VERSIONS = -13;
    public static final int FIELD_ATTACHMENTDESCRIPTION = -14;
    public static final int FIELD_ATTACHMENTFILENAME = -15;
    public static final int FIELD_HISTORY = -16;

    public static final int[] STANDARD_FIELDS = {
          FIELD_ID, FIELD_DESCRIPTION, FIELD_STATUS, FIELD_RESOLUTION, FIELD_SEVERITY,
          FIELD_CREATOR, FIELD_CREATEDATE, FIELD_OWNER, FIELD_LASTMODIFIED, FIELD_PROJECT,
          FIELD_TARGET_VERSION, FIELD_COMPONENTS, FIELD_VERSIONS, FIELD_ATTACHMENTDESCRIPTION,
          FIELD_ATTACHMENTFILENAME, FIELD_HISTORY };


    public static final int STATUS_NEW = 100;
    public static final int STATUS_UNASSIGNED = 200;
    public static final int STATUS_ASSIGNED = 300;
    public static final int STATUS_RESOLVED = 400;
    public static final int STATUS_CLOSED = 500;

    // This marks the end of all status numbers.  You can NOT add a status above this number or
    // they will not be found.
    public static final int STATUS_END = 600;

    public static final int ACTIVITY_ISSUE_CREATED = 1;
    public static final int ACTIVITY_STATUS_CHANGE = 2;
    public static final int ACTIVITY_OWNER_CHANGE = 3;
    public static final int ACTIVITY_SEVERITY_CHANGE = 4;
    public static final int ACTIVITY_COMPONENTS_MODIFIED = 5;
    public static final int ACTIVITY_VERSIONS_MODIFIED = 6;
    public static final int ACTIVITY_REMOVE_HISTORY = 7;
    public static final int ACTIVITY_ISSUE_MOVE = 8;
    public static final int ACTIVITY_SYSTEM_UPDATE = 9;
    public static final int ACTIVITY_TARGETVERSION_CHANGE = 10;
    public static final int ACTIVITY_DESCRIPTION_CHANGE = 11;
    public static final int ACTIVITY_RESOLUTION_CHANGE = 12;
    public static final int ACTIVITY_RELATION_ADDED = 13;
    public static final int ACTIVITY_RELATION_REMOVED = 14;

    public static final int HISTORY_STATUS_REMOVED = -1;
    public static final int HISTORY_STATUS_AVAILABLE = 1;

    /** Defines a related issue.  Sample text: related to */
    public static final int RELATION_TYPE_RELATED_P = 1;
    /** Defines a related issue.  Sample text: related to */
    public static final int RELATION_TYPE_RELATED_C = 2;
    /** Defines a duplicate issue.  Sample text: duplicates */
    public static final int RELATION_TYPE_DUPLICATE_P = 3;
    /** Defines a duplicate issue.  Sample text: duplicate of */
    public static final int RELATION_TYPE_DUPLICATE_C = 4;
    /** Defines a cloned issue.  Sample text: cloned to */
    public static final int RELATION_TYPE_CLONED_P = 5;
    /** Defines a cloned issue.  Sample text: cloned from */
    public static final int RELATION_TYPE_CLONED_C = 6;
    /** Defines a split issue.  Sample text: split to */
    public static final int RELATION_TYPE_SPLIT_P = 7;
    /** Defines a split issue.  Sample text: split from */
    public static final int RELATION_TYPE_SPLIT_C = 8;
    /** Defines a dependent issue.  Sample text: dependents */
    public static final int RELATION_TYPE_DEPENDENT_P = 9;
    /** Defines a dependent issue.  Sample text: depends on */
    public static final int RELATION_TYPE_DEPENDENT_C = 10;

    public static final int NUM_RELATION_TYPES = 10;

    private static List<Configuration> resolutions = new ArrayList<Configuration>();
    private static List<Configuration> severities = new ArrayList<Configuration>();
    private static List<Configuration> statuses = new ArrayList<Configuration>();
    private static List<CustomField> customFields = new ArrayList<CustomField>();
    private static final Logger logger = Logger.getLogger(IssueUtilities.class);
    
    public IssueUtilities() {
    }

    public static int getFieldType(Integer fieldId) {
        if(fieldId != null) {
            if(fieldId.intValue() > 0) {
                return FIELD_TYPE_MAP;
            }
/*
            switch(fieldId.intValue()) {
                case FIELD_COMPONENTS:
                    return FIELD_TYPE_INDEXED;
                case FIELD_VERSIONS:
                    return FIELD_TYPE_INDEXED;
            }
*/
        }

        return FIELD_TYPE_SINGLE;
    }

    public static String getFieldName(Integer fieldId) {
        if(fieldId == null) {
            return "";
        }

        if(fieldId.intValue() > 0) {
            return "customFields";
        }

        switch(fieldId.intValue()) {
            case FIELD_ID:
                return "id";
            case FIELD_DESCRIPTION:
                return "description";
            case FIELD_STATUS:
                return "status";
            case FIELD_RESOLUTION:
                return "resolution";
            case FIELD_SEVERITY:
                return "severity";
            case FIELD_CREATOR:
                return "creatorId";
            case FIELD_CREATEDATE:
                return "createdate";
            case FIELD_OWNER:
                return "ownerId";
            case FIELD_LASTMODIFIED:
                return "lastmodified";
            case FIELD_PROJECT:
                return "projectId";
            case FIELD_TARGET_VERSION:
                return "targetVersion";
            case FIELD_COMPONENTS:
                return "components";
            case FIELD_VERSIONS:
                return "versions";
            case FIELD_ATTACHMENTDESCRIPTION:
                return "attachmentDescription";
            case FIELD_ATTACHMENTFILENAME:
                return "attachment";
            case FIELD_HISTORY:
                return "history";
            default:
                return "";
        }
    }

    public static String getFieldName(Integer fieldId, List<CustomField> customFields, Locale locale) {
        if(fieldId.intValue() < 0) {
            return ITrackerResources.getString(getStandardFieldKey(fieldId.intValue()), locale);
        } else {
            for(int i = 0; i < customFields.size(); i++) {
                if(fieldId.equals(customFields.get(i).getId())) {
                    return CustomFieldUtilities.getCustomFieldName(fieldId, locale);
                }
            }
        }

        return ITrackerResources.getString("itracker.web.generic.unknown", locale);
    }

    public static String getStandardFieldKey(int fieldId) {
        switch(fieldId) {
            case FIELD_ID:
                return "itracker.web.attr.id";
            case FIELD_DESCRIPTION:
                return "itracker.web.attr.description";
            case FIELD_STATUS:
                return "itracker.web.attr.status";
            case FIELD_RESOLUTION:
                return "itracker.web.attr.resolution";
            case FIELD_SEVERITY:
                return "itracker.web.attr.severity";
            case FIELD_CREATOR:
                return "itracker.web.attr.creator";
            case FIELD_CREATEDATE:
                return "itracker.web.attr.createdate";
            case FIELD_OWNER:
                return "itracker.web.attr.owner";
            case FIELD_LASTMODIFIED:
                return "itracker.web.attr.lastmodified";
            case FIELD_PROJECT:
                return "itracker.web.attr.project";
            case FIELD_TARGET_VERSION:
                return "itracker.web.attr.target";
            case FIELD_COMPONENTS:
                return "itracker.web.attr.components";
            case FIELD_VERSIONS:
                return "itracker.web.attr.versions";
            case FIELD_ATTACHMENTDESCRIPTION:
                return "itracker.web.attr.attachmentdescription";
            case FIELD_ATTACHMENTFILENAME:
                return "itracker.web.attr.attachmentfilename";
            case FIELD_HISTORY:
                return "itracker.web.attr.detaileddescription";
            default:
                return "itracker.web.generic.unknown";
        }
    }

    public static NameValuePair[] getStandardFields(Locale locale) {
        NameValuePair[] fieldNames = new NameValuePair[STANDARD_FIELDS.length];
        for(int i = 0; i < STANDARD_FIELDS.length; i++) {
            fieldNames[i] = new NameValuePair(ITrackerResources.getString(getStandardFieldKey(STANDARD_FIELDS[i]), locale), Integer.toString(STANDARD_FIELDS[i]));
        }
        return fieldNames;
    }

    public static String getRelationName(int value) {
        return getRelationName(value, ITrackerResources.getLocale());
    }

    public static String getRelationName(int value, Locale locale) {
        return getRelationName(Integer.toString(value), locale);
    }

    public static String getRelationName(String value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_ISSUE_RELATION + value, locale);
    }

    public static int getMatchingRelationType(int relationType) {
        switch(relationType) {
            case RELATION_TYPE_RELATED_P:
                return RELATION_TYPE_RELATED_C;
            case RELATION_TYPE_RELATED_C:
                return RELATION_TYPE_RELATED_P;
            case RELATION_TYPE_DUPLICATE_P:
                return RELATION_TYPE_DUPLICATE_C;
            case RELATION_TYPE_DUPLICATE_C:
                return RELATION_TYPE_DUPLICATE_P;
            case RELATION_TYPE_CLONED_P:
                return RELATION_TYPE_CLONED_C;
            case RELATION_TYPE_CLONED_C:
                return RELATION_TYPE_CLONED_P;
            case RELATION_TYPE_SPLIT_P:
                return RELATION_TYPE_SPLIT_C;
            case RELATION_TYPE_SPLIT_C:
                return RELATION_TYPE_SPLIT_P;
            case RELATION_TYPE_DEPENDENT_P:
                return RELATION_TYPE_DEPENDENT_C;
            case RELATION_TYPE_DEPENDENT_C:
                return RELATION_TYPE_DEPENDENT_P;
            default:
                return -1;
        }
    }

    public static String componentsToString(Issue issue) {
        String value = "";
        if(issue != null && issue.getComponents().size() > 0) {
            for(int i = 0; i < issue.getComponents().size(); i++) {
                value += (i != 0 ? ", " : "") + issue.getComponents().get(i).getName();
            }
        }
        return value;
    }

    public static String versionsToString(Issue issue) {
        String value = "";
        if(issue != null && issue.getVersions().size() > 0) {
            for(int i = 0; i < issue.getVersions().size(); i++) {
                value += (i != 0 ? ", " : "") + issue.getVersions().get(i).getNumber();
            }
        }
        return value;
    }

    public static String historyToString(Issue issue, SimpleDateFormat sdf) {
        String value = "";
        if(issue != null && issue.getHistory().size() > 0 && sdf != null) {
            for(int i = 0; i < issue.getHistory().size(); i++) {
                value += (i != 0 ? "," : "") + issue.getHistory().get(i).getDescription() + "," + issue.getHistory().get(i).getUser().getFirstName();
                value += " " + issue.getHistory().get(i).getUser().getLastName() + "," + sdf.format(issue.getHistory().get(i).getLastModifiedDate());
            }
        }
        return value;
    }

    public static String getStatusName(int value) {
        return getStatusName(value, ITrackerResources.getLocale());
    }

    public static String getStatusName(int value, Locale locale) {
        return getStatusName(Integer.toString(value), locale);
    }

    public static String getStatusName(String value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_STATUS + value, locale);
    }

    /**
     * getStatuses() needs to get implemented..
     * 
     */
    public static List<Configuration> getStatuses() {
    	return statuses;
    }

    public static List<NameValuePair> getStatuses(Locale locale) {
        NameValuePair[] statusStrings = new NameValuePair[statuses.size()];
        for(int i = 0; i < statuses.size(); i++) {
            statusStrings[i] = new NameValuePair(ITrackerResources.getString(ITrackerResources.KEY_BASE_STATUS + statuses.get(i).getValue(), locale), statuses.get(i).getValue());
        }
        return Arrays.asList(statusStrings);
    }

    public static void setStatuses(List<Configuration> value) {
        statuses = (value == null ? new ArrayList<Configuration>() : value);
    }

    public static int getNumberStatuses() {
        return statuses.size();
    }

    public static String getSeverityName(int value) {
        return getSeverityName(value, ITrackerResources.getLocale());
    }

    public static String getSeverityName(int value, Locale locale) {
        return getSeverityName(Integer.toString(value), locale);
    }

    public static String getSeverityName(String value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_SEVERITY + value, locale);
    }

    /**
      * Returns the list of the defined issue severities in the system.  The array returned
      * is a cached list set from the setSeverities method.  The actual values are stored
      * in the database and and can be obtained from the ConfigurationService bean.
      * @param locale the locale to return the severities as
      * @returns array of translated strings from the cached severities list
      */
    public static List<NameValuePair> getSeverities(Locale locale) {
        List<NameValuePair> severityStrings = new ArrayList<NameValuePair>();
        
        for(int i = 0; i < severities.size(); i++) {
        	String string1 = ITrackerResources.getString(ITrackerResources.KEY_BASE_SEVERITY + severities.get(i).getValue(), locale);
        	String string2 = severities.get(i).getValue();
        	NameValuePair nvp = new NameValuePair(string1,string2);
            severityStrings.add(i,nvp);
        }
        return severityStrings;
    }

    public static void setSeverities(List<Configuration> value) {
        severities = (value == null ? new ArrayList<Configuration>() : value);
    }

    public static int getNumberSeverities() {
        return severities.size();
    }

    /**
      * Compares the severity of two issues.  The int returned will be negative if the
      * the severity of issue A is less than the severity of issue B, positive if issue
      * A is a higher severity than issue B, or 0 if the two issues have the same severity.
      * @param ma IssueModel A
      * @param mb IssueModel B
      * @returns an int representing the compared severities
      */
    public static int compareSeverity(Issue ma, Issue mb) {
        if(ma == null && mb == null) {
            return 0;
        } else if(ma == null && mb != null) {
            return -1;
        } else if(ma != null && mb == null) {
            return 1;
        } else {
            int maIndex = Integer.MAX_VALUE;
            int mbIndex = Integer.MAX_VALUE;
            for(int i = 0; i < severities.size(); i++) {
                if(severities.get(i) != null) {
                    if(severities.get(i).getValue().equalsIgnoreCase(Integer.toString(ma.getSeverity()))) {
                        maIndex = i;
                    }
                    if(severities.get(i).getValue().equalsIgnoreCase(Integer.toString(mb.getSeverity()))) {
                        mbIndex = i;
                    }
                }
            }
            if(maIndex > mbIndex) {
                return -1;
            } else if(maIndex < mbIndex) {
                return 1;
            }
        }

        return 0;
    }

    public static String getResolutionName(int value) {
        return getResolutionName(value, ITrackerResources.getLocale());
    }

    public static String getResolutionName(int value, Locale locale) {
        return getResolutionName(Integer.toString(value), locale);
    }

    public static String getResolutionName(String value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_RESOLUTION + value, locale);
    }

    public static String checkResolutionName(String value, Locale locale) throws MissingResourceException {
        return ITrackerResources.getCheckForKey(ITrackerResources.KEY_BASE_RESOLUTION + value, locale);
    }

    /**
      * Returns the list of predefined resolutions in the system.  The array returned
      * is a cached list set from the setResolutions method.  The actual values are stored
      * in the database and and can be obtained from the ConfigurationService bean.
      * @param locale the locale to return the resolutions as
      * @returns array of translated strings from the cached resolution list
      */
    public static List<NameValuePair> getResolutions(Locale locale) {
        NameValuePair[] resolutionStrings = new NameValuePair[resolutions.size()];
        for(int i = 0; i < resolutions.size(); i++) {
            resolutionStrings[i] = new NameValuePair(ITrackerResources.getString(ITrackerResources.KEY_BASE_RESOLUTION + resolutions.get(i).getValue(), locale), resolutions.get(i).getValue());
        }
        return Arrays.asList(resolutionStrings);
    }

    /**
      * Sets the cached list of predefined resolutions.
      */
    public static void setResolutions(List<Configuration> value) {
        resolutions = (value == null ? new ArrayList<Configuration>() : value);
    }

    public static String getActivityName(int value) {
        return getActivityName(value, ITrackerResources.getLocale());
    }

    public static String getActivityName(int value, Locale locale) {
        return ITrackerResources.getString("itracker.activity." + value, locale);
    }

    /**
      * Returns the cached array of CustomFieldModels.
      * @return an array of CustomFieldModels
      */
    public static List<CustomField> getCustomFields() {
        return (customFields == null ? new ArrayList<CustomField>() : customFields);
    }

    /**
      * Sets the cached array of CustomFieldModels.
      * @return an array of CustomFieldModels
      */
    public static void setCustomFields(List<CustomField> value) {
        customFields = (value == null ? new ArrayList<CustomField>() : value);
    }

    /**
      * Returns an array of the cached custom fields.  The fields labels will be initialized
      * based on the Locale given.  If no locale is given, the default locale of the
      * system will be used.
      * @param locale the locale to use to populate the field labels
      * @return the cached array of CustomFieldModels
      */
    public static List<CustomField> getCustomFields(Locale locale) {
        CustomField[] localizedFields = new CustomField[customFields.size()];
        for(int i = 0; i < customFields.size(); i++) {
            try {
                localizedFields[i] = (CustomField) customFields.get(i).clone();
                if(localizedFields[i] != null) {
                    localizedFields[i].setLabels(locale);
                }
            } catch(CloneNotSupportedException cnse) {
                logger.error("Error cloning CustomField: " + cnse.getMessage());
            }
        }
        return Arrays.asList(localizedFields);
    }

    /**
      * Returns the custom field with the supplied id.  Any labels
      * will be localized to the system default locale.
      * @param bitValue the id of the field to return
      * @return the requested CustomField object, or a new field if not found
      */
    public static CustomField getCustomField(Integer id) {
        return getCustomField(id, ITrackerResources.getLocale());
    }

    /**
      * Returns the custom field with the supplied id value.  Any labels will
      * be translated to the given locale.
      * @param id the id of the field to return
      * @param locale the locale to initialize any labels with
      * @return the requested CustomField object, or a new field if not found
      */
    public static CustomField getCustomField(Integer id, Locale locale) {
        CustomField retField = null;

        try {
            for(int i = 0; i < customFields.size(); i++) {
                if(customFields.get(i) != null && customFields.get(i).getId() != null && customFields.get(i).getId().equals(id)) {
                    retField = (CustomField) customFields.get(i).clone();
                    break;
                }
            }
        } catch(CloneNotSupportedException cnse) {
            logger.error("Error cloning CustomField: " + cnse.getMessage());
        }
        if(retField != null) {
            retField.setLabels(locale);
        } else {
            retField = new CustomField();
        }

        return retField;
    }

    /**
      * Returns the total number of defined custom fields
      */
    public static int getNumberCustomFields() {
        return customFields.size();
    }

    /**
      * Returns true if the user has permission to view the requested issue.
      * @param issue an IssueModel of the issue to check view permission for
      * @param user a User for the user to check permission for
      * @param permissions a HashMap of the users permissions
      */
    public static boolean canViewIssue(Issue issue, User user, Map<Integer, Set<PermissionType>> permissions) {
        if(user == null) {
            return false;
        }
        return canViewIssue(issue, user.getId(), permissions);
    }

    /**
      * Returns true if the user has permission to view the requested issue.
      * @param issue an IssueModel of the issue to check view permission for
      * @param userId the userId of the user to check permission for
      * @param permissions a HashMap of the users permissions
      */
    public static boolean canViewIssue(Issue issue, Integer userId, Map<Integer, Set<PermissionType>> permissions) {
        if (issue == null || userId == null || permissions == null) {
            return false;
        }

        if (UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_VIEW_ALL.toInt())) {
            return true;
        }
        
        if (! UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_VIEW_USERS.toInt())) {
            return false;
        }

        if (issue.getCreator().getId().equals(userId)) {
            return true;
        }

        if (issue.getOwner().getId().equals(userId)) {
            return true;
        }
        return false;
    }

    /**
      * Returns true if the user has permission to edit the requested issue.
      * @param issue an IssueModel of the issue to check edit permission for
      * @param userId the userId of the user to check permission for
      * @param permissions a HashMap of the users permissions
      */
    public static boolean canEditIssue(Issue issue, Integer userId, Map<Integer, Set<PermissionType>> permissions) {
        if (issue == null || userId == null || permissions == null) {
            return false;
        }

        if (UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_EDIT_ALL.toInt())) {
            return true;
        }
        if (! UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_EDIT_USERS.toInt())) {
            return false;
        }

        if (issue.getCreator().getId().equals(userId)) {
            return true;
        }

        if (issue.getOwner().getId().equals(userId)) {
            return true;
        }
        return false;
    }

    /**
      * Returns true if the user can be assigned to this issue.
      * @param issue an IssueModel of the issue to check assign permission for
      * @param userId the userId of the user to check permission for
      * @param permissions a HashMap of the users permissions
      */
    public static boolean canBeAssignedIssue(Issue issue, Integer userId, Map<Integer, Set<PermissionType>> permissions) {
        if(issue == null || userId == null || permissions == null) {
            return false;
        }

        if(UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_EDIT_ALL.toInt())) {
            return true;
        }
        if(UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_EDIT_USERS.toInt())) {
            if(issue.getCreator().getId().equals(userId)) {
                return true;
            } else if(UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_ASSIGNABLE.toInt())) {
                return true;
            } else if(issue.getOwner().getId() != null && issue.getOwner().getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    /**
      * Returns true if the user can unassign themselves from the issue.
      * @param issue an IssueModel of the issue to check assign permission for
      * @param userId the userId of the user to check permission for
      * @param permissions a HashMap of the users permissions
      */
    public static boolean canUnassignIssue(Issue issue, Integer userId, Map<Integer, Set<PermissionType>> permissions) {
        if(issue == null || userId == null || permissions == null) {
            return false;
        }

        if(UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_ASSIGN_OTHERS.toInt())) {
            return true;
        }
        if(issue.getOwner() != null && userId.equals(issue.getOwner().getId()) &&
           UserUtilities.hasPermission(permissions, issue.getProject().getId(), PermissionType.ISSUE_UNASSIGN_SELF.toInt())) {
            return true;
        }

        return false;
    }

    public static boolean hasIssueRelation(Issue issue, Integer relatedIssueId) {
        if(issue != null) {
            List<IssueRelation> relations = issue.getRelations();
            for(int i = 0; i < relations.size(); i++) {
                if(relations.get(i).getRelatedIssue().getId().equals(relatedIssueId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasIssueNotification(Issue issue, Integer userId) {
        return hasIssueNotification(issue, issue.getProject(), userId);
    }

    public static boolean hasIssueNotification(Issue issue, Project project, Integer userId) {
        if(issue == null || userId == null) {
            return false;
        }
        
        if( (issue.getOwner() != null && issue.getOwner().getId().equals(userId))
        || issue.getCreator().getId().equals(userId)) {
            return true;
        }

        if(project != null && project.getOwners() != null) {
            List<User> owners = project.getOwners();
            for(int i = 0; i < owners.size(); i++) {
                if(owners.get(i) != null && owners.get(i).getId().equals(userId)) {
                    return true;
                }
            }
        }

        List<Notification> notifications = issue.getNotifications();
        for(int i = 0; i < notifications.size(); i++) {
            if(notifications.get(i).getUser().getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }
}

class StatusComparator implements Comparator {

    public int compare(Object a, Object b) {
        if(a == null || b == null || ! (a instanceof String) || ! (b instanceof String)) {
            return 0;
        }

        if(((String) a).equals((String) b)) {
            return 0;
        }

        try {
            if(new Integer((String) a).intValue() > new Integer((String) b).intValue()) {
                return 1;
            } else {
                return -1;
            }
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }
}

