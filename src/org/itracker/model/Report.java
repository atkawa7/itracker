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

        Report model = new Report();
        model.setId(this.getId());
        model.setName(this.getName());
        model.setNameKey(this.getNameKey());
        model.setDescription(this.getDescription());
        model.setDataType(this.getDataType());
        model.setFileData(this.getFileData());
        model.setReportType(this.getReportType());
        model.setClassName(this.getClassName());
        model.setLastModifiedDate(this.getLastModifiedDate());
        model.setCreateDate(this.getCreateDate());

        return model;

    }



    public void setModel(Report model) {

        this.setName(model.getName());

        this.setNameKey(model.getNameKey());

        this.setDescription(model.getDescription());

        this.setDataType(model.getDataType());

        this.setFileData(model.getFileData());

        this.setReportType(model.getReportType());

        this.setClassName(model.getClassName());



        if(model.getFileData().length > 0) {

            this.setFileData(model.getFileData());

        }



        this.setCreateDate(new Timestamp(model.getCreateDate().getTime()));
        this.setLastModifiedDate(new Timestamp(new Date().getTime()));

    }

}