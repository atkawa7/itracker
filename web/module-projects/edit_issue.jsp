<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.web.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO : move redirect logic to Action class. 
	IssueService ih = (IssueService)request.getAttribute("ih");
	User um = (User)request.getSession().getAttribute("currUser");
	
    Issue issue = (Issue) session.getAttribute(Constants.ISSUE_KEY);
    Map<Integer, List<NameValuePair>> listOptions = 
            RequestHelper.getListOptions(session);
    final Map<Integer, Set<PermissionType>> permissions = 
            RequestHelper.getUserPermissions(session);
    
    Project project = (issue != null ? issue.getProject() : null);
    if(issue == null || project == null) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else { %><%-- 
      String caller = null;
      String description = null;
      String resolution = null; --%>
      <%
      Integer issueId = issue.getId();
      Integer currUserId = um.getId();
      boolean hasFullEdit = UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_EDIT_FULL);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.editissue.title"/>
<bean:define id="pageTitleArg" value="<%= issue.getId().toString() %>"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
<%-- <html:javascript formName="editIssueForm"/> replaced by --%>
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

      <html:form action="/editissue" method="post" enctype="multipart/form-data" onsubmit="return validateEditIssueForm(this);">
        <html:hidden property="id"/>
        <html:hidden property="projectId"/>
        <html:hidden property="prevStatus"/>
        <html:hidden property="caller"/>

        <table border="0" cellspacing="0" cellspacing="1" width="800px">
          <tr>
            <td width="15%"></td>
            <td width="35%"></td>
            <td width="15%"></td>
            <td width="35%"></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td colspan="3" class="editColumnText">
              <table border="0" cellspacing="0" cellspacing="1" width="100%">
                <tr>
                  <td align="left" width="100%" class="editColumnText"><html:text size="80" property="description" styleClass="editColumnText"/></td>
                  <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.actions"/>:&nbsp;</td>
                  <td align="right" valign="bottom" class="editColumnText" style="white-space: nowrap;" nowrap>
                    <it:formatImageAction forward="listissues" paramName="projectId" paramValue="<%= project.getId() %>" caller="editissue" src="/themes/defaulttheme/images/list.gif" altKey="itracker.web.image.issuelist.issue.alt" textActionKey="itracker.web.image.issuelist.texttag"/>
                    <% if(! ih.hasIssueNotification(issue.getId(), currUserId)) { %>
                         <it:formatImageAction forward="watchissue" paramName="id" paramValue="<%= issue.getId() %>" caller="editissue" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="<%= issue.getId() %>" textActionKey="itracker.web.image.watch.texttag"/>
                    <% } %>
                    <% if(UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_EDIT)) { %>
                         <it:formatImageAction action="moveissueform" paramName="id" paramValue="<%= issue.getId() %>" caller="editissue" src="/themes/defaulttheme/images/move.gif" altKey="itracker.web.image.move.issue.alt" arg0="<%= issue.getId() %>" textActionKey="itracker.web.image.move.texttag"/>
                         <%-- TODO re-include this once relate issue correctly works 
                           <it:formatImageAction forward="relateissue" paramName="id" paramValue="<%= issue.getId() %>" caller="editissue" src="/themes/defaulttheme/images/link.gif" altKey="itracker.web.image.link.issue.alt" textActionKey="itracker.web.image.link.texttag"/>
                         --%>
                    <% } %>
                    <% if(project.getStatus() == ProjectUtilities.STATUS_ACTIVE && UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_CREATE)) { %>
                        <it:formatImageAction forward="createissue" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.issue.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.create.texttag"/>
                    <% } %>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td class="editColumnText">
              <%
                 List<NameValuePair> statuses = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_STATUS);
                 if(statuses.size() > 0) {
              %>
                   <html:select property="status" styleClass="editColumnText">
              <%     for(int i = 0; i < statuses.size(); i++) { %>
                       <html:option styleClass="editColumnText" value="<%= statuses.get(i).getValue() %>"><%= statuses.get(i).getName() %></html:option>
              <%     } %>
                   </html:select>
              <% } else { %>
                   <%= IssueUtilities.getStatusName(issue.getStatus(), (java.util.Locale)pageContext.getAttribute("currLocale")) %>
              <% } %>
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.creator"/>:</td>
            <td class="editColumnText">
              <it:formatDate date="<%= issue.getCreateDate() %>"/>
              (<%= issue.getCreator().getFirstName() + " " + issue.getCreator().getLastName() %>)
            </td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.resolution"/>:</td>
            <td class="editColumnText">
              <% 
              
              if(um.isSuperUser() || (hasFullEdit && (issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED && issue.getStatus() < IssueUtilities.STATUS_CLOSED))) { %>
                  <% if(ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, project.getOptions())) { %>
                      <html:select property="resolution" styleClass="editColumnText">
                        <option value=""></option>
                        <%
                           List<NameValuePair> possResolutions = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_RESOLUTION);
                           for(int i = 0; i < possResolutions.size(); i++) {
                        %>
                              <html:option styleClass="editColumnText" value="<%= possResolutions.get(i).getValue() %>"><%= possResolutions.get(i).getName() %></html:option>
                        <% } %>
                      </html:select>
                  <% } else { %>
                      <html:text size="20" property="resolution" styleClass="editColumnText"/>
                  <% } %>
              <% } else { %>
                    <%= issue.getResolution() %>
              <% } %>
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= issue.getLastModifiedDate() %>"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>:</td>
            <td class="editColumnText">
              <% if(hasFullEdit) { %>
           
                    <html:select property="severity" styleClass="editColumnText">
                    <%
                       List<NameValuePair> severities = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_SEVERITY);
              if (!severities.isEmpty()) {  
                       for(int i = 0; i < severities.size(); i++) {
                    %>
                         <html:option value="<%= severities.get(i).getValue() %>" styleClass="editColumnText"><%= severities.get(i).getName() %></html:option>
                    <% } %>   <% } else { %>
                <html:option value="0" styleClass="editColumnText">is empty, something's wrong in the call to WorkflowUtilities.getListOptions(</html:option>
                   <% } %>
                    </html:select>
              <% } else { %>
                    <%= IssueUtilities.getSeverityName(issue.getSeverity(), (java.util.Locale)pageContext.getAttribute("currLocale")) %>
              <% } %>
           
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>:</td>
              <% if(issue.getStatus() >= IssueUtilities.STATUS_RESOLVED) { %>
                  <td class="editColumnText"><%= (issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", (java.util.Locale)pageContext.getAttribute("currLocale")) : issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName()) %></td>
              <% } else { %>
                  <input type="hidden" name="currentOwner" value="<%= (issue.getOwner() == null ? new Integer(-1) : issue.getOwner().getId()) %>">
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
                       <td class="editColumnText"><%= (issue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", (java.util.Locale)pageContext.getAttribute("currLocale")) : issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName()) %></td>
                  <% } %>
              <% } %>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
          <tr>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.project"/>:</td>
            <td valign="top" class="editColumnText"><%= issue.getProject().getName() %></td>
            <%
               List<NameValuePair> components = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_COMPONENTS);
               List<NameValuePair> versions = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_VERSIONS);
               List<NameValuePair> targetVersion = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_TARGET_VERSION);
            %>
            <% if(targetVersion.size() > 0) { %>
                <td valign="top" class="editColumnTitle" style="white-space: nowrap;" nowrap><it:message key="itracker.web.attr.target"/>:&nbsp;</td>
                <td valign="top" class="editColumnText">
                  <% if(hasFullEdit) { %>
                        <html:select property="targetVersion" styleClass="editColumnText">
                          <html:option value="-1">&nbsp;</html:option>
                          <% for(int i = 0; i < targetVersion.size(); i++) { %>
                                <html:option value="<%= targetVersion.get(i).getValue() %>" styleClass="editColumnText"><%= targetVersion.get(i).getName() %></html:option>
                          <% } %>
                        </html:select>
                  <% } else { %>
                        <%= issue.getTargetVersion().getNumber() %>
                  <% }  %>
                </td>
            <% } %>
          </tr>
          <tr>
            <% if(components.size() > 0) { %>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>:</td>
                  <td valign="top" class="editColumnText">
                    <% if(hasFullEdit) { %>
                          <html:select property="components" size="5" multiple="true" styleClass="editColumnText">
                          <% for(int i = 0; i < components.size(); i++) { %>
                                <html:option value="<%= components.get(i).getValue() %>" styleClass="editColumnText"><%= components.get(i).getName() %></html:option>
                          <% } %>
                          </html:select>
                    <% } else { %>
                          <%
                              List<Component> issueComponents = issue.getComponents();
                              Collections.sort(issueComponents);
                              for(int i = 0; i < issueComponents.size(); i++) {
                          %>
                              <%= issueComponents.get(i).getName() %><br/>
                          <% } %>
                    <% }  %>
                  </td>
            <%  } else { %>
                  <td></td>
                  <td></td>
            <%  } %>
            <% if(versions.size() > 0) { %>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>:</td>
                  <td valign="top" class="editColumnText">
                    <% if(hasFullEdit) { %>
                          <html:select property="versions" size="5" multiple="true" styleClass="editColumnText">
                          <% for(int i = 0; i < versions.size(); i++) { %>
                                <html:option value="<%= versions.get(i).getValue() %>" styleClass="editColumnText"><%= versions.get(i).getName() %></html:option>
                          <% } %>
                          </html:select>
                    <% } else { %>
                          <%
                              List<Version> issueVersions = issue.getVersions();
                              Collections.sort(issueVersions, new Version.VersionComparator());
                              for(int i = 0; i < issueVersions.size(); i++) {
                          %>
                              <%= issueVersions.get(i).getNumber() %><br/>
                          <% } %>
                    <% }  %>
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
                 List<IssueField> issueFields = issue.getFields();
                 HashMap<Integer,String> fieldValues = new HashMap<Integer,String>();
                 for(int i = 0; i < issueFields.size(); i++) {
                    fieldValues.put(issueFields.get(i).getCustomField().getId(), issueFields.get(i).getValue((java.util.Locale)pageContext.getAttribute("currLocale")));
                 }
          %>
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
                <%     }
                       String fieldValue = (String) fieldValues.get(projectFields.get(i).getId());
                %>
                       <it:formatCustomField field="<%= projectFields.get(i) %>" currentValue="<%= fieldValue %>" formName="issueForm" listOptions="<%= listOptions %>"/>
                <% } %>
                </tr>
                <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
          <% } %>
   <tr><td>
