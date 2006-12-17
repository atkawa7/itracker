<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Locale" %>

<%@ page import="org.itracker.model.*" %>

<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.core.resources.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.index.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<%
    final Map<Integer, Set<PermissionType>> permissions = (Map<Integer, Set<PermissionType>>)
        session.getAttribute("permissions");
        
  IssueService ih = (IssueService)request.getAttribute("ih");
  UserService uh = (UserService)request.getAttribute("uh");
  User currUser = (User) request.getSession().getAttribute("currUser");
  UserPreferences userPrefs = (UserPreferences) request.getAttribute("userPrefs");

  int hiddenSections = 0;   
  if(! "all".equalsIgnoreCase(request.getParameter("sections"))) {
 hiddenSections = userPrefs.getHiddenIndexSections(); 
  }
 
  List<Issue> createdIssues = new ArrayList<Issue>();
  List<Issue> ownedIssues = new ArrayList<Issue>();
  List<Issue> unassignedIssues = new ArrayList<Issue>();
  List<Issue> watchedIssues = new ArrayList<Issue>();
  if (null!=ih) {
  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
      createdIssues = ih.getIssuesCreatedByUser(currUser.getId());
  }
  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
      ownedIssues = ih.getIssuesOwnedByUser(currUser.getId());
  }
  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
      unassignedIssues = ih.getUnassignedIssues();
  }
  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
      watchedIssues = ih.getIssuesWatchedByUser(currUser.getId());
  }
}
if (null!=userPrefs) {
  String order = userPrefs.getSortColumnOnIssueList();
 
  if("id".equals(order)) {
      Collections.sort(createdIssues, new Issue.CompareById());
      Collections.sort(ownedIssues, new Issue.CompareById());
      Collections.sort(unassignedIssues, new Issue.CompareById());
      Collections.sort(watchedIssues, new Issue.CompareById());
  } else if("sev".equals(order)) {
      Collections.sort(createdIssues, new Issue.CompareBySeverity());
      Collections.sort(ownedIssues, new Issue.CompareBySeverity());
      Collections.sort(unassignedIssues, new Issue.CompareBySeverity());
      Collections.sort(watchedIssues, new Issue.CompareBySeverity());
  } else if("stat".equals(order)) {
      Collections.sort(createdIssues, new Issue.CompareByStatus());
      Collections.sort(ownedIssues, new Issue.CompareBySeverity());
      Collections.sort(unassignedIssues, new Issue.CompareBySeverity());
      Collections.sort(watchedIssues, new Issue.CompareByStatus());
  } else if("lm".equals(order)) {
      Collections.sort(createdIssues, new Issue.LastModifiedDateComparator(false));
      Collections.sort(ownedIssues, new Issue.LastModifiedDateComparator(false));
      Collections.sort(unassignedIssues, new Issue.LastModifiedDateComparator(false));
      Collections.sort(watchedIssues, new Issue.LastModifiedDateComparator(false));
  } else if("own".equals(order)) {
      Collections.sort(createdIssues, new Issue.CompareByOwnerAndStatus());
      Collections.sort(ownedIssues, new Issue.CompareBySeverity());
      Collections.sort(unassignedIssues, new Issue.CompareByOwnerAndStatus());
      Collections.sort(watchedIssues, new Issue.CompareByOwnerAndStatus());
  } else {
      Collections.sort(createdIssues, new Issue.CompareBySeverity());
      Collections.sort(ownedIssues, new Issue.CompareBySeverity());
      Collections.sort(unassignedIssues, new Issue.CompareBySeverity());
      Collections.sort(watchedIssues, new Issue.CompareBySeverity());
  }
}
  int j = 0;
%>

<logic:messagesPresent>
  <center>
    <span class="formError">
     <html:messages id="error">
        <bean:write name="error"/><br/>
     </html:messages>
    </span>
  </center>
  <br>
