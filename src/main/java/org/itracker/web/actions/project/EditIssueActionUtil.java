package org.itracker.web.actions.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.WorkflowException;
import org.itracker.services.util.*;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.AttachmentUtilities;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class EditIssueActionUtil {
    private static final Logger log = Logger.getLogger(EditIssueActionUtil.class);

    public static final Issue processFullEdit(Issue issue, Project project, User user,
                                              Map<Integer, Set<PermissionType>> userPermissions, Locale locale,
                                              IssueForm form, IssueService issueService, ActionMessages errors) throws Exception {
        int previousStatus = issue.getStatus();
        boolean needReloadIssue = false;
        ActionMessages msg = new ActionMessages();
        issue = AttachmentUtilities.addAttachment(issue, project, user, form, ServletContextUtils.getItrackerServices(), msg);

        if (!msg.isEmpty()) {
            // Validation of attachment failed
            errors.add(msg);
            return issue;
        }

        needReloadIssue = issueService.setIssueVersions(issue.getId(),
                new HashSet<Integer>(Arrays.asList(form.getVersions())),
                user.getId());

        needReloadIssue = needReloadIssue | issueService.setIssueComponents(issue.getId(),
                new HashSet<Integer>(Arrays.asList(form.getComponents())),
                user.getId());
        // save attachment and reload updated issue

//		needReloadIssue = needReloadIssue | addAttachment(issue, project, user, form, issueService, errors);

        // reload issue for further updates
        if (needReloadIssue) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: updating issue from session: " + issue);
            }
            issue = issueService.getIssue(issue.getId());
        }

        Integer targetVersion = form.getTargetVersion();
        if (targetVersion != null && targetVersion != -1) {
            ProjectService projectService = ServletContextUtils.getItrackerServices()
                    .getProjectService();
            Version version = projectService.getProjectVersion(targetVersion);
            if (version == null) {
                throw new RuntimeException("No version with Id "
                        + targetVersion);
            }
            issue.setTargetVersion(version);
        }

        issue.setResolution(form.getResolution());
        issue.setSeverity(form.getSeverity());

        applyLimitedFields(issue, project, user, userPermissions, locale, form, issueService);

        Integer formStatus = form.getStatus();
        issue.setStatus(formStatus);
        if (formStatus != null) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: processing status: " + formStatus);
            }
            if (previousStatus != -1) {
                // Reopened the issue. Reset the resolution field.
                if ((previousStatus >= IssueUtilities.STATUS_ASSIGNED && previousStatus < IssueUtilities.STATUS_RESOLVED)
                        && (previousStatus >= IssueUtilities.STATUS_RESOLVED && previousStatus < IssueUtilities.STATUS_END)) {
                    issue.setResolution("");
                }

                if (previousStatus >= IssueUtilities.STATUS_CLOSED
                        && !UserUtilities.hasPermission(userPermissions, project
                        .getId(), UserUtilities.PERMISSION_CLOSE)) {
                    if (previousStatus < IssueUtilities.STATUS_CLOSED) {
                        issue.setStatus(previousStatus);
                    } else {
                        issue.setStatus(IssueUtilities.STATUS_RESOLVED);
                    }
                }

                if (issue.getStatus() < IssueUtilities.STATUS_NEW
                        || issue.getStatus() >= IssueUtilities.STATUS_END) {
                    issue.setStatus(previousStatus);
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
                    && !UserUtilities.hasPermission(userPermissions, project
                    .getId(), UserUtilities.PERMISSION_CLOSE)) {
                issue.setStatus(IssueUtilities.STATUS_RESOLVED);
            }
        }

        if (issue.getStatus() < IssueUtilities.STATUS_NEW) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: status < STATUS_NEW: " + issue.getStatus());
            }
            issue.setStatus(IssueUtilities.STATUS_NEW);
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: updated to: " + issue.getStatus());
            }
        } else if (issue.getStatus() >= IssueUtilities.STATUS_END) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: status >= STATUS_END: " + issue.getStatus());
            }
            if (!UserUtilities.hasPermission(userPermissions, project.getId(),
                    UserUtilities.PERMISSION_CLOSE)) {
                issue.setStatus(IssueUtilities.STATUS_RESOLVED);
            } else {
                issue.setStatus(IssueUtilities.STATUS_CLOSED);
            }
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: status updated to: " + issue.getStatus());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("processFullEdit: updating issue " + issue);
        }
        return issueService.updateIssue(issue, user.getId());
    }

    public static final void applyLimitedFields(Issue issue, Project project,
                                                User user, Map<Integer, Set<PermissionType>> userPermissionsMap,
                                                Locale locale, IssueForm form, IssueService issueService) throws Exception {

        issue.setDescription(form.getDescription());

        setIssueFields(issue, user, locale, form, issueService);
        setOwner(issue, user, userPermissionsMap, form, issueService);
        addHistoryEntry(issue, user, form, issueService);
    }

    private static void setIssueFields(Issue issue, User user, Locale locale,
                                       IssueForm form, IssueService issueService) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("setIssueFields: called");
        }
        List<CustomField> projectCustomFields = issue.getProject()
                .getCustomFields();
        if (log.isDebugEnabled()) {
            log.debug("setIssueFields: got project custom fields: " + projectCustomFields);
        }

        if (projectCustomFields == null || projectCustomFields.size() == 0) {
            log.debug("setIssueFields: no custom fields, returning...");
            return;
        }


        // here you see some of the ugly side of Struts 1.3 - the forms... they
        // can only contain Strings and some simple objects types...
        HashMap<String, String> formCustomFields = form.getCustomFields();

        if (log.isDebugEnabled()) {
            log.debug("setIssueFields: got form custom fields: " + formCustomFields);
        }

        if (formCustomFields == null || formCustomFields.size() == 0) {
            log.debug("setIssueFields: no form custom fields, returning..");
            return;
        }

        ResourceBundle bundle = ITrackerResources.getBundle(locale);
