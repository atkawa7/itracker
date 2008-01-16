package org.itracker.web.actions.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.executeAlways(mapping,form,request,response);
        Locale currLocale = super.getLocale(request);
        // get the services
        IssueService issueService = this.getITrackerServices().getIssueService();
        ProjectService projectService = this.getITrackerServices().getProjectService();
        User um = (User)request.getSession().getAttribute("currUser");
        Integer currUserId = um.getId();
        HttpSession session = request.getSession(true);
        Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
        // get the request parameters
        UserPreferences userPrefs = (UserPreferences) request.getSession().getAttribute("preferences");
        Integer projectId = new Integer((request.getParameter("projectId") == null ? "-1" : (request.getParameter("projectId"))));
        logger.info("projectId: " +projectId);
        
        // get some values
        int status = (userPrefs.getShowClosedOnIssueList() ? IssueUtilities.STATUS_END : IssueUtilities.STATUS_CLOSED);
        
        // do some service calls
        Project project = projectService.getProject(projectId);
        logger.info("projectModel_Name: " +project.getName());
        List<Issue> listIssues = issueService.getIssuesByProjectId(projectId, status);
        logger.info("issues found for this project: " +listIssues.size());
        
        // prepare PTOs
        List<IssuePTO> issuePTOs = new ArrayList<IssuePTO>();
        
        // do some more order processing
        boolean hasOrderParam = false;
        String order = "";
        String orderParam = request.getParameter("order");
        if(orderParam == null || "".equals(orderParam)) {
            order = userPrefs.getSortColumnOnIssueList();
            orderParam = "";
        } else {
            hasOrderParam = true;
            order = orderParam;
        }
        
        if("id".equals(order)) {
            Collections.sort(listIssues, Issue.ID_COMPARATOR);
        } else if("sev".equals(order)) {
            Collections.sort(listIssues, Issue.SEVERITY_COMPARATOR);
        } else if("stat".equals(order)) {
            Collections.sort(listIssues, Issue.STATUS_COMPARATOR);
        } else if("lm".equals(order)) {
            Collections.sort(listIssues, Collections.reverseOrder(
                    Issue.LAST_MODIFIED_DATE_COMPARATOR));
        } else if("own".equals(order)) {
            Collections.sort(listIssues, Issue.OWNER_AND_STATUS_COMPARATOR);
        } else {
            Collections.sort(listIssues, Issue.STATUS_COMPARATOR);
        }
        
        int start = 0;
        String startString = request.getParameter("start");
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
        
        // start copying from Models to PTOs
        for(int i = 0; i < listIssues.size(); i++) {
            Issue issue = listIssues.get(i);
            IssuePTO issuePTO = new IssuePTO(issue);
            
            String statusLocalizedString=IssueUtilities.getStatusName(listIssues.get(i).getStatus(), currLocale);
            String severityLocalizedString = IssueUtilities.getSeverityName(listIssues.get(i).getSeverity(), currLocale) ;
            String componentsSize = (listIssues.get(i).getComponents().size() == 0 ? ITrackerResources.getString("itracker.web.generic.unknown", currLocale) : listIssues.get(i).getComponents().get(0).getName() + (listIssues.get(i).getComponents().size() > 1 ? " (+)" : ""));
            issuePTO.setStatusLocalizedString(statusLocalizedString);
            issuePTO.setSeverityLocalizedString(severityLocalizedString);
            issuePTO.setComponentsSize(componentsSize);
            if(issue.getOwner()==null) {
            	issuePTO.setUnassigned(true);
            }
            
            if(project.getStatus() == Status.ACTIVE && ! IssueUtilities.hasIssueNotification(listIssues.get(i), project, currUserId)) {
                issuePTO.setUserHasIssueNotification(true);
            }
            if(project.getStatus() == Status.ACTIVE) {
                if(IssueUtilities.canEditIssue(listIssues.get(i), currUserId, userPermissions)) {
                    issuePTO.setUserCanEdit(true);
                }
            }
            
            // TODO: check from here...
            if(! hasViewAll && ! IssueUtilities.canViewIssue(listIssues.get(i), currUserId, userPermissions)) {
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
        request.setAttribute("hasOrderParam", new Boolean(hasOrderParam));
        request.setAttribute("start", new Integer(start));
        request.setAttribute("orderParam", orderParam);
        request.setAttribute("issuePTOs",issuePTOs);
        request.setAttribute("project",project);
        request.setAttribute("projectId",projectId);
        request.setAttribute("hasIssues", new Boolean(hasIssues));
        request.setAttribute("hasViewAll", new Boolean(hasViewAll));
        request.setAttribute("numViewable", new Integer(numViewable));
        request.setAttribute("k", new Integer(k));
         
        request.setAttribute("itracker_web_generic_unassigned", ITrackerResources.getString("itracker.web.generic.unassigned", currLocale));
        String pageTitleKey = "itracker.web.listissues.title";
        String pageTitleArg = project.getName();
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
        logger.info("ListIssuesAction: Forward: list_issues");
        return mapping.findForward("list_issues");
    }
    
    public ListIssuesAction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}