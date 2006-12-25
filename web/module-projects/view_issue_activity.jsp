<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.services.util.IssueUtilities" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO : move redirect logic to the Action class. 
    final Map<Integer, Set<PermissionType>> permissions = 
        RequestHelper.getUserPermissions(session);
    User um = RequestHelper.getCurrentUser(session);
        
    IssueService ih = (IssueService)request.getAttribute("ih");
    
    Integer issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
    Integer currUserId = um.getId();

    List<IssueActivity> activity = ih.getIssueActivity(issueId);
    Project project = ih.getIssueProject(issueId);

    User owner = ih.getIssueOwner(issueId);
    User creator = ih.getIssueCreator(issueId);

    if(project == null ||
        (! UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_VIEW_ALL) &&
            ! (UserUtilities.hasPermission(permissions, project.getId(), UserUtilities.PERMISSION_VIEW_USERS) &&
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<%      if(activity == null || activity.size() == 0) { %>
            <center><span style="color: red;"><it:message key="itracker.web.error.noactivity"/></span></center>
<%      } else { %>
            <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
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
