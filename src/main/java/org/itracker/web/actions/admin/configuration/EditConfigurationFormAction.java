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

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.Language;
import org.itracker.model.NameValuePair;
import org.itracker.SystemConfigurationException;
import org.itracker.services.ConfigurationService;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ConfigurationForm;
import org.itracker.web.util.LoginUtilities;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class EditConfigurationFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(EditConfigurationFormAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (log.isDebugEnabled()) {
            log.debug("execute: called");
        }
        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request,
                response)) {
            log.warn("execute: user has not permissue user-admin: " + LoginUtilities.getCurrentUser(request)
                    + ", forwarding to unauthorized!");

            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices()
                    .getConfigurationService();

            ConfigurationForm configurationForm = (ConfigurationForm) form;
            if (configurationForm == null) {
                configurationForm = new ConfigurationForm();
            }

            String action = configurationForm.getAction();

            String formValue = configurationForm.getValue();

            if ("update".equals(action)) {
                Integer id = configurationForm.getId();
                formValue = String.valueOf(id);
                Configuration configItem = configurationService
                        .getConfigurationItem(id);

                if (configItem == null) {
                    throw new SystemConfigurationException(
                            "Invalid configuration item id " + id);
                }
                configurationForm.setId(id);
                configurationForm.setValue(configItem.getValue());
                configurationForm.setKey(SystemConfigurationUtilities
                                                .getLanguageKey(configItem));
                configurationForm.setTypeKey(SystemConfigurationUtilities.getTypeLanguageKey(configItem));
                configurationForm.setOrder(configItem.getOrder());

                HashMap<String, String> translations = new HashMap<String, String>();
                List<Language> languageItems = configurationService
                        .getLanguageItemsByKey(configurationForm.getKey());

                for (int i = 0; i < languageItems.size(); i++) {
                    translations.put(languageItems.get(i).getLocale(),
                            languageItems.get(i).getResourceValue());
                }
                configurationForm.setTranslations(translations);
            }
            Map<String, List<String>> languages = configurationService
                    .getAvailableLanguages();
            Map<NameValuePair, List<NameValuePair>> languagesNameValuePair = new HashMap<NameValuePair, List<NameValuePair>>();
            for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
                String language = entry.getKey();
                List<String> locales = entry.getValue();
                List<NameValuePair> localesNameValuePair = new ArrayList<NameValuePair>();
                for (String locale : locales) {
                    NameValuePair localeNameValuePair = new NameValuePair(
                            locale, ITrackerResources.getString(
                            "itracker.locale.name", locale));
                    localesNameValuePair.add(localeNameValuePair);
                }
                NameValuePair languageNameValuePair = new NameValuePair(
                        language, ITrackerResources.getString(
                        "itracker.locale.name", language));
                languagesNameValuePair.put(languageNameValuePair,
                        localesNameValuePair);
            }
            String baseLocaleKey = "translations("
                    + ITrackerResources.BASE_LOCALE + ")";

            String pageTitleKey = "";
            String pageTitleArg = "";
            boolean isUpdate = false;

            if (log.isDebugEnabled()) {
                log.debug("execute: action was "
                        + configurationForm.getAction());
            }
            if ("update".equals(configurationForm.getAction())) {
                isUpdate = true;
                pageTitleKey = "itracker.web.admin.editconfiguration.title.update";
            } else {
                Locale locale = getLocale(request);
                pageTitleKey = "itracker.web.admin.editconfiguration.title.create";
                if ("createseverity".equals(configurationForm.getAction())) {
                    pageTitleArg = ITrackerResources.getString(
                            "itracker.web.attr.severity", locale);
                } else if ("createstatus".equals(configurationForm.getAction())) {
                    pageTitleArg = ITrackerResources.getString(
                            "itracker.web.attr.status", locale);
                } else if ("createresolution".equals(configurationForm
                        .getAction())) {
                    pageTitleArg = ITrackerResources.getString(
                            "itracker.web.attr.resolution", locale);
                } else {
                    log.warn("execute: unrecognized action in form: "
                            + configurationForm.getAction());
                    return mapping.findForward("unauthorized");
                }
            }
            request.setAttribute("isUpdate", Boolean.valueOf(isUpdate));
            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);

            request.setAttribute("languagesNameValuePair",
                    languagesNameValuePair);
            request.setAttribute("sc", configurationService);
            request.setAttribute("configurationForm", configurationForm);
            request.setAttribute("action", action);
            request.setAttribute("value", formValue);
            request.setAttribute("baseLocaleKey", baseLocaleKey);
            saveToken(request);

            return mapping.getInputForward();
        } catch (SystemConfigurationException sce) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidconfiguration"));
        } catch (Exception e) {
            log.error("Exception while creating edit configuration form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }

}
