package org.itracker.web.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.ptos.IssuePTO;

public class PortalHomeAction extends ItrackerBaseAction {
    
    static final Logger LOGGER = Logger.getLogger("org.itracker.PortalHomeAction");
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward forward = new ActionForward();
        LOGGER.info("Stepping up into the loginRouter method");
        
        // mayb wrong the next line... setting a default forward...
        ActionForward thisactionforward=mapping.findForward("portalhome");
        forward = super.loginRouter(mapping,form,request,response, thisactionforward);
        
        if (forward==null) {
            return forward;
        } else if (forward!=null) {
            
            LOGGER.info("Found forward, let's go and check if this forward is portalhome...");
            super.executeAlways(mapping,form,request,response);
            
            if (forward.getName().equals("portalhome")||forward.getName().equals("index")) {
                
                IssueService issueService = this.getITrackerServices().getIssueService();
                ProjectService projectService = this.getITrackerServices().getProjectService();
                UserService userService = this.getITrackerServices().getUserService();
                User currUser = (User)request.getSession().getAttribute("currUser");
                Locale currLocale = super.getLocale(request);
                Integer userId = currUser.getId();
                Map<Integer, Set<PermissionType>> permissions = 
                        (Map<Integer, Set<PermissionType>>)request.getSession().getAttribute("permissions");
                
                // GETTING AND SETTING USER PREFS AND HIDDEN SECTIONS ACCORDINGLY
                UserPreferences userPrefs = this.getITrackerServices().getUserService().getUserPreferencesByUserId(userId);
                
                int hiddenSections = 0;
                if(! "all".equalsIgnoreCase(request.getParameter("sections"))) {
                    hiddenSections = userPrefs.getHiddenIndexSections();
                }
                
                List<IssuePTO> createdIssuePTOs = new ArrayList<IssuePTO>();
                List<IssuePTO> ownedIssuePTOs = new ArrayList<IssuePTO>();
                List<IssuePTO> unassignedIssuePTOs = new ArrayList<IssuePTO>();
                List<IssuePTO> watchedIssuePTOs = new ArrayList<IssuePTO>();
       
                // POPULATING ISSUE MODELS
                final List<Issue> createdIssues;
                final List<Issue> ownedIssues;
                final List<Issue> unassignedIssues;
                final List<Issue> watchedIssues;
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
                    createdIssues  = new ArrayList<Issue>();
                } else {
                    createdIssues = issueService.getIssuesCreatedByUser(currUser.getId());
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
                    ownedIssues = new ArrayList<Issue>();
                } else {
                    ownedIssues = issueService.getIssuesOwnedByUser(currUser.getId());
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
                    unassignedIssues = new ArrayList<Issue>();
                } else {
                    unassignedIssues = issueService.getUnassignedIssues();
                }

                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
                    watchedIssues = new ArrayList<Issue>();
                } else {
                    watchedIssues = issueService.getIssuesWatchedByUser(currUser.getId());
                }
                
                // SORTING ISSUES ACCORDING TO USER PREFS
                if (null!=userPrefs) {
                    String order = userPrefs.getSortColumnOnIssueList();
                    
                    if("id".equals(order)) {
                        Collections.sort(createdIssues, Issue.ID_COMPARATOR);
                        Collections.sort(ownedIssues, Issue.ID_COMPARATOR);
                        Collections.sort(unassignedIssues, Issue.ID_COMPARATOR);
                        Collections.sort(watchedIssues, Issue.ID_COMPARATOR);
                    } else if("sev".equals(order)) {
                        Collections.sort(createdIssues, Issue.SEVERITY_COMPARATOR);
                        Collections.sort(ownedIssues, Issue.SEVERITY_COMPARATOR);
                        Collections.sort(unassignedIssues, Issue.SEVERITY_COMPARATOR);
                        Collections.sort(watchedIssues, Issue.SEVERITY_COMPARATOR);
                    } else if("stat".equals(order)) {
                        Collections.sort(createdIssues, Issue.STATUS_COMPARATOR);
                        Collections.sort(ownedIssues, Issue.SEVERITY_COMPARATOR);
                        Collections.sort(unassignedIssues, Issue.STATUS_COMPARATOR);
                        Collections.sort(watchedIssues,Issue.STATUS_COMPARATOR);
                    } else if("lm".equals(order)) {
                        Collections.sort(createdIssues, Issue.LAST_MODIFIED_DATE_COMPARATOR);
                        Collections.sort(ownedIssues, Issue.LAST_MODIFIED_DATE_COMPARATOR);
                        Collections.sort(unassignedIssues, Issue.LAST_MODIFIED_DATE_COMPARATOR);
                        Collections.sort(watchedIssues, Issue.LAST_MODIFIED_DATE_COMPARATOR);
                    } else if("own".equals(order)) {
                        Collections.sort(createdIssues, Issue.OWNER_AND_STATUS_COMPARATOR);
                        Collections.sort(ownedIssues, Issue.SEVERITY_COMPARATOR);
                        Collections.sort(unassignedIssues, Issue.OWNER_AND_STATUS_COMPARATOR);
                        Collections.sort(watchedIssues, Issue.OWNER_AND_STATUS_COMPARATOR);
                    } else {
                        Collections.sort(createdIssues, Issue.STATUS_COMPARATOR);
                        Collections.sort(ownedIssues, Issue.SEVERITY_COMPARATOR);
                        Collections.sort(unassignedIssues, Issue.STATUS_COMPARATOR);
                        Collections.sort(watchedIssues, Issue.STATUS_COMPARATOR);
                    }
                }
                
