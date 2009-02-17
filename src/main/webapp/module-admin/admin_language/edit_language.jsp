<%@ include file="/common/taglibs.jsp"%>

<c:set var="isUpdate" value="false" />
<c:set var="localeType" value="${editlangtype}" />

<bean:parameter id="action" name="action"/>
<bean:parameter id="locale" name="locale"/>
<bean:parameter id="parentLocale" name="parentLocale" value="BASE"/>
   
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

      <html:form method="post" action="/editlanguage">
        <html:hidden property="action"/>
        <html:hidden property="parentLocale"/>
        <c:if test="${ languageForm.action == 'update' }" >
        	<html:hidden property="locale"/>
        	<c:set var="isUpdate" value="true" />
        </c:if>

       <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
          <c:set var="maxLength" value="2" />
          <c:set var="maxSize" value="2" />
          <c:set var="locMsg" value="itracker.web.attr.language" />
          <c:set var="afterTd" value="<td> </td>" />
          <c:set var="beforeTd" value="" />
          <c:set var="readOnly" value="false" />
          <c:if test="${ isUpdate == 'true'}" >
            <c:set var="readOnly" value="true" />
          </c:if>
          
          <!-- if localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE -->
          <c:if test="${ localeType == 3}" >
            <c:set var="afterTd" value="" />
            <c:set var="beforeTd" value="<td> </td>" />
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
          <tr align="left" class="listHeading">
            <td><it:message key="itracker.web.attr.key"/></td>
            <td><it:message key="itracker.web.attr.baselocale"/></td>
            ${ beforeTd }
            <td><it:message key="${ locMsg }"/></td>
            ${ afterTd }
          </tr>
          
          <c:set var="i" value="0" />
          
          <logic:iterate id="itemlangs" name="languageForm" property="items">
               <bean:define name="itemlangs" property="key" id="key" type="java.lang.String"/>
               <bean:define name="itemlangs" property="value" id="value" type="java.lang.String"/>
               
               <c:set var="propertyKey" value="items(${ key })" />
               <c:set var="styleClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }" />
               <c:set var="i" value="${ i + 1 }" />
               
               <c:if test="${ (key != 'itracker.locales') &&  (key != 'itracker.locale.name') }" >
                 <tr class="${ styleClass }">
                  <td valign="top">${ key }</td>
                  <!-- localeType != SystemConfigurationUtilities.LOCALE_TYPE_BASE -->
                  <c:if test="${ localeType != 1 }" >
                  	<td valign="top"><it:message key="${ key }" locale="BASE"/></td>
                  </c:if>
                  <!-- localeType != SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE -->
                  <c:if test="${ localeType != 2 }" >
                  	<td valign="top"><it:message key="${ key }" locale="${languageForm.parentLocale}"/></td>
                  </c:if>
                  <c:choose>
		          	<c:when test="${ it:ITrackerResources_IsLongString(key) }" >
                    	<td valign="top"><html:textarea indexed="false" name="languageForm"  rows="4" cols="40" property="${ propertyKey }" value="${ value }" styleClass="${ styleClass }"/></td>
		          	</c:when>
		          	<c:otherwise>
                    	<td valign="top"><html:text indexed="false" name="languageForm" property="${ propertyKey }" value="${ value }" size="40" styleClass="${ styleClass }"/></td>
		          	</c:otherwise>
          		   </c:choose>
          		   ${ afterTd } 
                </tr>
               
               </c:if>
          </logic:iterate>
          
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
          <c:choose>
          	<c:when test="${ isUpdate == 'true' }" >
          	  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
          	</c:when>
          	<c:otherwise>
       		  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>         	
          	</c:otherwise>
          </c:choose>
        
        </table>
      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

