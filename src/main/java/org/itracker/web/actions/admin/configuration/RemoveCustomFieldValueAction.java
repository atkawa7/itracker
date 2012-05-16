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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class RemoveCustomFieldValueAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(RemoveCustomFieldValueAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            Integer valueId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            if (valueId == null || valueId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid custom field value id.");
            }

            CustomFieldValue customFieldValue = configurationService.getCustomFieldValue(valueId);
            if (customFieldValue == null) {
                throw new SystemConfigurationException("Invalid custom field value id.");
            }

            String key = CustomFieldUtilities.getCustomFieldOptionLabelKey(customFieldValue.getCustomField().getId(), customFieldValue.getId());
            boolean status = configurationService.removeCustomFieldValue(customFieldValue.getId());

            if (status) {
                if (key != null) {
//                    status = configurationService.removeLanguageKey(key);
                    ITrackerResources.clearKeyFromBundles(key, false);
                }
                configurationService.resetConfigurationCache(SystemConfigurationUtilities.TYPE_CUSTOMFIELD);

                HttpSession session = request.getSession(true);
                CustomField customField = (CustomField) session.getAttribute(Constants.CUSTOMFIELD_KEY);
                if (customField == null) {
                    return mapping.findForward("listconfiguration");
                }
                return new ActionForward(mapping.findForward("editcustomfield").getPath() + "?id=" + customField.getId() + "&action=update");
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            }

        } catch (SystemConfigurationException sce) {
            log.debug(sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfieldvalue"));
        } catch (NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfieldvalue"));
            log.debug("Invalid custom field value id " + request.getParameter("id") + " specified.");
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
