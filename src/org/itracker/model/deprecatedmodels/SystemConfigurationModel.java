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

import java.util.ArrayList;
import java.util.Arrays; 
import java.util.List;

import org.itracker.services.util.SystemConfigurationUtilities;

public class SystemConfigurationModel extends GenericModel {
    private String version;

    private List<CustomFieldModel> customFields = new ArrayList<CustomFieldModel>();

    private List<ConfigurationModel> resolutions = new ArrayList<ConfigurationModel>();
    private List<ConfigurationModel> severities = new ArrayList<ConfigurationModel>();
    private List<ConfigurationModel> statuses = new ArrayList<ConfigurationModel>();

    public SystemConfigurationModel() {
    }

    public String getVersion() {
        return (version == null ? "" : version);
    }

    public void setVersion(String value) {
        version = value;
    }

    public List<CustomFieldModel> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldModel> value) {
        if(value != null) {
            customFields = value;
        }
    }

    public List<ConfigurationModel> getResolutions() {
        return (resolutions == null ? new ArrayList<ConfigurationModel>() : resolutions);
    }

    public void setResolutions(List<ConfigurationModel> value) {
        if(value != null) {
            resolutions = value;
        }
    }

    public List<ConfigurationModel> getSeverities() {
        return (severities == null ? new ArrayList<ConfigurationModel>() : severities);
    }

    public void setSeverities(List<ConfigurationModel> value) {
        if(value != null) {
            severities = value;
        }
    }

    public List<ConfigurationModel> getStatuses() {
        return (statuses == null ? new ArrayList<ConfigurationModel>() : statuses);
    }

    public void setStatuses(List<ConfigurationModel> value) {
        if(value != null) {
            statuses = value;
        }
    }

    public void addConfiguration(ConfigurationModel model) {
        if(model != null) {
            ConfigurationModel[] newArray;

            if(model.getType() == SystemConfigurationUtilities.TYPE_RESOLUTION) {
                newArray = new ConfigurationModel[getResolutions().size() + 1];
                if(getResolutions().size() > 0) {
                    System.arraycopy((Object) resolutions, 0, (Object) newArray, 0, resolutions.size());
                }
                newArray[getResolutions().size()] = model;
                setResolutions(Arrays.asList(newArray));
            } else if(model.getType() == SystemConfigurationUtilities.TYPE_SEVERITY) {
                newArray = new ConfigurationModel[getSeverities().size() + 1];
                if(getSeverities().size() > 0) {
                    System.arraycopy((Object) severities, 0, (Object) newArray, 0, severities.size());
                }
                newArray[getSeverities().size()] = model;
                setSeverities(Arrays.asList(newArray));
            } else if(model.getType() == SystemConfigurationUtilities.TYPE_STATUS) {
                newArray = new ConfigurationModel[getStatuses().size() + 1];
                if(getStatuses().size() > 0) {
                    System.arraycopy((Object) statuses, 0, (Object) newArray, 0, statuses.size());
                }
                newArray[getStatuses().size()] = model;
                setStatuses(Arrays.asList(newArray));
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("SystemConfiguration Version: " + getVersion() + "\n");
        buf.append("  Resolutions:");
        for(int i = 0; i < resolutions.size(); i++) {
            buf.append(" " + resolutions.get(i).getName() + "(" + resolutions.get(i).getValue() + ")");
        }
        buf.append("\n");
        buf.append("  Severities:");
        for(int i = 0; i < severities.size(); i++) {
            buf.append(" " + severities.get(i).getName() + "(" + severities.get(i).getValue() + ")");
        }
        buf.append("\n");
        buf.append("  Statuses:");
        for(int i = 0; i < statuses.size(); i++) {
            buf.append(" " + statuses.get(i).getName() + "(" + statuses.get(i).getValue() + ")");
        }
        buf.append("\n");

        return buf.toString();
    }
}
