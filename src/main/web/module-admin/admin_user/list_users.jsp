<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.*" %>
<%@ page import="org.itracker.web.util.SessionManager" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.model.User" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listusers.title"/>
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

<%
    UserService uh = (UserService)request.getAttribute("uh");
%>

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.users"/>: (<it:message key="itracker.web.admin.listusers.numactive" arg0="<%= Integer.toString(SessionManager.getNumActiveSessions()) %>"/>)</td>
    <% if(uh.allowProfileCreation(null, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) { %>
        <td align="right">
          <it:formatImageAction action="edituserform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.user.alt" textActionKey="itracker.web.image.create.texttag"/>
        </td>
    <% } %>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.login"/></td>
    <td><it:message key="itracker.web.attr.name"/></td>
    <td><it:message key="itracker.web.attr.email"/></td>
    <td><it:message key="itracker.web.attr.admin"/></td>
    <td><it:message key="itracker.web.attr.lastmodified"/></td>
    <td><it:message key="itracker.web.attr.online"/></td>
  </tr>

<%
    List<User> users = uh.getActiveUsers();

    Collections.sort(users, User.NAME_COMPARATOR);

    for(int i = 0; i < users.size(); i++) {
        String style = "";
        style += (users.get(i).getStatus() == UserUtilities.STATUS_LOCKED ? "color: red;" : "");
        style += (users.get(i).getRegistrationType() == UserUtilities.REGISTRATION_TYPE_SELF ? "font-style: italic;" : "");
        Date lastAccess = SessionManager.getSessionLastAccess(users.get(i).getLogin());
        if(i % 2 == 1) {
%>
<% 
String stylestring = null; 
if (! style.equals("")) { 
stylestring = "style=\"" + style + "\""; 
} else { 
stylestring = ""; 
} 
pageContext.setAttribute("stylestring",stylestring);
%>
          <tr align="right" class="listRowShaded" ${stylestring}>
<%      } else { %>
          <tr align="right" class="listRowUnshaded" ${stylestring}> 
<%      } %>
        <td>
          <it:formatImageAction action="edituserform" paramName="id" paramValue="<%= users.get(i).getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.user.alt" arg0="<%= users.get(i).getLogin() %>" textActionKey="itracker.web.image.edit.texttag"/>
          <% if(users.get(i).getStatus() == UserUtilities.STATUS_LOCKED) { %>
                <it:formatImageAction action="unlockuser" paramName="id" paramValue="<%= users.get(i).getId() %>" src="/themes/defaulttheme/images/unlock.gif" altKey="itracker.web.image.unlock.user.alt" arg0="<%= users.get(i).getLogin() %>" textActionKey="itracker.web.image.unlock.texttag"/>
          <% } else { %>
                <it:formatImageAction action="lockuser" paramName="id" paramValue="<%= users.get(i).getId() %>" src="/themes/defaulttheme/images/lock.gif" altKey="itracker.web.image.lock.user.alt" arg0="<%= users.get(i).getLogin() %>" textActionKey="itracker.web.image.lock.texttag"/>
          <% } %>
        </td>
        <td></td>
        <td><%= users.get(i).getLogin() %></td>
        <td><%= users.get(i).getFirstName() %> <%= users.get(i).getLastName() %></td>
        <td><%= users.get(i).getEmail() %></td>
        <td align="left"><%= (users.get(i).isSuperUser() ? ITrackerResources.getString("itracker.web.generic.yes", (java.util.Locale)pageContext.getAttribute("currLocale")) : ITrackerResources.getString("itracker.web.generic.no", (java.util.Locale)pageContext.getAttribute("currLocale"))) %></td>
        <td><it:formatDate date="<%= users.get(i).getLastModifiedDate() %>" format="notime"/></td>
        <td><it:formatDate date="<%= lastAccess %>" format="short" emptyKey="itracker.web.generic.no"/></td>
      </tr>
<%  } %>
  <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="8" width="1"/></td></tr>
  <tr><td colspan="8" class="tableNote"><it:message key="itracker.web.admin.listusers.note"/></td></tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>