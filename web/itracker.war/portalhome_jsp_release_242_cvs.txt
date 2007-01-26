<%@ page language="java" contentType="text/html;charset=UTF-8" %>
    2 <%@ taglib uri="/itracker.tld" prefix="it" %>
    3 <%@ taglib uri="/struts-bean.tld" prefix="bean" %>
    4 <%@ taglib uri="/struts-html.tld" prefix="html" %>
    5 <%@ taglib uri="/struts-logic.tld" prefix="logic" %>
    6 
    7 <%@ page import="java.util.Arrays" %>
    8 <%@ page import="javax.naming.*" %>
    9 <%@ page import="javax.rmi.*" %>
   10 <%@ page import="cowsultants.itracker.ejb.client.models.*" %>
   11 <%@ page import="cowsultants.itracker.ejb.client.util.*" %>
   12 <%@ page import="cowsultants.itracker.ejb.client.interfaces.*" %>
   13 <%@ page import="cowsultants.itracker.ejb.client.resources.ITrackerResources" %>
   14 
   15 
   16 <it:checkLogin/>
   17 
   18 <%@ include file="/includes/page_init.jsp" %>
   19 <bean:define id="pageTitleKey" value="itracker.web.index.title"/>
   20 <bean:define id="pageTitleArg" value=""/>
   21 <%@ include file="/includes/header.jsp" %>
   22 
   23 <%
   24   InitialContext ic = new InitialContext();
   25 
   26   Object ihRef = ic.lookup("java:comp/env/" + IssueHandler.JNDI_NAME);
   27   IssueHandlerHome ihHome = (IssueHandlerHome) PortableRemoteObject.narrow(ihRef, IssueHandlerHome.class);
   28   IssueHandler ih = ihHome.create();
   29 
   30   Object phRef = ic.lookup("java:comp/env/" + ProjectHandler.JNDI_NAME);
   31   ProjectHandlerHome phHome = (ProjectHandlerHome) PortableRemoteObject.narrow(phRef, ProjectHandlerHome.class);
   32   ProjectHandler ph = phHome.create();
   33 
   34   Object uhRef = ic.lookup("java:comp/env/" + UserHandler.JNDI_NAME);
   35   UserHandlerHome uhHome = (UserHandlerHome) PortableRemoteObject.narrow(uhRef, UserHandlerHome.class);
   36   UserHandler uh = uhHome.create();
   37 
   38   Integer currUserId = currUser.getId();
   39   UserPreferencesModel userPrefs = (UserPreferencesModel) session.getAttribute("preferences");
   40 
   41   int hiddenSections = 0;
   42   if(! "all".equalsIgnoreCase(request.getParameter("sections"))) {
   43       hiddenSections = userPrefs.getHiddenIndexSections();
   44   }
   45 
   46   IssueModel[] createdIssues = new IssueModel[0];
   47   IssueModel[] ownedIssues = new IssueModel[0];
   48   IssueModel[] unassignedIssues = new IssueModel[0];
   49   IssueModel[] watchedIssues = new IssueModel[0];
   50 
   51   if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
   52       createdIssues = ih.getIssuesCreatedByUser(currUserId);
   53   }
   54   if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
   55       ownedIssues = ih.getIssuesOwnedByUser(currUserId);
   56   }
   57   if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
   58       unassignedIssues = ih.getUnassignedIssues();
   59   }
   60   if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
   61       watchedIssues = ih.getIssuesWatchedByUser(currUserId);
   62   }
   63 
   64   String order = userPrefs.getSortColumnOnIssueList();
   65 
   66   if("id".equals(order)) {
   67       Arrays.sort(createdIssues, new IssueModel().new CompareById());
   68       Arrays.sort(ownedIssues, new IssueModel().new CompareById());
   69       Arrays.sort(unassignedIssues, new IssueModel().new CompareById());
   70       Arrays.sort(watchedIssues, new IssueModel().new CompareById());
   71   } else if("sev".equals(order)) {
   72       Arrays.sort(createdIssues, new IssueModel().new CompareBySeverity());
   73       Arrays.sort(ownedIssues, new IssueModel().new CompareBySeverity());
   74       Arrays.sort(unassignedIssues, new IssueModel().new CompareBySeverity());
   75       Arrays.sort(watchedIssues, new IssueModel().new CompareBySeverity());
   76   } else if("stat".equals(order)) {
   77       Arrays.sort(createdIssues, new IssueModel());
   78       Arrays.sort(ownedIssues, new IssueModel().new CompareBySeverity());
   79       Arrays.sort(unassignedIssues, new IssueModel());
   80       Arrays.sort(watchedIssues, new IssueModel());
   81   } else if("lm".equals(order)) {
   82       Arrays.sort(createdIssues, new IssueModel().new CompareByDate(false));
   83       Arrays.sort(ownedIssues, new IssueModel().new CompareByDate(false));
   84       Arrays.sort(unassignedIssues, new IssueModel().new CompareByDate(false));
   85       Arrays.sort(watchedIssues, new IssueModel().new CompareByDate(false));
   86   } else if("own".equals(order)) {
   87       Arrays.sort(createdIssues, new IssueModel().new CompareByOwnerAndStatus());
   88       Arrays.sort(ownedIssues, new IssueModel().new CompareBySeverity());
   89       Arrays.sort(unassignedIssues, new IssueModel().new CompareByOwnerAndStatus());
   90       Arrays.sort(watchedIssues, new IssueModel().new CompareByOwnerAndStatus());
   91   } else {
   92       Arrays.sort(createdIssues, new IssueModel());
   93       Arrays.sort(ownedIssues, new IssueModel().new CompareBySeverity());
   94       Arrays.sort(unassignedIssues, new IssueModel());
   95       Arrays.sort(watchedIssues, new IssueModel());
   96   }
   97 
   98 
   99   int j = 0;
  100 %>
  101 
  102 <logic:messagesPresent>
  103   <center>
  104     <span class="formError">
  105      <html:messages id="error">
  106         <bean:write name="error"/><br/>
  107      </html:messages>
  108     </span>
  109   </center>
  110   <br>
  111 </logic:messagesPresent>
  112 
  113 <table border="0" cellspacing="0" cellpadding="0" width="90%" align="center">
  114 <%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) { %>
  115       <tr>
  116         <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.assigned"/>:</td>
  117       </tr>
  118       <tr align="center" class="listHeading">
  119         <td width="50"></td>
  120         <td><html:img page="/images/blank.gif" width="3"/></td>
  121         <td><it:message key="itracker.web.attr.id"/></td>
  122         <td><html:img page="/images/blank.gif" width="3"/></td>
  123         <td><it:message key="itracker.web.attr.project"/></td>
  124         <td><html:img page="/images/blank.gif" width="3"/></td>
  125         <td><it:message key="itracker.web.attr.status"/></td>
  126         <td><html:img page="/images/blank.gif" width="3"/></td>
  127         <td><it:message key="itracker.web.attr.severity"/></td>
  128         <td><html:img page="/images/blank.gif" width="3"/></td>
  129         <td><it:message key="itracker.web.attr.description"/></td>
  130         <td><html:img page="/images/blank.gif" width="3"/></td>
  131         <td><it:message key="itracker.web.attr.owner"/></td>
  132         <td><html:img page="/images/blank.gif" width="3"/></td>
  133         <td><it:message key="itracker.web.attr.lastmodified"/></td>
  134       </tr>
  135 <%
  136       for(int i = 0; i < ownedIssues.length; i++) {
  137         if(userPrefs.getNumItemsOnIndex() > 0 && i >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
  138 %>
  139           <tr class="listRowUnshaded"><td align="center" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
  140 <%
  141           break;
  142         }
  143 
  144         UserModel owner = ih.getIssueOwner(ownedIssues[i].getId());
  145 %>
  146         <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
  147           <td>
  148             <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= ownedIssues[i].getId() %>" src="/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= ownedIssues[i].getId() %>"/>
  149             <% if(IssueUtilities.canEditIssue(ownedIssues[i], currUserId, currPermissions)) { %>
  150                  <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= ownedIssues[i].getId() %>" caller="index" src="/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= ownedIssues[i].getId() %>"/>
  151             <% } %>
  152           </td>
  153           <td></td>
  154           <td align="left"><%= ownedIssues[i].getId() %></td>
  155           <td></td>
  156           <td><%= ownedIssues[i].getProjectName() %></td>
  157           <td></td>
  158           <td><%= IssueUtilities.getStatusName(ownedIssues[i].getStatus(), currLocale) %></td>
  159           <td></td>
  160           <td><%= IssueUtilities.getSeverityName(ownedIssues[i].getSeverity(), currLocale) %></td>
  161           <td></td>
  162           <td><it:formatDescription><%= ownedIssues[i].getDescription() %></it:formatDescription></td>
  163           <td></td>
  164           <td><it:formatIssueOwner issue="<%= ownedIssues[i] %>" format="short"/></td>
  165           <td></td>
  166           <td><it:formatDate date="<%= ownedIssues[i].getLastModifiedDate() %>"/></td>
  167         </tr>
  168 <%    } %>
  169       <tr><td><html:img page="/images/blank.gif" width="1" height="20"/></td></tr>
  170 <%  } %>
  171 
  172 
  173 <%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) { %>
  174       <tr>
  175         <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.unassigned"/>:</td>
  176       </tr>
  177       <tr align="center" class="listHeading">
  178         <td></td>
  179         <td></td>
  180         <td><it:message key="itracker.web.attr.id"/></td>
  181         <td></td>
  182         <td><it:message key="itracker.web.attr.project"/></td>
  183         <td></td>
  184         <td><it:message key="itracker.web.attr.status"/></td>
  185         <td></td>
  186         <td><it:message key="itracker.web.attr.severity"/></td>
  187         <td></td>
  188         <td><it:message key="itracker.web.attr.description"/></td>
  189         <td></td>
  190         <td><it:message key="itracker.web.attr.owner"/></td>
  191         <td></td>
  192         <td><it:message key="itracker.web.attr.lastmodified"/></td>
  193       </tr>
  194 
  195 <%
  196       j = 0;
  197       HashMap possibleOwnersMap = new HashMap();
  198       HashMap usersWithEditOwnMap = new HashMap();
  199       for(int i = 0; i < unassignedIssues.length; i++) {
  200         if(! IssueUtilities.canViewIssue(unassignedIssues[i], currUserId, currPermissions)) {
  201             continue;
  202         }
  203         j++;
  204         if(userPrefs.getNumItemsOnIndex() > 0 && j >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
  205 %>
  206           <tr class="listRowUnshaded"><td align="center" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
  207 <%
  208           break;
  209         }
  210 %>
  211         <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
  212           <td>
  213             <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= unassignedIssues[i].getId() %>" src="/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= unassignedIssues[i].getId() %>"/>
  214             <% if(IssueUtilities.canEditIssue(unassignedIssues[i], currUserId, currPermissions)) { %>
  215                  <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= unassignedIssues[i].getId() %>" caller="index" src="/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= unassignedIssues[i].getId() %>"/>
  216             <%
  217                }
  218                if(! IssueUtilities.hasIssueNotification(unassignedIssues[i], currUserId)) {
  219             %>
  220                  <it:formatImageAction forward="watchissue" paramName="id" paramValue="<%= unassignedIssues[i].getId() %>" caller="index" src="/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="<%= unassignedIssues[i].getId() %>"/>
  221             <% } %>
  222           </td>
  223           <td></td>
  224           <td align="left"><%= unassignedIssues[i].getId() %></td>
  225           <td></td>
  226           <td><%= unassignedIssues[i].getProjectName() %></td>
  227           <td></td>
  228           <td><%= IssueUtilities.getStatusName(unassignedIssues[i].getStatus(), currLocale) %></td>
  229           <td></td>
  230           <td><%= IssueUtilities.getSeverityName(unassignedIssues[i].getSeverity(), currLocale) %></td>
  231           <td></td>
  232           <td><it:formatDescription><%= unassignedIssues[i].getDescription() %></it:formatDescription></td>
  233           <td></td>
  234           <% if(UserUtilities.hasPermission(currPermissions, unassignedIssues[i].getProjectId(), UserUtilities.PERMISSION_ASSIGN_OTHERS)) { %>
  235                 <html:form action="/assignissue">
  236                   <html:hidden property="issueId" value="<%= unassignedIssues[i].getId().toString() %>"/>
  237                   <html:hidden property="projectId" value="<%= unassignedIssues[i].getProjectId().toString() %>"/>
  238                   <td><html:select property="userId" styleClass="<%= (i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\") %>" onchange="this.form.submit();">
  239                       <%= (unassignedIssues[i].getOwnerId().intValue() == -1 ? "<option value=\"-1\">" + ITrackerResources.getString("itracker.web.generic.unassigned", currLocale) + "</option>" : "<option value=\"" + unassignedIssues[i].getOwnerId() + "\">" + UserUtilities.getInitial(unassignedIssues[i].getOwnerFirstName()) + " " + unassignedIssues[i].getOwnerLastName() + "</option>") %>
  240                       <%
  241                          // Because of the potentially large number of issues, and a multitude of projects, the
  242                          // possible owners for a project are stored in a Map.  This doesn't take into account the
  243                          // creator of the issue though since they may only have EDIT_USERS permission.  So if the
  244                          // creator isn't already in the project list, check to see if the creator has EDIT_USERS
  245                          // permissions, if so then add them to the lsit of owners and resort.
  246                          UserModel[] tempOwners = new UserModel[0];
  247                          UserModel[] possibleOwners = (UserModel[]) possibleOwnersMap.get(unassignedIssues[i].getProjectId());
  248                          if(possibleOwners == null) {
  249                             possibleOwners = uh.getPossibleOwners(null, unassignedIssues[i].getProjectId(), null);
  250                             Arrays.sort(possibleOwners, new UserModel().new CompareByName());
  251                             possibleOwnersMap.put(unassignedIssues[i].getProjectId(), possibleOwners);
  252                          }
  253                          UserModel[] editOwnUsers = (UserModel[]) usersWithEditOwnMap.get(unassignedIssues[i].getProjectId());
  254                          if(editOwnUsers == null) {
  255                             editOwnUsers = uh.getUsersWithProjectPermission(unassignedIssues[i].getProjectId(), UserUtilities.PERMISSION_EDIT_USERS, true);
  256                             usersWithEditOwnMap.put(unassignedIssues[i].getProjectId(), editOwnUsers);
  257                          }
  258                          boolean creatorPresent = false;
  259                          for(int k = 0; k < possibleOwners.length; k++) {
  260                             if(possibleOwners[k].getId().equals(unassignedIssues[i].getCreatorId())) {
  261                                 creatorPresent = true;
  262                                 break;
  263                             }
  264                          }
  265                          if(! creatorPresent) {
  266                              creatorPresent = true;
  267                              for(int k = 0; k < editOwnUsers.length; k++) {
  268                                 if(editOwnUsers[k].getId().equals(unassignedIssues[i].getCreatorId())) {
  269                                     tempOwners = new UserModel[possibleOwners.length + 1];
  270                                     for(int m = 0; m < possibleOwners.length; m++) {
  271                                         tempOwners[m] =  possibleOwners[m];
  272                                     }
  273                                     tempOwners[tempOwners.length - 1] = editOwnUsers[k];
  274                                     Arrays.sort(tempOwners, new UserModel().new CompareByName());
  275                                     creatorPresent = false;
  276                                 }
  277                              }
  278                          }
  279 
  280                          if(creatorPresent) {
  281                              for(int k = 0; k < possibleOwners.length; k++) {
  282                       %>
  283                                 <option value="<%= possibleOwners[k].getId() %>" <%= (unassignedIssues[i].getOwnerId() == possibleOwners[k].getId() ? "selected" : "") %>><%= possibleOwners[k].getFirstInitial() + " " + possibleOwners[k].getLastName() %></option>
  284                       <%
  285                              }
  286                          } else {
  287                              for(int k = 0; k < tempOwners.length; k++) {
  288                       %>
  289                                 <option value="<%= tempOwners[k].getId() %>" <%= (unassignedIssues[i].getOwnerId() == tempOwners[k].getId() ? "selected" : "") %>><%= tempOwners[k].getFirstInitial() + " " + tempOwners[k].getLastName() %></option>
  290                       <%
  291                              }
  292                          }
  293                       %>
  294                   </html:select></td>
  295                 </html:form>
  296           <% } else if(UserUtilities.hasPermission(currPermissions, unassignedIssues[i].getProjectId(), UserUtilities.PERMISSION_ASSIGN_SELF)) { %>
  297                 <html:form action="/assignissue">
  298                   <html:hidden property="issueId" value="<%= unassignedIssues[i].getId().toString() %>"/>
  299                   <html:hidden property="projectId" value="<%= unassignedIssues[i].getProjectId().toString() %>"/>
  300                   <td><html:select property="userId" styleClass="<%= (i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\") %>" onchange="this.form.submit();">
  301                       <%= (unassignedIssues[i].getOwnerId().intValue() == -1 ? "<option value=\"-1\">" + ITrackerResources.getString("itracker.web.generic.unassigned", currLocale) + "</option>" : "<option value=\"" + unassignedIssues[i].getOwnerId() + "\">" + UserUtilities.getInitial(unassignedIssues[i].getOwnerFirstName()) + " " + unassignedIssues[i].getOwnerLastName() + "</option>") %>
  302                       <option value="<%= currUser.getId() %>" <%= (unassignedIssues[i].getOwnerId() == currUser.getId() ? "selected" : "") %>><%= currUser.getFirstInitial() + " " + currUser.getLastName() %></option>
  303                   </html:select></td>
  304                 </html:form>
  305           <% } else { %>
  306                 <td><it:formatIssueOwner issue="<%= unassignedIssues[i] %>" format="short"/></td>
  307           <% } %>
  308           <td></td>
  309           <td><it:formatDate date="<%= unassignedIssues[i].getLastModifiedDate() %>"/></td>
  310         </tr>
  311 <%    } %>
  312       <tr><td><html:img page="/images/blank.gif" width="1" height="20"/></td></tr>
  313 <%  } %>
  314 
  315 
  316 <%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) { %>
  317       <tr>
  318         <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.created"/>:</td>
  319       </tr>
  320       <tr align="center" class="listHeading">
  321         <td></td>
  322         <td></td>
  323         <td><it:message key="itracker.web.attr.id"/></td>
  324         <td></td>
  325         <td><it:message key="itracker.web.attr.project"/></td>
  326         <td></td>
  327         <td><it:message key="itracker.web.attr.status"/></td>
  328         <td></td>
  329         <td><it:message key="itracker.web.attr.severity"/></td>
  330         <td></td>
  331         <td><it:message key="itracker.web.attr.description"/></td>
  332         <td></td>
  333         <td><it:message key="itracker.web.attr.owner"/></td>
  334         <td></td>
  335         <td><it:message key="itracker.web.attr.lastmodified"/></td>
  336       </tr>
  337 
  338 <%
  339       for(int i = 0; i < createdIssues.length; i++) {
  340         if(userPrefs.getNumItemsOnIndex() > 0 && i >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
  341 %>
  342           <tr class="listRowUnshaded"><td align="center" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
  343 <%
  344           break;
  345         }
  346 
  347         UserModel owner = ih.getIssueOwner(createdIssues[i].getId());
  348 %>
  349         <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
  350           <td>
  351             <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= createdIssues[i].getId() %>" src="/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= createdIssues[i].getId() %>"/>
  352             <% if(IssueUtilities.canEditIssue(createdIssues[i], currUserId, currPermissions)) { %>
  353                  <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= createdIssues[i].getId() %>" caller="index" src="/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= createdIssues[i].getId() %>"/>
  354             <% } %>
  355           </td>
  356           <td></td>
  357           <td align="left"><%= createdIssues[i].getId() %></td>
  358           <td></td>
  359           <td><%= createdIssues[i].getProjectName() %></td>
  360           <td></td>
  361           <td><%= IssueUtilities.getStatusName(createdIssues[i].getStatus(), currLocale) %></td>
  362           <td></td>
  363           <td><%= IssueUtilities.getSeverityName(createdIssues[i].getSeverity(), currLocale) %></td>
  364           <td></td>
  365           <td><it:formatDescription><%= createdIssues[i].getDescription() %></it:formatDescription></td>
  366           <td></td>
  367           <td><it:formatIssueOwner issue="<%= createdIssues[i] %>" format="short"/></td>
  368           <td></td>
  369           <td><it:formatDate date="<%= createdIssues[i].getLastModifiedDate() %>"/></td>
  370         </tr>
  371 <%    } %>
  372       <tr><td><html:img page="/images/blank.gif" width="1" height="20"/></td></tr>
  373 <%  } %>
  374 
  375 
  376 <%
  377    // I could make this all the issues that have changed since the last login.  Wonder if that would be
  378    // better than the watches? No then you lose them.
  379 %>
  380 
  381 <%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) { %>
  382       <tr>
  383         <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.watched"/>:</td>
  384       </tr>
  385       <tr align="center" class="listHeading">
  386         <td></td>
  387         <td></td>
  388         <td><it:message key="itracker.web.attr.id"/></td>
  389         <td></td>
  390         <td><it:message key="itracker.web.attr.project"/></td>
  391         <td></td>
  392         <td><it:message key="itracker.web.attr.status"/></td>
  393         <td></td>
  394         <td><it:message key="itracker.web.attr.severity"/></td>
  395         <td></td>
  396         <td><it:message key="itracker.web.attr.description"/></td>
  397         <td></td>
  398         <td><it:message key="itracker.web.attr.owner"/></td>
  399         <td></td>
  400         <td><it:message key="itracker.web.attr.lastmodified"/></td>
  401       </tr>
  402 <%
  403       for(int i = 0; i < watchedIssues.length; i++) {
  404         if(userPrefs.getNumItemsOnIndex() > 0 && i >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
  405 %>
  406           <tr class="listRowUnshaded"><td align="center" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
  407 <%
  408           break;
  409         }
  410 
  411         UserModel owner = ih.getIssueOwner(watchedIssues[i].getId());
  412 %>
  413         <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
  414           <td>
  415             <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= watchedIssues[i].getId() %>" src="/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= watchedIssues[i].getId() %>"/>
  416             <% if(IssueUtilities.canEditIssue(watchedIssues[i], currUserId, currPermissions)) { %>
  417                  <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= watchedIssues[i].getId() %>" caller="index" src="/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= watchedIssues[i].getId() %>"/>
  418             <% } %>
  419           </td>
  420           <td></td>
  421           <td align="left"><%= watchedIssues[i].getId() %></td>
  422           <td></td>
  423           <td><%= watchedIssues[i].getProjectName() %></td>
  424           <td></td>
  425           <td><%= IssueUtilities.getStatusName(watchedIssues[i].getStatus(), currLocale) %></td>
  426           <td></td>
  427           <td><%= IssueUtilities.getSeverityName(watchedIssues[i].getSeverity(), currLocale) %></td>
  428           <td></td>
  429           <td><it:formatDescription><%= watchedIssues[i].getDescription() %></it:formatDescription></td>
  430           <td></td>
  431           <td><it:formatIssueOwner issue="<%= watchedIssues[i] %>" format="short"/></td>
  432           <td></td>
  433           <td><it:formatDate date="<%= watchedIssues[i].getLastModifiedDate() %>"/></td>
  434         </tr>
  435 <%    } %>
  436       <tr><td><html:img page="/images/blank.gif" width="1" height="20"/></td></tr>
  437 <%  } %>
  438 
  439 <%  if(hiddenSections > 0) { %>
  440       <tr align="center" class="listRowUnshaded">
  441         <td colspan="15" align="center"><html:link page="/index.jsp?sections=all"><it:message key="itracker.web.index.viewhidden"/></html:link></td>
  442       </tr>
  443       <tr><td><html:img page="/images/blank.gif" width="1" height="20"/></td></tr>
  444 <%  } %>
  445 
  446 </table>
  447 
  448 <%@ include file="/includes/footer.jsp" %>