package org.itracker.web.actions.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ListIssuesActionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListIssuesAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(ListIssuesAction.class);

    /* (non-Javadoc)
      * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionForward af = ListIssuesActionUtil.init(this, mapping, request);
        if (af != null) return af;

        log.info("execute: Forward was: " + ListIssuesActionUtil.FWD_LIST_ISSUES);
        return mapping.findForward(ListIssuesActionUtil.FWD_LIST_ISSUES);
    }

}
