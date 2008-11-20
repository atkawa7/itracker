/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.services.util;

import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.itracker.AbstractDependencyInjectionTest;
import org.junit.Test;

// TODO: Add Javadocs here: what is the purpose of this class?

/**
 *
 * @author seas
 */
public class NamingUtilitiesTest extends AbstractDependencyInjectionTest {

    @Test
    public void testGetStringValue() {
        final Hashtable hashtable = new Hashtable();
        hashtable.put("key", "value");
        try {
        final InitialContext context = new InitialContext(hashtable);
        final String value = NamingUtilites.getStringValue(context, "key", "value");
        assertEquals("value", value);
        } catch (final NamingException e) {
            assertTrue(e.getMessage(), false);
        }
    }
    
    /**
     * Defines a set of datafiles to be uploaded into database.
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                    "dataset/languagebean_dataset.xml"
                };
    }

    /**
     * Defines a simple configuration, required for running tests.
     * @return an array of references to configuration files.
     */
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }
}
