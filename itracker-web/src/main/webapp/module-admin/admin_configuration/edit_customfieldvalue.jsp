<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE HTML >
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
    <tiles:put name="errorHide" value="${ false }" />
</tiles:insert>

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
        <tr><td colspan="4"><table width="100%" cellspacing="0" cellpadding="1" border="0" class="shadeList">
            <tr class="listRowShaded">
                <td colspan="3">
                    <label for="BASE">
                    <it:message key="itracker.web.attr.baselocale"/>

                    (<it:link action="editlanguageform" targetAction="update#${ messageKey }"
                              paramName="locale" paramValue="BASE"
                              titleKey="itracker.web.admin.listlanguages.update.alt" arg0="BASE"
                              styleClass="pre">BASE</it:link>)
                    </label>
                </td>
                <td>
                    <c:if test="${ not empty messageKey }">
                        <c:set var="placeholder"><it:message key="${ messageKey }" locale="BASE"/></c:set>
                        <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}" />
                    </c:if>
                    <html:text property="translations(BASE)" styleClass="editColumnText" styleId="BASE"/>
                </td>

            </tr>
            <c:set var="i" value="0"/>
            <c:forEach var="languageNameValue" items="${languagesNameValuePair}">
                <c:set var="listRowClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }"/>
                <c:set var="i" value="${ i + 1 }"/>

                <tr class="${listRowClass}">
                    <td></td>
                    <td colspan="2">
                        <label for="${ languageNameValue.key.name }">
                            ${languageNameValue.key.value}
                            (<it:link action="editlanguageform" targetAction="update#${ messageKey }"
                                      paramName="locale" paramValue="${ languageNameValue.key.name }"
                                      titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ languageNameValue.key.name }"
                                      styleClass="pre">${ languageNameValue.key.name }</it:link>)
                        </label>
                    </td>
                    <td>
                        <c:if test="${ not empty messageKey }">
                            <c:set var="placeholder"><it:message key="${ messageKey }"
                                                                 locale="${languageNameValue.key.name}"/></c:set>
                            <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                        </c:if>
                        <html:text property="translations(${languageNameValue.key.name })" styleClass="editColumnText" styleId="${ languageNameValue.key.name }"/>
                    </td>

                </tr>

                <c:forEach var="locale" items="${languageNameValue.value}">
                    <c:set var="listRowClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }"/>
                    <c:set var="i" value="${ i + 1 }"/>

                    <tr class="${listRowClass} level1">
                        <td></td>
                        <td></td>
                        <td>
                            <label for="${ locale.name }">
                                ${locale.value }
                                    (<it:link action="editlanguageform" targetAction="update#${ messageKey }"
                                              paramName="locale" paramValue="${ locale.name }"
                                              titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ locale.name }"
                                              styleClass="pre">${ locale.name }</it:link>)
                            </label>
                        </td>
                        <td>
                            <c:if test="${ not empty messageKey }">
                                <c:set var="placeholder"><it:message key="${ messageKey }"
                                                                     locale="${locale.name}"/></c:set>
                                <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                            </c:if>
                            <html:text property="translations(${locale.name})" styleClass="editColumnText" styleId="${ locale.name }"/>
                        </td>

                    </tr>

                </c:forEach>
            </c:forEach>
        </table>
        </td>
        </tr>
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

