<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<%-- <it:checkLogin/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->

<bean:define id="pageTitleKey" value="itracker.web.error.title"/>
<bean:define id="pageTitleArg" value=""/>

<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>


<logic:messagesPresent>
  <center>
    <span class="formError">
     <html:messages id="error">
        <bean:write name="error"/><br/>
     </html:messages>
    </span>
  </center>
  <br>
</logic:messagesPresent>
<logic:messagesNotPresent>
  <center>
    <span class="formError">
        <it:message key="itracker.web.error.system"/><br/>
    </span>
  </center>
</logic:messagesNotPresent>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
