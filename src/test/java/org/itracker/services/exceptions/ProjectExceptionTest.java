package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class ProjectExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        ProjectException e = new ProjectException();
        assertTrue(e instanceof Exception);

        e = new ProjectException("my_message");
        assertEquals("e.message", "my_message", e.getMessage());
    }

}
