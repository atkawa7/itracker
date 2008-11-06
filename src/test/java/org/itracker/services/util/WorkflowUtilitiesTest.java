/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.services.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.junit.Ignore;
import org.junit.Test;
import org.itracker.model.NameValuePair;

/**
 *
 * @author seas
 */
public class WorkflowUtilitiesTest extends AbstractDependencyInjectionTest {



    /**
     * Verifies WorkflowUtilities.getEventName
     */
    @Test
    @Ignore
    public void testGetEventName() {
        // testing a case of missing key
        doTestGetEventName(null,
                999, "MISSING KEY: itracker.workflow.field.event.999");
        
        // "On Populate"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONPOPULATE, "On Populate");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONPOPULATE, "On Populate");
        doTestGetEventName(new Locale("test_locale"),
                WorkflowUtilities.EVENT_FIELD_ONPOPULATE, "test_value");

        // "On Sort"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONSORT, "On Sort");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONSORT, "On Sort");
        doTestGetEventName(new Locale("test_locale"),
                WorkflowUtilities.EVENT_FIELD_ONSORT, "On Sort");

        // "On SetDefault"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, "On SetDefault");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, "On SetDefault");
        doTestGetEventName(new Locale("test_locale"),
                WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, "On SetDefault");

        // "On Validate"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, "On Validate");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, "On Validate");
        doTestGetEventName(new Locale("test_locale"),
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, "On Validate");

        // "On PreSubmit"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, "On PreSubmit");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, "On PreSubmit");
        doTestGetEventName(new Locale("test_locale"),
                WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, "On PreSubmit");

        // "On PostSubmit"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, "On PostSubmit");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, "On PostSubmit");
        doTestGetEventName(new Locale("test_locale"),
                WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, "On PostSubmit");
    }

   
    /**
     * Verifies WorkflowUtilities.getEvents
     */
    @Test
    @Ignore
    public void testGetEvents() {
        doTestGetEvents(null,
                new NameValuePair[]{
                    new NameValuePair("On Populate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)),
                    new NameValuePair("On Sort",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)),
                    new NameValuePair("On SetDefault",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)),
                    new NameValuePair("On Validate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)),
                    new NameValuePair("On PreSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)),
                    new NameValuePair("On PostSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT))
                });
        doTestGetEvents(new Locale(ITrackerResources.BASE_LOCALE),
                new NameValuePair[]{
                    new NameValuePair("On Populate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)),
                    new NameValuePair("On Sort",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)),
                    new NameValuePair("On SetDefault",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)),
                    new NameValuePair("On Validate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)),
                    new NameValuePair("On PreSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)),
                    new NameValuePair("On PostSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT))
                });
        doTestGetEvents(new Locale("test_locale"),
                new NameValuePair[]{
                    new NameValuePair("test_value",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)),
                    new NameValuePair("On Sort",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)),
                    new NameValuePair("On SetDefault",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)),
                    new NameValuePair("On Validate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)),
                    new NameValuePair("On PreSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)),
                    new NameValuePair("On PostSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT))
                });
    }
    
    /**
     * Verifies WorkflowUtilities.getListOptions
     */
    @Test
    public void testGetListOptions() {
        final List<NameValuePair> testedList = new Vector<NameValuePair>();
        testedList.add(new NameValuePair("On Populate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)));
        testedList.add(new NameValuePair("On Sort",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)));
        testedList.add(new NameValuePair("On SetDefault",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)));
        testedList.add(new NameValuePair("On Validate",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)));
        testedList.add(new NameValuePair("On PreSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)));
        testedList.add(new NameValuePair("On PostSubmit",
                    Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT)));
        final Map<Integer, List<NameValuePair>> testedListOptions =
                new HashMap<Integer, List<NameValuePair>>();
        testedListOptions.put(888, testedList);
        assertTrue(testedList == WorkflowUtilities.getListOptions(testedListOptions, 888));
        assertTrue(0 == WorkflowUtilities.getListOptions(testedListOptions, 999).size());
    }

    /**
     * Verifies WorkflowUtilities.processFieldScripts
     */
    @Test
    @Ignore
    public void testProcessFieldScripts() {
        // @todo
    }

    private void doTestGetEvents(final Locale locale,
            final NameValuePair[] expected) {
        final NameValuePair[] actual = WorkflowUtilities.getEvents(locale);
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                if (found = nvpActual.equals(nvpExpected)) {
                    break;
                }
            }
            assertTrue("Pair of " +
                    "(" + nvpExpected.getName() + "," +
                    nvpExpected.getValue() + ") " +
                    "is missing from actual results.",
                    found);
        }
        assertTrue("Size of expected and actual results do not match.",
                expected.length == actual.length);
    }

    private void doTestGetEventName(final Locale locale,
            final int eventId, final String expected) {
        final String actual =
                WorkflowUtilities.getEventName(eventId, locale);
//        junit.framework.AssertionFailedError: Actual (On Populate) and expected (test_value) eventNames should match for test_locale locale.

        
//        assertTrue("Actual (" + (null != actual ? actual : "null") + ") " +
//                "and expected (" + (null != expected ? expected : "null") + ") " +
//                "eventNames should match for " + locale + " locale.",
//                (null == expected && null == actual) ||
//                (null != expected && null != actual) && expected.equals(actual));
    
        assertEquals("WorkflowUtilities.getEventName(" + eventId + ", " + locale + ")", expected, actual);
    }
    
    
    /**
     * Defines a set of datafiles to be uploaded into database.
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
            "dataset/languagebean_dataset.xml"};
    }

    /**
     * Defines a simple configuration, required for running tests.
     * @return an array of references to configuration files.
     */
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

    
}
