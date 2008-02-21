<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.web.util.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.core.resources.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO : move redirect logic to the Action class. 
    User user = (User) session.getAttribute(Constants.EDIT_USER_KEY);
    java.util.HashMap userPermissions = (java.util.HashMap) session.getAttribute(Constants.EDIT_USER_PERMS_KEY);
    %>
  <%  if(user == null) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else {
      UserService uh = (UserService) request.getAttribute("uh");
	  ProjectService ph = (ProjectService) request.getAttribute("ph");

      boolean isUpdate = false;
      if(user.getId().intValue() > 0) {
          isUpdate = true;
      }

      boolean allowProfileUpdate = uh.allowProfileUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
      boolean allowPasswordUpdate = uh.allowPasswordUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
      boolean allowPermissionUpdate = uh.allowPermissionUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
%>

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

      <html:form action="/edituser">
        <html:hidden property="action"/>
        <html:hidden property="id"/>

        <table border="0" cellspacing="0"  cellspacing="1" width="800px">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
            <% if(isUpdate && ! allowProfileUpdate) { %>
                <td class="editColumnText"><%= user.getLogin() %><html:hidden property="login" /> 
                	*</td>
            <% } else { %>
                <td><html:text property="login" styleClass="editColumnText"/></td>
            <% } %>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td class="editColumnText"><%= UserUtilities.getStatusName(user.getStatus(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.firstname"/>:</td>
            <% if(isUpdate && ! allowProfileUpdate) { %>
                <td class="editColumnText"><%= user.getFirstName() %><html:hidden property="firstName" /> 
                	*</td>
            <% } else { %>
                <td><html:text property="firstName" styleClass="editColumnText"/></td>
            <% } %>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= user.getCreateDate() %>"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastname"/>:</td>
            <% if(isUpdate && ! allowProfileUpdate) { %>
                <td class="editColumnText"><%= user.getLastName() %><html:hidden property="lastName" /> 
                	*</td>
            <% } else { %>
                <td><html:text property="lastName" styleClass="editColumnText"/></td>
            <% } %>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= user.getLastModifiedDate() %>"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.email"/>:</td>
            <% if(isUpdate && ! allowProfileUpdate) { %>
                <td class="editColumnText"><%= user.getEmail() %><html:hidden property="email" /> 
                	*</td>
            <% } else { %>
                <td><html:text property="email" styleClass="editColumnText"/></td>
            <% } %>
          </tr>
          <% if(! isUpdate || allowPasswordUpdate) { %>
                      <tr>
                <td class="editColumnTitle"><br></td>
                <td> </td>
              </tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.password"/>:</td>
                <td><html:password property="password" styleClass="editColumnText" redisplay="false"/></td>
              </tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.confpassword"/>:</td>
                <td><html:password property="confPassword" styleClass="editColumnText" redisplay="false"/></td>
              </tr>
          <% } %>
                      <tr>
                <td class="editColumnTitle"><br></td>
                <td> </td>
  

            <td class="editColumnTitle"><it:message key="itracker.web.attr.superuser"/>:</td>
            <% if(isUpdate && ! allowProfileUpdate) { %>
                <td class="editColumnText"><%= ITrackerResources.getString((user.isSuperUser() ? "itracker.web.generic.yes" : "itracker.web.generic.no"), (java.util.Locale)pageContext.getAttribute("currLocale")) %><html:hidden property="superUser" /></td>
            <% } else { %>
                <td class="editColumnText">
                  <html:radio property="superUser" value="true"/><it:message key="itracker.web.generic.yes"/> &nbsp;&nbsp;&nbsp;&nbsp;
                  <html:radio property="superUser" value="false"/><it:message key="itracker.web.generic.no"/>
                </td>
            <% } %>
          </tr>
                    <tr>
                <tr>
            <td class="editColumnText"><sup>*</sup><it:message key="itracker.web.generic.reqfield"/></td>
               <td></td>
          
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>
        </table>
          <% if(isUpdate) { %>
                <html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit> 
          <% } else { %>
                <html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit> 
          <% } %>
     <br/>   <br/>
     
        <table border="0" cellspacing="0"  cellspacing="1"  width="100%" align="center">
          <tr>
            <td class="editColumnTitle" colspan="8"><it:message key="itracker.web.attr.permissions"/>:</td>
          </tr>
          <tr class="listHeading">
            <td colspan="2"></td>
            <td align="left"><it:message key="itracker.web.attr.permission"/></td>
            <td align="left"><it:message key="itracker.web.attr.lastmodified"/></td>
            <td colspan="2"></td>
            <td align="left"><it:message key="itracker.web.attr.permission"/></td>
            <td align="left"><it:message key="itracker.web.attr.lastmodified"/></td>
          </tr>
          <tr>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="10"/></td>
            <td colspan="3"></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="10"/></td>
            <td colspan="3"></td>
          </tr>
          <%
              List<Project> projects = ph.getAllAvailableProjects();
              Collections.sort(projects);
              List<NameValuePair> permissionNames = UserUtilities.getPermissionNames((java.util.Locale)pageContext.getAttribute("currLocale"));

              for(int i = 0; i < projects.size(); i++) {
          %>
                  <tr align="left" class="listRowShaded"><td colspan="8"><input title="toggle all" type="checkbox" onchange="toggleProjectPermissionsChecked(this)" name="Proj<%= projects.get(i).getId() %>"/>&nbsp;<it:message key="itracker.web.attr.project"/> <%= projects.get(i).getName() %></td></tr>
                  <tr align="right" class="listRowUnshaded">
                  <%
                        java.util.HashMap projectPermissions = (java.util.HashMap) userPermissions.get(projects.get(i).getId());
                        java.util.Date currentPermissionDate = null;
                        String checkmarkImage = "";
                        for(int j = 0; j < permissionNames.size(); j++) {
                            currentPermissionDate = null;
                            if(projectPermissions != null && projectPermissions.get(permissionNames.get(j).getValue()) != null) {
                                currentPermissionDate = ((Permission) projectPermissions.get(permissionNames.get(j).getValue())).getCreateDate();
                            }
                            if(currentPermissionDate == null) {
                                checkmarkImage = "/themes/defaulttheme/images/checkmark_empty.png";
                            } else {
                                checkmarkImage = "/themes/defaulttheme/images/checkmark_checked.png";
                            }

                            if(j != 0 && j % 2 == 0) {
                  %>
                              </tr><tr align="right" class="listRowUnshaded">
                  <%        } %>
                            <td></td>
                            <% String keyName = "permissions(Perm" + permissionNames.get(j).getValue() + "Proj" + projects.get(i).getId() + ")"; %>
                            <% if(isUpdate && ! allowPermissionUpdate) { %>
                                <td align="left"><html:img page="<%= checkmarkImage %>"/><html:hidden property="<%= keyName %>" /></td>
                            <% } else { %>
                                <td align="left"><html:checkbox property="<%= keyName %>" value="on"/></td>
                            <% } %>
                            <td><%= permissionNames.get(j).getName() %></td>
                            <td><it:formatDate date="<%= currentPermissionDate %>"/></td>
                  <%    } %>
                  </tr>
                  <tr><td colspan="8"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="5" width="1"/></td></tr>
          <% } %>
          <tr><td colspan="8"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
        </table>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        	<c:choose>
        		<c:when test="${isUpdate}">  
        		<tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
        		</c:when> 
        		<c:otherwise>
        		<tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
        		</c:otherwise>
        	</c:choose>    
        </table>

      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>
