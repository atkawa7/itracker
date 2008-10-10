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
<%@ page import="org.apache.struts.taglib.TagUtils" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
	Logger log = Logger.getLogger(this.getClass());
	log.debug("_jspService: got request: ih: " +request.getAttribute("ih") + ", currUser: " + request.getSession().getAttribute("currUser")
			+ ", issue: " + request.getAttribute(Constants.ISSUE_KEY));
// TODO here we stuck, e.g. when validation had an error
    IssueService ih = (IssueService) request.getAttribute("ih");
    User um = (User) request.getSession().getAttribute("currUser");

    Issue currentIssue = (Issue) request.getAttribute(Constants.ISSUE_KEY);

    Map<Integer, List<NameValuePair>> listOptions =
            RequestHelper.getListOptions(session);
    
    log.debug("_jspService: listOptions: " + listOptions);
    final Map<Integer, Set<PermissionType>> permissions =
            RequestHelper.getUserPermissions(session);

    log.debug("_jspService: permissions: " + permissions);
    Project project = currentIssue.getProject();
	
    
    
    Integer currUserId = um.getId();
    boolean hasFullEdit = UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_EDIT_FULL);
    String formName = "issueForm";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.editissue.title"/>
<bean:define id="pageTitleArg" value="${issue.id}"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

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

<html:form action="/editissue" method="post" enctype="multipart/form-data">
<html:hidden property="id"/>
<html:hidden property="projectId"/>
<html:hidden property="prevStatus"/>
<html:hidden property="caller"/>

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
<tr>
    <td width="15%"></td>
    <td width="35%"></td>
    <td width="15%"></td>
    <td width="35%"></td>
</tr>
<tr>
    <td  class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
    <td>
                    <html:text size="80"
                               property="description"
                               styleClass="editColumnText"/></td>
   	<td class="editColumnTitle"><it:message key="itracker.web.attr.actions"/>: </td>
    <td class="editColumnText">

        <table style="border: none; padding: 1px; border-spacing: 0;">
            <tr>
                <td style="text-align: right; vertical-align: bottom; white-space: nowrap;" class="editColumnText" >
                    <it:formatImageAction forward="listissues"
                                          module="/module-projects"
                                          paramName="projectId"
                                          paramValue="${issue.project.id}"
                                          caller="editissue"
                                          src="/themes/defaulttheme/images/list.gif"
                                          altKey="itracker.web.image.issuelist.issue.alt"
                                          textActionKey="itracker.web.image.issuelist.texttag"/>

                    <% if (!ih.hasIssueNotification(currentIssue.getId(), currUserId)) { %>

                    <it:formatImageAction forward="watchissue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          caller="editissue"
                                          src="/themes/defaulttheme/images/watch.gif"
                                          altKey="itracker.web.image.watch.issue.alt"
                                          arg0="${issue.id}"
                                          textActionKey="itracker.web.image.watch.texttag"/>

                    <% } %>

                    <% if (UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_EDIT)) { %>

                    <it:formatImageAction action="moveissueform"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          caller="editissue"
                                          src="/themes/defaulttheme/images/move.gif"
                                          altKey="itracker.web.image.move.issue.alt"
                                          arg0="${issue.id}"
                                          textActionKey="itracker.web.image.move.texttag"/>

                        <%-- TODO re-include this once relate issue correctly works
                          <it:formatImageAction forward="relateissue" paramName="id" paramValue="<%= issue.getId() %>" caller="editissue" src="/themes/defaulttheme/images/link.gif" altKey="itracker.web.image.link.issue.alt" textActionKey="itracker.web.image.link.texttag"/>
                        --%>

                    <% } %>

                    <% if (project.getStatus() == Status.ACTIVE && UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_CREATE)) { %>

                    <it:formatImageAction forward="createissue"
                                          module="/module-projects"
                                          paramName="projectId"
                                          paramValue="${issue.project.id}"
                                          src="/themes/defaulttheme/images/create.gif"
                                          altKey="itracker.web.image.create.issue.alt"
                                          arg0="${issue.project.name}"
                                          textActionKey="itracker.web.image.create.texttag"/>

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
            if (statuses.size() > 0) {
        %>

        <html:select property="status" styleClass="editColumnText">

            <% for (int i = 0; i < statuses.size(); i++) { %>

            <html:option styleClass="editColumnText"
                         value="<%= statuses.get(i).getValue() %>"><%= statuses.get(i).getName() %>
            </html:option>

            <% } %>

        </html:select>

        <% } else { %>
		<html:hidden property="status"/>
        <%= IssueUtilities.getStatusName(currentIssue.getStatus(), (java.util.Locale) pageContext.getAttribute("currLocale")) %>

        <% } %>

    </td>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.creator"/>:</td>
    <td class="editColumnText">
        <it:formatDate date="${issue.createDate}"/>
        (${issue.creator.firstName}&nbsp;${issue.creator.lastName})
    </td>
</tr>
<tr>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.resolution"/>:</td>
    <td class="editColumnText">
        <% if (um.isSuperUser() || (hasFullEdit && (currentIssue.getStatus() >= IssueUtilities.STATUS_ASSIGNED && currentIssue.getStatus() < IssueUtilities.STATUS_CLOSED))) { %>

        <% if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, project.getOptions())) { %>

        <html:select property="resolution" styleClass="editColumnText">
            <option value=""></option>

            <%
                List<NameValuePair> possResolutions = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_RESOLUTION);
                for (int i = 0; i < possResolutions.size(); i++) {
            %>

            <html:option styleClass="editColumnText"
                         value="<%= possResolutions.get(i).getValue() %>"><%= possResolutions.get(i).getName() %>
            </html:option>

            <% } %>

        </html:select>

        <% } else { %>

        <html:text size="20" property="resolution" styleClass="editColumnText"/>

        <% } %>

        <% } else { %>

        <%= currentIssue.getResolution() %>

        <% } %>

    </td>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
    <td class="editColumnText">
        <it:formatDate date="${issue.lastModifiedDate}"/>
    </td>
