<%@ include file="/common/taglibs.jsp"%>
<bean:define toScope="request" id="pageTitleKey" value="itracker.web.index.title"/>
<bean:define toScope="request" id="pageTitleArg" value=""/>
<!-- assigned issues -->

<table class="portalhomeMain shadeList" cellspacing="0">
<c:if test="${(! UserUtilities_PREF_HIDE_ASSIGNED) || allSections}">
    <tr id="ownedIssues">
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.assigned"/>:</td>
    </tr>
    <c:choose>
    <c:when test="${empty ownedIssues}">
        <tr class="listRowUnshaded">
            <td ></td>
            <td colspan="16">
                <it:message key="itracker.web.error.noissues"></it:message>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
    <tr style="text-align: left" class="listHeading">
        <td style="width:50px;" ></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.id"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.status"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.severity"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.description"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.owner"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="text-align:right; white-space: nowrap" ><it:message key="itracker.web.attr.lastmodified"/></td>
    </tr>

    <c:forEach items="${ownedIssues}" var="ownedIssue" step="1" varStatus="i">

        <c:choose>
            <c:when test="${showAll || (i.count <=userPrefs.numItemsOnIndex)}">
            <%--  --%>
                <c:set var="listRowClass" value="${i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}"/>
                <tr class="${listRowClass}">
                     <td style="white-space: nowrap">
                        <c:if test="${ownedIssue.userCanViewIssue}">
                            <it:formatImageAction
                                forward="viewissue" module="/module-projects"
                                paramName="id"
                                paramValue="${ownedIssue.issue.id}"
                                src="/themes/defaulttheme/images/view.gif"
                                altKey="itracker.web.image.view.issue.alt"
                                arg0="${ownedIssue.issue.id}"
                                textActionKey="itracker.web.image.view.texttag"/>
                        </c:if>
                        <c:if test="${ownedIssue.userCanEdit}">
                            <it:formatImageAction
                                    forward="editissue" module="/module-projects"
                                    paramName="id"
                                    paramValue="${ownedIssue.issue.id}"
                                    caller="index"
                                    src="/themes/defaulttheme/images/edit.gif"
                                    altKey="itracker.web.image.edit.issue.alt"
                                    arg0="${ownedIssue.issue.id}"
                                    textActionKey="itracker.web.image.edit.texttag"/>
                        </c:if>
                    </td>
                    <td></td>
                    <td align="left">${ownedIssue.issue.id}</td>
                    <td></td>
                    <td style="white-space: nowrap"><c:out value="${ownedIssue.issue.project.name}"/></td>
                    <td></td>
                    <td nowrap="nowrap">${ownedIssue.statusLocalizedString}</td>
                    <td></td>
                    <td>${ownedIssue.severityLocalizedString}</td>
                    <td></td>
                    <td style="white-space: nowrap;"><it:formatDescription><c:out value="${ownedIssue.issue.description}"/></it:formatDescription></td>
                    <td></td>
                    <td style="white-space: nowrap;">${ownedIssue.issue.owner.firstName} ${ownedIssue.issue.owner.lastName}</td>
                    <td></td>
                    <td style="text-align: right; white-space: nowrap"><it:formatDate date="${ownedIssue.issue.lastModifiedDate}"/></td>
                </tr>
            </c:when>
            <c:otherwise>

                <c:if test="${i.count == userPrefs.numItemsOnIndex + 1}">
                    <tr class="listRowUnshaded">
                        <td class="moreissues" colspan="15"><html:link module="/" action="/portalhome?showAll=true" ><it:message key="itracker.web.index.moreissues"/></html:link></td>
                    </tr>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    </c:otherwise></c:choose>
    <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
<%-- END c:if UserUtilities_PREF_HIDE_ASSIGNED --%>
</c:if>


<!-- unassigned issues -->


