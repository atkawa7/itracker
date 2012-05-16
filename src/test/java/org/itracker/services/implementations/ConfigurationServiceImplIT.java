package org.itracker.services.implementations;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.*;
import org.itracker.model.CustomField.Type;
import org.itracker.persistence.dao.*;
import org.itracker.services.ConfigurationService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfigurationServiceImplIT extends
        AbstractDependencyInjectionTest {

    private static final Logger log = Logger
            .getLogger(ConfigurationServiceImplIT.class);

    private ConfigurationDAO configurationDAO;
    private ProjectDAO projectDAO;
    private ProjectScriptDAO projectScriptDAO;
    private WorkflowScriptDAO workflowScriptDAO;
    private CustomFieldDAO customFieldDAO;
    private CustomFieldValueDAO customFieldValueDAO;
    private LanguageDAO languageDAO;
    private Properties configurationProperties;

    /**
     * Object to be Tested: configuration-service
     */
    private ConfigurationService configurationService;

    public static final class TestInitialContextFactory implements
            InitialContextFactory {

        public TestInitialContextFactory() {

        }

        public Context getInitialContext(Hashtable<?, ?> environment)
                throws NamingException {
            if (null == SimpleNamingContextBuilder.getCurrentContextBuilder()) {
                SimpleNamingContextBuilder.emptyActivatedContextBuilder();
            }
            SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.getCurrentContextBuilder();
//            builder.bind("java:comp", new InitialContext());
            return builder.createInitialContextFactory(environment)
                    .getInitialContext(environment);
        }

    }


    @Test
    public void testConfigurationServiceImplConstructor() {
        try {
            configurationService = new ConfigurationServiceImpl(null, configurationDAO, customFieldDAO, customFieldValueDAO,
                    languageDAO, projectScriptDAO, workflowScriptDAO);
            fail("argument Properties configurationProperties is null, did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testLookupConfigurationItemById() {
        // defined in configurationbean_dataset.xml
        Configuration config = configurationService.getConfigurationItem(2000);

        assertNotNull("configuration (id:2000)", config);
        assertEquals("id", 2000, config.getId().intValue());
        DateFormat format = new SimpleDateFormat("M-d-y");
        try {
            assertEquals("create date", format.parse("1-1-2008"), config
                    .getCreateDate());
            assertEquals("modified date", format.parse("1-1-2008"), config
                    .getLastModifiedDate());
        } catch (ParseException e) {
            log
                    .error(
                            "testLookupConfigurationItemById: failed to parse date for assertion",
                            e);
            fail("failed to parse date for assertion: " + e.getMessage());
        }
        assertEquals("value", "Test Value", config.getValue());
        assertEquals("version", "Version 1.0", config.getVersion());
        assertEquals("order", 1, config.getOrder());
        assertEquals("type", 1, config.getType());

    }

    @Test
    public void testFailGetConfigurationItemById() {

        // not defined id
        Configuration config = configurationService
                .getConfigurationItem(999999);

        assertNull("non existing configuration item", config);

    }

    /**
     * Test if the configurationServiceImpl does override property values by JNDI.
     */
    @Test
    public void testGetJndiOverriddenProperty() throws Exception {


        final String web_session_timeout = "web_session_timeout";

        // initialize new initialContextFactoryBuilder
        SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        SimpleNamingContextBuilder.getCurrentContextBuilder().bind("java:comp/env/itracker/web_session_timeout", "300");

        Context ctx = new InitialContext();

        // basic assertions
        assertEquals("java:comp/env/itracker/web_session_timeout", "300", ctx.lookup("java:comp/env/itracker/web_session_timeout"));
        String val = configurationProperties.getProperty(web_session_timeout);
        assertEquals("configurationProperties#web_session_timeout", "30", val);

        // check getProperty() in configurationService for jndi overridden value
        val = configurationService.getProperty(web_session_timeout);
        assertEquals("configurationService.web_session_timeout", "300", val);

        // check getProperties().get() in configurationService for jndi overridden value
        Object valObj = configurationService.getProperties().get(web_session_timeout);
        assertEquals("configurationService.properties#web_session_timeout", "300", valObj);

        // check getProperties().getProperty() in configurationService for jndi overridden value
        val = configurationService.getProperties().getProperty(web_session_timeout);
        assertEquals("configurationService.properties#web_session_timeout", "300", valObj);

        SimpleNamingContextBuilder.emptyActivatedContextBuilder().deactivate();

    }

    @Test
    public void testGetBooleanProperty() {
        boolean prop = configurationService.getBooleanProperty("non_existent_property_name", false);
        assertFalse(prop);

        prop = configurationService.getBooleanProperty("non_existent_property_name", true);
        assertTrue(prop);
    }

    @Test
    public void testGetIntegerProperty() {
        int prop = configurationService.getIntegerProperty("non_existent_property_name", 123456);
        assertEquals(123456, prop);

        prop = configurationService.getIntegerProperty("non_existent_property_name", 123456);
        assertEquals(123456, prop);

        //project property is in configuration.properties, value is itracker
        prop = configurationService.getIntegerProperty("project", 123456);
        assertEquals(123456, prop);
    }

    @Test
    public void testGetLongProperty() {
        long prop = configurationService.getLongProperty("non_existent_property_name", 123456L);
        assertEquals(123456, prop);

        prop = configurationService.getLongProperty("non_existent_property_name", 123456L);
        assertEquals(123456, prop);

        //project property is in configuration.properties, value is itracker
        prop = configurationService.getLongProperty("project", 123456);
        assertEquals(123456, prop);
    }


    @Test
    public void testProperty() {
        assertEquals("project property", configurationProperties
                .getProperty("default_locale"), configurationService
                .getProperty("default_locale"));

    }

    @Test
    public void testResetConfigurationCache() {
        //SystemConfigurationUtilities.TYPE_RESOLUTION, value 4
        configurationService.resetConfigurationCache(4);
        assertEquals(1, IssueUtilities.getResolutions(Locale.ENGLISH).size());

        //SystemConfigurationUtilities.TYPE_SEVERITY, value 3
        configurationService.resetConfigurationCache(3);
        assertEquals(1, IssueUtilities.getSeverities(Locale.ENGLISH).size());

        //SystemConfigurationUtilities.TYPE_STATUS, value 2
        configurationService.resetConfigurationCache(2);
        assertEquals(1, IssueUtilities.getStatuses(Locale.ENGLISH).size());

        //SystemConfigurationUtilities.TYPE_CUSTOMFIELD, value 5
        configurationService.resetConfigurationCache(5);
        assertEquals(4, IssueUtilities.getCustomFields().size());
    }

    @Test
    public void testGetConfigurationItemsByType() {
        List<Configuration> configs = configurationService.getConfigurationItemsByType(1);
        assertNotNull(configs);
        assertEquals("configs of type ", 2, configs.size());
        assertEquals("config type", 1, configs.get(0).getType());
        assertEquals("configs of type 2", 2, configs.size());

        configs = configurationService.getConfigurationItemsByType(2);
        assertNotNull(configs);
        assertEquals("configs of type ", 1, configs.size());
        assertEquals("configs of type 2", 1, configs.size());
        assertEquals("config type", 2, configs.get(0).getType());

        //SystemConfigurationUtilities.TYPE_LOCALE, value is 1
        configs = configurationService.getConfigurationItemsByType(1, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs of type", 2, configs.size());
        assertEquals("configs of type 2", 2, configs.size());

        //SystemConfigurationUtilities.TYPE_STATUS, value is 2
        configs = configurationService.getConfigurationItemsByType(2, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs of type ", 1, configs.size());
        assertEquals("configs of type 1", 1, configs.size());

        //SystemConfigurationUtilities.TYPE_SEVERITY, value is 3
        configs = configurationService.getConfigurationItemsByType(3, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs of type 1", 1, configs.size());

        //SystemConfigurationUtilities.TYPE_RESOLUTION, value is 4
        configs = configurationService.getConfigurationItemsByType(4, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs of type 1", 1, configs.size());

    }

    @Test
    public void testUpdateConfigurationItem() {
        Configuration conf = configurationDAO.findByPrimaryKey(2000);
        assertFalse(conf.getOrder() == 789);
        conf.setOrder(789);
        configurationService.updateConfigurationItem(conf);
        conf = configurationDAO.findByPrimaryKey(2000);
        assertEquals("new order value", 789, conf.getOrder());


    }

    @Test
    public void testUpdateConfigurationItems() {
        Configuration conf = configurationDAO.findByPrimaryKey(2000);
        assertFalse(conf.getOrder() == 987);
        conf.setOrder(987);
        List<Configuration> items = new ArrayList<Configuration>();
        items.add(conf);

        // FIXME: What's the purpose of passing type here?
        configurationService.updateConfigurationItems(items, 1);
        conf = configurationDAO.findByPrimaryKey(2000);
        assertEquals("new order value", 987, conf.getOrder());

    }

    @Test
    public void testRemoveConfigurationItem() {
        Configuration conf = new Configuration(1, "1", "1", 2);
        configurationDAO.save(conf);
        Integer id = conf.getId();
        assertNotNull(id);
        assertNotNull(configurationDAO.findByPrimaryKey(id));

        configurationService.removeConfigurationItem(id);
        conf = configurationDAO.findByPrimaryKey(id);
        assertNull("removed item", conf);

    }

    @Test
    public void testRemoveConfigurationItems() {
        //test method removeConfigurationItems(int type)
        Configuration conf = new Configuration(123, "1", "1", 2);
        configurationDAO.save(conf);
        Integer id = conf.getId();
        assertNotNull(id);
        assertNotNull(configurationDAO.findByPrimaryKey(id));

        configurationService.removeConfigurationItems(123);
        conf = configurationDAO.findByPrimaryKey(id);
        assertNull("removed item", conf);

        //test method removeConfigurationItems(Configuration configuration)
        Configuration conf1 = new Configuration(234, "1", "1", 2);
        configurationDAO.save(conf1);
        Integer id1 = conf1.getId();
        assertNotNull(id1);
        assertNotNull(configurationDAO.findByPrimaryKey(id1));

        configurationService.removeConfigurationItems(conf1);
        conf1 = configurationDAO.findByPrimaryKey(id1);
        assertNull("removed item", conf1);
    }

    @Test
    public void testRemoveCustomFieldValues() {
        final Integer id = 4;
        CustomField customField = customFieldDAO.findByPrimaryKey(id);

        CustomFieldValue customFieldValue = new CustomFieldValue(
                customField, "my_value"
        );

        customFieldValue = configurationService.createCustomFieldValue(customFieldValue);
        assertNotNull(customFieldValue);
        assertNotNull(customFieldValue.getId());
        assertEquals("custom field value", "my_value", customFieldValue.getValue());

        assertNotNull("CustomField#" + id, customField);
        assertEquals("customField.size", 4, customField.getOptions().size());

        CustomFieldValue cfv = customField.getOptions().get(2);
        Integer cfvId = cfv.getId();
        assertNotNull("CustomFieldValue#id", cfvId);

        configurationService.removeCustomFieldValues(id);

        try {
            fail("delete #" + customFieldValueDAO.findByPrimaryKey(cfvId));
            customField = customFieldDAO.findByPrimaryKey(4);
            assertEquals("customField.size", 3, customField.getOptions().size());
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void testConfigurationItemExists() {
        // searched by type and value ( + needs version!!)
        Configuration conf = new Configuration(1, "Test Value", "1");
        assertTrue("conf type: 1, value: Test Value", configurationService.configurationItemExists(conf));

        conf = new Configuration(1, "Unknown Value", "1");
        assertFalse("conf type: 1, value: Unknown Value", configurationService.configurationItemExists(conf));
    }

    @Test
    public void testConfigurationItemUpToDate() {

        Configuration configuration = new Configuration(1, "Test Value", "Version 1.0", 1);
        assertTrue(configurationService.configurationItemUpToDate(configuration));

    }

    @Test
    public void testGetProjectScript() {
        ProjectScript projectScript = configurationService.getProjectScript(1);
        assertNotNull("project script #1", projectScript);
        assertEquals("project script id", new Integer(1), projectScript.getId());
    }

    @Test
    public void testGetProjectScripts() {
        List<ProjectScript> projectScripts = configurationService.getProjectScripts();
        assertNotNull(projectScripts);
        assertEquals("total project scripts", 1, projectScripts.size());
    }

    @Test
    public void testCreateProjectScript() {
        ProjectScript projectScript = new ProjectScript();
        projectScript.setFieldId(1);
        projectScript.setPriority(1);
        projectScript.setProject(projectDAO.findByPrimaryKey(3));
        projectScript.setScript(workflowScriptDAO.findByPrimaryKey(1));


        ProjectScript newProjectScript = configurationService.createProjectScript(projectScript);
        assertNotNull(newProjectScript);
        assertNotNull(newProjectScript.getId());
        assertEquals("priority", projectScript.getPriority(), newProjectScript.getPriority());
        assertEquals("script", projectScript.getScript(), newProjectScript.getScript());
        assertEquals("field id", projectScript.getFieldId(), newProjectScript.getFieldId());
        assertEquals("project", projectScript.getProject(), newProjectScript.getProject());

        // remove project script
        projectScriptDAO.delete(projectScript);
    }

    @Test
    public void testUpdateProjectScript() {
        ProjectScript projectScript = projectScriptDAO.findByPrimaryKey(1);
        projectScript.setPriority(999);

        ProjectScript newProjectScript = configurationService.updateProjectScript(projectScript);
        assertNotNull(newProjectScript);
        assertEquals("priority", 999, newProjectScript.getPriority());

        projectScript = projectScriptDAO.findByPrimaryKey(1);
        assertEquals("priority", 999, projectScript.getPriority());
    }

    @Test
    public void testRemoveProjectScript() {

        ProjectScript projectScript = new ProjectScript();
        projectScript.setFieldId(1);
        projectScript.setPriority(1);
        projectScript.setProject(projectDAO.findByPrimaryKey(3));
        projectScript.setScript(workflowScriptDAO.findByPrimaryKey(1));
        projectScriptDAO.save(projectScript);
        Integer id = projectScript.getId();
        assertNotNull(id);
        configurationService.removeProjectScript(id);
        assertNull("removed project script", projectScriptDAO.findByPrimaryKey(id));
    }

    @Test
    public void testGetWorkflowScript() {
        WorkflowScript workflowScript = configurationService.getWorkflowScript(1);
        assertNotNull(workflowScript);
        assertEquals(new Integer(1), workflowScript.getId());
    }

    @Test
    public void testGetWorkflowScripts() {
        List<WorkflowScript> workflowScripts = configurationService.getWorkflowScripts();
        assertNotNull(workflowScripts);
        assertEquals("total worflow scripts", 2, workflowScripts.size());
    }

    @Test
    public void testCreateWorkflowScript() {
        WorkflowScript workflowScript = new WorkflowScript();
        workflowScript.setName("script");
        workflowScript.setScript("my_script");
        WorkflowScript newWorkflowScript = configurationService.createWorkflowScript(workflowScript);

        assertNotNull(newWorkflowScript);
        assertNotNull(newWorkflowScript.getId());
        assertEquals("new workflow script", "my_script", newWorkflowScript.getScript());
    }

    @Test
    public void testUpdateWorkflowScript() {
        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(1);
        workflowScript.setScript("my_brand_new_script");
        configurationService.updateWorkflowScript(workflowScript);

        workflowScript = workflowScriptDAO.findByPrimaryKey(1);
        assertNotNull(workflowScript);
        assertEquals("new workflow script", "my_brand_new_script", workflowScript.getScript());
    }

    @Test
    public void testRemoveWorkflowScript() {
        WorkflowScript workflowScript = new WorkflowScript();
        workflowScript.setName("script");
        workflowScript.setScript("my_script");
        workflowScriptDAO.save(workflowScript);
        Integer id = workflowScript.getId();
        assertNotNull(id);

        configurationService.removeWorkflowScript(id);

        workflowScript = workflowScriptDAO.findByPrimaryKey(id);
        assertNull("removed script", workflowScript);

    }

    @Test
    public void testGetCustomField() {
        CustomField customField = configurationService.getCustomField(1);
        assertNotNull(customField);
        assertEquals(new Integer(1), customField.getId());
        assertTrue(customField.isRequired());
    }

    @Test
    public void testGetCustomFields() {
        List<CustomField> fields = configurationService.getCustomFields();
        assertNotNull("fields[]", fields);
        assertEquals("total custom fields", 4, fields.size());
    }

    @Test
    public void testCreateCustomField() {
        CustomField customField = new CustomField("my_field", Type.STRING);
        customField = configurationService.createCustomField(customField);
        assertNotNull(customField);
        assertNotNull(customField.getId());
        assertNotNull(customField.getId());
//		assertEquals("field name", "my_field", customField.getName());
        assertEquals("field type", Type.STRING, customField.getFieldType());
    }

    @Test
    public void testUpdateCustomField() {
        CustomField customField = customFieldDAO.findByPrimaryKey(1);
        assertTrue("required", customField.isRequired());
        customField.setRequired(false);
        configurationService.updateCustomField(customField);

        customField = customFieldDAO.findByPrimaryKey(1);
        assertFalse("required", customField.isRequired());

    }

    @Test
    public void testRemoveCustomField() throws Exception {
        //test CustomField which type is String
        CustomField customField = new CustomField("my_field", Type.STRING);
        customFieldDAO.save(customField);
        Integer id = customField.getId();
        assertNotNull(id);
        assertNotNull(customFieldDAO.findByPrimaryKey(id));

        configurationService.removeCustomField(id);
        assertNull("customFieldDAO.findByPrimaryKey(id)", customFieldDAO.findByPrimaryKey(id));


        //test CustomField which type is List
        CustomField customField1 = new CustomField("List CustomField", Type.LIST);
        customFieldDAO.save(customField1);
        Integer id1 = customField1.getId();
        assertNotNull(id1);
        assertNotNull("customFieldDAO.findByPrimaryKey(id1)", customFieldDAO.findByPrimaryKey(id1));

        configurationService.removeCustomField(id1);
        assertNull("customFieldDAO.findByPrimaryKey(id1)", customFieldDAO.findByPrimaryKey(id1));
    }

    @Test
    public void testGetCustomFieldValue() {
        CustomFieldValue customFieldValue = configurationService.getCustomFieldValue(1);
        assertEquals("id", new Integer(1), customFieldValue.getId());
        assertEquals("value", "2", customFieldValue.getValue());

    }

    @Test
    public void testCreateCustomFieldValue() {
        CustomFieldValue customFieldValue = new CustomFieldValue(
                customFieldDAO.findByPrimaryKey(1), "my_value"
        );

        customFieldValue = configurationService.createCustomFieldValue(customFieldValue);
        assertNotNull(customFieldValue);
        assertNotNull(customFieldValue.getId());
        assertEquals("custom field value", "my_value", customFieldValue.getValue());

    }

    @Test
    public void testUpdateCustomFieldValue() {
        CustomFieldValue customFieldValue = customFieldValueDAO.findByPrimaryKey(1);
        assertEquals("value", "2", customFieldValue.getValue());

        customFieldValue.setValue("brand_new_value");
        configurationService.updateCustomFieldValue(customFieldValue);

        customFieldValue = customFieldValueDAO.findByPrimaryKey(1);
        assertEquals("new value", "brand_new_value", customFieldValue.getValue());

    }

    @Test
    public void testUpdateCustomFieldValues() {
        CustomFieldValue customFieldValue2 = customFieldValueDAO.findByPrimaryKey(2);
        CustomFieldValue customFieldValue3 = customFieldValueDAO.findByPrimaryKey(3);
        List<CustomFieldValue> customFieldValues = new ArrayList<CustomFieldValue>();
        customFieldValues.add(customFieldValue2);
        customFieldValues.add(customFieldValue3);

        assertEquals("value#2", "1", customFieldValue2.getValue());
        assertEquals("value#3", "4", customFieldValue3.getValue());

        customFieldValue2.setValue("22");
        customFieldValue3.setValue("33");

        configurationService.updateCustomFieldValues(1, customFieldValues);

        customFieldValue2 = customFieldValueDAO.findByPrimaryKey(2);
        customFieldValue3 = customFieldValueDAO.findByPrimaryKey(3);
        assertEquals("new value#2", "22", customFieldValue2.getValue());
        assertEquals("new value#3", "33", customFieldValue3.getValue());

    }

    @Test
    public void testRemoveCustomFieldValue() {
        CustomFieldValue customFieldValue = new CustomFieldValue(
                customFieldDAO.findByPrimaryKey(1), "my_value");
        customFieldValueDAO.save(customFieldValue);
        Integer id = customFieldValue.getId();
        assertNotNull(id);
        assertNotNull(customFieldValueDAO.findByPrimaryKey(id));

        configurationService.removeCustomFieldValue(id);
        try {
            CustomFieldValue value = customFieldValueDAO.findByPrimaryKey(id);
            fail("custom field value: " + value);
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
        }


    }

    @Test
    public void testGetLanguageItemByKey() {
        Language languageItem = configurationService.getLanguageItemByKey("test_key", null);
        assertNotNull("Language", languageItem);
        assertEquals("id", new Integer(9999971), languageItem.getId());
        assertEquals("resource_value", "test_value", languageItem.getResourceValue());

    }

    @Test
    public void testGetLanguageItemsByKey() {
        List<Language> languages = configurationService.getLanguageItemsByKey("test_key");
        assertNotNull(languages);
        assertEquals("total languaes with test_key key", 2, languages.size());

        languages = configurationService.getLanguageItemsByKey("non_existent_key");
        assertNotNull(languages);
        assertTrue("total languaes with test_key key", languages.isEmpty());
    }

    @Test
    public void testUpdateLanguageItem() {
        Language language = languageDAO.findById(999999);
        language.setLocale("de");
        configurationService.updateLanguageItem(language);
        language = languageDAO.findById(999999);
        assertEquals("locale", "de", language.getLocale());
    }

    @Test
    public void testRemoveLanguageKey() {
        Language language1 = new Language("ua", "key1", "value1");
        Language language2 = new Language("ru", "key1", "value2");
        languageDAO.save(language1);
        languageDAO.save(language2);
        assertNotNull(language1.getId());
        assertNotNull(language2.getId());

        configurationService.removeLanguageKey("key1");
        language1 = languageDAO.findById(language1.getId());
        language2 = languageDAO.findById(language2.getId());

        assertNull("removed language", language1);
        assertNull("removed language", language2);
    }

    @Test
    public void testRemoveLanguageItem() {
        Language language = new Language("ua", "my_key", "my_value");
        languageDAO.save(language);
        Integer id = language.getId();
        assertNotNull(id);

        configurationService.removeLanguageItem(language);
        language = languageDAO.findById(id);
        assertNull("removed language", language);
    }

    @Test
    public void testGetSortedKeys() {
        String[] keys = configurationService.getSortedKeys();
        assertNotNull(keys);
        assertEquals("total keys", 2, keys.length); // search is done on a base locale
    }

    @Test

    public void testGetDefinedKeys() {
        Map<String, String> keyMap = configurationService.getDefinedKeys("test_locale");
        assertNotNull(keyMap);
        assertEquals("total keys", 1, keyMap.size());

        keyMap = configurationService.getDefinedKeys("undefined_locale");
        assertNotNull(keyMap);
        assertEquals("total keys", 0, keyMap.size());

    }

    @Test
    public void testGetDefinedKeysAsArray() {
        List<NameValuePair> keyMap = configurationService.getDefinedKeysAsArray("test_locale");
        assertNotNull(keyMap);
        assertEquals("total keys", 1, keyMap.size());

        keyMap = configurationService.getDefinedKeysAsArray("undefined_locale");
        assertNotNull(keyMap);
        assertEquals("total keys", 0, keyMap.size());

    }

    @Test
    public void testGetNumberDefinedKeys() {
        int totalKeys = configurationService.getNumberDefinedKeys("test_locale");
        assertEquals("total keys", 1, totalKeys);

        totalKeys = configurationService.getNumberDefinedKeys("undefined_locale");
        assertEquals("total keys", 0, totalKeys);

    }

    @Test
    public void testGetAvailableLanguages() {
        Map<String, List<String>> availableLanguages = configurationService.getAvailableLanguages();
        assertNotNull(availableLanguages);

        // languagebean_dataset.xml contains one entry, why we got 0 here?
        assertEquals("available languages", 1, availableLanguages.size());

    }

    @Test
    public void testGetNumberAvailableLanguages() {
        int availableLanguages = configurationService.getNumberAvailableLanguages();

        // languagebean_dataset.xml contains one entry, why we got 0 here?
        assertEquals("available languages", 1, availableLanguages);

    }

    @Test
    public void testUpdateLanguage() {
        Language language = languageDAO.findById(999999);
        language.setResourceValue("brand_new_value");

        List<Language> items = new ArrayList<Language>();
        items.add(language);

        // FIXME: what's the purpose of passing locale here?
        configurationService.updateLanguage(Locale.ENGLISH, items);

        language = languageDAO.findById(999999);
        assertNotNull(language);
        assertEquals("resource value", "brand_new_value", language.getResourceValue());

    }


    @Test
    public void testGetLanguage() {
        List<Language> languages = configurationService.getLanguage(new Locale("test_locale"));
        assertNotNull(languages);
        assertEquals("total languages with test_locale", 1, languages.size());

        languages = configurationService.getLanguage(new Locale("undefined_locale"));
        assertNotNull(languages);
        assertEquals("total languages with undefined_locale", 0, languages.size());

    }

    @Override
    public void onTearDown() throws Exception {
        super.onTearDown();
        SystemConfigurationUtilities.initializeAllLanguages(configurationService, false);
        configurationService.initializeConfiguration();
    }

    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();

        configurationService = (ConfigurationService) applicationContext
                .getBean("configurationService");
        configurationProperties = (Properties) applicationContext
                .getBean("configurationProperties");

        this.configurationDAO = (ConfigurationDAO) applicationContext.getBean("configurationDAO");
        this.projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
        this.projectScriptDAO = (ProjectScriptDAO) applicationContext.getBean("projectScriptDAO");
        this.workflowScriptDAO = (WorkflowScriptDAO) applicationContext.getBean("workflowScriptDAO");
        this.customFieldDAO = (CustomFieldDAO) applicationContext.getBean("customFieldDAO");
        this.customFieldValueDAO = (CustomFieldValueDAO) applicationContext.getBean("customFieldValueDAO");
        this.languageDAO = (LanguageDAO) applicationContext.getBean("languageDAO");


    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/configurationbean_dataset.xml",
                "dataset/workflowscriptbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/projectscriptbean_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/customfieldbean_dataset.xml",
                "dataset/customfieldvaluebean_dataset.xml",

        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
