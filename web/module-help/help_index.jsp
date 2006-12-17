<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
