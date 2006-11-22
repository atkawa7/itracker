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

class VersionModel extends GenericModel implements Comparator<VersionModel> {
    private String number;
    private int major;
    private int minor;
    private String description;
    private Integer projectId;

    public VersionModel() {
        major = 0;
        minor = 0;
        number = "";
        description = "";
        projectId = new Integer(-1);
    }

    public VersionModel(Integer id) {
        super(id);
    }

    public String getNumber() {
        return (number == null ? "" : number);
    }

    public void setNumber(String value) {
         number = value;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int value) {
        major = value;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int value) {
        minor = value;
    }

    public String getDescription() {
        return (description == null ? "" : description);
    }

    public void setDescription(String value) {
         description = value;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer value) {
        projectId = value;
    }

    /**
      * This convience method will set the number, major and minor
      * fields with a single call.  It will first set the number to
      * the provided data, and then attempt to parse the info if in
      * the form major.minor into parts to set the other information.
      * @parameter versionInfo the version number string to use
      */
    public void setVersionInfo(String versionInfo) {
        setNumber(versionInfo);

        String versionNumber = getNumber().trim();
        int firstDot = versionNumber.indexOf('.');
        String major = "0";
        major = (firstDot > 0 ? versionNumber.substring(0, firstDot).trim() : versionNumber.trim());

        try {
            setMajor(Integer.parseInt(major));
        } catch(NumberFormatException nfe) {
            setMajor(0);
        }

        int secondDot = (firstDot > -1 ? versionNumber.indexOf('.', firstDot + 1) : -1);
        String minor = (secondDot > -1 ? versionNumber.substring(firstDot + 1, secondDot).trim() : versionNumber.substring(firstDot + 1).trim());
        try {
            setMinor(Integer.parseInt(minor));
        } catch(NumberFormatException nfe) {
            setMinor(0);
        }
    }

    public String toString() {
        return "Version [" + this.getId() + "] Project: " + this.getProjectId() + "  Number: " + this.getNumber();
    }

    public int compare(VersionModel a, VersionModel b) {
        return new VersionModel.CompareByMajorMinor(false).compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof VersionModel)) {
            return false;
        }

        try {
            VersionModel mo = (VersionModel) obj;
            if(VersionModel.this.getMajor() == mo.getMajor() && VersionModel.this.getMinor() == mo.getMinor()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (VersionModel.this.getMajor() ^ VersionModel.this.getMinor());
    }

    public static class CompareByMajorMinor implements Comparator {
        private boolean isAscending = true;

        public CompareByMajorMinor() {
        }

        public CompareByMajorMinor(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Object a, Object b) {
            int result = 0;

            if(! (a instanceof VersionModel) || ! (b instanceof VersionModel)) {
                throw new ClassCastException();
            }

            VersionModel ma = (VersionModel) a;
            VersionModel mb = (VersionModel) b;

            if(ma.getMajor() == mb.getMajor()) {
                if(ma.getMinor() == mb.getMinor()) {
                    if(ma.getNumber() == null) {
                        result = -1;
                    } else if(mb.getNumber() == null) {
                        result = 1;
                    } else {
                        result = ma.getNumber().compareTo(mb.getNumber());
                    }
                } else if(ma.getMinor() > mb.getMinor()) {
                    result = 1;
                } else {
                    result = -1;
                }
            } else if(ma.getMajor() > mb.getMajor()) {
                result = 1;
            } else {
                result = -1;
            }

            return (isAscending ? result : result * -1);
        }
    }
}
