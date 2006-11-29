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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;



public class RemoveCustomFieldAction extends ItrackerBaseAction {

    public RemoveCustomFieldAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        if(! hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            Integer valueId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            if(valueId == null || valueId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid custom field id.");
            }

            CustomField customField = configurationService.getCustomField(valueId);
            if(customField == null) {
                throw new SystemConfigurationException("Invalid custom field id.");
            }

            if(customField.getFieldType() == CustomFieldUtilities.TYPE_LIST) {
                List<CustomFieldValue> options = customField.getOptions();
                for(int i = 0; i < options.size(); i++) {
                    String key = CustomFieldUtilities.getCustomFieldOptionLabelKey(customField.getId(), options.get(i).getId());
                    configurationService.removeCustomFieldValue(options.get(i).getId());
                    if(key != null) {
                        configurationService.removeLanguageKey(key);
                        ITrackerResources.clearKeyFromBundles(key, false);
                    }
                }
            }
            String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
            configurationService.removeCustomField(customField.getId());
            if(key != null) {
                configurationService.removeLanguageKey(key);
                ITrackerResources.clearKeyFromBundles(key, false);
            }

            configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_CUSTOMFIELD);

            return mapping.findForward("listconfiguration");
        } catch(SystemConfigurationException sce) {
            logger.debug(sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfield"));
        } catch(NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfield"));
            logger.debug("Invalid custom field  id " + request.getParameter("id") + " specified.");
        } catch(Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            logger.error("System Error.", e);
        }
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }

}
  