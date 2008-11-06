package org.itracker.web.actions.admin.attachment;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.IssueAttachment;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.springframework.web.bind.ServletRequestUtils;

public class DownloadAttachmentAction extends ItrackerBaseAction {

	private static final Logger log = Logger.getLogger(DownloadAttachmentAction.class);
	
	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Integer attachmentID = ServletRequestUtils.getIntParameter(request, "id");

		IssueService issueService = getITrackerServices().getIssueService();

		IssueAttachment attachment = issueService.getIssueAttachment(attachmentID);

		if (attachment.getFileData() == null) {
			ActionMessages errors = new ActionMessages();

			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingattachmentdata"));

			saveErrors(request, errors);

			return actionMapping.findForward("error_page");
		}

		response.setContentType(attachment.getType());
		response.setHeader("Content-Disposition", "inline; filename=" + attachment.getOriginalFileName() + "");
		ServletOutputStream outputStream = response.getOutputStream();
		log.debug("Displaying attachment " + attachment.getId() + " of type " + attachment.getType()
				+ " to client.  Attachment size: " + attachment.getFileData().length);

		outputStream.write(attachment.getFileData());

		return null;

	}

}
