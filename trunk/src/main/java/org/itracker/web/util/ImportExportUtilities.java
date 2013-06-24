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

package org.itracker.web.util;

import org.apache.log4j.Logger;
import org.itracker.ImportExportException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * FIXME: This is not XML, this is string concatenating/parsing. Use proper SAX Handler or remove this unsave code. see java.xml.parsers for more information.
 * <p/>
 * This class provides functionality needed to import and export issues and their associated
 * data as XML.  This xml provides all the data necessary to import the issues into another
 * instance of ITracker or some other issue tracking tool.
 */
public class ImportExportUtilities implements ImportExportTags {

    private static final Logger logger = Logger.getLogger(ImportExportUtilities.class);
    public static final int IMPORT_STAT_NEW = 0;
    public static final int IMPORT_STAT_REUSED = 1;

    public static final int IMPORT_STAT_USERS = 0;
    public static final int IMPORT_STAT_PROJECTS = 1;
    public static final int IMPORT_STAT_ISSUES = 2;
    public static final int IMPORT_STAT_STATUSES = 3;
    public static final int IMPORT_STAT_SEVERITIES = 4;
    public static final int IMPORT_STAT_RESOLUTIONS = 5;
    public static final int IMPORT_STAT_FIELDS = 6;

    public ImportExportUtilities() {
    }


    /**
     * Takes an XML file matching the ITracker import/export DTD and returns an array
     * of AbstractBean objects.  The array will contain all of the projects, components
     * versions, users, custom fields, and issues contained in the XML.
     *
     * @param xmlReader an xml reader to import
     * @throws ImportExportException thrown if the xml can not be parsed into the appropriate objects
     */
    public static AbstractEntity[] importIssues(Reader xmlReader) throws ImportExportException {
        AbstractEntity[] abstractBeans;

        try {
            logger.debug("Starting XML data import.");

            XMLReader reader = XMLReaderFactory.createXMLReader();
            ImportHandler handler = new ImportHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(new InputSource(xmlReader));
            abstractBeans = handler.getModels();

            logger.debug("Imported a total of " + abstractBeans.length + " beans.");
        } catch (Exception e) {
            logger.error("Exception.", e);
            throw new ImportExportException(e.getMessage());
        }

        return abstractBeans;
    }


    /**
     * export the issues to an XML and write it to the response.
     * @param issues
     * @param config
     * @param request
     * @param response
     * @return  if <code>true</code> the export was sucessful.
     * @throws ServletException
     * @throws IOException
     */
    public static boolean exportIssues(List<Issue> issues, SystemConfiguration config, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"issue_export.xml\"");
        PrintWriter out = response.getWriter();

