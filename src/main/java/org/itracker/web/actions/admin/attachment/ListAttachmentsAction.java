package org.itracker.web.actions.admin.attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.IssueAttachment;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListAttachmentsAction extends ItrackerBaseAction {

	private static final Logger log = Logger.getLogger(ListAttachmentsAction.class);
	
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

        boolean hasAttachments = false;
        long sizeOfAllAttachments = 0;

        IssueService issueService = this.getITrackerServices().getIssueService();

        List<IssueAttachment> attachments = new ArrayList<IssueAttachment>();
        try {
            attachments = issueService.getAllIssueAttachments();
        } catch (Exception e) {
        	log.error("execute: failed to get all attachments", e);
        	throw e;
        }

        if( attachments.size() > 0 ) {

            hasAttachments = true;
            
            Collections.sort(attachments, IssueAttachment.ID_COMPARATOR);

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
