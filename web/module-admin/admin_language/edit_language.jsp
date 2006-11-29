<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

 
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="org.itracker.services.util.*" %>
 
<%@ page import="org.itracker.web.util.*" %>

<%-- <it: checkLogin permission="< % = UserUtilities.PERMISSION_USER_ADMIN %>"/> --%>

<%
    Integer localeTypeInteger = (Integer) session.getAttribute(Constants.EDIT_LANGUAGE_TYPE_KEY);
    if(localeTypeInteger == null || localeTypeInteger.intValue() < 1) {
%>
      <logic:forward name="unauthorized"/>
<%
    } else {
      String[] keys = (String[]) session.getAttribute(Constants.EDIT_LANGUAGE_KEYS_KEY);
 %> 
 <%--    java.util.HashMap baseKeys = (java.util.HashMap) session.getAttribute(Constants.EDIT_LANGUAGE_BASE_KEY);
      java.util.HashMap langKeys = (java.util.HashMap) session.getAttribute(Constants.EDIT_LANGUAGE_LANG_KEY);
      java.util.HashMap locKeys = (java.util.HashMap) session.getAttribute(Constants.EDIT_LANGUAGE_LOC_KEY);
--%>
  <%    boolean isUpdate = false;
      int localeType = localeTypeInteger.intValue();
%>
      <bean:parameter id="action" name="action"/>
      <bean:parameter id="locale" name="locale"/>
      <bean:parameter id="parentLocale" name="parentLocale" value="BASE"/>
      <!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
   
     <%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
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

      <html:form action="/editlanguage">
        <html:hidden property="action"/>
        <% if(isUpdate) { %>
            <html:hidden property="locale"/>
        <% } %>

        <table border="0" cellspacing="0"  cellspacing="1"  width="95%" align="left">
          <% if(! isUpdate) { %>
                <tr>
                  <td colspan="4">
                  <% if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) { %>
                        <span class="editColumnTitle"><it:message key="itracker.web.attr.localeiso"/>: </span>
                        <html:text property="locale" styleClass="editColumnText" maxlength="5" size="5"/></td>
                  <% } else { %>
                        <span class="editColumnTitle"><it:message key="itracker.web.attr.localeiso"/>: </span>
                        <html:text property="locale" styleClass="editColumnText" maxlength="2" size="2"/></td>
                  <% } %>
                  </tr>
          <% } %>
          <tr align="left" class="listHeading">
            <td><it:message key="itracker.web.attr.key"/></td>
            <td><it:message key="itracker.web.attr.baselocale"/></td>
            <% if(localeType >= SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE) { %>
                <td><it:message key="itracker.web.attr.language"/></td>
            <% } %>
            <% if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) { %>
                <td><it:message key="itracker.web.attr.locale"/></td>
            <% } %>
          </tr>

          <% /* Added replaces to fix bug in beanutils with periods in key names */
             for(int i = 0; i < keys.length; i++) {
                String propertyKey = "items(" + keys[i] + ")";
                String styleClass = (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" );
          %>
                <tr class="<%= styleClass %>">
                  <td valign="top"><%= keys[i].replace('/', '.') %></td>
                  <% if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_BASE) { %>
                      <% if(ITrackerResources.isLongString(keys[i].replace('/', '.'))) { %>
                          <td valign="top"><html:textarea rows="4" cols="40" property="<%= propertyKey %>" styleClass="<%= styleClass %>"/></td>
                      <% } else { %>
                          <td valign="top"><html:text property="<%= propertyKey %>" size="40" styleClass="<%= styleClass %>"/></td>
                      <% } %>
                  <% } else { %>
                      <td valign="top"><it:message key="<%= keys[i].replace('/', '.') %>" locale="<%= ITrackerResources.BASE_LOCALE %>"/></td>
                      <% if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE) { %>
                          <% if(ITrackerResources.isLongString(keys[i].replace('/', '.'))) { %>
                              <td valign="top"><html:textarea rows="4" cols="40" property="<%= propertyKey %>" styleClass="<%= styleClass %>"/></td>
                          <% } else { %>
                              <td valign="top"><html:text property="<%= propertyKey %>" size="40" styleClass="<%= styleClass %>"/></td>
                          <% } %>
                      <% } else { %>
                          <td valign="top"><it:message key="<%= keys[i].replace('/', '.') %>" locale="<%= parentLocale %>"/></td>
                          <% if(ITrackerResources.isLongString(keys[i].replace('/', '.'))) { %>
                              <td valign="top"><html:textarea rows="4" cols="40" property="<%= propertyKey %>" styleClass="<%= styleClass %>"/></td>
                          <% } else { %>
                              <td valign="top"><html:text property="<%= propertyKey %>" size="40" styleClass="<%= styleClass %>"/></td>
                          <% } %>
                      <% } %>
                  <% } %>
                </tr>
          <% } %>

          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
        	<c:choose>
        		<c:when test="${isUpdate}">
        		  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
        		</c:when>
        		<c:otherwise>
        		  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
        		</c:otherwise>
        	</c:choose>
          
          
          
          
        </table>
      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>
