<%@ page contentType="text/html;charset=UTF-8" %>


<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.editissue.title"/>
<bean:define id="pageTitleArg" value="${issue.id}"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="issueForm"/>


<logic:messagesPresent >
    <center>
          <span class="formError">
           <html:messages id="error">
               <bean:write name="error"/><br/>
           </html:messages>
          </span>
    </center>
    <br>
</logic:messagesPresent>

<html:form action="/editissue" method="post" enctype="multipart/form-data">
<html:hidden property="id"/>
<html:hidden property="projectId"/>
<html:hidden property="prevStatus"/>
<html:hidden property="caller"/>

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
<tr>
    <td width="15%"></td>
    <td width="35%"></td>
    <td width="15%"></td>
    <td width="35%"></td>
</tr>
<tr>
    <td  class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
    <td>
                    <html:text size="80"
                               property="description"
                               styleClass="editColumnText"/></td>
   	<td class="editColumnTitle"><it:message key="itracker.web.attr.actions"/>: </td>
    <td class="editColumnText">

        <table style="border: none; padding: 1px; border-spacing: 0;">
            <tr>
                <td style="text-align: right; vertical-align: bottom; white-space: nowrap;" class="editColumnText" >
                    <it:formatImageAction forward="listissues"
                                          module="/module-projects"
                                          paramName="projectId"
                                          paramValue="${issue.project.id}"
                                          caller="editissue"
                                          src="/themes/defaulttheme/images/list.gif"
                                          altKey="itracker.web.image.issuelist.issue.alt"
                                          textActionKey="itracker.web.image.issuelist.texttag"/>

                   
                    <c:if test="${hasIssueNotification}">

                    <it:formatImageAction forward="watchissue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          caller="editissue"
                                          src="/themes/defaulttheme/images/watch.gif"
                                          altKey="itracker.web.image.watch.issue.alt"
                                          arg0="${issue.id}"
                                          textActionKey="itracker.web.image.watch.texttag"/>

                    </c:if>

                   
					<c:if test="${hasEditIssuePermission}">
                    <it:formatImageAction action="moveissueform"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          caller="editissue"
                                          src="/themes/defaulttheme/images/move.gif"
                                          altKey="itracker.web.image.move.issue.alt"
                                          arg0="${issue.id}"
                                          textActionKey="itracker.web.image.move.texttag"/>

                        <%-- TODO re-include this once relate issue correctly works 
                          <it:formatImageAction action="addissuerelation" 
                          						module="/module-projects" 
                          						paramName="id" 
                          						paramValue="${issue.id}" 
                          						caller="editissue" 
                          						src="/themes/defaulttheme/images/link.gif" 
                          						altKey="itracker.web.image.link.issue.alt" 
                          						textActionKey="itracker.web.image.link.texttag"/>
                   --%>
					</c:if>
                   

                    
					<c:if test="${canCreateIssue}">
                    <it:formatImageAction forward="createissue"
                                          module="/module-projects"
                                          paramName="projectId"
                                          paramValue="${issue.project.id}"
                                          src="/themes/defaulttheme/images/create.gif"
                                          altKey="itracker.web.image.create.issue.alt"
                                          arg0="${issue.project.name}"
                                          textActionKey="itracker.web.image.create.texttag"/>


                    </c:if>

                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
    <td class="editColumnText">

       
		<c:choose>
		<c:when test="${not empty statuses}">
    	    <html:select property="status" styleClass="editColumnText">
        	   <c:forEach var="status" items="${statuses}">
	            <html:option styleClass="editColumnText"
                         value="${status.value}">${status.name}
    	        </html:option>
	           </c:forEach>
	    	</html:select>
        </c:when>
        <c:otherwise>
		<html:hidden property="status"/>
        	${statusName}

        </c:otherwise>
        </c:choose>
        

    </td>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.creator"/>:</td>
    <td class="editColumnText">
        <it:formatDate date="${issue.createDate}"/>
        (${issue.creator.firstName}&nbsp;${issue.creator.lastName})
    </td>
