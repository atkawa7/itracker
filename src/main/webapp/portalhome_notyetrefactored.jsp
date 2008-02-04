<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.index.title"/>
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

<!-- assigned issues -->

<table border="0" cellspacing="0" cellpadding="1" width="100%">
<c:if test="${! UserUtilities_PREF_HIDE_ASSIGNED}">     
<tr>
    <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.assigned"/>:</td>
</tr>
<tr align="left" class="listHeading">
    <td style="width:50px;" ></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td><it:message key="itracker.web.attr.id"/></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td><it:message key="itracker.web.attr.project"/></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td><it:message key="itracker.web.attr.status"/></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td><it:message key="itracker.web.attr.severity"/></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td ><it:message key="itracker.web.attr.description"/></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td><it:message key="itracker.web.attr.owner"/></td>
    <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
    <td style="text-align:right;" ><it:message key="itracker.web.attr.lastmodified"/></td>
</tr>

<c:forEach items="${ownedIssues}" var="ownedIssues" step="1" varStatus="i">
    
    <tr class="listRowUnshaded">
        <c:if test="${(userPrefs.numItemsOnIndex > 0) && (ownedIssues >=userPrefs.numItemsOnIndex) && ! showAll}">
            <td style="text-align:left;" colspan="15"><html:link module="/" action="/portalhome" paramId="showAll" ><it:message key="itracker.web.index.moreissues"/></html:link></td>
        </c:if>
    </tr>
    
    <c:choose>
        <c:when test="${i.count % 2 == 1}">
            <tr style="text-align:left;" class="listRowShaded">
        </c:when>
        <c:otherwise>
            <tr style="text-align:left;" class="listRowUnshaded">	
        </c:otherwise>
    </c:choose>
    
    <td style="white-space: nowrap">
        <it:formatImageAction forward="viewissue" paramName="id" paramValue="${ownedIssues.issue.id}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="${ownedIssues.issue.id}" textActionKey="itracker.web.image.view.texttag"/>
        
        <c:if test="${ownedIssues.userCanEdit}">
            <it:formatImageAction action="/module-projects/editissueform" paramName="id" paramValue="${ownedIssues.issue.id}" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="${ownedIssues.issue.id}" textActionKey="itracker.web.image.edit.texttag"/>
        </c:if>
    </td>
    <td></td>
    <td align="left">${ownedIssues.issue.id}</td>
    <td></td>
    <td style="white-space: nowrap"><c:out value="${ownedIssues.issue.project.name}"/></td>
    <td></td>
    <td nowrap="nowrap">${ownedIssues.statusLocalizedString}</td>
    <td></td>
    <td>${ownedIssues.severityLocalizedString}</td>
    <td></td>
    <td><it:formatDescription><c:out value="${ownedIssues.issue.description}"/></it:formatDescription></td>
    <td></td>
    <td nowrap>${ownedIssues.issue.owner.firstName} ${ownedIssues.issue.owner.lastName}</td>
    <td></td>
    <td align="right" style="white-space: nowrap"><it:formatDate date="${ownedIssues.issue.lastModifiedDate}"/></td>
    </tr>
</c:forEach>
<tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>	
</c:if>


<!-- unassigned issues -->


