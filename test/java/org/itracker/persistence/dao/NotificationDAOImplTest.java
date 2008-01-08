package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Notification;
import org.junit.Test;

import java.util.List;

public class NotificationDAOImplTest extends AbstractDependencyInjectionTest {

    private NotificationDAO notificationDAO;

    @Test
    public void findById() {

        Notification notification = notificationDAO.findById(1);

        assertNotNull(notification);

        assertEquals(1, notification.getIssue().getId().intValue());
        assertEquals(2, notification.getUser().getId().intValue());
        assertEquals(1, notification.getNotificationRole());

    }

    @Test
    public void findByIssueId() {

        List<Notification> notifications = notificationDAO.findByIssueId(1);

        assertNotNull(notifications);
        assertEquals(1, notifications.size());

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        notificationDAO = (NotificationDAO) applicationContext.getBean("notificationDAO");

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/notificationbean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }

}