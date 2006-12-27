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

package org.itracker.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.model.NameValuePair;
import org.itracker.model.ProjectScript;
import org.itracker.model.SystemConfiguration;
import org.itracker.model.WorkflowScript;


public interface ConfigurationService {
    
    Properties getProperties();

    String getProperty(String name);
    
    String getProperty(String name, String defaultValue);
    
    boolean getBooleanProperty(String name, boolean defaultValue);
    
    int getIntegerProperty(String name, int defaultValue);
    
    long getLongProperty(String name, long defaultValue);

    Configuration getConfigurationItem(Integer id);
    
    /**
      * Returns all the configuration items of a particular type.  The name values
      * for all the items will not be initialized.
      * @param type the type of configuration items to retrieve
      * @return an array of ConfigurationModels
      */
    List<Configuration> getConfigurationItemsByType(int type);
    
    /**
      * Returns all the configuration items of a particular type.  In addition, all
      * of the configuration items name values will be initialized to the values
      * for the supplied locale.
      * @param type the type of configuration items to retrieve
      * @param locale the locale to use when setting the configuration items name values
      * @return an array of ConfigurationModels
      */   
    List<Configuration> getConfigurationItemsByType(int type, Locale locale);

    /**
      * This method will create a new Configuration for persistance in the database.
      * @param model the model to create the bean from
      * @returns and updated model with the information from the newly created bean
      */
    Configuration createConfigurationItem(Configuration configuration);
 
    /**
      * This method updates a configuration item in the database.  It does not include any updates
      * to language items that would be used to display the localized value of the item.
      * @param model a Configuration of the item to update
      * @return a Configuration with the updated item
      */
    Configuration updateConfigurationItem(Configuration configuration);
    
    List<Configuration> updateConfigurationItems(List<Configuration> configurations, int type);

    boolean configurationItemExists(Configuration configuration);
    
    boolean configurationItemUpToDate(Configuration configuration);

    /**
      * This method will remove the configuration item with the supplied id.
      * @param id the id of the configuration information to remove
      */
    void removeConfigurationItem(Integer id);
 
    /**
      * This method will remove all configuration items that match the supplied type.  This
      * will remove all items of that type such as all system status values.
      * @param type the type of configuration information to remove
      */
    void removeConfigurationItems(int type);
 
    /**
      * This method will remove all configuration items that match the supplied models
      * type and value.  This effectively eliminates all previous versions of the item.
      * It is normally called prior to a create to remove any older copies of this item.
      * @param model the model to determine the type and value from
      */
    void removeConfigurationItems(Configuration configuration);

    /**
      * This method will reset any caches in the system of configuration items for all configuration
      * item types.
      */
    void resetConfigurationCache();
 
    /**
      * This method will reset any caches in the system of configuration items for the specified
      * configuration item type.
      * @param type the type of configuration item to reset in any caches
      */
    void resetConfigurationCache(int type);

    /**
      * This method will return the requested project script.
      * @param id the id of the requested script
      * @return a ProjectScript with the requested script, or null if not found
      */
    ProjectScript getProjectScript(Integer id);
    
    /**
      * This method will return all defined project scripts.
      * @return a ProjectScript array with all defined scripts
      */
    List<ProjectScript> getProjectScripts();
    
    /**
      * This method will create a new project script for persistance in the database.
      * @param model the model to create the script from
      * @returns and updated model with the information from the newly created script
      */
    ProjectScript createProjectScript(ProjectScript projectScript);
    
    /**
      * This method updates a project script in the database.
      * @param model a ProjectScript of the item to update
      * @return a ProjectScript with the updated item
      */
    ProjectScript updateProjectScript(ProjectScript projectScript);
    
    /**
      * This method removes a project script in the database.
      * @param id  The id of the project script
      */
    void removeProjectScript(Integer id);

    /**
      * This method will return the requested workflow script.
      * @param id the id of the requested script
      * @return a WorkflowScript with the requested script, or null if not found
      */
    WorkflowScript getWorkflowScript(Integer id);
    
    /**
      * This method will return all defined workflow scripts.
      * @return a WorkflowScript array with all defined scripts
      */
    List<WorkflowScript> getWorkflowScripts();
    
    /**
      * This method will create a new workflow script for persistance in the database.
      * @param model the model to create the script from
      * @returns and updated model with the information from the newly created script
      */
    WorkflowScript createWorkflowScript(WorkflowScript workflowScript);
    
    /**
      * This method updates a workflow script in the database.
      * @param model a WorkflowScript of the item to update
      * @return a WorkflowScript with the updated item
      */
    WorkflowScript updateWorkflowScript(WorkflowScript workflowScript);
    
    /**
      * This method removes a workflow script in the database.
      * @param id  The id of the workflow script
      */
    void removeWorkflowScript(Integer id);

    /**
      * This method will return the requested custom field.
      * @param id the id of the requested field
      * @return a CustomField with the requested field, or null if not found
      */
    CustomField getCustomField(Integer id);

