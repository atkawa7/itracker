/**
 * 
 */
package org.itracker.web.ptos;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.itracker.model.CustomField;
import org.itracker.model.NameValuePair;
import org.itracker.model.Project;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.util.Constants;

/**
 * @author Venkoba
 * 
 */
public class CreateIssuePTO {
	@SuppressWarnings("unchecked")
	public static void setupCreateIssue(HttpServletRequest req) {
		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
		Map<Integer, List<NameValuePair>> listOptions = (Map<Integer, List<NameValuePair>>) session
				.getAttribute(Constants.LIST_OPTIONS_KEY);
		List<NameValuePair> possibleOwners = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_OWNER);
		List<NameValuePair> severities = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_SEVERITY);
		List<NameValuePair> possibleCreators = WorkflowUtilities
				.getListOptions(listOptions, IssueUtilities.FIELD_CREATOR);
		List<NameValuePair> components = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_COMPONENTS);
		List<NameValuePair> versions = WorkflowUtilities.getListOptions(
				listOptions, IssueUtilities.FIELD_VERSIONS);
		List<CustomField> projectFields = project.getCustomFields();
		if (projectFields != null && projectFields.size() > 0) {
			Collections.sort(projectFields, CustomField.ID_COMPARATOR);
		}
		String wrap = "soft";
        if(ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, project.getOptions())) {
           wrap = "hard";
        }
		/*
		 * Get the status name for the current locale and set in request
		 */
		String pageTitleKey = "itracker.web.createissue.title";
		String pageTitleArg = project.getName();
		req.setAttribute("pageTitleKey", pageTitleKey);
		req.setAttribute("pageTitleArg", pageTitleArg);
		
		req.setAttribute("statusName", IssueUtilities.getStatusName(
				IssueUtilities.STATUS_NEW, (java.util.Locale) session
						.getAttribute("currLocale")));
		req.setAttribute("hasAttachmentOption", !ProjectUtilities.hasOption(
				ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions()));
		req.setAttribute("possibleOwners", possibleOwners);
		req.setAttribute("severities", severities);
		req.setAttribute("possibleCreators", possibleCreators);
		req.setAttribute("components", components);
		req.setAttribute("versions", versions);
		req.setAttribute("projectFields", projectFields);
		req.setAttribute("listOptions", listOptions);
		req.setAttribute("wrap", wrap);
	}

}
