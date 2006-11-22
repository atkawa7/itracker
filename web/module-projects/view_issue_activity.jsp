<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
 
<%@ page import="java.util.List" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.IssueUtilities" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.model.*" %>

<%-- <it:checkLogin/> --%>
<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->

<%
    IssueService ih = (IssueService)request.getAttribute("ih");
    User um = (User)session.getAttribute("currUser");
    Integer issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
    Integer currUserId = um.getId();

    List<IssueActivity> activity = ih.getIssueActivity(issueId);
    Project project = ih.getIssueProject(issueId);

    User owner = ih.getIssueOwner(issueId);
    User creator = ih.getIssueCreator(issueId);

    if(project == null ||
        (! UserUtilities.hasPermission((java.util.HashMap)request.getSession().getAttribute("permissions"), project.getId(), UserUtilities.PERMISSION_VIEW_ALL) &&
            ! (UserUtilities.hasPermission((java.util.HashMap)request.getSession().getAttribute("permissions"), project.getId(), UserUtilities.PERMISSION_VIEW_USERS) &&
                  ((owner != null && owner.getId().equals(currUserId)) ||
                  (creator != null && creator.getId().equals(currUserId)))
              )
        )
      ) {
%>
        <logic:forward name="unauthorized"/>
<%  } else { %>
        <bean:define id="pageTitleKey" value="itracker.web.issueactivity.title"/>
        <bean:define id="pageTitleArg" value="<%= issueId.toString() %>"/>
   <%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<%      if(activity == null || activity.size() == 0) { %>
            <center><span style="color: red;"><it:message key="itracker.web.error.noactivity"/></span></center>
<%      } else { %>
            <table border="0" cellspacing="0"  cellspacing="1"  width="95%" align="left">
              <tr>
                <td class="editColumnTitle" colspan="7">Activity:</td>
              </tr>
              <tr align="left" class="listHeading">
                <td><it:message key="itracker.web.attr.date"/></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                <td><it:message key="itracker.web.attr.type"/></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                <td><it:message key="itracker.web.attr.description"/></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                <td><it:message key="itracker.web.attr.user"/></td>
              </tr>
				<!-- // TODO: we need a comparation strategy, we don't have one now... -->
              <%
              	// probably will be done on the action
				//Arrays.sort(activity, new IssueActivityModel());
                //Arrays.sort(activity);
%><%
                for(int i = 0; i < activity.size(); i++) {
              %>
                  <tr class="<%= (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded") %>" >
                    <td valign="top"><it:formatDate date="<%= activity.get(i).getCreateDate() %>"/></td>
                    <td></td>
                    <td valign="top"><%= IssueUtilities.getActivityName(activity.get(i).getType(), (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
                    <td></td>
                    <td valign="top"><%= activity.get(i).getDescription() %></td>
                    <td></td>
                    <td valign="top"><%= activity.get(i).getUser().getFirstName() + " " + activity.get(i).getUser().getLastName() %></td>
                  </tr>
             <% } %>
            </table>
<%      } %>
        <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>
