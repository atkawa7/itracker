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
  <%  boolean isUpdate = false;
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

      <html:form method="post" action="/editlanguage">
        <html:hidden property="action"/>
        <html:hidden property="parentLocale"/>
        <% if ( "update".equals(action) )
                isUpdate = true;
           if(isUpdate) { %>
            <html:hidden property="locale"/>
        <% } %>

       <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
          <%  
              String maxLength="2";
              String maxSize = "2";   
              String LocMsg = "itracker.web.attr.language";
              String afterTd = "<td> </td>";
              String beforeTd = "";
              if(localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE) {
                    afterTd="";
                    beforeTd = "<td> </td>";
                    LocMsg = "itracker.web.attr.locale";
                    maxLength = "5";    
                    maxSize = "5";     
              }
              boolean Readonly = false;
              if( isUpdate) { 
                 Readonly = true;
              }
          %>
            
             <tr>
                <td colspan="2">
                      <span class="editColumnTitle"><it:message key="itracker.web.attr.localeiso"/>: </span>
                      <html:text readonly="<%=Readonly%>" property="locale" styleClass="editColumnText" maxlength="<%=maxLength%>" size="<%=maxSize%>"/>
                </td>
                <td colspan="2">
                      <span class="editColumnTitle"><it:message key="itracker.web.attr.language"/>: </span>
                      <html:text property="localeTitle" styleClass="editColumnText" maxlength="20" size="20"/>
                </td>
             </tr>
          <tr align="left" class="listHeading">
            <td><it:message key="itracker.web.attr.key"/></td>
            <td><it:message key="itracker.web.attr.baselocale"/></td>
            <%=beforeTd%>
            <td><it:message key="<%=LocMsg%>"/></td>
            <%=afterTd%>
          </tr>
          <% int i = 0; %>
          <logic:iterate id="itemlangs" name="languageForm" property="items">
               <bean:define name="itemlangs" property="key" id="key" type="java.lang.String"/>
               <bean:define name="itemlangs" property="value" id="value" type="java.lang.String"/>
          <% /* Added replaces to fix bug in beanutils with periods in key names */
//             for(int i = 0; i < keys.length; i++) {
                String propertyKey = "items("+key+")" ;
                String Skey = key.replace('/', '.');
                String styleClass = (i % 2 == 1 ? "listRowShaded" : "listRowUnshaded" );
                i++;
                if ( ! Skey.equals("itracker.locales") && ! Skey.equals("itracker.locale.name")) {
          %>
                <tr class="<%= styleClass %>">
                  <td valign="top"><%= Skey %></td>
                  <% if(localeType != SystemConfigurationUtilities.LOCALE_TYPE_BASE) { %>
                          <td valign="top"><it:message key="<%= Skey %>" locale="<%= ITrackerResources.BASE_LOCALE %>"/></td>
                  <% } %>
                  <% if(localeType != SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE) { %>
                           <td valign="top"><it:message key="<%= Skey %>" locale="<%= parentLocale %>"/></td>
                  <% }  %>
                  <% if(ITrackerResources.isLongString(Skey)) { %>
                         <td valign="top"><html:textarea indexed="false" name="languageForm"  rows="4" cols="40" property="<%= propertyKey %>" value="<%=value%>" styleClass="<%= styleClass %>"/></td>
                  <% } else { %>
                          <td valign="top"><html:text indexed="false" name="languageForm" property="<%= propertyKey %>" value="<%=value%>" size="40" styleClass="<%= styleClass %>"/></td>
                  <% } %>
                  <%=afterTd%>
                </tr>
          <%   } %>
          </logic:iterate>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
          <% if ( isUpdate ) { %>
       		  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
          <% } else { %>
       		  <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
          <% } %>
        </table>
      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>
