<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="java.util.*" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>
<%@ page import="org.itracker.model.Notification.Role"%>
<%@ page import="org.itracker.web.util.LoginUtilities"%>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.viewissue.title"/>
<bean:define id="pageTitleArg" value="${issue.id}"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

            <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
              <tr>
                <td width="15%"></td>
                <td width="35%"></td>
                <td width="15%"></td>
                <td width="35%"></td>
              </tr>
              <tr>
                <td class="editColumnTitle" valign="top"><it:message key="itracker.web.attr.description"/>: </td>
                <td class="editColumnText">${issue.description}</td>
                <td class="editColumnTitle" valign="top"><it:message key="itracker.web.attr.actions"/>: </td>
                
                <td class="editColumnText" valign="top">
                  <table style="border: none; padding: 1px; border-spacing: 0; text-align: left;">
                    <tr>
                      <td style="text-align: right; vertical-align: top;"  class="editColumnText" style="white-space: nowrap;" nowrap>

                        <it:formatImageAction forward="listissues"
                                              module="/module-projects"
                                              paramName="projectId"
                                              paramValue="${project.id}"
                                              caller="viewissue"
                                              src="/themes/defaulttheme/images/list.gif"
                                              altKey="itracker.web.image.issuelist.issue.alt"
                                              textActionKey="itracker.web.image.issuelist.texttag"/>

                        <c:if test="${hasIssueNotification}">
                             <it:formatImageAction forward="watchissue"
                                              module="/module-projects"
                                                   paramName="id"
                                                   paramValue="${issue.id}"
                                                   caller="viewissue"
                                                   src="/themes/defaulttheme/images/watch.gif"
                                                   altKey="itracker.web.image.watch.issue.alt"
                                                   arg0="${issue.id}"
                                                   textActionKey="itracker.web.image.watch.texttag"/>
                        </c:if>
                        <c:if test="${canEditIssue}">

                             <it:formatImageAction action="editissueform"
                                                   module="/module-projects"
                                                   paramName="id"
                                                   paramValue="${issue.id}"
                                                   caller="viewissue"
                                                   src="/themes/defaulttheme/images/edit.gif"
                                                   altKey="itracker.web.image.edit.issue.alt"
                                                   arg0="${issue.id}"
                                                   textActionKey="itracker.web.image.edit.texttag"/>

                             <it:formatImageAction action="moveissueform"
                                                   module="/module-projects"
                                                   paramName="id"
                                                   paramValue="${issue.id}"
                                                   caller="viewissue"
                                                   src="/themes/defaulttheme/images/move.gif"
                                                   altKey="itracker.web.image.move.issue.alt"
                                                   arg0="${issue.id}"
                                                   textActionKey="itracker.web.image.move.texttag"/>

                             <%-- TODO reinstate this when relate issues works correctly
                             <it:formatImageAction forward="relateissue" paramName="id" paramValue="${issue.id}" caller="viewissue" src="/images/link.gif" altKey="itracker.web.image.link.issue.alt" textActionKey="itracker.web.image.link.texttag"/>
                             --%>
                        </c:if>
                        <c:if test="${canCreateIssue}">
                            <it:formatImageAction forward="createissue"
                                                  module="/module-projects"
                                                  paramName="projectId"
                                                  paramValue="${project.id}"
                                                  src="/themes/defaulttheme/images/create.gif"
                                                  altKey="itracker.web.image.create.issue.alt"
                                                  arg0="${project.name}"
                                                  textActionKey="itracker.web.image.create.texttag"/>
                        </c:if>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="8"/></td></tr>
              <tr>
                <td class="editColumnTitle"><it:message  key="itracker.web.attr.status"/>: </td>
                <td class="editColumnText">${issueStatusName}</td>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>: </td>
                <td class="editColumnText">
                  <it:formatDate date="${issue.createDate}"/>
                  (${issue.creator.firstName}&nbsp;${issue.creator.lastName})
                </td>
              </tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.resolution"/>: </td>
                <td class="editColumnText"><it:formatResolution projectOptions="${project.options}">${issue.resolution}</it:formatResolution></td>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>: </td>
                <td class="editColumnText"><it:formatDate date="${issue.lastModifiedDate}"/></td>
              </tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>: </td>
                <td class="editColumnText">${issueSeverityName}</td>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>: </td>
                <td class="editColumnText">${issueOwnerName}</td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.project"/>: </td>
                <td class="editColumnText">
                                          
                        <it:formatImageAction forward="listissues"
                                              module="/module-projects"
                                              paramName="projectId"
                                              paramValue="${issue.project.id}"
                                              caller="viewissue"
                                              src="/themes/defaulttheme/images/list.gif"
                                              altKey="itracker.web.image.issuelist.issue.alt"
                                              textActionKey="itracker.web.image.issuelist.texttag"/> &nbsp; ${issue.project.name}
                </td>
                
                <c:if test="${not empty project.versions}">
                    <td class="editColumnTitle" style="white-space: nowrap;" ><it:message key="itracker.web.attr.target"/>:</td>
                    <td class="editColumnText">${issue.targetVersion == null ? '' : issue.targetVersion.number}</td>
                </c:if>
                
              </tr>
              <tr>
                
                <c:choose>
                   <c:when test="${not empty project.components}">
                      <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>: </td>
                      <td valign="top" class="editColumnText">

                        	<c:forEach var="component" items="${issue.components}">
                             ${component.name} <br/>
	                        </c:forEach>
                      </td>
                   </c:when>
                  <c:otherwise>
                    <td></td>
                    <td></td>
                  </c:otherwise>
           		</c:choose>
               
                <c:choose>
                <c:when test="${not empty project.versions}">
                      <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.versions"/>: </td>
                      <td valign="top" class="editColumnText">
	                  	<c:forEach var="version" items="${project.versions}">
                            ${version.number} <br/>
                         </c:forEach>
                        
                      </td>
                 </c:when>
                 <c:otherwise>
                      <td></td>
                      <td></td>
                 </c:otherwise>
               </c:choose>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>

              
              <c:if test="${not empty projectFieldsMap}">
                     <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>:</td></tr>
                     <tr class="listHeading"><td colspan="4"><html:img height="2" width="1" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
                     <tr><td colspan="4"><html:img height="3" width="1" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
                     <tr>
                     <c:forEach var="projectField" varStatus="i" items="${projectFieldsMap} ">
              
              			<c:if test="${i.count % 2 == 0}">
                             </tr>
                             <tr>
                        </c:if>
              
                         <it:formatCustomField field="${projectField.key}" currentValue="${projectField.value}" displayType="view"/>

              		</c:forEach>
                     </tr>
                     <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
             
              </c:if>
            </table>
            <br />
            <%-- TODO reinsert this once issue relation has been implemented correctly 
            <%
               IssueRelationModel[] relations = issue.getRelations();
               Arrays.sort(relations, new IssueRelationModel());
               if(relations != null && relations.size() > 0) {
                 int colNumber = 0;

            %>
                 <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
                   <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
                   <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                   <tr style="text-align: left;">
                      <td colspan="2" width="50%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
                      <td colspan="2" width="50%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
                   </tr>

                   <% for(int i = 1; i <= IssueUtilities.NUM_RELATION_TYPES; i++) {
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
                                    <table width="100%" border="0" cellspacing="0"  cellspacing="1" >
                                      <tr>
                                        <td class="listRowTextBold" style="text-align: left;" colspan="5">
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
                                <td></td>
                                <td class="listRowText" style="text-align: right;"><it:link forward="viewissue" styleClass="listRowText" paramName="id" paramValue="<%= relations[j].getRelatedIssueId() %>"><%= relations[j].getRelatedIssueId() %></it:link></td>
                                <td></td>
                                <td class="listRowText" style="text-align: left;"><it:formatDescription><%= relations[j].getRelatedIssueDescription() %></it:formatDescription></td>
                                <td class="listRowText" style="text-align: left;"><%= IssueUtilities.getStatusName(relations[j].getRelatedIssueStatus(), currLocale) %></td>
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
                  <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
                </table>
                <br>
            <% } %>
               
                  --%>
   
        
            <c:if test="${hasAttachmentOption}">
                <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
                  <tr>
                    <td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.attachments"/>:</td>
                  </tr>
                  <tr style="text-align: left;" class="listHeading">
                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.filename"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.description"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.filetype"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.filesize"/></td>
                    <td style="text-align: left;"><it:message key="itracker.web.attr.submittor"/></td>
                    <td style="text-align: right;"><it:message key="itracker.web.attr.lastupdated"/></td>
                  </tr>

                  <c:forEach  var="attachment" items="${attachments}" varStatus="i">
                  	
                      <tr class="${i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}" >
                        <td class="listRowText" style="text-align: left;">
                            <it:formatImageAction forward="downloadAttachment.do"
                                                  paramName="id"
                                                  paramValue="${attachment.id}"
                                                  target="_blank"
                                                  src="/themes/defaulttheme/images/download.png"
                                                  altKey="itracker.web.image.download.attachment.alt"
                                                  textActionKey="itracker.web.image.download.texttag"/>
                        </td>
                        <td></td>
                        <td class="listRowText" style="text-align: left;">${attachment.originalFileName}</td>
                        <td class="listRowText" style="text-align: left;"><it:formatDescription>${attachment.description}</it:formatDescription></td>
                        <td class="listRowText" style="text-align: left;">${attachment.type}</td>
                        <td class="listRowText" style="text-align: left;"><fmt:formatNumber pattern="0.##" value="${attachment.size / 1024}" type="number" /></td>
                        <td class="listRowText" style="text-align: left;">${attachment.user.firstName}&nbsp;${attachment.user.lastName}</td>
                        <td class="listRowText" style="text-align: right;"><it:formatDate date="${attachment.lastModifiedDate}"/></td>
                      </tr>
                 </c:forEach>
                  <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
                </table>
                <br>
            </c:if>

            <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">
              <tr>
                <td class="editColumnTitle" colspan="3"><it:message key="itracker.web.attr.history"/>:</td>
                <td style="text-align: right;"><it:formatImageAction forward="view_issue_activity.do" paramName="id" paramValue="${issueId}" src="/themes/defaulttheme/images/view.gif" altKey="itracker.web.image.view.activity.alt" textActionKey="itracker.web.image.view.texttag"/></td>
              </tr>
              <tr style="text-align: left;" class="listHeading">
                <td width="15"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                <td width="8"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                <td><it:message key="itracker.web.attr.updator"/></td>
                <td style="text-align: right;"><it:message key="itracker.web.attr.updated"/></td>
              </tr>
              <c:forEach var="history" items="${histories}" varStatus="i">
                    <tr class="${i.count % 2 == 0 ? 'listRowShaded' : 'listRowUnshaded'}" >
                      <td style="text-align: right;" class="historyName">${i.count})</td>
                      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="8" height="1"/></td>
                      <td class="historyName">
                        ${history.user.firstName}&nbsp;${history.user.lastName}
                        (<a href="mailto:${history.user.email}" class="mailto">${history.user.email}</a>)
                      </td>
                      <td style="text-align: right;" class="historyName"><it:formatDate date="${history.createDate}"/></td>
                    </tr>
                    <tr class="${i.count % 2 == 0 ? 'listRowShaded' : 'listRowUnshaded'}" >
                      <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="3"/></td>
                    </tr>
                    <tr class="${i.count % 2 == 0 ? 'listRowShaded' : 'listRowUnshaded'}" >
                      <td colspan="2"></td>
                      <td colspan="3">
                        <table style="border: none; padding: 1px; border-spacing: 0;">
                          <tr class="${i.count % 2 == 0 ? 'listRowShaded' : 'listRowUnshaded'}" >
                            <td style="text-align: left;"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                            <td style="text-align: left;" >
	                            <div style="white-space: normal; overflow: auto; width: 900px">
	                              <it:formatHistoryEntry projectOptions="${project.options}">${history.description}</it:formatHistoryEntry>
	                            </div>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                </c:forEach>
                <tr class="listRowUnshaded">
                  <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="8"/></td>
                </tr>
            </table>
            <br/><br/>
            <table style="border: none; padding: 1px; border-spacing: 0; width: 100%;">
              <tr>
                <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.notifications"/>:</td>
              </tr>
              <tr style="text-align: left;" class="listHeading">
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
            </table>
        <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
    </body>
</html>
             

