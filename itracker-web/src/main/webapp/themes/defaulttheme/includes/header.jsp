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
  <!-- inserted header -->
<tiles:insert template="default.header.jsp" >
	<tiles:put name="title"><it:message key="${ pageTitleKey }" arg0="${ pageTitleArg }" /></tiles:put>
  <tiles:put name="errorHide" value="${true}" />
</tiles:insert>
