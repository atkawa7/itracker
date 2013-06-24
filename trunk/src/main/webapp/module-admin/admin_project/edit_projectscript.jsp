<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
        <tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

	<logic:messagesPresent>
                <div class="formError">
                    <html:messages id="error">
                        <bean:write name="error"/><br/>
                    </html:messages>
                </div>
            <br>
	</logic:messagesPresent>

	<html:form action="/editprojectscript" acceptCharset="UTF-8" enctype="multipart/form-data">
            <html:hidden property="projectId"/>
            <table border="0" cellspacing="0" cellspacing="1"  width="80%">
                <tr>
                    <th valign="top" align="center">Add</th>
                    <th valign="top" align="left">Script Name</th>
                    <th valign="top" align="left">Field Id</th>
                    <th valign="top" align="left">Priority</th>
                </tr>
                <tr>
                    <td colspan="4" ><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1"/></td>
                </tr>
                    <c:forEach items="${ workflowScripts }" var="script" varStatus="idx">
                        <c:set var="styleClass" value="${ idx.index%2==1?'listRowShaded':'listRowUnshaded' }" />

                        <c:set var="scriptAvailable" value="${ false }" />

                        <c:set var="customFieldsDropdown">
                            <html:select property="fieldId(${script.id})" styleClass="editColumnText" value="">
                                <c:forEach items="${ customFields }" var="customfield">
                                    <c:set var="available" value="${ true }" />
                                    <c:forEach items="${ projectScripts }" var="projectScript">
                                        <c:if test="${ (script.id eq projectScript.script.id)
                                                      and projectScript.fieldId eq customfield.key }">
                                            <c:set var="available" value="${ false }" />
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${ available }">
                                        <option value="${customfield.key}" >
                                            <c:out value="${ customfield.value }" />
                                            <c:set var="scriptAvailable" value="${ true }" />
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </html:select>
                        </c:set>


                        <tr class="${ styleClass }">
                           <td valign="top" align="center">
                               <c:if test="${ scriptAvailable }">
                                <html:checkbox indexed="false" name="projectScriptForm" property="scriptItems(${ script.id })"/>
                               </c:if>
                           </td>
                           <td valign="top" align="left"><c:out value="${ script.name }" /></td>
                           <td valign="top" align="left">

                               <c:choose>
                                   <c:when test="${ scriptAvailable }">
                                    ${ customFieldsDropdown }
                                   </c:when>
                                   <c:otherwise>
                                    <it:message key="itracker.web.generic.notapplicable" />
                                   </c:otherwise>
                               </c:choose>

                           </td>
                           <td valign="top" align="left">
                               <c:if test="${ scriptAvailable }">
                                <html:select property="priority(${script.id})" styleClass="editColumnText" value="4">
                                    <c:forEach items="${priorityList}" var="priority" varStatus="k">
                                        <html:option value="${priority.key}">
                                            <c:out value="${ priority.value }" />
                                        </html:option>
                                    </c:forEach>
                                </html:select>
                               </c:if>
                           </td>
                        </tr>
                    </c:forEach>
                <tr>
                    <td colspan="4" ><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1"/></td>
                </tr>
            </table>

            <html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit>

 	</html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
</html>
