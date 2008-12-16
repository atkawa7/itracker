<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
<html:form action="/editworkflowscript">
    <html:hidden property="action"/>
    <html:hidden property="id"/>

    <table border="0" cellspacing="0" cellspacing="1" width="100%">
        <tr>
            <td colspan="2" width="48%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
            <td colspan="2" width="48%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
        </tr>

        <c:if test="${action == 'update'}">
            <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.id"/>:</td>
                <td class="editColumnText">${workflowscript.id}</td>
            </tr>
        </c:if>

        <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.name"/>:</td>
            <td class="editColumnText"><html:text property="name" size="40" styleClass="editColumnText"/></td>
            <td></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="${workflowscript.createDate}"/></td>
        </tr>
        <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.event"/>:</td>
            <td>
                <html:select property="event" styleClass="editColumnText">
                    <c:forEach items="${nameValuePair}" var="pair">
                        <html:option value="${pair.value}" styleClass="editColumnText">${pair.name}</html:option>
                    </c:forEach>
                    NameValuePair[] eventTypes = WorkflowUtilities.getEvents((java.util.Locale)pageContext.getAttribute("currLocale")); %>
                </html:select>
            </td>
            <td></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="${workflowscript.lastModifiedDate}"/></td>
        </tr>
        <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
        <tr>
            <td class="editColumnTitle" colspan="5"><it:message key="itracker.web.attr.script"/>:</td>
        </tr>
        <tr>
            <td class="editColumnText" colspan="5">
                <html:textarea rows="20" cols="120" property="script" styleClass="editColumnText"/>
            </td>
        </tr>
        <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
        <c:choose>
            <c:when test="${action == 'update'}">
                <tr><td colspan="5" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
            </c:when>
            <c:otherwise>
                <tr><td colspan="5" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
            </c:otherwise>
        </c:choose>
    </table>
</html:form>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
