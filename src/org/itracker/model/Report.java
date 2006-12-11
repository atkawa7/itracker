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

import java.sql.Timestamp;
import java.util.Date;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * 
 * @author ready
 * 
 */

public class Report extends AbstractBean {

	private String name;

	private String nameKey;

	private String description;

	private int dataType;

	private int reportType;

	private byte[] fileData;

	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = (fileData != null && fileData.length > 0 ? fileData : new byte[0]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameKey() {
		return nameKey;
	}

	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public Report getModel() {

		Report report = new Report();
		report.setId(this.getId());
		report.setName(this.getName());
		report.setNameKey(this.getNameKey());
		report.setDescription(this.getDescription());
		report.setDataType(this.getDataType());
		report.setFileData(this.getFileData());
		report.setReportType(this.getReportType());
		report.setClassName(this.getClassName());
		report.setLastModifiedDate(this.getLastModifiedDate());
		report.setCreateDate(this.getCreateDate());

		return report;

	}

	public void setModel(Report report) {
		this.setName(report.getName());
		this.setNameKey(report.getNameKey());
		this.setDescription(report.getDescription());
		this.setDataType(report.getDataType());
		this.setFileData(report.getFileData());
		this.setReportType(report.getReportType());
		this.setClassName(report.getClassName());
		if (report.getFileData().length > 0) {
			this.setFileData(report.getFileData());
		}
		this.setCreateDate(new Timestamp(report.getCreateDate().getTime()));
		this.setLastModifiedDate(new Timestamp(new Date().getTime()));
	}

}