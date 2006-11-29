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

package org.itracker.services.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;

import org.itracker.persistence.dao.IssueDAO;
import org.itracker.services.IssueService;
import org.itracker.services.IssueSearchService;
import org.itracker.services.ConfigurationService;
import org.itracker.services.exceptions.IssueSearchException;
import org.itracker.services.util.HTMLUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.SystemConfigurationUtilities;


public class IssueSearchServiceImpl implements IssueSearchService {
    
    private static String componentbeanTableName;
    private static String componentbeanRelTableName;
    private static String issuebeanTableName;
    private static String issuehistorybeanTableName;
    private static String projectbeanTableName;
    private static String versionbeanTableName;
    private static String versionbeanRelTableName;
    
    private final Logger logger;
    private IssueService issueService;
    private IssueDAO issueDAO;

    public IssueSearchServiceImpl(ConfigurationService configurationService) {
        this.logger = Logger.getLogger(getClass());
        componentbeanTableName = configurationService.getProperty("componentbean_table_name",
                SystemConfigurationUtilities.DEFAULT_COMPONENTBEAN_TABLE_NAME);
        componentbeanRelTableName = configurationService.getProperty("componentbean_rel_table_name",
                SystemConfigurationUtilities.DEFAULT_COMPONENTBEAN_REL_TABLE_NAME);
        issuebeanTableName = configurationService.getProperty("issuebean_table_name",
                SystemConfigurationUtilities.DEFAULT_ISSUEBEAN_TABLE_NAME);
        issuehistorybeanTableName = configurationService.getProperty("issuehistorybean_table_name",
                SystemConfigurationUtilities.DEFAULT_ISSUEHISTORYBEAN_TABLE_NAME);
        projectbeanTableName = configurationService.getProperty("projectbean_table_name",
                SystemConfigurationUtilities.DEFAULT_PROJECTBEAN_TABLE_NAME);
        versionbeanTableName = configurationService.getProperty("versionbean_table_name",
                SystemConfigurationUtilities.DEFAULT_VERSIONBEAN_TABLE_NAME);
        versionbeanRelTableName = configurationService.getProperty("versionbean_rel_table_name",
                SystemConfigurationUtilities.DEFAULT_VERSIONBEAN_REL_TABLE_NAME);
    }