</tr>
<tr>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.resolution"/>:</td>
    <td class="editColumnText">
       
        <c:choose>
        <c:when test="${currUser.superUser || (hasFullEdit && (issue.status >= 300) && (issue.status < 500))}">

        
		<c:choose>
		<c:when test="${hasPredefinedResolutionsOption}">
        <html:select property="resolution" styleClass="editColumnText">
            <option value=""></option>
			<c:forEach var="resolution" items="${resolutions}">
           		 <html:option styleClass="editColumnText"
                         value="${resolution.value}">${resolution.name}
           		 </html:option>
			</c:forEach>
	    </html:select>
		</c:when>
		<c:otherwise>
             <html:text size="20" property="resolution" styleClass="editColumnText"/>
        </c:otherwise>
		</c:choose>
        </c:when>
        <c:otherwise>
			${issue.resolution == null ? '' : issue.resolution }
        </c:otherwise>
        </c:choose>

    </td>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
    <td class="editColumnText">
        <it:formatDate date="${issue.lastModifiedDate}"/>
    </td>
</tr>
<tr>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>:</td>
    <td class="editColumnText">

        
        <c:choose>
		<c:when test="${ hasFullEdit }">
        <html:select property="severity" styleClass="editColumnText">
            <c:forEach items="${ fieldSeverity }" var="severity" varStatus="status">
            <html:option value="${ severity.value }"styleClass="editColumnText">
            ${ severity.name }
			</html:option>
            </c:forEach>
        </html:select>
		</c:when>
		<c:otherwise>
			${ severityName }
        </c:otherwise>
        </c:choose>

    </td>
    <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>:</td>

    
    <c:choose>
     <c:when test="${ isStatusResolved }">
	  	<td class="editColumnText">
       		${issueOwnerName}
    	</td>
	</c:when>
	<c:otherwise>
	    <input type="hidden"
           name="currentOwner"
           value="${issue.owner == null ? -1 : issue.owner.id}">
	    <c:choose>
			<c:when test="${not empty possibleOwners}">
    			<td>
        			<html:select property="ownerId" styleClass="editColumnText">
            			<c:forEach items="${possibleOwners}" var="possibleOwner" varStatus="status">
                			<html:option value="${possibleOwner.value}">${possibleOwner.name}</html:option>
        				</c:forEach>
        			</html:select>
    			</td>
			</c:when>
			<c:otherwise>
    			 <td class="editColumnText">${issueOwnerName}
	 			</td>
    		</c:otherwise>
    	</c:choose>
	</c:otherwise>
	</c:choose>
</tr>
<tr>
    <td colspan="4">
        <html:img module="/"
                  page="/themes/defaulttheme/images/blank.gif" 
                  width="1"
                  height="12"/>
    </td>
</tr>
<tr>
    <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.project"/>:</td>
    <td valign="top" class="editColumnText">
    	<it:formatImageAction forward="listissues"
                               module="/module-projects"
		                       paramName="projectId"
		                       paramValue="${issue.project.id}"
		                       caller="editissue"
		                       src="/themes/defaulttheme/images/list.gif"
		                       altKey="itracker.web.image.issuelist.issue.alt"
		                       textActionKey="itracker.web.image.issuelist.texttag"/>&nbsp;${issue.project.name}
		</td>

    <c:if test="${not empty targetVersions}">
     <td valign="top" class="editColumnTitle" style="white-space: nowrap;" nowrap>
        <it:message key="itracker.web.attr.target"/>:&nbsp;</td>
 	   <td valign="top" class="editColumnText">

        
		<c:choose>
			<c:when test="${hasFullEdit}">
        <html:select property="targetVersion" styleClass="editColumnText">
            <html:option value="-1">&nbsp;</html:option>
			<c:forEach var="targetVersion" items="${targetVersions}">
	            <html:option value="${targetVersion.value}"
                         styleClass="editColumnText">${targetVersion.name}
    	        </html:option>

            </c:forEach>

        </html:select>
			</c:when>
			<c:otherwise>
        

        <c:if test="${not empty issue.targetVersion}">
            ${issue.targetVersion.number}
        </c:if>

        
        </c:otherwise>
        </c:choose>

    </td>

    
    </c:if>

