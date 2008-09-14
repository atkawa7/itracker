package org.itracker.web.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UserForm extends ValidatorForm {

    private static final long serialVersionUID = 2726189244374789017L;

    private String action = null;
    private Integer id = -1;
    private String login = null;
    private String currPassword = null;
    private String password = null;
    private String confPassword = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;

    private boolean superUser = false;

    private Map<String, String> permissions = new HashMap<String, String>();

    private String userLocale = null;
    private String saveLogin = null;
    private String numItemsOnIndex = null;
    private String numItemsOnIssueList = null;
    private String showClosedOnIssueList = null;
    private String sortColumnOnIssueList = null;
    private Integer[] hiddenIndexSections = null;
    private String rememberLastSearch = null;
    private String useTextActions = null;

    public String getAction() {
        return action;
    }

    public void setAction(String value) {
        action = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer value) {
        id = value;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String value) {
        login = value;
    }

    public String getCurrPassword() {
        return currPassword;
    }

    public void setCurrPassword(String value) {
        currPassword = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    public String getConfPassword() {
        return confPassword;
    }

    public void setConfPassword(String value) {
        confPassword = value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        firstName = value;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        lastName = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        email = value;
    }

    public boolean isSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean value) {
        superUser = value;
    }

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, String> value) {
        permissions = value;
    }

    public String getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(String value) {
        userLocale = value;
    }

    public String getSaveLogin() {
        return saveLogin;
    }

    public void setSaveLogin(String value) {
        saveLogin = value;
    }

    public String getNumItemsOnIndex() {
        return numItemsOnIndex;
    }

    public void setNumItemsOnIndex(String value) {
        numItemsOnIndex = value;
    }

    public String getNumItemsOnIssueList() {
        return numItemsOnIssueList;
    }

    public void setNumItemsOnIssueList(String value) {
        numItemsOnIssueList = value;
    }

    public String getShowClosedOnIssueList() {
        return showClosedOnIssueList;
    }

    public void setShowClosedOnIssueList(String value) {
        showClosedOnIssueList = value;
    }

    public String getSortColumnOnIssueList() {
        return sortColumnOnIssueList;
    }

    public void setSortColumnOnIssueList(String value) {
        sortColumnOnIssueList = value;
    }

    public Integer[] getHiddenIndexSections() {
        return hiddenIndexSections;
    }

    public void setHiddenIndexSections(Integer[] value) {
        hiddenIndexSections = value;
    }

    public String getRememberLastSearch() {
        return rememberLastSearch;
    }

    public void setRememberLastSearch(String value) {
        rememberLastSearch = value;
    }

    public String getUseTextActions() {
        return useTextActions;
    }

    public void setUseTextActions(String value) {
        useTextActions = value;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        action = null;
        id = -1;
        login = null;
        currPassword = null;
        password = null;
        confPassword = null;
        firstName = null;
        lastName = null;
        email = null;
        superUser = false;

        permissions = new HashMap<String, String>();

        userLocale = null;
        saveLogin = null;
        numItemsOnIndex = null;
        numItemsOnIssueList = null;
        showClosedOnIssueList = null;
        sortColumnOnIssueList = null;
        hiddenIndexSections = null;
        rememberLastSearch = null;
        useTextActions = null;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        if(password == null || password.trim().equals("")) {
            return errors;
        }

        if (!("register".equalsIgnoreCase(action)
                || "create".equalsIgnoreCase(action)
                || "update".equalsIgnoreCase(action)
                || "preferences".equalsIgnoreCase(action))
                && (currPassword == null
                || "".equals(currPassword))) {

            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingpassword"));
            request.setAttribute("warnings", errors);

        } else if (!password.equals(confPassword)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.matchingpass"));
            request.setAttribute("warnings", errors);
        }

        return errors;
    }

}
