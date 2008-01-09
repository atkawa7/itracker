package org.itracker.web.actions.admin.attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.IssueAttachment;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;



public class ListAttachmentsAction extends ItrackerBaseAction {

	public ListAttachmentsAction() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			super.executeAlways(mapping,form,request,response);
			
			IssueService issueService = this.getITrackerServices().getIssueService(); 
			boolean hasAttachments;
			long sizeOfAllAttachments = 0;
			
                        List<IssueAttachment> attachments = new ArrayList<IssueAttachment>();
			try {
				attachments = issueService.getAllIssueAttachments();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (attachments.size() == 0) {
				hasAttachments = false;
			} else {
				hasAttachments = true;
                // TODO: Temporarily disabled sorting as it causes an NPE.
                // Some issue status are null
//                Collections.sort(attachments);

                for (IssueAttachment issueAttachment: attachments){
					sizeOfAllAttachments += issueAttachment.getSize();
					}
				if(sizeOfAllAttachments>0){
					sizeOfAllAttachments = sizeOfAllAttachments / 1024;			
				}
			}
			
			String pageTitleKey = "itracker.web.admin.listattachments.title"; 
			String pageTitleArg = "";			
			request.setAttribute("pageTitleKey",pageTitleKey); 
			request.setAttribute("pageTitleArg",pageTitleArg); 
	
			request.setAttribute("sizeOfAllAttachements", new Long(sizeOfAllAttachments));
			request.setAttribute("ih",issueService);
			request.setAttribute("hasAttachements",new Boolean(hasAttachments));
			request.setAttribute("attachments",attachments);
			return mapping.findForward("listattachments");
		 
	}
	
}
