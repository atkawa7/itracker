<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<script type="text/javascript">
    function validateForm() {
        var userForm = document.forms['userForm'];

        var password = userForm.password.value;
        var confPassword = userForm.confPassword.value;

        if(password != confPassword) {
            alert("Password does not match confirmation password.");
            return false;
        }
    }
</script>

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

      <html:form action="/edituser" method="post" acceptCharset="UTF-8" enctype="multipart/form-data">
        <html:hidden property="action"/>
        <html:hidden property="id"/>

        <table border="0" cellspacing="0"  cellspacing="1" width="800px">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${edituser.login}<html:hidden property="login" /></td>
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="login" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td class="editColumnText">
            ${userStatus}
            <c:choose>
                <c:when test="${ edituser.status != 1 }">
                    <it:formatImageAction action="unlockuser" paramName="id" paramValue="${edituser.id}" src="/themes/defaulttheme/images/unlock.gif" altKey="itracker.web.image.unlock.user.alt" arg0="${edituser.login}" textActionKey="itracker.web.image.unlock.texttag"/>
                </c:when>
                <c:otherwise>
                    <it:formatImageAction action="lockuser" paramName="id" paramValue="${edituser.id}" src="/themes/defaulttheme/images/lock.gif" altKey="itracker.web.image.lock.user.alt" arg0="${edituser.login}" textActionKey="itracker.web.image.lock.texttag"/>
                </c:otherwise>
            </c:choose></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.firstname"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${ edituser.firstName }<html:hidden property="firstName" />
                          *</td>
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="firstName" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="${ edituser.createDate }"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastname"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${ edituser.lastName }<html:hidden property="lastName" />
                          *</td>
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="lastName" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="${ edituser.lastModifiedDate }"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.email"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${edituser.email}<html:hidden property="email" />
                          *</td>
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="email" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
          </tr>
            <c:if test="${!isUpdate || allowPasswordUpdate}">
                <tr>
                    <td class="editColumnTitle"><br></td>
                    <td></td>
                </tr>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.password"/>:</td>
                    <td><html:password property="password" styleId="password" styleClass="editColumnText"
                                       redisplay="false"/></td>
                </tr>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.confpassword"/>:</td>
                    <td><html:password property="confPassword" styleId="confPassword" styleClass="editColumnText"
                                       redisplay="false"/></td>
                </tr>
            </c:if>
                      <tr>
                <td class="editColumnTitle"><br></td>
                <td> </td>
  

            <td class="editColumnTitle"><it:message key="itracker.web.attr.superuser"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <c:choose>
                          <c:when test="${edituser.superUser}">
                              <td class="editColumnText"><it:message key="itracker.web.generic.yes"/><html:hidden property="superUser" /></td>
                          </c:when>
                          <c:otherwise>
                              <td class="editColumnText"><it:message key="itracker.web.generic.no"/><html:hidden property="superUser" /></td>
                          </c:otherwise>
                      </c:choose>
                  </c:when>
                  <c:otherwise>
                      <td class="editColumnText">
                        <html:radio property="superUser" value="true"/><it:message key="itracker.web.generic.yes"/> &nbsp;&nbsp;&nbsp;&nbsp;
                        <html:radio property="superUser" value="false"/><it:message key="itracker.web.generic.no"/>
                      </td>
                  </c:otherwise>
              </c:choose>
          </tr>
                    <tr>
                <tr>
            <td class="editColumnText"><sup>*</sup><it:message key="itracker.web.generic.reqfield"/></td>
               <td></td>
          
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>
        </table>
        <c:choose>
            <c:when test="${isUpdate}">
                <html:submit styleClass="button"
                             altKey="itracker.web.button.update.alt"
                             titleKey="itracker.web.button.update.alt">
                    <it:message key="itracker.web.button.update"/>
                </html:submit>
            </c:when>
            <c:otherwise>
                <html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit>
            </c:otherwise>
        </c:choose>
        <br/>
        <br/>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%" align="center">
          <tr>
            <td class="editColumnTitle" colspan="8"><it:message key="itracker.web.attr.permissions"/>:</td>
          </tr>
          <tr class="listHeading">
            <td colspan="2"></td>
            <td align="left"><it:message key="itracker.web.attr.permission"/></td>
            <td align="left"><it:message key="itracker.web.attr.lastmodified"/></td>
            <td colspan="2"></td>
            <td align="left"><it:message key="itracker.web.attr.permission"/></td>
            <td align="left"><it:message key="itracker.web.attr.lastmodified"/></td>
          </tr>
          <tr>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="10"/></td>
            <td colspan="3"></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1" width="10"/></td>
            <td colspan="3"></td>
          </tr>


		<c:forEach items="${projects}" var="project" varStatus="i" step="1">
			<tr align="left" class="listRowShaded">
				<td colspan="8"><input title="toggle all" type="checkbox"
					onchange="toggleProjectPermissionsChecked(this)"
					name="Proj${ project.id }" />&nbsp;<it:message
					key="itracker.web.attr.project" /> ${ project.name }</td>
			</tr>
			
			<c:set var="projectPermissions"
				value="${ edituserperms[project.id] }" />
			<c:set var="currentPermissionDate" value="${ null }" />
			
			<%-- iterate over all permissions, step: 2 --%>
			<c:forEach items="${ permissionNames }" step="2" var="permissionName" varStatus="j">
				<tr class="listRowUnshaded">
					<td><!-- ${ j.index } --></td>
					<%-- iterate over all columns --%>
					<c:forEach items="${ permissionRowColIdxes }" var="idxAdd"
						varStatus="idxStatus">
						<%-- skip last row second column if not present --%>
						
						<c:set var="permissionName" value="${ permissionNames[ j.index + idxStatus.index] }" />
						<c:if test="${ permissionName != null }">
							<c:set var="keyName"
								value="permissions(Perm${ permissionName.value }Proj${ project.id })" />
								
							<c:set var="permission" value="${ projectPermissions[ permissionName.value ] }" />
							
							<td><c:choose>
								<c:when test="${isUpdate && !allowPermissionUpdate}">
									<html:img page="/themes/defaulttheme/images/${ checkmarkImage }" />
									
									<html:hidden property="${ keyName }" />
								</c:when>
								<c:otherwise>
									<html:checkbox property="${ keyName }" value="on" />
								</c:otherwise>
							</c:choose></td>
							<td>${ permissionName.name }</td>
							<td>
								<c:if test="${ permission != null }">
									<it:formatDate date="${ permission.lastModifiedDate }" />
								</c:if>
							</td>
						</c:if>
						<td></td>
					</c:forEach>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="8"><html:img module="/"
					page="/themes/defaulttheme/images/blank.gif" height="5" width="1" /></td>
			</tr>
		</c:forEach>

		<tr>
              <td colspan="8">
                  <html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/>
              </td>
          </tr>
        </table>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
            <c:choose>
                <c:when test="${isUpdate}">
                <tr>
                    <td colspan="4" align="left">
                        <html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt" onclick="validateForm()">
                            <it:message key="itracker.web.button.update"/>
                        </html:submit>
                    </td>
                </tr>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="4" align="left">
                            <html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt">
                                <it:message key="itracker.web.button.create"/>
                            </html:submit>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>

      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
