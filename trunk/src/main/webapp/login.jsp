<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.login.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:form action="/login" focus="login" onsubmit="return validateLoginForm(this);">

  <input type="hidden" name="authtype" value="1">
    <%-- TODO is this AUTH_REDIRECT_KEY still needed? --%>
  <logic:notEmpty name="authredirect" scope="request">
      <input type="hidden" name="authredirect" value="<bean:write name="authredirect" scope="request"/>">
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

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
</html>

