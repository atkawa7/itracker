<%@ include file="/common/taglibs.jsp" %>

<%@ page import="org.itracker.model.PermissionType" %>
<%@ page import="org.itracker.model.Project" %>
<%@ page import="org.itracker.model.Report" %>
<%@ page import="org.itracker.model.util.ReportUtilities" %>
<%@ page import="org.itracker.model.util.UserUtilities" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.services.ProjectService" %>
<%@ page import="org.itracker.services.ReportService" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>
<%@ page import="java.util.*" %>

<bean:define id="pageTitleKey" value="itracker.web.listreports.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="displayReportForm"/>

<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>

<html:form action="/displayreport">
   <html:hidden property="type" value="project"/>

   <table border="0" cellspacing="0" cellspacing="1" width="100%">
      <tr>
         <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.projects"/>:</td>
      </tr>
      <tr align="left" class="listHeading">
         <td><input type="checkbox" name="checkAll" value="false" onchange="toggleChecked(this, 'projectIds')"
                    title="toggle select all"/></td>
         <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
         <td><it:message key="itracker.web.attr.name"/></td>
         <td><it:message key="itracker.web.attr.description"/></td>
         <td align="left"><it:message key="itracker.web.attr.totalissues"/></td>
         <td><it:message key="itracker.web.attr.lastissueupdate"/></td>
      </tr>

      <%
         final Map<Integer, Set<PermissionType>> permissions =
                 RequestHelper.getUserPermissions(session);

         IssueService is = (IssueService) request.getAttribute("ih");
         ProjectService ps = (ProjectService) request.getAttribute("ph");
         ReportService rs = (ReportService) request.getAttribute("rh");

         List<Project> projectsList = ps.getAllAvailableProjects();
         Collections.sort(projectsList);
         Iterator<Project> projectsIt = projectsList.iterator();
         Project currentProject;
         boolean hasProjects = false;
         boolean shade = true;
         while (projectsIt.hasNext()) {
            currentProject = projectsIt.next();
            if (!UserUtilities.hasPermission(permissions, currentProject.getId(), new PermissionType[]{PermissionType.ISSUE_VIEW_ALL, PermissionType.ISSUE_VIEW_USERS})) {
               continue;
            }
            // update the alternating of row-background
            shade = !shade;
            hasProjects = true;

            int totalIssueCount = 0;
            Date newestIssueDate = null;

            totalIssueCount = is.getTotalIssueCountByProjectId(currentProject.getId());
            newestIssueDate = (totalIssueCount == 0 ? null : is.getLatestIssueDateByProjectId(currentProject.getId()));
      %>
      <tr class="<%= (shade ? "listRowShaded" : "listRowUnshaded" ) %>">
         <td><html:multibox property="projectIds" value="<%= currentProject.getId().toString() %>"/></td>
         <td></td>
         <td><%= currentProject.getName() %>
         </td>
         <td><%= currentProject.getDescription() %>
         </td>
         <td><%= totalIssueCount %>
         </td>
         <td><it:formatDate date="<%= newestIssueDate %>" emptyKey="itracker.web.generic.notapplicable"/></td>
      </tr>
      <%
         }

         if (!hasProjects) {
      %>
      <tr>
         <td colspan="6" class="listRowUnshaded" style="text-align: center;"><it:message
                 key="itracker.web.error.noprojects"/></td>
      </tr>
      <% } else { %>
      <tr>
         <td colspan="6" align="left" style="vertical-align: top;">
            <html:select property="reportId">
               <%
                  List<Report> reports = new ArrayList<Report>();
                  try {
                     reports = rs.getAllReports();
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
                  Iterator<Report> reportsIt = reports.iterator();
                  Report currentReport;
                  while (reportsIt.hasNext()) {
                     currentReport = reportsIt.next();
                     if (currentReport.getNameKey() != null) {
               %>
               <html:option value="<%= currentReport.getId().toString() %>" key="<%= currentReport.getNameKey() %>"/>
               <% } else { %>
               <html:option value="<%= currentReport.getId().toString() %>"><%= currentReport.getName() %>
               </html:option>
               <%
                     }
                  }
               %>
               <html:option value="<%= Integer.toString(ReportUtilities.REPORT_EXPORT_XML) %>"
                            key="itracker.report.exportxml"/>
            </html:select>
            <html:select property="reportOutput">
               <html:option value="<%= ReportUtilities.REPORT_OUTPUT_PDF %>">PDF</html:option>
               <%-- TODO HTMLreports will not show images, should be embedded in downloaded file or zipped
               <html:option value="<%= ReportUtilities.REPORT_OUTPUT_HTML %>">HTML</html:option>--%>
               <html:option value="<%= ReportUtilities.REPORT_OUTPUT_XLS %>">Excel</html:option>
               <html:option value="<%= ReportUtilities.REPORT_OUTPUT_CSV %>">CSV</html:option>
            </html:select>
            <html:submit styleClass="button" altKey="itracker.web.button.run.alt"
                         titleKey="itracker.web.button.run.alt"><it:message
                    key="itracker.web.button.run"/></html:submit>
         </td>
      </tr>
      <% } %>

   </table>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
</html>
