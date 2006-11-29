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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
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
                final List<Issue> createdIssueModels;
                final List<Issue> ownedIssueModels;
                final List<Issue> unassignedIssueModels;
                final List<Issue> watchedIssueModels;
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
                    createdIssueModels  = new ArrayList<Issue>();
                } else {
                    createdIssueModels = issueService.getIssuesCreatedByUser(currUser.getId());
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
                    ownedIssueModels = new ArrayList<Issue>();
                } else {
                    ownedIssueModels = issueService.getIssuesOwnedByUser(currUser.getId());
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
                    unassignedIssueModels = new ArrayList<Issue>();
                } else {
                    unassignedIssueModels = issueService.getUnassignedIssues();
                }

                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
                    watchedIssueModels = new ArrayList<Issue>();
                } else {
                    watchedIssueModels = issueService.getIssuesWatchedByUser(currUser.getId());
                }
                
                // SORTING ISSUES ACCORDING TO USER PREFS
                if (null!=userPrefs) {
                    String order = userPrefs.getSortColumnOnIssueList();
                    
                    if("id".equals(order)) {
                        Collections.sort(createdIssueModels, new Issue.CompareById());
                        Collections.sort(ownedIssueModels, new Issue.CompareById());
                        Collections.sort(unassignedIssueModels, new Issue.CompareById());
                        Collections.sort(watchedIssueModels, new Issue.CompareById());
                    } else if("sev".equals(order)) {
                        Collections.sort(createdIssueModels, new Issue.CompareBySeverity());
                        Collections.sort(ownedIssueModels, new Issue.CompareBySeverity());
                        Collections.sort(unassignedIssueModels, new Issue.CompareBySeverity());
                        Collections.sort(watchedIssueModels, new Issue.CompareBySeverity());
                    } else if("stat".equals(order)) {
                        Collections.sort(createdIssueModels, new Issue.CompareByStatus());
                        Collections.sort(ownedIssueModels, new Issue.CompareBySeverity());
                        Collections.sort(unassignedIssueModels, new Issue.CompareByStatus());
                        Collections.sort(watchedIssueModels, new Issue.CompareByStatus());
                    } else if("lm".equals(order)) {
                        Collections.sort(createdIssueModels, new Issue.LastModifiedDateComparator());
                        Collections.sort(ownedIssueModels, new Issue.LastModifiedDateComparator());
                        Collections.sort(unassignedIssueModels, new Issue.LastModifiedDateComparator());
                        Collections.sort(watchedIssueModels, new Issue.LastModifiedDateComparator());
                    } else if("own".equals(order)) {
                        Collections.sort(createdIssueModels, new Issue.CompareByOwnerAndStatus());
                        Collections.sort(ownedIssueModels, new Issue.CompareBySeverity());
                        Collections.sort(unassignedIssueModels, new Issue.CompareByOwnerAndStatus());
                        Collections.sort(watchedIssueModels, new Issue.CompareByOwnerAndStatus());
                    } else {
                        Collections.sort(createdIssueModels, new Issue.CompareByStatus());
                        Collections.sort(ownedIssueModels, new Issue.CompareBySeverity());
                        Collections.sort(unassignedIssueModels, new Issue.CompareByStatus());
                        Collections.sort(watchedIssueModels, new Issue.CompareByStatus());
                    }
                }
                
                // COPYING MODELS INTO PTOS
                
                // SETTING USER PERMISSIONS ON THE ISSUES
                
                for (int i=0;i<ownedIssueModels.size();i++) {
                    Issue issue = ownedIssueModels.get(i);
                    
                    IssuePTO issuePTO = new IssuePTO(issue);
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(issue.getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(issue.getSeverity(), currLocale);

                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    
                    ownedIssuePTOs.add(issuePTO);
                }
                
                for (int i=0;i<unassignedIssueModels.size();i++) {
                    Issue issue = unassignedIssueModels.get(i);
                    IssuePTO issuePTO = new IssuePTO(issue);
                    
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(unassignedIssueModels.get(i).getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(unassignedIssueModels.get(i).getSeverity(), currLocale);
                    boolean canViewIssue = IssueUtilities.canViewIssue(unassignedIssueModels.get(i), currUser.getId(), permissions);
                    issuePTO.setUserCanEdit(canViewIssue);
                    boolean userHasIssueNotification = IssueUtilities.hasIssueNotification(unassignedIssueModels.get(i), currUser.getId());
                    issuePTO.setUserCanEdit(userHasIssueNotification);
                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    
                    unassignedIssuePTOs.add(issuePTO);
                }
                
                for (int i=0;i<createdIssueModels.size();i++) {
                    Issue issue = createdIssueModels.get(i);
                    IssuePTO issuePTO = new IssuePTO(issue);
                    
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(createdIssueModels.get(i).getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(createdIssueModels.get(i).getSeverity(), currLocale);
                    boolean canViewIssue = IssueUtilities.canViewIssue(createdIssueModels.get(i), currUser.getId(), permissions);
                    issuePTO.setUserCanEdit(canViewIssue);
                    boolean userHasIssueNotification = IssueUtilities.hasIssueNotification(createdIssueModels.get(i), currUser.getId());
                    issuePTO.setUserCanEdit(userHasIssueNotification);
                    issuePTO.setStatusLocalizedString(statusLocalizedString);
                    issuePTO.setSeverityLocalizedString(severityLocalizedString);
                    issuePTO.setUserCanEdit(canEditIssue);
                    
                    createdIssuePTOs.add(issuePTO);
                }
                for (int i=0;i<watchedIssueModels.size();i++) {
                    Issue issue = watchedIssueModels.get(i);
                    IssuePTO issuePTO = new IssuePTO(issue);
                    
                    boolean canEditIssue = IssueUtilities.canEditIssue(issue, currUser.getId(), permissions);
                    String statusLocalizedString = IssueUtilities.getStatusName(watchedIssueModels.get(i).getStatus(), currLocale);
                    String severityLocalizedString = IssueUtilities.getSeverityName(watchedIssueModels.get(i).getSeverity(), currLocale);
                    boolean canViewIssue = IssueUtilities.canViewIssue(watchedIssueModels.get(i), currUser.getId(), permissions);
                    issuePTO.setUserCanEdit(canViewIssue);
                    boolean userHasIssueNotification = IssueUtilities.hasIssueNotification(watchedIssueModels.get(i), currUser.getId());
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
                
                for (int i = 0; i < unassignedIssueModels.size(); i++) {
                    
                    HashMap<Integer,List<User>> possibleOwnersMap = new HashMap<Integer,List<User>>();
                    HashMap<Integer,List<User>> usersWithEditOwnMap = new HashMap<Integer,List<User>>();
                    List<User> tempOwners = new ArrayList<User>();
                    //   List<User> possibleIssueOwners = possibleOwnersMap.get(unassignedIssues.get(i).getProjectId());
                    List<User> possibleIssueOwners = new ArrayList<User>();
                    
                    final Issue issue = unassignedIssuePTOs.get(i).getIssue();
                    
                    if(possibleIssueOwners == null) {
                        possibleIssueOwners = userService.getPossibleOwners(null, issue.getProject().getId(), null);
                        Collections.sort(possibleIssueOwners, new User.CompareByName());
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
                                Collections.sort(tempOwners, new User.CompareByName());
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
                    request.setAttribute("UserUtilities_PREF_HIDE_CREATED",new Integer(UserUtilities.PREF_HIDE_CREATED));
                }
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_ASSIGNED",new Integer(UserUtilities.PREF_HIDE_ASSIGNED));
                }
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_UNASSIGNED",new Integer(UserUtilities.PREF_HIDE_UNASSIGNED));
                }
                if (UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
                    request.setAttribute("UserUtilities_PREF_HIDE_WATCHED",new Integer(UserUtilities.PREF_HIDE_WATCHED));
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
