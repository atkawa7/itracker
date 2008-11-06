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

package org.itracker.services.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.NameValuePair;
import org.itracker.model.ProjectScript;
import org.itracker.services.exceptions.WorkflowException;

import bsh.Interpreter;

/**
 * Contains utilities used when displaying and processing workflow and field events
 */
public class WorkflowUtilities  {
    
    /** Fires for each field when building the form.  Mainly used to build dynamic list options. */
    public static final int EVENT_FIELD_ONPOPULATE = 1;
    /** NOT CURRENTLY IMPLEMENTED.  Use the onPopulate event instead. In the future, this event may be implemented to allow for list sorting after list value population. */
    public static final int EVENT_FIELD_ONSORT = 2;
    /** Fires to set the current value of a form field.  This will overwrite any data in the form field pulled from the database. */
    public static final int EVENT_FIELD_ONSETDEFAULT = 3;
    /** Fires on validation of the form field. */
    public static final int EVENT_FIELD_ONVALIDATE = 4;
    /** Fires after validation, but before the data is committed to the database. */
    public static final int EVENT_FIELD_ONPRESUBMIT = 5;
    /** Fires after all data is submitted to the db for all fields. Performed right before the response is sent. */
    public static final int EVENT_FIELD_ONPOSTSUBMIT = 6;
    
    private static final Logger logger = Logger.getLogger(WorkflowUtilities.class);
    
    public WorkflowUtilities() {
    }
    
    /**
     * Returns a title of workflow event, according to selected locale.
     * @param value is an identifier of incoming event.
     * @param locale is a selected locale.
     * @return a name of event or something like "MISSING KEY: <resourceBundleKey>".
     */
    public static String getEventName(int value, Locale locale) {
        final String eventName = getEventName(Integer.toString(value), locale);
//        assert null != eventName : "event name should never be null.";
        return eventName;
    }
    
