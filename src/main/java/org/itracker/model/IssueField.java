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

package org.itracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.exceptions.IssueException;
import org.itracker.services.util.CustomFieldUtilities;

/**
 * A CustomField with its value for an Issue.
 * 
 * <p>
 * An IssueField can only belong to 1 Issue (composition).
 * </p>
 * 
 * @author ready
 * @see CustomField
 */
public class IssueField extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient final Logger log = Logger
			.getLogger(IssueField.class);


	private Issue issue;

	private CustomField customField;

	private String stringValue;

	private int intValue;

	private Date dateValue;

	/**
	 * Default constructor (required by Hibernate).
	 * 
	 * <p>
	 * PENDING: should be <code>private</code> so that it can only be used by
	 * Hibernate, to ensure that the fields which form an instance's identity
	 * are always initialized/never <tt>null</tt>.
	 * </p>
	 */
	public IssueField() {
	}

	public IssueField(Issue issue, CustomField field) {
		setIssue(issue);
		setCustomField(field);
	}


	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		if (issue == null) {
			throw new IllegalArgumentException("null issue");
		}
		this.issue = issue;
	}

	public CustomField getCustomField() {
		return customField;
	}

	public void setCustomField(CustomField customField) {
		if (customField == null) {
			throw new IllegalArgumentException("null customField");
		}
		this.customField = customField;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public Date getDateValue() {
		if (null == dateValue)
			return null;
		return new Date(dateValue.getTime());
	}

	public void setDateValue(Date dateValue) {
		if (null == dateValue)
			this.dateValue = null;
		else
			this.dateValue = new Date(dateValue.getTime());
	}

	/**
	 * Gets the custom field value as a String.
	 * 
	 * @param locale
	 *            the locale used for any string formatting
	 * @return the current value of this field
	 */
	public String getValue(Locale locale) {
		return getValue(ITrackerResources.getBundle(locale));
	}

	/**
	 * Gets the custom field value as a String.
	 * 
	 * @param bundle
	 *            a resource bundle to use for any string formatting
	 * @param locale
	 *            a locale to use for any string formatting
	 * @return the current value of this field
	 * @deprecated use getValue(ResourceBundle bundle) instead, locale is taken
	 *             from bundle
	 * 
	 */
	public String getValue(ResourceBundle bundle, Locale locale) {
		if (log.isDebugEnabled()) {
			log.debug("getValue: called with bundle: " + bundle + ", locale: "
					+ locale);
		}
		return getValue(bundle);
	}

	/**
	 * Gets the custom field value as a String.
	 * 
	 * @param bundle
	 *            a resource bundle to use for any string formatting
	 * @return the current value of this field
	 */
	public String getValue(ResourceBundle bundle) {
		Locale locale = bundle.getLocale();

		if (log.isDebugEnabled()) {
			log.debug("getValue: called with bundle: " + bundle + ", locale: "
					+ locale);
		}
		switch (customField.getFieldType()) {

		case INTEGER:
			if (log.isDebugEnabled()) {
				log
						.debug("getValue: type was INTEGER, value: "
								+ this.intValue);
			}
			return Integer.toString(this.intValue);

		case DATE:
			if (log.isDebugEnabled()) {
				log.debug("getValue: type was DATE, value: " + this.dateValue);
			}
			if (!customField.isRequired() && this.dateValue == null) {

				if (log.isDebugEnabled()) {
					log.debug("getValue: value was null and not required");
				}
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat();
			// Fall back to a default date format or make date format mandatory!
			String dateFormat = (customField.getDateFormat()
					.equalsIgnoreCase(CustomFieldUtilities.DATE_FORMAT_UNKNOWN)) ? CustomFieldUtilities.DATE_FORMAT_DATEONLY
					: customField.getDateFormat();

			if (log.isDebugEnabled()) {
				log.debug("getValue: " + dateFormat + " (original was "
						+ customField.getDateFormat() + ")");
			}
			dateFormat = bundle.getString("itracker.dateformat." + dateFormat);
			if (log.isDebugEnabled()) {
				log.debug("getValue: dateFormat from itracker configuration "
						+ dateFormat);
			}
			try {
				sdf = new SimpleDateFormat(dateFormat, locale);
				String formattedDate = sdf.format(this.dateValue);
				if (log.isDebugEnabled()) {
					log.debug("getValue: formated date " + this.dateValue
							+ " to " + formattedDate);
				}
				return formattedDate;
			} catch (NullPointerException ne) {
				log.debug("getValue: ", ne);
				if (dateValue == null) {
					log.warn("getValue: failed to format date, null for "
							+ customField.getName());
					return sdf.format(new Date());
				}
			}
		default:
			return (this.stringValue == null ? "" : this.stringValue);
		}

	}

	/**
	 * Sets the custom field value.
	 * 
	 * <p>
	 * Takes a string and then converts the value to the appropriate type based
	 * on the defined field type.
	 * </p>
	 * 
	 * TODO : throw IllegalArgumentException instead of IssueException ?
	 * 
	 * @param value
	 *            the value to set this field to as a string
	 * @param locale
	 *            the locale used for any string formatting
	 * @param bundle
	 *            the ResourceBundle used for any string formatting
	 * @throws IssueException
	 *             represents an error formatting or parsing the value
	 *             
	 * @deprecated locale is redundant set, in bundle and as separate parameter. use {@link IssueField#setValue(String, ResourceBundle)} instead
	 */
	public void setValue(String value, Locale locale, ResourceBundle bundle)
			throws IssueException {
		if (value != null) {
			switch (customField.getFieldType()) {
		
			case INTEGER:
				try {
					setIntValue(Integer.parseInt(value));
				} catch (NumberFormatException nfe) {
					throw new IssueException("Invalid integer.",
							IssueException.TYPE_CF_PARSE_NUM);
				}
				break;

			case DATE:
				try {
					if (customField.getDateFormat() != CustomFieldUtilities.DATE_FORMAT_UNKNOWN) {
						SimpleDateFormat sdf = new SimpleDateFormat(bundle
								.getString("itracker.dateformat."
										+ customField.getDateFormat()), locale);
						Date dateValue = sdf.parse(value);
						if (dateValue != null) {
							setDateValue(dateValue);
						} else {
							throw new IssueException("Invalid date.",
									IssueException.TYPE_CF_PARSE_DATE);
						}
					}
				} catch (Exception ex) {
					throw new IssueException("Invalid date format.",
							IssueException.TYPE_CF_PARSE_DATE);
				}
				break;

			default:
				setStringValue(value);
			}

		}
	}
	/**
	 * Sets the custom field value.
	 * 
	 * <p>
	 * Takes a string and then converts the value to the appropriate type based
	 * on the defined field type.
	 * </p>
	 * 
	 * TODO : throw IllegalArgumentException instead of IssueException ?
	 * 
	 * @param value
	 *            the value to set this field to as a string
	 * @param bundle
	 *            the ResourceBundle used for any string formatting
	 * @throws IssueException
	 *             represents an error formatting or parsing the value
	 *             
	 */
	public void setValue(String value, ResourceBundle bundle) throws IssueException {
		setValue(value, bundle.getLocale(), bundle);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", getId())
				.append("issue", getIssue()).append("customField", getCustomField())
				.toString();
	}

}
