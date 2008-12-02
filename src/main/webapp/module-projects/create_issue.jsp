<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<bean:define id="pageTitleKey" value="itracker.web.createissue.title"/>
<bean:define id="pageTitleArg" value="${project.name}"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<%-- <html:javascript formName="createIssueForm"/> replaced by --%>
      <html:javascript formName="createIssueForm"/>

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

      <html:form action="/createissue" focus="description" enctype="multipart/form-data">
        <html:hidden property="projectId" value="${project.id}"/>

        <table border="0" cellspacing="0"  cellspacing="1"  width="800px">
          <tr>
            <td width="15%"></td>
            <td width="35%"></td>
            <td width="15%"></td>
            <td width="35%"></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td colspan="3" class="editColumnText"><html:text size="80" maxlength="255" property="description" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td class="editColumnText">${statusName}</td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.owner"/>:</td>
              
              <c:choose>
              	<c:when test="${not empty possibleOwners}">
                   <td><html:select property="ownerId" styleClass="editColumnText">
                      <html:option value="-1" key="itracker.web.generic.unassigned"/>
                      
                      <c:forEach var="possibleOwner" items="${possibleOwners}">
                           <html:option value="${possibleOwner.value}">${possibleOwner.name}</html:option>
                      
                      </c:forEach>
                   </html:select></td>
               	</c:when>
               <c:otherwise>
                 <td class="editColumnText"><it:message key="itracker.web.generic.unassigned"/></td>
               </c:otherwise>
              </c:choose>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.severity"/>:</td>
            <td class="editColumnText">
              <html:select property="severity" styleClass="editColumnText">
             
              <c:forEach var="severity" items="${severities}">
                   <html:option value="${severity.value}">${severity.name}</html:option>
              </c:forEach>
              </html:select>
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.creator"/>:</td>
            
            <c:choose>
            	<c:when test="${not empty possibleCreators}">
                  <td><html:select property="creatorId" styleClass="editColumnText">
            
                  	<c:forEach var="possibleCreator" items="${possibleCreators}">
                       <html:option value="${possibleCreator.value}">${possibleCreator.name}</html:option>
            
                  	</c:forEach>
                  </html:select></td>
                 </c:when>
                <c:otherwise>
            	      <td class="editColumnText">currUser<!-- TODO: fix this <- % -= currUser.getFirstName() + " " + currUser.getLastName() % >--></td>
               	</c:otherwise>
            </c:choose>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.project"/>: </td>
            <td class="editColumnText">
                                      
                    <it:formatImageAction forward="listissues"
                                          module="/module-projects"
                                          paramName="projectId"
                                          paramValue="${project.id}"
                                          caller="viewissue"
                                          src="/themes/defaulttheme/images/list.gif"
                                          altKey="itracker.web.image.issuelist.issue.alt"
                                          textActionKey="itracker.web.image.issuelist.texttag"/>&nbsp;${project.name}
            </td>

          </tr>
              
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
          <tr>
            
            <c:choose>
            	<c:when test="${not empty components}">
                  <td valign="top" class="editColumnTitle"><it:message key="itracker.web.attr.components"/>:</td>
                  <td valign="top" class="editColumnText">
                    <html:select property="components" size="5" multiple="true" styleClass="editColumnText">
                    	<c:forEach var="component" items="${components}">
                          <html:option value="${component.value}">${component.name}</html:option>
                        </c:forEach>
                    </html:select>
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
                    <html:select property="versions" size="5" multiple="true" styleClass="editColumnText">
                    	<c:forEach var="version" items="${versions}">
                           <html:option value="${version.value}">${version.name}</html:option>
                        </c:forEach>
                    </html:select>
                  </td>
                </c:when>
                <c:otherwise>
                  <td></td>
                  <td></td>
                </c:otherwise>
            </c:choose>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>

          
          <c:if test="${not empty projectFields}">
                <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.customfields"/>:</td></tr>
                <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
                <c:forEach var="projectField" items="${ projectFields }" varStatus="i" step="2">
                	<tr>
                       <it:formatCustomField field="${ projectField }" formName="createIssueForm" listOptions="${ listOptions }"/>
                       <c:forEach var="projectField" items="${ projectFields }" begin="${ i.index + 1 }" end="${ i.index + 1 }">
                       		<it:formatCustomField field="${ projectField }" formName="createIssueForm" listOptions="${ listOptions }"/>
                       </c:forEach>
               		<tr>
                </c:forEach>
                
            <%-- TODO reinsert this once related issues have been implemented correctly
                <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
              --%>
            </c:if>
         
            <%-- TODO reinsert this once related issues have been implemented correctly
             <tr><td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.relatedissues"/>:</td></tr>
             <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
            <tr><td colspan="4" class="editColumnText">
              <it:message key="itracker.web.attr.thisissue"/>
              <html:select property="relationType" styleClass="editColumnText">
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_RELATED_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_RELATED_P, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_P, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_C, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_P, currLocale) %></html:option>
                  <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_C, currLocale) %></html:option>
              </html:select>
              <it:message key="itracker.web.attr.issue"/>
              <html:text size="5" property="relatedIssueId" styleClass="editColumnText"/>.
            </td></tr>
            --%>
            <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td></tr>

          
          <c:if test="${hasAttachmentOption}">
              <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.addattachment"/>:</td></tr>
              <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
              <tr>
                <td class="listRowText"><it:message key="itracker.web.attr.description"/>:</td>
                <td class="editColumnText"><html:text property="attachmentDescription" size="30" maxlength="60" styleClass="editColumnText"/></td>
                <td class="listRowText"><it:message key="itracker.web.attr.file"/>:</td>
                <td class="editColumnText"><html:file property="attachment" styleClass="editColumnText"/></td>
              </tr>
              <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
              </c:if>
          

          <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.detaileddescription"/>:</td></tr>
          <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>

          
          <tr><td colspan="4" class="editColumnText" style="text-align: center;"><textarea name="history" wrap="${wrap}" cols="110" rows="6" class="editColumnText"><bean:write name="createIssueForm" property="history"/></textarea></td></tr>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td></tr>

          <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
        </table>
        <br/>
      </html:form>

      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
