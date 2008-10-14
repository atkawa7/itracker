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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This is a POJO Business Domain Object modelling a project.
 * 
 * <p>
 * Hibernate Bean.
 * </p>
 * 
 * @author ready
 */
public class Project extends AbstractEntity implements Comparable<Entity> {

	public static final ProjectComparator PROJECT_COMPARATOR = new ProjectComparator();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private String description;

	/**
	 * Project's current status.
	 * 
	 * <p>
	 * Invariant : never <tt>null</tt>.
	 * </p>
	 */
	private Status status;

	private int options;

	/**
	 * The list of Components that belong to this project.
	 * 
	 * <p>
	 * Project - Component is a 1-N relationship.
	 * </p>
	 */
	private List<Component> components = new ArrayList<Component>();

	/**
	 * The list of Versions of this Project.
	 * 
	 * <p>
	 * Project - Version is a 1-N relationship.
	 * </p>
	 */
	private List<Version> versions = new ArrayList<Version>();

	/**
	 * The Permissions of all Users on this Project.
	 * 
	 * <p>
	 * Project - Permission is a 1-N relationship.
	 * </p>
	 * 
	 * PENDING: Does this relationship need to be navigatable ?
	 */
	private List<Permission> permissions = new ArrayList<Permission>();

	/**
	 * The Users who are responsible for this Project.
	 * 
	 * Project - User (owners) is a M-N relationship.
	 */
	private List<User> owners = new ArrayList<User>();

	/**
	 * The custom fields associated to this Project.
	 * 
	 * <p>
	 * Project - CustomField is a M-N relationship.
	 * </p>
	 * 
	 * <p>
	 * All Issues of this Project will have these custom fields.
	 * </p>
	 */
	private List<CustomField> customFields = new ArrayList<CustomField>();

	/**
	 * Project - ProjectScript is a 1-N relationship.
	 */
	private List<ProjectScript> scripts = new ArrayList<ProjectScript>();

	/*
	 * This class used to have a <code>issues</code> attribute, which was a
	 * Collection<Issue>. This has been removed because the association Project -
	 * Issue doesn't need to be navigatable in this direction.
	 */

	/**
	 * Default constructor (required by Hibernate).
	 * 
	 * <p>
	 * PENDING: should be <code>private</code> so that it can only be used by
	 * Hibernate, to ensure that the fields which form an instance's identity
	 * are always initialized/never <tt>null</tt>.
	 * </p>
	 */
	public Project() {
	}

	public Project(String name) {
		setName(name);
		this.status = Status.ACTIVE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("null name");
		}
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return project's current status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 * @throws IllegalArgumentException
	 *             status is <tt>null</tt>
	 */
	public void setStatus(Status status) {
		if (status == null) {
			throw new IllegalArgumentException("null status");
		}
		this.status = status;
	}

	public int getOptions() {
		return options;
	}

	public void setOptions(int options) {
		this.options = options;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> getComponents) {
		this.components = getComponents;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> getVersions) {
		this.versions = getVersions;
	}

	public List<CustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomField> getCustomFields) {
		this.customFields = getCustomFields;
	}

	public List<User> getOwners() {
		return owners;
	}

	public void setOwners(List<User> getOwners) {
		this.owners = getOwners;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> getPermissions) {
		this.permissions = getPermissions;
	}

	public List<ProjectScript> getScripts() {
		return scripts;
	}

	public void setScripts(List<ProjectScript> getScripts) {
		this.scripts = getScripts;
	}

	// /**
	// * Two project instances are equal if they have the same name.
	// */
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj) {
	// return true;
	// }
	// if (!super.equals(obj)) {
	// return false;
	// }
	//        
	// // if (obj instanceof Project) {
	// // final Project other = (Project)obj;
	// //
	// // return this.name.equals(other.name);
	// // }
	// return true;
	// }
	//    
	// /**
	// * Overridden to match implementation of method {@link #equals(Object)}.
	// */
	// @Override
	// public int hashCode() {
	// return this.name.hashCode();
	// }

	/**
	 * @return <tt>Project [id=id, name=name]</tt>
	 */
	@Override
	public String toString() {

		return new ToStringBuilder(this).append("id", this.id).append("name",
				this.name).append("description", description).toString();
	}

	// /**
	// * Compares 2 projects by name.
	// */
	// public int compareTo(Project other) {
	// if (equals(other)) {
	// return 0;
	// }
	// String name = this.name;
	// if (name == null) {
	// name = "";
	// }
	// return name.compareTo((null==other.name)? "": other.name);
	// }
	/**
	 * Comparator for comparing projects by name
	 */
	public static final class ProjectComparator implements Comparator<Project>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int compare(Project o1, Project o2) {
			return new CompareToBuilder().append(o1.name, o2.name).append(
					o1.description, o2.description).toComparison();
		}
	}
}
