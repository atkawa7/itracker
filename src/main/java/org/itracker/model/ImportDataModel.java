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

import org.apache.commons.lang.builder.ToStringBuilder;

public class ImportDataModel extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractEntity[] dataModels;
	private boolean[] existingModel;

	private boolean reuseConfig = true;
	private boolean reuseFields = true;
	private boolean reuseProjects = true;
	private boolean reuseUsers = true;
	private boolean createPasswords = true;

	private int[][] verifyStatistics = new int[7][2];

	public ImportDataModel() {
	}

	public AbstractEntity[] getData() {
		return (dataModels == null ? new AbstractEntity[0] : dataModels);
	}

	public boolean[] getExistingModel() {
		return (existingModel == null ? new boolean[0] : existingModel);
	}

	public boolean getExistingModel(int i) {
		return (existingModel != null && i < existingModel.length ? existingModel[i]
				: false);
	}

	public void setExistingModel(int i, boolean value) {
		if (existingModel != null && i < existingModel.length) {
			existingModel[i] = value;
		}
	}

	public void setData(AbstractEntity[] dataModels, boolean[] existingModel) {
		if (dataModels != null && existingModel != null
				&& dataModels.length == existingModel.length) {
			this.dataModels = dataModels;
			this.existingModel = existingModel;
			this.verifyStatistics = new int[7][2];
		}
	}

	public boolean getReuseConfig() {
		return reuseConfig;
	}

	public void setReuseConfig(boolean value) {
		reuseConfig = value;
	}

	public void setReuseConfig(Boolean value) {
		reuseConfig = (value != null ? value.booleanValue() : true);
	}

	public boolean getReuseFields() {
		return reuseFields;
	}

	public void setReuseFields(boolean value) {
		reuseFields = value;
	}

	public void setReuseFields(Boolean value) {
		reuseFields = (value != null ? value.booleanValue() : true);
	}

	public boolean getReuseProjects() {
		return reuseProjects;
	}

	public void setReuseProjects(boolean value) {
		reuseProjects = value;
	}

	public void setReuseProjects(Boolean value) {
		reuseProjects = (value != null ? value.booleanValue() : true);
	}

	public boolean getReuseUsers() {
		return reuseUsers;
	}

	public void setReuseUsers(boolean value) {
		reuseUsers = value;
	}

	public void setReuseUsers(Boolean value) {
		reuseUsers = (value != null ? value.booleanValue() : true);
	}

	public boolean getCreatePasswords() {
		return createPasswords;
	}

	public void setCreatePasswords(boolean value) {
		createPasswords = value;
	}

	public void setCreatePasswords(Boolean value) {
		createPasswords = (value != null ? value.booleanValue() : true);
	}

	public int[][] getImportStatistics() {
		return verifyStatistics;
	}

	public void addVerifyStatistic(int itemType, int category) {
		try {
			verifyStatistics[itemType][category]++;
		} catch (Exception e) {
		}
	}

	public String statsToString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < verifyStatistics.length; i++) {
			buf.append(i + ":[" + verifyStatistics[i][0] + ", "
					+ verifyStatistics[i][1] + "] ");
		}
		return buf.toString();
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", id).append(
				"dataModels.length", dataModels.length).append("reuseUsers",
				reuseUsers).append("reuseProjects", reuseProjects).append(
				"reuseUsers", reuseUsers).append("reuseFields", reuseFields)
				.append("createPasswords", createPasswords).toString();
	}
}
