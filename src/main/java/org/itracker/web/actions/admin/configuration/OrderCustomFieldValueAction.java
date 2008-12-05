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

package org.itracker.web.actions.admin.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;



public class OrderCustomFieldValueAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(OrderCustomFieldValueAction.class);
	
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        
        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }
        
        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            
            Integer customFieldValueId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            if(customFieldValueId == null || customFieldValueId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid custom field value id.");
            }
            
            CustomFieldValue customFieldValue = configurationService.getCustomFieldValue(customFieldValueId);
            if(customFieldValue == null) {
                throw new SystemConfigurationException("Invalid custom field value id.");
            }
            
            CustomField customField = configurationService.getCustomField(customFieldValue.getCustomField().getId());
            if(customField == null) {
                throw new SystemConfigurationException("Invalid custom field id.");
            }
            List<CustomFieldValue> customFieldvalues = customField.getOptions();
            List<CustomFieldValue> newCustomFieldValueItems = new ArrayList<CustomFieldValue>();
            
            for(int i = 0; i < customFieldvalues.size(); i++) {
                newCustomFieldValueItems.add(customFieldvalues.get(i));
            }
            
            for(int i = 0; i < customFieldvalues.size(); i++) {
                if ( customFieldvalues.get(i) != null ) {
                    CustomFieldValue firstCustomFieldValue = new CustomFieldValue();
                    CustomFieldValue secondCustomFieldValue = new CustomFieldValue();
                    CustomFieldValue curCustomFieldValue = (CustomFieldValue) customFieldvalues.get(i);
                    int todo_i = -1;
                    if ( curCustomFieldValue.getId() == customFieldValueId ) {
                        if ("up".equals(action) ){
                            todo_i = i - 1;
                        } else {
                            todo_i = i + 1;
                        }
                        CustomFieldValue todoCustomFieldValue = (CustomFieldValue) customFieldvalues.get(todo_i);

                        firstCustomFieldValue.setId(curCustomFieldValue.getId());
                        firstCustomFieldValue.setCreateDate(todoCustomFieldValue.getCreateDate());
                        firstCustomFieldValue.setLastModifiedDate(todoCustomFieldValue.getLastModifiedDate());
//                        firstCustomFieldValue.setName(todoCustomFieldValue.getName());
                        firstCustomFieldValue.setValue(todoCustomFieldValue.getValue());
                        firstCustomFieldValue.setCustomField(todoCustomFieldValue.getCustomField());
                        firstCustomFieldValue.setSortOrder(todoCustomFieldValue.getSortOrder());
                        
                        secondCustomFieldValue.setId(todoCustomFieldValue.getId());
                        secondCustomFieldValue.setCreateDate(curCustomFieldValue.getCreateDate());
                        secondCustomFieldValue.setLastModifiedDate(curCustomFieldValue.getLastModifiedDate());
//                        secondCustomFieldValue.setName(curCustomFieldValue.getName());
                        secondCustomFieldValue.setValue(curCustomFieldValue.getValue());
                        secondCustomFieldValue.setCustomField(curCustomFieldValue.getCustomField());
                        secondCustomFieldValue.setSortOrder(curCustomFieldValue.getSortOrder());

                        newCustomFieldValueItems.set(todo_i,firstCustomFieldValue);
                        newCustomFieldValueItems.set(i,secondCustomFieldValue);
                    }
                }
            }
            
//            newCustomFieldValueItems = configurationService.updateCustomFieldValues(customField.getId(),newCustomFieldValueItems);
            
/*            for(int i = 0; i < values.size(); i++) {
                if(values.get(i) != null && valueId.equals(values.get(i).getId())) {
                    if("up".equalsIgnoreCase(action) && i > 0) {
                        int tempOrder = values.get(i).getSortOrder();
                        values.get(i).setSortOrder(values.get(i-1).getSortOrder());
                        values.get(i-1).setSortOrder(tempOrder);
                        values = configurationService.updateCustomFieldValues(customField.getId(), values);
                        if("up".equalsIgnoreCase(action) && i > 0) {
                        } else if("down".equalsIgnoreCase(action) && i < (values.size() - 1)) {
                            tempOrder = values.get(i).getSortOrder();
                            values.get(i).setSortOrder(values.get(i+1).getSortOrder());
                            values.get(i+1).setSortOrder(tempOrder);
                            values = configurationService.updateCustomFieldValues(customField.getId(), values);
                        }
                        break;
                    }
               if(values.get(i) != null && valueId.equals(values.get(i).getId())) {
                    if("up".equalsIgnoreCase(action) && i > 0) {
                        CustomFieldValue moveUpRecord = values.get(i);
                        int moveDwnInt = moveUpRecord.getId();
                        CustomFieldValue moveDwnRecord = values.get(i-1);
                        int moveUpInt = moveDwnRecord.getId();
                        moveDwnRecord.setId(moveDwnInt);
                        moveUpRecord.setId(moveUpInt);
                        values.set(i, moveDwnRecord);
                        values.set(i-1,moveUpRecord);
                        values = configurationService.updateCustomFieldValues(customField.getId(), values);
                    } else if("down".equalsIgnoreCase(action) && i < (values.size() - 1)) {
                        CustomFieldValue moveUpRecord = values.get(i+1);
                        int moveDwnInt = moveUpRecord.getId();
                        CustomFieldValue moveDwnRecord = values.get(i);
                        int moveUpInt = moveDwnRecord.getId();
                        moveDwnRecord.setId(moveDwnInt);
                        moveUpRecord.setId(moveUpInt);
                        values.set(i, moveDwnRecord);
                        values.set(i-1,moveUpRecord);
                        values = configurationService.updateCustomFieldValues(customField.getId(), values);
                    }
                    break;
                }
 
                }
            }
*/            
            configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_CUSTOMFIELD);
            request.setAttribute("action",action);
            return new ActionForward(mapping.findForward("editcustomfield").getPath() + "?id=" + customField.getId() + "&action=update");
        } catch(SystemConfigurationException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfieldvalue"));
            log.debug("Invalid custom field value id " + request.getParameter("id") + " specified.");
        } catch(NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfieldvalue"));
            log.debug("Invalid custom field value id " + request.getParameter("id") + " specified.");
        } catch(Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if(! errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }
    
}
