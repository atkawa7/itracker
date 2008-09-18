package org.itracker.web.actions.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.ptos.IssuePTO;

public class ListIssuesAction extends ItrackerBaseAction {

	/**
	 * Logger for ListIssuesAction
	 */
	private static final Logger log = Logger.getLogger(ListIssuesAction.class);
	
	// TODO check for other occurences for this constants and maybe place somewhere else?
	public static final String LIST_ISSUES_PAGE_TITLE_KEY = "itracker.web.listissues.title";
	// request attribute names
	public static final String ATT_NAME_PAGE_TITLE_KEY = "pageTitleKey";
	public static final String ATT_NAME_PAGE_TITLE_ARG = "pageTitleArg";
	public static final String ATT_NAME_HAS_ORDER_PARAM = "hasOrderParam";
	public static final String ATT_NAME_START = "start";

	public static final String ATT_NAME_ORDER_PARAM = "orderParam";

	public static final String ATT_NAME_ISSUE_PTOS = "issuePTOs";
	public static final String ATT_NAME_PROJECT = "project";
	public static final String ATT_NAME_PROJCET_ID = "projectId";
	public static final String ATT_NAME_HAS_ISSUES = "hasIssues";
	public static final String ATT_NAME_HAS_VIEW_ALL = "hasViewAll";
	public static final String ATT_NAME_NUM_VIEWABLE = "numViewable";
	public static final String ATT_NAME_K = "k";
	public static final String ATT_NAME_UNASSIGNED = "itracker_web_generic_unassigned";
	
	public static final String SES_ATT_NAME_CURRENT__USER = "currUser";
	public static final String SES_ATT_NAME_PREFERENCES = "preferences";
	public static final String PARAM_NAME_PROJECT_ID = "projectId";

	
	public static final String PARAM_NAME_START = "start";
	public static final String PARAM_NAME_ORDER = "order";
	
	public static final String ORDER_KEY_ID = "id";
	public static final String ORDER_KEY_SEVERITY = "sev";
	public static final String ORDER_KEY_STATUS = "stat";
	public static final String ORDER_KEY_LAST_MODIFIED = "lm";
	public static final String ORDER_KEY_OWNER_AND_STATUS = "own";
	
	
	public static final String RES_KEY_UNASSIGNED = "itracker.web.generic.unassigned";
	public static final String RES_KEY_UNKNOWN = "itracker.web.generic.unknown";
	
	public static final String FWD_LIST_ISSUES = "list_issues";
	
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
//        super.executeAlways(mapping,form,request,response);
        Locale locale = getLocale(request);
        // get the services
        IssueService issueService = this.getITrackerServices().getIssueService();
        ProjectService projectService = this.getITrackerServices().getProjectService();
        User um = (User)request.getSession().getAttribute(SES_ATT_NAME_CURRENT__USER);
        Integer currUserId = um.getId();
        HttpSession session = request.getSession(true);
        Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
        // get the request parameters
        UserPreferences userPrefs = (UserPreferences) request.getSession().getAttribute(SES_ATT_NAME_PREFERENCES);
        Integer projectId = Integer.valueOf(request.getParameter(PARAM_NAME_PROJECT_ID) == null ? "-1" : (request.getParameter(PARAM_NAME_PROJECT_ID)));
        log.info("execute: " + PARAM_NAME_PROJECT_ID + " was: " + projectId);
        
        // get some values
        int status = (userPrefs.getShowClosedOnIssueList() ? IssueUtilities.STATUS_END : IssueUtilities.STATUS_CLOSED);
        
        // do some service calls
        Project project = projectService.getProject(projectId);
        log.info("execute: projectModel_Name: " + project.getName());
        List<Issue> listIssues = issueService.getIssuesByProjectId(projectId, status);
        log.info("execute: issues found for this project: " + listIssues.size());
        
        // prepare PTOs
        List<IssuePTO> issuePTOs = new ArrayList<IssuePTO>();
        
        // do some more order processing
        boolean hasOrderParam = false;
        String order = "";
        String orderParam = request.getParameter(PARAM_NAME_ORDER);
        if(orderParam == null || "".equals(orderParam)) {
            order = userPrefs.getSortColumnOnIssueList();
            orderParam = "";
        } else {
            hasOrderParam = true;
            order = orderParam;
        }
        
        if(ORDER_KEY_ID.equals(order)) {
            Collections.sort(listIssues, Issue.ID_COMPARATOR);
        } else if(ORDER_KEY_SEVERITY.equals(order)) {
            Collections.sort(listIssues, Issue.SEVERITY_COMPARATOR);
        } else if(ORDER_KEY_STATUS.equals(order)) {
            Collections.sort(listIssues, Issue.STATUS_COMPARATOR);
        } else if(ORDER_KEY_LAST_MODIFIED.equals(order)) {
            Collections.sort(listIssues, Collections.reverseOrder(
                    Issue.LAST_MODIFIED_DATE_COMPARATOR));
        } else if(ORDER_KEY_OWNER_AND_STATUS.equals(order)) {
            Collections.sort(listIssues, Issue.OWNER_AND_STATUS_COMPARATOR);
        } else {
            Collections.sort(listIssues, Issue.STATUS_COMPARATOR);
        }
        
