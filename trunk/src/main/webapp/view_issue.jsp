<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<%@page import="java.util.Hashtable"%><bean:define id="params" toScope="request">
<% 
	request.setAttribute("id", request.getParameter("id"));
%>

</bean:define>
<logic:redirect forward="viewissue" paramScope="request" paramName="id" paramId="id"></logic:redirect>