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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.web.actions.admin.project.EditProjectFormActionUtil;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class ProjectForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String action;
    private Integer id;
    private String name;
    private Integer status;
    private String description;
    private Integer[] owners;
    private Integer[] users;
    private Integer[] permissions;
    private Integer[] options;
    private Integer[] fields;

    private static final Logger log = Logger.getLogger(ProjectForm.class);

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        action = null;
        id = null;
        name = null;
        status = null;
        description = null;
        owners = null;
        users = null;
        permissions = null;
        options = null;
        fields = null;

    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        if (log.isDebugEnabled()) {
            log.debug("ProjectForm validate called: mapping: " + mapping
                    + ", request: " + request + ", errors: " + errors);
        }
        if (ServletContextUtils.getItrackerServices().getProjectService()
                .isUniqueProjectName(getName(), getId())) {
        } else {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.project.duplicate.name"));
            // throw new ProjectException(
            // "Project already exist with this name.");
        }

        new EditProjectFormActionUtil().init(mapping, request, this);
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
        if (null == fields)
            return null;
        return fields.clone();
    }

    public void setFields(Integer[] fields) {
        if (null == fields)
            this.fields = null;
        else
            this.fields = fields.clone();
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
        if (null == options)
            return null;
        return options.clone();
    }

    public void setOptions(Integer[] options) {
        if (null == options)
            this.options = null;
        else
            this.options = options.clone();
    }

    public Integer[] getOwners() {
        if (null == owners)
            return null;
        return owners.clone();
    }

    public void setOwners(Integer[] owners) {
        if (null == owners)
            this.owners = null;
        else
            this.owners = owners.clone();
    }

    public Integer[] getPermissions() {
        if (null == permissions)
            return null;

        return permissions.clone();

    }

    public void setPermissions(Integer[] permissions) {
        if (null == permissions)
            this.permissions = null;
        else
            this.permissions = permissions.clone();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer[] getUsers() {
        if (null == users)
            return null;
        return users.clone();
    }

    public void setUsers(Integer[] users) {
        if (null == users)
            this.users = null;
        else
            this.users = users.clone();
    }

}
