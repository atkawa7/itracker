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
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.util.Constants;
// import org.apache.struts.util.RequestUtils;
// import org.apache.struts.util.ResponseUtils;


public final class FormatLinkTag extends BodyTagSupport {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String text = null;

    private String action = null;
    private String forward = null;
    private String paramName = null;
    private String paramValue = null;
    private String titleKey = null;
    private String arg0 = null;
    private String caller = null;
    private String targetAction = null;
    private String target = null;
    private String styleClass = null;
    private String queryString = null;

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

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String value) {
        queryString = value;
    }

    public String getTitleKey() {
      	return titleKey;
    }

    public void setTitleKey(String value) {
    	  titleKey = value;
    }

    public Object getArg0() {
      	return arg0;
    }

    public void setArg0(Object value) {
    	  arg0 = (value != null ? value.toString() : null);
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

    public String getStyleClass() {
      	return styleClass;
    }

    public void setStyleClass(String value) {
    	  styleClass = value;
    }

    public int doStartTag() throws JspException {
        text = null;
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        if(bodyContent != null) {
            String value = bodyContent.getString().trim();
            if(value.length() > 0) {
                text = value;
            }
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        boolean hasParams = false;
        Locale currLocale = null;

        HttpSession session = pageContext.getSession();
        if(session != null) {
            currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
        }

        StringBuffer buf = new StringBuffer("<a href=\"");
        try {
            // buf.append(RequestUtils.computeURL(pageContext, forward, null, null, action, null, null, false));
            buf.append(TagUtils.getInstance().computeURL(pageContext, forward, null, null, action, null, null, null, false));            
        } catch(MalformedURLException murle) {
            buf.append(forward);
        }
        if(queryString != null) {
            buf.append("?" + queryString);
            hasParams = true;
        }
        if(paramName != null && paramValue != null) {
            buf.append((hasParams ? "&" : "?") + paramName + "=" + paramValue);
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
        if(titleKey != null) {
            buf.append(" title=\"" + ITrackerResources.getString(titleKey, currLocale, (arg0 == null ? "" : arg0)) + "\"");
        }
        if(styleClass != null) {
            buf.append(" class=\"" + styleClass + "\"");
        }
        buf.append(">");
        buf.append((text == null ? "" : text));
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
        text = null;
        action = null;
        forward = null;
        paramName = null;
        paramValue = null;
        titleKey = null;
        arg0 = null;
        caller = null;
        target = null;
        targetAction = null;
        styleClass = null;
        queryString = null;
    }
}
