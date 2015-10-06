<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.unauthorized.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<center><span style="color: red;"><it:message key="itracker.web.error.unauthorized"/></span></center>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
