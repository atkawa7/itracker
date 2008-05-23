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
 * An option for the value of a CustomField of type <code>LIST</code>. 
 * 
 * @author ready
 * @author johnny
 */
public class CustomFieldValue extends AbstractEntity 
        implements Comparable<Entity> {
    
    public static final Comparator<CustomFieldValue> NAME_COMPARATOR = 
            new NameComparator();
    
    /** The custom field to which this option belongs. */
    private CustomField customField;
    
    /** 
     * This option's localized label. 
     * 
     * <p>This property is obtained from a <code>ResourceBundle</code>, 
     * not from the database! </p>
     */
    private String name;
    
    /** This option's value. */
    private String value;
    
    /** 
     * This option's order among all available options for 
     * the <code>customField</code>. 
     */
    private int sortOrder;
    
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public CustomFieldValue() {
    }

    public CustomFieldValue(CustomField customField, String value) {
        setCustomField(customField);
        setValue(value);
    }
    
    public CustomField getCustomField() {
        return(customField);
    }
    
    public void setCustomField(CustomField customField) {
        if (customField == null) {
            throw new IllegalArgumentException("null customField");
        }
        this.customField = customField;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        this.value = value;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Uses the <code>sortOrder</code> ascending order as natural ordering. 
     *
     * <p>Natural ordering != natural key ascending order, but this
     * implementation is still consistent with <code>equals</code>. </p>
     */
    public int compareTo(CustomFieldValue other) {
        final int fieldComparison = 
                this.customField.compareTo(other.customField);
        
        if (fieldComparison == 0) {
            //return this.value.compareTo(other.value);
            return this.sortOrder - other.sortOrder;
        }
        return fieldComparison;
    }
    
    /**
     * Natural key = customField + value
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof CustomFieldValue) {
            final CustomFieldValue other = (CustomFieldValue)obj;
            
            return this.customField.equals(other.customField)
                && this.value.equals(other.value);
        }
        return false;
    }
    
    /**
     * Overridden to remain consistent with method <code>equals</code>. 
     */
    @Override
    public int hashCode() {
        return this.customField.hashCode() + this.value.hashCode();
    }
    
    /**
     * Returns a string with this instance's id and natural key. 
     */
    @Override
    public String toString() {
        return "CustomFieldValue [id" + this.id 
            + ", customField=" + this.customField 
            + ", value=" + this.value + "]";
    }
    
    
    /**
     * Compares 2 CustomFieldValues by custom field and sort order. 
     * 
     * <p>Note that it doesn't match the class' natural ordering because 
     * it doesn't take into account the custom field. <br>
     * It should therefore only be used to compare options that belong 
     * to a single custom field. </p>
     */
    private static class SortOrderComparator 
            implements Comparator<CustomFieldValue> {
        
        private SortOrderComparator(){
          super();
        }

        public int compare(CustomFieldValue a, CustomFieldValue b) {
            //final int fieldComparison = 
            //    a.customField.compareTo(b.customField);
        
            //if (fieldComparison == 0) {
                return a.sortOrder - b.sortOrder;
            //}
            //return fieldComparison;
        }
        
    }

    /**
     * Compares 2 CustomFieldValues by name. 
     * 
     * <p>If 2 instances have the same name, they are ordered by sortOrder. </p>
     *
     * <p>It doesn't take into account the custom field. <br>
     * It should therefore only be used to compare options that belong 
     * to a single custom field. </p>
     */
    private static class NameComparator 
            implements Comparator<CustomFieldValue> {
        
        private NameComparator(){
        }

        public int compare(CustomFieldValue a, CustomFieldValue b) {
            //final int fieldComparison = 
            //    a.customField.compareTo(b.customField);
        
            //if (fieldComparison == 0) {
                final int nameComparison = a.name.compareTo(b.name);
                
                if (nameComparison == 0) {
                    return a.sortOrder - b.sortOrder;
                }
                return nameComparison;
            //}
            //return fieldComparison;
        }
        
    }
    
}
