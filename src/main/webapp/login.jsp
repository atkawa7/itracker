<%@ include file="/common/taglibs.jsp"%>
<html:form action="/login" focus="login" onsubmit="return validateLoginForm(this);">

  <input type="hidden" name="authtype" value="1">
    <%-- TODO is this AUTH_REDIRECT_KEY still needed? --%>
  <logic:notEmpty name="authredirect" scope="request">
      <input type="hidden" name="authredirect" value="<bean:write name="authredirect" scope="request" />">
  </logic:notEmpty>

  <html:javascript formName="loginForm"/>

  <table border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.login" />:</td>
      <td align="left"><html:text property="login" styleClass="editColumnText"/></td>
    </tr>
    <tr>
      <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.password"/>:</td>
      <td align="left"><html:password property="password" styleClass="editColumnText" redisplay="false"/></td>
    </tr>
    <tr>
      <td colspan="2" align="left"><html:submit styleClass="button" altKey="itracker.web.button.login.alt" titleKey="itracker.web.button.login.alt"><it:message key="itracker.web.button.login"/></html:submit></td>
    </tr>
  </table>
</html:form>