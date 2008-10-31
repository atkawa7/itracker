<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.web.util.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
final Component component = (Component)session.getAttribute(Constants.COMPONENT_KEY);
final boolean isNew = component.isNew();
%>

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

<html:form action="/editcomponent">
    <html:hidden property="action"/>
    <html:hidden property="projectId"/>
    <html:hidden property="id"/>
    
    <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.name"/>:</td>
            <td><html:text property="name" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= component.getCreateDate() %>"/></td>
        </tr>
        <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td><html:text property="description" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= component.getLastModifiedDate() %>"/></td>
        </tr>
        <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
        <% if (isNew) { %>
        <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
        <% } else { %>
        <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
        <% } %>
    </table>
</html:form>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
