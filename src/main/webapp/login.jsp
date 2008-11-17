<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.services.util.*" %>
<%@ page import="org.itracker.web.util.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.login.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:form action="/login" focus="login" onsubmit="return validateLoginForm(this);">
  <input type="hidden" name="<%= Constants.AUTH_TYPE_KEY %>" value="<%= AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN %>">
  <%-- TODO is this AUTH_REDIRECT_KEY still needed? --%>
  <logic:notEmpty name="<%=Constants.AUTH_REDIRECT_KEY %>" scope="request">
      <input type="hidden" name="<%= Constants.AUTH_REDIRECT_KEY %>" value="<%= request.getAttribute(Constants.AUTH_REDIRECT_KEY) %>">
  </logic:notEmpty>

  <html:javascript formName="loginForm"/>

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
  <table border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.login" />:</td>
      <td align="left"><html:text property="login" styleClass="editColumnText"/></td>
    </tr>
    <tr>
      <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.password"/>:</td>
      <td align="left"><html:password property="password" styleClass="editColumnText" redisplay="false"/></td>
    </tr>
    <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td></tr>
    <tr>
      <td colspan="2" align="left"><html:submit styleClass="button" altKey="itracker.web.button.login.alt" titleKey="itracker.web.button.login.alt"><it:message key="itracker.web.button.login"/></html:submit></td>
    </tr>
  </table>
<br>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

