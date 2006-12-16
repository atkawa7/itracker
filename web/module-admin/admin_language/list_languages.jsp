<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
 
<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.core.resources.*" %>
 <%@ page import="java.util.List" %>

<%-- <it: checkLogin permission="< % = UserUtilities.PERMISSION_USER_ADMIN %>"/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
<bean:define id="pageTitleKey" value="itracker.web.admin.listlanguages.title"/>
<bean:define id="pageTitleArg" value=""/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>


<table border="0" cellspacing="0"  cellspacing="1"  width="100%">
<%
  ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");
  java.util.HashMap<String,List<String>> languages = sc.getAvailableLanguages();
  String baseLocaleName = ITrackerResources.getString("itracker.web.attr.baselocale");
%>
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
        <it:link action="editlanguageform" targetAction="create" paramName="locale" paramValue="<%= ITrackerResources.BASE_LOCALE %>" titleKey="itracker.web.admin.listlanguages.create.alt" arg0="<%= baseLocaleName %>"><it:message key="itracker.web.admin.listlanguages.create" arg0="<%= baseLocaleName %>"/></it:link>
        <it:link action="createlanguagekeyform" targetAction="create" titleKey="itracker.web.admin.listlanguages.createkey.alt"><it:message key="itracker.web.admin.listlanguages.createkey"/></it:link>
        <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="<%= ITrackerResources.BASE_LOCALE %>" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="<%= baseLocaleName %>"><it:message key="itracker.web.admin.listlanguages.update" arg0="<%= baseLocaleName %>"/></it:link>
        <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="<%= ITrackerResources.BASE_LOCALE %>" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="<%= baseLocaleName %>"><it:message key="itracker.web.admin.listlanguages.export" arg0="<%= baseLocaleName %>"/></it:link>
    </td>
  </tr>
  <%
      int lineCount = 0;
      for(java.util.Iterator<String> iter = languages.keySet().iterator(); iter.hasNext(); ) {
          lineCount++;
          String language = iter.next();
          String languageName = ITrackerResources.getString("itracker.locale.name", language);
          java.util.List<String> locales = (java.util.List<String>) languages.get(language);
  %>
          <tr class="listRowUnshaded">
            <td></td>
            <td colspan="2">
              <%= languageName %>
            </td>
            <td align="right">
              <it:link action="editlanguageform" targetAction="create" paramName="locale" paramValue="<%= language %>" titleKey="itracker.web.admin.listlanguages.create.alt" arg0="<%= languageName %>"><it:message key="itracker.web.admin.listlanguages.create"/></it:link>
              <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="<%= language %>" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="<%= languageName %>"><it:message key="itracker.web.admin.listlanguages.update"/></it:link>
              <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="<%= language %>" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="<%= languageName %>"><it:message key="itracker.web.admin.listlanguages.export"/></it:link>
            </td>
          </tr>
  <%      for(int i = 0; i < locales.size(); i++) {
            lineCount++;
            String localeName = ITrackerResources.getString("itracker.locale.name", (String) locales.get(i));
  %>
            <tr class="listRowUnshaded">
              <td></td>
              <td></td>
              <td>
                <%= localeName %>
              </td>
              <td align="right">
                <it:link action="editlanguageform" targetAction="update" paramName="locale" paramValue="<%= (String) locales.get(i) %>" titleKey="itracker.web.admin.listlanguages.update.alt" arg0="<%= localeName %>"><it:message key="itracker.web.admin.listlanguages.update"/></it:link>
                <it:link action="exportlanguage" targetAction="export" paramName="locale" paramValue="<%= (String) locales.get(i) %>" titleKey="itracker.web.admin.listlanguages.export.alt" arg0="<%= localeName %>"><it:message key="itracker.web.admin.listlanguages.export"/></it:link>
              </td>
            </tr>
  <%      }
      }
  %>
</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
