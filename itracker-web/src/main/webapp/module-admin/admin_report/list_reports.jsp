<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listreports.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<c:set var="pageTitleKey" scope="request">itracker.web.admin.listreports.title</c:set>
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

<table class="shadeList" style="border: none; padding: 1px; border-spacing: 0; width: 100%">
   <tr>
      <td class="editColumnTitle" colspan="6"><it:message key="itracker.web.attr.reports"/>:</td>
      <td style="text-align: right">
         <it:formatIconAction action="editreportform" targetAction="create"
                               icon="plus" iconClass="fa-2x"
                               info="itracker.web.image.create.report.alt"
                               textActionKey="itracker.web.image.create.texttag"/>
      </td>
   </tr>
   <tr class="listHeading">
      <td></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
      <td><it:message key="itracker.web.attr.report"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
      <td><it:message key="itracker.web.attr.description"/></td>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="4" height="1"/></td>
      <td><it:message key="itracker.web.attr.lastmodified"/></td>
   </tr>

   <c:forEach items="${reports}" var="report" varStatus="i">
      <c:choose>
         <c:when test="${i.count % 2 == 1}">
            <tr class="listRowShaded">
         </c:when>
         <c:otherwise>
            <tr class="listRowUnshaded">
         </c:otherwise>
      </c:choose>

      <td>
         <it:formatIconAction action="editreportform" paramName="id" paramValue="${report.id}" targetAction="update"
                              icon="edit" info="itracker.web.image.edit.report.alt" iconClass="fa-lg"
                              arg0="${report.name}" textActionKey="itracker.web.image.edit.texttag"/>
         <it:formatIconAction action="downloadreport" paramName="id" paramValue="${report.id}"
                              icon="download" info="itracker.web.image.download.report.alt"
                              iconClass="fa-lg"
                              arg0="${report.name}"
                              textActionKey="itracker.web.image.download.texttag"/>
         <it:formatIconAction action="removereport" paramName="id" paramValue="${report.id}"
                              icon="remove" iconClass="fa-lg"
                              info="itracker.web.image.delete.report.alt" arg0="${report.name}"
                              textActionKey="itracker.web.image.delete.texttag"/>
      </td>
      <td></td>
      <td>${report.name}</td>
      <td></td>
      <td><it:formatDescription truncateLength="60">${report.description}</it:formatDescription></td>
      <td></td>
      <td style="text-align: right"><it:formatDate date="${report.lastModifiedDate}"/></td>

      </tr>

   </c:forEach>

</table>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
