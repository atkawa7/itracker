<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>

    <title><it:message key="itracker.web.generic.itracker"/>: <it:message
            key="${pageTitleKey}" arg0="${pageTitleArg}"/></title>
    <link rel="STYLESHEET" type="text/css"
          href="${contextPath}/themes/defaulttheme/includes/styles.css"/>
    <c:if test="${not empty rssFeed}">
        <link href="${contextPath}${rssFeed}" rel="alternate" type="application/rss+xml" title="RSS" />
    </c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <!-- script type="text/javascript"
	src="${contextPath}/themes/defaulttheme/includes/calendar.js"></script -->

    <!-- Include calendar resources -->
    <script src="${contextPath}/themes/defaulttheme/includes/calendar/javascripts/prototype.js"
            type="text/javascript"></script>
    <script src="${contextPath}/themes/defaulttheme/includes/calendar/javascripts/effects.js"
            type="text/javascript"></script>
    <script src="${contextPath}/themes/defaulttheme/includes/calendar/javascripts/scal.js"
            type="text/javascript"></script>
    <link rel="stylesheet" href="${contextPath}/themes/defaulttheme/includes/calendar/styles/scaltables.css"
          type="text/css" media="screen"/>

    <script type="text/javascript"
            src="${contextPath}/themes/defaulttheme/includes/scripts.js"></script>

</head>
<body>
<%--<h1><tiles:getAsString name="title" ></tiles:getAsString></h1>--%>
<%--<h1><tiles:getAsString name="title"></tiles:getAsString></h1>--%>
<%--<c:set var="pageTitle" scope="request"><tiles:getAsString name="title"></tiles:getAsString></c:set>--%>

<tiles:insert attribute="header">
<tiles:put name="title" beanName="title" />
</tiles:insert>
<tiles:insert attribute="body"/>
<tiles:insert attribute="footer"/>
</body>

</html>

