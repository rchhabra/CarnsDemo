/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */

package de.hybris.platform.traveladdon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerPreferenceData;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.traveladdon.forms.APIForm;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.TravelI18NFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.DocumentType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Extended Controller for home page - adds functionality to AccountPageController
 */
@Controller
public class TransportAccountPageController extends AbstractPageController
{
	// CMS Pages
	protected static final String ACCOUNT_SAVED_SEARCHES_CMS_PAGE = "my-saved-searches";
	protected static final String ACCOUNT_ADVANCE_PASSENGER_CMS_PAGE = "advance-passenger";
	protected static final String ACCOUNT_SAVED_PASSENGERS_CMS_PAGE = "saved-passengers";
	protected static final String PREFERENCES_CMS_PAGE = "preferences";
	protected static final String SAVED_TRAVELLLERS = "savedTravellers";
	protected static final String REMOVED_TRAVELLER = "removedTraveller";
	protected static final String SAVED_SEARCHES = "savedSearches";
	protected static final String ERROR = "error";
	protected static final String LANGUAGES = "languages";
	protected static final String LANGUAGE_PREFERENCE_TYPE = "LANGUAGE";
	protected static final String TRANSPORT_FACILITY_PREFERENCE_TYPE = "TRANSPORT_FACILITY";
	protected static final String TRANSPORT_FACILITY_PREFERENCE_CODE = "TRANSPORT_FACILITY_CODE";
	protected static final String COUNTRIES = "countries";
	protected static final String NATIONALITIES = "nationalities";
	protected static final String TITLES = "titles";
	protected static final String API_FORM = "apiForm";
	protected static final String ADULT = "adult";
	protected static final String FORM_GLOBAL_CONFIRMATION = "form.global.confirmation";
	protected static final String BREADCRUMBS = "breadcrumbs";

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "travelCustomerFacade")
	private TravelCustomerFacade travelCustomerFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "travelI18NFacade")
	private TravelI18NFacade travelI18NFacade;

	@Resource(name = "transportOfferingFacade")
	private TransportOfferingFacade transportOfferingFacade;

	@Resource(name = "apiFormValidator")
	private Validator apiFormValidator;

	private String[] adultTitles;

	@RequestMapping(value = "/my-account/preferences", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getPreferencesPage(final Model model) throws CMSItemNotFoundException
	{
		final List<TravellerPreferenceData> travellerPreference = travellerFacade.getTravellerPreferences();

		travellerPreference.forEach(tp -> {
			if (StringUtils.equalsIgnoreCase(tp.getType(), TRANSPORT_FACILITY_PREFERENCE_TYPE))
			{
				final Map<String, Map<String, String>> suggestions = transportOfferingFacade.getOriginSuggestions(tp.getValue());

				if (!suggestions.isEmpty())
				{
					final String transportFacilityName = suggestions.get(suggestions.keySet().iterator().next()).keySet().iterator()
							.next();
					final String transportFacilityCode = suggestions.get(suggestions.keySet().iterator().next())
							.get(transportFacilityName);

					model.addAttribute(TRANSPORT_FACILITY_PREFERENCE_TYPE,
							transportFacilityName.replace("<strong>", "").replace("</strong>", ""));
					model.addAttribute(TRANSPORT_FACILITY_PREFERENCE_CODE, transportFacilityCode);
				}
			}

			if (StringUtils.equalsIgnoreCase(tp.getType(), LANGUAGE_PREFERENCE_TYPE))
			{
				model.addAttribute(LANGUAGE_PREFERENCE_TYPE, tp.getValue());
			}
		});

		model.addAttribute(LANGUAGES, travelI18NFacade.getAllLanguages());

		storeCmsPageInModel(model, getContentPageForLabelOrId(PREFERENCES_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PREFERENCES_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/my-account/save-preferences", method = RequestMethod.POST)
	@RequireHardLogIn
	public String saveTravellerPreferencesPage(@RequestParam final String preferredLanguage,
			@RequestParam final String transportFacilityCode, final Model model) throws CMSItemNotFoundException
	{
		final List<TravellerPreferenceData> travellerPreferences = new ArrayList<>();

		if (StringUtils.isNotEmpty(preferredLanguage))
		{
			final TravellerPreferenceData language = new TravellerPreferenceData();
			language.setType(LANGUAGE_PREFERENCE_TYPE);
			language.setValue(preferredLanguage);
			travellerPreferences.add(language);
		}

		if (StringUtils.isNotEmpty(transportFacilityCode))
		{
			final TravellerPreferenceData transportFacilityPreference = new TravellerPreferenceData();
			transportFacilityPreference.setType(TRANSPORT_FACILITY_PREFERENCE_TYPE);
			transportFacilityPreference.setValue(transportFacilityCode);
			travellerPreferences.add(transportFacilityPreference);
		}

		travellerFacade.getSaveTravellerPreferences(travellerPreferences);
		GlobalMessages.addInfoMessage(model, FORM_GLOBAL_CONFIRMATION);

		return getPreferencesPage(model);
	}

	/**
	 * Method responsible for handling GET request for Saved Customer Searches.
	 *
	 * @param model
	 * @return mySavedSearches page
	 */

	@RequestMapping(value = "/my-account/my-saved-searches", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getSavedSearchesPage(final Model model) throws CMSItemNotFoundException
	{
		final List<SavedSearchData> savedSearchDatas = travelCustomerFacade.getCustomerSearches();
		model.addAttribute(SAVED_SEARCHES, savedSearchDatas);

		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_SAVED_SEARCHES_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_SAVED_SEARCHES_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return getViewForPage(model);
	}

	/**
	 * Method responsible for handling GET request to remove the specific saved customer search.
	 *
	 * @param savedSearchID
	 * @param model
	 * @param request
	 * @param redirectModel
	 * @return mySavedSearches page
	 */
	@RequestMapping(value = "/my-account/remove-saved-searches/{savedSearchID}", method = RequestMethod.GET)
	public String getRemoveSavedSearchPage(@PathVariable final String savedSearchID, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final boolean isSearchRemoved = travelCustomerFacade.removeSavedSearch(savedSearchID);

		if (!isSearchRemoved)
		{
			redirectModel.addFlashAttribute(ERROR, true);
		}

		return REDIRECT_PREFIX + "/my-account/" + ACCOUNT_SAVED_SEARCHES_CMS_PAGE;

	}

	@RequestMapping(value = "/my-account/advance-passenger", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getAdvancePassenger(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(COUNTRIES, travelI18NFacade.getAllCountries());
		model.addAttribute(NATIONALITIES, travelI18NFacade.getAllCountries());
		model.addAttribute(TITLES, userFacade.getTitles().stream()
				.filter((titleData -> Arrays.asList(getAdultTitles()).contains(titleData.getCode()))).collect(Collectors.toList()));

		if (!model.containsAttribute(API_FORM))
		{
			final PassengerInformationData passengerInformationData = travellerFacade.getPassengerInformation();
			final APIForm apiForm = new APIForm();

			if (passengerInformationData != null)
			{
				apiForm.setFirstname(passengerInformationData.getFirstName());
				apiForm.setGender(passengerInformationData.getGender());
				apiForm.setLastname(passengerInformationData.getSurname());
				apiForm
						.setTitle((passengerInformationData.getTitle() == null) ? null : passengerInformationData.getTitle().getCode());
				apiForm.setDateOfBirth(passengerInformationData.getDateOfBirth() != null ? TravelDateUtils.convertDateToStringDate(
						passengerInformationData.getDateOfBirth(), TravelservicesConstants.DATE_PATTERN) : null);
				apiForm.setCountryOfIssue((passengerInformationData.getCountryOfIssue() == null) ? null
						: passengerInformationData.getCountryOfIssue().getIsocode());
				apiForm.setDocumentExpiryDate(
						passengerInformationData.getDocumentExpiryDate() != null ? TravelDateUtils.convertDateToStringDate(
								passengerInformationData.getDocumentExpiryDate(), TravelservicesConstants.DATE_PATTERN) : null);
				apiForm.setDocumentNumber(passengerInformationData.getDocumentNumber());
				apiForm.setDocumentType(passengerInformationData.getDocumentType() != null
						? passengerInformationData.getDocumentType().getCode() : null);
				apiForm.setNationality((passengerInformationData.getNationality() == null) ? null
						: passengerInformationData.getNationality().getIsocode());
			}

			model.addAttribute(API_FORM, apiForm);
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_ADVANCE_PASSENGER_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_ADVANCE_PASSENGER_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/my-account/advance-passenger", method = RequestMethod.POST)
	@RequireHardLogIn
	public String getAdvancePassenger(@ModelAttribute(API_FORM) final APIForm apiForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		apiFormValidator.validate(apiForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, TravelacceleratorstorefrontValidationConstants.FORM_GLOBAL_ERROR);
			return getAdvancePassenger(model);
		}

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		final CountryData countryOfIssue = new CountryData();
		countryOfIssue.setIsocode(apiForm.getCountryOfIssue());
		passengerInformationData.setCountryOfIssue(countryOfIssue);
		passengerInformationData.setDateOfBirth(
				TravelDateUtils.convertStringDateToDate(apiForm.getDateOfBirth(), TravelservicesConstants.DATE_PATTERN));
		passengerInformationData.setDocumentExpiryDate(
				TravelDateUtils.convertStringDateToDate(apiForm.getDocumentExpiryDate(), TravelservicesConstants.DATE_PATTERN));
		passengerInformationData.setDocumentNumber(apiForm.getDocumentNumber());
		passengerInformationData.setDocumentType(DocumentType.valueOf(apiForm.getDocumentType()));

		final CountryData nationality = new CountryData();
		nationality.setIsocode(apiForm.getNationality());
		passengerInformationData.setNationality(nationality);

		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode(ADULT);

		passengerInformationData.setPassengerType(passengerTypeData);

		final TitleData titleData = new TitleData();
		titleData.setCode(apiForm.getTitle());

		passengerInformationData.setTitle(titleData);
		passengerInformationData.setFirstName(apiForm.getFirstname());
		passengerInformationData.setSurname(apiForm.getLastname());
		passengerInformationData.setGender(apiForm.getGender());

		final TravellerData travellerData = new TravellerData();
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setLabel(ADULT);
		travellerData.setFormId("0");

		travellerFacade.updatePassengerInformation(travellerData);

		GlobalMessages.addInfoMessage(model, FORM_GLOBAL_CONFIRMATION);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_ADVANCE_PASSENGER_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_ADVANCE_PASSENGER_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return getAdvancePassenger(model);
	}

	@RequestMapping(value = "/my-account/saved-passengers", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getSavedPassengersPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(SAVED_TRAVELLLERS, travellerFacade.getSavedTravellersForCurrentUser());

		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_SAVED_PASSENGERS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_SAVED_PASSENGERS_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/my-account/remove-saved-passenger/{passengerUid}", method = RequestMethod.GET)
	public String getRemoveSavedPassengersPage(@PathVariable final String passengerUid, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (StringUtils.isBlank(passengerUid))
		{
			redirectModel.addFlashAttribute(ERROR, true);
			return REDIRECT_PREFIX + "/my-account/" + ACCOUNT_SAVED_PASSENGERS_CMS_PAGE;
		}

		final TravellerData removedTraveller = travellerFacade.removeSavedTraveller(passengerUid);

		if (removedTraveller == null)
		{
			redirectModel.addFlashAttribute(ERROR, true);
			return REDIRECT_PREFIX + "/my-account/" + ACCOUNT_SAVED_PASSENGERS_CMS_PAGE;
		}

		final PassengerInformationData passengerInformation = (PassengerInformationData) removedTraveller.getTravellerInfo();
		final String travellerName = passengerInformation.getTitle().getName() + " " + passengerInformation.getFirstName() + " "
				+ passengerInformation.getSurname();

		redirectModel.addFlashAttribute(REMOVED_TRAVELLER, travellerName);

		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_SAVED_PASSENGERS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_SAVED_PASSENGERS_CMS_PAGE));

		return REDIRECT_PREFIX + "/my-account/" + ACCOUNT_SAVED_PASSENGERS_CMS_PAGE;
	}
	/**
	 * @return the adultTitles
	 */
	protected String[] getAdultTitles()
	{
		return adultTitles;
	}

	/**
	 * @param adultTitles
	 *           the adultTitles to set
	 */
	@Required
	public void setAdultTitles(final String[] adultTitles)
	{
		this.adultTitles = adultTitles;
	}
}
