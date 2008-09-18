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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A configuration item.
 * 
 * @author ready
 */
public class Configuration extends AbstractEntity implements Comparable<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * PENDING: this field doesn't exist in the database!?
	 * 
	 * <p>
	 * TODO : every configuration item should have a name, similar to a Java
	 * property in a properties file. A description would be nice to have too.
	 * name + version should be the natural key. (note: we shouldn't allow 2
	 * configuration items with the same name and version, but with different
	 * types).
	 * </p>
	 * 
	 * <p>
	 * But since <code>name</code> is nullable, only the type and value can be
	 * used as natural key at the moment. This should be a temporary situation,
	 * because the value is allowed to change.
	 * </p>
	 */
	private String name;

	/** ITracker version in which this configuration item was added. */
	private String version;

	/** The real type of the value stored as a string. */
	private int type;

	/** The configuration value as a string. */
	private String value;

	/**
	 * Display order.
	 * 
	 * <p>
	 * Several instances may have the same display order.
	 * </p>
	 */
	private int order;

	/**
	 * Default constructor (required by Hibernate).
	 * 
	 * <p>
	 * PENDING: should be <code>private</code> so that it can only be used by
	 * Hibernate, to ensure that the fields which form an instance's identity
	 * are always initialized/never <tt>null</tt>.
	 * </p>
	 */
	public Configuration() {
	}

	public Configuration(int type, String value) {
		setType(type);
		setValue(value);
	}

	public Configuration(int type, NameValuePair pair) {
		this(type, pair.getValue());
		setName(pair.getName());
	}

	public Configuration(int type, String value, String version) {
		this(type, value);
		setVersion(version);
	}

	public Configuration(int type, String value, int order) {
		this(type, value);
		setOrder(order);
	}

	public Configuration(int type, String value, String version, int order) {
		this(type, value, version);
		setOrder(order);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (value == null) {
			throw new IllegalArgumentException("null value");
		}
		this.value = value;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		if (version == null) {
			throw new IllegalArgumentException("null version");
		}
		this.version = version;
	}

	// /**
	// * Compares by natural key (type and value).
	// */
	// public int compareTo(Configuration other) {
	// final int typeComparison = this.type - other.type;
	//        
	// if (typeComparison == 0) {
	// return this.value.compareTo(other.value);
	// }
	// return typeComparison;
	// }

	// /**
	// * Compares configuration items by order, value.
	// */
	// public int compareTo(Configuration other) {
	// final int orderComparison = this.order - other.order;
	//        
	// if (orderComparison == 0) {
	// return this.value.compareTo(other.value);
	// }
	// return orderComparison;
	// }
	//    
	// /**
	// * Compares by natural key (type and value).
	// */
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj) {
	// return true;
	// }
	//        
	// if (obj instanceof Configuration) {
	// final Configuration other = (Configuration)obj;
	//            
	// return (this.type == other.type)
	// && this.value.equals(other.value);
	// }
	// return false;
	// }
	//    
	// /**
	// * Natural key (type and value) hash code.
	// */
	// @Override
	// public int hashCode() {
	// return this.type + this.value.hashCode();
	// }

	// /**
	// * Compares by natural key (name and version).
	// */
	// public int compareTo(Configuration other) {
	// final int nameComparison = this.name.compareTo(other.name);
	//        
	// if (nameComparison == 0) {
	// return this.version.compareTo(other.version);
	// }
	// return nameComparison;
	// }
	//    
	// /**
	// * Compares by natural key (name and version).
	// */
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj) {
	// return true;
	// }
	//        
	// if (obj instanceof Configuration) {
	// final Configuration other = (Configuration)obj;
	//            
	// return this.name.equals(other.name)
	// && this.version.equals(other.version);
	// }
	// return false;
	// }
	//    
	// /**
	// * Natural key (name and version) hash code.
	// */
	// @Override
	// public int hashCode() {
	// return this.name.hashCode() + this.version.hashCode();
	// }

	/**
	 * String composed of system ID and natural key (name and version).
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("type", type)
				.append("name", name).append("version", version).append(
						"value", value).toString();

	}

	public static final class ConfigurationOrderComparator implements
			Comparator<Configuration>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public int compare(Configuration o1, Configuration o2) {
			return new CompareToBuilder().append(o1.order, o2.order)
					.toComparison();
		}

		// /**
		// * Compares configuration items by order, value.
		// */
		// public int compareTo(Configuration other) {
		// final int orderComparison = this.order - other.order;
		//            
		// if (orderComparison == 0) {
		// return this.value.compareTo(other.value);
		// }
		// return orderComparison;
		// }

	}

}
