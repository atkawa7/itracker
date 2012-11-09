<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/common/taglibs.jsp"%>


<c:set var="themePath">/themes/defaulttheme</c:set>
<c:if test="${ empty siteTitle}">
    <c:set var="siteTitle" value="itracker.org" />
</c:if>
<c:set var="pageTitle">
    <c:choose>
        <c:when test="${empty pageTitleKey}"><tiles:getAsString name="title" ignore="false"/>
        </c:when>
        <c:otherwise><it:message
                	key="${pageTitleKey}" arg0="${pageTitleArg}" />
        </c:otherwise>
    </c:choose>
</c:set>

<c:set var="pageTitleFull">
    <c:out value="${ siteTitle }: ${ pageTitle }" />
</c:set>

<html>
<head>

    <title><c:out value="${ pageTitle }" /></title>
    <link rel="STYLESHEET" type="text/css"
          href="${contextPath}${themePath}/includes/styles.css"/>
    <c:if test="${not empty rssFeed}">
        <link href="${contextPath}${rssFeed}" rel="alternate" type="application/rss+xml" title="${pageTitle} RSS" />
    </c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <!-- script type="text/javascript"
	src="${contextPath}/themes/defaulttheme/includes/calendar.js"></script -->

    <!-- Include calendar resources -->
    <script src="${contextPath}${themePath}/includes/calendar/javascripts/prototype.js"
            type="text/javascript"></script>
    <script src="${contextPath}${themePath}/includes/calendar/javascripts/effects.js"
            type="text/javascript"></script>
    <script src="${contextPath}${themePath}/includes/calendar/javascripts/scal.js"
            type="text/javascript"></script>
    <link rel="stylesheet" href="${contextPath}${themePath}/includes/calendar/styles/scaltables.css"
          type="text/css" media="screen"/>

    <script type="text/javascript"
            src="${contextPath}${themePath}/includes/scripts.js"></script>

</head>
<body>

<tiles:useAttribute name="isErrorPage" id="isErrorPage" ignore="true" ></tiles:useAttribute>
<logic:empty name="isErrorPage"><c:set var="isErrorPage" value="false" />
</logic:empty>
<tiles:insert attribute="header" flush="false">
    <tiles:put name="title" beanName="pageTitle" />
    <tiles:put name="isErrorPage" beanName="isErrorPage" />
</tiles:insert>

<tiles:insert attribute="body" />

<tiles:useAttribute name="footer" id="footerPath" ></tiles:useAttribute>
<tiles:insert attribute="footer" />
</body>

</html>