                // COPYING MODELS INTO PTOS
                
                // SETTING USER PERMISSIONS ON THE ISSUES
                
                for (int i=0;i<ownedIssues.size();i++) {
                    Issue issue = ownedIssues.get(i);
                    
                    IssuePTO issuePTO = new IssuePTO(issue);
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(issue.getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(issue.getSeverity(), currLocale);

                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    
                    ownedIssuePTOs.add(issuePTO);
                }
                
                for (int i=0;i<unassignedIssues.size();i++) {
                    Issue issue = unassignedIssues.get(i);
                    IssuePTO issuePTO = new IssuePTO(issue);
                    
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(unassignedIssues.get(i).getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(unassignedIssues.get(i).getSeverity(), currLocale);
                    boolean canViewIssue = IssueUtilities.canViewIssue(unassignedIssues.get(i), currUser.getId(), permissions);
                    issuePTO.setUserCanViewIssue(canViewIssue);
                    
                    boolean userHasIssueNotification = IssueUtilities.hasIssueNotification(unassignedIssues.get(i), currUser.getId());
                    issuePTO.setUserCanEdit(userHasIssueNotification);
                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    issuePTO.setUnassigned(true);
                    issuePTO.setUserHasPermission_PERMISSION_ASSIGN_SELF(UserUtilities.hasPermission(permissions, unassignedIssues.get(i).getProject().getId(), UserUtilities.PERMISSION_ASSIGN_SELF));
                    issuePTO.setUserHasPermission_PERMISSION_ASSIGN_OTHERS(UserUtilities.hasPermission(permissions, unassignedIssues.get(i).getProject().getId(), UserUtilities.PERMISSION_ASSIGN_OTHERS));
                    unassignedIssuePTOs.add(issuePTO);
                }
                
                for (int i=0;i<createdIssues.size();i++) {
                    Issue issue = createdIssues.get(i);
                    IssuePTO issuePTO = new IssuePTO(issue);
                    
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(createdIssues.get(i).getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(createdIssues.get(i).getSeverity(), currLocale);
                    boolean canViewIssue = IssueUtilities.canViewIssue(createdIssues.get(i), currUser.getId(), permissions);
                    issuePTO.setUserCanEdit(canViewIssue);
                    boolean userHasIssueNotification = IssueUtilities.hasIssueNotification(createdIssues.get(i), currUser.getId());
                    issuePTO.setUserCanEdit(userHasIssueNotification);
                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    if (issuePTO.getIssue().getOwner()==null){
                    	issuePTO.setUnassigned(true);
                    }
                    createdIssuePTOs.add(issuePTO);
                }
                for (int i=0;i<watchedIssues.size();i++) {
                    Issue issue = watchedIssues.get(i);
                    IssuePTO issuePTO = new IssuePTO(issue);
                    
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(watchedIssues.get(i).getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(watchedIssues.get(i).getSeverity(), currLocale);
                    boolean canViewIssue = IssueUtilities.canViewIssue(watchedIssues.get(i), currUser.getId(), permissions);
                    issuePTO.setUserCanEdit(canViewIssue);
                    boolean userHasIssueNotification = IssueUtilities.hasIssueNotification(watchedIssues.get(i), currUser.getId());
                    issuePTO.setUserCanEdit(userHasIssueNotification);
                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    
                    watchedIssuePTOs.add(issuePTO);
                }
                // POSSIBLE OWNERS CODE...
                
                // Because of the potentially large number of issues, and a multitude of projects, the
                // possible owners for a project are stored in a Map.  This doesn't take into account the
                // creator of the issue though since they may only have EDIT_USERS permission.  So if the
                // creator isn't already in the project list, check to see if the creator has EDIT_USERS
                // permissions, if so then add them to the list of owners and resort.
                
                for (int i = 0; i < unassignedIssues.size(); i++) {
                    
                    HashMap<Integer,List<User>> possibleOwnersMap = new HashMap<Integer,List<User>>();
                    HashMap<Integer,List<User>> usersWithEditOwnMap = new HashMap<Integer,List<User>>();
                    List<User> tempOwners = new ArrayList<User>();
                    //   List<User> possibleIssueOwners = possibleOwnersMap.get(unassignedIssues.get(i).getProjectId());
                    List<User> possibleIssueOwners = new ArrayList<User>();
                    
                    final Issue issue = unassignedIssuePTOs.get(i).getIssue();
                    
                    if(possibleIssueOwners == null) {
                        possibleIssueOwners = userService.getPossibleOwners(null, issue.getProject().getId(), null);
                        Collections.sort(possibleIssueOwners, User.NAME_COMPARATOR);
                        possibleOwnersMap.put(issue.getProject().getId(), possibleIssueOwners);
                    }
                    
                    List<User> editOwnUsers = new ArrayList<User>();
                    User userWithEditOwnMap = (User)usersWithEditOwnMap.get(issue.getProject().getId());
                    if (userWithEditOwnMap == null) {
                        editOwnUsers = null;
                    } else {
                        editOwnUsers.add(userWithEditOwnMap);
                    }
                    
                    
                    if(editOwnUsers == null) {
                        Integer projectId = issue.getProject().getId();
                        editOwnUsers = userService.getUsersWithProjectPermission(projectId, UserUtilities.PERMISSION_EDIT_USERS, true);
                        usersWithEditOwnMap.put(projectId, editOwnUsers);
                    }
                    
                    boolean creatorPresent = false;
                    
                    for(int k = 0; k < possibleIssueOwners.size(); k++) {
                        if(possibleIssueOwners.get(k).getId().equals(issue.getCreator().getId())) {
                            creatorPresent = true;
                            break;
                        }
                        request.setAttribute("creatorPresent", new Boolean(creatorPresent));
                    }
                    
                    if(! creatorPresent) {
                        creatorPresent = true;
                        for(int k = 0; k < editOwnUsers.size(); k++) {
                            Integer creatorId = issue.getCreator().getId();
                            Integer ownUserId = editOwnUsers.get(k).getId();
                            if(ownUserId.equals(creatorId)) {
                                tempOwners = new ArrayList<User>();
                                for(int m = 0; m < possibleIssueOwners.size(); m++) {
                                    tempOwners.add(m,possibleIssueOwners.get(m));
                                }
                                tempOwners.add(tempOwners.size() == 0 ? 0 : tempOwners.size() - 1,(User)editOwnUsers.get(k));
                                Collections.sort(tempOwners, User.NAME_COMPARATOR);
                                creatorPresent = false;
                            }
                        }
                        request.setAttribute("creatorPresent", new Boolean(creatorPresent));
                    }
                    LOGGER.info("possibleIssueOwners Size: "+possibleIssueOwners.size());
                    request.setAttribute("possibleIssueOwners", possibleIssueOwners);
                }
                
                
                // SETTING THE STATUS ON THE ISSUES AS STRINGS IN THE RIGHT LOCALE
                //IssueUtilities.getStatusName(createdIssues[i].getStatus(), (Locale)pageContext.getAttribute("currLocale"));
                
                // SETTING THE SEVERITIES ON THE ISSUES AS STRINGS  IN THE RIGHT LOCALE
                //IssueUtilities.getSeverityName(createdIssues[i].getSeverity(), (Locale)pageContext.getAttribute("currLocale"));
                
                // SETTING UNASSIGNED ON THE ISSUES AS STRINGS  IN THE RIGHT LOCALE
                //String unassignedString = ITrackerResources.getString("itracker.web.generic.unassigned", (Locale)pageContext.getAttribute("currLocale"));
                
                // PUTTING PERMISSIONS INTO THE REQUEST SCOPE
                
                
                // PUTTING PREFERENCES INTO THE REQUEST SCOPE
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_CREATED",new Boolean(true));
                }
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_ASSIGNED",new Boolean(true));
                }
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_UNASSIGNED",new Boolean(true));
                }
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_WATCHED",new Boolean(true));
                }
                
