<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.editconfiguration.title.create"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
    <tiles:put name="errorHide" value="${ false }" />
</tiles:insert>

<html:form action="/editconfiguration">
    <html:hidden property="action"/>
    <html:hidden property="id"/>
    <table border="0" cellspacing="0" cellpadding="1" width="100%" class="shadeList">
        <tr>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
            <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
        </tr>

        <c:if test="${ configurationForm.action=='createstatus' || (configurationForm.action == 'update'
    && not empty configurationForm.value) }">
            <tr>
                <td colspan="4">
             <span class="editColumnTitle">
              <c:choose>
                  <c:when test="${ configurationForm.action=='createstatus' }"><it:message
                          key="itracker.web.attr.status"/></c:when>
                  <c:otherwise><it:message key="${configurationForm.typeKey}"/></c:otherwise>
              </c:choose>

              <it:message key="itracker.web.attr.value"/>:
              <c:choose>
                  <c:when test="${ configurationForm.action=='createstatus' }"><html:text property="value"
                                                                                          styleClass="editColumnText"/></c:when>
                  <c:otherwise>${configurationForm.value}</c:otherwise>
              </c:choose>
            </span>
                </td>
            </tr>
        </c:if>
        <tr>
            <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1"
                                      height="12"/></td>
        </tr>
        <tr>
            <td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.translations"/>:</td>
        </tr>
        <tr class="listHeading">
            <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2"
                                      width="1"/></td>
        </tr>
        <tr>
            <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1"
                                      height="4"/></td>
        </tr>
        <tr class="listRowShaded">
            <td colspan="3">
                <label for="BASE">
                    <it:message key="itracker.web.attr.baselocale"/>
                    (<it:link action="editlanguageform" targetAction="update#${ configurationForm.key }"
                              paramName="locale" paramValue="BASE"
                              titleKey="itracker.web.admin.listlanguages.update.alt" arg0="BASE"
                              styleClass="pre">BASE</it:link>)
                </label>
            </td>
            <td>
                <c:set var="placeholder"><it:message key="${ configurationForm.key }" locale="BASE"/></c:set>
                <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                <html:text property="translations(BASE)" styleId="BASE" styleClass="editColumnText"/></td>

        </tr>
        <c:set var="i" value="0"/>
        <c:forEach var="languageNameValue" items="${configurationForm.languages}" varStatus="itStatus">
            <c:set var="listRowClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }"/>
            <c:set var="i" value="${ i + 1 }"/>

            <tr class="${listRowClass} level1">
                <td></td>
                <td colspan="2">
                    <label for="${languageNameValue.key.name}">
                            ${languageNameValue.key.value}
                        (<it:link action="editlanguageform" targetAction="update#${ configurationForm.key }"
                                  paramName="locale" paramValue="${ languageNameValue.key.name }"
                                  titleKey="itracker.web.admin.listlanguages.update.alt"
                                  arg0="${ languageNameValue.key.name }"
                                  styleClass="pre">${languageNameValue.key.name}</it:link>)
                    </label>
                </td>
                <td>
                    <c:set var="placeholder"><it:message key="${ configurationForm.key }" locale="${languageNameValue.key.name}"/></c:set>
                    <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                    <html:text property="translations(${languageNameValue.key.name})"
                               styleId="${languageNameValue.key.name}" styleClass="editColumnText"/></td>

            </tr>
            <c:forEach var="locale" items="${languageNameValue.value }" varStatus="itLStatus">
                <c:set var="listRowClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }"/>
                <c:set var="i" value="${ i + 1 }"/>

                <tr class="${listRowClass} level2">
                    <td></td>
                    <td></td>
                    <td>
                        <label for="${locale.name}">
                                ${locale.value}
                            (<it:link action="editlanguageform" targetAction="update#${ configurationForm.key }"
                                      paramName="locale" paramValue="${ locale.name }"
                                      titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ locale.name }"
                                      styleClass="pre">${locale.name}</it:link>)
                        </label>
                    </td>
                    <td>
                        <c:set var="placeholder"><it:message key="${ configurationForm.key }" locale="${locale.name}"/></c:set>
                        <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                        <html:text property="translations(${locale.name})" styleId="${locale.name}"
                                   styleClass="editColumnText"/></td>

                </tr>
            </c:forEach>
        </c:forEach>
        <tr>
            <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1"
                                      height="12"/></td>
        </tr>
        <c:choose>
            <c:when test="${configurationForm.action == 'update'}">
                <tr>
                    <td colspan="4" align="left"><html:submit styleClass="button"
                                                              altKey="itracker.web.button.update.alt"
                                                              titleKey="itracker.web.button.update.alt"><it:message
                            key="itracker.web.button.update"/></html:submit></td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr>
                    <td colspan="4" align="left"><html:submit styleClass="button"
                                                              altKey="itracker.web.button.create.alt"
                                                              titleKey="itracker.web.button.create.alt"><it:message
                            key="itracker.web.button.create"/></html:submit></td>
                </tr>
            </c:otherwise>
        </c:choose>
    </table>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
