<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.web.util.*" %>

<%
final Version version = (Version)session.getAttribute(Constants.VERSION_KEY);
final boolean isNew = version.isNew();
%>
<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:form action="/editversion">
    <html:hidden property="action"/>
    <html:hidden property="projectId"/>
    <html:hidden property="id"/>
    
    <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.number"/>:</td>
            <td><html:text property="number" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= version.getCreateDate() %>"/></td>
        </tr>
        <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
            <td><html:text property="description" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="<%= version.getLastModifiedDate() %>"/></td>
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
