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
 *
 *
 */

package de.hybris.platform.travelcommons.controllers.page;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.forms.LeadGuestDetailsForm;
import de.hybris.platform.accommodationaddon.validators.GuestDetailsValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.ProfileData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.travelacceleratorstorefront.security.impl.B2BCheckOutAuthenticatonValidator;
import de.hybris.platform.traveladdon.forms.PassengerInformationForm;
import de.hybris.platform.traveladdon.forms.TravellerForm;
import de.hybris.platform.traveladdon.forms.validation.TravellerFormValidator;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelcommons.forms.cms.PersonalDetailsForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageFacade;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Personal Details page
 */
@Controller
@RequestMapping("/checkout/personal-details")
public class PersonalDetailsPageController extends TravelAbstractPageController
{
	private static final String PERSONAL_DETAILS_FORMS = "personalDetailsForms";
	private static final String ADULT_AGE_RANGE = "adultsAgeRange";
	private static final String CHILDREN_AGE_RANGE = "childrenAgeRange";
	private static final String INFANT_AGE_RANGE = "infantsAgeRange";
	private static final String TITLES = "titles";
	private static final String ADULT_TITLES = "adultsTitles";
	private static final String CHILDREN_TITLES = "childrenTitles";
	private static final String REASON_FOR_TRAVEL_OPTIONS = "reasonForTravelOptions";
	private static final String SAVED_TRAVELLERS = "savedTravellers";
	private static final String TRANSPORT_RESERVATION_DATA = "transportReservationData";
	private static final String TRAVELLERS_NAMES_MAP = "travellersNamesMap";

	private static final String HOME_PAGE_PATH = "/";

	private static final String PERSONAL_DETAILS_CMS_PAGE = "personalDetailsPage";
	private static final String HOURS = "hours";
	private static final String MINUTES = "minutes";
	private static final String PASSENGER_TYPE_QUANITY_MAP = "passengerTypeMaxQuantityMapPerRoom";
	private static final String DATE_PATTERN = "datePattern";

	private static final String DEFAULT_GUEST_CHECK_IN_TIME = " 12:00:00";

