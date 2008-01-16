<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.forgotpass.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<c:choose>
	<%-- <nitrox:var name="allowForgotPassword" type="java.lang.Boolean"/> --%>
	<c:when test="${! allowForgotPassword}">
	<center><span style="color: red;"><it:message key="itracker.web.error.notenabled"/></span></center>
	</c:when>
	<c:otherwise>

      <html:form action="/forgotpassword" focus="login" onsubmit="validateForgotPasswordForm(this);">

      <html:javascript formName="forgotPasswordForm"/>

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

      <table border="0" cellpadding="2" cellspacing="0"  align="left">
        <tr>
          <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.login"/>: </td>
          <td align="left"><html:text property="login" styleClass="editColumnText"/></td>
        </tr>
        <tr>
          <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.lastname"/>: </td>
          <td align="left"><html:text property="lastName" styleClass="editColumnText"/></td>
        </tr>
        <tr><td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="10"/></td></tr>
        <tr>
          <td colspan="2" align="left"><html:submit styleClass="button" altKey="itracker.web.button.submit.alt" titleKey="itracker.web.button.submit.alt"><it:message key="itracker.web.button.submit"/></html:submit></td>
        </tr>
      </table>

      </html:form>
	</c:otherwise>
</c:choose>


<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
