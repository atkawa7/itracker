<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
 
<bean:define id="pageTitleKey" value="itracker.web.admin.createlanguagekey.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:form action="/createlanguagekey">
  <html:hidden property="action" value="create"/>
  <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
  <%
    ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");
    Map<String,List<String>> languages = sc.getAvailableLanguages();
  %>
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
      <% String localeKey = "items(" + ITrackerResources.BASE_LOCALE + ")"; %>
      <td>
        <html:textarea rows="2" cols="60" property="<%=localeKey%>" styleClass="editColumnText"/>
      </td>
    </tr>
    <%
        for(java.util.Iterator<String> iter = languages.keySet().iterator(); iter.hasNext(); ) {
            String language = iter.next();
          List<String> locales = (List<String>) languages.get(language);
    %>
            <tr class="listRowUnshaded">
              <td>&nbsp;</td>
              <td colspan="2" valign="top">
                <%= ITrackerResources.getString("itracker.locale.name", language) %>
              </td>
              <% localeKey = "items(" + language + ")"; %>
              <c:set var="localeKey" value="<%=localeKey%>"/>
              <td>
                <html:textarea rows="2" cols="60" property="<%=localeKey%>"  styleClass="editColumnText"/>
              </td>
            </tr>
    <%      for(int i = 0; i < locales.size(); i++) { %>
                <tr class="listRowUnshaded">
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td valign="top"><%= ITrackerResources.getString("itracker.locale.name", (String) locales.get(i)) %></td>
                  <% localeKey = "items(" + (String) locales.get(i) + ")"; %>
                   <c:set var="localeKey" value="<%=localeKey%>"/>
                  <td>
                    <html:textarea rows="2" cols="60" property="<%=localeKey%>" styleClass="editColumnText"/>
                  </td>
                </tr>
    <%      }
        }
    %>
    <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
  </table>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>