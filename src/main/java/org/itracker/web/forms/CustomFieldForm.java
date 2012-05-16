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

package org.itracker.web.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.actions.admin.configuration.EditCustomFieldActionUtil;
import org.itracker.web.util.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * This is the Struts Form. It is used by action.
 *
 * @author ready
 */
@SuppressWarnings("serial")
public class CustomFieldForm extends ValidatorForm {
    String action = null;
    Integer id = null;
    Integer fieldType = null;
    String required = null;
    String dateFormat = null;
    String sortOptionsByName = null;
    String value = null;
    //	private String base_locale;
    HashMap<String, String> translations = new HashMap<String, String>();


    /*
      * public void reset(ActionMapping mapping, HttpServletRequest request) {
      * action = null; id = null; fieldType = null; required= null; dateFormat=
      * null; sortOptionsByName= null; value= null; translations = null;
      *  }
      */
    @Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        if (null == getBaseTranslation() || "".equals(getBaseTranslation().trim())) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.required",
                    ITrackerResources.getString("itracker.web.attr.baselocale", LoginUtilities.getCurrentLocale(request))));
        }

        EditCustomFieldActionUtil.setRequestEnv(request, this);
        return errors;
    }


    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getSortOptionsByName() {
        return sortOptionsByName;
    }

    public void setSortOptionsByName(String sortOptionsByName) {
        this.sortOptionsByName = sortOptionsByName;
    }

    public HashMap<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(HashMap<String, String> translations) {
        this.translations = translations;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * get localization in base locale
     */
    private String getBaseTranslation() {
        return translations.get(ITrackerResources.BASE_LOCALE);
    }


}
