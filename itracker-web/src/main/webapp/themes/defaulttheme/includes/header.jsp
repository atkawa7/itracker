<%@ include file="/common/taglibs.jsp" %>

<c:if test="${ empty siteTitle}">
    <c:set var="siteTitle" value="itracker.org" />
</c:if>

<html>
<head>
<title><c:out value="${ siteTitle }" />: <it:message
	key="${pageTitleKey}" arg0="${pageTitleArg}" /></title>
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/themes/defaulttheme/includes/styles.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- script type="text/javascript"
	src="${contextPath}/themes/defaulttheme/includes/calendar.js"></script -->

<!-- Include calendar resources -->
    <script src="${contextPath}/themes/defaulttheme/includes/calendar/javascripts/prototype.js" type="text/javascript"></script>
    <script src="${contextPath}/themes/defaulttheme/includes/calendar/javascripts/effects.js" type="text/javascript"></script>
    <script src="${contextPath}/themes/defaulttheme/includes/calendar/javascripts/scal.js" type="text/javascript"></script>
    <link rel="stylesheet" href="${contextPath}/themes/defaulttheme/includes/calendar/styles/scaltables.css" type="text/css" media="screen"/>

<script type="text/javascript"
	src="${contextPath}/themes/defaulttheme/includes/scripts.js"></script>
</head>

<body>

<table border="0" cellspacing="1" cellspacing="0" width="100%">
	<tr>
		<td class="headerText">
            <c:choose>
                <c:when test="${ not empty siteLogo }">
                    <html:img title="${ siteTitle }"  src="${ siteLogo }"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${ siteTitle }" />
                </c:otherwise>
            </c:choose>
        </td>
		<td class="headerTextPageTitle">
		<h1><it:message key="${ pageTitleKey }" arg0="${ pageTitleArg }" /></h1>
		</td>
        <td class="headerTextWelcome"><it:message
                key="itracker.web.header.welcome"/>
            <c:choose>
                 <c:when test="${ currUser != null}">
                     <html:link module="/module-preferences"
                         forward="editpreferences" styleClass="headerTextWelcome"
                         title="${currUser.login}">
                         ${ currUser.firstName } ${ currUser.lastName }</html:link>
                </c:when>
                <c:otherwise>
                    <em><it:message key="itracker.web.header.guest"/></em>
                </c:otherwise>
            </c:choose>
        </td>
	</tr>
	<tr>
		<td colspan="3" class="top_ruler">
            <hr/>
		</td>
	</tr>
</table>
<table border="0" cellspacing="0" cellspacing="0" width="100%">
	<tr>
        <td class="headerLinks" align="left">
            <c:if
			test="${currUser != null}">

			<form name="lookupForm"
				action="<html:rewrite module="/module-projects" forward="viewissue"/>">
			<input type="text" name="id" size="5" class="lookupBox"
				onchange="document.lookupForm.submit();"></form>
            </c:if>
        </td>
		<td class="headerLinks" align="right"><c:if
			test="${currUser != null}">

			<html:link styleClass="headerLinks"
				titleKey="itracker.web.header.menu.home.alt" module="/"
				action="/portalhome">
				<it:message key="itracker.web.header.menu.home" />
			</html:link>	
	             		
				 | <html:link linkName="listprojects" styleClass="headerLinks"
				titleKey="itracker.web.header.menu.projectlist.alt"
				module="/module-projects" action="/list_projects">
				<it:message key="itracker.web.header.menu.projectlist" />
			</html:link> 
				| <html:link forward="searchissues" module="/module-searchissues"
				styleClass="headerLinks"
				titleKey="itracker.web.header.menu.search.alt">
				<it:message key="itracker.web.header.menu.search" />
			</html:link>
            <c:if test="${hasPermissionViewAll}">
                | <html:link styleClass="headerLinks"
                    titleKey="itracker.web.header.menu.reports.alt"
                    module="/module-reports" action="/list_reports" >
                    <it:message key="itracker.web.header.menu.reports" />
                </html:link>
            </c:if>
			<c:if test="${hasPermissionUserAdmin}">
                | <html:link styleClass="headerLinks"
					titleKey="itracker.web.header.menu.admin.alt"
					module="/module-admin" action="/adminhome">
					<it:message key="itracker.web.header.menu.admin" />
				</html:link>
			</c:if>
			<c:if test="${hasPermissionProductAdmin}">
	            | <html:link styleClass="headerLinks" linkName="projectadmin"
					titleKey="itracker.web.header.menu.projectadmin.alt"
					module="/module-admin" action="/listprojectsadmin">
					<it:message key="itracker.web.header.menu.projectadmin" />
				</html:link>
			</c:if>
	           
	            | <html:link module="/module-preferences"
				forward="editpreferences" styleClass="headerLinks"
				titleKey="itracker.web.header.menu.preferences.alt">
				<it:message key="itracker.web.header.menu.preferences" />
			</html:link>

	            | <html:link forward="help" styleClass="headerLinks"
				titleKey="itracker.web.header.menu.help.alt" module="/module-help">
				<it:message key="itracker.web.header.menu.help" />
			</html:link>

                | <html:link linkName="logoff" action="/logoff"
				styleClass="headerLinks"
				titleKey="itracker.web.header.menu.logout.alt" module="/">
				<it:message key="itracker.web.header.menu.logout" />
			</html:link>
		</c:if> <c:if test="${currUser == null}">

            <c:if test="${fn:length(locales) gt 1}">
                <div class="locales"><c:forEach items="${locales}" var="locMap">
                    <span> | <a href="?loc=${locMap.key}" class="${locMap.key}_loc">${locMap.key}</a></span>
                    <c:forEach items="${locMap.value}" var="loc"> <span> | <a href="?loc=${loc}" class="${loc}_loc">${loc}</a></span></c:forEach>
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
                <it:message key="itracker.web.login.title" />
            </html:link>
			<c:if test="${allowForgotPassword}">
				       | <html:link linkName="forgotpassword" forward="forgotpassword"
					styleClass="headerLinks"
					titleKey="itracker.web.header.menu.forgotpass.alt">
					<it:message key="itracker.web.header.menu.forgotpass" />
				</html:link>
			</c:if>
			<%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
			<c:if test="${allowSelfRegister}">      
	                   | <html:link forward="selfregistration"
					styleClass="headerLinks"
					titleKey="itracker.web.header.menu.selfreg.alt">
					<it:message key="itracker.web.header.menu.selfreg" />
				</html:link>
			</c:if>
		</c:if></td>
	</tr>
</table>

<p class="pageHeader"><%-- TODO: temp. removed code, fix this: <it:message key="<%= pageTitleKey %>" arg0="<%= pageTitleArg %>"/>--%></p>