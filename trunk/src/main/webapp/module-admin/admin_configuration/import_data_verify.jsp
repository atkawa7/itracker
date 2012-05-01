<%@ include file="/common/taglibs.jsp"%>

<%@ page import="org.itracker.web.util.*" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>

<% // TODO : move redirect logic to Action class. 
    ImportDataModel importModel = (ImportDataModel) session.getAttribute(Constants.IMPORT_DATA_KEY);
    if(importModel == null) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else {
      int[][] verifyStatistics = importModel.getImportStatistics();
%>

<bean:define id="pageTitleKey" value="itracker.web.admin.import.verify.title"/>
<bean:define id="pageTitleArg" value=""/>
      
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

      <html:javascript formName="importForm"/>

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

      <html:form action="/importdataprocess">
        <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
          <tr><td class="editColumnTitle" colspan="3"><it:message key="itracker.web.admin.import.verify.heading"/>:</td></tr>
          <tr class="listHeading">
            <td><it:message key="itracker.web.attr.type"/></td>
            <td><it:message key="itracker.web.attr.create"/></td>
            <td><it:message key="itracker.web.attr.reuse"/></td>
          </tr>
          <tr class="listRowUnshaded">
            <td class="listRowText"><it:message key="itracker.web.attr.resolutions"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_RESOLUTIONS][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_RESOLUTIONS][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>
          <tr>
            <td class="listRowText"><it:message key="itracker.web.attr.severities"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_SEVERITIES][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_SEVERITIES][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>
          <tr>
            <td class="listRowText"><it:message key="itracker.web.attr.statuses"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_STATUSES][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_STATUSES][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>
          <tr>
            <td class="listRowText"><it:message key="itracker.web.attr.customfields"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_FIELDS][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_FIELDS][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>

          <tr>
            <td class="listRowText"><it:message key="itracker.web.attr.projects"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_PROJECTS][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_PROJECTS][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>
          <tr>
            <td class="listRowText"><it:message key="itracker.web.attr.users"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_USERS][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_USERS][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>
          <tr>
            <td class="listRowText"><it:message key="itracker.web.attr.issues"/>:</td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_ISSUES][ImportExportUtilities.IMPORT_STAT_NEW] %></td>
            <td class="listRowText"><%= verifyStatistics[ImportExportUtilities.IMPORT_STAT_ISSUES][ImportExportUtilities.IMPORT_STAT_REUSED] %></td>
          </tr>
          <tr><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
          <tr>
            <td colspan="3" align="left"><html:submit styleClass="button" altKey="itracker.web.button.import.alt" titleKey="itracker.web.button.import.alt"><it:message key="itracker.web.button.import"/></html:submit></td>
          </tr>
        </table>
        <br/>
      </html:form>

      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<% } %>