<html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit><br/><br/>
 </td></tr>
    <%-- TODO reinclude this once related issues has been implemented corretly
          <%
             IssueRelationModel[] relations = issue.getRelations();
             Arrays.sort(relations, new IssueRelationModel());
          %>
               <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
               <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>

               <% int colNumber = 0;
                  for(int i = 1; i <= IssueUtilities.NUM_RELATION_TYPES; i++) {
                    if(i == IssueUtilities.RELATION_TYPE_RELATED_C) {
                       continue;
                    }

                    boolean displayedType = false;
                    for(int j = 0; j < relations.size(); j++) {
                      int relType = relations[j].getRelationType();
                      if(relType == i || (i == IssueUtilities.RELATION_TYPE_RELATED_P && relType == IssueUtilities.RELATION_TYPE_RELATED_C)) {
                          if(! displayedType) {
                              displayedType = true;
                              if(colNumber == 0) {
               %>
                                  <tr valign="top">
               <%             } else if(colNumber % 2 == 0) { %>
                                  </tr><tr valign="top">
               <%             } %>
                              <td colspan="2" valign="top">
                                <table width="100%" border="0" cellspacing="0" cellspacing="1" >
                                  <tr>
                                    <td class="listRowTextBold" align="left" colspan="5">
                                      <%= ITrackerResources.getString(ITrackerResources.KEY_BASE_ISSUE_RELATION + i, currLocale) %>:
                                    </td>
                                  </tr>
                                  <tr>
                                    <td width="10"><html:img page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                                    <td width="15%"></td>
                                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                                    <td width="60%"></td>
                                    <td width="25%"></td>
                                  </tr>
              <%          } %>
                          <tr>
                            <td valign="top"><it:formatImageAction action="removerelation" paramName="relationId" paramValue="<%= relations[j].getId() %>" caller="editissue" src="/images/delete.gif" altKey="itracker.web.image.delete.relation.alt" textActionKey="itracker.web.image.delete.texttag"/></td>
                            <td class="listRowText" align="right" valign="top" ><it:link forward="viewissue" styleClass="listRowText" paramName="id" paramValue="<%= relations[j].getRelatedIssueId() %>"><%= relations[j].getRelatedIssueId() %></it:link></td>
                            <td></td>
                            <td class="listRowText" align="left" valign="top" ><it:formatDescription><%= relations[j].getRelatedIssueDescription() %></it:formatDescription></td>
                            <td class="listRowText" align="left" valign="top" ><%= IssueUtilities.getStatusName(relations[j].getRelatedIssueStatus(), currLocale) %></td>
                          </tr>
              <%
                      }
                   }
                   if(displayedType) {
                     colNumber++;
              %>
                     </table></td>
              <%
                   }
                 }
              %>
              <tr><td colspan="4"><html:img page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td></tr>
          --%>
          <% if(! ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions())) { %>
              <tr><td colspan="4">
                <table border="0" cellspacing="0"  cellspacing="1" width="100%">
                  <tr>
                    <td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.attachments"/>:</td>
                  </tr>
                  <%
                     List<IssueAttachment> attachments = issue.getAttachments();
                     Collections.sort(attachments, IssueAttachment.CREATE_DATE_COMPARATOR);
                     if(attachments.size() > 0) {
                  %>
                        <tr align="left" class="listHeading">
                          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                          <td align="left"><it:message key="itracker.web.attr.filename"/></td>
                          <td align="left"><it:message key="itracker.web.attr.description"/></td>
                          <td align="left"><it:message key="itracker.web.attr.filetype"/></td>
                          <td align="left"><it:message key="itracker.web.attr.filesize"/></td>
                          <td align="left"><it:message key="itracker.web.attr.submittor"/></td>
                          <td align="right"><it:message key="itracker.web.attr.lastupdated"/></td>
                        </tr>

                  <%    for(int i = 0; i < attachments.size(); i++) { %>
                          <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                            <td class="listRowText" align="left"><it:formatImageAction forward="downloadattachment" paramName="id" paramValue="<%= attachments.get(i).getId() %>" target="_blank" src="/themes/defaulttheme/images/download.png" altKey="itracker.web.image.download.attachment.alt" textActionKey="itracker.web.image.download.texttag"/></td>
                            <td></td>
                            <td class="listRowText" align="left"><%= attachments.get(i).getOriginalFileName() %></td>
                            <td class="listRowText" align="left"><it:formatDescription><%= attachments.get(i).getDescription() %></it:formatDescription></td>
                            <td class="listRowText" align="left"><%= attachments.get(i).getType() %></td>
                            <td class="listRowText" align="left"><%= attachments.get(i).getSize() / 1024 %></td>
                            <td class="listRowText" align="left"><%= attachments.get(i).getUser().getFirstName() + " " + attachments.get(i).getUser().getLastName() %></td>
                            <td class="listRowText" align="right"><it:formatDate date="<%= attachments.get(i).getLastModifiedDate() %>"/></td>
                          </tr>
                  <%
                        }
                     } else {
                  %>
                        <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                        <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
                  <% } %>
                </table>
              </td></tr>
              <tr class="listRowUnshaded">
                <td colspan="4">
                  <it:message key="itracker.web.attr.description"/> <html:text property="attachmentDescription" size="30" maxlength="60" styleClass="editColumnText"/>
                  &nbsp;&nbsp;&nbsp; <it:message key="itracker.web.attr.filename"/> <html:file property="attachment" styleClass="editColumnText"/>
                </td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td></tr>
          <% } %>
          </table>
          
      <table border="0" cellspacing="0" cellspacing="1" width="100%">
          <tr><td colspan="4">
            <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
              <tr>
                <td class="editColumnTitle" colspan="3"><it:message key="itracker.web.attr.history"/>:</td>
                <td align="right"><it:formatImageAction forward="view_issue_activity.do" paramName="id" paramValue="<%= issueId %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.activity.alt" textActionKey="itracker.web.image.view.texttag"/></td>
              </tr>
              <tr align="left" class="listHeading">
                <% if(um.isSuperUser()) { %>
                    <td width="30"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="30" height="1"/></td>
                <% } else { %>
                    <td width="15"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                <% } %>
                <td width="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3" height="1"/></td>
                <td><it:message key="itracker.web.attr.updator"/></td>
                <td align="right"><it:message key="itracker.web.attr.updated"/></td>
              </tr>

              <%
                List<IssueHistory> history = ih.getIssueHistory(issueId);
 
                Collections.sort(history, IssueHistory.CREATE_DATE_COMPARATOR);

                int i = 0;
                for(i = 0; i < history.size(); i++) {
              %>
                  <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                    <td align="right" valign="bottom" nowrap>
                      <% if(um.isSuperUser()) { %>
                            <it:formatImageAction action="removehistory" paramName="historyId" paramValue="<%= history.get(i).getId() %>" caller="editissue" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.history.alt" textActionKey="itracker.web.image.delete.texttag"/>
                      <% } %>
                      <%= i + 1 %>)
                    </td>
                    <td></td>
                    <td class="historyName">
                      <%= history.get(i).getUser().getFirstName() + " " + history.get(i).getUser().getLastName() %>
                      (<a href="mailto:<%= history.get(i).getUser().getEmail() %>" class="mailto"><%= history.get(i).getUser().getEmail() %></a>)
                    </td>
                    <td align="right" class="historyName"><it:formatDate date="<%= history.get(i).getCreateDate() %>"/></td>
                  </tr>
                  <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                    <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="3"/></td>
                  </tr>
                  <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                    <td colspan="2"></td>
                    <td colspan="3">
                      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
                        <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                          <td align="left"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                          <td align="left" width="100%">
                            <it:formatHistoryEntry><%= history.get(i).getDescription() %></it:formatHistoryEntry>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr class="listRowUnshaded">
                    <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="8"/></td>
                  </tr>
             <% } %>
                  <tr>
                    <td valign="top" align="right" class="historyName"><%= i + 1 %>)</td>
                    <td></td>
                    <%
                       String wrap = "soft";
                       if(ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, project.getOptions()) ||
                          ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, project.getOptions())) {
                          wrap = "hard";
                       }
                    %>
                    <td colspan="3" class="editColumnText"><textarea name="history" wrap="<%= wrap %>" cols="110" rows="6" class="editColumnText"><bean:write name="issueForm" property="history"/></textarea></td>
                  </tr>
            </table>
          </td></tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>

          <tr><td colspan="4">
            <table border="0" cellspacing="0" cellspacing="1" width="100%">
              <tr>
                <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.notifications"/>:</td>
              </tr>
              <tr align="left" class="listHeading">
                <td><it:message key="itracker.web.attr.name"/></td>
                <td><it:message key="itracker.web.attr.email"/></td>
                <td><it:message key="itracker.web.attr.role"/></td>
              </tr>

              <%
                List<Notification> notifications = ih.getIssueNotifications(issueId);
 			 
                Collections.sort(notifications, Notification.TYPE_COMPARATOR);
%>
<%
                for(i = 0; i < notifications.size(); i++) {
              %>
                  <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                    <td class="listRowSmall"><%= notifications.get(i).getUser().getFirstName() + " " + notifications.get(i).getUser().getLastName() %></td>
                    <td class="listRowSmall"><a href="mailto:<%= notifications.get(i).getUser().getEmail() %>" class="mailto"><%= notifications.get(i).getUser().getEmail() %></a></td>
                    <td class="listRowSmall"><%= NotificationUtilities.getRoleName(notifications.get(i).getNotificationRole()) %></td>
                  </tr>
              <% } %>
            </table>
          </td></tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
          <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
        </table>

       

      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>
