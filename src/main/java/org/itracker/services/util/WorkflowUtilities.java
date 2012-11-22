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

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.NameValuePair;
import org.itracker.model.ProjectScript;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.web.forms.IssueForm;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Contains utilities used when displaying and processing workflow and field events
 */
public class WorkflowUtilities {

    /**
     * Fires for each field when building the form.  Mainly used to build dynamic list options.
     */
    public static final int EVENT_FIELD_ONPOPULATE = 1;
    /**
     * NOT CURRENTLY IMPLEMENTED.  Use the onPopulate event instead. In the future, this event may be implemented to allow for list sorting after list value population.
     */
    public static final int EVENT_FIELD_ONSORT = 2;
    /**
     * Fires to set the current value of a form field.  This will overwrite any data in the form field pulled from the database.
     */
    public static final int EVENT_FIELD_ONSETDEFAULT = 3;
    /**
     * Fires on validation of the form field.
     */
    public static final int EVENT_FIELD_ONVALIDATE = 4;
    /**
     * Fires after validation, but before the data is committed to the database.
     */
    public static final int EVENT_FIELD_ONPRESUBMIT = 5;
    /**
     * Fires after all data is submitted to the db for all fields. Performed right before the response is sent.
     */
    public static final int EVENT_FIELD_ONPOSTSUBMIT = 6;

    private static final Logger logger = Logger.getLogger(WorkflowUtilities.class);

    public WorkflowUtilities() {
    }

    /**
     * Returns a title of workflow event, according to selected locale.
     *
     * @param value  is an identifier of incoming event.
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

    /**
     * Returns an array of pairs (eventName, eventId), where eventName
     * is an event title, according to selected locale.
     *
     * @param locale is a selected locale.
     * @return an array of pairs (eventName, eventId), which is never null.
     */
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

    /**
     * Select a list of NameValuePair objects from provided map object according
     * to fieldId selector. Typesafe version of #getListOptions(Map, Integer)
     *
     * @param listOptions is a map, with stored NameValuePair objects lists
     *                    associated with specific integer id.
     * @param fieldId     is a selector from map.
     * @return a list of objects, which may be empty, but never null.
     */
    public static List<NameValuePair> getListOptions(Map<Integer, List<NameValuePair>> listOptions, int fieldId) {
        return getListOptions(listOptions, Integer.valueOf(fieldId));
    }

    /**
     * Select a list of NameValuePair objects from provided map object according
     * to fieldId selector.
     *
     * @param listOptions is a map, with stored NameValuePair objects lists
     *                    associated with specific integer id.
     * @param fieldId     is a selector from map.
     * @return a list of objects, which may be empty, but never null.
     */
    @SuppressWarnings("unchecked")
    public static List<NameValuePair> getListOptions(Map listOptions, Integer fieldId) {
        List<NameValuePair> options = new ArrayList<NameValuePair>();

        if (listOptions != null && listOptions.size() != 0 && fieldId != null) {
            Object mapOptions = listOptions.get(fieldId);
            if (mapOptions != null) {
                options = (List<NameValuePair>) mapOptions;
            }
        }

        return options;
    }

