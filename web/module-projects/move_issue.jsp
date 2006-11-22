<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<%-- <it:checkLogin/> --%>

 
         <bean:define id="pageTitleKey" value="itracker.web.moveissue.title"/>
      <%--bean:define id="pageTitleArg" value="${issue.id}"/>--%>
     <%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

      <html:javascript formName="moveIssueForm"/>

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

     <it:message key="itracker.web.moveissue.instructions"/>
      <br/>
      <br/>

      <html:form action="/moveissue" method="post" onsubmit="return validateMoveIssueForm(this);">
        <html:hidden property="issueId"/>
        <html:hidden property="caller"/>

        <table border="0" cellspacing="0"  cellspacing="1">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.issue"/>: &nbsp;</td>
            <td class="editColumnText">${issue.id}</td>
            <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.project"/>: &nbsp;</td>
            <td class="editColumnText">
              <html:select property="projectId" styleClass="editColumnText">
                <option value=""></option>
           <c:forEach items="${projects}" var="projects"  step="1">
             <html:option value="${projects.id}">${projects.name}</html:option>
           </c:forEach>
              </html:select>
            </td>
          </tr>
          <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="18"/></td></tr>
          <tr><td colspan="5" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
        </table>

        <br/>

      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
       
        	