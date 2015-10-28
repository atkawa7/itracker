<%@ include file="/common/taglibs.jsp"%>


<c:set var="isUpdate" value="${ languageForm.action == 'update' }" />
<c:set var="localeType" value="${editlangtype}" />

<bean:parameter id="action" name="action"/>
<bean:parameter id="locale" name="locale"/>
<bean:parameter id="parentLocale" name="parentLocale" value="BASE"/>

<c:choose>
    <c:when test="${ isUpdate }">
        <c:set var="pageTitleKey" scope="request">itracker.web.admin.editlanguage.title.update</c:set>
    </c:when>
    <c:otherwise>
        <c:set var="pageTitleKey" scope="request">itracker.web.admin.editlanguage.title.create</c:set>
    </c:otherwise>
</c:choose>
<c:set var="pageTitleArg" value="${ languageForm.localeTitle }" scope="request" />

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
    <tiles:put name="errorHide" value="${ false }" />
</tiles:insert>

      <html:form method="post" action="/editlanguage">
        <html:hidden property="action"/>
        <html:hidden property="parentLocale"/>

        <c:if test="${ isUpdate }" >
        	<html:hidden property="locale"/>
        </c:if>

       <table border="0" cellspacing="0"  cellpadding="1"  width="100%" class="shadeList">
          <c:set var="maxLength" value="2" />
          <c:set var="maxSize" value="2" />
          <c:set var="locMsg" value="itracker.web.attr.language" />
          <c:set var="afterTd" value="<td> </td>" />
          <c:set var="beforeTd" value="" />

           <c:set var="locHead"><it:message key="itracker.web.attr.baselocale"/></c:set>
          <c:set var="readOnly" value="false" />
          <c:if test="${ isUpdate }" >
            <c:set var="readOnly" value="true" />
          </c:if>
           <c:if test="${ localeType == 2 }" >
               <c:set var="beforeTd" value="<td>${locHead}</td>" />
           </c:if>
          <%-- if localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE --%>
          <c:if test="${ localeType == 3}" >
            <c:set var="afterTd" value="" />

              <c:set var="locHead2"><it:message key="${locMsg}"/> ( <c:out value="${ languageForm.parentLocale }" /> )</c:set>
            <c:set var="beforeTd" value="<td>${locHead}</td><td>${locHead2}</td>" />
            <c:set var="locMsg" value="itracker.web.attr.locale" />
            <c:set var="maxLength" value="5" />
            <c:set var="maxSize" value="5" />
          </c:if>

          <tr>
	          <td colspan="2">
	              <span class="editColumnTitle"><it:message key="itracker.web.attr.localeiso"/>: </span>
	              <html:text readonly="${ readOnly }" property="locale" styleClass="editColumnText" maxlength="${ maxLength }" size="${ maxSize }"/>
	          </td>
	          <td colspan="2">
	              <span class="editColumnTitle"><it:message key="itracker.web.attr.language"/>: </span>
	              <html:text property="localeTitle" styleClass="editColumnText" maxlength="20" size="20"/>
	          </td>
          </tr>
           <tr>
	          <td colspan="2">
	          </td>
	          <td colspan="2">
	              <span class="editColumnTitle"><it:message key="itracker.web.attr.baselocale"/>: </span>
	              <html:text property="localeBaseTitle" styleClass="editColumnText" maxlength="20" size="20"/>
	          </td>
          </tr>
          <tr align="left" class="listHeading">
            <td><it:message key="itracker.web.attr.key"/></td>
            ${ beforeTd }
            <td><it:message key="${ locMsg }"/></td>
            ${ afterTd }
          </tr>
          
          <c:set var="i" value="0" />
          
          <logic:iterate id="itemlangs" name="languageForm" property="items">
               <bean:define name="itemlangs" property="key" id="key" type="java.lang.String"/>
               <bean:define name="itemlangs" property="value" id="value" type="java.lang.String"/>
               
               <c:set var="propertyKey" value="items(${ key })" />

              <c:set var="isLongString" value="${ it:ITrackerResources_IsLongString(key) }" />
               <c:set var="styleClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded'}${ (isLongString?' long':'') }" />
               <c:set var="i" value="${ i + 1 }" />
               
               <c:if test="${ (key != 'itracker.locales') &&  (key != 'itracker.locale.name') }" >
                 <tr class="${ styleClass }" >
                  <td valign="top"><label for="${ key }"><code>${ key }</code></label></td>
                  <%-- localeType != SystemConfigurationUtilities.LOCALE_TYPE_BASE --%>
                  <c:if test="${ localeType > 1 }" >
                  	<td valign="top">
                        <c:set var="pl" value="BASE" />
                        <c:choose>
                            <c:when test="${ isLongString }">
                                <pre class="pre localization"><it:message key="${ key }" locale="BASE"/></pre>
                            </c:when>
                            <c:otherwise>
                                <code class="pre localization"><it:message key="${ key }" locale="BASE"/></code>
                            </c:otherwise>
                        </c:choose>
                    </td>
                  </c:if>
                  <%-- localeType != SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE --%>
                  <c:if test="${ localeType > 2 }">
                     <td valign="top">
                         <c:set var="pl" value="${languageForm.parentLocale}" />

                         <c:choose>
                             <c:when test="${ isLongString }">
                                 <pre class="pre localization localizationSub"><it:message key="${ key }" locale="${languageForm.parentLocale}"/></pre>
                             </c:when>
                             <c:otherwise>
                                 <code class="pre localization localizationSub"><it:message key="${ key }"
                                                               locale="${languageForm.parentLocale}"/></code>
                             </c:otherwise>
                         </c:choose>
                     </td>
                  </c:if>
                     <c:set var="lc" value="${isUpdate?languageForm.locale:pl}" />

                  <c:set var="loadedString"><it:message key="${key}" locale="${lc}"/></c:set>

                     <td valign="top">
                         <input type="hidden" name="placeholder" value="${fn:escapeXml(loadedString)}"/>
                         <c:choose>
                             <c:when test="${ isLongString }">
                                 <html:textarea indexed="false" name="languageForm" rows="6" cols="40"
                                                property="${ propertyKey }" value="${ value }"
                                                styleClass="${ styleClass }" styleId="${ key }"/>
                             </c:when>
                             <c:otherwise>
                                 <html:text indexed="false" name="languageForm" property="${ propertyKey }"
                                            value="${ value }" size="40" styleClass="${ styleClass }" styleId="${ key }"
                                            title=""/>
                             </c:otherwise>
                         </c:choose>
                     </td>
          		   ${ afterTd } 
                </tr>
               
               </c:if>
          </logic:iterate>
          
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
          <c:choose>
          	<c:when test="${ isUpdate }" >
          	  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
          	</c:when>
          	<c:otherwise>
       		  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>         	
          	</c:otherwise>
          </c:choose>
        
        </table>
      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