<c:if test="${! UserUtilities_PREF_HIDE_UNASSIGNED}">      
    
    <tr>
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.unassigned"/>:</td>
    </tr>
    <tr align="left" class="listHeading">
        <td></td>
        <td></td>
        <td><it:message key="itracker.web.attr.id"/></td>
        <td></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.status"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.severity"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.description"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.owner"/></td>
        <td></td>
        <td style="text-align:left; white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
    </tr>
    
    <c:forEach items="${unassignedIssues}" var="unassignedIssues" step="1" varStatus="i">    
        
        <c:if test="${unassignedIssues.userCanViewIssue}">  
            
            <c:if test="${userPrefs.numItemsOnIndex > 0 && i >= userPrefs.numItemsOnIndex && ! showAll}">
                <tr class="listRowUnshaded"><td align="left" colspan="15"><html:link page="/index.jsp?showAll=true"><it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
            </c:if>
            
            <c:choose>
                <c:when test="${i.count % 2 == 1}">
                    <tr style="text-align:left;" class="listRowShaded">

                </c:when>
                <c:otherwise>
                    <tr style="text-align:left;" class="listRowUnshaded">	

                </c:otherwise>
            </c:choose>
            
            <td style="white-space: nowrap">
                <it:formatImageAction forward="viewissue" paramName="id" paramValue="${unassignedIssues.issue.id}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="${unassignedIssues.issue.id}" textActionKey="itracker.web.image.view.texttag"/>
                <c:if test="${unassignedIssues.userCanEdit}">
                    <it:formatImageAction action="/module-projects/editissueform" paramName="id" paramValue="${unassignedIssues.issue.id}" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="${unassignedIssues.issue.id}" textActionKey="itracker.web.image.edit.texttag"/>
                </c:if>
                <c:if test="${unassignedIssues.userHasIssueNotification}">
                    <it:formatImageAction forward="watchissue" paramName="id" paramValue="${unassignedIssues.issue.id}" caller="index" src="/themes/defaulttheme/images/watch.gif" altKey="itracker.web.image.watch.issue.alt" arg0="${unassignedIssues.issue.id}" textActionKey="itracker.web.image.watch.texttag"/>
                </c:if>
            </td>
            <td></td>
            <td style="text-align:left;" >${unassignedIssues.issue.id}</td>
            <td></td>
            <td style="white-space: nowrap">${unassignedIssues.issue.project.name}</td>
            <td></td>
            <td><c:out value="${unassignedIssues.statusLocalizedString}"/></td>
            <td></td>
            <td><c:out value="${unassignedIssues.severityLocalizedString}"/></td>
            <td></td>
            <td><it:formatDescription>${unassignedIssues.issue.description}</it:formatDescription></td>
            <td></td>
<!-- Marky:  modified the code to place the two checks in the chooser statement so only one select list will
         be displayed. -->
            <!--c:if test="$ {unassignedIssues.userHasPermission_PERMISSION_ASSIGN_OTHERS}" -->  
            <c:choose>
                <c:when test="${unassignedIssues.userHasPermission_PERMISSION_ASSIGN_OTHERS}"> 
                    
                    <html:form action="/assignissue">
                        <html:hidden property="issueId" value="${unassignedIssues.issue.id}"/>
                        <html:hidden property="projectId" value="${unassignedIssues.issue.project.id}"/>
                        <%! String styleClass1 = "(i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\")"; %>
                        <td><html:select property="userId" styleClass="<%=styleClass1 %>" onchange="this.form.submit();">
<!-- Marky:  I commented out the original <C : tags and replaced them with my <C : tages. 
I change code to test for unassigned attribute instead of owner, since owner is not set.-->
                                <c:choose>
                                   <c:when test="${unassignedIssues.unassigned}">
                                   <!-- c:when test="$ {unassignedIssues.issue.owner == null}" -->
                                        <option value="-1"><c:out value="${itracker_web_generic_unassigned}"/></option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${unassignedIssues.issue.owner.id}"><c:out value="${unassignedIssues.issue.owner.firstName}"/> <c:out value="${unassignedIssues.issue.owner.lastName}"/></option> 
                                    </c:otherwise>
                                </c:choose>
                                <c:forEach items="${possibleIssueOwnersMap[i.index]}" var="possibleIssueOwner" varStatus="k">
                                    <c:if test="${possibleIssueOwner.lastName != null}">
                                        <option value="${possibleIssueOwner.id}" 
                                                <c:choose>
                                                    <c:when test="${unassignedIssues.issue.owner.id == possibleIssueOwner.id}">
                                                        selected
                                                    </c:when>
                                                    <c:otherwise> 
                                                    </c:otherwise>
                                                </c:choose>>${possibleIssueOwner.firstName} ${possibleIssueOwner.lastName}
                                        </option>
                                    </c:if>
                                </c:forEach>
                        </html:select></td>
                    </html:form>
                    <!-- /c:if --><!--End of  unassignedIssues.userHasPermission_PERMISSION_ASSIGN_OTHERS-->  
                </c:when>
                <c:otherwise>
                    <c:choose>
                       <c:when test="${unassignedIssues.userHasPermission_PERMISSION_ASSIGN_SELF}"> 
                            <html:form action="/assignissue">
                            <html:hidden property="issueId" value="${unassignedIssues.issue.id}"/>
                            <html:hidden property="projectId" value="${unassignedIssues.issue.project.id}"/>
                        
                            <%! String styleClass2="(i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\")"; %>
                            <td><html:select property="userId" styleClass="<%=styleClass2 %>" onchange="this.form.submit();">
                                    <c:choose>
                                        <c:when test="${unassignedIssues.unassigned}">
                                            <option value="-1"><c:out value="${itracker_web_generic_unassigned}"/></option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${unassignedIssues.issue.owner.id}"><c:out value="${unassignedIssues.issue.owner.firstName}"/> <c:out value="${unassignedIssues.issue.owner.lastName}"/>Test2</option>
                                        </c:otherwise>
                                    </c:choose>
                                   <option value="${currUser.id}" <c:if test="${unassignedIssues.issue.id==currUser.id}">selected</c:if>> 
                                   ${currUser.firstName} ${currUser.lastName}</option>			
                                
                                </html:select></td>
                            </html:form>
                        </c:when>
                        <c:otherwise>
                            <td><it:formatIssueOwner issue="${unassignedIssues.issue}" format="short"/></td>
                        </c:otherwise>
                    </c:choose>  
                </c:otherwise>
            </c:choose>  
            <td></td>
            <td style="text-align:right; white-space: nowrap"><it:formatDate date="${unassignedIssues.issue.lastModifiedDate}"/></td>
            </tr>
        </c:if>   
    </c:forEach>
    <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>  


