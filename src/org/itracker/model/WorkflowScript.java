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

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * 
 * @author ready
 */
public class WorkflowScript extends AbstractEntity {
    
    private String name;
    
    private String script;
    
    private int event;
    
    // TODO: what's the expected type here? 
//    private Collection projectFields;
    private int numUses;
    
    public int getEvent() {
        return event;
    }
    
    public void setEvent(int event) {
        this.event = event;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
/*
    public Collection getProjectFields() {
        return projectFields;
    }
    
    public void setProjectFields(Collection projectFields) {
        this.projectFields = projectFields;
    }
*/    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public int getNumberUses() {
        return numUses;
    }
    
    public void setNumberUses(int value) {
        numUses = value;
    }
    
}
