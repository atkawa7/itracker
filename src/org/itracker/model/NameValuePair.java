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
public class NameValuePair implements Comparator<NameValuePair>, Serializable {
    private String name = "";
    private String value = "";

    public NameValuePair() {
    }

    public NameValuePair(String name, String value) {
        this.name = (name == null ? "" : name);
        this.value = (value == null ? "" : value);
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
    public void setName(String value) {
        name = value;
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

    public int compare(NameValuePair a, NameValuePair b) {
        return new NameValuePair.CompareByName().compare(a, b);
    }

    // let's try to put the generic Object here (or is this stupid?): 
    public static class CompareByName implements Comparator<Object> {
        protected boolean isAscending = true;

        public CompareByName() {
        }

        public CompareByName(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Object a, Object b) {
            int result = 0;
            if(! (a instanceof NameValuePair) || ! (b instanceof NameValuePair)) {
                throw new ClassCastException();
            }

            NameValuePair ma = (NameValuePair) a;
            NameValuePair mb = (NameValuePair) b;

            if(ma.getName() == null && mb.getName() == null) {
                result = 0;
            } else if(ma.getName() == null) {
                result = 1;
            } else if(mb.getName() == null) {
                result = -1;
            } else {
                result = ma.getName().compareTo(mb.getName());
            }

            return (isAscending ? result : result * -1);
        }
    }
}
