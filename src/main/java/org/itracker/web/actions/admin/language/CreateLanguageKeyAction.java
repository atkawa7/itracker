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

package org.itracker.web.actions.admin.language;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

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
import org.itracker.model.Language;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;



public class CreateLanguageKeyAction extends ItrackerBaseAction {

    public CreateLanguageKeyAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }
        if(! isTokenValid(request)) {
            logger.debug("Invalid request token while creating language key.");
            return mapping.findForward("listlanguages");
        }
        resetToken(request);

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            String key = (String) PropertyUtils.getSimpleProperty(form, "key");
            HashMap items = (HashMap) PropertyUtils.getSimpleProperty(form, "items");

            // Move to validation code
            if(items != null) {
                logger.debug("Adding new language key: " + key);
                for(Iterator iter = items.keySet().iterator(); iter.hasNext(); ) {
                    String locale = (String) iter.next();
                    logger.debug("Checking translation for locale " + locale);
                    if(locale != null) {
                        String value = (String) items.get(locale);
                        logger.debug("Locale value: " + value);
                        if(value != null && ! value.equals("")) {
                            logger.debug("Adding new translation for locale " + locale + " for key " + key);
                            configurationService.updateLanguageItem(new Language(locale, key, value));
                        }
                    }
                }
                String baseValue = (String) items.get(ITrackerResources.BASE_LOCALE);
                //if(baseValue == null || baseValue.equals("")) {
                //errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.required", ));
                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, key, baseValue));
                ITrackerResources.clearKeyFromBundles(key, true);
            }

            return mapping.findForward("listlanguages");
        } catch(Exception e) {
            logger.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }
        return mapping.findForward("error");
    }
}
  