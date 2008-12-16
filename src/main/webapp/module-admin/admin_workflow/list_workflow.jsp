<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listworkflow.title"/>
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

<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <tr>
    <td class="editColumnTitle" colspan="8"><it:message key="itracker.web.attr.workflowscripts"/>:</td>
    <td align="right">
      <it:formatImageAction action="editworkflowscriptform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.workflowscript.alt" textActionKey="itracker.web.image.create.texttag"/>
    </td>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.name"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td><it:message key="itracker.web.attr.event"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
    <td align="right"><it:message key="itracker.web.attr.numberuses"/></td>
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
        <it:formatImageAction action="editworkflowscriptform" paramName="id" paramValue="${aScript.id}" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.workflowscript.alt" arg0="${aScript.name}" textActionKey="itracker.web.image.edit.texttag"/>
        <it:formatImageAction action="removeworkflowscript" paramName="id" paramValue="${aScript.id}" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.workflowscript.alt" arg0="${aScript.name}" textActionKey="itracker.web.image.delete.texttag"/>
      </td>                           
      <td></td>
      <td>${aScript.name}</td>
      <td></td>
      <td align="left"><it:message key="itracker.workflow.field.event.${aScript.event}"/></td>
      <td></td>
      <td align="right">${aScript.numberUses}</td>
      <td></td>
      <td align="right"><it:formatDate date="${aScript.lastModifiedDate}"/></td>
    </tr>
  </c:forEach>

</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
