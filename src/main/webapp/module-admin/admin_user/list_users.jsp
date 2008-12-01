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

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
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
    List<User> users = uh.getAllUsers();

    Collections.sort(users, User.NAME_COMPARATOR);
	User currentUser = null;
	Iterator<User> usersIt = users.iterator();
	boolean shade = true;
	String style;
	while (usersIt.hasNext()) {
		shade = !shade;
		currentUser = usersIt.next();
        style  = (currentUser.getStatus() == UserUtilities.STATUS_LOCKED ? "color: red;" : "");
        style += (currentUser.getRegistrationType() == UserUtilities.REGISTRATION_TYPE_SELF ? "font-style: italic;" : "");
        Date lastAccess = SessionManager.getSessionLastAccess(currentUser.getLogin());
		if (null != style && !(style.length() == 0)) { 
			style = "style=\"" + style + "\""; 
		} else { 
			style = ""; 
		} 
		pageContext.setAttribute("style",style);
		
        if(shade) { %>
          <tr align="right" class="listRowShaded" ${style}>
<%      } else { %>
          <tr align="right" class="listRowUnshaded" ${style}> 
<%      } %>
        <td>
          <it:formatImageAction action="edituserform" paramName="id" paramValue="<%= currentUser.getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.user.alt" arg0="<%= currentUser.getLogin() %>" textActionKey="itracker.web.image.edit.texttag"/>
          <% if(currentUser.getStatus() == UserUtilities.STATUS_LOCKED) { %>
                <it:formatImageAction action="unlockuser" paramName="id" paramValue="<%= currentUser.getId() %>" src="/themes/defaulttheme/images/unlock.gif" altKey="itracker.web.image.unlock.user.alt" arg0="<%= currentUser.getLogin() %>" textActionKey="itracker.web.image.unlock.texttag"/>
          <% } else { %>
                <it:formatImageAction action="lockuser" paramName="id" paramValue="<%= currentUser.getId() %>" src="/themes/defaulttheme/images/lock.gif" altKey="itracker.web.image.lock.user.alt" arg0="<%= currentUser.getLogin() %>" textActionKey="itracker.web.image.lock.texttag"/>
          <% } %>
        </td>
        <td></td>
        <td><%= currentUser.getLogin() %></td>
        <td><%= currentUser.getFirstName() %> <%= currentUser.getLastName() %></td>
        <td><%= currentUser.getEmail() %></td>
        <td align="left"><%= (currentUser.isSuperUser() ? ITrackerResources.getString("itracker.web.generic.yes", LoginUtilities.getCurrentLocale(request)) : ITrackerResources.getString("itracker.web.generic.no", LoginUtilities.getCurrentLocale(request))) %></td>
        <td><it:formatDate date="<%= currentUser.getLastModifiedDate() %>" format="notime"/></td>
        <td><it:formatDate date="<%= lastAccess %>" format="short" emptyKey="itracker.web.generic.no"/></td>
      </tr>
<%  } %>
  <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="8" width="1"/></td></tr>
  <tr><td colspan="8" class="tableNote"><it:message key="itracker.web.admin.listusers.note"/></td></tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
<%@page import="org.itracker.web.util.LoginUtilities"%></html>
