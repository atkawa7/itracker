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

package org.itracker.services.implementations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.model.NameValuePair;
import org.itracker.model.ProjectScript;
import org.itracker.model.SystemConfiguration;
import org.itracker.model.WorkflowScript;
import org.itracker.model.util.PropertiesFileHandler;
import org.itracker.persistence.dao.ConfigurationDAO;
import org.itracker.persistence.dao.CustomFieldDAO;
import org.itracker.persistence.dao.CustomFieldValueDAO;
import org.itracker.persistence.dao.LanguageDAO;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.persistence.dao.ProjectScriptDAO;
import org.itracker.persistence.dao.WorkflowScriptDAO;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.NamingUtilites;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.jfree.util.Log;

/**
 * Implementation of the ConfigurationService Interface.
 *
 * @see ConfigurationService
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    
    private static final Logger logger = Logger.getLogger(ConfigurationServiceImpl.class.getName());
    // TODO make final static?
    private final Properties props;
    private ConfigurationDAO configurationDAO;
    private CustomFieldDAO customFieldDAO;
    private CustomFieldValueDAO customFieldValueDAO;
    private LanguageDAO languageDAO;
    private ProjectScriptDAO projectScriptDAO;
    private WorkflowScriptDAO workflowScriptDAO;

    
    private static final Long _START_TIME_MILLIS = System.currentTimeMillis();
    private String jndiPropertiesOverridePrefix;
    
    /**
     * Creates a new instance using the given configuration.
     *
     * @param configurationProperties itracker configuration properties
     *        (see /WEB-INF/configuration.properties)
     */
    public ConfigurationServiceImpl(Properties configurationProperties, 
    		ConfigurationDAO configurationDAO, CustomFieldDAO customFieldDAO,
    		CustomFieldValueDAO customFieldValueDAO, LanguageDAO languageDAO, 
    		ProjectScriptDAO projectScriptDAO, WorkflowScriptDAO workflowScriptDAO) {
        if (configurationProperties == null) {
            throw new IllegalArgumentException("null configurationProperties");
        }
        this.props = configurationProperties;
        props.setProperty("start_time_millis", String.valueOf(_START_TIME_MILLIS));
        
        // initialize naming context prefix for properties overrides
        this.jndiPropertiesOverridePrefix = props.getProperty(
				"jndi_override_prefix", null);
        
        this.configurationDAO = configurationDAO;
        this.customFieldDAO = customFieldDAO;
        this.customFieldValueDAO = customFieldValueDAO;
        this.languageDAO = languageDAO;
        
        this.projectScriptDAO = projectScriptDAO;
        this.workflowScriptDAO = workflowScriptDAO;
    }
    
    public String getProperty(String name) {
    	String value = null;
    	if (null != jndiPropertiesOverridePrefix) {

			if (logger.isDebugEnabled()) {

				logger.debug("getProperty: looking up '" + name
						+ "' from jndi context "
						+ jndiPropertiesOverridePrefix);
				

			}
			try {
				value = NamingUtilites.getStringValue(new InitialContext(),
						jndiPropertiesOverridePrefix + "/" + name, null);
		    	if (null == value) {
		    		if (logger.isDebugEnabled()) {
		    			logger.debug("getProperty: value not found in jndi: " + name);
		    		}	
		    	}
			} catch (Exception e) {
				logger.error("getProperty: exception looking up value for " + name, e);
			}

    	}
    	
    	if (null == value) {
    		value = props.getProperty(name, null);
    	}
    	if (logger.isDebugEnabled()) {
    		logger.debug("getProperty: returning " + value + " for name: " + name);
    	}
    	return value;
    }
    
    public String getProperty(String name, String defaultValue) {
        String val = getProperty(name);
    	return (val == null) ? defaultValue : val;
    }
    
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = getProperty(name);
        
        return (value == null ? defaultValue : Boolean.valueOf(value));
    }
    
    public int getIntegerProperty(String name, int defaultValue) {
        String value = getProperty(name);
        
        try {
            return (value == null) ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
        
    }
    
    public long getLongProperty(String name, long defaultValue) {
        String value = getProperty(name);
        try {
            return (value == null) ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
        
    }
    /**
     * returns a proxy to the properties, supplying jndi awareness
     */
    public Properties getProperties() {
    	Properties p = new Properties(props) {
    		
    		/**
			 * 
			 */
			private static final long serialVersionUID = -9126991683132905153L;

			@Override
    		public synchronized Object get(Object key) {
    			if (null != super.getProperty(
    					"jndi_override_prefix", null)) {
    				if (logger.isInfoEnabled()) {
						logger.info("get: looking for override for " + key
								+ " in jndi properties override: "
								+ super.getProperty(
				    					"jndi_override_prefix", null));
    				}
					Object val = null;
					try {
						val = NamingUtilites.lookup(new InitialContext(),
								super.getProperty(
				    					"jndi_override_prefix", null) + "/" + String.valueOf(key));
					} catch (NamingException e) {
						if (Log.isDebugEnabled()) {
							logger.debug("get: failed to create initial context", e);
						}
					}
					if (null != val) {
						if (logger.isDebugEnabled()) {
							logger.debug("get: returning " + val);
						}
						return val;
					}

				}
    			if (logger.isDebugEnabled()) {
    				logger.debug("get: get value of " + key + " from super");
    			}
    			return super.get(key);
    		}
    	};
        return p;
    }
    
    public Configuration getConfigurationItem(Integer id) {
        Configuration configItem = configurationDAO.findByPrimaryKey(id);
        return configItem;
    }
    
    public List<Configuration> getConfigurationItemsByType(int type) {
        List<Configuration> configItems = configurationDAO.findByType(type);
        Collections.sort(configItems, new Configuration.ConfigurationOrderComparator());
        return configItems;
    }
    
    public List<Configuration> getConfigurationItemsByType(int type, Locale locale) {
        List<Configuration> items = getConfigurationItemsByType(type);
        
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == SystemConfigurationUtilities.TYPE_STATUS) {
                items.get(i).setName(IssueUtilities.getStatusName(items.get(i).getValue(), locale));
            } else if (items.get(i).getType() == SystemConfigurationUtilities.TYPE_SEVERITY) {
                items.get(i).setName(IssueUtilities.getSeverityName(items.get(i).getValue(), locale));
            } else if (items.get(i).getType() == SystemConfigurationUtilities.TYPE_RESOLUTION) {
                items.get(i).setName(IssueUtilities.getResolutionName(items.get(i).getValue(), locale));
            }
        }
        return items;
    }
    
    /**
     * Creates a <code>Configuration</code>.
     *
     * @param model The <code>Configuration</code> to store
     * @return the <code>Configuration</code> after saving
     * @todo replace hardcoded version by a resource
     */
    public Configuration createConfigurationItem(Configuration configuration) {
        
        Configuration configurationItem = new Configuration();
        
        configurationItem.setType( configuration.getType() );
        configurationItem.setOrder( configuration.getOrder() );
        configurationItem.setValue( configuration.getValue() );
        configurationItem.setCreateDate(new Date());
        configurationItem.setVersion( this.getProperty("version") );
        configurationDAO.saveOrUpdate(configurationItem);
        
        return configurationItem;
        
    }
    
    /**
     * Updates a <code>ConfigurationItem</code>
     *
     * @param model The model containing the data
     * @return the <code>Configuration</code> after save
     */
    public Configuration updateConfigurationItem(Configuration configuration) {
        // find item by primary key
        Configuration configurationItem = configurationDAO.findByPrimaryKey(configuration.getId());
        
        // update now
        configurationDAO.saveOrUpdate( configurationItem );
        // get model from saved item
        return configurationItem;
    }
    
    /**
     * Updates the configuration items
     *
     * @param models the <code>ConfigurationModels</code> to update
     * @param  type The type of the <code>ConfigurationItem</code>s to update
     * @return an array with the saved models
     */
    public List<Configuration> updateConfigurationItems(List<Configuration> configurations, int type) {
        
        // remove all items
//        removeConfigurationItems(type);
        List<Configuration> configurationItems = new ArrayList<Configuration>();
        for (Iterator<Configuration> iterator = configurations.iterator(); iterator.hasNext();) {
            
            // create a new item
            Configuration configurationItem = (Configuration) iterator.next();
            Configuration curConfiguration = configurationDAO.findByPrimaryKey(configurationItem.getId());
            
            curConfiguration.setCreateDate(configurationItem.getCreateDate());
            curConfiguration.setLastModifiedDate(configurationItem.getLastModifiedDate());
            curConfiguration.setName(configurationItem.getName());
            curConfiguration.setOrder(configurationItem.getOrder());
            curConfiguration.setType(configurationItem.getType());
            curConfiguration.setValue(configurationItem.getValue());
            curConfiguration.setVersion(configurationItem.getVersion());
            
            // set Modified date
            curConfiguration.setLastModifiedDate(new Date());
            // save or update
            this.configurationDAO.saveOrUpdate( curConfiguration );
            configurationItems.add(curConfiguration);
        }
        // sort array
        Collections.sort(configurationItems);
        
        return configurationItems;
    }
    
    /**
     * Finds the <code>Configuration</code> by primary key <code>id<code>
     * and deletes it.
     *
     * @param id The id of the <code>COnfigurationBean</code> to remove
     */
    public void removeConfigurationItem(Integer id) {
        
        Configuration configBean = this.configurationDAO.findByPrimaryKey(id);
        if ( configBean != null ) {
            this.configurationDAO.delete( configBean );
        }
    }
    
    /**
     * Removes all <code>Configuration</code>s of the give <code>type</code>
     *
     * @param type the type of <code>Configuration</code> to remove
     */
    public void removeConfigurationItems(int type) {
        
        // find the configuration beans by its type
        Collection<Configuration> currentItems = configurationDAO.findByType(type);
        
        for (Iterator<Configuration> iter = currentItems.iterator(); iter.hasNext();) {
            // get current config bean
            Configuration config = (Configuration) iter.next();
            // delete it
            this.configurationDAO.delete( config );
        }
    }
    
    public void removeConfigurationItems(Configuration configuration) {
        // TODO: never used, therefore commented, task added:
        // Vector currentIds = new Vector();
        Collection<Configuration> currentItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());
        for (Iterator<Configuration> iter = currentItems.iterator(); iter.hasNext();) {
            Configuration configItem = (Configuration) iter.next();
            configurationDAO.delete(configItem);
        }
    }
    
    public boolean configurationItemExists(Configuration configuration) {
        
        if (configuration != null && configuration.getVersion() != null) {
            
            Collection<Configuration> configItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());
            
            if (configItems != null && configItems.size() > 0) {
                
                return true;
                
            }
            
        }
        
        return false;
        
    }
    
    public boolean configurationItemUpToDate(Configuration configuration) {
        
        long currentVersion = 0;
        
        if (configuration != null && configuration.getVersion() != null) {
            
            Collection<Configuration> configItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());
            
            for (Iterator<Configuration> iter = configItems.iterator(); iter.hasNext();) {
                
                Configuration configItem = (Configuration) iter.next();
                
                if (configItem != null) {
                    
                    currentVersion = Math.max(SystemConfigurationUtilities.getVersionAsLong(configItem.getVersion()),
                            currentVersion);
                    
                }
                
            }
            
            if (currentVersion >= SystemConfigurationUtilities.getVersionAsLong(configuration.getVersion())) {
                
                return true;
                
            }
            
        }
        
        return false;
        
    }
    
    public void resetConfigurationCache() {
        
        IssueUtilities.setResolutions(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION));
        
        IssueUtilities.setSeverities(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY));
        
        IssueUtilities.setStatuses(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS));
        
        IssueUtilities.setCustomFields(getCustomFields());
        
    }
    
    public void resetConfigurationCache(int type) {
        
        if (type == SystemConfigurationUtilities.TYPE_RESOLUTION) {
            
            IssueUtilities.setResolutions(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION));
            
        } else if (type == SystemConfigurationUtilities.TYPE_SEVERITY) {
            
            IssueUtilities.setSeverities(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY));
            
        } else if (type == SystemConfigurationUtilities.TYPE_STATUS) {
            
            IssueUtilities.setStatuses(getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS));
            
        } else if (type == SystemConfigurationUtilities.TYPE_CUSTOMFIELD) {
            
            IssueUtilities.setCustomFields(getCustomFields());
            
        }
        
    }
    
    public ProjectScript getProjectScript(Integer scriptId) {
        
        ProjectScript projectScript = this.projectScriptDAO.findByPrimaryKey(scriptId);
        return projectScript;
        
    }
    
    public List<ProjectScript> getProjectScripts() {
        List<ProjectScript> projectScripts = this.projectScriptDAO.findAll();
        return projectScripts;
    }
    
    
    public ProjectScript createProjectScript(ProjectScript projectScript) {
        
        // create project script and populate data
        ProjectScript editprojectScript = new ProjectScript();
        editprojectScript.setFieldId(projectScript.getFieldId());
        editprojectScript.setPriority(projectScript.getPriority());
        editprojectScript.setProject(projectScript.getProject());
        editprojectScript.setScript(projectScript.getScript());
        editprojectScript.setCreateDate(new Date());
        editprojectScript.setLastModifiedDate(editprojectScript.getCreateDate());
        
        // save entity
        this.projectScriptDAO.save(editprojectScript);
        
        return editprojectScript;
    }
    
    public ProjectScript updateProjectScript(ProjectScript projectScript) {
        ProjectScript editprojectScript;
        
        editprojectScript = projectScriptDAO.findByPrimaryKey(projectScript.getId());
        editprojectScript.setFieldId(projectScript.getFieldId());
        editprojectScript.setPriority(projectScript.getPriority());
        editprojectScript.setProject(projectScript.getProject());
        editprojectScript.setScript(projectScript.getScript());
        editprojectScript.setLastModifiedDate(new Date());
        this.projectScriptDAO.saveOrUpdate(editprojectScript);
        return editprojectScript;
    }
    
    /**
     * remove a project script by its id
     *
     * @todo get all ProjectScriptBeans with that script attached and delete the ProjectScriptBean
     * @param id the id of the project script to remove
     */
    public void removeProjectScript( Integer projectScript_id ) {
        if ( projectScript_id != null ) {
            ProjectScript projectScript = this.projectScriptDAO.findByPrimaryKey(projectScript_id);
            if ( projectScript != null ) {
                this.projectScriptDAO.delete(projectScript);
            }
        }
    }
    
    public WorkflowScript getWorkflowScript(Integer id) {
        
        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(id);
        
        return workflowScript;
        
    }
    
    public List<WorkflowScript> getWorkflowScripts() {
        List<WorkflowScript> workflowScripts = workflowScriptDAO.findAll();
        return workflowScripts;
    }
    
    /**
     * Creates a workflow script.
     *
     * @param model The <code>WorkflowScript</code> carring the data
     * @return The <code>WorkflowScript</code> after inserting
     */
    public WorkflowScript createWorkflowScript(WorkflowScript workflowScript) {
        
        // create workflow script and populate data
        WorkflowScript editworkflowScript = new WorkflowScript();
        editworkflowScript.setName(workflowScript.getName());
        editworkflowScript.setScript(workflowScript.getScript());
        editworkflowScript.setEvent(workflowScript.getEvent());
        editworkflowScript.setLastModifiedDate(new Date());
        editworkflowScript.setCreateDate(new Date());
        editworkflowScript.setLastModifiedDate(editworkflowScript.getCreateDate());
        
        // save entity
        workflowScriptDAO.save(editworkflowScript);
        
        return editworkflowScript;
    }
    
    public WorkflowScript updateWorkflowScript(WorkflowScript workflowScript) {
        WorkflowScript editworkflowScript;
        
        editworkflowScript = workflowScriptDAO.findByPrimaryKey(workflowScript.getId());
        editworkflowScript.setName(workflowScript.getName());
        editworkflowScript.setScript(workflowScript.getScript());
        editworkflowScript.setEvent(workflowScript.getEvent());
        editworkflowScript.setLastModifiedDate(new Date());
        workflowScriptDAO.saveOrUpdate(editworkflowScript);
        return editworkflowScript;
    }
    
    /**
     * remove a workflow script by its id
     *
     * @todo get all ProjectScriptBeans with that script attached and delete the ProjectScriptBean
     * @param id the id of the workflow script to remove
     */
    public void removeWorkflowScript( Integer workflowScript_id ) {
        if ( workflowScript_id != null ) {
            WorkflowScript workflowScript = this.workflowScriptDAO.findByPrimaryKey(workflowScript_id);
            if ( workflowScript != null ) {
                this.workflowScriptDAO.delete(workflowScript);
            }
        }
    }
    
    public CustomField getCustomField(Integer id) {
        
        CustomField customField = customFieldDAO.findByPrimaryKey(id);
        
        return customField;
        
    }
    
    public List<CustomField> getCustomFields() {
        List<CustomField> customFields = customFieldDAO.findAll();
        Collections.sort(customFields, new CustomField.NameComparator());
        return customFields;
        
    }
    
    public List<CustomField> getCustomFields(Locale locale) {
        
        List<CustomField> fields = getCustomFields();
        
        for (int i = 0; i < fields.size(); i++) {
            
            fields.get(i).setLabels(locale);
            
        }
        Collections.sort(fields,  new CustomField.NameComparator());
        
        return fields;
        
    }
    
    /**
     * Creates a custom field
     *
     * @param customField The <code>CustomField</code> carrying the data
     * @return  the <code>CustomField</code> after saving
     */
    public CustomField createCustomField(CustomField customField) {
        CustomField addcustomField = new CustomField();
        addcustomField.setDateFormat(customField.getDateFormat());
        addcustomField.setFieldType(customField.getFieldType());
        addcustomField.setOptions(customField.getOptions());
        addcustomField.setName(customField.getName());
        addcustomField.setRequired(customField.isRequired());
        this.customFieldDAO.save( addcustomField );
        
/*        if (addcustomField.getOptions() !=  null && addcustomField.getOptions().size() > 0) {
            removeCustomFieldValues(addcustomField.getId());
            List<CustomFieldValue> newOptions = addcustomField.getOptions();
 
            for (int i = 0; i < newOptions.size(); i++) {
                newOptions.get(i).getCustomField().setId(addcustomField.getId());
                createCustomFieldValue(newOptions.get(i));
            }
        }
 */
        return addcustomField;
    }
    
    public CustomField updateCustomField(CustomField customField) {
        CustomField editcustomField = customFieldDAO.findByPrimaryKey(customField.getId());
        
        editcustomField.setDateFormat(customField.getDateFormat());
        editcustomField.setFieldType(customField.getFieldType());
        editcustomField.setOptions(customField.getOptions());
        editcustomField.setName(customField.getName());
        editcustomField.setRequired(customField.isRequired());
        editcustomField.setLastModifiedDate(new Date());
        
        this.customFieldDAO.saveOrUpdate( editcustomField );
        
/*        if (editcustomField.getOptions() != null && editcustomField.getOptions().size() > 0) {
            removeCustomFieldValues(editcustomField.getId());
            List<CustomFieldValue> newOptions = editcustomField.getOptions();
 
            for (int i = 0; i < newOptions.size(); i++) {
                createCustomFieldValue(newOptions.get(i));
            }
        }
 */
        return editcustomField;
    }
    
    /**
     * searches for a custom field by primary key and removes it
     *
     * @param customFieldId the primary key
     */
    public boolean removeCustomField(Integer customFieldId) {
        boolean status = true;
        boolean del_Status = true;
        CustomField customField = customFieldDAO.findByPrimaryKey(customFieldId);
        
        if ( customField != null ) {
            try {
                if(customField.getFieldType() == CustomField.Type.LIST)
                    status = this.removeCustomFieldValues(customFieldId);
                String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
                this.customFieldDAO.delete(customField);
                if(key != null)
                    status = this.removeLanguageKey(key);
            } catch (Exception ex) {
                del_Status = false;
            }
        }
        if ( ! del_Status )
            status = del_Status;
        
        return status;
    }
    
    
    /**
     * Gets a <code>CustomFieldValue</code> by primary key
     *
     * @param id the primary key
     * @return The <code>CustomFieldValue</code> found or <code>null</code>
     */
    public CustomFieldValue getCustomFieldValue(Integer id) {
        
        CustomFieldValue cfvBean = (CustomFieldValue)
        this.customFieldValueDAO.findByPrimaryKey(id);
        
        return cfvBean;
    }
    
    public CustomFieldValue createCustomFieldValue(CustomFieldValue customFieldValue) {
        CustomFieldValue addcustomFieldValue = new CustomFieldValue();
        addcustomFieldValue.setCustomField(customFieldValue.getCustomField());
        addcustomFieldValue.setValue(customFieldValue.getValue());
        addcustomFieldValue.setName(customFieldValue.getName());
        this.customFieldValueDAO.save(addcustomFieldValue);
        
        return addcustomFieldValue;
    }
    
    
    /**
     * Updates a <code>CustomFieldValue</code>.
     *
     * @param model The model to update
     * @return The <code>CustomFieldValue</code> after saving
     */
    public CustomFieldValue updateCustomFieldValue(CustomFieldValue customFieldValue) {
        CustomFieldValue editcustomFieldValue = this.customFieldValueDAO.findByPrimaryKey( customFieldValue.getId() );
        editcustomFieldValue.setCreateDate(customFieldValue.getCreateDate());
        editcustomFieldValue.setCustomField(customFieldValue.getCustomField());
        editcustomFieldValue.setValue(customFieldValue.getValue());
        editcustomFieldValue.setLastModifiedDate(new Date());
        editcustomFieldValue.setName(customFieldValue.getName());
        this.customFieldValueDAO.saveOrUpdate( editcustomFieldValue );
        return editcustomFieldValue;
    }
    
    public List<CustomFieldValue> updateCustomFieldValues(Integer customFieldId, List<CustomFieldValue> customFieldValues) {
        List<CustomFieldValue> customFieldValueItems = new ArrayList<CustomFieldValue>();
        
        if(customFieldId != null) {
            try {
                CustomField customField = customFieldDAO.findByPrimaryKey(customFieldId);
                if(customFieldValues == null || customFieldValues.size() == 0) {
                    // Collection<CustomFieldValue> currValues = customField.getOptions();
                    // boolean status = currValues.removeAll(currValues);
                } else {
                    for (Iterator<CustomFieldValue> iterator = customFieldValues.iterator(); iterator.hasNext();) {
                        
                        // create a new item
                        CustomFieldValue customFieldValueItem = (CustomFieldValue) iterator.next();
                        CustomFieldValue curCustomFieldValue = customFieldValueDAO.findByPrimaryKey(customFieldValueItem.getId());
                        
                        curCustomFieldValue.setCreateDate(customFieldValueItem.getCreateDate());
                        curCustomFieldValue.setLastModifiedDate(customFieldValueItem.getLastModifiedDate());
                        curCustomFieldValue.setName(customFieldValueItem.getName());
                        curCustomFieldValue.setValue(customFieldValueItem.getValue());
                        curCustomFieldValue.setCustomField(customFieldValueItem.getCustomField());
                        curCustomFieldValue.setSortOrder(customFieldValueItem.getSortOrder());
                        
                        // set Modified date
                        curCustomFieldValue.setLastModifiedDate(new Date());
                        // save or update
                        this.customFieldValueDAO.saveOrUpdate( curCustomFieldValue );
                        customFieldValueItems.add(curCustomFieldValue);
                        
                    }
                    // sort array
//                    Collections.sort(customFieldValueItems);
                    customField.setOptions(customFieldValueItems);
                    return customFieldValueItems;
                    
                }
            } catch(Exception fe) {
            }
        }
        
//        Arrays.sort(customFieldValues, new CustomFieldValue());
        return customFieldValues;
    }
    
    /**
     * removes a custom field value by primary key
     *
     * @param customFieldValueId the id of the custoem field
     */
    public boolean removeCustomFieldValue(Integer customFieldValueId) {
        boolean status = true;
        boolean del_Status = true;
        
        // find custom field value by id
        CustomFieldValue customFieldValue = this.customFieldValueDAO.findByPrimaryKey(customFieldValueId);
        
        // remove from parent field
//        customFieldValue.getCustomField().getOptions().remove(customFieldValue);
        // delete it
        try {
            this.customFieldValueDAO.delete(customFieldValue);
        } catch (Exception ex) {
            del_Status = false;
        }
        if ( ! del_Status )
            status = del_Status;
        
        return status;
    }
    
    /**
     * Removes all field values of a given custom field
     *
     * @param customFieldId The id of the customField
     */
    public boolean removeCustomFieldValues(Integer customFieldId) {
        boolean status = true;
        boolean lp_Status = true;
        CustomField customField = this.customFieldDAO.findByPrimaryKey( customFieldId );
        // get values of the field
        List<CustomFieldValue> customFieldValues = customField.getOptions();
        for ( Iterator<CustomFieldValue> iter = customFieldValues.iterator(); iter.hasNext();) {
            // get current
            CustomFieldValue customFieldValue = (CustomFieldValue)iter.next();
            String key = CustomFieldUtilities.getCustomFieldOptionLabelKey(customFieldId, customFieldValue.getId());
            // remove from collection
            iter.remove();
            // delete from datasource
            try {
                this.customFieldValueDAO.delete( customFieldValue );
                
                if(key != null)
                    status = this.removeLanguageKey(key);
            } catch (Exception ex) {
                lp_Status = false;
            }
        }
        if (! lp_Status )
            status = lp_Status;
        
        return status;
    }
    
    public Language getLanguageItemByKey(String key, Locale locale) {
        Language languageItem = null;
        
        languageItem = languageDAO.findByKeyAndLocale(key, ITrackerResources.BASE_LOCALE);
        
        if (locale != null && !"".equals(locale.getLanguage())) {
            languageItem = languageDAO.findByKeyAndLocale(key, locale.getLanguage());
            if (!"".equals(locale.getCountry())) {
                try {
                    languageItem = languageDAO.findByKeyAndLocale(key, locale.toString());
                } catch (Exception ex){
                }
            }
        }
        return languageItem;
        
    }
    
    public List<Language> getLanguageItemsByKey(String key) {
        List<Language> languageItems = languageDAO.findByKey(key);
        
        return languageItems;
    }
    
    public Language updateLanguageItem(Language language) {
        Language languageItem;
        
        try {
            languageItem = languageDAO.findByKeyAndLocale(language.getResourceKey(), language.getLocale());
            languageItem.setLocale(language.getLocale());
            languageItem.setResourceKey(language.getResourceKey());
            languageItem.setResourceValue(language.getResourceValue());
            languageItem.setLastModifiedDate(new Timestamp(new Date().getTime()));
        } catch (NoSuchEntityException fe) {
            logger.debug("NoSuchEntityException: Language, now populating Language");
            languageItem = new Language();
            languageItem.setLocale(language.getLocale());
            languageItem.setResourceKey(language.getResourceKey());
            languageItem.setResourceValue(language.getResourceValue());
            languageItem.setCreateDate(new Timestamp(new Date().getTime()));
            languageItem.setLastModifiedDate(languageItem.getCreateDate());
        }
        logger.debug("Start saveOrUpdate Language");
        languageDAO.saveOrUpdate(languageItem);
        logger.debug("Saved Language");
        return languageItem;
    }
    
    /**
     * Removes all <code>Language</code>s with the give key
     *
     * @param key The key to be removed
     */
    public boolean removeLanguageKey(String key) {
        boolean status = true;
        boolean lp_Status = true;
        
        // find all <code>Language</code>s for the given key
        List<Language> languageItems = languageDAO.findByKey(key);
        
        for (Iterator<Language> iter = languageItems.iterator(); iter.hasNext();) {
            // delete current item
            Language language = (Language) iter.next();
            try {
                this.languageDAO.delete(language);
            } catch (Exception ex) {
                lp_Status = false;
            }
        }
        if ( ! lp_Status )
            status = lp_Status;
        
        return status;
    }
    /**
     * Removes the <code>Language</code> passed as parameter
     *
     * @param model The <code>Language</code> to remove
     */
    public void removeLanguageItem(Language language) {
        
        Language languageItem = languageDAO.findByKeyAndLocale(language.getResourceKey(), language.getLocale());
        
        if ( languageItem != null ) {
            // delete item
            this.languageDAO.delete( languageItem );
        }
    }
    
    public String[] getSortedKeys() {
        
        int i = 0;
        Collection<Language> items = languageDAO.findByLocale(ITrackerResources.BASE_LOCALE);
        String[] sortedKeys = new String[items.size()];
        
        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); i++) {
            Language item = (Language) iter.next();
            sortedKeys[i] = item.getResourceKey();
        }
        
        // Now sort the list of keys in a logical manner
        
        Arrays.sort(sortedKeys);
        return sortedKeys;
        
    }
    
    public HashMap<String,String> getDefinedKeys(String locale) {
        
        HashMap<String,String> keys = new HashMap<String,String>();
        
        if (locale == null || locale.equals("")) {
            locale = ITrackerResources.BASE_LOCALE;
        }
        
        Collection<Language> items = languageDAO.findByLocale(locale);
        for (Iterator<Language> iter = items.iterator(); iter.hasNext();) {
            Language item = (Language) iter.next();
            keys.put(item.getResourceKey(), item.getResourceValue());
        }
        return keys;
        
    }
    
    public List<NameValuePair> getDefinedKeysAsArray(String locale) {
        NameValuePair[] keys = null;
        if (locale == null || locale.equals("")) {
            locale = ITrackerResources.BASE_LOCALE;
        }
        
        int i = 0;
        Collection<Language> items = languageDAO.findByLocale(locale);
        keys = new NameValuePair[items.size()];
        
        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); i++) {
            Language item = (Language) iter.next();
            keys[i] = new NameValuePair(item.getResourceKey(), item.getResourceValue());
        }
        
        Arrays.sort(keys);
        return Arrays.asList(keys);
        
    }
    
    public int getNumberDefinedKeys(String locale) {
        
        return getDefinedKeys(locale).size();
        
    }
    
    public List<Language> getLanguage(Locale locale) {
        
        
        Map<String,String> language = new HashMap<String,String>();
        
        Collection<Language> baseItems = languageDAO.findByLocale(ITrackerResources.BASE_LOCALE);
        
        for (Iterator<Language> iterator = baseItems.iterator(); iterator.hasNext();) {
            
            Language item = (Language) iterator.next();
            
            language.put(item.getResourceKey(), item.getResourceValue());
            
        }
        
        if (locale != null && !"".equals(locale.getLanguage())) {
            
            Collection<Language> languageItems = languageDAO.findByLocale(locale.getLanguage());
            
            for (Iterator<Language> iterator = languageItems.iterator(); iterator.hasNext();) {
                
                Language item = (Language) iterator.next();
                
                language.put(item.getResourceKey(), item.getResourceValue());
                
            }
            
            if (!"".equals(locale.getCountry())) {
                
                Collection<Language> countryItems = languageDAO.findByLocale(locale.toString());
                
                for (Iterator<Language> iterator = countryItems.iterator(); iterator.hasNext();) {
                    
                    Language item = (Language) iterator.next();
                    
                    language.put(item.getResourceKey(), item.getResourceValue());
                    
                }
                
            }
            
        }
        
        Language[] languageArray = new Language[language.size()];
        
        int i = 0;
        
        String localeString = (locale == null ? ITrackerResources.BASE_LOCALE : locale.toString());
        
        for (Iterator<String> iterator = language.keySet().iterator(); iterator.hasNext(); i++) {
            
            String key = (String) iterator.next();

            languageArray[i] = new Language(localeString, key, (String) language.get(key));
            
        }
        
        return Arrays.asList(languageArray);
        
    }
    
    @SuppressWarnings("unchecked")
    public HashMap<String,List<String>> getAvailableLanguages() {
        
        HashMap<String,List<String>> languages = new HashMap<String,List<String>>();
        List<Configuration> locales = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_LOCALE);
        
        for (int i = 0; i < locales.size(); i++) {
            String Baselocalestring = locales.get(i).getValue();
            if (! ITrackerResources.BASE_LOCALE.equalsIgnoreCase(Baselocalestring)) {
                
                if (Baselocalestring.length() == 2) {
//                languages.put(Baselocalestring, new ArrayList());
                    List<String> languageList = new ArrayList<String>();
                    for (int j = 0; j < locales.size(); j++) {
                        String localestring = locales.get(j).getValue();
                        if (!ITrackerResources.BASE_LOCALE.equalsIgnoreCase(localestring) && localestring.length() > 2 ) {
                            String baseLanguage = localestring.substring(0, 2);
                            if (baseLanguage.equals(Baselocalestring) && localestring.length() == 5 && localestring.indexOf('_') == 2) {
                                languageList.add(localestring);
                            }
                        }
                    }
                    languages.put(Baselocalestring,languageList);
                }
            }
        }
        
        return languages;
        
    }
    
    @SuppressWarnings("unchecked")
    public int getNumberAvailableLanguages() {
        
        int numLanguages = 0;
        HashMap<String,List<String>> availableLanguages = getAvailableLanguages();
        
        for (Iterator iter = availableLanguages.keySet().iterator(); iter.hasNext();) {
            List<List> languages = new ArrayList<List>();
            List list = availableLanguages.get((String)iter.next());
            languages.add(list);
            
            if (languages != null && languages.size() > 0) {
                numLanguages += languages.size();
            } else {
                numLanguages += 1;
            }
            
        }
        
        return numLanguages;
        
    }
    
    public void updateLanguage(Locale locale, List<Language> items) {
        
        if (locale != null && items != null) {
            Configuration configItem = new Configuration(SystemConfigurationUtilities.TYPE_LOCALE, locale
                    .toString(), props.getProperty("version"));
            updateLanguage(locale, items, configItem);
            
        }
        
    }
    
    public void updateLanguage(Locale locale, List<Language> items, Configuration configItem) {
        for (int i = 0; i < items.size(); i++) {
            
            if (items.get(i) != null) {
                updateLanguageItem(items.get(i));
            }
        }
        removeConfigurationItems(configItem);
        createConfigurationItem(configItem);
    }
    
    public SystemConfiguration getSystemConfiguration(Locale locale) {
        
        SystemConfiguration config = new SystemConfiguration();
        
        // Load the basic system configuration
        
        List<Configuration> resolutions = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION);
        
        for (int i = 0; i < resolutions.size(); i++) {
            
            resolutions.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities
                    .getLanguageKey(resolutions.get(i)), locale));
            
        }
        
        config.setResolutions(resolutions);
        
        List<Configuration> severities = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY);
        
        for (int i = 0; i < severities.size(); i++) {
            
            severities.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities
                    .getLanguageKey(severities.get(i)), locale));
            
        }
        
        config.setSeverities(severities);
        
        List<Configuration> statuses = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS);
        
        for (int i = 0; i < statuses.size(); i++) {
            
            statuses.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities.getLanguageKey(statuses.get(i)),
                    locale));
            
        }
        
        config.setStatuses(statuses);
        
        // Now load the CustomFields
        
        config.setCustomFields(IssueUtilities.getCustomFields(locale));
        
        // Now set the system version
        
        config.setVersion(props.getProperty("version"));
        
        return config;
        
    }
    
    
    
    public boolean initializeLocale(String locale, boolean forceReload) {
        boolean result = false;
        
        Configuration localeConfig = new Configuration(SystemConfigurationUtilities.TYPE_LOCALE, locale,
                props.getProperty("version"));
        
        if (!configurationItemUpToDate(localeConfig) || forceReload) {
            
            logger.debug("Loading database with locale " + locale);
            
            PropertiesFileHandler localePropertiesHandler = new PropertiesFileHandler(
                    "/org/itracker/core/resources/ITracker"
                    + (ITrackerResources.BASE_LOCALE.equals(locale) ? "" : "_" + locale) + ".properties");
            
            if (localePropertiesHandler.hasProperties()) {
                
                Properties localeProperties = localePropertiesHandler.getProperties();
                
                logger.debug("Locale " + locale + " contains " + localeProperties.size() + " properties.");
                
                for (Enumeration<?> propertiesEnumeration = localeProperties.propertyNames();
                propertiesEnumeration.hasMoreElements();) {
                    String key = (String) propertiesEnumeration.nextElement();
                    String value = localeProperties.getProperty(key);
                    updateLanguageItem(new Language(locale, key, value));
                }
                
                removeConfigurationItems(localeConfig);
                
                createConfigurationItem(localeConfig);
                
                ITrackerResources.clearBundle(ITrackerResources.getLocale(locale));
                
                result = true;
                
            } else {
                
                logger.info("Locale " + locale + " contained no properties.");
                
            }
            
        }
        
        return result;
        
    }
    
    public void initializeConfiguration() {
        
        // Need to eventually add in code that detects the current version of
        // the config and update
        
        // if necessary

            
            List<Configuration> initialized = getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_INITIALIZED);
            
            if (initialized == null || initialized.size() != 1) {
                
                logger.debug("System does not appear to be initialized, initializing system configuration.");
                
                List<Language> baseLanguage = getLanguage(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE));
                
                if (baseLanguage == null || baseLanguage.size() == 0) {
                    
                    throw new IllegalStateException (
                            "Languages must be initialized before the system configuration can be loaded.");
                    
                }
                
                // Remove any previous configuration information, possibly left
                // over from previous failed initialization
                
                logger.debug("Removing previous incomplete initialization information.");
                
                removeConfigurationItems(SystemConfigurationUtilities.TYPE_STATUS);
                
                removeConfigurationItems(SystemConfigurationUtilities.TYPE_SEVERITY);
                
                removeConfigurationItems(SystemConfigurationUtilities.TYPE_RESOLUTION);
                
                for (int i = 0; i < baseLanguage.size(); i++) {
                    
                    if (baseLanguage.get(i).getResourceKey().startsWith(ITrackerResources.KEY_BASE_RESOLUTION)) {
                        
                        try {
                            
                            String resolutionString = baseLanguage.get(i).getResourceKey().substring(20);
                            
                            logger.debug("Adding new configuration resolution value: " + resolutionString);
                            
                            int resolutionNumber = Integer.parseInt(resolutionString);
                            
                            createConfigurationItem(new Configuration(
                                    SystemConfigurationUtilities.TYPE_RESOLUTION, resolutionString, props
                                    .getProperty("version"), resolutionNumber));
                            
                        } catch (RuntimeException e) {
                            
                            logger.error("Unable to load resolution value: " + baseLanguage.get(i).getResourceKey(), e);
                            throw e;
                            
                        }
                        
                    }
                    
                    if (baseLanguage.get(i).getResourceKey().startsWith(ITrackerResources.KEY_BASE_SEVERITY)) {
                        
                        try {
                            
                            String severityString = baseLanguage.get(i).getResourceKey().substring(18);
                            
                            logger.debug("Adding new configuration severity value: " + severityString);
                            
                            int severityNumber = Integer.parseInt(severityString);
                            
                            createConfigurationItem(new Configuration(SystemConfigurationUtilities.TYPE_SEVERITY,
                                    severityString, props.getProperty("version"), severityNumber));
                            
                        } catch (RuntimeException e) {
                            
                            logger.error("Unable to load severity value: " + baseLanguage.get(i).getResourceKey(), e);
                            throw e;
                        }
                        
                    }
                    
                    if (baseLanguage.get(i).getResourceKey().startsWith(ITrackerResources.KEY_BASE_STATUS)) {
                        
                        try {
                            
                            String statusString = baseLanguage.get(i).getResourceKey().substring(16);
                            
                            logger.debug("Adding new configuration status value: " + statusString);
                            
                            int statusNumber = Integer.parseInt(statusString);
                            
                            createConfigurationItem(new Configuration(SystemConfigurationUtilities.TYPE_STATUS,
                                    statusString, props.getProperty("version"), statusNumber));
                        } catch (RuntimeException e) {
                            logger.error("Unable to load status value: " + baseLanguage.get(i).getResourceKey(), e);
                            throw e;
                        }
                    }
                }
                createConfigurationItem(new Configuration(SystemConfigurationUtilities.TYPE_INITIALIZED, "1",
                        props.getProperty("version")));
            }
        
        
    }
    
    public LanguageDAO getLanguageDAO() {
        return languageDAO;
    }
    
    public ConfigurationDAO getConfigurationDAO() {
        return configurationDAO;
    }
    
    public CustomFieldDAO getCustomFieldDAO() {
        return customFieldDAO;
    }
    
    public CustomFieldValueDAO getCustomFieldValueDAO() {
        return customFieldValueDAO;
    }
    
    public WorkflowScriptDAO getWorkflowScriptDAO() {
        return workflowScriptDAO;
    }
    
}