    public static String getEventName(String value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_WORKFLOW_EVENT + value, locale);
    }
    
    public static NameValuePair[] getEvents(Locale locale) {
        NameValuePair[] eventNames = new NameValuePair[6];
        eventNames[0] = new NameValuePair(getEventName(EVENT_FIELD_ONPOPULATE, locale), Integer.toString(EVENT_FIELD_ONPOPULATE));
        eventNames[1] = new NameValuePair(getEventName(EVENT_FIELD_ONSORT, locale), Integer.toString(EVENT_FIELD_ONSORT));
        eventNames[2] = new NameValuePair(getEventName(EVENT_FIELD_ONSETDEFAULT, locale), Integer.toString(EVENT_FIELD_ONSETDEFAULT));
        eventNames[3] = new NameValuePair(getEventName(EVENT_FIELD_ONVALIDATE, locale), Integer.toString(EVENT_FIELD_ONVALIDATE));
        eventNames[4] = new NameValuePair(getEventName(EVENT_FIELD_ONPRESUBMIT, locale), Integer.toString(EVENT_FIELD_ONPRESUBMIT));
        eventNames[5] = new NameValuePair(getEventName(EVENT_FIELD_ONPOSTSUBMIT, locale), Integer.toString(EVENT_FIELD_ONPOSTSUBMIT));
        return eventNames;
    }
    
    public static List<NameValuePair> getListOptions(Map<Integer, List<NameValuePair>> listOptions, int fieldId) {
        return getListOptions(listOptions, Integer.valueOf(fieldId));
    }
    
    @SuppressWarnings("unchecked")
    public static List<NameValuePair> getListOptions(Map listOptions, Integer fieldId) {
        List<NameValuePair> options = new ArrayList<NameValuePair>();
        
        if(listOptions != null && listOptions.size() != 0 && fieldId != null) {
            Object mapOptions = listOptions.get(fieldId);
            if(mapOptions != null) {
                options = (List<NameValuePair>) mapOptions;
            }
        }
        
        return options;
    }
    
    public static void processFieldScripts(List<ProjectScript> projectScriptModels, int event, Map<Integer, List<NameValuePair>> currentValues, ActionMessages currentErrors, ValidatorForm form) throws WorkflowException {
        if(projectScriptModels == null || projectScriptModels.size() == 0) {
            return;
        }
        logger.debug("Processing " + projectScriptModels.size() + " field scripts for project " + projectScriptModels.get(0).getProject().getId());
        
        List<ProjectScript> scriptsToRun = new ArrayList<ProjectScript>();
        for(int i = 0; i < projectScriptModels.size(); i++) {
            if(projectScriptModels.get(i).getScript().getEvent() == event) {
                int insertIndex = 0;
                for(insertIndex = 0; insertIndex < scriptsToRun.size(); insertIndex++) {
                    if(projectScriptModels.get(i).getPriority() < ((ProjectScript) scriptsToRun.get(insertIndex)).getPriority()) {
                        break;
                    }
                }
                scriptsToRun.add(insertIndex,projectScriptModels.get(i));
            }
        }
        logger.debug(scriptsToRun.size() + " eligible scripts found for event " + event);
        
        if (currentValues == null) {
            currentValues = new HashMap<Integer, List<NameValuePair>>();
        }
        
        for (int i = 0; i < scriptsToRun.size(); i++) {
            ProjectScript currentScript = (ProjectScript) scriptsToRun.get(i);
            try {
                logger.debug("Running script " + currentScript.getScript().getId() + " with priority " + currentScript.getPriority());
                List<NameValuePair> currentValue = currentValues.get(currentScript.getFieldId());
                
                logger.debug("Before script current value for field " + IssueUtilities.getFieldName(currentScript.getFieldId()) + " (" + currentScript.getFieldId() + ") is " + (currentValue == null ? "NULL" : "'" + currentValue.toString() + "' (" + currentValue.getClass().getName() + "'"));
                currentValue = processFieldScript(currentScript, event, currentScript.getFieldId(), currentValue, currentErrors, form);
                logger.debug("After script current value for field " + IssueUtilities.getFieldName(currentScript.getFieldId()) + " (" + currentScript.getFieldId() + ") is " + (currentValue == null ? "NULL" : "'" + currentValue.toString() + "' (" + currentValue.getClass().getName() + "'"));
                
                currentValues.put(currentScript.getFieldId(), currentValue);
            } catch(WorkflowException we) {
                logger.error("Error processing script " + currentScript.getScript().getId() + ": " + we.getMessage());
            }
        }
    }
    
    public static List<NameValuePair> processFieldScripts(List<ProjectScript> projectScripts, int event, Integer fieldId, List<NameValuePair> currentValue, ActionErrors currentErrors, ValidatorForm form) throws WorkflowException {
        if(projectScripts == null || projectScripts.size() == 0 || fieldId == null) {
            return null;
        }
        logger.debug("Processing " + projectScripts.size() + " field scripts for project " + projectScripts.get(0).getProject().getId());
        
        List<ProjectScript> scriptsToRun = new LinkedList<ProjectScript>();
        for(int i = 0; i < projectScripts.size(); i++) {
            if(projectScripts.get(i).getScript().getEvent() == event && fieldId.equals(projectScripts.get(i).getFieldId())) {
                int insertIndex = 0;
                for(insertIndex = 0; insertIndex < scriptsToRun.size(); insertIndex++) {
                    if(projectScripts.get(i).getPriority() < ((ProjectScript) scriptsToRun.get(insertIndex)).getPriority()) {
                        break;
                    }
                }
                scriptsToRun.add(insertIndex,projectScripts.get(i));
            }
        }
        logger.debug(scriptsToRun.size() + " eligible scripts found for event " + event + " on field " + fieldId);
        
        for(int i = 0; i < scriptsToRun.size(); i++) {
            ProjectScript currentScript = (ProjectScript) scriptsToRun.get(i);
            try {
                logger.debug("Running script " + currentScript.getScript().getId() + " with priority " + currentScript.getPriority());
                currentValue = processFieldScript(currentScript, event, fieldId, currentValue, currentErrors, form);
            } catch(WorkflowException we) {
                logger.error("Error processing script " + currentScript.getScript().getId() + ": " + we.getMessage());
            }
        }
        
        return currentValue;
    }
    
    @SuppressWarnings("unchecked")
    public static List<NameValuePair> processFieldScript(ProjectScript projectScript, int event, Integer fieldId, List<NameValuePair> currentValue, ActionMessages currentErrors, ValidatorForm form) throws WorkflowException {
        if(projectScript == null) {
            throw new WorkflowException("ProjectScript was null.", WorkflowException.INVALID_ARGS);
        }
        
        if(currentErrors == null) {
            currentErrors = new ActionMessages();
        }
        
        try {
            Interpreter bshInterpreter = new Interpreter();
            bshInterpreter.set("event", event);
            bshInterpreter.set("fieldId", fieldId);
            bshInterpreter.set("currentValue", currentValue);
            bshInterpreter.set("currentErrors", currentErrors);
            bshInterpreter.set("currentForm", form);
            bshInterpreter.eval(projectScript.getScript().getScript());
            currentValue = (List<NameValuePair>)bshInterpreter.get("currentValue");
            logger.debug("Script returned current value of '" + currentValue + "' (" + (currentValue != null ? currentValue.getClass().getName() : "NULL") + ")");
            if(event == EVENT_FIELD_ONSETDEFAULT && form != null && currentValue != null) {
                logger.debug("Setting current form field value for field " + IssueUtilities.getFieldName(projectScript.getFieldId()) + " to '" + currentValue + "'");
                setFormProperty(form, projectScript.getFieldId(), currentValue);
            }
        } catch(Exception e) {
            logger.error("Error processing field script.", e);
        }
        
        return currentValue;
    }
    
    @SuppressWarnings("unchecked")
    private static void setFormProperty(ValidatorForm form, Integer fieldId, Object currentValue) {
        String fieldName = IssueUtilities.getFieldName(fieldId);
        int fieldType = IssueUtilities.getFieldType(fieldId);
        if(fieldType == IssueUtilities.FIELD_TYPE_SINGLE) {
            try {
                Field formField = form.getClass().getField( fieldName );
                if ( formField != null ) {
                    formField.set( form , currentValue );
                } else {
                    throw new IllegalArgumentException( "no field with name "
                            + fieldName + " found in form " + form );
                }
            } catch ( NoSuchFieldException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        } else if(fieldType == IssueUtilities.FIELD_TYPE_INDEXED) {
            try {
                Object indexedField = null;
                Field formField = form.getClass().getField(fieldName);
                indexedField = formField.get( form );
                if ( indexedField instanceof List ) {
                    ((List)indexedField).set( 0 , currentValue );
                } else if ( indexedField instanceof Collection ) {
                    ((Collection)indexedField).add( currentValue );
                } else {
                    throw new IllegalArgumentException( "field with name "
                            + fieldName + " found in form " + form + " is of unknown type" );
                }
            } catch ( NoSuchFieldException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        } else if(fieldType == IssueUtilities.FIELD_TYPE_MAP) {
            try {
                Object indexedField = null;
                Field formField = form.getClass().getField( fieldName );
                indexedField = formField.get( form );
                if ( indexedField instanceof Map ) {
                    ((Map)indexedField).put( fieldId.toString(), currentValue );
                } else {
                    throw new IllegalArgumentException( "field with name "
                            + fieldName + " found in form " + form + " is of unknown type" );
                }
            } catch ( NoSuchFieldException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }
    }
    
}
