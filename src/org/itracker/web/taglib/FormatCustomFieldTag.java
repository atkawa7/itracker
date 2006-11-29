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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


import org.apache.struts.taglib.TagUtils;
import org.itracker.model.CustomField;
import org.itracker.model.NameValuePair;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.WorkflowUtilities;
import org.itracker.web.util.Constants;


public final class FormatCustomFieldTag extends TagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String DISPLAY_TYPE_EDIT = "edit";
    public static final String DISPLAY_TYPE_VIEW = "view";
    
    private CustomField field;
    private String currentValue;
    private String displayType;
    private String formName;
    private HashMap<String,String> listOptions;
    
    public CustomField getField() {
        return field;
    }
    
    public void setField(CustomField value) {
        field = value;
    }
    
    public String getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(String value) {
        currentValue = value;
    }
    
    public String getDisplayType() {
        return displayType;
    }
    
    public void setDisplayType(String value) {
        displayType = value;
    }
    
    public String getFormName() {
        return formName;
    }
    
    public void setFormName(String value) {
        formName = value;
    }
    
    public HashMap<String,String> getListOptions() {
        return (listOptions == null ? new HashMap<String,String>() : listOptions);
    }
    
    public void setListOptions(HashMap<String,String> value) {
        listOptions = value;
    }
    
    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException {
        Locale currLocale = null;
        
        if(field != null) {
            HttpSession session = pageContext.getSession();
            if(session != null) {
                currLocale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            }
            
            field.setLabels(currLocale);
            
            StringBuffer buf = new StringBuffer();
            buf.append("<td class=\"" + (DISPLAY_TYPE_VIEW.equalsIgnoreCase(displayType) ? "editColumnTitle" : "editColumnTitle") +"\">");
            buf.append(field.getName() + ": ");
            buf.append("</td>\n");
            buf.append("<td align=\"left\" class=\"editColumnText\">");
            if(DISPLAY_TYPE_VIEW.equalsIgnoreCase(displayType)) {
                if(currentValue != null) {
                    buf.append((field.getFieldType() == CustomFieldUtilities.TYPE_LIST ? field.getOptionNameByValue(currentValue) : currentValue));
                }
            } else {
                // Object requestValue = RequestUtils.lookup(pageContext, org.apache.struts.taglib.html.Constants.BEAN_KEY, "customFields(" + field.getId() + ")", null);
                Object requestValue = TagUtils.getInstance().lookup(pageContext, org.apache.struts.taglib.html.Constants.BEAN_KEY, "customFields(" + field.getId() + ")", null);
                if(currentValue == null && requestValue != null) {
                    currentValue = requestValue.toString();
                }
                
                if(field.getFieldType() == CustomFieldUtilities.TYPE_LIST) {
                    List<NameValuePair> options = WorkflowUtilities.getListOptions(getListOptions(), field.getId());

                    buf.append("<select name=\"customFields(" + field.getId() + ")\" class=\"editColumnText\">\n");
                    for(int i = 0; i < options.size(); i++) {
                        buf.append("<option value=\"" + options.get(i).getValue() + "\"");
                        buf.append((currentValue != null && currentValue.equals(options.get(i).getValue()) ? " selected=\"selected\"" : ""));
                        buf.append(" class=\"editColumnText\">");
                        buf.append(options.get(i).getName());
                        buf.append("</option>\n");
                    }
                    buf.append("</select>\n");
                } else if(field.getFieldType() == CustomFieldUtilities.TYPE_DATE) {
                    buf.append("<input type=\"text\" name=\"customFields(" + field.getId() +")\"");
                    buf.append((currentValue != null && ! currentValue.equals("") ?  " value=\"" + currentValue + "\"" : ""));
                    buf.append(" class=\"editColumnText\">&nbsp;");
                    buf.append("<img onmouseup=\"toggleDatePicker('cf" + field.getId() + "','" + formName + ".customFields(" + field.getId() + ")')\" ");
                    buf.append("id=cf" + field.getId() + "Pos name=cf" + field.getId() + "Pos width=19 height=19 src=\"");
                    try {
                        // buf.append(RequestUtils.computeURL(pageContext, null, null, "/images/calendar.gif", null, null, null, false));
                        buf.append(TagUtils.getInstance().computeURL(pageContext, null, null, "/images/calendar.gif", null, null, null, null, false));
                        
                    } catch(MalformedURLException murle) {
                        buf.append("images/calendar.gif");
                    }
                    buf.append("\" align=\"top\" border=\"0\">");
                    buf.append("<div id=\"cf" + field.getId() + "\" style=\"position:absolute;\"></div>");
                } else {
                    buf.append("<input type=\"text\" name=\"customFields(" + field.getId() +")\"");
                    buf.append((currentValue != null && ! currentValue.equals("") ?  " value=\"" + currentValue + "\"" : ""));
                    buf.append(" class=\"editColumnText\">");
                }
            }
            buf.append("</td>\n");
            
            //ResponseUtils.write(pageContext, buf.toString());
            TagUtils.getInstance().write(pageContext, buf.toString());
        }
        
        clearState();
        return (EVAL_PAGE);
    }
    
    public void release() {
        super.release();
        clearState();
    }
    
    private void clearState() {
        field = null;
        currentValue = null;
    }
}
