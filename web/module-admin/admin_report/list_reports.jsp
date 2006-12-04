<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
 
<%-- <it: checkLogin permission="< % = UserUtilities.PERMISSION_USER_ADMIN %>"/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
<bean:define id="pageTitleKey" value="itracker.web.admin.listreports.title"/>
<bean:define id="pageTitleArg" value=""/>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
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

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="10"><it:message key="itracker.web.attr.reports"/>:</td>
    <td align="right">
      <it:formatImageAction action="editreportform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.report.alt" textActionKey="itracker.web.image.create.texttag"/>
    </td>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.report"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.description"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td align="left"><it:message key="itracker.web.attr.reporttype"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td align="left"><it:message key="itracker.web.attr.datatype"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td align="left"><it:message key="itracker.web.attr.lastmodified"/></td>
  </tr>
  
	<c:forEach items="${reports}" var="report" varStatus="i">
		<c:choose>
			<c:when test="i % 2 == 1">
			   <tr align="right" class="listRowShaded"> 
			</c:when>
			<c:otherwise>
			     <tr align="right" class="listRowUnshaded"> 
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
      <td align="left"><%-- = reportTypeString --%></td>
      <td></td>
      <td align="left"><%-- = dataTypeString --%></td>
      <td></td>
      <td align="right"><it:formatDate date="${report.lastModifiedDate}"/></td>
    </tr>

	</c:forEach>
	
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
