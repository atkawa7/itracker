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

package org.itracker.model.deprecatedmodels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;




class IssueModel extends GenericModel implements Comparator<IssueModel>, IIssue {
    private String description;
    private int severity;
    private int status;
    private String resolution;
    private ProjectModel project;
    private VersionModel targetVersion;
    private UserModel creator;
    private UserModel owner;
    private List<IssueHistoryModel> history;
    private List<IssueAttachmentModel> attachments;
    private List<IssueFieldModel> fields;
    private List<IssueRelationModel> relations;
    private List<NotificationModel> notifications;
    private List<ComponentModel> components;
    private List<VersionModel> versions;
 
    public IssueModel() {
        description = "";
        severity = -1;
        status = -1;
        resolution = "";
        targetVersion = null;

        creator = null;
        owner = null;

        project = null;
        history = null;
        attachments = null;
        components = null;
        versions = null;
        notifications = null;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getDescription()
	 */
    public String getDescription() {
        return (description == null ? "" : description);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setDescription(java.lang.String)
	 */
    public void setDescription(String value) {
        description = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getSeverity()
	 */
    public int getSeverity() {
        return severity;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setSeverity(int)
	 */
    public void setSeverity(int value) {
        severity = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getStatus()
	 */
    public int getStatus() {
        return status;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setStatus(int)
	 */
    public void setStatus(int value) {
        status = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getResolution()
	 */
    public String getResolution() {
        return (resolution == null ? "" : resolution);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setResolution(java.lang.String)
	 */
    public void setResolution(String value) {
        resolution = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getProject()
	 */
    public ProjectModel getProject() {
        return project;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setProject(org.itracker.model.deprecatedmodels.ProjectModel)
	 */
    public void setProject(ProjectModel value) {
        project = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getProjectId()
	 */
    public Integer getProjectId() {
        return (project == null ? new Integer(-1) : project.getId());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getProjectName()
	 */
    public String getProjectName() {
        return (project == null ? "" : project.getName());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getTargetVersion()
	 */
    public VersionModel getTargetVersion() {
        return targetVersion;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setTargetVersion(org.itracker.model.deprecatedmodels.VersionModel)
	 */
    public void setTargetVersion(VersionModel value) {
        targetVersion = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getTargetVersionNumber()
	 */
    public String getTargetVersionNumber() {
        return (targetVersion == null ? "" : targetVersion.getNumber());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getTargetVersionId()
	 */
    public Integer getTargetVersionId() {
        return (targetVersion == null ? new Integer(-1) : targetVersion.getId());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getCreator()
	 */
    public UserModel getCreator() {
        return creator;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setCreator(org.itracker.model.deprecatedmodels.UserModel)
	 */
    public void setCreator(UserModel value) {
        creator = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getCreatorId()
	 */
    public Integer getCreatorId() {
        return (creator == null ? new Integer(-1) : creator.getId());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getCreatorFirstName()
	 */
    public String getCreatorFirstName() {
        return (creator == null ? "" : creator.getFirstName());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getCreatorLastName()
	 */
    public String getCreatorLastName() {
        return (creator == null ? "" : creator.getLastName());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getOwner()
	 */
    public UserModel getOwner() {
        return owner;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setOwner(org.itracker.model.deprecatedmodels.UserModel)
	 */
    public void setOwner(UserModel value) {
        owner = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getOwnerId()
	 */
    public Integer getOwnerId() {
        return (owner == null ? new Integer(-1) : owner.getId());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getOwnerLogin()
	 */
    public String getOwnerLogin() {
        return (owner == null ? "" : owner.getLogin());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getOwnerFirstName()
	 */
    public String getOwnerFirstName() {
        return (owner == null ? "" : owner.getFirstName());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getOwnerLastName()
	 */
    public String getOwnerLastName() {
        return (owner == null ? "" : owner.getLastName());
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getHistory()
	 */
    public List<IssueHistoryModel> getHistory() {
        return (history == null ? new ArrayList<IssueHistoryModel>() : history);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setHistory(java.util.List)
	 */
    public void setHistory(List<IssueHistoryModel> value) {
        history = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getAttachments()
	 */
    public List<IssueAttachmentModel> getAttachments() {
        return (attachments == null ? new ArrayList<IssueAttachmentModel>() : attachments);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setAttachments(java.util.List)
	 */
    public void setAttachments(List<IssueAttachmentModel> value) {
        attachments = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getFields()
	 */
    public List<IssueFieldModel> getFields() {
        return (fields == null ? new ArrayList<IssueFieldModel>() : fields);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setFields(java.util.List)
	 */
    public void setFields(List<IssueFieldModel> value) {
        fields = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getRelations()
	 */
    public List<IssueRelationModel> getRelations() {
        return (relations == null ? new ArrayList<IssueRelationModel>() : relations);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setRelations(java.util.List)
	 */
    public void setRelations(List<IssueRelationModel> value) {
        relations = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getNotifications()
	 */
    public List<NotificationModel> getNotifications() {
        return (notifications == null ? new ArrayList<NotificationModel>() : notifications);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setNotifications(java.util.List)
	 */
    public void setNotifications(List<NotificationModel> value) {
        notifications = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getComponents()
	 */
    public List<ComponentModel> getComponents() {
        return (components == null ? new ArrayList<ComponentModel>() : components);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setComponents(java.util.List)
	 */
    public void setComponents(List<ComponentModel> value) {
        components = value;
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#getVersions()
	 */
    public List<VersionModel> getVersions() {
        return (versions == null ? new ArrayList<VersionModel>() : versions);
    }

    /* (non-Javadoc)
	 * @see org.itracker.model.deprecatedmodels.IIssue#setVersions(java.util.List)
	 */
    public void setVersions(List<VersionModel> value) {
        versions = value;
    }

    public String toString() {
        return "Issue [" + getId() + "] Description: " + getDescription() + " Project: " + getProjectId() +
               " Severity: " + getSeverity() + " Status: " + getStatus() +
               " Creator Id: " + getCreatorId() + " OwnerId: " + getOwnerId() +
               " Num Affected Versions:" + getVersions().size() +
               " Num Affected Components: " + getComponents().size() + " Num Attachments: " + getAttachments().size() +
               " Num History Entries: " + getHistory().size() + " Created: " + getCreateDate() +
               " Last Mod: " + getLastModifiedDate();
    }

    public int compare(IssueModel a, IssueModel b) {
        return new IssueModel.CompareByStatus().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof IssueModel)) {
            return false;
        }

        try {
            IssueModel mo = (IssueModel) obj;
            if(IssueModel.this.getId() == mo.getId()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (IssueModel.this.getId() == null ? 1 : IssueModel.this.getId().intValue());
    }

    public static abstract class IssueModelComparator implements Comparator<IssueModel> {
        protected boolean isAscending = true;

        public IssueModelComparator() {
        }

        public IssueModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(IssueModel ma, IssueModel mb);


        public final int compare(IssueModel a, IssueModel b) {
            if(! (a instanceof IssueModel) || ! (b instanceof IssueModel)) {
                throw new ClassCastException();
            }

            IssueModel ma = (IssueModel) a;
            IssueModel mb = (IssueModel) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByStatus extends IssueModelComparator {
        public CompareByStatus(){
          super();
        }

        public CompareByStatus(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueModel ma, IssueModel mb) {
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

    public static class CompareByProjectAndStatus extends IssueModelComparator {
        public CompareByProjectAndStatus() {
            super();
        }

        public CompareByProjectAndStatus(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(IssueModel ma, IssueModel mb) {
            if(ma.getProjectName().equals(mb.getProjectName())) {
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
                return ma.getProjectName().compareTo(mb.getProjectName());
            }

            return 0;
        }
    }

    public static class CompareByOwnerAndStatus extends IssueModelComparator {
        public CompareByOwnerAndStatus() {
            super();
        }

        public CompareByOwnerAndStatus(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(IssueModel ma, IssueModel mb) {
            if(ma.getOwnerLastName().equals(mb.getOwnerLastName())) {
                if(ma.getOwnerFirstName().equals(mb.getOwnerFirstName())) {
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
                    return ma.getOwnerFirstName().compareTo(mb.getOwnerFirstName());
                }
            } else {
                return ma.getOwnerLastName().compareTo(mb.getOwnerLastName());
            }

            return 0;
        }
    }

    public static class CompareById extends IssueModelComparator {
        public CompareById() {
            super();
        }

        public CompareById(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(IssueModel ma, IssueModel mb) {
            if(ma.getId().intValue() > mb.getId().intValue()) {
                return 1;
            } else if(ma.getId().intValue() < mb.getId().intValue()) {
                return -1;
            }

            return 0;
        }
    }

    public static class CompareBySeverity extends IssueModelComparator {
        public CompareBySeverity() {
            super();
        }

        public CompareBySeverity(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(IssueModel ma, IssueModel mb) {
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

    public static class CompareByDate extends IssueModelComparator {
        public CompareByDate() {
            super();
        }

        public CompareByDate(boolean isAscending) {
            super(isAscending);
        }

        protected int doComparison(IssueModel ma, IssueModel mb) {
            if(ma.getLastModifiedDate() == null && mb.getLastModifiedDate() == null) {
                return 0;
            } else if(ma.getLastModifiedDate() == null) {
                return -1;
            } else if(mb.getLastModifiedDate() == null) {
                return 1;
            }

            if(ma.getLastModifiedDate().equals(mb.getLastModifiedDate())) {
                return 0;
            } else if(ma.getLastModifiedDate().after(mb.getLastModifiedDate())) {
                return 1;
            } else {
                return -1;
            }
        }
    }


}
