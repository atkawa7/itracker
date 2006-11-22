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

import java.net.MalformedURLException;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.UserPreferences;
import org.itracker.web.util.Constants;
//import org.apache.struts.util.RequestUtils;
//import org.apache.struts.util.ResponseUtils;


public final class FormatImageActionTag extends TagSupport {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String action = null;
    private String forward = null;
    private String module = null;
    private String paramName = null;
    private String paramValue = null;
    private String src = null;
    private String altKey = null;
    private String arg0 = null;
    private String textActionKey = null;
    private String border = null;
    private String caller = null;
    private String targetAction = null;
    private String target = null;

    public String getAction() {
      	return action;
    }

    public void setAction(String value) {
    	  action = value;
    }

    public String getForward() {
      	return forward;
    }

    public void setForward(String value) {
    	  forward = value;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getParamName() {
      	return paramName;
    }

    public void setParamName(String value) {
    	  paramName = value;
    }

    public Object getParamValue() {
      	return paramValue;
    }

    public void setParamValue(Object value) {
    	  paramValue = (value != null ? value.toString() : null);
    }

    public String getSrc() {
      	return src;
    }

    public void setSrc(String value) {
    	  src = value;
    }

    public String getAltKey() {
      	return altKey;
    }

    public void setAltKey(String value) {
    	  altKey = value;
    }

    public Object getArg0() {
      	return arg0;
    }

    public void setArg0(Object value) {
    	  arg0 = (value != null ? value.toString() : null);
    }

    public String getTextActionKey() {
      	return textActionKey;
    }

    public void setTextActionKey(String value) {
    	  textActionKey = value;
    }

    public String getBorder() {
      	return border;
    }

    public void setBorder(String value) {
    	  border = value;
    }

    public String getCaller() {
      	return caller;
    }

    public void setCaller(String value) {
    	  caller = value;
    }

    public String getTargetAction() {
      	return targetAction;
    }

    public void setTargetAction(String value) {
    	  targetAction = value;
    }

    public String getTarget() {
      	return target;
    }

    public void setTarget(String value) {
    	  target = value;
    }

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        boolean hasParams = false;
        boolean useTextActions = false;
        Locale currLocale = null;

        HttpSession session = pageContext.getSession();
        if(session != null) {
            currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            UserPreferences currUserPrefs = (UserPreferences) session.getAttribute(Constants.PREFERENCES_KEY);
            useTextActions = (currUserPrefs != null ? currUserPrefs.getUseTextActions() : false);
        }

        StringBuffer buf = new StringBuffer("<a href=\"");
        try {
          //  buf.append(RequestUtils.computeURL(pageContext, forward, null, null, action, null, null, false));
            buf.append(TagUtils.getInstance().computeURL(pageContext, forward, null, null, action, getModule(), null, null, false));
        } catch(MalformedURLException murle) {
            buf.append(forward);
        }
        if(paramName != null && paramValue != null) {
            buf.append("?" + paramName + "=" + paramValue);
            hasParams = true;
        }
        if(caller != null) {
            buf.append((hasParams ? "&" : "?") + "caller=" + caller);
            hasParams = true;
        }
        if(targetAction != null) {
            buf.append((hasParams ? "&" : "?") + "action=" + targetAction);
            hasParams = true;
        }
        buf.append("\"");
        if(target != null) {
            buf.append(" target=\"" + target + "\"");
        }
        if(useTextActions) {
            buf.append(" title=\"" + ITrackerResources.getString(altKey, currLocale, (arg0 == null ? "" : arg0)) + "\"");
            buf.append(" class=\"action\">");
            buf.append(ITrackerResources.getString(textActionKey, currLocale));
        } else {
            buf.append(">");
            buf.append("<img src=\"");
            try {
                // buf.append(RequestUtils.computeURL(pageContext, null, null, src, null, null, null, false));
                buf.append(TagUtils.getInstance().computeURL(pageContext, null, null, src, null, "", null, null, false));

            } catch(MalformedURLException murle) {
                buf.append(src);
            }
            buf.append("\"");
            if(altKey != null) {
                buf.append(" alt=\"" + ITrackerResources.getString(altKey, currLocale, (arg0 == null ? "" : arg0)) + "\"");
                buf.append(" title=\"" + ITrackerResources.getString(altKey, currLocale, (arg0 == null ? "" : arg0)) + "\"");
            }
            buf.append(" border=\"" + (border == null ? "0" : border) + "\"");
            buf.append(">");
        }
        buf.append("</a>");
        // ResponseUtils.write(pageContext, buf.toString());
        TagUtils.getInstance().write(pageContext, buf.toString());
        clearState();
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        action = null;
        forward = null;
        paramName = null;
        paramValue = null;
        src = null;
        altKey = null;
        arg0 = null;
        textActionKey = null;
        border = null;
        caller = null;
    }
}