</tr>
<tr>
    <c:choose>
    <c:when test="${not empty components }">
    <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>:</td>
    <td valign="top" class="editColumnText">
    	<c:choose>
    	<c:when test="${hasFullEdit}">    	
        
        <html:select property="components" size="5" multiple="true" styleClass="editColumnText">
            
            <c:forEach var="component" items="${components }">
            <html:option value="${component.value}"
                         styleClass="editColumnText">${component.name}
            </html:option>
            
            </c:forEach>
        </html:select>
        </c:when>
        <c:otherwise>
	        <c:forEach var="issueComponent" items="${issueComponents}">
    		    ${issueComponent.name}
        		<br/>
        	</c:forEach>
        </c:otherwise>
        </c:choose>
    </td>
    </c:when>
    <c:otherwise>
    <td></td>
    <td></td>
    </c:otherwise>
    </c:choose>
    
    <c:choose>
    <c:when test="${not empty versions}">
    <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>:</td>
    <td valign="top" class="editColumnText">
        
        <c:choose>
        <c:when test="${hasFullEdit}">
        <html:select property="versions" size="5" multiple="true" styleClass="editColumnText">
            
            <c:forEach var="version" items="${versions}">
            <html:option value="${version.value}"
                         styleClass="editColumnText">${version.name}
            </html:option>
            
            </c:forEach>
        </html:select>
        </c:when>
        <c:otherwise>
        
        
        <c:forEach var="issueVersion" items="${issueVersions}">
        	${issueVersion.number}
        <br/>
        </c:forEach>
        
       
        </c:otherwise>
        </c:choose>
    </td>
    </c:when>
    <c:otherwise>
    
    <td></td>
    <td></td>
   
    </c:otherwise>
    </c:choose>
</tr>
<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td>
</tr>



		<c:if test="${ not empty projectFieldsMap }">
			<tr>
				<td colspan="4" class="editColumnTitle"><it:message
					key="itracker.web.attr.customfields" />:</td>
			</tr>
			<tr class="listHeading">
				<td><it:message key="itracker.web.attr.field" /></td>
				<td><it:message key="itracker.web.attr.value" /></td>
				<td><it:message key="itracker.web.attr.field" /></td>
				<td><it:message key="itracker.web.attr.value" /></td>
			</tr>
			<c:forEach var="projectField" varStatus="i" items="${ projectFieldsMap }"
				step="2">
				<tr>
					<it:formatCustomField field="${ projectField.key }"
										  currentValue="${ projectField.value }" 
										  displayType="${ hasFullEdit?'edit' : 'view' }" />
					<c:forEach begin="${ i.index + 1 }" end="${ i.index + 1 }"
						var="projectField" items="${ projectFieldsMap }">
						<it:formatCustomField field="${ projectField.key }"
											  currentValue="${ projectField.value }"
											  displayType="${ hasFullEdit?'edit' : 'view' }" />
					</c:forEach>
				</tr>
			</c:forEach>

			</tr>
			<tr>
				<td colspan="4"><html:img module="/"
					page="/themes/defaulttheme/images/blank.gif" width="1" height="18" /></td>
			</tr>

		</c:if>
		
		<tr>
    <td>
        <html:submit styleClass="button" altKey="itracker.web.button.update.alt"
                     titleKey="itracker.web.button.update.alt"><it:message
                key="itracker.web.button.update"/></html:submit><br/><br/>
    </td>
