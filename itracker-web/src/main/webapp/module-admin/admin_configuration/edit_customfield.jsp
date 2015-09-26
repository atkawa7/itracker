<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.admin.editcustomfield.title.create"/>
<bean:define id="pageTitleArg" value=""/>
<%--   redirect logic moved to Action --%>
        <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
        <tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
        
        <logic:messagesPresent>
            
            <span class="formError"> 
                <html:messages id="error">
                    <bean:write name="error"/><br/>
                </html:messages> 
            </span>
            
            <br/><br/><br/>
            
        </logic:messagesPresent>
        
        <html:form action="/editcustomfield">
            <html:hidden property="action"/>
            <html:hidden property="id"/>
            
            
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
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
                            <html:option value="${fieldTypeString}" styleClass="editColumnText"><it:message key="itracker.web.generic.string"/></html:option>
                            <html:option value="${fieldTypeInteger}" styleClass="editColumnText"><it:message key="itracker.web.generic.integer"/></html:option>
                            <html:option value="${fieldTypeDate}" styleClass="editColumnText"><it:message key="itracker.web.generic.date"/></html:option>
                            <html:option value="${fieldTypeList}" styleClass="editColumnText"><it:message key="itracker.web.generic.list"/></html:option>
                        </html:select>
                    </td>
                    <td></td>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
                    <td class="editColumnText"><it:formatDate date="${field.createDate}"/></td>
                </tr>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.required"/>:</td>
                    <td class="editColumnText">
                    	<html:checkbox property="required" value="true"><it:message key="itracker.web.generic.yes"/></html:checkbox>
                        <%--<html:select property="required" styleClass="editColumnText">
                            <html:option value="false" styleClass="editColumnText"><it:message key="itracker.web.generic.no"/></html:option>
                            <html:option value="true" styleClass="editColumnText"><it:message key="itracker.web.generic.yes"/></html:option>
                        </html:select>--%>
                    </td>
                    <td></td>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
                    <td class="editColumnText"><it:formatDate date="${field.lastModifiedDate}"/></td>
                </tr>
                <tr>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.sortoptions"/>:</td>
                    <td class="editColumnText">
                    	<html:checkbox property="sortOptionsByName" value="true"><it:message key="itracker.web.generic.yes"/></html:checkbox>
                        <%--<html:select property="sortOptionsByName" styleClass="editColumnText">
                            <html:option value="false" styleClass="editColumnText"><it:message key="itracker.web.generic.no"/></html:option>
                            <html:option value="true" styleClass="editColumnText"><it:message key="itracker.web.generic.yes"/></html:option>
                        </html:select>--%>
                    </td>
                    <td></td>
                    <td class="editColumnTitle"><it:message key="itracker.web.attr.dateformat"/>:</td>
                    <td>
                        <html:select property="dateFormat" styleClass="editColumnText">
                            <html:option value="${dateFormatDateOnly}" styleClass="editColumnText"><it:message key="itracker.web.attr.date.dateonly"/> (<it:message key="itracker.dateformat.dateonly"/>)</html:option>
