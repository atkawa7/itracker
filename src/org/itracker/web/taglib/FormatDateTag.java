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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.util.Constants;
//import org.apache.struts.util.ResponseUtils;


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
        return date;
    }

    public void setDate(Date value) {
        date = value;
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
        Locale currLocale = null;

        HttpSession session = pageContext.getSession();
        if(session != null) {
            currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
        }

        if(currLocale == null) {
            currLocale = ITrackerResources.getLocale();
        }

        if(date == null) {
            value = ITrackerResources.getString(emptyKey, currLocale);
        } else {
            try {
                if("short".equalsIgnoreCase(format)) {
                    sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.short", currLocale), currLocale);
                } else if("notime".equalsIgnoreCase(format)) {
                    sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.dateonly", currLocale), currLocale);
                } else {
                    sdf = new SimpleDateFormat(ITrackerResources.getString("itracker.dateformat.full", currLocale), currLocale);
                }
                value = sdf.format(date);
            } catch(Exception e) {
                value = ITrackerResources.getString(emptyKey, currLocale);
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
