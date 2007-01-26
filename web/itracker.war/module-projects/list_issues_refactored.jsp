<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.services.util.ProjectUtilities" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
final Map<Integer, Set<PermissionType>> permissions = 
    RequestHelper.getUserPermissions(session);
        
    //UserPreferencesModel userPrefs = (UserPreferencesModel) session.getAttribute("preferences");
    Project project = (Project)request.getAttribute("project");
    //String orderParam = (String)request.getAttribute("orderParam");
    //Integer numViewable = (Integer)request.getAttribute("numViewable");
    //Integer k = (Integer)request.getAttribute("k");
    %>
 <%-- TODO : move redirect logic to the Action class. --%>
<c:choose>
	<c:when test="${project == null}">
	    <it:addError key="itracker.web.error.invalidproject"/>
      <logic:forward name="error"/>
	</c:when>
	<c:when test="${project.status != ProjectUtilities_STATUS_ACTIVE && project.status != ProjectUtilities_STATUS_VIEWABLE}">
	<it:addError key="itracker.web.error.projectlocked"/>
      <logic:forward name="error"/>
	</c:when>
	<c:otherwise>
            
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.listissues.title"/>
<bean:define id="pageTitleArg" value="<%= project.getName() %>"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

      <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
          <td class="editColumnTitle" colspan="14"><it:message key="itracker.web.attr.issues"/>:</td>
          <td align="right" style="white-space: nowrap">
            <% if(project.getStatus() == ProjectUtilities.STATUS_ACTIVE && UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_EDIT)) { %>
                  <it:formatImageAction forward="createissue"
                                        paramName="projectId"
                                        paramValue="<%= project.getId() %>"
                                        src="/themes/defaulttheme/images/create.gif"
                                        altKey="itracker.web.image.create.issue.alt"
                                        arg0="<%= project.getName() %>"
                                        textActionKey="itracker.web.image.create.texttag"/>
            <% } %>
            <it:formatImageAction forward="searchissues" paramName="projectId" paramValue="<%= project.getId() %>" src="/themes/defaulttheme/images/search.gif" altKey="itracker.web.image.search.issue.alt" arg0="<%= project.getName() %>" textActionKey="itracker.web.image.search.texttag"/>
          </td>
        </tr>
        <tr align="left" class="listHeading">
          <td width="55"></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="<%= project.getId() %>" styleClass="listHeading" order="id"><it:message key="itracker.web.attr.id"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="<%= project.getId() %>" styleClass="listHeading" order="stat"><it:message key="itracker.web.attr.status"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="<%= project.getId() %>" styleClass="listHeading" order="sev"><it:message key="itracker.web.attr.severity"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:message key="itracker.web.attr.components"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:message key="itracker.web.attr.description"/></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="<%= project.getId() %>" styleClass="listHeading" order="own"><it:message key="itracker.web.attr.owner"/></it:formatPaginationLink></td>
          <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
          <td><it:formatPaginationLink page="/list_issues.do" projectId="<%= project.getId() %>" styleClass="listHeading" order="lm"><it:message key="itracker.web.attr.lastmodified"/></it:formatPaginationLink></td>
        </tr>
 
      	<c:forEach items="${issuePTOs}" var="issuePTO" step="1" varStatus="i"><%-- nitrox:varType="org.itracker.web.ptos.IssuePTO" --%>
           	<c:choose>
            		<c:when test="${i.count % 2 == 1}">  <tr align="right" class="listRowShaded">
            		</c:when>
            		<c:otherwise>  <tr align="right" class="listRowUnshaded">
            		</c:otherwise>
            	</c:choose>
            
            <td style="white-space: nowrap">
              <it:formatImageAction forward="viewissue" paramName="id" paramValue="${issuePTO.issue.id}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="${issuePTO.issue.id}" textActionKey="itracker.web.image.view.texttag"/>
            	<c:if test="${issuePTO.userCanEdit}">
            		    <it:formatImageAction action="editissueform" paramName="id" paramValue="${issuePTO.issue.id}" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="${issuePTO.issue.id}" textActionKey="itracker.web.image.edit.texttag"/>
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
            		<c:otherwise>${issuePTO.issue.owner.firstName}. ${issuePTO.issue.owner.lastName}
            		  <%-- it: formatIssueOwner issue="${issuePTOs.owner.}" format="short" / --%>
            		</c:otherwise>
            	</c:choose>
       </td>
            <td></td>
            <td><it:formatDate date="${issuePTO.issue.lastModifiedDate}"/></td>
          </tr>
 
	
	
    
</c:forEach>

  <%-- 
  
  if(! hasIssues) {
%>
            <tr class="listRowUnshaded" align="left"><td colspan="15" align="left"><it:message key="itracker.web.error.noissues"/></td></tr>
<%      } else { %>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td>
            </tr>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">
                <it:message key="itracker.web.generic.totalissues" arg0="<%= Integer.toString(numViewable) %>"/>
              </td>
            </tr>
            <tr class="listRowUnshaded" align="left">
              <td colspan="15" align="left">
<%               if(k > 0 && userPrefs.getNumItemsOnIssueList() > 0) { %>
                      <it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="headerLinks"
                                               start="<%= (k - userPrefs.getNumItemsOnIssueList()) %>" order="<%= orderParam %>">
                        <it:message key="itracker.web.generic.prevpage"/>
                      </it:formatPaginationLink>
<%                } %>
<%                if((j + k) < numViewable && userPrefs.getNumItemsOnIssueList() > 0) { %>
                      <it:formatPaginationLink page="/list_issues.jsp" projectId="<%= project.getId() %>" styleClass="headerLinks"
                                               start="<%= (j + k) %>" order="<%= orderParam %>">
                        <it:message key="itracker.web.generic.nextpage"/>
                      </it:formatPaginationLink>
<%                } %>
              </td>
            </tr>
<%      } --%>

      </table>



 	</c:otherwise>
</c:choose>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
