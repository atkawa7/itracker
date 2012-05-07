<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<table border="0" cellspacing="1" cellspacing="0" width="100%">
    <tr>
        <td class="headerText"></td>
        <td class="headerTextPageTitle">
            <h1>
                <c:set var="pageTitle"><it:message key="${pageTitleKey}" arg0="${pageTitleArg}"/></c:set>
                <c:if test="${empty pageTitleKey or empty pageTitle}">
                    <c:set var="pageTitle"><tiles:getAsString name="title" ignore="false"/></c:set>
                </c:if>
                <c:out value="${pageTitle}"/>
            </h1>
        </td>
        <td class="headerTextWelcome"><it:message
                key="itracker.web.header.welcome"/> <c:choose>
            <c:when test="${ currUser != null}">${ currUser.firstName } ${ currUser.lastName } (<em>${
            currUser.login }</em>)</c:when>
            <c:otherwise>
                <em><it:message key="itracker.web.header.guest"/></em>
            </c:otherwise>
        </c:choose></td>
    </tr>
    <tr>
        <td colspan="3" class="top_ruler">
            <hr/>
        </td>
    </tr>
</table>
<table border="0" cellspacing="0" cellspacing="0" width="100%">
    <tr>
        <td class="headerLinks" align="left"><c:if
                test="${currUser != null}">

            <form name="lookupForm"
                  action="<html:rewrite module="/module-projects" forward="viewissue"/>">
                <input type="text" name="id" size="5" class="lookupBox"
                       onchange="document.lookupForm.submit();"></form>
        </c:if></td>
        <td class="headerLinks" align="right"><c:if
                test="${currUser != null}">

            <html:link styleClass="headerLinks"
                       titleKey="itracker.web.header.menu.home.alt" module="/"
                       action="/portalhome">
                <it:message key="itracker.web.header.menu.home"/>
            </html:link>

            | <html:link linkName="listprojects" styleClass="headerLinks"
                         titleKey="itracker.web.header.menu.projectlist.alt"
                         module="/module-projects" action="/list_projects">
            <it:message key="itracker.web.header.menu.projectlist"/>
        </html:link>
            | <html:link forward="searchissues" module="/module-searchissues"
                         styleClass="headerLinks"
                         titleKey="itracker.web.header.menu.search.alt">
            <it:message key="itracker.web.header.menu.search"/>
        </html:link>
            <%-- TODO: fix reports-section
                   <c:if
                       test="<%=UserUtilities.hasPermission(permissions,
                                           UserUtilities.PERMISSION_VIEW_ALL)%>">

                       | <html:link styleClass="headerLinks"
                           titleKey="itracker.web.header.menu.reports.alt"
                           module="/module-reports" action="/list_reports" >
                           <it:message key="itracker.web.header.menu.reports" />
                       </html:link>
                   </c:if>
                   --%>
            <c:if test="${hasPermissionUserAdmin}">
                |
                <html:link styleClass="headerLinks"
                           titleKey="itracker.web.header.menu.admin.alt"
                           module="/module-admin" action="/adminhome">
                    <it:message key="itracker.web.header.menu.admin"/>
                </html:link>
            </c:if>
            <c:if test="${hasPermissionProductAdmin}">
                | <html:link styleClass="headerLinks"
                             titleKey="itracker.web.header.menu.projectadmin.alt"
                             module="/module-admin" action="/listprojectsadmin">
                <it:message key="itracker.web.header.menu.projectadmin"/>
            </html:link>
            </c:if>


            | <html:link module="/module-preferences"
                         forward="editpreferences" styleClass="headerLinks"
                         titleKey="itracker.web.header.menu.preferences.alt">
            <it:message key="itracker.web.header.menu.preferences"/>
        </html:link>
            | <html:link forward="help" styleClass="headerLinks"
                         titleKey="itracker.web.header.menu.help.alt" module="/module-help">
            <it:message key="itracker.web.header.menu.help"/>
        </html:link>
            | <html:link linkName="logoff" action="/logoff"
                         styleClass="headerLinks"
                         titleKey="itracker.web.header.menu.logout.alt" module="/">
            <it:message key="itracker.web.header.menu.logout"/>
        </html:link>
        </c:if> <c:if test="${currUser == null}">

            <c:if test="${fn:length(locales) gt 1}">
                <div class="locales"><c:forEach items="${locales}" var="locMap">
                    <span> | <a href="?loc=${locMap.key}" class="${locMap.key}_loc">${locMap.key}</a></span>
                    <c:forEach items="${locMap.value}" var="loc"> <span> | <a href="?loc=${loc}"
                                                                              class="${loc}_loc">${loc}</a></span></c:forEach>
                </c:forEach></div>
            </c:if>
            <%--<c:if test="${locales and (fn:length(locales) gt 1)}">
            <div id="locales"><c:forEach items="${locales}" var="locMap">
               <span><a href="?loc=${locMap.key}" class="${locMap.key}_loc">${locMap.key}</a><c:forEach items="${locMap.value}" var="loc"> <a href="?loc=${loc}" class="${loc}_loc">${loc}</a></c:forEach> </span>


            </c:forEach>
            </div>
            </c:if--%>


            <%-- TODO: localization separated from page title? --%>
            <html:link linkName="index" forward="index"
                       styleClass="headerLinks"
                       titleKey="itracker.web.login.title">
                <it:message key="itracker.web.login.title"/>
            </html:link>
            <c:if test="${allowForgotPassword}">
                | <html:link linkName="forgotpassword" forward="forgotpassword"
                             styleClass="headerLinks"
                             titleKey="itracker.web.header.menu.forgotpass.alt">
                <it:message key="itracker.web.header.menu.forgotpass"/>
            </html:link>
            </c:if>
             <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/>
            <c:if test="${allowSelfRegister}">
                | <html:link forward="selfregistration"
                             styleClass="headerLinks"
                             titleKey="itracker.web.header.menu.selfreg.alt">
                <it:message key="itracker.web.header.menu.selfreg"/>
            </html:link>
            </c:if>
        </c:if></td>
    </tr>
</table>


<logic:messagesPresent>

    <div id="pageErrors" class="formError">
        <html:messages id="error">
           <div><bean:write name="error"/></div>
        </html:messages>
    </div>

</logic:messagesPresent>