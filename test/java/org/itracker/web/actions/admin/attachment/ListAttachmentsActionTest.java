package org.itracker.web.actions.admin.attachment;

import org.apache.struts.action.ActionForward;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.IssueAttachment;
import org.itracker.model.User;
import org.itracker.persistence.dao.IssueAttachmentDAO;
import org.itracker.web.struts.mock.MockActionMapping;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

public class ListAttachmentsActionTest extends AbstractDependencyInjectionTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionMapping actionMapping;
    private IssueAttachmentDAO issueAttachmentDAO;

    @Test
    public void testBasicCall() throws Exception {

        ListAttachmentsAction listAttachmentsAction = new ListAttachmentsAction();
        ActionForward actionForward = listAttachmentsAction.execute(actionMapping, null, request, response);

        assertNotNull(actionForward);
        assertEquals("listattachments", actionForward.getPath());

    }

    @Test
    public void testAttributeValues() throws Exception {

        ListAttachmentsAction listAttachmentsAction = new ListAttachmentsAction();
        listAttachmentsAction.execute(actionMapping, null, request, response);

        assertEquals("itracker.web.admin.listattachments.title", request.getAttribute("pageTitleKey"));
        assertEquals("", request.getAttribute("pageTitleArg"));

        // TODO: Rename attribute key
        Long sizeOfAllAttachments = (Long) request.getAttribute("sizeOfAllAttachements");
        assertEquals(8L, sizeOfAllAttachments.longValue());

        // TODO: Rename attribute key
        assertTrue((Boolean) request.getAttribute("hasAttachements"));

        List attachments = (List) request.getAttribute("attachments");

        assertEquals(4, attachments.size());

    }

    @Test
    public void testAttachmentDetails() throws Exception {

        ListAttachmentsAction listAttachmentsAction = new ListAttachmentsAction();
        listAttachmentsAction.execute(actionMapping, null, request, response);

        List<IssueAttachment> attachments = (List<IssueAttachment>) request.getAttribute("attachments");

        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(1), attachments);
        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(2), attachments);
        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(3), attachments);
        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(4), attachments);

    }

    private void assertContainsAttachment(IssueAttachment attachment, List<IssueAttachment> attachments) {

        if (!attachments.contains(attachment)) {
            fail("Attachment not found in the list.");
        }

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueAttachmentDAO = (IssueAttachmentDAO) applicationContext.getBean("issueAttachmentDAO");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        actionMapping = new MockActionMapping();

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
