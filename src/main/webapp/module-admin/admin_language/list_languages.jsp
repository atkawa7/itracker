<%@ page contentType="text/html;charset=utf-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listlanguages.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<table border="0" cellspacing="0" cellspacing="1" width="100%">
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
        <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.export" arg0="${ baseLocaleName }"/></it:link>
    </td>
  </tr>
  <c:set var="key" value="itracker.locale.name" />
  <c:forEach items="${ languageKeys }" var="language">
  	<c:set var="locales" value="${ languages[language] }" />
  	<c:set var="languageName" value="${ it:ITrackerResources_GetString(key, language) }" />
  	
          <tr class="listRowUnshaded">
            <td></td>
            <td colspan="2">
	      ${ languageName }
            </td>
            <td align="right">
	      <it:link action="editlanguageform" targetAction="create" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.create.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.create"/></it:link>
	      <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.update"/></it:link>
	      <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.export"/></it:link>
            </td>
          </tr>
    <c:forEach items="${ locales }" var="locale">
    	<c:set var="localeName" value="${ it:ITrackerResources_GetString(key, locale) }" />
    	
            <tr class="listRowUnshaded">
              <td></td>
              <td></td>
              <td>
	          ${ localeName }
              </td>
              <td align="right">
	          <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="${ locale }" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ localeName }"><it:message key="itracker.web.admin.listlanguages.update"/></it:link>
	          <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="${ locale }" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="${ localeName }"><it:message key="itracker.web.admin.listlanguages.export"/></it:link>
              </td>
            </tr>
    </c:forEach>
  </c:forEach>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