    /**
     * The most general way to run scripts. All matching of event and fields
     * are embedded within. As a result, optionValues parameter will
     * contain updated values and form will contain new default values
     * if appropriate.
     *
     * @param projectScriptModels is a list of scripts.
     * @param event               is an event type.
     * @param currentValues       values mapped to field-ids
     * @param optionValues        is a map of current values to fields by field-Id.
     * @param currentErrors       is a container for errors.
     * @param form                contains default values of fields.
     */
    public static void processFieldScripts(List<ProjectScript> projectScriptModels, int event, Map<Integer, String> currentValues, Map<Integer, List<NameValuePair>> optionValues, ActionMessages currentErrors, IssueForm form) throws WorkflowException {
        if (projectScriptModels == null || projectScriptModels.size() == 0) {
            return;
        }
        logger.debug("Processing " + projectScriptModels.size() + " field scripts for project " + projectScriptModels.get(0).getProject().getId());

        List<ProjectScript> scriptsToRun = new ArrayList<ProjectScript>(projectScriptModels.size());
        for (ProjectScript model : projectScriptModels) {
            if (model.getScript().getEvent() == event) {
                scriptsToRun.add(model);
            }
        }
        // order the scripts by priority
        Collections.sort(scriptsToRun, ProjectScript.PRIORITY_COMPARATOR);

        if (logger.isDebugEnabled()) {
            logger.debug(scriptsToRun.size() + " eligible scripts found for event " + event);
        }

        String currentValue;
        for (ProjectScript currentScript : scriptsToRun) {
            try {
                currentValue = currentValues.get(currentScript.getFieldId());
                logger.debug("Running script " + currentScript.getScript().getId()
                        + " with priority " + currentScript.getPriority());

                logger.debug("Before script current value for field " + IssueUtilities.getFieldName(currentScript.getFieldId())
                        + " (" + currentScript.getFieldId() + ") is "
                        + currentValue + "'");

                List<NameValuePair> options = optionValues.get(currentScript.getFieldId());
                if (null == options) {
                    options = new ArrayList<NameValuePair>();
                    optionValues.put(currentScript.getFieldId(), options);
                }
                currentValue = processFieldScript(currentScript, event,
                        currentScript.getFieldId(),
                        currentValue,
                        options, currentErrors, form);
                currentValues.put( currentScript.getFieldId(), currentValue );

                logger.debug("After script current value for field " + IssueUtilities.getFieldName(currentScript.getFieldId())
                        + " (" + currentScript.getFieldId() + ") is "
                        + currentValue + "'");

            } catch (WorkflowException we) {
                logger.error("Error processing script ", we);
                currentErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", we.getMessage(), "Workflow"));
            }
        }

        for (Integer fieldId: currentValues.keySet()) {
            form.getCustomFields().put(String.valueOf(fieldId),
                    currentValues.get(fieldId));
        }
    }

