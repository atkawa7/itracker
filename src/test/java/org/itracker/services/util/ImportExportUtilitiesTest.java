/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.services.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.AbstractEntity;
import org.itracker.model.Component;
import org.itracker.model.CustomField;
import org.itracker.model.CustomField.Type;
import org.itracker.model.Issue;
import org.itracker.model.IssueAttachment;
import org.itracker.model.IssueHistory;
import org.itracker.model.Project;
import org.itracker.model.SystemConfiguration;
import org.itracker.model.User;
import org.itracker.model.Version;
import org.itracker.services.exceptions.ImportExportException;
import org.junit.Test;

/**
 *
 * @author seas
 */
public class ImportExportUtilitiesTest extends AbstractDependencyInjectionTest {

    private String flatXml(final String xml) {
        return xml.replace("\n", "").replace("\r", "").replaceAll("> +<", "><").trim();
    }

    public void doTestImportIssues(final String xml,
            final AbstractEntity[] expected) {
        try {
            final AbstractEntity[] actual = ImportExportUtilities.importIssues(xml);
            final List<AbstractEntity> actualList = new Vector<AbstractEntity>(Arrays.asList(actual));
            final List<AbstractEntity> expectedList = new Vector<AbstractEntity>(Arrays.asList(expected));
            assertEquals(expected.length, actual.length);
            for (final AbstractEntity aeExpected : expectedList) {
                boolean found = false;
                for (final AbstractEntity aeActual : actualList) {
                    if (aeExpected instanceof Issue && aeActual instanceof Issue) {
                        final Issue issueExpected = (Issue) aeExpected;
                        final Issue issueActual = (Issue) aeActual;
                        if (issueExpected.getId().equals(issueActual.getId())) {
                            found = true;
                        }
                    } else if (aeExpected instanceof Project && aeActual instanceof Project) {
                        final Project projectExpected = (Project) aeExpected;
                        final Project projectActual = (Project) aeActual;
                        if (projectExpected.getId().equals(projectActual.getId())) {
                            found = true;
                        }
                    } else if (aeExpected instanceof User && aeActual instanceof User) {
                        final User userExpected = (User) aeExpected;
                        final User userActual = (User) aeActual;
                        if (userExpected.getId().equals(userActual.getId())) {
                            found = true;
                        }
                    } else if (aeExpected instanceof SystemConfiguration && aeActual instanceof SystemConfiguration) {
                        final SystemConfiguration configExpected =
                                (SystemConfiguration) aeExpected;
                        final SystemConfiguration configActual =
                                (SystemConfiguration) aeActual;
                        found = true;
                    }
                    if (found) {
                        actualList.remove(aeActual);
                        break;
                    }
                }
                assertTrue(found);
            }
        } catch (final ImportExportException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testImportIssue() {
        final List<Issue> issues = new Vector<Issue>();
        final Project project = new Project("project");
        project.setId(1);
        final User creator = new User();
        creator.setId(1);
        final User owner = new User();
        owner.setId(2);
        final Date dateCreate = new Date();
        final Date dateModify = new Date();
        final Issue issue1 = new Issue();
        issue1.setId(1);
        issue1.setProject(project);
        issue1.setDescription("issue description");
        issue1.setCreator(creator);
        issue1.setOwner(owner);
        issue1.setCreateDate(dateCreate);
        issue1.setLastModifiedDate(dateModify);
        issues.add(issue1);

        final Issue issue2 = new Issue();
        issue2.setId(2);
        issue2.setProject(project);
        issue2.setDescription("issue description");
        issue2.setCreator(creator);
        issue2.setOwner(owner);
        issue2.setCreateDate(dateCreate);
        issue2.setLastModifiedDate(dateModify);
        issues.add(issue2);

        final SystemConfiguration systemConfiguration =
                new SystemConfiguration();
        final String xml = "<itracker>" +
                "<configuration>" +
                "<configuration-version><![CDATA[]]></configuration-version>" +
                "<custom-fields>" +
                "</custom-fields>" +
                "<resolutions>" +
                "</resolutions>" +
                "<severities>" +
                "</severities>" +
                "<statuses>" +
                "</statuses>" +
                "</configuration>" +
                "<users>" +
                "<user id=\"user1\" systemid=\"1\">" +
                "<login><![CDATA[]]></login>" +
                "<first-name><![CDATA[]]></first-name>" +
                "<last-name><![CDATA[]]></last-name>" +
                "<email><![CDATA[]]></email>" +
                "<user-status>MISSING KEY: itracker.user.status.0</user-status>" +
                "<super-user>false</super-user>" +
                "</user>" +
                "<user id=\"user2\" systemid=\"2\">" +
                "<login><![CDATA[]]></login>" +
                "<first-name><![CDATA[]]></first-name>" +
                "<last-name><![CDATA[]]></last-name>" +
                "<email><![CDATA[]]></email>" +
                "<user-status>MISSING KEY: itracker.user.status.0</user-status>" +
                "<super-user>false</super-user>" +
                "</user>" +
                "</users>" +
                "<projects>" +
                "<project id=\"project1\" systemid=\"1\">" +
                "<project-name><![CDATA[project]]></project-name>" +
                "<project-description><![CDATA[]]></project-description>" +
                "<project-status>" + ProjectUtilities.getStatusName(project.getStatus(), ImportExportUtilities.EXPORT_LOCALE) + "</project-status>" +
                "<project-options>0</project-options>" +
                "</project>" +
                "</projects>" +
                "<issues>" +
                "<issue id=\"issue1\" systemid=\"1\">" +
                "<issue-project><![CDATA[project1]]></issue-project>" +
                "<issue-description><![CDATA[issue description]]></issue-description>" +
                "<issue-severity>3</issue-severity>" +
                "<issue-status>100</issue-status>" +
                "<issue-resolution><![CDATA[]]></issue-resolution>" +
                "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                "<creator>user1</creator>" +
                "<owner>user2</owner>" +
                "</issue>" +
                "<issue id=\"issue2\" systemid=\"2\">" +
                " <issue-project><![CDATA[project1]]></issue-project>" +
                "<issue-description><![CDATA[issue description]]></issue-description>" +
                "<issue-severity>3</issue-severity>" +
                "<issue-status>100</issue-status>" +
                "<issue-resolution><![CDATA[]]></issue-resolution>" +
                "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                "<creator>user1</creator>" +
                "<owner>user2</owner>" +
                "</issue>" +
                "</issues>" +
                "</itracker>";
        doTestImportIssues(xml,
                new AbstractEntity[]{
                    systemConfiguration,
                    creator,
                    owner,
                    project,
                    issue1,
                    issue2
                });
    }

    @Test
    public void testExportIssues() {
        final List<Issue> issues = new Vector<Issue>();
        final Project project = new Project("project");
        project.setId(1);
        final User creator = new User();
        creator.setId(1);
        final User owner = new User();
        owner.setId(2);
        final Date dateCreate = new Date();
        final Date dateModify = new Date();
        {
            final Issue issue = new Issue();
            issue.setId(1);
            issue.setProject(project);
            issue.setDescription("issue description");
            issue.setCreator(creator);
            issue.setOwner(owner);
            issue.setCreateDate(dateCreate);
            issue.setLastModifiedDate(dateModify);
            issues.add(issue);
        }
        {
            final Issue issue = new Issue();
            issue.setId(2);
            issue.setProject(project);
            issue.setDescription("issue description");
            issue.setCreator(creator);
            issue.setOwner(owner);
            issue.setCreateDate(dateCreate);
            issue.setLastModifiedDate(dateModify);
            issues.add(issue);
        }
        final SystemConfiguration systemConfiguration =
                new SystemConfiguration();
        try {
            final String expected = "<itracker>" +
                    "<configuration>" +
                    "<configuration-version><![CDATA[]]></configuration-version>" +
                    "<custom-fields>" +
                    "</custom-fields>" +
                    "<resolutions>" +
                    "</resolutions>" +
                    "<severities>" +
                    "</severities>" +
                    "<statuses>" +
                    "</statuses>" +
                    "</configuration>" +
                    "<users>" +
                    "<user id=\"user1\" systemid=\"1\">" +
                    "<login><![CDATA[]]></login>" +
                    "<first-name><![CDATA[]]></first-name>" +
                    "<last-name><![CDATA[]]></last-name>" +
                    "<email><![CDATA[]]></email>" +
                    "<user-status>MISSING KEY: itracker.user.status.0</user-status>" +
                    "<super-user>false</super-user>" +
                    "</user>" +
                    "<user id=\"user2\" systemid=\"2\">" +
                    "<login><![CDATA[]]></login>" +
                    "<first-name><![CDATA[]]></first-name>" +
                    "<last-name><![CDATA[]]></last-name>" +
                    "<email><![CDATA[]]></email>" +
                    "<user-status>MISSING KEY: itracker.user.status.0</user-status>" +
                    "<super-user>false</super-user>" +
                    "</user>" +
                    "</users>" +
                    "<projects>" +
                    "<project id=\"project1\" systemid=\"1\">" +
                    "<project-name><![CDATA[project]]></project-name>" +
                    "<project-description><![CDATA[]]></project-description>" +
                    "<project-status>" + ProjectUtilities.getStatusName(project.getStatus(), ImportExportUtilities.EXPORT_LOCALE) + "</project-status>" +
                    "<project-options>0</project-options>" +
                    "</project>" +
                    "</projects>" +
                    "<issues>" +
                    "<issue id=\"issue1\" systemid=\"1\">" +
                    "<issue-project><![CDATA[project1]]></issue-project>" +
                    "<issue-description><![CDATA[issue description]]></issue-description>" +
                    "<issue-severity>null</issue-severity>" +
                    "<issue-status>null</issue-status>" +
                    "<issue-resolution><![CDATA[]]></issue-resolution>" +
                    "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                    "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                    "<creator>user1</creator>" +
                    "<owner>user2</owner>" +
                    "</issue>" +
                    "<issue id=\"issue2\" systemid=\"2\">" +
                    " <issue-project><![CDATA[project1]]></issue-project>" +
                    "<issue-description><![CDATA[issue description]]></issue-description>" +
                    "<issue-severity>null</issue-severity>" +
                    "<issue-status>null</issue-status>" +
                    "<issue-resolution><![CDATA[]]></issue-resolution>" +
                    "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                    "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                    "<creator>user1</creator>" +
                    "<owner>user2</owner>" +
                    "</issue>" +
                    "</issues>" +
                    "</itracker>";
            assertEquals(flatXml(expected),
                    flatXml(ImportExportUtilities.exportIssues(issues,
                    systemConfiguration)));
        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }
    }

    @Test
    public void testExportModel() {
        try {
            final User creator = new User();
            creator.setId(1);
            final User owner = new User();
            owner.setId(2);
            final Project project = new Project("project");
            project.setId(1);
            project.getOwners().add(creator);
            project.getOwners().add(owner);
            final Component component = new Component(project, "component");
            project.getComponents().add(component);
            final Issue issue = new Issue();
            issue.setProject(project);
            issue.setId(1);
            issue.setDescription("issue description");
            issue.setSeverity(1);
            issue.setResolution("fixed");
            issue.setTargetVersion(new Version(project, "1.1.1"));
            final Date dateCreate = new Date();
            issue.setCreateDate(dateCreate);
            final Date dateModify = new Date();
            issue.setLastModifiedDate(dateModify);
            issue.setCreator(creator);
            issue.setOwner(owner);
            final IssueHistory issueHistory = new IssueHistory(issue, creator, "some description", IssueUtilities.STATUS_NEW);
            issueHistory.setCreateDate(dateCreate);
            issueHistory.setLastModifiedDate(dateModify);
            issue.getHistory().add(issueHistory);
            issue.getComponents().add(component);
            final IssueAttachment attachment = new IssueAttachment(issue, "file.txt");
            attachment.setUser(creator);
            issue.getAttachments().add(attachment);
            issue.getVersions().add(new Version(project, "1.1.1"));
            issue.getVersions().add(new Version(project, "1.1.2"));
            final String expected = "<issue id=\"issue1\" systemid=\"1\">" +
                    "<issue-project><![CDATA[project1]]></issue-project>" +
                    "<issue-description><![CDATA[issue description]]></issue-description>" +
                    "<issue-severity>1</issue-severity>" +
                    "<issue-status>null</issue-status>" +
                    "<issue-resolution><![CDATA[fixed]]></issue-resolution>" +
                    "<target-version-id>versionnull</target-version-id>" +
                    "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                    "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                    "<creator>user1</creator>" +
                    "<owner>user2</owner>" +
                    "<issue-components>" +
                    "<component-id>componentnull</component-id>" +
                    "</issue-components>" +
                    "<issue-versions>" +
                    "<version-id>versionnull</version-id>" +
                    "<version-id>versionnull</version-id>" +
                    "</issue-versions>" +
                    "<issue-attachments>" +
                    "<issue-attachment>      <issue-attachment-description><![CDATA[]]></issue-attachment-description>" +
                    "<issue-attachment-filename><![CDATA[]]></issue-attachment-filename>" +
                    "<issue-attachment-origfile><![CDATA[file.txt]]></issue-attachment-origfile>" +
                    "<issue-attachment-size><![CDATA[0]]></issue-attachment-size>" +
                    "<issue-attachment-type><![CDATA[null]]></issue-attachment-type>" +
                    "<issue-attachment-creator><![CDATA[user1]]></issue-attachment-creator>" +
                    "</issue-attachment>" +
                    "</issue-attachments>" +
                    "<issue-history>" +
                    "<history-entry creator-id=\"user1\" date=\"" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "\" status=\"100\"><![CDATA[some description]]></history-entry>" +
                    "</issue-history>" +
                    "</issue>";
            assertEquals(flatXml(expected),
                    flatXml(ImportExportUtilities.exportModel(issue)));
        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }

        try {
            final Project project = new Project("project");
            project.setId(1);
            final Component component = new Component(project, "component");
            component.setId(1);
            project.getComponents().add(component);
            final String expected = "<project id=\"project1\" systemid=\"1\">" +
                    "<project-name><![CDATA[project]]></project-name>" +
                    "<project-description><![CDATA[]]></project-description>" +
                    "<project-status>" + ProjectUtilities.getStatusName(project.getStatus(), ImportExportUtilities.EXPORT_LOCALE) + "</project-status>" +
                    "<project-options>0</project-options>" +
                    "<components>" +
                    "<component id=\"component1\" systemid=\"1\">" +
                    "<component-name><![CDATA[component]]></component-name>" +
                    "<component-description><![CDATA[]]></component-description>" +
                    "</component>" +
                    "</components>" +
                    "</project>";
            assertEquals(flatXml(expected),
                    flatXml(ImportExportUtilities.exportModel(project)));
        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }

        try {
            final User user = new User();
            user.setId(1);
            user.setFirstName("firstName");
            user.setLastName("lastName");
            final String expected = "<user id=\"user1\" systemid=\"1\">" +
                    "<login><![CDATA[]]></login>" +
                    "<first-name><![CDATA[firstName]]></first-name>" +
                    "<last-name><![CDATA[lastName]]></last-name>" +
                    "<email><![CDATA[]]></email>" +
                    "<user-status>MISSING KEY: itracker.user.status.0</user-status>" +
                    "<super-user>false</super-user>" +
                    "</user>";
            assertEquals(flatXml(expected),
                    flatXml(ImportExportUtilities.exportModel(user)));
        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }
    }

    @Test
    public void testGetConfigurationXML() {
        final SystemConfiguration config = new SystemConfiguration();
        config.setId(1);
        config.setVersion("1/1");
        final CustomField customField1 = new CustomField("field1", Type.STRING);
        config.getCustomFields().add(customField1);
        final String expected =
                "<configuration-version><![CDATA[1/1]]></configuration-version>" +
                "<custom-fields>" +
                "<custom-field id=\"custom-fieldnull\" systemid=\"null\">" +
                "<custom-field-label><![CDATA[field1]]></custom-field-label>" +
                "<custom-field-type><![CDATA[STRING]]></custom-field-type>" +
                "<custom-field-required><![CDATA[false]]></custom-field-required>" +
                "<custom-field-dateformat><![CDATA[]]></custom-field-dateformat>" +
                "<custom-field-sortoptions><![CDATA[false]]></custom-field-sortoptions>" +
                "</custom-field>" +
                "</custom-fields>" +
                "<resolutions>" +
                "</resolutions>" +
                "<severities>" +
                "</severities>" +
                "<statuses>" +
                "</statuses>";
        assertEquals(flatXml(expected),
                flatXml(ImportExportUtilities.getConfigurationXML(config)));
    }

    /**
     * Defines a set of datafiles to be uploaded into database.
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                    "dataset/languagebean_dataset.xml"
                };
    }

    /**
     * Defines a simple configuration, required for running tests.
     * @return an array of references to configuration files.
     */
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }
}