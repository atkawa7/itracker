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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.itracker.services.NotificationService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class NotificationServiceImpl implements NotificationService {

	private JmsTemplate jmsTemplate = new JmsTemplate();
	private Queue queue = null;

	public void sendNotification(String login, String email, String baseURL) {
		

		jmsTemplate.send(queue, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("hello queue world");
			}
		});
		
			/*Session session;
			MapMessage message = session.createMapMessage();
			message.setInt("type", NotificationUtilities.TYPE_SELF_REGISTER);
			String systemBaseURL = "";
			if (systemBaseURL != null && !systemBaseURL.equals("")) {
				message.setString("baseURL", systemBaseURL);
			} else if (baseURL != null) {
				message.setString("baseURL", baseURL);
			}
			message.setString("toAddress", email);
			message.setString("login", login);*/			
	}
}