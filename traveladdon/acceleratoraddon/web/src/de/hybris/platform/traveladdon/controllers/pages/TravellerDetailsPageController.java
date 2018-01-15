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
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.travelacceleratorstorefront.security.impl.B2BCheckOutAuthenticatonValidator;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.PassengerInformationForm;
import de.hybris.platform.traveladdon.forms.TravellerDetails;
import de.hybris.platform.traveladdon.forms.TravellerForm;
import de.hybris.platform.traveladdon.forms.validation.TravellerFormValidator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Traveller Details page
 */
@Controller
@RequestMapping("/checkout/traveller-details")
public class TravellerDetailsPageController extends TravelAbstractPageController
{
	private final String TRAVELLER_DETAILS_CMS_PAGE = "travellerDetailsPage";
	private final String TRAVELLER_DETAILS_FORMS = "travellerDetailsForms";
	private final String ADULT_AGE_RANGE = "adultsAgeRange";
	private final String CHILDREN_AGE_RANGE = "childrenAgeRange";
	private final String INFANT_AGE_RANGE = "infantsAgeRange";
	private final String TITLES = "titles";
	private final String ADULT_TITLES = "adultsTitles";
	private final String CHILDREN_TITLES = "childrenTitles";
	private final String REASON_FOR_TRAVEL_OPTIONS = "reasonForTravelOptions";
	private final String SAVED_TRAVELLERS = "savedTravellers";
	private final String RESERVATION_DATA = "reservationData";
	private final String SHOW_ADDITIONAL_SECURITY = "showAdditionalSecurity";
	private static final String TRAVELLERS_NAMES_MAP = "travellersNamesMap";
	private static final String TRAVELLERS_PER_TYPE_MAP = "travellersPerTypeMap";

	// Populated through properties files
	private String[] adultAgesRange;
	private String[] childrenAgeRange;
	private String[] infantAgeRange;
	private String[] adultTitles;
	private String[] childrenTitles;

