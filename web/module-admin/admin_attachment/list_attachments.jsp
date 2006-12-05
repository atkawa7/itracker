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

<bean:define id="pageTitleKey" value="itracker.web.admin.listattachments.title"/>
<bean:define id="pageTitleArg" value=""/>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.attachments"/>:</td>
    <td align="right"><it:formatImageAction action="exportattachments" src="/themes/defaulttheme/images/export.png" altKey="itracker.web.image.export.attachments.alt" textActionKey="itracker.web.image.export.texttag"/></td>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.issue"/></td>
    <td><it:message key="itracker.web.attr.name"/></td>
    <td><it:message key="itracker.web.attr.description"/></td>
    <td><it:message key="itracker.web.attr.filesize"/></td>
    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
    <td><it:message key="itracker.web.attr.lastupdated"/></td>
  </tr>
	<c:forEach items="${attachments}" var="attachment" varStatus="i">
	<!--c:forEach items="{attachments}" var="i" -->
	
        <c:choose>
            <c:when test="i % 2 == 1">
                <tr class="listRowShaded">
            </c:when>
            <c:otherwise>
                <tr class="listRowUnshaded">
            </c:otherwise>
        </c:choose>

      <td>
        <it:formatImageAction action="removeattachment" paramName="id" paramValue="${attachment.id}" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.attachment.alt" textActionKey="itracker.web.image.delete.texttag"/>
      </td>
      <td></td>
      <td><c:out value="${attachment.issue.id}"/></td>
      <td><c:out value="${attachment.originalFileName}"/></td>
      <td><c:out value="${attachment.description}"/></td>
      <td align="right"><c:out value="${attachment.size / 1024}"/><it:message key="itracker.web.generic.kilobyte"/></td>
      <td></td>
      <td><it:formatDate date="${$attachment.lastModifiedDate}"></it:formatDate></td>
    </tr>
	</c:forEach>
 
	<c:if test="${!hasAttachements}">
    <tr><td colspan="8" class="listRowText" align="left"><it:message key="itracker.web.error.noattachments"/></td></tr>
	</c:if>

    <tr><td colspan="8"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3"/></td></tr>
    <tr><td colspan="8" class="listHeadingBackground"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2"/></td></tr>
    <tr><td colspan="8"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3"/></td></tr>
 
    <tr class="listRowUnshaded">
      <td colspan="4"></td>
      <td align="right"><it:message key="itracker.web.attr.total"/>:</td>
      <td align="right"><fmt:formatNumber value="${sizeOfAllAttachements}"/></td>
      <td align="left"><it:message key="itracker.web.generic.kilobyte"/></td>
      <td></td>
    </tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
