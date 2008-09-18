package org.itracker.web.scheduler.tasks;

import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.apache.log4j.Logger;

/**
 * @author rui (rui.silva@emation.pt)
 *  
 */
public class MailNotification extends BaseJob {

    private final Logger logger;
    private String projectId;
    private String mailHost;
    private String user;
    private String password;
    private String folderName;
    private String protocol;

    public MailNotification() {
        this.logger = Logger.getLogger(getClass());
    }
    
    /**
     *  
     */
    @SuppressWarnings("unused")
	private String getProjectId() {
        return projectId;
    }

    private void setProjectId(String id) {
        projectId= id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.itracker.web.scheduler.SchedulableTask#performTask(java.lang.String[])
     */
    public void performTask(String[] args) {

        mailHost = args[0];
        user = args[1];
        password = args[2];
        folderName = args[3];

        setProjectId(args[4]);
        
        protocol =  args[5];

        try {
            process();
        } catch (MessagingException ex) {            
            logger.error(ex);
        } catch (NotificationException ex) {
            logger.error(ex);
        }
    }

    /**
     * process() checks for new messages and calls processMsg() for every new
     * message
     * @throws MessagingException
     * @throws NotificationException
     */
    public void process() throws MessagingException, NotificationException {

        // Get a Session object
        //
        Session session = Session.getDefaultInstance(new Properties(), null);

        // Connect to host
        //
        Store store = session.getStore(protocol);
        store.connect(mailHost, -1, user, password);

        // Open the default folder
        //
        Folder src_folder = store.getFolder(folderName);

        if (src_folder == null) {
            throw new NotificationException("Unable to get folder: null");
        }
        // Get message count
        //
        src_folder.open(Folder.READ_WRITE);

        // TODO: never used, commented, task added:
        // int totalMessages = src_folder.getMessageCount();

        // Get attributes & flags for all messages
        //
        Message[] messages = src_folder.getMessages();
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add("From");
        src_folder.fetch(messages, fp);

        // Process each message
        //

        FlagTerm search = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            if (search.match(message)) {
                try {
                    
                    // TODO: process message
                    
                    message.setFlag(Flags.Flag.SEEN, true);
                    logger.info("Processed Message: " + message.getSubject() + " From: " + message.getFrom()[0]);
                } catch (Exception e) {
                    logger.error("Couldn't process Message: " + message.getSubject() + " From: "
                            + message.getFrom()[0], e);
                    try {
                        message.setFlag(Flags.Flag.SEEN, false);
                    } catch (Exception exception) {
                        logger.error(exception.getMessage(), exception);
                    }
                }
            } else {
                logger.info("Didn't process Message: " + message.getSubject() + " From: " + message.getFrom()[0]
                        + ". Message already read.");
            }

        }
        src_folder.close(true);
        store.close();
    }

}

