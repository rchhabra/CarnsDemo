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

package de.hybris.platform.accommodationaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.GuestDetails;
import de.hybris.platform.accommodationaddon.forms.LeadGuestDetailsForm;
import de.hybris.platform.accommodationaddon.validators.GuestDetailsValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AvailableServiceData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.travelacceleratorstorefront.security.impl.B2BCheckOutAuthenticatonValidator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.TravelI18NFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationExtrasFacade;
import de.hybris.platform.travelfacades.facades.accommodation.RoomPreferenceFacade;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Guest Details page
 */
@Controller
@RequestMapping("/checkout/guest-details")
public class GuestDetailsPageController extends TravelAbstractPageController
{
	private static final Logger LOG = Logger.getLogger(GuestDetailsPageController.class);
	private static final String GUEST_DETAILS_CMS_PAGE = "guestDetailsPage";
	private static final String LEAD_GUEST_DETAILS_FORMS = "leadGuestDetailsForms";
	private static final String COUNTRY_CODES = "countries";
	private static final String HOURS = "hours";
	private static final String MINUTES = "minutes";
	private static final String GUEST_QUANTITY = "guestQuantity";
	private static final String HOME_PAGE_PATH = "/";
	private static final String PASSENGER_TYPE_QUANITY_MAP = "passengerTypeMaxQuantityMapPerRoom";
	private static final String DATE_PATTERN = "datePattern";
	private static final String ACCOMMODATION_DETAILS_PAGE = "/manage-booking/accommodation-details/";
	private static final String FAILED = "FAILED";
	private static final String SUCCESS = "SUCCESS";
	private static final String REDIRECT = "REDIRECT";
	private static final String REMOVE_ROOM_RESULT = "removeRoomResult";
	private static final String BOOKING_DETAILS_URL = "/manage-booking/booking-details/";

	@Resource(name = "accommodationExtrasFacade")
	private AccommodationExtrasFacade accommodationExtrasFacade;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	@Resource(name = "leadGuestDetailsFormsValidator")
	private GuestDetailsValidator leadGuestDetailsFormsValidator;

	@Resource(name = "bookingFacade")
	BookingFacade bookingFacade;