    /**
     * Run appropriate script, selecting it from provided list by matching
     * event and field.
     *
     * @param projectScripts is a list of provided scripts.
     * @param event          is an event type.
     * @param fieldId        is a field, associated with event.
     * @param currentValue   the current value
     * @param optionValues   is a set of current values.
     * @param currentErrors  is a container for errors.
     * @param form           is a form, holder of default values.
     * @return new set of values.
     */
    public static String processFieldScripts(List<ProjectScript> projectScripts, int event, Integer fieldId, String currentValue, List<NameValuePair> optionValues, ActionErrors currentErrors, ValidatorForm form) throws WorkflowException {
        if (projectScripts == null || projectScripts.size() == 0 || fieldId == null) {
            return null;
        }
        logger.debug("Processing " + projectScripts.size() + " field scripts for project " + projectScripts.get(0).getProject().getId());

        List<ProjectScript> scriptsToRun = new LinkedList<ProjectScript>();
        for (int i = 0; i < projectScripts.size(); i++) {
            if (projectScripts.get(i).getScript().getEvent() == event && fieldId.equals(projectScripts.get(i).getFieldId())) {
                int insertIndex = 0;
                for (insertIndex = 0; insertIndex < scriptsToRun.size(); insertIndex++) {
                    if (projectScripts.get(i).getPriority() < ((ProjectScript) scriptsToRun.get(insertIndex)).getPriority()) {
                        break;
                    }
                }
                scriptsToRun.add(insertIndex, projectScripts.get(i));
            }
        }
        logger.debug(scriptsToRun.size() + " eligible scripts found for event " + event + " on field " + fieldId);

        String result = currentValue;
        for (int i = 0; i < scriptsToRun.size(); i++) {
            ProjectScript currentScript = (ProjectScript) scriptsToRun.get(i);
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Running script " + currentScript.getScript().getId() + " with priority "
                            + currentScript.getPriority());
                }
                result = processFieldScript(currentScript, event, fieldId,
                        result, optionValues, currentErrors, form);
            } catch (WorkflowException we) {
                logger.error("Error processing script " + currentScript.getScript().getId() + ": " + we.getMessage());
            }
        }

        return result;
    }

    /**
     * Run provided BEANSHELL script against form instance, taking into account
     * incoming event type, field raised an event and current values.
     * As a result, a set of new current values is returned and if
     * appropriate, default values are changed in form.
     * TODO: should issue, project, user, services be available too?
     *
     * @param projectScript is a script to run.
     * @param event         is an event type.
     * @param fieldId       is a field id associated with event.
     * @param currentValue  the current field value
     * @param optionValues  is a set of valid option-values.
     * @param currentErrors is a container for occured errors.
     * @param form          is a form instance, holding values.
     * @return new changed currentValue.
     */
    public static String processFieldScript(ProjectScript projectScript, int event, Integer fieldId, String currentValue, List<NameValuePair> optionValues, ActionMessages currentErrors, ValidatorForm form) throws WorkflowException {
        if (projectScript == null) {
            throw new WorkflowException("ProjectScript was null.", WorkflowException.INVALID_ARGS);
        }
        if (currentErrors == null) {
            throw new WorkflowException("Errors was null.", WorkflowException.INVALID_ARGS);
        }

        String result = "";

        try {
            Interpreter bshInterpreter = new Interpreter();
            bshInterpreter.set("event", event);
            bshInterpreter.set("fieldId", fieldId);
            currentValue = StringUtils.defaultString(currentValue);
            bshInterpreter.set("currentValue", currentValue);
            bshInterpreter.set("optionValues", optionValues);
            bshInterpreter.set("currentErrors", currentErrors);
            bshInterpreter.set("currentForm", form);

            bshInterpreter.eval(projectScript.getScript().getScript());

            result = String.valueOf(bshInterpreter.get("currentValue"));
            if (logger.isDebugEnabled()) {
                logger.debug("processFieldScript: Script returned current value of '" + optionValues + "' (" + (optionValues != null ? optionValues.getClass().getName() : "NULL") + ")");
            }
            if (event == EVENT_FIELD_ONSETDEFAULT && form != null && optionValues != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("processFieldScript: Setting current form field value for field " + IssueUtilities.getFieldName(projectScript.getFieldId()) + " to '" + optionValues + "'");
                }
                setFormProperty(form, projectScript.getFieldId(), result);
            }
        } catch (EvalError evalError) {
            logger.error("processFieldScript: eval failed: " + projectScript, evalError);
            currentErrors.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("itracker.web.error.invalidscriptdata", evalError.getMessage()));
        } catch (RuntimeException e) {
            logger.warn("processFieldScript: Error processing field script: " + projectScript, e);
            currentErrors.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("itracker.web.error.system.message",
                            new Object[]{ITrackerResources.getString("itracker.web.attr.script"), // Script
                                    e.getMessage()
                            }));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("processFieldScript: returning " + result + ", errors: " + currentErrors);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void setFormProperty(ValidatorForm form, Integer fieldId, Object currentValue) {
        String fieldName = IssueUtilities.getFieldName(fieldId);
        int fieldType = IssueUtilities.getFieldType(fieldId);
        if (fieldType == IssueUtilities.FIELD_TYPE_SINGLE) {
            try {
                Field formField = form.getClass().getField(fieldName);
                if (formField != null) {
                    formField.set(form, currentValue);
                } else {
                    throw new IllegalArgumentException("no field with name "
                            + fieldName + " found in form " + form);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (fieldType == IssueUtilities.FIELD_TYPE_INDEXED) {
            try {
                Object indexedField = null;
                Field formField = form.getClass().getField(fieldName);
                indexedField = formField.get(form);
                if (indexedField instanceof List) {
                    ((List) indexedField).set(0, currentValue);
                } else if (indexedField instanceof Collection) {
                    ((Collection) indexedField).add(currentValue);
                } else {
                    throw new IllegalArgumentException("field with name "
                            + fieldName + " found in form " + form + " is of unknown type");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (fieldType == IssueUtilities.FIELD_TYPE_MAP) {
            try {
                Object indexedField = null;
                Field formField = form.getClass().getField(fieldName);
                indexedField = formField.get(form);
                if (indexedField instanceof Map) {
                    ((Map) indexedField).put(fieldId.toString(), currentValue);
                } else {
                    throw new IllegalArgumentException("field with name "
                            + fieldName + " found in form " + form + " is of unknown type");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
