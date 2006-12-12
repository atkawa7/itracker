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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.util.CustomFieldUtilities;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class IssueField extends AbstractBean {
    
    private String stringValue;
    private int intValue;
    private Date dateValue;
    private Issue issue;
    private CustomField customField;
    
    public IssueField() {
    }
    
    public IssueField(CustomField field) {
        this.customField = field;
        this.customField = field;
    }
    
    public IssueField(CustomField field, Issue issue) {
        this(field);
        this.issue = issue;
    }
    
    public CustomField getCustomField() {
        return customField;
    }
    
    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }
    
    public Date getDateValue() {
        return dateValue;
    }
    
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public int getIntValue() {
        return intValue;
    }
    
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    
    public Issue getIssue() {
        return issue;
    }
    
    public void setIssue(Issue issue) {
        this.issue = issue;
    }
    
    public String getStringValue() {
        return stringValue;
    }
    
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
    
    /**
     * Gets the custom field value as a String.
     * @param locale the locale used for any string formatting
     * @return the current value of this field
     */
    public String getValue(Locale locale) {
        return getValue(ITrackerResources.getBundle(locale), locale);
    }
    
    /**
     * Gets the custom field value as a String.
     * @param bundle a resource bundle to use for any string formatting
     * @param locale a locale to use for any string formatting
     * @return the current value of this field
     */
    public String getValue(ResourceBundle bundle, Locale locale) {
        if(customField == null) {
            return "";
        }
        
        if(customField.getFieldType() == CustomFieldUtilities.TYPE_INTEGER) {
            return Integer.toString(getIntValue());
        } else if(customField.getFieldType() == CustomFieldUtilities.TYPE_DATE) {
            if(customField.getDateFormat() != CustomFieldUtilities.DATE_FORMAT_UNKNOWN) {
                SimpleDateFormat sdf = new SimpleDateFormat(bundle.getString("itracker.dateformat." + customField.getDateFormat()), locale);
                return sdf.format(getDateValue());
            }
        } else {
            return (getStringValue() == null ? "" : getStringValue());
        }
        return "";
    }
    
    /**
     * Sets the custom field value.  Takes a string and then converts the value to the
     * appropriate type based on the defined field type.
     * @param value the value to set this field to as a string
     * @param locale the locale used for any string formatting
     * @throws IssueException represents an error formatting or parsing the value
     */
    public void setValue(String value, Locale locale) throws IssueException {
        setValue(value, locale, ITrackerResources.getBundle(locale));
    }
    
    /**
     * Sets the custom field value.  Takes a string and then converts the value to the
     * appropriate type based on the defined field type.
     * @param value the value to set this field to as a string
     * @param locale the locale used for any string formatting
     * @param bundle the ResourceBundle used for any string formatting
     * @throws IssueException represents an error formatting or parsing the value
     */
    public void setValue(String value, Locale locale, ResourceBundle bundle) throws IssueException {
        if(value != null) {
            if(customField.getFieldType() == CustomFieldUtilities.TYPE_INTEGER) {
                try {
                    setIntValue(Integer.parseInt(value));
                } catch(NumberFormatException nfe) {
                    throw new IssueException("Invalid integer.", IssueException.TYPE_CF_PARSE_NUM);
                }
            } else if(customField.getFieldType() == CustomFieldUtilities.TYPE_DATE) {
                try {
                    if(customField.getDateFormat() != CustomFieldUtilities.DATE_FORMAT_UNKNOWN) {
                        SimpleDateFormat sdf = new SimpleDateFormat(bundle.getString("itracker.dateformat." + customField.getDateFormat()), locale);
                        Date dateValue = sdf.parse(value);
                        if(dateValue != null) {
                            setDateValue(dateValue);
                        } else {
                            throw new IssueException("Invalid date.", IssueException.TYPE_CF_PARSE_DATE);
                        }
                    }
                } catch(Exception ex) {
                    throw new IssueException("Invalid date format.", IssueException.TYPE_CF_PARSE_DATE);
                }
            } else {
                setStringValue(value);
            }
        }
    }
}
