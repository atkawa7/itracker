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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.model.CustomField;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 * 
 * @author ready
 * 
 */
public class ProjectScriptForm extends ValidatorForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String action;
	Integer delId;
	Integer projectId;
	// Integer[] fieldId;
	// Integer[] priority;
	HashMap<String, String> id = new HashMap<String, String>();
	HashMap<String, String> fieldId = new HashMap<String, String>();
	HashMap<String, String> priority = new HashMap<String, String>();
	HashMap<String, String> scriptDescs = new HashMap<String, String>();
	HashMap<String, String> scriptItems = new HashMap<String, String>();
	List<CustomField> customFields = new ArrayList<CustomField>();
	HashMap<String, String> priorityList = new HashMap<String, String>();

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		action = null;
		delId = null;
		projectId = null;
	}

	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);

		return errors;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<CustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomField> customFields) {
		this.customFields = customFields;
	}

	public Integer getDelId() {
		return delId;
	}

	public HashMap<String, String> getFieldId() {
		return fieldId;
	}

	public void setFieldId(HashMap<String, String> fieldId) {
		this.fieldId = fieldId;
	}

	public void setId(Integer delId) {
		this.delId = delId;
	}

	public HashMap<String, String> getId() {
		return id;
	}

	public void setId(HashMap<String, String> id) {
		this.id = id;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public HashMap<String, String> getPriority() {
		return priority;
	}

	public void setPriority(HashMap<String, String> priority) {
		this.priority = priority;
	}

	public HashMap<String, String> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(HashMap<String, String> priorityList) {
		this.priorityList = priorityList;
	}

	public HashMap<String, String> getScriptDescs() {
		return scriptDescs;
	}

	public void setScriptDescs(HashMap<String, String> scriptDescs) {
		this.scriptDescs = scriptDescs;
	}

	public HashMap<String, String> getScriptItems() {
		return scriptItems;
	}

	public void setScriptItems(HashMap<String, String> scriptItems) {
		this.scriptItems = scriptItems;
	}
}
