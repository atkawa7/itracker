package org.itracker.web.ptos;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.services.ProjectService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.util.ServletContextUtils;

public class ProjectPTO {

	private final Project project;
	
	private Long totalOpenIssues = null;
	private Long totalResolvedIssues = null;
	private Date lastUpdatedIssueDate = null;
	private Boolean canCreate = null;
	
	private final ProjectService projectService;
	private final Map<Integer, Set<PermissionType>> permissions;
	
	public ProjectPTO(Project project, Map<Integer, Set<PermissionType>> permissions) {
		this(project, ServletContextUtils.getItrackerServices().getProjectService(), permissions);
		
	}
	public ProjectPTO(Project project, ProjectService projectService, final Map<Integer, Set<PermissionType>> permissions) {
		if (null == project) {
			throw new IllegalArgumentException("Project must not be null");
		}
		this.project = project;
		this.projectService = projectService;
		this.permissions = Collections.unmodifiableMap(permissions);
	}
	public Project getProject() {
		return project;
	}

	public Long getTotalNumberIssues() {
		return getTotalOpenIssues() + getTotalResolvedIssues();
	}
	public void setTotalNumberIssues(Long totalNumberIssues) {
		setTotalOpenIssues(totalNumberIssues);
		setTotalResolvedIssues(0l);
	}
	
	public void setTotalOpenIssues(Long totalOpenIssues) {
		this.totalOpenIssues = totalOpenIssues;
	}
	public Long getTotalOpenIssues() {
		if (null == totalOpenIssues) {
			setupNumberOfOpenIssues(this, projectService);
		}
		return totalOpenIssues;
	}
	public void setTotalResolvedIssues(Long totalResolvedIssues) {
		this.totalResolvedIssues = totalResolvedIssues;
	}
	public Long getTotalResolvedIssues() {
		if (null == totalResolvedIssues) {
			setupNumberOfResolvedIssues(this, projectService);
		}
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
		if (null == this.canCreate) {
			setupCanCreate(this, permissions);
		}
		return this.canCreate;
	}
	public Boolean isCanCreate() {
		return getCanCreate();
	}
	public void setCanCreate(Boolean canCreate) {
		this.canCreate = canCreate;
	}
	
	
	public void setLastUpdatedIssueDate(Date lastUpdatedIssueDate) {
		if (null == lastUpdatedIssueDate) {
			setupLastIssueUpdateDate(this, projectService);
		}
		this.lastUpdatedIssueDate = lastUpdatedIssueDate;
	}
	public Date getLastUpdatedIssueDate() {
		return lastUpdatedIssueDate;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("project", getProject()).toString();
	}

	//TODO: Code Cleanup 
	private static final void setupNumberOfIssues(ProjectPTO pto,
			ProjectService service) {
		pto.setTotalNumberIssues(service.getTotalNumberIssuesByProject(pto
				.getId()));
	}

	private static final void setupNumberOfOpenIssues(ProjectPTO pto,
			ProjectService service) {
		pto.setTotalOpenIssues(service.getTotalNumberOpenIssuesByProject(pto
				.getId()));
	}

	private static final void setupNumberOfResolvedIssues(ProjectPTO pto,
			ProjectService service) {
		pto.setTotalResolvedIssues(service
				.getTotalNumberResolvedIssuesByProject(pto.getId()));
	}

	private static final void setupCanCreate(ProjectPTO pto,
			final Map<Integer, Set<PermissionType>> permissions) {
		pto.setCanCreate(UserUtilities.hasPermission(permissions, pto.getId(),
				UserUtilities.PERMISSION_CREATE));
	}

	private static final void setupLastIssueUpdateDate(ProjectPTO pto,
			ProjectService service) {
		pto.setLastUpdatedIssueDate(service
				.getLatestIssueUpdatedDateByProjectId(pto.getId()));
	}
}
