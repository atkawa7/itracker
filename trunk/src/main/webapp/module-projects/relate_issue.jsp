<%@ page contentType="text/html;charset=UTF-8" %>
 
<%@ page import="org.itracker.services.util.IssueUtilities" %>
 
<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% // TODO : move redirect logic to the Action class. 
    String issueId = (String) request.getParameter("id");
    if(issueId == null || issueId.equals("")) {
%>
      <logic:forward name="unauthorized"/>
<%  } else { %>
        <bean:define id="pageTitleKey" value="itracker.web.relateissue.title"/>
        <bean:define id="pageTitleArg" value="<%= issueId %>"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

        <html:form action="/addissuerelation" onsubmit="validateIssueRelationForm(this);">

        <html:javascript formName="issueRelationForm"/>

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

        <br/>
        <html:hidden property="issueId" value="<%= issueId %>"/>
        <html:hidden property="caller" value="<%= (String) request.getParameter("caller") %>"/>
        <table width="100%" cellspacing="1"  cellspacing="0"  border="0" align="left">
          <tr><td align="left">
            <span class="editColumnText"><it:message key="itracker.web.attr.thisissue" /></span>
            <html:select property="relationType" styleClass="editColumnText">
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_RELATED_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_RELATED_P, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_P, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_C, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_P, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_C, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
            </html:select>
            <span class="editColumnText"><it:message key="itracker.web.attr.issue"/></span>
            <html:text size="5" property="relatedIssueId" styleClass="editColumnText"/>.
          </td></tr>
          <tr><td><html:img width="1" height="18" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
          <tr><td align="left">
            <html:submit styleClass="button" altKey="itracker.web.button.add.alt" titleKey="itracker.web.button.add.alt"><it:message key="itracker.web.button.add"/></html:submit>
          </td></tr>
        </table>
        </html:form>
        <br/>

        <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>