</tr>
    <%-- TODO reinclude this once related issues has been implemented corretly
    <c:if test="${ not empty issueRelations }">
    	       <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
               <tr class="listHeading">
               		<td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
    				<td colspan="2" valign="top">
                                <table width="100%" border="0" cellspacing="0" cellspacing="1" >
                                  <tr>
                                    <td class="listRowTextBold" align="left" colspan="5">
                                    	<it:message key="itracker.issuerelation.${  }"></it:message>
                                      <%--= ITrackerResources.getString(ITrackerResources.KEY_BASE_ISSUE_RELATION + i, currLocale) --:
                                    </td>
                                  </tr>
                                  <tr>
                                    <td width="10"><html:img page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                                    <td width="15%"></td>
                                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                                    <td width="60%"></td>
                                    <td width="25%"></td>
                                  </tr>
    	
    	<c:forEach items="${ issueRelations }" var="relation" varStatus="status">
    	<c:set var="relType" value="${ relation.relationType }" />
    	<c:if test="${ relType < 3 }"><-- RELATION_TYPE_RELATED_P = 1; RELATION_TYPE_RELATED_C = 2 -->
    	
                        <tr>
                            <td valign="top"><it:formatImageAction action="removerelation" paramName="relationId" paramValue="${ relation.id }" caller="editissue" src="/images/delete.gif" altKey="itracker.web.image.delete.relation.alt" textActionKey="itracker.web.image.delete.texttag"/></td>
                            <td class="listRowText" align="right" valign="top" ><it:link forward="viewissue" styleClass="listRowText" paramName="id" paramValue="${ relation.relatedIssueId }">${ relation.relatedIssueId }</it:link></td>
                            <td></td>
                            <td class="listRowText" align="left" valign="top" ><it:formatDescription>${ relation.relatedIssueDescription }</it:formatDescription></td>
                            <td class="listRowText" align="left" valign="top" ><it:message key="itracker.status.${ relation.relatedIssueStatus }"></it:message></td>
                          </tr>
    	
    	</c:if>
    	</c:forEach>
    
                     </table></td>
                     </tr>
		<tr><td colspan="4"><html:img page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td></tr>
    </c:if>
    
          %--
             IssueRelationModel[] relations = issue.getRelations();
             Arrays.sort(relations, new IssueRelationModel());
          %>
               <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
               <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>

               <% int colNumber = 0;
                  for(int i = 1; i <= IssueUtilities.NUM_RELATION_TYPES; i++) {
                    if(i == IssueUtilities.RELATION_TYPE_RELATED_C) {
                       continue;
                    }

                    boolean displayedType = false;
                    for(int j = 0; j < relations.size(); j++) {
                      int relType = relations[j].getRelationType();
                      if(relType == i || (i == IssueUtilities.RELATION_TYPE_RELATED_P && relType == IssueUtilities.RELATION_TYPE_RELATED_C)) {
                          if(! displayedType) {
                              displayedType = true;
                              if(colNumber == 0) {
               %>
                                  <tr valign="top">
               <%             } else if(colNumber % 2 == 0) { %>
                                  </tr><tr valign="top">
               <%             } %>
                              <td colspan="2" valign="top">
                                <table width="100%" border="0" cellspacing="0" cellspacing="1" >
                                  <tr>
                                    <td class="listRowTextBold" align="left" colspan="5">
                                      <%= ITrackerResources.getString(ITrackerResources.KEY_BASE_ISSUE_RELATION + i, currLocale) %>:
                                    </td>
                                  </tr>
                                  <tr>
                                    <td width="10"><html:img page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                                    <td width="15%"></td>
                                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                                    <td width="60%"></td>
                                    <td width="25%"></td>
                                  </tr>
              <%          } %>
                          <tr>
                            <td valign="top"><it:formatImageAction action="removerelation" paramName="relationId" paramValue="<%= relations[j].getId() %>" caller="editissue" src="/images/delete.gif" altKey="itracker.web.image.delete.relation.alt" textActionKey="itracker.web.image.delete.texttag"/></td>
                            <td class="listRowText" align="right" valign="top" ><it:link forward="viewissue" styleClass="listRowText" paramName="id" paramValue="<%= relations[j].getRelatedIssueId() %>"><%= relations[j].getRelatedIssueId() %></it:link></td>
                            <td></td>
                            <td class="listRowText" align="left" valign="top" ><it:formatDescription><%= relations[j].getRelatedIssueDescription() %></it:formatDescription></td>
                            <td class="listRowText" align="left" valign="top" ><%= IssueUtilities.getStatusName(relations[j].getRelatedIssueStatus(), currLocale) %></td>
                          </tr>
              <%
                      }
                   }
                   if(displayedType) {
                     colNumber++;
              %>
                     </table></td>
              <%
                   }
                 }
              %>
              <tr><td colspan="4"><html:img page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td></tr>
          %-- 
          END Re-Integrated relation stuff
          --%>
