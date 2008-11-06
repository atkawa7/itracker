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

package org.itracker.web.actions.preferences;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.Constants;

public class EditPreferencesFormAction extends ItrackerBaseAction {
	private static final Logger log = Logger
			.getLogger(EditPreferencesFormAction.class);

	public EditPreferencesFormAction() {
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionMessages errors = new ActionMessages();

		try {
			UserService userService = getITrackerServices().getUserService();
			request.setAttribute("uh", userService);
			ConfigurationService configurationService = getITrackerServices()
					.getConfigurationService();
			request.setAttribute("sc", configurationService);
			Map<String, List<String>> languagesMap = configurationService
					.getAvailableLanguages();
			request.setAttribute("languagesMap", languagesMap);
			Set<String> languageCodes = languagesMap.keySet();
			request.setAttribute("languageCodes", languageCodes);

			Map<String, String> languagesList = new LinkedHashMap<String, String>();
			for (String languageCode : languageCodes) {
				List<String> languagelist = languagesMap.get(languageCode);
				ITrackerResources.getString("itracker.locale.name",
						languageCode);
				languagesList.put(languageCode, ITrackerResources.getString(
						"itracker.locale.name", languageCode));
				for (String languageitem : languagelist) {
					languagesList.put(languageitem, ITrackerResources
							.getString("itracker.locale.name", languageitem));
				}

			}
			if (languagesList.size() == 0) {
				languagesList.put("en_US", ITrackerResources.getString(
						"itracker.locale.name", "en_US"));
			}
			request.setAttribute("languagesList", languagesList);
			Integer languagesListSize = languagesList.size();
			request.setAttribute("languagesListSize", languagesListSize);
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(Constants.USER_KEY);
			if (user == null) {
				return mapping.findForward("login");
			}
			
			UserPreferences userPrefs = user.getPreferences();
			// TODO hack, this should be handled central, there are other
			// intances
			// of this hack
			if (userPrefs == null) {
				userPrefs = new UserPreferences();
			}

			UserForm userForm = (UserForm) form;
			if (userForm == null) {
				userForm = new UserForm();
			}
			userForm.setLogin(user.getLogin());
			userForm.setEmail(user.getEmail());
			userForm.setFirstName(user.getFirstName());
			userForm.setLastName(user.getLastName());

			Locale userLocale = ITrackerResources.getLocale(userPrefs
					.getUserLocale());
			userForm.setUserLocale((userLocale == null ? ITrackerResources
					.getDefaultLocale() : userLocale.toString()));
			userForm
					.setSaveLogin((userPrefs.getSaveLogin() ? "true" : "false"));
			userForm
					.setNumItemsOnIndex((userPrefs.getNumItemsOnIndex() < 1 ? ITrackerResources
							.getString("itracker.web.generic.all", userLocale)
							: Integer.toString(userPrefs.getNumItemsOnIndex())));
			userForm
					.setNumItemsOnIssueList((userPrefs.getNumItemsOnIssueList() < 1 ? ITrackerResources
							.getString("itracker.web.generic.all", userLocale)
							: Integer.toString(userPrefs
									.getNumItemsOnIssueList())));
			userForm.setShowClosedOnIssueList((userPrefs
					.getShowClosedOnIssueList() ? "true" : "false"));
			userForm.setSortColumnOnIssueList(userPrefs
					.getSortColumnOnIssueList());
			userForm
					.setHiddenIndexSections(UserUtilities
							.getHiddenIndexSections(userPrefs
									.getHiddenIndexSections()));
			userForm
					.setRememberLastSearch((userPrefs.getRememberLastSearch() ? "true"
							: "false"));
			userForm.setUseTextActions((userPrefs.getUseTextActions() ? "true"
					: "false"));

			Locale locale = getLocale(request);
			String statusName = UserUtilities.getStatusName(user.getStatus(),
					locale);
			request.setAttribute("statusName", statusName);
			String userLocaleAsString = ITrackerResources.getString(
					"itracker.locale.name", userPrefs.getUserLocale());
			request.setAttribute("userLocaleAsString", userLocaleAsString);
			String getSaveLoginLocalized = ITrackerResources.getString(
					(userPrefs.getSaveLogin() ? "itracker.web.generic.yes"
							: "itracker.web.generic.no"), locale);
			request
					.setAttribute("getSaveLoginLocalized",
							getSaveLoginLocalized);
			String showClosedOnIssueListLocalized = ITrackerResources
					.getString(
							(userPrefs.getShowClosedOnIssueList() ? "itracker.web.generic.yes"
									: "itracker.web.generic.no"), locale);
			request.setAttribute("showClosedOnIssueListLocalized",
					showClosedOnIssueListLocalized);
			String getRememberLastSearchLocalized = ITrackerResources
					.getString(
							(userPrefs.getRememberLastSearch() ? "itracker.web.generic.yes"
									: "itracker.web.generic.no"), locale);
			request.setAttribute("getRememberLastSearchLocalized",
					getRememberLastSearchLocalized);
			request.setAttribute("preferencesForm", userForm);
			session.setAttribute(Constants.EDIT_USER_KEY, user);
			session.setAttribute(Constants.EDIT_USER_PREFS_KEY, userPrefs);
			saveToken(request);

			Boolean allowPreferenceUpdate = false;
			Boolean allowProfileUpdate = false;
			Boolean allowPasswordUpdate = false;
			
			if (user != null) {
				allowProfileUpdate = userService.allowProfileUpdates(
						user, null, UserUtilities.AUTH_TYPE_UNKNOWN,
						UserUtilities.REQ_SOURCE_WEB);
				allowPasswordUpdate = userService.allowPasswordUpdates(
						user, null, UserUtilities.AUTH_TYPE_UNKNOWN,
						UserUtilities.REQ_SOURCE_WEB);

				allowPreferenceUpdate = userService
						.allowPreferenceUpdates(user, null,
								UserUtilities.AUTH_TYPE_UNKNOWN,
								UserUtilities.REQ_SOURCE_WEB);

			}

			request.setAttribute("allowProfileUpdate", allowProfileUpdate);
			request.setAttribute("allowPasswordUpdate", allowPasswordUpdate);				
			request.setAttribute("allowPreferenceUpdate", allowPreferenceUpdate);
			
			if (!allowPreferenceUpdate) {
				StringBuilder hiddenSectionsString = new StringBuilder();
				userPrefs = (UserPreferences) session
						.getAttribute("edituserprefs");
				Integer[] hiddenSections = UserUtilities
						.getHiddenIndexSections(userPrefs.getHiddenIndexSections());
				for (int i = 0; i < hiddenSections.length; i++) {
					int sectionNumber = (hiddenSections[i] != null ? hiddenSections[i]
							.intValue()
							: -1);
					if (sectionNumber == UserUtilities.PREF_HIDE_ASSIGNED) {
						hiddenSectionsString.append((hiddenSectionsString.length() == 0 ? ""
								: ", "));
						hiddenSectionsString.append(", ").append(ITrackerResources.getString(
										"itracker.web.editprefs.section.assigned",
										(java.util.Locale) session
												.getAttribute("currLocale")));
					} else if (sectionNumber == UserUtilities.PREF_HIDE_UNASSIGNED) {
						hiddenSectionsString.append((hiddenSectionsString.length() == 0 ? ""
								: ", "));
						hiddenSectionsString.append(", ").append(
								ITrackerResources
										.getString(
												"itracker.web.editprefs.section.unassigned",
												(java.util.Locale) session
														.getAttribute("currLocale")));
					} else if (sectionNumber == UserUtilities.PREF_HIDE_CREATED) {
						hiddenSectionsString.append((hiddenSectionsString.length() == 0 ? ""
								: ", "));
						hiddenSectionsString.append(", ").append(
								ITrackerResources.getString(
										"itracker.web.editprefs.section.created",
										(java.util.Locale) session
												.getAttribute("currLocale")));
					} else if (sectionNumber == UserUtilities.PREF_HIDE_WATCHED) {
						hiddenSectionsString.append((hiddenSectionsString.length() == 0 ? ""
								: ", "));
						hiddenSectionsString.append(", ").append(
								ITrackerResources.getString(
										"itracker.web.editprefs.section.watched",
										(java.util.Locale) session
												.getAttribute("currLocale")));
					}
				
				}
				request.setAttribute("hiddenSectionsString", hiddenSectionsString.toString());
			}

			request
					.setAttribute("pageTitleKey",
							"itracker.web.editprefs.title");
			request.setAttribute("pageTitleArg", "");

			return mapping.findForward("editpreferencesform");

		} catch (Exception e) {
			log.error("Exception while creating edit issue form.", e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"itracker.web.error.system"));
		}

		return mapping.findForward("error");
	}

}
