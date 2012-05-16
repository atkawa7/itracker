package org.itracker.web.servlets;

import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.feed.rss.*;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.ModuleUtils;
import org.itracker.model.*;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: masta
 * Date: 25.04.12
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public class RssFeedController extends GenericController {


    public RssFeedController() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

        ServletServerHttpResponse sr = new ServletServerHttpResponse(resp);

        RssChannelHttpMessageConverter conv = new RssChannelHttpMessageConverter();
        try {
            Matcher uriMatcher = Pattern.compile("(?i).*/issues/p?([0-9]+)/?i?([0-9]*)")
                    .matcher(req.getRequestURI());

            Integer projectId = null;
            Integer issueId = null;
            try {
                if (uriMatcher.matches()) {
                    projectId = Integer.valueOf(uriMatcher.group(1));
                    issueId = uriMatcher.group(2).equals("") ? null : Integer.valueOf(uriMatcher.group(2));

                }

            } catch (RuntimeException e) {
                deny(sr);
                return;
            }

            User um = (User) req.getSession().getAttribute(Constants.USER_KEY);
            Integer currUserId = um.getId();

            final IssueService is = getITrackerServices(getServletContext()).getIssueService();
            final ProjectService ps = getITrackerServices(getServletContext()).getProjectService();
            final ConfigurationService cs = getITrackerServices(getServletContext()).getConfigurationService();

            final String baseURL = cs.getSystemBaseURL();
            final String generator = cs.getProperty("notification_from_text",
                    "iTracker "
                            + cs.getProperty(Constants.VERSION_KEY, "3"));


            Channel c = new Channel(conv.getSupportedMediaTypes().get(0).getType());


            final SyndFeed f = new SyndFeedImpl();


            c.setGenerator(generator);


            c.setModules(Arrays.asList(
                    new DCModuleImpl(),
                    new SyModuleImpl()
            ));


            c.setFeedType("rss_2.0");

            final Map<Integer, Set<PermissionType>> userPermissions = getPermissions(req.getSession(false));

            if (null != issueId) {
                Issue i = is.getIssue(issueId);
                if (!IssueUtilities.canViewIssue(i, currUserId, userPermissions)) {
                    deny(sr);
                    return;
                }

                toChannel(c, i, req, baseURL);
            } else if (null != projectId) {
                Project p = ps.getProject(projectId);
                if (null == p) {
                    deny(sr);
                    return;
                }
                if (!UserUtilities.hasPermission(userPermissions, p.getId(), UserUtilities.PERMISSION_VIEW_USERS)) {
                    deny(sr);
                    return;
                }

                toChannel(c, p, currUserId, is, userPermissions, req, baseURL);

            } else {

                toChannel(c, userPermissions, ps, req, baseURL);
            }

            conv.write(c, conv.getSupportedMediaTypes().get(0), sr);
        } finally {
            sr.close();
        }

    }

    private void toChannel(Channel c, Map<Integer, Set<PermissionType>> userPermissions, ProjectService ps, HttpServletRequest req, String baseURL) {
        List<Project> projects = ps.getAllAvailableProjects();
        c.setTitle("Projects Overview");
        c.setDescription("Projects for " + baseURL);
        c.setLink(baseURL);
        c.setUri(baseURL);
        for (Project p : projects) {
            if (userPermissions.containsKey(p.getId())
                    && UserUtilities.hasPermission(userPermissions, p.getId(), UserUtilities.PERMISSION_VIEW_USERS)) {
                Item pi = new Item();
                pi.setLink(getProjectURL(p, null, req, getServletContext(), baseURL));

                pi.setTitle(p.getName());

                Description desc = new Description();
                desc.setType(Content.TEXT);
                desc.setValue("Project " + p.getId());
                pi.setDescription(desc);

                Content content = new Content();
                content.setType(Content.HTML);

                content.setValue(
//                        HtmlUtils.htmlEscape(
                        p.getDescription()
//                )
                );

                c.getItems().add(pi);
            }
        }
    }

    private void toChannel(Channel channel, Project project, Integer currUserId, IssueService is, Map<Integer, Set<PermissionType>> userPermissions, HttpServletRequest req, String baseURL) {
        List<Issue> listIssues;
        if (!UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_VIEW_ALL)) {
            listIssues = new ArrayList<Issue>();
            for (Issue issue : is.getIssuesByProjectId(project.getId())) {
                if (IssueUtilities.canViewIssue(issue, currUserId, userPermissions)) {
                    listIssues.add(issue);
                }
            }
        } else {
            listIssues = is.getIssuesByProjectId(project.getId());
        }

        channel.setDescription("Project Feed for " + project.getId());
        channel.setTitle("Project#" + project.getId() + ": " + project.getDescription());
        channel.setLink(getProjectURL(project, null, req, getServletContext(), baseURL));

        channel.setTitle(project.getId() + " - " + project.getDescription());
        is.getIssuesByProjectId(project.getId());
        List<Item> sItems = projectIssueItems(req, baseURL, project, listIssues);

        channel.setItems(sItems);
    }

    private void toChannel(Channel channel, Issue issue, HttpServletRequest req, String baseURL) {
        channel.setDescription("Issue Feed for " + issue.getId());
        channel.setTitle("Issue#" + issue.getId() + ": " + issue.getDescription());
        channel.setLink(getIssueURL(issue, null, req, getServletContext(), baseURL));

        channel.setTitle(issue.getId() + " - " + issue.getDescription());
        List<Item> sItems = issueHistoryItems(req, baseURL, issue);

        channel.setItems(sItems);
    }


    private void deny(ServletServerHttpResponse sr) {
        sr.getHeaders().clear();
        sr.setStatusCode(HttpStatus.FORBIDDEN);
    }

    private List<Item> projectIssueItems(HttpServletRequest req, String baseURL, Project p, List<Issue> listIssues) {
//
//        int numViewable = 0;
//        boolean hasIssues = false;
//        final Map<Integer, Set<PermissionType>> userPermissions = getPermissions(req.getSession(false));


//        final User currentUser = LoginUtilities.getCurrentUser(req);
//        final boolean hasViewAll = UserUtilities.hasPermission(userPermissions, p.getId(), UserUtilities.PERMISSION_VIEW_ALL);

//        final Locale locale = getLocale(req);
//        if (hasViewAll) {
//            numViewable = listIssues.size();
//        } else {
//            for (Issue listIssue : listIssues) {
//                if (IssueUtilities.canViewIssue(listIssue, currentUser.getId(), userPermissions)) {
//                    numViewable++;
//                }
//            }
//        }
        List<Item> sItems = new ArrayList<Item>(listIssues.size());

//        int row = 0;
//        int k = 0;
        final Iterator<Issue> issuesIt = listIssues.iterator();
// start copying from Models to PTOs
        Issue issue;
        String statusLocalizedString, severityLocalizedString, componentsSize;
        Item current;

        while (issuesIt.hasNext()) {

            issue = issuesIt.next();
            current = new Item();
            current.setUri("http://mgpro.local/itracker/servlets/issues/1/4");
            current.setLink(getIssueURL(issue, null, req, getServletContext(), baseURL));

            Guid guid = new Guid();
            guid.setValue(current.getLink());
            guid.setPermaLink(true);

            current.setGuid(guid);

            Description desc = new Description();
            desc.setType(Content.TEXT);
            desc.setValue("Issue " + issue.getId());
            current.setDescription(desc);

            current.setTitle(issue.getDescription());
            current.setPubDate(issue.getLastModifiedDate());
            Content c = new Content();
            c.setType(Content.HTML);

            c.setValue(
//                    HtmlUtils.htmlEscape(
                    issue.getHistory().get(issue.getHistory().size() - 1).getDescription()
//            )
            );
            current.setContent(c);

// TODO semantic style
//            statusLocalizedString = IssueUtilities.getStatusName(issue.getStatus(), locale);
//            severityLocalizedString = IssueUtilities.getSeverityName(issue.getSeverity(), locale);
//            if (issue.getComponents().size() == 0) {
//                componentsSize = ITrackerResources.getString(
//                        ListIssuesActionUtil.RES_KEY_UNKNOWN, locale);
//            } else {
//                componentsSize = issue.getComponents().get(0).getName()
//                        + (issue.getComponents().size() > 1 ? " (+)" : "");
//            }

//            current.setStatusLocalizedString(statusLocalizedString);
//            current.setSeverityLocalizedString(severityLocalizedString);
//            current.setComponentsSize(componentsSize);
//            if (issue.getOwner() == null) {
//                current.setUnassigned(true);
//            }
//            if (p.getStatus() == Status.ACTIVE && !IssueUtilities.hasIssueNotification(issue, p, currentUser.getId())) {
//                current.setUserHasIssueNotification(true);
//            }
//            if (p.getStatus() == Status.ACTIVE) {
//                if (IssueUtilities.canEditIssue(issue, currentUser.getId(), userPermissions)) {
//                    current.setUserCanEdit(true);
//                }
//            }

            sItems.add(current);
        }
        return sItems;
    }

    private List<Item> issueHistoryItems(HttpServletRequest req, String baseURL, Issue i) {
        List<Item> sItems = new ArrayList<Item>(i.getHistory().size());
        Item current;

        for (IssueHistory ih : i.getHistory()) {
            current = new Item();
            Content content = new Content();

            current.setLink(getIssueURL(i, ih, req, getServletContext(), baseURL));

            Guid guid = new Guid();
            guid.setValue(current.getLink());
            guid.setPermaLink(true);
            current.setGuid(guid);

            current.setTitle("History entry " + (sItems.size() + 1));

            content.setType(Content.HTML);
            content.setValue(
//                    HtmlUtils.htmlEscape(
                    "<p>" + ih.getDescription() + "</p>"
//            )
            );
//            current.setContent(content);
            current.setPubDate(ih.getCreateDate());

            current.setAuthor(ih.getUser().getLogin());

            sItems.add(current);
        }
        return sItems;
    }


    private String getIssueURL(Issue i, @Nullable IssueHistory ih, HttpServletRequest req, ServletContext context, String baseUrl) {
        ModuleConfig conf = ModuleUtils.getInstance().getModuleConfig(
                "/module-projects",
                req,
                context);

        ForwardConfig forwardConfig = conf.findForwardConfig("viewissue");
        return
                //HtmlUtils.htmlEscape(
                baseUrl + TagUtils.getInstance().pageURL(req, forwardConfig.getPath(), conf)
                        + i.getId()
//                        + "?id=" + i.getId() + (null == ih ? "" : "#history_" + ih.getId()
////                )
//                )
                ;
    }

    private String getProjectURL(Project p, @Nullable Issue i, HttpServletRequest req, ServletContext context, String baseUrl) {
        ModuleConfig conf = ModuleUtils.getInstance().getModuleConfig(
                "/module-projects",
                req,
                context);

        ForwardConfig forwardConfig = conf.findForwardConfig("listissues");
        return
//                HtmlUtils.htmlEscape(
                baseUrl + TagUtils.getInstance().pageURL(req, forwardConfig.getPath(), conf) + p.getId()
//                        + "?id=" + p.getId() + (null == i ? "" : "#issue_"
//                        + i.getId()
////                        )
//                )
                ;
    }
}
