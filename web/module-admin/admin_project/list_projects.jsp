<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.model.*" %>
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

<bean:define id="pageTitleKey" value="itracker.web.admin.listprojects.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="6"><it:message key="itracker.web.attr.projects"/>:</td>
    <% if(UserUtilities.isSuperUser(permissions)) { %>
         <td align="right">
           <it:formatImageAction action="editprojectform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.project.alt" textActionKey="itracker.web.image.create.texttag"/>
           <it:formatImageAction forward="listattachments" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.attachments.alt" textActionKey="itracker.web.image.view.texttag"/>
         </td>
    <% } %>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.name"/></td>
    <td><it:message key="itracker.web.attr.description"/></td>
    <td><it:message key="itracker.web.attr.created"/></td>
    <td><it:message key="itracker.web.attr.lastmodified"/></td>
    <td align="left"><it:message key="itracker.web.attr.issues"/></td>
  </tr>

<%
  ProjectService ph = (ProjectService)request.getAttribute("ph");
  List<Project> projects = ph.getAllProjects();

  Collections.sort(projects);

  for(int i = 0; i < projects.size(); i++) {
    if(! UserUtilities.hasPermission(permissions, projects.get(i).getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
        continue;
    }
    String style = "";
    if(projects.get(i).getStatus() == ProjectUtilities.STATUS_VIEWABLE) {
        style = "style=\"color: #00BB00;\"";
    } else if(projects.get(i).getStatus() != ProjectUtilities.STATUS_ACTIVE) {
        style = "style=\"color: red;\"";
    }

    if(i % 2 == 1) {
%>
      <tr align="right" class="listRowShaded" <%= style %>>
<%  } else { %>
      <tr align="right" class="listRowUnshaded" <%= style %>>
<%  } %>
      <td>
        <it:formatImageAction action="editprojectform" paramName="id" paramValue="<%= projects.get(i).getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.project.alt" arg0="<%= projects.get(i).getName() %>" textActionKey="itracker.web.image.edit.texttag"/>
      </td>
      <td></td>
      <td><%= projects.get(i).getName() %></td>
      <td><%= projects.get(i).getDescription() %></td>
      <td><it:formatDate date="<%= projects.get(i).getCreateDate() %>"/></td>
      <td><it:formatDate date="<%= projects.get(i).getLastModifiedDate() %>"/></td>
      <td align="left"><%= ph.getTotalNumberIssuesByProject(projects.get(i).getId()) %></td>
    </tr>
<%
  }
%>

  <tr><td><html:img height="8" width="1" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
  <tr><td colspan="7" class="tableNote"><it:message key="itracker.web.admin.listprojects.note"/></td></tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
