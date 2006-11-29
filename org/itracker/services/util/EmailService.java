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

package org.itracker.services.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

import org.itracker.services.ConfigurationService;


public class EmailService {
    
    public static final String DEFAULT_FROM_ADDRESS = "itracker@localhost";
    public static final String DEFAULT_FROM_TEXT = "ITracker Notification System";
    public static final String DEFAULT_REPLYTO_ADDRESS = "itracker@localhost";
    public static final String DEFAULT_SMTP_HOST = "localhost";
    public static final String DEFAULT_SMTP_USERID = null;
    public static final String DEFAULT_SMTP_PASSWORD = null;
    public static final String DEFAULT_SMTP_CHARSET = "ISO-8859-1";

    private String fromAddress;
    private String fromText;
    private String replyToAddress;
    private String smtpHost;
    private String smtpUserid;
    private String smtpPassword;
    private String smtpCharset;

    private final Logger logger;

    public EmailService(ConfigurationService configurationService) {
        this.logger = Logger.getLogger(getClass());

        fromAddress = configurationService.getProperty("notification_from_address", DEFAULT_FROM_ADDRESS);
        fromText = configurationService.getProperty("notification_from_text", DEFAULT_FROM_TEXT);
        replyToAddress = configurationService.getProperty("notification_replyto_address", DEFAULT_REPLYTO_ADDRESS);
        smtpHost = configurationService.getProperty("notification_smtp_host", DEFAULT_SMTP_HOST);
        smtpUserid = configurationService.getProperty("notification_smtp_userid", null);
        smtpPassword = configurationService.getProperty("notification_smtp_password", null);
        smtpCharset = configurationService.getProperty("notification_smtp_charset", DEFAULT_SMTP_CHARSET);

        if ("".equals(smtpUserid) || "".equals(smtpPassword)) {
            smtpUserid = null;
            smtpPassword = null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Notification Init: From Address set to: " + fromAddress);
            logger.debug("Notification Init: From Text set to: " + fromText);
            logger.debug("Notification Init: ReplyTo Address set to: " + replyToAddress);
            logger.debug("Notification Init: SMTP server set to: " + smtpHost);
            logger.debug("Notification Init: SMTP userid set to: " + smtpUserid);
            logger.debug("Notification Init: SMTP password set to: " + smtpPassword);
        }
    }

    public void sendEmail(String address, String subject, String msgText) {
        try {
            InternetAddress[] recipients = new InternetAddress[1];
            recipients[0] = new InternetAddress(address);

            sendEmail(recipients, subject, msgText);
        } catch (AddressException ex) {
            logger.debug("AddressException while sending email.", ex);
        }
    }

    public void sendEmail(HashSet addresses, String subject, String msgText) {
        int i = 0;

        try {
            InternetAddress[] recipients = new InternetAddress[addresses.size()];
            for (Iterator iterator = addresses.iterator(); iterator.hasNext(); i++) {
                recipients[i] = new InternetAddress((String) iterator.next());
            }

            sendEmail(recipients, subject, msgText);
        } catch (AddressException ex) {
            logger.debug("AddressException while sending email.", ex);
        }
    }

    public void sendEmail(InternetAddress[] addresses, String subject, String msgText) {
        try {
            Authenticator smtpAuth = null;
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.mime.charset", smtpCharset);

            if (smtpUserid != null && smtpPassword != null) {
                smtpAuth = (Authenticator) new EmailAuthenticator(new PasswordAuthentication(smtpUserid, smtpPassword));
            }

            MimeMessage msg = new MimeMessage(javax.mail.Session.getInstance(props, smtpAuth));

            msg.setFrom(new InternetAddress(fromAddress, fromText));
            msg.setReplyTo(new InternetAddress[] { new InternetAddress(replyToAddress, fromText) });
            msg.setRecipients(javax.mail.Message.RecipientType.TO, addresses);
            msg.setSubject(subject, smtpCharset);
            msg.setSentDate(new Date());

            msg.setHeader("Content-Transfer-Encoding", "quoted-printable");
            msg.setContent(msgText.toString(), "text/plain; charset=\"" + smtpCharset + "\"");

            Transport.send(msg);
        } catch (AddressException ae) {
            logger.debug("AddressException while sending email.", ae);
        } catch (MessagingException me) {
            logger.debug("MessagingException while sending email.", me);
        } catch (UnsupportedEncodingException uee) {
            logger.debug("UnsupportedEncodingException while sending email.", uee);
        }
    }
}