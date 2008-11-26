<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 


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
    <td width="47%" style="vertical-align: top; white-space: nowrap">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.statuses"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <it:link action="editconfigurationform" targetAction="createstatus" titleKey="itracker.web.admin.listconfiguration.status.create.alt"><it:message key="itracker.web.admin.listconfiguration.status.create"/></it:link>
          </td>
        </tr>
        <c:forEach items="${ statuses }" var="status">
        	<tr class="listRowUnshaded">
              <td style="text-align: left; white-space: nowrap">${ it:getStatusName(status.value, pageLocale) } (${ status.value })</td>
              <td style="text-align: right; white-space: nowrap">
                <it:link action="editconfigurationform" targetAction="update" paramName="id" paramValue="${ status.id }" titleKey="itracker.web.admin.listconfiguration.status.update.alt"><it:message key="itracker.web.admin.listconfiguration.status.update"/></it:link>
                <it:link action="removeconfiguration" targetAction="delete" paramName="id" paramValue="${ status.id }" titleKey="itracker.web.admin.listconfiguration.status.delete.alt"><it:message key="itracker.web.admin.listconfiguration.status.delete"/></it:link>
              </td>
            </tr>
        </c:forEach>
      </table>
    </td>
    <td width="6%">&nbsp;</td>
    <td width="47%" style="vertical-align: top; white-space: nowrap">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle"><it:message key="itracker.web.attr.severities"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <span  style="text-align: right"><it:link action="editconfigurationform" targetAction="createseverity" titleKey="itracker.web.admin.listconfiguration.severity.create.alt"><it:message key="itracker.web.admin.listconfiguration.severity.create"/></it:link></span>
          </td>
        </tr>
        <c:forEach items="${ severities }" var="severity" varStatus="i" step="1">
        	<tr class="listRowUnshaded">
              <td style="text-align: left; white-space: nowrap">${ it:getSeverityName(severity.value, pageLocale) } (${ severity.value })</td>
              <td style="text-align: right; white-space: nowrap">
              	<c:if test="${ i.index != 0 }">
              		<it:link action="orderconfiguration" targetAction="up" paramName="id" paramValue="${ severity.id }" titleKey="itracker.web.admin.listconfiguration.severity.orderup.alt"><it:message key="itracker.web.admin.listconfiguration.severity.orderup"/></it:link>
              	</c:if>
                <c:if test="${ i.index != ((fn:length(severities)) - 1) }">
                	<it:link action="orderconfiguration" targetAction="down" paramName="id" paramValue="${ severity.id }" titleKey="itracker.web.admin.listconfiguration.severity.orderdown.alt"><it:message key="itracker.web.admin.listconfiguration.severity.orderdown"/></it:link>
                </c:if>
               
                <it:link action="editconfigurationform" targetAction="update" paramName="id" paramValue="${ severity.id }" titleKey="itracker.web.admin.listconfiguration.severity.update.alt"><it:message key="itracker.web.admin.listconfiguration.severity.update"/></it:link>
                <it:link action="removeconfiguration" targetAction="delete" paramName="id" paramValue="${ severity.id }" titleKey="itracker.web.admin.listconfiguration.severity.delete.alt"><it:message key="itracker.web.admin.listconfiguration.severity.delete"/></it:link>
              </td>
            </tr>
        </c:forEach>
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
            <it:link action="editconfigurationform" targetAction="createresolution" titleKey="itracker.web.admin.listconfiguration.resolution.create.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.create"/></it:link>
          </td>
        </tr>
        <c:forEach items="${ resolutions }" var="resolution" varStatus="i" step="1">
        	<tr class="listRowUnshaded">
              <td style="text-align: left; white-space: nowrap">${ it:getResolutionName(resolution.value, pageLocale) } (${ resolution.value })</td>
              <td style="text-align: right; white-space: nowrap">
              	<c:if test="${ i.index != 0 }">
                	<it:link action="orderconfiguration" targetAction="up" paramName="id" paramValue="${ resolution.id }" titleKey="itracker.web.admin.listconfiguration.resolution.orderup.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.orderup"/></it:link>
              	</c:if>
                <c:if test="${ i.index != ((fn:length(resolutions)) - 1) }">
                      <it:link action="orderconfiguration" targetAction="down" paramName="id" paramValue="${ resolution.id }" titleKey="itracker.web.admin.listconfiguration.resolution.orderdown.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.orderdown"/></it:link>
                </c:if>
                
                <it:link action="editconfigurationform" targetAction="update" paramName="id" paramValue="${ resolution.id }" titleKey="itracker.web.admin.listconfiguration.resolution.update.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.update"/></it:link>
                <it:link action="removeconfiguration" targetAction="delete" paramName="id" paramValue="${ resolution.id }" titleKey="itracker.web.admin.listconfiguration.resolution.delete.alt"><it:message key="itracker.web.admin.listconfiguration.resolution.delete"/></it:link>
              </td>
            </tr>
        </c:forEach>
      </table>
    </td>
    <td width="6%">&nbsp;</td>
    <td width="47%" style="vertical-align: top; white-space: nowrap">
      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td style="text-align: left; white-space: nowrap" class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>:</td>
          <td class="listRowUnshaded" style="text-align: right;">
            <span style="text-align: right; white-space: nowrap"><it:link action="editcustomfieldform" targetAction="create" titleKey="itracker.web.admin.listconfiguration.customfield.create.alt"><it:message key="itracker.web.admin.listconfiguration.customfield.create"/></it:link></span>
          </td>
        </tr>
        <c:forEach items="${ customfields }" var="customField">
        	<tr class="listRowUnshaded">
              <td style="text-align: left; white-space: nowrap">
                ${ it:getCustomFieldName(customField.id, pageLocale) }
                (<it:message key="itracker.web.attr.id"/>: ${ customField.id },
                 <it:message key="itracker.web.attr.fieldtype"/>: ${ it:getCustomFieldTypeString(customField.fieldType.code, pageLocale) }
                )
              </td>
              <td style="text-align: right; white-space: nowrap">
                <it:link action="editcustomfieldform" targetAction="update" paramName="id" paramValue="${ customField.id }" titleKey="itracker.web.admin.listconfiguration.customfield.update.alt"><it:message key="itracker.web.admin.listconfiguration.customfield.update"/></it:link>
                <it:link action="removecustomfield" targetAction="delete" paramName="id" paramValue="${ customField.id }" titleKey="itracker.web.admin.listconfiguration.customfield.delete.alt"><it:message key="itracker.web.admin.listconfiguration.customfield.delete"/></it:link>
              </td>
            </tr>
        </c:forEach>
      </table>
    </td>
  </tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