<!-- created issues -->

<c:if test="${! UserUtilities_PREF_HIDE_CREATED}"> 
    
    <tr>
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.created"/>:</td>
    </tr>
    <tr style="text-align:left;"  class="listHeading">
        <td></td>
        <td></td>
        <td><it:message key="itracker.web.attr.id"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.project"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.status"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.severity"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.description"/></td>
        <td></td>
        <td><it:message key="itracker.web.attr.owner"/></td>
        <td></td>
        <td style="text-align:right; white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
    </tr>
    
    <c:forEach items="${createdIssues}" var="createdIssues" step="1" varStatus="i"> 

        <%--	<c:if test="${(userPrefs.numItemsOnIndex > 0) && (createdIssues >= userPrefs.numItemsOnIndex) && ! showAll}">
	   <tr class="listRowUnshaded"><td align="left" colspan="15"><html:link page="/index.jsp?showAll=true">
	   <it:message key="itracker.web.index.moreissues"/></html:link></td></tr>
	  
          break; this meant that we break out of the for each loop... but that's not possible in JSTL, I guess. So let's not break out for now. 
 
	</c:if>  --%>
	 
   
        
        <c:choose>
	        <c:when test="${z.count % 2 == 1}">
	            <tr style="text-align:left;" class="listRowShaded">
	        </c:when>
	        <c:otherwise>
	            <tr style="text-align:left;" class="listRowUnshaded">	
	        </c:otherwise>
        </c:choose>  
        
        
            <td style="white-space: nowrap">
                <it:formatImageAction forward="viewissue" paramName="id" paramValue="${createdIssues.issue.id}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="${createdIssues.issue.id}" textActionKey="itracker.web.image.view.texttag"/>
                <c:if test="${createdIssues.userCanEdit}">
                    <it:formatImageAction action="/module-projects/editissueform" paramName="id" paramValue="${createdIssues.issue.id}" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="${createdIssues.issue.id}" textActionKey="itracker.web.image.edit.texttag"/>
                </c:if>
            </td>
            <td></td>
            <td style="text-align:left;">${createdIssues.issue.id}</td>
            <td></td>
            <td style="white-space: nowrap">${createdIssues.issue.project.name}</td>
            <td></td>
            <td>${createdIssues.statusLocalizedString}</td>
            <td></td>
            <td>${createdIssues.severityLocalizedString}</td>
            <td></td>
            <td><it:formatDescription>${createdIssues.issue.description}</it:formatDescription></td>
            <td></td>
            <td style="white-space: nowrap">
                <c:choose>
                    <c:when test="${createdIssues.unassigned}">
                        unassigned
                    </c:when>
                    <c:otherwise>
                        ${createdIssues.issue.owner.firstName} ${createdIssues.issue.owner.lastName}
                    </c:otherwise>
                </c:choose>
            </td>
            <td></td>
            <td style="text-align: right; white-space: nowrap"><it:formatDate date="${createdIssues.issue.lastModifiedDate}"/></td>
        </tr>
    </c:forEach>
    <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>



