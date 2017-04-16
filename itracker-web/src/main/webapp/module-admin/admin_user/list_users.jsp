<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listusers.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
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

<div class="container-fluid maincontent">

   <div class="row">
      <div class="col-xs-12">
         <div class="pull-right">
            <c:if test="${allowProfileCreation}">
               <it:formatIconAction action="edituserform" targetAction="create"
                                    icon="plus" iconClass="fa-2x"
                                    info="itracker.web.image.create.user.alt"
                                    textActionKey="itracker.web.image.create.texttag"/>
            </c:if>
         </div>
         <h4><it:message key="itracker.web.attr.users"/>: (<it:message
                 key="itracker.web.admin.listusers.numactive" arg0="${activeSessions}"/>)</h4>
      </div>
   </div>
   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">
            <table id="users" class="table table-striped">
               <colgroup>
                  <col class="col-xs-1">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-1">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
               </colgroup>
               <thead>
               <tr>
                  <th></th>
                  <th><it:message key="itracker.web.attr.login"/></th>
                  <th><it:message key="itracker.web.attr.name"/></th>
                  <th><it:message key="itracker.web.attr.email"/></th>
                  <th><it:message key="itracker.web.attr.admin"/></th>
                  <th><it:message key="itracker.web.attr.lastmodified"/></th>
                  <th><it:message key="itracker.web.attr.online"/></th>
               </tr>
               </thead>
               <tbody>
               <c:forEach items="${users}" var="aUser" varStatus="i">
                  <c:set var="trClass" value="" scope="page"/>
                  <c:if test="${aUser.statusLocked}">
                     <c:set var="trClass" value="text-danger"/>
                  </c:if>
                  <tr class="${trClass}">
                     <td class="actions">
                        <it:formatIconAction action="edituserform" paramName="id"
                                             paramValue="${aUser.user.id}" targetAction="update"
                                             icon="edit" iconClass="fa-lg"
                                             info="itracker.web.image.edit.user.alt"
                                             arg0="${aUser.user.login}"
                                             textActionKey="itracker.web.image.edit.texttag"/>
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
                     <c:set var="tdClass" value="${trClass}"/>
                     <c:if test="${aUser.regisrationTypeSelf}">
                        <c:set var="tdClass">${tdClass} text-warning</c:set>
                     </c:if>
                     <c:set var="tdStyle" value=""/>
                     <c:if test="${aUser.regisrationTypeSelf}">
                        <c:set var="tdStyle" value="font-style: italic"/>
                     </c:if>
                     <td class="login ${tdClass}" style="${tdStyle}">${aUser.user.login}</td>
                     <td class="name">${aUser.user.firstName} ${aUser.user.lastName}</td>
                     <td class="email">${aUser.user.email}</td>
                     <td class="superuser">
                        <c:choose>
                           <c:when test="${aUser.user.superUser}">
                              <it:message key="itracker.web.generic.yes"/>
                           </c:when>
                           <c:otherwise>
                              <it:message key="itracker.web.generic.no"/>
                           </c:otherwise>
                        </c:choose>
                     </td>
                     <td class="lastModifiedDate"><it:formatDate date="${aUser.user.lastModifiedDate}" format="notime"/></td>
                     <td class="lastAccess"><it:formatDate date="${aUser.lastAccess}" format="short"
                                        emptyKey="itracker.web.generic.no"/></td>
                  </tr>
               </c:forEach>
               </tbody>
            </table>
            <div class="alert alert-info">
               <it:message key="itracker.web.admin.listusers.note"/>
            </div>
         </div>
      </div>
   </div>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
