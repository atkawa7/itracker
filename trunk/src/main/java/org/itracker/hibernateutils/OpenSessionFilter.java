package org.itracker.hibernateutils;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;

/**
 * @deprecated this seems not to be implemented or used at all. Override to
 *             {@link #lookupSessionFactory()} seems not to be implemented
 *             correctly.
 * @author ranks
 * 
 */
public class OpenSessionFilter extends OpenSessionInViewFilter {

	public static SessionFactory sessionFactory;

	protected SessionFactory lookupSessionFactory() {
		return sessionFactory;
	}

}
