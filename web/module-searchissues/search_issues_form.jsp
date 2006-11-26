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
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="org.itracker.web.util.*" %>
  

<%-- <it:checkLogin/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
<bean:define id="pageTitleKey" value="itracker.web.search.title"/>
<bean:define id="pageTitleArg" value=""/>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<%--
    IssueService ih = ServletContextUtils.getItrackerServices(getServletContext()).getIssueService();
    ProjectService ph = ServletContextUtils.getItrackerServices(getServletContext()).getProjectService();
--%>

<%  
        final Map<Integer, Set<PermissionType>> permissions = (Map<Integer, Set<PermissionType>>)
            session.getAttribute("permissions");
        ReportService rh = (ReportService)request.getAttribute("rh");
	UserService uh = (UserService)request.getAttribute("uh");
	User um = (User)request.getSession().getAttribute("currUser");
    Integer currUserId = um.getId();

    IssueSearchQuery query = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);
    %>
    <%   if(query == null) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else {
	    List<Report> reports = new ArrayList<Report>();
    	try { 
      		reports = rh.getAllReports();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
      Project project = null;
      List<User> possibleContributors = new ArrayList<User>();
      if(query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) {
          project = query.getProject();
          if(project == null) {
%>
              <logic:forward name="unauthorized"/>
<%
          }
          possibleContributors = uh.getUsersWithAnyProjectPermission(query.getProjectId(), new int[] { UserUtilities.PERMISSION_CREATE, UserUtilities.PERMISSION_EDIT, UserUtilities.PERMISSION_EDIT_USERS });
          Collections.sort(possibleContributors, new User.CompareByName());
      }
%>

      <html:javascript formName="searchForm"/>

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

        <html:form action="/searchissues" onsubmit="return validateSearchForm(this);">
        <% if(query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
            <input type="hidden" name="type" value="<%= IssueSearchQuery.TYPE_PROJECT %>">
            <input type="hidden" name="projects" value="<%= query.getProjectId() %>">
        <% } else { %>
            <input type="hidden" name="type" value="<%= IssueSearchQuery.TYPE_FULL %>">
        <% } %>
        <table>
        <tr>
        <td>
        <table border="0" cellspacing="0"  cellspacing="1" align="left" width="800px">
          <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <tr>
            <% if(query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
                <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.project"/>: </td>
                <td valign="top" class="editColumnText"><%= query.getProjectName() %></td>
            <% } else { %>
                <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.projects"/>: </td>
                <td valign="top" class="editColumnText">
                  <html:select property="projects" styleClass="editColumnText" size="5" multiple="true">
                  <%
                      for(int i = 0; i < query.getAvailableProjects().size(); i++) {
                  %>
                        <html:option value="<%= query.getAvailableProjects().get(i).getId().toString() %>" styleClass="editColumnText"><%= query.getAvailableProjects().get(i).getName() %></html:option>
                  <% } %>
                  </html:select>
                </td>
            <% } %>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.sortorder"/>: </td>
            <td valign="top" class="editColumnText">
              <html:select property="orderBy" styleClass="editColumnText">
                <html:option value="id" key="itracker.web.attr.id"/>
                <html:option value="proj" key="itracker.web.attr.project"/>
                <html:option value="stat" key="itracker.web.attr.status"/>
                <html:option value="sev" key="itracker.web.attr.severity"/>
                <html:option value="owner" key="itracker.web.attr.owner"/>
                <html:option value="lm" key="itracker.web.attr.lastmodified"/>
              </html:select>
            </td>
          </tr>

          <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
          <tr>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.status"/>: </td>
            <td valign="top" class="editColumnText">
              <% List<Configuration> statuses = IssueUtilities.getStatuses(); %>
              <html:select property="statuses" styleClass="editColumnText" size="5" multiple="true">
              <% for(int i = 0; i < statuses.size(); i++) { %>
                    <html:option value="<%= statuses.get(i).getValue() %>" styleClass="editColumnText"><%= IssueUtilities.getStatusName(statuses.get(i).getValue(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
              <% } %>
              </html:select>
            </td>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>: </td>
            <td valign="top" class="editColumnText">
              <% List<NameValuePair> severities = IssueUtilities.getSeverities((java.util.Locale)pageContext.getAttribute("currLocale")); %>
              <html:select property="severities" styleClass="editColumnText" size="5" multiple="true">
              <%
                 for(int i = 0; i < severities.size(); i++) {
              %>
                    <html:option value="<%= severities.get(i).getValue() %>" styleClass="editColumnText"><%= severities.get(i).getName() %></html:option>
              <% } %>
              </html:select>
            </td>
          </tr>

          <% if(query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
                <% List<Component> components = project.getComponents(); %>
                <% List<Version> versions = project.getVersions(); %>
                <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
                <tr>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.creator"/>: </td>
                  <td valign="top" class="editColumnText">
                    <html:select property="creator" styleClass="editColumnText">
                      <html:option value="-1" key="itracker.web.generic.any"/>
                      <% for(int j = 0; j < possibleContributors.size(); j++) { %>
                           <html:option value="<%= possibleContributors.get(j).getId().toString() %>"><%= possibleContributors.get(j).getFirstName() + " " + possibleContributors.get(j).getLastName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>: </td>
                  <td valign="top" class="editColumnText">
                    <html:select property="owner" styleClass="editColumnText">
                      <html:option value="-1" key="itracker.web.generic.any"/>
                      <% for(int j = 0; j < possibleContributors.size(); j++) { %>
                           <html:option value="<%= possibleContributors.get(j).getId().toString() %>"><%= possibleContributors.get(j).getFirstName() + " " + possibleContributors.get(j).getLastName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
                </tr>
                <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
                <tr>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.contributor"/>: </td>
                  <td valign="top" class="editColumnText">
                    <html:select property="contributor" styleClass="editColumnText">
                      <html:option value="-1" key="itracker.web.generic.any"/>
                      <% for(int j = 0; j < possibleContributors.size(); j++) { %>
                           <html:option value="<%= possibleContributors.get(j).getId().toString() %>"><%= possibleContributors.get(j).getFirstName() + " " + possibleContributors.get(j).getLastName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.target"/>: </td>
                  <td valign="top" class="editColumnText">
                    <html:select property="targetVersion" styleClass="editColumnText">
                      <html:option value="-1" key="itracker.web.generic.any"/>
                      <% for(int i = 0; i < versions.size(); i++) { %>
                            <html:option value="<%= versions.get(i).getId().toString() %>" styleClass="editColumnText"><%= versions.get(i).getNumber() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
                </tr>
                <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
                <tr>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>: </td>
                  <td valign="top" class="editColumnText">
                    <html:select property="components" styleClass="editColumnText" size="3" multiple="true">
                      <% for(int i = 0; i < components.size(); i++) { %>
                            <html:option value="<%= components.get(i).getId().toString() %>" styleClass="editColumnText"><%= components.get(i).getName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>: </td>
                  <td valign="top" class="editColumnText">
                    <html:select property="versions" styleClass="editColumnText" size="3" multiple="true">
                      <% for(int i = 0; i < versions.size(); i++) { %>
                            <html:option value="<%= versions.get(i).getId().toString() %>" styleClass="editColumnText"><%= versions.get(i).getNumber() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
                </tr>
          <% } %>

          <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
          <tr>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.resolution"/>: </td>
            <% if(query.getType().equals(IssueSearchQuery.TYPE_PROJECT) && ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, project.getOptions())) { %>
                  <td valign="top" class="editColumnText">
                    <html:select property="resolution" styleClass="editColumnText">
                      <option value=""></option>
                      <%
                         List<NameValuePair> possResolutions = IssueUtilities.getResolutions((java.util.Locale)pageContext.getAttribute("currLocale"));
                         for(int i = 0; i < possResolutions.size(); i++) {
                      %>
                            <html:option value="<%= possResolutions.get(i).getValue() %>"><%= possResolutions.get(i).getName() %></html:option>
                      <% } %>
                    </html:select>
                  </td>
            <% } else { %>
                  <td valign="top" class="editColumnText"><html:text property="resolution" size="20" styleClass="editColumnText"/></td>
            <% } %>
            <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.phrase"/>: </td>
            <td valign="top" class="editColumnText"><html:text property="textphrase" size="30" styleClass="editColumnText"/></td>
          </tr>
          <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
          <tr><td colspan="99" align="left"><html:submit styleClass="button" altKey="itracker.web.button.search.alt" titleKey="itracker.web.button.search.alt"><it:message key="itracker.web.button.search"/></html:submit></td></tr>
        </table>
      </html:form>
        </td></tr>
        <tr><td>
<%
      List<Issue> issues = query.getResults();
      if(issues != null) {
%>
        <br/>
        <hr width="75%" height="1" noshade/>
        <br/>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
          <tr>
            <td class="editColumnTitle" colspan="8"><it:message key="itracker.web.attr.issues"/>:</td>
            <td class="listRowText" colspan="2" align="right"><it:message key="itracker.web.generic.totalissues" arg0="<%= Integer.toString(issues.size()) %>"/></td>
          </tr>
          <tr align="left" class="listHeading">
            <td width="55"></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
            <td><it:message key="itracker.web.attr.id"/></td>
            <td><it:message key="itracker.web.attr.project"/></td>
            <td><it:message key="itracker.web.attr.status"/></td>
            <td><it:message key="itracker.web.attr.severity"/></td>
            <td><it:message key="itracker.web.attr.components"/></td>
            <td><it:message key="itracker.web.attr.description"/></td>
            <td><it:message key="itracker.web.attr.owner"/></td>
            <td><it:message key="itracker.web.attr.lastmodified"/></td>
          </tr>

          <% for(int i = 0; i < issues.size(); i++) { %>
              <tr align="right" class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ) %>">
                <td>

                  <it:formatImageAction action="viewissue"
                                        module="/module-searchissues"
                                        paramName="id"
                                        paramValue="<%= issues.get(i).getId() %>"
                                        src="/themes/defaulttheme/images/view.gif"
                                        altKey="itracker.web.image.view.issue.alt"
                                        arg0="<%= issues.get(i).getId() %>"
                                        textActionKey="itracker.web.image.view.texttag"/>

                  <% if(UserUtilities.hasPermission(permissions, issues.get(i).getProject().getId(), UserUtilities.PERMISSION_EDIT)) { %>
                        <it:formatImageAction forward="editissueform"
                                              module="/module-searchissues"
                                              paramName="id"
                                              paramValue="<%= issues.get(i).getId() %>"
                                              caller="index"
                                              src="/themes/defaulttheme/images/edit.gif"
                                              altKey="itracker.web.image.edit.issue.alt"
                                              arg0="<%= issues.get(i).getId() %>"
                                              textActionKey="itracker.web.image.edit.texttag"/>
                  <% } %>
                  <% if(! IssueUtilities.hasIssueNotification(issues.get(i), currUserId)) { %>
                        <it:formatImageAction forward="watchissue" paramName="id" paramValue="<%= issues.get(i).getId() %>" caller="index" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="<%= issues.get(i).getId() %>" textActionKey="itracker.web.image.watch.texttag"/>
                  <% } %>
                </td>
                <td></td>
                <td><%= issues.get(i).getId() %></td>
                <td><%= issues.get(i).getProject().getName() %></td>
                <td><%= IssueUtilities.getStatusName(issues.get(i).getStatus(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                <td><%= IssueUtilities.getSeverityName(issues.get(i).getSeverity(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                <td><%= (issues.get(i).getComponents().size() == 0 ? ITrackerResources.getString("itracker.web.generic.unknown", (java.util.Locale)pageContext.getAttribute("currLocale")) : issues.get(i).getComponents().get(0).getName() + (issues.get(i).getComponents().size() > 1 ? " (+)" : "")) %></td>
                <td><it:formatDescription><%= issues.get(i).getDescription() %></it:formatDescription></td>
                <td><it:formatIssueOwner issue="<%= issues.get(i) %>" format="short"/></td>
                <td><it:formatDate date="<%= issues.get(i).getLastModifiedDate() %>"/></td>
              </tr>
          <%
             }
             if(issues.size() == 0) {
          %>
              <tr class="listRowUnshaded" align="left"><td colspan="10" align="left"><it:message key="itracker.web.error.noissues"/></td></tr>
           
          <% } else { %>
              <html:form action="/displayreport" target="_blank">
                <tr><td colspan="99"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td></tr>
                <tr class="listRowUnshaded" align="left" style="vertical-align: top;">
                  <td colspan="10" align="left" style="vertical-align: top;">
                    <html:select property="reportId" styleClass="listRowUnshaded" style="vertical-align: top;">
                      <%
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
              </html:form>
          <% } %>
        </table>
        </td></tr>
        </table>
<%
      }
    }
%>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
