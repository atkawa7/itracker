package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class ReportExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        ReportException e = new ReportException();
        assertTrue(e instanceof Exception);

        e = new ReportException("my_message");
        assertEquals("e.message", "my_message", e.getMessage());

        Throwable cause = new Throwable();
        e = new ReportException(cause);
        assertSame("e.cause", cause, e.getCause());

        e = new ReportException("my_message", "my_key");
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.errorKey", "my_key", e.getErrorKey());

    }
}
