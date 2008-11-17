package org.itracker.web.actions.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.NameValuePair;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.ITrackerServices;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.ProjectUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

public class EditProjectFormActionUtil {
	private static final Logger log = Logger.getLogger(EditProjectFormActionUtil.class);

	public class CustomFieldInfo {
		private int id;
		private String name;
		private String type;

		public CustomFieldInfo(Integer id, String customFieldName, String string) {
			this.id = id; this.name = customFieldName; this.type = string;
		}

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}
	public class VersionInfo {
		private int id;
		private String number;
		private String description;
		private Date lastModifiedDate;
		private Long countIssuesByVersion;

		public VersionInfo(int id, String number, String description, 
				Date lastModifiedDate, Long countIssuesByVersion) {
			this.id = id;
			this.number = number;
			this.description = description;
			this.lastModifiedDate = lastModifiedDate;
			this.countIssuesByVersion = countIssuesByVersion;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Date getDate() {
			return lastModifiedDate;
		}
		public void setDate(Date lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}
		public Long getCount() {
			return countIssuesByVersion;
		}
		public void setCount(Long countIssuesByVersion) {
			this.countIssuesByVersion = countIssuesByVersion;
		}

	}
	public class ComponentInfo {
		private int id;
		private String name;
		private String description;
		private Date lastModifiedDate;
		private Long countIssuesByComponent;

		public ComponentInfo(Integer id, String name, String description,
				Date lastModifiedDate, Long countIssuesByComponent) {
			this.id = id; 
			this.name = name; 
			this.description = description;
			this.lastModifiedDate = lastModifiedDate; 
			this.countIssuesByComponent = countIssuesByComponent;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Date getDate() {
			return lastModifiedDate;
		}
		public void setDate(Date date) {
			this.lastModifiedDate = date;
		}
		public Long getCount() {
			return countIssuesByComponent;
		}
		public void setCount(Long countIssuesByComponent) {
			this.countIssuesByComponent = countIssuesByComponent;
		}

	}

	public ActionForward init(ActionMapping mapping, HttpServletRequest request){
		ITrackerServices itrackerServices = ServletContextUtils
		.getItrackerServices();
		ProjectService projectService = itrackerServices.getProjectService();
		UserService userService = itrackerServices.getUserService();

		Boolean allowPermissionUpdate = (Boolean) request.getAttribute("allowPermissionUpdate");

		HttpSession session = request.getSession();
		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
		.getUserPermissions(session);
		Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
		boolean isUpdate;
		
		if (project == null) {
			log.info("EditProjectAction: Forward: unauthorized");
			return mapping.findForward("unauthorized");
		}
		else {
			isUpdate = false;
			if (project.getId().intValue() > 0) {
				isUpdate = true;
			}
		}
		request.setAttribute("isUpdate", isUpdate);


		List<NameValuePair> statuses = new ArrayList<NameValuePair>();
		statuses.add(new NameValuePair(ProjectUtilities.getStatusName(Status.ACTIVE, (java.util.Locale) session.getAttribute("currLocale")), Integer.toString(Status.ACTIVE.getCode())));
		statuses.add(new NameValuePair(ProjectUtilities.getStatusName(Status.VIEWABLE, (java.util.Locale) session.getAttribute("currLocale")), Integer.toString(Status.VIEWABLE.getCode())));
		statuses.add(new NameValuePair(ProjectUtilities.getStatusName(Status.LOCKED, (java.util.Locale) session.getAttribute("currLocale")), Integer.toString(Status.LOCKED.getCode())));
		request.setAttribute("statuses", statuses);

		Set<User> owners = new TreeSet<User>(User.NAME_COMPARATOR);
		owners.addAll(userService.getUsersWithProjectPermission(project.getId(), UserUtilities.PERMISSION_VIEW_ALL));
		owners.addAll(project.getOwners());
		request.setAttribute("owners", owners);

		boolean allowPermissionUpdateOption = allowPermissionUpdate==null?false:allowPermissionUpdate && UserUtilities.hasPermission(permissions, new Integer(-1), UserUtilities.PERMISSION_USER_ADMIN);
		request.setAttribute("allowPermissionUpdateOption", allowPermissionUpdateOption);

		List<User> users = new ArrayList<User>();
		List<User> activeUsers = userService.getActiveUsers();
		Collections.sort(activeUsers, User.NAME_COMPARATOR);
		for (int i = 0; i < activeUsers.size(); i++) {
			if (owners.contains(activeUsers.get(i))) {
				continue;
			}
			users.add(activeUsers.get(i));
		}
		request.setAttribute("users", users);

		List<NameValuePair> permissionNames = UserUtilities.getPermissionNames((java.util.Locale) session.getAttribute("currLocale"));
		request.setAttribute("permissions", permissionNames);

		request.setAttribute("optionSupressHistoryHtml", Integer.toString(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML));
		request.setAttribute("optionPredefinedResolutions", Integer.toString(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS));
		request.setAttribute("optionAllowAssignToClose", Integer.toString(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE));
		request.setAttribute("optionAllowSefRegisteredCreate", Integer.toString(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE));
		request.setAttribute("optionLiteralHistoryHtml", Integer.toString(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML));
		request.setAttribute("optionNoAttachments", Integer.toString(ProjectUtilities.OPTION_NO_ATTACHMENTS));
		request.setAttribute("optionAllowSelfRegisteredViewAll", Integer.toString(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL));

		List<CustomField> customFields = IssueUtilities.getCustomFields();


		
		List<CustomFieldInfo> fieldInfos = new ArrayList<CustomFieldInfo>(customFields.size());
		Iterator<CustomField> fieldsIt = customFields.iterator();
		CustomField ci;
		while (fieldsIt.hasNext()) {
			ci = (CustomField) fieldsIt.next();
			fieldInfos.add(new CustomFieldInfo( ci.getId(), 
					CustomFieldUtilities.getCustomFieldName(ci.getId(), (java.util.Locale) session.getAttribute("currLocale")), 
					CustomFieldUtilities.getTypeString(ci.getFieldType(), (java.util.Locale) session.getAttribute("currLocale"))));
		}
		
		request.setAttribute("customFields", fieldInfos);



		List<Version> versions = project.getVersions();
		Collections.sort(versions, new Version.VersionComparator());
		List<VersionInfo> vis = new ArrayList<VersionInfo>(); 

		for (Version v: versions)
			vis.add(new VersionInfo(v.getId(), v.getNumber(), v.getDescription(), v.getLastModifiedDate(), projectService.countIssuesByVersion(v.getId())));    	   
		request.setAttribute("versions", vis);

		List<Component> components = project.getComponents();
		Collections.sort(components);
		List<ComponentInfo> cis = new ArrayList<ComponentInfo>(); 

		for (Component c: components)
			cis.add(new ComponentInfo(c.getId(), c.getName(), c.getDescription(), c.getLastModifiedDate(), projectService.countIssuesByComponent(c.getId())));    	   
		request.setAttribute("components", cis);
		return null;         
	}

}
