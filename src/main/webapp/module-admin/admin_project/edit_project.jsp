<%@ page contentType="text/html;charset=UTF-8"%>



<%@ taglib uri="/tags/itracker" prefix="it"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp" />

<logic:messagesPresent>
	<center><span class="formError"> <html:messages
		id="error">
		<bean:write name="error" />
		<br />
	</html:messages> </span></center>
	<br>
</logic:messagesPresent>

<table>
	<tr>
		<td><html:form action="/editproject">
			<html:hidden property="action" />
			<html:hidden property="id" />

			<table border="0" cellspacing="0" cellspacing="1" width="800">
				<tr>
					<td class="editColumnTitle"><it:message
						key="itracker.web.attr.name" />:</td>
					<td><html:text property="name" styleClass="editColumnText" /></td>
					<td valign="top" class="editColumnTitle"><it:message
						key="itracker.web.attr.status" />:</td>
					<td valign="top" class="editColumnText"><html:select
						property="status" styleClass="editColumnText">
						<c:forEach var="status" items="${statuses}">
							<html:option styleClass="editColumnText" value="${status.value}">${status.name}
    	        </html:option>
						</c:forEach>
					</html:select></td>
				</tr>
				<tr>
					<td class="editColumnTitle"><it:message
						key="itracker.web.attr.description" />:</td>
					<td><html:text property="description"
						styleClass="editColumnText" /></td>
					<td class="editColumnTitle"><it:message
						key="itracker.web.attr.created" />:</td>
					<td class="editColumnText"><it:formatDate
						date="${project.createDate}" /></td>
				</tr>

				<tr>
					<td valign="top" class="editColumnTitle"><it:message
						key="itracker.web.attr.owners" />:</td>
					<td valign="top" class="editColumnText"><html:select
						property="owners" size="5" multiple="true"
						styleClass="editColumnText">
						<c:forEach var="owner" items="${owners}">
							<html:option value="${owner.id}">${owner.firstName}&nbsp;${owner.lastName} 
    	        </html:option>
						</c:forEach>
					</html:select></td>

					<c:choose>
						<c:when test="${isUpdate}">

							<td class="editColumnTitle" valign="top"><it:message
								key="itracker.web.attr.lastmodified" />:</td>
							<td class="editColumnText" valign="top"><it:formatDate
								date="${project.lastModifiedDate}" /></td>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${allowPermissionUpdateOption}">

									<td valign="top" class="editColumnTitle"><it:message
										key="itracker.web.admin.editproject.addusers" />:</td>
									<td valign="top" class="editColumnText" nowrap><html:select
										property="users" size="5" multiple="true"
										styleClass="editColumnText">
										<c:forEach var="user" items="${users}">
											<html:option value="${user.id}">${user.firstName}&nbsp;${user.lastName}
              </html:option>

										</c:forEach>

									</html:select> <html:select property="permissions" size="5" multiple="true"
										styleClass="editColumnText">
										<c:forEach var="permission" items="${permissions}">
											<html:option value="${permission.value}">${permission.name}
            </html:option>
										</c:forEach>
									</html:select></td>
								</c:when>
							</c:choose>
						</c:otherwise>
					</c:choose>

				</tr>
				<tr>
					<td colspan="4"><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" height="15" width="1" />
					</td>
				</tr>

				<tr class="editColumnTitle">
					<td colspan="4"><it:message
						key="itracker.web.admin.editproject.options" />:</td>
				</tr>
				<tr class="listHeading">
					<td colspan="4"><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" height="2" width="1" /></td>
				</tr>
				<tr>
					<td colspan="4"><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" height="3" width="1" /></td>
				</tr>
				<tr>
					<td width="25"></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="2" class="editColumnText">
						<html:multibox property="options"
							value="${optionSupressHistoryHtml}" />
						<it:message
							key="itracker.web.admin.editproject.options.html" />
					</td>

					<td colspan="2" class="editColumnText">
						<html:multibox property="options"
							value="${optionLiteralHistoryHtml}" />
						<it:message
							key="itracker.web.admin.editproject.options.htmlliteral" />
					</td>
				</tr>
				<tr>
					<td colspan="2" class="editColumnText">
						<html:multibox property="options"
							value="${optionPredefinedResolutions}" /> 
						<it:message
							key="itracker.web.admin.editproject.options.resolution" />
					</td>

					<td colspan="2" class="editColumnText">
						<html:multibox property="options"
							value="${optionNoAttachments}" />
						<it:message
							key="itracker.web.admin.editproject.options.noattach" />
					</td>
				</tr>
				<tr>
					<td colspan="2" class="editColumnText">
						<html:multibox property="options"
						value="${optionAllowAssignToClose}" />
						<it:message
						key="itracker.web.admin.editproject.options.closed" /></td>
					<c:if test="${allowSelfRegister}">

						<td colspan="2" class="editColumnText">
							<html:multibox
								property="options" value="${optionAllowSelfRegisteredViewAll}" />
							<it:message
								key="itracker.web.admin.editproject.options.srview" />
						</td>
					</c:if>
				</tr>
				<c:if test="${allowSelfRegister}">
					<tr>
						<td colspan="2" class="editColumnText">
							<html:multibox
								property="options" value="${optionAllowSefRegisteredCreate}" />
							<it:message
								key="itracker.web.admin.editproject.options.srcreate" />
						</td>
					</tr>
				</c:if>

				<tr>
					<td colspan="4"><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" height="15" width="1" /></td>
				</tr>

				<c:if test="${ not empty customFields }">

					<tr class="editColumnTitle">
						<td colspan="4"><it:message
							key="itracker.web.attr.customfields" />:</td>
					</tr>
					<tr class="listHeading">
						<td colspan="4"><html:img module="/"
							page="/themes/defaulttheme/images/blank.gif" height="2" width="1" /></td>
					</tr>
					<tr>
						<td colspan="4"><html:img module="/"
							page="/themes/defaulttheme/images/blank.gif" height="3" width="1" /></td>
					</tr>
					
					
					<c:forEach var="customField" items="${ customFields }" step="2" varStatus="i">
						<tr>
							<td width="25"></td>
							<td></td>
						</tr>
						<tr>
							<td colspan="2" class="editColumnText">
								<html:multibox
								property="fields" value="${customField.id}" />
								${customField.name} (${customField.type})
							</td>
							<c:forEach var="customField" items="${ customFields }" begin="${ i.index + 1 }" end="${ i.index + 1 }">
								<td colspan="2" class="editColumnText">
									<html:multibox
									property="fields" value="${customField.id}" />
									${customField.name} (${customField.type})
								</td>
							</c:forEach>
						</tr>
					
					</c:forEach>
					
					
					<tr>
						<td colspan="4"><html:img module="/"
							page="/themes/defaulttheme/images/blank.gif" height="15"
							width="1" /></td>
					</tr>

				</c:if>

				<c:choose>
					<c:when test="${isUpdate}">

						<tr>
							<td colspan="4" align="left"><html:submit
								styleClass="button" altKey="itracker.web.button.update.alt"
								titleKey="itracker.web.button.update.alt">
								<it:message key="itracker.web.button.update" />
							</html:submit></td>
						</tr>

					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="4" align="left"><html:submit
								styleClass="button" altKey="itracker.web.button.create.alt"
								titleKey="itracker.web.button.create.alt">
								<it:message key="itracker.web.button.create" />
							</html:submit></td>
						</tr>
					</c:otherwise>
				</c:choose>

			</table>
		</html:form></td>
	</tr>
	<tr>
		<td><c:if test="${isUpdate}">
			<table border="0" cellspacing="0" cellspacing="1" width="100%">
				<%-- 