</logic:messagesPresent>
<table border="0" cellspacing="0" cellpadding="1" width="100%">
<%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) { %>
      <tr>
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.assigned"/>:</td>
      </tr>
      <tr align="left" class="listHeading">
        <td width="50"></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td><it:message key="itracker.web.attr.id"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td><it:message key="itracker.web.attr.project"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td><it:message key="itracker.web.attr.status"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td><it:message key="itracker.web.attr.severity"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td width="100%"><it:message key="itracker.web.attr.description"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td><it:message key="itracker.web.attr.owner"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td align="right" style="white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
      </tr>
<%
      for(int i = 0; i < ownedIssues.size(); i++) {
        if(userPrefs.getNumItemsOnIndex() > 0 && i >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
%>
          <tr class="listRowUnshaded"><td align="left" colspan="15"><html:link page="/index.jsp?showAll=true" module="/"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
<%
          break;
        }
%>
    <%--     User owner = ih.getIssueOwner(ownedIssues.get(i).getId()); --%>

        <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
          <td style="white-space: nowrap">
            <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= ownedIssues.get(i).getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= ownedIssues.get(i).getId() %>" textActionKey="itracker.web.image.view.texttag"/>
            <% if(IssueUtilities.canEditIssue(ownedIssues.get(i), currUser.getId(), permissions)) { %>
                 <it:formatImageAction action="/module-projects/editissueform" paramName="id" paramValue="<%= ownedIssues.get(i).getId() %>" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= ownedIssues.get(i).getId() %>" textActionKey="itracker.web.image.edit.texttag"/>
            <% } %>
          </td>
          <td></td>
          <td align="left"><%= ownedIssues.get(i).getId() %></td>
          <td></td>
          <td style="white-space: nowrap"><%= ownedIssues.get(i).getProject().getName() %></td>
          <td></td>
          <td><%= IssueUtilities.getStatusName(ownedIssues.get(i).getStatus(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><%= IssueUtilities.getSeverityName(ownedIssues.get(i).getSeverity(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><it:formatDescription><%= ownedIssues.get(i).getDescription() %></it:formatDescription></td>
          <td></td>
          <td nowrap><it:formatIssueOwner issue="<%= ownedIssues.get(i) %>" format="short"/></td>
          <td></td>
          <td align="right" style="white-space: nowrap"><it:formatDate date="<%= ownedIssues.get(i).getLastModifiedDate() %>"/></td>
        </tr>
<%    } %>
      <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
<%  } %>


<%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) { %>
      <tr>
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.unassigned"/>:</td>
      </tr>
      <tr align="left" class="listHeading">
        <td></td>
        <td></td>
        <td><it:message key="itracker.web.attr.id"/></td>
        <td></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.status"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.severity"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.description"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.owner"/></td>
        <td></td>
        <td align="right" style="white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
      </tr>

<%
      j = 0;
      HashMap<Integer,List<User>> possibleOwnersMap = new HashMap<Integer,List<User>>();
      HashMap<Integer,List<User>> usersWithEditOwnMap = new HashMap<Integer,List<User>>();
      for(int i = 0; i < unassignedIssues.size(); i++) {
        if(! IssueUtilities.canViewIssue(unassignedIssues.get(i), currUser.getId(), permissions)) {
            continue;
        }
        j++;
        if(userPrefs.getNumItemsOnIndex() > 0 && j >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
%>
          <tr class="listRowUnshaded"><td align="left" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
<%
          break;
        }
%>
        <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
          <td>
            <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= unassignedIssues.get(i).getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= unassignedIssues.get(i).getId() %>" textActionKey="itracker.web.image.view.texttag"/>
            <% if(IssueUtilities.canEditIssue(unassignedIssues.get(i), currUser.getId(), permissions)) { %>
                 <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= unassignedIssues.get(i).getId() %>" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= unassignedIssues.get(i).getId() %>" textActionKey="itracker.web.image.edit.texttag"/>
            <%
               }
               if(! IssueUtilities.hasIssueNotification(unassignedIssues.get(i), currUser.getId())) {
            %>
                 <it:formatImageAction forward="watchissue" paramName="id" paramValue="<%= unassignedIssues.get(i).getId() %>" caller="index" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="<%= unassignedIssues.get(i).getId() %>" textActionKey="itracker.web.image.watch.texttag"/>
            <% } %>
          </td>
          <td></td>
          <td align="left"><%= unassignedIssues.get(i).getId() %></td>
          <td></td>
          <td style="white-space: nowrap"><%= unassignedIssues.get(i).getProject().getName() %></td>
          <td></td>
          <td><%= IssueUtilities.getStatusName(unassignedIssues.get(i).getStatus(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><%= IssueUtilities.getSeverityName(unassignedIssues.get(i).getSeverity(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><it:formatDescription><%= unassignedIssues.get(i).getDescription() %></it:formatDescription></td>
          <td></td>
          <% if(UserUtilities.hasPermission(permissions, unassignedIssues.get(i).getProject().getId(), UserUtilities.PERMISSION_ASSIGN_OTHERS)) { %>
                <html:form action="/assignissue">
                  <html:hidden property="issueId" value="<%= unassignedIssues.get(i).getId().toString() %>"/>
                  <html:hidden property="projectId" value="<%= unassignedIssues.get(i).getProject().getId().toString() %>"/>
                  <%! String styleClass1 = "(i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\")"; %>
                  <td><html:select property="userId" styleClass="<%=styleClass1 %>" onchange="this.form.submit();">
                    <%= (unassignedIssues.get(i).getOwner().getId().intValue() == -1 ? "<option value=\"-1\">" + ITrackerResources.getString("itracker.web.generic.unassigned", (Locale)pageContext.getAttribute("currLocale")) + "</option>" : "<option value=\"" + unassignedIssues.get(i).getOwner().getId() + "\">" + UserUtilities.getInitial(unassignedIssues.get(i).getOwner().getFirstName()) + " " + unassignedIssues.get(i).getOwner().getLastName() + "</option>") %>
                    <%
                         // Because of the potentially large number of issues, and a multitude of projects, the
                         // possible owners for a project are stored in a Map.  This doesn't take into account the
                         // creator of the issue though since they may only have EDIT_USERS permission.  So if the
                         // creator isn't already in the project list, check to see if the creator has EDIT_USERS
                         // permissions, if so then add them to the list of owners and resort.
                         List<User> tempOwners = new ArrayList<User>();
                       List<User> possibleOwners = possibleOwnersMap.get(unassignedIssues.get(i).getProject().getId());
                       if(possibleOwners == null) {
                            possibleOwners = uh.getPossibleOwners(null, unassignedIssues.get(i).getProject().getId(), null);
                            Collections.sort(possibleOwners, new User.CompareByName());
                          possibleOwnersMap.put(unassignedIssues.get(i).getProject().getId(), possibleOwners);
                       }
                         List<User> editOwnUsers = usersWithEditOwnMap.get(unassignedIssues.get(i).getProject().getId());
                         if(editOwnUsers == null) {
                            editOwnUsers = uh.getUsersWithProjectPermission(unassignedIssues.get(i).getProject().getId(), UserUtilities.PERMISSION_EDIT_USERS, true);
                            usersWithEditOwnMap.put(unassignedIssues.get(i).getProject().getId(), editOwnUsers);
                         }
                         boolean creatorPresent = false;
                         for(int k = 0; k < possibleOwners.size(); k++) {
                            if(possibleOwners.get(k).getId().equals(unassignedIssues.get(i).getCreator().getId())) {
                                creatorPresent = true;
                                break;
                            }
                         }
                         if(! creatorPresent) {
                             creatorPresent = true;
                             for(int k = 0; k < editOwnUsers.size(); k++) {
                                if(editOwnUsers.get(k).getId().equals(unassignedIssues.get(i).getCreator().getId())) {
                                    tempOwners = new ArrayList<User>();
                                    for(int m = 0; m < possibleOwners.size(); m++) {
                                        tempOwners.add(m,possibleOwners.get(m));
                                    }
                                    tempOwners.add(tempOwners.size() - 1,editOwnUsers.get(k));
                                    Collections.sort(tempOwners, new User.CompareByName());
                                    creatorPresent = false;
                                }
                             }
                         }

                         if(creatorPresent) {
                       for(int k = 0; k < possibleOwners.size(); k++) {
                    %>
                          <option value="<%= possibleOwners.get(k).getId() %>" <%= (unassignedIssues.get(i).getOwner().getId() == possibleOwners.get(k).getId() ? "selected" : "") %>><%= possibleOwners.get(k).getFirstInitial() + " " + possibleOwners.get(k).getLastName() %></option>
                      <%
                             }
                         } else {
                             for(int k = 0; k < tempOwners.size(); k++) {
                      %>
                                <option value="<%= tempOwners.get(k).getId() %>" <%= (unassignedIssues.get(i).getOwner().getId() == tempOwners.get(k).getId() ? "selected" : "") %>><%= tempOwners.get(k).getFirstInitial() + " " + tempOwners.get(k).getLastName() %></option>
                      <%
                             }
                         }
                      %>
                  </html:select></td>
                </html:form>
          <% } else if(UserUtilities.hasPermission(permissions, unassignedIssues.get(i).getProject().getId(), UserUtilities.PERMISSION_ASSIGN_SELF)) { %>
                <html:form action="/assignissue">
                  <html:hidden property="issueId" value="<%= unassignedIssues.get(i).getId().toString() %>"/>
                  <html:hidden property="projectId" value="<%= unassignedIssues.get(i).getProject().getId().toString() %>"/>
                 
                  <%! String styleClass2="(i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\")"; %>
                  <td><html:select property="userId" styleClass="<%=styleClass2 %>" onchange="this.form.submit();">
                    <%= (unassignedIssues.get(i).getOwner().getId().intValue() == -1 ? "<option value=\"-1\">" + ITrackerResources.getString("itracker.web.generic.unassigned", (Locale)pageContext.getAttribute("currLocale")) + "</option>" : "<option value=\"" + unassignedIssues.get(i).getOwner().getId() + "\">" + UserUtilities.getInitial(unassignedIssues.get(i).getOwner().getFirstName()) + " " + unassignedIssues.get(i).getOwner().getLastName() + "</option>") %>
                    <option value="<%= currUser.getId() %>" <%= (unassignedIssues.get(i).getOwner().getId() == currUser.getId() ? "selected" : "") %>><%= currUser.getFirstInitial() + " " + currUser.getLastName() %></option>
                  </html:select></td>
                </html:form>
          <% } else { %>
                <td><it:formatIssueOwner issue="<%= unassignedIssues.get(i) %>" format="short"/></td>
          <% } %>
          <td></td>
          <td align="right" style="white-space: nowrap"><it:formatDate date="<%= unassignedIssues.get(i).getLastModifiedDate() %>"/></td>
        </tr>
<%    } %>
      <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
<%  } %>


<%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) { %>
      <tr>
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.created"/>:</td>
      </tr>
      <tr align="left" class="listHeading">
        <td></td>
        <td></td>
        <td><it:message key="itracker.web.attr.id"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.project"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.status"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.severity"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.description"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.owner"/></td>
        <td></td>
        <td align="right" style="white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
      </tr>

<%
      for(int i = 0; i < createdIssues.size(); i++) {
        if(userPrefs.getNumItemsOnIndex() > 0 && i >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
%>
          <tr class="listRowUnshaded"><td align="left" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
<%
          break;
        }
%>
  <%      User owner = ih.getIssueOwner(createdIssues.get(i).getId());  %>

        <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
          <td>
            <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= createdIssues.get(i).getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= createdIssues.get(i).getId() %>" textActionKey="itracker.web.image.view.texttag"/>
            <% if(IssueUtilities.canEditIssue(createdIssues.get(i), currUser.getId(), permissions)) { %>
                 <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= createdIssues.get(i).getId() %>" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= createdIssues.get(i).getId() %>" textActionKey="itracker.web.image.edit.texttag"/>
            <% } %>
          </td>
          <td></td>
          <td align="left"><%= createdIssues.get(i).getId() %></td>
          <td></td>
          <td style="white-space: nowrap"><%= createdIssues.get(i).getProject().getName() %></td>
          <td></td>
          <td><%= IssueUtilities.getStatusName(createdIssues.get(i).getStatus(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><%= IssueUtilities.getSeverityName(createdIssues.get(i).getSeverity(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><it:formatDescription><%= createdIssues.get(i).getDescription() %></it:formatDescription></td>
          <td></td>
          <td><it:formatIssueOwner issue="<%= createdIssues.get(i) %>" format="short"/></td>
          <td></td>
          <td align="right" style="white-space: nowrap"><it:formatDate date="<%= createdIssues.get(i).getLastModifiedDate() %>"/></td>
        </tr>
<%    } %>
      <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
<%  } %>


<%
   // I could make this all the issues that have changed since the last login.  Wonder if that would be
   // better than the watches? No then you lose them.
%>

<%  if(! UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) { %>
      <tr>
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.watched"/>:</td>
      </tr>
      <tr align="left" class="listHeading">
        <td></td>
        <td></td>
        <td><it:message key="itracker.web.attr.id"/></td>
        <td></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.status"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.severity"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.description"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.owner"/></td>
        <td></td>
        <td align="right" style="white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
      </tr>
<%
      for(int i = 0; i < watchedIssues.size(); i++) {
        if(userPrefs.getNumItemsOnIndex() > 0 && i >= userPrefs.getNumItemsOnIndex() && ! "true".equals(request.getParameter("showAll"))) {
%>
          <tr class="listRowUnshaded"><td align="left" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
<%
          break;
        }

        
%>
  <%     User owner = ih.getIssueOwner(watchedIssues.get(i).getId()); %>

        <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
          <td>
            <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= watchedIssues.get(i).getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= watchedIssues.get(i).getId() %>" textActionKey="itracker.web.image.view.texttag"/>
            <% if(IssueUtilities.canEditIssue(watchedIssues.get(i), currUser.getId(), permissions)) { %>
                 <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= watchedIssues.get(i).getId() %>" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= watchedIssues.get(i).getId() %>" textActionKey="itracker.web.image.edit.texttag"/>
            <% } %>
          </td>
          <td></td>
          <td align="left"><%= watchedIssues.get(i).getId() %></td>
          <td></td>
          <td style="white-space: nowrap"><%= watchedIssues.get(i).getProject().getName() %></td>
          <td></td>
          <td><%= IssueUtilities.getStatusName(watchedIssues.get(i).getStatus(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><%= IssueUtilities.getSeverityName(watchedIssues.get(i).getSeverity(), (Locale)pageContext.getAttribute("currLocale")) %></td>
          <td></td>
          <td><it:formatDescription><%= watchedIssues.get(i).getDescription() %></it:formatDescription></td>
          <td></td>
          <td><it:formatIssueOwner issue="<%= watchedIssues.get(i) %>" format="short"/></td>
          <td></td>
          <td align="right" style="white-space: nowrap"><it:formatDate date="<%= watchedIssues.get(i).getLastModifiedDate() %>"/></td>
        </tr>
<%    } %>
      <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
<%  } %>

<%  if(hiddenSections > 0) { %>
      <tr align="left" class="listRowUnshaded">
        <td colspan="15" align="left"><html:link page="/index.jsp?sections=all"><it:message key="itracker.web.index.viewhidden"/></html:link></td>
      </tr>
      <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
<%  } %>

</table>
 
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
