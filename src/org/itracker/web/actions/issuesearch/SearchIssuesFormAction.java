/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.web.actions.issuesearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.UserPreferences;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.SearchForm;
import org.itracker.web.util.Constants;



public class SearchIssuesFormAction extends ItrackerBaseAction {

    public SearchIssuesFormAction() {
    }

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
        super.executeAlways(mapping,form,request,response);
        if(! isLoggedIn(request, response)) {
            return mapping.findForward("login");
        }

        HttpSession session = request.getSession();

        try {
            ProjectService projectService = getITrackerServices().getProjectService();

            ReportService reportService = this.getITrackerServices().getReportService();
            UserService userService = this.getITrackerServices().getUserService();
            request.setAttribute("rh",reportService); 
            request.setAttribute("uh",userService); 
            
            String pageTitleKey = "itracker.web.search.title"; 
	        String pageTitleArg = "";			
	        request.setAttribute("pageTitleKey",pageTitleKey); 
	        request.setAttribute("pageTitleArg",pageTitleArg); 
            
            UserPreferences userPrefs = (UserPreferences) session.getAttribute(Constants.PREFERENCES_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);

            String projectId = request.getParameter("projectId");
            String action = (String) PropertyUtils.getSimpleProperty((ValidatorForm) form, "action");

            SearchForm searchForm = (SearchForm) form;
            if(searchForm == null) {
                searchForm = new SearchForm();
            }

            boolean newQuery = false;
            IssueSearchQuery query = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);

            logger.debug("projectid = " + projectId);
            logger.debug("query type = " + (query == null ? "NULL" : query.getType().toString()));
            logger.debug("query projectid = " + (query == null ? "NULL" : query.getProjectId().toString()));

            if(query == null || query.getType() == null || "reset".equalsIgnoreCase(action) || (userPrefs != null && ! userPrefs.getRememberLastSearch())) {
                logger.debug("New search query.  No existing query, reset forced, or saved querys not allowed.");
                query = new IssueSearchQuery();
                query.setType(IssueSearchQuery.TYPE_FULL);
                newQuery = true;
            } else if(query.getType().intValue() == IssueSearchQuery.TYPE_FULL.intValue() && projectId != null) {
                logger.debug("New search query.  Previous query FULL, new query PROJECT.");
                query = new IssueSearchQuery();
                query.setType(IssueSearchQuery.TYPE_PROJECT);
                newQuery = true;
            } else if(query.getType().intValue() == IssueSearchQuery.TYPE_PROJECT.intValue()) {
                if(projectId == null || projectId.equals("")) {
                    logger.debug("New search query.  Previous query PROJECT, new query FULL.");
                    query = new IssueSearchQuery();
                    query.setType(IssueSearchQuery.TYPE_FULL);
                    newQuery = true;
                } else if(! projectId.equals(query.getProjectId().toString())) {
                    logger.debug("New search query.  Requested project (" + projectId + ") different from previous query (" + query.getProjectId().toString() + ")");
                    query = new IssueSearchQuery();
                    query.setType(IssueSearchQuery.TYPE_PROJECT);
                    newQuery = true;
                }
            }

            query.setAvailableProjects(null);

            List<Project> projects = projectService.getAllAvailableProjects();
            Collections.sort(projects);

            List<Project> availableProjectsList = new ArrayList<Project>();
            List<Integer> selectedProjectsList = new ArrayList<Integer>();
            for(int i = 0; i < projects.size(); i++) {
                if(! UserUtilities.hasPermission(userPermissions, projects.get(i).getId(), UserUtilities.PERMISSION_VIEW_ALL) &&
                   ! UserUtilities.hasPermission(userPermissions, projects.get(i).getId(), UserUtilities.PERMISSION_VIEW_USERS)) {
                       continue;
                }
                logger.debug("Adding project " + projects.get(i).getId() + " to list of available projects.");
                availableProjectsList.add(projects.get(i));

                if(projectId != null && projects.get(i).getId().toString().equals(projectId)) {
                    query.setType(IssueSearchQuery.TYPE_PROJECT);
                    query.setProject(projects.get(i));
                    break;
                } else {
                    for(int j = 0; j < query.getProjects().size(); j++) {
                        if(query.getProjects().get(j).equals(projects.get(i).getId())) {
                            selectedProjectsList.add(projects.get(i).getId());
                            break;
                        }
                    }
                }
            }
         
            

