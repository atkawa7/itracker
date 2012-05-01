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
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * 
 * <p>
 * All entities are Java Beans and should inherit this class to make sure they
 * are Serializable and Cloneable and have the following fields : an id, a
 * creation date and a last modifiation date.
 * </p>
 * 
 * @author ready
 */
public abstract class AbstractEntity implements Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Comparator<Entity> ID_COMPARATOR = new IdComparator();

	public static final Comparator<AbstractEntity> CREATE_DATE_COMPARATOR = new CreateDateComparator();

	public static final Comparator<AbstractEntity> LAST_MODIFIED_DATE_COMPARATOR = new LastModifiedDateComparator();

	/** System ID */
	private Integer id;

	/** Creation date and time. */
	private Date createDate = new Date();

	/** Last modification date and time. */
	private Date lastModifiedDate = new Date();

	/**
	 * Default constructor (required by Hibernate).
	 */
	public AbstractEntity() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return creation time stamp or <tt>null</tt> for transient entities
	 */
	public Date getCreateDate() {
		if (null == createDate)
			createDate = new Date();
		return new Date(createDate.getTime());
	}

	/**
	 * Sets the creation date and time.
	 * 
	 * <p>
	 * The persistence framework automatically sets this property when a new
	 * entity is persisted. <br>
	 * Note that the value is managed by the persistence framework and may be
	 * generated by the database in the future.
	 * </p>
	 * 
	 * <p>
	 * The creation time stamp should never change once initialized.
	 * </p>
	 * 
	 * @param dateTime
	 *            creation time stamp
	 */
	public void setCreateDate(Date dateTime) {
		if (null == dateTime)
			return;
		this.createDate = new Date(dateTime.getTime());
	}

	/**
	 * @return last modification time stamp or <tt>null</tt> for transient
	 *         entities
	 */
	public Date getLastModifiedDate() {
		if (null == this.lastModifiedDate)
			this.lastModifiedDate = new Date();
		return new Date(lastModifiedDate.getTime());
	}

	/**
	 * Sets the last modification date and time.
	 * 
	 * <p>
	 * The persistence framework automatically sets this property to the same
	 * value as the createDate property when persisting a new entity and
	 * automatically updates it when saving an existing one. <br>
	 * Note that the value is managed by the persistence framework and may be
	 * generated by the database in the future.
	 * </p>
	 * 
	 * @param dateTime
	 *            last modification time stamp
	 */
	public void setLastModifiedDate(Date dateTime) {
		if (null == dateTime)
			return;
		this.lastModifiedDate = new Date(dateTime.getTime());
	}

	/**
	 * Returns whether this instance represents a new transient instance.
	 * 
	 * @return <tt>true</tt> if <code>id</code> is <tt>null</tt>
	 */
	public boolean isNew() {
		return (this.getId() == null);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Compares 2 instances by ID.
	 */
	protected static class IdComparator implements Comparator<Entity>, Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public int compare(Entity a, Entity b) {
			return new CompareToBuilder().append(a.getId(), b.getId())
					.toComparison();
		}

	}

	protected static class CreateDateComparator implements
			Comparator<AbstractEntity>, Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int compare(AbstractEntity a, AbstractEntity b) {
			return new CompareToBuilder().append(a.getCreateDate(),
					b.getCreateDate()).toComparison();
		}

	}

	/**
	 * Compares 2 instances by last modified date.
	 */
	protected static class LastModifiedDateComparator implements
			Comparator<AbstractEntity>, Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int compare(AbstractEntity a, AbstractEntity b) {
			return new CompareToBuilder().append(a.getLastModifiedDate(),
					b.getLastModifiedDate()).toComparison();
		}

	}

	@Override
	public final boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (isNew() || null == obj) {
			return false;
		}

		if (getClass().equals(obj.getClass())) {
			Entity o = (Entity) obj;
			return new EqualsBuilder()
					.append(getId(), o.getId()).isEquals();

		}

		return false;

	}

	public final int compareTo(Entity o) {
		if (this.equals(o)) {
			return 0;
		}
		return new CompareToBuilder().append(getClass(), o.getClass(), AbstractEntity.CLASS_COMPARATOR).append(
				getId(), o.getId()).toComparison();
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder().append(getClass()).append(getId()).toHashCode();

	}
	
	private static final Comparator<Class<?>> CLASS_COMPARATOR = new Comparator<Class<?>>() {
		public int compare(Class<?> o1, Class<?> o2) {
			return new CompareToBuilder().append(o1.getSimpleName(), o2.getSimpleName()).append(o1.hashCode(), hashCode()).toComparison();
		}
	};

}