<c:if test="${(! UserUtilities_PREF_HIDE_UNASSIGNED)  || allSections}">

    <tr id="unassignedIssues">
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.unassigned"/>:</td>
    </tr>
    <c:choose>
       <c:when test="${empty unassignedIssues}">
       <tr class="listRowUnshaded">
        <td ></td>
        <td colspan="16">
            <it:message key="itracker.web.error.noissues"></it:message>
        </td>
       </tr>
    </c:when>
    <c:otherwise>
    <tr style="text-align: left" class="listHeading">
        <td style="width:50px;" ></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.id"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.status"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.severity"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.description"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.owner"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="text-align:right; white-space: nowrap" ><it:message key="itracker.web.attr.lastmodified"/></td>
    </tr>

    <c:forEach items="${unassignedIssues}" var="unassignedIssue" step="1" varStatus="i">
        <c:set var="iCount" value="${i.index +1}" />
        <c:choose>
            <c:when test="${showAll || (iCount <=userPrefs.numItemsOnIndex)}">

                <c:set var="listRowClass"
                    value="${i.index % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}" />
                <tr id="unassignedIssue.${iCount}" class="${listRowClass}">
                    <td style="white-space: nowrap">
                        <c:if test="${not unassignedIssue.userHasIssueNotification}">
                            <it:formatImageAction forward="watchissue" paramName="id"
                                paramValue="${unassignedIssue.issue.id}" caller="index"
                                src="/themes/defaulttheme/images/watch.gif"
                                altKey="itracker.web.image.watch.issue.alt"
                                arg0="${unassignedIssue.issue.id}"
                                textActionKey="itracker.web.image.watch.texttag" />
                        </c:if>
                        <it:formatImageAction
                            forward="viewissue" module="/module-projects"
                            paramName="id" paramValue="${unassignedIssue.issue.id}"
                            src="/themes/defaulttheme/images/view.gif"
                            altKey="itracker.web.image.view.issue.alt"
                            arg0="${unassignedIssue.issue.id}"
                            textActionKey="itracker.web.image.view.texttag" />
                        <c:if test="${unassignedIssue.userCanEdit}">
                            <it:formatImageAction
                                forward="editissue" module="/module-projects"
                                paramName="id" paramValue="${unassignedIssue.issue.id}"
                                caller="index"
                                src="/themes/defaulttheme/images/edit.gif"
                                altKey="itracker.web.image.edit.issue.alt"
                                arg0="${unassignedIssue.issue.id}"
                                textActionKey="itracker.web.image.edit.texttag" />
                        </c:if>

                    </td>
                    <td></td>
                    <td style="text-align: left;">${unassignedIssue.issue.id}</td>
                    <td></td>
                    <td style="white-space: nowrap">${unassignedIssue.issue.project.name}</td>
                    <td></td>
                    <td><c:out value="${unassignedIssue.statusLocalizedString}" /></td>
                    <td></td>
                    <td><c:out
                        value="${unassignedIssue.severityLocalizedString}" /></td>
                    <td></td>
                    <td style="white-space: nowrap;"><it:formatDescription>${unassignedIssue.issue.description}</it:formatDescription></td>
                    <td></td>
                    <!-- Marky:  modified the code to place the two checks in the chooser statement so only one select list will
    be displayed. -->
                    <%--c:if test="$ {unassignedIssue.userHasPermission_PERMISSION_ASSIGN_OTHERS}" --%>
                    <c:choose>
                        <c:when
                            test="${unassignedIssue.userHasPermission_PERMISSION_ASSIGN_OTHERS}">

                            <td>
                                <html:form action="/assignissue">
                                    <html:hidden property="issueId"
                                                 value="${unassignedIssue.issue.id}"/>
                                    <html:hidden property="projectId"
                                                 value="${unassignedIssue.issue.project.id}"/>

                                    <html:select property="userId"
                                                 styleClass="${listRowClass}" onchange="this.form.submit();">
                                        <!-- Marky: I commented out the original <C : tags and replaced them with my <C : tages.
                                        I change code to test for unassigned attribute instead of owner, since owner is not set.-->
                                        <c:choose>
                                            <c:when test="${unassignedIssue.unassigned}">
                                                <!-- c:when test="$ {unassignedIssue.issue.owner == null}" -->
                                                <option value="-1"><it:message
                                                        key="itracker.web.generic.unassigned"/></option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${unassignedIssue.issue.owner.id}"><c:out
                                                        value="${unassignedIssue.issue.owner.firstName}"/> <c:out
                                                        value="${unassignedIssue.issue.owner.lastName}"/></option>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:forEach items="${unassignedIssue.possibleOwners}"
                                                   var="possibleIssueOwner" varStatus="k">
                                            <c:if test="${possibleIssueOwner.lastName != null}">
                                                <option value="${possibleIssueOwner.id}"
                                                        <c:choose>
                                                            <c:when test="${unassignedIssue.issue.owner.id == possibleIssueOwner.id}">
                                                                selected
                                                            </c:when>
                                                            <c:otherwise>
                                                            </c:otherwise>
                                                        </c:choose>>${possibleIssueOwner.firstName}
                                                        ${possibleIssueOwner.lastName}</option>
                                            </c:if>
                                        </c:forEach>
                                    </html:select>
                                </html:form>
                            </td>
                            <!-- /c:if -->
                            <!--End of  unassignedIssue.userHasPermission_PERMISSION_ASSIGN_OTHERS-->
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when
                                    test="${unassignedIssue.userHasPermission_PERMISSION_ASSIGN_SELF}">
                                    <td style="white-space: nowrap;">
                                        <html:form action="/assignissue">
                                            <html:hidden property="issueId"
                                                         value="${unassignedIssue.issue.id}"/>
                                            <html:hidden property="projectId"
                                                         value="${unassignedIssue.issue.project.id}"/>

                                            <%--!String styleClass2 = "(i % 2 == 1 ? \"listRowShaded\" : \"listRowUnshaded\")";--%>
                                            <html:select property="userId"
                                                         styleClass="${listRowClass}"
                                                         onchange="this.form.submit();">
                                                <c:choose>
                                                    <c:when test="${unassignedIssue.unassigned}">
                                                        <option value="-1">
                                                            <it:message key="itracker.web.generic.unassigned"/>
                                                        </option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${unassignedIssue.issue.owner.id}"><c:out
                                                                value="${unassignedIssue.issue.owner.firstName}"/>
                                                            <c:out
                                                                    value="${unassignedIssue.issue.owner.lastName}"/>Test2
                                                        </option>
                                                    </c:otherwise>
                                                </c:choose>
                                                <option value="${currUser.id}"
                                                        <c:if test="${unassignedIssue.issue.id==currUser.id}">selected</c:if>>
                                                        ${currUser.firstName} ${currUser.lastName}</option>

                                            </html:select>
                                        </html:form>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td><it:formatIssueOwner
                                        issue="${unassignedIssue.issue}" format="short" /></td>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                    <td></td>
                    <td style="text-align: right; white-space: nowrap"><it:formatDate
                        date="${unassignedIssue.issue.lastModifiedDate}" /></td>
                </tr>
            </c:when>
            <c:otherwise>

                <c:if test="${iCount == userPrefs.numItemsOnIndex + 1}">
                    <tr class="listRowUnshaded">
                        <td class="moreissues" colspan="15"><html:link anchor="unassignedIssues" module="/" action="/portalhome?showAll=true" ><it:message key="itracker.web.index.moreissues"/></html:link></td>
                    </tr>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    </c:otherwise></c:choose>
       <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>


