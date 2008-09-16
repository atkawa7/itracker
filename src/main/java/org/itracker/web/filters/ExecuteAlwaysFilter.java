package org.itracker.web.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ITrackerServices;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

/**
 * Configurations:
 * <ul>
 * <li>AuthExcludedPaths: Comma separated list of Regex url-Patterns (eg.
 * <code>/login.do, /unprotected-path/**</code>)</li>
 * </ul>
 * 
 * @author ranks
 * 
 */
public class ExecuteAlwaysFilter implements Filter {

	/**
	 * Logger for ExecuteAlwaysFilter
	 */
	private static final Logger log = Logger
			.getLogger(ExecuteAlwaysFilter.class);
	private static final String DEFAULT_LOGIN_FORWARD = "/login.do";
	/**
	 * Name for session key for forward after successful authentication.
	 */
	private static final String SES_KEY_REDIRECT_ON_SUCCESS = ExecuteAlwaysFilter.class
			.getName()
			+ "/REDIRECT_ON_SUCCESS";

	private ITrackerServices iTrackerServices;
	/**
	 * this match paths which are not protected
	 */
	private Set<String> unprotectedMatches = null;

	private String loginForwardPath;

	public void destroy() {
		this.unprotectedMatches = null;
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (null == this.unprotectedMatches) {
			RuntimeException re = new IllegalStateException(
					"Filter has not been initialized yet.");
			log.error("doFilter: failed, not initialized", re);
			throw re;
		}

		if (!(servletRequest instanceof HttpServletRequest)) {
			RuntimeException re = new IllegalArgumentException(
					"Usupported servlet-request of type: "
							+ servletRequest.getClass().getName());
			log.error("doFilter: failed, invalid request type", re);
			throw re;
		}

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpSession session = request.getSession(true);

		String path = request.getRequestURI().substring(
				request.getContextPath().length());
		if (log.isDebugEnabled()) {
			log.debug("doFilter: called with path " + path);
		}

		// From IrackerBaseAction.executeAlways
		if (log.isDebugEnabled()) {
			log
					.debug("doFilter: setting the common request attributes, (coming from the former header.jsp)");
		}
		ConfigurationService configurationService = getITrackerServices()
				.getConfigurationService();
		setupCommonReqAttributes(request, configurationService);

		boolean protect = isProtected(path, this.unprotectedMatches);

		// do not protect the login-page itself.
		if (protect && this.loginForwardPath.equals(path)) {
			protect = false;
		}

		if (log.isDebugEnabled()) {
			log.debug("doFilter: protecting '" + path + "': " + protect);
		}

		// pasted from page_init (which is now empty, since we are moving logic
		// into actions):

		// TODO: think about this: are permissions put into the request? or into
		// the session? Markys knowledge: permissions are being set, when login
		// happens... do we really need the following line then?
		// removed:
		// Map<Integer, Set<PermissionType>> permissions =
		// getUserPermissions(request
		// .getSession());
//		Locale currLocale = LoginUtilities.getCurrentLocale(request);
//		request.setAttribute("currLocale", currLocale);

		User currUser = (User) session.getAttribute("currUser");
		if (null != currUser) {
			if (log.isDebugEnabled()) {
				log.debug("doFilter: found user in session");
			}
			String currLogin = (currUser == null ? null : currUser.getLogin());
			request.setAttribute("currLogin", currLogin);
		} else if (!protect) {
			session.setAttribute("currUser", currUser);
			// request.setAttribute("permissions", permissions);
			// TODO: itracker.web.generic.unknown for unknown user?
			request.setAttribute("currLogin", ITrackerResources
					.getString("itracker.web.generic.unknown"));
			request.getSession().setAttribute("currUser", null);
		} else {
			// unauthenticated.. forward to login

			forwardToLogin(path
					+ (request.getQueryString() != null ? "?"
							+ request.getQueryString() : ""), request,
					(HttpServletResponse) response);
			return;
		}
		try {
			if (log.isDebugEnabled()) {
				log.info("doFilter: executing chain..");
			}
			chain.doFilter(request, response);

			if (log.isDebugEnabled()) {
				log.info("doFilter: completed chain execution.");
			}
		} catch (RuntimeException e) {
			log.error(
					"doFilter: failed to execute chain with runtime exception: "
							+ e.getMessage(), e);
			throw e;
		} catch (IOException ioe) {
			log.error("doFilter: failed to execute chain with i/o exception: "
					+ ioe.getMessage(), ioe);
			throw ioe;
		} catch (ServletException se) {
			log.error(
					"doFilter: failed to execute chain with servlet exception: "
							+ se.getMessage(), se);
			throw se;
		} catch (Error err) {
			log.fatal("doFilter: caught fatal error executing filter chain",
					err);
			throw err;
		}
	}

