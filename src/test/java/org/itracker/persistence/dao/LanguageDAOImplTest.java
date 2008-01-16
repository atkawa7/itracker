package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Language;
import org.junit.Test;

public class LanguageDAOImplTest extends AbstractDependencyInjectionTest {

    private LanguageDAO languageDAO;

    @Test
    public void findByID() {

        Language language = languageDAO.findById(1);

        assertNotNull(language);
        assertLanguageEquals(language, "test_locale", "test_key", "test_value");

    }

    @Test
    public void findByKeyAndLocale() {

        Language language = languageDAO.findByKeyAndLocale(
                "test_key", "test_locale" );

        assertNotNull( language );

        assertLanguageEquals( language, "test_locale",
                              "test_key", "test_value" );

    }

    @Test
    public void findByKey() {

        List<Language> languages = languageDAO.findByKey( "test_key" );

        assertNotNull( languages );

        assertEquals( 1, languages.size() );

        Language language = languages.get( 0 );

        assertLanguageEquals( language, "test_locale",
                              "test_key", "test_value" );

    }

    @Test
    public void findByLocale() {

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

    @Override
    public void onSetUp() throws Exception {
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
