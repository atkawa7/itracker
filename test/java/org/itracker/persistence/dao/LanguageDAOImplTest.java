package org.itracker.persistence.dao;

import java.util.Date;
import java.util.List;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Language;

public class LanguageDAOImplTest extends AbstractDependencyInjectionTest {

    private LanguageDAO languageDAO;

    public void testFindByKeyAndLocale() {

        Language language = languageDAO.findByKeyAndLocale(
                "test_key", "test_locale" );

        assertNotNull( language );
        assertEquals( new Integer( 999 ), language.getId() );

        Date date = new Date( 1136044800000L );

        assertEquals( date, language.getCreateDate() );
        assertEquals( date, language.getLastModifiedDate() );

    }

    public void testFindByKey() {

        List<Language> languages = languageDAO.findByKey( "itracker.dateformat.full" );

        assertNotNull( languages );

        // TODO: Need more assertions

    }

    public void testFindByLocale() {

        List<Language> languages = languageDAO.findByLocale( "test_locale" );

        assertNotNull( languages );

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