//		List<IssueField> issueFieldsList = new ArrayList<IssueField>(projectCustomFields.size());
        Iterator<CustomField> customFieldsIt = projectCustomFields.iterator();
        // declare iteration fields
        CustomField field;
        String fieldValue;
        IssueField issueField;
        try {
            if (log.isDebugEnabled()) {
                log.debug("setIssueFields: processing project fields");
            }
            // set values to issue-fields and add if needed
            while (customFieldsIt.hasNext()) {

                field = customFieldsIt.next();
                fieldValue = (String) formCustomFields.get(String.valueOf(field
                        .getId()));

                // remove the existing field for re-setting
                issueField = getIssueField(issue, field);


                if (fieldValue != null && fieldValue.trim().length() > 0) {
                    if (null == issueField) {
                        issueField = new IssueField(issue, field);
                        issue.getFields().add(issueField);
                    }

                    issueField.setValue(fieldValue, bundle);
                } else {
                    if (null != issueField) {
                        issue.getFields().remove(issueField);
                    }
                }
            }

            // set new issue fields for later saving
//			issue.setFields(issueFieldsList);

//			issueService.setIssueFields(issue.getId(), issueFieldsList);
        } catch (Exception e) {
            log.error("setIssueFields: failed to process custom fields", e);
            throw e;
        }
    }

    private static IssueField getIssueField(Issue issue, CustomField field) {
        Iterator<IssueField> it = issue.getFields().iterator();
        IssueField issueField = null;
        while (it.hasNext()) {
            issueField = it.next();
            if (issueField.getCustomField().equals(field)) {
                return issueField;
            }
        }
        return null;

    }

    private static void setOwner(Issue issue, User user,
                                 Map<Integer, Set<PermissionType>> userPermissionsMap,
                                 IssueForm form, IssueService issueService) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("setOwner: called to " + form.getOwnerId());
        }
        Integer currentOwner = (issue.getOwner() == null) ? null : issue
                .getOwner().getId();

        Integer ownerId = form.getOwnerId();

        if (ownerId == null || ownerId.equals(currentOwner)) {
            if (log.isDebugEnabled()) {
                log.debug("setOwner: returning, existing owner is the same: " + issue.getOwner());
            }
            return;
        }

        if (UserUtilities.hasPermission(userPermissionsMap,
                UserUtilities.PERMISSION_ASSIGN_OTHERS)
                || (UserUtilities.hasPermission(userPermissionsMap,
                UserUtilities.PERMISSION_ASSIGN_SELF) && user.getId()
                .equals(ownerId))
                || (UserUtilities.hasPermission(userPermissionsMap,
                UserUtilities.PERMISSION_UNASSIGN_SELF)
                && user.getId().equals(currentOwner) && ownerId
                .intValue() == -1)) {
            User newOwner = ServletContextUtils.getItrackerServices().getUserService().getUser(ownerId);
            if (log.isDebugEnabled()) {
                log.debug("setOwner: setting new owner " + newOwner + " to " + issue);
            }
            issue.setOwner(newOwner);
//			issueService.assignIssue(issue.getId(), ownerId, user.getId());
        }

    }

    private static void addHistoryEntry(Issue issue, User user, IssueForm form,
                                        IssueService issueService) throws Exception {
        try {
            String history = form.getHistory();

            if (history == null || history.equals("")) {
                if (log.isDebugEnabled()) {
                    log.debug("addHistoryEntry: skip history to " + issue);
                }
                return;
            }


            if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, issue.getProject().getOptions())) {
                history = HTMLUtilities.removeMarkup(history);
            } else if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, issue.getProject().getOptions())) {
                history = HTMLUtilities.escapeTags(history);
            } else {
                history = HTMLUtilities.newlinesToBreaks(history);
            }


            if (log.isDebugEnabled()) {
                log.debug("addHistoryEntry: adding history to " + issue);
            }
            IssueHistory issueHistory = new IssueHistory(issue, user, history,
                    IssueUtilities.HISTORY_STATUS_AVAILABLE);

            issueHistory.setDescription(((IssueForm) form).getHistory());
            issueHistory.setCreateDate(new Date());

            issueHistory.setLastModifiedDate(new Date());
            issue.getHistory().add(issueHistory);