</tr>
<tr>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>:</td>
    <td class="editColumnText">

        <% if (hasFullEdit) { %>

        <html:select property="severity" styleClass="editColumnText">
            <c:forEach items="${fieldSeverity}" var="severity" varStatus="status">
                <html:option value="${severity.value}"styleClass="editColumnText">${severity.name}</html:option>
            </c:forEach>
        </html:select>

        <% } else { %>

        <%= IssueUtilities.getSeverityName(currentIssue.getSeverity(), (java.util.Locale) pageContext.getAttribute("currLocale")) %>

        <% } %>

    </td>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>:</td>

    <% if (currentIssue.getStatus() >= IssueUtilities.STATUS_RESOLVED) { %>

    <td class="editColumnText">
        <%= (currentIssue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", (java.util.Locale) pageContext.getAttribute("currLocale")) : currentIssue.getOwner().getFirstName() + " " + currentIssue.getOwner().getLastName()) %>
    </td>

    <% } else { %>

    <input type="hidden"
           name="currentOwner"
           value="<%= (currentIssue.getOwner() == null ? new Integer(-1) : currentIssue.getOwner().getId()) %>">

    <%
        List<NameValuePair> possibleOwners = (List<NameValuePair>) request.getAttribute("possibleOwners");
        if (possibleOwners.size() > 0) {
    %>

    <td>
        <html:select property="ownerId" styleClass="editColumnText">
            <c:forEach items="${possibleOwners}" var="possibleOwner" varStatus="status">
                <html:option value="${possibleOwner.value}">${possibleOwner.name}</html:option>
            </c:forEach>
        </html:select>
    </td>

    <% } else { %>

    <td class="editColumnText"><%= (currentIssue.getOwner() == null ? ITrackerResources.getString("itracker.web.generic.unassigned", (java.util.Locale) pageContext.getAttribute("currLocale")) : currentIssue.getOwner().getFirstName() + " " + currentIssue.getOwner().getLastName()) %>

    </td>

    <% } %>

    <% } %>

</tr>
<tr>
    <td colspan="4">
        <html:img module="/"
                  page="/themes/defaulttheme/images/blank.gif" 
                  width="1"
                  height="12"/>
    </td>
</tr>
<tr>
    <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.project"/>:</td>
    <td valign="top" class="editColumnText">
    	<it:formatImageAction forward="listissues"
                               module="/module-projects"
		                       paramName="projectId"
		                       paramValue="${issue.project.id}"
		                       caller="editissue"
		                       src="/themes/defaulttheme/images/list.gif"
		                       altKey="itracker.web.image.issuelist.issue.alt"
		                       textActionKey="itracker.web.image.issuelist.texttag"/>&nbsp;${issue.project.name}
		</td>

    <%
        List<NameValuePair> components = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_COMPONENTS);
        List<NameValuePair> versions = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_VERSIONS);
        List<NameValuePair> targetVersion = WorkflowUtilities.getListOptions(listOptions, IssueUtilities.FIELD_TARGET_VERSION);
    %>

    <% if (targetVersion.size() > 0) { %>

    <td valign="top" class="editColumnTitle" style="white-space: nowrap;" nowrap>
        <it:message key="itracker.web.attr.target"/>:&nbsp;</td>
    <td valign="top" class="editColumnText">

        <% if (hasFullEdit) { %>

        <html:select property="targetVersion" styleClass="editColumnText">
            <html:option value="-1">&nbsp;</html:option>

            <% for (int i = 0; i < targetVersion.size(); i++) { %>

            <html:option value="<%= targetVersion.get(i).getValue() %>"
                         styleClass="editColumnText"><%= targetVersion.get(i).getName() %>
            </html:option>

            <% } %>

        </html:select>

        <% } else { %>

        <c:if test="${not empty issue.targetVersion}">
            ${issue.targetVersion.number}
        </c:if>

        <% } %>

    </td>

    <% } %>

