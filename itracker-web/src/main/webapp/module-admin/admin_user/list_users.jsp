<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listusers.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>

<table style="border: none; padding: 1px; border-spacing: 0; width: 100%" class="shadeList">
   <tr>
      <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.attr.users"/>: (<it:message
              key="itracker.web.admin.listusers.numactive" arg0="${activeSessions}"/>)
      </td>
      <c:if test="${allowProfileCreation}">
         <td align="right">
            <it:formatIconAction action="edituserform" targetAction="create"
                                  icon="plus" iconClass="fa-2x"
                                  info="itracker.web.image.create.user.alt"
                                  textActionKey="itracker.web.image.create.texttag"/>
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
      <c:set var="class" value=""/>
      <c:if test="${aUser.statusLocked}">
         <c:set var="class" value="text-danger"/>
      </c:if>

      <c:choose>
         <c:when test="${i.count % 2 == 1}">
            <tr align="right" class="listRowUnshaded ${class}">
         </c:when>
         <c:otherwise>
            <tr align="right" class="listRowShaded ${class}">
         </c:otherwise>
      </c:choose>

      <td>
         <it:formatIconAction action="edituserform" paramName="id"
                              paramValue="${aUser.user.id}" targetAction="update"
                              icon="edit" iconClass="fa-lg"
                              info="itracker.web.image.edit.user.alt"
                              arg0="${aUser.user.login}" textActionKey="itracker.web.image.edit.texttag"/>
         <c:choose>
            <c:when test="${aUser.statusLocked}">
               <it:formatIconAction action="unlockuser" paramName="id" paramValue="${aUser.user.id}"
                                    icon="key" iconClass="fa-lg text-success"
                                    info="itracker.web.image.unlock.user.alt"
                                    arg0="${aUser.user.login}"
                                    textActionKey="itracker.web.image.unlock.texttag"/>
            </c:when>
            <c:otherwise>
               <it:formatIconAction action="lockuser" paramName="id" paramValue="${aUser.user.id}"
                                    icon="lock" iconClass="fa-lg text-danger"
                                    info="itracker.web.image.lock.user.alt" arg0="${aUser.user.login}"
                                    textActionKey="itracker.web.image.lock.texttag"/>
            </c:otherwise>
         </c:choose>
      </td>
      <td></td>
      <c:set var="tdClass" value="${class}" />
      <c:if test="${aUser.regisrationTypeSelf}">
         <c:set var="tdClass" value="${tdClass} text-warning"/>
      </c:if>
      <c:set var="tdStyle" value="" />
      <c:if test="${aUser.regisrationTypeSelf}">
         <c:set var="tdStyle" value="font-style: italic"/>
      </c:if>
      <td class="${tdClass}" style="${tdStyle}">${aUser.user.login}</td>
      <td>${aUser.user.firstName} ${aUser.user.lastName}</td>
      <td>${aUser.user.email}</td>
      <td align="left">
         <c:choose>
            <c:when test="${aUser.user.superUser}">
               <it:message key="itracker.web.generic.yes"/>
            </c:when>
            <c:otherwise>
               <it:message key="itracker.web.generic.no"/>
            </c:otherwise>
         </c:choose>
      </td>
      <td><it:formatDate date="${aUser.user.lastModifiedDate}" format="notime"/></td>
      <td><it:formatDate date="${aUser.lastAccess}" format="short" emptyKey="itracker.web.generic.no"/></td>
      </tr>
   </c:forEach>

   <tr>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="8" width="1"/></td>
   </tr>
   <tr>
      <td colspan="8" class="tableNote"><it:message key="itracker.web.admin.listusers.note"/></td>
   </tr>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
