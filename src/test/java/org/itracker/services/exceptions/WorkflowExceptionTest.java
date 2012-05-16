package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class WorkflowExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        WorkflowException e = new WorkflowException();
        assertTrue(e instanceof Exception);

        e = new WorkflowException("my_message");
        assertEquals("e.message", "my_message", e.getMessage());

        e = new WorkflowException(1);
        assertEquals("e.type", 1, e.getType());

        e = new WorkflowException("my_message", 1);
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.type", 1, e.getType());

    }

    @Test
    public void testSetType() {
        WorkflowException e = new WorkflowException();
        e.setType(1);
        assertEquals("e.type", 1, e.getType());
    }

}
