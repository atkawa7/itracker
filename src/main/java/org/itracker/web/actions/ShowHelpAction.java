package org.itracker.web.actions;

import java.io.File;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;


public class ShowHelpAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(ShowHelpAction.class);
	
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.executeAlways(mapping,form,request,response);
        
        String helpPage = "";
        String helpParam = request.getParameter("page");
        HttpSession session = request.getSession(true);
        Locale currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
        log.debug("Requesting Help Page: " + helpParam);
        if("ct".equals(helpParam)) {
            helpPage = ITrackerResources.getString("itracker.web.helppage.commontasks", currLocale);
        } else if("ab".equals(helpParam)) {
            helpPage = "help_about.jsp";
        } else {
            helpPage = "help_index_" + currLocale + ".jsp";
            if(! (new File(helpPage)).exists()) {
                helpPage = "help_index.jsp";
            }
        }
        log.debug("Redirecting to Help Page: " + helpPage);
        request.setAttribute("helpPage",helpPage);
        
        String pageTitleKey = "itracker.web.showhelp.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
        return mapping.findForward("show_help");
    }
    
    public ShowHelpAction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