<c:if test="${ not hasNoViewAttachmentOption }">
<tr>
    <td colspan="4">
        <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
            <tr>
                <td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.attachments"/>:</td>
            </tr>

            <c:choose>
                <c:when test="${not empty issue.attachments}">

                    <tr style="text-align: left;" class="listHeading">
                        <td>
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      width="15"
                                      height="1"/>
                        </td>
                        <td>
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      width="8"
                                      height="1"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.filename"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.description"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.filetype"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.filesize"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.submittor"/>
                        </td>
                        <td >
                            <it:message key="itracker.web.attr.lastupdated"/>
                        </td>
                    </tr>

                    <c:forEach items="${issue.attachments}" var="attachment" varStatus="status">

                        <c:choose>
                            <c:when test="${status.count % 2 == 0}">
                                <c:set var="rowShading" value="listRowShaded"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="rowShading" value="listRowUnshaded"/>
                            </c:otherwise>
                        </c:choose>

                        <tr class="${rowShading}">
                            <td class="listRowText" style="text-align: left;" >
                                <it:formatImageAction action="downloadAttachment.do"
                                				      module="/module-projects"
                                                      paramName="id"
                                                      paramValue="${attachment.id}"
                                                      target="_blank"
                                                      src="/themes/defaulttheme/images/download.png"
                                                      altKey="itracker.web.image.download.attachment.alt"
                                                      textActionKey="itracker.web.image.download.texttag"/>
                            </td>

                            <td></td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.originalFileName}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                <it:formatDescription>${attachment.description}</it:formatDescription>
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.type}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                              <fmt:formatNumber pattern="0.##" value="${attachment.size / 1024}" type="number" />
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                    ${attachment.user.firstName}&nbsp;${attachment.user.lastName}
                            </td>
                            <td class="listRowText" style="text-align: left;">
                                <it:formatDate date="${attachment.lastModifiedDate}"/>
                            </td>
                        </tr>

                    </c:forEach>

                </c:when>

                <c:otherwise>
                    <tr class="listHeading">
                        <td colspan="4">
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      height="2"
                                      width="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <html:img module="/"
                                      page="/themes/defaulttheme/images/blank.gif"
                                      height="3"
                                      width="1"/>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>

        </table>
    </td>
</tr>
<tr class="listRowUnshaded">
    <td colspan="4">
        <it:message key="itracker.web.attr.description"/>
        <html:text property="attachmentDescription" size="30"
                   maxlength="60" styleClass="editColumnText"/>
        &nbsp;&nbsp;&nbsp; <it:message key="itracker.web.attr.filename"/>
        <html:file property="attachment"
                   styleClass="editColumnText"/>
    </td>
</tr>
<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td>
</tr>

</c:if>

</table>

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
<tr>
    <td colspan="4">
        <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
            <tr>
                <td class="editColumnTitle" colspan="3">
                    <it:message key="itracker.web.attr.history"/>:</td>
                <td style="text-align: right">
                    <it:formatImageAction forward="view_issue_activity.do"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          src="/themes/defaulttheme/images/view.gif"
                                          altKey="itracker.web.image.view.activity.alt"
                                          textActionKey="itracker.web.image.view.texttag"/>
                </td>
            </tr>
            <tr style="text-align: left" class="listHeading">

                
				<c:choose>
				<c:when test="${currUser.superUser}">
                <td width="30">
                    <html:img module="/"
                              page="/themes/defaulttheme/images/blank.gif"
                              width="30"
                              height="1"/>
                </td>
				</c:when>
				<c:otherwise>
                

                <td width="15">
                    <html:img module="/"
                              page="/themes/defaulttheme/images/blank.gif"
                              width="15"
                              height="1"/>
                </td>

                
                </c:otherwise>
				</c:choose>	

                <td width="3">
                    <html:img module="/"
                              page="/themes/defaulttheme/images/blank.gif"
                              width="3"
                              height="1"/>
                </td>
                <td><it:message key="itracker.web.attr.updator"/></td>
                <td align="right"><it:message key="itracker.web.attr.updated"/></td>
            </tr>

            <c:forEach items="${issueHistory}" var="historyEntry" varStatus="status">

                <c:choose>
                    <c:when test="${status.count % 2 == 0}">
                        <c:set var="rowShading" value="listRowShaded"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rowShading" value="listRowUnshaded"/>
                    </c:otherwise>
                </c:choose>

                <tr class="${rowShading}">
                    <td align="right" valign="bottom">

                       
                        <c:if test="${currUser.superUser}">

                        <it:formatImageAction action="removehistory"
                                              paramName="historyId"
                                              paramValue="${historyEntry.id}"
                                              caller="editissue"
                                              src="/themes/defaulttheme/images/delete.gif"
                                              altKey="itracker.web.image.delete.history.alt"
                                              textActionKey="itracker.web.image.delete.texttag"/>

                       
                        </c:if>

                        ${status.count})

                    </td>
                    <td></td>
                    <td class="historyName">
                        ${historyEntry.user.firstName}&nbsp;${historyEntry.user.lastName}
                        (<a href="mailto:${historyEntry.user.email}" class="mailto">${historyEntry.user.email}</a>)
                    </td>
                    <td align="right" class="historyName">
                        <it:formatDate date="${historyEntry.createDate}"/>
                    </td>
                </tr>
                <tr class="${rowShading}">
                    <td colspan="5">
                        <html:img module="/"
                                  page="/themes/defaulttheme/images/blank.gif"
                                  width="1"
                                  height="3"/>
                    </td>
                </tr>
                <tr class="${rowShading}">
                    <td colspan="2"></td>
                    <td colspan="3">
                        <table style="border: none; padding: 1px; border-spacing: 0;">
                            <tr class="${rowShading}">
                                <td align="left">
                                    <html:img module="/"
                                              page="/themes/defaulttheme/images/blank.gif"
                                              width="10"
                                              height="1"/>
                                </td>
                                <td style="text-align: left; white-space: normal;">
                                <div style="white-space: normal; overflow: auto; width: 900px">
                                    <it:formatHistoryEntry>${historyEntry.description}</it:formatHistoryEntry>
								</div>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr class="listRowUnshaded">
                    <td colspan="5">
                        <html:img module="/"
                                  page="/themes/defaulttheme/images/blank.gif"
                                  width="1"
                                  height="8"/>
                    </td>
                </tr>

            </c:forEach>

            <tr>
                <td valign="top" align="right" class="historyName"></td>
                <td></td>
               
                <td colspan="3" class="editColumnText">
                    <textarea name="history"
                              wrap="${wrap}"
                              cols="110"
                              rows="6"
                              class="editColumnText"><bean:write name="issueForm" property="history"/></textarea>
                </td>
            </tr>
        </table>
    </td>
