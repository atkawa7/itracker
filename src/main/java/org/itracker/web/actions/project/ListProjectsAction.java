package org.itracker.web.actions.project;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Project;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;


public class ListProjectsAction extends ItrackerBaseAction {
    
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.executeAlways(mapping,form,request,response);
        IssueService issueService = this.getITrackerServices().getIssueService();
        ProjectService projectService = this.getITrackerServices().getProjectService();
        List<Project> projects = projectService.getAllAvailableProjects();
        Collections.sort(projects);
        request.setAttribute("projects", projects);
        request.setAttribute("ih",issueService);
        request.setAttribute("ph",projectService);
        
        String pageTitleKey = "itracker.web.listprojects.title";
        String pageTitleArg = "";
        
        
        
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
        logger.info("ListProjectsAction: Forward: listprojects");
        return mapping.findForward("listprojects");
    }
    
    public ListProjectsAction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
