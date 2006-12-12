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
import java.util.Date;
import java.util.Iterator;

import org.itracker.model.CustomFieldValue;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.util.CustomFieldUtilities;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class CustomField extends AbstractBean implements Comparable<CustomField> {
    
    private String name;
    private int fieldType;
    private String dateFormat;
    private boolean required;
    private boolean sortOptionsByName;
    private List<CustomFieldValue> values = new ArrayList<CustomFieldValue>();
    private List<Project> projects = new ArrayList<Project>();
//  TODO: What's this ? What type is it?  
    private List fields = new ArrayList(); 
    
    private static final Comparator<CustomField> comparator = new CompareById();
    
    public CustomField() {
    }
    
    public CustomField(Integer id, int fieldType, boolean required) {
        this.setId(id);
        this.setFieldType(fieldType);
        this.setRequired(required);
    }
    
    public CustomField(Integer id, int fieldType, boolean required, String dateFormat) {
        this(id, fieldType, required);
        this.dateFormat = (dateFormat == null ? CustomFieldUtilities.DATE_FORMAT_DATEONLY : dateFormat);
    }
    
    public CustomField(Integer id, int fieldType, boolean required, List<CustomFieldValue> values, boolean sortOptions) {
        this(id, fieldType, required);
        this.values = values;
        this.sortOptionsByName = sortOptions;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String value) {
        name = value;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public Collection getFields() {
        return fields;
    }
    
    public void setFields(List fields) {
        this.fields = fields;
    }
    
    public int getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }
    
    public Collection<Project> getProjects() {
        return projects;
    }
    
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    /**
      * Returns the name for a particular option value.
      * @param optionValue the value to lookup the name for
      * @return the localized name for the supplied value
      */
    public String getOptionNameByValue(String optionValue) {
        if(optionValue != null && ! optionValue.equals("")) {
            for(int i = 0; i < this.values.size(); i++) {
                if(this.values.get(i) != null && this.values.get(i).getValue().equalsIgnoreCase(optionValue)) {
                    return this.values.get(i).getCustomField().getName();
                }
            }
        }
        return "";
    }
    
    public boolean getSortOptionsByName() {
        return sortOptionsByName;
    }
    
    public void setSortOptionsByName(boolean sortOptionsByName) {
        this.sortOptionsByName = sortOptionsByName;
    }
    
    public List<CustomFieldValue> getValues() {
        return values;
    }
    
    public void setValues(List<CustomFieldValue> values) {
        this.values = values;
    }
    
    public List<CustomFieldValue> getOptions() {
        return (this.values == null ? new ArrayList<CustomFieldValue>() : this.values);
    }

    public void setOptions(List<CustomFieldValue> value) {
        this.values = value;
    }
    
    /**
      * Adds a new option value/name to the custom field.  New options are put
      * at the end of the list even if they should be sorted.  This method is
      * mainly used to build a new custom field so it can be saved later.
      * @param value the option value
      * @param label the label/name for the new option
      */
    public void addOption(String value, String label) {
        List<CustomFieldValue> newOptions = new ArrayList<CustomFieldValue>();
        if(getOptions().size()  > 0) {
            System.arraycopy(this.values, 0, newOptions, 0, this.values.size());
        }
        newOptions.add(getOptions().size(), new CustomFieldValue(this, value));

        this.values = newOptions;
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
        
        for(int i = 0; i < this.values.size(); i++) {
            if(this.values.get(i) != null) {
                this.values.get(i).getCustomField().setName(CustomFieldUtilities.getCustomFieldOptionName(getId(), 
                        this.values.get(i).getId(), locale));
            }
        }
        
        if(getSortOptionsByName()) {
            try {
                Collections.sort(this.values, new CustomFieldValue.CompareByName());
            } catch(Exception e) {
            }
        }
    }
    
    public int compareTo(CustomField other) {
        return comparator.compare(this, other);
    }
    
    public static class CompareById implements Comparator<CustomField> {
        protected boolean isAscending = true;

        public CompareById() {
        }

        public CompareById(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(CustomField ma, CustomField mb) {
            int result = 0;

            if(ma.getId() == null && mb.getId() == null) {
                result = 0;
            } else if(ma.getId() == null) {
                result = 1;
            } else if(mb.getId() == null) {
                result = -1;
            } else {
                result = ma.getId().compareTo(mb.getId());
            }
            return isAscending ? result : -result;
        }
    }
    
}
