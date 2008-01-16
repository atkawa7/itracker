package org.itracker.web.actions.admin.attachment;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.IssueAttachment;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


public class ListAttachmentsAction extends ItrackerBaseAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

        boolean hasAttachments = false;
        long sizeOfAllAttachments = 0;

        super.executeAlways(mapping, form, request, response);

        IssueService issueService = this.getITrackerServices().getIssueService();

        List<IssueAttachment> attachments = new ArrayList<IssueAttachment>();
        try {
            attachments = issueService.getAllIssueAttachments();
        } catch (Exception e) {
            // TODO: Do we just drown the exception?
            e.printStackTrace();
        }

        if( attachments.size() > 0 ) {

            hasAttachments = true;
            // TODO: Temporarily disabled sorting as it causes an NPE.
            // Some issue status are null
//                Collections.sort(attachments);

            for (IssueAttachment issueAttachment : attachments) {
                sizeOfAllAttachments += issueAttachment.getSize();
            }

            if (sizeOfAllAttachments > 0) {
                sizeOfAllAttachments = sizeOfAllAttachments / 1024;
            }

        }

        String pageTitleKey = "itracker.web.admin.listattachments.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        request.setAttribute("sizeOfAllAttachments", sizeOfAllAttachments);
        request.setAttribute("ih", issueService);
        request.setAttribute("hasAttachments", hasAttachments);
        request.setAttribute("attachments", attachments);

        return mapping.findForward("listattachments");

    }

}
