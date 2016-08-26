<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey"
	value="itracker.web.admin.listattachments.title" />
<bean:define id="pageTitleArg" value="" />

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp" />

<table border="0" cellspacing="0" cellpadding="1" width="100%" class="shadeList">
	<tr>
		<td class="editColumnTitle" colspan="7"><it:message
			key="itracker.web.attr.attachments" />:</td>
		<td align="right"><it:formatIconAction
			action="exportattachments" icon="download" iconClass="fa-2x"
			info="itracker.web.image.export.attachments.alt"
			textActionKey="itracker.web.image.export.texttag" /></td>
	</tr>
	<tr align="left" class="listHeading">
		<td width="1"></td>
		<td><html:img module="/"
			page="/themes/defaulttheme/images/blank.gif" width="4" /></td>
		<td><it:message key="itracker.web.attr.issue" /></td>
		<td><it:message key="itracker.web.attr.name" /></td>
		<td><it:message key="itracker.web.attr.description" /></td>
		<td><it:message key="itracker.web.attr.filesize" /></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td><it:message key="itracker.web.attr.lastupdated" /></td>
	</tr>
	<c:forEach items="${pto.attachments}" var="attachment" varStatus="i">
		<c:choose>
			<c:when test="i % 2 == 1">
				<tr class="listRowShaded">
			</c:when>
			<c:otherwise>
				<tr class="listRowUnshaded">
			</c:otherwise>
		</c:choose>

		<td><it:formatIconAction action="removeattachment"
			paramName="id" paramValue="${attachment.id}"
			icon="remove" styleClass="deleteButton" iconClass="fa-lg"
			info="itracker.web.image.delete.attachment.alt"
			textActionKey="itracker.web.image.delete.texttag" /></td>
		<td></td>
		<td><c:out value="${attachment.issue.id}" /></td>
		<td><c:out value="${attachment.originalFileName}" /></td>
		<td><c:out value="${attachment.description}" /></td>
		<td align="right">
                     <fmt:formatNumber value="${attachment.size / 1024}" pattern="0.##"/></td>
		<td><it:message	key="itracker.web.generic.kilobyte" /></td>
		<td><it:formatDate date="${attachment.lastModifiedDate}"></it:formatDate></td>
		</tr>
	</c:forEach>

	<c:if test="${!pto.hasAttachments}">
		<tr>
			<td colspan="8" class="listRowText" align="left"><it:message
				key="itracker.web.error.noattachments" /></td>
		</tr>
	</c:if>

	<tr>
		<td colspan="8"><html:img module="/"
			page="/themes/defaulttheme/images/blank.gif" height="3" /></td>
	</tr>
	<tr>
		<td colspan="8" class="listHeadingBackground"><html:img
			module="/" page="/themes/defaulttheme/images/blank.gif" height="2" /></td>
	</tr>
	<tr>
		<td colspan="8"><html:img module="/"
			page="/themes/defaulttheme/images/blank.gif" height="3" /></td>
	</tr>

	<tr class="listRowUnshaded">
		<td colspan="4"></td>
		<td align="right"><it:message key="itracker.web.attr.total" />:</td>
		<td align="right"><fmt:formatNumber
			value="${pto.sizeOfAllAttachments / 1024} " pattern="0.##"/></td>
		<td align="left"><it:message key="itracker.web.generic.kilobyte" /></td>
		<td></td>
	</tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp" />
</body>
</html>
