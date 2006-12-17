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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 * @author ready
 *
 */
public class CustomFieldForm extends ValidatorForm  {
	  String action;
      Integer id;
      Integer fieldType;
      String required;
      String dateFormat;
      String sortOptionsByName;
      String value;
//    let's try to put String,String here:
      HashMap<String,String> translations;
 
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		action = null;    
		id = null;
		fieldType = null;
		required= null;
		dateFormat= null;
		sortOptionsByName= null;
		value= null;
		translations = null;
     
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
      
        return errors;
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
//  let's try to put String,String here:
	public HashMap<String,String> getTranslations() {
		return translations;
	}
//  let's try to put String,String here:
	public void setTranslations(HashMap<String,String> translations) {
		this.translations = translations;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


}
