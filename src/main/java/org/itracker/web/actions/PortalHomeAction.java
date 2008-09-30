package org.itracker.web.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.itracker.model.NameValuePair;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
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
        LOGGER.info("Stepping up into the loginRouter method");
        
        // mayb wrong the next line... setting a default forward...
        ActionForward thisactionforward=mapping.findForward("portalhome");
        ActionForward forward = super.loginRouter(mapping,form,request,response, thisactionforward);
        
        if (forward==null) {
            return forward;
        } else {
            
            LOGGER.info("Found forward, let's go and check if this forward is portalhome...");
//            super.executeAlways(mapping,form,request,response);
            
            if (forward.getName().equals("portalhome")||forward.getName().equals("index")) {
                
                IssueService issueService = this.getITrackerServices().getIssueService();
                ProjectService projectService = this.getITrackerServices().getProjectService();
                UserService userService = this.getITrackerServices().getUserService();
                User currUser = (User)request.getSession().getAttribute("currUser");
                Locale locale = super.getLocale(request);
//                Integer userId = currUser.getId();
//				Map<Integer, Set<PermissionType>> permissions =
//                        (Map<Integer, Set<PermissionType>>)request.getSession().getAttribute("permissions");
                
                // GETTING AND SETTING USER PREFS AND HIDDEN SECTIONS ACCORDINGLY
                UserPreferences userPrefs = currUser.getPreferences();
                if(userPrefs == null) userPrefs = new UserPreferences();
                
                int hiddenSections = 0;
                if(! "all".equalsIgnoreCase(request.getParameter("sections"))) {
                    hiddenSections = userPrefs.getHiddenIndexSections();
                }
                
                final List<IssuePTO> createdIssuePTOs;
                final List<IssuePTO> ownedIssuePTOs;
                final List<IssuePTO> unassignedIssuePTOs;
                final List<IssuePTO> watchedIssuePTOs;
                
                // POPULATING ISSUE MODELS
                final List<Issue> createdIssues;
                final List<Issue> ownedIssues;
                final List<Issue> unassignedIssues;
                final List<Issue> watchedIssues;
                
                // PUTTING PREFERENCES INTO THE REQUEST SCOPE
// Marky:  since the setAttribute is done below,  changed code to place attribute where it is
//         set.
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
                    createdIssues  = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_CREATED", Boolean.TRUE);
                } else {
                    createdIssues = issueService.getIssuesCreatedByUser(currUser.getId());
                    request.setAttribute("UserUtilities_PREF_HIDE_CREATED", Boolean.FALSE);
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
                    ownedIssues = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_ASSIGNED", Boolean.TRUE);
                } else {
                    ownedIssues = issueService.getIssuesOwnedByUser(currUser.getId());
                    request.setAttribute("UserUtilities_PREF_HIDE_ASSIGNED", Boolean.FALSE);
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
                    unassignedIssues = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_UNASSIGNED", Boolean.TRUE);
                } else {
                    unassignedIssues = issueService.getUnassignedIssues();
                    request.setAttribute("UserUtilities_PREF_HIDE_UNASSIGNED", Boolean.FALSE);
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
                    watchedIssues = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_WATCHED", Boolean.TRUE);
                } else {
                    watchedIssues = issueService.getIssuesWatchedByUser(currUser.getId());
                    request.setAttribute("UserUtilities_PREF_HIDE_WATCHED", Boolean.FALSE);
                }
                
                // SORTING ISSUES ACCORDING TO USER PREFS
                if (null!=userPrefs) {
                    String order = userPrefs.getSortColumnOnIssueList();
                    Comparator sort_id = Issue.STATUS_COMPARATOR;
//  Marky:  since repetative code, set a common Comparator variable to contain the Comparator to use and
//          execute the sort pre issue type only once.
                    if("id".equals(order)) {
                        sort_id = Issue.ID_COMPARATOR;
                    } else if("sev".equals(order)) {
                        sort_id = Issue.SEVERITY_COMPARATOR;
                    } else if("stat".equals(order)) {
                        sort_id = Issue.STATUS_COMPARATOR;
                    } else if("lm".equals(order)) {
                        sort_id = Issue.LAST_MODIFIED_DATE_COMPARATOR;
                    } else if("own".equals(order)) {
                        sort_id = Issue.OWNER_AND_STATUS_COMPARATOR;
                    }
                    
                    Collections.sort(createdIssues, sort_id);
                    Collections.sort(ownedIssues, sort_id);
                    Collections.sort(unassignedIssues, sort_id);
                    Collections.sort(watchedIssues, sort_id);
                }
                
                // COPYING MODELS INTO PTOS
                
                // SETTING USER PERMISSIONS ON THE ISSUES
// Marky:  Made function that would built the PTOs
                ownedIssuePTOs = buildIssueList( ownedIssues, request );
                unassignedIssuePTOs = buildIssueList( unassignedIssues, request );
                createdIssuePTOs = buildIssueList( createdIssues, request );
                watchedIssuePTOs = buildIssueList( watchedIssues, request );
                if ( watchedIssuePTOs != null && watchedIssuePTOs.size() > 0 && unassignedIssuePTOs != null && unassignedIssuePTOs.size() > 0 ) {
                    for ( Iterator<IssuePTO> witerator = watchedIssuePTOs.iterator(); witerator.hasNext(); ) {
                        IssuePTO watchedIssue = (IssuePTO) witerator.next();
                        for ( int i = 0; i < unassignedIssuePTOs.size(); i++ ) {
                            if ( watchedIssue.getIssue().getId() == unassignedIssuePTOs.get(i).getIssue().getId() ) {
                                unassignedIssuePTOs.get(i).setUserHasIssueNotification(false);
                            }
                        }
                    }
                }
                // POSSIBLE OWNERS CODE...
                
                // Because of the potentially large number of issues, and a multitude of projects, the
                // possible owners for a project are stored in a Map.  This doesn't take into account the
                // creator of the issue though since they may only have EDIT_USERS permission.  So if the
                // creator isn't already in the project list, check to see if the creator has EDIT_USERS
                // permissions, if so then add them to the list of owners and resort.
                
// Marky:  moved these out of for loop so they can be referenced after loop is over to set attributes.
                
//                List<Boolean> creatorsPresent = new ArrayList<Boolean>();
                List[] possibleOwnersMap = new List[unassignedIssues.size()];
//                HashMap<Integer,List<User>> usersWithEditOwnMap = new HashMap<Integer,List<User>>();
                HttpSession session = request.getSession(true);
                Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
                
                for (int i = 0; i < unassignedIssues.size(); i++) {
                    List<User> possibleIssueOwners = new ArrayList<User>();
                    boolean creatorPresent = false;
                    final Issue issue = unassignedIssuePTOs.get(i).getIssue();
                    final Project project = issueService.getIssueProject(issue.getId());
                    
                    //List<User> tempOwners = new ArrayList<User>();
                    final List<NameValuePair> ownersList;
                    
                    ownersList = UserUtilities.getAssignableIssueOwnersList(issue, project, currUser, locale, userService, userPermissions);
                    
                    for ( Iterator idIterator = ownersList.iterator(); idIterator.hasNext(); ) {
                        NameValuePair owner = (NameValuePair) idIterator.next();
                        possibleIssueOwners.add(userService.getUser(Integer.parseInt(owner.getValue())));
                        if ( owner.getValue().equals(String.valueOf(issue.getCreator().getId()) )) {
                            creatorPresent = true;
                        }
                    }
                    
                    if(! creatorPresent) {
                        List<Permission> creatorpermissions = issue.getCreator().getPermissions();
                        for ( Iterator premIterator = creatorpermissions.iterator(); premIterator.hasNext(); ) {
                            Permission creatorPermission = (Permission) premIterator.next();
                            if ( creatorPermission.getPermissionType() == UserUtilities.PERMISSION_EDIT_USERS ) {
                                possibleIssueOwners.add(userService.getUser(issue.getCreator().getId()));
                                break;
                            }
                        }
                    }
//                    creatorsPresent.add(Boolean.valueOf(creatorPresent));
                    possibleOwnersMap[i] = possibleIssueOwners;
                    
                }

// Moved the creatorPresent and possibleIssueOwners attributes so they are set only once.
//                request.setAttribute("creatorsPresent", creatorsPresent);
                
//                LOGGER.info("possibleIssueOwners Size: "+possibleIssueOwners.size());
                request.setAttribute("possibleIssueOwnersMap", possibleOwnersMap);
                
                // SETTING THE STATUS ON THE ISSUES AS STRINGS IN THE RIGHT LOCALE
                //IssueUtilities.getStatusName(createdIssues[i].getStatus(), (Locale)pageContext.getAttribute("currLocale"));
                
                // SETTING THE SEVERITIES ON THE ISSUES AS STRINGS  IN THE RIGHT LOCALE
                //IssueUtilities.getSeverityName(createdIssues[i].getSeverity(), (Locale)pageContext.getAttribute("currLocale"));
                
                // SETTING UNASSIGNED ON THE ISSUES AS STRINGS  IN THE RIGHT LOCALE
                //String unassignedString = ITrackerResources.getString("itracker.web.generic.unassigned", (Locale)pageContext.getAttribute("currLocale"));
                
                // PUTTING PERMISSIONS INTO THE REQUEST SCOPE
                
                request.setAttribute("itracker_web_generic_unassigned", ITrackerResources.getString("itracker.web.generic.unassigned", locale));
                
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
                //String showall = null == request.getParameter("showAll")? "false": request.getParameter("showAll");
                
                //request.setAttribute("showAll", Boolean.valueOf(showall));
                request.setAttribute("showAll", Boolean.TRUE);
                LOGGER.info("Action is trying to forward portalhome");
            }
            return forward;
        }
    }
    
    // this function is used to load the issue type PTOs List with issue/owner/project data.  It will return this the the main
    // function for further processing.
    
    @SuppressWarnings("unchecked")
	public List<IssuePTO> buildIssueList( List<Issue> issues, HttpServletRequest request ) {
        User currUser = (User)request.getSession().getAttribute("currUser");
        Locale currLocale = getLocale(request);
        //Integer userId = currUser.getId();
        Map<Integer, Set<PermissionType>> permissions =
                (Map<Integer, Set<PermissionType>>)request.getSession().getAttribute("permissions");
        
        List<IssuePTO> issuePTOs = new ArrayList<IssuePTO>();
        
        for (int i=0;i<issues.size();i++) {
            Issue issue = issues.get(i);
            IssuePTO issuePTO = new IssuePTO(issue);
            issuePTO.setSeverityLocalizedString(IssueUtilities.getSeverityName(issue.getSeverity(), currLocale));
            issuePTO.setStatusLocalizedString(IssueUtilities.getStatusName(issue.getStatus(), currLocale));
            issuePTO.setUnassigned((issuePTO.getIssue().getOwner() == null ? true : false));
            issuePTO.setUserCanEdit(IssueUtilities.canEditIssue(issue, currUser.getId(), permissions));
            issuePTO.setUserCanViewIssue(IssueUtilities.canViewIssue(issue, currUser.getId(), permissions));
            issuePTO.setUserHasPermission_PERMISSION_ASSIGN_SELF(UserUtilities.hasPermission(permissions, issue.getProject().getId(), UserUtilities.PERMISSION_ASSIGN_SELF));
            issuePTO.setUserHasPermission_PERMISSION_ASSIGN_OTHERS(UserUtilities.hasPermission(permissions, issue.getProject().getId(), UserUtilities.PERMISSION_ASSIGN_OTHERS));
            issuePTO.setUserHasIssueNotification(IssueUtilities.hasIssueNotification(issue, currUser.getId()));
            
            issuePTOs.add(issuePTO);
        }
        return issuePTOs;
    }
    
    
    public PortalHomeAction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
