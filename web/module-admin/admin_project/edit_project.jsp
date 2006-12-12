<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ page import="java.util.*" %>
 
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>
 
<%@ page import="org.itracker.web.util.*" %>
     <%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
     
<%--<it : checkLogin permission="< % = UserUtilities.PERMISSION_PRODUCT_ADMIN % > "/>--%>

<%
    final Map<Integer, Set<PermissionType>> permissions = (Map<Integer, Set<PermissionType>>)
        session.getAttribute("permissions");
    ProjectService ph = (ProjectService) request.getAttribute("ph");
    UserService uh =  (UserService) request.getAttribute("uh");
   
   Boolean allowPermissionUpdate = (Boolean) request.getAttribute("allowPermissionUpdate");
   
    Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
    if(project == null) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else {
      boolean isUpdate = false;
      if(project.getId().intValue() > 0) {
          isUpdate = true;
      }
%>
      <!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
 
 

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
     <table>
         <tr>
         <td>
      <html:form action="/editproject">
        <html:hidden property="action"/>
        <html:hidden property="id"/>

        <table border="0" cellspacing="0"  cellspacing="1" width="800px" align="left">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.name"/>:</td>
            <td><html:text property="name" styleClass="editColumnText"/></td>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td valign="top" class="editColumnText">
              <html:select property="status" styleClass="editColumnText">
                <html:option value="<%= Integer.toString(ProjectUtilities.STATUS_ACTIVE) %>"><%= ProjectUtilities.getStatusName(ProjectUtilities.STATUS_ACTIVE, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(ProjectUtilities.STATUS_VIEWABLE) %>"><%= ProjectUtilities.getStatusName(ProjectUtilities.STATUS_VIEWABLE, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(ProjectUtilities.STATUS_LOCKED) %>"><%= ProjectUtilities.getStatusName(ProjectUtilities.STATUS_LOCKED, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
              </html:select>
            </td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td><html:text property="description" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= project.getCreateDate() %>"/></td>
          </tr>
          <tr>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.owners"/>:</td>
            <td valign="top" class="editColumnText">
              <html:select property="owners" size="5" multiple="true" styleClass="editColumnText">
              <%  List<User> owners = uh.getUsersWithProjectPermission(project.getId(), UserUtilities.PERMISSION_VIEW_ALL);
                  Collections.sort(owners, new User.CompareByName());

                  for(int i = 0; i < owners.size(); i++) {
              %>
                      <html:option value="<%= owners.get(i).getId().toString() %>"><%= owners.get(i).getFirstName() + " " + owners.get(i).getLastName() %></html:option>
              <% } %>
              </html:select>
            </td>
            <% if(isUpdate) { %>
                <td class="editColumnTitle" valign="top"><it:message key="itracker.web.attr.lastmodified"/>:</td>
                <td class="editColumnText" valign="top"><it:formatDate date="<%= project.getLastModifiedDate() %>"/></td>
            <% } else if(allowPermissionUpdate && UserUtilities.hasPermission(permissions, new Integer(-1), UserUtilities.PERMISSION_USER_ADMIN)) { %>
                <td valign="top" class="editColumnTitle"><it:message key="itracker.web.admin.editproject.addusers"/>:</td>
                <td valign="top" class="editColumnText" nowrap>
                  <html:select property="users" size="5" multiple="true" styleClass="editColumnText">
                    <% List<User> users = uh.getAllUsers();
                       Collections.sort(users, new User.CompareByName());
                       for(int i = 0; i < users.size(); i++) {
                    %>
                          <html:option value="<%= users.get(i).getId().toString() %>"><%= users.get(i).getFirstName() + " " + users.get(i).getLastName() %></html:option>
                    <% } %>
                  </html:select>
                  <html:select property="permissions" size="5" multiple="true" styleClass="editColumnText">
                    <%
                       List<NameValuePair> permissionNames = UserUtilities.getPermissionNames((java.util.Locale)pageContext.getAttribute("currLocale"));
                       for(int i = 0; i < permissionNames.size(); i++) {
                    %>
                          <html:option value="<%= permissionNames.get(i).getValue() %>"><%= permissionNames.get(i).getName() %></html:option>
                    <% } %>
                  </html:select>
                </td>
            <% } %>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

          <tr class="editColumnTitle"><td colspan="4"><it:message key="itracker.web.admin.editproject.options"/>:</td></tr>
          <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
          <tr>
            <td colspan="2" valign="top">
              <table width="100%" cellspacing="0"  cellspacing="1"  border="0">
                <tr>
                  <td width="25"></td>
                  <td></td>
                </tr>
                <tr>
                  <td class="editColumnText"><html:multibox property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML) %>"/></td>
                  <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.html"/></td>
                </tr>
                <tr>
                  <td class="editColumnText"><html:multibox property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS) %>"/></td>
                  <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.resolution"/></td>
                </tr>
                <tr>
                  <td class="editColumnText"><html:multibox property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE) %>"/></td>
                  <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.closed"/></td>
                </tr>
                <%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
              	<c:if test="${allowSelfRegister}">
                      <tr>
                        <td class="editColumnText"><html:multibox  property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE) %>"/></td>
                        <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.srcreate"/></td>
                      </tr>
                 	</c:if>
              </table>
            </td>
            <td colspan="2" valign="top">
              <table width="100%" cellspacing="0"  cellspacing="1"  border="0">
                <tr>
                  <td width="25"></td>
                  <td></td>
                </tr>
                <tr>
                  <td class="editColumnText"><html:multibox property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML) %>"/></td>
                  <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.htmlliteral"/></td>
                </tr>
                <tr>
                  <td class="editColumnText"><html:multibox property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_NO_ATTACHMENTS) %>"/></td>
                  <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.noattach"/></td>
                </tr>
                <tr><td></td></tr>
              	<c:if test="${allowSelfRegister}">
                      <tr>
                        <td class="editColumnText"><html:multibox  property="options" value="<%= Integer.toString(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL) %>"/></td>
                        <td class="editColumnText"><it:message key="itracker.web.admin.editproject.options.srview"/></td>
                      </tr>
              	</c:if>
              </table>
            </td>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

          <%
             List<CustomField> customFields = IssueUtilities.getCustomFields();
             int numRows = (customFields.size() / 2);
             if(customFields.size() > 0) {
          %>
              <tr class="editColumnTitle"><td colspan="4"><it:message key="itracker.web.attr.customfields"/>:</td></tr>
              <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
              <tr>
                <td colspan="2" valign="top">
                  <table width="100%" cellspacing="0"  cellspacing="1"  border="0">
                    <tr>
                      <td width="25"></td>
                      <td></td>
                      <td></td>
                    </tr>
                    <% for(int i = 0; i < numRows + (customFields.size() % 2); i++) { %>
                          <tr>
                            <td class="editColumnText"><html:multibox property="fields" value="<%= customFields.get(i).getId().toString() %>"/></td>
                            <td class="editColumnText"><%= CustomFieldUtilities.getCustomFieldName(customFields.get(i).getId(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                            <td class="editColumnText"><%= CustomFieldUtilities.getTypeString(customFields.get(i).getFieldType(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                          </tr>
                    <% } %>
                  </table>
                </td>
                <td colspan="2" valign="top">
                  <table width="100%" cellspacing="0"  cellspacing="1"  border="0">
                    <tr>
                      <td width="25"></td>
                      <td></td>
                      <td></td>
                    </tr>
                    <%
                       for(int i = numRows + (customFields.size() % 2); i < customFields.size(); i++) {
                    %>
                          <tr>
                            <td class="editColumnText"><html:multibox property="fields" value="<%= customFields.get(i).getId().toString() %>"/></td>
                            <td class="editColumnText"><%= CustomFieldUtilities.getCustomFieldName(customFields.get(i).getId(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                            <td class="editColumnText"><%= CustomFieldUtilities.getTypeString(customFields.get(i).getFieldType(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                          </tr>
                    <% } %>
                  </table>
                </td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>
          <% } %>

          <% if(isUpdate) { %>
            <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
          <% } else { %>
            <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
          <% } %>
        </table>
      </html:form>

         </td></tr>
         <tr><td>

      <% if(isUpdate) { %>
            <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
              <tr>
                <td class="editColumnTitle" colspan="5"><it:message key="itracker.web.attr.scripts"/>:</td>
                <td align="right"><it:formatImageAction action="editprojectscriptform" paramName="projectId" paramValue="<%= project.getId() %>" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.projectscript.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.create.texttag"/></td>
              </tr>
              <tr align="left" class="listHeading">
                <td width="40"></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
                <td><it:message key="itracker.web.attr.field"/></td>
                <td><it:message key="itracker.web.attr.script"/></td>
                <td align="left"><it:message key="itracker.web.attr.priority"/></td>
                <td><it:message key="itracker.web.attr.event"/></td>
              </tr>

              <%
                  List<CustomField> customFields = IssueUtilities.getCustomFields();
                  List<ProjectScript> scripts = project.getScripts();

                  Collections.sort(scripts, new ProjectScript.CompareByFieldAndPriority());

                  for(int i = 0; i < scripts.size(); i++) {
                    if(i % 2 == 1) {
              %>
                      <tr align="right" class="listRowShaded">
              <%    } else { %>
                      <tr align="right" class="listRowUnshaded">
              <%    } %>
                    <td align="right">
                      <it:formatImageAction action="removeprojectscript" paramName="id" paramValue="<%= scripts.get(i).getId() %>" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.projectscript.alt" textActionKey="itracker.web.image.delete.texttag"/>
                    </td>
                    <td></td>
                    <td><%= IssueUtilities.getFieldName(scripts.get(i).getFieldId(), customFields, (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                    <td><%= scripts.get(i).getScript().getName() %></td>
                    <td><%= WorkflowUtilities.getEventName(scripts.get(i).getScript().getEvent(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                    <td align="left"><%= scripts.get(i).getPriority() %></td>
                    </tr>
              <%  } %>
              <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15"/></td></tr>

              <tr>
                <td class="editColumnTitle" colspan="5"><it:message key="itracker.web.attr.versions"/>:</td>
                <td align="right"><it:formatImageAction action="editversionform" paramName="projectId" paramValue="<%= project.getId() %>" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.version.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.createtexttag"/></td>
              </tr>
              <tr align="left" class="listHeading">
                <td width="40"></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
                <td><it:message key="itracker.web.attr.number"/></td>
                <td><it:message key="itracker.web.attr.description"/></td>
                <td><it:message key="itracker.web.attr.lastmodified"/></td>
                <td align="left"><it:message key="itracker.web.attr.issues"/></td>
              </tr>

              <%
                  List<Version> versions = project.getVersions();
                  Collections.sort(versions, new Version.VersionComparator());

                  for(int i = 0; i < versions.size(); i++) {
                    if(i % 2 == 1) {
              %>
                      <tr align="right" class="listRowShaded">
              <%    } else { %>
                      <tr align="right" class="listRowUnshaded">
              <%    } %>
                      <td align="right">
                        <it:formatImageAction action="editversionform" paramName="id" paramValue="<%= versions.get(i).getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.version.alt" arg0="<%= versions.get(i).getNumber() %>" textActionKey="itracker.web.image.edit.texttag"/>
                      </td>
                      <td></td>
                      <td><%= versions.get(i).getNumber() %></td>
                      <td><%= versions.get(i).getDescription() %></td>
                      <td><it:formatDate date="<%= versions.get(i).getLastModifiedDate() %>"/></td>
                      <td align="left"><%= ph.countIssuesByVersion(versions.get(i).getId()) %></td>
                    </tr>
              <%  } %>
              <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15"/></td></tr>

              <tr>
                <td class="editColumnTitle" colspan="5"><it:message key="itracker.web.attr.components"/>:</td>
                <td align="right"><it:formatImageAction action="editcomponentform" paramName="projectId" paramValue="<%= project.getId() %>" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.component.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.create.texttag"/></td>
              </tr>
              <tr align="left" class="listHeading">
                <td width="40"></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
                <td><it:message key="itracker.web.attr.name"/></td>
                <td><it:message key="itracker.web.attr.description"/></td>
                <td><it:message key="itracker.web.attr.lastmodified"/></td>
                <td align="left"><it:message key="itracker.web.attr.issues"/></td>
              </tr>

              <%
                  List<Component> components = project.getComponents();
                  Collections.sort(components, new Component.NameComparator());

                  for(int i = 0; i < components.size(); i++) {
                    if(i % 2 == 1) {
              %>
                      <tr align="right" class="listRowShaded">
              <%    } else { %>
                      <tr align="right" class="listRowUnshaded">
              <%    } %>
                    <td align="right">
                      <it:formatImageAction action="editcomponentform" paramName="id" paramValue="<%= components.get(i).getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.component.alt" arg0="<%= components.get(i).getName() %>" textActionKey="itracker.web.image.edit.texttag"/>
                    </td>
                    <td></td>
                    <td><%= components.get(i).getName() %></td>
                    <td><%= components.get(i).getDescription() %></td>
                    <td><it:formatDate date="<%= components.get(i).getLastModifiedDate() %>"/></td>
                    <td align="left"><%= ph.countIssuesByComponent(components.get(i).getId()) %></td>
                    </tr>
              <%  } %>
            </table>
      <% } %>
         </td>
         </tr>
     </table>
<% } %>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