        int start = 0;
        String startString = request.getParameter(PARAM_NAME_START);
        if(startString != null) {
            try {
                start = Integer.parseInt(startString);
            } catch(NumberFormatException nfe) {
            }
        }
        int numViewable = 0;
        boolean hasIssues = false;
        boolean hasViewAll = UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_VIEW_ALL);
        
        if(hasViewAll) {
            numViewable = listIssues.size();
        } else {
            for(int i = 0; i < listIssues.size(); i++) {
                if(IssueUtilities.canViewIssue(listIssues.get(i), currUserId, userPermissions)) {
                    numViewable++;
                }
            }
        }
        int row = 0;
        int k = 0;
        Iterator<Issue> issuesIt = listIssues.iterator();
        // start copying from Models to PTOs
        Issue issue;
        IssuePTO issuePTO;
        String statusLocalizedString, severityLocalizedString, componentsSize;
        while (issuesIt.hasNext()) {
        	
            issue = issuesIt.next();
            issuePTO = new IssuePTO(issue);
            
            statusLocalizedString=IssueUtilities.getStatusName(issue.getStatus(), locale);
            severityLocalizedString = IssueUtilities.getSeverityName(issue.getSeverity(), locale) ;
            if (issue.getComponents().size() == 0) {
				componentsSize = ITrackerResources.getString(
						RES_KEY_UNKNOWN, locale);
			} else {
				componentsSize = issue.getComponents().get(0).getName()
						+ (issue.getComponents().size() > 1 ? " (+)" : "");
			}
            issuePTO.setStatusLocalizedString(statusLocalizedString);
            issuePTO.setSeverityLocalizedString(severityLocalizedString);
            issuePTO.setComponentsSize(componentsSize);
            if(issue.getOwner()==null) {
            	issuePTO.setUnassigned(true);
            }
            
            if(project.getStatus() == Status.ACTIVE && ! IssueUtilities.hasIssueNotification(issue, project, currUserId)) {
                issuePTO.setUserHasIssueNotification(true);
            }
            if(project.getStatus() == Status.ACTIVE) {
                if(IssueUtilities.canEditIssue(issue, currUserId, userPermissions)) {
                    issuePTO.setUserCanEdit(true);
                }
            }
            
            // TODO: check from here...
            if(! hasViewAll && ! IssueUtilities.canViewIssue(issue, currUserId, userPermissions)) {
                continue;
            }
            hasIssues = true;
            if(start > 0 && k < start) {
                k++;
                continue;
            }
            if(userPrefs.getNumItemsOnIssueList() > 0 && row >= userPrefs.getNumItemsOnIssueList()) {
                break;
            }
            row++;
            // TODO: check to here...
            
            issuePTOs.add(issuePTO);
        }
        
        // populate the request
        request.setAttribute(ATT_NAME_HAS_ORDER_PARAM, new Boolean(hasOrderParam));
        request.setAttribute(ATT_NAME_START, start);
        request.setAttribute(ATT_NAME_ORDER_PARAM, orderParam);
        request.setAttribute(ATT_NAME_ISSUE_PTOS, issuePTOs);
        request.setAttribute(ATT_NAME_PROJECT, project);
        request.setAttribute(ATT_NAME_PROJCET_ID, projectId);
        request.setAttribute(ATT_NAME_HAS_ISSUES, hasIssues);
        request.setAttribute(ATT_NAME_HAS_VIEW_ALL, hasViewAll);
        request.setAttribute(ATT_NAME_NUM_VIEWABLE, numViewable);
        request.setAttribute(ATT_NAME_K, k);
         
        request.setAttribute(ATT_NAME_UNASSIGNED, ITrackerResources.getString(RES_KEY_UNASSIGNED, locale));
        String pageTitleArg = project.getName();
        request.setAttribute(ATT_NAME_PAGE_TITLE_KEY, LIST_ISSUES_PAGE_TITLE_KEY);
        request.setAttribute(ATT_NAME_PAGE_TITLE_ARG, pageTitleArg);
        
        log.info("execute: Forward was: " + FWD_LIST_ISSUES);
        return mapping.findForward(FWD_LIST_ISSUES);
    }
    
    public ListIssuesAction() {
        super();
        if (log.isDebugEnabled()) {
        	log.debug("ListIssuesAction created");
        }
    }
    
}
