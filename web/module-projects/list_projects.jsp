<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
final Map<Integer, Set<PermissionType>> permissions = 
    RequestHelper.getUserPermissions(session);
%>

<bean:define id="pageTitleKey" value="itracker.web.listprojects.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
    
  boolean hasProjects = false;
  int numDisplayed = 0;
  int totalOpenIssues = 0;
  int totalResolvedIssues = 0;
  
  for(int i = 0; i < projects.size(); i++) {
      Project project = projects.get(i);
    
    // TODO: move this to the Action class. 
    if(! UserUtilities.hasPermission(permissions, project.getId(), new int[] { UserUtilities.PERMISSION_VIEW_ALL, UserUtilities.PERMISSION_VIEW_USERS })) {
         continue;
    }
    hasProjects = true;
    
    // PENDING: Not scalable, should fetch all in 1 query. 
    int[] projectStats = ph.getProjectStats(project.getId());
    totalOpenIssues += projectStats[0];
    totalResolvedIssues += projectStats[1];
%>
    <tr align="right" class="<%= (numDisplayed % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
      <td nowrap>
        <it:formatImageAction forward="listissues" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.project.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.view.texttag"/>
        <% if(project.getStatus() == ProjectUtilities.STATUS_ACTIVE && UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_CREATE)) { %>
        <it:formatImageAction forward="createissue" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.issue.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.create.texttag"/>
        <% } %>
        <it:formatImageAction forward="searchissues" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/search.gif" altKey="itracker.web.image.search.issue.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.search.texttag"/>
      </td>
      <td></td>
      <td><%= project.getName() %></td>
      <td align="left"><%= projectStats[0] %></td>
      <td align="left"><%= projectStats[1] %></td>
      <td align="left"><%= projectStats[0] + projectStats[1] %></td>
      <td align="right"><it:formatDate date="<%= new Date() %>" emptyKey="itracker.web.generic.notapplicable"/></td>
    </tr>
<%
    numDisplayed++;
  } 
  int totalIssues = totalOpenIssues + totalResolvedIssues;
  %>
  
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