	// Populated through properties files
	private String[] adultAgesRange;
	private String[] childrenAgeRange;
	private String[] infantAgeRange;
	private String[] adultTitles;
	private String[] childrenTitles;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "packageFacade")
	private PackageFacade packageFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	@Resource(name = "travellerFormValidator")
	private TravellerFormValidator travellerFormValidator;

	@Resource(name = "b2BCheckOutAuthenticatonValidator")
	private B2BCheckOutAuthenticatonValidator b2BCheckOutAuthenticatonValidator;

	@Resource(name = "leadGuestDetailsFormsValidator")
	private GuestDetailsValidator leadGuestDetailsFormsValidator;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;


	/**
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String getPersonalDetailsPage(final Model model, final RedirectAttributes redirectAttributes,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);
		if (getTravelCustomerFacade().isCurrentUserB2bCustomer())
		{
			final ValidationResults validationResult = b2BCheckOutAuthenticatonValidator.validate(redirectAttributes);
			if (!ValidationResults.SUCCESS.getResult().equals(validationResult.getResult()))
			{
				return REDIRECT_PREFIX + HOME_PAGE_PATH;
			}
		}

		if (!packageFacade.isPackageInCart())
		{
			return REDIRECT_PREFIX + HOME_PAGE_PATH;
		}

		final List<TravellerData> travellers = travellerFacade.getTravellersForCartEntries();

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
		model.addAttribute(TRANSPORT_RESERVATION_DATA, reservationData);

		final AccommodationReservationData accommodationReservationData = bookingFacade
				.getAccommodationReservationDataForGuestDetailsFromCart();

		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_RESERVATION_DATA, accommodationReservationData);
		if (!model.containsAttribute(PERSONAL_DETAILS_FORMS))
		{
			model.addAttribute(PERSONAL_DETAILS_FORMS,
					getPersonalDetailsForm(travellers, accommodationReservationData.getRoomStays(), model));
		}
		final Map<Integer, Map<String, Integer>> passengerTypeMaxQuantityMapPerRoom = getSessionService()
				.getAttribute(PASSENGER_TYPE_QUANITY_MAP);
		model.addAttribute(PASSENGER_TYPE_QUANITY_MAP, passengerTypeMaxQuantityMapPerRoom);
		model.addAttribute(DATE_PATTERN, TravelservicesConstants.DATE_PATTERN);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.AMEND, getTravelCartFacade().isAmendmentCart());
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_PACKAGE);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_PACKAGE);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(PERSONAL_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PERSONAL_DETAILS_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}


	protected void initializeGuestDetails(final PersonalDetailsForm personalDetailsForm,
			final List<ReservedRoomStayData> reservedRoomStays)
	{
		final List<LeadGuestDetailsForm> leadForms = new ArrayList<>();
		final Map<Integer, Map<String, Integer>> passengerTypeMaxQuantityMapPerRoom = new HashMap<>();
		reservedRoomStays.forEach(reservedRoomStayData -> {
			final LeadGuestDetailsForm leadGuestDetailsForm = new LeadGuestDetailsForm();
			leadGuestDetailsForm.setGuestData(CollectionUtils.isNotEmpty(reservedRoomStayData.getReservedGuests())
					? reservedRoomStayData.getReservedGuests().get(0) : null);
			leadGuestDetailsForm
					.setPassengerTypeQuantityData(getPassengerTypeQuantityData(reservedRoomStayData.getRoomStayRefNumber(),
							passengerTypeMaxQuantityMapPerRoom, reservedRoomStayData.getGuestCounts()));
			leadGuestDetailsForm.setArrivalTime(reservedRoomStayData.getArrivalTime());
			leadForms.add(leadGuestDetailsForm);
		});
		getSessionService().setAttribute(PASSENGER_TYPE_QUANITY_MAP, passengerTypeMaxQuantityMapPerRoom);
		personalDetailsForm.setLeadForms(leadForms);
	}

	protected List<PassengerTypeQuantityData> getPassengerTypeQuantityData(final int roomRefNumber,
			final Map<Integer, Map<String, Integer>> passengerTypeMaxQuantityMapPerRoom,
			final List<PassengerTypeQuantityData> guestCounts)
	{

		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<GuestOccupancyData> guestOccupancies = bookingFacade.getGuestOccupanciesFromCart(roomRefNumber);
		final List<PassengerTypeData> passengerTypes = passengerTypeFacade.getPassengerTypes();
		final Map<String, Integer> passengerTypeMaxQuantityMap = new HashMap<>();
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		final int maxNumberOfGuests = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
		populatePassengerTypeQuantityList(passengerTypeQuantityList, guestOccupancies, guestCounts, passengerTypeMaxQuantityMap,
				true);
		if (CollectionUtils.isNotEmpty(guestOccupancies))
		{
			if (passengerTypes.size() != guestOccupancies.size())
			{
				final List<String> passengerCodes = new ArrayList<>();
				guestOccupancies.forEach(go -> passengerCodes.add(go.getPassengerType().getCode()));
				final List<PassengerTypeData> passengerTypeList = passengerTypes.stream()
						.filter(pt -> !passengerCodes.contains(pt.getCode())).collect(Collectors.toList());
				passengerTypeList.forEach(pt -> {
					final GuestOccupancyData guestOccupancyData = new GuestOccupancyData();
					guestOccupancyData.setPassengerType(pt);
					guestOccupancyData.setQuantityMin(
							StringUtils.equals(TravelacceleratorstorefrontValidationConstants.PASSENGER_TYPE_ADULT, pt.getCode())
									? TravelfacadesConstants.DEFAULT_QUANTITY_FOR_ADULTS
									: TravelfacadesConstants.DEFAULT_QUANTITY_FOR_NON_ADULTS);
					guestOccupancyData.setQuantityMax(maxNumberOfGuests);
					guestOccupancies.add(guestOccupancyData);
					guestOccupancies.sort(Comparator.comparing(p -> p.getPassengerType().getCode()));
				});

			}
			populatePassengerTypeQuantityList(passengerTypeQuantityList, guestOccupancies, guestCounts, passengerTypeMaxQuantityMap,
					false);
		}
		else
		{
			final List<PassengerTypeData> sortedPassengerTypes = travellerSortStrategy.sortPassengerTypes(passengerTypes);
			for (final PassengerTypeData passengerTypeData : sortedPassengerTypes)
			{
				final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
				passengerTypeQuantityData.setPassengerType(passengerTypeData);
				passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
				passengerTypeMaxQuantityMap.put(passengerTypeData.getCode(), maxNumberOfGuests);
				passengerTypeQuantityList.add(passengerTypeQuantityData);
			}
		}
		passengerTypeMaxQuantityMapPerRoom.put(roomRefNumber, passengerTypeMaxQuantityMap);
		return passengerTypeQuantityList;
	}

	protected void populatePassengerTypeQuantityList(final List<PassengerTypeQuantityData> passengerTypeQuantityList,
			final List<GuestOccupancyData> guestOccupancies, final List<PassengerTypeQuantityData> guestCounts,
			final Map<String, Integer> passengerTypeMaxQuantityMap, final boolean forceMaxQuantity)
	{
		for (final GuestOccupancyData guestOccupancy : guestOccupancies)
		{
			if (passengerTypeMaxQuantityMap.containsKey(guestOccupancy.getPassengerType().getCode()))
			{
				continue;
			}
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(guestOccupancy.getPassengerType());
			if (CollectionUtils.isNotEmpty(guestCounts))
			{
				passengerTypeQuantityData.setQuantity(
						guestCounts.stream().filter(guestCount -> StringUtils.equals(guestCount.getPassengerType().getCode(),
								guestOccupancy.getPassengerType().getCode())).findFirst().get().getQuantity());
			}
			else
			{
				passengerTypeQuantityData.setQuantity(
						forceMaxQuantity ? guestOccupancy.getQuantityMax() : TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
			}
			passengerTypeMaxQuantityMap.put(guestOccupancy.getPassengerType().getCode(), guestOccupancy.getQuantityMax());

			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}
	}

	protected void populateGuestDetails(final PersonalDetailsForm personalDetailsForm,
			final List<ReservedRoomStayData> reservedRoomStays)
	{
		final String checkInDate = TravelDateUtils.convertDateToStringDate(reservedRoomStays.get(0).getCheckInDate(),
				TravelservicesConstants.DATE_PATTERN) + DEFAULT_GUEST_CHECK_IN_TIME;
		final Map<Integer, Map<String, Integer>> passengerTypeMaxQuantityMapPerRoom = new HashMap<>();
		final List<LeadGuestDetailsForm> leadForms = new ArrayList<>();
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		reservedRoomStays.forEach(reservedRoomStayData -> {
			final LeadGuestDetailsForm leadGuestDetailsForm = new LeadGuestDetailsForm();
			leadGuestDetailsForm.setGuestData(CollectionUtils.isNotEmpty(reservedRoomStayData.getReservedGuests())
					? reservedRoomStayData.getReservedGuests().get(0) : null);
			leadGuestDetailsForm.setRoomPreferenceCodes(CollectionUtils.isNotEmpty(reservedRoomStayData.getRoomPreferences())
					? Stream.of(reservedRoomStayData.getRoomPreferences().get(0).getCode()).collect(Collectors.toList()) : null);
			leadGuestDetailsForm
					.setPassengerTypeQuantityData(getPassengerTypeQuantityData(reservedRoomStayData.getRoomStayRefNumber(),
							passengerTypeMaxQuantityMapPerRoom, reservedRoomStayData.getGuestCounts()));

			leadGuestDetailsForm.setArrivalTime(checkInDate);
			leadGuestDetailsForm.setRoomStayRefNumber(reservedRoomStayData.getRoomStayRefNumber().toString());
			leadGuestDetailsForm.setFormId(Integer.valueOf(reservedRoomStays.indexOf(reservedRoomStayData)).toString());
			leadGuestDetailsForm.setNotRemoved(Boolean.FALSE);
			leadForms.add(leadGuestDetailsForm);
		});
		personalDetailsForm.setLeadForms(leadForms);
	}

	@RequestMapping(value = "/validate-personal-details-forms", method = RequestMethod.POST)
	public String validatePersonalDetailsForm(@Valid final PersonalDetailsForm personalDetailsForm,
			final BindingResult bindingResult, final Model model)
	{
		travellerFormValidator.validate(personalDetailsForm.getTravellerForms(), bindingResult);

		if (personalDetailsForm.isUseDiffLeadDetails())
		{
			leadGuestDetailsFormsValidator.validate(personalDetailsForm.getLeadForms(), bindingResult);
		}
		return getValidationErrorMessage(bindingResult, model);
	}

	protected String getValidationErrorMessage(final BindingResult bindingResult, final Model model)
	{
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(AccommodationaddonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(AccommodationaddonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}
		return TravelcommonsControllerConstants.Views.Pages.FormErrors.FormErrorsResponse;
	}


	/**
	 * Save personal details string.
	 *
	 * @param personalDetailsForms
	 * 		the personal details forms
	 * @param bindingResult
	 * 		the binding result
	 * @param model
	 * 		the model
	 * @param redirectModel
	 * 		the redirect model
	 * @param request
	 * 		the request
	 * @param response
	 * 		the response
	 * @return the string
	 * @throws CMSItemNotFoundException
	 * 		the cms item not found exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String savePersonalDetails(
			@Valid @ModelAttribute(PERSONAL_DETAILS_FORMS) final PersonalDetailsForm personalDetailsForms,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{

		travellerFormValidator.validate(personalDetailsForms.getTravellerForms(), bindingResult);

		if (bindingResult.hasErrors())
		{
			return getPersonalDetailsPage(model, redirectModel, request, response);
		}

		if (!personalDetailsForms.isUseDiffLeadDetails())
		{
			useTravellerDetailsForAccommodationBooking(personalDetailsForms);
		}
		else
		{
			leadGuestDetailsFormsValidator.validate(personalDetailsForms.getLeadForms(), bindingResult);
			if (bindingResult.hasErrors())
			{
				return getPersonalDetailsPage(model, redirectModel, request, response);
			}
		}

		final List<TravellerData> travellerData = new ArrayList<>();

		personalDetailsForms.getTravellerForms().forEach(tf -> {

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
			passengerInformationData.setMembershipNumber(passengerInformation.getFrequentFlyerMembershipNumber());
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

		for (final LeadGuestDetailsForm lf : personalDetailsForms.getLeadForms())
		{

			final Boolean isSaved = bookingFacade.updateAccommodationOrderEntryGroup(Integer.parseInt(lf.getRoomStayRefNumber()),
					lf.getGuestData(), lf.getPassengerTypeQuantityData(), Collections.emptyList(), lf.getArrivalTime());

			if (!isSaved)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"system.error.guest.data.not.saved", null);
				return getPersonalDetailsPage(model, redirectModel, request, response);
			}
		}

		return nextPage();
	}

	protected void useTravellerDetailsForAccommodationBooking(final PersonalDetailsForm personalDetailsForm)
	{
		final AccommodationReservationData accommodationReservationData = bookingFacade
				.getAccommodationReservationDataForGuestDetailsFromCart();
		populateGuestDetails(personalDetailsForm, accommodationReservationData.getRoomStays());
		for (int roomCount = 0; roomCount < CollectionUtils.size(personalDetailsForm.getLeadForms()); roomCount++)
		{
			final LeadGuestDetailsForm leadForm = personalDetailsForm.getLeadForms().get(roomCount);
			final PassengerInformationForm travellerDetails = personalDetailsForm.getTravellerForms().get(roomCount)
					.getPassengerInformation();
			final ProfileData guestProfileDetails = leadForm.getGuestData().getProfile();
			guestProfileDetails.setFirstName(travellerDetails.getFirstname());
			guestProfileDetails.setLastName(travellerDetails.getLastname());
			guestProfileDetails.setEmail(travellerDetails.getEmail());
			if (roomCount == 0)
			{
				guestProfileDetails.setContactNumber(travellerDetails.getContactNumber());
			}
		}
	}

	/**
	 * Redirects user to the next checkout page which is payment details
	 *
	 * @return payment details page or payment type page
	 */
	protected String nextPage()
	{
		if (getTravelCustomerFacade().isCurrentUserB2bCustomer())
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_TYPE_PATH;
		}

		final String paymentFlowProperty = getConfigurationService().getConfiguration().getString("payment.flow");
		if (StringUtils.isNotBlank(paymentFlowProperty))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
		}
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH
				+ TravelacceleratorstorefrontWebConstants.PAYMENT_FLOW;
	}


	protected PersonalDetailsForm getPersonalDetailsForm(final List<TravellerData> travellers,
			final List<ReservedRoomStayData> reservedRoomStays, final Model model)
	{
		final PersonalDetailsForm personalDetailsForm = new PersonalDetailsForm();
		initializeTravellerDetails(travellers, personalDetailsForm);
		initializeGuestDetails(personalDetailsForm, reservedRoomStays);
		return personalDetailsForm;
	}

	protected void initializeTravellerDetails(final List<TravellerData> travellers, final PersonalDetailsForm personalDetailsForm)
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
		final boolean isAddNewRoomPackage = travelCartFacade.isAmendmentCart() && accommodationCartFacade.isNewRoomInCart();
		if (isAddNewRoomPackage)
		{
			personalDetailsForm.setUseDiffLeadDetails(Boolean.TRUE);
			travellerForms.get(0).getPassengerInformation().setValidateContactNumber(Boolean.FALSE);
		}

		personalDetailsForm.setTravellerForms(travellerForms);
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

	@ModelAttribute(MINUTES)
	public List<String> populateMinutes()
	{
		final List<String> minutes = new ArrayList<>();
		for (int i = 0; i <= 59; i++)
		{

			minutes.add(i < 10 ? "0" + i : String.valueOf(i));

		}
		return minutes;
	}

	@ModelAttribute(HOURS)
	public List<String> populateHours()
	{
		final List<String> hours = new ArrayList<>();
		for (int i = 0; i <= 23; i++)
		{
			hours.add(i < 10 ? "0" + i : String.valueOf(i));

		}
		return hours;
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
	@Required
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
	@Required
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
	@Required
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
	@Required
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
	@Required
	public void setChildrenTitles(final String[] childrenTitles)
	{
		this.childrenTitles = childrenTitles;
	}
}
