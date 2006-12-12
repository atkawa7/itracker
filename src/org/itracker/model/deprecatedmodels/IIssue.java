package org.itracker.model.deprecatedmodels;

import java.util.List;

public interface IIssue {

	public abstract String getDescription();

	public abstract void setDescription(String value);

	public abstract int getSeverity();

	public abstract void setSeverity(int value);

	public abstract int getStatus();

	public abstract void setStatus(int value);

	public abstract String getResolution();

	public abstract void setResolution(String value);

	public abstract ProjectModel getProject();

	public abstract void setProject(ProjectModel value);

	public abstract Integer getProjectId();

	public abstract String getProjectName();

	public abstract VersionModel getTargetVersion();

	public abstract void setTargetVersion(VersionModel value);

	public abstract String getTargetVersionNumber();

	public abstract Integer getTargetVersionId();

	public abstract UserModel getCreator();

	public abstract void setCreator(UserModel value);

	public abstract Integer getCreatorId();

	public abstract String getCreatorFirstName();

	public abstract String getCreatorLastName();

	public abstract UserModel getOwner();

	public abstract void setOwner(UserModel value);

	public abstract Integer getOwnerId();

	public abstract String getOwnerLogin();

	public abstract String getOwnerFirstName();

	public abstract String getOwnerLastName();

	public abstract List<IssueHistoryModel> getHistory();

	public abstract void setHistory(List<IssueHistoryModel> value);

	public abstract List<IssueAttachmentModel> getAttachments();

	public abstract void setAttachments(List<IssueAttachmentModel> value);

	public abstract List<IssueFieldModel> getFields();

	public abstract void setFields(List<IssueFieldModel> value);

	public abstract List<IssueRelationModel> getRelations();

	public abstract void setRelations(List<IssueRelationModel> value);

	public abstract List<NotificationModel> getNotifications();

	public abstract void setNotifications(List<NotificationModel> value);

	public abstract List<ComponentModel> getComponents();

	public abstract void setComponents(List<ComponentModel> value);

	public abstract List<VersionModel> getVersions();

	public abstract void setVersions(List<VersionModel> value);

}