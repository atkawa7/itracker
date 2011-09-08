package org.itracker.web.ptos;

import java.util.Locale;

import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.services.util.CustomFieldUtilities;
import org.itracker.services.util.IssueUtilities;
import org.itracker.services.util.WorkflowUtilities;

public class ProjectScriptPTO {

	private final ProjectScript script;
	private final Locale locale;
	
	public ProjectScriptPTO(ProjectScript script, Locale locale) {
		this.script = script;
		this.locale = locale;
	}
	
	public String getFieldName() {
        return CustomFieldUtilities.getCustomFieldName(this.script.getFieldId(), locale);
    	//return IssueUtilities.getFieldName(this.script.getFieldId(), script.getProject().getCustomFields(), locale);
	}
	
	public String getEventName() {
		return WorkflowUtilities.getEventName(script.getScript().getEvent(), locale);
	}
    @Deprecated
	public ProjectScript getVO() {
		return this.script;
	}
    public ProjectScript getScript() {
        return this.script;
    }

}
