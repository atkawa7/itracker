<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.viewissue.title"/>
<bean:define id="pageTitleArg" value="${issue.id}"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="container-fluid maincontent">


    <div class="row">
        <div class="col-sm-2 col-sm-push-6"><label><it:message key="itracker.web.attr.actions"/>:</label>
            <div id="actions" class="actions">


                <c:if test="${!hasHardIssueNotification}">
                    <c:if test="${hasIssueNotification}">
                   <span class="HTTP_POST">
                       <it:formatIconAction forward="watchissue"
                                            module="/module-projects"
                                            paramName="id"
                                            paramValue="${issue.id}"
                                            caller="viewissue"
                                            icon="bell-slash" iconClass="fa-lg"
                                            info="itracker.web.image.watch.issue.alt"
                                            arg0="${issue.id}"
                                            textActionKey="itracker.web.image.watch.texttag"/>
                   </span>
                    </c:if>
                    <c:if test="${!hasIssueNotification}">
                   <span class="HTTP_POST">
                       <it:formatIconAction forward="watchissue"
                                            module="/module-projects"
                                            paramName="id"
                                            paramValue="${issue.id}"
                                            caller="viewissue"
                                            icon="bell" iconClass="fa-lg"
                                            info="itracker.web.image.watch.issue.alt"
                                            arg0="${issue.id}"
                                            textActionKey="itracker.web.image.watch.texttag"/>
                   </span>
                    </c:if>
                </c:if>
                <c:if test="${hasHardIssueNotification}">
                    <c:set var="watched"><it:message key="itracker.web.attr.notifications"/></c:set>
                   <i class="fa fa-bell-o fa-lg" title="${watched}"></i>
               </c:if>
                <c:if test="${canEditIssue}">

                    <it:formatIconAction action="editissueform"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          caller="viewissue"
                                          icon="edit" iconClass="fa-lg"
                                          info="itracker.web.image.edit.issue.alt"
                                          arg0="${issue.id}"
                                          textActionKey="itracker.web.image.edit.texttag"/>

                    <it:formatIconAction action="moveissueform"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${issue.id}"
                                          caller="viewissue"
                                          icon="share-square-o"
                                          styleClass="moveIssue" iconClass="fa-lg"
                                          info="itracker.web.image.move.issue.alt"
                                          arg0="${issue.id}"
                                          textActionKey="itracker.web.image.move.texttag"/>

                    <%-- TODO reinstate this when relate issues works correctly
                    <it:formatImageAction forward="relateissue" paramName="id" paramValue="${issue.id}" caller="viewissue" src="/images/link.gif" altKey="itracker.web.image.link.issue.alt" textActionKey="itracker.web.image.link.texttag"/>
                    --%>
                </c:if>
                <c:if test="${canCreateIssue}">
                    <it:formatIconAction forward="createissue"
                                          module="/module-projects"
                                          paramName="projectId"
                                          paramValue="${project.id}"
                                          icon="plus-square-o" iconClass="fa-lg"
                                          info="itracker.web.image.create.issue.alt"
                                          arg0="${project.name}"
                                          textActionKey="itracker.web.image.create.texttag"/>
                </c:if>

                <div class="pull-right">
                    <c:if test="${not empty previousIssue}">
                        <it:formatIconAction action="view_issue"
                                              module="/module-projects"
                                              paramName="id"
                                              paramValue="${previousIssue.id}"
                                              caller="viewissue"
                                              icon="chevron-circle-left" iconClass="fa-lg"
                                              info="itracker.web.image.previous.issue.alt"
                                              arg0="${previousIssue.id}"
                                              textActionKey="itracker.web.image.previous.texttag"/>
                    </c:if>
                    <c:choose>
                        <c:when test="${not empty nextIssue}">
                            <it:formatIconAction action="view_issue"
                                                  module="/module-projects"
                                                  paramName="id"
                                                  paramValue="${nextIssue.id}"
                                                  caller="viewissue"
                                                  icon="chevron-circle-right" iconClass="fa-lg"
                                                  info="itracker.web.image.next.issue.alt"
                                                  arg0="${nextIssue.id}"
                                                  textActionKey="itracker.web.image.next.texttag"/>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-sm-pull-2">

            <div class="form-group">
                <label><it:message key="itracker.web.attr.description"/>:</label>
                <p class="form-control-static" id="description">${issue.description}
                </p>
            </div>
        </div>
        <div class="col-sm-4">
            <div>
                <div class="form-group">
                    <label><it:message key="itracker.web.attr.project"/>:</label>
                    <p class="form-control-static">
                        <it:formatIconAction forward="listissues"
                                              module="/module-projects"
                                              paramName="projectId"
                                              paramValue="${project.id}"
                                              caller="viewissue"
                                              icon="tasks"
                                              styleClass="issuelist" iconClass="fa-lg"
                                              info="itracker.web.image.issuelist.issue.alt"
                                              textActionKey="itracker.web.image.issuelist.texttag"/>
                        ${issue.project.name}
                    </p>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-6">
            <div>
                <div class="form-group">
                    <label><it:message key="itracker.web.attr.creator"/>:</label>
                    <p class="form-control-static" id="creator"><it:formatDate date="${issue.createDate}"/>
                        (${issue.creator.firstName}&nbsp;${issue.creator.lastName})</p>
                </div>
            </div>
        </div>

        <div class="col-sm-6">
            <div>
                <div class="form-group"><label><it:message key="itracker.web.attr.lastmodified"/>:</label>
                    <p class="form-control-static" id="lastmodified"><it:formatDate date="${issue.lastModifiedDate}"/></p>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-6">
            <div class="form-group"><label><it:message key="itracker.web.attr.status"/>:</label>
                <p class="form-control-static" id="status">${issueStatusName}</p>
            </div>
        </div>
        <div class="col-sm-6">
            <div class="form-group"><label><it:message key="itracker.web.attr.resolution"/>:</label>
                <p class="form-control-static" id="resolution"><it:formatResolution
                        projectOptions="${project.options}">${issue.resolution}</it:formatResolution></p>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-6">
            <div class="form-group"><label><it:message key="itracker.web.attr.severity"/>:</label>
                <p class="form-control-static" id="severity">${issueSeverityName}</p></div>
        </div>
        <div class="col-sm-6">
            <div class="form-group"><label><it:message key="itracker.web.attr.owner"/>:</label>
                <p class="form-control-static" id="owner">${issueOwnerName}</p></div>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-6">

        </div>
        <div class="col-sm-6">
            <c:if test="${not empty project.versions}">
                <div class="form-group">
                    <label><it:message key="itracker.web.attr.target"/>:</label>
                    <p class="form-control-static" id="target">${issue.targetVersion == null ? '' : issue.targetVersion.number}</p>
                </div>
            </c:if>
        </div>
    </div>


    <div class="row">
        <div class="col-sm-6">

            <c:choose>
                <c:when test="${not empty project.components}">
                    <div class="form-group" id="components">
                        <label><it:message key="itracker.web.attr.components"/>:</label>
                        <c:forEach var="component" items="${issue.components}">
                            <p class="form-control-static">
                                    ${component.name}
                            </p>
                        </c:forEach>
                    </div>
                </c:when>
            </c:choose>
        </div>
        <div class="col-sm-6">

            <c:choose>
                <c:when test="${not empty project.versions}">
                    <div class="form-group"  id="versions">
                        <label><it:message key="itracker.web.attr.versions"/>:</label>
                        <c:forEach var="version" items="${issue.versions}">
                            <p class="form-control-static">
                                    ${version.number}
                            </p>
                        </c:forEach>
                    </div>
                </c:when>
            </c:choose>
        </div>
    </div>

    <c:if test="${ not empty projectFieldsMap }">

        <div class="row">
            <div class="col-sm-6">
                <h5><it:message
                        key="itracker.web.attr.customfields"/></h5>
            </div>
        </div>
        <div class="row" id="customfields">
            <c:forEach var="projectField" varStatus="i" items="${ projectFieldsMap }">
                <div class="col-md-6" id="customfield-${projectField.key}">
                    <it:formatCustomField field="${projectField.key}" currentValue="${projectField.value}"
                                          displayType="view"/>
                </div>
            </c:forEach>

        </div>
    </c:if>


    <c:if test="${hasAttachmentOption && not empty attachments}">
        <div class="row">
            <div class="col-sm-6">
                <h5><it:message key="itracker.web.attr.attachments"/></h5>
            </div>
        </div>
        <c:choose>
            <c:when test="${not empty issue.attachments}">
                <div class="row" id="attachments">
                    <c:forEach items="${issue.attachments}" var="attachment" varStatus="status">
                        <div class="col-sm-6 ">
                            <it:formatIconAction action="downloadAttachment.do"
                                                 module="/module-projects"
                                                 paramName="id"
                                                 paramValue="${attachment.id}"
                                                 icon="download" iconClass="fa-lg"
                                                 info="itracker.web.image.download.attachment.alt"
                                                 textActionKey="itracker.web.image.download.texttag"/>

                            <it:link action="downloadAttachment"
                                     paramName="id"
                                     paramValue="${attachment.id}">${attachment.originalFileName}
                                (${attachment.type}, <fmt:formatNumber pattern="0.##" value="${attachment.size / 1024}"
                                                                       type="number"/> <it:message
                                        key="itracker.web.generic.kilobyte"/>)
                            </it:link>
                            <em><it:formatDate date="${attachment.lastModifiedDate}"/>
                                (${attachment.user.firstName}&nbsp;${attachment.user.lastName})</em>

                            <div class="well well-sm">
                                <it:formatDescription>${attachment.description}</it:formatDescription></div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
        </c:choose>
    </c:if>

    <div class="row">
        <div class="col-sm-6">
            <h5><it:message key="itracker.web.attr.history"/></h5>
        </div>
        <div class="col-sm-6 text-right" id="history">

            <c:if test="${not empty previousIssue}">
                <it:formatIconAction action="view_issue"
                                      module="/module-projects"
                                      paramName="id"
                                      paramValue="${previousIssue.id}"
                                      caller="viewissue"
                                      icon="chevron-circle-left" iconClass="fa-lg"
                                      info="itracker.web.image.previous.issue.alt"
                                      arg0="${previousIssue.id}"
                                      textActionKey="itracker.web.image.previous.texttag"/>
            </c:if>
            <c:choose>
                <c:when test="${not empty nextIssue}">
                    <it:formatIconAction action="view_issue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${nextIssue.id}"
                                          caller="viewissue"
                                          icon="chevron-circle-right" iconClass="fa-lg"
                                          info="itracker.web.image.next.issue.alt"
                                          arg0="${nextIssue.id}"
                                          textActionKey="itracker.web.image.next.texttag"/>
                </c:when>
            </c:choose>
            <it:formatIconAction forward="view_issue_activity.do"
                                  paramName="id"
                                  paramValue="${issue.id}"
                                  icon="history" iconClass="fa-lg"
                                  info="itracker.web.image.view.activity.alt"
                                  textActionKey="itracker.web.image.view.texttag"/>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-12">
            <div class="table-responsive">

                <table class="table">

                    <tr class="">
                        <th width="15"></th>
                        <th width="8"></th>
                        <th><it:message key="itracker.web.attr.updator"/></th>
                        <th class="text-right"><it:message key="itracker.web.attr.updated"/></th>
                    </tr>
                    <c:forEach items="${histories}" var="historyEntry" varStatus="status">

                        <tr class="">
                            <td align="right" valign="bottom">
                                    ${status.count})
                            </td>
                            <td>
                            </td>
                            <td class="historyName">
                                    ${historyEntry.user.firstName}&nbsp;${historyEntry.user.lastName}
                                (<a href="mailto:${historyEntry.user.email}"
                                    class="mailto">${historyEntry.user.email}</a>)
                            </td>
                            <td class="historyName text-right">
                                <it:formatDate date="${historyEntry.createDate}"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"></td>
                            <td colspan="3">
                                <it:formatHistoryEntry
                                        projectOptions="${project.options}">${historyEntry.description}</it:formatHistoryEntry>

                            </td>
                        </tr>

                    </c:forEach>

                </table>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-12">
            <h5><it:message key="itracker.web.attr.notifications"/></h5>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">

            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th><it:message key="itracker.web.attr.name"/></th>
                        <th><it:message key="itracker.web.attr.email"/></th>
                        <th><it:message key="itracker.web.attr.role"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${notifiedUsers}" var="user" varStatus="status">
                        <tr>
                            <td>${user.firstName}&nbsp;${user.lastName}</td>
                            <td>
                                <a href="mailto:${user.email}"
                                   class="mailto">${user.email}</a>
                            </td>
                            <td>
                                <ul class="list-inline">
                                    <c:forEach items="${notificationMap[user]}" var="role">
                                        <li><it:message key="itracker.notification.role.${role.code}"></it:message></li>
                                    </c:forEach>
                                </ul>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
             

