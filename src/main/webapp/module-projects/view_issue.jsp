<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="java.util.*" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>
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

<% // TODO : move redirect logic to the Action class. 
final Map<Integer, Set<PermissionType>> permissions = 
    RequestHelper.getUserPermissions(session);
User um = RequestHelper.getCurrentUser(session);

  IssueService ih = (IssueService)request.getAttribute("ih");
  
  Integer issueId = null;
  Issue issue = null; 

  Integer currUserId = um.getId();

  try {
      issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
      issue = ih.getIssue(issueId);
  } catch (Exception ex) {
      issue = null;
  }

  if(issue == null) {
%>
      <it:addError key="itracker.web.error.noissue"/>
      <logic:forward name="error"/>
<%
  } else {
      Project project = issue.getProject();
      if(project.getStatus() != Status.ACTIVE && project.getStatus() != Status.VIEWABLE) {
%>
          <it:addError key="itracker.web.error.projectlocked"/>
          <logic:forward name="error"/>
<%
      } else {

          User owner = issue.getOwner();
          User creator = issue.getCreator();
          boolean canViewIssue = IssueUtilities.canViewIssue(issue, currUserId, permissions);

          if(project == null || ! canViewIssue) {
%>
              <logic:forward name="unauthorized" />
<%
          } else {
              if(issue == null) {
                  issue = new Issue();
              }
%>

<bean:define id="pageTitleKey" value="itracker.web.viewissue.title"/>
<bean:define id="pageTitleArg" value="<%= issue.getId().toString() %>"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

            <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
              <tr>
                <td width="15%"></td>
                <td width="35%"></td>
                <td width="15%"></td>
                <td width="35%"></td>
              </tr>
              <tr>
                <td class="editColumnTitle" valign="top"><it:message key="itracker.web.attr.description"/>: </td>
                <td class="editColumnText"><%= issue.getDescription() %></td>
                <td class="editColumnTitle" valign="top"><it:message key="itracker.web.attr.actions"/>: </td>
                
                <td class="editColumnText" valign="top">
                  <table style="border: none; padding: 1px; border-spacing: 0; text-align: left;">
                    <tr>
                      <td style="text-align: right; vertical-align: top;"  class="editColumnText" style="white-space: nowrap;" nowrap>

                        <it:formatImageAction forward="listissues"
                                              module=""
                                              paramName="projectId"
                                              paramValue="<%= project.getId() %>"
                                              caller="viewissue"
                                              src="/themes/defaulttheme/images/list.gif"
                                              altKey="itracker.web.image.issuelist.issue.alt"
                                              textActionKey="itracker.web.image.issuelist.texttag"/>

                        <% if(! ih.hasIssueNotification(issue.getId(), currUserId)) { %>
                             <it:formatImageAction forward="watchissue"
                                                   paramName="id"
                                                   paramValue="<%= issue.getId() %>"
                                                   caller="viewissue"
                                                   src="/themes/defaulttheme/images/watch.gif"
                                                   altKey="itracker.web.image.watch.issue.alt"
                                                   arg0="<%= issue.getId() %>"
                                                   textActionKey="itracker.web.image.watch.texttag"/>
                        <% } %>
                        <% if(IssueUtilities.canEditIssue(issue, currUserId, permissions)) { %>

                             <it:formatImageAction action="editissueform"
                                                   module="/module-projects"
                                                   paramName="id"
                                                   paramValue="<%= issue.getId() %>"
                                                   caller="viewissue"
                                                   src="/themes/defaulttheme/images/edit.gif"
                                                   altKey="itracker.web.image.edit.issue.alt"
                                                   arg0="<%= issue.getId() %>"
                                                   textActionKey="itracker.web.image.edit.texttag"/>

                             <it:formatImageAction action="moveissueform"
                                                   module="/module-projects"
                                                   paramName="id"
                                                   paramValue="<%= issue.getId() %>"
                                                   caller="viewissue"
                                                   src="/themes/defaulttheme/images/move.gif"
                                                   altKey="itracker.web.image.move.issue.alt"
                                                   arg0="<%= issue.getId() %>"
                                                   textActionKey="itracker.web.image.move.texttag"/>

                             <%-- TODO reinstate this when relate issues works correctly
                             <it:formatImageAction forward="relateissue" paramName="id" paramValue="<%= issue.getId() %>" caller="viewissue" src="/images/link.gif" altKey="itracker.web.image.link.issue.alt" textActionKey="itracker.web.image.link.texttag"/>
                             --%>
                        <% } %>
                        <% if(project.getStatus() == Status.ACTIVE && UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_CREATE)) { %>
                            <it:formatImageAction forward="createissue"
                                                  module="/module-projects"
                                                  paramName="projectId"
                                                  paramValue="<%= project.getId() %>"
                                                  src="/themes/defaulttheme/images/create.gif"
                                                  altKey="itracker.web.image.create.issue.alt"
                                                  arg0="<%= project.getName() %>"
                                                  textActionKey="itracker.web.image.create.texttag"/>
                        <% } %>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="8"/></td></tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>: </td>
                <td class="editColumnText"><%= IssueUtilities.getStatusName(issue.getStatus(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>: </td>
                <td class="editColumnText">
                  <it:formatDate date="<%= issue.getCreateDate() %>"/>
                  (<%= creator.getFirstName() + " " + creator.getLastName() %>)
                </td>
              </tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.resolution"/>: </td>
                <td class="editColumnText"><it:formatResolution projectOptions="<%= project.getOptions() %>"><%= issue.getResolution() %></it:formatResolution></td>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>: </td>
                <td class="editColumnText"><it:formatDate date="<%= issue.getLastModifiedDate() %>"/></td>
              </tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>: </td>
                <td class="editColumnText"><%= IssueUtilities.getSeverityName(issue.getSeverity(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>: </td>
                <td class="editColumnText"><%= (owner == null ? ITrackerResources.getString("itracker.web.generic.unassigned", (java.util.Locale)pageContext.getAttribute("currLocale")) : owner.getFirstName() + " " + owner.getLastName()) %></td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.project"/>: </td>
                <td class="editColumnText"><%= issue.getProject().getName() %></td>
                <% if(project.getVersions().size() > 0) { %>
                    <td class="editColumnTitle" style="white-space: nowrap;" nowrap><it:message key="itracker.web.attr.target"/>:</td>
                    <td class="editColumnText"><%= (issue.getTargetVersion() == null) ? "" : issue.getTargetVersion().getNumber() %></td>
                <% } %>
              </tr>
              <tr>
                <% if(project.getComponents().size() > 0) { %>
                      <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>: </td>
                      <td valign="top" class="editColumnText">
                        <% List<Component> components = issue.getComponents();

                            Collections.sort(components);

                            for(int i = 0; i < components.size(); i++) {
                        %>
                            <%= components.get(i).getName() %><br/>
                        <% } %>
                      </td>
                <%  } else { %>
                      <td></td>
                      <td></td>
                <%  } %>

                <% if(project.getVersions().size() > 0) { %>
                      <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>: </td>
                      <td valign="top" class="editColumnText">
                        <%  List<Version> versions = issue.getVersions();

                            Collections.sort(versions, new Version.VersionComparator());

                            for(int i = 0; i < versions.size(); i++) {
                        %>
                            <%= versions.get(i).getNumber() %><br/>
                        <% } %>
                      </td>
                <%  } else { %>
                      <td></td>
                      <td></td>
                <%  } %>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>

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
                     <tr class="listHeading"><td colspan="4"><html:img height="2" width="1" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
                     <tr><td colspan="4"><html:img height="3" width="1" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
                     <tr>
              <%
                     for(int i = 0; i < projectFields.size(); i++) {
                         if(i % 2 == 0) {
              %>
                             </tr>
                             <tr>
              <%         }
                         String fieldValue = (String) fieldValues.get(projectFields.get(i).getId());
              %>
                         <it:formatCustomField field="<%= projectFields.get(i) %>" currentValue="<%= fieldValue %>" displayType="view"/>
              <%     } %>
                     </tr>
                     <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
              <% } %>
            </table>
            <br />
            <%-- TODO reinsert this once issue relation has been implemented correctly 
            <%
               IssueRelationModel[] relations = issue.getRelations();
               Arrays.sort(relations, new IssueRelationModel());
               if(relations != null && relations.size() > 0) {
                 int colNumber = 0;

            %>
                 <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
                   <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
                   <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                   <tr style="text-align: left;">
                      <td colspan="2" width="50%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
                      <td colspan="2" width="50%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
                   </tr>

                   <% for(int i = 1; i <= IssueUtilities.NUM_RELATION_TYPES; i++) {
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
                                    <table width="100%" border="0" cellspacing="0"  cellspacing="1" >
                                      <tr>
                                        <td class="listRowTextBold" style="text-align: left;" colspan="5">
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
                                <td></td>
                                <td class="listRowText" style="text-align: right;"><it:link forward="viewissue" styleClass="listRowText" paramName="id" paramValue="<%= relations[j].getRelatedIssueId() %>"><%= relations[j].getRelatedIssueId() %></it:link></td>
                                <td></td>
                                <td class="listRowText" style="text-align: left;"><it:formatDescription><%= relations[j].getRelatedIssueDescription() %></it:formatDescription></td>
                                <td class="listRowText" style="text-align: left;"><%= IssueUtilities.getStatusName(relations[j].getRelatedIssueStatus(), currLocale) %></td>
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
                  <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
                </table>
                <br>
            <% } %>
               
                  --%>
   
        
            <% if(! ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions())) { %>
                <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
                  <tr>
                    <td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.attachments"/>:</td>
                  </tr>
                  <tr style="text-align: left;" class="listHeading">
                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.filename"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.description"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.filetype"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.filesize"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.submittor"/></td>
                    <td style="text-align: right;"><it:message key="itracker.web.attr.lastupdated"/></td>
                  </tr>

                  <%
                    List<IssueAttachment> attachments = issue.getAttachments();

                    Collections.sort(attachments, IssueAttachment.CREATE_DATE_COMPARATOR);

                    for(int i = 0; i < attachments.size(); i++) {
                  %>
                      <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                        <td class="listRowText" style="text-align: left;">
                            <it:formatImageAction forward="downloadAttachment.do"
                                                  paramName="id"
                                                  paramValue="<%= attachments.get(i).getId() %>"
                                                  target="_blank"
                                                  src="/themes/defaulttheme/images/download.png"
                                                  altKey="itracker.web.image.download.attachment.alt"
                                                  textActionKey="itracker.web.image.download.texttag"/>
                        </td>
                        <td></td>
                        <td class="listRowText" style="text-align: left;"><%= attachments.get(i).getOriginalFileName() %></td>
                        <td class="listRowText" style="text-align: left;"><it:formatDescription><%= attachments.get(i).getDescription() %></it:formatDescription></td>
                        <td class="listRowText" style="text-align: left;"><%= attachments.get(i).getType() %></td>
                        <td class="listRowText" style="text-align: left;"><%= attachments.get(i).getSize() / 1024 %></td>
                        <td class="listRowText" style="text-align: left;"><%= attachments.get(i).getUser().getFirstName() + " " + attachments.get(i).getUser().getLastName() %></td>
                        <td class="listRowText" style="text-align: right;"><it:formatDate date="<%= attachments.get(i).getLastModifiedDate() %>"/></td>
                      </tr>
                 <% } %>
                  <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
                </table>
                <br>
            <% } %>

            <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
              <tr>
                <td class="editColumnTitle" colspan="3"><it:message key="itracker.web.attr.history"/>:</td>
                <td style="text-align: right;"><it:formatImageAction forward="view_issue_activity.do" paramName="id" paramValue="<%= issueId %>" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.activity.alt" textActionKey="itracker.web.image.view.texttag"/></td>
              </tr>
              <tr style="text-align: left;" class="listHeading">
                <td width="15"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                <td width="8"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                <td><it:message key="itracker.web.attr.updator"/></td>
                <td style="text-align: right;"><it:message key="itracker.web.attr.updated"/></td>
              </tr>
              <%
                List<IssueHistory> history = issue.getHistory();

                Collections.sort(history, IssueHistory.CREATE_DATE_COMPARATOR);

                int count = 0;
                for(int i = 0; i < history.size(); i++) {
                  if(history.get(i).getStatus() == IssueUtilities.HISTORY_STATUS_AVAILABLE) {
                    count++;
              %>
                    <tr class="<%= (count % 2 == 0 ? "listRowShaded" : "listRowUnshaded") %>" >
                      <td style="text-align: right;" class="historyName"><%= i + 1 %>)</td>
                      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                      <td class="historyName">
                        <%= history.get(i).getUser().getFirstName() + " " + history.get(i).getUser().getLastName() %>
                        (<a href="mailto:<%= history.get(i).getUser().getEmail() %>" class="mailto"><%= history.get(i).getUser().getEmail() %></a>)
                      </td>
                      <td style="text-align: right;" class="historyName"><it:formatDate date="<%= history.get(i).getCreateDate() %>"/></td>
                    </tr>
                    <tr class="<%= (count % 2 == 0 ? "listRowShaded" : "listRowUnshaded") %>" >
                      <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="3"/></td>
                    </tr>
                    <tr class="<%= (count % 2 == 0 ? "listRowShaded" : "listRowUnshaded") %>" >
                      <td colspan="2"></td>
                      <td colspan="3">
                        <table style="border: none; padding: 1px; border-spacing: 0;">
                          <tr class="<%= (count % 2 == 0 ? "listRowShaded" : "listRowUnshaded") %>" >
                            <td style="text-align: left;"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                            <td style="text-align: left;" >
	                            <div style="white-space: normal; overflow: auto; width: 900px">
	                              <it:formatHistoryEntry projectOptions="<%= project.getOptions() %>"><%= history.get(i).getDescription() %></it:formatHistoryEntry>
	                            </div>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr class="listRowUnshaded">
                      <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="8"/></td>
                    </tr>
             <%
                  }
                }
             %>
            </table>

            <br/><br/>

            <table style="border: none; padding: 1px; border-spacing: 0; width: 100%;">
              <tr>
                <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.notifications"/>:</td>
              </tr>
              <tr style="text-align: left;" class="listHeading">
                <td><it:message key="itracker.web.attr.name"/></td>
                <td><it:message key="itracker.web.attr.email"/></td>
                <td><it:message key="itracker.web.attr.role"/></td>
              </tr>

              <%
                List<Notification> notifications = ih.getIssueNotifications(issueId);

                Collections.sort(notifications, Notification.USER_COMPARATOR);

                for(int i = 0; i < notifications.size(); i++) {
              %>
                  <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                    <td class="listRowSmall"><%= notifications.get(i).getUser().getFirstName() + " " + notifications.get(i).getUser().getLastName() %></td>
                    <td class="listRowSmall"><a href="mailto:<%= notifications.get(i).getUser().getEmail() %>" class="mailto"><%= notifications.get(i).getUser().getEmail() %></a></td>
                    <td class="listRowSmall"><%= NotificationUtilities.getRoleName(notifications.get(i).getNotificationRole(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                  </tr>
              <% } %>
            </table>

            <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
             
<%
          }
      }
  }
%>
