/*
 * RemoveWorkflowScriptAction.java
 *
 * Created on 13. November 2005, 04:51
 */

package org.itracker.web.actions.admin.workflow;

 
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.PermissionType;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;

/**
 * Action to remove a workflow script
 *
 * <ol>
 *   <li>get all project and remove the script to be deleted</li>
 *   <li>delete the script</li>
 * </ol>
 *
 * @author mbae@bcwin.ch
 */
public class RemoveWorkflowScriptAction extends ItrackerBaseAction {
	private static final Logger log = Logger.getLogger(RemoveWorkflowScriptAction.class);
	
    /**
     * executes the action which removes a workflow script 
     *
     * @param form the form with user input
     * @param request the request triggering the action
     * @param response response to the client
     * @param mapping The action mapping
     *
     * @throws ServletException thrown if execution fails
     * @throws IOException thrown if io to client fails
     *
     * @return the <code>ActionForward</code> to forward to
     */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        ActionForward fw = mapping.findForward("error");

        
        // check permissions
        HttpSession session = request.getSession(true);
        Map<Integer, Set<PermissionType>> userPermissionsMap = getUserPermissions(session);
        if(! UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_USER_ADMIN)) {
            return mapping.findForward("unauthorized");
        }
        try {

            // get the id from the form
            Integer scriptId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            
            // remove the script
            this.getITrackerServices().getConfigurationService()
                .removeWorkflowScript(scriptId);
        
            // find the mapping for the list of all worksflows
            fw = mapping.findForward( "listworkflow" );
            
        } catch (InvocationTargetException ex) {
            log.error( ex.getMessage(), ex );
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidworkflowscript"));
        } catch (NoSuchMethodException ex) {
            log.error( ex.getMessage(), ex );
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidworkflowscript"));
        } catch (IllegalAccessException ex) {
            log.error( ex.getMessage(), ex );
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidworkflowscript"));
        }
        
        if(! errors.isEmpty()) {
        	saveErrors(request, errors);
        }
        return fw;
     }
}
