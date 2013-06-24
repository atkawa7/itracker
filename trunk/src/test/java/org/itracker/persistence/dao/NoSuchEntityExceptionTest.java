package org.itracker.persistence.dao;

import junit.framework.TestCase;
import org.junit.Test;

public class NoSuchEntityExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        NoSuchEntityException ex = new NoSuchEntityException("my_message");
        assertEquals("ex.message", "my_message", ex.getMessage());

        ex = new NoSuchEntityException("my_message", new Throwable());
        assertNotNull("ex.message", ex.getMessage());
        assertTrue("ex.message", ex.getMessage().indexOf("my_message") >= 0);


    }
}
