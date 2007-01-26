<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.services.util.*" %>
 
<%@ page import="java.util.List" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<%
  ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");
  
  List<Configuration> resolutions = sc.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_RESOLUTION);
  List<Configuration>  severities = sc.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_SEVERITY);
  List<Configuration> statuses = sc.getConfigurationItemsByType(SystemConfigurationUtilities.TYPE_STATUS);
  List<CustomField> customfields = sc.getCustomFields();
%>

<bean:define id="pageTitleKey" value="itracker.web.admin.listconfiguration.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
    <td width="47%" valign="top">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.statuses"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <span align="right"><it:link action="editconfigurationform" targetAction="createstatus" titleKey="itracker.web.admin.listconfiguration.status.create.alt"><it:message key="itracker.web.admin.listconfiguration.status.create"/></it:link></span>
          </td>
        </tr>
        <% for(int i = 0; i < statuses.size(); i++) { %>
            <tr class="listRowUnshaded">
              <td align="left"><%= IssueUtilities.getStatusName(statuses.get(i).getValue(), (java.util.Locale)pageContext.getAttribute("currLocale")) %> (<%= statuses.get(i).getValue() %>)</td>
              <td align="right">
                <it:link action="editconfigurationform" targetAction="update" paramName="id" paramValue="<%= statuses.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.status.update.alt"><it:message key="itracker.web.admin.listconfiguration.status.update"/></it:link>
                <it:link action="removeconfiguration" targetAction="delete" paramName="id" paramValue="<%= statuses.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.status.delete.alt"><it:message key="itracker.web.admin.listconfiguration.status.delete"/></it:link>
              </td>
            </tr>
        <% } %>
      </table>
    </td>
    <td width="6%">&nbsp;</td>
    <td width="47%" valign="top">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.severities"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <span align="right"><it:link action="editconfigurationform" targetAction="createseverity" titleKey="itracker.web.admin.listconfiguration.severity.create.alt"><it:message key="itracker.web.admin.listconfiguration.severity.create"/></it:link></span>
          </td>
        </tr>
        <% for(int i = 0; i < severities.size(); i++) { %>
            <tr class="listRowUnshaded">
              <td align="left"><%= IssueUtilities.getSeverityName(severities.get(i).getValue(), (java.util.Locale)pageContext.getAttribute("currLocale")) %> (<%= severities.get(i).getValue() %>)</td>
              <td align="right">
                <% if(i != 0) { %>
                      <it:link action="orderconfiguration" targetAction="up" paramName="id" paramValue="<%= severities.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.severity.orderup.alt"><it:message key="itracker.web.admin.listconfiguration.severity.orderup"/></it:link>
                <% }
                   if(i != (severities.size() - 1)) {
                %>
                      <it:link action="orderconfiguration" targetAction="down" paramName="id" paramValue="<%= severities.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.severity.orderdown.alt"><it:message key="itracker.web.admin.listconfiguration.severity.orderdown"/></it:link>
                <% } %>
                <it:link action="editconfigurationform" targetAction="update" paramName="id" paramValue="<%= severities.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.severity.update.alt"><it:message key="itracker.web.admin.listconfiguration.severity.update"/></it:link>
                <it:link action="removeconfiguration" targetAction="delete" paramName="id" paramValue="<%= severities.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.severity.delete.alt"><it:message key="itracker.web.admin.listconfiguration.severity.delete"/></it:link>
              </td>
            </tr>
        <% } %>
      </table>
    </td>
  </tr>
  <tr><td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
  <tr>
    <td width="47%">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.resolutions"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <span align="right"><it:link action="editconfigurationform" targetAction="createresolution" titleKey="itracker.web.admin.listconfiguration.resolution.create.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.create"/></it:link></span>
          </td>
        </tr>
        <% for(int i = 0; i < resolutions.size(); i++) { %>
            <tr align="right" class="listRowUnshaded">
              <td align="left"><%= IssueUtilities.getResolutionName(resolutions.get(i).getValue(), (java.util.Locale)pageContext.getAttribute("currLocale")) %> (<%= resolutions.get(i).getValue() %>)</td>
              <td align="right">
                <% if(i != 0) { %>
                      <it:link action="orderconfiguration" targetAction="up" paramName="id" paramValue="<%= resolutions.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.resolution.orderup.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.orderup"/></it:link>
                <% }
                   if(i != (resolutions.size() - 1)) {
                %>
                      <it:link action="orderconfiguration" targetAction="down" paramName="id" paramValue="<%= resolutions.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.resolution.orderdown.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.orderdown"/></it:link>
                <% } %>
                <it:link action="editconfigurationform" targetAction="update" paramName="id" paramValue="<%= resolutions.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.resolution.update.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.update"/></it:link>
                <it:link action="removeconfiguration" targetAction="delete" paramName="id" paramValue="<%= resolutions.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.resolution.delete.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.delete"/></it:link>
              </td>
            </tr>
        <% } %>
      </table>
    </td>
    <td width="6%">&nbsp;</td>
    <td width="47%" valign="top">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <span align="right"><it:link action="editcustomfieldform" targetAction="create" titleKey="itracker.web.admin.listconfiguration.customfield.create.alt"><it:message key="itracker.web.admin.listconfiguration.customfield.create"/></it:link></span>
          </td>
        </tr>
        <% for(int i = 0; i < customfields.size(); i++) { %>
            <tr class="listRowUnshaded">
              <td align="left">
                <%= CustomFieldUtilities.getCustomFieldName(customfields.get(i).getId(), (java.util.Locale)pageContext.getAttribute("currLocale")) %>
                (<it:message key="itracker.web.attr.id"/>: <%= customfields.get(i).getId() %>,
                 <it:message key="itracker.web.attr.fieldtype"/>: <%= CustomFieldUtilities.getTypeString(customfields.get(i).getFieldType(), (java.util.Locale)pageContext.getAttribute("currLocale")) %>
                )
              </td>
              <td align="right">
                <it:link action="editcustomfieldform" targetAction="update" paramName="id" paramValue="<%= customfields.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.customfield.update.alt"><it:message key="itracker.web.admin.listconfiguration.customfield.update"/></it:link>
                <it:link action="removecustomfield" targetAction="delete" paramName="id" paramValue="<%= customfields.get(i).getId() %>" titleKey="itracker.web.admin.listconfiguration.customfield.delete.alt"><it:message key="itracker.web.admin.listconfiguration.customfield.delete"/></it:link>
              </td>
            </tr>
        <% } %>
      </table>
    </td>
  </tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
