<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
 
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>

<%-- <it : checkLogin /> --%>
<%
    final Map<Integer, Set<PermissionType>> permissions = (Map<Integer, Set<PermissionType>>)
        session.getAttribute("permissions");
%>
<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
<bean:define id="pageTitleKey" value="itracker.web.listprojects.title"/>
<bean:define id="pageTitleArg" value=""/>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

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

<table border="0" cellspacing="0"  cellpadding="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.projects"/>:</td>
  </tr>
  <tr align="left" class="listHeading">
    <td width="40"></td>
    <td><html:img width="4" src="../themes/defaulttheme/images/blank.gif"/></td>
    <td align="left"><it:message key="itracker.web.attr.name"/></td>
    <td align="left"><it:message key="itracker.web.attr.openissues"/></td>
    <td align="left"><it:message key="itracker.web.attr.resolvedissues"/></td>
    <td align="left"><it:message key="itracker.web.attr.totalissues"/></td>
    <td align="right"><it:message key="itracker.web.attr.lastissueupdate"/></td>
  </tr>

<%
  ProjectService ph = (ProjectService)request.getAttribute("ph");
  List<Project> projects = (List<Project>)request.getAttribute("projects");
 %> 
  
  <%
  boolean hasProjects = false;
  int numDisplayed = 0;
  int totalIssues = 0;
  int totalOpenIssues = 0;
  int totalResolvedIssues = 0;
  for(int i = 0; i < projects.size(); i++) {
    if(! UserUtilities.hasPermission((java.util.HashMap)request.getSession().getAttribute("permissions"), projects.get(i).getId(), new int[] { UserUtilities.PERMISSION_VIEW_ALL, UserUtilities.PERMISSION_VIEW_USERS })) {
         continue;
    }
    hasProjects = true;

    Object[] projectStats = ph.getProjectStats(projects.get(i).getId());
    totalOpenIssues += Integer.parseInt((String) projectStats[0]);
    totalResolvedIssues += Integer.parseInt((String) projectStats[1]);
    totalIssues += Integer.parseInt((String) projectStats[2]);
%>
    <tr align="right" class="<%= (numDisplayed % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
      <td>
        <it:formatImageAction forward="listissues" paramName="projectId" paramValue="<%= projects.get(i).getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.project.alt" arg0="<%= projects.get(i).getName() %>" textActionKey="itracker.web.image.view.texttag"/>
        <% if(projects.get(i).getStatus() == ProjectUtilities.STATUS_ACTIVE && UserUtilities.hasPermission(permissions, projects.get(i).getId(), UserUtilities.PERMISSION_CREATE)) { %>
        <it:formatImageAction forward="createissue" paramName="projectId" paramValue="<%= projects.get(i).getId() %>" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.issue.alt" arg0="<%= projects.get(i).getName() %>" textActionKey="itracker.web.image.create.texttag"/>
        <% } %>
        <it:formatImageAction forward="searchissues" paramName="projectId" paramValue="<%= projects.get(i).getId() %>" src="/themes/defaulttheme/images/search.gif" altKey="itracker.web.image.search.issue.alt" arg0="<%= projects.get(i).getName() %>" textActionKey="itracker.web.image.search.texttag"/>
      </td>
      <td></td>
      <td><%= projects.get(i).getName() %></td>
      <td align="left"><%= projectStats[0] %></td>
      <td align="left"><%= projectStats[1] %></td>
      <td align="left"><%= projectStats[2] %></td>
      <td align="right"><it:formatDate date="<%= (Date) projectStats[3] %>" emptyKey="itracker.web.generic.notapplicable"/></td>
    </tr>
<%
    numDisplayed++;
  } %>
  
 <% if(hasProjects) {
%>
    <tr><td colspan="7"><html:img height="3" src="../themes/defaulttheme/images/blank.gif"/></td></tr>
    <tr><td colspan="7" class="listHeadingBackground"><html:img height="2" src="../themes/defaulttheme/images/blank.gif"/></td></tr>
    <tr><td colspan="7"><html:img height="3" src="../themes/defaulttheme/images/blank.gif"/></td></tr>
   <tr></tr>
    <tr class="listRowUnshaded">
      <td colspan="2"></td>
      <td align="right"><it:message key="itracker.web.attr.total"/>:</td>
      <td align="left"><%= totalOpenIssues %></td>
      <td align="left"><%= totalResolvedIssues %></td>
      <td align="left"><%= totalIssues %></td>
      <td></td>
    </tr>
<% } else { %>
      <tr align="left"><td colspan="7" class="listRowUnshaded" style="text-align: center;"><it:message key="itracker.web.error.noprojects"/></td></tr>
<%
  }
%>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