</tr>
<tr>
    <% if (components.size() > 0) { %>
    <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>:</td>
    <td valign="top" class="editColumnText">
        <% if (hasFullEdit) { %>
        <html:select property="components" size="5" multiple="true" styleClass="editColumnText">
            <% for (int i = 0; i < components.size(); i++) { %>
            <html:option value="<%= components.get(i).getValue() %>"
                         styleClass="editColumnText"><%= components.get(i).getName() %>
            </html:option>
            <% } %>
        </html:select>
        <% } else { %>
        <%
            List<Component> issueComponents = currentIssue.getComponents();
            Collections.sort(issueComponents);
            for (int i = 0; i < issueComponents.size(); i++) {
        %>
        <%= issueComponents.get(i).getName() %><br/>
        <% } %>
        <% } %>
    </td>
    <% } else { %>
    <td></td>
    <td></td>
    <% } %>
    <% if (versions.size() > 0) { %>
    <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>:</td>
    <td valign="top" class="editColumnText">
        <% if (hasFullEdit) { %>
        <html:select property="versions" size="5" multiple="true" styleClass="editColumnText">
            <% for (int i = 0; i < versions.size(); i++) { %>
            <html:option value="<%= versions.get(i).getValue() %>"
                         styleClass="editColumnText"><%= versions.get(i).getName() %>
            </html:option>
            <% } %>
        </html:select>
        <% } else { %>
        <%
            List<Version> issueVersions = currentIssue.getVersions();
            Collections.sort(issueVersions, new Version.VersionComparator());
            for (int i = 0; i < issueVersions.size(); i++) {
        %>
        <%= issueVersions.get(i).getNumber() %><br/>
        <% } %>
        <% } %>
    </td>
    <% } else { %>
    <td></td>
    <td></td>
    <% } %>
</tr>
<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td>
</tr>

