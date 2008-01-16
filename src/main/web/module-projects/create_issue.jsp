<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.web.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %> 
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO : move redirect logic to Action class. 
    Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
    Map<Integer, List<NameValuePair>> listOptions = 
            RequestHelper.getListOptions(session);
            
    if(project == null) {
%>
      <logic:forward name="unauthorized"/>
<%  } else { %>
<bean:define id="pageTitleKey" value="itracker.web.createissue.title"/>
<bean:define id="pageTitleArg" value="<%= project.getName() %>"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<%-- <html:javascript formName="createIssueForm"/> replaced by --%>
      <html:javascript formName="issueForm"/>

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

      <html:form action="/createissue" focus="description" enctype="multipart/form-data" onsubmit="return validateCreateIssueForm(this);">
        <html:hidden property="projectId" value="<%= project.getId().toString() %>"/>

        <table border="0" cellspacing="0"  cellspacing="1"  width="800px">
          <tr>
            <td width="15%"></td>
            <td width="35%"></td>
            <td width="15%"></td>
            <td width="35%"></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td colspan="3" class="editColumnText"><html:text size="80" maxlength="255" property="description" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td class="editColumnText"><%= IssueUtilities.getStatusName(IssueUtilities.STATUS_NEW, (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>:</td>
              <%
                 List<NameValuePair> possibleOwners = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_OWNER);
                 if(possibleOwners.size() > 0) {
              %>
                   <td><html:select property="ownerId" styleClass="editColumnText">
                      <html:option value="-1" key="itracker.web.generic.unassigned"/>
                      <% for(int i = 0; i < possibleOwners.size(); i++) { %>
                           <html:option value="<%= possibleOwners.get(i).getValue() %>"><%= possibleOwners.get(i).getName() %></html:option>
                      <% } %>
                   </html:select></td>
              <% } else { %>
                   <td class="editColumnText"><it:message key="itracker.web.generic.unassigned"/></td>
              <% } %>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>:</td>
            <td class="editColumnText">
              <html:select property="severity" styleClass="editColumnText">
              <%
                 List<NameValuePair> severities = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_SEVERITY);
                 for(int i = 0; i < severities.size(); i++) {
              %>
                   <html:option value="<%= severities.get(i).getValue() %>"><%= severities.get(i).getName() %></html:option>
              <% } %>
              </html:select>
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.creator"/>:</td>
            <%
                List<NameValuePair> possibleCreators = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_CREATOR);
                if(possibleCreators.size() > 0) {
            %>
                  <td><html:select property="creatorId" styleClass="editColumnText">
                  <% for(int i = 0; i < possibleCreators.size(); i++) { %>
                       <html:option value="<%= possibleCreators.get(i).getValue() %>"><%= possibleCreators.get(i).getName() %></html:option>
                  <% } %>
                  </html:select></td>
            <% } else { %>
                  <td class="editColumnText">currUser<!-- TODO: fix this <- % -= currUser.getFirstName() + " " + currUser.getLastName() % >--></td>
            <% } %>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
          <tr>
            <%
               List<NameValuePair> components = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_COMPONENTS);
               List<NameValuePair> versions = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_VERSIONS);
               if(components.size() > 0) {
            %>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>:</td>
                  <td valign="top" class="editColumnText">
                    <html:select property="components" size="5" multiple="true" styleClass="editColumnText">
                      <% for(int i = 0; i < components.size(); i++) { %>
                           <html:option value="<%= components.get(i).getValue() %>"><%= components.get(i).getName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
            <%  } else { %>
                  <td></td>
                  <td></td>
            <%  } %>
            <% if(versions.size() > 0) { %>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>:</td>
                  <td valign="top" class="editColumnText">
                    <html:select property="versions" size="5" multiple="true" styleClass="editColumnText">
                      <% for(int i = 0; i < versions.size(); i++) { %>
                            <html:option value="<%= versions.get(i).getValue() %>"><%= versions.get(i).getName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
            <%  } else { %>
                  <td></td>
                  <td></td>
            <%  } %>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>

          <%
             List<CustomField> projectFields = project.getCustomFields();
             if(projectFields != null && projectFields.size() > 0) {
                Collections.sort(projectFields, CustomField.ID_COMPARATOR);
                
          %>
          <!-- TODO: never used? int numRows = (projectFields.size() / 2);-->
                <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>:</td></tr>
                <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
                <tr>
                <%
                   for(int i = 0; i < projectFields.size(); i++) {
                       if(i % 2 == 0) {
                %>
                           </tr>
                           <tr>
                <%     } %>
                       <it:formatCustomField field="<%= projectFields.get(i) %>" formName="issueForm" listOptions="<%= listOptions %>"/>
                <% } %>
                </tr>
                
            <%-- TODO reinsert this once related issues have been implemented correctly
                <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
              --%>
          <% } %>
            <%-- TODO reinsert this once related issues have been implemented correctly
             <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
             <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
            <tr><td colspan="4" class="editColumnText">
              <it:message key="itracker.web.attr.thisissue"/>
              <html:select property="relationType" styleClass="editColumnText">
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_RELATED_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_RELATED_P, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_P, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_C, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_P, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_C, currLocale) %></html:option>
              </html:select>
              <it:message key="itracker.web.attr.issue"/>
              <html:text size="5" property="relatedIssueId" styleClass="editColumnText"/>.
            </td></tr>
            --%>
            <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td></tr>

          <% if(! ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions())) { %>
              <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.addattachment"/>:</td></tr>
              <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
              <tr>
                <td class="listRowText"><it:message key="itracker.web.attr.description"/>:</td>
                <td class="editColumnText"><html:text property="attachmentDescription" size="30" maxlength="60" styleClass="editColumnText"/></td>
                <td class="listRowText"><it:message key="itracker.web.attr.file"/>:</td>
                <td class="editColumnText"><html:file property="attachment" styleClass="editColumnText"/></td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
          <% } %>

          <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.detaileddescription"/>:</td></tr>
          <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>

          <%
             String wrap = "soft";
             if(ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, project.getOptions())) {
                wrap = "hard";
             }
          %>
          <tr><td colspan="4" class="editColumnText" style="text-align: center;"><textarea name="history" wrap="<%= wrap %>" cols="110" rows="6" class="editColumnText"><bean:write name="issueForm" property="history"/></textarea></td></tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td></tr>

          <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
        </table>
        <br/>
      </html:form>

      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>