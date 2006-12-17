<%@ page language="java" contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
