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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.itracker.services.ConfigurationService;
import org.itracker.web.util.NamingUtilites;

public class EmailService {

	private ConfigurationService configurationService;
	
	@Deprecated
	public static final String DEFAULT_FROM_ADDRESS = "itracker@localhost";
	public static final String DEFAULT_FROM_TEXT = "ITracker Notification System";
	@Deprecated
	public static final String DEFAULT_REPLYTO_ADDRESS = "itracker@localhost";
	@Deprecated
	public static final String DEFAULT_SMTP_HOST = "localhost";

	public static final String DEFAULT_SMTP_CHARSET = "ISO-8859-1";



	private Session session;

	private final Logger logger = Logger.getLogger(EmailService.class);
	private InternetAddress replyTo;

	public void setConfigurationService(
			ConfigurationService configurationService) {
		if (null == configurationService) {
			throw new IllegalArgumentException("configuration service must not be null.");
		}
		if (null != this.configurationService) {
			throw new IllegalStateException("configuration service was already set.");
		}
		this.configurationService = configurationService;
	}
	/**
	 * @deprecated use mailssession from JNDI instead
	 */
	private void initMailsession(String smtpHost, String smtpUserid, String smtpPassword) {
		Authenticator smtpAuth = null;
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		// props.put("mail.mime.charset", this.smtpCharset);

		if (smtpUserid != null && smtpPassword != null) {
			smtpAuth = (Authenticator) new EmailAuthenticator(
					new PasswordAuthentication(smtpUserid,
							smtpPassword));
		}
		this.session = Session.getInstance(props, smtpAuth);
	}

	private final void initCharset(String smtpCharset) {
		if (null != smtpCharset) {
			try {
				new String(new byte[0], smtpCharset);
				this.session.getProperties().put("mail.mime.charset",
						smtpCharset);
				logger.info("initCharset: cherset was initialized to "
						+ session.getProperty("mail.mime.charset"));
			} catch (UnsupportedEncodingException use) {
				logger
						.warn(
								"initCharset: unsupported smtp charset configured, ignoring",
								use);
			}
		}
	}

	private final void initFrom(String fromAddress, String fromText) {
		if (null != fromAddress) {
			try {
				this.session.getProperties().put(
						"mail.from",
						String.valueOf(new InternetAddress(fromAddress,
								fromText)));
				logger.info("initFrom: initialized to "
						+ this.session.getProperty("mail.from"));
			} catch (UnsupportedEncodingException e) {
				logger.warn(
						"initFrom: failed to set FROM in session, ignoring", e);
			}
		}
	}

	private final void initReplyTo(String replyToAddress, String replyToText) {
		try {
			this.replyTo = new InternetAddress(replyToAddress, replyToText);
			logger.info("initFrom: initialized reply-to: " + this.replyTo);
		} catch (UnsupportedEncodingException e) {
			logger.warn("initReplyTo: could not initialize reply-to", e);
		}
	}

	
	
	public void init() {

		if (this.configurationService == null) {
			throw new IllegalStateException("configuration service was not set.");
		}
		
		String fromAddress = configurationService.getProperty(
				"notification_from_address", DEFAULT_FROM_ADDRESS);
		String fromText = configurationService.getProperty("notification_from_text",
				DEFAULT_FROM_TEXT);
		String replyToAddress = configurationService.getProperty(
				"notification_replyto_address", DEFAULT_REPLYTO_ADDRESS);

		String smtpCharset = configurationService.getProperty(
				"notification_smtp_charset", DEFAULT_SMTP_CHARSET);

		String mailSessionLookupName = configurationService.getProperty(
				"mail_session_jndi_lookup", "none");

		logger
				.info("init: looking for Session in JNDI, lookup name from configuration: "
						+ mailSessionLookupName);

		logger.debug("init: got Mailsession from Naming Context:" + NamingUtilites.lookup(NamingUtilites
				.getDefaultInitialContext(), mailSessionLookupName));
		
		this.session = (Session) NamingUtilites.lookup(NamingUtilites
				.getDefaultInitialContext(), mailSessionLookupName);

		if (null == this.session) {
			logger
					.warn("init: failed to lookup Session from JNDI lookup " + mailSessionLookupName + ", using manual session");
			String smtpHost = configurationService.getProperty(
					"notification_smtp_host", DEFAULT_SMTP_HOST);
			String smtpUserid = configurationService.getProperty(
					"notification_smtp_userid", null);
			String smtpPassword = configurationService.getProperty(
					"notification_smtp_password", null);

			logger
					.warn("init: setting up SMTP manually, no session-lookup found in configuration!");
			if ("".equals(smtpUserid) || "".equals(smtpPassword)) {
				smtpUserid = null;
				smtpPassword = null;
			}
			initMailsession(smtpHost, smtpUserid, smtpPassword);
			initCharset(smtpCharset);
			initFrom(fromAddress, fromText);

			if (logger.isDebugEnabled()) {
				logger.debug("init: From Address set to: " + fromAddress);
				logger.debug("init: From Text set to: " + fromText);
				logger.debug("init: ReplyTo Address set to: " + replyToAddress);
				logger.debug("init: SMTP server set to: " + smtpHost);
				logger.debug("init: SMTP userid set to: " + smtpUserid);
				logger.debug("init: SMTP password set to: " + smtpPassword);
			}
		} else {
			initCharset(smtpCharset);
			initFrom(fromAddress, fromText);
		}
		initReplyTo(replyToAddress, fromText);
		
		if (logger.isDebugEnabled()) {
			logger.debug("init: From Address set to: " + fromAddress);
			logger.debug("init: From Text set to: " + fromText);
			logger.debug("init: ReplyTo Address set to: " + replyToAddress);

		}
	}

