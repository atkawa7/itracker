package org.itracker.web.forms;

import junit.framework.TestCase;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Iterator;

public class UserFormTest extends TestCase {

    private UserForm userForm;

    public void testValidateMismatchPassword() {
        userForm.setAction("edit");
        userForm.setCurrPassword("password1");
        userForm.setPassword("password2");
        userForm.setConfPassword("password3");

        ActionErrors errors = userForm.validate(new ActionMapping(), new MockHttpServletRequest());

        assertNotNull(errors);
        assertEquals(1, errors.size());

        Iterator iterator = errors.get(ActionMessages.GLOBAL_MESSAGE);
        ActionMessage message = (ActionMessage) iterator.next();
        assertEquals("itracker.web.error.matchingpass", message.getKey());
    }

    protected void setUp() throws Exception {
        userForm = new UserForm();
    }

}
