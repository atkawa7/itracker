package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class IssueSearchExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        IssueSearchException e = new IssueSearchException();
        assertTrue(e instanceof Exception);

        e = new IssueSearchException("my_message");
        assertEquals("e.message", "my_message", e.getMessage());

        e = new IssueSearchException("my_message", 1);
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.type", 1, e.getType());

    }

    @Test
    public void testSetType() {
        IssueSearchException e = new IssueSearchException();
        e.setType(1);
        assertEquals("e.type", 1, e.getType());
    }
}