//  TODO why do we need to updateIssue here, and can not later?
//			issueService.updateIssue(issue, user.getId());
        } catch (Exception e) {
            log.error("addHistoryEntry: failed to add", e);
            throw e;
        }
//		issueService.addIssueHistory(issueHistory);
        if (log.isDebugEnabled()) {
            log.debug("addHistoryEntry: added history for issue " + issue);
        }
    }

    public static final Issue processLimitedEdit(Issue issue, Project project,
                                                 User user, Map<Integer, Set<PermissionType>> userPermissionsMap,
                                                 Locale locale, IssueForm form, IssueService issueService, ActionMessages messages)
            throws Exception {
        ActionMessages msg = new ActionMessages();
        issue = AttachmentUtilities.addAttachment(issue, project, user, form, ServletContextUtils.getItrackerServices(), msg);

        if (!msg.isEmpty()) {
            messages.add(msg);
            return issue;
        }

        Integer formStatus = form.getStatus();

        if (formStatus != null) {

            if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                    && formStatus >= IssueUtilities.STATUS_CLOSED
                    && UserUtilities.hasPermission(userPermissionsMap,
                    UserUtilities.PERMISSION_CLOSE)) {

                issue.setStatus(formStatus);
            }

        }

        EditIssueActionUtil.applyLimitedFields(issue, project, user, userPermissionsMap, locale, form, issueService);
        return issueService.updateIssue(issue, user.getId());

    }

    public static final void sendNotification(Integer issueId, int previousStatus,
                                              String baseURL, NotificationService notificationService) {
        Type notificationType = Type.UPDATED;

        Issue issue = ServletContextUtils.getItrackerServices().getIssueService().getIssue(issueId);

        if ((previousStatus < IssueUtilities.STATUS_CLOSED)
                && issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
            notificationType = Type.CLOSED;
        }

        if (log.isDebugEnabled()) {
            log.debug("notificationService: before send");
        }
        notificationService.sendNotification(issue, notificationType, baseURL);

        if (log.isDebugEnabled()) {
            log.debug("notificationService: after send");
        }
    }

    public static final ActionForward getReturnForward(Issue issue, Project project,
                                                       IssueForm form, ActionMapping mapping) throws Exception {
        if ("index".equals(form.getCaller())) {
            log.info("EditIssueAction: Forward: index");
            return mapping.findForward("index");
        } else if ("viewissue".equals(form.getCaller()) && issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
            log.info("EditIssueAction: Forward: viewissue");
            return new ActionForward(mapping.findForward("viewissue").getPath()
                    + "?id=" + issue.getId());
        } else {
            log.info("EditIssueAction: Forward: listissues");
            return new ActionForward(mapping.findForward("listissues")
                    .getPath()
                    + "?projectId=" + project.getId());
        }
    }

    /**
     * method needed to prepare request for edit_issue.jsp
     */
    public static final void setupJspEnv(ActionMapping mapping,
                                         IssueForm issueForm, HttpServletRequest request, Issue issue,
                                         IssueService issueService, UserService userService,
                                         Map<Integer, Set<PermissionType>> userPermissions,
                                         Map<Integer, List<NameValuePair>> listOptions, ActionMessages errors)
            throws ServletException, IOException, WorkflowException {

        if (log.isDebugEnabled()) {
            log.debug("setupJspEnv: for issue " + issue);
        }

        NotificationService notificationService = ServletContextUtils
                .getItrackerServices().getNotificationService();
        String pageTitleKey = "itracker.web.editissue.title";
        String pageTitleArg = request.getParameter("id");
        Locale locale = LoginUtilities.getCurrentLocale(request);
        User um = LoginUtilities.getCurrentUser(request);
        List<NameValuePair> statuses = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_STATUS);
        String statusName = IssueUtilities.getStatusName(issue.getStatus(), locale);
        boolean hasFullEdit = UserUtilities.hasPermission(userPermissions,
                issue.getProject().getId(), UserUtilities.PERMISSION_EDIT_FULL);
        List<NameValuePair> resolutions = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_RESOLUTION);
        String severityName = IssueUtilities.getSeverityName(issue
                .getSeverity(), locale);
        List<NameValuePair> components = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_COMPONENTS);
        List<NameValuePair> versions = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_VERSIONS);
        List<NameValuePair> targetVersion = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_TARGET_VERSION);
        List<Component> issueComponents = issue.getComponents();
        Collections.sort(issueComponents);
        List<Version> issueVersions = issue.getVersions();
        Collections.sort(issueVersions, new Version.VersionComparator());
        /* Get project fields and put name and value in map */
        setupProjectFieldsMapJspEnv(issue.getProject().getCustomFields(), issue.getFields(), request);

        setupRelationsRequestEnv(issue.getRelations(), request);


        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);
