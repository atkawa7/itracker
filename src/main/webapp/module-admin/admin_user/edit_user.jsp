<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.web.util.*" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.core.resources.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    // TODO : move redirect logic to the Action class. 
    User user = (User) session.getAttribute(Constants.EDIT_USER_KEY);

    if(user == null) {
%>
      <logic:forward name="unauthorized"/>
<%  } %>

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

      <html:form action="/edituser">
        <html:hidden property="action"/>
        <html:hidden property="id"/>

        <table border="0" cellspacing="0"  cellspacing="1" width="800px">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${user.login}<html:hidden property="login" />
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="login" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.status"/>:</td>
            <td class="editColumnText"><%= UserUtilities.getStatusName(user.getStatus(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.firstname"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${user.firstName}<html:hidden property="firstName" />
                          *</td>
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="firstName" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="${user.createDate}"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastname"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${user.lastName}<html:hidden property="lastName" />
                          *</td>
                  </c:when>
                  <c:otherwise>
                      <td><html:text property="lastName" styleClass="editColumnText"/></td>
                  </c:otherwise>
              </c:choose>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="${user.lastModifiedDate}"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.email"/>:</td>
              <c:choose>
                  <c:when test="${isUpdate && !allowProfileUpdate}">
                      <td class="editColumnText">${user.email}<html:hidden property="email" />
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
                      <td class="editColumnText"><%= ITrackerResources.getString((user.isSuperUser() ? "itracker.web.generic.yes" : "itracker.web.generic.no"), (java.util.Locale)pageContext.getAttribute("currLocale")) %><html:hidden property="superUser" /></td>
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
			
			<c:forEach items="${ permissionNames }" step="2" var="permissionName" varStatus="j">
				<tr class="listRowUnshaded">
					<td><!-- ${ j.index } --></td>

					<c:forEach items="${ permissionRowColIdxes }" var="idxAdd"
						varStatus="idxStatus">
						<c:if test="${ permissionNames[j.index + idxStatus.index] != null }">
							<c:set var="keyName"
								value="permissions(Perm${ permissionNames[j.index + idxStatus.index].value }Proj${ project.id })" />
							<c:set var="keyId"
								value="Perm${ permissionNames[j.index + idxStatus.index].value }_Proj${ project.id }" />
							<td><c:choose>
								<c:when test="${isUpdate && !allowPermissionUpdate}">
									<html:img page="/themes/defaulttheme/images/${ checkmarkImage }" />
									
									<html:hidden property="${ keyName }" />
								</c:when>
								<c:otherwise>
									<html:checkbox property="${ keyName }" value="on" />
								</c:otherwise>
							</c:choose></td>
							<td>${ permissionNames[j.index + idxStatus.index].name }</td>
							<td><it:formatDate date="${ currentPermissionDate}" /></td>
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
