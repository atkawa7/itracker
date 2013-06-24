<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.listissues.title"/>
<bean:define id="pageTitleArg" value="${project.name}"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

      <table style="border: none; padding: 1px; border-spacing: 0; width: 100%" class="shadeList">
        <tr>
          <td id="issues" class="editColumnTitle" colspan="14"><it:message key="itracker.web.attr.issues"/>:</td>
          <td align="right" style="white-space: nowrap">
            <c:if test="${canCreateIssue}">
                  <it:formatImageAction forward="createissue"
                                        paramName="projectId"
                                        paramValue="${project.id}"
                                        src="/themes/defaulttheme/images/create.gif"
                                        altKey="itracker.web.image.create.issue.alt"
                                        arg0="${project.name}"
                                        textActionKey="itracker.web.image.create.texttag"/>
            </c:if>
            <it:formatImageAction forward="searchissues" paramName="projectId" paramValue="${project.id}" src="/themes/defaulttheme/images/search.gif" altKey="itracker.web.image.search.issue.alt" arg0="${project.name}" textActionKey="itracker.web.image.search.texttag"/>
          </td>
        </tr>
        <tr align="left" class="listHeading">
          <td width="55"></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="listHeading" order="id"><it:message key="itracker.web.attr.id"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="listHeading" order="stat"><it:message key="itracker.web.attr.status"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="listHeading" order="sev"><it:message key="itracker.web.attr.severity"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:message key="itracker.web.attr.components"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:message key="itracker.web.attr.description"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="listHeading" order="own"><it:message key="itracker.web.attr.owner"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="listHeading" order="lm"><it:message key="itracker.web.attr.lastmodified"/></it:formatPaginationLink></td>
        </tr>
 
      	<c:forEach items="${issuePTOs}" var="issuePTO" step="1" varStatus="i"><%-- nitrox:varType="org.itracker.web.ptos.IssuePTO" --%>
           	
            <tr id="issue.${i.count}" align="right" class="${i.count % 2 == 1 ? 'listRowShaded':'listRowUnshaded'}">
            <td style="white-space: nowrap">
              <it:formatImageAction forward="viewissue" module="/module-projects" paramName="id" paramValue="${issuePTO.issue.id}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="${issuePTO.issue.id}" textActionKey="itracker.web.image.view.texttag"/>
            	<c:if test="${issuePTO.userCanEdit}">
            		    <it:formatImageAction action="editissueform"  module="/module-projects" paramName="id" paramValue="${issuePTO.issue.id}" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="${issuePTO.issue.id}" textActionKey="itracker.web.image.edit.texttag"/>
            	</c:if>
                      
            	<c:if test="${issuePTO.userHasIssueNotification}">
            	<it:formatImageAction forward="watchissue" paramName="id" paramValue="${issuePTO.issue.id}" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="${issuePTO.issue.id}" textActionKey="itracker.web.image.watch.texttag"/>
            	</c:if>
         
            </td>
            <td></td>
            <td>${issuePTO.issue.id}</td>
            <td></td>
            <td>${issuePTO.statusLocalizedString}</td>
            <td></td>
            <td>${issuePTO.severityLocalizedString}</td>
            <td></td>
            <td>${issuePTO.componentsSize}</td>
            <td></td>
            <td><it:formatDescription>${issuePTO.issue.description}</it:formatDescription></td>
            <td></td>  <td>
                <c:choose>
            		<c:when test="${issuePTO.unassigned}">
            			<c:out value="${itracker_web_generic_unassigned}"/>
            	 	</c:when>
            		<c:otherwise><%-- ${issuePTO.issue.owner.firstName}. ${issuePTO.issue.owner.lastName}--%>
            		  <it:formatIssueOwner issue="${issuePTO.issue}" format="short" />
            		</c:otherwise>
            	</c:choose>
       </td>
            <td></td>
            <td><it:formatDate date="${issuePTO.issue.lastModifiedDate}"/></td>
          </tr>
 
	
	
    
</c:forEach>

 <c:choose>
           

	<c:when test="${hasIssues}">
            <tr class="listRowUnshaded" align="left">
              <td colspan="15"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td>
            </tr>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">
                <it:message key="itracker.web.generic.totalissues" arg0="${numViewable}"/>
              </td>
            </tr>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">

				<c:if test="${preferences.numItemsOnIssueList > 0}">
                      <it:formatPaginationLink page="/list_issues.jsp" projectId="${project.id}" styleClass="headerLinks"
                                               start="${100 - preferences.numItemsOnIssueList}" order="${orderParam}">
                        <it:message key="itracker.web.generic.prevpage"/>
                      </it:formatPaginationLink>
                </c:if>
				<c:if test="${k < numViewable && preferences.numItemsOnIssueList > 0}">
                      <it:formatPaginationLink page="/list_issues.jsp" projectId="${project.id}" styleClass="headerLinks"
                                               start="${k}" order="${orderParam}">
                        <it:message key="itracker.web.generic.nextpage"/>
                      </it:formatPaginationLink>
                </c:if>

              </td>
            </tr>
            </c:when>
            <c:otherwise>
            	 <tr class="listRowUnshaded" align="left"><td colspan="15" align="left"><it:message key="itracker.web.error.noissues"/></td></tr>
            </c:otherwise>

</c:choose>

      </table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
