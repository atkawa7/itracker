package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class IssueExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        IssueException e = new IssueException();
        assertTrue(e instanceof Exception);

        e = new IssueException("my_message");
        assertEquals("e.message", "my_message", e.getMessage());

        e = new IssueException("my_message", "my_type");
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.type", "my_type", e.getType());

        Throwable cause = new Throwable();
        e = new IssueException("my_message", cause);
        assertEquals("e.message", "my_message", e.getMessage());
        assertSame("e.cause", cause, e.getCause());

        e = new IssueException("my_message", "my_type", cause);
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.type", "my_type", e.getType());
        assertSame("e.cause", cause, e.getCause());


    }

    @Test
    public void testSetType() {
        IssueException e = new IssueException();
        e.setType("my_type");
        assertEquals("e.type", "my_type", e.getType());
    }

}
