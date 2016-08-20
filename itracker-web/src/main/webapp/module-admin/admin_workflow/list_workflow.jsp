<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listworkflow.title"/>
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

<table border="0" cellspacing="0" cellpadding="1" width="100%" class="shadeList">
   <tr>
      <td class="editColumnTitle" colspan="6"><it:message key="itracker.web.attr.workflowscripts"/>:</td>
      <td align="right">
         <it:formatIconAction action="editworkflowscriptform" targetAction="create"
                              icon="plus" iconClass="fa-2x"
                              info="itracker.web.image.create.workflowscript.alt"
                              textActionKey="itracker.web.image.create.texttag"/>
      </td>
   </tr>
   <tr align="left" class="listHeading">
      <td width="1"></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
      <td><it:message key="itracker.web.attr.name"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
      <td><it:message key="itracker.web.attr.event"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
      <td align="right"><it:message key="itracker.web.attr.lastmodified"/></td>
   </tr>

   <c:forEach items="${sc.workflowScripts}" var="aScript" varStatus="i">
      <c:choose>
         <c:when test="${i.count % 2 == 1}">
            <tr align="right" class="listRowUnshaded">
         </c:when>
         <c:otherwise>
            <tr align="right" class="listRowShaded">
         </c:otherwise>
      </c:choose>

      <td>
         <it:formatIconAction action="editworkflowscriptform" paramName="id"
                              paramValue="${aScript.id}" targetAction="update" arg0="${aScript.name}"
                              icon="edit" iconClass="fa-lg"
                              info="itracker.web.image.edit.workflowscript.alt"
                              textActionKey="itracker.web.image.edit.texttag"/>
         <it:formatIconAction action="removeworkflowscript" paramName="id"
                              paramValue="${aScript.id}" arg0="${aScript.name}"
                              icon="remove" iconClass="fa-lg" styleClass="deleteButton"
                              info="itracker.web.image.delete.workflowscript.alt"
                              textActionKey="itracker.web.image.delete.texttag"/>
      </td>
      <td></td>
      <td>${aScript.name}</td>
      <td></td>
      <td align="left"><it:message key="itracker.workflow.field.event.${aScript.event}"/></td>
      <td></td>
      <td align="right"><it:formatDate date="${aScript.lastModifiedDate}"/></td>
      </tr>
   </c:forEach>

</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
