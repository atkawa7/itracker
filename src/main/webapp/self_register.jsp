<%@ include file="/common/taglibs.jsp" %>

<c:choose>
    <c:when test="${! allowSelfRegister}">
        <span style="color: red;"><it:message key="itracker.web.error.notenabled"/></span>
    </c:when>
    <c:otherwise>

        <html:form action="/selfregister" focus="login">
            <html:hidden property="action" value="register"/>
            <table border="0" cellspacing="0" cellpadding="2" align="left">
                <tr>
                    <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
                    <td align="left"><html:text property="login" styleClass="editColumnText"/></td>
                </tr>
                <tr>
                    <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.password"/>:</td>
                    <td align="left"><html:password property="password" styleClass="editColumnText"
                                                    redisplay="false"/></td>
                </tr>
                <tr>
                    <td align="right" class="editColumnTitle"><it:message key="itracker.web.attr.confpassword"/>:</td>
                    <td align="left"><html:password property="confPassword" styleClass="editColumnText"
                                                    redisplay="false"/></td>
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
            </table>

            <table border="0" cellspacing="0" cellspacing="1" width="100%" align="left">
                <tr>
                    <td align="left"><html:submit styleClass="button" altKey="itracker.web.button.submit.alt"
                                                  titleKey="itracker.web.button.submit.alt"><it:message
                            key="itracker.web.button.submit"/></html:submit></td>
                </tr>
            </table>
        </html:form>
    </c:otherwise>
</c:choose>