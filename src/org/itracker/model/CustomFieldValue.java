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

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class CustomFieldValue extends AbstractBean {
    
    private CustomField customField;
    private String value;
    private int sortOrder;
    
    public CustomFieldValue() {
    }

    public CustomFieldValue(CustomField customField, String value) {
        setCustomField(customField);
        setValue(value);
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public CustomField getCustomField() {
        return(customField);
    }
    
    public void setCustomField(CustomField values) {
        this.customField = values;
    }
    
    public static abstract class CustomFieldValueModelComparator implements Comparator<CustomFieldValue> {
        protected boolean isAscending = true;

        public CustomFieldValueModelComparator() {
        }

        public CustomFieldValueModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(CustomFieldValue ma, CustomFieldValue mb);


        public final int compare(CustomFieldValue a, CustomFieldValue b) {
            if(! (a instanceof CustomFieldValue) || ! (b instanceof CustomFieldValue)) {
                throw new ClassCastException();
            }

            CustomFieldValue ma = (CustomFieldValue) a;
            CustomFieldValue mb = (CustomFieldValue) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareBySortOrder extends CustomFieldValueModelComparator {
        public CompareBySortOrder(){
          super();
        }

        public CompareBySortOrder(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(CustomFieldValue ma, CustomFieldValue mb) {
            if(ma.getSortOrder() == mb.getSortOrder()) {
                return 0;
            } else if(ma.getSortOrder() > mb.getSortOrder()) {
                return 1;
            } else if(mb.getSortOrder() < mb.getSortOrder()) {
                return -1;
            }

            return 0;
        }
    }

    public static class CompareByName extends CustomFieldValueModelComparator {
        
        public CompareByName(){
          super();
        }

        public CompareByName(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(CustomFieldValue ma, CustomFieldValue mb) {
            if(ma.getCustomField().equals(mb.getCustomField())) {
                if(ma.getSortOrder() > mb.getSortOrder()) {
                    return 1;
                } else if(mb.getSortOrder() < mb.getSortOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                return ma.getCustomField().compareTo(mb.getCustomField());
            }
        }
    }
    
}