    /**
      * This method will return all the custom fields defined in the system.
      * @return an array of CustomFieldModels
      */
    List<CustomField> getCustomFields();
    
    /**
      * This method will return all the custom fields defined in the system.  It will
      * also initialize all of the field labels using the supplied locale.
      * @param locale the locale to use to initialize the labels
      * @return an array of CustomFieldModels
      */
    List<CustomField> getCustomFields(Locale locale);
    
    /**
      * This method will create a new CustomField for persistance in the database.  If the
      * field includes a set of option values, those will be created also.
      * @param model the model to create the field from
      * @returns and updated model with the information from the newly created field
      */
    CustomField createCustomField(CustomField customField);
    
    /**
      * This method updates a custom field in the database.  It does not include any updates
      * to language items that would be used to display the localized label for the field.
      * If any options are included, the list will be used to replace any existing options.
      * @param model a CustomField of the item to update
      * @return a CustomField with the updated item
      */
    CustomField updateCustomField(CustomField customField);
    
    /**
      * Removes a single custom field from the database.
      * @param customFieldValueId the id of the custom field to remove
      */
    void removeCustomField(Integer customFieldId);
    
    /**
      * This method will return the requested custom field value.
      * @param id the id of the requested field value
      * @return a CustomField with the requested field value, or null if not found
      */
    CustomFieldValue getCustomFieldValue(Integer id);
    
    /**
      * This method will create a new CustomFieldValue for persistance in the database.
      * @param model the model to create the field from
      * @returns and updated model with the information from the newly created field
      */
    CustomFieldValue createCustomFieldValue(CustomFieldValue customFieldValue);
    
    /**
      * This method updates a custom field value in the database.  It does not include any updates
      * to language items that would be used to display the localized label for the field value.
      * @param model a CustomFieldValue of the item to update
      * @return a CustomFieldValue with the updated item
      */
    CustomFieldValue updateCustomFieldValue(CustomFieldValue customFieldValue);
    
    /**
      * This method updates a set of custom field values in the database.  If the array of values
      * is null or zero length, it will remove all existing values from the custom field.  Otherwise
      * it will update the value and sort order of all the values.  If the array of models contains
      * more or less values than the current custom field, it will not remove theose values, or create
      * new values.
      * @param customFieldId the id of the custom field to update
      * @param models an array of CustomFieldValueModels to update
      * @return a array of CustomFieldValueModels with the updated items
      */
    List<CustomFieldValue> updateCustomFieldValues(Integer customFieldId, 
            List<CustomFieldValue> customFieldValues);
    
    /**
      * Removes a single custom field value from the database.
      * @param customFieldValueId the id of the custom field value to remove
      */
    void removeCustomFieldValue(Integer customFieldValueId);
    
    /**
      * Removes all custom field values from the database for a single custom field.
      * @param customFieldId the id of the custom field to remove the values for
      */
    void removeCustomFieldValues(Integer customFieldId);
    
    /**
      * This method will return the translation for a particular key in a locale.
      * @param key the key to look up
      * @param locale the localue to translate the key for
      * @return a Language with the translation
      */
    Language getLanguageItemByKey(String key, Locale locale);
    
    /**
      * This method will return all the translations for a particular key.
      * @param key the key to look up
      * @return an array of LanguageModels with the translations for the key
      */
    List<Language> getLanguageItemsByKey(String key);
    
    /**
      * Updates a translations for a particular key and locale.
      * @param model A Language for the key to update
      * @return a Language with the updated translation
      */
    Language updateLanguageItem(Language language);
    
    /**
      * This method will remove all language items with the supplied key regardless
      * of locale.
      * @param key the key to remove
      */
    void removeLanguageKey(String key);
    
    void removeLanguageItem(Language language);

    /**
      * This method will return the current configuration of the system.
      * @return a SystemConfiguration with the current configuration of the system
      */
    SystemConfiguration getSystemConfiguration(Locale locale);

    /**
      * Returns all of the keys currently defined in the base locale sorted and grouped in a
      * logical manner.
      */
    String[] getSortedKeys();

    Map<String,String> getDefinedKeys(String locale);
    
    List<NameValuePair> getDefinedKeysAsArray(String locale);
    
    int getNumberDefinedKeys(String locale);
    
    Map<String,List<String>> getAvailableLanguages();
    
    int getNumberAvailableLanguages();
    
    List<Language> getLanguage(Locale locale);
    
    void updateLanguage(Locale locale, List<Language> languages);
    
    void updateLanguage(Locale locale, List<Language> languages, Configuration config);

    /**
      * This method will load the specified locale.  It will look for the appropriate properties file,
      * and then load all of the resources into the database.
      * @param locale the locale to load
      * @param forceReload if true, it will reload the languages from the property file even if it is listed
      *                    as being up to date
      */
    boolean initializeLocale(String locale, boolean forceReload);

    /**
      * This method will load the some default system configuration data into the database.  The values
      * it loads are determined from the base ITracker.properties file so the language intiialization
      * <b>must</b> be performed before this method is called.
      */
    void initializeConfiguration();
    
}
