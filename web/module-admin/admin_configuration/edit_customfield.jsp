<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="org.itracker.services.*" %>
<%@ page import="org.itracker.core.resources.ITrackerResources" %>
<%@ page import="org.itracker.web.util.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ page import="org.itracker.services.util.CustomFieldUtilities" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Marky: check the NitroX types here. Are they right? -->
<%-- <nitrox:var name="action" type="java.lang.String"/> --%>
<%-- <nitrox:var name="languages" type="java.lang.String"/> --%>
<%-- <nitrox:var name="CustomFieldType_List" type="java.lang.String"/> --%>

<bean:define id="pageTitleKey" value="itracker.web.admin.editcustomfield.title.create"/>
<bean:define id="pageTitleArg" value=""/>

<bean:define id="field" name="<%=Constants.CUSTOMFIELD_KEY%>" type="org.itracker.model.CustomField"/>

<%-- TODO : move redirect logic to Action --%>
<c:choose>
    <c:when test="${field == null}">
        <logic:forward name="unauthorized"/>
    </c:when>
    <c:otherwise>
        
        <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
        <tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
        
        <logic:messagesPresent>
            
            <span class="formError"> 
                <html:messages id="error">
                    <bean:write name="error"/><br/>
                </html:messages> 
            </span>
            
            <br/>
        </logic:messagesPresent>
        <br/><br/>
        
        <html:form action="/editcustomfield">
            <html:hidden property="action"/>
            <html:hidden property="id"/>
            
            <%
            ConfigurationService sc = (ConfigurationService)request.getAttribute("sc");
            Map<String,List<String>> languages = sc.getAvailableLanguages();
            %>
            <table border="0" cellspacing="0" cellspacing="1" width="800px">
                <tr>
                    <td colspan="2" width="48%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                    <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="10" height="1"/></td>
                    <td colspan="2" width="48%"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                </tr>
                <c:if test="${action == 'update'}">
                    <tr>
                        <td class="editColumnTitle"><it:message key="itracker.web.attr.id"/>:</td>
                        <td class="editColumnText">${field.id}</td>
                    </tr>
                </c:if>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.fieldtype"/>:</td>
                    <td>
                        <html:select property="fieldType" styleClass="editColumnText">
                            <html:option value="<%= Integer.toString(CustomFieldUtilities.TYPE_STRING) %>" styleClass="editColumnText"><it:message key="itracker.web.generic.string"/></html:option>
                            <html:option value="<%= Integer.toString(CustomFieldUtilities.TYPE_INTEGER) %>" styleClass="editColumnText"><it:message key="itracker.web.generic.integer"/></html:option>
                            <html:option value="<%= Integer.toString(CustomFieldUtilities.TYPE_DATE) %>" styleClass="editColumnText"><it:message key="itracker.web.generic.date"/></html:option>
                            <html:option value="<%= Integer.toString(CustomFieldUtilities.TYPE_LIST) %>" styleClass="editColumnText"><it:message key="itracker.web.generic.list"/></html:option>
                        </html:select>
                    </td>
                    <td></td>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
                    <td class="editColumnText"><it:formatDate date="${field.createDate}"/></td>
                </tr>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.required"/>:</td>
                    <td>
                        <html:select property="required" styleClass="editColumnText">
                            <html:option value="false" styleClass="editColumnText"><it:message key="itracker.web.generic.no"/></html:option>
                            <html:option value="true" styleClass="editColumnText"><it:message key="itracker.web.generic.yes"/></html:option>
                        </html:select>
                    </td>
                    <td></td>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
                    <td class="editColumnText"><it:formatDate date="${field.lastModifiedDate}"/></td>
                </tr>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.sortoptions"/>:</td>
                    <td>
                        <html:select property="sortOptionsByName" styleClass="editColumnText">
                            <html:option value="false" styleClass="editColumnText"><it:message key="itracker.web.generic.no"/></html:option>
                            <html:option value="true" styleClass="editColumnText"><it:message key="itracker.web.generic.yes"/></html:option>
                        </html:select>
                    </td>
                    <td></td>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.dateformat"/>:</td>
                    <td>
                        <html:select property="dateFormat" styleClass="editColumnText">
                            <html:option value="<%= CustomFieldUtilities.DATE_FORMAT_DATEONLY %>" styleClass="editColumnText"><it:message key="itracker.web.attr.date.dateonly"/> (<it:message key="itracker.dateformat.dateonly"/>)</html:option>
                            <html:option value="<%= CustomFieldUtilities.DATE_FORMAT_TIMEONLY %>" styleClass="editColumnText"><it:message key="itracker.web.attr.date.timeonly"/> (<it:message key="itracker.dateformat.timeonly"/>)</html:option>
                            <html:option value="<%= CustomFieldUtilities.DATE_FORMAT_FULL %>" styleClass="editColumnText"><it:message key="itracker.web.attr.date.full"/> (<it:message key="itracker.dateformat.full"/>)</html:option>
                        </html:select>
                    </td>
                </tr>
                <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
                <tr>
                <td colspan="2" valign="top">
                    <table width="100% cellspacing="0" cellpadding="0" border="0" src="../../images/blank.gif">
                           <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.translations"/>:</td></tr>
                        <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                        <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="4"/></td></tr>
                        <tr class="listRowUnshaded">
                            <td colspan="2" valign="top"><it:message key="itracker.web.attr.baselocale"/></td>
                            <% String localeKey = "translations(" + ITrackerResources.BASE_LOCALE + ")"; %>
                            <td colspan="2" valign="top">
                                <html:textarea rows="2" cols="60" property="<%=localeKey%>" styleClass="editColumnText"/>
                            </td>
                        </tr>
                        <%
                        for(java.util.Iterator<String> iter = languages.keySet().iterator(); iter.hasNext(); ) {
                                String language = iter.next();
                                List<String> locales = (List<String>) languages.get(language);
                        %>
                        <tr class="listRowUnshaded">
                            <td colspan="2" valign="top">
                                <%= ITrackerResources.getString("itracker.locale.name", language) %>
                            </td>
                            <% localeKey = "translations(" + language + ")"; %>
                            <c:set var="localeKey" value="<%=localeKey%>"/>
                            <td colspan="2" valign="top">
                                <html:textarea rows="2" cols="60" property="<%=localeKey%>"  styleClass="editColumnText"/>
                            </td>
                        </tr>
                        <%      for(int i = 0; i < locales.size(); i++) { %>
                        <tr class="listRowUnshaded">
                            <td colspan="2" valign="top"><%= ITrackerResources.getString("itracker.locale.name", (String) locales.get(i)) %></td>
                            <% localeKey = "translations(" + (String) locales.get(i) + ")"; %>
                            <c:set var="localeKey" value="<%=localeKey%>"/>
                            <td colspan="2" valign="top">
                                <html:textarea rows="2" cols="60" property="<%=localeKey%>" styleClass="editColumnText"/>
                            </td>
                        </tr>
                        <%      }
                        }
                        %>
                    </table>
                </td>
                <td></td>
                <!-- To Kimba from Marky: I see problems when you try to assign a Primitve such as boolean or int
 	to the pageContext.setAttribute("String",Object) method because this method only takes Objects, but no Primitives. 
 	 -->
                <%
                pageContext.setAttribute("CustomFieldType_List",String.valueOf(CustomFieldUtilities.TYPE_LIST));
                %>
                <c:if test="${field.fieldType == CustomFieldType_List}">
                    <td colspan="2" valign="top">
                        <table cellspacing="0" cellspacing="1" border="0">
                            <tr>
                                <td align="right" colspan="2" class="editColumnTitle"><it:message key="itracker.web.attr.fieldoptions"/>:</td>
                                <td align="right" class="listRowUnshaded"><span align="right"><it:link action="editcustomfieldvalueform" targetAction="create" paramName="id" paramValue="${field.id}" titleKey="itracker.web.admin.editcustomfield.option.create.alt"><it:message key="itracker.web.admin.editcustomfield.option.create"/></it:link></span></td>
                            </tr>
                            <tr class="listHeading"><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                            <tr><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="4"/></td></tr>
                            
                            <c:set var="f_id" value="${field.id}"/>
                            <c:set var="opt_len" value="0"/>
                            <c:set var="idx" value="1"/>
                            <c:forEach items="${field.options}" var="i">
                                <c:set var="opt_len" value="${opt_len + 1}"/>
                            </c:forEach>
                            <c:forEach items="${field.options}" var="i">
                                <c:set var="o_id" value="${i.id}"/>
                                <tr>
                                    <td class="listRowUnshaded" style="white-space: nowrap;" nowrap>
                                        <it:link action="editcustomfieldvalueform" targetAction="update" paramName="id" paramValue="${i.id}" titleKey="itracker.web.admin.editcustomfield.option.edit.alt"><it:message key="itracker.web.admin.editcustomfield.option.edit"/></it:link>
                                        <it:link action="removecustomfieldvalue" targetAction="delete" paramName="id" paramValue="${i.id}" titleKey="itracker.web.admin.editcustomfield.option.delete.alt"><it:message key="itracker.web.admin.editcustomfield.option.delete"/></it:link>
                                        <!-- c:if test="$ {idx != 1}" -->
                                            <!-- it:link action="ordercustomfieldvalue" targetAction="up" paramName="id" paramValue="$ {i.id}" titleKey="itracker.web.admin.editcustomfield.option.orderup.alt" -->
                                                <!-- it:message key="itracker.web.admin.editcustomfield.option.orderup"/ -->
                                            <!-- /it:link -->
                                        <!-- /c:if -->
                                        <!-- c:if test="$ {idx != opt_len}" -->
                                            <!-- it:link action="ordercustomfieldvalue" targetAction="down" paramName="id" paramValue="$ {i.id}" titleKey="itracker.web.admin.editcustomfield.option.orderdown.alt" -->
                                                <!-- it:message key="itracker.web.admin.editcustomfield.option.orderdown"/ -->
                                            <!-- /it:link -->
                                        <!-- /c:if -->
                                        
                                        <html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/>
                                        </td>
                                    <%
                                    String field_id = pageContext.getAttribute("f_id").toString();
                                    String option_id = pageContext.getAttribute("o_id").toString();
                                    Integer fld_id = Integer.valueOf(field_id);
                                    Integer opt_id = Integer.valueOf(option_id);
                                    %>
                                    <td align="right" colspan="2" class="editColumnText">
                                        <%=CustomFieldUtilities.getCustomFieldOptionName(fld_id,opt_id)%>
                                    </td>
                                </tr>
                                <c:set var="idx" value="${idx + 1}"/>
                            </c:forEach>
                        </table>
                    </td>
                </c:if>
                
                <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
                <c:choose>
                    <c:when test="${action == 'update'}">
                        <tr><td colspan="5" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="5" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
                    </c:otherwise>
                </c:choose>
            </table>
        </html:form>
        
        
        <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
    </c:otherwise>
</c:choose>