    public List<Issue> searchIssues(IssueSearchQuery queryModel, User user, Map<Integer, Set<PermissionType>> permissions)
            throws IssueSearchException {
        
        // TODO : this should not be done this way... Why are we querying in pure JDBC
        // legacy from EJB, because of finders ?
        Connection conn = issueDAO.getConnection();

        if (queryModel == null) {
            throw new IssueSearchException("Null search query received.", IssueSearchException.ERROR_NULL_QUERY);
        }

        try {
            String queryString = "";
            if (queryModel.getProjects() != null && queryModel.getProjects().size() > 0) {
                List<Integer> values = queryModel.getProjects();
                queryString += " AND project_id IN ( ";
                for (int i = 0; i < values.size(); i++) {
                    queryString += (i == 0 ? "" : ", ") + values.get(i).toString();
                }
                queryString += " )";
            }

            if (queryModel.getSeverities() != null && queryModel.getSeverities().size() > 0) {
                List<Integer> values = queryModel.getSeverities();
                queryString += " AND severity IN ( ";
                for (int i = 0; i < values.size(); i++) {
                    queryString += (i == 0 ? "" : ", ") + values.get(i).toString();
                }
                queryString += " )";
            }

            if (queryModel.getStatuses() != null && queryModel.getStatuses().size() > 0) {
                List<Integer> values = queryModel.getStatuses();
                queryString += " AND i.status IN ( ";
                for (int i = 0; i < values.size(); i++) {
                    queryString += (i == 0 ? "" : ", ") + values.get(i).toString();
                }
                queryString += " )";
            }

            boolean hasComponents = false;
            if (queryModel.getComponents() != null && queryModel.getComponents().size() > 0) {
                hasComponents = true;
                List<Integer> values = queryModel.getComponents();
                queryString += " AND c.component_id IN ( ";
                for (int i = 0; i < values.size(); i++) {
                    queryString += (i == 0 ? "" : ", ") + values.get(i).toString();
                }
                queryString += " )";
            }

            boolean hasVersions = false;
            if (queryModel.getVersions() != null && queryModel.getVersions().size() > 0) {
                hasVersions = true;
                List<Integer> values = queryModel.getVersions();
                queryString += " AND v.version_id IN ( ";
                for (int i = 0; i < values.size(); i++) {
                    queryString += (i == 0 ? "" : ", ") + values.get(i).toString();
                }
                queryString += " )";
            }

            if (queryModel.getTargetVersion() != null && queryModel.getTargetVersion().intValue() > 0) {
                queryString += " AND i.target_version_id = " + queryModel.getTargetVersion();
            }

            if (queryModel.getOwner() != null && queryModel.getOwner().intValue() > 0) {
                queryString += " AND i.owner_id = " + queryModel.getOwner();
            }

            if (queryModel.getCreator() != null && queryModel.getCreator().intValue() > 0) {
                queryString += " AND i.creator_id = " + queryModel.getCreator();
            }

            if (queryModel.getContributor() != null && queryModel.getContributor().intValue() > 0) {
                queryString += " AND (ih.user_id = " + queryModel.getContributor() + " OR i.creator_id = "
                        + queryModel.getCreator() + " OR i.owner_id = " + queryModel.getOwner() + ")";
            }

            if (queryModel.getText() != null && !queryModel.getText().equals("")) {
                queryString += " AND (i.description like '%" + HTMLUtilities.removeQuotes(queryModel.getText())
                        + "%' OR " + "ih.description like '%" + HTMLUtilities.removeQuotes(queryModel.getText())
                        + "%')";
            }

            if (queryModel.getResolution() != null && !queryModel.getResolution().equals("")) {
                queryString += " AND i.resolution like '%" + HTMLUtilities.removeQuotes(queryModel.getResolution())
                        + "%'";
            }

            queryString = "select distinct i.id as id from " + issuebeanTableName + " i, "
                    + (hasComponents ? componentbeanRelTableName + " c, " : "")
                    + (hasVersions ? versionbeanRelTableName + " v, " : "") + issuehistorybeanTableName + " ih "
                    + "where ih.issue_id = i.id" + (hasComponents ? " AND i.id = c.issue_id" : "")
                    + (hasVersions ? " AND i.id = v.issue_id" : "") + queryString;
            if (logger.isDebugEnabled()) {
                logger.debug("Searching for issues using the following query: " + queryString);
            }

            PreparedStatement pstmt = conn.prepareStatement(queryString);
            ResultSet rs = pstmt.executeQuery();

            List<Integer> issuesFound = new ArrayList<Integer>();
            while (rs.next()) {
                issuesFound.add(new Integer(rs.getInt("id")));
            }
            rs.close();
            pstmt.close();
            /*if (conn != null) {
                conn.close();
            }*/

            List<Issue> issuesAvailable = new ArrayList<Issue>();
            for (int i = 0; i < issuesFound.size(); i++) {
                Issue issue = issueService.getIssue((Integer) issuesFound.get(i));

                if (IssueUtilities.canViewIssue(issue, user, permissions)) {
                    issuesAvailable.add(issue);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Adding viewable issue " + issue.getId() + " to user results.");
                    }
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Skipping nonviewable issue " + issue.getId());
                }
            }

            Issue[] issues = new Issue[issuesAvailable.size()];
            issues = issuesAvailable.toArray( issues );

            return Arrays.asList(issues);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            if (logger.isDebugEnabled()) {
                logger.debug("SQLException while searching for issue ids. " + sqle.getMessage());
            }
            throw new IssueSearchException("SQL Exception caught while performing search.",
                    IssueSearchException.ERROR_SQL_EXCEPTION);
        } /**finally {
            
             * do not close as we get it from the session
             try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
            }
        }*/
    }

    public IssueService getIssueService() {
        return issueService;
    }

    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }
    
    public IssueDAO getIssueDAO() {
        return issueDAO;
    }

    public void setIssueDAO(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }

	public static String getComponentbeanRelTableName() {
		return componentbeanRelTableName;
	}

	public static void setComponentbeanRelTableName(String componentbeanRelTableName) {
		IssueSearchServiceImpl.componentbeanRelTableName = componentbeanRelTableName;
	}

	public static String getComponentbeanTableName() {
		return componentbeanTableName;
	}

	public static void setComponentbeanTableName(String componentbeanTableName) {
		IssueSearchServiceImpl.componentbeanTableName = componentbeanTableName;
	}

	public static String getIssuebeanTableName() {
		return issuebeanTableName;
	}

	public static void setIssuebeanTableName(String issuebeanTableName) {
		IssueSearchServiceImpl.issuebeanTableName = issuebeanTableName;
	}

	public static String getIssuehistorybeanTableName() {
		return issuehistorybeanTableName;
	}

	public static void setIssuehistorybeanTableName(String issuehistorybeanTableName) {
		IssueSearchServiceImpl.issuehistorybeanTableName = issuehistorybeanTableName;
	}

	public static String getProjectbeanTableName() {
		return projectbeanTableName;
	}

	public static void setProjectbeanTableName(String projectbeanTableName) {
		IssueSearchServiceImpl.projectbeanTableName = projectbeanTableName;
	}

	public static String getVersionbeanRelTableName() {
		return versionbeanRelTableName;
	}

	public static void setVersionbeanRelTableName(String versionbeanRelTableName) {
		IssueSearchServiceImpl.versionbeanRelTableName = versionbeanRelTableName;
	}

	public static String getVersionbeanTableName() {
		return versionbeanTableName;
	}

	public static void setVersionbeanTableName(String versionbeanTableName) {
		IssueSearchServiceImpl.versionbeanTableName = versionbeanTableName;
	}
}
