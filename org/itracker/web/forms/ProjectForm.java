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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 * @author ready
 *
 */
public class ProjectForm extends ValidatorForm  {
    
	String action;
    Integer id;
	String name;
	Integer status;
	String description;
	Integer[] owners;
	Integer[] users;
	Integer[] permissions;
	Integer[] options;
	Integer[] fields;
      
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		action = null;    
		id = null;
		name = null;
		status = null;
		description = null;
		owners	= null;
		users = null;
		permissions = null;
		options = null;
		fields = null;
		
     
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer[] getFields() {
		return fields;
	}

	public void setFields(Integer[] fields) {
		this.fields = fields;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer[] getOptions() {
		return options;
	}

	public void setOptions(Integer[] options) {
		this.options = options;
	}

	public Integer[] getOwners() {
		return owners;
	}

	public void setOwners(Integer[] owners) {
		this.owners = owners;
	}

	public Integer[] getPermissions() {
		return permissions;
	}

	public void setPermissions(Integer[] permissions) {
		this.permissions = permissions;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer[] getUsers() {
		return users;
	}

	public void setUsers(Integer[] users) {
		this.users = users;
	}


}