<%
    List<CustomField> projectFields = project.getCustomFields();

    if (projectFields != null && projectFields.size() > 0) {
        Collections.sort(projectFields, CustomField.ID_COMPARATOR);
        List<IssueField> issueFields = currentIssue.getFields();
        HashMap<String, String> fieldValues = new HashMap<String, String>();
        for (int j = 0; j < issueFields.size(); j++) {
            if (issueFields.get(j).getCustomField() != null && issueFields.get(j).getCustomField().getId() > 0) {
                Locale currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
                fieldValues.put(issueFields.get(j).getCustomField().getId().toString(), issueFields.get(j).getValue(currLocale));
            }
        }
%>
<tr>
    <td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>:</td>
</tr>
<tr class="listHeading">
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
</tr>
<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td>
</tr>
<tr>
    <%
        for (int i = 0; i < projectFields.size(); i++) {
            if (i % 2 == 0) {
    %>
</tr>
<tr>
    <% }
        String fieldValue = (fieldValues.get(String.valueOf(projectFields.get(i).getId())) == null ? "" : fieldValues.get(String.valueOf(projectFields.get(i).getId())));
    %>
    <td class="editColumnTitle">
        <%=CustomFieldUtilities.getCustomFieldName(projectFields.get(i).getId()) + ": "%>
    </td>
    <td align="left" class="editColumnText">
        <% if (hasFullEdit) {

            String customFieldkey = "customFields(" + projectFields.get(i).getId() + ")";
        %>
        <c:set var="customFields" value="<%=customFieldkey%>"/>
        <% if (projectFields.get(i).getFieldType() == CustomField.Type.LIST) {
            List<CustomFieldValue> options = projectFields.get(i).getOptions();
        %>
        <html:select property="<%=customFieldkey%>" styleClass="editColumnText">
            <%
                for (int l = 0; l < options.size(); l++) {
            %>
            <html:option value="<%=options.get(l).getValue()%>"><%=options.get(l).getName()%>
            </html:option>
            <% }
            %>
        </html:select>
        <% } else {
            String img = "";

            if (projectFields.get(i).getFieldType() == CustomField.Type.DATE) {
                img = "<img onmouseup=\"toggleDatePicker('cf" + projectFields.get(i).getId() + "','" + formName + ".customFields(" + projectFields.get(i).getId() + ")')\"";
                img += " id=cf" + projectFields.get(i).getId() + "Pos name=cf" + projectFields.get(i).getId() + "Pos width=19 height=19 src=\" ";
                try {
                    img += TagUtils.getInstance().computeURL(pageContext, null, null, "/images/calendar.gif", null, null, null, null, false);
                } catch (Exception murle) {
                    img += " images/calendar.gif";
                }
                img += "\" align=\"top\" border=\"0\"";
                img += "<div id=\"cf" + projectFields.get(i).getId() + "\" style=\"position:absolute;\"></div>";
            }

        %>
        <html:text property="<%=customFieldkey%>" styleClass="editColumnText"/>
        <%= img %>
        <% }
        } else {
            if (fieldValue != null) { %>
        <%=(projectFields.get(i).getFieldType() == CustomField.Type.LIST ? projectFields.get(i).getOptionNameByValue(fieldValue) : fieldValue)%>
        <% } %>
        <% } %>
    </td>
    <% } %>
</tr>
<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td>
</tr>
<% } %>
<tr>
    <td>
        <html:submit styleClass="button" altKey="itracker.web.button.update.alt"
                     titleKey="itracker.web.button.update.alt"><it:message
                key="itracker.web.button.update"/></html:submit><br/><br/>
    </td>
</tr>
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

<c:if test="${not hasNoViewAttachmentOption}">