                request.setAttribute("itracker_web_generic_unassigned", ITrackerResources.getString("itracker.web.generic.unassigned", currLocale));
                
                // PUTTING ISSUES INTO THE REQUEST SCOPE
                LOGGER.info("ownedIssues Size: "+ownedIssuePTOs.size());
                request.setAttribute("ownedIssues",ownedIssuePTOs);
                
                LOGGER.info("unassignedIssues Size:  "+unassignedIssuePTOs.size());
                request.setAttribute("unassignedIssues",unassignedIssuePTOs);
                
                LOGGER.info("createdIssues Size: "+createdIssuePTOs.size());
                request.setAttribute("createdIssues",createdIssuePTOs);
                
                LOGGER.info("watchedIssues Size: "+watchedIssuePTOs.size());
                request.setAttribute("watchedIssues",watchedIssuePTOs);
                
                
                LOGGER.info("Found forward: "+forward.getName()+" and stepped into action method that's populating portalhome");
                
                
                String pageTitleKey = "itracker.web.index.title";
                String pageTitleArg = "";
                request.setAttribute("pageTitleKey",pageTitleKey);
                request.setAttribute("pageTitleArg",pageTitleArg);
                
                request.setAttribute("ih",issueService);
                request.setAttribute("ph",projectService);
                request.setAttribute("uh",userService);
                request.setAttribute("userPrefs",userPrefs);
                //TODO: set the next value based on the request attribute!
                request.setAttribute("showAll", Boolean.TRUE);
                LOGGER.info("Action is trying to forward portalhome");
            }
            return forward;
        }
        return forward;
    }
    
    public PortalHomeAction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
