<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listprojects.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="5"><it:message key="itracker.web.attr.projects"/>:</td>
    <c:if test="${ isSuperUser }">
         <td align="right">
         	<c:choose>
         		<c:when test="${ showAll }">
         			<html:link module="/module-admin" action="/listprojectsadmin?showAll=false"><it:message key="itracker.web.listprojects.locked.hide"/></html:link>--%>
         			<%-- TODO: better icon for show/hide closed projects
         			<it:formatImageAction action="/listprojectsadmin?showAll=false" src="/themes/defaulttheme/images/unwatch.gif" altKey="itracker.web.listprojects.locked.hide" textActionKey="itracker.web.listprojects.locked.hide"/>--%>
         		</c:when>
         		<c:otherwise>
         			<html:link module="/module-admin" action="/listprojectsadmin?showAll=true"><it:message key="itracker.web.listprojects.locked.show"/></html:link>
         			<%-- <it:formatImageAction action="/listprojectsadmin?showAll=true" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.listprojects.locked.show" textActionKey="itracker.web.listprojects.locked.show"/>--%>
         			
         		</c:otherwise>
         	</c:choose>
         	
           <it:formatImageAction action="editprojectform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.project.alt" textActionKey="itracker.web.image.create.texttag"/>
           <it:formatImageAction forward="listattachments" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.attachments.alt" textActionKey="itracker.web.image.view.texttag"/>
         </td>
    </c:if>
  </tr>
  
  <c:choose>
  <c:when test="${not empty projects}">
  <tr style="text-align: left" class="listHeading">
    <td width="1"></td>
    <td style="white-space: nowrap"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td style="white-space: nowrap"><it:message key="itracker.web.attr.name"/></td>
    <td style="white-space: nowrap"><it:message key="itracker.web.attr.description"/></td>
    <td  style="text-align: right; white-space: nowrap"><it:message key="itracker.web.attr.created"/></td>
    <td style="text-align: right; white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
   <%-- <td align="left"><it:message key="itracker.web.attr.issues"/></td> --%>
  </tr>

	<c:forEach var="project" varStatus="i" items="${ projects }" step="1">

		<tr
			class="${ i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded' }"
			style="${ project.active ? ( project.viewable ? 'color: yellow' : ''): 'color: red' }">

			<td>
				<it:formatImageAction action="editprojectform"
					paramName="id" paramValue="${ project.id }"
					targetAction="update" src="/themes/defaulttheme/images/edit.gif"
					altKey="itracker.web.image.edit.project.alt"
					arg0="${ project.name }"
					textActionKey="itracker.web.image.edit.texttag" />
			</td>
			<td>
			</td>
			<td>${ project.name }</td>
			<td>${ project.description }</td>
			<td style="text-align: right; white-space: nowrap"><it:formatDate date="${ project.createDate }" /></td>
			<td style="text-align: right; white-space: nowrap"><it:formatDate
				date="${ project.lastModifiedDate }" /></td>
			<%-- <td align="left">${ project.totalNumberIssues }</td> --%>
		</tr>

	</c:forEach>


	<tr>
		
	</tr>
	<tr>
		<td colspan="6"><html:img height="3" src="../themes/defaulttheme/images/blank.gif" /></td>
	</tr>
	<tr><td colspan="6" class="tableNote"><it:message key="itracker.web.admin.listprojects.note"/></td></tr>

	</c:when>
	<c:otherwise>
		<tr align="left">
			<td colspan="6" class="listRowUnshaded" style="text-align: center;"><strong><it:message
				key="itracker.web.error.noprojects" /></strong></td>
		</tr>
	</c:otherwise>
	</c:choose>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