	private static final void setupCommonReqAttributes(
			HttpServletRequest request,
			ConfigurationService configurationService) {
		boolean allowForgotPassword = true;
		boolean allowSelfRegister = false;
		boolean allowSaveLogin = true;
		String alternateLogo = null;

		allowForgotPassword = configurationService.getBooleanProperty(
				"allow_forgot_password", true);
		allowSelfRegister = configurationService.getBooleanProperty(
				"allow_self_register", false);
		allowSaveLogin = configurationService.getBooleanProperty(
				"allow_save_login", true);
		alternateLogo = configurationService
				.getProperty("alternate_logo", null);
		Locale currLocale = LoginUtilities.getCurrentLocale(request);
		
		// TODO: this should be configured per-instance. Request server-name
		// should only be used for exception and logged (configuration not
		// found!)
		String baseURL = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();

		request.setAttribute("allowForgotPassword", Boolean
				.valueOf(allowForgotPassword));
		request.setAttribute("allowSelfRegister", Boolean
				.valueOf(allowSelfRegister));
		request.setAttribute("allowSaveLogin", Boolean.valueOf(allowSaveLogin));
		request.setAttribute("alternateLogo", alternateLogo);
		request.setAttribute("baseURL", baseURL);
		// TODO: remove deprecated currLocale attribute
		request.setAttribute("currLocale", currLocale);
		request.setAttribute(Constants.LOCALE_KEY, currLocale);
	}

	private static final boolean isProtected(String path, Set<String> patterns) {
		if (null == path) {
			// match the empty path
			if (patterns.contains("") || patterns.contains("/")
					|| patterns.contains("/*") || patterns.contains("/**")) {
				if (log.isDebugEnabled()) {
					log.debug("isProtected: matched path: " + path);
				}
				return false;
			}
		} else {

			Iterator<String> matchPattern = patterns.iterator();
			Pattern pattern;

			while (matchPattern.hasNext()) {
				pattern = Pattern.compile(matchPattern.next());
				if (log.isDebugEnabled()) {
					log.debug("isProtected: processing path " + path
							+ " for pattern " + pattern.pattern());
				}
				if (pattern.matcher(path).matches()) {
					if (log.isDebugEnabled()) {
						log.debug("isProtected: matched path: " + path);
					}
					return false;
				}
			}

		}
		if (log.isDebugEnabled()) {
			log.debug("isProtected: protecting " + path);
		}
		return true;
	}

	/**
	 * 
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		if (null != unprotectedMatches) {
			throw new IllegalStateException(
					"Filter was already initialized before.");
		}
		String excludePaths = filterConfig
				.getInitParameter("AuthExcludedPaths");

		this.loginForwardPath = filterConfig.getInitParameter("LoginForward");
		if (null == this.loginForwardPath) {
			this.loginForwardPath = DEFAULT_LOGIN_FORWARD;
		}
		this.unprotectedMatches = new HashSet<String>();
		if (null != excludePaths) {
			StringTokenizer tk = new StringTokenizer(excludePaths, ",");
			while (tk.hasMoreTokens()) {
				this.unprotectedMatches.add(tk.nextToken());
			}
		}
		if (log.isInfoEnabled()) {
			log.info("init: initialized with " + this.loginForwardPath
					+ ", excludes: " + this.unprotectedMatches);
		}
	}

	public ITrackerServices getITrackerServices() {
		if (null == this.iTrackerServices) {

			this.iTrackerServices = ServletContextUtils.getItrackerServices();
		}
		return iTrackerServices;
	}

	/**
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param thisactionforward
	 * @return String - outcome
	 */
	private void forwardToLogin(String path, HttpServletRequest request,
			HttpServletResponse response) {
		if (log.isDebugEnabled()) {
			log.debug("forwardToLogin: called with " + path + " request: "
					+ request + " response: " + response);
		}
		String forwardPath = request.getContextPath() + this.loginForwardPath;
		if (log.isDebugEnabled()) {
			log
					.debug("forwardToLogin: (formerly Checklogin tag) procedure... to "
							+ forwardPath);
		}
		try {

			HttpSession session = request.getSession();

			log.info("forwardToLogin: setting redirectURL "
					+ SES_KEY_REDIRECT_ON_SUCCESS + " = " + path);
			session.setAttribute(SES_KEY_REDIRECT_ON_SUCCESS, path);

			response.sendRedirect(forwardPath);
			response.flushBuffer();

		} catch (Exception e) {
			log.error("forwardToLogin: IOException while checking login", e);
			response.reset();
			try {
				response.sendRedirect(forwardPath);
			} catch (IOException e1) {
				log.error("forwardToLogin: failed to redirect to "
						+ forwardPath, e1);
			}
		}
	}

	public static void redirectToOnLoginSuccess(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String path = (String) request.getSession().getAttribute(
				SES_KEY_REDIRECT_ON_SUCCESS);
		if (null == path) {
			path = "/";
		}
		if (log.isDebugEnabled()) {
			log.debug("redirectToOnLoginSuccess: sending redirect to " + path);
		}
		response.sendRedirect(path);
	}

	public static ActionForward forwardToOnLoginSuccess(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String path = (String) request.getSession().getAttribute(
				SES_KEY_REDIRECT_ON_SUCCESS);
		if (null == path) {
			return new ActionForward("arrivalforward");
		}
		if (log.isDebugEnabled()) {
			log.debug("redirectToOnLoginSuccess: sending redirect to " + path);
		}
		return new ActionForward(path, true);
	}
}