	/**
	 * 
	 * @param address
	 * @param subject
	 * @param msgText
	 */
	public void sendEmail(String address, String subject, String msgText) {
		try {
			InternetAddress[] recipients = new InternetAddress[1];
			recipients[0] = new InternetAddress(address);

			sendEmail(recipients, subject, msgText);
		} catch (AddressException ex) {
			logger.warn(
					"AddressException while sending email. caught. address was "
							+ address, ex);
		}
	}

	public void sendEmail(Set<InternetAddress> receipients, String subject,
			String message) {

		Iterator<InternetAddress> addrIt = receipients.iterator();
		InternetAddress[] receipientsArray = new InternetAddress[] {};
		while (addrIt.hasNext()) {
			receipientsArray[receipientsArray.length] = addrIt.next();
		}
		this.sendEmail(receipientsArray, subject, message);

	}

	/**
	 * @deprecated use method with InetAddress[] addresses instead.
	 * @param addresses
	 * @param subject
	 * @param msgText
	 */
	public void sendEmail(HashSet<String> addresses, String subject,
			String msgText) {

		if (null == addresses || 0 == addresses.size()) {
			throw new IllegalArgumentException(
					"No addresses in receipients set.");
		}

		try {

			ArrayList<InternetAddress> recipients = new ArrayList<InternetAddress>(
					addresses.size());

			Iterator<String> iterator = addresses.iterator();
			while (iterator.hasNext()) {
				recipients.add(new InternetAddress((String) iterator.next()));
			}

			sendEmail(recipients.toArray(new InternetAddress[0]), subject,
					msgText);
		} catch (AddressException ex) {
			logger.warn("AddressException while sending email.", ex);
		}
	}

	/**
	 * 
	 * @param address
	 * @param subject
	 * @param msgText
	 */
	public void sendEmail(InternetAddress address, String subject,
			String msgText) {
		this.sendEmail(new InternetAddress[] { address }, subject, msgText);
	}

	/**
	 * 
	 * @param receipients
	 * @param subject
	 * @param msgText
	 */
	public void sendEmail(InternetAddress[] receipients, String subject,
			String msgText) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("sendEmail: called with receipients: " + Arrays.asList(receipients) + ", subject: " + subject + ", msgText: " + subject);
			}
			if (null == this.session) {
				throw new IllegalStateException("session was not initialized.");
			}
			if (null == receipients || receipients.length < 1) {
				throw new IllegalArgumentException(
						"at least one receipient must be specified.");
			}
			if (null == subject || subject.length() < 1) {
				throw new IllegalArgumentException("subject must be specified.");
			}
			if (null == msgText) {
				msgText = "Empty message";
			}

			MimeMessage msg = new MimeMessage(this.session);

			// msg.setFrom(new InternetAddress(fromAddress, fromText));
			if (null != this.replyTo) {
				msg.setReplyTo(new InternetAddress[] { this.replyTo });
			}
			msg.setRecipients(javax.mail.Message.RecipientType.TO, receipients);
			msg.setSubject(subject);
			msg.setSentDate(new Date());

			// really needed?
			msg.setHeader("Content-Transfer-Encoding", "quoted-printable");
			msg.setContent(msgText.toString(), "text/plain; charset=\""
					+ session.getProperty("mail.mime.charset") + "\"");

			Transport.send(msg);

		} catch (MessagingException me) {
			logger.warn("MessagingException while sending email, caught.", me);
		}
	}
}