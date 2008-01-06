package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.junit.Test;

public class IssueDAOImplTest extends AbstractDependencyInjectionTest {

    private IssueDAO issueDAO;

    @Test
    public void testCountByProjectAndLowerStatus() {

        int issueCount = issueDAO.countByProjectAndLowerStatus(2, 3);

        assertEquals(2, issueCount);
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}
