<%@ page contentType="text/html;charset=UTF-8" %>
 
<%-- page import="org.itracker.model.*" --%>
<%-- page import="org.itracker.core.resources.*" --%>
<%-- page import="org.itracker.web.scheduler.*" --%>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listtasks.title"/>
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

<table border="0" cellspacing="0" cellspacing="1" width="100%">
  <tr>
    <td class="editColumnTitle" colspan="8"><it:message key="itracker.web.attr.tasks"/>: <span class="listRowText">(<it:message key="itracker.web.admin.listtasks.lastran"/> <%--<it:formatDate date="<%= Scheduler.getLastRun() %>" format="full"/>)--%></span></td>
    <td align="right">
      <it:formatImageAction action="edittaskform" targetAction="create" src="/themes/defaulttheme/images/create.gif" altKey="itracker.web.image.create.task.alt" textActionKey="itracker.web.image.create.texttag"/>
    </td>
  </tr>
  <tr align="left" class="listHeading">
    <td width="1"></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
    <td><it:message key="itracker.web.attr.task"/></td>
    <td align="left"><it:message key="itracker.web.attr.months"/></td>
    <td align="left"><it:message key="itracker.web.attr.daysofmonth"/></td>
    <td align="left"><it:message key="itracker.web.attr.hours"/></td>
    <td align="left"><it:message key="itracker.web.attr.minutes"/></td>
    <td align="left"><it:message key="itracker.web.attr.weekdays"/></td>
    <td align="left"><it:message key="itracker.web.attr.lastrun"/></td>
  </tr>

<%--
  ScheduledTask[] tasks = Scheduler.getTasks();

  for(int i = 0; i < tasks.length; i++) {
    String taskName = SchedulerUtilities.getClassKey(tasks[i].getClassName());
    if(taskName == null) {
        if(tasks[i].getClassName() != null) {
            int dotIndex = tasks[i].getClassName().lastIndexOf(".");
            taskName = (dotIndex > -1 && (dotIndex + 1) < tasks[i].getClassName().length() ? tasks[i].getClassName().substring(dotIndex + 1) : tasks[i].getClassName());
        } else {
            taskName = ITrackerResources.getString("itracker.web.generic.unassigned");
        }
    } else {
        taskName = ITrackerResources.getString(taskName, (java.util.Locale)pageContext.getAttribute("currLocale"));
    }

    if(i % 2 == 1) {
--%>
      <tr class="listRowShaded">
<%--  } else { --%>
      <tr class="listRowUnshaded">
<%--  } --%>
      <td>
        <%--<it:formatImageAction action="removetask" paramName="id" paramValue="<%= tasks[i].getId() %>" src="/themes/defaulttheme/images/delete.gif" altKey="itracker.web.image.delete.task.alt" textActionKey="itracker.web.image.delete.texttag"/>--%>
        <%--<it:formatImageAction action="edittaskform" paramName="id" paramValue="<%= tasks[i].getId() %>" targetAction="update" src="/themes/defaulttheme/images/edit.gif" altKey="itracker.web.image.edit.task.alt" textActionKey="itracker.web.image.edit.texttag"/>--%>
      </td>
      <td></td>
      <td align="left"><%-- = taskName --%></td>
      <td align="left"><%-- = (tasks[i].isAll(tasks[i].getMonths()) ? "*" : tasks[i].joinString(tasks[i].getMonths())) --%></td>
      <td align="left"><%-- = (tasks[i].isAll(tasks[i].getDaysOfMonth()) ? "*" : tasks[i].joinString(tasks[i].getDaysOfMonth())) --%></td>
      <td align="left"><%-- = (tasks[i].isAll(tasks[i].getHours()) ? "*" : tasks[i].joinString(tasks[i].getHours())) --%></td>
      <td align="left"><%-- = (tasks[i].isAll(tasks[i].getMinutes()) ? "*" : tasks[i].joinString(tasks[i].getMinutes())) --%></td>
      <td align="left"><%-- = (tasks[i].isAll(tasks[i].getWeekdays()) ? "*" : tasks[i].joinString(tasks[i].getWeekdays())) --%></td>
      <td align="right"><%--<it:formatDate date="<%= tasks[i].getLastRun() %>" format="full"/>--%></td>
    </tr>
<%--
  }
  if(tasks.length == 0) {
--%>
    <tr><td colspan="9" class="listRowText" align="left"><it:message key="itracker.web.error.notasks"/></td></tr>
<%--
  }
--%>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
