package org.itracker.hibernateutils;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;

public class OpenSessionFilter extends OpenSessionInViewFilter {
	
	public static SessionFactory sessionFactory;

	protected SessionFactory lookupSessionFactory() {
		return sessionFactory;
	}

}
