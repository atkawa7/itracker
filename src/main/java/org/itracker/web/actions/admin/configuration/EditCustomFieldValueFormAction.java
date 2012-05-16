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
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.model.NameValuePair;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.SystemConfigurationException;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldValueForm;
import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCustomFieldValueFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(EditCustomFieldValueFormAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        //  TODO: Action Cleanup

        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request,
                response)) {
            return mapping.findForward("unauthorized");
        }
        ConfigurationService configurationService = getITrackerServices()
                .getConfigurationService();

        try {

            // TODO: it looks like to following 3 lines can be removed, we
            // comment them and add a task.
            HttpSession session = request.getSession(true);
            Map<String, List<String>> languages = configurationService
                    .getAvailableLanguages();

            CustomFieldValueForm customFieldValueForm = (CustomFieldValueForm) form;

            if (customFieldValueForm == null) {
                customFieldValueForm = new CustomFieldValueForm();
            }

            CustomFieldValue customFieldValue = new CustomFieldValue();

            String action = customFieldValueForm.getAction();

            if ("update".equals(action)) {
                Integer id = customFieldValueForm.getId();
                customFieldValue = configurationService.getCustomFieldValue(id);
                if (customFieldValue == null) {
                    throw new SystemConfigurationException(
                            "Invalid custom field value id " + id);
                }
//				String name = CustomFieldUtilities.getCustomFieldOptionName(
//						customFieldValue.getCustomField().getId(), id);
//				customFieldValue.setName(name);

                customFieldValueForm.setId(id);
                customFieldValueForm.setValue(customFieldValue.getValue());

                customFieldValueForm.setSortOrder(customFieldValue.getSortOrder());

                HashMap<String, String> translations = new HashMap<String, String>();
                List<Language> languageItems = configurationService
                        .getLanguageItemsByKey(CustomFieldUtilities
                                .getCustomFieldOptionLabelKey(customFieldValue
                                        .getCustomField().getId(),
                                        customFieldValue.getId()));

                for (int i = 0; i < languageItems.size(); i++) {
                    translations.put(languageItems.get(i).getLocale(),
                            languageItems.get(i).getResourceValue());
                }
                customFieldValueForm.setTranslations(translations);
            }
            CustomField field = (CustomField) session
                    .getAttribute(Constants.CUSTOMFIELD_KEY);
            if (field == null) {
                return mapping.findForward("unauthorized");

            } else {
                String pageTitleKey = "";
                String pageTitleArg = "";
                pageTitleKey = "itracker.web.admin.editcustomfield.title.create";
                if (action == "update") {
                    pageTitleKey = "itracker.web.admin.editcustomfield.title.update";
                }

                request.setAttribute("languages", configurationService.getAvailableLanguages());
                request.setAttribute("pageTitleKey", pageTitleKey);
                request.setAttribute("pageTitleArg", pageTitleArg);

                request.setAttribute("languages", languages);
                request.setAttribute("customFieldValueForm", customFieldValueForm);
                request.setAttribute("action", action);
                session.setAttribute(Constants.CUSTOMFIELDVALUE_KEY,
                        customFieldValue);
                session.setAttribute("field", field);
                saveToken(request);
                setRequestEnvironment(request, configurationService);
                return mapping.getInputForward();
            }


        } catch (SystemConfigurationException sce) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidcustomfieldvalue"));
        } catch (Exception e) {
            log.error("Exception while creating edit custom field value form.",
                    e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            setRequestEnvironment(request, configurationService);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }

    public static void setRequestEnvironment(HttpServletRequest request, ConfigurationService configurationService) {
        Map<String, List<String>> languages = configurationService.getAvailableLanguages();
        Map<NameValuePair, List<NameValuePair>> languagesNameValuePair = new HashMap<NameValuePair, List<NameValuePair>>();
        for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
            String language = entry.getKey();
            List<String> locales = entry.getValue();
//			System.out.println(language);
//			System.out.println(locales);
            List<NameValuePair> localesNameValuePair = new ArrayList<NameValuePair>();
            for (String locale : locales) {
                NameValuePair localeNameValuePair = new NameValuePair(locale, ITrackerResources.getString("itracker.locale.name", locale));
                localesNameValuePair.add(localeNameValuePair);
            }
            NameValuePair languageNameValuePair = new NameValuePair(language, ITrackerResources.getString("itracker.locale.name", language));
            languagesNameValuePair.put(languageNameValuePair, localesNameValuePair);
        }
        request.setAttribute("languagesNameValuePair", languagesNameValuePair);
        request.setAttribute("baseLocale", ITrackerResources.BASE_LOCALE);
    }

}
