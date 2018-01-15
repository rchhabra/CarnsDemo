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

package de.hybris.platform.travelcommons.controllers.cms;

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationAvailabilityForm;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationReviewForm;
import de.hybris.platform.accommodationaddon.forms.cms.AddRequestForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationAvailabilityValidator;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.BookingActionRequestData;
import de.hybris.platform.commercefacades.travel.BookingActionResponseData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractBookingDetailsComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractBookingDetailsComponentModel;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.accommodation.impl.DefaultAccommodationOfferingCustomerReviewFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller("TravelBookingDetailsComponentController")
@RequestMapping(value = TravelcommonsControllerConstants.Actions.Cms.TravelBookingDetailsComponent)
public class TravelBookingDetailsComponentController extends AbstractBookingDetailsComponentController
{

	private static final String GLOBAL_RESERVATION_DATA = "globalReservationData";
	private static final String ADD_REQUEST_FORM = "addRequestForm";
	private static final String ADDITIONAL_SECURITY = "additionalSecurity";
	public static final String ACCOMMODATION_FINDER_FORM = "accommodationFinderForm";
	private static final String ACCOMMODATION_QUANTITY = "accommodationsQuantity";
	private static final String GUEST_QUANTITY = "guestQuantity";
	private static final String ERROR_ADD_ROOM_ORDER_CODE = "error.page.bookingdetails.add.room.orderCode";
	private static final String DEFAULT_ACCOMMODATION_QUANTITY = "1";

