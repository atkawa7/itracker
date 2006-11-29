<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<%-- <it:checkLogin/> --%>

<table width="100%" border="0" cellspacing="1"  cellspacing="0">
  <tr>
    <td>
      <span class="pageHeader"><it:message key="itracker.web.helpindex.title"/></span><br/>
      <ul>
        <li><html:link page="/show_help.jsp?page=ct"><it:message key="itracker.web.helpindex.commontasks"/></html:link></li>
        <li><html:link page="/show_help.jsp?page=ab"><it:message key="itracker.web.helpindex.about"/></html:link></li>
      </ul>
    </td>
  </tr>
</table>
