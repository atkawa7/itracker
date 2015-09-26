<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.listprojects.title"/>
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

<table border="0" cellspacing="0"  cellpadding="1"  width="100%" class="shadeList">
  <tr>
    <td id="projects" class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.projects"/>:</td>
  </tr>
  <tr align="left" class="listHeading">
    <td width="40"></td>
    <td><html:img width="4" src="../themes/defaulttheme/images/blank.gif"/></td>
    <td align="left"><it:message key="itracker.web.attr.name"/></td>
    <td align="left"><it:message key="itracker.web.attr.openissues"/></td>
    <td align="left"><it:message key="itracker.web.attr.resolvedissues"/></td>
    <td align="left"><it:message key="itracker.web.attr.totalissues"/></td>
    <td align="right"><it:message key="itracker.web.attr.lastissueupdate"/></td>
  </tr>
  
  	<c:set var="hasProjects" value="false" />	 
  	<c:set var="totalOpenIssues" value="0" />
  	<c:set var="totalResolvedIssues" value="0" />
  	<c:set var="totalNumberProjects" value="0" />
  	<c:forEach var="project" varStatus="i" items="${ projects }" step="1" >
	
	  	<c:set var="totalOpenIssues" value="${ project.totalOpenIssues + totalOpenIssues }" />
	  	<c:set var="totalResolvedIssues" value="${ project.totalResolvedIssues + totalResolvedIssues}" />
  		<c:set var="totalNumberProjects" value="${ totalNumberProjects + 1 }" />

	    <tr id="project.${i.count}" class="${ i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded' }">
	      <td nowrap>
	        <it:formatImageAction forward="listissues" paramName="projectId" paramValue="${ project.id }" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.project.alt" arg0="${ project.name }" textActionKey="itracker.web.image.view.texttag"/>
	        <c:if test="${ project.active && project.canCreate }">
	        	<it:formatImageAction forward="createissue" paramName="projectId" paramValue="${ project.id }" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.issue.alt" arg0="${ project.name }" textActionKey="itracker.web.image.create.texttag"/>
	        </c:if>
	        <it:formatImageAction forward="searchissues" paramName="projectId" paramValue="${ project.id }" src="/themes/defaulttheme/images/search.gif" altKey="itracker.web.image.search.issue.alt" arg0="${ project.name }" textActionKey="itracker.web.image.search.texttag"/>
	      </td>
	      <td></td>
	      <td>${ project.name }</td>
	      <td align="left">${ project.totalOpenIssues }</td>
	      <td align="left">${ project.totalResolvedIssues }</td>
	      <td align="left">${ project.totalNumberIssues }</td>
	      <td align="right"><it:formatDate date="${ project.lastModifiedDate }" emptyKey="itracker.web.generic.notapplicable"/></td>
	    </tr>
	    
  		<c:set var="hasProjects" value="true" />

	</c:forEach>

	<c:choose>
		<c:when test="${ hasProjects }">
			<tr>
				<td colspan="7"><html:img height="3"
					src="../themes/defaulttheme/images/blank.gif" /></td>
			</tr>

			<tr class="listRowUnshaded listProjectsTotals">
				<td colspan="2"></td>
				<td align="right"><strong><it:message key="itracker.web.attr.total" />:&nbsp;<%-- ${ totalNumberProjects }--%></strong></td>
				<td align="left"><strong>${ totalOpenIssues }</strong></td>
				<td align="left"><strong>${ totalResolvedIssues }</strong></td>
				<td align="left"><strong>${ totalOpenIssues + totalResolvedIssues }</strong></td>
				<td></td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr align="left">
				<td colspan="7" class="listRowUnshaded" style="text-align: center;"><strong><it:message
					key="itracker.web.error.noprojects" /></strong></td>
			</tr>
		</c:otherwise>
	</c:choose>


</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