<!--                            <html:option value="${dateFormatTimeonly}" styleClass="editColumnText"><it:message key="itracker.web.attr.date.timeonly"/> (<it:message key="itracker.dateformat.timeonly"/>)</html:option>-->
                            <html:option value="${dateFormatFull}" styleClass="editColumnText"><it:message key="itracker.web.attr.date.full"/> (<it:message key="itracker.dateformat.full"/>)</html:option>
                        </html:select>
                    </td>
                </tr>
                <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="12"/></td></tr>
                <tr>
                    <td colspan="5">
                        <table width="100%" cellspacing="0" cellpadding="1" border="0" class="shadeList">
                            <tr>
                              <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                              <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                              <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                              <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="15" height="1"/></td>
                            </tr>
                            <tr><td colspan="4" class="editColumnTitle"><it:message key="itracker.web.attr.translations"/>:</td></tr>
                            <tr class="listHeading"><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                            <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="4"/></td></tr>
                            <tr class="listRowShaded">

                                <td colspan="3" >
                                    <span class="systemDefault"><it:message key="itracker.customfield.${ field.id }.label" locale="BASE"/></span>
                                    <label for="BASE">
                                        <it:message key="itracker.web.attr.baselocale"/>
                                        (<it:link action="editlanguageform" targetAction="update"
                                          paramName="locale" paramValue="BASE"
                                          titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ languageNameValue.key.name }"
                                          styleClass="pre">BASE</it:link>)
                                    </label>
                                <td colspan="2" >
                                    <html:text property="${baseLocaleKey}" styleId="BASE" styleClass="editColumnText"/>
                                </td>
                            </tr>


                            <c:set var="i" value="0" />
                          	<c:forEach var="languageNameValue" items="${languagesNameValuePair}" varStatus="itStatus">
                                  <c:set var="listRowClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }" />
                                  <c:set var="i" value="${ i + 1 }" />

                                   <tr class="${listRowClass} level1">
                                    <td></td>
                                    <td colspan="2">
                                        <span class="systemDefault"><it:message key="itracker.customfield.${ field.id }.label" locale="${languageNameValue.key.name}"/></span>
                                        <label for="${languageNameValue.key.name}">
                                          ${languageNameValue.key.value}
                                              (<it:link action="editlanguageform" targetAction="update"
                                                paramName="locale" paramValue="${ languageNameValue.key.name }"
                                                titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ languageNameValue.key.name }"
                                                styleClass="pre">${languageNameValue.key.name}</it:link>)
                                        </label>
                                    </td>
                                    <td>
                                      <html:text property="translations(${languageNameValue.key.name})" styleId="${languageNameValue.key.name}" styleClass="editColumnText"/></td>

                                  </tr>
                          		<c:forEach var="locale" items="${languageNameValue.value }" varStatus="itLStatus">
                                      <c:set var="listRowClass" value="${ (i % 2 == 1) ? 'listRowShaded' : 'listRowUnshaded' }" />
                                      <c:set var="i" value="${ i + 1 }" />

                                      <tr class="${listRowClass} level2">
                                        <td></td>
                                        <td></td>
                                        <td>
                                            <span class="systemDefault"><it:message key="itracker.customfield.${ field.id }.label" locale="${locale.name}"/></span>
                                            <label for="${locale.name}">
                                              ${locale.value}
                                                  (<it:link action="editlanguageform" targetAction="update"
                                                    paramName="locale" paramValue="${ locale.name }"
                                                    titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ locale.name }"
                                                    styleClass="pre">${locale.name}</it:link>)
                                            </label>
                                        </td>
                                        <td>
                                          <html:text property="translations(${locale.name})" styleId="${locale.name}" styleClass="editColumnText"/></td>

                                      </tr>
                          	    </c:forEach>
                            </c:forEach>

                            <%-- REMOVE:

                            <c:forEach var="languageNameValue" items="${languagesNameValuePair}">
                            <tr class="listRowUnshaded">
                            	<td colspan="2" valign="top">
                                	${languageNameValue.key.value}
                                </td>
                                
                                <td colspan="2" valign="top">
                                    <html:textarea rows="2" cols="60" property="translations(${languageNameValue.key.name})"  styleClass="editColumnText"/>
                                </td>
                            </tr>
                           
                            <c:forEach var="locale" items="${languageNameValue.value }">
                            <tr class="listRowUnshaded">
                                <td colspan="2" valign="top">${locale.value}</td>
                                <td colspan="2" valign="top">
                                    <html:textarea rows="2" cols="60" property="translations(${locale.name})" styleClass="editColumnText"/>
                                </td>
                            </tr>
                            
                            	</c:forEach>
                            </c:forEach> --%>
                        </table>
                    </td>
                    <td></td>
          
                    
                <c:if test="${field.fieldType.code == CustomFieldType_List}">
                    <td colspan="2" valign="top">
                        <table cellspacing="0" cellspacing="1" border="0">
                            <tr>
                                <td align="right" colspan="2" class="editColumnTitle"><it:message key="itracker.web.attr.fieldoptions"/>:</td>
                                <td align="right" class="listRowUnshaded"><span align="right"><it:link action="editcustomfieldvalueform" targetAction="create" paramName="id" paramValue="${field.id}" titleKey="itracker.web.admin.editcustomfield.option.create.alt"><it:message key="itracker.web.admin.editcustomfield.option.create"/></it:link></span></td>
                            </tr>
                            <tr class="listHeading"><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
                            <tr><td colspan="3"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="1" height="4"/></td></tr>
                            <c:forEach items="${options}" var="option" varStatus="i">
                                
                                <tr>
                                    <td class="listRowUnshaded" style="white-space: nowrap;" nowrap="nowrap">
                                        <it:link action="editcustomfieldvalueform" targetAction="update" paramName="id" paramValue="${option.id}" titleKey="itracker.web.admin.editcustomfield.option.edit.alt"><it:message key="itracker.web.admin.editcustomfield.option.edit"/></it:link>
                                        <it:link action="removecustomfieldvalue" targetAction="delete" paramName="id" paramValue="${option.id}" titleKey="itracker.web.admin.editcustomfield.option.delete.alt"><it:message key="itracker.web.admin.editcustomfield.option.delete"/></it:link>
                                        <c:if test="${i.index != 0}">
                                            <it:link action="ordercustomfieldvalue" targetAction="up" paramName="id" paramValue="${option.id}" titleKey="itracker.web.admin.editcustomfield.option.orderup.alt">
                                                <it:message key="itracker.web.admin.editcustomfield.option.orderup"/>
                                            </it:link>
                                        </c:if>
                                        <c:if test="${i.index lt fn:length(options)-1 }">
                                            <it:link action="ordercustomfieldvalue" targetAction="down" paramName="id" paramValue="${option.id}" titleKey="itracker.web.admin.editcustomfield.option.orderdown.alt">
                                                <it:message key="itracker.web.admin.editcustomfield.option.orderdown"/>
                                            </it:link>
                                        </c:if>
                                        <html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/>
                                    </td>
                                    
                                    <td align="right" colspan="2" class="editColumnText">
                                    	${ optionsMap[option.id]  } (${  option.value }) 
                                    </td>
                                </tr>
                                
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
 