<tr>
    <td colspan="4">
        <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
            <tr>
                <td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.attachments"/>:</td>
            </tr>

            <c:choose>
                <c:when test="${not empty issue.attachments}">

                    <tr style="text-align: left;" class="listHeading">
                        <td>
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      width="15"
                                      height="1"/>
                        </td>
                        <td>
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      width="8"
                                      height="1"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.filename"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.description"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.filetype"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.filesize"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.submittor"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.lastupdated"/>
                        </td>
                    </tr>

                    <c:forEach items="${issue.attachments}" var="attachment" varStatus="status">

                        <c:choose>
                            <c:when test="${status.count % 2 == 0}">
                                <c:set var="rowShading" value="listRowShaded"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="rowShading" value="listRowUnshaded"/>
                            </c:otherwise>
                        </c:choose>

                        <tr class="${rowShading}">
                            <td class="listRowText" style="text-align: left;" >
                                <it:formatImageAction forward="downloadAttachment.do"
                                                      paramName="id"
                                                      paramValue="${attachment.id}"
                                                      target="_blank"
                                                      src="/themes/defaulttheme/images/download.png"
                                                      altKey="itracker.web.image.download.attachment.alt"
                                                      textActionKey="itracker.web.image.download.texttag"/>
                            </td>

                            <td></td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.originalFileName}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                <it:formatDescription>${attachment.description}</it:formatDescription>
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.type}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.size / 1024}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.user.firstName}&nbsp;${attachment.user.lastName}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                <it:formatDate date="${attachment.lastModifiedDate}"/>
                            </td>
                        </tr>

                    </c:forEach>

                </c:when>

                <c:otherwise>
                    <tr class="listHeading">
                        <td colspan="4">
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      height="2"
                                      width="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      height="3"
                                      width="1"/>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>

        </table>
    </td>
</tr>
<tr class="listRowUnshaded">
    <td colspan="4">
        <it:message key="itracker.web.attr.description"/>
        <html:text property="attachmentDescription" size="30"
                   maxlength="60" styleClass="editColumnText"/>
        &nbsp;&nbsp;&nbsp; <it:message key="itracker.web.attr.filename"/>
        <html:file property="attachment"
                   styleClass="editColumnText"/>
    </td>
</tr>
<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td>
</tr>

</c:if>

