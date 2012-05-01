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

package org.itracker.web.taglib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.util.LoginUtilities;

public class FormatDateTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String emptyKey = "itracker.web.generic.unavailable";
	private String format;
	private Date date;

	public String getFormat() {
		return format;
	}

	public void setFormat(String value) {
		format = value;
	}

	public Date getDate() {
		if (null == date)
			return null;
		return new Date(date.getTime());
	}

	public void setDate(Date value) {
		if (null == value)
			this.date = null;
		else
			date = new Date(value.getTime());
	}

	public String getEmptyKey() {
		return emptyKey;
	}

	public void setEmptyKey(String value) {
		emptyKey = value;
	}

	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		String value = "";
		SimpleDateFormat sdf;
		Locale locale = null;
		if (pageContext.getRequest() instanceof HttpServletRequest) {
			locale = LoginUtilities.getCurrentLocale((HttpServletRequest)pageContext.getRequest());
		}
		if (locale == null) {
			locale = ITrackerResources.getLocale();
		}
		
		if (date == null) {
			value = ITrackerResources.getString(emptyKey, locale);
		} else {
			try {
				if ("short".equalsIgnoreCase(format)) {
					sdf = new SimpleDateFormat(ITrackerResources.getString(
							"itracker.dateformat.short", locale), locale);
				} else if ("notime".equalsIgnoreCase(format)) {
					sdf = new SimpleDateFormat(ITrackerResources.getString(
							"itracker.dateformat.dateonly", locale), locale);
				} else {
					sdf = new SimpleDateFormat(ITrackerResources.getString(
							"itracker.dateformat.full", locale), locale);
				}
				value = sdf.format(date);
			} catch (Exception e) {
				value = ITrackerResources.getString(emptyKey, locale);
			}
		}
		// ResponseUtils.write(pageContext, value);
		TagUtils.getInstance().write(pageContext, value);
		clearState();
		return EVAL_PAGE;
	}

	public void release() {
		super.release();
		clearState();
	}

	private void clearState() {
		emptyKey = "itracker.web.generic.unavailable";
		format = null;
		date = null;
	}

}
