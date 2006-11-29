<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

 

<%-- <it: checkLogin permission="< % = UserUtilities.PERMISSION_USER_ADMIN %>"/> --%>

<!-- once there was page_init here, but now this has been moved into the ItrackerBaseAction -->
<bean:define id="pageTitleKey" value="itracker.web.admin.import.load.title"/>
<bean:define id="pageTitleArg" value=""/>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="importForm"/>

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

<html:form action="/importdataverify" enctype="multipart/form-data">
  <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
    <tr>
      <td colspan="2" width="50%"></td>
      <td colspan="2" width="50%"></td>
    </tr>
    <tr>
      <td class="editColumnTitle" width="1%"><it:message key="itracker.web.attr.file"/>:&nbsp;&nbsp;</td>
      <td class="editColumnText" colspan="3" align="left"><html:file property="importFile" styleClass="editColumnText"/></td>
    </tr>

    <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
    <tr class="editColumnTitle"><td colspan="4"><it:message key="itracker.web.admin.import.options"/></td></tr>
    <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
    <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
    <tr>
      <td colspan="2" valign="top">
        <table width="100%" cellspacing="0"  cellspacing="1"  border="0">
          <tr>
            <td width="25"></td>
            <td></td>
          </tr>
          <tr>
            <td class="editColumnText"><html:checkbox property="optionreuseusers" value="true"/></td>
            <td class="editColumnText"><it:message key="itracker.web.admin.import.options.reuseusers"/></td>
          </tr>
          <tr>
            <td class="editColumnText"><html:checkbox property="optionreuseprojects" value="true"/></td>
            <td class="editColumnText"><it:message key="itracker.web.admin.import.options.reuseprojects"/></td>
          </tr>
          <tr>
            <td class="editColumnText"><html:checkbox property="optioncreatepasswords" value="true"/></td>
            <td class="editColumnText"><it:message key="itracker.web.admin.import.options.createpasswords"/></td>
          </tr>
        </table>
      </td>
      <td colspan="2" valign="top">
        <table width="100%" cellspacing="0"  cellspacing="1"  border="0">
          <tr>
            <td width="25"></td>
            <td></td>
          </tr>
          <tr>
            <td class="editColumnText"><html:checkbox property="optionreuseconfig" value="true"/></td>
            <td class="editColumnText"><it:message key="itracker.web.admin.import.options.reuseconfig"/></td>
          </tr>
          <tr>
            <td class="editColumnText"><html:checkbox property="optionreusefields" value="true"/></td>
            <td class="editColumnText"><it:message key="itracker.web.admin.import.options.reusefields"/></td>
          </tr>
        </table>
      </td>
    </tr>

    <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td></tr>
    <tr>
      <td align="left" colspan="4"><html:submit styleClass="button" altKey="itracker.web.button.import.alt" titleKey="itracker.web.button.import.alt"><it:message key="itracker.web.button.import"/></html:submit></td>
    </tr>
  </table>
  <br/>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
