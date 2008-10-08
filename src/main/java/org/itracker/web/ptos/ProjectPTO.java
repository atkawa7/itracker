package org.itracker.web.ptos;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.itracker.model.Project;
import org.itracker.model.Status;

public class ProjectPTO {

	private final Project project;
	
//	private Long totalNumberIssues = 0l;
	private Long totalOpenIssues = 0l;
	private Long totalResolvedIssues = 0l;
	private Date lastUpdatedIssueDate = null;
	private Boolean canCreate = false;
	
	public ProjectPTO(Project project) {
		if (null == project) {
			throw new IllegalArgumentException("Project must not be null");
		}
		this.project = project;
	}
	public Project getProject() {
		return project;
	}

	public Long getTotalNumberIssues() {
		return totalOpenIssues + totalResolvedIssues;
	}
//	public void setTotalNumberIssues(Long totalNumberIssues) {
//		this.totalNumberIssues = totalNumberIssues;
//	}
	
	public void setTotalOpenIssues(Long totalOpenIssues) {
		this.totalOpenIssues = totalOpenIssues;
	}
	public Long getTotalOpenIssues() {
		return totalOpenIssues;
	}
	public void setTotalResolvedIssues(Long totalResolvedIssues) {
		this.totalResolvedIssues = totalResolvedIssues;
	}
	public Long getTotalResolvedIssues() {
		return totalResolvedIssues;
	}
	
	/**
	 * @return
	 * @see org.itracker.model.AbstractEntity#getCreateDate()
	 */
	public Date getCreateDate() {
		return project.getCreateDate();
	}
	/**
	 * @return
	 * @see org.itracker.model.Project#getDescription()
	 */
	public String getDescription() {
		return project.getDescription();
	}
	/**
	 * @return
	 * @see org.itracker.model.AbstractEntity#getId()
	 */
	public Integer getId() {
		return project.getId();
	}
	/**
	 * @return
	 * @see org.itracker.model.AbstractEntity#getLastModifiedDate()
	 */
	public Date getLastModifiedDate() {
		return project.getLastModifiedDate();
	}
	/**
	 * @return
	 * @see org.itracker.model.Project#getName()
	 */
	public String getName() {
		return project.getName();
	}
	/**
	 * @return
	 * @see org.itracker.model.Project#getStatus()
	 */
	public Status getStatus() {
		return project.getStatus();
	}
	
	public Boolean getActive() {
		return getStatus() == Status.ACTIVE;
	}
	public Boolean isActive() {
		return getActive();
	}
	public Boolean getViewable() {
		return getProject().getStatus() == Status.VIEWABLE;
	}
	public Boolean isViewable() {
		return getViewable();
	}
	public Boolean getCanCreate() {
		return this.canCreate;
	}
	public Boolean isCanCreate() {
		return getCanCreate();
	}
	public void setCanCreate(Boolean canCreate) {
		this.canCreate = canCreate;
	}
	
	
	public void setLastUpdatedIssueDate(Date lastUpdatedIssueDate) {
		this.lastUpdatedIssueDate = lastUpdatedIssueDate;
	}
	public Date getLastUpdatedIssueDate() {
		return lastUpdatedIssueDate;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("project", getProject()).toString();
	}

}
