<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.IssueUtilities" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.core.resources.*" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<!-- import? UserUtilities? --> 
<%@ page import="org.itracker.model.PermissionType" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.services.util.ProjectUtilities" %>

<%-- <it:checkLogin/> --%>
<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->

<%
 
    User um = (User)session.getAttribute("currUser");
    Integer currUserId = um.getId();
    UserPreferences userPrefs = (UserPreferences) session.getAttribute("preferences");
    Project project = (Project)request.getAttribute("project");
    List<Issue> issues = (List<Issue>)request.getAttribute("issues");
    Integer start = (Integer)request.getAttribute("start");
    String orderParam = (String)request.getAttribute("orderParam");
  
    %>
    
    <%
    if(project == null) {
%>
      <it:addError key="itracker.web.error.invalidproject"/>
      <logic:forward name="error"/>
<%  } else if(project.getStatus() != ProjectUtilities.STATUS_ACTIVE && project.getStatus() != ProjectUtilities.STATUS_VIEWABLE) { %>
      <it:addError key="itracker.web.error.projectlocked"/>
      <logic:forward name="error"/>
<%  } else { %>
      <bean:define id="pageTitleKey" value="itracker.web.listissues.title"/>
      <bean:define id="pageTitleArg" value="<%= project.getName() %>"/>
     <%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

 
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle" colspan="14"><it:message key="itracker.web.attr.issues"/>:</td>
          <td align="right">
            <% if(project.getStatus() == ProjectUtilities.STATUS_ACTIVE && UserUtilities.hasPermission((java.util.HashMap)request.getSession().getAttribute("permissions"), project.getId(), UserUtilities.PERMISSION_EDIT)) { %>
                  <it:formatImageAction forward="createissue" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.issue.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.create.texttag"/>
            <% } %>
            <it:formatImageAction forward="searchissues" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/search.gif" altKey="itracker.web.image.search.issue.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.search.texttag"/>
          </td>
        </tr>
        <tr align="left" class="listHeading">
          <td width="55"></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="listHeading" order="id"><it:message key="itracker.web.attr.id"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="listHeading" order="stat"><it:message key="itracker.web.attr.status"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="listHeading" order="sev"><it:message key="itracker.web.attr.severity"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:message key="itracker.web.attr.components"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:message key="itracker.web.attr.description"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="listHeading" order="own"><it:message key="itracker.web.attr.owner"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="listHeading" order="lm"><it:message key="itracker.web.attr.lastmodified"/></it:formatPaginationLink></td>
        </tr>

<%
        int j = 0;
        int k = 0;
        int numViewable = 0;
        boolean hasIssues = false;
        boolean hasViewAll = UserUtilities.hasPermission((java.util.HashMap)request.getSession().getAttribute("permissions"), project.getId(), UserUtilities.PERMISSION_VIEW_ALL);
        Map<Integer, Set<PermissionType>> userPermissions = (Map<Integer, Set<PermissionType>>)request.getSession().getAttribute("permissions");
        if(hasViewAll) {
            numViewable = issues.size();
        } else {
        	
            for(int i = 0; i < issues.size(); i++) {
            	
                if(IssueUtilities.canViewIssue(issues.get(i), currUserId, userPermissions)) {
                    numViewable++;
                }
            }
        }

        for(int i = 0; i < issues.size(); i++) {
          if(! hasViewAll && ! IssueUtilities.canViewIssue(issues.get(i), currUserId, userPermissions)) {
              continue;
          }
          hasIssues = true;
          if(start > 0 && k < start) {
              k++;
              continue;
          }
          if(userPrefs.getNumItemsOnIssueList() > 0 && j >= userPrefs.getNumItemsOnIssueList()) {
              break;
          }
%>
          <tr align="right" class="<%= (j % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
            <td>
              <it:formatImageAction forward="viewissue" paramName="id" paramValue="<%= issues.get(i).getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="<%= issues.get(i).getId() %>" textActionKey="itracker.web.image.view.texttag"/>
              <%
              
                 if(project.getStatus() == ProjectUtilities.STATUS_ACTIVE) {
                     if(IssueUtilities.canEditIssue(issues.get(i), currUserId,userPermissions)) {
              %>
                        <it:formatImageAction action="editissueform" paramName="id" paramValue="<%= issues.get(i).getId() %>" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="<%= issues.get(i).getId() %>" textActionKey="itracker.web.image.edit.texttag"/>
              <%
                     }
                 }
              %>


              <% if(project.getStatus() == ProjectUtilities.STATUS_ACTIVE && ! IssueUtilities.hasIssueNotification(issues.get(i), project, currUserId)) { %>
                    <it:formatImageAction forward="watchissue" paramName="id" paramValue="<%= issues.get(i).getId() %>" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="<%= issues.get(i).getId() %>" textActionKey="itracker.web.image.watch.texttag"/>
              <% } %>
            </td>
            <td></td>
            <td><%= issues.get(i).getId() %></td>
            <td></td>
            <td><%= IssueUtilities.getStatusName(issues.get(i).getStatus(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
            <td></td>
            <td><%= IssueUtilities.getSeverityName(issues.get(i).getSeverity(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
            <td></td>
            <td><%= (issues.get(i).getComponents().size() == 0 ? ITrackerResources.getString("itracker.web.generic.unknown", (java.util.Locale)pageContext.getAttribute("currLocale")) : issues.get(i).getComponents().get(0).getName() + (issues.get(i).getComponents().size() > 1 ? " (+)" : "")) %></td>
            <td></td>
            <td><it:formatDescription><%= issues.get(i).getDescription() %></it:formatDescription></td>
            <td></td>
            <td><it:formatIssueOwner issue="<%= issues.get(i) %>" format="short"/></td>
            <td></td>
            <td><it:formatDate date="<%= issues.get(i).getLastModifiedDate() %>"/></td>
          </tr>
<%
          j++;
        }
        if(! hasIssues) {
%>
            <tr class="listRowUnshaded" align="left"><td colspan="15" align="left"><it:message key="itracker.web.error.noissues"/></td></tr>
<%      } else { %>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td>
            </tr>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">
                <it:message key="itracker.web.generic.totalissues" arg0="<%= Integer.toString(numViewable) %>"/>
              </td>
            </tr>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">
<%               if(k > 0 && userPrefs.getNumItemsOnIssueList() > 0) { %>
                      <it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="headerLinks"
                                               start="<%= (k - userPrefs.getNumItemsOnIssueList()) %>" order="<%= orderParam %>">
                        <it:message key="itracker.web.generic.prevpage"/>
                      </it:formatPaginationLink>
<%                } %>
<%                if((j + k) < numViewable && userPrefs.getNumItemsOnIssueList() > 0) { %>
                      <it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="headerLinks"
                                               start="<%= (j + k) %>" order="<%= orderParam %>">
                        <it:message key="itracker.web.generic.nextpage"/>
                      </it:formatPaginationLink>
<%                } %>
              </td>
            </tr>
<%      } %>
      </table>
<%  } %>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
