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

package org.itracker.web.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 * @author ready
 *
 */
public class DisplayReportForm extends ValidatorForm  {
     
	 String type;
     Integer[] projectIds;
     Integer reportId;
     String reportOutput;
     
          
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		type = null;    
		projectIds = null;
		reportId = null;
		reportOutput = null;
		
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
      
        return errors;
    }

	public Integer[] getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(Integer[] projectIds) {
		this.projectIds = projectIds;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getReportOutput() {
		return reportOutput;
	}

	public void setReportOutput(String reportOutput) {
		this.reportOutput = reportOutput;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}