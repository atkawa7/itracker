<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.services.IssueService" %>

<%-- <it:checkLogin/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
<bean:define id="pageTitleKey" value="itracker.web.listreports.title"/>
<bean:define id="pageTitleArg" value=""/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="displayReportForm"/>

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

<center>

<html:form action="/displayreport" target="_blank" onsubmit="return validateDisplayReportForm(this);">
<html:hidden property="type" value="project"/>

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.projects"/>:</td>
  </tr>
  <tr align="left" class="listHeading">
    <td></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.name"/></td>
    <td><it:message key="itracker.web.attr.description"/></td>
    <td align="left"><it:message key="itracker.web.attr.totalissues"/></td>
    <td><it:message key="itracker.web.attr.lastissueupdate"/></td>
  </tr>

<%
        final Map<Integer, Set<PermissionType>> permissions = (Map<Integer, Set<PermissionType>>)
            session.getAttribute("permissions");
        
	IssueService ih = (IssueService)request.getAttribute("ih");
	ProjectService ph = (ProjectService)request.getAttribute("ph");
    ReportService rh = (ReportService)request.getAttribute("rh");

    List projectsList = ph.getAllAvailableProjects();
    Project[] projects = (Project[])projectsList.toArray(new Project[projectsList.size()]);
    Arrays.sort(projects, new Project.CompareByName());

    boolean hasProjects = false;
    for(int i = 0; i < projects.length; i++) {
        if(! UserUtilities.hasPermission(permissions, projects[i].getId(), new int[] {UserUtilities.PERMISSION_VIEW_ALL, UserUtilities.PERMISSION_VIEW_USERS})) {
             continue;
        }
        hasProjects = true;

        int totalIssueCount = 0;
        Date newestIssueDate = null;

        totalIssueCount = ih.getTotalIssueCountByProjectId(projects[i].getId());
        newestIssueDate = (totalIssueCount == 0 ? null : ih.getLatestIssueDateByProjectId(projects[i].getId()));
%>
        <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
          <td><html:multibox property="projectIds" value="<%= projects[i].getId().toString() %>"/></td>
          <td></td>
          <td><%= projects[i].getName() %></td>
          <td><%= projects[i].getDescription() %></td>
          <td align="left"><%= totalIssueCount %></td>
          <td><it:formatDate date="<%= newestIssueDate %>" emptyKey="itracker.web.generic.notapplicable"/></td>
        </tr>
<%
    }

    if(! hasProjects) {
%>
        <tr><td colspan="6" class="listRowUnshaded" style="text-align: center;"><it:message key="itracker.web.error.noprojects"/></td></tr>
<%  } else { %>
        <tr><td colspan="6"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
        <tr class="listRowUnshaded" align="left" style="vertical-align: top;">
          <td colspan="6" align="left" style="vertical-align: top;">
            <html:select property="reportId" styleClass="listRowUnshaded" style="vertical-align: top;">
              <%
				    List<Report> reports = new ArrayList<Report>();
    	try { 
    	
      		reports = rh.getAllReports();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
                 for(int i = 0; i < reports.size(); i++ ) {
                    if(reports.get(i).getNameKey() != null) {
              %>
                       <html:option value="<%= reports.get(i).getId().toString() %>" key="<%= reports.get(i).getNameKey() %>"/>
              <%    } else { %>
                       <html:option value="<%= reports.get(i).getId().toString() %>"><%= reports.get(i).getName() %></html:option>
              <%
                    }
                 }
              %>
              <html:option value="<%= Integer.toString(ReportUtilities.REPORT_EXPORT_XML) %>" key="itracker.report.exportxml"/>
            </html:select>
            <html:select property="reportOutput" styleClass="listRowUnshaded" style="vertical-align: top;">
                <html:option value="<%= ReportUtilities.REPORT_OUTPUT_HTML %>">HTML</html:option>
                <html:option value="<%= ReportUtilities.REPORT_OUTPUT_PDF %>">PDF</html:option>
                <html:option value="<%= ReportUtilities.REPORT_OUTPUT_XLS %>">Excel</html:option>
                <html:option value="<%= ReportUtilities.REPORT_OUTPUT_CSV %>">CSV</html:option>
            </html:select>
            <html:submit styleClass="button" altKey="itracker.web.button.run.alt" titleKey="itracker.web.button.run.alt"><it:message key="itracker.web.button.run"/></html:submit>
          </td>
        </tr>
<%  } %>

</table>
</html:form>
</center>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