            if(availableProjectsList.size() != 0) {
            	logger.debug("Issue Search has " + availableProjectsList.size() + " available projects.");
                query.setAvailableProjects(availableProjectsList);
                if(query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) {
                    searchForm.setProject(query.getProjectId());
                }

                if(newQuery) {
                    logger.debug("New search query.  Clearing results and setting defaults.");
                    query.setResults(null);
                    List<Integer> selectedStatusesIntegerList = new ArrayList<Integer>();
                    for(int i = 0; i < IssueUtilities.getStatuses().size(); i++) {
                        try {
                            int statusNumber = Integer.parseInt(IssueUtilities.getStatuses().get(i).getValue());
                            if(statusNumber < IssueUtilities.STATUS_CLOSED) {
                            	selectedStatusesIntegerList.add(new Integer(statusNumber));
                            }
                        } catch(Exception e) {
                            logger.debug("Invalid status entry: " + IssueUtilities.getStatuses().get(i));
                        }
                    }
                    Integer[] statusesArray = new Integer[selectedStatusesIntegerList.size()];
                    selectedStatusesIntegerList.toArray(statusesArray);
                    searchForm.setStatuses(statusesArray);

                    List<Integer> selectedSeverities = new ArrayList<Integer>();
                    for(int i = 1; i <= IssueUtilities.getNumberSeverities(); i++) {
                        selectedSeverities.add(new Integer(i));
                    }
                    Integer[] severitiesArray = new Integer[selectedSeverities.size()];
                    selectedSeverities.toArray(severitiesArray);
                    searchForm.setSeverities(severitiesArray);
                } else {
                    List<Integer> selectedProjects = new ArrayList<Integer>();
                    selectedProjects = selectedProjectsList;
                    query.setProjects(selectedProjects);
                    
                    Integer[] componentsArray = new Integer[query.getComponents().size()];
                    query.getComponents().toArray(componentsArray);
                    searchForm.setComponents(componentsArray);
                    
                    searchForm.setContributor(query.getContributor());
                    searchForm.setCreator(query.getCreator());
                    searchForm.setOrderBy(query.getOrderBy());
                    searchForm.setProject(query.getProjectId());
                    
                    Integer[] projectsArray = new Integer[query.getProjects().size()];
                    query.getProjects().toArray(projectsArray);
                    searchForm.setProjects(projectsArray);
                    
                    searchForm.setResolution(query.getResolution());
                    
                    Integer[] severitiesArray = new Integer[query.getSeverities().size()];
                    query.getSeverities().toArray(severitiesArray);
                    searchForm.setSeverities(severitiesArray);
    
                    Integer[] statusesArray = new Integer[query.getStatuses().size()];
                    query.getStatuses().toArray(statusesArray);
                    searchForm.setStatuses(statusesArray);
                           
                    searchForm.setTargetVersion(query.getTargetVersion());
                    searchForm.setTextphrase(query.getText());
                    
                    Integer[] versionsArray = new Integer[query.getVersions().size()];
                    query.getVersions().toArray(versionsArray);
                    searchForm.setVersions(versionsArray);
                     
                }
                
       
                request.setAttribute("searchForm", searchForm);
                
                session.setAttribute(Constants.SEARCH_QUERY_KEY, query);
                
            } else {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprojects"));
            }

            if(errors.isEmpty()) {
                return mapping.getInputForward();
            }
        } catch(Exception e) {
            logger.error("Exception while creating search issues form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }

        return mapping.findForward("error");
    }

}
  