</tr>

<tr>
    <td colspan="4">
        <html:img module="/"
                  page="/themes/defaulttheme/images/blank.gif"
                  width="1"
                  height="18"/>
    </td>
</tr>

<tr>
    <td colspan="4">
        <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
            <tr>
                <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.notifications"/>:</td>
            </tr>
            <tr align="left" class="listHeading">
                <td><it:message key="itracker.web.attr.name"/></td>
                <td><it:message key="itracker.web.attr.email"/></td>
                <td><it:message key="itracker.web.attr.role"/></td>
            </tr>

			<c:forEach items="${notifiedUsers}" var="user" varStatus="status">
                <tr class="${status.count % 2 == 0?'listRowShaded' : 'listRowUnshaded'}">
                    <td class="listRowSmall">${user.firstName}&nbsp;${user.lastName}</td>
                    <td class="listRowSmall">
                        <a href="mailto:${user.email}"
                           class="mailto">${user.email}</a>
                    </td>
                    <td class="listRowSmall"><ul>
                    	<c:forEach items="${notificationMap[user]}" var="role">
                    	<li><it:message key="itracker.notification.role.${role.code}"></it:message></li>
                    	</c:forEach>
						</ul>
                    
                    </td>
                </tr>
			</c:forEach>

            <%--<c:forEach items="${notifications}" var="notification" varStatus="status">

                <c:choose>
                    <c:when test="${status.count % 2 == 0}">
                        <c:set var="rowShading" value="listRowShaded"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rowShading" value="listRowUnshaded"/>
                    </c:otherwise>
                </c:choose>

                <tr class="${rowShading}">
                    <td class="listRowSmall">${notification.user.firstName}&nbsp;${notification.user.lastName}</td>
                    <td class="listRowSmall">
                        <a href="mailto:${notification.user.email}"
                           class="mailto">${notification.user.email}</a>
                    </td>
                    <td class="listRowSmall"><it:message key="itracker.notification.role.${notification.notificationRole}"></it:message></td>
                    <!--NotificationUtilities.getRoleName(notifications.get(i).getNotificationRole())-->
                </tr>

            </c:forEach>
--%>
        </table>
    </td>
</tr>

<tr>
    <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td>
</tr>
<tr>
    <td colspan="4" align="left">
        <html:submit styleClass="button"
                     altKey="itracker.web.button.update.alt"
                     titleKey="itracker.web.button.update.alt">
            <it:message key="itracker.web.button.update"/>
        </html:submit>
    </td>
</tr>
</table>

</html:form>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>

</html>
