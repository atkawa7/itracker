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

import java.util.Comparator;
import java.sql.Timestamp;
import java.util.Date;


/**
 * This is a POJO bean, and yes, a Hibernate bean.
 * @author ready
 *  
 */
 
public class Configuration extends AbstractBean implements Comparable<Configuration> {

    private String name;
    private int type;
    private int order;
    private String value;
    private String version;

    private static final Comparator<Configuration> comparator = new ConfigurationComparator();
    
    public Configuration() {
    }

    public Configuration(int type, NameValuePair nvp) {
        setType(type);
        if(nvp != null) {
            setValue(nvp.getValue());
            setName(nvp.getName());
        }
    }

    public Configuration(int type, String value) {
        setType(type);
        setValue(value);
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
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int compareTo(Configuration other) {
        return comparator.compare(this, other);
    }

    public String toString() {
        return "Configuration [" + this.getId() + "] Name: " + this.getName() 
            + " Value: " + this.getValue() + " Type: " + this.getType() 
            + " Version: " + this.getVersion();
    }
    
    public static class ConfigurationComparator implements Comparator<Configuration> {
        
        public int compare(Configuration ma, Configuration mb) {
            if(ma.getOrder() == mb.getOrder()) {
                if(! "".equals(ma.getName()) && ! "".equals(mb.getName()) 
                && ! ma.getName().equals(mb.getName())) {
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
    
}