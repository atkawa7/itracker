package org.itracker.web.actions.project;

import org.apache.struts.action.ActionForward;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.User;
import org.itracker.web.struts.mock.MockActionMapping;
import org.itracker.web.util.Constants;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class EditIssueActionTest extends AbstractDependencyInjectionTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionMapping actionMapping;

    @Test
    public void testBasicCall() throws Exception {

        EditIssueAction editIssueAction = new EditIssueAction();
        ActionForward actionForward = editIssueAction.execute(actionMapping, null, request, response);

        assertNotNull(actionForward);
        assertEquals("listprojects", actionForward.getPath());

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        actionMapping = new MockActionMapping();

        User user = new User();
        user.setLogin("asdf");
        MockHttpSession session = (MockHttpSession) request.getSession();
        session.setAttribute(Constants.USER_KEY, user);

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueattachmentbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
