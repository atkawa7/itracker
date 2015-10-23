<%@ include file="/common/taglibs.jsp"%>

<c:set var="pageTitleKey" scope="request">itracker.web.admin.listlanguages.title</c:set>
<c:set var="pageTitleArg" value="" scope="request" />

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
      
      
<table border="0" cellspacing="0" cellpadding="1" width="100%" class="shadeList">
  <tr>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
  </tr>
  <tr class="listRowUnshaded">
    <td colspan="3">
        <it:message key="itracker.web.attr.baselocale"/>
    </td>
    <td align="right">
        <it:link action="editlanguageform" targetAction="create" paramName="locale" paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.create.alt" arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.create" arg0="${ baseLocaleName }"/></it:link>
        <it:link action="createlanguagekeyform" targetAction="create" titleKey="itracker.web.admin.listlanguages.createkey.alt"><it:message key="itracker.web.admin.listlanguages.createkey"/></it:link>
        <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.update" arg0="${ baseLocaleName }"/></it:link>
<!--        <it:link action="editlanguage" targetAction="disable" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.disable.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.disable"/></it:link>-->
        <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.export" arg0="${ baseLocaleName }"/></it:link>
    </td>
  </tr>
  <c:set var="key" value="itracker.locale.name" />

    <c:set var="i" value="0" />
  <c:forEach items="${ languageKeys }" var="language">

      <c:set var="styleClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }" />
      <c:set var="i" value="${ i + 1 }" />

  	<c:set var="locales" value="${ languages[language] }" />
  	<c:set var="languageName" value="${ it:ITrackerResources_GetString(key, language) }" />
  	<c:set var="keyL" >${ key }.${ language }</c:set>
    <c:set var="localizedName" value="${ it:ITrackerResources_GetString(keyL, 'BASE' ) }" />
          <tr class="${ styleClass }">
            <td></td>
            <td colspan="2">
	      ${ languageName }
	       ( ${ localizedName } )
            </td>
            <td align="right">
	      <it:link action="editlanguageform" targetAction="create" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.create.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.create"/></it:link>
	      <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.update"/></it:link>
<!--	      <it:link action="editlanguage" targetAction="disable" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.disable.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.disable"/></it:link>-->
	      <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.export"/></it:link>
            </td>
          </tr>
    <c:forEach items="${ locales }" var="locale">

        <c:set var="styleClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }" />
        <c:set var="i" value="${ i + 1 }" />

    	<c:set var="localeName" value="${ it:ITrackerResources_GetString(key, locale) }" />
		<c:set var="keyL" >${ key }.${ locale }</c:set>
    	<c:set var="localizedName" value="${ it:ITrackerResources_GetString(keyL, 'BASE' ) }" />
            <tr class="${ styleClass }">
              <td></td>
              <td></td>
              <td>
	          ${ localeName }
		       ( ${ localizedName } )
              </td>
              <td align="right">
	          <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="${ locale }" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ localeName }"><it:message key="itracker.web.admin.listlanguages.update"/></it:link>
<!--	          <it:link action="editlanguage" targetAction="disable" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.disable.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.disable"/></it:link>-->
	          <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="${ locale }" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="${ localeName }"><it:message key="itracker.web.admin.listlanguages.export"/></it:link>
              </td>
            </tr>
    </c:forEach>
  </c:forEach>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
