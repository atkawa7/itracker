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

/**
  * Class provides basic storage for name values pairs.  The name is usually
  * a key of some type, like a status number, and the value is a localized name
  * for that key.
  */
public class NameValuePair implements Serializable, Comparable<NameValuePair> {
    
    private String name = "";
    
    private String value = "";

    public NameValuePair() {
    }

    public NameValuePair(String name, String value) {
        setName(name);
        setValue(value);
    }

    /**
      * Returns the name of the name/value pair.
      */
    public String getName() {
        return name;
    }

    /**
      * Sets the name of the name/value pair.
      */
    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    /**
      * Returns the value of the name/value pair.
      */
    public String getValue() {
        return value;
    }

    /**
      * Sets the value of the name/value pair.
      */
    public void setValue(String value) {
        this.value = value;
    }

    public int compareTo(NameValuePair other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof NameValuePair) {
            final NameValuePair other = (NameValuePair)obj;
            
            return this.name.equals(other.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        return "NameValuePair [name=" + this.name + "]";
    }
    
}
