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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.itracker.services.util.SystemConfigurationUtilities;

/**
 * 
 */
public class SystemConfiguration extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String version;

	private List<CustomField> customFields = new ArrayList<CustomField>();

	private List<Configuration> resolutions = new ArrayList<Configuration>();
	private List<Configuration> severities = new ArrayList<Configuration>();
	private List<Configuration> statuses = new ArrayList<Configuration>();

	public SystemConfiguration() {
	}

	public String getVersion() {
		return (version == null ? "" : version);
	}

	public void setVersion(String value) {
		version = value;
	}

	public List<CustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomField> value) {
		if (value != null) {
			customFields = value;
		}
	}

	public List<Configuration> getResolutions() {
		return (resolutions == null ? new ArrayList<Configuration>()
				: resolutions);
	}

	public void setResolutions(List<Configuration> value) {
		if (value != null) {
			resolutions = value;
		}
	}

	public List<Configuration> getSeverities() {
		return (severities == null ? new ArrayList<Configuration>()
				: severities);
	}

	public void setSeverities(List<Configuration> value) {
		if (value != null) {
			severities = value;
		}
	}

	public List<Configuration> getStatuses() {
		return (statuses == null ? new ArrayList<Configuration>() : statuses);
	}

	public void setStatuses(List<Configuration> value) {
		if (value != null) {
			statuses = value;
		}
	}

	public void addConfiguration(Configuration configuration) {
		if (configuration != null) {
			Configuration[] newArray;

			if (configuration.getType() == SystemConfigurationUtilities.TYPE_RESOLUTION) {
				newArray = new Configuration[getResolutions().size() + 1];
				if (getResolutions().size() > 0) {
					System.arraycopy((Object) resolutions, 0,
							(Object) newArray, 0, resolutions.size());
				}
				newArray[getResolutions().size()] = configuration;
				setResolutions(Arrays.asList(newArray));
			} else if (configuration.getType() == SystemConfigurationUtilities.TYPE_SEVERITY) {
				newArray = new Configuration[getSeverities().size() + 1];
				if (getSeverities().size() > 0) {
					System.arraycopy((Object) severities, 0, (Object) newArray,
							0, severities.size());
				}
				newArray[getSeverities().size()] = configuration;
				setSeverities(Arrays.asList(newArray));
			} else if (configuration.getType() == SystemConfigurationUtilities.TYPE_STATUS) {
				newArray = new Configuration[getStatuses().size() + 1];
				if (getStatuses().size() > 0) {
					System.arraycopy((Object) statuses, 0, (Object) newArray,
							0, statuses.size());
				}
				newArray[getStatuses().size()] = configuration;
				setStatuses(Arrays.asList(newArray));
			}
		}
	}

	public String toString() {

		return new ToStringBuilder(this).append("id", id).append("version", version).append(
				"resolutions", resolutions).append("severities", severities)
				.append("statuses", statuses).toString();

	}
}
