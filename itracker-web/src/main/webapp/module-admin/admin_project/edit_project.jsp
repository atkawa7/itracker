<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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

<table>
   <tr>
      <td><html:form action="/editproject" acceptCharset="UTF-8" enctype="multipart/form-data">
         <html:hidden property="action"/>
         <html:hidden property="id"/>

         <table border="0" cellspacing="0" width="800">
            <tr>
               <td class="editColumnTitle"><it:message
                       key="itracker.web.attr.name"/>:
               </td>
               <td><html:text property="name" styleClass="editColumnText"/></td>
               <td valign="top" class="editColumnTitle"><it:message
                       key="itracker.web.attr.status"/>:
               </td>
               <td valign="top" class="editColumnText"><html:select
                       property="status" styleClass="editColumnText">
                  <c:forEach var="status" items="${statuses}">
                     <html:option styleClass="editColumnText" value="${status.value}">${status.name}
                     </html:option>
                  </c:forEach>
               </html:select></td>
            </tr>
            <tr>
               <td class="editColumnTitle"><it:message
                       key="itracker.web.attr.description"/>:
               </td>
               <td><html:text property="description"
                              styleClass="editColumnText"/></td>
               <td class="editColumnTitle"><it:message
                       key="itracker.web.attr.created"/>:
               </td>
               <td class="editColumnText"><it:formatDate
                       date="${project.createDate}"/></td>
            </tr>

            <tr>
               <td valign="top" class="editColumnTitle"><it:message
                       key="itracker.web.attr.owners"/>:
               </td>
               <td valign="top" class="editColumnText">
                  <html:select
                          property="owners" size="5" multiple="true"
                          styleClass="editColumnText">
                     <c:forEach var="owner" items="${owners}">
                        <html:option value="${owner.id}">${owner.firstName}&nbsp;${owner.lastName}
                        </html:option>
                     </c:forEach>
                  </html:select>
               </td>

               <c:choose>
                  <c:when test="${isUpdate}">

                     <td class="editColumnTitle" valign="top"><it:message
                             key="itracker.web.attr.lastmodified"/>:
                     </td>
                     <td class="editColumnText" valign="top"><it:formatDate
                             date="${project.lastModifiedDate}"/></td>
                  </c:when>
                  <c:otherwise>
                     <c:choose>
                        <c:when test="${allowPermissionUpdateOption}">

                           <td valign="top" class="editColumnTitle"><it:message
                                   key="itracker.web.admin.editproject.addusers"/>:
                           </td>
                           <td valign="top" class="editColumnText" nowrap="nowrap"><html:select
                                   property="users" size="5" multiple="true"
                                   styleClass="editColumnText">
                              <c:forEach var="user" items="${users}">
                                 <html:option value="${user.id}">${user.firstName}&nbsp;${user.lastName}
                                 </html:option>

                              </c:forEach>

                           </html:select> <html:select property="permissions" size="5" multiple="true"
                                                       styleClass="editColumnText">
                              <c:forEach var="permission" items="${permissions}">
                                 <html:option value="${permission.value}">${permission.name}
                                 </html:option>
                              </c:forEach>
                           </html:select></td>
                        </c:when>
                     </c:choose>
                  </c:otherwise>
               </c:choose>

            </tr>
            <tr>
               <td colspan="4"><html:img module="/"
                                         page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/>
               </td>
            </tr>

            <tr class="editColumnTitle">
               <td colspan="4"><it:message
                       key="itracker.web.admin.editproject.options"/>:
               </td>
            </tr>
            <tr class="listHeading">
               <td colspan="4"><html:img module="/"
                                         page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
            </tr>
            <tr>
               <td colspan="4"><html:img module="/"
                                         page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td>
            </tr>
            <tr>
               <td width="25"></td>
               <td></td>
            </tr>
            <tr>
               <td colspan="2" class="editColumnText">
                  <html:multibox property="options"
                                 value="${optionSupressHistoryHtml}"/>
                  <it:message
                          key="itracker.web.admin.editproject.options.html"/>
               </td>

               <td colspan="2" class="editColumnText">
                  <html:multibox property="options"
                                 value="${optionLiteralHistoryHtml}"/>
                  <it:message
                          key="itracker.web.admin.editproject.options.htmlliteral"/>
               </td>
            </tr>
            <tr>
               <td colspan="2" class="editColumnText">
                  <html:multibox property="options"
                                 value="${optionPredefinedResolutions}"/>
                  <it:message
                          key="itracker.web.admin.editproject.options.resolution"/>
               </td>

               <td colspan="2" class="editColumnText">
                  <html:multibox property="options"
                                 value="${optionNoAttachments}"/>
                  <it:message
                          key="itracker.web.admin.editproject.options.noattach"/>
               </td>
            </tr>
            <tr>
               <td colspan="2" class="editColumnText">
                  <html:multibox property="options"
                                 value="${optionAllowAssignToClose}"/>
                  <it:message
                          key="itracker.web.admin.editproject.options.closed"/></td>
               <c:if test="${allowSelfRegister}">

                  <td colspan="2" class="editColumnText">
                     <html:multibox
                             property="options" value="${optionAllowSelfRegisteredViewAll}"/>
                     <it:message
                             key="itracker.web.admin.editproject.options.srview"/>
                  </td>
               </c:if>
            </tr>
            <c:if test="${allowSelfRegister}">
               <tr>
                  <td colspan="2" class="editColumnText">
                     <html:multibox
                             property="options" value="${optionAllowSefRegisteredCreate}"/>
                     <it:message
                             key="itracker.web.admin.editproject.options.srcreate"/>
                  </td>
               </tr>
            </c:if>

            <tr>
               <td colspan="4"><html:img module="/"
                                         page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td>
            </tr>

            <c:if test="${ not empty customFields }">

               <tr class="editColumnTitle">
                  <td colspan="4"><it:message
                          key="itracker.web.attr.customfields"/>:
                  </td>
               </tr>
               <tr class="listHeading">
                  <td colspan="4"><html:img module="/"
                                            page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
               </tr>
               <tr>
                  <td colspan="4"><html:img module="/"
                                            page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td>
               </tr>


               <c:forEach var="customField" items="${ customFields }" step="2" varStatus="i">
                  <tr>
                     <td width="25"></td>
                     <td></td>
                  </tr>
                  <tr>
                     <td colspan="2" class="editColumnText">
                        <html:multibox
                                property="fields" value="${customField.id}"/>
                           ${customField.name} (${customField.type})
                     </td>
                     <c:forEach var="customField" items="${ customFields }" begin="${ i.index + 1 }"
                                end="${ i.index + 1 }">
                        <td colspan="2" class="editColumnText">
                           <html:multibox
                                   property="fields" value="${customField.id}"/>
                              ${customField.name} (${customField.type})
                        </td>
                     </c:forEach>
                  </tr>

               </c:forEach>


               <tr>
                  <td colspan="4"><html:img module="/"
                                            page="/themes/defaulttheme/images/blank.gif" height="15"
                                            width="1"/></td>
               </tr>

            </c:if>

            <c:choose>
               <c:when test="${isUpdate}">

                  <tr>
                     <td colspan="4" align="left"><html:submit
                             styleClass="button" altKey="itracker.web.button.update.alt"
                             titleKey="itracker.web.button.update.alt">
                        <it:message key="itracker.web.button.update"/>
                     </html:submit></td>
                  </tr>

               </c:when>
               <c:otherwise>
                  <tr>
                     <td colspan="4" align="left"><html:submit
                             styleClass="button" altKey="itracker.web.button.create.alt"
                             titleKey="itracker.web.button.create.alt">
                        <it:message key="itracker.web.button.create"/>
                     </html:submit></td>
                  </tr>
               </c:otherwise>
            </c:choose>

         </table>
      </html:form></td>
   </tr>
   <tr>
      <td><c:if test="${isUpdate}">
         <table style="border: none; padding: 1px; border-spacing: 0; width: 100%">

               <%-- TODO: this should be tested more, or postponed for next release? --%>

               <%-- REVIEW: Should it be possible to assign same script on different fields of the same project (HBM index)? --%>
            <c:if test="${currUser.superUser}">
               <tr>
                  <td class="editColumnTitle" colspan="4"><it:message key="itracker.web.attr.scripts"/>:</td>
                  <td align="right">
                     <it:formatIconAction action="editprojectscriptform"
                                          paramName="projectId"
                                          paramValue="${ project.id }"
                                          targetAction="update"
                                          icon="plus"
                                          info="itracker.web.image.create.projectscript.alt"
                                          arg0="${ project.name }"
                                          textActionKey="itracker.web.image.create.texttag"/>
                  </td>
               </tr>
            </c:if>
            <tr align="left" class="listHeading">
               <td><it:message key="itracker.web.attr.field"/></td>
               <td><it:message key="itracker.web.attr.script"/></td>
               <td align="left"><it:message key="itracker.web.attr.priority"/></td>
               <td><it:message key="itracker.web.attr.event"/></td>
               <td><!-- action --></td>
            </tr>

            <c:if test="${ projectScripts != null && not empty projectScripts }">
               <c:forEach items="${ projectScripts }" var="script" varStatus="i">

                  <tr style="text-align: left;" class="${i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}">


                     <td>${ script.fieldName }
                     </td>
                     <td>${ script.script.script.name }
                     </td>
                     <td class="priority-${script.script.priority}">${ priorityList[script.script.priority] }
                     </td>
                     <td>${ script.eventName }
                     </td>
                     <td style="text-align: right;">
                        <c:if test="${currUser.superUser}">
                           <it:formatIconAction action="removeprojectscript"
                                                paramName="delId"
                                                paramValue="${ script.script.id }"
                                                icon="remove"
                                                info="itracker.web.image.delete.projectscript.alt"
                                                textActionKey="itracker.web.image.delete.texttag"/>
                        </c:if>
                     </td>
                  </tr>
               </c:forEach>
            </c:if>

            <tr>
               <td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15"/></td>
            </tr>
            <tr>
               <td class="editColumnTitle" colspan="4">
                  <it:message key="itracker.web.attr.versions"/>:
               </td>
               <td align="right"><it:formatIconAction action="editversionform"
                                                      icon="plus"
                                                      paramName="projectId"
                                                      paramValue="${ project.id }"
                                                      targetAction="update"
                                                      info="itracker.web.image.create.version.alt"
                                                      arg0="${ project.name }"
                                                      textActionKey="itracker.web.image.create.texttag"/></td>
            </tr>

            <tr align="left" class="listHeading">

               <td><it:message key="itracker.web.attr.number"/></td>
               <td><it:message key="itracker.web.attr.description"/></td>
               <td><it:message key="itracker.web.attr.lastmodified"/></td>
               <td align="left"><it:message key="itracker.web.attr.issues"/></td>
               <td><html:img module="/"
                             page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
            </tr>

            <c:forEach var="version" items="${versions}" varStatus="i">
               <tr align="right" class="${ i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded' }">
                  <td class="listRowSmall" align="left">${version.number}</td>
                  <td class="listRowSmall" align="left">${version.description}</td>
                  <td class="listRowSmall" align="left"><it:formatDate date="${version.date}"/></td>
                  <td class="listRowSmall" align="right">${version.count}</td>
                  <td align="right"><it:formatIconAction
                          action="editversionform" paramName="id"
                          paramValue="${version.id}" targetAction="update"
                          icon="pencil"
                          info="itracker.web.image.edit.version.alt"
                          arg0="${version.number}"
                          textActionKey="itracker.web.image.edit.texttag"/></td>
               </tr>
            </c:forEach>
            <tr>
               <td colspan="5"><html:img module="/"
                                         page="/themes/defaulttheme/images/blank.gif" height="15"/></td>
            </tr>

            <tr>
               <td class="editColumnTitle" colspan="4">
                  <it:message key="itracker.web.attr.components"/>:
               </td>
               <td align="right"><it:formatIconAction
                       action="editcomponentform" paramName="projectId"
                       paramValue="${project.id}" targetAction="create"
                       icon="plus"
                       info="itracker.web.image.create.component.alt"
                       arg0="${project.name}"
                       textActionKey="itracker.web.image.create.texttag"/></td>
            </tr>
            <tr align="left" class="listHeading">

               <td><it:message key="itracker.web.attr.name"/></td>
               <td><it:message key="itracker.web.attr.description"/></td>
               <td><it:message key="itracker.web.attr.lastmodified"/></td>
               <td align="left"><it:message key="itracker.web.attr.issues"/></td>
               <td><html:img module="/"
                             page="/themes/defaulttheme/images/blank.gif" width="4"/></td>
            </tr>

            <c:forEach var="component" items="${components}" varStatus="i">
               <tr align="right" class="${i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}">
                  <td class="listRowSmall" align="left">${component.name}</td>
                  <td class="listRowSmall" align="left">${component.description}</td>
                  <td class="listRowSmall" align="left"><it:formatDate date="${component.date}"/></td>
                  <td class="listRowSmall" align="right">${component.count}</td>
                  <td align="right"><it:formatIconAction
                          action="editcomponentform" paramName="id"
                          paramValue="${component.id}" targetAction="update"
                          icon="pencil"
                          info="itracker.web.image.edit.component.alt"
                          arg0="${component.name}"
                          textActionKey="itracker.web.image.edit.texttag"/></td>
               </tr>

            </c:forEach>
         </table>

      </c:if></td>
   </tr>
</table>


<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>

 	  	 
