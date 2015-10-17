<%@ include file="/common/taglibs.jsp"%>

<%-- <nitrox:var name="action" type="java.lang.String"/> --%>
<!-- bean:define id="action" name="action" type="java.lang.String" scope="request"/ -->

<bean:define id="pageTitleKey" value="itracker.web.admin.editcustomfieldvalue.title.create"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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

<html:form action="/editcustomfieldvalue">
    <html:hidden property="action"/>
    <html:hidden property="id"/>
    <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
        
        <tr>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
        </tr>
        <tr>
            <td colspan="2"><span class="editColumnTitle"><it:message key="itracker.web.attr.value"/>:</span> <html:text property="value" styleClass="editColumnText"/></td>
            <td colspan="2"><span class="editColumnTitle"><it:message key="itracker.web.attr.sortorder"/>:</span> <html:text property="sortOrder" styleClass="editColumnText"/></td>
        </tr>
        
        <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
        <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.translations"/>:</td></tr>
        <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
        <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="4"/></td></tr>
        <tr class="listRowUnshaded">
            <td colspan="3">
                <it:message key="itracker.web.attr.baselocale"/>
            </td>
            <td>
               
            <html:text property="translations(${baseLocale})" styleClass="editColumnText"/></td>
            
        </tr>
       
        <c:forEach var="languageNameValue" items="${languagesNameValuePair}">
            <tr class="listRowUnshaded">
                <td></td>
                <td colspan="2">
                	${languageNameValue.key.value}
                </td>
                <td>
                <html:text property="translations(${languageNameValue.key.name })" styleClass="editColumnText"/></td>

            </tr>
            
            <c:forEach var="locale" items="${languageNameValue.value}">
                  <tr class="listRowUnshaded">
                    <td></td>
                    <td></td>
                    <td>
                        ${locale.value }
                    </td>
                    <td>
                    <html:text property="translations(${locale.name})" styleClass="editColumnText"/></td>

                </tr>
        
        </c:forEach>
        </c:forEach>
        <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
        <%-- <nitrox:var name="isUpdate" type="java.lang.Boolean"/> --%>
        <c:choose>
            <c:when test="${action == 'update'}">
                <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
            </c:when>
            <c:otherwise>
                <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
            </c:otherwise>
        </c:choose>
        
        
    </table>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
