<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.web.util.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO : move redirect logic to the Action class. 
    Report report = (Report) session.getAttribute(Constants.REPORT_KEY);
    if(report == null) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else {
      boolean isUpdate = false;
      if(report.getId().intValue() > 0) {
          isUpdate = true;
      }
%>

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

      <html:form action="/editreport" enctype="multipart/form-data">
        <html:hidden property="action"/>
        <html:hidden property="id"/>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.name"/>:</td>
            <td><html:text property="name" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= report.getCreateDate() %>"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.namekey"/>:</td>
            <td><html:text property="nameKey" styleClass="editColumnText" size="30"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= report.getLastModifiedDate() %>"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td colspan="3"><html:text property="description" styleClass="editColumnText" size="80"/></td>
          </tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.reporttype"/>:</td>
            <td>
              <html:select property="reportType" styleClass="editColumnText">
                <html:option value="<%= Integer.toString(ReportUtilities.REPORTTYPE_JFREE) %>" styleClass="editColumnText">JFreeReport</html:option>
                <html:option value="<%= Integer.toString(ReportUtilities.REPORTTYPE_JASPER) %>" styleClass="editColumnText">JasperReport</html:option>
              </html:select>
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.class"/>:</td>
            <td><html:text property="className" styleClass="editColumnText" size="50"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.datatype"/>:</td>
            <td>
              <html:select property="dataType" styleClass="editColumnText">
                <html:option value="<%= Integer.toString(ReportUtilities.DATATYPE_ISSUE) %>" styleClass="editColumnText"><it:message key="itracker.web.attr.issues"/></html:option>
                <html:option value="<%= Integer.toString(ReportUtilities.DATATYPE_PROJECT) %>" styleClass="editColumnText"><it:message key="itracker.web.attr.projects"/></html:option>
              </html:select>
            </td>
          </tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.reportdefinition"/>:</td>
            <td colspan="3" classs="editColumnText"><html:file property="fileDataFile" styleClass="editColumnText"/></td>
          </tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.reportcontent"/>:</td>
            <td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td>
            <tr>
            <td colspan="4" classs="editColumnText"><html:textarea onchange="setFileInfo();" cols="120" rows="10" property="fileData" styleClass="editColumnText"/></td>
          </tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <% if(isUpdate) { %>
            <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
          <% } else { %>
            <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
          <% } %>
        </table>
      </html:form>

      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>