	private static final String NEXT_URL = "/checkout/traveller-details/next";
	private static final String HOME_PAGE_PATH = "/";

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "cartFacade")
	private TravelCartFacade cartFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "travellerFormValidator")
	private TravellerFormValidator travellerFormValidator;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "b2BCheckOutAuthenticatonValidator")
	private B2BCheckOutAuthenticatonValidator b2BCheckOutAuthenticatonValidator;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	/**
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String getTravellerDetailsPage(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		if (getTravelCustomerFacade().isCurrentUserB2bCustomer())
		{
			final ValidationResults validationResult = b2BCheckOutAuthenticatonValidator.validate(redirectAttributes);
			if (!ValidationResults.SUCCESS.getResult().equals(validationResult.getResult()))
			{
				return REDIRECT_PREFIX + HOME_PAGE_PATH;
			}

		}

		if (!cartFacade.isCurrentCartValid())
		{
			return REDIRECT_PREFIX + HOME_PAGE_PATH;
		}

		if (!bookingFacade.isCurrentCartOfType(OrderEntryType.TRANSPORT.getCode()))
		{
			return REDIRECT_PREFIX + HOME_PAGE_PATH;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(TRAVELLER_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRAVELLER_DETAILS_CMS_PAGE));
		// model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}

		final List<TravellerData> travellers = travellerFacade.getTravellersForCartEntries();
		if (!model.containsAttribute(TRAVELLER_DETAILS_FORMS))
		{
			model.addAttribute(TRAVELLER_DETAILS_FORMS, getTravellerForms(travellers));
		}

		final Map<String, TravellerDetails> travellerFormsPerType = new TreeMap<>();
		final List<PassengerTypeData> passengerTypes = travellerSortStrategy
				.sortPassengerTypes(passengerTypeFacade.getPassengerTypes());
		for (final PassengerTypeData passengerTypeData : passengerTypes)
		{

			final List<TravellerData> travellersPerType = travellers.stream()
					.filter(traveller -> passengerTypeData.getCode()
							.equals(((PassengerInformationData) traveller.getTravellerInfo()).getPassengerType().getCode()))
					.collect(Collectors.toList());

			travellerFormsPerType.put(passengerTypeData.getCode(), getTravellerForms(travellersPerType));
		}

		model.addAttribute(TRAVELLERS_PER_TYPE_MAP, travellerFormsPerType);
		model.addAttribute(SHOW_ADDITIONAL_SECURITY, !bookingFacade.isCurrentCartOfType(OrderEntryType.ACCOMMODATION.getCode()));
		model.addAttribute(TRAVELLERS_NAMES_MAP, travellerFacade.populateTravellersNamesMap(travellers));
		model.addAttribute(ADULT_AGE_RANGE, adultAgesRange);
		model.addAttribute(CHILDREN_AGE_RANGE, childrenAgeRange);
		model.addAttribute(INFANT_AGE_RANGE, infantAgeRange);
		model.addAttribute(TITLES, userFacade.getTitles());
		model.addAttribute(REASON_FOR_TRAVEL_OPTIONS, travellerFacade.getReasonForTravelTypes());
		model.addAttribute(SAVED_TRAVELLERS, travellerFacade.getSavedTravellersForCurrentUser());

		final List<TitleData> titles = userFacade.getTitles();

		model.addAttribute(ADULT_TITLES, getTravellerTitle(adultTitles, titles));
		model.addAttribute(CHILDREN_TITLES, getTravellerTitle(childrenTitles, titles));

		final ReservationData reservationData = reservationFacade.getCurrentReservationData();
		model.addAttribute(RESERVATION_DATA, reservationData);

		model.addAttribute(TraveladdonWebConstants.NEXT_URL, NEXT_URL);

		return getViewForPage(model);
	}

	/**
	 * @param travellerDetails
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String saveTravellerDetails(@Valid @ModelAttribute(TRAVELLER_DETAILS_FORMS) final TravellerDetails travellerDetails,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{

		travellerFormValidator.validate(travellerDetails.getTravellerForms(), bindingResult);

		if (bindingResult.hasErrors())
		{
			return getTravellerDetailsPage(model, redirectModel);
		}

		final List<TravellerData> travellerData = new ArrayList<>();

		travellerDetails.getTravellerForms().forEach(tf -> {

			final PassengerInformationForm passengerInformation = tf.getPassengerInformation();

			final PassengerInformationData passengerInformationData = new PassengerInformationData();

			final TitleData title = new TitleData();
			title.setCode(passengerInformation.getTitle());
			passengerInformationData.setTitle(title);
			passengerInformationData.setFirstName(passengerInformation.getFirstname());
			passengerInformationData.setSurname(passengerInformation.getLastname());
			passengerInformationData.setGender(passengerInformation.getGender());
			passengerInformationData.setReasonForTravel(passengerInformation.getReasonForTravel());
			passengerInformationData.setSaveDetails(passengerInformation.isSaveDetails());
			passengerInformationData.setEmail(passengerInformation.getEmail());

			if (passengerInformation.isFrequentFlyer())
			{
				passengerInformationData.setMembershipNumber(passengerInformation.getFrequentFlyerMembershipNumber());
			}

			if (StringUtils.isNotBlank(passengerInformation.getSelectedSavedTravellerUId()))
			{
				passengerInformationData.setSavedTravellerUId(passengerInformation.getSelectedSavedTravellerUId());
			}

			final PassengerTypeData passengerType = new PassengerTypeData();
			passengerType.setCode(passengerInformation.getPassengerTypeCode());

			passengerInformationData.setPassengerType(passengerType);

			final TravellerData traveller = new TravellerData();
			traveller.setFormId(tf.getFormId());
			traveller.setLabel(tf.getLabel());
			traveller.setBooker(tf.getBooker() == null ? false : tf.getBooker());
			traveller.setUid(tf.getUid());
			traveller.setTravellerInfo(passengerInformationData);

			if (tf.isSpecialAssistance())
			{
				final SpecialServiceRequestData disability = new SpecialServiceRequestData();
				disability.setCode("disability");

				final List<SpecialServiceRequestData> specialServiceRequests = new ArrayList<>();
				specialServiceRequests.add(disability);

				final SpecialRequestDetailData specialRequestDetail = new SpecialRequestDetailData();
				specialRequestDetail.setSpecialServiceRequests(specialServiceRequests);

				traveller.setSpecialRequestDetail(specialRequestDetail);
			}

			if (StringUtils.isNotBlank(tf.getSelectedSavedTravellerUId()))
			{
				traveller.setSavedTravellerUid(tf.getSelectedSavedTravellerUId());
			}

			travellerData.add(traveller);
		});

		travellerFacade.updateTravellerDetails(travellerData);
		cartFacade.setAdditionalSecurity(travellerDetails.getAdditionalSecurityActive());

		return nextPage();
	}


	/**
	 * Redirects user to the next checkout page which is payment details
	 *
	 * @return payment details page or payment type page
	 */
	protected String nextPage()
	{
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		if (getTravelCustomerFacade().isCurrentUserB2bCustomer())
		{
			if (StringUtils.isNotEmpty(sessionBookingJourney) && !StringUtils.equalsIgnoreCase(sessionBookingJourney,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
			{
				return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.GUEST_DETAILS_PATH;
			}
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_TYPE_PATH;
		}

		if (StringUtils.isNotEmpty(sessionBookingJourney) && !StringUtils.equalsIgnoreCase(sessionBookingJourney,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.GUEST_DETAILS_PATH;
		}

		final String paymentFlowProperty = getConfigurationService().getConfiguration().getString("payment.flow");
		if (StringUtils.isNotBlank(paymentFlowProperty))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
		}
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH
				+ TravelacceleratorstorefrontWebConstants.PAYMENT_FLOW;
	}

	/**
	 * Ajax controller which first checks if the user is an anonymous user and if not then it will get the users details
	 *
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = "/get-current-user-details", method = RequestMethod.GET)
	public String getCurrentUserDetails(final Model model)
	{
		final boolean isAnonymousUser = travellerFacade.isAnonymousUser();
		model.addAttribute("isAuthenticated", travellerFacade.isAnonymousUser() ? false : true);

		if (!isAnonymousUser)
		{
			final TravellerData travellerData = travellerFacade.getCurrentUserDetails();

			if (travellerData != null)
			{
				model.addAttribute("travellerData", travellerData);
			}
		}
		return TraveladdonControllerConstants.Views.Pages.TravellerDetails.JSONTravellerDetailsAuthentication;
	}

	@RequestMapping(value = "/validate-traveller-details-forms", method = RequestMethod.POST)
	public String validateTravellerDetailsForm(
			@Valid @ModelAttribute(TRAVELLER_DETAILS_FORMS) final TravellerDetails travellerDetails,
			final BindingResult bindingResult, final Model model)
	{
		travellerFormValidator.validate(travellerDetails.getTravellerForms(), bindingResult);

		return getValidationErrorMessage(bindingResult, model);
	}

	protected String getValidationErrorMessage(final BindingResult bindingResult, final Model model)
	{
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(TraveladdonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(TraveladdonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}
		return TraveladdonControllerConstants.Views.Pages.FormErrors.formErrorsResponse;
	}

	/**
	 * @param text
	 *           search text
	 * @param model
	 * @return json page
	 */
	@RequestMapping(value = "/suggestions/first-name", method = RequestMethod.GET, produces = "application/json")
	public String firstNameSuggestions(@RequestParam(value = "text") final String text,
			@RequestParam(value = "passengerType") final String passengerType, final Model model)
	{
		model.addAttribute(SAVED_TRAVELLERS, travellerFacade.findSavedTravellersUsingFirstName(text, passengerType));
		return TraveladdonControllerConstants.Views.Pages.Suggestions.JSONNamesSearchResponse;
	}

	/**
	 * @param text
	 *           search text
	 * @param model
	 * @return json page
	 */
	@RequestMapping(value = "/suggestions/last-name", method = RequestMethod.GET, produces = "application/json")
	public String surNameSuggestions(@RequestParam(value = "text") final String text,
			@RequestParam(value = "passengerType") final String passengerType, final Model model)
	{
		model.addAttribute(SAVED_TRAVELLERS, travellerFacade.findSavedTravellersUsingSurname(text, passengerType));
		return TraveladdonControllerConstants.Views.Pages.Suggestions.JSONNamesSearchResponse;
	}

	/**
	 * @return List<TravellerForm>
	 */
	private TravellerDetails getTravellerForms(final List<TravellerData> travellers)
	{
		final List<TravellerForm> travellerForms = new ArrayList<TravellerForm>();

		travellers.forEach(traveller -> {
			final PassengerInformationData passengerInformationData = (PassengerInformationData) traveller.getTravellerInfo();
			final PassengerInformationForm passengerInformationForm = new PassengerInformationForm();
			passengerInformationForm.setPassengerTypeCode(passengerInformationData.getPassengerType().getCode());
			passengerInformationForm.setPassengerTypeName(passengerInformationData.getPassengerType().getName());
			passengerInformationForm.setEmail(passengerInformationData.getEmail());
			passengerInformationForm.setFirstname(passengerInformationData.getFirstName());
			passengerInformationForm.setLastname(passengerInformationData.getSurname());
			passengerInformationForm.setGender(passengerInformationData.getGender());
			if (Objects.nonNull(passengerInformationData.getTitle()))
			{
				passengerInformationForm.setTitle(passengerInformationData.getTitle().getName());
			}
			passengerInformationForm.setReasonForTravel(passengerInformationData.getReasonForTravel());
			passengerInformationForm.setFrequentFlyerMembershipNumber(passengerInformationData.getMembershipNumber());
			passengerInformationForm.setFrequentFlyer(StringUtils.isNotEmpty(passengerInformationData.getMembershipNumber()));
			passengerInformationForm.setSelectedSavedTravellerUId(traveller.getSavedTravellerUid());

			final TravellerForm travellerForm = new TravellerForm();
			travellerForm.setLabel(traveller.getLabel());
			travellerForm.setUid(traveller.getUid());
			travellerForm.setSelectedSavedTravellerUId(traveller.getSavedTravellerUid());
			travellerForm.setPassengerInformation(passengerInformationForm);
			travellerForm.setSpecialAssistance(Objects.nonNull(traveller.getSpecialRequestDetail()));
			travellerForm.setBooker(traveller.isBooker());
			travellerForms.add(travellerForm);
		});

		Collections.sort(travellerForms);

		final TravellerDetails travellerDetails = new TravellerDetails();
		travellerDetails.setTravellerForms(travellerForms);

		travellerDetails.setAdditionalSecurityActive(cartFacade.isAdditionalSecurityActive());

		return travellerDetails;
	}

	protected List<TitleData> getTravellerTitle(final String[] travellerTitles, final List<TitleData> titles)
	{
		final List<String> tt = new ArrayList<>();
		Collections.addAll(tt, travellerTitles);
		return titles.stream().filter(t -> tt.contains(t.getCode())).collect(Collectors.toList());
	}

	protected void disableCachingForResponse(final HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	}

	/**
	 * @return Array of Age Ranges
	 */
	protected String[] getAdultAgesRange()
	{
		return adultAgesRange;
	}

	/**
	 * @param adultAgesRange
	 */
	public void setAdultAgesRange(final String[] adultAgesRange)
	{
		this.adultAgesRange = adultAgesRange;
	}

	/**
	 * @return Array of Age Ranges
	 */
	protected String[] getChildrenAgeRange()
	{
		return childrenAgeRange;
	}

	/**
	 * @param childrenAgeRange
	 */
	public void setChildrenAgeRange(final String[] childrenAgeRange)
	{
		this.childrenAgeRange = childrenAgeRange;
	}

	/**
	 * @return Array of Age Ranges
	 */
	public String[] getInfantAgeRange()
	{
		return infantAgeRange;
	}

	/**
	 * @param infantAgeRange
	 */
	public void setInfantAgeRange(final String[] infantAgeRange)
	{
		this.infantAgeRange = infantAgeRange;
	}

	/**
	 * @return Array of Age Ranges
	 */
	protected String[] getAdultTitles()
	{
		return adultTitles;
	}

	/**
	 * @param adultTitles
	 */
	public void setAdultTitles(final String[] adultTitles)
	{
		this.adultTitles = adultTitles;
	}

	/**
	 * @return Array of Age Ranges
	 */
	protected String[] getChildrenTitles()
	{
		return childrenTitles;
	}

	/**
	 * @param childrenTitles
	 */
	public void setChildrenTitles(final String[] childrenTitles)
	{
		this.childrenTitles = childrenTitles;
	}

}
