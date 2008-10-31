<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.Map"
    import="java.util.Set"
    import="org.itracker.model.PermissionType"
    import="org.itracker.services.util.*" 
    import="org.itracker.web.util.RequestHelper" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>    

<%-- <nitrox:var name="pageTitleKey" type="java.lang.String"/> --%>
<%-- <nitrox:var name="pageTitleArg" type="java.lang.String"/> --%>
<%
	final Map<Integer, Set<PermissionType>> permissions = RequestHelper
			.getUserPermissions(session);
%>
<html>
  <head>
    <title><it:message key="itracker.web.generic.itracker"/>: <it:message key="${pageTitleKey}" arg0="${pageTitleArg}"/>
   </title>
    <link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/themes/defaulttheme/includes/styles.css"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/> 
     <meta http-equiv="Pragma" content="no-cache"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/themes/defaulttheme/includes/calendar.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/themes/defaulttheme/includes/scripts.js"></script>
  </head>

  <body>
 
    <table border="0" cellspacing="1" cellspacing="0" width="100%">
      <tr>
        <td class="headerText">
         <%-- TODO: temp. removed logo code, fix again <% if(alternateLogo != null && ! alternateLogo.equals("")) { %>
               <!--  <img src="<%= alternateLogo %>"> itracker.org<br>
          <% } else { %>
                <!--  <html:img page="/themes/defaulttheme/images/logo.gif"/> itracker.org<br>
          <% } %> --%>
        </td>
        <td class="headerTextPageTitle"><h1><it:message key="${pageTitleKey}" arg0="${pageTitleArg}"/></h1></td>
        <td class="headerTextWelcome">
          <it:message key="itracker.web.header.welcome"/>
		  <c:choose>
		  	<c:when test="${ currUser != null}">${ currUser.firstName } ${ currUser.lastName } (<em>${ currUser.login }</em>)</c:when>
		  	<c:otherwise><em><it:message key="itracker.web.header.guest"/></em></c:otherwise>
		  </c:choose>	
		  </td>
        </td>
      </tr>
      <tr><td colspan="3" class="top_ruler"><hr/></td></tr>
    </table>
    <table border="0" cellspacing="0" cellspacing="0" width="100%">
      <tr>
        <td class="headerLinks" align="left">
        	<c:if test="${currUser != null}">
        	
    	<form name="lookupForm" action="<html:rewrite module="/module-projects" forward="viewissue"/>">
                <input type="text" name="id" size="5" class="lookupBox" onchange="document.lookupForm.submit();">
    	</form>
        	</c:if>
        </td>
        <td class="headerLinks" align="right">

			<c:if test="${currUser != null}">
	
	          	<html:link styleClass="headerLinks" titleKey="itracker.web.header.menu.home.alt" module="/" action="/portalhome">
	             	<it:message key="itracker.web.header.menu.home"/></html:link>	
	             		
				 | <html:link
					styleClass="headerLinks"
					titleKey="itracker.web.header.menu.projectlist.alt"
					module="/module-projects" action="/list_projects">
					<it:message key="itracker.web.header.menu.projectlist" />
				</html:link> 
				| <html:link forward="searchissues" module="/module-searchissues"
					styleClass="headerLinks"
					titleKey="itracker.web.header.menu.search.alt">
					<it:message key="itracker.web.header.menu.search" />
				</html:link>
				<%-- TODO: fix reports-section 
				<c:if
					test="<%=UserUtilities.hasPermission(permissions,
										UserUtilities.PERMISSION_VIEW_ALL)%>">
	                      
	                | <html:link styleClass="headerLinks"
						titleKey="itracker.web.header.menu.reports.alt"
						module="/module-reports" action="/list_reports" >
						<it:message key="itracker.web.header.menu.reports" />
					</html:link>
				</c:if>
				--%>
				<c:if
					test="<%=UserUtilities.hasPermission(permissions,
										UserUtilities.PERMISSION_USER_ADMIN)%>">
	                      |
	                      <html:link styleClass="headerLinks"
						titleKey="itracker.web.header.menu.admin.alt"
						module="/module-admin" action="/adminhome">
						<it:message key="itracker.web.header.menu.admin" />
					</html:link>
				</c:if>
				<c:if
					test="<%=UserUtilities.hasPermission(permissions,
										UserUtilities.PERMISSION_PRODUCT_ADMIN)%>">
	               | <html:link styleClass="headerLinks"
						titleKey="itracker.web.header.menu.projectadmin.alt"
						module="/module-admin" action="/listprojectsadmin">
						<it:message key="itracker.web.header.menu.projectadmin" />
					</html:link>
				</c:if>
	           
	            
	                | <html:link module="/module-preferences"
					forward="editpreferences" styleClass="headerLinks"
					titleKey="itracker.web.header.menu.preferences.alt">
					<it:message key="itracker.web.header.menu.preferences" />
				</html:link>
	                | <html:link forward="help" styleClass="headerLinks"
					titleKey="itracker.web.header.menu.help.alt" module="/module-help">
					<it:message key="itracker.web.header.menu.help" />
				</html:link>
	                | <html:link action="/logoff" styleClass="headerLinks"
					titleKey="itracker.web.header.menu.logout.alt" module="/">
					<it:message key="itracker.web.header.menu.logout" />
				</html:link>
			</c:if> 
			<c:if test="${currUser == null}">
		             	<%-- <nitrox:var name="allowForgotPassword" type="java.lang.Boolean"/> --%>
				<c:if test="${allowForgotPassword}">
			           
		          	<html:link forward="forgotpassword" styleClass="headerLinks" titleKey="itracker.web.header.menu.forgotpass.alt">
		            	<it:message key="itracker.web.header.menu.forgotpass"/></html:link>
		        </c:if>  
		                <%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
		        <c:if test="${allowSelfRegister}">      
	                   | <html:link forward="selfregistration" styleClass="headerLinks" titleKey="itracker.web.header.menu.selfreg.alt">
	                     <it:message key="itracker.web.header.menu.selfreg"/></html:link>
		        </c:if>
	   		</c:if> 
        </td>
      </tr>
    </table>
    
    <p class="pageHeader"><%-- TODO: temp. removed code, fix this: <it:message key="<%= pageTitleKey %>" arg0="<%= pageTitleArg %>"/>--%></p>
 


    