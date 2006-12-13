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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.util.CustomFieldUtilities;


class IssueFieldModel extends GenericModel {
    private CustomFieldModel customField;
    private Integer customFieldId = new Integer(-1);
    
    private String stringValue;
    private int intValue;
    private Date dateValue;
    
    private Integer issueId;
    
    public IssueFieldModel() {
    }
    
    public IssueFieldModel(CustomFieldModel field) {
        this.customField = field;
        this.customFieldId = field.getId();
    }
    
    public IssueFieldModel(CustomFieldModel field, Integer issueId) {
        this(field);
        this.issueId = issueId;
    }
    
    /**
     * Gets the current field id.
     */
    public Integer getCustomFieldId() {
        return (customField != null ? customField.getId() : customFieldId);
    }
    
    /**
     * Sets the current field id.
     */
    public void setCustomFieldId(Integer value) {
        customFieldId = value;
    }
    
    public CustomFieldModel getCustomField() {
        return customField;
    }
    
    public void setCustomField(CustomFieldModel value) {
        customField = value;
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
            if (customField.getDateFormat() != CustomFieldUtilities.DATE_FORMAT_UNKNOWN) {
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
                if(customField.getDateFormat() != CustomFieldUtilities.DATE_FORMAT_UNKNOWN) {
                    SimpleDateFormat sdf = new SimpleDateFormat(bundle.getString("itracker.dateformat." + customField.getDateFormat()), locale);
                    try {
                        @SuppressWarnings("unused")
						Date dateValue = sdf.parse(value);
                    } catch (ParseException ex) {
                        throw new IssueException(
                                    "Unable to set custom field date. Data was '" + value + "'"
                                    , IssueException.TYPE_CF_PARSE_DATE, ex);
                    }
                    setDateValue(dateValue);
                }
            } else {
                setStringValue(value);
            }
        }
    }
    
    public String getStringValue() {
        return stringValue;
    }
    
    public void setStringValue(String value) {
        stringValue = value;
    }
    
    public int getIntValue() {
        return intValue;
    }
    
    public void setIntValue(int value) {
        intValue = value;
    }
    
    public Date getDateValue() {
        return dateValue;
    }
    
    public void setDateValue(Date value) {
        dateValue = value;
    }
    
    public Integer getIssueId() {
        return issueId;
    }
    
    public void setIssueId(Integer value) {
        issueId = value;
    }
}
