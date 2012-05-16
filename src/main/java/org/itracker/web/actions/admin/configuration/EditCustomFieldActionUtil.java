package org.itracker.web.actions.admin.configuration;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.NameValuePair;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.web.forms.CustomFieldForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public class EditCustomFieldActionUtil {

    private static final Logger logger = Logger.getLogger(EditCustomFieldActionUtil.class);

    public static final void setRequestEnv(HttpServletRequest request, CustomFieldForm customFieldForm) {

        ConfigurationService configurationService = ServletContextUtils
                .getItrackerServices().getConfigurationService();
        Locale currentLocale = LoginUtilities.getCurrentLocale(request);
        CustomField customField = (CustomField) request.getSession().getAttribute(Constants.CUSTOMFIELD_KEY);

        Map<String, List<String>> languages_map = configurationService.getAvailableLanguages();
        String[] languagesArray = new String[languages_map.size()];
        int idx = 0;
        // TODO: there is some bugs around here still, needs debugging. See
        // jsp error output.
        for (Iterator<String> iter = languages_map.keySet().iterator(); iter.hasNext(); ) {
            String language = (String) iter.next();
            // TODO: the following two lines have not been used, commented,
            // task added
            // String languageKey = "translations(" + language + ")";
            // Vector locales = (Vector) languages_map.get(language);
            languagesArray[idx] = language;
            idx++;
        }

        String pageTitleKey = "itracker.web.admin.editcustomfield.title.create";
        String pageTitleArg = "";

        String action = customFieldForm.getAction();

        if ("update".equals(action)) {
            pageTitleKey = "itracker.web.admin.editcustomfield.title.update";
            pageTitleArg = ITrackerResources.getString(CustomFieldUtilities.getCustomFieldLabelKey(customField.getId()));
        }
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        request.setAttribute("customFieldForm", customFieldForm);
        request.setAttribute("languages", languagesArray);
        request.setAttribute("action", action);

        Map<String, List<String>> languages = configurationService.getAvailableLanguages();
        Map<NameValuePair, List<NameValuePair>> languagesNameValuePair = new HashMap<NameValuePair, List<NameValuePair>>();
        for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
            String language = entry.getKey();
            List<String> locales = entry.getValue();
            List<NameValuePair> localesNameValuePair = new ArrayList<NameValuePair>();
            for (String locale : locales) {
                NameValuePair localeNameValuePair = new NameValuePair(locale, ITrackerResources.getString("itracker.locale.name", locale));
                localesNameValuePair.add(localeNameValuePair);
            }
            NameValuePair languageNameValuePair = new NameValuePair(language, ITrackerResources.getString("itracker.locale.name", language));
            languagesNameValuePair.put(languageNameValuePair, localesNameValuePair);
        }
//		request.setAttribute("sc", configurationService);
        HttpSession session = request.getSession();
        String baseLocaleKey = "translations(" + ITrackerResources.BASE_LOCALE + ")";

        List<CustomFieldValue> options = customField.getOptions();

        Collections.sort(options, CustomFieldValue.SORT_ORDER_COMPARATOR);
        if (logger.isDebugEnabled()) {
            logger.debug("setRequestEnv: sorted values by sort order comparator: " + options);
        }

        HashMap<Integer, String> optionsMap = new HashMap<Integer, String>();
        for (CustomFieldValue option : options) {
            String optionName = CustomFieldUtilities.getCustomFieldOptionName(customField.getId(), option.getId(), currentLocale);
            optionsMap.put(option.getId(), optionName);
        }

        String fieldTypeString = Integer.toString(CustomField.Type.STRING.getCode());
        String fieldTypeInteger = Integer.toString(CustomField.Type.INTEGER.getCode());
        String fieldTypeDate = Integer.toString(CustomField.Type.DATE.getCode());
        String fieldTypeList = Integer.toString(CustomField.Type.LIST.getCode());

        request.setAttribute("fieldTypeString", fieldTypeString);
        request.setAttribute("fieldTypeInteger", fieldTypeInteger);
        request.setAttribute("fieldTypeDate", fieldTypeDate);
        request.setAttribute("fieldTypeList", fieldTypeList);
        String dateFormatDateOnly = CustomFieldUtilities.DATE_FORMAT_DATEONLY;
        String dateFormatTimeOnly = CustomFieldUtilities.DATE_FORMAT_TIMEONLY;
        String dateFormatFull = CustomFieldUtilities.DATE_FORMAT_FULL;

        request.setAttribute("dateFormatDateOnly", dateFormatDateOnly);
        request.setAttribute("dateFormatTimeOnly", dateFormatTimeOnly);
        request.setAttribute("dateFormatFull", dateFormatFull);

        session.setAttribute("CustomFieldType_List", Integer.toString(CustomField.Type.LIST.getCode()));
        request.setAttribute("baseLocaleKey", baseLocaleKey);
        request.setAttribute("field", customField);
        request.setAttribute("languagesNameValuePair", languagesNameValuePair);
        request.setAttribute("options", options);
        request.setAttribute("optionsMap", optionsMap);

    }

}
