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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.IssueSearchException;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;

public class SearchIssuesAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(SearchIssuesAction.class);
	

    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionErrors errors = new ActionErrors();
//        super.executeAlways(mapping,form,request,response);
        
        String pageTitleKey = "itracker.web.search.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey",pageTitleKey);
        request.setAttribute("pageTitleArg",pageTitleArg);
        
//        if(! isLoggedIn(request, response)) {
//            return mapping.findForward("login");
//        }
        
        HttpSession session = request.getSession();
        
        User user = (User) session.getAttribute(Constants.USER_KEY);
        Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(session);
        if(user == null || userPermissions == null) {
            return mapping.findForward("login");
        }
        
        try {
            
            ReportService reportService = getITrackerServices().getReportService();
            UserService userService = getITrackerServices().getUserService();
            request.setAttribute("rh",reportService);
            request.setAttribute("uh",userService);                      
            
            IssueSearchQuery isqm = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);
            if(isqm == null) {
                return mapping.findForward("searchissues");
            }
            processQueryParameters(isqm, (ValidatorForm) form, errors);
            
            if(errors.isEmpty()) {
                List<Issue> results = getITrackerServices().getIssueService().searchIssues(isqm, user, userPermissions);
                if(log.isDebugEnabled()) {
                    log.debug("SearchIssuesAction received " + results.size() + " results to query.");
                }                               
                
                isqm.setResults(results);
                log.debug("Setting search results with " + isqm.getResults().size() + " results");
                session.setAttribute(Constants.SEARCH_QUERY_KEY, isqm);
            }
        } catch(IssueSearchException ise) {
            if(ise.getType() == IssueSearchException.ERROR_NULL_QUERY) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.nullsearch"));
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        
        if(! errors.isEmpty()) {
            saveMessages(request, errors);
        }
        
        return mapping.getInputForward();
    }
    
    private IssueSearchQuery processQueryParameters(IssueSearchQuery isqm, ValidatorForm form, ActionErrors errors) {
        if(isqm == null) {
            isqm = new IssueSearchQuery();
        }
        
        try {
            Integer creatorValue = (Integer) PropertyUtils.getSimpleProperty(form, "creator");
            if(creatorValue != null && creatorValue.intValue() != -1) {
                isqm.setCreator(creatorValue);
            } else {
                isqm.setCreator(null);
            }
            
            Integer contributorValue = (Integer) PropertyUtils.getSimpleProperty(form, "contributor");
            if(contributorValue != null && contributorValue.intValue() != -1) {
                isqm.setContributor(contributorValue);
            } else {
                isqm.setContributor(null);
            }
            
            Integer ownerValue = (Integer) PropertyUtils.getSimpleProperty(form, "owner");
            if(ownerValue != null && ownerValue.intValue() != -1) {
                isqm.setOwner(ownerValue);
            } else {
                isqm.setOwner(null);
            }
            
            String textValue = (String) PropertyUtils.getSimpleProperty(form, "textphrase");
            if(textValue != null && ! textValue.equals("")) {
                isqm.setText(textValue);
            } else {
                isqm.setText(null);
            }
            
            String resolutionValue = (String) PropertyUtils.getSimpleProperty(form, "resolution");
            if(resolutionValue != null && ! resolutionValue.equals("")) {
                isqm.setResolution(resolutionValue);
            } else {
                isqm.setResolution(null);
            }

            Integer[] projectsArray = (Integer[])PropertyUtils.getSimpleProperty(form, "projects");
            List<Integer> projects = Arrays.asList(projectsArray);
            if(projects == null || projects.size() == 0) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectrequired"));
            } else {
                isqm.setProjects(projects);
            }

            Integer[] severitiesArray = (Integer[])PropertyUtils.getSimpleProperty(form, "severities");
            if( severitiesArray != null && severitiesArray.length > 0 ) {
                List<Integer> severities = Arrays.asList(severitiesArray);
                isqm.setSeverities(severities);
            }

            Integer[] statusesArray = (Integer[])PropertyUtils.getSimpleProperty(form, "statuses");
            if( statusesArray != null && statusesArray.length > 0 ) {
                List<Integer> statuses = Arrays.asList(statusesArray);
                isqm.setStatuses(statuses);
            }

            Integer[] componentsArray = (Integer[])PropertyUtils.getSimpleProperty(form, "components");
            if( componentsArray != null && componentsArray.length > 0 ) {
                List<Integer> components = Arrays.asList(componentsArray);
                isqm.setComponents(components);
            }

            Integer[] versionsArray = (Integer[])PropertyUtils.getSimpleProperty(form, "versions");
            if( versionsArray != null && versionsArray.length > 0 ) {
                List<Integer> versions = Arrays.asList(versionsArray);
                isqm.setVersions(versions);
            }
            
            Integer targetVersion = (Integer) PropertyUtils.getSimpleProperty(form, "targetVersion");
            if(targetVersion != null && targetVersion > 0) {
                isqm.setTargetVersion(targetVersion);
            } else {
                isqm.setTargetVersion(null);
            }
            
            String orderBy = (String) PropertyUtils.getSimpleProperty(form, "orderBy");
            if(orderBy != null && ! orderBy.equals("")) {
                isqm.setOrderBy(orderBy);
            }
            
            Integer type = (Integer) PropertyUtils.getSimpleProperty(form, "type");
            if(type != null) {
                isqm.setType(type);
            }
        } catch(RuntimeException e) {
            log.debug("Unable to parse search query parameters: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidsearchquery"));
        } catch (IllegalAccessException e) {
            log.debug("Unable to parse search query parameters: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidsearchquery"));
		} catch (InvocationTargetException e) {
            log.debug("Unable to parse search query parameters: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidsearchquery"));
		} catch (NoSuchMethodException e) {
            log.debug("Unable to parse search query parameters: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidsearchquery"));
		}
        
        return isqm;
    }
}
