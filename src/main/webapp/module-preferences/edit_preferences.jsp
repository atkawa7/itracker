<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.services.ConfigurationService" %>
<%@ page import="org.itracker.services.util.UserUtilities" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.core.resources.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- TODO : Move redirect logic to Action class. --%>
<c:choose>
    <c:when test="${edituser==null}">
      <logic:forward name="unauthorized"/>
    </c:when>
<c:otherwise>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<bean:define id="pageTitleKey" value="itracker.web.editprefs.title"/>
<bean:define id="pageTitleArg" value=""/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

      <html:javascript formName="preferencesForm"/>

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
      
<html:messages name="warnings" id="msg"> 
      <c:out value="${msg}"/> 
   </html:messages> 
   
      <html:form action="/editpreferences" onsubmit="return validatePreferencesForm(this);">
        <html:hidden property="action" value="preferences"/>
        <html:hidden property="login" value="${edituser.login}"/>
        <%
        ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");
        Map<String,List<String>> languages = sc.getAvailableLanguages();
        %>
        <table border="0" cellspacing="0" cellspacing="1" width="800px">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.login"/>:</td>
                  <td> </td>
            <td class="editColumnText"><c:out value="${edituser.login}"/></td>
            <td class="editColumnTitle"></td>
            <td class="editColumnTitle"> <it:message key="itracker.web.attr.status"/>: </td>
            <td  class="editColumnText"><c:out value="${statusName}"/></td>
           <td></td>
          </tr>
          <tr>
          	<td class="editColumnTitle"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/> 
          		</td>
          		  <td> </td>
          	<td class="editColumnText"> </td>
          	   <td> </td>
          	<td class="editColumnTitle"></td>
          	<td class="editColumnText"> </td>
          	   <td> </td>
          </tr>
          <tr>
            <td class="editColumnTitle"><sup>*</sup><it:message key="itracker.web.attr.firstname"/></td>
              <td> </td>
           <c:choose>
          		<c:when test="${allowProfileUpdate}">
          		 <td><html:text property="firstName" styleClass="editColumnText"/></td>
          		</c:when>
          		<c:otherwise>
          		 <td class="editColumnText"><c:out value="${edituser.firstName}"/></td>
                <html:hidden property="firstName" value="${edituser.firstName}"/>
          		</c:otherwise>
          	</c:choose>
           
               <td> </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><it:formatDate date="${edituser.createDate}"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><sup>*</sup><it:message key="itracker.web.attr.lastname"/>:</td>
              <td> </td>
              <c:choose>
          		<c:when test="${allowProfileUpdate}">
          		 <td><html:text property="lastName" styleClass="editColumnText"/></td>
          		</c:when>
          		<c:otherwise>
          		<td class="editColumnText">${edituser.lastName}</td>
                <html:hidden property="lastName" value="${edituser.lastName}"/>
          		</c:otherwise>
          	</c:choose>
         
               <td></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><it:formatDate date="${edituser.lastModifiedDate}"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><sup>*</sup><it:message key="itracker.web.attr.email"/>:</td>
              <td> </td>
            <c:choose>
          		<c:when test="${allowProfileUpdate}">
          		<td><html:text property="email" styleClass="editColumnText"/></td>
          		</c:when>
          		<c:otherwise>
          		  <td class="editColumnText">${edituser.email}</td>
                <html:hidden property="email" value="${edituser.email}"/>
          		</c:otherwise>
          	</c:choose>
         
               <td></td>
                  <td> </td>
                     <td> </td>
          </tr>
             <tr>
             <td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="13" width="1"/></td>
                <td></td>
                  <td> </td>
                   <td></td>
                      <td></td>
                         <td></td></tr>
         <c:if test="${allowPasswordUpdate}">
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.currpassword"/>:</td>
                  <td> </td>
                <td><html:password property="currPassword" styleClass="editColumnText" redisplay="false"/></td>
               <td></td>
                  <td></td>
                     <td></td>
                        <td></td>
              </tr>
               <tr>
               <td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td>
                  <td></td>
                     <td></td>
                        <td></td>
                           <td></td>
                             <td> </td></tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.attr.newpassword"/>:</td>
                  <td> </td>
                <td><html:password property="password" styleClass="editColumnText" redisplay="false"/></td>
                <td class="editColumnTitle"></td>
                <td></td>
                   <td></td>
                      <td></td>
              </tr>
          <tr>
          	<td class="editColumnTitle"><it:message key="itracker.web.attr.confpassword"/>:</td>
            <td> </td>	<td><html:password property="confPassword" styleClass="editColumnText" redisplay="false"/></td>
          
          	<td class="editColumnTitle"></td>
          	<td> </td>
          	   <td> </td>
          	      <td> </td>
          </tr>
    	</c:if>
    	
          <tr>
          <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="8" width="1"/></td>
             <td></td>
                <td></td>
                    </tr>
          <tr>
            <td class="editColumnText"><sup>*</sup><it:message key="itracker.web.generic.reqfield"/></td>
               <td></td>
                  <td></td>
                     <td></td>
                        <td></td>   <td></td>
          </tr>
          <tr>
          <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="18" width="1"/></td>
             <td></td>
                <td></td>
                    </tr>
        </table>
      	<c:if test="${allowProfileUpdate || allowPasswordUpdate || allowPreferenceUpdate}">
    
     
    <html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit> 
    	</c:if>
        
        
        <table border="0" cellspacing="0" cellspacing="1" width="800px">
          <tr>
          <td class="editColumnTitle" colspan="3" align="center"><it:message key="itracker.web.attr.preferences"/>:</td></tr>
          <tr class="listHeading"><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
          <tr><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.locale"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		   <td class="editColumnText">
                  <html:select property="userLocale" styleClass="editColumnText">
                    <html:option value="" styleClass="editColumnText"></html:option>
                    <%
                    if  ( languages != null ) {
                        for(java.util.Iterator<String> iter = languages.keySet().iterator(); iter.hasNext(); ) {
                            String language = iter.next();
                            List<String> locales = (List<String>) languages.get(language);
                        %>
                            <html:option value="<%=language%>" styleClass="editColumnText"><%= ITrackerResources.getString("itracker.locale.name", language) %></html:option>
                        <%  for(int i = 0; i < locales.size(); i++) {
                                String languageListItem = (String) locales.get(i);
                        %>
                                <html:option value="<%=languageListItem%>" styleClass="editColumnText"><%= ITrackerResources.getString("itracker.locale.name", languageListItem) %></html:option>
                        <%  } 
                        }
                    } else {

                        String name = ITrackerResources.getString("itracker.locale.name", "en_US");
                        %>

                      <html:option key="<%= name %>" value="en_US" styleClass="editColumnText"><%= name %></html:option>
                 <% } %>
                  </html:select><!-- $ {languageCodeName} -->
                </td>
          		</c:when>
          		<c:otherwise>
          		  <td class="editColumnText">${userLocaleAsString} }</td>
       
          		</c:otherwise>
          	</c:choose>  
          </tr>
          <%-- <nitrox:var name="allowSaveLogin" type="java.lang.Boolean"/> --%>
        	<c:if test="${allowSaveLogin}"> 
              <tr><td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
              <tr>
                <td class="editColumnTitle"><it:message key="itracker.web.editprefs.savelogin"/>:</td>
              	<c:choose>
              		<c:when test="${allowPreferenceUpdate}">
              		      <td class="editColumnText">
                      <html:radio property="saveLogin" value="true"/><it:message key="itracker.web.generic.yes"/> &nbsp;&nbsp;&nbsp;&nbsp;
                      <html:radio property="saveLogin" value="false"/><it:message key="itracker.web.generic.no"/>
                    </td>
              		</c:when>
              		<c:otherwise>
              		 <td class="editColumnText">${getSaveLoginLocalized} }</td>
              		</c:otherwise>
              	</c:choose>
               
              
            
              </tr>
   </c:if>
          <tr><td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.editprefs.numissuesindex"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		<td><html:text property="numItemsOnIndex" styleClass="editColumnText" size="6"/></td>
          		</c:when>
          		<c:otherwise>
          		<td class="editColumnText">${edituserprefs.numItemsOnIndex}</td>
          		</c:otherwise>
          	</c:choose>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.editprefs.numissuesproject"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		  <td><html:text property="numItemsOnIssueList" styleClass="editColumnText" size="6"/></td>
          		</c:when>
          		<c:otherwise>
          		<td class="editColumnText">${edituserprefs.numItemsOnIndex}</td>
          		</c:otherwise>
          	</c:choose>
            
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.editprefs.showclosed"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		  <td class="editColumnText">
                  <html:radio property="showClosedOnIssueList" value="true"/><it:message key="itracker.web.generic.yes"/> &nbsp;&nbsp;&nbsp;&nbsp;
                  <html:radio property="showClosedOnIssueList" value="false"/><it:message key="itracker.web.generic.no"/>
                </td>
          		</c:when>
          		<c:otherwise>
          		 <td class="editColumnText">${showClosedOnIssueListLocalized}</td>
          		</c:otherwise>
          	</c:choose>
           
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.editprefs.sortcolumn"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		 <td class="editColumnText">
                  <html:select property="sortColumnOnIssueList" styleClass="editColumnText">
                    <html:option value="id" styleClass="editColumnText" key="itracker.web.attr.id"/>
                    <html:option value="sev" styleClass="editColumnText" key="itracker.web.attr.severity"/>
                    <html:option value="stat" styleClass="editColumnText" key="itracker.web.attr.status"/>
                    <html:option value="own" styleClass="editColumnText" key="itracker.web.attr.owner"/>
                    <html:option value="lm" styleClass="editColumnText" key="itracker.web.attr.lastmodified"/>
                  </html:select>
                </td>
          		</c:when>
          		<c:otherwise>
          			<c:choose>
          				<c:when test="${edituserprefs.sortColumnOnIssueList=='sev'}">
          				<td class="editColumnText"><%= ITrackerResources.getString("itracker.web.attr.severity", (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          				</c:when>
          				<c:when test="${edituserprefs.sortColumnOnIssueList=='stat'}">
          				<td class="editColumnText"><%= ITrackerResources.getString("itracker.web.attr.status", (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          				</c:when>
          				<c:when test="${edituserprefs.sortColumnOnIssueList=='own'}">
          				<td class="editColumnText"><%= ITrackerResources.getString("itracker.web.attr.owner", (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          				</c:when>
          				<c:when test="${edituserprefs.sortColumnOnIssueList=='lm'}">
          				<td class="editColumnText"><%= ITrackerResources.getString("itracker.web.attr.lastmodified", (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          				</c:when>
          				<c:otherwise>
          				<td class="editColumnText"><%= ITrackerResources.getString("itracker.web.attr.id", (java.util.Locale)pageContext.getAttribute("currLocale")) %></td>
          				</c:otherwise>
          			</c:choose>
          		 
          		</c:otherwise>
          	</c:choose>
          
          </tr>
          <tr><td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.editprefs.remembersearch"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		 <td class="editColumnText">
                  <html:radio property="rememberLastSearch" value="true"/><it:message key="itracker.web.generic.yes"/> &nbsp;&nbsp;&nbsp;&nbsp;
                  <html:radio property="rememberLastSearch" value="false"/><it:message key="itracker.web.generic.no"/>
                </td>
          		</c:when>
          		<c:otherwise>
          		<td class="editColumnText">${getRememberLastSearchLocalized}</td>
          		</c:otherwise>
          	</c:choose>
            
          </tr>
          <tr><td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
    
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.editprefs.usetextactions"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
                <td class="editColumnText">
                  <html:radio property="useTextActions" value="true"/><it:message key="itracker.web.generic.yes"/> &nbsp;&nbsp;&nbsp;&nbsp;
                  <html:radio property="useTextActions" value="false"/><it:message key="itracker.web.generic.no"/>
                </td>
          		</c:when>
          		<c:otherwise>
          		 <td class="editColumnText">${getRememberLastSearchLocalized}</td>
          		</c:otherwise>
          	</c:choose>
          </tr>
          <tr><td colspan="2"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle" style="vertical-align: top;"><it:message key="itracker.web.editprefs.hideindex"/>:</td>
          	<c:choose>
          		<c:when test="${allowPreferenceUpdate}">
          		  <td class="editColumnText" style="vertical-align: top;">
                  <table width="100%" border="0" cellspacing="0">
                    <tr>
                      <td class="editColumnText"><html:multibox property="hiddenIndexSections" styleClass="editColumnText" value="<%= Integer.toString(UserUtilities.PREF_HIDE_ASSIGNED) %>"/><it:message key="itracker.web.editprefs.section.assigned"/></td>
                      <td class="editColumnText"><html:multibox property="hiddenIndexSections" styleClass="editColumnText" value="<%= Integer.toString(UserUtilities.PREF_HIDE_UNASSIGNED) %>"/><it:message key="itracker.web.editprefs.section.unassigned"/></td>
                    </tr>
                    <tr>
                      <td class="editColumnText"><html:multibox property="hiddenIndexSections" styleClass="editColumnText" value="<%= Integer.toString(UserUtilities.PREF_HIDE_CREATED) %>"/><it:message key="itracker.web.editprefs.section.created"/></td>
                      <td class="editColumnText"><html:multibox property="hiddenIndexSections" styleClass="editColumnText" value="<%= Integer.toString(UserUtilities.PREF_HIDE_WATCHED) %>"/><it:message key="itracker.web.editprefs.section.watched"/></td>
                    </tr>
                  </table>
                </td>
          		</c:when>
          		<c:otherwise>
          		   <%  String hiddenSectionsString = null;
          		 UserPreferences userPrefs = (UserPreferences) session.getAttribute("edituserprefs");
                    Integer[] hiddenSections = UserUtilities.getHiddenIndexSections(userPrefs.getHiddenIndexSections());
                    for(int i = 0; i < hiddenSections.length; i++) {
                        int sectionNumber = (hiddenSections[i] != null ? hiddenSections[i].intValue() : -1);
                        if(sectionNumber == UserUtilities.PREF_HIDE_ASSIGNED) {
                            hiddenSectionsString += (hiddenSectionsString == null ? "" : ", ");
                            hiddenSectionsString += ", " + ITrackerResources.getString("itracker.web.editprefs.section.assigned", (java.util.Locale)pageContext.getAttribute("currLocale"));
                        } else if(sectionNumber == UserUtilities.PREF_HIDE_UNASSIGNED) {
                            hiddenSectionsString += (hiddenSectionsString == null ? "" : ", ");
                            hiddenSectionsString += ", " + ITrackerResources.getString("itracker.web.editprefs.section.unassigned", (java.util.Locale)pageContext.getAttribute("currLocale"));
                        } else if(sectionNumber == UserUtilities.PREF_HIDE_CREATED) {
                            hiddenSectionsString += (hiddenSectionsString == null ? "" : ", ");
                            hiddenSectionsString += ", " + ITrackerResources.getString("itracker.web.editprefs.section.created", (java.util.Locale)pageContext.getAttribute("currLocale"));
                        } else if(sectionNumber == UserUtilities.PREF_HIDE_WATCHED) {
                            hiddenSectionsString += (hiddenSectionsString == null ? "" : ", ");
                            hiddenSectionsString += ", " + ITrackerResources.getString("itracker.web.editprefs.section.watched", (java.util.Locale)pageContext.getAttribute("currLocale"));
                        }
                    }
                 %>
                 <td class="editColumnText"><%= (hiddenSectionsString == null ? "" : hiddenSectionsString) %></td>
          		</c:otherwise>
          	</c:choose>
          
          </tr>
        </table>

        <br>
      	<c:if test="${allowProfileUpdate || allowPasswordUpdate || allowPreferenceUpdate}">
      	     <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
              <tr><td align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
            </table>
      	</c:if>
       
      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
	</c:otherwise>
</c:choose>
