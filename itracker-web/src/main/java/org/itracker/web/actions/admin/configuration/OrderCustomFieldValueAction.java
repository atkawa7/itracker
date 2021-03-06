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
import org.itracker.SystemConfigurationException;
import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OrderCustomFieldValueAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(OrderCustomFieldValueAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request,
                response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
                    .getConfigurationService();

            Integer customFieldValueId = (Integer) PropertyUtils
                    .getSimpleProperty(form, "id");
            String action = (String) PropertyUtils.getSimpleProperty(form,
                    "action");
            if (customFieldValueId == null
                    || customFieldValueId.intValue() <= 0) {
                throw new SystemConfigurationException(
                        "Invalid custom field value id.");
            }

            CustomFieldValue customFieldValue = configurationService
                    .getCustomFieldValue(customFieldValueId);
            if (customFieldValue == null) {
                throw new SystemConfigurationException(
                        "Invalid custom field value id.");
            }

            CustomField customField = configurationService
                    .getCustomField(customFieldValue.getCustomField().getId());
            if (customField == null) {
                throw new SystemConfigurationException(
                        "Invalid custom field id.");
            }
            List<CustomFieldValue> customFieldvalues = customField.getOptions();


            Collections.sort(customFieldvalues,
                    CustomFieldValue.SORT_ORDER_COMPARATOR);
            Iterator<CustomFieldValue> valuesIt = customFieldvalues.iterator();
            CustomFieldValue curCustomFieldValue, prevCustomFieldValue = null;
            int i = 0;
            while (valuesIt.hasNext()) {
                curCustomFieldValue = valuesIt.next();
                curCustomFieldValue.setSortOrder(i);

                if (curCustomFieldValue.getId() == customFieldValueId) {

                    if ("up".equals(action)) {
                        if (prevCustomFieldValue != null) {
                            curCustomFieldValue.setSortOrder(i - 1);
                            prevCustomFieldValue.setSortOrder(i);
                        }
                    } else {
                        CustomFieldValue value = valuesIt.next();
                        value.setSortOrder(i++);
                        curCustomFieldValue.setSortOrder(i);
                        curCustomFieldValue = value;
                    }

                }
                prevCustomFieldValue = curCustomFieldValue;
                i++;
            }

            configurationService.updateCustomField(customField);

            configurationService
                    .resetConfigurationCache(Configuration.Type.customfield);
            request.setAttribute("action", action);
            return new ActionForward(mapping.findForward("editcustomfield")
                    .getPath()
                    + "?id=" + customField.getId() + "&action=update");
        } catch (SystemConfigurationException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidcustomfieldvalue"));
            log.debug("Invalid custom field value id "
                    + request.getParameter("id") + " specified.");
        } catch (NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidcustomfieldvalue"));
            log.debug("Invalid custom field value id "
                    + request.getParameter("id") + " specified.");
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
