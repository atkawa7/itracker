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

import java.util.Comparator;

class CustomFieldValueModel extends GenericModel implements Comparator<CustomFieldValueModel> {
    private Integer customFieldId;
    private String optionValue;
    private String name;
    private int sortOrder;

    public CustomFieldValueModel() {
    }

    public CustomFieldValueModel(Integer customFieldId, String value, String name) {
        setCustomFieldId(customFieldId);
        setValue(value);
        setName(name);
    }

    public String getValue() {
        return (optionValue == null ? "" : optionValue);
    }

    public void setValue(String value) {
        optionValue = value;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int value) {
        sortOrder = value;
    }

    public Integer getCustomFieldId() {
        return (customFieldId == null ? new Integer(-1) : customFieldId);
    }

    public void setCustomFieldId(Integer value) {
        customFieldId = value;
    }

    public String getName() {
        return (name == null ? "" : name);
    }

    public void setName(String value) {
        name = value;
    }

    public int compare(CustomFieldValueModel a, CustomFieldValueModel b) {
        return new CustomFieldValueModel.CompareBySortOrder().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof CustomFieldValueModel)) {
            return false;
        }

        try {
            CustomFieldValueModel mo = (CustomFieldValueModel) obj;
            if(CustomFieldValueModel.this.getId() == mo.getId()) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return (CustomFieldValueModel.this.getId() == null ? 1 : CustomFieldValueModel.this.getId().intValue());
    }

    public String toString() {
        return "CustomFieldValue [" + getId() + "] Value: " + getValue() + " CustomField: " + getCustomFieldId();
    }

    public static abstract class CustomFieldValueModelComparator implements Comparator<CustomFieldValueModel> {
        protected boolean isAscending = true;

        public CustomFieldValueModelComparator() {
        }

        public CustomFieldValueModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(CustomFieldValueModel ma, CustomFieldValueModel mb);


        public final int compare(CustomFieldValueModel a, CustomFieldValueModel b) {
            if(! (a instanceof CustomFieldValueModel) || ! (b instanceof CustomFieldValueModel)) {
                throw new ClassCastException();
            }

            CustomFieldValueModel ma = (CustomFieldValueModel) a;
            CustomFieldValueModel mb = (CustomFieldValueModel) b;

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

        protected int doComparison(CustomFieldValueModel ma, CustomFieldValueModel mb) {
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

        protected int doComparison(CustomFieldValueModel ma, CustomFieldValueModel mb) {
            if(ma.getName().equals(mb.getName())) {
                if(ma.getSortOrder() > mb.getSortOrder()) {
                    return 1;
                } else if(mb.getSortOrder() < mb.getSortOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                return ma.getName().compareTo(mb.getName());
            }
        }
    }
}
