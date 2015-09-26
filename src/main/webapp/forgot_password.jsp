<%@ include file="/common/taglibs.jsp" %>

<c:choose>
    <c:when test="${! allowForgotPassword}">
        <span style="color: red;"><it:message key="itracker.web.error.notenabled"/></span>
    </c:when>
    <c:otherwise>

        <html:form action="/forgotpassword" focus="login" >

            <html:javascript formName="forgotPasswordForm" />


            <table border="0" cellpadding="2" cellspacing="0" align="left">
                <tr>
                    <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
                    <td align="left"><html:text property="login" styleClass="editColumnText"/></td>
                </tr>
                <tr>
                    <td align="left" class="editColumnTitle"><it:message key="itracker.web.attr.lastname"/>:</td>
                    <td align="left"><html:text property="lastName" styleClass="editColumnText"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="left"><html:submit styleClass="button"
                                                              altKey="itracker.web.button.submit.alt"
                                                              titleKey="itracker.web.button.submit.alt"><it:message
                            key="itracker.web.button.submit"/></html:submit></td>
                </tr>
            </table>

        </html:form>
    </c:otherwise>
</c:choose>
