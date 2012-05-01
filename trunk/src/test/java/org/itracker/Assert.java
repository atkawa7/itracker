package org.itracker;

import java.util.Comparator;



import org.apache.log4j.Logger;
import org.itracker.model.Entity;

/**
 * Itracker specific assertions for include static to test-classes.
 * 
 */
public class Assert extends junit.framework.Assert {

	private static final Logger log = Logger.getLogger(Assert.class);
	
	/**
	 * asserts true when comparator compares for lhs being &lt; rhs and rhs &gt; lhs.
	 * 
	 * @param message
	 * @param comparator
	 * @param lhs
	 * @param rhs
	 */
	@SuppressWarnings("unchecked")
	public static void assertEntityComparator(String message, Comparator comparator, Entity lhs, Entity rhs)  {
		if (log.isDebugEnabled()) {
			log.debug("assertEntityComparator: " + message + " " + comparator);
		}
		if (null == lhs) {
			throw new IllegalArgumentException("lhs must not both be null.");
		}
		// test nullpointer
		
		if (null == rhs) {

			try {
				assertNull(message + " lhs, null: " + comparator, comparator.compare(lhs, rhs));
				fail();
			} catch (NullPointerException npe) {} // ok
			
			try {
				assertNull(message  + " null, lhs: " + comparator, comparator.compare(rhs, lhs));
				fail();
			} catch (NullPointerException npe) {} // ok
			
			return;
		}
		
		assertTrue(message + " lhs, rhs: " + comparator, 0 > comparator.compare(lhs, rhs));
		assertTrue(message + " rhs, lhs: " + comparator, 0 < comparator.compare(rhs, lhs));
	}
	
	/**
	 * asserts true when comparator compares for lhs being equal to rhs and rhs equal lhs.
	 * 
	 * @param message
	 * @param comparator
	 * @param lhs
	 * @param rhs
	 */

	@SuppressWarnings("unchecked")
	public static void assertEntityComparatorEquals(String message, Comparator comparator, Entity lhs, Entity rhs) {
		if (log.isDebugEnabled()) {
			log.debug("assertEntityComparatorEquals: " + message + " " + comparator);
		}
		if (null == lhs || null == rhs) {
			throw new IllegalArgumentException("rhs and lhs must not be null.");
		}
		
		assertTrue(message + " lhs, rhs: " + comparator, 0 == comparator.compare(lhs, rhs));
		assertTrue(message + " rhs, lhs: " + comparator, 0 == comparator.compare(rhs, lhs));
	}
}
