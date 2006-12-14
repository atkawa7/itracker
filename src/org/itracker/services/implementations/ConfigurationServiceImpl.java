/* * This software was designed and created by Jason Carroll. * Copyright (c) 2002, 2003, 2004 Jason Carroll. * The author can be reached at jcarroll@cowsultants.com * ITracker website: http://www.cowsultants.com * ITracker forums: http://www.cowsultants.com/phpBB/index.php * * This program is free software; you can redistribute it and/or modify * it only under the terms of the GNU General Public License as published by * the Free Software Foundation; either version 2 of the License, or * (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. */package org.itracker.services.implementations;import java.io.FileNotFoundException;import java.io.IOException;import java.io.InputStream;import java.sql.Timestamp;import java.util.ArrayList;import java.util.Arrays;import java.util.Collection;import java.util.Collections;import java.util.Date;import java.util.Enumeration;import java.util.HashMap;import java.util.Iterator;import java.util.List;import java.util.Locale;import java.util.Properties;import org.apache.log4j.Logger;import org.itracker.core.resources.ITrackerResources;import org.itracker.model.Configuration;import org.itracker.model.CustomField;import org.itracker.model.CustomFieldValue;import org.itracker.model.Language;import org.itracker.model.NameValuePair;import org.itracker.model.SystemConfiguration;import org.itracker.model.WorkflowScript;import org.itracker.model.util.PropertiesFileHandler;import org.itracker.persistence.dao.ConfigurationDAO;import org.itracker.persistence.dao.CustomFieldDAO;import org.itracker.persistence.dao.CustomFieldValueDAO;import org.itracker.persistence.dao.LanguageDAO;import org.itracker.persistence.dao.NoSuchEntityException;import org.itracker.persistence.dao.WorkflowScriptDAO;import org.itracker.services.ConfigurationService;import org.itracker.services.exceptions.SystemConfigurationException;import org.itracker.services.util.IssueUtilities;import org.itracker.services.util.SystemConfigurationUtilities;/** *  * Implimetation of the ConfigurationService Interface. *  * @see ConfigurationService *  */public class ConfigurationServiceImpl implements ConfigurationService {    private final Logger logger;    private final Properties props;    private ConfigurationDAO configurationDAO;    private CustomFieldDAO customFieldDAO;    private CustomFieldValueDAO customFieldValueDAO;    private LanguageDAO languageDAO;    private WorkflowScriptDAO workflowScriptDAO;    public ConfigurationServiceImpl() throws IOException {        this.logger = Logger.getLogger(getClass());        this.props = new Properties();        loadProperties("/itrackerApplication.properties");        loadProperties("/itrackerVersion.properties");        props.setProperty("start_time_millis", Long.toString(new java.util.Date().getTime()));    }    /**     * Load a properties file resource into the given Properties object.      *      * @param resourcePath     */    private void loadProperties(String resourcePath) throws IOException {        InputStream inputStream = getClass().getResourceAsStream(resourcePath);                if (inputStream == null) {           throw new FileNotFoundException("Resource '" + resourcePath + "'");        }                try {            this.props.load(inputStream);        } finally {            inputStream.close();        }    }        public String getProperty(String name) {        return props.getProperty(name);    }    public String getProperty(String name, String defaultValue) {        return props.getProperty(name, defaultValue);    }    public boolean getBooleanProperty(String name, boolean defaultValue) {        String value = props.getProperty(name);        return (value == null ? defaultValue : new Boolean(value).booleanValue());    }    public int getIntegerProperty(String name, int defaultValue) {        String value = props.getProperty(name);        try {            return (value == null) ? defaultValue : Integer.parseInt(value);        } catch (NumberFormatException ex) {            return defaultValue;        }    }    public long getLongProperty(String name, long defaultValue) {        String value = props.getProperty(name);        try {            return (value == null) ? defaultValue : Long.parseLong(value);        } catch (NumberFormatException ex) {            return defaultValue;        }    }    public Properties getProperties() {        return props;    }    public Configuration getConfigurationItem(Integer id) {        Configuration configItem = configurationDAO.findByPrimaryKey(id);        return configItem;    }    public List<Configuration> getConfigurationItemsByType(int type) {        List<Configuration> configItems = configurationDAO.findByType(type);        return configItems;    }    public List<Configuration> getConfigurationItemsByType(int type, Locale locale) {        List<Configuration> items = getConfigurationItemsByType(type);        for (int i = 0; i < items.size(); i++) {            if (items.get(i).getType() == SystemConfigurationUtilities.TYPE_STATUS) {                items.get(i).setName(IssueUtilities.getStatusName(items.get(i).getValue(), locale));            } else if (items.get(i).getType() == SystemConfigurationUtilities.TYPE_SEVERITY) {                items.get(i).setName(IssueUtilities.getSeverityName(items.get(i).getValue(), locale));            } else if (items.get(i).getType() == SystemConfigurationUtilities.TYPE_RESOLUTION) {                items.get(i).setName(IssueUtilities.getResolutionName(items.get(i).getValue(), locale));            }        }        return items;    }    /**     * Creates a <code>Configuration</code>.     *     * @param model The <code>Configuration</code> to store     * @return the <code>Configuration</code> after saving     * @todo replace hardcoded version by a resource     */    public Configuration createConfigurationItem(Configuration configuration) {        Configuration configurationItem = new Configuration();        configurationItem.setType( configuration.getType() );        configurationItem.setOrder( configuration.getOrder() );        configurationItem.setValue( configuration.getValue() );        configurationItem.setCreateDate(new Date());        configurationItem.setVersion( this.getProperty("version") );        configurationDAO.saveOrUpdate(configurationItem);        return configurationItem;    }    /**     * Updates a <code>ConfigurationItem</code>     *     * @param model The model containing the data      * @return the <code>Configuration</code> after save     */    public Configuration updateConfigurationItem(Configuration configuration) {        // find item by primary key        Configuration configurationItem = configurationDAO.findByPrimaryKey(configuration.getId());        // update now        configurationDAO.saveOrUpdate( configurationItem );        // get model from saved item        return configurationItem;    }    /**     * Updates the configuration items     *     * @param models the <code>ConfigurationModels</code> to update     * @param  type The type of the <code>ConfigurationItem</code>s to update     * @return an array with the saved models     */    public List<Configuration> updateConfigurationItems(List<Configuration> configurations, int type) {        // remove all items        removeConfigurationItems(type);        for (int i = 0; i < configurations.size(); i++) {            // create a new item            Configuration configurationItem = new Configuration();            // set creation date             configurationItem.setCreateDate(new Date());            // save or update            this.configurationDAO.saveOrUpdate( configurationItem );        }        // sort array        Collections.sort(configurations, new Configuration.ConfigurationComparator());        return configurations;    }    /**     * Finds the <code>Configuration</code> by primary key <code>id<code>     * and deletes it.     *     * @param id The id of the <code>COnfigurationBean</code> to remove     */    public void removeConfigurationItem(Integer id) {        Configuration configBean = this.configurationDAO.findByPrimaryKey(id);        if ( configBean != null ) {            this.configurationDAO.delete( configBean );        }    }    /**     * Removes all <code>Configuration</code>s of the give <code>type</code>     *     * @param type the type of <code>Configuration</code> to remove     */    public void removeConfigurationItems(int type) {        // find the configuration beans by its type        Collection<Configuration> currentItems = configurationDAO.findByType(type);        for (Iterator<Configuration> iter = currentItems.iterator(); iter.hasNext();) {            // get current config bean            Configuration config = (Configuration) iter.next();            // delete it            this.configurationDAO.delete( config );        }    }    public void removeConfigurationItems(Configuration configuration) {        // TODO: never used, therefore commented, task added:    	// Vector currentIds = new Vector();        Collection<Configuration> currentItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());        for (Iterator<Configuration> iter = currentItems.iterator(); iter.hasNext();) {            Configuration configItem = (Configuration) iter.next();            configurationDAO.delete(configItem);        }    }    public boolean configurationItemExists(Configuration configuration) {        if (configuration != null && configuration.getVersion() != null) {            Collection<Configuration> configItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());            if (configItems != null && configItems.size() > 0) {                return true;            }        }        return false;    }    public boolean configurationItemUpToDate(Configuration configuration) {        long currentVersion = 0;        if (configuration != null && configuration.getVersion() != null) {            Collection<Configuration> configItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());            for (Iterator<Configuration> iter = configItems.iterator(); iter.hasNext();) {                Configuration configItem = (Configuration) iter.next();                if (configItem != null) {                    currentVersion = Math.max(SystemConfigurationUtilities.getVersionAsLong(configItem.getVersion()),                            currentVersion);                }            }            if (currentVersion >= SystemConfigurationUtilities.getVersionAsLong(configuration.getVersion())) {                return true;            }        }        return false;    }    public void resetConfigurationCache() {        IssueUtilities.setResolutions(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION));        IssueUtilities.setSeverities(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY));        IssueUtilities.setStatuses(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS));        IssueUtilities.setCustomFields(getCustomFields());    }    public void resetConfigurationCache(int type) {        if (type == SystemConfigurationUtilities.TYPE_RESOLUTION) {            IssueUtilities.setResolutions(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION));        } else if (type == SystemConfigurationUtilities.TYPE_SEVERITY) {            IssueUtilities.setSeverities(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY));        } else if (type == SystemConfigurationUtilities.TYPE_STATUS) {            IssueUtilities.setStatuses(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS));        } else if (type == SystemConfigurationUtilities.TYPE_CUSTOMFIELD) {            IssueUtilities.setCustomFields(getCustomFields());        }    }    public WorkflowScript getWorkflowScript(Integer id) {        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(id);        return workflowScript;    }    public List<WorkflowScript> getWorkflowScripts() {        List<WorkflowScript> workflowScripts = workflowScriptDAO.findAll();        return workflowScripts;    }    /**     * Creates a workflow script.     *     * @param model The <code>WorkflowScript</code> carring the data     * @return The <code>WorkflowScript</code> after inserting     */    public WorkflowScript createWorkflowScript(WorkflowScript wflscrpt) {        // create workflow script and populate data        WorkflowScript workflowScript = new WorkflowScript();        workflowScript.setCreateDate(new Date());                // save entity        this.workflowScriptDAO.saveOrUpdate( workflowScript );        wflscrpt.setId(workflowScript.getId());        return workflowScript;    }    public WorkflowScript updateWorkflowScript(WorkflowScript wflscrpt) {        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(wflscrpt.getId());        return workflowScript;    }        /**     * remove a workflow script by its id     *     * @todo get all ProjectScriptBeans with that script attached and delete the ProjectScriptBean     * @param id the id of the workflow script to remove     */    public void removeWorkflowScript( Integer id ) {        if ( id != null ) {            WorkflowScript wfsBean = this.workflowScriptDAO.findByPrimaryKey(id);            if ( wfsBean != null ) {                this.workflowScriptDAO.delete(wfsBean);            }        }    }    public CustomField getCustomField(Integer id) {        CustomField customField = customFieldDAO.findByPrimaryKey(id);        return customField;    }    public List<CustomField> getCustomFields() {        List<CustomField> customFields = customFieldDAO.findAll();        return customFields;    }    public List<CustomField> getCustomFields(Locale locale) {        List<CustomField> fields = getCustomFields();        for (int i = 0; i < fields.size(); i++) {            fields.get(i).setLabels(locale);        }        return fields;    }    /**     * Creates a custom field     *     * @param customField The <code>CustomField</code> carrying the data     * @return  the <code>CustomField</code> after saving     */    public CustomField createCustomField(CustomField customField) {        customField.setCreateDate(new Date());        this.customFieldDAO.saveOrUpdate( customField );                if (customField.getOptions().size() > 0) {            removeCustomFieldValues(customField.getId());            List<CustomFieldValue> newOptions = customField.getOptions();            for (int i = 0; i < newOptions.size(); i++) {                newOptions.get(i).getCustomField().setId(customField.getId());                createCustomFieldValue(newOptions.get(i));            }        }        return customField;    }    public CustomField updateCustomField(CustomField cf) {        CustomField customField = customFieldDAO.findByPrimaryKey(cf.getId());        customField.setCreateDate(new Date());        this.customFieldDAO.saveOrUpdate( customField );        if (cf.getOptions().size() > 0) {            removeCustomFieldValues(customField.getId());            List<CustomFieldValue> newOptions = cf.getOptions();            for (int i = 0; i < newOptions.size(); i++) {                createCustomFieldValue(newOptions.get(i));            }        }        return customField;    }    /**     * searches for a custom field by primary key and removes it     *     * @param customFieldId the primary key     */    public void removeCustomField(Integer customFieldId) {        CustomField customField = customFieldDAO.findByPrimaryKey(customFieldId);                if ( customField != null ) {            this.customFieldDAO.delete(customField);        }    }        /**     * Gets a <code>CustomFieldValue</code> by primary key     *     * @param id the primary key     * @return The <code>CustomFieldValue</code> found or <code>null</code>     */    public CustomFieldValue getCustomFieldValue(Integer id) {        CustomFieldValue cfvBean = (CustomFieldValue)            this.customFieldValueDAO.findByPrimaryKey(id);                return cfvBean;    }    public CustomFieldValue createCustomFieldValue(CustomFieldValue customFieldValue) {        customFieldValue.setCustomField(customFieldValue.getCustomField());                customFieldValue.setCreateDate(new Date());                this.customFieldValueDAO.saveOrUpdate(customFieldValue);        return customFieldValue;    }    /**     * Updates a <code>CustomFieldValue</code>.     *     * @param model The model to update     * @return The <code>CustomFieldValue</code> after saving     */    public CustomFieldValue updateCustomFieldValue(CustomFieldValue cfv) {        CustomFieldValue customFieldValue = this.customFieldValueDAO.findByPrimaryKey( cfv.getId() );        this.customFieldValueDAO.saveOrUpdate( customFieldValue );        return customFieldValue;    }    public List<CustomFieldValue> updateCustomFieldValues(Integer customFieldId, List<CustomFieldValue> customFieldValues) {        return customFieldValues;    }    /**     * removes a custom field value by primary key     *      * @param customFieldValueId the id of the custoem field     */    public void removeCustomFieldValue(Integer customFieldValueId) {        // find custom field value by id        CustomFieldValue customFieldValue =            this.customFieldValueDAO.findByPrimaryKey(customFieldValueId);        // remove from parent field        customFieldValue.getCustomField().getValues().remove(customFieldValue);        // delete it        this.customFieldValueDAO.delete(customFieldValue);    }    /**     * Removes all field values of a given custom field     *     * @param customFieldId The id of the customField     */    public void removeCustomFieldValues(Integer customFieldId) {        CustomField customFieldBean = this.customFieldDAO.findByPrimaryKey( customFieldId );        // get values of the field        Collection<CustomFieldValue> values = customFieldBean.getValues();        for ( Iterator<CustomFieldValue> iter = values.iterator(); iter.hasNext();) {            // get current            CustomFieldValue customFieldValue = (CustomFieldValue)iter.next();            // remove from collection            iter.remove();            // delete from datasource            this.customFieldValueDAO.delete( customFieldValue );        }    }    public Language getLanguageItemByKey(String key, Locale locale) {        Language languageItem = null;        languageItem = languageDAO.findByKeyAndLocale(key, ITrackerResources.BASE_LOCALE);        if (locale != null && !"".equals(locale.getLanguage())) {            languageItem = languageDAO.findByKeyAndLocale(key, locale.getLanguage());            if (!"".equals(locale.getCountry())) {                try {                    languageItem = languageDAO.findByKeyAndLocale(key, locale.toString());                } catch (Exception ex){                }            }        }        return languageItem;    }    public List<Language> getLanguageItemsByKey(String key) {        List<Language> languageItems = languageDAO.findByKey(key);        return languageItems;    }    public Language updateLanguageItem(Language language) {        Language languageItem;        try {            languageItem = languageDAO.findByKeyAndLocale(language.getResourceKey(), language.getLocale());            languageItem.setLocale(language.getLocale());            languageItem.setResourceKey(language.getResourceKey());            languageItem.setResourceValue(language.getResourceValue());            languageItem.setLastModifiedDate(new Timestamp(new Date().getTime()));        } catch (NoSuchEntityException fe) {            logger.debug("NoSuchEntityException: Language, now populating Language");            languageItem = new Language();            languageItem.setLocale(language.getLocale());            languageItem.setResourceKey(language.getResourceKey());            languageItem.setResourceValue(language.getResourceValue());            languageItem.setCreateDate(new Timestamp(new Date().getTime()));            languageItem.setLastModifiedDate(languageItem.getCreateDate());        }        logger.debug("Start saveOrUpdate Language");        languageDAO.saveOrUpdate(languageItem);        logger.debug("Saved Language");        return languageItem;    }    /**     * Removes all <code>Language</code>s with the give key     *     * @param key The key to be removed     */    public void removeLanguageKey(String key) {        // find all <code>Language</code>s for the given key        List<Language> languageItems = languageDAO.findByKey(key);        for (Iterator<Language> iter = languageItems.iterator(); iter.hasNext();) {            // delete current item            this.languageDAO.delete(iter.next());        }    }    /**     * Removes the <code>Language</code> passed as parameter     *     * @param model The <code>Language</code> to remove     */    public void removeLanguageItem(Language language) {        Language languageItem = languageDAO.findByKeyAndLocale(language.getResourceKey(), language.getLocale());        if ( languageItem != null ) {            // delete item            this.languageDAO.delete( languageItem );        }     }    public String[] getSortedKeys() {        String[] sortedKeys = new String[0];        int i = 0;        Collection<Language> items = languageDAO.findByLocale(ITrackerResources.BASE_LOCALE);        sortedKeys = new String[items.size()];        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); i++) {            Language item = (Language) iter.next();            sortedKeys[i] = item.getResourceKey();        }        // Now sort the list of keys in a logical manner        Arrays.sort(sortedKeys);        return sortedKeys;    }    public HashMap<String,String> getDefinedKeys(String locale) {        HashMap<String,String> keys = new HashMap<String,String>();        if (locale == null || locale.equals("")) {            locale = ITrackerResources.BASE_LOCALE;        }        Collection<Language> items = languageDAO.findByLocale(locale);        for (Iterator<Language> iter = items.iterator(); iter.hasNext();) {            Language item = (Language) iter.next();            keys.put(item.getResourceKey(), item.getResourceValue());        }        return keys;    }    public List<NameValuePair> getDefinedKeysAsArray(String locale) {        NameValuePair[] keys = new NameValuePair[0];        if (locale == null || locale.equals("")) {            locale = ITrackerResources.BASE_LOCALE;        }        int i = 0;        Collection<Language> items = languageDAO.findByLocale(locale);        keys = new NameValuePair[items.size()];        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); i++) {            Language item = (Language) iter.next();            keys[i] = new NameValuePair(item.getResourceKey(), item.getResourceValue());        }                Arrays.sort(keys, new NameValuePair());        return Arrays.asList(keys);    }    public int getNumberDefinedKeys(String locale) {        return getDefinedKeys(locale).size();    }    public List<Language> getLanguage(Locale locale) {        Language[] languageArray = new Language[0];        HashMap<String,String> language = new HashMap<String,String>();        Collection<Language> baseItems = languageDAO.findByLocale(ITrackerResources.BASE_LOCALE);        for (Iterator<Language> iterator = baseItems.iterator(); iterator.hasNext();) {            Language item = (Language) iterator.next();            language.put(item.getResourceKey(), item.getResourceValue());        }        if (locale != null && !"".equals(locale.getLanguage())) {            Collection<Language> languageItems = languageDAO.findByLocale(locale.getLanguage());            for (Iterator<Language> iterator = languageItems.iterator(); iterator.hasNext();) {                Language item = (Language) iterator.next();                language.put(item.getResourceKey(), item.getResourceValue());            }            if (!"".equals(locale.getCountry())) {                Collection<Language> countryItems = languageDAO.findByLocale(locale.toString());                for (Iterator<Language> iterator = countryItems.iterator(); iterator.hasNext();) {                    Language item = (Language) iterator.next();                    language.put(item.getResourceKey(), item.getResourceValue());                }            }        }        languageArray = new Language[language.size()];        int i = 0;        String localeString = (locale == null ? ITrackerResources.BASE_LOCALE : locale.toString());        for (Iterator<String> iterator = language.keySet().iterator(); iterator.hasNext(); i++) {            String key = (String) iterator.next();            languageArray[i] = new Language(localeString, key, (String) language.get(key));        }        return Arrays.asList(languageArray);    }    @SuppressWarnings("unchecked")	public HashMap<String,List<String>> getAvailableLanguages() {        HashMap<String,List<String>> languages = new HashMap<String,List<String>>();        List<Configuration> locales = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_LOCALE);        for (int i = 0; i < locales.size(); i++) {            String Baselocalestring = locales.get(i).getValue();            if (! ITrackerResources.BASE_LOCALE.equalsIgnoreCase(Baselocalestring)) {                            if (Baselocalestring.length() == 2) {//                languages.put(Baselocalestring, new ArrayList());                    List<String> languageList = new ArrayList();                    for (int j = 0; j < locales.size(); j++) {                        String localestring = locales.get(j).getValue();                        if (!ITrackerResources.BASE_LOCALE.equalsIgnoreCase(localestring) && localestring.length() > 2 ) {                                 String baseLanguage = localestring.substring(0, 2);                            if (baseLanguage.equals(Baselocalestring) && localestring.length() == 5 && localestring.indexOf('_') == 2) {                                languageList.add(localestring);                            }                        }                    }                    languages.put(Baselocalestring,languageList);                }            }        }        return languages;    }    @SuppressWarnings("unchecked")	public int getNumberAvailableLanguages() {        int numLanguages = 0;        HashMap<String,List<String>> availableLanguages = getAvailableLanguages();        for (Iterator iter = availableLanguages.keySet().iterator(); iter.hasNext();) {            List languages = new ArrayList();            List list = availableLanguages.get((String)iter.next());            languages.add(list);            if (languages != null && languages.size() > 0) {                numLanguages += languages.size();            } else {                numLanguages += 1;            }        }        return numLanguages;    }    public void updateLanguage(Locale locale, List<Language> items) {        if (locale != null && items != null) {            Configuration configItem = new Configuration(SystemConfigurationUtilities.TYPE_LOCALE, locale                    .toString(), props.getProperty("version"));            updateLanguage(locale, items, configItem);        }    }    public void updateLanguage(Locale locale, List<Language> items, Configuration configItem) {        for (int i = 0; i < items.size(); i++) {            if (items.get(i) != null) {                updateLanguageItem(items.get(i));            }        }        removeConfigurationItems(configItem);        createConfigurationItem(configItem);    }    public SystemConfiguration getSystemConfiguration(Locale locale) {        SystemConfiguration config = new SystemConfiguration();        // Load the basic system configuration        List<Configuration> resolutions = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION);        for (int i = 0; i < resolutions.size(); i++) {            resolutions.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities                    .getLanguageKey(resolutions.get(i)), locale));        }        config.setResolutions(resolutions);        List<Configuration> severities = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY);        for (int i = 0; i < severities.size(); i++) {            severities.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities                    .getLanguageKey(severities.get(i)), locale));        }        config.setSeverities(severities);        List<Configuration> statuses = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS);        for (int i = 0; i < statuses.size(); i++) {            statuses.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities.getLanguageKey(statuses.get(i)),                    locale));        }        config.setStatuses(statuses);        // Now load the CustomFields        config.setCustomFields(IssueUtilities.getCustomFields(locale));        // Now set the system version        config.setVersion(props.getProperty("version"));        return config;    }        public boolean initializeLocale(String locale, boolean forceReload) {        boolean result = false;        Configuration localeConfig = new Configuration(SystemConfigurationUtilities.TYPE_LOCALE, locale,                props.getProperty("version"));        if (!configurationItemUpToDate(localeConfig) || forceReload) {        	logger.debug("Loading database with locale " + locale);            PropertiesFileHandler localePropertiesHandler = new PropertiesFileHandler(                    "/org/itracker/core/resources/ITracker"                            + (ITrackerResources.BASE_LOCALE.equals(locale) ? "" : "_" + locale) + ".properties");            if (localePropertiesHandler.hasProperties()) {                Properties localeProperties = localePropertiesHandler.getProperties();                logger.debug("Locale " + locale + " contains " + localeProperties.size() + " properties.");                for (Enumeration propertiesEnumeration = localeProperties.propertyNames(); propertiesEnumeration                        .hasMoreElements();) {                    String key = (String) propertiesEnumeration.nextElement();                    String value = localeProperties.getProperty(key);                    updateLanguageItem(new Language(locale, key, value));                }                removeConfigurationItems(localeConfig);                createConfigurationItem(localeConfig);                ITrackerResources.clearBundle(ITrackerResources.getLocale(locale));                result = true;            } else {            	logger.info("Locale " + locale + " contained no properties.");            }        }        return result;    }    public void initializeConfiguration() {        // Need to eventually add in code that detects the current version of        // the config and update        // if necessary        try {            List<Configuration> initialized = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_INITIALIZED);            if (initialized == null || initialized.size() != 1) {                logger.debug("System does not appear to be initialized, initializing system configuration.");                List<Language> baseLanguage = getLanguage(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE));                if (baseLanguage == null || baseLanguage.size() == 0) {                    throw new SystemConfigurationException(                            "Languages must be initialized before the system configuration can be loaded.");                }                // Remove any previous configuration information, possibly left                // over from previous failed initialization                logger.debug("Removing previous incomplete initialization information.");                removeConfigurationItems(SystemConfigurationUtilities.TYPE_STATUS);                removeConfigurationItems(SystemConfigurationUtilities.TYPE_SEVERITY);                removeConfigurationItems(SystemConfigurationUtilities.TYPE_RESOLUTION);                for (int i = 0; i < baseLanguage.size(); i++) {                    if (baseLanguage.get(i).getResourceKey().startsWith(ITrackerResources.KEY_BASE_RESOLUTION)) {                        try {                            String resolutionString = baseLanguage.get(i).getResourceKey().substring(20);                            logger.debug("Adding new configuration resolution value: " + resolutionString);                            int resolutionNumber = Integer.parseInt(resolutionString);                            createConfigurationItem(new Configuration(                                    SystemConfigurationUtilities.TYPE_RESOLUTION, resolutionString, props                                            .getProperty("version"), resolutionNumber));                        } catch (Exception e) {                            logger.error("Unable to load resolution value: " + baseLanguage.get(i).getResourceKey(), e);                        }                    }                    if (baseLanguage.get(i).getResourceKey().startsWith(ITrackerResources.KEY_BASE_SEVERITY)) {                        try {                            String severityString = baseLanguage.get(i).getResourceKey().substring(18);                            logger.debug("Adding new configuration severity value: " + severityString);                            int severityNumber = Integer.parseInt(severityString);                            createConfigurationItem(new Configuration(SystemConfigurationUtilities.TYPE_SEVERITY,                                    severityString, props.getProperty("version"), severityNumber));                        } catch (Exception e) {                            logger.error("Unable to load severity value: " + baseLanguage.get(i).getResourceKey(), e);                        }                    }                    if (baseLanguage.get(i).getResourceKey().startsWith(ITrackerResources.KEY_BASE_STATUS)) {                        try {                            String statusString = baseLanguage.get(i).getResourceKey().substring(16);                            logger.debug("Adding new configuration status value: " + statusString);                            int statusNumber = Integer.parseInt(statusString);                            createConfigurationItem(new Configuration(SystemConfigurationUtilities.TYPE_STATUS,                                    statusString, props.getProperty("version"), statusNumber));                        } catch (Exception e) {                            logger.error("Unable to load status value: " + baseLanguage.get(i).getResourceKey(), e);                        }                    }                }                createConfigurationItem(new Configuration(SystemConfigurationUtilities.TYPE_INITIALIZED, "1",                        props.getProperty("version")));            }        } catch (Exception e) {            logger.error("Unable to initialize system configuration.", e);        }    }    public LanguageDAO getLanguageDAO() {        return languageDAO;    }    public void setLanguageDAO(LanguageDAO languageDAO) {        this.languageDAO = languageDAO;    }    public ConfigurationDAO getConfigurationDAO() {        return configurationDAO;    }    public void setConfigurationDAO(ConfigurationDAO configurationDAO) {        this.configurationDAO = configurationDAO;    }    public CustomFieldDAO getCustomFieldDAO() {        return customFieldDAO;    }    public void setCustomFieldDAO(CustomFieldDAO customFieldDAO) {        this.customFieldDAO = customFieldDAO;    }    public CustomFieldValueDAO getCustomFieldValueDAO() {        return customFieldValueDAO;    }    public void setCustomFieldValueDAO(CustomFieldValueDAO customFieldValueDAO) {        this.customFieldValueDAO = customFieldValueDAO;    }    public WorkflowScriptDAO getWorkflowScriptDAO() {        return workflowScriptDAO;    }    public void setWorkflowScriptDAO(WorkflowScriptDAO workflowScriptDAO) {        this.workflowScriptDAO = workflowScriptDAO;    }}