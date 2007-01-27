<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.services.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.itracker.core.resources.*" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.editconfiguration.title.create"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:form action="/editconfiguration">
  <html:hidden property="action"/>
  <html:hidden property="id"/>
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
    <% if("createstatus".equals(request.getAttribute("action")) || ("update".equals(request.getAttribute("action")) && pageContext.getAttribute("value") != null && ! pageContext.getAttribute("value").equals(""))) { %>
        <tr>
          <td colspan="4"><span class="editColumnTitle"><it:message key="itracker.web.attr.value"/>:</span> <html:text property="value" styleClass="editColumnText"/></td>
        </tr>
    <% } %>
    <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
    <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.translations"/>:</td></tr>
    <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
    <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="4"/></td></tr>
    <tr class="listRowUnshaded">
      <td colspan="3">
          <it:message key="itracker.web.attr.baselocale"/>
      </td>
      <td>
        <% String baseKey = "translations(" + ITrackerResources.BASE_LOCALE + ")"; %>
        <html:text property="<%= baseKey %>" styleClass="editColumnText"/></td>
 
    </tr>
    <%
        for(java.util.Iterator<String> iter = languages.keySet().iterator(); iter.hasNext(); ) {
            String language = iter.next();
            String languageKey = "translations(" + language + ")";
            java.util.List<String> locales = (java.util.List<String>) languages.get(language);
    %>
            <c:set var="languageKey" value="<%=languageKey%>"/>
            <tr class="listRowUnshaded">
              <td></td>
              <td colspan="2">
                <%= ITrackerResources.getString("itracker.locale.name", language) %>
              </td>
              <td>
                <html:text property="<%= languageKey %>" styleClass="editColumnText"/></td>
          
            </tr>
    <%
            for(int i = 0; i < locales.size(); i++) {
                String localeKey = "translations(" + locales.get(i) + ")";
    %>
                <c:set var="localeKey" value="<%=localeKey%>"/>
                <tr class="listRowUnshaded">
                  <td></td>
                  <td></td>
                  <td>
                    <%= ITrackerResources.getString("itracker.locale.name", (String) locales.get(i)) %>
                  </td>
                  <td>
                    <html:text property="<%= localeKey %>" styleClass="editColumnText"/></td>
               
                </tr>
    <%      }
        }
    %>
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

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>