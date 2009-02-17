<%@ include file="/common/taglibs.jsp"%>

<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.web.util.*" %>
<%@ page import="org.itracker.web.forms.*" %>
<%@ page import="org.itracker.services.util.*" %>

<%@ page import="java.util.List" %>

<bean:parameter id="action" name="action"/>
  
<% // TODO : move redirect logic to the Action class. 
   ProjectScriptForm projectScriptForm = (ProjectScriptForm) request.getAttribute("projectScriptForm");
   Locale locale = LoginUtilities.getCurrentLocale(request);
   boolean isUpdate = false;
    
   if ( "update".equals(action) )
        isUpdate = true;
     Project project = (Project) session.getAttribute(Constants.PROJECT_SCRIPT_KEY);

    if(project == null) {
%>
        <logic:forward name="unauthorized"/>
<%  } else { %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
        <tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

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

	<html:form action="/editprojectscript" acceptCharset="UTF-8" enctype="multipart/form-data">
            <html:hidden property="action"/>
            <html:hidden property="projectId"/>
            <table border="0" cellspacing="0" cellspacing="1"  width="80%">
                <tr>
                    <th valign="top" align="center">Add</th>
                    <th valign="top" align="left">Script Name</th>
                    <th valign="top" align="left">Field Id</th>
                    <th valign="top" align="left">Priority</th>
                </tr>
                <tr>
                    <td colspan="4" ><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1"/></td>
                </tr>
                <% 
/*                    Integer[] Ids = projectScriptForm.getId();
                    Integer[] fieldIds = projectScriptForm.getFieldId();
                    Integer[] priorities = projectScriptForm.getPriority();
                    Integer[] wsfIds = new Integer[projectScriptForm.getScriptItems().size()];
*/
                    int j = -1; 
                    List<CustomField> cfFields = projectScriptForm.getCustomFields();
                %>
                    <logic:iterate indexId="idx" id="itemlangs" name="projectScriptForm" property="scriptDescs">
                        <bean:define name="itemlangs" property="key" id="key" type="java.lang.String"/>
                        <bean:define name="itemlangs" property="value" id="value" type="java.lang.String"/>
                        <% 
                           
                            String descKey = "scriptDescs("+key+")" ;
                            String chkboxKey = "scriptItems("+key+")" ;
                            String fieldIdKey = "fieldId("+key+")" ;
                            String priorityKey = "priority("+key+")" ;
                            String idKey = "id("+key+")" ;
                            String idxcnt = "idx"+String.valueOf(j);
                            j++; 
                            String styleClass = (j % 2 == 1 ? "listRowShaded" : "listRowUnshaded" ); 
                            int indx = 0;
                        %>
                        <c:set var="fieldId" value="<%=projectScriptForm.getFieldId().get(key)%>"/>
                        <html:hidden indexed="false" property="<%=idKey%>"/>
                        <tr class="<%=styleClass%>">
                           <td valign="top" align="center">
                               <html:checkbox indexed="false" name="projectScriptForm" property="<%=chkboxKey%>" value="on"/>                             
                           </td> 
                           <td valign="top" align="left"><%=value%></td>
                           <td valign="top" align="left">
                                <html:select property="<%=fieldIdKey%>" styleClass="editColumnText" value="${ fieldId }">
                                    <c:forEach items="${projectScriptForm.customFields}" var="customfield" varStatus="k">
                                        <%  CustomField cfField = (CustomField) cfFields.get(indx);
                                            String name = CustomFieldUtilities.getCustomFieldName(cfField.getId(), locale);
                                            name += " ";
                                            name += CustomFieldUtilities.getTypeString(cfField.getFieldType(), locale);
                                            indx++;
                                        %>
                                        <option value="${customfield.id}" >
                                            <%--c:if test="${customfield.id == fieldId}">
                                            	<jsp:attribute name="selected">selected</jsp:attribute>
                                            </c:if--%>
                                            <%= name %>
                                        </option>
                                    </c:forEach>
                                </html:select>
                           </td>
                           <td valign="top" align="left">
                                <c:set var="priorityId" value="<%=projectScriptForm.getPriority().get(key)%>"/>
                                <html:select property="<%=priorityKey%>" styleClass="editColumnText" value="${ priorityId }">
                                    <c:forEach items="${projectScriptForm.priorityList}" var="priority" varStatus="k">
                                        <option value="${priority.key}">
                                            <%--c:if test="${priority.key == priorityId}">
                                            	<jsp:attribute name="selected">selected</jsp:attribute>
                                            </c:if--%>
											${priority.value}
                                        </option>
                                    </c:forEach>
                                </html:select>
                           </td>
                        </tr>
                       
                    </logic:iterate>
                <tr>
                    <td colspan="4" ><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="1"/></td>
                </tr>
            </table>
                       
                 <% if (isUpdate) { %>
                        <html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit>
                 <% } else { %>
                        <html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit>
                 <% } %>
 	</html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
<%@page import="java.util.Locale"%></html>
<%  } %>