	@Resource(name = "accommodationOfferingCustomerReviewFacade")
	private DefaultAccommodationOfferingCustomerReviewFacade accommodationOfferingCustomerReviewFacade;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "dealBundleTemplateService")
	private DealBundleTemplateService dealBundleTemplateService;

	@Resource(name = "accommodationAvailabilityValidator")
	private AccommodationAvailabilityValidator accommodationAvailabilityValidator;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AbstractBookingDetailsComponentModel component)
	{
		final String bookingReference = getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_REFERENCE);
		try
		{
			final GlobalTravelReservationData globalReservationData = getBookingFacade()
					.getGlobalTravelReservationData(bookingReference);
			model.addAttribute(GLOBAL_RESERVATION_DATA, globalReservationData);
			handleDisruptedReservation(model, bookingReference, globalReservationData.getReservationData());

			// BookingActions
			final BookingActionRequestData transportBookingActionRequest = createTransportBookingActionRequest(bookingReference);
			final BookingActionRequestData accommodationBookingActionRequest = createAccommodationBookingActionRequest(
					bookingReference);
			final BookingActionRequestData globalBookingActionRequest = createGlobalBookingActionRequest(bookingReference);
			final BookingActionResponseData bookingActionResponse = getActionFacade().getTravelBookingAction(
					transportBookingActionRequest, accommodationBookingActionRequest, globalBookingActionRequest,
					globalReservationData);

			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_ACTION_RESPONSE, bookingActionResponse);

			model.addAttribute(ADDITIONAL_SECURITY, getBookingFacade().isAdditionalSecurityActive(bookingReference));
			model.addAttribute(ADD_REQUEST_FORM, new AddRequestForm());
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_REVIEW_FORM, new AccommodationReviewForm());
			model.addAttribute(TravelcommonsWebConstants.IS_PACKAGE_IN_ORDER, getPackageFacade().isPackageInOrder(bookingReference));
			model.addAttribute(TravelcommonsWebConstants.IS_DEAL_IN_ORDER,
					dealBundleTemplateService.isDealBundleOrder(bookingReference));
			model.addAttribute(TravelcommonsWebConstants.IS_PACKAGE_BOOKING_JOURNEY,
					getBookingFacade().checkBookingJourneyType(bookingReference, BookingJourneyType.BOOKING_PACKAGE));

			if (Objects.nonNull(globalReservationData.getAccommodationReservationData()))
			{
				final PriceData amountPaid = getTravelCommercePriceFacade()
						.getPaidAmount(globalReservationData.getAccommodationReservationData());
				model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_PAID, amountPaid);
				final PriceData dueAmount = getTravelCommercePriceFacade()
						.getDueAmount(globalReservationData.getAccommodationReservationData(), amountPaid);

				if (Objects.nonNull(dueAmount))
				{
					model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_DUE, dueAmount);
				}
			}
			//TODO: review this to make sure the logic is correct. Should review logic be part of the booking action?
			if (globalReservationData.getAccommodationReservationData() != null
					&& globalReservationData.getAccommodationReservationData().getAccommodationReference() != null)
			{
				model.addAttribute(AccommodationaddonWebConstants.SUBMITTED_REVIEWS,
						accommodationOfferingCustomerReviewFacade.retrieveCustomerReviewByBooking(bookingReference,
								globalReservationData.getAccommodationReservationData().getAccommodationReference()
										.getAccommodationOfferingCode()));
			}
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			model.addAttribute(PAGE_NOT_AVAILABLE, TravelacceleratorstorefrontWebConstants.PAGE_TEMPORARY_NOT_AVAILABLE);
		}

	}

	/**
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = "/add-room-options", method = RequestMethod.POST, produces =
			{ "application/json" })
	public String getAddRoomOptions(@ModelAttribute(value = "orderCode") final String orderCode, final Model model)
	{
		final AccommodationReservationData accommodationReservationData = reservationFacade
				.getAccommodationReservationSummary(orderCode);
		final String sessionBookingJourney = reservationFacade.getBookingJourneyType(orderCode);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY, sessionBookingJourney);
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY, sessionBookingJourney);
		if (Objects.isNull(accommodationReservationData))
		{
			model.addAttribute(TravelcommonsWebConstants.HAS_ERROR_FLAG, Boolean.TRUE);
			model.addAttribute(TravelcommonsWebConstants.ERROR_MESSAGE, ERROR_ADD_ROOM_ORDER_CODE);
			return TravelcommonsControllerConstants.Views.Pages.Booking.AddAccommodationRoomJsonResponse;
		}

		final AccommodationAvailabilityForm accommodationAvailabilityForm = initializeAccommodationAvailabilityForm(
				accommodationReservationData);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM, accommodationAvailabilityForm);
		model.addAttribute(ACCOMMODATION_QUANTITY, populateAccommodationsQuantity(accommodationReservationData));
		model.addAttribute(GUEST_QUANTITY, populatePassengersQuantity());
		model.addAttribute(AccommodationaddonWebConstants.ORDER_CODE, orderCode);
		model.addAttribute(TravelcommonsWebConstants.HAS_ERROR_FLAG, Boolean.FALSE);

		return TravelcommonsControllerConstants.Views.Pages.Booking.AddAccommodationRoomJsonResponse;
	}

	@RequestMapping(value = "/validate-accommodation-availability-form/{orderCode}", method = RequestMethod.POST, produces =
			{ "application/json" })
	public String validateAccommodationAvailabilityForm(final AccommodationAvailabilityForm accommodationAvailabilityForm,
			@PathVariable final String orderCode, final BindingResult bindingResult, final Model model)
	{
		accommodationAvailabilityValidator.validateGuestsQuantity(accommodationAvailabilityForm.getNumberOfRooms(),
				accommodationAvailabilityForm.getRoomStayCandidates(), bindingResult);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(TraveladdonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);
		if (hasErrorFlag)
		{
			model.addAttribute(TravelcommonsWebConstants.ERROR_MESSAGE, bindingResult.getFieldErrors().get(0).getCode());
		}
		return TravelcommonsControllerConstants.Views.Pages.Booking.AddAccommodationRoomPackageFormValidationJsonResponse;
	}


	protected List<String> populatePassengersQuantity()
	{
		final List<String> guestsQuantity = new ArrayList<>();
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
		for (int i = 0; i <= maxGuestQuantity; i++)
		{
			guestsQuantity.add(String.valueOf(i));
		}
		return guestsQuantity;
	}

	protected List<String> populateAccommodationsQuantity(final AccommodationReservationData accommodationReservationData)
	{
		final List<String> accommodationQuantity = new ArrayList<>();
		final int maxAccommodationsQuantity = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);

		final int roomsBooked = CollectionUtils.size(accommodationReservationData.getRoomStays());


		for (int i = 1; i <= (maxAccommodationsQuantity - roomsBooked); i++)
		{
			accommodationQuantity.add(String.valueOf(i) + " ");

		}
		return accommodationQuantity;
	}

	protected AccommodationAvailabilityForm initializeAccommodationAvailabilityForm(
			final AccommodationReservationData accommodationReservationData)
	{
		final AccommodationAvailabilityForm accommodationAvailabilityForm = new AccommodationAvailabilityForm();
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		final int maxAccommodationsQuantity = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);

		final int roomsBooked = CollectionUtils.size(accommodationReservationData.getRoomStays());
		for (int i = roomsBooked; i < maxAccommodationsQuantity; i++)
		{
			final RoomStayCandidateData roomStayCandidateData = createRoomStayCandidatesData();
			roomStayCandidateData.setRoomStayCandidateRefNumber(i);
			roomStayCandidates.add(roomStayCandidateData);
		}
		accommodationAvailabilityForm.setRoomStayCandidates(roomStayCandidates);
		accommodationAvailabilityForm.setNumberOfRooms(DEFAULT_ACCOMMODATION_QUANTITY);
		return accommodationAvailabilityForm;
	}

	protected RoomStayCandidateData createRoomStayCandidatesData()
	{
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		roomStayCandidateData.setPassengerTypeQuantityList(getPassengerTypeQuantityList());
		for (final PassengerTypeQuantityData passengeTypeQuantityData : roomStayCandidateData.getPassengerTypeQuantityList())
		{
			if (passengeTypeQuantityData.getPassengerType().getCode().equals(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT))
			{
				passengeTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_ADULT_QUANTITY);
			}
		}
		return roomStayCandidateData;
	}

	protected List<PassengerTypeQuantityData> getPassengerTypeQuantityList()
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<PassengerTypeData> sortedPassengerTypes = travellerSortStrategy
				.sortPassengerTypes(passengerTypeFacade.getPassengerTypes());
		for (final PassengerTypeData passengerTypeData : sortedPassengerTypes)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}
		return passengerTypeQuantityList;
	}

	@Override
	protected BookingActionRequestData createAccommodationBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = super.createAccommodationBookingActionRequest(bookingReference);
		bookingActionRequestData.getRequestActions().add(ActionTypeOption.CANCEL_ACCOMMODATION_BOOKING);
		return bookingActionRequestData;
	}

	@Override
	protected BookingActionRequestData createTransportBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = super.createTransportBookingActionRequest(bookingReference);
		bookingActionRequestData.getRequestActions().add(ActionTypeOption.CANCEL_TRANSPORT_BOOKING);
		return bookingActionRequestData;
	}

	protected BookingActionRequestData createGlobalBookingActionRequest(final String bookingReference)
	{
		final BookingActionRequestData bookingActionRequestData = new BookingActionRequestData();

		bookingActionRequestData.setBookingReference(bookingReference);

		final List<ActionTypeOption> requestActions = new ArrayList<>();
		requestActions.add(ActionTypeOption.CANCEL_BOOKING);
		bookingActionRequestData.setRequestActions(requestActions);

		final CustomerData currentCustomer = getCustomerFacade().getCurrentCustomer();
		bookingActionRequestData.setUserId(currentCustomer.getUid());

		return bookingActionRequestData;
	}

}
