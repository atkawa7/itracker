<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define toScope="request" id="pageTitleKey" value="itracker.web.listissues.title"/>
<bean:define toScope="request" id="pageTitleArg" value="${project.name}"/>

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
                    <span class="HTTP_POST">
            	    <it:formatImageAction forward="watchissue" paramName="id" paramValue="${issuePTO.issue.id}" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="${issuePTO.issue.id}" textActionKey="itracker.web.image.watch.texttag" />
                    </span>
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
                        <it:message key="itracker.web.generic.unassigned"/>
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
        <c:if test="${preferences.numItemsOnIssueList > 0}">
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">

                  <bean:define id="pageCount" value="${(numViewable / preferences.numItemsOnIssueList) + (numViewable > preferences.numItemsOnIssueList && numViewable % preferences.numItemsOnIssueList > 0 ? 1:0)}"/>

                  <bean:define id="pageNr" value="${(start / preferences.numItemsOnIssueList)}"/>
                  <bean:define id="pageNr" value="${pageNr + (1 - (pageNr % 1))}"/>


				<c:if test="${pageNr > 1.0}">
                      <it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="prev"
                                               start="${start - preferences.numItemsOnIssueList}" order="${orderParam}">
                        <it:message key="itracker.web.generic.prevpage"/>
                      </it:formatPaginationLink>
                </c:if>
				<c:if test="${pageNr < pageCount}">
                      <it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="next"
                                               start="${start + preferences.numItemsOnIssueList}" order="${orderParam}">
                        <it:message key="itracker.web.generic.nextpage"/>
                      </it:formatPaginationLink>
                </c:if>

          <div class="paging">
              (<fmt:formatNumber value="${pageNr}" maxFractionDigits="0"
                                    />/<fmt:formatNumber
                                    value="${pageCount}" maxFractionDigits="0" />)
          </div>

              </td>
            </tr>
                  </c:if>
            </c:when>
            <c:otherwise>
            	 <tr class="listRowUnshaded" align="left"><td colspan="15" align="left"><it:message key="itracker.web.error.noissues"/></td></tr>
            </c:otherwise>

</c:choose>

      </table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
