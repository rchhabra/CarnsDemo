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

package de.hybris.platform.travelcommons.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationAvailabilityForm;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.AccommodationAmendmentFacade;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.order.TravelCartService;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class TravelManageBookingController extends AbstractController
{
	private static final Logger LOG = Logger.getLogger(TravelManageBookingController.class);

	private static final String ACCOMMODATION = "accommodation";
	private static final String TRANSPORT = "transport";
	private static final String CANCEL_ORDER_TYPE = "cancelOrderType";
	private static final String GLOBAL_RESERVATION_DATA = "globalReservationData";
	private static final String REFUND_ORDER_RESULT = "text.page.managemybooking.refund.order.result";
	private static final String CANCEL_TRANSPORT_ORDER_SUCCESSFUL = "text.page.managemybooking.cancel.transport.order.successful";
	private static final String CANCEL_TRANSPORT_ORDER_FAILED = "text.page.managemybooking.cancel.transport.order.failed";
	private static final String CANCEL_ACCOMMODATION_ORDER_SUCCESSFUL = "text.page.managemybooking.cancel.accommodation.order.successful";
	private static final String CANCEL_ACCOMMODATION_ORDER_FAILED = "text.page.managemybooking.cancel.accommodation.order.failed";
	private static final String ROOM = "r";
	private static final String BOOKING_DETAILS_PAGE = "/manage-booking/booking-details/";


	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "accommodationAmendmentFacade")
	private AccommodationAmendmentFacade accommodationAmendmentFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "travelCartService")
	private TravelCartService travelCartService;

	/**
	 * Starts the process of cancellation of the transport order
	 *
	 * @param model
	 * @param redirectModel
	 * @param bookingReference
	 * @return transport cancellation modal
	 */
	@RequestMapping(value = "/manage-booking/cancel-transport-order-request/{bookingReference}", method = RequestMethod.GET, produces = "application/json")
	@RequireHardLogIn
	public String beginCancelTransportOrder(final Model model, final RedirectAttributes redirectModel,
			@PathVariable final String bookingReference)
	{

		final boolean partialCancellationStarted = bookingFacade.beginPartialOrderCancellation(bookingReference,
				OrderEntryType.TRANSPORT, bookingFacade.getCurrentUserUid());

		if (partialCancellationStarted)
		{
			final GlobalTravelReservationData globalReservationData = bookingFacade.getGlobalTravelReservationData(bookingReference);
			model.addAttribute(GLOBAL_RESERVATION_DATA, globalReservationData);

			final PriceData totalToRefund = bookingFacade.getRefundTotal(bookingReference, OrderEntryType.TRANSPORT);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.TOTAL_TO_REFUND, totalToRefund.getFormattedValue());

			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, true);
		}
		else
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, false);
		}

		model.addAttribute(CANCEL_ORDER_TYPE, TRANSPORT);
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.cancelOrderResponse;
	}


	/**
	 * Proceeds with cancellation of the transport order and places a new (amended) order
	 *
	 * @param redirectModel
	 * @param bookingReference
	 * @return the redirect to booking details page with result of cancellation process
	 */
	@RequestMapping(value = "/manage-booking/cancel-transport-order/{bookingReference}")
	@RequireHardLogIn
	public String cancelTransportOrder(final RedirectAttributes redirectModel, @PathVariable final String bookingReference)
	{
		final PriceData totalToRefund = bookingFacade.getRefundTotal(bookingReference, OrderEntryType.TRANSPORT);
		final boolean result = bookingFacade.cancelPartialOrder(totalToRefund, OrderEntryType.TRANSPORT);

		if (result)
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT,
					CANCEL_TRANSPORT_ORDER_SUCCESSFUL);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUND_RESULT, REFUND_ORDER_RESULT);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUNDED_AMOUNT, totalToRefund);
		}
		else
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT,
					CANCEL_TRANSPORT_ORDER_FAILED);
		}

		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
	}

	/**
	 * Starts the process of cancellation of the accommodation order
	 *
	 * @param model
	 * @param redirectModel
	 * @param bookingReference
	 * @return accommodation cancellation modal
	 */
	@RequestMapping(value = "/manage-booking/cancel-accommodation-order-request/{bookingReference}", method = RequestMethod.GET, produces = "application/json")
	@RequireHardLogIn
	public String beginCancelAccommodationOrder(final Model model, final RedirectAttributes redirectModel,
			@PathVariable final String bookingReference)
	{

		final boolean partialCancellationStarted = bookingFacade.beginPartialOrderCancellation(bookingReference,
				OrderEntryType.ACCOMMODATION, bookingFacade.getCurrentUserUid());

		if (partialCancellationStarted)
		{
			final GlobalTravelReservationData globalReservationData = bookingFacade.getGlobalTravelReservationData(bookingReference);
			model.addAttribute(GLOBAL_RESERVATION_DATA, globalReservationData);

			final PriceData totalToRefund = bookingFacade.getRefundTotal(bookingReference, OrderEntryType.ACCOMMODATION);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.TOTAL_TO_REFUND, totalToRefund.getFormattedValue());

			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, true);
		}
		else
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.IS_CANCEL_POSSIBLE, false);
		}

		model.addAttribute(CANCEL_ORDER_TYPE, ACCOMMODATION);
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.cancelOrderResponse;
	}

	/**
	 * Proceeds with cancellation of the accommodation order and places a new (amended) order
	 *
	 * @param redirectModel
	 * @param bookingReference
	 * @return the redirect to booking details page with result of cancellation process
	 */
	@RequestMapping(value = "/manage-booking/cancel-accommodation-order/{bookingReference}")
	@RequireHardLogIn
	public String cancelAccommodationOrder(final RedirectAttributes redirectModel, @PathVariable final String bookingReference)
	{
		final PriceData totalToRefund = bookingFacade.getRefundTotal(bookingReference, OrderEntryType.ACCOMMODATION);
		final boolean result = bookingFacade.cancelPartialOrder(totalToRefund, OrderEntryType.ACCOMMODATION);

		if (result)
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT,
					CANCEL_ACCOMMODATION_ORDER_SUCCESSFUL);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUND_RESULT, REFUND_ORDER_RESULT);
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.REFUNDED_AMOUNT, totalToRefund);
		}
		else
		{
			redirectModel.addFlashAttribute(TravelacceleratorstorefrontWebConstants.CANCELLATION_RESULT,
					CANCEL_ACCOMMODATION_ORDER_FAILED);
		}

		return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + bookingReference;
	}

	/**
	 * Proceeds with cancellation of the accommodation order and places a new (amended) order
	 *
	 * @param redirectModel
	 * @param bookingReference
	 * @return the redirect to booking details page with result of cancellation process
	 */
	@RequestMapping(value = "/manage-booking/add-room-package/{orderCode}", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addAccommodationRoom(@PathVariable final String orderCode,
			@ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM) final AccommodationAvailabilityForm accommodationAvailabilityForm,
			final RedirectAttributes redirectModel, final Model model)
	{
		final boolean isAddRoomStarted = accommodationAmendmentFacade.startAmendment(orderCode);
		if (!isAddRoomStarted)
		{
			// If there was any error while creating cart from order, do not redirect to accommodation details
			return REDIRECT_PREFIX + BOOKING_DETAILS_PAGE + orderCode;
		}
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		final String queryString = buildPackageDetailsQuery(accommodationAvailabilityForm);
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PACKAGE_DETAILS_AMENDMENT_PAGE + queryString;
	}


	protected String buildPackageDetailsQuery(final AccommodationAvailabilityForm accommodationAvailabilityForm)
	{
		final Map<String, String> params = bookingFacade.getAccommodationDetailsParametersFromCart();

		if (MapUtils.isEmpty(params))
		{
			return StringUtils.EMPTY;
		}
		// number of rooms is set to 1 by default and there is just 1 AD in this room to offer user all possible options
		return params.get(TravelservicesConstants.ACCOMMODATION_OFFERING_CODE)
				+ TravelacceleratorstorefrontWebConstants.QUESTION_MARK + TravelservicesConstants.CHECK_IN_DATE_TIME
				+ TravelacceleratorstorefrontWebConstants.EQUALS + params.get(TravelservicesConstants.CHECK_IN_DATE_TIME)
				+ TravelacceleratorstorefrontWebConstants.AMPERSAND + TravelservicesConstants.CHECK_OUT_DATE_TIME
				+ TravelacceleratorstorefrontWebConstants.EQUALS + params.get(TravelservicesConstants.CHECK_OUT_DATE_TIME)
				+ TravelacceleratorstorefrontWebConstants.AMPERSAND + getNumberOfRoomsQuery(accommodationAvailabilityForm);
	}

	protected String getNumberOfRoomsQuery(final AccommodationAvailabilityForm accommodationAvailabilityForm)
	{
		final StringBuilder urlParameters = new StringBuilder();
		try
		{
			final int numberOfRooms = Integer.parseInt(accommodationAvailabilityForm.getNumberOfRooms());
			urlParameters.append(TravelacceleratorstorefrontValidationConstants.NUMBER_OF_ROOMS);
			urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			urlParameters.append(String.valueOf(numberOfRooms));
			urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
			for (int i = 0; i < numberOfRooms; i++)
			{
				final StringBuilder guestsStringPerRoom = new StringBuilder();
				final List<PassengerTypeQuantityData> guestCounts = accommodationAvailabilityForm.getRoomStayCandidates().get(i)
						.getPassengerTypeQuantityList();
				for (int j = 0; j < guestCounts.size(); j++)
				{
					final String passengerType = guestCounts.get(j).getPassengerType().getCode();
					final int passengerQuantity = guestCounts.get(j).getQuantity();
					final String guestParam = String.valueOf(passengerQuantity) + TravelacceleratorstorefrontWebConstants.HYPHEN
							+ passengerType;
					guestsStringPerRoom.append(guestParam);
					guestsStringPerRoom.append(TravelacceleratorstorefrontWebConstants.COMMA);
				}
				String result = guestsStringPerRoom.toString();
				result = result.substring(0, result.length() - 1);
				urlParameters.append(ROOM + i);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				urlParameters.append(result);
				if (i < numberOfRooms - 1)
				{
					urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
				}
			}
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer");
			LOG.error(e.getClass().getName() + " : " + e.getMessage());
			LOG.debug(e);
			return StringUtils.EMPTY;
		}
		return urlParameters.toString();
	}
}
