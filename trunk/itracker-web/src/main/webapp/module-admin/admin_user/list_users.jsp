<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.admin.listusers.title"/>
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

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%" class="shadeList">
  <tr>
    <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.users"/>: (<it:message key="itracker.web.admin.listusers.numactive" arg0="${activeSessions}"/>)</td>
    <c:if test="${allowProfileCreation}">
        <td align="right">
          <it:formatImageAction action="edituserform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.user.alt" textActionKey="itracker.web.image.create.texttag"/>
        </td>
    </c:if>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.login"/></td>
    <td><it:message key="itracker.web.attr.name"/></td>
    <td><it:message key="itracker.web.attr.email"/></td>
    <td><it:message key="itracker.web.attr.admin"/></td>
    <td><it:message key="itracker.web.attr.lastmodified"/></td>
    <td><it:message key="itracker.web.attr.online"/></td>
  </tr>

    <c:forEach items="${users}" var="aUser" varStatus="i">
        <c:set var="style" value="" />
        <c:if test="${aUser.statusLocked}">
            <c:set var="style" value="color: red;" />
        </c:if>
        <c:if test="${aUser.regisrationTypeSelf}">
            <c:set var="style" value="${style}font-style: italic;" />
        </c:if>

        <c:choose>
            <c:when test="${i.count % 2 == 1}">
                <tr align="right" class="listRowUnshaded" style="${style}">
            </c:when>
            <c:otherwise>
                <tr align="right" class="listRowShaded" style="${style}">
            </c:otherwise>
        </c:choose>

        <td>
          <it:formatImageAction action="edituserform" paramName="id" paramValue="${aUser.user.id}" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.user.alt" arg0="${aUser.user.login}" textActionKey="itracker.web.image.edit.texttag"/>
            <c:choose>
                <c:when test="${aUser.statusLocked}">
                    <it:formatImageAction action="unlockuser" paramName="id" paramValue="${aUser.user.id}" src="/themes/defaulttheme/images/unlock.gif" altKey="itracker.web.image.unlock.user.alt" arg0="${aUser.user.login}" textActionKey="itracker.web.image.unlock.texttag"/>
                </c:when>
                <c:otherwise>
                    <it:formatImageAction action="lockuser" paramName="id" paramValue="${aUser.user.id}" src="/themes/defaulttheme/images/lock.gif" altKey="itracker.web.image.lock.user.alt" arg0="${aUser.user.login}" textActionKey="itracker.web.image.lock.texttag"/>
                </c:otherwise>
            </c:choose>
        </td>
        <td></td>
        <td>${aUser.user.login}</td>
        <td>${aUser.user.firstName} ${aUser.user.lastName}</td>
        <td>${aUser.user.email}</td>
        <td align="left">
            <c:choose>
                <c:when test="${aUser.user.superUser}">
                    <it:message key="itracker.web.generic.yes" />
                </c:when>
                <c:otherwise>
                    <it:message key="itracker.web.generic.no" />
                </c:otherwise>
            </c:choose>
        </td>
        <td><it:formatDate date="${aUser.user.lastModifiedDate}" format="notime"/></td>
        <td><it:formatDate date="${aUser.lastAccess}" format="short" emptyKey="itracker.web.generic.no"/></td>
      </tr>
    </c:forEach>

  <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="8" width="1"/></td></tr>
  <tr><td colspan="8" class="tableNote"><it:message key="itracker.web.admin.listusers.note"/></td></tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