<tr>
    <td class="editColumnTitle" colspan="5"><it:message key="itracker.web.attr.scripts"/>:</td>
    <td align="right">
        <it:formatImageAction action="editprojectscriptform"
                              paramName="projectId"
                              paramValue="<%= project.getId() %>"
                              targetAction="update"
                              src="/themes/defaulttheme/images/create.gif"
                              altKey="itracker.web.image.create.projectscript.alt"
                              arg0="<%= project.getName() %>"
                              textActionKey="itracker.web.image.create.texttag"/>
    </td>
</tr>
<tr align="left" class="listHeading">
    <td width="40"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.field"/></td>
    <td><it:message key="itracker.web.attr.script"/></td>
    <td align="left"><it:message key="itracker.web.attr.priority"/></td>
    <td><it:message key="itracker.web.attr.event"/></td>
</tr>
<%
    List<CustomField> customFields2 = IssueUtilities.getCustomFields();
    List<ProjectScript> scripts = project.getScripts();

    Collections.sort(scripts, new ProjectScript.CompareByFieldAndPriority());

    for (int i = 0; i < scripts.size(); i++) {
        if (i % 2 == 1) {
%>

<tr align="right" class="listRowShaded">

    <%    } else { %>

<tr align="right" class="listRowUnshaded">

    <% } %>

    <td align="right">
        <it:formatImageAction action="removeprojectscript"
                              paramName="delId"
                              paramValue="<%= scripts.get(i).getId() %>"
                              src="/themes/defaulttheme/images/delete.gif"
                              altKey="itracker.web.image.delete.projectscript.alt"
                              textActionKey="itracker.web.image.delete.texttag"/>
    </td>
    <td></td>
    <td><%= IssueUtilities.getFieldName(scripts.get(i).getFieldId(), customFields2, (java.util.Locale) pageContext.getAttribute("currLocale")) %>
    </td>
    <td><%= scripts.get(i).getScript().getName() %>
    </td>
    <td><%= WorkflowUtilities.getEventName(scripts.get(i).getScript().getEvent(), (java.util.Locale) pageContext.getAttribute("currLocale")) %>
    </td>
    <td align="left"><%= scripts.get(i).getPriority() %>
    </td>
</tr>
<% } %>
<tr>
    <td colspan="6"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15"/></td>
