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

public class ConfigurationModel extends GenericModel implements Comparator<ConfigurationModel> {
    private int type;
    private int order;
    private String version;
    private String configValue;
    private String name;

    public ConfigurationModel() {
    }

    public ConfigurationModel(int type, NameValuePair model) {
        setType(type);
        if(model != null) {
            setValue(model.getValue());
            setName(model.getName());
        }
    }

    public ConfigurationModel(int type, String value) {
        setType(type);
        setValue(value);
    }

    public ConfigurationModel(int type, String value, String version) {
        this(type, value);
        setVersion(version);
    }

    public ConfigurationModel(int type, String value, int order) {
        this(type, value);
        setOrder(order);
    }

    public ConfigurationModel(int type, String value, String version, int order) {
        this(type, value, version);
        setOrder(order);
    }

    public int getType() {
        return type;
    }

    public void setType(int value) {
        type = value;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int value) {
        order = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        version = value;
    }

    public String getValue() {
        return (configValue == null ? "" : configValue);
    }

    public void setValue(String value) {
        this.configValue = value;
    }

    public String getName() {
        return (name == null ? "" : name);
    }

    public void setName(String value) {
        this.name = value;
    }

    public String toString() {
        return "Configuration [" + this.getId() + "] Name: " + this.getName() + " Value: " + this.getValue() + " Type: " + this.getType() + " Version: " + this.getVersion();
    }

    public int compare(ConfigurationModel a, ConfigurationModel b) {
        if(! (a instanceof ConfigurationModel) || ! (b instanceof ConfigurationModel)) {
            throw new ClassCastException();
        }

        ConfigurationModel ma = (ConfigurationModel) a;
        ConfigurationModel mb = (ConfigurationModel) b;

        if(ma.getOrder() == mb.getOrder()) {
            if(! "".equals(ma.getName()) && ! "".equals(mb.getName()) && ! ma.getName().equals(mb.getName())) {
                return ma.getValue().compareTo(mb.getValue());
            } else {
                return ma.getValue().compareTo(mb.getValue());
            }
        } else if(ma.getOrder() > mb.getOrder()) {
            return 1;
        } else if(mb.getOrder() < mb.getOrder()) {
            return -1;
        }

        return 0;
    }
}