</table>

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
<tr>
    <td colspan="4">
        <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
            <tr>
                <td class="editColumnTitle" colspan="3">
                    <it:message key="itracker.web.attr.history"/>:</td>
                <td style="text-align: right">
                    <it:formatImageAction forward="view_issue_activity.do"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          src="/themes/defaulttheme/images/view.gif"
                                          altKey="itracker.web.image.view.activity.alt"
                                          textActionKey="itracker.web.image.view.texttag"/>
                </td>
            </tr>
            <tr style="text-align: left" class="listHeading">

                <% if (um.isSuperUser()) { %>

                <td width="30">
                    <html:img module="/"
                              page="/themes/defaulttheme/images/blank.gif"
                              width="30"
                              height="1"/>
                </td>

                <% } else { %>

                <td width="15">
                    <html:img module="/"
                              page="/themes/defaulttheme/images/blank.gif"
                              width="15"
                              height="1"/>
                </td>

                <% } %>

                <td width="3">
                    <html:img module="/"
                              page="/themes/defaulttheme/images/blank.gif"
                              width="3"
                              height="1"/>
                </td>
                <td><it:message key="itracker.web.attr.updator"/></td>
                <td align="right"><it:message key="itracker.web.attr.updated"/></td>
            </tr>

            <c:forEach items="${issueHistory}" var="historyEntry" varStatus="status">

                <c:choose>
                    <c:when test="${status.count % 2 == 0}">
                        <c:set var="rowShading" value="listRowShaded"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rowShading" value="listRowUnshaded"/>
                    </c:otherwise>
                </c:choose>

                <tr class="${rowShading}">
                    <td align="right" valign="bottom">

                        <% if (um.isSuperUser()) { %>

                        <it:formatImageAction action="removehistory"
                                              paramName="historyId"
                                              paramValue="${historyEntry.id}"
                                              caller="editissue"
                                              src="/themes/defaulttheme/images/delete.gif"
                                              altKey="itracker.web.image.delete.history.alt"
                                              textActionKey="itracker.web.image.delete.texttag"/>

                        <% } %>

                        ${status.count})

                    </td>
                    <td></td>
                    <td class="historyName">
                        ${historyEntry.user.firstName}&nbsp;${historyEntry.user.lastName}
                        (<a href="mailto:${historyEntry.user.email}" class="mailto">${historyEntry.user.email}</a>)
                    </td>
                    <td align="right" class="historyName">
                        <it:formatDate date="${historyEntry.createDate}"/>
                    </td>
                </tr>
                <tr class="${rowShading}">
                    <td colspan="5">
                        <html:img module="/"
                                  page="/themes/defaulttheme/images/blank.gif"
                                  width="1"
                                  height="3"/>
                    </td>
                </tr>
                <tr class="${rowShading}">
                    <td colspan="2"></td>
                    <td colspan="3">
                        <table style="border: none; padding: 1px; border-spacing: 0;">
                            <tr class="${rowShading}">
                                <td align="left">
                                    <html:img module="/"
                                              page="/themes/defaulttheme/images/blank.gif"
                                              width="10"
                                              height="1"/>
                                </td>
                                <td style="text-align: left; white-space: normal;">
                                <div style="white-space: normal; overflow: auto; width: 900px">
                                    <it:formatHistoryEntry>${historyEntry.description}</it:formatHistoryEntry>

                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr class="listRowUnshaded">
                    <td colspan="5">
                        <html:img module="/"
                                  page="/themes/defaulttheme/images/blank.gif"
                                  width="1"
                                  height="8"/>
                    </td>
                </tr>

            </c:forEach>

            <tr>
                <td valign="top" align="right" class="historyName"></td>
                <td></td>
                <%
                    String wrap = "soft";
                    if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, project.getOptions()) ||
                            ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, project.getOptions())) {
                        wrap = "hard";
                    }
                %>
                <td colspan="3" class="editColumnText">
                    <textarea name="history"
                              wrap="<%= wrap %>"
                              cols="110"
                              rows="6"
                              class="editColumnText"><bean:write name="issueForm" property="history"/></textarea>
                </td>
            </tr>
        </table>
    </td>
</tr>

<tr>
    <td colspan="4">
        <html:img module="/"
                  page="/themes/defaulttheme/images/blank.gif"
                  width="1"
                  height="18"/>
    </td>
</tr>

<tr>
    <td colspan="4">
        <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
            <tr>
                <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.notifications"/>:</td>
            </tr>
            <tr align="left" class="listHeading">
                <td><it:message key="itracker.web.attr.name"/></td>
                <td><it:message key="itracker.web.attr.email"/></td>
                <td><it:message key="itracker.web.attr.role"/></td>
            </tr>

            <c:forEach items="${notifications}" var="notification" varStatus="status">

                <c:choose>
                    <c:when test="${status.count % 2 == 0}">
                        <c:set var="rowShading" value="listRowShaded"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rowShading" value="listRowUnshaded"/>
                    </c:otherwise>
                </c:choose>

                <tr class="${rowShading}">
                    <td class="listRowSmall">${notification.user.firstName}&nbsp;${notification.user.lastName}</td>
                    <td class="listRowSmall">
                        <a href="mailto:${notification.user.email}"
                           class="mailto">${notification.user.email}</a>
                    </td>
                    <td class="listRowSmall"><it:message key="itracker.notification.role.${notification.notificationRole}"></it:message></td>
                    <!--NotificationUtilities.getRoleName(notifications.get(i).getNotificationRole())-->
                </tr>

            </c:forEach>

        </table>
    </td>
</tr>

<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td>
</tr>
<tr>
    <td colspan="4" align="left">
        <html:submit styleClass="button"
                     altKey="itracker.web.button.update.alt"
                     titleKey="itracker.web.button.update.alt">
            <it:message key="itracker.web.button.update"/>
        </html:submit>
    </td>
</tr>
</table>

</html:form>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body><%@page import="org.apache.log4j.Logger"%>
</html>
