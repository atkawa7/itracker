<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<center><span class="pageHeader"><u><it:message key="itracker.web.helpabout.title"/></u></span></center>
<br/>
<br/>
<p class="help">
  <b><it:message key="itracker.web.helpabout.itrackerversion"/>:</b> <bean:write name="version"/><br/>
  <b><it:message key="itracker.web.helpabout.starttime"/>:</b> <bean:write name="starttime"/><br/>
  <b><it:message key="itracker.web.helpabout.defaultlocale"/>:</b> <it:message key="itracker.locale.name"/><br/>
  <br/>

  <b><it:message key="itracker.web.helpabout.javaversion"/>:</b> <bean:write name="javaVersion" scope="request"/>, <bean:write name="javaVendor" scope="request"/><br/>
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