<!-- created issues -->

<c:if test="${(! UserUtilities_PREF_HIDE_CREATED)  || allSections}">

    <tr id="createdIssues">
        <td class="editColumnTitle" colspan="15"><it:message key="itracker.web.index.created"/>:</td>
    </tr>
    <c:choose>
    <c:when test="${empty createdIssues}">
        <tr class="listRowUnshaded">
            <td ></td>
            <td colspan="16">
                <it:message key="itracker.web.error.noissues"></it:message>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
    <tr style="text-align: left" class="listHeading">
        <td style="width:50px;" ></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.id"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.status"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.severity"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.description"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.owner"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="text-align:right; white-space: nowrap" ><it:message key="itracker.web.attr.lastmodified"/></td>
    </tr>
    <c:if test="${empty createdIssues}">
        <tr class="listRowUnshaded">
            <td ></td>
            <td colspan="16">
                <it:message key="itracker.web.error.noissues"></it:message>
            </td>
        </tr>
    </c:if>
    <c:forEach items="${createdIssues}" var="createdIssue" step="1"
        varStatus="i">

        <c:choose>
            <c:when test="${showAll || (i.count <=userPrefs.numItemsOnIndex)}">

                <c:set var="listRowClass"
                    value="${i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}" />
                <tr id="createdIssue.${i.count}" class="${listRowClass}">
                    <td style="white-space: nowrap">
                        <c:if test="${createdIssue.userCanViewIssue}">
                            <it:formatImageAction
                                forward="viewissue" module="/module-projects"
                                paramName="id"
                                paramValue="${createdIssue.issue.id}"
                                src="/themes/defaulttheme/images/view.gif"
                                altKey="itracker.web.image.view.issue.alt"
                                arg0="${createdIssue.issue.id}"
                                textActionKey="itracker.web.image.view.texttag" />

                                <c:if test="${createdIssue.userCanEdit}">
                                    <it:formatImageAction
                                        forward="editissue" module="/module-projects"
                                           paramName="id"
                                           paramValue="${createdIssue.issue.id}"
                                           caller="index"
                                           src="/themes/defaulttheme/images/edit.gif"
                                           altKey="itracker.web.image.edit.issue.alt"
                                           arg0="${createdIssue.issue.id}"
                                           textActionKey="itracker.web.image.edit.texttag"/>

                                   </c:if>
                        </c:if>
                    </td>
                    <td></td>
                    <td style="text-align: left;">${createdIssue.issue.id}</td>
                    <td></td>
                    <td style="white-space: nowrap; overflow: hidden">${createdIssue.issue.project.name}</td>
                    <td></td>
                    <td>${createdIssue.statusLocalizedString}</td>
                    <td></td>
                    <td>${createdIssue.severityLocalizedString}</td>
                    <td></td>
                    <td style="white-space: nowrap;"><it:formatDescription>${createdIssue.issue.description}</it:formatDescription></td>
                    <td></td>
                    <td style="white-space: nowrap">
                        <c:choose>
                            <c:when test="${createdIssue.unassigned}">
                                <it:message key="itracker.web.generic.unassigned"/>
                            </c:when>
                            <c:otherwise>
                                ${createdIssue.issue.owner.firstName} ${createdIssue.issue.owner.lastName}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td></td>
                    <td style="text-align: right; white-space: nowrap">
                        <it:formatDate date="${createdIssue.issue.lastModifiedDate}" />
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:if test="${i.count == userPrefs.numItemsOnIndex + 1}">
                    <tr class="listRowUnshaded">
                        <td class="moreissues" colspan="15"><html:link anchor="createdIssues" module="/" action="/portalhome?showAll=true" ><it:message key="itracker.web.index.moreissues"/></html:link></td>
                    </tr>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    </c:otherwise>
    </c:choose>
    <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>



