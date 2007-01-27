<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.itracker.services.*" %>
 
<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%! SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); %>

<%
    String versionNumber = "";
    long startTimeMillis = 0;

    ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");

    versionNumber = sc.getProperty("version", "Unknown");
    startTimeMillis = Long.parseLong(sc.getProperty("start_time_millis", ""));
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<center><span class="pageHeader"><u><it:message key="itracker.web.helpabout.title"/></u></span></center>
<br/>
<br/>
<p class="help">
  <b><it:message key="itracker.web.helpabout.itrackerversion"/>:</b> <%= versionNumber %><br/>
  <b><it:message key="itracker.web.helpabout.starttime"/>:</b> <%= sdf.format(new Date(startTimeMillis)) %><br/>
  <b><it:message key="itracker.web.helpabout.defaultlocale"/>:</b> <it:message key="itracker.locale.name"/><br/>
  <br/>

  <b><it:message key="itracker.web.helpabout.javaversion"/>:</b> <%= System.getProperty("java.version") + ", " + System.getProperty("java.vendor") %><br/>
  <br/>
</p>

<br/><br/>
<table cellspacing="0"  cellspacing="1"  border="0" width="100%" class="help">
  <tr>
    <td><b><it:message key="itracker.web.helpabout.createdby"/>:</b></td>
    <td align="left">Initial Code Donation: Jason Carroll (<a href="mailto:jcarroll@cowsultants.com">jcarroll@cowsultants.com</a>), and the itracker developer community...</td>
  </tr>
  <tr>
    <td></td>
    <td align="left"><it:message key="itracker.web.attr.copyright"/> 2002, 2003, 2004 by <a href="mailto:jcarroll@cowsultants.com">Jason Carroll</a>, donated it to public domain,
        	2005 by <a href="http://www.itracker.org" target="_blank">itracker.org Version
        	3.0, licensed under LGPL.</a></td>
  </tr>
</table>
