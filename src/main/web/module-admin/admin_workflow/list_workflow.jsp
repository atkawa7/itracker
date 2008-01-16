<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.itracker.core.resources.*" %>

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

<%
  ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");
  
  List<WorkflowScript> workflowScripts = sc.getWorkflowScripts();

  for(int i = 0; i < workflowScripts.size(); i++) {
    if(i % 2 == 1) {
%>
      <tr align="right" class="listRowShaded">
<%  } else { %>
      <tr align="right" class="listRowUnshaded">
<%  } %>
      <td>
        <it:formatImageAction action="editworkflowscriptform" paramName="id" paramValue="<%= workflowScripts.get(i).getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.workflowscript.alt" arg0="<%= workflowScripts.get(i).getName() %>" textActionKey="itracker.web.image.edit.texttag"/>
        <it:formatImageAction action="removeworkflowscript" paramName="id" paramValue="<%= workflowScripts.get(i).getId() %>" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.workflowscript.alt" arg0="<%= workflowScripts.get(i).getName() %>" textActionKey="itracker.web.image.delete.texttag"/>
      </td>                           
      <td></td>
      <td><%= workflowScripts.get(i).getName() %></td>
      <td></td>
      <td align="left"><%= ITrackerResources.getString(ITrackerResources.KEY_BASE_WORKFLOW_EVENT + workflowScripts.get(i).getEvent(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
      <td></td>
      <td align="right"><%= workflowScripts.get(i).getNumberUses() %></td>
      <td></td>
      <td align="right"><it:formatDate date="<%= workflowScripts.get(i).getLastModifiedDate() %>"/></td>
    </tr>
<%
  }
%>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
