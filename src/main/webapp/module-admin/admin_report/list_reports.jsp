<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listreports.title"/>
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

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
  <tr>
    <td class="editColumnTitle" colspan="10"><it:message key="itracker.web.attr.reports"/>:</td>
    <td style="text-align: right">
      <it:formatImageAction action="editreportform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.report.alt" textActionKey="itracker.web.image.create.texttag"/>
    </td>
  </tr>
  <tr class="listHeading">
    <td></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.report"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.description"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.reporttype"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.datatype"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.lastmodified"/></td>
  </tr>
  
	<c:forEach items="${reports}" var="report" varStatus="i">
		<c:choose>
			<c:when test="${i.count % 2 == 1}">
			   <tr class="listRowShaded"> 
			</c:when>
			<c:otherwise>
			   <tr class="listRowUnshaded"> 
			</c:otherwise>
		</c:choose>
		
			<td>
				<it:formatImageAction action="editreportform" paramName="id" paramValue="${report.id}" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.report.alt" arg0="${report.name}" textActionKey="itracker.web.image.edit.texttag"/>
				<it:formatImageAction action="downloadreport" paramName="id" paramValue="${report.id}" src="/themes/defaulttheme/images/download.png" altKey="itracker.web.image.download.report.alt" arg0="${report.name}" textActionKey="itracker.web.image.download.texttag"/>
				<it:formatImageAction action="exportreport" paramName="id" paramValue="${report.id}" src="/themes/defaulttheme/images/export.png" altKey="itracker.web.image.export.report.alt" arg0="${report.name}" textActionKey="itracker.web.image.export.texttag"/>
				<it:formatImageAction action="removereport" paramName="id" paramValue="${report.id}" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.report.alt" arg0="${report.name}" textActionKey="itracker.web.image.delete.texttag"/>
			</td>
			<td></td>
			<td>${report.name}</td>
			<td></td>
			<td><it:formatDescription truncateLength="60">${report.description}</it:formatDescription></td>
			<td></td>
			<td >${report.reportTypeString}</td>
			<td></td>
			<td >${report.dataTypeString}</td>
			<td></td>
			<td style="text-align: right"><it:formatDate date="${report.lastModifiedDate}"/></td>
    
    	</tr>

	</c:forEach>
	
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
