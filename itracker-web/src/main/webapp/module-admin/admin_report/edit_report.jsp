<%@ include file="/common/taglibs.jsp" %>

<%@ page import="org.itracker.model.Report" %>
<%@ page import="org.itracker.web.util.Constants" %>

<% // TODO : move redirect logic to the Action class.
   Report report = (Report) session.getAttribute(Constants.REPORT_KEY);

   boolean isUpdate = false;
   if (report == null) {
%>
<logic:forward name="unauthorized"/>
<%
} else {
   if (report.getId().intValue() > 0) {
      isUpdate = true;
   }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<c:choose>
   <c:when test="${ reportForm.action == 'update' }">
      <c:set var="pageTitleKey" scope="request">itracker.web.admin.editreport.title.update</c:set>
   </c:when>
   <c:otherwise>
      <c:set var="pageTitleKey" scope="request">itracker.web.admin.editreport.title.create</c:set>
   </c:otherwise>
</c:choose>
<c:set var="pageTitleArg" value="" scope="request"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>

<html:form action="/editreport" enctype="multipart/form-data" acceptCharset="utf-8">
   <html:hidden property="action"/>
   <html:hidden property="id"/>

   <table border="0" cellspacing="0" cellspacing="1" width="100%">
      <tr>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.name"/>:</td>
         <td><html:text property="name" styleClass="editColumnText"/></td>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
         <td class="editColumnText"><it:formatDate date="<%= report.getCreateDate() %>"/></td>
      </tr>
      <tr>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.namekey"/>:</td>
         <td><html:text property="nameKey" styleClass="editColumnText" size="30"/></td>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
         <td class="editColumnText"><it:formatDate date="<%= report.getLastModifiedDate() %>"/></td>
      </tr>
      <tr>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
         <td colspan="3"><html:text property="description" styleClass="editColumnText" size="80"/></td>
      </tr>

      <tr>
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td>
      </tr>
      <tr>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.reportdefinition"/>:</td>
         <td colspan="3" classs="editColumnText"><html:file property="fileDataFile" styleClass="editColumnText"/></td>
      </tr>

      <c:set var="fileData" value="${reportForm.fileData}"/>
      <logic:notEmpty name="fileData">
         <tr>
            <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10"
                                      width="1"/></td>
         </tr>
         <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.reportcontent"/>:</td>
            <td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10"
                                      width="1"/></td>
         <tr>
            <td colspan="4" classs="editColumnText">

               <pre style="width: 80em; height: 40em; overflow: scroll;"><c:out value="${fileData}"
                                                                                escapeXml="true"/></pre>
                  <%--html:textarea readonly="${true}" cols="120" rows="10" property="fileData" styleClass="editColumnText"/--%>
            </td>
         </tr>

      </logic:notEmpty>
      <tr>
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td>
      </tr>
      <% if (isUpdate) { %>
      <tr>
         <td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt"
                                                   titleKey="itracker.web.button.update.alt"><it:message
                 key="itracker.web.button.update"/></html:submit></td>
      </tr>
      <% } else { %>
      <tr>
         <td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt"
                                                   titleKey="itracker.web.button.create.alt"><it:message
                 key="itracker.web.button.create"/></html:submit></td>
      </tr>
      <% } %>
   </table>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<% } %>
