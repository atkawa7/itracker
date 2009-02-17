<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.selfreg.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
<%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
<c:choose>
	<c:when test="${! allowSelfRegister}">
	   <center><span style="color: red;"><it:message key="itracker.web.error.notenabled"/></span></center>
	</c:when>
	<c:otherwise>
	
      <html:javascript formName="selfRegistrationForm"/>

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

      <html:form action="/selfregister" focus="login" onsubmit="return validateSelfRegistrationForm();">
        <html:hidden property="action" value="register"/>
        <table border="0" cellspacing="0"  cellpadding="2" align="left">
          <tr>
            <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
            <td align="left"><html:text property="login" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.password"/>:</td>
            <td align="left"><html:password property="password" styleClass="editColumnText" redisplay="false"/></td>
          </tr>
          <tr>
            <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.confpassword"/>:</td>
            <td align="left"><html:password property="confPassword" styleClass="editColumnText" redisplay="false"/></td>
          </tr>
          <tr>
            <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.firstname"/>:</td>
            <td align="left"><html:text property="firstName" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.lastname"/>:</td>
            <td align="left"><html:text property="lastName" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.email"/>:</td>
            <td align="left"><html:text property="email" styleClass="editColumnText"/></td>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>
        </table>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%"align="left">
          <tr><td align="left"><html:submit styleClass="button" altKey="itracker.web.button.submit.alt" titleKey="itracker.web.button.submit.alt"><it:message key="itracker.web.button.submit"/></html:submit></td></tr>
        </table>
      </html:form>
	</c:otherwise>
</c:choose>

   
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
