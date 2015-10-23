<%@ include file="/common/taglibs.jsp"%>

<c:set var="pageTitleKey" scope="request">itracker.web.admin.createlanguagekey.title</c:set>
<c:set var="pageTitleArg" value="" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:form action="/createlanguagekey">
  <html:hidden property="action" value="create"/>
  <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
    <tr>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
    </tr>
    <tr>
      <td colspan="4"><span class="editColumnTitle"><it:message key="itracker.web.attr.key"/>:</span> <html:text property="key" styleClass="editColumnText"/></td>
    </tr>
    <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
    <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.translations"/>:</td></tr>
    <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
    <tr class="listRowUnshaded">
      <td colspan="3" valign="top"><it:message key="itracker.web.attr.baselocale"/></td>
      <c:set var="localeKey" value="items(${baseLocale})" />
      <td>
        <html:textarea rows="2" cols="60" property="${ localeKey }" styleClass="editColumnText"/>
      </td>
    </tr>
    <c:set var="localeName" value="itracker.locale.name" />
    <c:forEach items="${ languageKeys }" var="language">
      <c:set var="locales" value="${ languages[language] }" />
      
      <tr class="listRowUnshaded">
	      <td>&nbsp;</td>
	      <td colspan="2" valign="top">
	      	${ it:ITrackerResources_GetString (localeName, language) }
	      </td>
	      <c:set var="localeKey" value="items(${language})" />
	      <td>
	        <html:textarea rows="2" cols="60" property="${localeKey}"  styleClass="editColumnText"/>
	      </td>
       </tr>
       <c:forEach items="${ locales }" var="locale">
       	<tr class="listRowUnshaded">
           <td>&nbsp;</td>
           <td>&nbsp;</td>
           <td valign="top">${ it:ITrackerResources_GetString (localeName, locale) }</td>
             <c:set var="localeKey" value="items(${locale})" />
           <td>
             <html:textarea rows="2" cols="60" property="${localeKey}" styleClass="editColumnText"/>
           </td>
         </tr>
       </c:forEach>
    </c:forEach>
       
    <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
  </table>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