//		request.setAttribute("wrap", wrap);
        request.getSession().setAttribute(Constants.LIST_OPTIONS_KEY,
                listOptions);
        request.setAttribute("targetVersions", targetVersion);
        request.setAttribute("components", components);
        request.setAttribute("versions", versions);
        request.setAttribute("hasIssueNotification", !notificationService
                .hasIssueNotification(issue, um.getId()));
        request.setAttribute("hasEditIssuePermission", UserUtilities
                .hasPermission(userPermissions, issue.getProject().getId(),
                        UserUtilities.PERMISSION_EDIT));
        request.setAttribute("canCreateIssue",
                issue.getProject().getStatus() == Status.ACTIVE
                        && UserUtilities.hasPermission(userPermissions, issue
                        .getProject().getId(),
                        UserUtilities.PERMISSION_CREATE));
        request.setAttribute("issueComponents", issueComponents);
        request.setAttribute("issueVersions",
                issueVersions == null ? new ArrayList<Version>()
                        : issueVersions);
        request.setAttribute("statuses", statuses);
        request.setAttribute("statusName", statusName);
        request.setAttribute("hasFullEdit", hasFullEdit);
        request.setAttribute("resolutions", resolutions);
        request.setAttribute("severityName", severityName);
        request.setAttribute("hasPredefinedResolutionsOption", ProjectUtilities
                .hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
                        issue.getProject().getOptions()));
        request.setAttribute("issueOwnerName",
                (issue.getOwner() == null ? ITrackerResources.getString(
                        "itracker.web.generic.unassigned", locale)
                        : issue.getOwner().getFirstName() + " "
                        + issue.getOwner().getLastName()));
        request.setAttribute("isStatusResolved",
                issue.getStatus() >= IssueUtilities.STATUS_RESOLVED);


        request.setAttribute("fieldSeverity", WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_SEVERITY));
        request.setAttribute("possibleOwners", WorkflowUtilities
                .getListOptions(listOptions, IssueUtilities.FIELD_OWNER));

        request.setAttribute("hasNoViewAttachmentOption", ProjectUtilities
                .hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, issue
                        .getProject().getOptions()));

        if (log.isDebugEnabled()) {
            log.debug("setupJspEnv: options " + issue.getProject().getOptions() + ", hasNoViewAttachmentOption: " + request.getAttribute("hasNoViewAttachmentOption"));
        }

        setupNotificationsInRequest(request, issue, notificationService);

        // setup issue to request, as it's needed by the jsp.
        request.setAttribute(Constants.ISSUE_KEY, issue);
        request.setAttribute("issueForm", issueForm);
        request.setAttribute(Constants.PROJECT_KEY, issue.getProject());
        List<IssueHistory> issueHistory = issueService.getIssueHistory(issue
                .getId());
        Collections.sort(issueHistory, IssueHistory.CREATE_DATE_COMPARATOR);
        request.setAttribute("issueHistory", issueHistory);


    }

    /**
     * Get project fields and put name and value in map
     * TODO: simplify this code, it's not readable, unsave yet.
     */
    protected static final void setupProjectFieldsMapJspEnv(List<CustomField> projectFields, Collection<IssueField> issueFields, HttpServletRequest request) {
        Map<CustomField, String> projectFieldsMap = new HashMap<CustomField, String>();

        if (projectFields != null && projectFields.size() > 0) {
            Collections.sort(projectFields, CustomField.ID_COMPARATOR);

            HashMap<String, String> fieldValues = new HashMap<String, String>();
            Iterator<IssueField> issueFieldsIt = issueFields.iterator();
            while (issueFieldsIt.hasNext()) {
                IssueField issueField = (IssueField) issueFieldsIt.next();

                if (issueField.getCustomField() != null
                        && issueField.getCustomField().getId() > 0) {
                    if (issueField.getCustomField().getFieldType() == CustomField.Type.DATE) {
                        Locale locale = LoginUtilities.getCurrentLocale(request);
                        String value = issueField.getValue(locale);
                        fieldValues.put(issueField.getCustomField().getId()
                                .toString(), value);
                    } else {
                        fieldValues.put(issueField.getCustomField().getId()
                                .toString(), issueField
                                .getStringValue());
                    }
                }
            }
            Iterator<CustomField> fieldsIt = projectFields.iterator();
            CustomField field;
            while (fieldsIt.hasNext()) {

                field = fieldsIt.next();

                String fieldValue = fieldValues.get(String.valueOf(field
                        .getId()));
                if (null == fieldValue) {
                    fieldValue = "";
                }
//				if (fieldValue != null && field.getFieldType() == CustomField.Type.LIST) { 
//					fieldValue = CustomFieldUtilities.getCustomFieldOptionName(field, 
//							fieldValue, LoginUtilities.getCurrentLocale(request)); 
////					projectFields.get(i).getOptionNameByValue(fieldValue));
//	        	} 
                projectFieldsMap.put(field, fieldValue);

            }

            request.setAttribute("projectFieldsMap", projectFieldsMap);
        }
    }


    protected static final void setupRelationsRequestEnv(List<IssueRelation> relations, HttpServletRequest request) {
        Collections.sort(relations, IssueRelation.LAST_MODIFIED_DATE_COMPARATOR);
        request.setAttribute("issueRelations", relations);

    }


    public static final void setupNotificationsInRequest(
            HttpServletRequest request, Issue issue,
            NotificationService notificationService) {
        List<Notification> notifications = notificationService
                .getIssueNotifications(issue);

        Collections.sort(notifications, Notification.TYPE_COMPARATOR);

        request.setAttribute("notifications", notifications);
        Map<User, Set<Role>> notificationMap = NotificationUtilities
                .mappedRoles(notifications);
        request.setAttribute("notificationMap", notificationMap);
        request.setAttribute("notifiedUsers", notificationMap.keySet());
    }

    public static Map<Integer, List<NameValuePair>> getListOptions(
            HttpServletRequest request, Issue issue,
            List<NameValuePair> ownersList,
            Map<Integer, Set<PermissionType>> userPermissions, Project project,
            User currUser) {
        Map<Integer, List<NameValuePair>> listOptions = new HashMap<Integer, List<NameValuePair>>();

        Locale locale = (Locale) request.getSession().getAttribute(
                Constants.LOCALE_KEY);

        if (ownersList != null && ownersList.size() > 0) {
            listOptions.put(IssueUtilities.FIELD_OWNER, ownersList);
        }

        boolean hasFullEdit = UserUtilities.hasPermission(userPermissions,
                project.getId(), UserUtilities.PERMISSION_EDIT_FULL);

        List<NameValuePair> allStatuses = IssueUtilities.getStatuses(locale);
        List<NameValuePair> statusList = new ArrayList<NameValuePair>();

        if (!hasFullEdit) {

            if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                    && UserUtilities.hasPermission(userPermissions, project
                    .getId(), UserUtilities.PERMISSION_CLOSE)) {
                for (int i = 0; i < allStatuses.size(); i++) {
                    int statusNumber = Integer.parseInt(allStatuses.get(i)
                            .getValue());
                    if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
                            && statusNumber >= IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuses.get(i));
                    } else if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                            && statusNumber >= IssueUtilities.STATUS_RESOLVED) {
                        statusList.add(allStatuses.get(i));
                    }
                }
            } else {
                // Can't change
            }

        } else {

            if (currUser.isSuperUser()) {
                for (int i = 0; i < allStatuses.size(); i++) {
                    statusList.add(allStatuses.get(i));
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED
                    && issue.getStatus() < IssueUtilities.STATUS_RESOLVED) {
                for (int i = 0; i < allStatuses.size(); i++) {
                    int statusNumber = Integer.parseInt(allStatuses.get(i)
                            .getValue());
                    if (statusNumber >= IssueUtilities.STATUS_ASSIGNED
                            && statusNumber < IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuses.get(i));
                    } else if (statusNumber >= IssueUtilities.STATUS_CLOSED
                            && ProjectUtilities
                            .hasOption(
                                    ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                                    project.getOptions())
                            && UserUtilities.hasPermission(userPermissions,
                            project.getId(),
                            UserUtilities.PERMISSION_CLOSE)) {
                        statusList.add(allStatuses.get(i));
                    }
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                    && issue.getStatus() < IssueUtilities.STATUS_CLOSED) {
                for (int i = 0; i < allStatuses.size(); i++) {
                    int statusNumber = Integer.parseInt(allStatuses.get(i)
                            .getValue());
                    if (statusNumber >= IssueUtilities.STATUS_ASSIGNED
                            && statusNumber < IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuses.get(i));
                    } else if (statusNumber >= IssueUtilities.STATUS_CLOSED
                            && UserUtilities.hasPermission(userPermissions,
                            project.getId(),
                            UserUtilities.PERMISSION_CLOSE)) {
                        statusList.add(allStatuses.get(i));
                    }
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
                for (int i = 0; i < allStatuses.size(); i++) {
                    int statusNumber = Integer.parseInt(allStatuses.get(i)
                            .getValue());
                    if ((statusNumber >= IssueUtilities.STATUS_ASSIGNED && statusNumber < IssueUtilities.STATUS_RESOLVED)
                            || statusNumber >= IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuses.get(i));
                    }
                }
            } else {
                // Can't change
            }

        }
        // sort by status code so it will be ascending output.
        Collections.sort(statusList, NameValuePair.VALUE_COMPARATOR);
        listOptions.put(IssueUtilities.FIELD_STATUS, statusList);

        List<NameValuePair> severities = IssueUtilities.getSeverities(locale);
        // sort by severity code so it will be ascending output.
        Collections.sort(severities, NameValuePair.VALUE_COMPARATOR);
        listOptions.put(IssueUtilities.FIELD_SEVERITY, severities);

        List<NameValuePair> resolutions = IssueUtilities.getResolutions(locale);
        listOptions.put(IssueUtilities.FIELD_RESOLUTION, resolutions);

        List<Component> components = project.getComponents();
        Collections.sort(components, Component.NAME_COMPARATOR);
        listOptions.put(IssueUtilities.FIELD_COMPONENTS, Convert
                .componentsToNameValuePairs(components));

        List<Version> versions = project.getVersions();
        // Collections.sort(versions, new Version());
        listOptions.put(IssueUtilities.FIELD_VERSIONS, Convert
                .versionsToNameValuePairs(versions));
        listOptions.put(IssueUtilities.FIELD_TARGET_VERSION, Convert
                .versionsToNameValuePairs(versions));

        List<CustomField> projectFields = project.getCustomFields();
        for (int i = 0; i < projectFields.size(); i++) {
            if (projectFields.get(i).getFieldType() == CustomField.Type.LIST) {
//				projectFields.get(i).setLabels(locale);
                listOptions.put(projectFields.get(i).getId(), Convert
                        .customFieldOptionsToNameValuePairs(projectFields
                                .get(i).getOptions()));
            }
        }

        return listOptions;
    }

    public static List<NameValuePair> fieldOptions(CustomField field) {
        return Convert.customFieldOptionsToNameValuePairs(field.getOptions());
    }
    public static Map<Integer, List<NameValuePair>> mappedFieldOptions(List<CustomField> fields ) {
        Map<Integer, List<NameValuePair>> options = new HashMap<Integer, List<NameValuePair>>(fields.size());
        for (CustomField field: fields) {
            options.put(field.getId(), fieldOptions(field));
        }

        return options;
    }

    public static final void setupIssueForm(IssueForm issueForm, Issue issue,
                                            final Map<Integer, List<NameValuePair>> listOptions,
                                            HttpServletRequest request, ActionMessages errors)
            throws WorkflowException {
        HttpSession session = request.getSession(true);

        IssueService issueService = ServletContextUtils.getItrackerServices()
                .getIssueService();
        Locale locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
        issueForm.setId(issue.getId());
        issueForm.setProjectId(issue.getProject().getId());
        issueForm.setPrevStatus(issue.getStatus());
        issueForm.setCaller(request.getParameter("caller"));

        issueForm.setDescription(HTMLUtilities.handleQuotes(issue
                .getDescription()));
        issueForm.setStatus(issue.getStatus());

        if (!ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, issue
                .getProject().getOptions())) {
            // TODO What happens here, validation?
            try {
                issue.setResolution(IssueUtilities.checkResolutionName(issue
                        .getResolution(), locale));
            } catch (MissingResourceException mre) {
                log.error(mre.getMessage());
            } catch (NumberFormatException nfe) {
                log.error(nfe.getMessage());
            }
        }

        issueForm.setResolution(HTMLUtilities.handleQuotes(issue
                .getResolution()));
        issueForm.setSeverity(issue.getSeverity());

        issueForm.setTargetVersion(issue.getTargetVersion() == null ? -1
                : issue.getTargetVersion().getId());

        issueForm.setOwnerId((issue.getOwner() == null ? -1 : issue.getOwner()
                .getId()));

        List<IssueField> fields = issue.getFields();
        HashMap<String, String> customFields = new HashMap<String, String>();
        for (int i = 0; i < fields.size(); i++) {
            customFields.put(fields.get(i).getCustomField().getId().toString(),
                    fields.get(i).getValue(locale));
        }

        issueForm.setCustomFields(customFields);

        HashSet<Integer> selectedComponents = issueService
                .getIssueComponentIds(issue.getId());
        if (selectedComponents != null) {
            Integer[] componentIds = null;
            ArrayList<Integer> components = new ArrayList<Integer>(
                    selectedComponents);
            componentIds = components.toArray(new Integer[]{});
            issueForm.setComponents(componentIds);
        }

        HashSet<Integer> selectedVersions = issueService
                .getIssueVersionIds(issue.getId());
        if (selectedVersions != null) {
            Integer[] versionIds = null;
            ArrayList<Integer> versions = new ArrayList<Integer>(
                    selectedVersions);
            versionIds = versions.toArray(new Integer[]{});
            issueForm.setVersions(versionIds);
        }

        invokeProjectScripts(issue.getProject(), WorkflowUtilities.EVENT_FIELD_ONPOPULATE, listOptions, errors, issueForm);

        invokeProjectScripts(issue.getProject(), WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, errors, issueForm);

    }


    public static void invokeProjectScripts(Project project, int event, final Map<Integer, List<NameValuePair>> options, ActionMessages errors, IssueForm form)
            throws WorkflowException {
        final Map<Integer, String> values = new HashMap<Integer, String>(options.size());
        for (CustomField field: project.getCustomFields()) {
            values.put(field.getId(), form.getCustomFields().get(String.valueOf(field.getId())));
        }

        WorkflowUtilities.processFieldScripts(project.getScripts(),
                event, values, options, errors, form);

    }

    public static Map<Integer, List<NameValuePair>> invokeProjectScripts(Project project, int event, ActionMessages errors, IssueForm form)
            throws WorkflowException {

        final Map<Integer, List<NameValuePair>> options = EditIssueActionUtil.mappedFieldOptions(project.getCustomFields()) ;
        invokeProjectScripts(project, event, options, errors, form);
        return options;
    }

}