        try {
            // TODO instead to have a string returned, it should directly serialize the
            // export to the response-writer.
            String xml = ImportExportUtilities.exportIssues(issues, config);

            TransformerFactory.newInstance().newTransformer().transform(
                    new StreamSource(new StringReader(xml)),
                    new StreamResult(out));

            out.flush();
        } catch (ImportExportException iee) {
            logger.error("Error exporting issue data. Message: " + iee.getMessage(), iee);
            return false;
        } catch (TransformerConfigurationException tce) {
            logger.error("Error exporting issue data. Message: " + tce.getMessage(), tce);
            return false;
        } catch (TransformerException te) {
            logger.error("Error exporting issue data. Message: " + te.getMessage(), te);
            return false;
        }
        out.flush();
        out.close();
        return true;
    }

    public static AbstractEntity importXml(InputSource is) throws Exception {
        // TODO unmarshal from is
        JAXBContext jc = JAXBContext.newInstance("org.itracker");
        Unmarshaller u = jc.createUnmarshaller();
        AbstractEntity o = (AbstractEntity) u.unmarshal(is);
        return o;
    }

    public static void export(AbstractEntity o, OutputStream os) throws Exception {
        // TODO marshal to System.out
        JAXBContext jc = JAXBContext.newInstance("org.itracker");
        Marshaller m = jc.createMarshaller();
        m.marshal(o, System.out);
    }

    /**
     * Takes an array of IssueModels and exports them as XML suitable for import into another
     * instance of ITracker, or another issue tracking tool.
     *
     * @param issues an array of Issue objects to export
     * @throws ImportExportException thrown if the array of issues can not be exported
     */
    public static String exportIssues(List<Issue> issues, SystemConfiguration config) throws ImportExportException {
        StringBuffer buf = new StringBuffer();
        HashMap<String, Project> projects = new HashMap<String, Project>();
        HashMap<String, User> users = new HashMap<String, User>();

        if (issues == null || issues.size() == 0) {
            throw new ImportExportException("The issue list was null or zero length.");
        }
        buf.append("<" + TAG_ISSUES + ">\n");
        for (int i = 0; i < issues.size(); i++) {
            if (!projects.containsKey(issues.get(i).getProject().getId().toString())) {
                logger.debug("Adding new project " + issues.get(i).getProject().getId() + " to export.");
                projects.put(issues.get(i).getProject().getId().toString(), issues.get(i).getProject());
            }

            if (issues.get(i).getCreator() != null && !users.containsKey(issues.get(i).getCreator().getId().toString())) {
                logger.debug("Adding new user " + issues.get(i).getCreator().getId() + " to export.");
                users.put(issues.get(i).getCreator().getId().toString(), issues.get(i).getCreator());
            }
            if (issues.get(i).getOwner() != null && !users.containsKey(issues.get(i).getOwner().getId().toString())) {
                logger.debug("Adding new user " + issues.get(i).getOwner().getId() + " to export.");
                users.put(issues.get(i).getOwner().getId().toString(), issues.get(i).getOwner());
            }

            List<IssueHistory> history = issues.get(i).getHistory();
            for (int j = 0; j < history.size(); j++) {
                if (history.get(j) != null && history.get(j).getUser() != null && !users.containsKey(history.get(j).getUser().getId().toString())) {
                    logger.debug("Adding new user " + history.get(j).getUser().getId() + " to export.");
                    users.put(history.get(j).getUser().getId().toString(), history.get(j).getUser());
                }
            }

            List<IssueAttachment> attachments = issues.get(i).getAttachments();
            for (int j = 0; j < attachments.size(); j++) {
                if (attachments.get(j) != null && attachments.get(j).getUser() != null && !users.containsKey(attachments.get(j).getUser().getId().toString())) {
                    logger.debug("Adding new user " + attachments.get(j).getUser().getId() + " to export.");
                    users.put(attachments.get(j).getUser().getId().toString(), attachments.get(j).getUser());
                }
            }

            buf.append(exportModel((AbstractEntity) issues.get(i)));
        }
        buf.append("</" + TAG_ISSUES + ">\n");
        buf.append("</" + TAG_ROOT + ">\n");


        buf.insert(0, "</" + TAG_PROJECTS + ">\n");
        for (Iterator<String> iter = projects.keySet().iterator(); iter.hasNext(); ) {
            Project project = (Project) projects.get((String) iter.next());
            for (int i = 0; i < project.getOwners().size(); i++) {
                users.put(project.getOwners().get(i).getId().toString(), project.getOwners().get(i));
            }
            buf.insert(0, exportModel((AbstractEntity) project));
        }
        buf.insert(0, "<" + TAG_PROJECTS + ">\n");

        buf.insert(0, "</" + TAG_USERS + ">\n");
        for (Iterator<String> iter = users.keySet().iterator(); iter.hasNext(); ) {
            buf.insert(0, exportModel((AbstractEntity) users.get((String) iter.next())));
        }
        buf.insert(0, "<" + TAG_USERS + ">\n");

        if (config != null) {
            buf.insert(0, "</" + TAG_CONFIGURATION + ">\n");
            buf.insert(0, getConfigurationXML(config));
            buf.insert(0, "<" + TAG_CONFIGURATION + ">\n");
        }

        buf.insert(0, "<" + TAG_ROOT + ">\n");

        return buf.toString();
    }

    /**
     * Returns the appropriate XML block for a given model.
     *
     * @param abstractBean a model that extends AbstractEntity
     * @throws ImportExportException thrown if the given model can not be exported
     */
    public static String exportModel(AbstractEntity abstractBean) throws ImportExportException {
        if (abstractBean == null) {
            throw new ImportExportException("The bean to export was null.");
        } else if (abstractBean instanceof Issue) {
            return getIssueXML((Issue) abstractBean);
        } else if (abstractBean instanceof Project) {
            return getProjectXML((Project) abstractBean);
        } else if (abstractBean instanceof User) {
            return getUserXML((User) abstractBean);
        } else {
            throw new ImportExportException("This bean type can not be exported.");
        }
    }

    /**
     * Generates an XML block that encapsulates an issue for import or export.  This
     * function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param issue an Issue to generate the XML for
     * @return a String containing the XML for the issue
     */
    public static String getIssueXML(Issue issue) {
        if (issue == null) {
            return "";
        }

        StringBuffer buf = new StringBuffer();

        buf.append("<" + TAG_ISSUE + " " + ATTR_ID + "=\"" + TAG_ISSUE + issue.getId() + "\" " + ATTR_SYSTEMID + "=\"" + issue.getId() + "\">\n");
        buf.append("  <" + TAG_ISSUE_PROJECT + "><![CDATA[" + TAG_PROJECT + issue.getProject().getId() + "]]></" + TAG_ISSUE_PROJECT + ">\n");
        buf.append("  <" + TAG_ISSUE_DESCRIPTION + "><![CDATA[" + ITrackerResources.escapeUnicodeString(issue.getDescription(), false) + "]]></" + TAG_ISSUE_DESCRIPTION + ">\n");
        buf.append("  <" + TAG_ISSUE_SEVERITY + ">" + issue.getSeverity() + "</" + TAG_ISSUE_SEVERITY + ">\n");
        buf.append("  <" + TAG_ISSUE_STATUS + ">" + issue.getStatus() + "</" + TAG_ISSUE_STATUS + ">\n");
        buf.append("  <" + TAG_ISSUE_RESOLUTION + "><![CDATA[" + ITrackerResources.escapeUnicodeString(issue.getResolution(), false) + "]]></" + TAG_ISSUE_RESOLUTION + ">\n");
        if (issue.getTargetVersion() != null) {
            buf.append("  <" + TAG_TARGET_VERSION_ID + ">" + TAG_VERSION + issue.getTargetVersion().getId() + "</" + TAG_TARGET_VERSION_ID + ">\n");
        }
        buf.append("  <" + TAG_CREATE_DATE + ">" + DATE_FORMATTER.format(issue.getCreateDate()) + "</" + TAG_CREATE_DATE + ">\n");
        buf.append("  <" + TAG_LAST_MODIFIED + ">" + DATE_FORMATTER.format(issue.getLastModifiedDate()) + "</" + TAG_LAST_MODIFIED + ">\n");
        buf.append("  <" + TAG_CREATOR + ">" + TAG_USER + issue.getCreator().getId() + "</" + TAG_CREATOR + ">\n");
        if (issue.getOwner() != null) {
            buf.append("  <" + TAG_OWNER + ">" + TAG_USER + issue.getOwner().getId() + "</" + TAG_OWNER + ">\n");
        }
        if (issue.getComponents().size() > 0) {
            buf.append("  <" + TAG_ISSUE_COMPONENTS + ">\n");
            for (int i = 0; i < issue.getComponents().size(); i++) {
                if (issue.getComponents().get(i) != null) {
                    buf.append("    <" + TAG_COMPONENT_ID + ">" + TAG_COMPONENT + issue.getComponents().get(i).getId() + "</" + TAG_COMPONENT_ID + ">\n");
                }
            }
            buf.append("  </" + TAG_ISSUE_COMPONENTS + ">\n");
        }
        if (issue.getVersions().size() > 0) {
            buf.append("  <" + TAG_ISSUE_VERSIONS + ">\n");
            for (int i = 0; i < issue.getVersions().size(); i++) {
                if (issue.getVersions().get(i) != null) {
                    buf.append("    <" + TAG_VERSION_ID + ">" + TAG_VERSION + issue.getVersions().get(i).getId() + "</" + TAG_VERSION_ID + ">\n");
                }
            }
            buf.append("  </" + TAG_ISSUE_VERSIONS + ">\n");
        }
        if (issue.getFields().size() > 0) {
            buf.append("  <" + TAG_ISSUE_FIELDS + ">\n");
            for (int i = 0; i < issue.getFields().size(); i++) {
                if (issue.getFields().get(i) != null) {
                    buf.append("    <" + TAG_ISSUE_FIELD + " " + ATTR_ID + "=\"" + TAG_CUSTOM_FIELD + issue.getFields().get(i).getCustomField().getId() + "\"><![CDATA[" + issue.getFields().get(i).getValue(EXPORT_LOCALE) + "]]></" + TAG_ISSUE_FIELD + ">\n");
                }
            }
            buf.append("  </" + TAG_ISSUE_FIELDS + ">\n");
        }
        if (issue.getAttachments().size() > 0) {
            buf.append("  <" + TAG_ISSUE_ATTACHMENTS + ">\n");
            for (int i = 0; i < issue.getAttachments().size(); i++) {
                if (issue.getAttachments().get(i) != null) {
                    buf.append("    <" + TAG_ISSUE_ATTACHMENT + ">");
                    buf.append("      <" + TAG_ISSUE_ATTACHMENT_DESCRIPTION + "><![CDATA[" + ITrackerResources.escapeUnicodeString(issue.getAttachments().get(i).getDescription(), false) + "]]></" + TAG_ISSUE_ATTACHMENT_DESCRIPTION + ">\n");
                    buf.append("      <" + TAG_ISSUE_ATTACHMENT_FILENAME + "><![CDATA[" + ITrackerResources.escapeUnicodeString(issue.getAttachments().get(i).getFileName(), false) + "]]></" + TAG_ISSUE_ATTACHMENT_FILENAME + ">\n");
                    buf.append("      <" + TAG_ISSUE_ATTACHMENT_ORIGFILE + "><![CDATA[" + ITrackerResources.escapeUnicodeString(issue.getAttachments().get(i).getOriginalFileName(), false) + "]]></" + TAG_ISSUE_ATTACHMENT_ORIGFILE + ">\n");
                    buf.append("      <" + TAG_ISSUE_ATTACHMENT_SIZE + "><![CDATA[" + issue.getAttachments().get(i).getSize() + "]]></" + TAG_ISSUE_ATTACHMENT_SIZE + ">\n");
                    buf.append("      <" + TAG_ISSUE_ATTACHMENT_TYPE + "><![CDATA[" + issue.getAttachments().get(i).getType() + "]]></" + TAG_ISSUE_ATTACHMENT_TYPE + ">\n");
                    buf.append("      <" + TAG_ISSUE_ATTACHMENT_CREATOR + "><![CDATA[" + TAG_USER + issue.getAttachments().get(i).getUser().getId() + "]]></" + TAG_ISSUE_ATTACHMENT_CREATOR + ">\n");
                    buf.append("    </" + TAG_ISSUE_ATTACHMENT + ">\n");
                }
            }
            buf.append("  </" + TAG_ISSUE_ATTACHMENTS + ">\n");
        }
        if (issue.getHistory().size() > 0) {
            buf.append("  <" + TAG_ISSUE_HISTORY + ">\n");
            for (int i = 0; i < issue.getHistory().size(); i++) {
                if (issue.getHistory().get(i) != null) {
                    buf.append("    <" + TAG_HISTORY_ENTRY + " ");
                    buf.append(ATTR_CREATOR_ID + "=\"" + TAG_USER + issue.getHistory().get(i).getUser().getId() + "\" ");
                    buf.append(ATTR_DATE + "=\"" + DATE_FORMATTER.format(issue.getHistory().get(i).getCreateDate()) + "\" ");
                    buf.append(ATTR_STATUS + "=\"" + issue.getHistory().get(i).getStatus() + "\">");
                    buf.append("<![CDATA[" + ITrackerResources.escapeUnicodeString(issue.getHistory().get(i).getDescription(), false) + "]]>");
                    buf.append("</" + TAG_HISTORY_ENTRY + ">\n");
                }
            }
            buf.append("  </" + TAG_ISSUE_HISTORY + ">\n");
        }
        buf.append("</" + TAG_ISSUE + ">\n");

        return buf.toString();
    }

    /**
     * Generates an XML block that encapsulates a project for import or export.  This
     * function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param project a Project to generate the XML for
     * @return a String containing the XML for the project
     */
    public static String getProjectXML(Project project) {
        if (project == null) {
            return "";
        }

        StringBuffer buf = new StringBuffer();

        buf.append("<" + TAG_PROJECT + " " + ATTR_ID + "=\"" + TAG_PROJECT + project.getId() + "\" " + ATTR_SYSTEMID + "=\"" + project.getId() + "\">\n");
        buf.append("  <" + TAG_PROJECT_NAME + "><![CDATA[" + ITrackerResources.escapeUnicodeString(project.getName(), false) + "]]></" + TAG_PROJECT_NAME + ">\n");
        buf.append("  <" + TAG_PROJECT_DESCRIPTION + "><![CDATA[" + ITrackerResources.escapeUnicodeString(project.getDescription(), false) + "]]></" + TAG_PROJECT_DESCRIPTION + ">\n");
        buf.append("  <" + TAG_PROJECT_STATUS + ">" + ProjectUtilities.getStatusName(project.getStatus(), EXPORT_LOCALE) + "</" + TAG_PROJECT_STATUS + ">\n");
        buf.append("  <" + TAG_PROJECT_OPTIONS + ">" + project.getOptions() + "</" + TAG_PROJECT_OPTIONS + ">\n");

        if (project.getCustomFields().size() > 0) {
            buf.append("  <" + TAG_PROJECT_FIELDS + ">\n");
            for (int i = 0; i < project.getCustomFields().size(); i++) {
                if (project.getCustomFields().get(i) != null) {
                    buf.append("    <" + TAG_PROJECT_FIELD_ID + ">" + TAG_CUSTOM_FIELD + project.getCustomFields().get(i).getId() + "</" + TAG_PROJECT_FIELD_ID + ">\n");
                }
            }
            buf.append("  </" + TAG_PROJECT_FIELDS + ">\n");
        }
        if (project.getOwners().size() > 0) {
            buf.append("  <" + TAG_PROJECT_OWNERS + ">\n");
            for (int i = 0; i < project.getOwners().size(); i++) {
                if (project.getOwners().get(i) != null) {
                    buf.append("    <" + TAG_PROJECT_OWNER_ID + ">" + TAG_USER + project.getOwners().get(i).getId() + "</" + TAG_PROJECT_OWNER_ID + ">\n");
                }
            }
            buf.append("  </" + TAG_PROJECT_OWNERS + ">\n");
        }
        if (project.getComponents().size() > 0) {
            buf.append("  <" + TAG_COMPONENTS + ">\n");
            for (int i = 0; i < project.getComponents().size(); i++) {
                if (project.getComponents().get(i) != null) {
                    buf.append("    <" + TAG_COMPONENT + " " + ATTR_ID + "=\"" + TAG_COMPONENT + project.getComponents().get(i).getId() + "\" " + ATTR_SYSTEMID + "=\"" + project.getComponents().get(i).getId() + "\">\n");
                    buf.append("      <" + TAG_COMPONENT_NAME + "><![CDATA[" + ITrackerResources.escapeUnicodeString(project.getComponents().get(i).getName(), false) + "]]></" + TAG_COMPONENT_NAME + ">\n");
                    buf.append("      <" + TAG_COMPONENT_DESCRIPTION + "><![CDATA[" + ITrackerResources.escapeUnicodeString(project.getComponents().get(i).getDescription(), false) + "]]></" + TAG_COMPONENT_DESCRIPTION + ">\n");
                    buf.append("    </" + TAG_COMPONENT + ">\n");
                }
            }
            buf.append("  </" + TAG_COMPONENTS + ">\n");
        }
        if (project.getVersions().size() > 0) {
            buf.append("  <" + TAG_VERSIONS + ">\n");
            for (int i = 0; i < project.getVersions().size(); i++) {
                if (project.getVersions().get(i) != null) {
                    buf.append("    <" + TAG_VERSION + " " + ATTR_ID + "=\"" + TAG_VERSION + project.getVersions().get(i).getId() + "\" " + ATTR_SYSTEMID + "=\"" + project.getVersions().get(i).getId() + "\">\n");
                    buf.append("      <" + TAG_VERSION_NUMBER + "><![CDATA[" + ITrackerResources.escapeUnicodeString(project.getVersions().get(i).getNumber(), false) + "]]></" + TAG_VERSION_NUMBER + ">\n");
                    buf.append("      <" + TAG_VERSION_DESCRIPTION + "><![CDATA[" + ITrackerResources.escapeUnicodeString(project.getVersions().get(i).getDescription(), false) + "]]></" + TAG_VERSION_DESCRIPTION + ">\n");
                    buf.append("    </" + TAG_VERSION + ">\n");
                }
            }
            buf.append("  </" + TAG_VERSIONS + ">\n");
        }
        buf.append("</" + TAG_PROJECT + ">\n");

        return buf.toString();
    }

    /**
     * Generates an XML block that encapsulates a user for import or export.  This
     * function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param user a User to generate the XML for
     * @return a String containing the XML for the user
     */
    public static String getUserXML(User user) {
        if (user == null) {
            return "";
        }

        StringBuffer buf = new StringBuffer();

        buf.append("<" + TAG_USER + " " + ATTR_ID + "=\"" + TAG_USER + user.getId() + "\" " + ATTR_SYSTEMID + "=\"" + user.getId() + "\">\n");
        buf.append("  <" + TAG_LOGIN + "><![CDATA[" + ITrackerResources.escapeUnicodeString(user.getLogin(), false) + "]]></" + TAG_LOGIN + ">\n");
        buf.append("  <" + TAG_FIRST_NAME + "><![CDATA[" + ITrackerResources.escapeUnicodeString(user.getFirstName(), false) + "]]></" + TAG_FIRST_NAME + ">\n");
        buf.append("  <" + TAG_LAST_NAME + "><![CDATA[" + ITrackerResources.escapeUnicodeString(user.getLastName(), false) + "]]></" + TAG_LAST_NAME + ">\n");
        buf.append("  <" + TAG_EMAIL + "><![CDATA[" + ITrackerResources.escapeUnicodeString(user.getEmail(), false) + "]]></" + TAG_EMAIL + ">\n");
        buf.append("  <" + TAG_USER_STATUS + ">" + user.getStatus() + "</" + TAG_USER_STATUS + ">\n");
        buf.append("  <" + TAG_SUPER_USER + ">" + user.isSuperUser() + "</" + TAG_SUPER_USER + ">\n");
        buf.append("</" + TAG_USER + ">\n");

        return buf.toString();
    }

    /**
     * Generates an XML block that encapsulates the system configuration for import or export.
     * This function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param config a SystemConfiguration to generate the XML for
     * @return a String containing the XML for the configuration
     */
    public static String getConfigurationXML(SystemConfiguration config) {
        if (config == null) {
            return "";
        }

        StringBuffer buf = new StringBuffer();
        buf.append("  <" + TAG_CONFIGURATION_VERSION + "><![CDATA[" + config.getVersion() + "]]></" + TAG_CONFIGURATION_VERSION + ">\n");
        buf.append("  <" + TAG_CUSTOM_FIELDS + ">\n");
        for (int i = 0; i < config.getCustomFields().size(); i++) {
            buf.append("    <" + TAG_CUSTOM_FIELD + " " + ATTR_ID + "=\"" + TAG_CUSTOM_FIELD + config.getCustomFields().get(i).getId() + "\" " + ATTR_SYSTEMID + "=\"" + config.getCustomFields().get(i).getId() + "\">\n");
            buf.append("      <" + TAG_CUSTOM_FIELD_LABEL + "><![CDATA[" + ITrackerResources.escapeUnicodeString(CustomFieldUtilities.getCustomFieldName(config.getCustomFields().get(i).getId()), false) + "]]></" + TAG_CUSTOM_FIELD_LABEL + ">\n");
            buf.append("      <" + TAG_CUSTOM_FIELD_TYPE + "><![CDATA[" + config.getCustomFields().get(i).getFieldType().name() + "]]></" + TAG_CUSTOM_FIELD_TYPE + ">\n");
            buf.append("      <" + TAG_CUSTOM_FIELD_REQUIRED + "><![CDATA[" + config.getCustomFields().get(i).isRequired() + "]]></" + TAG_CUSTOM_FIELD_REQUIRED + ">\n");
            buf.append("      <" + TAG_CUSTOM_FIELD_DATEFORMAT + "><![CDATA[" + ITrackerResources.escapeUnicodeString(config.getCustomFields().get(i).getDateFormat(), false) + "]]></" + TAG_CUSTOM_FIELD_DATEFORMAT + ">\n");
            buf.append("      <" + TAG_CUSTOM_FIELD_SORTOPTIONS + "><![CDATA[" + config.getCustomFields().get(i).isSortOptionsByName() + "]]></" + TAG_CUSTOM_FIELD_SORTOPTIONS + ">\n");
            if (config.getCustomFields().get(i).getFieldType() == CustomField.Type.LIST) {
                List<CustomFieldValue> options = config.getCustomFields().get(i).getOptions();
                for (int j = 0; j < options.size(); j++) {
                    buf.append("      <" + TAG_CUSTOM_FIELD_OPTION + " " + ATTR_VALUE + "=\"" + ITrackerResources.escapeUnicodeString(options.get(j).getValue(), false) + "\"><![CDATA[" + ITrackerResources.escapeUnicodeString(CustomFieldUtilities.getCustomFieldOptionName(options.get(j), null), false) + "]]></" + TAG_CUSTOM_FIELD_OPTION + ">\n");
                }
            }
            buf.append("    </" + TAG_CUSTOM_FIELD + ">\n");
        }
        buf.append("  </" + TAG_CUSTOM_FIELDS + ">\n");
        buf.append("  <" + TAG_RESOLUTIONS + ">\n");
        for (int i = 0; i < config.getResolutions().size(); i++) {
            buf.append("  <" + TAG_RESOLUTION + " " + ATTR_VALUE + "=\"" + config.getResolutions().get(i).getValue() + "\" " + ATTR_ORDER + "=\"" + config.getResolutions().get(i).getOrder() + "\">");
            buf.append("<![CDATA[" + ITrackerResources.escapeUnicodeString(config.getResolutions().get(i).getName(), false) + "]]>");
            buf.append("</" + TAG_RESOLUTION + ">\n");
        }
        buf.append("  </" + TAG_RESOLUTIONS + ">\n");
        buf.append("  <" + TAG_SEVERITIES + ">\n");
        for (int i = 0; i < config.getSeverities().size(); i++) {
            buf.append("  <" + TAG_SEVERITY + " " + ATTR_VALUE + "=\"" + config.getSeverities().get(i).getValue() + "\" " + ATTR_ORDER + "=\"" + config.getSeverities().get(i).getOrder() + "\">");
            buf.append("<![CDATA[" + ITrackerResources.escapeUnicodeString(config.getSeverities().get(i).getName(), false) + "]]>");
            buf.append("</" + TAG_SEVERITY + ">\n");
        }
        buf.append("  </" + TAG_SEVERITIES + ">\n");
        buf.append("  <" + TAG_STATUSES + ">\n");
        for (int i = 0; i < config.getStatuses().size(); i++) {
            buf.append("  <" + TAG_STATUS + " " + ATTR_VALUE + "=\"" + config.getStatuses().get(i).getValue() + "\" " + ATTR_ORDER + "=\"" + config.getStatuses().get(i).getOrder() + "\">");
            buf.append("<![CDATA[" + ITrackerResources.escapeUnicodeString(config.getStatuses().get(i).getName(), false) + "]]>");
            buf.append("</" + TAG_STATUS + ">\n");
        }
        buf.append("  </" + TAG_STATUSES + ">\n");

        return buf.toString();
    }
}