<!-- watched issues -->
 


<%--
   // I could make this all the issues that have changed since the last login.  Wonder if that would be
   // better than the watches? No then you lose them.
--%>


<c:if test="${! UserUtilities_PREF_HIDE_WATCHED}"> 

<tr>
    <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.watched"/>: </td>
</tr>
<tr style="text-align:left;"  class="listHeading">
    <td></td>
    <td></td>
    <td><it:message key="itracker.web.attr.id"/></td>
    <td></td>
    <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
    <td></td>
    <td><it:message key="itracker.web.attr.status"/></td>
    <td></td>
    <td><it:message key="itracker.web.attr.severity"/></td>
    <td></td>
    <td><it:message key="itracker.web.attr.description"/></td>
    <td></td>
    <td><it:message key="itracker.web.attr.owner"/></td>
    <td></td>
    <td style="text-align:right; white-space: nowrap"><it:message key="itracker.web.attr.lastmodified"/></td>
</tr>

<c:forEach items="${watchedIssues}" var="watchedIssues" step="1" varStatus="z">
    
    <c:if test="${(userPrefs.numItemsOnIndex > 0) && (watchedIssues >=userPrefs.numItemsOnIndex) && ! showAll}">
		<tr class="listRowUnshaded">
			<td align="left" colspan="15">
			    <html:link
				page="/index.jsp?showAll=true">
					<it:message key="itracker.web.index.moreissues" />
				</html:link>
			</td>
		</tr>
	</c:if> 
    
    
    <c:choose><c:when test="${z.count % 2 == 1}">
            <tr style="text-align:left;" class="listRowShaded">
        </c:when>
        <c:otherwise>
            <tr style="text-align:left;" class="listRowUnshaded">	
        </c:otherwise>
    </c:choose>
    <td style="white-space: nowrap">
        <it:formatImageAction forward="viewissue" paramName="id" paramValue="${watchedIssues.issue.id}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.issue.alt" arg0="${watchedIssues.issue.id}" textActionKey="itracker.web.image.view.texttag"/>
        <it:formatImageAction forward="watchissue" paramName="id" paramValue="${watchedIssues.issue.id}" caller="index" src="/themes/defaulttheme/images/unwatch.gif" altKey="itracker.web.image.unwatch.issue.alt" arg0="${watchedIssues.issue.id}" textActionKey="itracker.web.image.unwatch.texttag"/>
        
        <%-- %>	<c:if test="${watchedIssues.canEditIssue}">
          	<it:formatImageAction action="/module-projects/editissueform" paramName="id" paramValue="${watchedIssues.issue.id}" caller="index" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.issue.alt" arg0="${watchedIssues.issue.id}" textActionKey="itracker.web.image.edit.texttag"/>
          	</c:if> --%>
      
             
    </td>
    <td></td>
    <td style="text-align:left;" >${watchedIssues.issue.id}</td>
    <td></td>
    <td style="white-space: nowrap">${watchedIssues.issue.project.name}</td>
    <td></td>
    <td style="white-space: nowrap">${watchedIssues.statusLocalizedString}</td>
    <td></td>
    <td>${watchedIssues.severityLocalizedString}</td>
    <td></td>
    <td><it:formatDescription>${watchedIssues.issue.description}</it:formatDescription></td>
    <td></td>
    <td>   <c:choose>
            <c:when test="${watchedIssues.unassigned}">
                unassigned
            </c:when>
            <c:otherwise>
                ${watchedIssues.issue.owner.firstName} ${watchedIssues.issue.owner.lastName}
            </c:otherwise>
    </c:choose></td>
    <td></td>
    <td style="text-align:right; white-space: nowrap"><it:formatDate date="${watchedIssues.issue.lastModifiedDate}"/></td>
    
    
    </tr>
    
</c:forEach>
<tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>

<!-- view hidden sections link -->

<c:if test="${userPrefs.hiddenIndexSections>0}">
    <tr style="text-align:left;" class="listRowUnshaded">
        <td colspan="15" style="text-align:left;" ><html:link page="/portalhome.do?sections=all">
        <it:message key="itracker.web.index.viewhidden"/></html:link></td>
    </tr>
    <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>

</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
