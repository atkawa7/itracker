<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.services.util.ReportUtilities" %>
<%@ page import="org.itracker.web.scheduler.*" %>
<%@ page import="org.itracker.model.PermissionType" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO: move this redirect logic to the Action. 
	final Map<Integer, Set<PermissionType>> permissions = RequestHelper.getUserPermissions(session);
	ProjectService projectService = (ProjectService)request.getAttribute("ph");

 if(! UserUtilities.hasPermission(permissions, UserUtilities.PERMISSION_USER_ADMIN)) { %>
      <logic:forward name="listprojectsadmin"/>
<% } else { %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.admin.index.title"/>
<bean:define id="pageTitleArg" value=""/>

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
 
      <table border="0" cellspacing="0"  cellspacing="1"  width="800px">
        <tr>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="15"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="15"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="15"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="15"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="15"/></td>
        </tr>

<%--
	PROJECTS
 --%>
        <tr class="listHeading">
          <td colspan="3"><it:message key="itracker.web.admin.index.projectadmin"/></td>
          <td colspan="2" align="right"><it:link action="editprojectform" targetAction="create" styleClass="editColumnText">[<it:message key="itracker.web.attr.create"/>]</it:link>
          <it:link forward="listprojectsadmin" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link></td>
        </tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalprojects"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="<%= projectService.getAllProjects().size() %>"/></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalissues"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${ih.numberIssues}"/></td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>


<%--
	USERS
 --%>
        <tr class="listHeading">
          <td colspan="3"><it:message key="itracker.web.admin.index.useradmin"/></td>
          <td colspan="2" align="right"><it:link forward="listusers" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link>
          </td>
        </tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalactive"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${numberofActiveSesssions}"/></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalusers"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${uh.numberUsers}"/></td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>


<%--
	REPORTS
 --%>
        <tr class="listHeading">
          <td colspan="3"><it:message key="itracker.web.admin.index.reportadmin"/></td>
          <td colspan="2" align="right"><it:link forward="listreportsadmin" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link></td>
        </tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalnumber"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${rh.numberReports}"/></td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

<%--
	CONFIG
 --%>
        <tr class="listHeading">
          <td colspan="3" ><it:message key="itracker.web.admin.index.configadmin"/></td>
          <td colspan="2" align="right">
             <% String exportReport = "type=all&reportOutput=XML&reportId=" + ReportUtilities.REPORT_EXPORT_XML; %> 
             <it:link action="importdata" titleKey="itracker.web.admin.index.config.import.alt" styleClass="editColumnText">[<it:message key="itracker.web.attr.import"/>]</it:link>
             <it:link action="displayreport" target="_blank" queryString="<%= exportReport %>" titleKey="itracker.web.admin.index.config.export.alt" styleClass="editColumnText">[<it:message key="itracker.web.attr.export"/>]</it:link>
             <it:link forward="listconfiguration" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link>
          </td>
        </tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.statuses"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${numberOfStatuses}"/></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.severities"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${numberOfSeverities}"/></td>
        </tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.resolutions"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${numberOfResolutions}"/></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${numberOfCustomProjectFields}"/></td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

<%--
	WORKFLOW
 --%>
        <tr class="listHeading">
          <td colspan="3" ><it:message key="itracker.web.admin.index.workflowadmin"/></td>
          <td colspan="2" align="right">
             <it:link forward="listworkflow" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link>
          </td>
        </tr>
		<tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.workflowscripts"/>: </td>
          <td class="editColumnText">
           <fmt:formatNumber value="${numberOfWorkflowScripts}"/>
           </td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

<%--
	LANGUAGE
 --%>
        <tr class="listHeading">
          <td colspan="3" ><it:message key="itracker.web.admin.index.languageadmin"/></td>
          <td colspan="2" align="right">
            <it:link action="initializelanguages" titleKey="itracker.web.admin.index.language.reinitialize.alt" styleClass="editColumnText">[<it:message key="itracker.web.attr.reinitialize"/>]</it:link>
            <it:link forward="listlanguages" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link>
          </td>
        </tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totallanguages"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${sc.numberAvailableLanguages}"/></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalkeys"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${numberDefinedKeys}"/></td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

<%--
	ATTACHMENTS
 --%>
        <tr class="listHeading">
          <td colspan="3"><it:message key="itracker.web.admin.index.attachmentadmin"/></td>
          <td colspan="2" align="right"><it:link forward="listattachments" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link></td>
        </tr>

       
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalnumber"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${allIssueAttachmentsTotalNumber}"/><!-- ih.getAllIssueAttachmentsTotalNumber() --></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totalsize"/>: </td>
          <td class="editColumnText"><fmt:formatNumber value="${allIssueAttachmentsTotalSize}" pattern="0.##"/><!-- ih.getAllIssueAttachmentsTotalSize() --> <it:message key="itracker.web.generic.kilobyte" /></td>
        </tr>
        
     
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>

<%--
	SCHEDULER
 --%>
       <%--  <tr>
          <td colspan="3" class="editColumnTitle"><it:message key="itracker.web.admin.index.scheduleradmin"/></td>
          <td colspan="2" align="right"><it:link forward="listtasks" styleClass="editColumnText">[<it:message key="itracker.web.attr.administer"/>]</it:link></td>
        </tr>
        <tr class="listHeading"><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
        <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
        <tr class="listRowUnshaded">
          <td class="editColumnTitle"><it:message key="itracker.web.attr.totaltasks"/>: </td>
          <td class="editColumnText"><%= -1/*Scheduler.getNumTasks()*/ %></td>
          <td></td>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.lastrun"/>: </td>
          <td class="editColumnText"><c:out value="${lastRun}"/></td>
        </tr>--%>
      </table>
      <br>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<% } %>
