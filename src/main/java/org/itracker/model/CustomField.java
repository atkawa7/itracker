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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.util.CustomFieldUtilities;

/**
 * A custom field that can be added to an Issue. 
 * 
 * <p>Allows the user to dynamically extend the set of attributes/properties 
 * of the Issue class. </p>
 * 
 * <p>A CustomField must be configured to be used in a Project in order to 
 * extend the attributes/properties of all Issues created for that project. 
 * A CustomField may be used in more than 1 project. (Project - CustomField 
 * is a M-N relathionship). </p>
 * 
 * <p>A CustomField has a type, which indicates the data type of its value. <br>
 * The special type <code>LIST</code>, allows to associate a list of string options 
 * to a CustomField, which are the enumeration of possible values for that field. <br>
 * Each option value is represented by a CustomFieldValue instance. 
 * There's a 1-N relationship between CustomField - CustomFieldValue. 
 * A CustomFieldValue can only belong to 1 CustomField (composition). </p>
 * 
 * <p>A value of a CustomField for a given Issue is represented by 
 * an IssueField instance. (CustomField - IssueField is a 1-N relationship). </p>
 * 
 * @author ready
 * @see CustomFieldValue 
 * @see IssueField
 */
public class CustomField extends AbstractEntity 
        implements Comparable<CustomField> {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
     * Unique name identifying this custom field globally. 
     * 
     * <p>No matching field exists in the database for this property :
     * it is obtained from a <code>ResourceBundle</code>, 
     * not from the database! </p> 
     */
    private String name;
    
    /* Is a CustomField instance locale-specific ? */
    //private Locale locale;
    
    /** 
     * Field value data type. 
     */
    private Type type;
    
    /** 
     * Display format to use if <code>fieldType</code> is a Date.
     * 
     * TODO: use type-safe enum CustomField.DateFormat 
     */
    private String dateFormat;
    
    /** 
     * Whether this field is mandatory or optional. 
     * PENDING: this should be specified when the field is used in a project! 
     */
    private boolean required;
    
    /** 
     * List of options for a field of type <code>LIST</code>. 
     * 
     * <p>This is the enumeration of possible values for the field. </p>
     * 
     * Note: this field used to be named <code>values</code> is iTracker 2.
     * 
     * <p>PENDING: There's no way to use this as a list of proposed values, 
     * allowing the user to enter a value that's not in this list. </p>
     */
    private List<CustomFieldValue> options = new ArrayList<CustomFieldValue>();
    
    /** 
     * Whether the options of a field of type List should be sorted 
     * by their name rather than by {@link CustomFieldValue#getSortOrder() }. 
     */
    private boolean sortOptionsByName;
    
    /* This class used to have a <code>fields</code> attribute, which was 
     * a Collection<IssueField>. This has been removed because the association 
     * CustomField - IssueField doesn't need to be navigatable in this direction. 
     */
    
    /**
     * Default constructor (required by Hibernate). 
     * 
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's 
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public CustomField() {
    }
    
    public CustomField(String name, Type type) {
        setName(name);
        setFieldType(type);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Type getFieldType() {
        return type;
    }
    
    public void setFieldType(Type type) {
        this.type = type;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public List<CustomFieldValue> getOptions() {
        return options;
    }
    
    public void setOptions(List<CustomFieldValue> options) {
        this.options = options;
    }
    
    /**
     * Adds a new option value/name to the custom field.  
     * 
     * <p>New options are put at the end of the list even if they should be 
     * sorted. <br>
     * This method is mainly used to build a new custom field so it can be 
     * saved later. </p>
     * 
     * @param value the option value
     * @param label the label/name for the new option
     */
    public void addOption(String value, String label) {
        this.options.add(new CustomFieldValue(this, value));
    }
    
    /**
     * Returns the name for a particular option value. 
     * 
     * @param optionValue the value to lookup the name for
     * @return the localized name for the supplied value
     */
    public String getOptionNameByValue(String optionValue) {
        final Iterator<CustomFieldValue> iter = this.options.iterator();
            
        while (iter.hasNext()) {
            CustomFieldValue option = iter.next();

            if (option.getValue().equalsIgnoreCase(optionValue)) {
                return option.getName();
            }
        }
        return "";
    }
    
    public boolean isSortOptionsByName() {
        return sortOptionsByName;
    }
    
    public void setSortOptionsByName(boolean sortOptionsByName) {
        this.sortOptionsByName = sortOptionsByName;
    }
    
    /**
     * Sets this custom fields names based on the supplied local string.
     * @param locale the name of the locale to use for the names
     */
    public void setLabels(String locale) {
        Locale loc = ITrackerResources.getLocale(locale);
        setLabels(loc);
    }
    
    /**
     * Sets this custom fields names based on the supplied locale.
     * @param locale the locale to use for the names
     */
    public void setLabels(Locale locale) {
        @SuppressWarnings("unused")
	String localeCode = locale.toString();
        
        this.name = CustomFieldUtilities.getCustomFieldName(getId(), locale);
        
        final Iterator<CustomFieldValue> iter = this.options.iterator();
            
        while (iter.hasNext()) {
            CustomFieldValue option = iter.next();

            option.setName(CustomFieldUtilities.getCustomFieldOptionName(
                    this.id, option.getId(), locale));
        }
        
        if (isSortOptionsByName()) {
            // Specify ordering other than the natural ordering of CustomFieldValue. 
            Collections.sort(this.options, CustomFieldValue.NAME_COMPARATOR);
        }
    }
    
    public int compareTo(CustomField other) {
        return this.name.compareTo(other.name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof CustomField) {
            final CustomField other = (CustomField)obj;
            
            return this.name.equals(other.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    /**
     * Checks if the given value is assignable to this custom field. 
     * 
     * @param value custom field data
     * @param locale 
     * @param bundle 
     * @throws IssueException if it isn't
     * @see IssueField#setValue(String, Locale, ResourceBundle)
     */
    public void checkAssignable(String value, Locale locale, ResourceBundle bundle) 
        throws IssueException {
        
        switch (this.type) {
            
            case INTEGER:
                try {
                    Integer.parseInt(value);
                } catch(NumberFormatException nfe) {
                    throw new IssueException("Invalid integer.", 
                            IssueException.TYPE_CF_PARSE_NUM);
                }
                break;

            case DATE:
                if (this.dateFormat != CustomFieldUtilities.DATE_FORMAT_UNKNOWN) {
                    SimpleDateFormat format = new SimpleDateFormat(
                            bundle.getString("itracker.dateformat." + this.dateFormat), locale);

                    try {
                        format.parse(value);
                    } catch (ParseException ex) {
                        throw new IssueException("Invalid date format.", 
                                IssueException.TYPE_CF_PARSE_DATE);
                    }
                }
                break;

            default:
                // Value is OK
        }
    }
    
    /** 
     * Enumeration of possible data types. 
     */
    public static enum Type implements IntCodeEnum<Type> {
        
        STRING(1), 
        INTEGER(2), 
        DATE(3), 
        LIST(4);
        
        private final int code;
        
        private Type(int code) {
            this.code = code;
        }
        
        public int getCode() { 
            return code; 
        }
        
        public Type fromCode(int code) {
            return Type.valueOf(code);
        }
        
        public static Type valueOf(int code) {
            switch (code) {
                case 1: return STRING;
                case 2: return INTEGER;
                case 3: return DATE;
                case 4: return LIST;
                default: 
                    throw new IllegalArgumentException(
                        "Unknown code : " + code);
            }
        }
        
    }
    
    /** 
     * Date format for fields of type DATE.
     * 
     * PENDING: consider replacing the DATE Type with these 3 new data types. 
     */
    public static enum DateFormat {
        
        DATE_TIME("full"), 
        DATE("dateonly"), 
        TIME("timeonly");
        
        final String code;
        
        DateFormat(String code) {
            this.code = code;
        }
        
    }
    
}
