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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.util.CustomFieldUtilities;

public class CustomFieldModel extends GenericModel implements Comparator<CustomFieldModel> {
    private int fieldType = -1;
    private boolean required = false;
    private String dateFormat = CustomFieldUtilities.DATE_FORMAT_DATEONLY;
    private String dataScript = null;
    private String validationScript = null;
    private List<CustomFieldValueModel> options = null;
    private boolean sortOptionsByName = false;
    private String name = "";
    private String locale = "";

    public CustomFieldModel() {
        this.setId(new Integer(-1));
    }

    public CustomFieldModel(Integer id, int fieldType, boolean required) {
        this.setId(id);
        this.setFieldType(fieldType);
        this.setRequired(required);
    }

    public CustomFieldModel(Integer id, int fieldType, boolean required, String dateFormat) {
        this(id, fieldType, required);
        this.dateFormat = (dateFormat == null ? CustomFieldUtilities.DATE_FORMAT_DATEONLY : dateFormat);
    }

    public CustomFieldModel(Integer id, int fieldType, boolean required, List<CustomFieldValueModel> options, boolean sortOptions) {
        this(id, fieldType, required);
        this.options = options;
        this.sortOptionsByName = sortOptions;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int value) {
        fieldType = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean value) {
        required = value;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String value) {
        dateFormat = value;
    }

    public boolean getSortOptionsByName() {
        return sortOptionsByName;
    }

    public void setSortOptionsByName(boolean value) {
        sortOptionsByName = value;
    }

    public List<CustomFieldValueModel> getOptions() {
        return (options == null ? new ArrayList<CustomFieldValueModel>() : options);
    }

    public void setOptions(List<CustomFieldValueModel> value) {
        options = value;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String value) {
        locale = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    /**
      * Returns the name for a particular option value.
      * @param optionValue the value to lookup the name for
      * @return the localized name for the supplied value
      */
    public String getOptionNameByValue(String optionValue) {
        if(optionValue != null && ! optionValue.equals("")) {
            for(int i = 0; i < options.size(); i++) {
                if(options.get(i) != null && options.get(i).getValue().equalsIgnoreCase(optionValue)) {
                    return options.get(i).getName();
                }
            }
        }
        return "";
    }

    /**
      * Adds a new option value/name to the custom field.  New options are put
      * at the end of the list even if they should be sorted.  This method is
      * mainly used to build a new custom field so it can be saved later.
      * @param value the option value
      * @param label the label/name for the new option
      */
    public void addOption(String value, String label) {
        List<CustomFieldValueModel> newOptions = new ArrayList<CustomFieldValueModel>();
        if(getOptions().size()  > 0) {
            System.arraycopy((Object) options, 0, (Object) newOptions, 0, options.size());
        }
        newOptions.add(getOptions().size(),new CustomFieldValueModel(this.getId(), value, label));

        options = newOptions;
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
        this.locale = locale.toString();
        this.name = CustomFieldUtilities.getCustomFieldName(getId(), locale);
        for(int i = 0; i < options.size(); i++) {
            if(options.get(i) != null) {
                options.get(i).setName(CustomFieldUtilities.getCustomFieldOptionName(getId(), options.get(i).getId(), locale));
            }
        }
        if(getSortOptionsByName()) {
            try {
                Collections.sort(options, new CustomFieldValueModel.CompareByName());
            } catch(Exception e) {
            }
        }
    }

    public int compare(CustomFieldModel a, CustomFieldModel b) {
        return new CustomFieldModel.CompareById().compare(a, b);
    }

    public String toString() {
        return "CustomField[" + this.getId() + "] Type: " + this.getFieldType() + " Required: " + this.isRequired() +
               " Date Format: " + this.getDateFormat() + " Num Options: " + this.getOptions().size();
    }

    public static class CompareById implements Comparator {
        protected boolean isAscending = true;

        public CompareById() {
        }

        public CompareById(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        public int compare(Object a, Object b) {
            int result = 0;
            if(! (a instanceof CustomFieldModel) || ! (b instanceof CustomFieldModel)) {
                throw new ClassCastException("Invalid class found during compare.");
            }

            CustomFieldModel ma = (CustomFieldModel) a;
            CustomFieldModel mb = (CustomFieldModel) b;

            if(ma.getId() == null && mb.getId() == null) {
                result = 0;
            } else if(ma.getId() == null) {
                result = 1;
            } else if(mb.getId() == null) {
                result = -1;
            } else {
                result = ma.getId().compareTo(mb.getId());
            }

            return (isAscending ? result : result * -1);
        }
    }

	public String getDataScript() {
		return dataScript;
	}

	public void setDataScript(String dataScript) {
		this.dataScript = dataScript;
	}

	public String getValidationScript() {
		return validationScript;
	}

	public void setValidationScript(String validationScript) {
		this.validationScript = validationScript;
	}
}
