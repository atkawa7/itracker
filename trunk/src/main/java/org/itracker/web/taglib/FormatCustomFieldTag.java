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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.NameValuePair;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.HTMLUtilities;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

public final class FormatCustomFieldTag extends TagSupport {
	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final String DISPLAY_TYPE_EDIT = "edit";
	public static final String DISPLAY_TYPE_VIEW = "view";

	private static final Logger logger = Logger
			.getLogger(FormatCustomFieldTag.class);

	private CustomField field;
	private String currentValue;
	private String displayType;
	private String formName;
	private Map<Integer, List<NameValuePair>> listOptions;

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

	public Map<Integer, List<NameValuePair>> getListOptions() {
		return (listOptions == null ? new HashMap<Integer, List<NameValuePair>>()
				: listOptions);
	}

	public void setListOptions(Map<Integer, List<NameValuePair>> value) {
		listOptions = value;
	}

	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		Locale locale = null;

		if (field != null) {
			locale = LoginUtilities
					.getCurrentLocale((HttpServletRequest) pageContext
							.getRequest());

			StringBuffer buf = new StringBuffer();
			buf
					.append("<td class=\""
							+ (DISPLAY_TYPE_VIEW.equalsIgnoreCase(displayType) ? "editColumnTitle"
									: "editColumnTitle") + "\">");
			buf.append(CustomFieldUtilities.getCustomFieldName(field.getId(),
					locale)
					+ ": ");
			buf.append("</td>\n");
			buf.append("<td align=\"left\" class=\"editColumnText\">");
			if (DISPLAY_TYPE_VIEW.equalsIgnoreCase(displayType)) {
				if (currentValue != null) {
					// buf.append((field.getFieldType() == CustomField.Type.LIST
					// ? field.getOptionNameByValue(currentValue) :
					// currentValue));

					if (field.getFieldType() == CustomField.Type.LIST) {
						buf.append(CustomFieldUtilities
								.getCustomFieldOptionName(getField(),
										currentValue, locale));

					} else {

						buf.append(currentValue);
					}
				}
			} else {
				// Object requestValue = RequestUtils.lookup(pageContext,
				// org.apache.struts.taglib.html.Constants.BEAN_KEY,
				// "customFields(" + field.getId() + ")", null);
				Object requestValue = TagUtils.getInstance().lookup(
						pageContext,
						org.apache.struts.taglib.html.Constants.BEAN_KEY,
						"customFields(" + field.getId() + ")", null);
				if (currentValue == null && requestValue != null) {
					currentValue = requestValue.toString();
				}

				if (field.getFieldType() == CustomField.Type.LIST) {
					List<CustomFieldValue> options = field.getOptions();
					// WorkflowUtilities.getListOptions(getListOptions(),
					// field.getId());

					buf.append("<select name=\"customFields(").append(
							field.getId()).append(
							")\" class=\"editColumnText\">\n");
					for (int i = 0; i < options.size(); i++) {
						// TODO: why not work with option-id here? if value
						// contains quotes, problem.
						buf.append("<option value=\"").append(
								HTMLUtilities.escapeTags(options.get(i)
										.getValue())).append("\"");
						if (currentValue != null
								&& currentValue.equals(options.get(i)
										.getValue())) {
							buf.append(" selected=\"selected\"");
						}
						buf.append(" class=\"editColumnText\">");
						buf.append(CustomFieldUtilities
								.getCustomFieldOptionName(options.get(i),
										locale));
						// buf.append(options.get(i).getName());
						buf.append("</option>\n");
					}
					buf.append("</select>\n");
					
					
					
				} else if (field.getFieldType() == CustomField.Type.DATE) {
					String df = ITrackerResources.getString(
							"itracker.dateformat.dateonly", locale);
					if (field.getDateFormat().equals("full")) {
						df = ITrackerResources.getString(
								"itracker.dateformat.full", locale);
					}
					
					String fieldName = "customFields(" + field.getId() + ")";
					String cf = "cf" + field.getId();
					buf.append("<input type=\"text\" name=\"")
							.append(fieldName).append("\" id=\"")
							.append(fieldName).append("\"");
					buf.append((currentValue != null
							&& !currentValue.equals("") ? " value=\""
							+ currentValue + "\"" : ""));
//					buf.append(" onchange=\"onDateChange(this, " + cf + ")\"");
					buf.append(" class=\"editColumnText\" />&nbsp;");
					buf.append("<img onmouseup=\"toggleCalendar(" + cf + ")\"");
					buf.append("id=\"").append(cf).append(
							"Pos\" name=\"").append(cf).append(
							"Pos\" width=\"19\" height=\"19\" src=\"");
					buf.append(ServletContextUtils.getItrackerServices()
							.getConfigurationService().getSystemBaseURL());
					buf.append("/themes/defaulttheme/images/calendar.gif");
					buf.append("\" align=\"top\" border=\"0\" />");
					buf.append("<div class=\"scal tinyscal\" id=\"").append(cf).append(
							"\"></div>");
					
					SimpleDateFormat monthDf = new SimpleDateFormat("MMMMM", locale);
					SimpleDateFormat dayDf = new SimpleDateFormat("E", locale);
					Calendar cal = new GregorianCalendar(locale);
					cal.set(Calendar.MONTH, 0);
					cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					
					Date date = null;
					try  {
						DateFormat df1 = new SimpleDateFormat(df);
						date = df1.parse(currentValue);
					} catch (ParseException e) {
						
					} catch (RuntimeException e) {
						
					}

					buf.append("    <script type=\"text/javascript\" language=\"javascript\">");
					buf.append("    /* <![CDATA[*/\n");
					buf.append("Object.extend(Date.prototype, {");
					buf.append("monthnames: [\"");
					for (int i = 0; i < 12; i++) {
						cal.set(Calendar.MONTH, i);
						buf.append(monthDf.format(cal.getTime()));
						if (i == 11) {
							buf.append("\"]");
						} else {
							buf.append("\",\n \"");
						}
					}
					buf.append(",\n");
					buf.append("daynames: [\"");
					for (int i = 1; i < 8; i++) {
						cal.set(Calendar.DAY_OF_WEEK, i);
						buf.append(dayDf.format(cal.getTime()));
						if (i == 7) {
							buf.append("\"]");
						} else {
							buf.append("\",\n \"");
						}
					}
					buf.append("});\n");

					buf.append("        var options = Object.extend({\n");
					buf.append("            titleformat:'mmmm yyyy',\n");
//					buf.append("            updateformat:'mm/dd/yyyy',\n");
					buf.append("            updateformat:'" + HTMLUtilities.getJSDateFormat(df) + "',\n");
					buf.append("            dayheadlength:2,\n");
					buf.append("            weekdaystart:1,\n");
					buf.append("            tabular: true,\n");
					buf.append("            closebutton:'x',\n");
					buf.append("            planner: false,\n");
//					custom expanded option
					buf.append("            expanded: false\n");
					buf.append("        });\n");
//					buf.append("\nvar updateyear = function(d){ $('"+ fieldName + "').value = d.format('" + HTMLUtilities.getJSDateFormat(df) + "'); };");
					buf.append("\nvar " + cf + " = new scal('" + cf + "', '"+ fieldName + "', options);");
					
					if (null != date) {
						Calendar cal1 = GregorianCalendar.getInstance();
						cal1.setTime(date);
						buf.append("\n " + cf + ".setCurrentDate(new Date(" + cal1.get(Calendar.YEAR) + ", " + cal1.get(Calendar.MONTH) + ", " + cal1.get(Calendar.DAY_OF_MONTH) + "));");
					}
					
//					buf.append("\n$('" + cf + "').cf=" + cf + ";");
					buf.append("// ]]>");
					buf.append("</script>");

//					// try {
//					// buf.append(RequestUtils.computeURL(pageContext, null,
//					// null, "/images/calendar.gif", null, null, null, false));
//					buf.append(ServletContextUtils.getItrackerServices()
//							.getConfigurationService().getSystemBaseURL());
//					buf.append("/themes/defaulttheme/images/calendar.gif");
//					// buf.append(TagUtils.getInstance().computeURL(pageContext,
//					// null, null, "/images/calendar.gif", null, null, null,
//					// null, false));
//
//					// } catch(MalformedURLException murle) {
//					// buf.append("images/calendar.gif");
//					// }

				} else {
					buf.append("<input type=\"text\" name=\"customFields(")
							.append(field.getId()).append(")\"");
					buf.append((currentValue != null
							&& !currentValue.equals("") ? " value=\""
							+ currentValue + "\"" : ""));
					buf.append(" class=\"editColumnText\">");
				}
			}
			buf.append("</td>\n");

			// ResponseUtils.write(pageContext, buf.toString());
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
		displayType = null;
		listOptions = null;
		formName = null;
	}
	

	private String formatDate(Date dateValue, ResourceBundle bundle) {
		// TODO Auto-generated method stub

		try {

			SimpleDateFormat sdf =
				new SimpleDateFormat(bundle
						.getString("itracker.dateformat."
								+ field.getDateFormat()), bundle.getLocale());

//			if (log.isDebugEnabled()) {
//				log.debug("getValue: dateFormat from itracker configuration "
//						+ sdf.toPattern());
//			}
			
			// sdf = new SimpleDateFormat(dateFormat, locale);
			String formattedDate = sdf.format(dateValue);
//			if (log.isDebugEnabled()) {
//				log.debug("getValue: formated date " + this.dateValue
//						+ " to " + formattedDate);
//			}
			return formattedDate;
		} catch (Exception ne) {
//			log.debug("getValue: ", ne);
//			if (dateValue == null) {
//				log.warn("getValue: failed to format date, null for "
//						+ customField);
//			}
			return "";
		}
	}
}