<!-- watched issues -->



<%--
   // I could make this all the issues that have changed since the last login.  Wonder if that would be
   // better than the watches? No then you lose them.
--%>


<c:if test="${(! UserUtilities_PREF_HIDE_WATCHED) || allSections}">

    <tr id="watchedIssues">
        <td class="editColumnTitle" colspan="15"><it:message
            key="itracker.web.index.watched" />:</td>
    </tr>
    <c:choose>
    <c:when test="${empty watchedIssues}">
        <tr class="listRowUnshaded">
            <td ></td>
            <td colspan="16">
                <it:message key="itracker.web.error.noissues"></it:message>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
    <tr style="text-align: left" class="listHeading">
        <td style="width:50px;" ></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.id"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.project"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.status"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.severity"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.description"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="white-space: nowrap"><it:message key="itracker.web.attr.owner"/></td>
        <td><html:img page="/themes/defaulttheme/images/blank.gif" width="3"/></td>
        <td style="text-align:right; white-space: nowrap" ><it:message key="itracker.web.attr.lastmodified"/></td>
    </tr>


    <c:forEach items="${watchedIssues}" var="watchedIssue" step="1"
        varStatus="i">


        <c:choose>
            <c:when test="${showAll || (i.count <=userPrefs.numItemsOnIndex)}">

                <c:set var="listRowClass"
                    value="${i.count % 2 == 1 ? 'listRowShaded' : 'listRowUnshaded'}" />
                <tr id="watchedIssue.${i.count}" class="${listRowClass}">
                    <td style="white-space: nowrap"><it:formatImageAction
                        forward="watchissue" paramName="id"
                        paramValue="${watchedIssue.issue.id}" caller="index"
                        src="/themes/defaulttheme/images/unwatch.gif"
                        altKey="itracker.web.image.unwatch.issue.alt"
                        arg0="${watchedIssue.issue.id}"
                        textActionKey="itracker.web.image.unwatch.texttag" />
                        <c:if test="${watchedIssue.userCanViewIssue}">
                            <it:formatImageAction
                                forward="viewissue" module="/module-projects"
                                paramName="id"
                                paramValue="${watchedIssue.issue.id}"
                                src="/themes/defaulttheme/images/view.gif"
                                altKey="itracker.web.image.view.issue.alt"
                                arg0="${watchedIssue.issue.id}"
                                textActionKey="itracker.web.image.view.texttag" />
                            <c:if test="${watchedIssue.userCanEdit}">
                                   <it:formatImageAction
                                    forward="editissue" module="/module-projects"
                                       paramName="id"
                                       paramValue="${watchedIssue.issue.id}"
                                       caller="index"
                                       src="/themes/defaulttheme/images/edit.gif"
                                       altKey="itracker.web.image.edit.issue.alt"
                                       arg0="${watchedIssue.issue.id}"
                                       textActionKey="itracker.web.image.edit.texttag"/>
                               </c:if>
                        </c:if>
                    </td>
                    <td></td>
                    <td style="text-align: left;">${watchedIssue.issue.id}</td>
                    <td></td>
                    <td style="white-space: nowrap">${watchedIssue.issue.project.name}</td>
                    <td></td>
                    <td style="white-space: nowrap">${watchedIssue.statusLocalizedString}</td>
                    <td></td>
                    <td>${watchedIssue.severityLocalizedString}</td>
                    <td></td>
                    <td style="white-space: nowrap;"><it:formatDescription>${watchedIssue.issue.description}</it:formatDescription></td>
                    <td></td>
                    <td>
                        <c:choose>
                            <c:when test="${watchedIssue.unassigned}">
                                <it:message key="itracker.web.generic.unassigned"/>
                          </c:when>
                          <c:otherwise>
                              ${watchedIssue.issue.owner.firstName} ${watchedIssue.issue.owner.lastName}
                          </c:otherwise>
                        </c:choose>
                    </td>
                    <td></td>
                    <td style="text-align: right; white-space: nowrap"><it:formatDate
                        date="${watchedIssue.issue.lastModifiedDate}" /></td>

                </tr>
            </c:when>
            <c:otherwise>
                <c:if test="${i.count == userPrefs.numItemsOnIndex + 1}">
                    <tr class="listRowUnshaded">
                        <td class="moreissues" colspan="15"><html:link anchor="watchedIssues" module="/" action="/portalhome?showAll=true" ><it:message key="itracker.web.index.moreissues"/></html:link></td>
                    </tr>
                </c:if>
            </c:otherwise>
        </c:choose>

    </c:forEach>
    </c:otherwise></c:choose>
    <tr><td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20"/></td></tr>
</c:if>

<!-- view hidden sections link -->
    <c:if test="${showAll && userPrefs.numItemsOnIndex > 0}">
        <tr class="listRowUnshaded">
            <td class="moreissues" colspan="15">
                <html:link module="/" action="/portalhome?showAll=false" ><it:message key="itracker.web.index.lessissues"/></html:link>
            </td>
        </tr>
    </c:if>
    <c:if test="${userPrefs.hiddenIndexSections>0}">
        <tr style="text-align: left;" class="listRowUnshaded">
            <td colspan="15" style="text-align: left;">
            <c:choose>
                <c:when test="${!allSections}">
                <html:link action="/portalhome?allSections=true"><it:message key="itracker.web.index.viewhidden" /></html:link>
                </c:when>
                <c:otherwise>
                <html:link action="/portalhome?allSections=false"><it:message key="itracker.web.index.hidehidden" /></html:link>
                </c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td><html:img page="/themes/defaulttheme/images/blank.gif" width="1" height="20" /></td>
        </tr>
    </c:if>
</table>