	@Resource(name = "roomPreferenceFacade")
	RoomPreferenceFacade roomPreferenceFacade;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "travelI18NFacade")
	private TravelI18NFacade travelI18NFacade;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "b2BCheckOutAuthenticatonValidator")
	private B2BCheckOutAuthenticatonValidator b2BCheckOutAuthenticatonValidator;

	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String getGuestDetailsPage(@RequestParam(value = "roomStay", required = false) final Integer roomStayRefNumberToUpdate,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		if (getTravelCustomerFacade().isCurrentUserB2bCustomer())
		{
			final ValidationResults validationResult = b2BCheckOutAuthenticatonValidator.validate(redirectAttributes);
			if (!ValidationResults.SUCCESS.getResult().equals(validationResult.getResult()))
			{
				return REDIRECT_PREFIX + HOME_PAGE_PATH;
			}

		}

		if (!travelCartFacade.isCurrentCartValid())
		{
			return REDIRECT_PREFIX + HOME_PAGE_PATH;
		}

		if (!bookingFacade.isCurrentCartOfType(OrderEntryType.ACCOMMODATION.getCode()))
		{
			return REDIRECT_PREFIX + HOME_PAGE_PATH;
		}

		final AccommodationReservationData accommodationReservationData = bookingFacade
				.getAccommodationReservationDataForGuestDetailsFromCart();
		if (travelCartFacade.isAmendmentCart())
		{
			// During amendments, if there are any new rooms added, it means that Add Room amendment is performed and therefore we
			// only want to allow users to modify details about these new rooms
			final List<ReservedRoomStayData> newReservedRoomStays = bookingFacade.getNewReservedRoomStays();
			if (CollectionUtils.isNotEmpty(newReservedRoomStays))
			{
				accommodationReservationData.setRoomStays(newReservedRoomStays);
			}
		}

		if (Objects.nonNull(roomStayRefNumberToUpdate))
		{
			final Optional<ReservedRoomStayData> roomStayToUpdate = accommodationReservationData.getRoomStays().stream()
					.filter(roomStay -> roomStayRefNumberToUpdate.equals(roomStay.getRoomStayRefNumber())).findAny();
			if (roomStayToUpdate.isPresent())
			{
				accommodationReservationData.setRoomStays(Collections.singletonList(roomStayToUpdate.get()));
			}
		}

		final List<AvailableServiceData> availableServices = accommodationExtrasFacade
				.getAvailableServices(accommodationReservationData);

		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_RESERVATION_DATA, accommodationReservationData);
		model.addAttribute(AccommodationaddonWebConstants.AVAILABLE_SERVICES, availableServices);
		model.addAttribute(LEAD_GUEST_DETAILS_FORMS, initializeGuestDetails(accommodationReservationData.getRoomStays(), model));
		model.addAttribute(COUNTRY_CODES, travelI18NFacade.getAllCountries());
		model.addAttribute(DATE_PATTERN, TravelservicesConstants.DATE_PATTERN);

		final Map<Integer, List<RoomPreferenceData>> accommodationRoomPreferenceMap = createAccommodationRoomPreferenceMap(
				accommodationReservationData);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_ROOM_PREFERENCE_MAP, accommodationRoomPreferenceMap);

		model.addAttribute(AccommodationaddonWebConstants.AMEND_EXTRAS, accommodationCartFacade.isAmendmentForServices());

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		}

		model.addAttribute(TravelacceleratorstorefrontWebConstants.AMEND, travelCartFacade.isAmendmentCart());

		storeCmsPageInModel(model, getContentPageForLabelOrId(GUEST_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUEST_DETAILS_CMS_PAGE));
		return getViewForPage(model);
	}

	protected Map<Integer, List<RoomPreferenceData>> createAccommodationRoomPreferenceMap(
			final AccommodationReservationData accommodationReservationData)
	{
		final Map<Integer, List<RoomPreferenceData>> baseAccommodationRoomPreferenceMap = accommodationReservationData
				.getRoomStays()
				.stream().collect(Collectors.toMap(RoomStayData::getRoomStayRefNumber, roomStay -> roomPreferenceFacade
						.getRoomPreferencesForTypeAndAccommodation(AccommodationaddonWebConstants.ACCOMMODATION_ROOM_PREFERENCE_TYPE,
								roomStay.getRoomTypes())));

		final OptionalInt min = baseAccommodationRoomPreferenceMap.keySet().stream().mapToInt(Integer::intValue).min();
		if (!min.isPresent())
		{
			return baseAccommodationRoomPreferenceMap;
		}

		final Map<Integer, List<RoomPreferenceData>> finalAccommodationRoomPreferenceMap = new HashMap<>();
		baseAccommodationRoomPreferenceMap
				.forEach((key, value) -> finalAccommodationRoomPreferenceMap.put(key - min.getAsInt(), value));

		return finalAccommodationRoomPreferenceMap;
	}

	protected GuestDetails initializeGuestDetails(final List<ReservedRoomStayData> reservedRoomStays, final Model model)
	{
		final GuestDetails guestDetails = new GuestDetails();
		final List<LeadGuestDetailsForm> leadForms = new ArrayList<>();
		final Map<Integer, Map<String, Integer>> passengerTypeMaxQuantityMapPerRoom = new HashMap<>();
		reservedRoomStays.forEach(reservedRoomStayData ->
		{
			final LeadGuestDetailsForm leadGuestDetailsForm = new LeadGuestDetailsForm();
			leadGuestDetailsForm.setGuestData(CollectionUtils.isNotEmpty(reservedRoomStayData.getReservedGuests())
					? reservedRoomStayData.getReservedGuests().get(0) : null);
			leadGuestDetailsForm.setRoomPreferenceCodes(CollectionUtils.isNotEmpty(reservedRoomStayData.getRoomPreferences())
					? Stream.of(reservedRoomStayData.getRoomPreferences().get(0).getCode()).collect(Collectors.toList()) : null);
			leadGuestDetailsForm
					.setPassengerTypeQuantityData(getPassengerTypeQuantityData(reservedRoomStayData.getRoomStayRefNumber(),
							passengerTypeMaxQuantityMapPerRoom, model, reservedRoomStayData.getGuestCounts()));
			leadGuestDetailsForm.setArrivalTime(reservedRoomStayData.getArrivalTime());
			leadForms.add(leadGuestDetailsForm);
		});
		guestDetails.setLeadForms(leadForms);
		return guestDetails;
	}

	protected List<PassengerTypeQuantityData> getPassengerTypeQuantityData(final int roomRefNumber,
			final Map<Integer, Map<String, Integer>> passengerTypeMaxQuantityMapPerRoom, final Model model,
			final List<PassengerTypeQuantityData> guestCounts)
	{

		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<GuestOccupancyData> guestOccupancies = bookingFacade.getGuestOccupanciesFromCart(roomRefNumber);
		final List<PassengerTypeData> passengerTypes = passengerTypeFacade.getPassengerTypes();
		final Map<String, Integer> passengerTypeMaxQuantityMap = new HashMap<>();
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
		if (CollectionUtils.isNotEmpty(guestOccupancies))
		{
			if (passengerTypes.size() != guestOccupancies.size())
			{
				final List<String> passengerCodes = new ArrayList<>();
				guestOccupancies.forEach(go -> passengerCodes.add(go.getPassengerType().getCode()));
				final List<PassengerTypeData> passengerTypeList = passengerTypes.stream()
						.filter(pt -> !passengerCodes.contains(pt.getCode())).collect(Collectors.toList());
				passengerTypeList.forEach(pt ->
				{
					final GuestOccupancyData guestOccupancyData = new GuestOccupancyData();
					guestOccupancyData.setPassengerType(pt);
					guestOccupancyData.setQuantityMin(
							StringUtils.equals(TravelacceleratorstorefrontValidationConstants.PASSENGER_TYPE_ADULT, pt.getCode())
									? TravelfacadesConstants.DEFAULT_QUANTITY_FOR_ADULTS
									: TravelfacadesConstants.DEFAULT_QUANTITY_FOR_NON_ADULTS);
					guestOccupancyData.setQuantityMax(maxGuestQuantity);
					guestOccupancies.add(guestOccupancyData);
					guestOccupancies.sort((p1, p2) -> p1.getPassengerType().getCode().compareTo(p2.getPassengerType().getCode()));
				});

			}
			for (final GuestOccupancyData guestOccupancy : guestOccupancies)
			{
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
					passengerTypeQuantityData
							.setQuantity(StringUtils.equals(TravelacceleratorstorefrontValidationConstants.PASSENGER_TYPE_ADULT,
									guestOccupancy.getPassengerType().getCode()) ? guestOccupancy.getQuantityMax()
									: TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
				}
				passengerTypeMaxQuantityMap.put(guestOccupancy.getPassengerType().getCode(), guestOccupancy.getQuantityMax());

				passengerTypeQuantityList.add(passengerTypeQuantityData);
			}
		}
		else
		{
			final List<PassengerTypeData> sortedPassengerTypes = travellerSortStrategy.sortPassengerTypes(passengerTypes);
			for (final PassengerTypeData passengerTypeData : sortedPassengerTypes)
			{
				final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
				passengerTypeQuantityData.setPassengerType(passengerTypeData);
				passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
				passengerTypeMaxQuantityMap.put(passengerTypeData.getCode(),
						configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney)));
				passengerTypeQuantityList.add(passengerTypeQuantityData);
			}
		}
		passengerTypeMaxQuantityMapPerRoom.put(roomRefNumber, passengerTypeMaxQuantityMap);
		model.addAttribute(PASSENGER_TYPE_QUANITY_MAP, passengerTypeMaxQuantityMapPerRoom);
		return passengerTypeQuantityList;
	}

	@RequestMapping(value = "/validate-lead-guest-details-forms", method = RequestMethod.POST)
	public String validateFareFinderForm(@Valid final GuestDetails guestDetails, final BindingResult bindingResult,
			final Model model)
	{
		guestDetails.setLeadForms(filterOutRemovedRoom(guestDetails.getLeadForms()));
		leadGuestDetailsFormsValidator.validate(guestDetails.getLeadForms(), bindingResult);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(AccommodationaddonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(AccommodationaddonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}
		return AccommodationaddonControllerConstants.Views.Pages.FormErrors.FormErrorsResponse;
	}

	protected List<LeadGuestDetailsForm> filterOutRemovedRoom(final List<LeadGuestDetailsForm> leadForms)
	{
		if (CollectionUtils.isEmpty(leadForms))
		{
			return Collections.emptyList();
		}

		final List<LeadGuestDetailsForm> filteredLeadGuestDetailsForm = new ArrayList<LeadGuestDetailsForm>();
		leadForms.forEach(lf ->
		{
			if (lf.isNotRemoved())
			{
				filteredLeadGuestDetailsForm.add(lf);
			}
		});
		return filteredLeadGuestDetailsForm;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveGuestDetails(@Valid @ModelAttribute(LEAD_GUEST_DETAILS_FORMS) final GuestDetails guestDetails,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		guestDetails.setLeadForms(filterOutRemovedRoom(guestDetails.getLeadForms()));
		leadGuestDetailsFormsValidator.validate(guestDetails.getLeadForms(), bindingResult);


		if (bindingResult.hasErrors())
		{
			return getGuestDetailsPage(
					CollectionUtils.size(guestDetails.getLeadForms()) == 1
							? Integer.parseInt(guestDetails.getLeadForms().stream().findFirst().get().getRoomStayRefNumber()) : null,
					model, redirectModel);
		}

		for (final LeadGuestDetailsForm lf : guestDetails.getLeadForms())
		{
			final Boolean isSaved = bookingFacade.updateAccommodationOrderEntryGroup(Integer.parseInt(lf.getRoomStayRefNumber()),
					lf.getGuestData(), lf.getPassengerTypeQuantityData(), lf.getRoomPreferenceCodes(), lf.getArrivalTime());

			if (!isSaved)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"system.error.guest.data.not.saved", null);
				return getGuestDetailsPage(
						CollectionUtils.size(guestDetails.getLeadForms()) == 1
								? Integer.parseInt(guestDetails.getLeadForms().stream().findFirst().get().getRoomStayRefNumber()) : null,
						model, redirectModel);
			}
		}

		return nextPage();
	}

	@ModelAttribute(COUNTRY_CODES)
	public List<String> populateCountryCodes()
	{
		final List<String> countryCodes = new ArrayList<>();
		for (int i = 1; i <= 59; i++)
		{
			final String code = (i < 10 ? "0" + i : String.valueOf(i));
			countryCodes.add("\u002B" + code);

		}
		return countryCodes;
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

	public List<String> populateGuestsQuantity(final int maxGuestQuantity)
	{
		final List<String> guestsQuantity = new ArrayList<>();
		for (int i = 0; i <= maxGuestQuantity; i++)
		{
			guestsQuantity.add(String.valueOf(i));
		}
		return guestsQuantity;
	}

	@ModelAttribute(GUEST_QUANTITY)
	public List<String> populatePassengersQuantity()
	{
		final List<String> guestsQuantity = new ArrayList<>();
		final String attribute = sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(attribute));
		for (int i = 0; i <= maxGuestQuantity; i++)
		{
			guestsQuantity.add(String.valueOf(i));
		}
		return guestsQuantity;
	}


	/**
	 * Redirects user to the next checkout page which is payment details
	 *
	 * @return payment details page or payment type page
	 */
	protected String nextPage()
	{
		if (travelCartFacade.isAmendmentCart())
		{
			if (!travelCartFacade.hasCartBeenAmended())
			{
				return REDIRECT_PREFIX + BOOKING_DETAILS_URL + travelCartFacade.getOriginalOrderCode();
			}
		}
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

	@RequestMapping(value = "/remove-room/{roomStayReference}", method = RequestMethod.GET, produces = "application/json")
	public String removeRoom(@PathVariable("roomStayReference") final String roomStayReference, final Model model)
	{
		String result = FAILED;
		String resultInfo = AccommodationaddonWebConstants.REMOVE_ROOM_VALIDATION_ERROR;
		try
		{
			final int roomStayRef = Integer.parseInt(roomStayReference);
			if (roomStayRef > 0)
			{
				final Boolean wasRemoved = accommodationCartFacade.removeAccommodationOrderEntryGroup(roomStayRef);
				if (wasRemoved)
				{
					result = SUCCESS;
					resultInfo = AccommodationaddonWebConstants.REMOVE_ROOM_CONFIRMATION;
				}
			}
		}
		catch (final NumberFormatException e)
		{
			LOG.debug("Unable to parse int from" + roomStayReference, e);
		}

		if (CollectionUtils.isEmpty(bookingFacade.getNewAccommodationOrderEntryGroupRefs()))
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
			final String queryString = bookingFacade.buildAccommodationDetailsQueryFromCart();
			result = REDIRECT;
			resultInfo = ACCOMMODATION_DETAILS_PAGE + queryString;
		}
		model.addAttribute(REMOVE_ROOM_RESULT, Collections.singletonMap(result, resultInfo));
		return AccommodationaddonControllerConstants.Views.Pages.Hotel.removeRoomJsonResponse;
	}
}
