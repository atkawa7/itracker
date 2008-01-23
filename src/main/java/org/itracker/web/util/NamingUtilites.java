package org.itracker.web.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Utilities class for naming
 * 
 * @author ranks@rosa.com
 * 
 */
public class NamingUtilites {

	private static final Logger log = Logger.getLogger(NamingUtilites.class
			.getName());

	private static final InitialContext initialContext;	
	static {
		try {
			initialContext = new InitialContext();
		} catch (NamingException e) {
			throw new Error("failed to initialize naming utility", e);
		}

	}
	/**
	 * Read a String value of any type of object
	 * 
	 * @param ctx -
	 *            Context for lookup
	 * @param lookupName -
	 *            lookup name
	 * @param defaultValue -
	 *            default value
	 */
	public static final String getStringValue(Context ctx, String lookupName,
			String defaultValue) {
		String value = null;
		Object val;
		if (log.isDebugEnabled()) {
			log.debug("getStringValue: look up " + lookupName + " in context "
					+ ctx + ", default: " + defaultValue);

		}
		if (null == ctx) {
			log.debug("getStringValue: creating new InitialContext");
			try {
				ctx = new InitialContext();
				if (log.isDebugEnabled()) {
					log.debug("created new InitialContext: " + ctx);
				}
			} catch (NamingException e) {
				log.warn("getStringValue: failed to create InitialContext", e);
				if (log.isDebugEnabled())
					log.debug("getStringValue: context was " + ctx);
			}
		}

		if (null != ctx) {

			val = lookup(ctx, lookupName);
			if (val instanceof String) {
				value = (String) val;
			} else {
				value = (null == val) ? null : String.valueOf(val);
			}

		}
		value = (null == value) ? defaultValue : value;
		if (log.isDebugEnabled()) {
			log.debug("getStringValue: returning " + value);

		}

		return value;
	}

	/**
	 * savely get object from naming context
	 * 
	 * @param ctx
	 * @param lookupName
	 * @return Object - value
	 * @throws IllegalArgumentException -
	 *             if any argument is null, or the lookup name was empty
	 */
	public static final Object lookup(Context ctx, String lookupName) {
		if (null == ctx) {
			throw new IllegalArgumentException("context must not be null");
		}
		if (null == lookupName || lookupName.trim().length() == 0) {
			throw new IllegalArgumentException(
					"lookup name must not be empty, got: "
							+ ((null == lookupName) ? "<null>" : "'"
									+ lookupName + "'"));
		}
		try {
			return ctx.lookup(lookupName);
		} catch (NamingException e) {
			if (e instanceof NameNotFoundException) {
				log.info("lookup: failed to lookup " + lookupName
						+ ": name not found");
			} else {
				log.warn("lookup: failed to lookup " + lookupName
						+ " in context " + ctx, e);
			}
			return null;
		}
	}
	
	/**
	 * get a default initial context
	 * @return initial context.
	 * @throws IllegalStateException - if initial context was null
	 */
	public static final Context getDefaultInitialContext() {
		if (null == initialContext) {
			throw new IllegalStateException("initial context was not initialized");
		}
		return initialContext;
	}

}
