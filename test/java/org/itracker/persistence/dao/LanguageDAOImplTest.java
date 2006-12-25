package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Language;

public class LanguageDAOImplTest extends AbstractDependencyInjectionTest {

    private LanguageDAO languageDAO;

    public void testFindByKeyAndLocale() {

        Language language = languageDAO.findByKeyAndLocale(
                "test_key", "test_locale" );

        assertNotNull( language );

        assertLanguageEquals( language, "test_locale",
                              "test_key", "test_value" );

    }

    public void testFindByKey() {

        List<Language> languages = languageDAO.findByKey( "test_key" );

        assertNotNull( languages );

        assertEquals( 1, languages.size() );

        Language language = languages.get( 0 );

        assertLanguageEquals( language, "test_locale",
                              "test_key", "test_value" );

    }

    public void testFindByLocale() {

        List<Language> languages = languageDAO.findByLocale( "test_locale" );

        assertNotNull( languages );

        assertEquals( 1, languages.size() );

        Language language = languages.get( 0 );

        assertLanguageEquals( language, "test_locale",
                              "test_key", "test_value" );

    }

    private void assertLanguageEquals( Language language,
                                       String locale,
                                       String key,
                                       String value ) {

        assertEquals( locale, language.getLocale() );
        assertEquals( key, language.getResourceKey() );
        assertEquals( value, language.getResourceValue() );

    }

    protected void onSetUp() throws Exception {
        super.onSetUp();

        languageDAO = (LanguageDAO)applicationContext.getBean( "languageDAO" );
    }

    protected String[] getDataSetFiles() {
        return new String[] {
                "dataset/languagebean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[] { "application-context.xml" };
    }

}
