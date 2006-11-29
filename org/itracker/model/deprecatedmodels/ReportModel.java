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

package org.itracker.model.deprecatedmodels;

import java.util.Comparator;

class ReportModel extends GenericModel implements Comparator<ReportModel> {
    private String name;
    private String nameKey;
    private String description;
    private int dataType;
    private int reportType;
    private byte[] fileData;
    private String className;

    public ReportModel() {
        name = "";
        nameKey = null;
        description = "";
        dataType = 0;
        reportType = 0;
        fileData = new byte[0];
        className = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String value) {
        nameKey = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int value) {
        dataType = value;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int value) {
        reportType = value;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] value) {
        fileData = (value != null && value.length > 0 ? value : new byte[0]);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String value) {
        className = value;
    }

    public String toString() {
        return "Report [" + getId() + "] Name: " + getName() + " Name Key: " + getNameKey() + " Data Type: " + getDataType() +
               " Report Type: " + getReportType() + " Class: " + getClassName() + " Definition Size: " + getFileData().length;
    }

    public int compare(ReportModel a, ReportModel b) {
        return new ReportModel.CompareById().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof ReportModel)) {
            return false;
        }

        try {
            ReportModel mo = (ReportModel) obj;
            if(ReportModel.this.getName().equals(mo.getName()) &&
               ReportModel.this.getDescription().equals(mo.getDescription()) &&
               ReportModel.this.getNameKey().equals(mo.getNameKey())) {
                return true;
            }
        } catch(Exception e) {
        }

        return false;
    }

    public int hashCode() {
        return ((ReportModel.this.getName() == null ? 1 : ReportModel.this.getName().hashCode()) ^
                (ReportModel.this.getDescription() == null ? 1 : ReportModel.this.getDescription().hashCode()) ^
                (ReportModel.this.getNameKey() == null ? 1 : ReportModel.this.getNameKey().hashCode()));
    }

    public static abstract class ReportModelComparator implements Comparator {
        protected boolean isAscending = true;

        public ReportModelComparator() {
        }

        public ReportModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public final int compare(Object a, Object b) {
            if(! (a instanceof ReportModel) || ! (b instanceof ReportModel)) {
                throw new ClassCastException();
            }

            ReportModel ma = (ReportModel) a;
            ReportModel mb = (ReportModel) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }

        protected int doComparison(ReportModel ma, ReportModel mb) {
            if(ma.getId() == null) {
                return -1;
            } else if(mb.getId() == null) {
                return 1;
            }

            return ma.getId().compareTo(mb.getId());
        }
    }

    public static class CompareById extends ReportModelComparator {
        public CompareById(){
          super();
        }

        public CompareById(boolean isAscending) {
          super(isAscending);
        }
    }
}