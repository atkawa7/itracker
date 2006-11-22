<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<%-- <it:checkLogin/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->

<!-- <html>
  <head>
    <title>ITracker: <it:message key="itracker.web.showhelp.title"/></title>
   <link rel="STYLESHEET" type="text/css" href="/themes/defaulttheme/includes/styles.css"/>
	  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT">
    <meta http-equiv="Pragma" content="no-cache">
  </head>
-->
  <body onload="javascript:self.focus()"> 
  
  <tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
    <table border="0" cellspacing="1"  cellspacing="0"  width="100%">
      <tr>
        <td>itracker.org</td>
        <td align="right" valign="bottom" class="headerText"><it:message key="itracker.web.showhelp.title"/></td>
      <tr>
      <tr><td colspan="2" class="listHeadingBackground"><html:img  module="/" page="/themes/defaulttheme/images/blank.gif" height="2"/></td></tr>
    </table>
    <br/>
    <br/>

<!-- Now include the appropriate help file -->
<jsp:include page="${helpPage}" flush="true" />

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
</body>