</tr>
--%>
				<tr>
					<td class="editColumnTitle" colspan="5"><it:message
						key="itracker.web.attr.versions" />:</td>
					<td align="right"><it:formatImageAction
						action="editversionform" paramName="projectId"
						paramValue="${project.id}" targetAction="create"
						src="/themes/defaulttheme/images/create.gif"
						altKey="itracker.web.image.create.version.alt"
						arg0="${project.name}"
						textActionKey="itracker.web.image.createtexttag" /></td>
				</tr>

				<tr align="left" class="listHeading">
					<td width="40"></td>
					<td><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" width="4" /></td>
					<td><it:message key="itracker.web.attr.number" /></td>
					<td><it:message key="itracker.web.attr.description" /></td>
					<td><it:message key="itracker.web.attr.lastmodified" /></td>
					<td align="left"><it:message key="itracker.web.attr.issues" /></td>
				</tr>

				<c:forEach var="version" items="${versions}" varStatus="i">
					<tr align="right"
						class="${i.index % 2 == 0}?'listRowShaded' :'listRowUnshaded'">
						<td align="right"><it:formatImageAction
							action="editversionform" paramName="id"
							paramValue="${version.id}" targetAction="update"
							src="/themes/defaulttheme/images/edit.gif"
							altKey="itracker.web.image.edit.version.alt"
							arg0="${version.number}"
							textActionKey="itracker.web.image.edit.texttag" /></td>
						<td></td>
						<td>${version.number}</td>
						<td>${version.description}</td>
						<td><it:formatDate date="${version.date}" /></td>
						<td align="left">${version.count}</td>
					</tr>
				</c:forEach>
				<tr>
					<td colspan="6"><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" height="15" /></td>
				</tr>

				<tr>
					<td class="editColumnTitle" colspan="5"><it:message
						key="itracker.web.attr.components" />:</td>
					<td align="right"><it:formatImageAction
						action="editcomponentform" paramName="projectId"
						paramValue="${project.id}" targetAction="create"
						src="/themes/defaulttheme/images/create.gif"
						altKey="itracker.web.image.create.component.alt"
						arg0="${project.name}"
						textActionKey="itracker.web.image.create.texttag" /></td>
				</tr>
				<tr align="left" class="listHeading">
					<td width="40"></td>
					<td><html:img module="/"
						page="/themes/defaulttheme/images/blank.gif" width="4" /></td>
					<td><it:message key="itracker.web.attr.name" /></td>
					<td><it:message key="itracker.web.attr.description" /></td>
					<td><it:message key="itracker.web.attr.lastmodified" /></td>
					<td align="left"><it:message key="itracker.web.attr.issues" /></td>
				</tr>

				<c:forEach var="component" items="${components}" varStatus="i">
					<tr align="right"
						class="${i.index % 2 == 0}?'listRowShaded' :'listRowUnshaded'">
						<td align="right"><it:formatImageAction
							action="editcomponentform" paramName="id"
							paramValue="${component.id}" targetAction="update"
							src="/themes/defaulttheme/images/edit.gif"
							altKey="itracker.web.image.edit.component.alt"
							arg0="${component.name}"
							textActionKey="itracker.web.image.edit.texttag" /></td>
						<td></td>
						<td>${component.name}</td>
						<td>${component.description}</td>
						<td><it:formatDate date="${component.date}" /></td>
						<td align="left">${component.count}</td>
					</tr>

				</c:forEach>
			</table>

		</c:if></td>
	</tr>
</table>


<tiles:insert page="/themes/defaulttheme/includes/footer.jsp" />
</body>
</html>
