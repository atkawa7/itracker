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
public class CustomFieldValueForm extends ValidatorForm  {
    
    private String action;
    
    private Integer id;
    
    private String value;
    
    
    //  let's try to put String,String here:
    HashMap<String,String> translations = new HashMap<String,String>();
//    private Map<String, String> translations;
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    //  let's try to put String,String here:
    public HashMap<String, String> getTranslations() {
        return translations;
    }
    
    //  let's try to put String,String here:
    public void setTranslations(HashMap<String, String> translations) {
        this.translations = translations;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
    }
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        return errors;
    }
    
}