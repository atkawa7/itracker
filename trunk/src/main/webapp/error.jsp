<%@ include file="/common/taglibs.jsp"%>
<%@ page language="java" isErrorPage="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
    <div id="content">
    <h1><it:message key="errorPage.heading"/></h1>
    <%-- Error Messages --%>
    <logic:messagesPresent>
        <div class="error">
            <html:messages id="error">
                <bean:write name="error" filter="false"/><br/>
            </html:messages>
        </div>
    </logic:messagesPresent>
 <% if (exception != null) { %>
    <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>
 <% } else if ((Exception)request.getAttribute("javax.servlet.error.exception") != null) { %>
    <pre><% ((Exception)request.getAttribute("javax.servlet.error.exception"))
                           .printStackTrace(new java.io.PrintWriter(out)); %></pre>
 <% } else if (pageContext.findAttribute("org.apache.struts.action.EXCEPTION") != null) { %>
    <bean:define id="exception2" name="org.apache.struts.action.EXCEPTION"
     type="java.lang.Exception"/>
    <c:if test="exception2 != null">
        <pre><% exception2.printStackTrace(new java.io.PrintWriter(out));%></pre>
    </c:if>
    <%-- only show this if no error messages present --%>
    <c:if test="exception2 == null">
        <bean:message key="errors.none"/>
    </c:if>
 <% } %>
    </div>
</body>
</html>
